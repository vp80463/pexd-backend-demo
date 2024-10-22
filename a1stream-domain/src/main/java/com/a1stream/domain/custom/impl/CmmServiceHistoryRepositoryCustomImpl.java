package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.domain.bo.service.SVM010201ServiceHistoryBO;
import com.a1stream.domain.bo.service.SVM0102PrintServiceHistoryBO;
import com.a1stream.domain.bo.unit.SVM010402ServiceHistoryBO;
import com.a1stream.domain.custom.CmmServiceHistoryRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class CmmServiceHistoryRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmServiceHistoryRepositoryCustom {

    /**
     * @author mid1341
     */
    @Override
    public Integer getMaxBaseDateByMcId(Long serializedProductId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT max(csd.base_date_after)                         ");
        sql.append("   FROM cmm_service_history csh                          ");
        sql.append("   LEFT JOIN cmm_service_demand csd                      ");
        sql.append("          ON csd.service_demand_id = csh.service_demand  ");
        sql.append("  WHERE csh.serialized_product_id = :serializedProductId ");
        sql.append("  AND csh.service_category = :freeCoupon                 ");

        params.put("serializedProductId", serializedProductId);
        params.put("freeCoupon", ServiceCategory.FREECOUPON.getCodeDbid());

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }

    /**
     * 服务单履历
     *
     * @author mid1341
     */
    @Override
    public List<SVM010201ServiceHistoryBO> findServiceHistoryByMotorId(Long serializedProductId, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        //当siteId是本经销商时，才显示orderNo
        sql.append(" SELECT case site_id when :siteId then order_no else '' end as orderNo  ");
        sql.append("      , order_date as orderDate                     ");
        sql.append("      , service_category_content as serviceCategory ");
        sql.append("      , service_subject as serviceTitle             ");
        sql.append("   FROM cmm_service_history                         ");
        sql.append("  WHERE serialized_product_id = :serializedProductId");
        sql.append("  ORDER BY order_date desc, order_no desc           ");
        sql.append("  LIMIT 3                                           ");

        params.put("siteId", siteId);
        params.put("serializedProductId", serializedProductId);

        return super.queryForList(sql.toString(), params, SVM010201ServiceHistoryBO.class);
    }

    @Override
    public List<SVM0102PrintServiceHistoryBO> getServiceHistoryPrintList(Long serviceOrderId, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT order_date               AS orderDate              ");
        sql.append("         , CASE                                               ");
        sql.append("             WHEN site_id != :siteId THEN NULL                ");
        sql.append("             ELSE order_no                                    ");
        sql.append("           END AS orderNo                                     ");
        sql.append("         , service_category_content AS serviceCategory        ");
        sql.append("         , replaced_part            AS description            ");
        sql.append("      FROM cmm_service_history                                ");
        sql.append("     WHERE serialized_product_id IN (                         ");
        sql.append("           SELECT serialized_product_id                       ");
        sql.append("             FROM service_order so                            ");
        sql.append("            WHERE so.service_order_id = :serviceOrderId    )  ");
        sql.append("  ORDER BY order_date DESC, order_no DESC                     ");
        sql.append("     LIMIT 3                                                  ");

        params.put("siteId", siteId);
        params.put("serviceOrderId", serviceOrderId);
        return super.queryForList(sql.toString(), params, SVM0102PrintServiceHistoryBO.class);
    }

    @Override
    public List<SVM010402ServiceHistoryBO> getMcServiceHistData(Long cmmSerializedProductId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT csh.order_date               AS orderDate           ");
        sql.append("         , csh.order_no                 AS orderNo             ");
        sql.append("         , csh.service_category_content AS serviceCategory     ");
        sql.append("         , csh.service_subject          AS serviceTitle        ");
        sql.append("         , csh.service_demand_content   AS serviceDemand       ");
        sql.append("         , csh.site_id                  AS siteId              ");
        sql.append("      From cmm_service_history csh                             ");
        sql.append("     WHERE csh.serialized_product_id = :cmmSerializedProductId ");

        params.put("cmmSerializedProductId", cmmSerializedProductId);

        return super.queryForList(sql.toString(), params, SVM010402ServiceHistoryBO.class);
    }

}
