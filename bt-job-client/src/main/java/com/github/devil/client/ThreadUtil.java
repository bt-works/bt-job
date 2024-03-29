package com.github.devil.client;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author eric.yao
 * @date 2021/1/29
 **/
@Slf4j
public class ThreadUtil {
    /**
     * 定时触发器
     */
    public final static ScheduledExecutorService SCHEDULE = new ScheduledThreadPoolExecutor(10,  newThreadFactory("SCHEDULE"));

    public final static ThreadPoolExecutor GLOBAL = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
        Runtime.getRuntime().availableProcessors()*4,30,
        TimeUnit.SECONDS,new LinkedBlockingDeque<>(),newThreadFactory("GLOBAL"));

    private static ThreadFactory newThreadFactory(String name){
        return r -> {
            Thread thread = new Thread(r);
            thread.setName(name);
            thread.setUncaughtExceptionHandler(newHandler());
            return thread;
        };
    }

    private static Thread.UncaughtExceptionHandler newHandler(){
        return (Thread t, Throwable e) -> {
          if (log.isErrorEnabled()){
              log.error("thread error,thread-name:{},error:",t.getName(),e);
          }
        };
    }
}
