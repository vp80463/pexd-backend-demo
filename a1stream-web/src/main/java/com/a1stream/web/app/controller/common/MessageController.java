package com.a1stream.web.app.controller.common;

import com.a1stream.common.manager.MessageSendManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试websocket接口
 *
 * @author dong zhen
 */
@Slf4j
@RestController
@RequestMapping("public/websocket")
public class MessageController {

//    @Resource
//    private MyWebSocketHandler webSocketHandler;

//    @Resource
//    private TopicSender topicSender;

    @Resource
    private MessageSendManager messageSendManager;

    @PostMapping("/sendMessage.json")
    public void sendMessage() {
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            String userId = "DZ";
//            List<HomePageMessageBO> messageList = new ArrayList<>();
//            HomePageMessageBO messageBO = getHomePageMessageBO();
//            messageList.add(messageBO);

            //将messageList转成String格式
//            String message = mapper.writeValueAsString(messageList);

//            webSocketHandler.sendMessageToUser(userId, message);
//            topicSender.sendMessage(userId, new MessageDTO(message));
//        } catch (IOException e) {
//
//            log.error("发送失败 {1}", e);
//        }
        String siteId = "DZ";
        messageSendManager.sendMessage(siteId);
    }

//    private static HomePageMessageBO getHomePageMessageBO() {
//        HomePageMessageBO messageBO = new HomePageMessageBO();
//        messageBO.setKey("Message");
//        messageBO.setName("Message");
//        List<HomePageMessageItemBO> homePageMessageItemList =  new ArrayList<>();
//        HomePageMessageItemBO homePageMessageItemBO = new HomePageMessageItemBO();
//        homePageMessageItemBO.setTitle("You have received 12 new weekly reports");
//        homePageMessageItemBO.setDatetime("2024/06/19 14:53");
//        homePageMessageItemList.add(homePageMessageItemBO);
//        messageBO.setList(homePageMessageItemList);
//        return messageBO;
//    }
}
