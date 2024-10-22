package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.data.domain.Page;

import com.a1stream.domain.bo.parts.SPQ020701BO;
import com.a1stream.domain.form.parts.SPQ020701Form;

public interface SalesOrderCancelHistoryRepositoryCustom {

    public Page<SPQ020701BO> findPartsCancelHisList(SPQ020701Form form, String siteId);

    public List<SPQ020701BO> findPartsCancelHisExport(SPQ020701Form form, String siteId);

}
