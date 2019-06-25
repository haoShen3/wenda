package com.nowcoder.model;

import java.util.HashMap;
import java.util.Map;

public class ViewObject {

    Map<String, Object> view = new HashMap<String, Object>();

    public void set(String key, Object val){
        view.put(key, val);
    }

    public Object get(String key){
        return this.view.get(key);
    }
}
