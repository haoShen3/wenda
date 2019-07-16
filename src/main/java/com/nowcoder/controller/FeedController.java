package com.nowcoder.controller;


import com.nowcoder.model.EntityType;
import com.nowcoder.model.Feed;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.User;
import com.nowcoder.service.FeedService;
import com.nowcoder.service.FollowerService;
import com.nowcoder.util.RedisAdaptor;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FeedController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    FeedService feedService;

    @Autowired
    FollowerService followerService;

    @Autowired
    RedisAdaptor redisAdaptor;

    @RequestMapping(path = "/pullFeeds", method = {RequestMethod.GET})
    public String getPullFeeds(Model model){

        int userId = hostHolder.getUsers() == null? 0: hostHolder.getUsers().getId();
        List<Integer>  followees = new ArrayList<>();
        //根据是否登录，查看所有关注的人，再查找他们的新鲜事
        if (userId != 0){
            followees = followerService.getFollowees(userId, EntityType.ENTITY_USER, Integer.MAX_VALUE);
        }
        List<Feed> feeds = feedService.selectUserFeeds(Integer.MAX_VALUE, followees, 10);
        model.addAttribute("feeds", feeds);
        return "feeds";
    }

    @RequestMapping(path = "/pushFeeds", method = {RequestMethod.GET})
    public String getPushFeeds(Model model){
        int userId = hostHolder.getUsers() == null? 0: hostHolder.getUsers().getId();
        List<String>  feedIds = redisAdaptor.lrange(RedisKeyUtil.getBizTimeline(userId), 0, 10);
        List<Feed> feeds = new ArrayList<Feed>();
        for(String key: feedIds){
            Feed feed = feedService.getFeedById(Integer.parseInt(key));
            if(feed == null){
                continue;
            }
            feeds.add(feed);
        }
        //根据是否登录，查看所有关注的人，再查找他们的新鲜事
        model.addAttribute("feeds", feeds);
        return "feeds";
    }
}
