/**
 * io.sean
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.sean.mq.common;

import io.sean.mq.exception.MQException;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/8 上午10:55 sean Exp $
 * @description
 */
public interface BaseUniformEventProcessor {

    /**
     * 启动统一消息处理器
     */
    void start() throws MQException;

    /**
     * 停止统一消息处理器
     */
    void shutdown() throws MQException;

    /**
     * 获取消息分组
     *
     * @return
     */
    String getGroup();

    /**
     * 获取命名服务器
     *
     * @return
     */
    String getNameSrvAddress();
}
