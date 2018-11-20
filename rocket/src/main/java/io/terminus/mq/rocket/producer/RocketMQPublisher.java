/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.terminus.mq.rocket.producer;

import com.google.common.base.Throwables;
import io.terminus.mq.common.UniformEventPublisher;
import io.terminus.mq.exception.MQException;
import io.terminus.mq.model.DefaultUniformEvent;
import io.terminus.mq.model.UniformEvent;
import io.terminus.mq.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.Map;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/8 下午12:08 sean Exp $
 * @description
 */
@Slf4j
public class RocketMQPublisher implements UniformEventPublisher {

    /** TurboMQ消息生产者 */
    private DefaultMQProducer     producer;

    /** TurboMQ事务性消息生产者 */
    private TransactionMQProducer transactionalProducer;

    /** 消息分组 */
    private String                group;

    /** 命名服务器地址 */
    private String                nameSrvAddress;

    /** 当消息投递失败，是否重试其他broker */
    private boolean               retryAnotherBrokerWhenNotStore;

    /** 重试次数 */
    private int                   retryTimesWhenSendFailed      = 2;

    /** 消息投递超时时间 */
    private int                   timeout                       = 3000;

    /** 消息体大小 */
    private int                   maxMessageSize                = 128 * 1024;

    /** 客户端回调线程数 */
    private int                   clientCallbackExecutorThreads = Runtime.getRuntime().availableProcessors();

    public RocketMQPublisher(String group, String nameSrvAddress) {
        this.group = group;
        this.nameSrvAddress = nameSrvAddress;
        this.producer = new DefaultMQProducer(group);
        this.transactionalProducer = new TransactionMQProducer(group + UniformEvent.TX_GROUP_SUFFIX);
    }

    public RocketMQPublisher(String group, String nameSrvAddress, boolean retryAnotherBrokerWhenNotStore, int retryTimesWhenSendFailed, int timeout, int maxMessageSize) {
        this.group = group;
        this.nameSrvAddress = nameSrvAddress;
        this.retryAnotherBrokerWhenNotStore = retryAnotherBrokerWhenNotStore;
        this.retryTimesWhenSendFailed = retryTimesWhenSendFailed;
        this.timeout = timeout;
        this.maxMessageSize = maxMessageSize;
    }

    @Override
    public void start() throws MQException {
        try {
            this.producer = new DefaultMQProducer(group);
            this.transactionalProducer = new TransactionMQProducer(group + UniformEvent.TX_GROUP_SUFFIX);

            producer.setNamesrvAddr(nameSrvAddress);
            producer.setRetryAnotherBrokerWhenNotStoreOK(retryAnotherBrokerWhenNotStore);
            producer.setRetryTimesWhenSendFailed(retryTimesWhenSendFailed);
            producer.setSendMsgTimeout(timeout);
            producer.setClientCallbackExecutorThreads(clientCallbackExecutorThreads);
            producer.setMaxMessageSize(maxMessageSize);

            transactionalProducer.setNamesrvAddr(nameSrvAddress);
            transactionalProducer.setRetryAnotherBrokerWhenNotStoreOK(retryAnotherBrokerWhenNotStore);
            transactionalProducer.setRetryTimesWhenSendFailed(retryTimesWhenSendFailed);
            transactionalProducer.setSendMsgTimeout(timeout);
            transactionalProducer.setClientCallbackExecutorThreads(clientCallbackExecutorThreads);
            transactionalProducer.setMaxMessageSize(maxMessageSize);

            producer.start();
            transactionalProducer.start();
            log.info("启动RocketMQ生产者，Group={}, NameServer={}", this.getGroup(), this.getNameSrvAddress());
        } catch (MQClientException e) {
            log.error("rocketmq start failed,cause:{}", Throwables.getStackTraceAsString(e));
            throw new MQException("rocketmq start failed");
        }
    }

    @Override
    public UniformEvent createUniformEvent(String topic, String eventCode) {
        UniformEvent e = new DefaultUniformEvent(topic, eventCode);
        e.setTimeout(timeout);
        return e;
    }

    @Override
    public UniformEvent createUniformEvent(String topic, String eventCode, long timeout) {
        UniformEvent e = new DefaultUniformEvent(topic, eventCode);
        e.setTimeout(timeout);
        return e;
    }

