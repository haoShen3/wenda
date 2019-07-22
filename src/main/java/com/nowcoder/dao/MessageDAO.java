package com.nowcoder.dao;


import com.nowcoder.model.Comment;
import com.nowcoder.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageDAO {
    String TABLE_NAME = "message";
    String INSERT_FIELDS = " from_id, to_id, content, created_date, has_read, conversation_id ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ")",
            "values(#{fromId}, #{toId}, #{content}, #{createdDate}, #{hasRead},#{conversationId})"})
    int addMessage(Message message);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME,
            "where conversation_id=#{conversationId} order by created_date desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId, @Param("offset") int offset,
                              @Param("limit") int limit);

    @Select("select from_id,to_id,conversation_id,has_read,content ,b.id as id,b.date as created_date from message," +
            "( select max(created_date) as date ,count(id) as id from message where from_id =#{userId} or to_id =#{userId}" +
            " group by conversation_id) as b where message.created_date = b.date limit #{offset}, #{limit}")
    List<Message> getConversationList(@Param("userId") int userId,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);

    //没看到的消息肯定是发送过来的消息
    @Select({"select count(id) from ", TABLE_NAME, " where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getConversationUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    @Update({"update ", TABLE_NAME, " set has_read=1 where to_id=#{userId} and conversation_id=#{conversationId}"})
    int updateUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);
}
