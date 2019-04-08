/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package com.sean.mq.rocket.consumer.initial;

import com.google.common.base.Throwables;
import io.terminus.mq.client.UniformEventListener;
import io.terminus.mq.common.UniformEventSubscriber;
import io.terminus.mq.config.MQConsumerConfig;
import io.terminus.mq.config.MQConsumerProperties;
import io.terminus.mq.config.MQProperties;
import io.terminus.mq.exception.MQException;
import io.terminus.mq.init.ConsumerInitiator;
import com.sean.mq.rocket.consumer.RocketMQSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/8 下午2:55 sean Exp $
 * @description
 */
@Component
@Slf4j
public class RocketMQConsumerInitiator implements ConsumerInitiator {

    @Autowired
    private MQConsumerProperties consumerProperties;

    @Autowired
    private MQProperties         mqProperties;

    @Override
    public void onConsumerStartUp(Map<String, UniformEventListener> listeners) {

        try {
            if (consumerProperties == null || CollectionUtils.isEmpty(consumerProperties.getList())) {
                log.info("the application does not has any consume listener");
                return;
            }
            for (MQConsumerConfig config : consumerProperties.getList()) {
                //1.创建消费者

                UniformEventSubscriber subscriber = createUniformEventSubscriber(config);
                //2.注册监听器
                UniformEventListener listener = listeners.get(config.getTopic());
                subscriber.registerUniformEventMessageListener(listener);

                //订阅消息
                subscriber.subscribe(config.getTopic(), config.getTags());
                subscriber.start();
            }
        } catch (MQException e) {
            log.error("ons消费者启动失败, 异常原因:{}", Throwables.getStackTraceAsString(e));
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private UniformEventSubscriber createUniformEventSubscriber(MQConsumerConfig config) {
        String nameServerAddr = mqProperties.getNameServer();
        String consumerGroup = config.getConsumerGroup();
        int consumeThreadMax = config.getConsumeThreadMax();
        int consumeThreadMin = config.getConsumeThreadMin();
        int pullBatchSize = config.getConsumeThreadMin();
        int pullInterval = config.getConsumeThreadMin();
        int pullThresholdForQueue = config.getConsumeThreadMin();

        return new RocketMQSubscriber(consumerGroup, nameServerAddr, consumeThreadMax, consumeThreadMin, pullBatchSize, pullInterval, pullThresholdForQueue);
    }

}
