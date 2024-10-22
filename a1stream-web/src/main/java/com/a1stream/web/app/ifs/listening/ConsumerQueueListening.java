package com.a1stream.web.app.ifs.listening;


import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.ifs.InterfProcessRequest;
import com.a1stream.common.ifs.InterfProcessResponse;
import com.a1stream.common.ifs.InterfProcessor;
import com.a1stream.web.app.ifs.manager.InterfManager;
import com.a1stream.web.app.ifs.service.ConsumerSendService;
import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.ymsl.solid.base.config.ApplicationContextHolder;
import com.ymsl.solid.base.util.DateUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile(value = "rabbitMq")
public class ConsumerQueueListening {

    @Resource
    private InterfManager interfManager;

    @Resource
    private ConsumerSendService consumerSendService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @RabbitListener(queues = "#{a1streamQueue.name}", concurrency = "1")
    @RabbitHandler
    public void process(Message msg, String message, Channel channel) {
        try{
            InterfProcessRequest request = JSON.parseObject(message,InterfProcessRequest.class);
            //保存队列名
            request.setQueueName(msg.getMessageProperties().getConsumerQueue());

            String interfCode = request.getInterfCode();
            request.setStartTime(DateUtils.getCurrentDateString(CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS));
            InterfProcessResponse response = new InterfProcessResponse();
            try{
                if(interfManager.findByCode(interfCode) == null) {
                    consumerSendService.sendErrorForProducer(request,"consumer listen not exist interfcode: " + interfCode);
                }else{
                    InterfProcessor process = ApplicationContextHolder.get(interfManager.findByCode(interfCode).getClass());
                    response = process.execute(request);
                    if (response == null) {
                        consumerSendService.sendErrorForProducer(request,"consumer response layout error, response: " + response);
                    } else {
                        consumerSendService.sendToProducer(response, request);
                    }
                }
            }catch(Exception e){
                log.error("ConsumerDirectReceiver, interfCode:" + interfCode, e);
                consumerSendService.sendErrorForProducer(request, e.toString());
            }
        }catch(Exception e){
            log.error("ConsumerDirectReceiver:request params error", e.getMessage());
            e.printStackTrace();
        }
    }
}
