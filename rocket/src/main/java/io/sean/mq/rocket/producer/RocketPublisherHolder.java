/**
 * io.sean
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.sean.mq.rocket.producer;

import io.sean.mq.config.MQProperties;
import io.sean.mq.config.MQProducerProperties;
import io.sean.mq.exception.MQException;
import lombok.Data;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/8 下午2:05 sean Exp $
 * @description
 */
@Component
@Data
public class RocketPublisherHolder implements DisposableBean {

    @Autowired
    private MQProducerProperties producerProperties;

    @Autowired
    private MQProperties mqProperties;

    private RocketMQPublisher    publisher;

    public void init() {
        try {
            String producerGroup = producerProperties.getProducerGroup();
            int timeout = producerProperties.getTimeout();
            String nameServerAddr = mqProperties.getNameServer();
            int retryTimesWhenFaild = producerProperties.getRetryTimesWhenSendFailed();
            boolean retryOtherBroker = producerProperties.isRetryAnotherBrokerWhenNotStore();
            int maxMessageSize = producerProperties.getMaxMessageSize();
            publisher = new RocketMQPublisher(producerGroup, nameServerAddr, retryOtherBroker, retryTimesWhenFaild, timeout, maxMessageSize);
            publisher.start();
        } catch (MQException e) {
            throw new RuntimeException("message producer init fail");
        }
    }

    @Override
    public void destroy() throws Exception {
        publisher.shutdown();
    }
}
