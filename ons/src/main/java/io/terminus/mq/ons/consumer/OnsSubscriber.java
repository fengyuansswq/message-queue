/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.terminus.mq.ons.consumer;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.OrderConsumerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderAction;
import com.google.common.base.Throwables;
import io.terminus.mq.client.UniformEventListener;
import io.terminus.mq.common.UniformEventSubscriber;
import io.terminus.mq.exception.MQException;
import io.terminus.mq.model.DefaultUniformEvent;
import io.terminus.mq.model.UniformEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/8 上午10:34 sean Exp $
 * @description
 */
@Slf4j
public class OnsSubscriber implements UniformEventSubscriber, DisposableBean {

    private static final int     DEFAULT_CONSUME_THREAD_MAX = 64;
    private static final String  DEFAULT_TAG_EXPRESSION     = "*";

    /** 注册中心地址 */
    private String               nameServerAddr;

    /** 消费者ID */
    private String               groupId;

    /** accessKey */
    private String               accessKey;

    /** accessKey */
    private String               secretKey;

    /** 消费线程数 */
    private Integer              consumeThreadNums;

    /** 启动属性 */
    private Properties           properties;

    /** 监听器  */
    private UniformEventListener listener;

    /** 顺序消费 */
    private OrderConsumerBean    orderConsumer;

    /** 并发消费 */
    private ConsumerBean         consumer;

    public OnsSubscriber(String nameServerAddr, String consumerId, String accessKey, String secretKey, Integer consumeThreadNums) {
        this.nameServerAddr = nameServerAddr;
        this.groupId = consumerId;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.consumeThreadNums = consumeThreadNums;
        initProperties();
    }

    private void initProperties() {
        properties = new Properties();
//        properties.put(PropertyKeyConst.ONSAddr, nameServerAddr);
        properties.put(PropertyKeyConst.NAMESRV_ADDR, nameServerAddr);
        properties.put(PropertyKeyConst.GROUP_ID, groupId);
        properties.put(PropertyKeyConst.AccessKey, accessKey);
        properties.put(PropertyKeyConst.SecretKey, secretKey);
        properties.put(PropertyKeyConst.ConsumeThreadNums, getConsumeThreadMax(consumeThreadNums));
    }

    @Override
    public void start() throws MQException {
        try {
            Assert.notNull(listener, "统一消息事件监听器UniformEventListener未设置");
            if (listener.getListenerType() == UniformEventListener.ListenerTypeEnum.CONCURRENTLY) {
                consumer.start();
            } else if (listener.getListenerType() == UniformEventListener.ListenerTypeEnum.ORDERLY) {
                orderConsumer.start();
            } else {
                throw new IllegalArgumentException("统一消息事件监听器类型不支持，类型：{}" + listener.getListenerType());
            }
        } catch (Exception e) {
            log.error("ons consumer start failed ,cause:{}", Throwables.getStackTraceAsString(e));
            throw new MQException("ons consumer start failed");
        }
    }

    @Override
    public void registerUniformEventMessageListener(UniformEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void subscribe(String topic, String tags) throws MQException {
        if (listener.getListenerType() == UniformEventListener.ListenerTypeEnum.CONCURRENTLY) {
            consumer = registerCurrentlyConsumer(topic, tags);
        } else if (listener.getListenerType() == UniformEventListener.ListenerTypeEnum.ORDERLY) {
            orderConsumer = registerOrderlyConsumer(topic, tags);
        }
    }

    @Override
    public void subscribe(String topic, String fullClassName, String filterClassSource) throws MQException {
        //todo
    }

    @Override
    public void resume() {

    }

    @Override
    public void suspend() {
    }

    @Override
    public void shutdown() throws MQException {
        if (listener.getListenerType() == UniformEventListener.ListenerTypeEnum.CONCURRENTLY) {
            consumer.shutdown();
        } else if (listener.getListenerType() == UniformEventListener.ListenerTypeEnum.ORDERLY) {
            orderConsumer.shutdown();
        } else {
            throw new MQException("统一消息事件监听器类型不支持，类型：{}" + listener.getListenerType());
        }
    }

    @Override
    public String getGroup() {
        return groupId;
    }

    @Override
    public String getNameSrvAddress() {
        return nameServerAddr;
    }

    /**
     * 并发消费
     * @param topic
     * @param tags
     * @return
     */
    private ConsumerBean registerCurrentlyConsumer(String topic, String tags) {
        consumer = new ConsumerBean();
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();
        Subscription subscription = new Subscription();
        subscription.setTopic(topic);
        subscription.setExpression(getTagExpression(tags));
        MessageListener messageListener = (message, context) -> {

            try {
                UniformEvent event = createUniformEvent(message);
                listener.onUniformEvent(event);
                return Action.CommitMessage;
            } catch (Exception e) {
                return Action.ReconsumeLater;
            }
        };
        subscriptionTable.put(subscription, messageListener);
        consumer.setProperties(properties);
        consumer.setSubscriptionTable(subscriptionTable);
        return consumer;
    }

    /**
     * 顺序消费
     * @param topic
     * @param tags
     * @return
     */
    private OrderConsumerBean registerOrderlyConsumer(String topic, String tags) {

        orderConsumer = new OrderConsumerBean();
        Map<Subscription, MessageOrderListener> subscriptionTable = new HashMap<>();
        Subscription subscription = new Subscription();
        subscription.setTopic(topic);
        subscription.setExpression(getTagExpression(tags));

        MessageOrderListener messageOrderListener = (message, ordercontext) -> {

            log.info("<<< 开始消费，消息topic:{},消息ID:{}", topic, message.getMsgID());
            try {
                UniformEvent event = createUniformEvent(message);
                listener.onUniformEvent(event);
                return OrderAction.Success;
            } catch (Exception e) {
                return OrderAction.Suspend;
            }
        };
        subscriptionTable.put(subscription, messageOrderListener);
        orderConsumer.setProperties(properties);
        orderConsumer.setSubscriptionTable(subscriptionTable);
        return orderConsumer;
    }

    /**
     *
     * @param message
     * @return
     */
    private UniformEvent createUniformEvent(Message message) {
        if (Objects.isNull(message)) {
            throw new IllegalStateException("消息消费失败，未能获取到消息实体");
        }

        // 因为在生产者端设定了每次只投递一条消息，所以这里只获取第一条消息进行处理
        String payload = new String(message.getBody());

        UniformEvent event = new DefaultUniformEvent(message.getTopic(), message.getTag(),message.getMsgID());
        event.setPayload(payload);

        return event;
    }

    private String getTagExpression(String tags) {
        if (StringUtils.isEmpty(tags)) {
            return DEFAULT_TAG_EXPRESSION;
        }
        return tags;
    }

    private int getConsumeThreadMax(Integer consumeThreadNums) {
        if (consumeThreadNums == null || consumeThreadNums.equals(0)) {
            return DEFAULT_CONSUME_THREAD_MAX;
        }
        return consumeThreadNums;
    }

    @Override
    public void destroy() throws Exception {
        this.shutdown();
    }
}
