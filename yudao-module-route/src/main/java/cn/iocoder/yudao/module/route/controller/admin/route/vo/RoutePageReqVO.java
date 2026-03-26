package cn.iocoder.yudao.module.route.controller.admin.route.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 旅游线路分页 Request VO")
@Data
public class RoutePageReqVO extends PageParam {

    @Schema(description = "线路名称", example = "王五")
    private String name;

    @Schema(description = "旅游天数")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private Integer[] days;

    @Schema(description = "线路价格", example = "477")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private BigDecimal[] price;

    @Schema(description = "用户")
    private Long userNickName;

    @Schema(description = "用户id")
    private Long userId;


    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}