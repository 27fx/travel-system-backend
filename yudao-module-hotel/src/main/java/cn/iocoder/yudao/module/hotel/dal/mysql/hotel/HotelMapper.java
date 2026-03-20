package cn.iocoder.yudao.module.hotel.dal.mysql.hotel;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.hotel.dal.dataobject.hotel.HotelDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.hotel.controller.admin.hotel.vo.*;

/**
 * 酒店信息 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface HotelMapper extends BaseMapperX<HotelDO> {

    default PageResult<HotelDO> selectPage(HotelPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<HotelDO>()
                .likeIfPresent(HotelDO::getName, reqVO.getName())
                .likeIfPresent(HotelDO::getLocation, reqVO.getLocation())
                .likeIfPresent(HotelDO::getDescription, reqVO.getDescription())
                .orderByDesc(HotelDO::getId));
    }

}