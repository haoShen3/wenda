package com.nowcoder.service;


import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    LoginTicketService loginTicketService;

    public User getUser(int id){
        return userDAO.selectById(id);
    }

    public User selectByName(String name){
        return userDAO.selectByName(name);
    }
    //注册方法
    public Map<String, String> register(String username, String password){
        HashMap<String, String> map = new HashMap<String, String>();
        if(StringUtils.isBlank(username)){
            map.put("msg", "用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg", "密码不能为空");
            return  map;
        }
        boolean flag = false;
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(username);
        flag = matcher.matches();
        if(!flag){
            map.put("msg", "请输入正确的邮箱地址");
            return map;
        }
        User user = userDAO.selectByName(username);
        if(user != null){
            map.put("msg", "该用户名已经存在");
            return map;
        }
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setPassword(WendaUtil.MD5(user.getSalt() + password));
        userDAO.addUser(user);

        //登录后赋值 ticket
        String ticket = addTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    public Map<String, String> login(String username, String password){
        HashMap<String, String> map = new HashMap<String, String>();
        if(StringUtils.isBlank(username)){
            map.put("msg", "用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg", "密码不能为空");
            return  map;
        }
        User user = userDAO.selectByName(username);
        if(user == null){
            map.put("msg", "该用户名不存在");
            return map;
        }
        if(!WendaUtil.MD5(user.getSalt() + password).equals(user.getPassword())){
            map.put("msg", "用户名密码错误");
            return map;
        }
        //登录后赋值 ticket
        String ticket = addTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    public String addTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 3600 * 24 * 10);
        loginTicket.setExpired(date);
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replace("-", ""));
        loginTicketService.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public void logout(String ticket) {
        loginTicketService.updateTicket(ticket, 1);
    }
}
