package com.nowcoder.async;


import com.alibaba.fastjson.JSONObject;
import com.nowcoder.util.RedisAdaptor;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {

    @Autowired
    RedisAdaptor redisAdaptor;

    public boolean fireEvent(EventModel eventModel){
        try{
            String json = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventQueue();
            redisAdaptor.lpush(key, json);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
