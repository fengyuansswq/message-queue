/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package com.sean.mq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/6 下午4:40 sean Exp $
 * @description
 */
@Data
@ConfigurationProperties("mq.producer")
public class MQProducerProperties {

    /** 生产者ID  ons用 */
    @Deprecated
    private String  producerId;

    /** 生产者组名称 mq */
    private String  producerGroup;

    /** 当消息投递失败，是否重试其他broker */
    private boolean retryAnotherBrokerWhenNotStore = true;

    /** 重试次数 */
    private int     retryTimesWhenSendFailed       = 2;

    /** 消息体大小 */
    private int     maxMessageSize                 = 128 * 1024;

    /** 消息投递超时时间 */
    private int     timeout                        = 3000;
}
