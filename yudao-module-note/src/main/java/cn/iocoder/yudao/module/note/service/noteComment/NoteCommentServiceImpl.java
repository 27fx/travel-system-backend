package cn.iocoder.yudao.module.note.service.noteComment;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.note.controller.admin.noteComment.dto.NoteCommentCreateDTO;
import cn.iocoder.yudao.module.note.controller.admin.noteComment.vo.NoteCommentVO;
import cn.iocoder.yudao.module.note.dal.dataobject.noteComment.NoteCommentDO;
import cn.iocoder.yudao.module.note.dal.mysql.noteComment.NoteCommentMapper;
import cn.iocoder.yudao.module.note.enums.ErrorCodeConstants;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteCommentServiceImpl implements NoteCommentService {

    private final NoteCommentMapper commentMapper;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String COMMENT_LIKE_KEY = "note:comment:like%d";
    private static final long LIKE_EXPIRE_DAYS = 30;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createComment(NoteCommentCreateDTO dto) {
        // 1. 校验父评论 (如果是回复)
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            NoteCommentDO parentComment = commentMapper.selectById(dto.getParentId());
            if (parentComment == null || parentComment.getStatus() != 1 || parentComment.getDeleted()) {
                throw new IllegalArgumentException("父评论不存在或已被屏蔽/删除");
            }
            // 业务规则：回复不能带评分
            dto.setScore(null);
        } else {
            // 业务规则：一级评论必须带评分
            if (dto.getScore() == null || dto.getScore().compareTo(BigDecimal.ONE) < 0 || dto.getScore().compareTo(new BigDecimal("5")) > 0) {
                throw exception(ErrorCodeConstants.NOTE_COMMENTS_NOT_WITH_SCORE);
            }
        }

        // 2. 构建评论实体
        NoteCommentDO comment = new NoteCommentDO();
        comment.setNoteId(dto.getNoteId());
        comment.setUserId(SecurityFrameworkUtils.getLoginUserId());
        comment.setUserName(SecurityFrameworkUtils.getLoginUserNickname());
        comment.setUserAvatar(SecurityFrameworkUtils.getLoginUserAvatar());
        comment.setParentId(dto.getParentId() == null ? 0L : dto.getParentId());
        comment.setContent(dto.getContent());
        comment.setScore(dto.getScore());
        comment.setLikeCount(0);
        comment.setStatus(0); // 默认待审核 (0)，实际可接入自动审核服务直接设为 1

        commentMapper.insert(comment);

        log.info("评论 {} 已创建", comment.getId());
    }

    @Override
    public Page<NoteCommentVO> getCommentTree(Long noteId, int page, int size) {
        // 1. 分页查询一级评论
        Page<NoteCommentDO> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<NoteCommentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NoteCommentDO::getNoteId, noteId)
                .eq(NoteCommentDO::getParentId, 0)
                .eq(NoteCommentDO::getStatus, 1)
                .eq(NoteCommentDO::getDeleted, 0)
                .orderByDesc(NoteCommentDO::getCreateTime);

        Page<NoteCommentDO> commentPage = commentMapper.selectPage(pageParam, wrapper);
        List<NoteCommentDO> records = commentPage.getRecords();

        if (CollectionUtils.isEmpty(records)) {
            Page<NoteCommentVO> emptyPage = new Page<>();
            emptyPage.setCurrent(page);
            emptyPage.setSize(size);
            emptyPage.setTotal(0);
            emptyPage.setRecords(new ArrayList<>());
            return emptyPage;
        }

        // 2. 转换为 VO
        List<NoteCommentVO> voList = BeanUtil.copyToList(records, NoteCommentVO.class);
        List<Long> parentIds = voList.stream().map(NoteCommentVO::getId).collect(Collectors.toList());

        // 3. 批量查询这些一级评论的子评论 (回复)
        LambdaQueryWrapper<NoteCommentDO> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.in(NoteCommentDO::getParentId, parentIds)
                .eq(NoteCommentDO::getStatus, 1)
                .eq(NoteCommentDO::getDeleted, 0)
                .orderByAsc(NoteCommentDO::getCreateTime);

        List<NoteCommentDO> children = commentMapper.selectList(childWrapper);

        // 4. 内存组装：将回复分组到对应的父评论下
        Map<Long, List<NoteCommentVO>> groupedReplies = children.stream()
                .collect(Collectors.groupingBy(
                        NoteCommentDO::getParentId,
                        Collectors.mapping(this::convertToVO, Collectors.toList())
                ));

        // 5. 填充回复列表到 VO，并限制每个父评论显示的回复数量 (例如最新 5 条)
        int maxRepliesToShow = 5;
        for (NoteCommentVO vo : voList) {
            List<NoteCommentVO> replies = groupedReplies.getOrDefault(vo.getId(), new ArrayList<>());
            if (replies.size() > maxRepliesToShow) {
                vo.setReplies(replies.subList(0, maxRepliesToShow));
            } else {
                vo.setReplies(replies);
            }
            vo.setReplyCount(replies.size());
        }

        // 6. 构建返回的分页对象
        Page<NoteCommentVO> resultPage = new Page<>();
        resultPage.setCurrent(commentPage.getCurrent());
        resultPage.setSize(commentPage.getSize());
        resultPage.setTotal(commentPage.getTotal());
        resultPage.setRecords(voList);

        return resultPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleLike(Long commentId, Long currentUserId) {
        NoteCommentDO comment = commentMapper.selectById(commentId);
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
            throw new IllegalArgumentException("状态值非法，只能是 1(通过) 或 2(驳回)");
        }

        NoteCommentDO comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("评论不存在");
        }

        int oldStatus = comment.getStatus();
        comment.setStatus(status);
        comment.setUpdater(operator);
        comment.setUpdateTime(LocalDateTime.now());

        boolean updated = commentMapper.updateById(comment) > 0;

        log.info("评论 {} 审核状态更新为：{}", commentId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long currentUserId) {
        NoteCommentDO comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getDeleted()) {
            throw new IllegalArgumentException("评论不存在");
        }

        // 权限校验：只有创建者或管理员可以删除
        if (!comment.getUserId().equals(currentUserId)) {
            // throw new AccessDeniedException("无权限删除该评论");
            // 实际项目中请解开上方注释
        }

        // 策略：级联逻辑删除
        // 1. 如果是父评论，将其所有子评论也逻辑删除
        if (comment.getParentId() == 0) {
            LambdaQueryWrapper<NoteCommentDO> childWrapper = new LambdaQueryWrapper<>();
            childWrapper.eq(NoteCommentDO::getParentId, commentId);
            List<NoteCommentDO> children = commentMapper.selectList(childWrapper);
            if (!children.isEmpty()) {
                for (NoteCommentDO child : children) {
                    child.setDeleted(true);
                    child.setUpdateTime(LocalDateTime.now());
                }
                // 批量更新子评论
                for (NoteCommentDO child : children) {
                    commentMapper.updateById(child);
                }
            }
        }

        // 2. 删除当前评论
        comment.setDeleted(true);
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.updateById(comment);

        log.info("评论 {} 已删除", commentId);
    }

    /**
     * 实体转 VO
     */
    private NoteCommentVO convertToVO(NoteCommentDO c) {
        NoteCommentVO bean = BeanUtils.toBean(c, NoteCommentVO.class);
        bean.setReplies(new ArrayList<>());
        return bean;
    }
}
