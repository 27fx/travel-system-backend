package cn.iocoder.yudao.module.feedback.controller.admin.Feedback.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户反馈分页 Request VO")
@Data
public class FeedbackPageReqVO extends PageParam {

    @Schema(description = "用户昵称", example = "21791")
    private String userNickName;

    @Schema(description = "反馈内容")
    private String content;

    @Schema(description = "处理状态", example = "1")
    @DictFormat("feedback_type")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "更新时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] updateTime;

}