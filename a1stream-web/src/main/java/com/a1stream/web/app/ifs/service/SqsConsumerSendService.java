package com.a1stream.web.app.ifs.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.SqsQueueConstants;
import com.a1stream.common.ifs.InterfProcessRequest;
import com.a1stream.common.ifs.InterfProcessResponse;
import com.a1stream.common.utils.SqsMessageUtil;
import com.alibaba.fastjson.JSON;

import jakarta.annotation.Resource;


@Component
public class SqsConsumerSendService {

    @Resource
    private SqsMessageUtil sqsMessageUtil;

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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String endTime = sdf.format(new Date());
        res.setEndTime(endTime);
        res.setAsyncCallbackUrl(request.getAsyncCallbackUrl());
        String response = JSON.toJSONString(res);
        sqsMessageUtil.sendMessage(SqsQueueConstants.Queue.Q_IFS_CONSUMER_RESULT_CATCH, response);
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String endTime = sdf.format(new Date());
        res.setEndTime(endTime);
        res.setAsyncCallbackUrl(request.getAsyncCallbackUrl());
        String response = JSON.toJSONString(res);
        sqsMessageUtil.sendMessage(SqsQueueConstants.Queue.Q_IFS_CONSUMER_RESULT_CATCH, response);
    }
}
