package cn.iocoder.yudao.module.food.controller.admin.foodComment.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodCommentListVO {
    private List<FoodCommentVO> commentVOList;
    private Long replyCount;
}
