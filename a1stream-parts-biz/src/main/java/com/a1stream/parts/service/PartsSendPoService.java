package com.a1stream.parts.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.manager.CallNewIfsManager;
import com.a1stream.common.model.BaseInfResponse;
import com.a1stream.domain.bo.batch.PartsSendPoModelBO;
import com.alibaba.fastjson.JSON;
import com.ymsl.solid.base.util.CollectionUtils;

import jakarta.annotation.Resource;

@Service
public class PartsSendPoService {

    @Resource
    private CallNewIfsManager callNewIfsManager;

    @Value("${ifs.request.url}")
    private String ifsRequestUrl;

    public BaseInfResponse doSendPo(List<PartsSendPoModelBO> sendPos) {

        BaseInfResponse response = new BaseInfResponse();

        if(CollectionUtils.isNotEmpty(sendPos)) {
            response = callNewIfsManager.callNewIfsService(ifsRequestUrl, InterfCode.DMSTOSP_SENDPO_INQ, JSON.toJSON(sendPos).toString());
        }

        return response;
    }
}