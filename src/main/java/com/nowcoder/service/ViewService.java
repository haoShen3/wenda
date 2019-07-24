package com.nowcoder.service;

import com.nowcoder.util.RedisAdaptor;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ViewService {

    @Autowired
    RedisAdaptor redisAdaptor;

    public int getCount(String key){
        return Integer.parseInt(redisAdaptor.getCount(key));
    }

    public void setCount(String key, String value){
        redisAdaptor.setCount(key, value);
    }
}
