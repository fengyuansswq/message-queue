/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package com.sean.mq.autoconfig;

import io.terminus.mq.config.MQConsumerProperties;
import io.terminus.mq.config.MQProducerProperties;
import io.terminus.mq.config.MQProperties;
import io.terminus.mq.integration.container.ListenerContainer;
import io.terminus.mq.rocket.initial.RocketMQConsumerInitiator;
import io.terminus.mq.rocket.producer.RocketPublisherHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/12 下午2:59 sean Exp $
 * @description
 */
@Configuration
@ComponentScan("io.terminus.mq")
@EnableConfigurationProperties({ MQProperties.class, MQProducerProperties.class, MQConsumerProperties.class })
@ConditionalOnProperty(name = "mq.clientType", havingValue = "rocketmq", matchIfMissing = true)
public class RocketMQAutoConfig {

    @Autowired
    private ListenerContainer         container;

    @Autowired
    private RocketPublisherHolder     rocketPublisherHolder;

    @Autowired
    private RocketMQConsumerInitiator rocketMQConsumerInitiator;

    @PostConstruct
    private void rocketMqStartUp() {

        //1.先注册监听器到容器中
        container.init();

        //2.启动生产者
        rocketPublisherHolder.init();

        //3.注册消费者
        rocketMQConsumerInitiator.onConsumerStartUp(container.getListeners());
    }
}
