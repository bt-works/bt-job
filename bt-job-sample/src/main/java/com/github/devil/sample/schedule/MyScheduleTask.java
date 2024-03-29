package com.github.devil.sample.schedule;

import com.github.devil.client.process.TaskContextHolder;
import com.github.devil.client.spring.annotation.Scheduled;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author eric.yao
 * @date 2021/2/3
 **/
@Component
public class MyScheduleTask {

    @Scheduled(taskName = "test1")
    public void test(){
        String s = DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss:SSS");
        TaskContextHolder.get().getLogger().info(s);
        System.out.println(s);
    }

}
