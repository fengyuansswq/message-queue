package io.terminus.mq.transaction;

import io.terminus.mq.exception.MQTransactionException;

import java.util.Map;

/**
 * @author sean
 * @version Id:,v0.1 2018/11/12 5:12 PM sean Exp $
 * @description
 */
public interface LocalTransactionService {

    Boolean executeTransaction(Map<String, String> checkProperties) throws MQTransactionException;

    Boolean checkTransactionStatus(Map<String, String> checkProperties) throws MQTransactionException;
}
