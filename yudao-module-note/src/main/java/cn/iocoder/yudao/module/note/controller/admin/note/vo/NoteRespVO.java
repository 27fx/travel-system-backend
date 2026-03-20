package cn.iocoder.yudao.module.note.controller.admin.note.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 游记 Response VO")
@Data
@ExcelIgnoreUnannotated
public class NoteRespVO {

    @Schema(description = "游记ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "26528")
    @ExcelProperty("游记ID")
    private Long id;

    @Schema(description = "用户ID", example = "1230")
    @ExcelProperty("用户ID")
    private Long userId;

    @Schema(description = "游记标题")
    @ExcelProperty("游记标题")
    private String title;

    @Schema(description = "游记内容")
    @ExcelProperty("游记内容")
    private String content;

    @Schema(description = "游记图片")
    @ExcelProperty("游记图片")
    private String image;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "游记对应创建的用户昵称")
    @ExcelProperty("游记对应创建的用户昵称")
    private String userNickname;

}
