package cn.iocoder.yudao.module.scenic.dal.mysql.spot;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.scenic.dal.dataobject.spot.SpotDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.scenic.controller.admin.spot.vo.*;

/**
 * 景点信息 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface SpotMapper extends BaseMapperX<SpotDO> {

    default PageResult<SpotDO> selectPage(SpotPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SpotDO>()
                .likeIfPresent(SpotDO::getName, reqVO.getName())
                .likeIfPresent(SpotDO::getLocation, reqVO.getLocation())
                .betweenIfPresent(SpotDO::getTicketPrice, reqVO.getTicketPrice())
                .eqIfPresent(SpotDO::getDescription, reqVO.getDescription())
                .orderByDesc(SpotDO::getId));
    }

}