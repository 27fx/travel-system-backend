package cn.iocoder.yudao.module.route.service.route;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.route.controller.admin.route.vo.*;
import cn.iocoder.yudao.module.route.dal.dataobject.route.RouteDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 旅游线路 Service 接口
 *
 * @author 芋道源码
 */
public interface RouteService {

    /**
     * 创建旅游线路
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createRoute(@Valid RouteSaveReqVO createReqVO);

    /**
     * 更新旅游线路
     *
     * @param updateReqVO 更新信息
     */
    void updateRoute(@Valid RouteSaveReqVO updateReqVO);

    /**
     * 删除旅游线路
     *
     * @param id 编号
     */
    void deleteRoute(Long id);

    /**
    * 批量删除旅游线路
    *
    * @param ids 编号
    */
    void deleteRouteListByIds(List<Long> ids);

    /**
     * 获得旅游线路
     *
     * @param id 编号
     * @return 旅游线路
     */
    RouteDO getRoute(Long id);

    /**
     * 获得旅游线路分页
     *
     * @param pageReqVO 分页查询
     * @return 旅游线路分页
     */
    PageResult<RoutePageRespVO> getRoutePage(RoutePageReqVO pageReqVO);

    PageResult<RoutePageRespVO> getMyRoute(RoutePageReqVO pageReqVO);
}