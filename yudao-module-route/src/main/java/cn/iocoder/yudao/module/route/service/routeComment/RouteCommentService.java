package cn.iocoder.yudao.module.route.service.routeComment;

import cn.iocoder.yudao.module.route.controller.admin.routeComment.dto.RouteCommentCreateDTO;
import cn.iocoder.yudao.module.route.controller.admin.routeComment.vo.RouteCommentVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface RouteCommentService {

    void createComment(RouteCommentCreateDTO dto, Long currentUserId);

    Page<RouteCommentVO> getCommentTree(Long routeId, int page, int size);

    void toggleLike(Long commentId, Long currentUserId);

    void auditComment(Long commentId, Integer status, String operator);

    void deleteComment(Long commentId, Long currentUserId);

    Page<RouteCommentVO> getChildComments(Long commentId, int page, int size);
}
