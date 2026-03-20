package cn.iocoder.yudao.module.food.dal.mysql.foodComment;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.food.dal.dataobject.foodComment.CommentDO;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface FoodCommentMapper extends BaseMapperX<CommentDO> {

}
