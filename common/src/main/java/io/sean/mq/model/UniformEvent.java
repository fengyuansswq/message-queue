/**
 * io.sean
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.sean.mq.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/4 下午10:44 sean Exp $
 * @description
 */
public interface UniformEvent extends Serializable {

    /** 事务消息分组后缀 */
    static final String TX_GROUP_SUFFIX = "_tx";

    /**
     *
     * @return
     */
    String getId();

    void setId(String id);

    /**
     * 获取主题
     *
     * @return
     */
    String getTopic();

    /**
     * 获取事件码
     *
     * @return
     */
    String getEventCode();

    /**
     * 设置消息载体
     *
     * @param payload
     */
    void setPayload(Object payload);

    /**
     * 获取消息载体
     *
     * @return
     */
    Object getPayload();

    /**
     * 设置发送超时时间
     *
     * @param timeout
     */
    void setTimeout(long timeout);

    /**
     * 获取发送超时时间
     *
     * @return
     */
    long getTimeout();

    /**
     * 设置延时消息等级
     *
     * @param level
     */
    void setDelayTimeLevel(int level);

    /**
     * 获取延时消息等级
     *
     * @return
     */
    int getDelayTimeLevel();

    /**
     * 设置延时消息等级
     *
     * @param date
     */
    void setScheduleTime(Date date);

    /**
     * 获取延时消息等级
     *
     * @return
     */
    Date getScheduleTime();

    /**
     * 设置是否是事务性消息
     *
     * @param transactional
     */
    void setTransactional(boolean transactional);

    /**
     * 获取是否事务性消息
     * @return
     */
    boolean isTransactional();
}
