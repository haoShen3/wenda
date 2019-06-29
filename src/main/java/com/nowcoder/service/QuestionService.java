package com.nowcoder.service;


import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    QuestionDAO questionDAO;

    public int addQuestion(Question question){
        return questionDAO.addQuestion(question) > 0? question.getId(): 0;
    }

    public List<Question> getLatestQuestions(int userId, int offset, int limit){
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }

    public void updateQuestions(int userId, int commentCount){
        questionDAO.updateQuestionCommentCount(userId, commentCount);
    }

}
