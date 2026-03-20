package cn.iocoder.yudao.module.scenic.controller.admin.spot;

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

import cn.iocoder.yudao.module.scenic.controller.admin.spot.vo.*;
import cn.iocoder.yudao.module.scenic.dal.dataobject.spot.SpotDO;
import cn.iocoder.yudao.module.scenic.service.spot.SpotService;

@Tag(name = "管理后台 - 景点信息")
@RestController
@RequestMapping("/scenic/spot")
@Validated
public class SpotController {

    @Resource
    private SpotService spotService;

    @PostMapping("/create")
    @Operation(summary = "创建景点信息")
    @PreAuthorize("@ss.hasPermission('scenic:spot:create')")
    public CommonResult<Long> createSpot(@Valid @RequestBody SpotSaveReqVO createReqVO) {
        return success(spotService.createSpot(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新景点信息")
    @PreAuthorize("@ss.hasPermission('scenic:spot:update')")
    public CommonResult<Boolean> updateSpot(@Valid @RequestBody SpotSaveReqVO updateReqVO) {
        spotService.updateSpot(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除景点信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('scenic:spot:delete')")
    public CommonResult<Boolean> deleteSpot(@RequestParam("id") Long id) {
        spotService.deleteSpot(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除景点信息")
                @PreAuthorize("@ss.hasPermission('scenic:spot:delete')")
    public CommonResult<Boolean> deleteSpotList(@RequestParam("ids") List<Long> ids) {
        spotService.deleteSpotListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得景点信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('scenic:spot:query')")
    public CommonResult<SpotRespVO> getSpot(@RequestParam("id") Long id) {
        SpotDO spot = spotService.getSpot(id);
        return success(BeanUtils.toBean(spot, SpotRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得景点信息分页")
    @PreAuthorize("@ss.hasPermission('scenic:spot:query')")
    public CommonResult<PageResult<SpotRespVO>> getSpotPage(@Valid SpotPageReqVO pageReqVO) {
        PageResult<SpotDO> pageResult = spotService.getSpotPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SpotRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出景点信息 Excel")
    @PreAuthorize("@ss.hasPermission('scenic:spot:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSpotExcel(@Valid SpotPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SpotDO> list = spotService.getSpotPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "景点信息.xls", "数据", SpotRespVO.class,
                        BeanUtils.toBean(list, SpotRespVO.class));
    }

}