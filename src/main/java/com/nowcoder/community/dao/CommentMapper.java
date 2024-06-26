package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ClassName: CommentMapper
 * Package: com.nowcoder.community.dao
 * Description:
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Mapper
public interface CommentMapper {

    // 根据实体类型和id查询对应评论(分页)
    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

    // 根据实体类类型和id查询评论数目
    int selectCommentByEntity(int entityType, int entityId);

    // 新增评论
    int insertComment(Comment comment);

    // 根据id查询评论
    Comment selectCommentById(int id);

}
