/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package com.sean.mq.rocket.consumer;

import com.google.common.base.Throwables;
import io.terminus.mq.client.UniformEventListener;
import io.terminus.mq.common.UniformEventSubscriber;
import io.terminus.mq.exception.MQException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.Assert;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/8 上午11:52 sean Exp $
 * @description
 */
@Slf4j
public class RocketMQSubscriber implements UniformEventSubscriber, DisposableBean {

    private static final int      DEFAULT_CONSUME_THREAD_MAX       = 64;
    private static final int      DEFAULT_CONSUME_THREAD_MIN       = 20;
    private static final int      DEFAULT_PULL_BATCH_SIZE          = 32;
    private static final int      DEFAULT_PULL_THRESHOLD_FOR_QUEUE = 1000;

    /** RocketMQ consumer */
    private DefaultMQPushConsumer consumer;

    /** 消息分组 */
    private String                group;

    /** 命名服务器地址 */
    private String                nameSrvAddress;

    /** 从队列的哪里开始消费 */
    private ConsumeFromWhere      consumeFromWhere;

    /** 消费模型 */
    private MessageModel          messageModel;

    /** 统一消息事件监听器 */
    private UniformEventListener  listener;

    /** 最大消费线程数 */
    private int                   consumeThreadMax;

    /** 最小消费线程数 */
    private int                   consumeThreadMin;

    /** 一次拉取消息数 */
    private int                   pullBatchSize;

    /** 拉取消息间隔时间 */
    private int                   pullInterval;

    /** 拉取消息阀值 */
    private int                   pullThresholdForQueue;

    /**
     * @param group
     * @param nameSrvAddress
     */
    public RocketMQSubscriber(String group, String nameSrvAddress) {
        this(group, nameSrvAddress, ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET, MessageModel.CLUSTERING, DEFAULT_CONSUME_THREAD_MAX, DEFAULT_CONSUME_THREAD_MIN,
            DEFAULT_PULL_BATCH_SIZE, 0, DEFAULT_PULL_THRESHOLD_FOR_QUEUE);
    }

    public RocketMQSubscriber(String group, String nameSrvAddress, int consumeThreadMax, int consumeThreadMin, int pullBatchSize, int pullInterval, int pullThresholdForQueue) {
        this(group, nameSrvAddress, ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET, MessageModel.CLUSTERING, consumeThreadMax, consumeThreadMin, pullBatchSize, pullInterval,
            pullThresholdForQueue);
    }

    /**
     * @param group
     * @param nameSrvAddress
     * @param consumeFromWhere
     * @param messageModel
     */
    public RocketMQSubscriber(String group, String nameSrvAddress, ConsumeFromWhere consumeFromWhere, MessageModel messageModel, int consumeThreadMax, int consumeThreadMin,
                              int pullBatchSize, int pullInterval, int pullThresholdForQueue) {
        this.group = group;
        this.nameSrvAddress = nameSrvAddress;
        this.consumeFromWhere = consumeFromWhere;
        this.messageModel = messageModel;
        this.consumeThreadMax = consumeThreadMax;
        this.consumeThreadMin = consumeThreadMin;
        this.pullBatchSize = pullBatchSize;
        this.pullInterval = pullInterval;
        this.pullThresholdForQueue = pullThresholdForQueue;
        consumer = new DefaultMQPushConsumer(group);
    }

    @Override
    public void start() throws MQException {
        Assert.notNull(listener, "统一消息事件监听器UniformEventMessageListener未设置");
        consumer.setNamesrvAddr(nameSrvAddress);
        consumer.setConsumeFromWhere(getConsumeFromWhere());
        consumer.setMessageModel(getMessageModel());

        consumer.setConsumeThreadMax(this.getConsumeThreadMax());
        consumer.setConsumeThreadMin(this.getConsumeThreadMin());
        consumer.setPullBatchSize(this.getPullBatchSize());
        consumer.setPullInterval(this.getPullInterval());
        consumer.setPullThresholdForQueue(this.getPullThresholdForQueue());


        // register listener
        if (listener.getListenerType() == UniformEventListener.ListenerTypeEnum.CONCURRENTLY) {
            consumer.registerMessageListener(new TerminusMessageListenerConcurrently(listener));
        } else if (listener.getListenerType() == UniformEventListener.ListenerTypeEnum.ORDERLY) {
            consumer.registerMessageListener(new TerminusMessageListenerOrderly(listener));
        } else {
            throw new IllegalArgumentException("统一消息事件监听器类型不支持，类型：" + listener.getListenerType());
        }

        // start consumer
        try {
            consumer.start();
            log.info("RocketMQ消费者注册成功 nameSrvAddress={}, group={}", nameSrvAddress, group);
        } catch (MQClientException e) {
            log.error("RocketMQ consumer start failed,cause:{}", Throwables.getStackTraceAsString(e));
            throw new MQException(e);
        }
    }

