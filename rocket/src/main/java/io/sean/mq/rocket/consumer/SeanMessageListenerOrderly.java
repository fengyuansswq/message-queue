/**
 * io.sean
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.sean.mq.rocket.consumer;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.common.message.MessageExt;
import io.sean.mq.client.UniformEventListener;
import io.sean.mq.model.UniformEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/8 下午12:02 sean Exp $
 * @description
 */
@Slf4j
public class SeanMessageListenerOrderly extends SeanMessageListener implements MessageListenerOrderly {

    /** 统一消息事件监听器 */
    private UniformEventListener listener;

    /**
     * @param listener
     */
    public SeanMessageListenerOrderly(UniformEventListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        try {
            UniformEvent event = resolveMessage(msgs);
            boolean success = listener.onUniformEvent(event);
            if (success) {
                return ConsumeOrderlyStatus.SUCCESS;
            } else {
                return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        }
    }
}
