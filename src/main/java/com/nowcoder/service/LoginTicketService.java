package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.model.LoginTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginTicketService {

    @Autowired
    LoginTicketDAO loginTicketDAO;

    public int addTicket(LoginTicket loginTicket){
        return loginTicketDAO.addTicket(loginTicket);
    }

    public LoginTicket selectTicket(String ticket){
        return loginTicketDAO.selectByTicket(ticket);
    }

    public void updateTicket(String ticket, int status){
        loginTicketDAO.updateStatus(ticket, status);
    }
}
