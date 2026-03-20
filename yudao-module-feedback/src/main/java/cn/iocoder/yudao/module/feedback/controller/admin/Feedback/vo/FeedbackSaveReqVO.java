package cn.iocoder.yudao.module.feedback.controller.admin.Feedback.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 用户反馈新增/修改 Request VO")
@Data
public class FeedbackSaveReqVO {

    @Schema(description = "反馈ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "21791")
    private Long id;

    @Schema(description = "反馈内容")
    private String content;

    @Schema(description = "管理员回复")
    private String reply;

    @Schema(description = "处理状态", example = "1")
    @DictFormat("feedback_type")
    private String status;

}