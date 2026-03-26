
package cn.iocoder.yudao.module.hotel.dal.dataobject.hotelComment;

import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("hotel_comment")
@KeySequence("hotel_comment_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelCommentDO extends BaseDO {

    @TableId
    private Long id;
    
    private Long hotelId;
    
    private Long userId;
    
    private String userName;
    
    private String userAvatar;
    
    private Long parentId;
    
    private String content;
    
    private BigDecimal score;
    
    private Integer likeCount;
    
    private Integer status;
}
