package cn.iocoder.yudao.module.route.dal.dataobject.route;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 旅游线路 DO
 *
 * @author 芋道源码
 */
@TableName("route")
@KeySequence("route_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteDO extends BaseDO {

    /**
     * 线路ID
     */
    @TableId
    private Long id;
    /**
     * 线路名称
     */
    private String name;
    /**
     * 旅游天数
     */
    private Integer days;
    /**
     * 线路价格
     */
    private BigDecimal price;
    /**
     * 线路简介
     */
    private String introduction;
    /**
     * 线路介绍
     */
    private String description;
    /**
     * 线路图片
     */
    private String image;


}