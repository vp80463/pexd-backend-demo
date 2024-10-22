package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.bo.service.BatteryBO;
import com.a1stream.domain.custom.ServiceOrderBatteryRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ServiceOrderBatteryRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ServiceOrderBatteryRepositoryCustom {

    @Override
    public List<BatteryBO> listServiceBatteryByOrderId(Long serviceOrderId){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("  SELECT sob.product_cd AS partCd                              ");
        sql.append("       , sob.product_id AS partId                              ");
        sql.append("       , sob.battery_id AS currentBatteryId                    ");
        sql.append("       , sob.battery_no AS currentBatteryNo                    ");
        sql.append("       , sob.warranty_start_date AS warttryStartDate           ");
        sql.append("       , sob.warranty_term AS warrantyTerm                     ");
        sql.append("       , sob.new_product_id AS newPartId                       ");
        sql.append("       , sob.new_product_cd AS newPartCd                       ");
        sql.append("       , sob.new_battery_no AS newBatteryNo                    ");
        sql.append("       , sob.selling_price AS sellingPrice                     ");
        sql.append("       , sob.battery_type AS batteryType                       ");
        sql.append("       , sob.service_order_battery_id AS serviceOrderBatteryId ");
        sql.append("       , sob.update_count AS updateCounter                     ");
        sql.append("   FROM service_order_battery sob                              ");
        sql.append("  WHERE sob.service_order_id = :serviceOrderId                 ");
        sql.append("  ORDER BY battery_type                                        ");

        params.put("serviceOrderId", serviceOrderId);

        return super.queryForList(sql.toString(), params, BatteryBO.class);
    }
}
