package com.a1stream.parts.service;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.parts.SPQ020101BO;
import com.a1stream.domain.form.parts.SPQ020101Form;
import com.a1stream.domain.repository.SalesOrderRepository;

import jakarta.annotation.Resource;

/**
*
* 功能描述:Sales Order Inquiry (By Customer)
*
* mid2330
* 2024年6月24日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/24   Liu Chaoran   New
*/
@Service
public class SPQ0201Service {

    @Resource
    private SalesOrderRepository salesOrderRepository;

    public Page<SPQ020101BO> findSalesOrderCustomerList(SPQ020101Form form, String siteId) {

        return salesOrderRepository.findSalesOrderCustomerList(form, siteId);
    }

    public List<SPQ020101BO> findSalesOrderCustomerExportList(SPQ020101Form form, String siteId) {

        return salesOrderRepository.findSalesOrderCustomerExportList(form, siteId);
    }

}
