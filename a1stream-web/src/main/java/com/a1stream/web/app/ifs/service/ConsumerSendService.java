package com.a1stream.web.app.ifs.service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.ifs.InterfProcessRequest;
import com.a1stream.common.ifs.InterfProcessResponse;
import com.a1stream.common.ifs.RabbitMqConstants;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;


@Component
public class ConsumerSendService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 推送消息返回生成者
     * @param res
     */
    public void sendToProducer(InterfProcessResponse res,InterfProcessRequest request){

        res.setQueueName(request.getQueueName());
        res.setInterfCode(request.getInterfCode());
        res.setBody(request.getBody());
        res.setProducerLogId(request.getProducerLogId());
        res.setConsumerLogId(request.getConsumerLogId());
        res.setStartTime(request.getStartTime());
        SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_DETAIL_FORMAT);
        String endTime = sdf.format(new Date());
        res.setEndTime(endTime);
        res.setAsyncCallbackUrl(request.getAsyncCallbackUrl());
        String response = JSON.toJSONString(res);
        rabbitTemplate.convertAndSend(RabbitMqConstants.Exchange.DIRECT_EXCHANGE, RabbitMqConstants.Queue.Q_IFS_CONSUMER_RESULT_CATCH, response);
    }

    public void sendErrorForProducer(InterfProcessRequest request,String error) {
        InterfProcessResponse res = new InterfProcessResponse();

        res.setQueueName(request.getQueueName());
        res.setCode("FAILURE"); //TODO
        res.setMessage(error);
        res.setInterfCode(request.getInterfCode());
        res.setBody(request.getBody());
        res.setProducerLogId(request.getProducerLogId());
        res.setConsumerLogId(request.getConsumerLogId());
        res.setStartTime(request.getStartTime());
        SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_DETAIL_FORMAT);
        String endTime = sdf.format(new Date());
        res.setEndTime(endTime);
        res.setAsyncCallbackUrl(request.getAsyncCallbackUrl());
        String response = JSON.toJSONString(res);
        rabbitTemplate.convertAndSend(RabbitMqConstants.Exchange.DIRECT_EXCHANGE, RabbitMqConstants.Queue.Q_IFS_CONSUMER_RESULT_CATCH, response);
    }
}