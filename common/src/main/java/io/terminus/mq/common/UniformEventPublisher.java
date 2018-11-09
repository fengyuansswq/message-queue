/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.terminus.mq.common;

import io.terminus.mq.exception.MQException;
import io.terminus.mq.model.UniformEvent;

/**
 * 统一事件发布
 *
 * @author sean
 * @version Id:,v0.1 2018/6/8 下午12:09 sean Exp $
 * @description
 */
public interface UniformEventPublisher extends BaseUniformEventProcessor {

    /**
     * 发布统一消息事件
     *
     * @param event
     * @return
     */
    boolean publishUniformEvent(UniformEvent event) throws MQException;

    /**
     * 发布统一消息事件(oneway方式)
     *
     * @param event
     * @return
     * @throws MQException
     */
    boolean publishUniformEventOneway(UniformEvent event) throws MQException;

    /**
     * 创建统一消息事件
     *
     * @param topic     主题
     * @param eventCode 事件码
     * @return
     */
    UniformEvent createUniformEvent(String topic, String eventCode);

    /**
     * 创建统一消息事件
     *
     * @param topic     主题
     * @param eventCode 事件码
     * @return
     */
    UniformEvent createUniformEvent(String topic, String eventCode, long timeout);

    /**
     * 创建统一消息事件
     *
     * @param topic         主题
     * @param eventCode     事件码
     * @param transactional 事务消息
     * @return
     */
    UniformEvent createUniformEvent(String topic, String eventCode, boolean transactional);

    /**
     * 创建统一消息事件
     *
     * @param topic         主题
     * @param eventCode     事件码
     * @param transactional 事务消息
     * @return
     */
    UniformEvent createUniformEvent(String topic, String eventCode, boolean transactional, long timeout);

    /**
     * @param topic         主题
     * @param eventCode     事件码
     * @param transactional 事务消息
     * @param payload       消息体
     * @return
     */
    UniformEvent createUniformEvent(String topic, String eventCode, boolean transactional, Object payload);

    /**
     * @param topic         主题
     * @param eventCode     事件码
     * @param transactional 事务消息
     * @param payload       消息体
     * @return
     */
    UniformEvent createUniformEvent(String topic, String eventCode, boolean transactional, Object payload, long timout);

}
