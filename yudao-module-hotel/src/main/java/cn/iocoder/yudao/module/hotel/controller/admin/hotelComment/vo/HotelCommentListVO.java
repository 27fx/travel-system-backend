package cn.iocoder.yudao.module.hotel.controller.admin.hotelComment.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelCommentListVO {
    private List<HotelCommentVO> commentVOList;
    private Long replyCount;

}
