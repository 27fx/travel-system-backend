package cn.iocoder.yudao.module.food.service.food;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.food.controller.admin.food.vo.*;
import cn.iocoder.yudao.module.food.dal.dataobject.food.FoodDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 美食信息 Service 接口
 *
 * @author 芋道源码
 */
public interface FoodService {

    /**
     * 创建美食信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createFood(@Valid FoodSaveReqVO createReqVO);

    /**
     * 更新美食信息
     *
     * @param updateReqVO 更新信息
     */
    void updateFood(@Valid FoodSaveReqVO updateReqVO);

    /**
     * 删除美食信息
     *
     * @param id 编号
     */
    void deleteFood(Long id);

    /**
    * 批量删除美食信息
    *
    * @param ids 编号
    */
    void deleteFoodListByIds(List<Long> ids);

    /**
     * 获得美食信息
     *
     * @param id 编号
     * @return 美食信息
     */
    FoodDO getFood(Long id);

    /**
     * 获得美食信息分页
     *
     * @param pageReqVO 分页查询
     * @return 美食信息分页
     */
    PageResult<FoodDO> getFoodPage(FoodPageReqVO pageReqVO);

}