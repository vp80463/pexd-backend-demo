package com.a1stream.unit.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.unit.SDM030101BO;
import com.a1stream.domain.bo.unit.SDM030103BO;
import com.a1stream.domain.bo.unit.SDM030103DetailBO;
import com.a1stream.domain.form.unit.SDM030101Form;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;

import jakarta.annotation.Resource;

/**
* 功能描述:Sales Order List sdm0301_01明细画面
*
* mid2330
* 2024年8月19日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/19   Liu Chaoran     New
*/
@Service
public class SDM0301Service {

    @Resource
    private SalesOrderRepository soRepo;

    @Resource
    private SalesOrderItemRepository soItemRepo;

    public List<SDM030101BO> findSalesOrderData(SDM030101Form form, String siteId) {

        return soRepo.findSalesOrderData(form, siteId);
    }

    //查询条件部内容
    public SDM030103BO getDealerWholeSOBasicInfo(Long orderId) {

        return soRepo.getDealerWholeSOBasicInfo(orderId);
    }

    public List<SDM030103DetailBO> getDealerWholeSODetails(Long orderId) {

        return soItemRepo.getDealerWholeSODetails(orderId);
    }
}
