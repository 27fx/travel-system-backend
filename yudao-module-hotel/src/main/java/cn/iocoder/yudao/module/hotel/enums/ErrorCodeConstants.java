package cn.iocoder.yudao.module.hotel.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {
    ErrorCode HOTEL_NOT_EXISTS = new ErrorCode(100001, "酒店信息不存在");

    ErrorCode HOTEL_COMMENT_NOT_EXISTS = new ErrorCode(100002, "酒店评论不存在");
    ErrorCode HOTEL_COMMENT_STATUS_ERROR = new ErrorCode(100003, "评论状态异常");
    ErrorCode USER_STATUS_ERROR = new ErrorCode(100004, "用户状态异常");
}
