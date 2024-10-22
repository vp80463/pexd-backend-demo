package com.a1stream.web.app.config.websocket;

import lombok.extern.slf4j.Slf4j;

/**
 * 暂时未使用
 *
 * @author dong zhen
 */
//@ServerEndpoint(value = "/websocket/custom/{sid}", subprotocols = WebSocketConstants.TOKEN_PROTOCOL)
@Slf4j
public class CustomSocketServer {
//public class CustomSocketServer extends BaseSocketServer {

//    @Getter
//    private String sid = "";
//
//    /**
//     * Whether broadcast message to all sockets in topic when receiving messages.
//     */
//    @Setter
//    @Getter
//    private boolean broadcast = true;
//
//    @Override
//    @OnOpen
//    public void onOpen(Session session, @PathParam("sid") String sid) {
//        this.sid = sid;
//        log.info("开开开开开开开开开开开开开开开开开开开开开开开开 {}", sid);
//        super.onOpen(session, sid);
//    }
//
//    /**
//     * When receive message from client, send it to topic.
//     * @param message message body
//     * @param session socket session
//     */
//    @Override
//    @OnMessage
//    public void onMessage(String message, Session session) {
//        log.info("Message in from {} : {}", getSid(), message);
//    }
//
//    /**
//     * Match the socket that should send messages to client.
//     * @param topic target session id(maybe a pattern)
//     * @param item item session
//     * @return true if this socket has specific topic
//     */
//    @Override
//    public boolean match(String topic, SocketServer item) {
//        return ((TopicSocketServer)item).getTopic().equals(topic);
//    }
}

