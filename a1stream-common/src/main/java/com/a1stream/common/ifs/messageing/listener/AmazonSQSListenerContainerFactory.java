package com.a1stream.common.ifs.messageing.listener;

import org.springframework.context.annotation.Configuration;

/*
 * Amazon SQA 消息监听容器工厂
 *
 * @param <C> 消息监听容器
 */
@Configuration
public interface AmazonSQSListenerContainerFactory<C extends MessageListenerContainer> {

    C createListenerContainer(String queueName,int waitTimeSeconds, int maxNumberOfMessages, MessageListener messageListener);

}