    @Override
    public UniformEvent createUniformEvent(String topic, String eventCode, boolean transactional) {
        UniformEvent e = new DefaultUniformEvent(topic, eventCode);
        e.setTimeout(timeout);
        e.setTransactional(transactional);
        return e;
    }

    @Override
    public UniformEvent createUniformEvent(String topic, String eventCode, boolean transactional, long timeout) {
        UniformEvent e = new DefaultUniformEvent(topic, eventCode);
        e.setTransactional(transactional);
        e.setTimeout(timeout);
        return e;
    }

    @Override
    public UniformEvent createUniformEvent(String topic, String eventCode, boolean transactional, Object payload, long timeout) {
        UniformEvent e = new DefaultUniformEvent(topic, eventCode);
        e.setTimeout(timeout);
        e.setTransactional(transactional);
        e.setPayload(payload);
        return e;
    }

    @Override
    public UniformEvent createUniformEvent(String topic, String eventCode, boolean transactional, Object payload) {
        UniformEvent e = new DefaultUniformEvent(topic, eventCode);
        e.setPayload(payload);
        e.setTimeout(timeout);
        e.setTransactional(transactional);
        return e;
    }

    @Override
    public boolean publishUniformEvent(UniformEvent event) throws MQException {
        if (event == null) {
            throw new IllegalArgumentException("UniformEvent is null");
        }

        // 消息序列化
        Message message = createRocketMQMessage(event);

        // 消息投递
        try {
            log.info("消息发送，统一事件Id：{}，RocketMQ消息：{}", event.getId(), message);
            return doPublishUniformEvent(event, message);
        } catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
            throw new MQException(e);
        }
    }

    protected boolean doPublishUniformEvent(UniformEvent event, Message message) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        //TODO  不支持异步回调，比较麻烦 且使用业务场景较少
        // sync
        SendResult sendResult = doSendMessage(event, message);
        if (sendResult == null) {
            log.error("同步消息发送失败，事件id：{}", event.getId());
            return false;
        }
        if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
            log.error("同步消息发送失败，事件id：{}，消息ID：{}，错误代码", event.getId(), sendResult.getMsgId(), sendResult.getSendStatus());
            return false;
        }
        log.info("同步消息发送成功，事件id：{}，消息ID：{}", event.getId(), sendResult.getMsgId());
        return true;
    }

    /**
     * 发送消息
     *
     * @param event
     * @param message
     * @return
     * @throws MQClientException
     * @throws RemotingException
     * @throws MQBrokerException
     * @throws InterruptedException
     */
    private SendResult doSendMessage(UniformEvent event, Message message) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        if (event.isTransactional()) {
            //TODO  事务消息的校验参数 需要再斟酌一下
            return transactionalProducer.sendMessageInTransaction(message, new Object());
        }
        return producer.send(message, event.getTimeout());
    }

    /**
     * Create TurboMQ message
     *
     * @param event 统一消息事件
     * @return
     * @throws MQException
     */
    private Message createRocketMQMessage(UniformEvent event) throws MQException {
        try {
            // 序列化数据
            byte[] data = JacksonUtils.toJson(event.getPayload()).getBytes();

            Message message = new Message(event.getTopic(), event.getEventCode(), data);

            if (event.getDelayTimeLevel() > 0) {
                message.setDelayTimeLevel(event.getDelayTimeLevel());
            }

            // 写入用户定义的消息扩展属性
            for (Map.Entry<String, String> prop : event.getProperties().entrySet()) {
                message.putUserProperty(prop.getKey(), prop.getValue());
            }

            return message;
        } catch (Exception e) {
            throw new MQException("构建RocketMQ 消息实体失败");
        }
    }

    @Override
    public boolean publishUniformEventOneway(UniformEvent event) throws MQException {
        if (event == null) {
            throw new IllegalArgumentException("UniformEvent is null");
        }

        // 消息序列化
        Message message = createRocketMQMessage(event);

        // 消息投递
        try {
            log.info("Oneway消息发送，统一事件：{}，TRocketMq消息：{}", event, message);
            producer.sendOneway(message);
            log.info("Oneway消息发送成功，事件id：{}", event.getId());
            return true;
        } catch (MQClientException | RemotingException | InterruptedException e) {
            throw new MQException(e);
        }
    }

    @Override
    public void shutdown() throws MQException {
        try {
            producer.shutdown();
            transactionalProducer.shutdown();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getNameSrvAddress() {
        return nameSrvAddress;
    }
}
