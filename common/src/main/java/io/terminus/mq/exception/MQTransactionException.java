package io.terminus.mq.exception;

/**
 * @author sean
 * @version Id:,v0.1 2018/11/12 5:12 PM sean Exp $
 * @description
 */
public class MQTransactionException extends Exception {
    public MQTransactionException() {
    }

    public MQTransactionException(String message) {
        super(message);
    }

    public MQTransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public MQTransactionException(Throwable cause) {
        super(cause);
    }

    public MQTransactionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
