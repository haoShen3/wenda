package com.nowcoder.async;

public enum EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3);

    private int value;
    private EventType(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    @Override
    public String toString() {
        return "EventType{" +
                "value=" + value +
                '}';
    }
}
