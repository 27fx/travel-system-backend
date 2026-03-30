package cn.iocoder.yudao.module.note.controller.admin.noteComment.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteCommentListVO {
    private List<NoteCommentVO> commentVOList;
    private Long replyCount;
}
