package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.util.MailSender;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//邮件发送
//@Component
public class LoginExceptionHandler implements EventHandler {
    @Autowired
    MailSender mailSender;


    @Override
    public void doHandler(EventModel eventModel) {
            //xxx判断用户登录异常
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", eventModel.getExts("username"));
        mailSender.sendWithHTMLTemplate(eventModel.getExts("email"), "登录ID异常",
                "mails/login_exception.html", map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
