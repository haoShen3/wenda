package com.nowcoder.dao;


import com.nowcoder.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentDAO {
    String TABLE_NAME = "comment";
    String INSERT_FIELDS = " user_id, created_date, entity_id, entity_type, status, content ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ")",
            "values(#{userId}, #{createdDate}, #{entityId}, #{entityType}, #{status},#{content})"})
    int addComment(Comment comment);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, "where id=#{id}"})
    Comment getCommentById(int id);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME,
            "where entity_type=#{entityType} and entity_id=#{entityId} order by created_date desc" })
    List<Comment> selectCommentByEntity(@Param("entityType") int entityType, @Param("entityId") int entityId);

    @Select({"select count(id)", " from ", TABLE_NAME, " where user_id=#{userId}"})
    int getUserCommentCount(@Param("userId") int userId);

    @Select({"select count(id) from ", TABLE_NAME, "where entity_type=#{entityType} and entity_id=#{entityId}"})
    int getCommentCount(@Param("entityType") int entityType, @Param("entityId") int entityId);

    @Update("update comment set status=#{status} where id=#{id}")
    int updateStatus(@Param("status") int status, @Param("id") int id);
}
