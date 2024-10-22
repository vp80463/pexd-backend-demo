package com.a1stream.common.ifs.messageing.listener;

import org.springframework.context.Lifecycle;

/**
 * 消息监听容器
 *
 */
public interface MessageListenerContainer extends Lifecycle {

    void setupMessageListener(MessageListener messageListener);

}
