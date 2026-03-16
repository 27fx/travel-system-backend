package cn.iocoder.yudao.module.food.controller.app.food.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 美食信息新增/修改 Request VO")
@Data
public class FoodSaveReqVO {

    @Schema(description = "美食ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "21999")
    private Long id;

    @Schema(description = "美食名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "美食名称不能为空")
    private String name;

    @Schema(description = "餐厅地址")
    private String location;

    @Schema(description = "人均消费", example = "7479")
    private BigDecimal price;

    @Schema(description = "美食介绍", example = "你说的对")
    private String description;

    @Schema(description = "图片")
    private String image;

    @Schema(description = "评分")
    private BigDecimal score;

}