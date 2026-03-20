package cn.iocoder.yudao.module.note.controller.admin.note.vo;


import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 游记分页 Request VO")
@Data

public class NotePageRespVO {
    @Schema(description = "游记ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "26528")
    @ExcelProperty("游记ID")
    private Long id;

    @Schema(description = "游记标题")
    @ExcelProperty("游记标题")
    private String title;

    //删去内容防止前端显示

    @Schema(description = "游记图片")
    @ExcelProperty("游记图片")
    private String image;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "用户昵称")
    @ExcelProperty("用户昵称")
    private String userNickname;

}
