package cn.iocoder.yudao.module.note.controller.admin.note.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 游记新增/修改 Request VO")
@Data
public class NoteSaveReqVO {

    @Schema(description = "游记ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "26528")
    private Long id;

    @Schema(description = "用户ID", example = "1230")
    private Long userId;

    @Schema(description = "游记标题")
    private String title;

    @Schema(description = "游记内容")
    private String content;

    @Schema(description = "游记图片")
    private String image;

}