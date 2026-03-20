package cn.iocoder.yudao.module.food.dal.dataobject.foodComment;

import lombok.*;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 美食评论 DO
 *
 * @author 芋道源码
 */
@TableName("food_comment")
@KeySequence("food_comment_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDO extends BaseDO {

    /**
     * 评论ID
     */
    @TableId
    private Long id;
    /**
     * 关联美食ID
     */
    private Long foodId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户名快照
     */
    private String userName;
    /**
     * 用户头像快照
     */
    private String userAvatar;
    /**
     * 父评论ID (0表示一级评论，非0表示回复某条评论)
     */
    private Long parentId;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 评分 (1-5分，一级评论必填，回复可选)
     */
    private BigDecimal score;
    /**
     * 点赞数
     */
    private Integer likeCount;
    /**
     * 状态 (0: 待审核, 1: 正常, 2: 屏蔽/违规)
     */
    private Integer status;


}
