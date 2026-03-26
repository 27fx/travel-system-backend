package cn.iocoder.yudao.module.hotel.service.hotelComment;

import cn.iocoder.yudao.module.hotel.controller.admin.hotelComment.dto.HotelCommentCreateDTO;
import cn.iocoder.yudao.module.hotel.controller.admin.hotelComment.vo.HotelCommentVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface HotelCommentService {

    void createComment(HotelCommentCreateDTO dto, Long currentUserId);

    Page<HotelCommentVO> getCommentTree(Long hotelId, int page, int size);

    void toggleLike(Long commentId, Long currentUserId);

    void auditComment(Long commentId, Integer status, String operator);

    void deleteComment(Long commentId, Long currentUserId);
}
