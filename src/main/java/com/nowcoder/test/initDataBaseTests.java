package com.nowcoder.test;


import com.nowcoder.WendaApplication;
import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
public class initDataBaseTests {


    @Autowired
    UserDAO userDAO;

    @Autowired
    QuestionDAO questionDAO;

    @Test
    public void initDatabase(){
        Random random = new Random();
        for(int i = 0; i < 11; ++i){
            User user = new User();
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            user.setName(String.format("User%d", i));
            user.setPassword("");
            user.setSalt("");
            System.out.println(user);
            userDAO.addUser(user);

            Question question = new Question();
            question.setCommentCount(i);
            Date date = new Date();
            date.setTime(date.getTime() + 1000 * 3600 * i);
            question.setCreatedDate(date);
            question.setUserId(i + 1);
            question.setTitle(String.format("TITLE {%d}", i));
            question.setContent(String.format("Balalalala Content %d", i));
            System.out.println(question);
            questionDAO.addQuestion(question);
        }
        List<Question> questionList = questionDAO.selectLatestQuestions(0, 0, 10);
        for(Question q1: questionList){
            System.out.println(q1);
        }
    }
}
