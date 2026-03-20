package cn.iocoder.yudao.module.feedback.dal.dataobject.Feedback;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 用户反馈 DO
 *
 * @author 芋道源码
 */
@TableName("feedback")
@KeySequence("feedback_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDO extends BaseDO {

    /**
     * 反馈ID
     */
    @TableId
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 反馈内容
     */
    private String content;
    /**
     * 管理员回复
     */
    private String reply;
    /**
     * 处理状态
     *
     * 枚举 {@link TODO feedback_type 对应的类}
     */
    private String status;


}