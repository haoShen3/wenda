package com.nowcoder.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class indexController {


    @RequestMapping(path={"/", "/index"})
    @ResponseBody
    public String index(){
        return "Hello NowCoder";
    }

    @RequestMapping(path={"/profile/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("userId") int userId,
                          @RequestParam("key") String key){
        return String.format("Profile page of %d %s", userId, key);
    }
}
