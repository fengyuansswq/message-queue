/**
 * io.sean
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.sean.mq.integration.client;

import io.sean.mq.client.MessageProducer;
import io.sean.mq.common.UniformEventPublisher;
import io.sean.mq.config.MQProperties;
import io.sean.mq.enums.ClientTypeEnum;
import io.sean.mq.exception.MQException;
import io.sean.mq.model.UniformEvent;
import io.sean.mq.ons.producer.OnsPublisherHolder;
import io.sean.mq.rocket.producer.RocketPublisherHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/8 下午4:05 sean Exp $
 * @description
 */
@Component
public class DefaultMessageProducer implements MessageProducer {

    @Autowired
    private MQProperties mqProperties;

    @Autowired
    private OnsPublisherHolder onsPublisherHolder;

    @Autowired
    private RocketPublisherHolder rocketPublisherHolder;

    @Override
    public boolean sendOneway(String topic, String eventCode, Object payload) throws MQException {
        UniformEvent event = null;
        UniformEventPublisher publisher = null;
        if (isOns()) {
            publisher = onsPublisherHolder.getOnsPublisher();
            event = publisher.createUniformEvent(topic, eventCode, false, payload);
            return publisher.publishUniformEventOneway(event);
        }
        publisher = rocketPublisherHolder.getPublisher();
        event = publisher.createUniformEvent(topic, eventCode, false, payload);
        return publisher.publishUniformEventOneway(event);
    }

    @Override
    public boolean send(String topic, String eventCode, Object payload) throws MQException {
        UniformEvent event = null;
        UniformEventPublisher publisher = null;
        if (isOns()) {
            publisher = onsPublisherHolder.getOnsPublisher();
            Assert.notNull(publisher, "message publisher can not be null");
            event = publisher.createUniformEvent(topic, eventCode, false, payload);
            return publisher.publishUniformEvent(event);
        }
        publisher = rocketPublisherHolder.getPublisher();
        Assert.notNull(publisher, "message publisher can not be null");
        event = publisher.createUniformEvent(topic, eventCode, false, payload);
        return publisher.publishUniformEvent(event);
    }

    @Override
    public boolean send(String topic, String eventCode, Object payload, long timeout) throws MQException {
        UniformEvent event = null;
        UniformEventPublisher publisher = null;
        if (isOns()) {
            publisher = onsPublisherHolder.getOnsPublisher();
            Assert.notNull(publisher, "message publisher can not be null");
            event = publisher.createUniformEvent(topic, eventCode, false, payload, timeout);
            return publisher.publishUniformEvent(event);
        }
        publisher = rocketPublisherHolder.getPublisher();
        Assert.notNull(publisher, "message publisher can not be null");
        event = publisher.createUniformEvent(topic, eventCode, false, payload, timeout);
        return publisher.publishUniformEvent(event);
    }

    public boolean send(String topic, String eventCode, Object payload, long timeout, int delayTimeLevel) throws MQException {
        UniformEvent event = null;
        UniformEventPublisher publisher = null;
        if (this.isOns()) {
            publisher = this.onsPublisherHolder.getOnsPublisher();
            Assert.notNull(publisher, "message publisher can not be null");
            event = publisher.createUniformEvent(topic, eventCode, false, payload, timeout);
            event.setDelayTimeLevel(delayTimeLevel);
            return publisher.publishUniformEvent(event);
        } else {
            publisher = this.rocketPublisherHolder.getPublisher();
            Assert.notNull(publisher, "message publisher can not be null");
            event = publisher.createUniformEvent(topic, eventCode, false, payload, timeout);
            event.setDelayTimeLevel(delayTimeLevel);
            return publisher.publishUniformEvent(event);
        }
    }


    private boolean isOns() {
        return StringUtils.endsWithIgnoreCase(mqProperties.getClientType(), ClientTypeEnum.ons.name());
    }
}
