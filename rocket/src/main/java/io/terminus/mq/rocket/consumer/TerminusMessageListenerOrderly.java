/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.terminus.mq.rocket.consumer;

import io.terminus.mq.client.UniformEventListener;
import io.terminus.mq.model.UniformEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/8 下午12:02 sean Exp $
 * @description
 */
@Slf4j
public class TerminusMessageListenerOrderly extends AbstractTerminusMessageListener implements MessageListenerOrderly {

    /** 统一消息事件监听器 */
    private UniformEventListener listener;

    /**
     * @param listener
     */
    public TerminusMessageListenerOrderly(UniformEventListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        try {
            log.info("[顺序消费 -- 消费者监听器] >>> coming ...");
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
        }finally {
            log.info("[顺序消费 -- 消费者监听器] <<< Exit ...");
        }
    }
}
