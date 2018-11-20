/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.terminus.mq.ons.producer;

import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;
import com.aliyun.openservices.ons.api.transaction.TransactionProducer;
import com.google.common.base.Throwables;
import io.terminus.mq.common.UniformEventPublisher;
import io.terminus.mq.enums.DelayTimeLevelEnum;
import io.terminus.mq.exception.MQException;
import io.terminus.mq.model.DefaultUniformEvent;
import io.terminus.mq.model.UniformEvent;
import io.terminus.mq.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Properties;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/8 下午12:12 sean Exp $
 * @description
 */
@Slf4j
public class OnsPublisher implements UniformEventPublisher {

    /** 注册中心地址 */
    private String                   nameServerAddr;

    /** 生产者Id */
    private String                   producerId;

    /** accessKey */
    private String                   accessKey;

    /** secretKey */
    private String                   secretKey;

    /** 发送的超时时间 */
    private int                      sendTimeOut;

    /** 事务消息本地校验器 */
    private LocalTransactionChecker  onsLocalTransactionChecker;

    /** 事务消息本地执行器 */
    private LocalTransactionExecuter onsLocalTransactionExecuter;

    /** 生产者 */
    private Producer                 producer;

    /** 事务消息生产者 */
    private TransactionProducer      transactionProducer;

    public OnsPublisher(String nameServerAddr, String producerId, String accessKey, String secretKey, int sendTimeOut) {
        this.nameServerAddr = nameServerAddr;
        this.producerId = producerId;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.sendTimeOut = sendTimeOut;
    }

    @Override
    public void start() throws MQException {
        try {
            // container 实例配置初始化
            Properties properties = new Properties();
            //在控制台创建的Producer ID
            properties.setProperty(PropertyKeyConst.ProducerId, producerId);
            // AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
            properties.setProperty(PropertyKeyConst.AccessKey, accessKey);
            // SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
            properties.setProperty(PropertyKeyConst.SecretKey, secretKey);
            //设置发送超时时间，单位毫秒
            properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, Integer.toString(sendTimeOut));
            // 设置 TCP 接入域名
            properties.setProperty(PropertyKeyConst.ONSAddr, nameServerAddr);
            producer = ONSFactory.createProducer(properties);

            TransactionProducer transactionProducer = ONSFactory.createTransactionProducer(properties, onsLocalTransactionChecker);
            // 在发送消息前，必须调用start方法来启动Producer，只需调用一次即可
            producer.start();

            transactionProducer.start();

        } catch (Exception e) {
            log.error("ons mq producer start fail,cause:{}", Throwables.getStackTraceAsString(e));
            throw new MQException("ons mq producer start fail");
        }
    }

    @Override
    public boolean publishUniformEvent(UniformEvent event) throws MQException {
        if (event == null) {
            throw new IllegalArgumentException("UniformEvent is null");
        }

        Message message = createOnsMessage(event);
        SendResult sendResult;
        try {
            if (event.isTransactional()) {
                sendResult = transactionProducer.send(message, onsLocalTransactionExecuter, new Object());
            } else {
                sendResult = producer.send(message);
            }
            log.info("消息发送，消息Id:{}，ONS消息：{}", sendResult.getMessageId(), JacksonUtils.toJson(event.getPayload()));
            return true;
        } catch (RuntimeException e) {
            throw new MQException(e);
        }
    }

    /**
     * @param event
     * @return
     * @throws MQException
     */
    private Message createOnsMessage(UniformEvent event) throws MQException {
        try {
            // 序列化数据
            byte[] data = JacksonUtils.toJson(event.getPayload()).getBytes();

            Message message = new Message(event.getTopic(), event.getEventCode(), data);

            if (event.getDelayTimeLevel() > 0) {
                Long sendTime = System.currentTimeMillis() + DelayTimeLevelEnum.getEnumByLevel(event.getDelayTimeLevel()).getTimeDelay();
                message.setStartDeliverTime(sendTime);
            }
            if (event.getScheduleTime() != null) {
                Long scheduleTime = event.getScheduleTime().getTime();
                message.setStartDeliverTime(scheduleTime);
            }

            // 写入用户定义的消息扩展属性
            Properties properties = new Properties();
            for (Map.Entry<String, String> prop : event.getProperties().entrySet()) {
                properties.put(prop.getKey(), prop.getValue());
            }
            message.setUserProperties(properties);

            return message;
        } catch (Exception e) {
            throw new MQException("消息实体构建失败");
        }
    }

    @Override
    public boolean publishUniformEventOneway(UniformEvent event) throws MQException {
        if (event == null) {
            throw new IllegalArgumentException("UniformEvent is null");
        }
        Message message = createOnsMessage(event);

        try {
            producer.sendOneway(message);
            log.info("Oneway消息发送成功，ONS消息：{}", JacksonUtils.toJson(event.getPayload()));
            return true;
        } catch (RuntimeException e) {
            throw new MQException(e);
        }
    }

    @Override
    public UniformEvent createUniformEvent(String topic, String eventCode) {
        UniformEvent e = new DefaultUniformEvent(topic, eventCode);
        e.setTimeout(sendTimeOut);
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
        e.setTransactional(transactional);
        e.setTimeout(sendTimeOut);
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
        e.setTransactional(transactional);
        e.setPayload(payload);
        e.setTimeout(timeout);
        return e;
    }

    @Override
    public UniformEvent createUniformEvent(String topic, String eventCode, boolean transactional, Object payload) {
        UniformEvent e = new DefaultUniformEvent(topic, eventCode);
        e.setTransactional(transactional);
        e.setTimeout(sendTimeOut);
        e.setPayload(payload);
        return e;
    }

    @Override
    public void shutdown() throws MQException {
        producer.shutdown();
    }

    @Override
    public String getGroup() {
        return producerId;
    }

    @Override
    public String getNameSrvAddress() {
        return nameServerAddr;
    }

    public void setOnsLocalTransactionChecker(LocalTransactionChecker onsLocalTransactionChecker) {
        this.onsLocalTransactionChecker = onsLocalTransactionChecker;
    }

    public void setOnsLocalTransactionExecuter(LocalTransactionExecuter onsLocalTransactionExecuter) {
        this.onsLocalTransactionExecuter = onsLocalTransactionExecuter;
    }
}