    @Override
    public void shutdown() throws MQException {
        consumer.shutdown();
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getNameSrvAddress() {
        return nameSrvAddress;
    }

    public ConsumeFromWhere getConsumeFromWhere() {
        if (consumeFromWhere == null) {
            consumeFromWhere = ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET;
        }
        return consumeFromWhere;
    }

    public MessageModel getMessageModel() {
        if (messageModel == null) {
            messageModel = MessageModel.CLUSTERING;
        }
        return messageModel;
    }

    @Override
    public void registerUniformEventMessageListener(UniformEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void subscribe(String topic, String eventId) throws MQException {
        try {
            consumer.subscribe(topic, eventId);
            log.info("RocketMQ 订阅消费者成功 nameSrvAddress={}, group={},topic={}", nameSrvAddress, group, topic);
        } catch (MQClientException e) {
            throw new MQException(e);
        }
    }

    @Override
    public void subscribe(String topic, String fullClassName, String filterClassSource) throws MQException {
        try {
            consumer.subscribe(topic, fullClassName, filterClassSource);
        } catch (MQClientException e) {
            throw new MQException(e);
        }
    }

    @Override
    public void resume() {
        consumer.resume();
    }

    @Override
    public void suspend() {
        consumer.suspend();
    }

    /**
     * Getter method for property <tt>consumeThreadMax</tt>.
     *
     * @return property value of consumeThreadMax
     */
    public int getConsumeThreadMax() {
        if (consumeThreadMax == 0) {
            consumeThreadMax = DEFAULT_CONSUME_THREAD_MAX;
        }
        return consumeThreadMax;
    }

    /**
     * Setter method for property <tt>consumeThreadMax</tt>.
     *
     * @param consumeThreadMax value to be assigned to property consumeThreadMax
     */
    public void setConsumeThreadMax(int consumeThreadMax) {
        this.consumeThreadMax = consumeThreadMax;
    }

    /**
     * Getter method for property <tt>consumeThreadMin</tt>.
     *
     * @return property value of consumeThreadMin
     */
    public int getConsumeThreadMin() {
        if (consumeThreadMin == 0) {
            consumeThreadMin = DEFAULT_CONSUME_THREAD_MIN;
        }
        return consumeThreadMin;
    }

    /**
     * Setter method for property <tt>consumeThreadMin</tt>.
     *
     * @param consumeThreadMin value to be assigned to property consumeThreadMin
     */
    public void setConsumeThreadMin(int consumeThreadMin) {
        this.consumeThreadMin = consumeThreadMin;
    }

    /**
     * Getter method for property <tt>pullBatchSize</tt>.
     *
     * @return property value of pullBatchSize
     */
    public int getPullBatchSize() {
        if (pullBatchSize == 0) {
            pullBatchSize = DEFAULT_PULL_BATCH_SIZE;
        }
        return pullBatchSize;
    }

    /**
     * Setter method for property <tt>pullBatchSize</tt>.
     *
     * @param pullBatchSize value to be assigned to property pullBatchSize
     */
    public void setPullBatchSize(int pullBatchSize) {
        this.pullBatchSize = pullBatchSize;
    }

    /**
     * Getter method for property <tt>pullInterval</tt>.
     *
     * @return property value of pullInterval
     */
    public int getPullInterval() {
        return pullInterval;
    }

    /**
     * Setter method for property <tt>pullInterval</tt>.
     *
     * @param pullInterval value to be assigned to property pullInterval
     */
    public void setPullInterval(int pullInterval) {
        this.pullInterval = pullInterval;
    }

    /**
     * Getter method for property <tt>pullThresholdForQueue</tt>.
     *
     * @return property value of pullThresholdForQueue
     */
    public int getPullThresholdForQueue() {
        if (pullThresholdForQueue == 0) {
            pullThresholdForQueue = DEFAULT_PULL_THRESHOLD_FOR_QUEUE;
        }
        return pullThresholdForQueue;
    }

    /**
     * Setter method for property <tt>pullThresholdForQueue</tt>.
     *
     * @param pullThresholdForQueue value to be assigned to property pullThresholdForQueue
     */
    public void setPullThresholdForQueue(int pullThresholdForQueue) {
        this.pullThresholdForQueue = pullThresholdForQueue;
    }

    @Override
    public void destroy() throws Exception {
        this.shutdown();
    }
}
