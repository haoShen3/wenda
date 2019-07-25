package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.*;
import com.nowcoder.util.RedisKeyUtil;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.View;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by nowcoder on 2016/7/22.
 */
@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(homeController.class);

    @Autowired
    ViewService viewService;

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @Autowired
    FollowerService followerService;

    @RequestMapping(value = "/question/{qid}", method = {RequestMethod.GET})
    public String questionDetail(Model model, @PathVariable("qid") int qid) {
        Question question = questionService.getById(qid);

        //展示问题
        model.addAttribute("question", question);

        //展示所有评论
        List<Comment> comments = commentService.selectAllComments(EntityType.ENTITY_QUESTION, qid);
        List<ViewObject> commentList = new ArrayList<ViewObject>();
        for(Comment comment: comments){
            ViewObject viewObject = new ViewObject();
            viewObject.set("comment", comment);
            viewObject.set("user", userService.getUser(comment.getUserId()));

            //查看该用户对某个评论的点赞状态
            if(hostHolder.getUsers() == null){
                viewObject.set("liked", 0);
            }else{
                viewObject.set("liked", likeService.getLikeStatus(hostHolder.getUsers().getId(), EntityType.ENTITY_COMMENT, comment.getId()));
            }

            viewObject.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
            commentList.add(viewObject);
        }
        model.addAttribute("comments", commentList);

        //获取关注用户的信息
        List<ViewObject> followerUsers = new ArrayList<ViewObject>();
        List<Integer> users = followerService.getFollowers(EntityType.ENTITY_QUESTION, qid, 10);
        for(Integer userId: users){
            ViewObject viewObject = new ViewObject();
            User u = userService.getUser(userId);
            if(u == null){
                continue;
            }
            viewObject.set("name", u.getName());
            viewObject.set("headUrl", u.getHeadUrl());
            viewObject.set("id", u.getId());
            followerUsers.add(viewObject);
        }
        model.addAttribute("followUsers", followerUsers);

        //查看自己关注状态
        if(hostHolder.getUsers() != null){
            model.addAttribute("followed", followerService.isFollower(hostHolder.getUsers().getId(), EntityType.ENTITY_QUESTION, qid));
        }else{
            model.addAttribute("followed", false);
        }
        String viewKey = RedisKeyUtil.getViewKey(qid);
        int count = viewService.getCount(viewKey);
        viewService.setCount(viewKey, String.valueOf(count + 1));
        model.addAttribute("viewCount", count+1);
        return "detail";
    }

    @RequestMapping(value = "/question/add", method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title, @RequestParam("content") String content) {
        try {
            Question question = new Question();
            question.setContent(content);
            question.setCreatedDate(new Date());
            question.setTitle(title);
            if (hostHolder.getUsers() == null) {
                question.setUserId(WendaUtil.ANONYMOUS_USERID);
//                 return WendaUtil.getJSONString(999);
            } else {
                question.setUserId(hostHolder.getUsers().getId());
            }
            if (questionService.addQuestion(question) > 0) {
                return WendaUtil.getJSONString(0);
            }
        } catch (Exception e) {
            logger.error("增加题目失败" + e.getMessage());
        }
        return WendaUtil.getJSONString(1, "失败");
    }

}
