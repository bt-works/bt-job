package com.github.devil.common.dto;

import com.github.devil.common.serialization.JobSerializable;
import lombok.Data;

/**
 * @author eric.yao
 * @date 2021/1/27
 **/
@Data
public class HeartBeat implements JobSerializable {

    private String appName;

    private String workerAddress;

    private Long timeStamp;

}
