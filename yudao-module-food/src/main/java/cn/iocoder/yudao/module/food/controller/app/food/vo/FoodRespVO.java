package cn.iocoder.yudao.module.food.controller.app.food.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 美食信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class FoodRespVO {

    @Schema(description = "美食ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "21999")
    @ExcelProperty("美食ID")
    private Long id;

    @Schema(description = "美食名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("美食名称")
    private String name;

    @Schema(description = "餐厅地址")
    @ExcelProperty("餐厅地址")
    private String location;

    @Schema(description = "人均消费", example = "7479")
    @ExcelProperty("人均消费")
    private BigDecimal price;

    @Schema(description = "美食介绍", example = "你说的对")
    @ExcelProperty("美食介绍")
    private String description;

    @Schema(description = "图片")
    @ExcelProperty("图片")
    private String image;

    @Schema(description = "评分")
    @ExcelProperty("评分")
    private BigDecimal score;

}
