package com.a1stream.parts.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.manager.CallNewIfsManager;
import com.a1stream.common.model.BaseInfResponse;
import com.a1stream.domain.bo.batch.PartsReceiveReportModelBO;
import com.alibaba.fastjson.JSON;
import com.ymsl.solid.base.util.CollectionUtils;

import jakarta.annotation.Resource;

@Service
public class PartsReceiveReportService {

    @Resource
    private CallNewIfsManager callNewIfsManager;

    @Value("${ifs.request.url}")
    private String ifsRequestUrl;

    public BaseInfResponse doReceiveReport(List<PartsReceiveReportModelBO> receiveReports) {

        BaseInfResponse response = new BaseInfResponse();

        if(CollectionUtils.isNotEmpty(receiveReports)) {
            response = callNewIfsManager.callNewIfsService(ifsRequestUrl, InterfCode.DMSTOSP_RECEIVEREPORT_INQ, JSON.toJSON(receiveReports).toString());
        }

        return response;
    }
}