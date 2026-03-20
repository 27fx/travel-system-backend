package cn.iocoder.yudao.module.note.dal.dataobject.note;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 游记 DO
 *
 * @author 芋道源码
 */
@TableName("travel_note")
@KeySequence("travel_note_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteDO extends BaseDO {

    /**
     * 游记ID
     */
    @TableId
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 游记标题
     */
    private String title;
    /**
     * 游记内容
     */
    private String content;
    /**
     * 游记图片
     */
    private String image;


}