package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.bo.service.SVM020102BO;
import com.a1stream.domain.custom.ServiceRequestBatteryRepositoryCustom;
import com.a1stream.domain.form.service.SVM020102Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ServiceRequestBatteryRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ServiceRequestBatteryRepositoryCustom {

    @Override
    public List<SVM020102BO> getRepairBatteryDetailList(SVM020102Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT srb.battery_id                   AS  batteryId                ");
        sql.append("          , srb.used_qty                     AS  batteryQty               ");
        sql.append("          , (CASE WHEN srb.select_flag = 'Y'                              ");
        sql.append("                  THEN true ELSE false END)  AS  batterySelectFlag        ");
        sql.append("          , srb.service_request_battery_id   AS  serviceRequestBatteryId  ");
        sql.append("       FROM service_request_battery srb                                   ");
        sql.append("      WHERE srb.service_request_id = :serviceRequestId                    ");

        params.put("serviceRequestId", form.getServiceRequestId());

        return super.queryForList(sql.toString(), params, SVM020102BO.class);
    }
}