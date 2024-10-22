package com.a1stream.common.manager;

import com.a1stream.common.bo.YnspReceivingBO;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IFSServiceManager {

    @Resource
    private CallNewIfsManager callNewIfsManager;

    public void sendReceivingList(List<YnspReceivingBO> receivingList,String interfCd) {

        String jonsStr = JSON.toJSONString(receivingList);

        callNewIfsManager.callNewIfsService( "newIfsUrl", "interfCode",jonsStr);

        System.out.println(" ");
    }

//    public void exportStoringReportFile(List<YnspReceivingBO> receivingList,String interfCd) {
//
//        String jonsStr = JSON.toJSONString(receivingList);
//
//        callNewIfsManager.callNewIfsService( "newIfsUrl", "interfCode",jonsStr);
//
//        System.out.println(" ");
//    }
}