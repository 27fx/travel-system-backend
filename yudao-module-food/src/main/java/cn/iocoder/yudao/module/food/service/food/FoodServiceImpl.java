package cn.iocoder.yudao.module.food.service.food;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.food.controller.admin.food.vo.*;
import cn.iocoder.yudao.module.food.dal.dataobject.food.FoodDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.food.dal.mysql.food.FoodMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.food.enums.ErrorCodeConstants.*;

/**
 * 美食信息 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class FoodServiceImpl implements FoodService {

    @Resource
    private FoodMapper foodMapper;

    @Override
    public Long createFood(FoodSaveReqVO createReqVO) {
        // 插入
        FoodDO food = BeanUtils.toBean(createReqVO, FoodDO.class);
        foodMapper.insert(food);

        // 返回
        return food.getId();
    }

    @Override
    public void updateFood(FoodSaveReqVO updateReqVO) {
        // 校验存在
        validateFoodExists(updateReqVO.getId());
        // 更新
        FoodDO updateObj = BeanUtils.toBean(updateReqVO, FoodDO.class);
        foodMapper.updateById(updateObj);
    }

    @Override
    public void deleteFood(Long id) {
        // 校验存在
        validateFoodExists(id);
        // 删除
        foodMapper.deleteById(id);
    }

    @Override
        public void deleteFoodListByIds(List<Long> ids) {
        // 删除
        foodMapper.deleteByIds(ids);
        }


    private void validateFoodExists(Long id) {
        if (foodMapper.selectById(id) == null) {
            throw exception(FOOD_NOT_EXISTS);
        }
    }

    @Override
    public FoodDO getFood(Long id) {
        return foodMapper.selectById(id);
    }

    @Override
    public PageResult<FoodDO> getFoodPage(FoodPageReqVO pageReqVO) {
        return foodMapper.selectPage(pageReqVO);
    }

}