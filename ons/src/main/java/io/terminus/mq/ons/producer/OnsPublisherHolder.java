/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.terminus.mq.ons.producer;

import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;
import io.terminus.mq.config.MQProducerProperties;
import io.terminus.mq.config.MQProperties;
import io.terminus.mq.exception.MQException;
import lombok.Data;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/8 下午2:04 sean Exp $
 * @description
 */
@Component
@Data
public class OnsPublisherHolder implements DisposableBean {

    @Autowired
    private MQProducerProperties     producerProperties;

    @Autowired
    private MQProperties             mqProperties;

    @Autowired
    private LocalTransactionChecker  onsLocalTransactionChecker;

    @Autowired
    private LocalTransactionExecuter onsLocalTransactionExecuter;

    private OnsPublisher             onsPublisher;

    public void init() {
        try {
            String producerId = producerProperties.getProducerId();
            int timeout = producerProperties.getTimeout();
            String nameServerAddr = mqProperties.getNameServer();
            String accessKey = mqProperties.getAccessKey();
            String secretKey = mqProperties.getSecretKey();

            onsPublisher = new OnsPublisher(nameServerAddr, producerId, accessKey, secretKey, timeout);
            onsPublisher.setOnsLocalTransactionChecker(onsLocalTransactionChecker);
            onsPublisher.setOnsLocalTransactionExecuter(onsLocalTransactionExecuter);
            onsPublisher.start();
        } catch (MQException e) {
            throw new RuntimeException("message producer init fail");
        }
    }

    @Override
    public void destroy() throws Exception {
        if (onsPublisher == null) {
            return;
        }
        onsPublisher.shutdown();
    }
}
