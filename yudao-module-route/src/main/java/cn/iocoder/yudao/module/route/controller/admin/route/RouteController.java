package cn.iocoder.yudao.module.route.controller.admin.route;

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

import cn.iocoder.yudao.module.route.controller.admin.route.vo.*;
import cn.iocoder.yudao.module.route.dal.dataobject.route.RouteDO;
import cn.iocoder.yudao.module.route.service.route.RouteService;
import cn.iocoder.yudao.module.route.controller.admin.route.vo.*;

@Tag(name = "管理后台 - 旅游线路")
@RestController
@RequestMapping("/route/route")
@Validated
public class RouteController {

    @Resource
    private RouteService routeService;

    @Resource
    private AdminUserService adminUserService;

    @PostMapping("/create")
    @Operation(summary = "创建旅游线路")
    @PreAuthorize("@ss.hasPermission('route:route:create')")
    public CommonResult<Long> createRoute(@Valid @RequestBody RouteSaveReqVO createReqVO) {
        return success(routeService.createRoute(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新旅游线路")
    public CommonResult<Boolean> updateRoute(@Valid @RequestBody RouteSaveReqVO updateReqVO) {
        routeService.updateRoute(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除旅游线路")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('route:route:delete')")
    public CommonResult<Boolean> deleteRoute(@RequestParam("id") Long id) {
        routeService.deleteRoute(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除旅游线路")
                @PreAuthorize("@ss.hasPermission('route:route:delete')")
    public CommonResult<Boolean> deleteRouteList(@RequestParam("ids") List<Long> ids) {
        routeService.deleteRouteListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得旅游线路")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('route:route:query')")
    public CommonResult<RouteRespVO> getRoute(@RequestParam("id") Long id) {
        RouteDO route = routeService.getRoute(id);
        RouteRespVO bean = BeanUtils.toBean(route, RouteRespVO.class);
        bean.setUserNickname(adminUserService.getUser(Long.valueOf(route.getCreator())).getNickname());
        return success(bean);
    }

    @GetMapping("/page")
    @Operation(summary = "获得旅游线路分页")
    @PreAuthorize("@ss.hasPermission('route:route:query')")
    public CommonResult<PageResult<RoutePageRespVO>> getRoutePage(@Valid RoutePageReqVO pageReqVO) {
        return success(routeService.getRoutePage(pageReqVO));

    }


    @GetMapping("/getMyRoute")
    @Operation(summary = "获得我的旅游线路分页")
    public CommonResult<PageResult<RoutePageRespVO>> getMyRoute(@Valid RoutePageReqVO pageReqVO) {
        return success(routeService.getMyRoute(pageReqVO));
    }


}