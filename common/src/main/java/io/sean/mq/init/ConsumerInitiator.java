/**
 * io.sean
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.sean.mq.init;

import io.sean.mq.client.UniformEventListener;

import java.util.Map;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/8 下午3:49 sean Exp $
 * @description
 */
public interface ConsumerInitiator {

    void onConsumerStartUp(Map<String, UniformEventListener> listeners);
}
