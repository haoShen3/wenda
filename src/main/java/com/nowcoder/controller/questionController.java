package com.nowcoder.controller;

import com.nowcoder.aspect.LogAspect;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Question;
import com.nowcoder.service.QuestionService;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Component
public class questionController {

    private static final Logger logger = LoggerFactory.getLogger(questionController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;



    @RequestMapping(value = "/question/add", method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title, @RequestParam("content") String content){
        try{
            Question question = new Question();
            question.setContent(content);
            question.setTitle(title);
            question.setCreatedDate(new Date());
            question.setCommentCount(0);
            if(hostHolder.getUsers() != null){
                question.setUserId(hostHolder.getUsers().getId());
            }else{
                return WendaUtil.getJSONString(999);//999自动跳转
            }
            if(questionService.addQuestion(question) > 0){
                return WendaUtil.getJSONString(0);//0 表示插入成功
            }
        }catch (Exception e){
            logger.error("增加题目失败" + e.getMessage());
        }
        return WendaUtil.getJSONString(1, "插入失败");
    }
}
