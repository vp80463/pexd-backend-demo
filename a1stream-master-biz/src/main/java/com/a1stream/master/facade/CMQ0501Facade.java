package com.a1stream.master.facade;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.a1stream.domain.bo.master.CMQ050101BO;
import com.a1stream.domain.bo.master.CMQ050101BasicInfoBO;
import com.a1stream.domain.bo.master.CMQ050101DemandDetailBO;
import com.a1stream.domain.bo.master.CMQ050101InformationBO;
import com.a1stream.domain.bo.master.CMQ050101PurchaseControlBO;
import com.a1stream.domain.bo.master.CMQ050101StockInfoBO;
import com.a1stream.domain.form.master.CMQ050101Form;
import com.a1stream.master.service.CMQ0501Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
 * @author liu chaoran
 */
@Component
public class CMQ0501Facade {

    @Resource
    private CMQ0501Service cmq0501Service;

    public CMQ050101BO findPartsInformationReportList(CMQ050101Form model, String siteId) {

        this.check(model);

        CMQ050101BO cmq050101BO = new CMQ050101BO();
        CMQ050101InformationBO informationBO = cmq0501Service.findPartsInformationReportList(model, siteId);
        List<CMQ050101BasicInfoBO> basicInfoList = cmq0501Service.findBasicInfoList(model, siteId);
        List<CMQ050101PurchaseControlBO> purchaseControlList = cmq0501Service.findPurchaseControlList(model, siteId);
        List<CMQ050101StockInfoBO> stockInfoList = cmq0501Service.findStockInfoList(model, siteId);
        List<CMQ050101DemandDetailBO> demandDetailList = cmq0501Service.findDemandList(model, siteId);
        cmq050101BO.setInformation(informationBO);
        cmq050101BO.setBasicInfoList(basicInfoList);
        cmq050101BO.setPurchaseControlList(purchaseControlList);
        cmq050101BO.setStockInfoList(stockInfoList);
        cmq050101BO.setDemandDetailList(demandDetailList);
        return cmq050101BO;
    }
    private void check(CMQ050101Form form) {

        //partsId不存在时，报错
        if (StringUtils.isNotBlank(form.getParts()) && Nulls.isNull(form.getPartsId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.point"),
                                             form.getParts(),
                                             CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }
    }
}

