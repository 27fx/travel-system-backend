package cn.iocoder.yudao.module.hotel.controller.admin.hotel;

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

import cn.iocoder.yudao.module.hotel.controller.admin.hotel.vo.*;
import cn.iocoder.yudao.module.hotel.dal.dataobject.hotel.HotelDO;
import cn.iocoder.yudao.module.hotel.service.hotel.HotelService;

@Tag(name = "管理后台 - 酒店信息")
@RestController
@RequestMapping("/hotel/hotel")
@Validated
public class HotelController {

    @Resource
    private HotelService hotelService;

    @PostMapping("/create")
    @Operation(summary = "创建酒店信息")
    @PreAuthorize("@ss.hasPermission('hotel:hotel:create')")
    public CommonResult<Long> createHotel(@Valid @RequestBody HotelSaveReqVO createReqVO) {
        return success(hotelService.createHotel(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新酒店信息")
    @PreAuthorize("@ss.hasPermission('hotel:hotel:update')")
    public CommonResult<Boolean> updateHotel(@Valid @RequestBody HotelSaveReqVO updateReqVO) {
        hotelService.updateHotel(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除酒店信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('hotel:hotel:delete')")
    public CommonResult<Boolean> deleteHotel(@RequestParam("id") Long id) {
        hotelService.deleteHotel(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除酒店信息")
                @PreAuthorize("@ss.hasPermission('hotel:hotel:delete')")
    public CommonResult<Boolean> deleteHotelList(@RequestParam("ids") List<Long> ids) {
        hotelService.deleteHotelListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得酒店信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('hotel:hotel:query')")
    public CommonResult<HotelRespVO> getHotel(@RequestParam("id") Long id) {
        HotelDO hotel = hotelService.getHotel(id);
        return success(BeanUtils.toBean(hotel, HotelRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得酒店信息分页")
    @PreAuthorize("@ss.hasPermission('hotel:hotel:query')")
    public CommonResult<PageResult<HotelRespVO>> getHotelPage(@Valid HotelPageReqVO pageReqVO) {
        PageResult<HotelDO> pageResult = hotelService.getHotelPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, HotelRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出酒店信息 Excel")
    @PreAuthorize("@ss.hasPermission('hotel:hotel:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportHotelExcel(@Valid HotelPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<HotelDO> list = hotelService.getHotelPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "酒店信息.xls", "数据", HotelRespVO.class,
                        BeanUtils.toBean(list, HotelRespVO.class));
    }

}