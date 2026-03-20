package cn.iocoder.yudao.module.hotel.dal.dataobject.hotel;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 酒店信息 DO
 *
 * @author 芋道源码
 */
@TableName("hotel")
@KeySequence("hotel_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelDO extends BaseDO {

    /**
     * 酒店ID
     */
    @TableId
    private Long id;
    /**
     * 酒店名称
     */
    private String name;
    /**
     * 酒店地址
     */
    private String location;
    /**
     * 酒店价格
     */
    private BigDecimal price;
    /**
     * 酒店介绍
     */
    private String description;
    /**
     * 酒店图片
     */
    private String image;
    /**
     * 评分
     */
    private BigDecimal score;


}