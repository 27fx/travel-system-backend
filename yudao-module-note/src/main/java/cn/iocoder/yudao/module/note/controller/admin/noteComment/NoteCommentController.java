package cn.iocoder.yudao.module.note.controller.admin.noteComment;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.note.controller.admin.noteComment.dto.NoteCommentCreateDTO;
import cn.iocoder.yudao.module.note.controller.admin.noteComment.vo.NoteCommentVO;
import cn.iocoder.yudao.module.note.service.noteComment.NoteCommentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/note/comments")
@RequiredArgsConstructor
public class NoteCommentController {

    @Resource
    private NoteCommentService commentService;

    /**
     * 创建评论
     */
    @PostMapping
    public CommonResult<Boolean> create(@Valid @RequestBody NoteCommentCreateDTO dto) {
        commentService.createComment(dto);
        return CommonResult.success(true);
    }

    /**
     * 获取评论树（分页）
     */
    @GetMapping("/note/{noteId}")
    public CommonResult<Page<NoteCommentVO>> list(
            @PathVariable Long noteId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<NoteCommentVO> voPage = commentService.getCommentTree(noteId, page, size);
        return CommonResult.success(voPage);
    }

    /**
     * 点赞/取消点赞
     */
    @PostMapping("/{id}/like")
    public CommonResult<Boolean> like(@PathVariable Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        commentService.toggleLike(id, userId);
        return CommonResult.success(true);
    }
}
