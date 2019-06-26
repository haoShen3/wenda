package com.nowcoder.controller;


import com.nowcoder.aspect.LogAspect;
import com.nowcoder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class registerController {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    UserService userService;

    //注册页面
    @RequestMapping(path={"/reg"}, method = {RequestMethod.POST})
    public String register(@RequestParam("password") String password,
                          @RequestParam("username") String username,
                           Model model){
        try{
            Map<String, String> map = userService.register(username, password);
            if(map.containsKey("msg")){
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
            return "redirect:/";
        }catch (Exception e){
            logger.error(e.getMessage());
            return "login";
        }
    }

    //登录页面
    @RequestMapping(path = "/reglogin", method = {RequestMethod.GET})
    public String login(Model model){
        return "login";
    }
}
