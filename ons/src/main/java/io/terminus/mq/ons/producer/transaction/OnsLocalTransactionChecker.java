package io.terminus.mq.ons.producer.transaction;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author sean
 * @version Id:,v0.1 2018/11/9 4:20 PM sean Exp $
 * @description
 */
@Component
public class OnsLocalTransactionChecker implements ApplicationContextAware,LocalTransactionChecker {

    @Override
    public TransactionStatus check(Message message) {
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //TODO
    }
}
