package com.nowcoder.controller;


import com.nowcoder.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
