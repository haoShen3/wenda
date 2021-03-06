package com.nowcoder.async.handler;


import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.Message;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class FollowHandler implements EventHandler {
    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @Autowired
    QuestionService questionService;

    @Override
    public void doHandler(EventModel eventModel) {
        Message message = new Message();
        message.setToId(eventModel.getEntityOwnerId());
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setCreatedDate(new Date());
        User user = userService.getUser(eventModel.getActorId());
        if (eventModel.getEntityType() == EntityType.ENTITY_QUESTION) {
            message.setContent("用户" + user.getName()
                    + "关注了你的问题: " + questionService.getById(eventModel.getEntityId()).getTitle());
        } else if (eventModel.getEntityType() == EntityType.ENTITY_USER) {
            message.setContent("用户" + user.getName()
                    + "关注了你");
        }
        messageService.addMessage(message);

    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
