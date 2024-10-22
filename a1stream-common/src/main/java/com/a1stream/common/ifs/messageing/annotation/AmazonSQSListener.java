package com.a1stream.common.ifs.messageing.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Amazon SQS Listener 标记类
 *
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AmazonSQSListener {

    /**
     * 队列名称
     * @return
     */
    String queueName() default "";

    /**
     * Amazon SQS 并发消费者(值必须大于 1 且小于 CPU 核心的 2 倍)
     * @return
     */
    int consumers() default 0;

    /**
     * 请求轮询时长(默认20s长轮询)
     * @return
     */
    int waitTimeSeconds() default 20;

    /**
     * 一次性最大拉取数(最大为10)
     * @return
     */
    int maxNumberOfMessages() default 1;

}