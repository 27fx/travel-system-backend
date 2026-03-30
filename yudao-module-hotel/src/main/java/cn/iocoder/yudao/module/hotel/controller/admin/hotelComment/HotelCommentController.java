package cn.iocoder.yudao.module.hotel.controller.admin.hotelComment;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.hotel.controller.admin.hotelComment.dto.HotelCommentCreateDTO;
import cn.iocoder.yudao.module.hotel.controller.admin.hotelComment.vo.HotelCommentListVO;
import cn.iocoder.yudao.module.hotel.controller.admin.hotelComment.vo.HotelCommentVO;
import cn.iocoder.yudao.module.hotel.service.hotelComment.HotelCommentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/hotel/comments")
@RequiredArgsConstructor
public class HotelCommentController {

    @Resource
    private HotelCommentService commentService;

    @PostMapping
    public CommonResult<Boolean> create(@Valid @RequestBody HotelCommentCreateDTO dto) {
        commentService.createComment(dto);
        return success(true);
    }

    @GetMapping("/hotel/{hotelId}")
    public CommonResult<Page<HotelCommentVO>> list(
            @PathVariable Long hotelId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<HotelCommentVO> voPage = commentService.getCommentTree(hotelId, page, size);
        return success(voPage);
    }

    @PostMapping("/{id}/like")
    public CommonResult<Boolean> like(@PathVariable Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        commentService.toggleLike(id, userId);
        return success(true);
    }

    @GetMapping("/getCommentChild/{commentId}")
    public CommonResult<HotelCommentListVO> getChildComments(
            @PathVariable Long commentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return success(commentService.getChildComments(commentId, page, size));
    }




}
