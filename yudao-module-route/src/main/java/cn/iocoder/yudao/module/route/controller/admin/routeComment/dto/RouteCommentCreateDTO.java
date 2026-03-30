package cn.iocoder.yudao.module.route.controller.admin.routeComment.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class RouteCommentCreateDTO {
    @NotNull(message = "线路 ID 不能为空")
    private Long routeId;

    private Long parentId = 0L;

    @NotBlank(message = "内容不能为空")
    private String content;

}
