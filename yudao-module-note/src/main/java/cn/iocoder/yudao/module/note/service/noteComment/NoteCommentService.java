package cn.iocoder.yudao.module.note.service.noteComment;

import cn.iocoder.yudao.module.note.controller.admin.noteComment.dto.NoteCommentCreateDTO;
import cn.iocoder.yudao.module.note.controller.admin.noteComment.vo.NoteCommentListVO;
import cn.iocoder.yudao.module.note.controller.admin.noteComment.vo.NoteCommentVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.validation.Valid;

public interface NoteCommentService {

    /**
     * 创建评论
     *
     * @param dto 创建评论 DTO
     */
    void createComment(@Valid NoteCommentCreateDTO dto);

    /**
     * 获取评论树（分页）
     *
     * @param noteId 笔记 ID
     * @param page 页码
     * @param size 每页大小
     * @return 评论分页数据
     */
    Page<NoteCommentVO> getCommentTree(Long noteId, int page, int size);

    /**
     * 点赞/取消点赞
     *
     * @param commentId 评论 ID
     * @param currentUserId 当前用户 ID
     */
    void toggleLike(Long commentId, Long currentUserId);

    /**
     * 审核评论
     *
     * @param commentId 评论 ID
     * @param status 状态 (1: 通过，2: 驳回)
     * @param operator 操作人
     */
    void auditComment(Long commentId, Integer status, String operator);

    /**
     * 删除评论
     *
     * @param commentId 评论 ID
     * @param currentUserId 当前用户 ID
     */
    void deleteComment(Long commentId, Long currentUserId);

    NoteCommentListVO getChildComments(Long commentId, int page, int size);
}

