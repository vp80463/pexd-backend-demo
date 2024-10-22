package com.a1stream.common.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.SqsQueueConstants;

import jakarta.annotation.Resource;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import software.amazon.awssdk.utils.StringUtils;

@Component
public class SqsMessageUtil {

    private Map<String, String> queueUrlMap = new HashMap<>();

    @Resource
    private SqsClient sqsClient;

    // 发送消息
    public SendMessageResponse sendMessage(String queueName, String message) {

        if(StringUtils.isBlank(queueName)){
            return null;
        }
        String queueUrl = "";
        //判断是否存在url
        if(!queueUrlMap.containsKey(queueName)){
            ListQueuesResponse res = sqsClient.listQueues();
            List<String> queueUrlList = res.queueUrls();
            for(String url:queueUrlList){
                int lastIndex = url.lastIndexOf('/');
                String name = url.substring(lastIndex + 1);
                if(StringUtils.equals(queueName, name)){
                    queueUrl = url;
                }
                queueUrlMap.put(name, url);
            }
        }else{
            queueUrl = queueUrlMap.get(queueName);
        }
        if(StringUtils.isEmpty(queueUrl)){
            return null;
        }

        String uuid = UUID.randomUUID().toString();
        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .messageGroupId(uuid)
                .messageDeduplicationId(uuid)
                .build();

        return sqsClient.sendMessage(sendMsgRequest);
    }

    // 创建队列
    public String createSqsQueue(String queueName) {
        String queueUrl = "";
        if(StringUtils.isBlank(queueName)){
            return null;
        }
        //判断是否存在url
        ListQueuesResponse res = sqsClient.listQueues();
        List<String> queueUrlList = res.queueUrls();
        for(String url:queueUrlList){
            int lastIndex = url.lastIndexOf('/');
            String name = url.substring(lastIndex + 1);
            if(StringUtils.equals(queueName, name)){
                queueUrl = url;
            }
        }
        if(StringUtils.isBlank(queueUrl)){
            Map<String, String> attributes = new HashMap<>();
            attributes.put(QueueAttributeName.VISIBILITY_TIMEOUT.toString(), "720");
            if(queueName.endsWith(".fifo")){
                attributes.put(QueueAttributeName.FIFO_QUEUE.toString(), "true");
            }
            if(StringUtils.equals(queueName, SqsQueueConstants.Queue.Q_IFS_CONSUMER_DEAD_COOL_QUEUE)){
                //冷却消息
                attributes.put(QueueAttributeName.DELAY_SECONDS.toString(), "300");
            }

            CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                                                         .queueName(queueName)
                                                         .attributesWithStrings(attributes)
                                                         .build();
            // 创建队列
            CreateQueueResponse createRes = sqsClient.createQueue(createQueueRequest);
            queueUrl = createRes.queueUrl();
        }

        return queueUrl;
    }

}
