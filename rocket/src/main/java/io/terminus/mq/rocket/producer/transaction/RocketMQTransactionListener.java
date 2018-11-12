package io.terminus.mq.rocket.producer.transaction;

import io.terminus.mq.transaction.TransactionServiceContainer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Throwables;

import io.terminus.mq.CommonConstants;
import io.terminus.mq.exception.MQTransactionException;
import io.terminus.mq.transaction.LocalTransactionService;
import io.terminus.mq.utils.HashUtil;

/**
 * @author sean
 * @version Id:,v0.1 2018/11/9 4:12 PM sean Exp $
 * @description
 */
@Component
@Slf4j
public class RocketMQTransactionListener implements TransactionListener {

    @Autowired
    private TransactionServiceContainer transactionServiceContainer;

    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {

        // 消息 ID（有可能消息体一样，但消息 ID 不一样，当前消息 ID 在控制台无法查询）
        String msgId = msg.getTransactionId();
        // 消息体内容进行 crc32，也可以使用其它的如 MD5
        long crc32Id = HashUtil.crc32Code(msg.getBody());
        // 消息 ID 和 crc32id 主要是用来防止消息重复
        // 如果业务本身是幂等的，可以忽略，否则需要利用 msgId 或 crc32Id 来做幂等
        // 如果要求消息绝对不重复，推荐做法是对消息体 body 使用 crc32或 md5来防止重复消息
        LocalTransactionState transactionStatus = LocalTransactionState.UNKNOW;
        try {
            String key = msg.getTopic().concat(CommonConstants.PLUS).concat(msg.getTags());

            LocalTransactionService localTransactionService = transactionServiceContainer.getLocalTransactionServiceMap().get(key);
            boolean isCommit = localTransactionService.executeTransaction(msgId, crc32Id);
            if (isCommit) {
                // 本地事务成功则提交消息
                transactionStatus = LocalTransactionState.COMMIT_MESSAGE;
            } else {
                // 本地事务失败则回滚消息
                transactionStatus = LocalTransactionState.ROLLBACK_MESSAGE;
            }
        } catch (MQTransactionException e) {
            transactionStatus = LocalTransactionState.ROLLBACK_MESSAGE;
            log.error("Transaction execute failed ,RocketMQ Message Id:{} ,cause = {}", msgId, Throwables.getStackTraceAsString(e));
        } catch (Exception e) {
            transactionStatus = LocalTransactionState.ROLLBACK_MESSAGE;
            log.error("Transaction execute failed ,RocketMQ Message Id:{} ,cause = {}", msgId, Throwables.getStackTraceAsString(e));
        }
        return transactionStatus;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        //消息 ID（有可能消息体一样，但消息 ID 不一样，当前消息属于 Half 消息，所以消息 ID 在控制台无法查询）
        String msgId = msg.getTransactionId();
        //消息体内容进行 crc32，也可以使用其它的方法如 MD5
        long crc32Id = HashUtil.crc32Code(msg.getBody());
        //消息 ID、消息本 crc32Id 主要是用来防止消息重复
        //如果业务本身是幂等的，可以忽略，否则需要利用 msgId 或 crc32Id 来做幂等
        //如果要求消息绝对不重复，推荐做法是对消息体使用 crc32 或  md5 来防止重复消息
        //业务自己的参数对象，这里只是一个示例，需要您根据实际情况来处理
        LocalTransactionState transactionStatus = LocalTransactionState.UNKNOW;
        try {
            String key = msg.getTopic().concat(CommonConstants.PLUS).concat(msg.getTags());

            LocalTransactionService localTransactionService = transactionServiceContainer.getLocalTransactionServiceMap().get(key);
            boolean isCommit = localTransactionService.checkTransactionStatus(msgId, crc32Id);
            if (isCommit) {
                //本地事务已成功则提交消息
                transactionStatus = LocalTransactionState.COMMIT_MESSAGE;
            } else {
                //本地事务已失败则回滚消息
                transactionStatus = LocalTransactionState.ROLLBACK_MESSAGE;
            }
        } catch (MQTransactionException e) {
            transactionStatus = LocalTransactionState.ROLLBACK_MESSAGE;
            log.error("transaction status check failed ,RocketMQ Message Id:{} ,cause = {}", msgId, Throwables.getStackTraceAsString(e));
        } catch (Exception e) {
            transactionStatus = LocalTransactionState.ROLLBACK_MESSAGE;
            log.error("transaction status check failed ,RocketMQ Message Id:{} ,cause = {}", msgId, Throwables.getStackTraceAsString(e));
        }
        log.info("RocketMQ Message Id:{}, transactionStatus:{}", msgId, transactionStatus.name());
        return transactionStatus;
    }

}
