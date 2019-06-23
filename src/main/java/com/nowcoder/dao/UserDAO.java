package com.nowcoder.dao;


import com.nowcoder.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDAO {

      String TABLE_NAME = " user ";
      String INSERT_FIELDS = " name, password, salt, head_url ";

    @Insert({"insert into " , TABLE_NAME , "(", INSERT_FIELDS, ") values(#{name}, #{password}, #{salt}, #{headUrl} "})
    int addUser(User user);
}
