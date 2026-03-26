package cn.iocoder.yudao.module.scenic.service.spotComment;


import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.scenic.controller.admin.spotComment.dto.CommentCreateDTO;
import cn.iocoder.yudao.module.scenic.controller.admin.spotComment.vo.CommentVO;
import cn.iocoder.yudao.module.scenic.dal.dataobject.spot.SpotDO;
import cn.iocoder.yudao.module.scenic.dal.dataobject.spotComment.SpotCommentDO;
import cn.iocoder.yudao.module.scenic.dal.mysql.spot.SpotMapper;
import cn.iocoder.yudao.module.scenic.dal.mysql.spotComment.SpotCommentMapper;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.scenic.enums.ErrorCodeConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotCommentServiceImpl implements SpotCommentService {

    private final SpotCommentMapper commentMapper;
    private final SpotMapper spotMapper;
    private final AdminUserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String COMMENT_LIKE_KEY = "comment:like%d";
    private static final long LIKE_EXPIRE_DAYS = 30;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createComment(CommentCreateDTO dto, Long currentUserId) {
        SpotDO spot = spotMapper.selectById(dto.getSpotId());
        if (spot == null || spot.getDeleted()) {
            throw new IllegalArgumentException("景点不存在或已删除");
        }

        AdminUserDO user = userMapper.selectById(currentUserId);
        if (user == null || user.getStatus() == 1 || user.getDeleted()) {
            throw exception(USER_STATUS_ERROR);
        }

        if (dto.getParentId() != null && dto.getParentId() > 0) {
            SpotCommentDO parentComment = commentMapper.selectById(dto.getParentId());
            if (parentComment == null || parentComment.getStatus() != 1 || parentComment.getDeleted()) {
                throw new IllegalArgumentException("父评论不存在或已被屏蔽/删除");
            }
            dto.setScore(null);
        } else {
            if (dto.getScore() == null || dto.getScore().compareTo(BigDecimal.ONE) < 0 || dto.getScore().compareTo(new BigDecimal("5")) > 0) {
                throw exception(SPOT_COMMENT_STATUS_ERROR);
            }
        }

        SpotCommentDO comment = new SpotCommentDO();
        comment.setSpotId(dto.getSpotId());
        comment.setUserId(currentUserId);
        comment.setUserName(user.getNickname());
        comment.setUserAvatar(user.getAvatar());
        comment.setParentId(dto.getParentId() == null ? 0L : dto.getParentId());
        comment.setContent(dto.getContent());
        comment.setScore(dto.getScore());
        comment.setLikeCount(0);
        comment.setStatus(0);
        comment.setCreator(user.getNickname());

        commentMapper.insert(comment);

        if (comment.getParentId() == 0 && comment.getScore() != null) {
            comment.setStatus(1);
            commentMapper.updateById(comment);
            recalculateSpotScore(dto.getSpotId());
            
            log.info("评论 {} 已创建，待审核后更新景点 {} 的评分", comment.getId(), dto.getSpotId());
        }
    }

    @Override
    public Page<CommentVO> getCommentTree(Long spotId, int page, int size) {
        Page<SpotCommentDO> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SpotCommentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpotCommentDO::getSpotId, spotId)
               .eq(SpotCommentDO::getParentId, 0)
               .eq(SpotCommentDO::getStatus, 1)
               .eq(SpotCommentDO::getDeleted, 0)
               .orderByDesc(SpotCommentDO::getCreateTime);

        Page<SpotCommentDO> commentPage = commentMapper.selectPage(pageParam, wrapper);
        List<SpotCommentDO> records = commentPage.getRecords();

        if (CollectionUtils.isEmpty(records)) {
            Page<CommentVO> emptyPage = new Page<>();
            emptyPage.setCurrent(page);
            emptyPage.setSize(size);
            emptyPage.setTotal(0);
            emptyPage.setRecords(new ArrayList<>());
            return emptyPage;
        }

        List<CommentVO> voList = BeanUtil.copyToList(records, CommentVO.class);
        List<Long> parentIds = voList.stream().map(CommentVO::getId).collect(Collectors.toList());

        LambdaQueryWrapper<SpotCommentDO> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.in(SpotCommentDO::getParentId, parentIds)
                    .eq(SpotCommentDO::getStatus, 1)
                    .eq(SpotCommentDO::getDeleted, 0)
                    .orderByAsc(SpotCommentDO::getCreateTime);
        
        List<SpotCommentDO> children = commentMapper.selectList(childWrapper);

        Map<Long, List<CommentVO>> groupedReplies = children.stream()
                .collect(Collectors.groupingBy(
                        SpotCommentDO::getParentId,
                        Collectors.mapping(this::convertToVO, Collectors.toList())
                ));

        int maxRepliesToShow = 5;
        for (CommentVO vo : voList) {
            List<CommentVO> replies = groupedReplies.getOrDefault(vo.getId(), new ArrayList<>());
            if (replies.size() > maxRepliesToShow) {
                vo.setReplies(replies.subList(0, maxRepliesToShow));
            } else {
                vo.setReplies(replies);
            }
        }

        Page<CommentVO> resultPage = new Page<>();
        resultPage.setCurrent(commentPage.getCurrent());
        resultPage.setSize(commentPage.getSize());
        resultPage.setTotal(commentPage.getTotal());
        resultPage.setRecords(voList);

        return resultPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleLike(Long commentId, Long currentUserId) {
        SpotCommentDO comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getDeleted()) {
            throw new IllegalArgumentException("评论不存在");
        }

        String redisKey = String.format(COMMENT_LIKE_KEY, commentId);

        Boolean isLiked = stringRedisTemplate.opsForSet().isMember(redisKey, currentUserId.toString());

        if (Boolean.TRUE.equals(isLiked)) {
            stringRedisTemplate.opsForSet().remove(redisKey, currentUserId.toString());
            int newLikeCount = Math.max(0, comment.getLikeCount() - 1);
            comment.setLikeCount(newLikeCount);
            log.info("用户 {} 取消对评论 {} 的点赞，当前点赞数：{}", currentUserId, commentId, newLikeCount);
        } else {
            stringRedisTemplate.opsForSet().add(redisKey, currentUserId.toString());
            stringRedisTemplate.expire(redisKey, LIKE_EXPIRE_DAYS, TimeUnit.DAYS);
            int newLikeCount = comment.getLikeCount() + 1;
            comment.setLikeCount(newLikeCount);
            log.info("用户 {} 点赞评论 {}，当前点赞数：{}", currentUserId, commentId, newLikeCount);
        }

        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.updateById(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditComment(Long commentId, Integer status, String operator) {
        if (status != 1 && status != 2) {
            throw new IllegalArgumentException("状态值非法，只能是 1(通过) 或 2(驳回)");
        }

        SpotCommentDO comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("评论不存在");
        }

        int oldStatus = comment.getStatus();
        comment.setStatus(status);
        comment.setUpdater(operator);
        comment.setUpdateTime(LocalDateTime.now());
        
        boolean updated = commentMapper.updateById(comment) > 0;

        if (updated && oldStatus != status) {
            if (status == 1 && comment.getParentId() == 0 && comment.getScore() != null) {
                recalculateSpotScore(comment.getSpotId());
            } else if (oldStatus == 1 && comment.getParentId() == 0 && comment.getScore() != null) {
                recalculateSpotScore(comment.getSpotId());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long currentUserId) {
        SpotCommentDO comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getDeleted()) {
            throw new IllegalArgumentException("评论不存在");
        }

        if (!comment.getUserId().equals(currentUserId)) {
             // throw new AccessDeniedException("无权限删除该评论");
        }

        if (comment.getParentId() == 0) {
            LambdaQueryWrapper<SpotCommentDO> childWrapper = new LambdaQueryWrapper<>();
            childWrapper.eq(SpotCommentDO::getParentId, commentId);
            List<SpotCommentDO> children = commentMapper.selectList(childWrapper);
            if (!children.isEmpty()) {
                for (SpotCommentDO child : children) {
                    child.setDeleted(true);
                    child.setUpdateTime(LocalDateTime.now());
                }
                for (SpotCommentDO child : children) {
                    commentMapper.updateById(child);
                }
            }
        }

        comment.setDeleted(true);
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.updateById(comment);

        if (comment.getParentId() == 0 && comment.getScore() != null) {
            recalculateSpotScore(comment.getSpotId());
        }
    }

    private void recalculateSpotScore(Long spotId) {
        LambdaQueryWrapper<SpotCommentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpotCommentDO::getSpotId, spotId)
                .eq(SpotCommentDO::getParentId, 0)
                .eq(SpotCommentDO::getStatus, 1)
                .eq(SpotCommentDO::getDeleted, 0);

        List<SpotCommentDO> validComments = commentMapper.selectList(wrapper);

        BigDecimal newScore = new BigDecimal("5.0");
        if (!validComments.isEmpty()) {
            BigDecimal sum = validComments.stream()
                    .map(SpotCommentDO::getScore)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            newScore = sum.divide(new BigDecimal(validComments.size()), 1, RoundingMode.HALF_UP);
        }

        SpotDO spot = new SpotDO();
        spot.setId(spotId);
        spot.setScore(newScore);
        spotMapper.updateById(spot);
        
        log.info("景点 {} 评分已更新为：{}", spotId, newScore);
    }

    private CommentVO convertToVO(SpotCommentDO c) {
        CommentVO vo = new CommentVO();
        vo.setId(c.getId());
        vo.setUserId(c.getUserId());
        vo.setUserName(c.getUserName());
        vo.setUserAvatar(c.getUserAvatar());
        vo.setContent(c.getContent());
        vo.setScore(c.getScore());
        vo.setLikeCount(c.getLikeCount());
        vo.setCreateTime(c.getCreateTime());
        vo.setParentId(c.getParentId());
        vo.setReplies(new ArrayList<>());
        return vo;
    }
}
