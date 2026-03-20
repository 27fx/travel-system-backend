package cn.iocoder.yudao.module.note.controller.admin.note.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 游记分页 Request VO")
@Data
public class NotePageReqVO extends PageParam {

    @Schema(description = "用户ID", example = "1230")
    private Long userId;

    @Schema(description = "游记对应创建的用户昵称")
    private String userNickname;

    @Schema(description = "游记标题")
    private String title;

    @Schema(description = "游记内容")
    private String content;

    @Schema(description = "游记图片")
    private String image;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;


}