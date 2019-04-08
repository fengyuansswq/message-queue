/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package com.sean.mq.autoconfig;

import io.terminus.mq.config.MQConsumerProperties;
import io.terminus.mq.config.MQProducerProperties;
import io.terminus.mq.config.MQProperties;
import io.terminus.mq.integration.container.ListenerContainer;
import io.terminus.mq.ons.initial.OnsConsumerInitiator;
import io.terminus.mq.ons.producer.OnsPublisherHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/12 下午2:58 sean Exp $
 * @description
 */
@Configuration
@ComponentScan("io.terminus.mq")
@EnableConfigurationProperties({ MQProperties.class, MQProducerProperties.class, MQConsumerProperties.class })
@ConditionalOnProperty(name = "mq.clientType", havingValue = "ons", matchIfMissing = true)
public class OnsAutoConfig {

    @Autowired
    private ListenerContainer container;

    @Autowired
    private OnsPublisherHolder onsPublisherHolder;

    @Autowired
    private OnsConsumerInitiator onsConsumerInitiator;

    @PostConstruct
    private void onsStartUp() {

        //1.先注册监听器到容器中
        container.init();

        //1.启动生产者
        onsPublisherHolder.init();

        //2.注册消费者
        onsConsumerInitiator.onConsumerStartUp(container.getListeners());

    }
}
