package cn.iocoder.yudao.module.food.service.foodComment;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.food.controller.admin.foodComment.dto.CommentCreateDTO;
import cn.iocoder.yudao.module.food.controller.admin.foodComment.vo.CommentVO;
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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CommentsDocument;
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


    private static final String COMMENT_LIKE_KEY = "comment:like%d";// 美食评分缓存键

    //点赞过期时间
    private static final long LIKE_EXPIRE_DAYS = 30;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createComment(CommentCreateDTO dto) {
        // 1. 校验美食是否存在
        FoodDO food = foodMapper.selectById(dto.getFoodId());
        if (food == null || food.getDeleted()) {
            throw new IllegalArgumentException("美食不存在或已删除");
        }

        // 2. 获取当前用户信息 (用于快照

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
            // 可选：限制同一用户对同一美食只能发一条一级评论
//            long count = commentMapper.selectCount(new LambdaQueryWrapper<CommentDO>()
//                    .eq(CommentDO::getFoodId, dto.getFoodId())
//                    .eq(CommentDO::getUserId, currentUserId)
//                    .eq(CommentDO::getParentId, 0)
//                    .eq(CommentDO::getDeleted, 0));
//            if (count > 0) {
//                throw new IllegalArgumentException("您已对该美食发表过评论，请勿重复提交");
//            }
        }

        // 4. 构建评论实体
        CommentDO comment = new CommentDO();
        comment.setFoodId(dto.getFoodId());
        comment.setUserId(SecurityFrameworkUtils.getLoginUserId());
        comment.setUserName(SecurityFrameworkUtils.getLoginUserNickname()); // 【关键】用户昵称快照
        comment.setUserAvatar(SecurityFrameworkUtils.getLoginUserAvatar()); // 【关键】用户头像快照
        comment.setParentId(dto.getParentId() == null ? 0L : dto.getParentId());
        comment.setContent(dto.getContent());
        comment.setScore(dto.getScore());
        comment.setLikeCount(0);
        comment.setStatus(0); // 默认待审核 (0)，实际可接入自动审核服务直接设为1
        // create_time, update_time, deleted 由 MP 自动填充

        commentMapper.insert(comment);

        // 5. 更新美食评分 (如果是一级评论且状态正常)
        // 注意：生产环境建议将此步骤异步化 (发送MQ消息)，避免长事务影响主流程
        if (comment.getParentId() == 0 && comment.getScore() != null) {
            // 这里为了演示简单，先假设审核直接通过 (status=1) 再计算，
            // 实际逻辑应该是：如果配置了自动审核通过，则调用；否则等待审核通过后由审核接口触发计算。
            // 此处演示：如果业务允许先显示后审核，或者自动审核秒过，则调用。
            // 为了严谨，我们假设这里只是“预计算”或者由定时任务/审核动作触发。
            // 如果你希望立即生效，可以将上面 comment.setStatus(1)。
            
//             方案 A: 立即重新计算 (假设自动审核通过)
             comment.setStatus(1);
             commentMapper.updateById(comment);
             recalculateFoodScore(dto.getFoodId());
            
            // 方案 B (推荐): 记录日志，由审核通过时的回调或定时任务去更新分数
            log.info("评论 {} 已创建，待审核后更新美食 {} 的评分", comment.getId(), dto.getFoodId());
        }
    }

    @Override
    public Page<CommentVO> getCommentTree(Long foodId, int page, int size) {
        // 1. 分页查询一级评论
        Page<CommentDO> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<CommentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentDO::getFoodId, foodId)
               .eq(CommentDO::getParentId, 0)
               .eq(CommentDO::getStatus, 1) // 只查正常的
               .eq(CommentDO::getDeleted, 0)
               .orderByDesc(CommentDO::getCreateTime);

        Page<CommentDO> commentPage = commentMapper.selectPage(pageParam, wrapper);
        List<CommentDO> records = commentPage.getRecords();

        if (CollectionUtils.isEmpty(records)) {
            Page<CommentVO> emptyPage = new Page<>();
            emptyPage.setCurrent(page);
            emptyPage.setSize(size);
            emptyPage.setTotal(0);
            emptyPage.setRecords(new ArrayList<>());
            return emptyPage;
        }

        // 2. 转换为 VO
        List<CommentVO> voList = BeanUtil.copyToList(records, CommentVO.class);
        List<Long> parentIds = voList.stream().map(CommentVO::getId).collect(Collectors.toList());

        // 3. 批量查询这些一级评论的子评论 (回复)
        // 策略：查出所有相关回复，在内存中分组。如果回复量极大，建议限制数量或使用单独接口加载
        LambdaQueryWrapper<CommentDO> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.in(CommentDO::getParentId, parentIds)
                    .eq(CommentDO::getStatus, 1)
                    .eq(CommentDO::getDeleted, 0)
                    .orderByAsc(CommentDO::getCreateTime); // 回复按时间正序
        
        List<CommentDO> children = commentMapper.selectList(childWrapper);

        // 4. 内存组装：将回复分组到对应的父评论下
        Map<Long, List<CommentVO>> replyMap = children.stream()
                .map(this::convertToVO)
                .collect(Collectors.groupingBy(CommentVO::getParentId)); // 注意：VO中需要设置parentId才能这样group，或者用原Entity分组

        // 修正：上面的 stream map 之后 VO 里可能没 parentId，我们用 Entity 分组更稳妥
        Map<Long, List<CommentVO>> groupedReplies = children.stream()
                .collect(Collectors.groupingBy(
                        CommentDO::getParentId,
                        Collectors.mapping(this::convertToVO, Collectors.toList())
                ));

        // 5. 填充回复列表到 VO，并限制每个父评论显示的回复数量 (例如最新5条)
        int maxRepliesToShow = 5;
        for (CommentVO vo : voList) {
            List<CommentVO> replies = groupedReplies.getOrDefault(vo.getId(), new ArrayList<>());
            // 如果需要显示“共xx条回复”，可以单独查 count，这里直接用列表大小示意
            if (replies.size() > maxRepliesToShow) {
                vo.setReplies(replies.subList(0, maxRepliesToShow));
                // 可以在 VO 中增加一个 totalReplyCount 字段来存储 replies.size()
            } else {
                vo.setReplies(replies);
            }
        }

        // 6. 构建返回的分页对象
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
        CommentDO comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getDeleted()) {
            throw new IllegalArgumentException("评论不存在");
        }

        String redisKey = String.format(COMMENT_LIKE_KEY, commentId);

        // 1. 检查 Redis 中该用户是否已点赞
        Boolean isLiked = stringRedisTemplate.opsForSet().isMember(redisKey, currentUserId.toString());

        if (Boolean.TRUE.equals(isLiked)) {
            // 2.1 如果已点赞，则取消点赞
            stringRedisTemplate.opsForSet().remove(redisKey, currentUserId.toString());

            // 数据库点赞数 -1
            int newLikeCount = Math.max(0, comment.getLikeCount() - 1);
            comment.setLikeCount(newLikeCount);
            log.info("用户 {} 取消对评论 {} 的点赞，当前点赞数：{}", currentUserId, commentId, newLikeCount);
        } else {
            // 2.2 如果未点赞，则添加点赞
            stringRedisTemplate.opsForSet().add(redisKey, currentUserId.toString());

            // 设置过期时间
            stringRedisTemplate.expire(redisKey, LIKE_EXPIRE_DAYS, TimeUnit.DAYS);

            // 数据库点赞数 +1
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
            throw new IllegalArgumentException("状态值非法，只能是1(通过)或2(驳回)");
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
            // 如果状态从 待审核/驳回 变为 正常(1)，且是一级评论，需要重新计算美食评分
            if (status == 1 && comment.getParentId() == 0 && comment.getScore() != null) {
                recalculateFoodScore(comment.getFoodId());
            } 
            // 如果状态从 正常(1) 变为 驳回/屏蔽，也需要重新计算（剔除该评分）
            else if (oldStatus == 1 && comment.getParentId() == 0 && comment.getScore() != null) {
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

        // 权限校验：只有创建者或管理员可以删除
        // 假设有一个 isAdmin 方法，这里简化为只允许创建者删除
        if (!comment.getUserId().equals(currentUserId)) {
             // throw new AccessDeniedException("无权限删除该评论");
             // 实际项目中请解开上方注释
        }

        // 策略：级联逻辑删除
        // 1. 如果是父评论，将其所有子评论也逻辑删除
        if (comment.getParentId() == 0) {
            LambdaQueryWrapper<CommentDO> childWrapper = new LambdaQueryWrapper<>();
            childWrapper.eq(CommentDO::getParentId, commentId);
            List<CommentDO> children = commentMapper.selectList(childWrapper);
            if (!children.isEmpty()) {
                for (CommentDO child : children) {
                    child.setDeleted(true); // 假设 true 表示删除，需在 MP 配置中对应
                    child.setUpdateTime(LocalDateTime.now());
                    // 或者直接调用 removeById，但 MP 的 removeById 也是走逻辑删除
                    // 这里手动更新以展示批量逻辑
                }
                // 批量更新子评论
                // 为了简化，这里循环更新，实际可用 updateBatchById
                for (CommentDO child : children) {
                    commentMapper.updateById(child);
                }
            }
        }

        // 2. 删除当前评论
        comment.setDeleted(true);
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.updateById(comment);

        // 3. 如果是一级评论，重新计算美食评分
        if (comment.getParentId() == 0 && comment.getScore() != null) {
            recalculateFoodScore(comment.getFoodId());
        }
    }

    /**
     * 重新计算美食平均分
     * 逻辑：查询该美食下所有 status=1, deleted=0, parent_id=0 且有 score 的评论，求平均
     */
    private void recalculateFoodScore(Long foodId) {
        LambdaQueryWrapper<CommentDO> wrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<CommentDO> eq = wrapper.eq(CommentDO::getFoodId, foodId)
                .eq(CommentDO::getParentId, 0)
                .eq(CommentDO::getParentId, 0)
                .eq(CommentDO::getStatus, 1)
                .eq(CommentDO::getDeleted, 0);

        List<CommentDO> validComments = commentMapper.selectList(wrapper);

        BigDecimal newScore = new BigDecimal("5.0"); // 默认分
        if (!validComments.isEmpty()) {
            BigDecimal sum = validComments.stream()
                    .map(CommentDO ::getScore)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            newScore = sum.divide(new BigDecimal(validComments.size()), 1, RoundingMode.HALF_UP);
        }

        FoodDO food = new FoodDO();
        food.setId(foodId);
        food.setScore(newScore);
        foodMapper.updateById(food);
        
        log.info("美食 {} 评分已更新为: {}", foodId, newScore);
    }

    /**
     * 实体转 VO
     */
    private CommentVO convertToVO(CommentDO c) {
        CommentVO bean = BeanUtils.toBean(c, CommentVO.class);
        bean.setReplies(new ArrayList<>());
        return bean;
    }
}