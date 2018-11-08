/**
 * io.sean
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.sean.mq.integration.listener;

import io.sean.mq.config.MQProperties;
import io.sean.mq.integration.container.ListenerContainer;
import io.sean.mq.ons.initial.OnsConsumerInitiator;
import io.sean.mq.ons.producer.OnsPublisherHolder;
import io.sean.mq.rocket.initial.RocketMQConsumerInitiator;
import io.sean.mq.rocket.producer.RocketPublisherHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/8 下午2:50 sean Exp $
 * @description
 */
@Component
public class SpringContainerStartupListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private MQProperties mqProperties;

    @Autowired
    private ListenerContainer container;

    @Autowired
    private OnsPublisherHolder onsPublisherHolder;

    @Autowired
    private RocketPublisherHolder rocketPublisherHolder;

    @Autowired
    private OnsConsumerInitiator onsConsumerInitiator;

    @Autowired
    private RocketMQConsumerInitiator rocketMQConsumerInitiator;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
//        //1.先注册监听器到容器中
//        container.init();
//
//        //2.判断是配置了哪种mq方式
//        String clientType = mqProperties.getClientType();
//
//        //3.启动生产者 注册消费者
//        if (StringUtils.equalsIgnoreCase(clientType, ClientTypeEnum.ons.name())) {
//            onsStartUp();
//        } else {
//            rocketMqStartUp();
//        }
    }

    private void rocketMqStartUp() {
        //1.启动生产者
        rocketPublisherHolder.init();

        //2.注册消费者
        rocketMQConsumerInitiator.onConsumerStartUp(container.getListeners());
    }

    private void onsStartUp() {
        //1.启动生产者
        onsPublisherHolder.init();

        //2.注册消费者
        onsConsumerInitiator.onConsumerStartUp(container.getListeners());

    }
}
