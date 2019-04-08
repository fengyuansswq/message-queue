/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package com.sean.mq.integration.client;

import io.terminus.mq.client.MessageProducer;
import io.terminus.mq.common.UniformEventPublisher;
import io.terminus.mq.config.MQProperties;
import io.terminus.mq.enums.ClientTypeEnum;
import io.terminus.mq.exception.MQException;
import io.terminus.mq.model.UniformEvent;
import io.terminus.mq.ons.producer.OnsPublisherHolder;
import io.terminus.mq.rocket.producer.RocketPublisherHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/8 下午4:05 sean Exp $
 * @description
 */
@Component
public class DefaultMessageProducer implements MessageProducer {

    @Autowired
    private MQProperties          mqProperties;

    @Autowired
    private OnsPublisherHolder    onsPublisherHolder;

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

    @Override
    public boolean send(String topic, String eventCode, Object payload, long timeout, Map<String, String> properties) throws MQException {
        UniformEvent event = null;
        UniformEventPublisher publisher = null;
        if (isOns()) {
            publisher = onsPublisherHolder.getOnsPublisher();
            Assert.notNull(publisher, "message publisher can not be null");
            event = publisher.createUniformEvent(topic, eventCode, false, payload, timeout);
            event.addProperties(properties);
            return publisher.publishUniformEvent(event);
        }
        publisher = rocketPublisherHolder.getPublisher();
        Assert.notNull(publisher, "message publisher can not be null");
        event = publisher.createUniformEvent(topic, eventCode, false, payload, timeout);
        event.addProperties(properties);
        return publisher.publishUniformEvent(event);
    }

    @Override
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

    @Override
    public boolean send(String topic, String eventCode, boolean transactional, Object payload) throws MQException {
        UniformEvent event = null;
        UniformEventPublisher publisher = null;
        if (isOns()) {
            publisher = onsPublisherHolder.getOnsPublisher();
            Assert.notNull(publisher, "message publisher can not be null");
            event = publisher.createUniformEvent(topic, eventCode, transactional, payload);
            return publisher.publishUniformEvent(event);
        }
        publisher = rocketPublisherHolder.getPublisher();
        Assert.notNull(publisher, "message publisher can not be null");
        event = publisher.createUniformEvent(topic, eventCode, transactional, payload);
        return publisher.publishUniformEvent(event);
    }

    @Override
    public boolean send(String topic, String eventCode, boolean transactional, Object payload, long timeout) throws MQException {
        UniformEvent event = null;
        UniformEventPublisher publisher = null;
        if (isOns()) {
            publisher = onsPublisherHolder.getOnsPublisher();
            Assert.notNull(publisher, "message publisher can not be null");
            event = publisher.createUniformEvent(topic, eventCode, transactional, payload, timeout);
            return publisher.publishUniformEvent(event);
        }
        publisher = rocketPublisherHolder.getPublisher();
        Assert.notNull(publisher, "message publisher can not be null");
        event = publisher.createUniformEvent(topic, eventCode, transactional, payload, timeout);
        return publisher.publishUniformEvent(event);
    }

    @Override
    public boolean send(String topic, String eventCode, boolean transactional, Object payload, long timeout, Map<String, String> properties) throws MQException {
        UniformEvent event = null;
        UniformEventPublisher publisher = null;
        if (isOns()) {
            publisher = onsPublisherHolder.getOnsPublisher();
            Assert.notNull(publisher, "message publisher can not be null");
            event = publisher.createUniformEvent(topic, eventCode, transactional, payload, timeout);
            event.addProperties(properties);
            return publisher.publishUniformEvent(event);
        }
        publisher = rocketPublisherHolder.getPublisher();
        Assert.notNull(publisher, "message publisher can not be null");
        event = publisher.createUniformEvent(topic, eventCode, transactional, payload, timeout);
        event.addProperties(properties);
        return publisher.publishUniformEvent(event);
    }

    @Override
    public boolean send(String topic, String eventCode, boolean transactional, Object payload, long timeout, int delayTimeLevel) throws MQException {
        UniformEvent event = null;
        UniformEventPublisher publisher = null;
        if (this.isOns()) {
            publisher = this.onsPublisherHolder.getOnsPublisher();
            Assert.notNull(publisher, "message publisher can not be null");
            event = publisher.createUniformEvent(topic, eventCode, transactional, payload, timeout);
            event.setDelayTimeLevel(delayTimeLevel);
            return publisher.publishUniformEvent(event);
        } else {
            publisher = this.rocketPublisherHolder.getPublisher();
            Assert.notNull(publisher, "message publisher can not be null");
            event = publisher.createUniformEvent(topic, eventCode, transactional, payload, timeout);
            event.setDelayTimeLevel(delayTimeLevel);
            return publisher.publishUniformEvent(event);
        }
    }

    private boolean isOns() {
        return StringUtils.endsWithIgnoreCase(mqProperties.getClientType(), ClientTypeEnum.ons.name());
    }
}
