package cn.iocoder.yudao.module.route.service.routeComment;
import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.route.controller.admin.routeComment.dto.CommentCreateDTO;
import cn.iocoder.yudao.module.route.controller.admin.routeComment.vo.CommentVO;
import cn.iocoder.yudao.module.route.dal.dataobject.route.RouteDO;
import cn.iocoder.yudao.module.route.dal.dataobject.routeComment.RouteCommentDO;
import cn.iocoder.yudao.module.route.dal.mysql.route.RouteMapper;
import cn.iocoder.yudao.module.route.dal.mysql.routeComment.RouteCommentMapper;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.route.enums.ErrorCodeConstants.USER_STATUS_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteCommentServiceImpl implements RouteCommentService {

    private final RouteCommentMapper commentMapper;
    private final RouteMapper routeMapper;
    private final AdminUserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String COMMENT_LIKE_KEY = "comment:like%d";
    private static final long LIKE_EXPIRE_DAYS = 30;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createComment(CommentCreateDTO dto, Long currentUserId) {
        RouteDO route = routeMapper.selectById(dto.getRouteId());
        if (route == null || route.getDeleted()) {
            throw new IllegalArgumentException("线路不存在或已删除");
        }

        AdminUserDO user = userMapper.selectById(currentUserId);
        if (user == null || user.getStatus() == 1 || user.getDeleted()) {
            throw exception(USER_STATUS_ERROR);
        }

        if (dto.getParentId() != null && dto.getParentId() > 0) {
            RouteCommentDO parentComment = commentMapper.selectById(dto.getParentId());
            if (parentComment == null || parentComment.getStatus() != 1 || parentComment.getDeleted()) {
                throw new IllegalArgumentException("父评论不存在或已被屏蔽/删除");
            }
            dto.setScore(null);
        } else {
            if (dto.getScore() == null || dto.getScore().compareTo(BigDecimal.ONE) < 0 || dto.getScore().compareTo(new BigDecimal("5")) > 0) {
                throw new IllegalArgumentException("评分必须在 1-5 分之间");
            }
        }

        RouteCommentDO comment = new RouteCommentDO();
        comment.setRouteId(dto.getRouteId());
        comment.setUserId(SecurityFrameworkUtils.getLoginUserId());
        comment.setUserName(SecurityFrameworkUtils.getLoginUserNickname());
        comment.setUserAvatar(SecurityFrameworkUtils.getLoginUserAvatar());
        comment.setParentId(dto.getParentId() == null ? 0L : dto.getParentId());
        comment.setContent(dto.getContent());
        comment.setScore(dto.getScore());
        comment.setLikeCount(0);
        comment.setStatus(1);

        commentMapper.insert(comment);

        log.info("评论 {} 已创建", comment.getId());
    }

    @Override
    public Page<CommentVO> getCommentTree(Long routeId, int page, int size) {
        Page<RouteCommentDO> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<RouteCommentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RouteCommentDO::getRouteId, routeId)
               .eq(RouteCommentDO::getParentId, 0)
               .eq(RouteCommentDO::getStatus, 1)
               .eq(RouteCommentDO::getDeleted, 0)
               .orderByDesc(RouteCommentDO::getCreateTime);

        Page<RouteCommentDO> commentPage = commentMapper.selectPage(pageParam, wrapper);
        List<RouteCommentDO> records = commentPage.getRecords();

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

        LambdaQueryWrapper<RouteCommentDO> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.in(RouteCommentDO::getParentId, parentIds)
                    .eq(RouteCommentDO::getStatus, 1)
                    .eq(RouteCommentDO::getDeleted, 0)
                    .orderByAsc(RouteCommentDO::getCreateTime);
        
        List<RouteCommentDO> children = commentMapper.selectList(childWrapper);

        Map<Long, List<CommentVO>> groupedReplies = children.stream()
                .collect(Collectors.groupingBy(
                        RouteCommentDO::getParentId,
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
        RouteCommentDO comment = commentMapper.selectById(commentId);
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

        RouteCommentDO comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("评论不存在");
        }

        comment.setStatus(status);
        comment.setUpdater(operator);
        comment.setUpdateTime(LocalDateTime.now());
        
        commentMapper.updateById(comment);

        log.info("评论 {} 审核状态已更新为：{}", commentId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long currentUserId) {
        RouteCommentDO comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getDeleted()) {
            throw new IllegalArgumentException("评论不存在");
        }

        if (!comment.getUserId().equals(currentUserId)) {
             // throw new AccessDeniedException("无权限删除该评论");
        }

        if (comment.getParentId() == 0) {
            LambdaQueryWrapper<RouteCommentDO> childWrapper = new LambdaQueryWrapper<>();
            childWrapper.eq(RouteCommentDO::getParentId, commentId);
            List<RouteCommentDO> children = commentMapper.selectList(childWrapper);
            if (!children.isEmpty()) {
                for (RouteCommentDO child : children) {
                    child.setDeleted(true);
                    child.setUpdateTime(LocalDateTime.now());
                }
                for (RouteCommentDO child : children) {
                    commentMapper.updateById(child);
                }
            }
        }

        comment.setDeleted(true);
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.updateById(comment);

        log.info("评论 {} 已删除", commentId);
    }

    private CommentVO convertToVO(RouteCommentDO c) {
        CommentVO bean = BeanUtils.toBean(c, CommentVO.class);
        bean.setReplies(new ArrayList<>());
        return bean;
    }
}
