package com.github.devil.srv.akka;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinPool;
import static com.github.devil.common.CommonConstants.*;

import com.github.devil.common.util.InetUtils;
import com.github.devil.srv.core.exception.JobException;
import com.github.devil.srv.core.notify.NotifyCenter;
import com.github.devil.srv.core.notify.listener.ServeUnReceiveListener;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author eric.yao
 * @date 2021/1/20
 **/
@Slf4j
public class MainAkServer {

    private final static String AKKA_CONF = "akka.conf";

    private final static String AKKA_SRV_PAT = "akka://%s@%s/user/%s";

    private static  ActorSystem system;

    @Getter
    private static String currentHost;

    public static void start(@Nonnull Environment environment){

        log.info("===============Job SRV Starting============");
        Map<String,String> configMap = Maps.newHashMap();
        String address = getAddress(environment);
        if (address != null){
            configMap.put("akka.remote.artery.canonical.hostname",address);
        }else {
            throw new JobException("cannot auto find local Ip address,Please set it");
        }

        String port = environment.getProperty("main.job.port","10010");

        configMap.put("akka.remote.artery.canonical.port",port);

        log.info("Start Job SRV,Host:{},Port:{}",address,port);

        currentHost = address+":"+port;

        Config config = ConfigFactory.parseMap(configMap).withFallback(ConfigFactory.load(AKKA_CONF));

        system = ActorSystem.apply(MAIN_JOB_SRV_NAME,config);

        system.actorOf(Props.create(MainActor.class)
                .withDispatcher("akka.job-srv-dispatcher")
                .withRouter(new RoundRobinPool(Runtime.getRuntime().availableProcessors())), MAIN_JOB_ACTOR_PATH);

        log.info("===============Job SRV Started==============");

        String memberList = Optional.ofNullable(environment.getProperty("main.job.memberList")).orElseGet(() -> currentHost);

        Set<String> servers = Sets.newHashSet(memberList.split(","));

        servers.add(currentHost);

        servers.forEach(ServerHolder::echo);
        NotifyCenter.addListener(new ServeUnReceiveListener());
    }

    public static ActorSelection getSrv(String host){
        return system.actorSelection(String.format(AKKA_SRV_PAT,MAIN_JOB_SRV_NAME,host,MAIN_JOB_ACTOR_PATH));
    }

    public static ActorSelection getWorker(String host){
        return system.actorSelection(String.format(AKKA_SRV_PAT,MAIN_JOB_WORKER_NAME,host,MAIN_JOB_WORKER_ACTOR_PATH));
    }

    //todo
    public static String nextHealthServer(){
        return currentHost;
    }

    public static Set<String> getAllSurvivalServer(){
        return ServerHolder.getSURVIVAL().keySet();
    }

    public static Set<String> getAllSurvivalWorker(){
        return Sets.newHashSet();
    }

    private static String getAddress(Environment environment){
        String address = environment.getProperty("main.job.address");
        if (address == null || address.isEmpty() ){
            InetUtils.HostInfo hostInfo = InetUtils.findFirstNonLoopbackHostInfo();
            if (hostInfo != null){
                return hostInfo.getIpAddress();
            }
            return null;
        }
        return address;
    }

}