package com.a1stream.web.app.handler;

/**
 * 暂时未使用
 *
 * @author dong zhen
 */
//public class CustomHandshakeInterceptor implements HandshakeInterceptor {
public class CustomHandshakeInterceptor {

//    @Override
//    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
//                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
//        // 在这里可以进行握手前的逻辑处理，例如验证请求头、设置属性等
//        // 你可以访问request和response对象，以及WebSocketHandler和attributes
//        // 例如，从请求中获取特定的参数并将其存储在attributes中供WebSocketHandler使用
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap((Message<?>) request);
//        String userId = accessor.getFirstNativeHeader("userId");
//        if (userId != null) {
//            attributes.put("userId", userId);
//        } else {
//            // 如果没有找到userId，可以选择终止握手
//            return false;
//        }
//
//        // 返回true继续握手，返回false终止握手
//        return true;
//    }
//
//    @Override
//    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
//                               WebSocketHandler wsHandler, Exception exception) {
//        // 在这里可以进行握手后的逻辑处理，例如记录日志、清理资源等
//        // 无论握手成功还是失败，这个方法都会被调用
//        if (exception != null) {
//            // 如果握手过程中发生异常，可以在这里处理
//            System.out.println("Handshake failed with exception: " + exception.getMessage());
//        } else {
//            // 握手成功时的处理
//            System.out.println("Handshake completed successfully.");
//        }
//    }
}
