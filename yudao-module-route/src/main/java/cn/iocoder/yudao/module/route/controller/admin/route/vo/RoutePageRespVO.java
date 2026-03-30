package cn.iocoder.yudao.module.route.controller.admin.route.vo;


import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RoutePageRespVO {
    @Schema(description = "线路ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "4578")
    @ExcelProperty("线路ID")
    private Long id;

    @Schema(description = "线路名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("线路名称")
    private String name;

    @Schema(description = "旅游天数")
    @ExcelProperty("旅游天数")
    private Integer days;

    @Schema(description = "线路价格", example = "477")
    @ExcelProperty("线路价格")
    private BigDecimal price;

    @Schema(description = "线路简介")
    private String introduction;

    @Schema(description = "线路图片")
    private String image;

    @Schema(description = "发表用户")
    @ExcelProperty("发表用户")
    private String userNickname;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
