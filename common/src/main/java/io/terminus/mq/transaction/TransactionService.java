package io.terminus.mq.transaction;

import java.lang.annotation.*;

/**
 * @author sean
 * @version Id:,v0.1 2018/11/12 5:17 PM sean Exp $
 * @description
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TransactionService {

    String topic() default "";

    String tag() default "";
}
