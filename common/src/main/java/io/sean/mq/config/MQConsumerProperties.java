/**
 * io.sean
 * Copyright(c) 2012-2018 All Rights Reserved.
 */
package io.sean.mq.config;

import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author sean
 * @version Id:,v0.1 2018/6/6 下午4:44 sean Exp $
 * @description
 */
@Data
@ConfigurationProperties("mq.consumer")
public class MQConsumerProperties {

    List<MQConsumerConfig> list = Lists.newArrayList();
}
