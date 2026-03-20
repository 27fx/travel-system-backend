package cn.iocoder.yudao.module.hotel.service.hotel;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.hotel.controller.admin.hotel.vo.*;
import cn.iocoder.yudao.module.hotel.dal.dataobject.hotel.HotelDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.hotel.dal.mysql.hotel.HotelMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.hotel.enums.ErrorCodeConstants.*;

/**
 * 酒店信息 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class HotelServiceImpl implements HotelService {

    @Resource
    private HotelMapper hotelMapper;

    @Override
    public Long createHotel(HotelSaveReqVO createReqVO) {
        // 插入
        HotelDO hotel = BeanUtils.toBean(createReqVO, HotelDO.class);
        hotelMapper.insert(hotel);

        // 返回
        return hotel.getId();
    }

    @Override
    public void updateHotel(HotelSaveReqVO updateReqVO) {
        // 校验存在
        validateHotelExists(updateReqVO.getId());
        // 更新
        HotelDO updateObj = BeanUtils.toBean(updateReqVO, HotelDO.class);
        hotelMapper.updateById(updateObj);
    }

    @Override
    public void deleteHotel(Long id) {
        // 校验存在
        validateHotelExists(id);
        // 删除
        hotelMapper.deleteById(id);
    }

    @Override
        public void deleteHotelListByIds(List<Long> ids) {
        // 删除
        hotelMapper.deleteByIds(ids);
        }


    private void validateHotelExists(Long id) {
        if (hotelMapper.selectById(id) == null) {
            throw exception(HOTEL_NOT_EXISTS);
        }
    }

    @Override
    public HotelDO getHotel(Long id) {
        return hotelMapper.selectById(id);
    }

    @Override
    public PageResult<HotelDO> getHotelPage(HotelPageReqVO pageReqVO) {
        return hotelMapper.selectPage(pageReqVO);
    }

}