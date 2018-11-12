package io.terminus.mq.transaction;

import io.terminus.mq.exception.MQTransactionException;

/**
 * @author sean
 * @version Id:,v0.1 2018/11/12 5:12 PM sean Exp $
 * @description
 */
public interface LocalTransactionService {

    Boolean executeTransaction(String msgId, Long checkCode) throws MQTransactionException;

    Boolean checkTransactionStatus(String msgId, Long checkCode) throws MQTransactionException;
}
