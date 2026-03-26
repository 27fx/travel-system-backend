
package cn.iocoder.yudao.module.scenic.service.spotComment;

import cn.iocoder.yudao.module.scenic.controller.admin.spotComment.dto.CommentCreateDTO;
import cn.iocoder.yudao.module.scenic.controller.admin.spotComment.vo.CommentVO;
import cn.iocoder.yudao.module.scenic.dal.dataobject.spotComment.SpotCommentDO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface SpotCommentService {

    void createComment(CommentCreateDTO dto, Long currentUserId);

    Page<CommentVO> getCommentTree(Long spotId, int page, int size);

    void toggleLike(Long commentId, Long currentUserId);

    void auditComment(Long commentId, Integer status, String operator);

    void deleteComment(Long commentId, Long currentUserId);
}
