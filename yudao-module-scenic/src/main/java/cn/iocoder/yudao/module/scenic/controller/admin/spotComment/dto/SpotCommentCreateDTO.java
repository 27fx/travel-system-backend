package cn.iocoder.yudao.module.scenic.controller.admin.spotComment.dto;


import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class SpotCommentCreateDTO {
    @NotNull(message = "景点 ID 不能为空")
    private Long spotId;

    private Long parentId = 0L;

    @NotBlank(message = "内容不能为空")
    private String content;

    private BigDecimal score;

}
