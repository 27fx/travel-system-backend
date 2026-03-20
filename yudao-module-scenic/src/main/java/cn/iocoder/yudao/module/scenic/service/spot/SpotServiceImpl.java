package cn.iocoder.yudao.module.scenic.service.spot;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.scenic.controller.admin.spot.vo.*;
import cn.iocoder.yudao.module.scenic.dal.dataobject.spot.SpotDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.scenic.dal.mysql.spot.SpotMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.scenic.enums.ErrorCodeConstants.*;

/**
 * 景点信息 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class SpotServiceImpl implements SpotService {

    @Resource
    private SpotMapper spotMapper;

    @Override
    public Long createSpot(SpotSaveReqVO createReqVO) {
        // 插入
        SpotDO spot = BeanUtils.toBean(createReqVO, SpotDO.class);
        spotMapper.insert(spot);

        // 返回
        return spot.getId();
    }

    @Override
    public void updateSpot(SpotSaveReqVO updateReqVO) {
        // 校验存在
        validateSpotExists(updateReqVO.getId());
        // 更新
        SpotDO updateObj = BeanUtils.toBean(updateReqVO, SpotDO.class);
        spotMapper.updateById(updateObj);
    }

    @Override
    public void deleteSpot(Long id) {
        // 校验存在
        validateSpotExists(id);
        // 删除
        spotMapper.deleteById(id);
    }

    @Override
        public void deleteSpotListByIds(List<Long> ids) {
        // 删除
        spotMapper.deleteByIds(ids);
        }


    private void validateSpotExists(Long id) {
        if (spotMapper.selectById(id) == null) {
            throw exception(SPOT_NOT_EXISTS);
        }
    }

    @Override
    public SpotDO getSpot(Long id) {
        return spotMapper.selectById(id);
    }

    @Override
    public PageResult<SpotDO> getSpotPage(SpotPageReqVO pageReqVO) {
        return spotMapper.selectPage(pageReqVO);
    }

}