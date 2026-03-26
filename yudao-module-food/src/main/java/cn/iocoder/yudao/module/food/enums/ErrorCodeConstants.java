package cn.iocoder.yudao.module.food.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {
    ErrorCode FOOD_NOT_EXISTS = new ErrorCode(1_001_201_000, "美食信息不存在");
    ErrorCode COMMENTS_NOT_WITH_SCORE = new ErrorCode(1_001_201_001,"一级评论请输入1-5分的评分");

    ErrorCode USER_STATUS_ERROR = new ErrorCode(1_001_201_002,"用户状态异常");

    ErrorCode LIKE_REPEAT = new ErrorCode(1_001_201_003,"请勿重复点赞");
}
