package cn.iocoder.yudao.module.hotel.controller.admin.hotelComment.vo;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class HotelCommentVO {
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String content;
    private BigDecimal score;
    private Integer likeCount;
    private LocalDateTime createTime;
    private Long parentId;
    
    private List<HotelCommentVO> replies;
    
    private Integer replyCount;
}
