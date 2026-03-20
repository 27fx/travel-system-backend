package cn.iocoder.yudao.module.scenic.controller.admin.spot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 景点信息新增/修改 Request VO")
@Data
public class SpotSaveReqVO {

    @Schema(description = "景点ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "15041")
    private Long id;

    @Schema(description = "景点名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "景点名称不能为空")
    private String name;

    @Schema(description = "景点地址")
    private String location;

    @Schema(description = "门票价格", example = "10462")
    private BigDecimal ticketPrice;

    @Schema(description = "景点介绍", example = "你猜")
    private String description;

    @Schema(description = "景点图片")
    private String image;

    @Schema(description = "评分")
    private BigDecimal score;

}