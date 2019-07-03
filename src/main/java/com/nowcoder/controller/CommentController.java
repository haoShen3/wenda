package com.nowcoder.controller;


import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.SensitiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;

@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    SensitiveService sensitiveService;

    @Autowired
    QuestionService questionService;

    @RequestMapping(path = "/addComment", method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content){
        try{
            content = HtmlUtils.htmlEscape(content);
            content = sensitiveService.filter(content);
            Comment comment = new Comment();
            comment.setContent(content);
            if(hostHolder.getUsers() != null){
                comment.setUserId(hostHolder.getUsers().getId());
            }else {
                return "redirect:/reglogin";
            }
            comment.setCreatedDate(new Date());
            comment.setEntityId(questionId);
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            commentService.addComment(comment);

            //更新评论数
            int count = commentService.getCommentCount(comment.getEntityType(), comment.getEntityId());
            System.out.println(count);
            questionService.updateCommentCount(comment.getEntityId(), count);

        }catch (Exception e){
            logger.error("增加评论失败" + e.getMessage());
        }
        return "redirect:/question/" + questionId;


    }
}
