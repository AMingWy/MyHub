package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nowcoder.community.entity.Comment;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * ClassName: CommentService
 * Package: com.nowcoder.community.service
 * Description:
 *
 * @Autuor Ming Zhang
 * @Version 1.0
 */
@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    /**
     * Find comments by entity list.
     * 根据实体类类型和id查询对应评论（分页）
     *
     * @param entityType the entity type
     * @param entityId   the entity id
     * @param offset     the offset
     * @param limit      the limit
     * @return the list
     */
    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    /**
     * Find comment count int.
     * 根据实体类类型和id查询评论数目
     *
     * @param entityType the entity type
     * @param entityId   the entity id
     * @return the int
     */
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCommentByEntity(entityType, entityId);
    }

    /**
     * Add comment int.
     * 新增评论信息
     * <p>
     * Propagation：
     * REQUIRED：支持当前事务（外部事务），如果不存在则创建新事务
     * REQUIRED_NEW：创建一个新的事务，并且暂停当前事务（外部事务）
     * NESTED：如果当前存在事务（外部事务），则嵌套在该事务中执行（独立的提交和回滚），否则就会跟REQUIRED一样
     *
     * @param comment the comment
     * @return the int
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if(comment==null) {
            throw new IllegalArgumentException("参数不为空!");
        }

        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);

        //更新帖子评论数量
        if(comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCommentByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(),count);
        }

        return rows;
    }

    /**
     * Find comment by id comment.
     * 根据id查询评论
     *
     * @param id the id
     * @return the comment
     */
    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }

}
