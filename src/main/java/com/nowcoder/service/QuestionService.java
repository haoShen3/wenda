package com.nowcoder.service;


import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    QuestionDAO questionDAO;


    @Autowired
    SensitiveService sensitiveService;

    public int addQuestion(Question question){

        //处理html 脚本
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));

        //处理敏感词
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));
        return questionDAO.addQuestion(question) > 0? question.getId(): 0;
    }

    public List<Question> getLatestQuestions(int userId, int offset, int limit){
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }

    public void updateQuestions(int userId, int commentCount){
        questionDAO.updateQuestionCommentCount(userId, commentCount);
    }

}
