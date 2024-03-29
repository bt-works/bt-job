package com.github.devil.srv.core.persist.core.repository;

import com.github.devil.common.enums.ExecuteStatue;
import com.github.devil.srv.core.persist.core.entity.InstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author eric.yao
 * @date 2021/1/18
 **/
@Repository
public interface JobInstanceRepository extends JpaRepository<InstanceEntity,Long>, JpaSpecificationExecutor<InstanceEntity> {

    /**
     * 修改触发时间和任务状态
     * @param triggerTime
     * @param executeStatue
     * @param id
     * @return
     */
    @Modifying
    @Query("update InstanceEntity set triggerTime = ?1 , upt = ?1 , executeStatue = ?2  where id = ?3")
    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    int updateTriggerTimeAndStatus(Date triggerTime, ExecuteStatue executeStatue,Long id);

    /**
     * 通过serverhost和executeStatue查询
     * @param serveHost
     * @param executeStatue
     * @return
     */
    @Query("select instance from InstanceEntity instance where instance.executeStatue in (?2) and instance.jobId in (select id from JobInfoEntity where serveHost = ?1)")
    List<InstanceEntity> findByServeHostAndExecuteStatueIn(String serveHost,List<ExecuteStatue> executeStatue);

    /**
     * 修改执行状态
     * @param id
     * @return
     */
    @Modifying
    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    @Query(value = "update InstanceEntity set  executeStatue = ?3, upt=?2,executeEndTime=?2 where id = ?1 and not exists (select id from WorkInstanceEntity where instanceId = ?1 and executeStatue in ('WAIT','EXECUTING'))")
    int updateStatusById(Long id,Date executeEndTime,ExecuteStatue statue);

    /**
     * 取消所有待执行的任务
     * @param serverHost
     * @return
     */
    @Modifying
    @Query("update InstanceEntity set executeStatue = 'CANCEL',upt=?2 where id in (?1) and executeStatue = 'WAIT'")
    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    int cancelAllWaitTask(List<Long> ids, Date date);

    /**
     * 查询状态持续时间
     * @param statue
     * @param upt
     * @return
     */
    List<InstanceEntity> findByExecuteStatueAndUptBefore(ExecuteStatue statue,Date upt);

    /**
     * 删除
     * @param id
     * @return
     */
    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    int deleteByIdLessThan(long id);
}
