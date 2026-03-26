package cn.iocoder.yudao.module.food.controller.admin.foodComment.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FoodCommentVO {
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
    private List<FoodCommentVO> replies;
    
    // 统计回复总数（用于显示“查看全部5条回复”）
    private Integer replyCount;
}