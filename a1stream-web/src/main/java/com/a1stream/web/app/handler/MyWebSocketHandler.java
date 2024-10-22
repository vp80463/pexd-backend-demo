package com.a1stream.web.app.handler;

import lombok.extern.slf4j.Slf4j;

/**
 * 暂时未使用
 *
 * WebSocket handler that manages sessions per user
 * @author dong zhen
 */
@Slf4j
//@Component
public class MyWebSocketHandler {
//public class MyWebSocketHandler extends TextWebSocketHandler {

//    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) {
//        String path = session.getUri().getPath();
//        log.info("WebSocket连接路径: {}", path);
//
//        String userId = extractUserIdFromSession(session);
//        if (userId != null) {
//            sessions.put(userId, session);
//            log.info("为用户 {} 建立WebSocket连接: {}", userId, session.getId());
//        } else {
//            log.warn("无法从WebSocket连接中提取用户ID: {}", session);
//        }
//    }
//
//    @Override
//    public void handleTextMessage(WebSocketSession session, TextMessage message) {
//        // Implement your logic to handle incoming messages here.
//        log.info("收到来自的消息 {}: {}", session.getId(), message.getPayload());
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
//        String userId = extractUserIdFromSession(session);
//        if (userId != null) {
//            sessions.remove(userId);
//            log.info("用户 {} 的WebSocket连接已关闭: {}, 关闭状态: {}", userId, session.getId(), status);
//        }
//    }
//
//    public void sendMessageToUser(String userId, String message) throws IOException {
//        WebSocketSession session = sessions.get(userId);
//        if (session != null) {
//            log.info("找到用户 {} 的会话: {}", userId, session.getId());
//        } else {
//            log.warn("未找到用户 {} 的会话", userId);
//        }
//
//        if (session != null && session.isOpen()) {
//            session.sendMessage(new TextMessage(message));
//            log.info("已将消息发送到 {}: {}", userId, message);
//        } else {
//            log.warn("向用户 {} 发送消息失败: 会话未找到或已关闭", userId);
//        }
//    }
//
//    private String extractUserIdFromSession(WebSocketSession session) {
//        String path = session.getUri().getPath();
//        UriTemplate template = new UriTemplate("/websocket/{userId}");
//        Map<String, String> variables = template.match(path);
//        String userId = variables.get("userId");
//        log.info("从路径 {} 中提取到的用户ID: {}", path, userId);
//        return userId;
//    }
}
