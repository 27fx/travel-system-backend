package cn.iocoder.yudao.module.note.controller.admin.noteComment.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class NoteCommentVO {
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String content;
    private BigDecimal score;
    private Integer likeCount;
    private LocalDateTime createTime;
    private Long parentId;
    
    // 嵌套回复列表
    private List<NoteCommentVO> replies;
    
    // 统计回复总数（用于显示"查看全部 5 条回复"）
    private Integer replyCount;
}
