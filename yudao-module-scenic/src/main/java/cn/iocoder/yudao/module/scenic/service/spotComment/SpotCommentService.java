package cn.iocoder.yudao.module.scenic.service.spotComment;

import cn.iocoder.yudao.module.scenic.controller.admin.spotComment.dto.SpotCommentCreateDTO;
import cn.iocoder.yudao.module.scenic.controller.admin.spotComment.vo.SpotCommentVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface SpotCommentService {

    void createComment(SpotCommentCreateDTO dto, Long currentUserId);

    Page<SpotCommentVO> getCommentTree(Long spotId, int page, int size);

    void toggleLike(Long commentId, Long currentUserId);

    void auditComment(Long commentId, Integer status, String operator);

    void deleteComment(Long commentId, Long currentUserId);

    Page<SpotCommentVO> getChildComments(Long commentId, int page, int size);
}
