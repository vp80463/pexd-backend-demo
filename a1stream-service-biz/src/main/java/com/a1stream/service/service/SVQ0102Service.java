package com.a1stream.service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import com.a1stream.domain.bo.service.SVQ010201BO;
import com.a1stream.domain.bo.service.SVQ010201ExportBO;
import com.a1stream.domain.form.service.SVQ010201Form;
import com.a1stream.domain.repository.ServiceOrderRepository;

import jakarta.annotation.Resource;

/**
* 功能描述:Service Order明细画面
*
* @author mid1341
*/
@Service
public class SVQ0102Service {

    @Resource
    private ServiceOrderRepository serviceOrderRepo;

    public Page<SVQ010201BO> pageServiceOrder(SVQ010201Form model, String siteId) {
    	
        return serviceOrderRepo.pageServiceOrder(model, siteId);
    }

    public List<SVQ010201ExportBO> listServiceOrder(SVQ010201Form model, String siteId) {
    	
        return serviceOrderRepo.listServiceOrder(model, siteId);
    }
}
