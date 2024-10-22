package com.a1stream.common.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.manager.CallNewIfsManager;
import com.a1stream.common.manager.ConsumerManager;
import com.a1stream.common.model.BaseInfResponse;
import com.a1stream.common.model.PrivacyPolicyCVBO;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.SystemParameterVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class CodeValueService {

    @Resource
    ConsumerManager consumerMgr;

    @Resource
    SystemParameterRepository systemParameterRepository;

    @Value("${ifs.request.url}")
    private String ifsRequestUrl;

    @Resource
    private CallNewIfsManager callNewIfsManager;

    public PrivacyPolicyCVBO getPrivacyPolicyByNameAndPhone(String siteId, String lastNm, String middleNm, String firstNm, String mobilePhone) {

        return new PrivacyPolicyCVBO(consumerMgr.getConsumerPolicyInfo(siteId, lastNm, middleNm, firstNm, mobilePhone));
    }

    public SystemParameterVO getSystemParameter(String paramCode) {

        return BeanMapUtils.mapTo(systemParameterRepository.findBySystemParameterTypeId(paramCode), SystemParameterVO.class);
    }

    public String getYmvnStock(String[][] ps) {

        LinkedHashMap<String, Object> map = Arrays.stream(ps).collect(LinkedHashMap::new, (m, v) -> m.put(v[0], v[1]), HashMap::putAll);
        String ifsCode = InterfCode.DMSTOSP_PARTSSTOCK_INQ;
        BaseInfResponse callNewIfsService = callNewIfsManager.callGetMethodNewIfsService(ifsRequestUrl, ifsCode, map);
        return callNewIfsService.getData();
    }
}
