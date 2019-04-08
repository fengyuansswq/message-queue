/**
 * io.terminus
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package com.sean.mq.component;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/7 下午9:48 sean Exp $
 * @description
 */

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscriber {
    String topic() default "";
}
