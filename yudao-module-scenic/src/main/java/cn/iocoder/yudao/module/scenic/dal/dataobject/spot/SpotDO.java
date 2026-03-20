package cn.iocoder.yudao.module.scenic.dal.dataobject.spot;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 景点信息 DO
 *
 * @author 芋道源码
 */
@TableName("scenic_spot")
@KeySequence("scenic_spot_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotDO extends BaseDO {

    /**
     * 景点ID
     */
    @TableId
    private Long id;
    /**
     * 景点名称
     */
    private String name;
    /**
     * 景点地址
     */
    private String location;
    /**
     * 门票价格
     */
    private BigDecimal ticketPrice;
    /**
     * 景点介绍
     */
    private String description;
    /**
     * 景点图片
     */
    private String image;
    /**
     * 评分
     */
    private BigDecimal score;


}