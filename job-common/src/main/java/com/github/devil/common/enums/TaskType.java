package com.github.devil.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author eric.yao
 * @date 2021/1/18
 **/
@Getter
@AllArgsConstructor
public enum TaskType {

    REMOTE_CLIENT("远程客户端"),

    SHELL("Shell脚本"),

//    GULE
    ;

    String message;
}