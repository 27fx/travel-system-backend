package cn.iocoder.yudao.module.scenic.controller.admin.spotComment.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentVO {
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String content;
    private BigDecimal score;
    private Integer likeCount;
    private LocalDateTime createTime;
    private Long parentId;
    
    private List<CommentVO> replies;
    
    private Integer replyCount;
}
