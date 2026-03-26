package cn.iocoder.yudao.module.hotel.controller.admin.hotelComment.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class HotelCommentCreateDTO {
    @NotNull(message = "酒店 ID 不能为空")
    private Long hotelId;

    private Long parentId = 0L;

    @NotBlank(message = "内容不能为空")
    private String content;

    private BigDecimal score; 
}
