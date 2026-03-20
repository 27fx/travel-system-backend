package cn.iocoder.yudao.module.feedback.controller.admin.Feedback.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 用户反馈 Response VO")
@Data
@ExcelIgnoreUnannotated
public class FeedbackRespVO {

    @Schema(description = "反馈ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "21791")
    @ExcelProperty("反馈ID")
    private Long id;

    @Schema(description = "用户ID")
    @ExcelProperty("用户ID")
    private Long userId;

    private String userNickname;



    @Schema(description = "反馈内容")
    @ExcelProperty("反馈内容")
    private String content;

    @Schema(description = "处理状态", example = "1")
    @ExcelProperty(value = "处理状态", converter = DictConvert.class)
    @DictFormat("feedback_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String status;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @ExcelProperty("更新时间")
    private LocalDateTime updateTime;

}
