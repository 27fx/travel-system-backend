package cn.iocoder.yudao.module.scenic.controller.admin.spotComment;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.scenic.controller.admin.spotComment.dto.SpotCommentCreateDTO;
import cn.iocoder.yudao.module.scenic.controller.admin.spotComment.vo.SpotCommentVO;
import cn.iocoder.yudao.module.scenic.service.spotComment.SpotCommentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/scenic/comments")
@RequiredArgsConstructor
public class SpotCommentController {

    @Resource
    private SpotCommentService commentService;

    @PostMapping
    public CommonResult<Boolean> create(@Valid @RequestBody SpotCommentCreateDTO dto) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        commentService.createComment(dto, userId);
        return CommonResult.success(true);
    }

    @GetMapping("/spot/{spotId}")
    public CommonResult<Page<SpotCommentVO>> list(
            @PathVariable Long spotId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SpotCommentVO> voPage = commentService.getCommentTree(spotId, page, size);
        return CommonResult.success(voPage);
    }

    @PostMapping("/{id}/like")
    public CommonResult<Boolean> like(@PathVariable Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        commentService.toggleLike(id, userId);
        return CommonResult.success(true);
    }

    @GetMapping("/getCommentChild/{commentId}")
    public CommonResult<Page<SpotCommentVO>> getChildComments(
            @PathVariable Long commentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return CommonResult.success(commentService.getChildComments(commentId, page, size));
    }
}
