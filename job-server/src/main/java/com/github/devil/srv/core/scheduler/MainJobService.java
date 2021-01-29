package com.github.devil.srv.core.scheduler;

import com.github.devil.common.enums.ExecuteStatue;
import com.github.devil.srv.akka.MainAkServer;
import com.github.devil.srv.core.MainThreadUtil;
import com.github.devil.srv.core.notify.NotifyCenter;
import com.github.devil.srv.core.notify.listener.JobExecuteFailListener;
import com.github.devil.srv.core.persist.core.entity.InstanceEntity;
import com.github.devil.srv.core.persist.core.entity.JobInfoEntity;
import com.github.devil.srv.core.persist.core.repository.JobInfoRepository;
import com.github.devil.srv.core.scheduler.runner.TaskRunner;
import com.github.devil.srv.core.service.JobService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author eric.yao
 * @date 2021/1/28
 **/
@Slf4j
@Component
public class MainJobService implements InitializingBean {

    private final static Integer MAX_BATCH = 50;

    private final static Long SCHEDULER_FIX = 400L;

    @Resource
    private JobInfoRepository jobInfoRepository;
    @Resource
    private JobService jobService;
    @Resource
    private TaskRunner taskRunner;

    @Override
    public void afterPropertiesSet() throws Exception {
        MainThreadUtil.JOB_PUSH.scheduleAtFixedRate(this::pushJobToTimer,10,SCHEDULER_FIX, TimeUnit.MILLISECONDS);
        NotifyCenter.addListener(new JobExecuteFailListener());
    }

    public void pushJobToTimer(){

        /**
         * 当前时间----> 下次定时时间触发的时候，需要执行的任务
         */
        List<JobInfoEntity> jobInfoEntities = jobInfoRepository.findUnExecuteJob(MainAkServer.getCurrentHost(), Lists.newArrayList(ExecuteStatue.WAIT),System.currentTimeMillis()+(int)(SCHEDULER_FIX*1.5));

        Lists.partition(jobInfoEntities,MAX_BATCH).forEach(lists -> {

            log.info("process task to timer,task count :{}",lists.size());

            Map<Long,JobInfoEntity> map = lists.stream().collect(Collectors.toMap(JobInfoEntity::getId,e -> e));

            List<InstanceEntity> instanceEntities = jobService.newInstance(lists);

            instanceEntities.forEach(instanceEntity -> {

                MainJobScheduler.schedule(instanceEntity.getId(),Math.max(instanceEntity.getExceptTriggerTime().getTime() - System.currentTimeMillis(),0),() -> {
                    taskRunner.runTask(map.get(instanceEntity.getJobId()),instanceEntity.getId());
                });
            });
        });
    }

}