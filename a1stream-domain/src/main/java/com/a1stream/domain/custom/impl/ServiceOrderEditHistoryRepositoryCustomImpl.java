package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.bo.service.SVM010201HistoryBO;
import com.a1stream.domain.custom.ServiceOrderEditHistoryRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ServiceOrderEditHistoryRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ServiceOrderEditHistoryRepositoryCustom {

    @Override
    public List<SVM010201HistoryBO> listServiceEditHistoryByOrderId(Long serviceOrderId){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT process_time   as processTime ");
        sql.append("      , operator_cd    as staffCd     ");
        sql.append("      , operator_nm    as staffNm     ");
        sql.append("      , operation_desc as operation   ");
        sql.append("   FROM service_order_edit_history    ");
        sql.append("  WHERE service_order_id = :serviceOrderId ");
        sql.append(" ORDER BY process_time                ");

        params.put("serviceOrderId", serviceOrderId);

        return super.queryForList(sql.toString(), params, SVM010201HistoryBO.class);
    }
}
