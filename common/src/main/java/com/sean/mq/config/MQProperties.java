/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package com.sean.mq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/6 下午5:14 sean Exp $
 * @description
 */
@Data
@ConfigurationProperties("mq")
public class MQProperties {

    /** MQ客户端类型 ons OR rocketmq */
    private String clientType;

    //@Value("${mq.accessKey}")
    /** ons accessKey */
    private String accessKey  = "";

    //@Value("${mq.secretKey}")
    /** ons secretKey */
    private String secretKey  = "";

    // @Value("${mq.nameServer}")
    /** 注册中心地址 */
    private String nameServer = "";

}
