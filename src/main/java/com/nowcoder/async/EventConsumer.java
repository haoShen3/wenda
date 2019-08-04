package com.nowcoder.async;

import com.alibaba.fastjson.JSON;
import com.nowcoder.util.RedisAdaptor;
import com.nowcoder.util.RedisKeyUtil;
import jdk.nashorn.internal.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Handler;

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private final Logger logger =  LoggerFactory.getLogger(EventConsumer.class);
    //关联任务类型及其 handler
    private Map<EventType, List<EventHandler>> config = new HashMap<EventType, List<EventHandler>>();
    private ApplicationContext applicationContext;


    @Autowired
    RedisAdaptor redisAdaptor;

    @Override
    public void afterPropertiesSet() throws Exception {
        //找到实现 handler 的所有接口 bean 对象
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if(beans != null){
            for(Map.Entry<String, EventHandler> entry: beans.entrySet()){
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
                //该 handler 能处理的 event 类型，都添加进 map
                for(EventType type: eventTypes){
                    if(!config.containsKey(type)){
                        config.put(type, new ArrayList<EventHandler>());
                    }
                    config.get(type).add(entry.getValue());
                }
            }
        }

        ExecutorService pool =  Executors.newFixedThreadPool(10);
        pool.execute(new Runnable() {
            @Override
            public void run() {
                while (true){
                    System.out.println("success");
                    String key = RedisKeyUtil.getEventQueue();
                    List<String> events = redisAdaptor.brpop(0, key);
                    for(String message: events){
                        if(message.equals(key)){
                            continue;
                        }
                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
                        if(!config.containsKey(eventModel.getType())){
                            logger.error("获取失败，不能识别的类型");
                            continue;
                        }
                        //根据 event 的类型，从 map 找到对应的 handler 集合
                        for(EventHandler handler: config.get(eventModel.getType())){
                            handler.doHandler(eventModel);
                        }
                    }
                }
            }
        });
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //开启无限循环，等待 event
//                while (true){
//                    String key = RedisKeyUtil.getEventQueue();
//                    List<String> events = redisAdaptor.brpop(0, key);
//                    for(String message: events){
//                        if(message.equals(key)){
//                            continue;
//                        }
//                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
//                        if(!config.containsKey(eventModel.getType())){
//                            logger.error("获取失败，不能识别的类型");
//                            continue;
//                        }
//                        //根据 event 的类型，从 map 找到对应的 handler 集合
//                        for(EventHandler handler: config.get(eventModel.getType())){
//                            handler.doHandler(eventModel);
//                        }
//                    }
//                }
//            }
//        });
//        thread.start();
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}
