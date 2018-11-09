/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.terminus.mq.client;

import io.terminus.mq.exception.MQException;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/6 下午9:50 sean Exp $
 * @description
 */
public interface MessageProducer {

    /**
     * 消息投递（Oneway方式）
     *
     * @param topic     主题
     * @param eventCode 事件码
     * @param payload   业务数据
     * @return
     * @throws MQException
     */
    boolean sendOneway(String topic, String eventCode, Object payload) throws MQException;

    /**
     * 消息投递（同步方式）
     *
     * @param topic     主题
     * @param eventCode 事件码
     * @param payload   业务数据
     * @return
     * @throws MQException
     */
    boolean send(String topic, String eventCode, Object payload) throws MQException;

    /**
     * 消息投递（同步方式）
     *
     * @param topic     主题
     * @param eventCode 事件码
     * @param payload   业务数据
     * @param timeout   投递超时时间
     * @return
     * @throws MQException
     */
    boolean send(String topic, String eventCode, Object payload, long timeout) throws MQException;

    /**
     * 消息投递（同步方式） 延时等级
     *
     * @param topic          主题
     * @param eventCode      时间码
     * @param payload        业务数据
     * @param timeout        超时时间
     * @param delayTimeLevel 延时等级
     * @return
     * @throws MQException
     */
    boolean send(String topic, String eventCode, Object payload, long timeout, int delayTimeLevel) throws MQException;

    /**
     * 消息投递（同步方式）
     *
     * @param topic         主题
     * @param eventCode     事件码
     * @param transactional 是否事务消息
     * @param payload       业务数据
     * @return
     * @throws MQException
     */
    boolean send(String topic, String eventCode, boolean transactional, Object payload) throws MQException;

    /**
     * 消息投递（同步方式）
     *
     * @param topic         主题
     * @param eventCode     事件码
     * @param transactional 是否事务消息
     * @param payload       业务数据
     * @param timeout       超时时间
     * @return
     * @throws MQException
     */
    boolean send(String topic, String eventCode, boolean transactional, Object payload, long timeout) throws MQException;

    /**
     * 消息投递（同步方式）
     *
     * @param topic          主题
     * @param eventCode      事件码
     * @param transactional  是否事务消息
     * @param payload        业务数据
     * @param timeout        超时时间
     * @param delayTimeLevel 延时等级
     * @return
     * @throws MQException
     */
    boolean send(String topic, String eventCode, boolean transactional, Object payload, long timeout, int delayTimeLevel) throws MQException;

}
