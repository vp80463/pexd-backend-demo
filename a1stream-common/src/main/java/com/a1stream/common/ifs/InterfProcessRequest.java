package com.a1stream.common.ifs;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterfProcessRequest{

    //接口信息
    private String interfName;
    private String fromSystemId;
    private String fromSystemName;

    // 生成者请求体
    private String interfCode;
    private String requestMethod;
    private Map<String, String> headerMap;
    private Map<String, Object> queryMap;
    private Object body;

    // body数组还是单笔
    private String bodyType;

    // 接口配置参数
    private String exchangeId;
    private String pushType;
    private Integer timeOut;
    private Map<String,String> queueAppMap;

    private Integer retryNum;
    private Integer retryInterval;

    private String requestTime;
    private String pushTime;

    private String producerLogId;
    private String consumerLogId;
    private String startTime;

    //错误信息
    private JSONObject result;

    private List<InterfProcessDetailsRequest> details;

    //消费参数
    private String queueName;
    private String asyncCallbackUrl;

    //20230612 新增
    private String throwMsg;

    //20230320 新增
    private InterfProcessDetailsRequest toAppDetail;
    private Boolean retryFlag = false;

    //20230320 lyx 废弃
    private String splitFlag;
    private List<String> fanoutBindQueues;
    //异步队列指定推送IP
    private String consumerIp;
}
