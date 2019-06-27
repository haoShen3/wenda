package com.nowcoder.dao;


import com.nowcoder.model.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketDAO {

    String TABLENAME = "login_ticket";
    String INSERT_FIELD = " user_id, ticket, expired, status";
    String SELECT_FIELD = " id " + INSERT_FIELD;

    @Insert({"insert into", TABLENAME, "(", INSERT_FIELD, ") values (#{userId}, #{ticket}, #{expired}, #{status})" })
    int  addTicket(LoginTicket loginTicket);


    @Select({"select ", SELECT_FIELD, "from ", TABLENAME, "where ticket=#{ticket}"})
    LoginTicket selectTicket(String ticket);


    @Update({"update ", TABLENAME, "set status=#{status} where ticket=#{ticket}"})
    void updateTicket(@Param("ticket") String ticket, @Param("status") String status);
}
