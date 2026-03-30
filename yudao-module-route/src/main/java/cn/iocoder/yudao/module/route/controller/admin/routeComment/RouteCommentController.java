package cn.iocoder.yudao.module.route.controller.admin.routeComment;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.route.controller.admin.routeComment.dto.RouteCommentCreateDTO;
import cn.iocoder.yudao.module.route.controller.admin.routeComment.vo.RouteCommentVO;
import cn.iocoder.yudao.module.route.service.routeComment.RouteCommentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/route/comments")
@RequiredArgsConstructor
public class RouteCommentController {

    @Resource
    private RouteCommentService commentService;

    @PostMapping
    public CommonResult<Boolean> create(@Valid @RequestBody RouteCommentCreateDTO dto) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        commentService.createComment(dto, userId);
        return CommonResult.success(true);
    }

    @GetMapping("/route/{routeId}")
    public CommonResult<Page<RouteCommentVO>> list(
            @PathVariable Long routeId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<RouteCommentVO> voPage = commentService.getCommentTree(routeId, page, size);
        return CommonResult.success(voPage);
    }

    @PostMapping("/{id}/like")
    public CommonResult<Boolean> like(@PathVariable Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        commentService.toggleLike(id, userId);
        return CommonResult.success(true);
    }

    @GetMapping("/getCommentChild/{commentId}")
    public CommonResult<Page<RouteCommentVO>> getChildComments(
            @PathVariable Long commentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return CommonResult.success(commentService.getChildComments(commentId, page, size));
    }
}
