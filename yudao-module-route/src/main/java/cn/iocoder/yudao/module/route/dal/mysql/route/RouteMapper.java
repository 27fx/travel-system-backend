package cn.iocoder.yudao.module.route.dal.mysql.route;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.route.dal.dataobject.route.RouteDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.route.controller.admin.route.vo.*;

import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Param;

/**
 * 旅游线路 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface RouteMapper extends BaseMapperX<RouteDO> {

    default PageResult<RouteDO> selectPage(RoutePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<RouteDO>()
                .likeIfPresent(RouteDO::getName, reqVO.getName())
                .betweenIfPresent(RouteDO::getDays, reqVO.getDays())
                .betweenIfPresent(RouteDO::getPrice, reqVO.getPrice())
                .betweenIfPresent(RouteDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(RouteDO::getId));
    }
    List<RoutePageRespVO> selectPageList(@Param("reqVO") RoutePageReqVO reqVO,
                                        @Param("offset") Integer offset,
                                        @Param("pageSize")Integer pageSize);


    Long selectPageCount(@Param("reqVO") RoutePageReqVO reqVO);

    List<RoutePageRespVO> selectPageVOList(@Param("reqVO") RoutePageReqVO reqVO);

}