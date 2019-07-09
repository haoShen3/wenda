package com.nowcoder.controller;


import com.nowcoder.aspect.LogAspect;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.service.UserService;
import com.sun.deploy.net.HttpResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class registerController {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    //注册页面
    @RequestMapping(path={"/reg"}, method = {RequestMethod.POST})
    public String register(@RequestParam("password") String password,
                           @RequestParam("username") String username,
                           Model model,
                           @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                           @RequestParam(value = "next", required = false) String next,
                           HttpServletResponse response){
        try{
            Map<String, String> map = userService.register(username, password);
            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                cookie.setPath("/");
                response.addCookie(cookie);
                if(StringUtils.isNotBlank(next)){
                    return "redirect:" + next;
                }
                return "redirect:/";

            }else{
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            return "login";
        }
    }

    //登录页面
    @RequestMapping(path = "/reglogin", method = {RequestMethod.GET})
    public String login(Model model, @RequestParam(value = "next", required = false) String next)
    {
        model.addAttribute("next", next);
        return "login";

    }


    @RequestMapping(path={"/login"}, method = {RequestMethod.POST})
    public String login(@RequestParam("password") String password,
                           @RequestParam("username") String username,
                           Model model,
                           @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                        @RequestParam(value = "next", required = false) String next,
                        HttpServletResponse response){
        try{
            Map<String, String> map = userService.login(username, password);
            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                cookie.setPath("/");
                if(rememberme){
                    cookie.setMaxAge(3600 * 24);
                }
                response.addCookie(cookie);

                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
                        .setExts("username", username).setExts("email", "2457937678@qq.com")
                        );

                if(StringUtils.isNotBlank(next)){
                    return "redirect:" + next;
                }
                return "redirect:/";

            }else{
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            return "login";
        }
    }

    @RequestMapping(path = "/logout", method = {RequestMethod.GET})
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/";
    }
}
