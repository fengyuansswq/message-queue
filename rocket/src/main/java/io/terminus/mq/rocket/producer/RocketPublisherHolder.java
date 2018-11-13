/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.terminus.mq.rocket.producer;

import com.google.common.base.Throwables;
import io.terminus.mq.config.MQProducerProperties;
import io.terminus.mq.config.MQProperties;
import io.terminus.mq.exception.MQException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.TransactionListener;
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
@Slf4j
public class RocketPublisherHolder implements DisposableBean {

    @Autowired
    private MQProducerProperties producerProperties;

    @Autowired
    private MQProperties         mqProperties;

    @Autowired
    private TransactionListener  transactionListener;

    private RocketMQPublisher    publisher;

    public void init() {
        try {
            if (producerProperties == null) {
                log.info("the application does not need to produce message");
                return;
            }
            String producerGroup = producerProperties.getProducerGroup();
            int timeout = producerProperties.getTimeout();
            String nameServerAddr = mqProperties.getNameServer();
            int retryTimesWhenFaild = producerProperties.getRetryTimesWhenSendFailed();
            boolean retryOtherBroker = producerProperties.isRetryAnotherBrokerWhenNotStore();
            int maxMessageSize = producerProperties.getMaxMessageSize();
            publisher = new RocketMQPublisher(producerGroup, nameServerAddr, retryOtherBroker, retryTimesWhenFaild, timeout, maxMessageSize);
            publisher.start();
        } catch (MQException e) {
            log.error("producer init failed ,cause ={}", Throwables.getStackTraceAsString(e));
            throw new RuntimeException("message producer init fail");
        }
    }

    @Override
    public void destroy() throws Exception {
        publisher.shutdown();
    }

}
