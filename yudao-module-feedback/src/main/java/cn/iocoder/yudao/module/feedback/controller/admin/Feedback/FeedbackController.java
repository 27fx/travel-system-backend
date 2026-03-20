package cn.iocoder.yudao.module.feedback.controller.admin.Feedback;

import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.feedback.controller.admin.Feedback.vo.*;
import cn.iocoder.yudao.module.feedback.dal.dataobject.Feedback.FeedbackDO;
import cn.iocoder.yudao.module.feedback.service.Feedback.FeedbackService;

@Tag(name = "管理后台 - 用户反馈")
@RestController
@RequestMapping("/feedback/feedback")
@Validated
public class FeedbackController {

    @Resource
    private FeedbackService feedbackService;

    @Resource
    private AdminUserService adminUserService;


    @PostMapping("/create")
    @Operation(summary = "创建用户反馈")
    public CommonResult<Long> createFeedback(@Valid @RequestBody FeedbackSaveReqVO createReqVO) {
        return success(feedbackService.createFeedback(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户反馈")
    @PreAuthorize("@ss.hasPermission('feedback:feedback:update')")
    public CommonResult<Boolean> updateFeedback(@Valid @RequestBody FeedbackSaveReqVO updateReqVO) {
        feedbackService.updateFeedback(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户反馈")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('feedback:feedback:delete')")
    public CommonResult<Boolean> deleteFeedback(@RequestParam("id") Long id) {
        feedbackService.deleteFeedback(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除用户反馈")
                @PreAuthorize("@ss.hasPermission('feedback:feedback:delete')")
    public CommonResult<Boolean> deleteFeedbackList(@RequestParam("ids") List<Long> ids) {
        feedbackService.deleteFeedbackListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户反馈")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('feedback:feedback:query')")
    public CommonResult<FeedbackRespVO> getFeedback(@RequestParam("id") Long id) {
        FeedbackDO feedback = feedbackService.getFeedback(id);
        FeedbackRespVO bean = BeanUtils.toBean(feedback, FeedbackRespVO.class);
        bean.setUserNickname(adminUserService.getUser(feedback.getUserId()).getNickname());
        return success(bean);
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户反馈分页")
    @PreAuthorize("@ss.hasPermission('feedback:feedback:query')")
    public CommonResult<PageResult<FeedbackPageRespVO>> getFeedbackPage(@Valid FeedbackPageReqVO pageReqVO) {
        return success(feedbackService.getFeedbackPageVO(pageReqVO));
    }


}