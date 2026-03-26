package cn.iocoder.yudao.module.hotel.service.hotelComment;
import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.hotel.controller.admin.hotelComment.dto.HotelCommentCreateDTO;
import cn.iocoder.yudao.module.hotel.controller.admin.hotelComment.vo.HotelCommentVO;
import cn.iocoder.yudao.module.hotel.dal.dataobject.hotel.HotelDO;
import cn.iocoder.yudao.module.hotel.dal.dataobject.hotelComment.HotelCommentDO;
import cn.iocoder.yudao.module.hotel.dal.mysql.hotel.HotelMapper;
import cn.iocoder.yudao.module.hotel.dal.mysql.hotelComment.HotelCommentMapper;
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
import static cn.iocoder.yudao.module.hotel.enums.ErrorCodeConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelCommentServiceImpl implements HotelCommentService {

    private final HotelCommentMapper commentMapper;
    private final HotelMapper hotelMapper;
    private final AdminUserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String COMMENT_LIKE_KEY = "comment:like%d";
    private static final long LIKE_EXPIRE_DAYS = 30;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createComment(HotelCommentCreateDTO dto, Long currentUserId) {
        HotelDO hotel = hotelMapper.selectById(dto.getHotelId());
        if (hotel == null || hotel.getDeleted()) {
            throw new IllegalArgumentException("酒店不存在或已删除");
        }

        AdminUserDO user = userMapper.selectById(currentUserId);
        if (user == null || user.getStatus() == 1 || user.getDeleted()) {
            throw exception(USER_STATUS_ERROR);
        }

        if (dto.getParentId() != null && dto.getParentId() > 0) {
            HotelCommentDO parentComment = commentMapper.selectById(dto.getParentId());
            if (parentComment == null || parentComment.getStatus() != 1 || parentComment.getDeleted()) {
                throw new IllegalArgumentException("父评论不存在或已被屏蔽/删除");
            }
            dto.setScore(null);
        } else {
            if (dto.getScore() == null || dto.getScore().compareTo(BigDecimal.ONE) < 0 || dto.getScore().compareTo(new BigDecimal("5")) > 0) {
                throw exception(HOTEL_COMMENT_STATUS_ERROR);
            }
        }

        HotelCommentDO comment = new HotelCommentDO();
        comment.setHotelId(dto.getHotelId());
        comment.setUserId(SecurityFrameworkUtils.getLoginUserId());
        comment.setUserName(SecurityFrameworkUtils.getLoginUserNickname());
        comment.setUserAvatar(SecurityFrameworkUtils.getLoginUserAvatar());
        comment.setParentId(dto.getParentId() == null ? 0L : dto.getParentId());
        comment.setContent(dto.getContent());
        comment.setScore(dto.getScore());
        comment.setLikeCount(0);
        comment.setStatus(0);

        commentMapper.insert(comment);

        if (comment.getParentId() == 0 && comment.getScore() != null) {
            comment.setStatus(1);
            commentMapper.updateById(comment);
            recalculateHotelScore(dto.getHotelId());
            
            log.info("评论 {} 已创建，待审核后更新酒店 {} 的评分", comment.getId(), dto.getHotelId());
        }
    }

    @Override
    public Page<HotelCommentVO> getCommentTree(Long hotelId, int page, int size) {
        Page<HotelCommentDO> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<HotelCommentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HotelCommentDO::getHotelId, hotelId)
               .eq(HotelCommentDO::getParentId, 0)
               .eq(HotelCommentDO::getStatus, 1)
               .eq(HotelCommentDO::getDeleted, 0)
               .orderByDesc(HotelCommentDO::getCreateTime);

        Page<HotelCommentDO> commentPage = commentMapper.selectPage(pageParam, wrapper);
        List<HotelCommentDO> records = commentPage.getRecords();

        if (CollectionUtils.isEmpty(records)) {
            Page<HotelCommentVO> emptyPage = new Page<>();
            emptyPage.setCurrent(page);
            emptyPage.setSize(size);
            emptyPage.setTotal(0);
            emptyPage.setRecords(new ArrayList<>());
            return emptyPage;
        }

        List<HotelCommentVO> voList = BeanUtil.copyToList(records, HotelCommentVO.class);
        List<Long> parentIds = voList.stream().map(HotelCommentVO::getId).collect(Collectors.toList());

        LambdaQueryWrapper<HotelCommentDO> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.in(HotelCommentDO::getParentId, parentIds)
                    .eq(HotelCommentDO::getStatus, 1)
                    .eq(HotelCommentDO::getDeleted, 0)
                    .orderByAsc(HotelCommentDO::getCreateTime);
        
        List<HotelCommentDO> children = commentMapper.selectList(childWrapper);

        Map<Long, List<HotelCommentVO>> groupedReplies = children.stream()
                .collect(Collectors.groupingBy(
                        HotelCommentDO::getParentId,
                        Collectors.mapping(this::convertToVO, Collectors.toList())
                ));

        int maxRepliesToShow = 5;
        for (HotelCommentVO vo : voList) {
            List<HotelCommentVO> replies = groupedReplies.getOrDefault(vo.getId(), new ArrayList<>());
            if (replies.size() > maxRepliesToShow) {
                vo.setReplies(replies.subList(0, maxRepliesToShow));
            } else {
                vo.setReplies(replies);
            }
        }

        Page<HotelCommentVO> resultPage = new Page<>();
        resultPage.setCurrent(commentPage.getCurrent());
        resultPage.setSize(commentPage.getSize());
        resultPage.setTotal(commentPage.getTotal());
        resultPage.setRecords(voList);

        return resultPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleLike(Long commentId, Long currentUserId) {
        HotelCommentDO comment = commentMapper.selectById(commentId);
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

        HotelCommentDO comment = commentMapper.selectById(commentId);
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
                recalculateHotelScore(comment.getHotelId());
            } else if (oldStatus == 1 && comment.getParentId() == 0 && comment.getScore() != null) {
                recalculateHotelScore(comment.getHotelId());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long currentUserId) {
        HotelCommentDO comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getDeleted()) {
            throw new IllegalArgumentException("评论不存在");
        }

        if (!comment.getUserId().equals(currentUserId)) {
             // throw new AccessDeniedException("无权限删除该评论");
        }

        if (comment.getParentId() == 0) {
            LambdaQueryWrapper<HotelCommentDO> childWrapper = new LambdaQueryWrapper<>();
            childWrapper.eq(HotelCommentDO::getParentId, commentId);
            List<HotelCommentDO> children = commentMapper.selectList(childWrapper);
            if (!children.isEmpty()) {
                for (HotelCommentDO child : children) {
                    child.setDeleted(true);
                    child.setUpdateTime(LocalDateTime.now());
                }
                for (HotelCommentDO child : children) {
                    commentMapper.updateById(child);
                }
            }
        }

        comment.setDeleted(true);
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.updateById(comment);

        if (comment.getParentId() == 0 && comment.getScore() != null) {
            recalculateHotelScore(comment.getHotelId());
        }
    }

    private void recalculateHotelScore(Long hotelId) {
        LambdaQueryWrapper<HotelCommentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HotelCommentDO::getHotelId, hotelId)
                .eq(HotelCommentDO::getParentId, 0)
                .eq(HotelCommentDO::getStatus, 1)
                .eq(HotelCommentDO::getDeleted, 0);

        List<HotelCommentDO> validComments = commentMapper.selectList(wrapper);

        BigDecimal newScore = new BigDecimal("5.0");
        if (!validComments.isEmpty()) {
            BigDecimal sum = validComments.stream()
                    .map(HotelCommentDO::getScore)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            newScore = sum.divide(new BigDecimal(validComments.size()), 1, RoundingMode.HALF_UP);
        }

        HotelDO hotel = new HotelDO();
        hotel.setId(hotelId);
        hotel.setScore(newScore);
        hotelMapper.updateById(hotel);
        
        log.info("酒店 {} 评分已更新为：{}", hotelId, newScore);
    }

    private HotelCommentVO convertToVO(HotelCommentDO c) {
        HotelCommentVO bean = BeanUtils.toBean(c, HotelCommentVO.class);
        bean.setReplies(new ArrayList<>());
        return bean;
    }
}
