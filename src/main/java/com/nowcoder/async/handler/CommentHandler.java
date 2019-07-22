package com.nowcoder.async.handler;


import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Message;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.nowcoder.util.WendaUtil.SYSTEM_USERID;

@Component
public class CommentHandler implements EventHandler {
    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    MessageService messageService;

    @Override
    public void doHandler(EventModel eventModel) {

        //有人评论，发送给楼主消息
        Message message = new Message();
        message.setCreatedDate(new Date());
        message.setFromId(SYSTEM_USERID);
        message.setToId(eventModel.getEntityOwnerId());
//        User user = userService.getUser(eventModel.getActorId());
        Question question = questionService.getById(Integer.parseInt(eventModel.getExts("questionId")));
        message.setContent("你的问题 "+ question.getTitle() + "被人评论了，快去看看吧");
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.COMMENT);
    }
}
