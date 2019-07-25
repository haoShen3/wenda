package com.nowcoder.controller;

import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.model.*;
import com.nowcoder.service.*;
import com.nowcoder.util.RedisAdaptor;
import com.nowcoder.util.RedisKeyUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import javax.swing.text.View;
import javax.swing.text.html.parser.Entity;
import javax.xml.ws.Holder;
import java.util.*;


@Controller
public class homeController {

    @Autowired
    QuestionService questionService;

    @Autowired
    RedisAdaptor redisAdaptor;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @Autowired
    FollowerService followerService;

    @Autowired
    HostHolder hostHolder;

    //主页
    @RequestMapping(path={"/", "/index"}, method = {RequestMethod.GET})
    public String index(Model model){
        model.addAttribute("vos", getQuestions(0, 0, 15));
        return "index";
    }


    //用户页面
    @RequestMapping(path = "/user/{userId}", method = {RequestMethod.GET})
    public String userIndex(Model model, @PathVariable("userId") int userId){
        model.addAttribute("vos", getQuestions(userId, 0, 10));

        User user = userService.getUser(userId);
        ViewObject viewObject = new ViewObject();
        viewObject.set("user", user);
        viewObject.set("commentCount", commentService.getUserCommentCount(userId));
        viewObject.set("followerCount", followerService.getFollowersCount(EntityType.ENTITY_USER, userId));
        viewObject.set("followeeCount", followerService.getFolloweesCount(EntityType.ENTITY_USER, userId));
        if(hostHolder.getUsers() != null){
            viewObject.set("followed", followerService.isFollower(hostHolder.getUsers().getId(), EntityType.ENTITY_USER, userId));
        }else{
            viewObject.set("followed", false);
        }
        model.addAttribute("profileUser", viewObject);
        return "profile";
    }

    @RequestMapping(path = "/personal", method = {RequestMethod.GET})
    public String personal(Model model){
        model.addAttribute("vos", getSortQuestions(0, 0, 30));
        return "index";
    }

    public List<ViewObject> getSortQuestions(int userId, int offset, int limit){
        List<Question> questionList = questionService.getLatestQuestions(userId, offset, limit);
        Collections.sort(questionList, new Comparator<Question>() {
            @Override
            public int compare(Question q1, Question q2){
                double score1 = getScore(q1);
                double score2 = getScore(q2);
                return (int)(score2 - score1);
            }
        });

        List<ViewObject> viewObjects = new ArrayList<ViewObject>();
        for(int i = 0; i < 10; ++i){
            Question question = questionList.get(i);
            ViewObject viewObject = new ViewObject();
            viewObject.set("question", question);
            viewObject.set("user", userService.getUser(question.getUserId()));
            viewObjects.add(viewObject);
        }
        return viewObjects;
    }

    public double getScore(Question question){
        String viewCountKey = RedisKeyUtil.getViewKey(question.getId());
        float commentCount = question.getCommentCount();
        int viewCount = Integer.parseInt(redisAdaptor.getCount(viewCountKey));
        List<Comment> comments = commentService.selectAllComments(EntityType.ENTITY_QUESTION, question.getId());
        int commentLike = 0;
        for (Comment comment: comments){
            long likeCount = likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getEntityId());
            long dislikeCount = likeService.getDislikeCount( EntityType.ENTITY_COMMENT, comment.getEntityId());
            commentLike += (likeCount - dislikeCount);
        }
        long dateDiff = new Date().getTime() - question.getCreatedDate().getTime();
        long i = (dateDiff / 24 / 3600 / 1000);
        return (Math.log(viewCount + 1) * 4 + (commentCount / 5) *  + commentLike) * 10/ i;
    }

    public List<ViewObject> getQuestions(int userId, int offset, int limit){
        List<Question> questionList = questionService.getLatestQuestions(userId, offset, limit);
        List<ViewObject> viewObjects = new ArrayList<ViewObject>();

        for(Question question: questionList){
            ViewObject viewObject = new ViewObject();
            viewObject.set("question", question);
            viewObject.set("user", userService.getUser(question.getUserId()));
            viewObject.set("followCount", followerService.getFollowersCount(EntityType.ENTITY_USER, userId));
            viewObjects.add(viewObject);
        }
        return viewObjects;
    }
}
