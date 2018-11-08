/**
 * io.sean
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.sean.mq.integration.container;

import com.google.common.collect.Maps;
import io.sean.mq.client.UniformEventListener;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/7 下午9:36 sean Exp $
 * @description
 */
@Component
@Data
public class ListenerContainer {

    private Map<String, UniformEventListener> listeners = Maps.newConcurrentMap();

    @Autowired
    private ApplicationContext                springContext;

    public void init() {
        Map<String, UniformEventListener> beans = springContext.getBeansOfType(UniformEventListener.class, false, true);
        if (beans != null) {
            try {
                for (UniformEventListener bean : beans.values()) {

                    // 判断类上是否有次注解  
                   /* boolean clzHasAnno = bean.getClass().isAnnotationPresent(Subscriber.class);
                    if (!clzHasAnno) {
                        throw new IllegalArgumentException("listener has not register topic");
                    }*/
                    // 获取类上的注解  
                   // Subscriber annotation = bean.getClass().getAnnotation(Subscriber.class);
                    // 输出注解上的属性  
                    //String topic = annotation.topic();

                    //获取listener实例中指定的的topic
                    String topic = bean.getTopic();
                    if(StringUtils.isBlank(topic)){
                        throw new IllegalArgumentException("listener has not identified topic");
                    }

                    listeners.put(topic, bean);
                }
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

    }
}
