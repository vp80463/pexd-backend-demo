package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.bo.parts.SPQ050801BO;
import com.a1stream.domain.bo.parts.SPQ050802BO;
import com.a1stream.domain.form.parts.SPQ050801Form;

@Repository
public interface SpStockAccountRepositoryCustom {

    List<SPQ050802BO> getAccountReportData(String siteId, String targetMonth, String pointCd, String largeGroupCd);

    /**
     * author: Tang Tiantian
     */
    List<SPQ050801BO> findStockAccountList(SPQ050801Form model);
}
