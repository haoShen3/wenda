package com.nowcoder.controller;


import com.nowcoder.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

//@Controller
public class indexController {


    @RequestMapping(path={"/", "/index"})
    @ResponseBody
    public String index(HttpSession httpSession){
        return "Hello NowCoder" + httpSession.getAttribute("msg");
    }


    @RequestMapping(path={"/profile/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("userId") int userId,
                          @RequestParam("key") String key){
        if(!key.equals("qwe")){
            throw new IllegalArgumentException("参数不对");
        }
        return String.format("Profile page of %d %s", userId, key);
    }

    @RequestMapping(path = "/vm", method={RequestMethod.GET})
    public String template(Model model){
        model.addAttribute("value1", "123");
        model.addAttribute("value2", "3456");
        List<String> colors = Arrays.asList(new String[]{"RED, GREEN", "BLUE"});
        model.addAttribute("colors", colors);

        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for(int i = 0; i < 4; ++i){
            map.put(i, i * i);
        }
        model.addAttribute("map", map);

        User user = new User("john");
        model.addAttribute("user", user);
        return "home";
    }

    @ResponseBody
    @RequestMapping(path = "/request")
    public String request(HttpServletRequest request, HttpServletResponse response){
        StringBuffer sb = new StringBuffer();
        if(request.getCookies() != null){
            for(Cookie cookie1: request.getCookies()){
                sb.append(cookie1 + "<br>");
            }
        }
        sb.append(request.getMethod() + "<br>");
        sb.append(request.getRequestURI() + "<br>");
        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            sb.append(enumeration.nextElement() + "<br>");
        }
        return sb.toString();
    }

    @RequestMapping(path = "/redirect/{code}", method={RequestMethod.GET})
    public RedirectView redirect(@PathVariable("code") int code,
                                 HttpSession httpSession){
        httpSession.setAttribute("msg", " jump from redirect");
        RedirectView redirectView = new RedirectView("/", true);
        if(code == 301){
            redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return redirectView;
    }

    @ExceptionHandler
    @ResponseBody
    public String error(Exception e){
        return "Error: " + e.getMessage();
    }
}
