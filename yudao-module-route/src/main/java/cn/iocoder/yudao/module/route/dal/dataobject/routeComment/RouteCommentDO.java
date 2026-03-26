package cn.iocoder.yudao.module.route.dal.dataobject.routeComment;

import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.math.BigDecimal;

@TableName("route_comment")
@KeySequence("route_comment_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteCommentDO extends BaseDO {

    @TableId
    private Long id;
    
    private Long routeId;
    
    private Long userId;
    
    private String userName;
    
    private String userAvatar;
    
    private Long parentId;
    
    private String content;
    
    private BigDecimal score;
    
    private Integer likeCount;
    
    private Integer status;
}
