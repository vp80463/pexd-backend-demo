package com.a1stream.common.ifs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterfProcessResponse{
    //请求体
    private String producerLogId;
    //请求体
    private String consumerLogId;
    //（SUCCESS/FAILER）
    private String code;
    //处理结果
    private String message;

    private String data;
    //处理对象
    private Object body;
    //开始消费时间
    private String startTime;
    //消费结束时间
    private String endTime;
    //接口编码
    private String interfCode;
    //队列名
    private String queueName;
    private String asyncCallbackUrl;
}

