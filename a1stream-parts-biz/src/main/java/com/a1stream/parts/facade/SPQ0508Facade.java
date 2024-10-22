package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Component;

import com.a1stream.domain.bo.parts.SPQ050801BO;
import com.a1stream.domain.bo.parts.SPQ050802BO;
import com.a1stream.domain.form.parts.SPQ050801Form;
import com.a1stream.domain.form.parts.SPQ050802Form;
import com.a1stream.parts.service.SPQ0508Service;

import jakarta.annotation.Resource;

/**
* 功能描述:Parts MI Account Report(Stock)
*
* @author mid2178
*/
@Component
public class SPQ0508Facade {

    @Resource
    private SPQ0508Service spq0508Service;

    public List<SPQ050802BO> doPageInitial(SPQ050802Form form, String siteId) {

        return spq0508Service.getAccountReportData(form, siteId);
    }

    public List<SPQ050801BO> findStockAccountList(SPQ050801Form model) {

        List<SPQ050801BO> stockAccountList = spq0508Service.retrieveStockAccountList(model);

        for(SPQ050801BO spq050801bo:stockAccountList) {

            BigDecimal salesCost = spq050801bo.getSalesCostAmt();
            BigDecimal returnCost = spq050801bo.getReturnCostAmt();
            BigDecimal month = new BigDecimal(stockAccountList.size());

            spq050801bo.setStockMonth((salesCost.subtract(returnCost)).divide(month,2,RoundingMode.HALF_UP));
        }

        return stockAccountList;
    }
}
