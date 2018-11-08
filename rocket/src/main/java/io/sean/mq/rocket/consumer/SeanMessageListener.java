/**
 * io.sean
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.sean.mq.rocket.consumer;

import com.alibaba.rocketmq.common.message.MessageExt;
import io.sean.mq.model.DefaultUniformEvent;
import io.sean.mq.model.UniformEvent;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/8 上午11:59 sean Exp $
 * @description
 */
public abstract class SeanMessageListener {

    /**
     * 解析RocketMQ投递过来的消息
     *
     * @param msgs
     * @return
     * @throws
     */
    protected UniformEvent resolveMessage(List<MessageExt> msgs) {
        if (CollectionUtils.isEmpty(msgs)) {
            throw new IllegalStateException("消息消费失败，未能正确获取队列中得消息，List<MessageExt>.size()=0");
        }

        // 因为在生产者端设定了每次只投递一条消息，所以这里只获取第一条消息进行处理
        MessageExt message = msgs.get(0);
        String payload = new String(message.getBody());
        UniformEvent event = new DefaultUniformEvent(message.getTopic(), message.getTags(),message.getMsgId());
        event.setPayload(payload);

        return event;
    }
}
