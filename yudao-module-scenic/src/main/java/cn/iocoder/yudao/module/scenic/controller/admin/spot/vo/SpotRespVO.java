package cn.iocoder.yudao.module.scenic.controller.admin.spot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 景点信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SpotRespVO {

    @Schema(description = "景点ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "15041")
    @ExcelProperty("景点ID")
    private Long id;

    @Schema(description = "景点名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("景点名称")
    private String name;

    @Schema(description = "景点地址")
    @ExcelProperty("景点地址")
    private String location;

    @Schema(description = "门票价格", example = "10462")
    @ExcelProperty("门票价格")
    private BigDecimal ticketPrice;

    @Schema(description = "景点介绍", example = "你猜")
    @ExcelProperty("景点介绍")
    private String description;

    @Schema(description = "景点图片")
    @ExcelProperty("景点图片")
    private String image;

    @Schema(description = "评分")
    @ExcelProperty("评分")
    private BigDecimal score;

}