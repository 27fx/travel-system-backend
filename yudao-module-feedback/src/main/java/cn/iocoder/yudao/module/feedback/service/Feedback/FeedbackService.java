package cn.iocoder.yudao.module.feedback.service.Feedback;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.feedback.controller.admin.Feedback.vo.*;
import cn.iocoder.yudao.module.feedback.dal.dataobject.Feedback.FeedbackDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 用户反馈 Service 接口
 *
 * @author 芋道源码
 */
public interface FeedbackService {

    /**
     * 创建用户反馈
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createFeedback(@Valid FeedbackSaveReqVO createReqVO);

    /**
     * 更新用户反馈
     *
     * @param updateReqVO 更新信息
     */
    void updateFeedback(@Valid FeedbackSaveReqVO updateReqVO);

    /**
     * 删除用户反馈
     *
     * @param id 编号
     */
    void deleteFeedback(Long id);

    /**
    * 批量删除用户反馈
    *
    * @param ids 编号
    */
    void deleteFeedbackListByIds(List<Long> ids);

    /**
     * 获得用户反馈
     *
     * @param id 编号
     * @return 用户反馈
     */
    FeedbackDO getFeedback(Long id);

    /**
     * 获得用户反馈分页
     *
     * @param pageReqVO 分页查询
     * @return 用户反馈分页
     */
    PageResult<FeedbackDO> getFeedbackPage(FeedbackPageReqVO pageReqVO);


    PageResult<FeedbackPageRespVO> getFeedbackPageVO(FeedbackPageReqVO pageReqVO);

}