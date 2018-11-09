package io.terminus.mq.rocket.producer.transaction;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author sean
 * @version Id:,v0.1 2018/11/9 4:12 PM sean Exp $
 * @description
 */
@Component
public class RocketMQTransactionListener implements ApplicationContextAware, TransactionListener {



    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        return null;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
