package com.sean.mq.ons.producer.transaction;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import com.google.common.base.Throwables;
import io.terminus.mq.CommonConstants;
import io.terminus.mq.exception.MQTransactionException;
import io.terminus.mq.transaction.LocalTransactionService;
import io.terminus.mq.transaction.TransactionServiceContainer;
import io.terminus.mq.utils.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sean
 * @version Id:,v0.1 2018/11/9 3:08 PM sean Exp $
 * @description
 */
@Slf4j
@Component
public class OnsLocalTransactionExecuter implements LocalTransactionExecuter {

    @Autowired
    private TransactionServiceContainer transactionServiceContainer;

    @Override
    public TransactionStatus execute(Message message, Object obj) {
        // 消息 ID（有可能消息体一样，但消息 ID 不一样，当前消息 ID 在控制台无法查询）
        String msgId = message.getMsgID();
        // 消息体内容进行 crc32，也可以使用其它的如 MD5
        long crc32Id = HashUtil.crc32Code(message.getBody());
        // 消息 ID 和 crc32id 主要是用来防止消息重复
        // 如果业务本身是幂等的，可以忽略，否则需要利用 msgId 或 crc32Id 来做幂等
        // 如果要求消息绝对不重复，推荐做法是对消息体 body 使用 crc32或 md5来防止重复消息
        Object businessServiceArgs = new Object();
        TransactionStatus transactionStatus = TransactionStatus.Unknow;
        try {
            String key = message.getTopic().concat(CommonConstants.PLUS).concat(message.getTag());

            LocalTransactionService localTransactionService = transactionServiceContainer.getLocalTransactionServiceMap().get(key);

            Map<String, String> checkProperties = new HashMap<String, String>((Map) message.getUserProperties());

            checkProperties.put(CommonConstants.MESSAGE_ID, msgId);

            checkProperties.put(CommonConstants.MESSAGE_BODY_ENCRYPTION, String.valueOf(crc32Id));

            boolean isCommit = localTransactionService.executeTransaction(checkProperties);
            if (isCommit) {
                // 本地事务成功则提交消息
                transactionStatus = TransactionStatus.CommitTransaction;
            } else {
                // 本地事务失败则回滚消息
                transactionStatus = TransactionStatus.RollbackTransaction;
            }
        } catch (MQTransactionException e) {
            transactionStatus = TransactionStatus.RollbackTransaction;
            log.error("Transaction execute failed ,Ons Message Id:{} ,cause = {}", msgId, Throwables.getStackTraceAsString(e));
        } catch (Exception e) {
            transactionStatus = TransactionStatus.RollbackTransaction;
            log.error("Transaction execute failed ,Ons Message Id:{} ,cause = {}", msgId, Throwables.getStackTraceAsString(e));
        }
        return transactionStatus;
    }

}
