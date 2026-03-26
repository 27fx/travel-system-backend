package cn.iocoder.yudao.module.note.service.note;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.note.controller.admin.note.vo.*;
import cn.iocoder.yudao.module.note.dal.dataobject.note.NoteDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 游记 Service 接口
 *
 * @author 芋道源码
 */
public interface NoteService {

    /**
     * 创建游记
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createNote(@Valid NoteSaveReqVO createReqVO);

    /**
     * 更新游记
     *
     * @param updateReqVO 更新信息
     */
    void updateNote(@Valid NoteSaveReqVO updateReqVO);

    /**
     * 删除游记
     *
     * @param id 编号
     */
    void deleteNote(Long id);

    /**
    * 批量删除游记
    *
    * @param ids 编号
    */
    void deleteNoteListByIds(List<Long> ids);

    /**
     * 获得游记
     *
     * @param id 编号
     * @return 游记
     */
    NoteDO getNote(Long id);

    /**
     * 获得游记分页
     *
     * @param pageReqVO 分页查询
     * @return 游记分页
     */
    PageResult<NotePageRespVO> getNotePage(NotePageReqVO pageReqVO);

    PageResult<NotePageRespVO> getMyNotePage(NotePageReqVO pageReqVO);
}