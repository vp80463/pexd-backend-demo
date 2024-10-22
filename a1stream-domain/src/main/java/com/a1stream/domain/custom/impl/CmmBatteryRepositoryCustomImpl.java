package com.a1stream.domain.custom.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.PJConstants.WarrantyPolicyType;
import com.a1stream.domain.bo.service.BatteryBO;
import com.a1stream.domain.custom.CmmBatteryRepositoryCustom;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class CmmBatteryRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmBatteryRepositoryCustom {

    @Override
    public List<BatteryBO> listServiceBatteryByMotorId(Long serializedProId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("  SELECT cb.position_sign AS batteryType               ");
        sql.append("       , cb.battery_cd AS partCd                       ");
        sql.append("       , cb.product_id AS partId                       ");
        sql.append("       , cb.battery_no AS currentBatteryNo             ");
        sql.append("       , cb.battery_id AS currentBatteryId             ");
        sql.append("       , cb.service_calculate_date AS warttryStartDate ");
        sql.append("       , CASE cwb.warranty_product_classification      ");
        sql.append("          WHEN :batteryWarrantyPolicyType              ");
        sql.append("               THEN :batteryWarrantyTerm               ");
        sql.append("          ELSE ''                                      ");
        sql.append("         END AS warrantyTerm                           ");
        sql.append("    FROM cmm_battery cb                                ");
        sql.append("    LEFT JOIN cmm_warranty_battery cwb                 ");
        sql.append("           ON cwb.battery_id = cb.battery_id           ");
        sql.append("   WHERE cb.serialized_product_id = :serializedProId   ");
        sql.append("     AND cb.from_date <= :sysDate                      ");
        sql.append("     AND cb.to_date >= :sysDate                        ");
        sql.append("   ORDER BY cb.position_sign                           ");

        params.put("batteryWarrantyPolicyType", WarrantyPolicyType.BATTERY.getCodeDbid());
        params.put("batteryWarrantyTerm", WarrantyPolicyType.BATTERY.getCodeData1());
        params.put("serializedProId", serializedProId);
        params.put("sysDate", LocalDate.now().format(DateTimeFormatter.ofPattern(DateUtils.FORMAT_YMD_NODELIMITER)));

        return super.queryForList(sql.toString(), params, BatteryBO.class);
    }

}
