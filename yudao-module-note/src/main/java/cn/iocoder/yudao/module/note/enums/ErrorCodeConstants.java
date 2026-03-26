package cn.iocoder.yudao.module.note.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {

    ErrorCode NOTE_NOT_EXISTS = new ErrorCode(10001, "游记不存在");

    ErrorCode NOTE_COMMENT_NOT_EXISTS = new ErrorCode(10002, "评论不存在");

    ErrorCode NOTE_COMMENTS_NOT_WITH_SCORE = new ErrorCode(10003, "一级评论请输入 1-5 分的评分");

    ErrorCode NOTE_USER_STATUS_ERROR = new ErrorCode(10004, "用户状态异常");

    ErrorCode NOTE_LIKE_REPEAT = new ErrorCode(10005, "请勿重复点赞");
}
