package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.*;
import com.nowcoder.service.*;
import com.nowcoder.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController {

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    FollowerService followerService;

    @Autowired
    QuestionService questionService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = "/followUser", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId){
        if(hostHolder.getUsers() == null){
            return WendaUtil.getJSONString(999);
        }
        boolean ret = followerService.follow(hostHolder.getUsers().getId(), EntityType.ENTITY_USER, userId);

        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUsers().getId()).setEntityId(userId)
            .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));
        return WendaUtil.getJSONString(ret? 0: 1
                , String.valueOf(followerService.getFolloweesCount(EntityType.ENTITY_USER, hostHolder.getUsers().getId())));
    }

    @RequestMapping(path = "/unfollowUser", method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowUser(@RequestParam("userId") int userId){
        if(hostHolder.getUsers() == null){
            return WendaUtil.getJSONString(999);
        }
        boolean ret = followerService.unfollow(hostHolder.getUsers().getId(), EntityType.ENTITY_USER, userId);

        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUsers().getId()).setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));
        return WendaUtil.getJSONString(ret? 0: 1
                , String.valueOf(followerService.getFolloweesCount(EntityType.ENTITY_USER, hostHolder.getUsers().getId())));
    }

    @RequestMapping(path = "/followQuestion", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId){
        if(hostHolder.getUsers() == null){
            return WendaUtil.getJSONString(999);
        }
        Question q = questionService.getById(questionId);
        if(q == null){
            return WendaUtil.getJSONString(1, "问题不存在");
        }
        boolean ret = followerService.follow(hostHolder.getUsers().getId(), EntityType.ENTITY_QUESTION, questionId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUsers().getId()).setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(q.getUserId()));

        Map<String, Object> info = new HashMap<String, Object>();
        info.put("headUrl", hostHolder.getUsers().getHeadUrl());
        info.put("name", hostHolder.getUsers().getName());
        info.put("id", hostHolder.getUsers().getId());
        info.put("count", followerService.getFollowersCount(EntityType.ENTITY_QUESTION, questionId));

        return WendaUtil.getJSONString(ret? 0: 1, info);
    }

    @RequestMapping(path = "/unfollowQuestion", method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId){
        if(hostHolder.getUsers() == null){
            return WendaUtil.getJSONString(999);
        }
        Question q = questionService.getById(questionId);
        if(q == null){
            return WendaUtil.getJSONString(1, "问题不存在");
        }
        boolean ret = followerService.unfollow(hostHolder.getUsers().getId(), EntityType.ENTITY_QUESTION, questionId);
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUsers().getId()).setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(q.getUserId()));

        Map<String, Object> info = new HashMap<String, Object>();
        info.put("id", hostHolder.getUsers().getId());
        info.put("count", followerService.getFollowersCount(EntityType.ENTITY_QUESTION, questionId));
        return WendaUtil.getJSONString(ret? 0: 1, info);
    }

    @RequestMapping(path ={"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followees(Model model,@PathVariable("uid") int uid){
        List<Integer> followeeIds = followerService.getFollowees(EntityType.ENTITY_USER, uid, 0, 10);
        if(hostHolder.getUsers() != null){
            model.addAttribute("followees", getUsersInfo(hostHolder.getUsers().getId(), followeeIds));
        }else{
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }
        model.addAttribute("followeeCount", followerService.getFolloweesCount(EntityType.ENTITY_USER, uid));
        model.addAttribute("curUser", userService.getUser(uid));
        return "followees";
    }

    @RequestMapping(path ={"/user/{uid}/followers"}, method = {RequestMethod.GET})
    public String followers(Model model, @PathVariable("uid") int uid){
        List<Integer> followersIds = followerService.getFollowers(EntityType.ENTITY_USER, uid, 0, 10);
        if(hostHolder.getUsers() != null){
            model.addAttribute("followers", getUsersInfo(hostHolder.getUsers().getId(), followersIds));
        }else{
            model.addAttribute("followers", getUsersInfo(0, followersIds));
        }
        model.addAttribute("followerCount", followerService.getFollowersCount(EntityType.ENTITY_USER, uid));
        model.addAttribute("curUser", userService.getUser(uid));
        return "followers";
    }


    //把粉丝或关注列表中用户的信息提取出来，判断用户之间的关注关系
    private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds){
        List<ViewObject> userInfo = new ArrayList<>();
        for(Integer uid: userIds){
            User user = userService.getUser(uid);
            if(user == null){
                continue;
            }
            ViewObject viewObject = new ViewObject();
            viewObject.set("user", user);
            viewObject.set("commentCount", commentService.getUserCommentCount(uid));
            viewObject.set("followerCount", followerService.getFollowersCount(EntityType.ENTITY_USER, uid));
            viewObject.set("followeeCount", followerService.getFolloweesCount(EntityType.ENTITY_USER, uid));
            //如果用户未登录，则看不到他与其他用户之间的关系
            if(localUserId != 0){
                viewObject.set("followed", followerService.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            }else{
                viewObject.set("followed", false);
            }
            userInfo.add(viewObject);
        }
        return userInfo;
    }







}
