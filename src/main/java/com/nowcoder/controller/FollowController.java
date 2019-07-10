package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.User;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.FollowerService;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    EventProducer eventProducer;

    @RequestMapping(path = "/followUser", method = {RequestMethod.POST})
    @ResponseBody
    public String follow(@RequestParam("userId") int userId){
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
}
