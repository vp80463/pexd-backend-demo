package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.bo.service.SVM020102BO;
import com.a1stream.domain.custom.ServiceRequestPartsRepositoryCustom;
import com.a1stream.domain.form.service.SVM020102Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ServiceRequestPartsRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ServiceRequestPartsRepositoryCustom {

    @Override
    public List<SVM020102BO> getRepairPartsDetailList(SVM020102Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT (mp.product_cd || ' ' || mp.local_description)  AS  parts                  ");
        sql.append("          , srp.used_qty                                    AS  partsQty               ");
        sql.append("          , (CASE WHEN srp.select_flag = 'Y'                                           ");
        sql.append("                  THEN true ELSE false END)                 AS  partsSelectFlag        ");
        sql.append("          , srp.service_request_parts_id                    AS  serviceRequestPartsId  ");
        sql.append("       FROM service_request_parts srp                                                  ");
        sql.append("  LEFT JOIN mst_product mp                                                             ");
        sql.append("         ON mp.product_id = srp.product_id                                             ");
        sql.append("      WHERE srp.service_request_id = :serviceRequestId                                 ");

        params.put("serviceRequestId", form.getServiceRequestId());

        return super.queryForList(sql.toString(), params, SVM020102BO.class);
    }
}