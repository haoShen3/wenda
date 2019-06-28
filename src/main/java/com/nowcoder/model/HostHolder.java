package com.nowcoder.model;


import org.springframework.stereotype.Component;

@Component
public class HostHolder {

    private static ThreadLocal<User> users = new ThreadLocal<User>();

    public User getUsers() {
        return users.get();
    }

    public  void setUsers(User users) {
        this.users.set(users);
    }

    public void clear(){
        users.remove();
    }
}
