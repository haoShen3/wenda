package com.nowcoder.service;

import com.nowcoder.util.RedisAdaptor;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    RedisAdaptor redisAdaptor;

    //评论喜欢的数量，可以不显示不喜欢的数量
    public long getLikeCount(int entityType, int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        return redisAdaptor.scard(likeKey);
    }

    public long getDislikeCount(int entityType, int entityId){
        String dislikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        return redisAdaptor.scard(dislikeKey);
    }

    //查看一个用户是否点赞
    public long getLikeStatus(int userId, int entityType, int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        if(redisAdaptor.sismember(likeKey, String.valueOf(userId))){
            return 1;
        }
        String dislikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        return redisAdaptor.sismember(dislikeKey, String.valueOf(userId))?  -1: 0;
    }

    public long like(int userId, int entityType, int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        redisAdaptor.sadd(likeKey, String.valueOf(userId));

        String dislikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        redisAdaptor.srem(dislikeKey, String.valueOf(userId));

        return redisAdaptor.scard(likeKey);
    }

    public long dislike(int userId, int entityType, int entityId){
        String dislikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        redisAdaptor.sadd(dislikeKey, String.valueOf(userId));

        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        redisAdaptor.srem(likeKey, String.valueOf(userId));

        return redisAdaptor.scard(likeKey);
    }
}
