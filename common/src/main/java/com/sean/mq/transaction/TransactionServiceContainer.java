package com.sean.mq.transaction;

import com.google.common.collect.Maps;
import com.sean.mq.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @author sean
 * @version Id:,v0.1 2018/11/12 5:21 PM sean Exp $
 * @description
 */
@Component
@Slf4j
public class TransactionServiceContainer implements ApplicationContextAware {

    private Map<String, LocalTransactionService> localTransactionServiceMap = Maps.newConcurrentMap();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, LocalTransactionService> beanMap = applicationContext.getBeansOfType(LocalTransactionService.class);
        if (CollectionUtils.isEmpty(beanMap)) {
            log.warn("there's no local transaction service exist in applicationContext");
        }
        for (Map.Entry<String, LocalTransactionService> me : beanMap.entrySet()) {
            String topic = me.getValue().getTopic();
            String tag = me.getValue().getTag();
            if (StringUtils.isBlank(topic) || StringUtils.isBlank(tag)) {
                log.warn("the localTransactionService is not combined with a topic and a tag, beanName = {}", me.getKey());
                continue;
            }
            localTransactionServiceMap.put(topic.concat(CommonConstants.PLUS).concat(tag), me.getValue());
        }
    }

    public Map<String, LocalTransactionService> getLocalTransactionServiceMap() {
        return localTransactionServiceMap;
    }

}
