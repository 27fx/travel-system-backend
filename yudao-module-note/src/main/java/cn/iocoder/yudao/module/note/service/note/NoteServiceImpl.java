package cn.iocoder.yudao.module.note.service.note;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.note.controller.admin.note.vo.*;
import cn.iocoder.yudao.module.note.dal.dataobject.note.NoteDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.note.dal.mysql.note.NoteMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.note.enums.ErrorCodeConstants.*;

/**
 * 游记 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class NoteServiceImpl implements NoteService {

    @Resource
    private NoteMapper noteMapper;

    @Override
    public Long createNote(NoteSaveReqVO createReqVO) {
        // 插入
        NoteDO note = BeanUtils.toBean(createReqVO, NoteDO.class);
        //获取当前用户Id
        note.setUserId(SecurityFrameworkUtils.getLoginUserId());
        noteMapper.insert(note);

        // 返回
        return note.getId();
    }

    @Override
    public void updateNote(NoteSaveReqVO updateReqVO) {
        // 校验存在
        validateNoteExists(updateReqVO.getId());
        // 更新
        NoteDO updateObj = BeanUtils.toBean(updateReqVO, NoteDO.class);
        noteMapper.updateById(updateObj);
    }

    @Override
    public void deleteNote(Long id) {
        // 校验存在
        validateNoteExists(id);
        // 删除
        noteMapper.deleteById(id);
    }

    @Override
        public void deleteNoteListByIds(List<Long> ids) {
        // 删除
        noteMapper.deleteByIds(ids);
        }


    private void validateNoteExists(Long id) {
        if (noteMapper.selectById(id) == null) {
            throw exception(NOTE_NOT_EXISTS);
        }
    }

    @Override
    public NoteDO getNote(Long id) {
        return noteMapper.selectById(id);
    }

    //分页查询方法
    @Override
    public PageResult<NotePageRespVO> getNotePage(NotePageReqVO pageReqVO) {
        int offset = (pageReqVO.getPageNo() - 1) * pageReqVO.getPageSize();
        List<NotePageRespVO> list = noteMapper.selectPageList(pageReqVO, offset, pageReqVO.getPageSize());
        return new PageResult<>(list, noteMapper.selectPageCount(pageReqVO));

    }

    @Override
    public PageResult<NotePageRespVO> getMyNotePage(NotePageReqVO pageReqVO) {
        int offset = (pageReqVO.getPageNo() - 1) * pageReqVO.getPageSize();
        pageReqVO.setUserId(SecurityFrameworkUtils.getLoginUserId());
        List<NotePageRespVO> list = noteMapper.selectPageList(pageReqVO, offset, pageReqVO.getPageSize());
        return new PageResult<>(list, noteMapper.selectPageCount(pageReqVO));
    }

}