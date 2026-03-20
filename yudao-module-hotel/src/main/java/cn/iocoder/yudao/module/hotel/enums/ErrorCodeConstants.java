package cn.iocoder.yudao.module.hotel.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {
    ErrorCode HOTEL_NOT_EXISTS = new ErrorCode(100001, "酒店信息不存在");
}
