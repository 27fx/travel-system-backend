package cn.iocoder.yudao.module.route.controller.admin.routeComment.vo;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RouteCommentVO {
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String content;
    private Integer likeCount;


    private LocalDateTime createTime;
    private Long parentId;
    
    private List<RouteCommentVO> replies;
    
    private Integer replyCount;
}
