package com.a1stream.parts.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.parts.SPQ040101BO;
import com.a1stream.domain.form.parts.SPQ040101Form;
import com.a1stream.domain.repository.PurchaseOrderRepository;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@Service
public class SPQ0401Service{
    @Resource
    private PurchaseOrderRepository purchaseOrderRepository;

    public Page<SPQ040101BO> searchPurchaseOrderList(SPQ040101Form model, String siteId) {

        return purchaseOrderRepository.getPurchaseOrderList(model, siteId);
    }

}
