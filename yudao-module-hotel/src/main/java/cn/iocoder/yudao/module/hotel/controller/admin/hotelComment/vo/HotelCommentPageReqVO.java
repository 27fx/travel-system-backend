package cn.iocoder.yudao.module.hotel.controller.admin.hotelComment.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 酒店评论分页 Request VO")
@Data
public class HotelCommentPageReqVO extends PageParam {

    @Schema(description = "关联酒店 ID", example = "15041")
    private Long hotelId;

    @Schema(description = "父评论 ID", example = "1024")
    private Long parentId;

    @Schema(description = "用户 ID", example = "15041")
    private Long userId;

    @Schema(description = "用户名", example = "张三")
    private String userName;

    @Schema(description = "评论内容", example = "评论内容")
    private String content;

    @Schema(description = "评分", example = "4.5")
    private BigDecimal score;

    @Schema(description = "状态 (0: 待审核，1: 正常，2: 屏蔽/违规)", example = "1")
    private Integer status;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;
}
