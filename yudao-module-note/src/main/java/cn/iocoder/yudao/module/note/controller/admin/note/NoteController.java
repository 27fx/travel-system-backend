package cn.iocoder.yudao.module.note.controller.admin.note;

import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.note.controller.admin.note.vo.*;
import cn.iocoder.yudao.module.note.dal.dataobject.note.NoteDO;
import cn.iocoder.yudao.module.note.service.note.NoteService;

@Tag(name = "管理后台 - 游记")
@RestController
@RequestMapping("/note/note")
@Validated
public class NoteController {

    @Resource
    private NoteService noteService;

    @Resource
    private AdminUserService adminUserService;
    private NotePageReqVO pageReqVO;


    @PostMapping("/create")
    @Operation(summary = "创建游记")
    public CommonResult<Long> createNote(@Valid @RequestBody NoteSaveReqVO createReqVO) {
        return success(noteService.createNote(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新游记")
    @PreAuthorize("@ss.hasPermission('note:note:update')")
    public CommonResult<Boolean> updateNote(@Valid @RequestBody NoteSaveReqVO updateReqVO) {
        noteService.updateNote(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除游记")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('note:note:delete')")
    public CommonResult<Boolean> deleteNote(@RequestParam("id") Long id) {
        noteService.deleteNote(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除游记")
    @PreAuthorize("@ss.hasPermission('note:note:delete')")
    public CommonResult<Boolean> deleteNoteList(@RequestParam("ids") List<Long> ids) {
        noteService.deleteNoteListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得游记")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<NoteRespVO> getNote(@RequestParam("id") Long id) {
        NoteDO note = noteService.getNote(id);
        Long userId = note.getUserId();
        NoteRespVO noteRespVO = new NoteRespVO();
        BeanUtils.copyProperties(note, noteRespVO);
        //获取相关的用户昵称显示
        noteRespVO.setUserNickname(adminUserService.getUser(userId).getNickname());

        return success(noteRespVO);

    }


    
    @GetMapping("/getNotePage")
    @Operation(summary = "获取游记分页")
    @PreAuthorize("@ss.hasPermission('note:note:query')")
    public CommonResult<PageResult<NotePageRespVO>> getNotePage(@Valid NotePageReqVO pageReqVO) {
        this.pageReqVO = pageReqVO;
        PageResult<NotePageRespVO> pageResult = noteService.getNotePage(pageReqVO);
        return success(pageResult);
    }


}