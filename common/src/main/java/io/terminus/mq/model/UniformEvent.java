/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.terminus.mq.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

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
     * 设置消息附加属性，用户级别的属性，如果添加的属性是系统属性，会抛出异常
     *
     * @param propKey
     * @param propVal
     */
    void addProperty(String propKey, String propVal);

    /**
     * 删除消息附加属性，用户级别的属性，如果删除的属性是系统属性，会抛出异常
     *
     * @param propKey
     */
    void removeProperty(String propKey);

    /**
     * 添加消息附加属性，用户级别的属性，如果添加的属性是系统属性，会抛出异常
     *
     * @param properties
     */
    void addProperties(Map<String, String> properties);

    /**
     * 获取用户定义的扩展属性
     *
     * @param key
     * @return
     */
    String getProperty(String key);

    /**
     * 获取用户定义的扩展属性
     *
     * @return
     */
    Map<String, String> getProperties();

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
