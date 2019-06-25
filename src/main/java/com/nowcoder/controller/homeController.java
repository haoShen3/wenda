package com.nowcoder.controller;

import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.model.Question;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import javax.swing.text.View;
import java.util.ArrayList;
import java.util.List;


@Controller
public class homeController {

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;


    @RequestMapping(path={"/", "/index"}, method = {RequestMethod.GET})
    public String index(Model model){
        model.addAttribute("vos", getQuestions(0, 0, 10));
        return "index";
    }

    @RequestMapping(path = "/user/{userId}", method = {RequestMethod.GET})
    public String userIndex(Model model, @PathVariable("userId") int userId){
        model.addAttribute("vos", getQuestions(userId, 0, 10));
        return "index";
    }

    public List<ViewObject> getQuestions(int userId, int offset, int limit){
        List<Question> questionList = questionService.getLatestQuestions(userId, offset, limit);
        List<ViewObject> viewObjects = new ArrayList<ViewObject>();

        for(Question question: questionList){
            ViewObject viewObject = new ViewObject();
            viewObject.set("question", question);
            viewObject.set("user", userService.getUser(question.getUserId()));
            viewObjects.add(viewObject);
        }
        return viewObjects;
    }
}
