package cn.iocoder.yudao.module.hotel.controller.admin.hotel.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 酒店信息分页 Request VO")
@Data
public class HotelPageReqVO extends PageParam {

    @Schema(description = "酒店名称", example = "张三")
    private String name;

    @Schema(description = "酒店地址")
    private String location;

    @Schema(description = "酒店介绍", example = "你说的对")
    private String description;

}