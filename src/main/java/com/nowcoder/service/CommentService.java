package com.nowcoder.service;


import com.nowcoder.dao.CommentDAO;
import com.nowcoder.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentDAO commentDAO;


    public int addComment(Comment comment){
        return commentDAO.addComment(comment);
    }

    public List<Comment> selectAllComments(int entityType, int entityId){
        return commentDAO.selectCommentByEntity(entityType, entityId);
    }

    public int getCommentCount(int entityType, int entityId){
        return commentDAO.getCommentCount(entityType, entityId);
    }

    public boolean updateStatus(int status, int id){
        return commentDAO.updateStatus(status, id) > 0;
    }
}
