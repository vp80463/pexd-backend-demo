package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.bo.service.SVM020102BO;
import com.a1stream.domain.custom.ServiceRequestEditHistoryRepositoryCustom;
import com.a1stream.domain.form.service.SVM020102Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ServiceRequestEditHistoryRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ServiceRequestEditHistoryRepositoryCustom {

    @Override
    public List<SVM020102BO> getProcessHistoryList(SVM020102Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT sreh.change_date                                   AS  date    ");
        sql.append("          , sreh.request_status                                AS  status  ");
        sql.append("          , (sreh.report_pic_cd || ' ' || sreh.report_pic_nm)  AS  pic     ");
        sql.append("       FROM service_request_edit_history sreh                              ");
        sql.append("      WHERE sreh.service_request_id = :serviceRequestId                    ");

        params.put("serviceRequestId", form.getServiceRequestId());

        return super.queryForList(sql.toString(), params, SVM020102BO.class);
    }
}