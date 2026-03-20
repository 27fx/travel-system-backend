package cn.iocoder.yudao.module.feedback.service.Feedback;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.feedback.controller.admin.Feedback.vo.*;
import cn.iocoder.yudao.module.feedback.dal.dataobject.Feedback.FeedbackDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.feedback.dal.mysql.Feedback.FeedbackMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.feedback.enums.ErrorCodeConstants.*;

/**
 * 用户反馈 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class FeedbackServiceImpl implements FeedbackService {

    @Resource
    private FeedbackMapper feedbackMapper;

    @Override
    public Long createFeedback(FeedbackSaveReqVO createReqVO) {
        // 插入
        FeedbackDO feedback = BeanUtils.toBean(createReqVO, FeedbackDO.class);
        feedback.setUserId(SecurityFrameworkUtils.getLoginUserId());
        //未处理
        feedback.setStatus("0");
        feedbackMapper.insert(feedback);
        // 返回
        return feedback.getId();
    }

    @Override
    public void updateFeedback(FeedbackSaveReqVO updateReqVO) {
        // 校验存在
        validateFeedbackExists(updateReqVO.getId());
        // 更新
        FeedbackDO updateObj = BeanUtils.toBean(updateReqVO, FeedbackDO.class);
        feedbackMapper.updateById(updateObj);
    }

    @Override
    public void deleteFeedback(Long id) {
        // 校验存在
        validateFeedbackExists(id);
        // 删除
        feedbackMapper.deleteById(id);
    }

    @Override
        public void deleteFeedbackListByIds(List<Long> ids) {
        // 删除
        feedbackMapper.deleteByIds(ids);
        }


    private void validateFeedbackExists(Long id) {
        if (feedbackMapper.selectById(id) == null) {
            throw exception(FEEDBACK_NOT_EXISTS);
        }
    }

    @Override
    public FeedbackDO getFeedback(Long id) {
        return feedbackMapper.selectById(id);
    }

    @Override
    public PageResult<FeedbackDO> getFeedbackPage(FeedbackPageReqVO pageReqVO) {
        return feedbackMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<FeedbackPageRespVO> getFeedbackPageVO(FeedbackPageReqVO pageReqVO) {
        int offset = (pageReqVO.getPageNo() - 1) * pageReqVO.getPageSize();
        List<FeedbackPageRespVO> list = feedbackMapper.selectPageList(pageReqVO, offset, pageReqVO.getPageSize());
        return new PageResult<>(list, feedbackMapper.selectPageCount(pageReqVO));
    }

}