package cn.iocoder.yudao.module.scenic.service.spot;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.scenic.controller.admin.spot.vo.*;
import cn.iocoder.yudao.module.scenic.dal.dataobject.spot.SpotDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 景点信息 Service 接口
 *
 * @author 芋道源码
 */
public interface SpotService {

    /**
     * 创建景点信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSpot(@Valid SpotSaveReqVO createReqVO);

    /**
     * 更新景点信息
     *
     * @param updateReqVO 更新信息
     */
    void updateSpot(@Valid SpotSaveReqVO updateReqVO);

    /**
     * 删除景点信息
     *
     * @param id 编号
     */
    void deleteSpot(Long id);

    /**
    * 批量删除景点信息
    *
    * @param ids 编号
    */
    void deleteSpotListByIds(List<Long> ids);

    /**
     * 获得景点信息
     *
     * @param id 编号
     * @return 景点信息
     */
    SpotDO getSpot(Long id);

    /**
     * 获得景点信息分页
     *
     * @param pageReqVO 分页查询
     * @return 景点信息分页
     */
    PageResult<SpotDO> getSpotPage(SpotPageReqVO pageReqVO);

}