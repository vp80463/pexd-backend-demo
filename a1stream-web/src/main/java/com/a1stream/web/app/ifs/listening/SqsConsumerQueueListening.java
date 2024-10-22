package com.a1stream.web.app.ifs.listening;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MessageContent;
import com.a1stream.common.constants.SqsQueueConstants;
import com.a1stream.common.ifs.InterfProcessRequest;
import com.a1stream.common.ifs.InterfProcessResponse;
import com.a1stream.common.ifs.InterfProcessor;
import com.a1stream.common.ifs.messageing.annotation.AmazonSQSListener;
import com.a1stream.web.app.ifs.manager.InterfManager;
import com.a1stream.web.app.ifs.service.SqsConsumerSendService;
import com.alibaba.fastjson.JSON;
import com.ymsl.solid.base.config.ApplicationContextHolder;
import com.ymsl.solid.base.util.DateUtils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SqsConsumerQueueListening {

    @Resource
    private InterfManager interfManager;

    @Resource
    private SqsConsumerSendService sqsConsumerSendService;


    @SuppressWarnings({ "rawtypes", "unchecked" })
    @AmazonSQSListener(queueName = SqsQueueConstants.Queue.YMVN_DMS_WEBMC1_QUEUE)
    public void onMessage(MessageContent  messageContent){

        String message = messageContent.getMessages().get(0).body();
        try{
            InterfProcessRequest request = JSON.parseObject(message,InterfProcessRequest.class);
            //保存队列名
            request.setQueueName(messageContent.getQueueName());

            String interfCode = request.getInterfCode();
            request.setStartTime(DateUtils.getCurrentDateString(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS));
            InterfProcessResponse response = new InterfProcessResponse();
            try{
                if(interfManager.findByCode(interfCode) == null) {
                    sqsConsumerSendService.sendErrorForProducer(request,"consumer listen not exist interfcode: " + interfCode);
                }else{
                    InterfProcessor process = ApplicationContextHolder.get(interfManager.findByCode(interfCode).getClass());
                    response = process.execute(request);
                    if (response == null) {
                        sqsConsumerSendService.sendErrorForProducer(request,"consumer response layout error, response: " + response);
                    } else {
                        sqsConsumerSendService.sendToProducer(response, request);
                    }
                }
            }catch(Exception e){
                log.error("ConsumerDirectReceiver, interfCode:" + interfCode, e);
                sqsConsumerSendService.sendErrorForProducer(request, e.toString());
            }
        }catch(Exception e){
            log.error("ConsumerDirectReceiver:request params error", e.getMessage());
        }
    }
}
