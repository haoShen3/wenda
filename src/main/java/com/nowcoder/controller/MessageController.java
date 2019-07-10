package com.nowcoder.controller;


import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @RequestMapping(path = "/msg/list", method = RequestMethod.GET)
    public String MessageList(Model model){
        if(hostHolder.getUsers() == null){
            return "redirect:/reglogin";
        }
        int localUserId = hostHolder.getUsers().getId();
        List<Message> messageList = messageService.getConversationList(localUserId, 0, 10);
        List<ViewObject> messages = new ArrayList<ViewObject>();
        for(Message message: messageList){
            ViewObject viewObject = new ViewObject();
            viewObject.set("conversation", message);

            //列表看到对方传来的信息
            int targetId = message.getFromId() == localUserId?  message.getToId(): message.getFromId();
            viewObject.set("user", userService.getUser(targetId));

            viewObject.set("unread", messageService.getConversationUnread(localUserId, message.getConversationId()));
            messages.add(viewObject);
        }

        model.addAttribute("conversations", messages);
        return "letter";
    }

    @RequestMapping(path = "/msg/detail", method = {RequestMethod.GET})
    public String MessageDetail(Model model, @RequestParam("conversationId") String conversationId){
        try {
            List<Message> messageList = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> messages = new ArrayList<ViewObject>();
            for(Message message:messageList){
                ViewObject viewObject = new ViewObject();
                viewObject.set("message", message);
                viewObject.set("user", userService.getUser(message.getFromId()));
                messages.add(viewObject);
            }
            model.addAttribute("messages", messages);
            int i = hostHolder.getUsers().getId();
            messageService.updateUnreadCount(i, conversationId);
        }catch (Exception e){
            logger.error("查看信息失败" + e.getMessage());
            return "redirect:/";
        }
        return "letterDetail";
    }


    @RequestMapping(path = "msg/addMessage", method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("content") String content,
                             @RequestParam("toName") String toName){
        if(hostHolder.getUsers() == null){
            return WendaUtil.getJSONString(999, "用户未登录");
        }
        try {
            Message message = new Message();
            User user = userService.selectByName(toName);
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setFromId(hostHolder.getUsers().getId());
            message.setToId(user.getId());

            messageService.addMessage(message);
            return WendaUtil.getJSONString(0);
        }catch (Exception e){
            logger.error("发送消息失败" + e.getMessage());
            return WendaUtil.getJSONString(1, "发送失败");
        }

    }
}
