
package cn.iocoder.yudao.module.scenic.dal.mysql.spotComment;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.scenic.controller.admin.spotComment.vo.SpotCommentPageReqVO;
import cn.iocoder.yudao.module.scenic.dal.dataobject.spotComment.SpotCommentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SpotCommentMapper extends BaseMapperX<SpotCommentDO> {

    default PageResult<SpotCommentDO> selectPage(SpotCommentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SpotCommentDO>()
                .eqIfPresent(SpotCommentDO::getSpotId, reqVO.getSpotId())
                .eqIfPresent(SpotCommentDO::getParentId, reqVO.getParentId())
                .eqIfPresent(SpotCommentDO::getStatus, reqVO.getStatus())
                .likeIfPresent(SpotCommentDO::getContent, reqVO.getContent())
                .orderByDesc(SpotCommentDO::getCreateTime));
    }

    default List<SpotCommentDO> selectBySpotId(Long spotId, int page, int size) {
        LambdaQueryWrapperX<SpotCommentDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eq(SpotCommentDO::getSpotId, spotId)
               .eq(SpotCommentDO::getParentId, 0L)
               .eq(SpotCommentDO::getStatus, 1)
               .eq(SpotCommentDO::getDeleted, 0)
               .orderByDesc(SpotCommentDO::getCreateTime);
        return selectList(wrapper);
    }

    default List<SpotCommentDO> selectRepliesByParentId(Long parentId) {
        return selectList(new LambdaQueryWrapperX<SpotCommentDO>()
                .eq(SpotCommentDO::getParentId, parentId)
                .eq(SpotCommentDO::getStatus, 1)
                .eq(SpotCommentDO::getDeleted, 0)
                .orderByAsc(SpotCommentDO::getCreateTime));
    }
}
