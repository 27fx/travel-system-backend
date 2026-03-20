package cn.iocoder.yudao.module.hotel.controller.admin.hotel.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 酒店信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class HotelRespVO {

    @Schema(description = "酒店ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "14068")
    @ExcelProperty("酒店ID")
    private Long id;

    @Schema(description = "酒店名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("酒店名称")
    private String name;

    @Schema(description = "酒店地址")
    @ExcelProperty("酒店地址")
    private String location;

    @Schema(description = "酒店价格", example = "32414")
    @ExcelProperty("酒店价格")
    private BigDecimal price;

    @Schema(description = "酒店介绍", example = "你说的对")
    @ExcelProperty("酒店介绍")
    private String description;

    @Schema(description = "酒店图片")
    @ExcelProperty("酒店图片")
    private String image;

    @Schema(description = "评分")
    @ExcelProperty("评分")
    private BigDecimal score;

}