package cn.iocoder.yudao.module.route.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {
    ErrorCode ROUTE_NOT_EXISTS = new ErrorCode(100001, "旅游线路不存在");

    // 线路评论相关错误码
    ErrorCode ROUTE_COMMENT_STATUS_ERROR = new ErrorCode(100100, "评分必须在 1-5 分之间");
    ErrorCode USER_STATUS_ERROR = new ErrorCode(100101, "用户状态异常");

}
