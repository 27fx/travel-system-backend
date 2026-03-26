package cn.iocoder.yudao.module.hotel.controller.admin.hotelComment;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.hotel.controller.admin.hotelComment.dto.CommentCreateDTO;
import cn.iocoder.yudao.module.hotel.controller.admin.hotelComment.vo.CommentVO;
import cn.iocoder.yudao.module.hotel.service.hotelComment.HotelCommentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/hotel/comments")
@RequiredArgsConstructor
public class HotelCommentController {

    @Resource
    private HotelCommentService commentService;

    @PostMapping
    public CommonResult<Boolean> create(@Valid @RequestBody CommentCreateDTO dto) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        commentService.createComment(dto, userId);
        return CommonResult.success(true);
    }

    @GetMapping("/hotel/{hotelId}")
    public CommonResult<Page<CommentVO>> list(
            @PathVariable Long hotelId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CommentVO> voPage = commentService.getCommentTree(hotelId, page, size);
        return CommonResult.success(voPage);
    }

    @PostMapping("/{id}/like")
    public CommonResult<Boolean> like(@PathVariable Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        commentService.toggleLike(id, userId);
        return CommonResult.success(true);
    }
}
