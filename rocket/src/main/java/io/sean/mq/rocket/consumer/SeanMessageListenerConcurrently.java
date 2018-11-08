/**
 * io.sean
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.sean.mq.rocket.consumer;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import io.sean.mq.client.UniformEventListener;
import io.sean.mq.model.UniformEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/8 下午12:01 sean Exp $
 * @description
 */
@Slf4j
public class SeanMessageListenerConcurrently extends SeanMessageListener implements MessageListenerConcurrently {

    /** 统一消息事件监听器 */
    private UniformEventListener listener;

    /**
     * @param listener
     */
    public SeanMessageListenerConcurrently(UniformEventListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        try {
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
            log.info("[消费者监听器] <<< Exit ...");
        }
    }
}
