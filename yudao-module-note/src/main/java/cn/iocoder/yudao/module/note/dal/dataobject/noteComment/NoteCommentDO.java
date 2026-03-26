package cn.iocoder.yudao.module.note.dal.dataobject.noteComment;

import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.math.BigDecimal;

/**
 * 旅游笔记评论 DO
 */
@TableName("travel_note_comment")
@KeySequence("travel_note_comment_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteCommentDO extends BaseDO {

    /**
     * 评论 ID
     */
    @TableId
    private Long id;

    /**
     * 关联游记 ID
     */
    private Long noteId;

    /**
     * 用户 ID
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
     * 父评论 ID (0 表示一级评论，非 0 表示回复某条评论)
     */
    private Long parentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评分 (1-5 分，一级评论必填，回复可选)
     */
    private BigDecimal score;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 状态 (0: 待审核，1: 正常，2: 屏蔽/违规)
     */
    private Integer status;
}
