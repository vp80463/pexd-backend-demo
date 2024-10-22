package com.a1stream.parts.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.parts.SPQ050801BO;
import com.a1stream.domain.bo.parts.SPQ050802BO;
import com.a1stream.domain.custom.SpStockAccountRepositoryCustom;
import com.a1stream.domain.form.parts.SPQ050801Form;
import com.a1stream.domain.form.parts.SPQ050802Form;
import com.ymsl.solid.jpa.tenant.annotation.MultiTenant;

import jakarta.annotation.Resource;

/**
* 功能描述:Parts MI Account Report(Stock)
*
* @author mid2178
*/
@Service
@MultiTenant("a1stream-mi-db")
public class SPQ0508Service {

    @Resource
    private SpStockAccountRepositoryCustom spStockAccountRepositoryCustom;

    public List<SPQ050802BO> getAccountReportData(SPQ050802Form form, String siteId) {

        return spStockAccountRepositoryCustom.getAccountReportData(siteId, form.getTargetMonth(), form.getPointCd(), form.getLargeGroupCd());
    }

    public List<SPQ050801BO> retrieveStockAccountList(SPQ050801Form model) {

        return spStockAccountRepositoryCustom.findStockAccountList(model);
    }
}
