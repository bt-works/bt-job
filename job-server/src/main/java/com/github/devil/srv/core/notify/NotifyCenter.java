package com.github.devil.srv.core.notify;

import com.github.devil.srv.core.notify.event.Event;
import com.github.devil.srv.core.notify.listener.Listener;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author eric.yao
 * @date 2021/1/29
 **/
@Slf4j
public class NotifyCenter {

    private final static List<Listener> LISTENERS = Lists.newArrayList();

    public static void addListener(Listener listener){
        for (Listener<?> lis : LISTENERS) {
            if (listener.getClass() == lis.getClass()){
                return;
            }
        }
        LISTENERS.add(listener);
    }

    public static void onEvent(Event event){
        for (Listener listener : LISTENERS) {
            if (listener.support(event)){
                listener.onEvent(event);
            }
        }
    }

}
