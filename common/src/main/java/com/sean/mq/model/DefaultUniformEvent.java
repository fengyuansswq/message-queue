/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package com.sean.mq.model;

import com.google.common.collect.Maps;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/6 下午8:55 sean Exp $
 * @description
 */
public class DefaultUniformEvent implements UniformEvent {

    /** 消息ID */
    private String              id;

    /** 消息主题 */
    private String              topic;

    /** 消息事件码 */
    private String              eventCode;

    /** 消息体 */
    private Object              payload;

    /** 发送超时时间 */
    private long                timeout;

    /** 延时消息等级 */
    private int                 delayTimeLevel;

    /** 延时消息等级 */
    private Date                scheduleTime;

    /** 用户级别消息附加属性 */
    private Map<String, String> userProps = Maps.newLinkedHashMap();

    /** 是否是事务消息 */
    private boolean             transactional;

    /**
     * @param topic
     * @param eventCode
     */
    public DefaultUniformEvent(String topic, String eventCode, String msgId) {
        this.topic = topic;
        this.eventCode = eventCode;
        this.id = msgId;
    }

    public DefaultUniformEvent(String topic, String eventCode) {
        this.topic = topic;
        this.eventCode = eventCode;
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String getEventCode() {
        return eventCode;
    }

    @Override
    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    @Override
    public Object getPayload() {
        return payload;
    }

    @Override
    public void setTransactional(boolean transactional) {
        this.transactional = transactional;
    }

    public boolean isTransactional() {
        return transactional;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public int getDelayTimeLevel() {
        return delayTimeLevel;
    }

    @Override
    public void setScheduleTime(Date date) {
        this.scheduleTime = date;
    }

    @Override
    public Date getScheduleTime() {
        return scheduleTime;
    }

    @Override
    public void setDelayTimeLevel(int delayTimeLevel) {
        this.delayTimeLevel = delayTimeLevel;
    }

    @Override
    public void addProperty(String propKey, String propVal) {
        userProps.put(propKey, propVal);
    }

    public void removeProperty(String propKey) {
        userProps.remove(propKey);
    }

    @Override
    public void addProperties(Map<String, String> properties) {
        if (properties != null && properties.size() != 0) {
            userProps.putAll(properties);
        }
    }

    /**
     * @param key
     * @return
     */
    @Override
    public String getProperty(String key) {
        return userProps.get(key);
    }

    /**
     *
     * @return
     */
    @Override
    public Map<String, String> getProperties() {
        return userProps;
    }
}
