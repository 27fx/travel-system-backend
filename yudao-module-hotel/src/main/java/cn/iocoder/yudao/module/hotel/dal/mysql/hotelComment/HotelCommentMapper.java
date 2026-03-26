
package cn.iocoder.yudao.module.hotel.dal.mysql.hotelComment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hotel.controller.admin.hotelComment.vo.HotelCommentPageReqVO;
import cn.iocoder.yudao.module.hotel.dal.dataobject.hotelComment.HotelCommentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HotelCommentMapper extends BaseMapperX<HotelCommentDO> {

    default PageResult<HotelCommentDO> selectPage(HotelCommentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<HotelCommentDO>()
                .eqIfPresent(HotelCommentDO::getHotelId, reqVO.getHotelId())
                .eqIfPresent(HotelCommentDO::getParentId, reqVO.getParentId())
                .eqIfPresent(HotelCommentDO::getStatus, reqVO.getStatus())
                .likeIfPresent(HotelCommentDO::getContent, reqVO.getContent())
                .orderByDesc(HotelCommentDO::getCreateTime));
    }

    default List<HotelCommentDO> selectByHotelId(Long hotelId, int page, int size) {
        LambdaQueryWrapperX<HotelCommentDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eq(HotelCommentDO::getHotelId, hotelId)
               .eq(HotelCommentDO::getParentId, 0L)
               .eq(HotelCommentDO::getStatus, 1)
               .eq(HotelCommentDO::getDeleted, 0)
               .orderByDesc(HotelCommentDO::getCreateTime);
        return selectList(wrapper);
    }

    default List<HotelCommentDO> selectRepliesByParentId(Long parentId) {
        return selectList(new LambdaQueryWrapperX<HotelCommentDO>()
                .eq(HotelCommentDO::getParentId, parentId)
                .eq(HotelCommentDO::getStatus, 1)
                .eq(HotelCommentDO::getDeleted, 0)
                .orderByAsc(HotelCommentDO::getCreateTime));
    }
}
