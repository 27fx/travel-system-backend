package cn.iocoder.yudao.module.food.controller.admin.foodComment;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.food.controller.admin.foodComment.dto.CommentCreateDTO;
import cn.iocoder.yudao.module.food.controller.admin.foodComment.vo.CommentVO;
import cn.iocoder.yudao.module.food.service.foodComment.FoodCommentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
// 假设有一个 UserContext 工具类获取当前登录用户ID

@RestController
@RequestMapping("/food/comments")
@RequiredArgsConstructor
public class FoodCommentController {

    @Resource

    private FoodCommentService commentService;

    @PostMapping
    public CommonResult<Boolean> create(@Valid @RequestBody CommentCreateDTO dto) {
        // 从 Token 解析
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        commentService.createComment(dto, userId);
        return CommonResult.success(true);
    }

    @GetMapping("/food/{foodId}")
    public CommonResult<Page<CommentVO>> list(
            @PathVariable Long foodId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CommentVO> voPage = commentService.getCommentTree(foodId, page, size);
        return CommonResult.success(voPage);
    }

    @PostMapping("/{id}/like")
    public CommonResult<Boolean> like(@PathVariable Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        commentService.toggleLike(id, userId);
        return CommonResult.success(true);
    }
}