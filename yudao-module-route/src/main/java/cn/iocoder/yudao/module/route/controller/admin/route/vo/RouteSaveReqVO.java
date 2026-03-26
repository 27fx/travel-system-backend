package cn.iocoder.yudao.module.route.controller.admin.route.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 旅游线路新增/修改 Request VO")
@Data
public class RouteSaveReqVO {

    @Schema(description = "线路ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "4578")
    private Long id;

    @Schema(description = "线路名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @NotEmpty(message = "线路名称不能为空")
    private String name;

    @Schema(description = "旅游天数")
    private Integer days;

    @Schema(description = "线路价格", example = "477")
    private BigDecimal price;

    @Schema(description = "线路简介")
    private String introduction;

    @Schema(description = "线路介绍", example = "你说的对")
    private String description;

    @Schema(description = "线路图片")
    private String image;

}