package cn.iocoder.yudao.module.food.dal.dataobject.food;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 美食信息 DO
 *
 * @author 芋道源码
 */
@TableName("food")
@KeySequence("food_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodDO extends BaseDO {

    /**
     * 美食ID
     */
    @TableId
    private Long id;
    /**
     * 美食名称
     */
    private String name;
    /**
     * 餐厅地址
     */
    private String location;
    /**
     * 人均消费
     */
    private BigDecimal price;
    /**
     * 美食介绍
     */
    private String description;
    /**
     * 图片
     */
    private String image;
    /**
     * 评分
     */
    private BigDecimal score;


}