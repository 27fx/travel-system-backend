package cn.iocoder.yudao.module.route.controller.admin.route.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 旅游线路 Response VO")
@Data
@ExcelIgnoreUnannotated
public class RouteRespVO {

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
    @ExcelProperty("线路简介")
    private String introduction;

    @Schema(description = "线路描述")
     private String description;

    @Schema(description = "线路图片")
    @ExcelProperty("线路图片")
    private String image;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建用户")
    @ExcelProperty("创建用户")
    private String userNickname;

}
