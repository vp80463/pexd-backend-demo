/******************************************************************************/
/* SYSTEM     : Stream                                                        */
/*                                                                            */
/* SUBSYSTEM  : Xm03                                                          */
/******************************************************************************/
package com.a1stream.parts.service;

import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.logic.IfsPrepareMessageHeaderLogic;
import com.a1stream.common.manager.CallNewIfsManager;
import com.a1stream.domain.bo.batch.PartsReturnRequestItemXmlBO;
import com.a1stream.domain.bo.batch.PartsReturnRequestModelBO;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PartsReturnRequestService {

    @Resource
    private CallNewIfsManager callNewIfsManager;
    @Resource
    private IfsPrepareMessageHeaderLogic ifsPrepareMessageHeaderLogic;

    @Value("${ifs.request.url}")
    private String ifsRequestUrl;

    public void exportPartReturnRequstFile(List<PartsReturnRequestModelBO> returnRequsts) {

        //Export to interface file.
        if(returnRequsts.size() > 0) {

            List<PartsReturnRequestItemXmlBO> sendList = new ArrayList<>();
            String ifsCode = InterfCode.OX_SPRETURNREQUEST;
            PartsReturnRequestItemXmlBO sendDataBo = new PartsReturnRequestItemXmlBO();
            sendDataBo.setHeader(ifsPrepareMessageHeaderLogic.setHeaderBo(ifsCode));
            sendDataBo.setOrderItems(returnRequsts);
            sendList.add(sendDataBo);

            String jsonStr = JSON.toJSON(sendList).toString();
            callNewIfsManager.callNewIfsService(ifsRequestUrl, ifsCode, jsonStr);
            log.info("Async Message Send Success...");
        }
    }
}