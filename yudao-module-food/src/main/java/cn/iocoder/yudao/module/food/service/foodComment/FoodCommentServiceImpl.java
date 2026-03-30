package cn.iocoder.yudao.module.food.service.foodComment;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.food.controller.admin.foodComment.dto.FoodCommentCreateDTO;
import cn.iocoder.yudao.module.food.controller.admin.foodComment.vo.FoodCommentListVO;
import cn.iocoder.yudao.module.food.controller.admin.foodComment.vo.FoodCommentVO;
import cn.iocoder.yudao.module.food.dal.dataobject.food.FoodDO;
import cn.iocoder.yudao.module.food.dal.dataobject.foodComment.CommentDO;
import cn.iocoder.yudao.module.food.dal.mysql.food.FoodMapper;
import cn.iocoder.yudao.module.food.dal.mysql.foodComment.FoodCommentMapper;
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
import static cn.iocoder.yudao.module.food.enums.ErrorCodeConstants.COMMENTS_NOT_WITH_SCORE;
import static cn.iocoder.yudao.module.food.enums.ErrorCodeConstants.USER_STATUS_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodCommentServiceImpl implements FoodCommentService {

    private final FoodCommentMapper commentMapper;
    private final FoodMapper foodMapper;
    private final AdminUserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String COMMENT_LIKE_KEY = "comment:like%d";
    private static final long LIKE_EXPIRE_DAYS = 30;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createComment(FoodCommentCreateDTO dto) {
        // 1. 校验美食是否存在
        FoodDO food = foodMapper.selectById(dto.getFoodId());
        if (food == null || food.getDeleted()) {
            throw new IllegalArgumentException("美食不存在或已删除");
        }

        // 2. 校验用户状态
        AdminUserDO user = userMapper.selectById(SecurityFrameworkUtils.getLoginUserId());
        if (user == null || user.getStatus() == 1 || user.getDeleted()) {
            throw exception(USER_STATUS_ERROR);
        }

        // 3. 校验父评论 (如果是回复)
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            CommentDO parentComment = commentMapper.selectById(dto.getParentId());
            if (parentComment == null || parentComment.getStatus() != 1 || parentComment.getDeleted()) {
                throw new IllegalArgumentException("父评论不存在或已被屏蔽/删除");
            }
            // 业务规则：回复不能带评分
            dto.setScore(null);
        } else {
            // 业务规则：一级评论必须带评分
            if (dto.getScore() == null || dto.getScore().compareTo(BigDecimal.ONE) < 0 || dto.getScore().compareTo(new BigDecimal("5")) > 0) {
                throw exception(COMMENTS_NOT_WITH_SCORE);
            }
        }

        // 4. 构建评论实体
        CommentDO comment = new CommentDO();
        comment.setFoodId(dto.getFoodId());
        comment.setUserId(SecurityFrameworkUtils.getLoginUserId());
        comment.setUserName(SecurityFrameworkUtils.getLoginUserNickname());
        comment.setUserAvatar(SecurityFrameworkUtils.getLoginUserAvatar());
        comment.setParentId(dto.getParentId() == null ? 0L : dto.getParentId());
        comment.setContent(dto.getContent());
        comment.setScore(dto.getScore());
        comment.setLikeCount(0);
        comment.setStatus(1);

        commentMapper.insert(comment);

        // 5. 更新美食评分 (如果是一级评论且状态正常)
        if (comment.getParentId() == 0 && comment.getScore() != null) {
            comment.setStatus(1);
            commentMapper.updateById(comment);
            recalculateFoodScore(dto.getFoodId());

            log.info("评论 {} 已创建，待审核后更新美食 {} 的评分", comment.getId(), dto.getFoodId());
        }
    }

    @Override
    public Page<FoodCommentVO> getCommentTree(Long foodId, int page, int size) {
        if (foodId == null || foodId <= 0) {
            throw new IllegalArgumentException("美食 ID 不能为空");
        }

        Page<CommentDO> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<CommentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentDO::getFoodId, foodId)
                .eq(CommentDO::getParentId, 0)
                .eq(CommentDO::getStatus, 1)
                .eq(CommentDO::getDeleted, 0)
                .orderByDesc(CommentDO::getCreateTime);

        Page<CommentDO> commentPage = commentMapper.selectPage(pageParam, wrapper);
        List<CommentDO> records = commentPage.getRecords();

        if (CollectionUtils.isEmpty(records)) {
            Page<FoodCommentVO> emptyPage = new Page<>();
            emptyPage.setCurrent(page);
            emptyPage.setSize(size);
            emptyPage.setTotal(0);
            emptyPage.setRecords(new ArrayList<>());
            return emptyPage;
        }

        List<FoodCommentVO> voList = BeanUtil.copyToList(records, FoodCommentVO.class);
        List<Long> parentIds = voList.stream()
                .map(FoodCommentVO::getId)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(parentIds)) {
            Page<FoodCommentVO> resultPage = new Page<>();
            resultPage.setCurrent(commentPage.getCurrent());
            resultPage.setSize(commentPage.getSize());
            resultPage.setTotal(commentPage.getTotal());
            resultPage.setRecords(voList);
            return resultPage;
        }

        // 一次性查询所有回复记录并统计数量
        LambdaQueryWrapper<CommentDO> replyWrapper = new LambdaQueryWrapper<>();
        replyWrapper.in(CommentDO::getParentId, parentIds)
                .eq(CommentDO::getFoodId, foodId)
                .eq(CommentDO::getStatus, 1)
                .eq(CommentDO::getDeleted, 0);

        List<CommentDO> allReplies = commentMapper.selectList(replyWrapper);

        // 使用 Stream 统计每个主评论的回复数
        Map<Long, Long> replyCountMap = allReplies.stream()
                .collect(Collectors.groupingBy(
                        CommentDO::getParentId,
                        Collectors.counting()
                ));

        // 设置回复数量到 VO
        for (FoodCommentVO vo : voList) {
            Long count = replyCountMap.getOrDefault(vo.getId(), 0L);
            vo.setReplyCount(count.intValue());
        }

        Page<FoodCommentVO> resultPage = new Page<>();
        resultPage.setCurrent(commentPage.getCurrent());
        resultPage.setSize(commentPage.getSize());
        resultPage.setTotal(commentPage.getTotal());
        resultPage.setRecords(voList);

        return resultPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleLike(Long commentId, Long currentUserId) {
        CommentDO comment = commentMapper.selectById(commentId);
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

        CommentDO comment = commentMapper.selectById(commentId);
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
                recalculateFoodScore(comment.getFoodId());
            } else if (oldStatus == 1 && comment.getParentId() == 0 && comment.getScore() != null) {
                recalculateFoodScore(comment.getFoodId());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long currentUserId) {
        CommentDO comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getDeleted()) {
            throw new IllegalArgumentException("评论不存在");
        }

        if (!comment.getUserId().equals(currentUserId)) {
            // throw new AccessDeniedException("无权限删除该评论");
        }

        if (comment.getParentId() == 0) {
            LambdaQueryWrapper<CommentDO> childWrapper = new LambdaQueryWrapper<>();
            childWrapper.eq(CommentDO::getParentId, commentId);
            List<CommentDO> children = commentMapper.selectList(childWrapper);
            if (!children.isEmpty()) {
                for (CommentDO child : children) {
                    child.setDeleted(true);
                    child.setUpdateTime(LocalDateTime.now());
                }
                for (CommentDO child : children) {
                    commentMapper.updateById(child);
                }
            }
        }

        comment.setDeleted(true);
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.updateById(comment);

        if (comment.getParentId() == 0 && comment.getScore() != null) {
            recalculateFoodScore(comment.getFoodId());
        }
    }


    @Override
    public FoodCommentListVO  getChildComments(Long commentId, int page, int size) {
        if (commentId == null || commentId <= 0) {
            throw new IllegalArgumentException("评论 ID 不能为空");
        }

        // 分页查询子评论
        Page<CommentDO> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<CommentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentDO::getParentId, commentId)
                .eq(CommentDO::getStatus, 1)
                .eq(CommentDO::getDeleted, 0)
                .orderByAsc(CommentDO::getCreateTime);

        Page<CommentDO> commentPage = commentMapper.selectPage(pageParam, wrapper);
        List<CommentDO> records = commentPage.getRecords();

        // 转换为 VO
        List<FoodCommentVO> voList;
        if (CollectionUtils.isEmpty(records)) {
            voList = new ArrayList<>();
        } else {
            voList = BeanUtil.copyToList(records, FoodCommentVO.class);
        }

        // 构建返回结果
        FoodCommentListVO result = new FoodCommentListVO();
        result.setCommentVOList(voList);
        result.setReplyCount(commentPage.getTotal());

        return result;
    }

    /**
     * 重新计算美食平均分
     */
    private void recalculateFoodScore(Long foodId) {
        LambdaQueryWrapper<CommentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentDO::getFoodId, foodId)
                .eq(CommentDO::getParentId, 0)
                .eq(CommentDO::getStatus, 1)
                .eq(CommentDO::getDeleted, 0);

        List<CommentDO> validComments = commentMapper.selectList(wrapper);

        BigDecimal newScore = new BigDecimal("5.0");
        if (!validComments.isEmpty()) {
            BigDecimal sum = validComments.stream()
                    .map(CommentDO::getScore)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            newScore = sum.divide(new BigDecimal(validComments.size()), 1, RoundingMode.HALF_UP);
        }

        FoodDO food = new FoodDO();
        food.setId(foodId);
        food.setScore(newScore);
        foodMapper.updateById(food);

        log.info("美食 {} 评分已更新为：{}", foodId, newScore);
    }
}
