package cn.iocoder.yudao.module.hotel.controller.admin.hotel.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 酒店信息新增/修改 Request VO")
@Data
public class HotelSaveReqVO {

    @Schema(description = "酒店ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "14068")
    private Long id;

    @Schema(description = "酒店名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "酒店名称不能为空")
    private String name;

    @Schema(description = "酒店地址")
    private String location;

    @Schema(description = "酒店价格", example = "32414")
    private BigDecimal price;

    @Schema(description = "酒店介绍", example = "你说的对")
    private String description;

    @Schema(description = "酒店图片")
    private String image;

    @Schema(description = "评分")
    private BigDecimal score;

}