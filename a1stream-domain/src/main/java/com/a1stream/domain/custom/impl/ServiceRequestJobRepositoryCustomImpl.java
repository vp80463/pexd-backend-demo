package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.bo.service.SVM020102BO;
import com.a1stream.domain.custom.ServiceRequestJobRepositoryCustom;
import com.a1stream.domain.form.service.SVM020102Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ServiceRequestJobRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ServiceRequestJobRepositoryCustom {

    @Override
    public List<SVM020102BO> getRepairJobDetailList(SVM020102Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT (mp.product_cd || ' ' || mp.local_description)  AS  job                  ");
        sql.append("          , (CASE WHEN srj.select_flag = 'Y'                                         ");
        sql.append("                  THEN true ELSE false END)                 AS  jobSelectFlag        ");
        sql.append("          , srj.service_request_job_id                      AS  serviceRequestJobId  ");
        sql.append("       FROM service_request_job srj                                                  ");
        sql.append("  LEFT JOIN mst_product mp                                                           ");
        sql.append("         ON mp.product_id = srj.job_id                                               ");
        sql.append("      WHERE srj.service_request_id = :serviceRequestId                               ");

        params.put("serviceRequestId", form.getServiceRequestId());

        return super.queryForList(sql.toString(), params, SVM020102BO.class);
    }
}