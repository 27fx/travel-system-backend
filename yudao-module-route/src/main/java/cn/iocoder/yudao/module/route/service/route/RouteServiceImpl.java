package cn.iocoder.yudao.module.route.service.route;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.route.controller.admin.route.vo.*;
import cn.iocoder.yudao.module.route.dal.dataobject.route.RouteDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.route.dal.mysql.route.RouteMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.route.enums.ErrorCodeConstants.*;

/**
 * 旅游线路 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class RouteServiceImpl implements RouteService {

    @Resource
    private RouteMapper routeMapper;

    @Override
    public Long createRoute(RouteSaveReqVO createReqVO) {
        // 插入
        RouteDO route = BeanUtils.toBean(createReqVO, RouteDO.class);
        routeMapper.insert(route);

        // 返回
        return route.getId();
    }

    @Override
    public void updateRoute(RouteSaveReqVO updateReqVO) {
        // 校验存在
        validateRouteExists(updateReqVO.getId());
        // 更新
        RouteDO updateObj = BeanUtils.toBean(updateReqVO, RouteDO.class);
        routeMapper.updateById(updateObj);
    }

    @Override
    public void deleteRoute(Long id) {
        // 校验存在
        validateRouteExists(id);
        // 删除
        routeMapper.deleteById(id);
    }

    @Override
        public void deleteRouteListByIds(List<Long> ids) {
        // 删除
        routeMapper.deleteByIds(ids);
        }


    private void validateRouteExists(Long id) {
        if (routeMapper.selectById(id) == null) {
            throw exception(ROUTE_NOT_EXISTS);
        }
    }

    @Override
    public RouteDO getRoute(Long id) {
        return routeMapper.selectById(id);
    }

    @Override
    public PageResult<RoutePageRespVO> getRoutePage(RoutePageReqVO pageReqVO) {
        int offset = (pageReqVO.getPageNo() - 1) * pageReqVO.getPageSize();
        List<RoutePageRespVO> list = routeMapper.selectPageList(pageReqVO, offset, pageReqVO.getPageSize());
        return new PageResult<>(list, routeMapper.selectPageCount(pageReqVO));

    }

    @Override
    public PageResult<RoutePageRespVO> getMyRoute(RoutePageReqVO pageReqVO) {
        int offset = (pageReqVO.getPageNo() - 1) * pageReqVO.getPageSize();
        pageReqVO.setUserId(SecurityFrameworkUtils.getLoginUserId());
        List<RoutePageRespVO> list = routeMapper.selectPageList(pageReqVO, offset, pageReqVO.getPageSize());
        return new PageResult<>(list, routeMapper.selectPageCount(pageReqVO));
    }

}