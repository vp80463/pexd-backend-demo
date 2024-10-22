package com.a1stream.master.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.master.CMQ050101BasicInfoBO;
import com.a1stream.domain.bo.master.CMQ050101DemandDetailBO;
import com.a1stream.domain.bo.master.CMQ050101InformationBO;
import com.a1stream.domain.bo.master.CMQ050101PurchaseControlBO;
import com.a1stream.domain.bo.master.CMQ050101StockInfoBO;
import com.a1stream.domain.form.master.CMQ050101Form;
import com.a1stream.domain.repository.MstProductRepository;

import jakarta.annotation.Resource;

/**
 * @author liu chaoran
 */
@Service
public class CMQ0501Service {

    @Resource
    private MstProductRepository mstProductRepository;

    public CMQ050101InformationBO findPartsInformationReportList(CMQ050101Form model, String siteId) {

        return mstProductRepository.findPartsInformationReportList(model, siteId);
    }

    public List<CMQ050101BasicInfoBO> findBasicInfoList(CMQ050101Form model, String siteId) {
        return mstProductRepository.findBasicInfoList(model, siteId);
    }

    public List<CMQ050101PurchaseControlBO> findPurchaseControlList(CMQ050101Form model, String siteId) {
        return mstProductRepository.findPurchaseControlList(model, siteId);
    }

    public List<CMQ050101StockInfoBO> findStockInfoList(CMQ050101Form model, String siteId) {
        return mstProductRepository.findStockInfoList(model, siteId);
    }

    public List<CMQ050101DemandDetailBO> findDemandList(CMQ050101Form model, String siteId) {
        return mstProductRepository.findDemandList(model, siteId);
    }
}