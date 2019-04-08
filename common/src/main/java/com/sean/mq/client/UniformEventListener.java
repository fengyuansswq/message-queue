/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package com.sean.mq.client;


import com.sean.mq.model.UniformEvent;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/6 下午8:11 sean Exp $
 * @description
 */
public interface UniformEventListener {

    /**
     * 获取监听器类型
     *
     * @return
     */
    ListenerTypeEnum getListenerType();

    /**
     * 实现业务代码的接口
     * @param event
     * @return
     */
    boolean onUniformEvent(UniformEvent event);

    enum ListenerTypeEnum {
                           CONCURRENTLY, ORDERLY
    }

    String getTopic();
}
