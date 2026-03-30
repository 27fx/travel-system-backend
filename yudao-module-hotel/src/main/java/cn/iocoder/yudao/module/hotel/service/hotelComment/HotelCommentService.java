package cn.iocoder.yudao.module.hotel.service.hotelComment;

import cn.iocoder.yudao.module.hotel.controller.admin.hotelComment.dto.HotelCommentCreateDTO;
import cn.iocoder.yudao.module.hotel.controller.admin.hotelComment.vo.HotelCommentListVO;
import cn.iocoder.yudao.module.hotel.controller.admin.hotelComment.vo.HotelCommentVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface HotelCommentService {

    void createComment(HotelCommentCreateDTO dto);

    Page<HotelCommentVO> getCommentTree(Long hotelId, int page, int size);

    void toggleLike(Long commentId, Long currentUserId);

    void auditComment(Long commentId, Integer status, String operator);

    void deleteComment(Long commentId, Long currentUserId);



    Long getCommentReplyCount(Long commentId);

    HotelCommentListVO getChildComments(Long commentId, int page, int size);
}
