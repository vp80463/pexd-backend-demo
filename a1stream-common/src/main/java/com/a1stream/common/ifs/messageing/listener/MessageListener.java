package com.a1stream.common.ifs.messageing.listener;

import java.lang.reflect.InvocationTargetException;

import com.a1stream.common.constants.MessageContent;

/**
 * 消息监听者
 *
 */
@FunctionalInterface
public interface MessageListener {

    void onMessage(MessageContent message) throws InvocationTargetException, IllegalAccessException;

}
