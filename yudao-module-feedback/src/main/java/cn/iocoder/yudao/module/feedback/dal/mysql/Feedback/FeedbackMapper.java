package cn.iocoder.yudao.module.feedback.dal.mysql.Feedback;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.feedback.dal.dataobject.Feedback.FeedbackDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.feedback.controller.admin.Feedback.vo.*;
import org.apache.ibatis.annotations.Param;

/**
 * 用户反馈 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface FeedbackMapper extends BaseMapperX<FeedbackDO> {

    default PageResult<FeedbackDO> selectPage(FeedbackPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FeedbackDO>()
                .eqIfPresent(FeedbackDO::getContent, reqVO.getContent())
                .eqIfPresent(FeedbackDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(FeedbackDO::getCreateTime, reqVO.getCreateTime())
                .betweenIfPresent(FeedbackDO::getUpdateTime, reqVO.getUpdateTime())
                .orderByDesc(FeedbackDO::getId));
    }

    List<FeedbackPageRespVO> selectPageList(@Param("reqVO") FeedbackPageReqVO reqVO,
                                        @Param("offset") Integer offset,
                                        @Param("pageSize")Integer pageSize);


    Long selectPageCount(@Param("reqVO") FeedbackPageReqVO reqVO);

    List<FeedbackPageRespVO> selectPageVOList(@Param("reqVO") FeedbackPageReqVO reqVO);

}