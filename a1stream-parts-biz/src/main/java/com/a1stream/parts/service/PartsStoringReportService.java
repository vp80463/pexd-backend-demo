/******************************************************************************/
/* SYSTEM     : Stream                                                        */
/*                                                                            */
/* SUBSYSTEM  : Xm03                                                          */
/******************************************************************************/
package com.a1stream.parts.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.logic.IfsPrepareMessageHeaderLogic;
import com.a1stream.common.manager.CallNewIfsManager;
import com.a1stream.domain.bo.batch.PartsStoringReportItemXmlBO;
import com.a1stream.domain.bo.batch.PartsStoringReportModelBO;
import com.alibaba.fastjson.JSON;
import com.ymsl.solid.base.util.CollectionUtils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PartsStoringReportService {

    @Resource
    private CallNewIfsManager callNewIfsManager;
    @Resource
    private IfsPrepareMessageHeaderLogic ifsPrepareMessageHeaderLogic;

    @Value("${ifs.request.url}")
    private String ifsRequestUrl;

    public void doStoringReport(List<PartsStoringReportModelBO> storingReports) {

        //Export to interface file.
        if(CollectionUtils.isNotEmpty(storingReports)) {

            List<PartsStoringReportItemXmlBO> sendList = new ArrayList<>();
            String ifsCode = InterfCode.OX_SPSTORINGREPORT;
            PartsStoringReportItemXmlBO sendDataBo = new PartsStoringReportItemXmlBO();
            sendDataBo.setHeader(ifsPrepareMessageHeaderLogic.setHeaderBo(ifsCode));
            sendDataBo.setOrderItems(storingReports);
            sendList.add(sendDataBo);

            String jsonStr = JSON.toJSON(sendList).toString();
            callNewIfsManager.callNewIfsService(ifsRequestUrl, ifsCode, jsonStr);
            log.info("Async Message Send Success...");
        }
    }
}