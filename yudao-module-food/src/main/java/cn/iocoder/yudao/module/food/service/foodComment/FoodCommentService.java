package cn.iocoder.yudao.module.food.service.foodComment;

import cn.iocoder.yudao.module.food.controller.admin.foodComment.dto.FoodCommentCreateDTO;
import cn.iocoder.yudao.module.food.controller.admin.foodComment.vo.FoodCommentVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


/**
 * 美食评论服务接口
 */
public interface FoodCommentService {

    /**
     * 发布评论或回复
     *
     * @param dto 评论数据传输对象
     *
     */
    void createComment(FoodCommentCreateDTO dto);

    /**
     * 获取美食评论列表（树形结构）
     * 包含一级评论分页，以及每条一级评论下的最新几条回复
     *
     * @param foodId 美食ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页后的评论VO列表
     */
    Page<FoodCommentVO> getCommentTree(Long foodId, int page, int size);

    /**
     * 点赞/取消点赞 (简化版：仅增加点赞数，实际生产需结合Redis防重)
     *
     * @param commentId 评论ID
     * @param currentUserId 当前用户ID
     */
    void toggleLike(Long commentId, Long currentUserId);

    /**
     * 审核评论 (管理员专用)
     *
     * @param commentId 评论ID
     * @param status 新状态 (1: 通过, 2: 驳回)
     * @param operator 操作人
     */
    void auditComment(Long commentId, Integer status, String operator);

    /**
     * 删除评论 (逻辑删除)
     * 如果是父评论，通常策略是：要么禁止删除有子评论的父评论，要么连带子评论一起删除
     * 这里采用：如果有一级评论下有回复，则只屏蔽该评论内容，不物理/逻辑删除，保持树结构完整
     * 或者：级联逻辑删除所有子评论
     *
     * @param commentId 评论ID
     * @param currentUserId 当前用户ID (用于权限校验)
     */
    void deleteComment(Long commentId, Long currentUserId);
}