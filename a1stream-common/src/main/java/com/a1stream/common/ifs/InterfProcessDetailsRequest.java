package com.a1stream.common.ifs;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class InterfProcessDetailsRequest  {

    private String toSystemId;
    private String toSystemName;
    private String toAppId;
    private String toAppName;

    private String pushType;
    private String syncApiUrl;
    private String syncResponseResultMapping;

    private String syncInterfMethod;
    private String syncQueryParams;
    private String syncHeaderParams;
    private String syncContentType;

    private Integer asyncPriority;
    private Integer asyncCallbackUrl;
    //寻址标识
    private String addressingIdentifierKey;
    //监听/调用
    private String consumeMethod;
    //请求参数过滤
    private String requestFilterParams;
    //返回body过滤
    private String responseFilterParams;
    //请求参数映射
    private String requestMappingParams;
    //返回参数映射
    private String responseMappingParams;
    //应用队列
    private List<String> appMethodQueueList;
    //寻址还是广播
    private String dataDistributionType;

    //推送队列和交换机
    private String routeKey;
    private String exchangeId;

    private String consumerLogId;

    //适配器
    private Long connectorId;
    private String connectorSetting;
    private String connectorType;

    private Object body;
    private String syncContentPath;

    //20230525 lyx ptc废弃
    private String syncSecurityType;
    private String syncTokenId;
    private String syncTokenPosition;
    private String syncTokenKey;

    //20230320 lyx 废弃
    private List<String> toAppList;
    private Map<String,InterfProcessRouteKeyModel> queueBodyMap;
    private Map<String,String> queueAppMap;
    private String addressingFlag;
    private String contentPath;
}

