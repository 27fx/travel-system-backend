package cn.iocoder.yudao.module.food.controller.app.food.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "用户端-美食信息分页 Request VO")
@Data
public class FoodPageReqVO extends PageParam {

    @Schema(description = "美食名称", example = "芋艿")
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