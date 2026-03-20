package cn.iocoder.yudao.module.note.dal.mysql.note;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.note.dal.dataobject.note.NoteDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.note.controller.admin.note.vo.*;
import org.apache.ibatis.annotations.Param;

/**
 * 游记 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface NoteMapper extends BaseMapperX<NoteDO> {

    default PageResult<NoteDO> selectPage(NotePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<NoteDO>()
                .eqIfPresent(NoteDO::getTitle, reqVO.getTitle())
                .eqIfPresent(NoteDO::getContent, reqVO.getContent())
                .eqIfPresent(NoteDO::getImage, reqVO.getImage())
                .betweenIfPresent(NoteDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(NoteDO::getId));
    }

    List<NotePageRespVO> selectPageList(@Param("reqVO") NotePageReqVO reqVO,
                                       @Param("offset") Integer offset,
                                       @Param("pageSize")Integer pageSize);


    Long selectPageCount(@Param("reqVO") NotePageReqVO reqVO);

    List<NotePageRespVO> selectPageVOList(@Param("reqVO") NotePageReqVO reqVO);


}