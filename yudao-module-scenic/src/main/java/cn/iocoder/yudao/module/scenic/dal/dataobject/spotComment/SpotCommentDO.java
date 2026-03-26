
package cn.iocoder.yudao.module.scenic.dal.dataobject.spotComment;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

@TableName("scenic_spot_comment")
@KeySequence("scenic_spot_comment_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotCommentDO extends BaseDO {

    @TableId
    private Long id;
    
    private Long spotId;
    
    private Long userId;
    
    private String userName;
    
    private String userAvatar;
    
    private Long parentId;
    
    private String content;
    
    private BigDecimal score;
    
    private Integer likeCount;
    
    private Integer status;
}
