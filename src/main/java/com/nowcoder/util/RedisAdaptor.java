package com.nowcoder.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


@Component
public class RedisAdaptor implements InitializingBean {
    private JedisPool pool;
    private final  static Logger logger = LoggerFactory.getLogger(RedisAdaptor.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("redis://localhost:6379");

    }

    public Long sadd(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        }catch (Exception e){
            logger.error("jedis 发生异常" + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return (long)0;
    }

    public Long scard(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("jedis 发生异常" + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return (long)0;
    }

    public Long srem(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.srem(key, value);
        }catch (Exception e){
            logger.error("jedis 发生异常" + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return (long)0;
    }

    public boolean sismember(String key, String member){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sismember(key, member);
        }catch (Exception e){
            logger.error("jedis 发生异常" + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return true;
    }
//    @Test
//    public static void main(String[] args){
//        Jedis jedis = new Jedis("127.0.0.1", 6379, 100000);
////        jedis.flushDB();
////        jedis.set("qwe", "asd");
////        jedis.set("pv", "100");
////        jedis.incr("pv");
////        System.out.println(jedis.get("pv"));
////
////        String listName = "list";
////        for(int i = 0; i < 10; ++i){
////            jedis.lpush(listName, "a" + String.valueOf(i));
////        }
////        jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a4", "xx");
////        System.out.println(jedis.lrange(listName, 0, 8));
////        System.out.println(jedis.lpop(listName));
//
////        String userKye = "user";
////        jedis.hset(userKye, "name", "jim");
////        jedis.hset(userKye, "age", "12");
////        jedis.hset(userKye, "phone", "12345");
////        System.out.println(jedis.hget(userKye, "name"));
////        System.out.println(jedis.hgetAll(userKye));
////        String set1 = "set1";
////        String set2 = "set2";
////        for(int i = 0; i < 10; ++i){
////            jedis.sadd(set1, String.valueOf(i));
////            jedis.sadd(set2, String.valueOf(i * i));
////        }
////
////        System.out.println(jedis.smembers(set1));
////        System.out.println(jedis.smembers(set2));
////
////        System.out.println(jedis.sunion(set1, set2));
////        System.out.println(jedis.sdiff(set1, set2));
////        System.out.println(jedis.sinter(set1, set2));
////        System.out.println(jedis.sismember(set1, "2"));
////        jedis.srem(set1, "5");
////        System.out.println(jedis.smembers(set1));
////        System.out.println(jedis.scard(set1));
////        System.out.println(jedis.srandmember(set1, 2));
//
//        String randKey = "randKey";
//        jedis.zadd(randKey, 1, "jim");
//        jedis.zadd(randKey, 1, "ben");
//        jedis.zadd(randKey, 1, "lee");
//        jedis.zadd(randKey, 1, "lucy");
//        System.out.println(jedis.zlexcount(randKey, "-", "+"));
//        System.out.println(jedis.zlexcount(randKey, "[b", "l]"));
//        jedis.zrem(randKey, "ben");
//        System.out.println(jedis.zrange(randKey, 0, 10));
////        System.out.println(jedis.zcard(randKey));
////        System.out.println(jedis.zcount(randKey, 60 , 100));
////        jedis.zincrby(randKey, 2, "jim");
////        System.out.println(jedis.zscore(randKey, "jim"));
////        System.out.println(jedis.zrange(randKey, 0, 2));
////        System.out.println(jedis.zrevrange(randKey, 0, 2));
////        for(Tuple tuple:jedis.zrangeByScoreWithScores(randKey, 60, 100)){
////            System.out.println(tuple.getElement() + " " + tuple.getScore());
////        }
////        System.out.println(jedis.zrank(randKey, "jim"));
//        jedis.close();
//    }


}
