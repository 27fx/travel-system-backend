package cn.iocoder.yudao.module.hotel.service.hotel;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.hotel.controller.admin.hotel.vo.*;
import cn.iocoder.yudao.module.hotel.dal.dataobject.hotel.HotelDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 酒店信息 Service 接口
 *
 * @author 芋道源码
 */
public interface HotelService {

    /**
     * 创建酒店信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createHotel(@Valid HotelSaveReqVO createReqVO);

    /**
     * 更新酒店信息
     *
     * @param updateReqVO 更新信息
     */
    void updateHotel(@Valid HotelSaveReqVO updateReqVO);

    /**
     * 删除酒店信息
     *
     * @param id 编号
     */
    void deleteHotel(Long id);

    /**
    * 批量删除酒店信息
    *
    * @param ids 编号
    */
    void deleteHotelListByIds(List<Long> ids);

    /**
     * 获得酒店信息
     *
     * @param id 编号
     * @return 酒店信息
     */
    HotelDO getHotel(Long id);

    /**
     * 获得酒店信息分页
     *
     * @param pageReqVO 分页查询
     * @return 酒店信息分页
     */
    PageResult<HotelDO> getHotelPage(HotelPageReqVO pageReqVO);

}