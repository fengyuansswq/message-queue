/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package com.sean.mq.common;

import com.sean.mq.client.UniformEventListener;
import com.sean.mq.exception.MQException;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/8 上午10:56 sean Exp $
 * @description
 */
public interface UniformEventSubscriber extends BaseUniformEventProcessor {

    /**
     * 注册统一消息事件监听器
     *
     * @param listener
     */
    void registerUniformEventMessageListener(UniformEventListener listener);

    /**
     * 订阅
     *
     * @param topic
     * @param eventId
     */
    void subscribe(final String topic, final String eventId) throws MQException;

    /**
     * 订阅
     *
     * @param topic
     * @param fullClassName
     * @param filterClassSource
     */
    void subscribe(final String topic, final String fullClassName, final String filterClassSource) throws MQException;

    /**
     * 恢复
     */
    void resume();

    /**
     * 挂起
     */
    void suspend();
}
