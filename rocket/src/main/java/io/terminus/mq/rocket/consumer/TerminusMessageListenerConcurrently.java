/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.terminus.mq.rocket.consumer;

import io.terminus.mq.client.UniformEventListener;
import io.terminus.mq.model.UniformEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/8 下午12:01 sean Exp $
 * @description
 */
@Slf4j
public class TerminusMessageListenerConcurrently extends AbstractTerminusMessageListener implements MessageListenerConcurrently {

    /** 统一消息事件监听器 */
    private UniformEventListener listener;

    /**
     * @param listener
     */
    public TerminusMessageListenerConcurrently(UniformEventListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        try {
            log.info("[并发消费 -- 消费者监听器] >>> coming ...");
            UniformEvent event = resolveMessage(msgs);
            boolean success = listener.onUniformEvent(event);
            if (success) {
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            } else {
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        } catch (Exception e) {
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        } finally {
            log.info("[并发消费 -- 消费者监听器] <<< Exit ...");
        }
    }
}
