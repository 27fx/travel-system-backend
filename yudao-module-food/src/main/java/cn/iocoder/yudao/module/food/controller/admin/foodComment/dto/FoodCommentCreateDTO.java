package cn.iocoder.yudao.module.food.controller.admin.foodComment.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class FoodCommentCreateDTO {
    @NotNull(message = "美食ID不能为空")
    private Long foodId;

    private Long parentId = 0L; // 默认0

    @NotBlank(message = "内容不能为空")
    private String content;

    // 一级评论必填评分，回复可选
    private BigDecimal score; 
}