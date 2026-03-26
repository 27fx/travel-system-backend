package cn.iocoder.yudao.module.note.controller.admin.noteComment.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class NoteCommentCreateDTO {
    @NotNull(message = "笔记 ID 不能为空")
    private Long noteId;

    private Long parentId = 0L; // 默认 0

    @NotBlank(message = "内容不能为空")
    private String content;

    // 一级评论必填评分，回复可选
    private BigDecimal score; 
}
