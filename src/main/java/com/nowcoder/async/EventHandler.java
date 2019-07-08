package com.nowcoder.async;

import java.util.List;

public interface EventHandler {

    void doHandler(EventModel eventModel);

    //查看这个handler 能执行哪种类型的 eventmodel
    List<EventType> getSupportEventTypes();
}
