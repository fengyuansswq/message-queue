/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.terminus.mq.ons.initial;

import com.google.common.base.Throwables;
import io.terminus.mq.client.UniformEventListener;
import io.terminus.mq.common.UniformEventSubscriber;
import io.terminus.mq.config.MQConsumerConfig;
import io.terminus.mq.config.MQConsumerProperties;
import io.terminus.mq.config.MQProperties;
import io.terminus.mq.exception.MQException;
import io.terminus.mq.init.ConsumerInitiator;
import io.terminus.mq.ons.consumer.OnsSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/8 下午2:54 sean Exp $
 * @description
 */
@Component
@Slf4j
public class OnsConsumerInitiator implements ConsumerInitiator {

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
                Assert.notNull(listener, "消息消费者的监听器未设定，topic：" + config.getTopic());
                subscriber.registerUniformEventMessageListener(listener);
                // 3.订阅
                subscriber.subscribe(config.getTopic(), config.getTags());

                subscriber.start();
            }
        } catch (MQException e) {
            log.error("ons消费者启动失败, 异常原因:{}", Throwables.getStackTraceAsString(e));
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 构建订阅者
     * @param config
     * @return
     */
    private UniformEventSubscriber createUniformEventSubscriber(MQConsumerConfig config) {
        String nameServerAddr = mqProperties.getNameServer();
        String consumerId = config.getConsumerId();
        String accessKey = mqProperties.getAccessKey();
        String secretKey = mqProperties.getSecretKey();
        int consumeThreadNums = config.getConsumeThreadMax();
        return new OnsSubscriber(nameServerAddr, consumerId, accessKey, secretKey, consumeThreadNums);
    }
}
