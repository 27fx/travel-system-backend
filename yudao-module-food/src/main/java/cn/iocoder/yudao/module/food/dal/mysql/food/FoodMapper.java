package cn.iocoder.yudao.module.food.dal.mysql.food;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.food.dal.dataobject.food.FoodDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.food.controller.admin.food.vo.*;

/**
 * 美食信息 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface FoodMapper extends BaseMapperX<FoodDO> {

    default PageResult<FoodDO> selectPage(FoodPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FoodDO>()
                .likeIfPresent(FoodDO::getName, reqVO.getName())
                .eqIfPresent(FoodDO::getLocation, reqVO.getLocation())
                .eqIfPresent(FoodDO::getPrice, reqVO.getPrice())
                .eqIfPresent(FoodDO::getDescription, reqVO.getDescription())
                .eqIfPresent(FoodDO::getImage, reqVO.getImage())
                .eqIfPresent(FoodDO::getScore, reqVO.getScore())
                .orderByDesc(FoodDO::getId));
    }

}