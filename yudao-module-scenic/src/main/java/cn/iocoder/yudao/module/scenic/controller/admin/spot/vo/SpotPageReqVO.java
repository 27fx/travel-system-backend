package cn.iocoder.yudao.module.scenic.controller.admin.spot.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 景点信息分页 Request VO")
@Data
public class SpotPageReqVO extends PageParam {

    @Schema(description = "景点名称", example = "李四")
    private String name;

    @Schema(description = "景点地址")
    private String location;

    @Schema(description = "门票价格", example = "10462")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private BigDecimal[] ticketPrice;

    @Schema(description = "景点介绍", example = "你猜")
    private String description;

}