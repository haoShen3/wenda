package com.nowcoder.service;

import com.nowcoder.util.RedisAdaptor;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class FollowerService {

    @Autowired
    RedisAdaptor redisAdaptor;

    public boolean follow(int userId, int entityType, int entityId){
        String folloerKey = RedisKeyUtil.getBizFollower(entityType, entityId);
        String folloeeKey = RedisKeyUtil.getBizFollowee(userId, entityType);
        Date date = new Date();
        Jedis jedis = redisAdaptor.getJedis();
        Transaction tx = redisAdaptor.multi(jedis);
        //粉丝集合
        redisAdaptor.zadd(folloerKey, date.getTime(), String.valueOf(userId));
        //关注对象集合
        redisAdaptor.zadd(folloeeKey, date.getTime(), String.valueOf(entityId));
        List<Object> ret = redisAdaptor.exec(tx, jedis);
        return ret.size() == 2 && (Long)ret.get(0) > 0 && (Long)ret.get(1) > 0;
    }

    public boolean unfollow(int userId, int entityType, int entityId){
        String folloerKey = RedisKeyUtil.getBizFollower(entityType, entityId);
        String folloeeKey = RedisKeyUtil.getBizFollowee(userId, entityType);
        Jedis jedis = redisAdaptor.getJedis();
        Transaction tx = redisAdaptor.multi(jedis);
        //粉丝集合
        redisAdaptor.zrem(folloerKey, String.valueOf(userId));
        //关注对象集合
        redisAdaptor.zrem(folloeeKey, String.valueOf(entityId));
        List<Object> ret = redisAdaptor.exec(tx, jedis);
        return ret.size() == 2 && (Long)ret.get(0) > 0 && (Long)ret.get(1) > 0;
    }

    private List<Integer> getIdsFromSet(Set<String> idset){
        List<Integer> ids = new ArrayList<>();
        for(String str: idset){
            System.out.println(str);
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }

    //分页
    public List<Integer> getFollowers(int entityType, int entityId, int count){
        String followerKey = RedisKeyUtil.getBizFollower(entityType, entityId);
        return getIdsFromSet(redisAdaptor.zrevrange(followerKey, 0, count));
    }

    public List<Integer> getFollowers(int entityType, int entityId, int offset, int count){
        String followerKey = RedisKeyUtil.getBizFollower(entityType, entityId);
        return getIdsFromSet(redisAdaptor.zrevrange(followerKey, offset, count));
    }

    public List<Integer> getFollowees(int entityType, int entityId, int count){
        String followeeKey = RedisKeyUtil.getBizFollowee(entityType, entityId);
        return getIdsFromSet(redisAdaptor.zrevrange(followeeKey, 0, count));
    }

    public List<Integer> getFollowees(int entityType, int entityId, int offset, int count){
        String followeeKey = RedisKeyUtil.getBizFollowee(entityType, entityId);
        return getIdsFromSet(redisAdaptor.zrevrange(followeeKey, offset, count));
    }

    public Long getFollowersCount(int entityType, int entityId){
        String followerKey = RedisKeyUtil.getBizFollower(entityType, entityId);
        return redisAdaptor.zcard(followerKey);
    }

    public Long getFolloweesCount(int entityType, int entityId){
        String followeeKey = RedisKeyUtil.getBizFollowee(entityType, entityId);
        return redisAdaptor.zcard(followeeKey);
    }

    public boolean isFollower(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getBizFollower(entityType, entityId);
        return redisAdaptor.zscore(followerKey, String.valueOf(userId)) != null;
    }
}
