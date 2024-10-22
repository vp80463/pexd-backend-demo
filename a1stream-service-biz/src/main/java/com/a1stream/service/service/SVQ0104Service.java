package com.a1stream.service.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.service.SVQ010401BO;
import com.a1stream.domain.form.service.SVQ010401Form;
import com.a1stream.domain.repository.ServiceOrderRepository;

import jakarta.annotation.Resource;

/**
* 功能描述:Service Order明细画面
*
* @author mid1341
*/
@Service
public class SVQ0104Service {

    @Resource
    private ServiceOrderRepository serviceOrderRepo;

    public Page<SVQ010401BO> page0KmServiceOrder(SVQ010401Form model, String siteId) {
    	
        return serviceOrderRepo.page0KmServiceOrder(model, siteId);
    }
}
