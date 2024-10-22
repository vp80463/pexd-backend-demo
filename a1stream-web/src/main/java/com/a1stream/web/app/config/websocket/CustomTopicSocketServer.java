package com.a1stream.web.app.config.websocket;

import lombok.extern.slf4j.Slf4j;

/**
 * 暂时未使用
 *
 * @author dong zhen
 */
//@Component
//@ServerEndpoint(value = "/websocket/custom/{sid}", subprotocols = WebSocketConstants.TOKEN_PROTOCOL)
@Slf4j
public class CustomTopicSocketServer {
//public class CustomTopicSocketServer extends TopicSocketServer {

//    @Setter
//    private static TopicSender topicSender;
//
//    /**
//     * inject default common sender.
//     * allow to inject other sender by override getTopicSender().
//     *
//     * @param topicSender default common sender
//     */
//    public void injectDefaultSender(TopicSender topicSender) {
//        setTopicSender(topicSender);
//    }
//
//    @Override
//    public TopicSender getTopicSender() {
//        return CustomTopicSocketServer.topicSender;
//    }
//
//    public CustomTopicSocketServer() {
//        super();
//        setEndpoint("Custom-topic-websocket");
//    }
}
