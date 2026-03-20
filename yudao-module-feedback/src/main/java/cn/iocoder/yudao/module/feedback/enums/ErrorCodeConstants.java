package cn.iocoder.yudao.module.feedback.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {
    ErrorCode FEEDBACK_NOT_EXISTS = new ErrorCode(100001, "用户反馈不存在");
}
