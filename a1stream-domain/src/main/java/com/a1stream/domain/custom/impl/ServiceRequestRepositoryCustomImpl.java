package com.a1stream.domain.custom.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.common.constants.PJConstants.ServiceRequestStatus;
import com.a1stream.domain.bo.service.SVM020101BO;
import com.a1stream.domain.bo.service.SVM0201PrintBO;
import com.a1stream.domain.bo.service.SVM0201PrintDetailBO;
import com.a1stream.domain.bo.service.SVM0202PrintBO;
import com.a1stream.domain.custom.ServiceRequestRepositoryCustom;
import com.a1stream.domain.form.service.SVM020101Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ServiceRequestRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ServiceRequestRepositoryCustom {

    @Override
    public List<SVM020101BO> findServiceRequestList(SVM020101Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT sr.request_date                              AS requestDate           ");
        sql.append("          , sr.request_no                                AS requestNo             ");
        sql.append("          , sr.request_type                              AS requestType           ");
        sql.append("          , sr.factory_receipt_no                        AS factoryReceiptNo      ");
        sql.append("          , so.order_no                                  AS serviceOrderNo        ");
        sql.append("          , so.facility_id                               AS facilityId            ");
        sql.append("          , mp.product_cd                                AS model                 ");
        sql.append("          , sr.campaign_no                               AS campaignNo            ");
        sql.append("          , sr.bulletin_no                               AS bulletinNo            ");
        sql.append("          , sp.plate_no                                  AS plateNo               ");
        sql.append("          , sr.request_status                            AS requestStatus         ");
        sql.append("          , sr.service_request_id                        AS serviceRequestId      ");
        sql.append("          , so.service_order_id                          AS serviceOrderId        ");
        sql.append("          , so.cmm_serialized_product_id                 AS serializedProductId   ");
        sql.append("          , so.service_category_id                       AS serviceCategoryId     ");
        sql.append("          , cwsp.warranty_product_classification         AS warrantyType          ");
        sql.append("          , cwsp.warranty_product_usage                  AS warrantyProductUsage  ");
        sql.append("          , cwsp.from_date                               AS fromDate              ");
        sql.append("          , cwsp.to_date                                 AS toDate                ");
        sql.append("       FROM service_request sr                                                    ");
        sql.append("  LEFT JOIN service_order so                                                      ");
        sql.append("         ON sr.service_order_id = so.service_order_id                             ");
        sql.append("        AND so.facility_id = :pointId                                             ");
        sql.append("  LEFT JOIN cmm_warranty_serialized_product cwsp                                  ");
        sql.append("         ON cwsp.serialized_product_id = so.cmm_serialized_product_id             ");
        sql.append("  LEFT JOIN serialized_product sp                                                 ");
        sql.append("         ON sp.serialized_product_id = so.cmm_serialized_product_id               ");
        sql.append("  LEFT JOIN mst_product mp                                                        ");
        sql.append("         ON mp.product_id = sp.serialized_product_id                              ");
        sql.append("      WHERE 1 = 1                                                                 ");

        if(form.getRequestCategoryId() != null) {
            sql.append("    AND sr.request_type = :requestCategoryId                                  ");
            params.put("requestCategoryId", form.getRequestCategoryId());
        }

        if(!ObjectUtils.isEmpty(form.getDateFrom())) {
            sql.append("    AND sr.request_date >= :dateFrom                                          ");
            params.put("dateFrom", form.getDateFrom());
        }

        if(!ObjectUtils.isEmpty(form.getDateTo())) {
            sql.append("    AND sr.request_date <= :dateTo                                            ");
            params.put("dateTo", form.getDateTo());
        }

        if(!ObjectUtils.isEmpty(form.getServiceOrderNo())) {
            sql.append("    AND so.order_no = :serviceOrderNo                                         ");
            params.put("serviceOrderNo", form.getServiceOrderNo());
        }

        if(form.getAllFlag().equals(CommonConstants.CHAR_N)) {
            sql.append("    AND sr.request_status IN (:status)                                        ");
            params.put("status", form.getStatus());
        }

        sql.append("   ORDER BY sr.request_date                                                       ");
        sql.append("          , sr.request_no                                                         ");

        params.put("pointId", form.getPointId());

        return super.queryForList(sql.toString(), params, SVM020101BO.class);
    }

    @Override
    public SVM0202PrintBO getServiceExpensesCouponStatementPrintDetailData(String siteId, String paymentMonth) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT sum(f1)   AS fir                                                           ");
        sql.append("          , sum(f2)   AS sec                                                           ");
        sql.append("          , sum(f3)   AS thi                                                           ");
        sql.append("          , sum(f4)   AS fou                                                           ");
        sql.append("          , sum(f5)   AS fif                                                           ");
        sql.append("          , sum(f6)   AS si                                                            ");
        sql.append("          , sum(f7)   AS sev                                                           ");
        sql.append("          , sum(f8)   AS eig                                                           ");
        sql.append("          , sum(f9)   AS nin                                                           ");
        sql.append("     FROM ( SELECT sum(CASE WHEN service_demand_id = '1' THEN 1 ELSE 0 END)  AS f1     ");
        sql.append("                 , sum(CASE WHEN service_demand_id = '2' THEN 1 ELSE 0 END)  AS f2     ");
        sql.append("                 , sum(CASE WHEN service_demand_id = '3' THEN 1 ELSE 0 END)  AS f3     ");
        sql.append("                 , sum(CASE WHEN service_demand_id = '4' THEN 1 ELSE 0 END)  AS f4     ");
        sql.append("                 , sum(CASE WHEN service_demand_id = '5' THEN 1 ELSE 0 END)  AS f5     ");
        sql.append("                 , sum(CASE WHEN service_demand_id = '6' THEN 1 ELSE 0 END)  AS f6     ");
        sql.append("                 , sum(CASE WHEN service_demand_id = '7' THEN 1 ELSE 0 END)  AS f7     ");
        sql.append("                 , sum(CASE WHEN service_demand_id = '8' THEN 1 ELSE 0 END)  AS f8     ");
        sql.append("                 , sum(CASE WHEN service_demand_id = '9' THEN 1 ELSE 0 END)  AS f9     ");
        sql.append("              FROM service_request sr                                                  ");
        sql.append("             WHERE sr.site_id = :siteId                                                ");
        sql.append("               AND sr.payment_month = :paymentMonth                                    ");
        sql.append("               AND sr.request_status IN (:requestStatus)                               ");
        sql.append("               AND sr.request_type = :requestType                                      ");
        sql.append("               AND sr.service_demand_id IS NOT NULL                                    ");
        sql.append("         GROUP BY service_demand_id ) AS couponCount                                   ");

        params.put("siteId", siteId);
        params.put("paymentMonth", paymentMonth);
        params.put("requestStatus", Arrays.asList(ServiceRequestStatus.APPROVED.getCodeDbid(),
                                                  ServiceRequestStatus.CONFIRMED.getCodeDbid()));

        params.put("requestType", ServiceCategory.FREECOUPON.getCodeDbid());
        return super.queryForSingle(sql.toString(), params, SVM0202PrintBO.class);
    }

    @Override
    public SVM0201PrintBO getPartsClaimTagPrintHeaderData(Long serviceRequestId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT ser.request_no                             AS claimNo               ");
        sql.append("         , ser.request_date                           AS claimDate             ");
        sql.append("         , ser.mileage                                AS mileage               ");
        sql.append("         , ser.campaign_no                            AS campaignNo            ");
        sql.append("         , sp.frame_no                                AS frameNo               ");
        sql.append("         , sp.stu_date                                AS saleDate              ");
        sql.append("         , sof.symptom_cd || ' ' || sof. symptom_nm   AS symptom               ");
        sql.append("         , sof.symptom_comment                        AS symptomComment        ");
        sql.append("         FROM service_request ser                                              ");
        sql.append("    LEFT JOIN serialized_product sp                                            ");
        sql.append("           ON ser.serialized_product_id = sp.serialized_product_id             ");
        sql.append("    LEFT JOIN service_order_fault sof                                          ");
        sql.append("           ON ser.service_order_fault_id = sof.service_order_fault_id          ");
        sql.append("        WHERE ser.service_request_id = :serviceRequestId                       ");

        params.put("serviceRequestId", serviceRequestId);
        return super.queryForSingle(sql.toString(), params, SVM0201PrintBO.class);
    }

    @Override
    public List<SVM0201PrintDetailBO> getPartsClaimTagPrintDetailList(Long serviceRequestId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT p.product_cd        AS partNo                       ");
        sql.append("         , p.local_description AS partName                     ");
        sql.append("         , parts.used_qty      AS qty                          ");
        sql.append("      FROM service_request ser                                 ");
        sql.append(" LEFT JOIN service_request_parts parts                         ");
        sql.append("        ON ser.service_request_id = parts.service_request_id   ");
        sql.append(" LEFT JOIN mst_product p                                       ");
        sql.append("        ON parts.product_id = p.product_id                     ");
        sql.append("     WHERE ser.service_request_id = :serviceRequestId          ");

        params.put("serviceRequestId", serviceRequestId);
        return super.queryForList(sql.toString(), params, SVM0201PrintDetailBO.class);
    }

    @Override
    public SVM0201PrintBO getPartsClaimForBatteryClaimTagPrintHeaderData(Long serviceRequestId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT ser.request_no                             AS claimNo               ");
        sql.append("         , ser.request_date                           AS claimDate             ");
        sql.append("         , ser.mileage                                AS mileage               ");
        sql.append("         , ser.campaign_no                            AS campaignNo            ");
        sql.append("         , bat.sale_date                              AS saleDate              ");
        sql.append("         , sob.battery_no                             AS batteryId             ");
        sql.append("         , sob.new_battery_no                         AS newBatteryId          ");
        sql.append("         , sof.symptom_cd || ' ' || sof. symptom_nm   AS symptom               ");
        sql.append("         , sof.symptom_comment                        AS symptomComment        ");
        sql.append("         FROM service_request ser                                              ");
        sql.append("    LEFT JOIN service_request_battery srb                                      ");
        sql.append("           ON ser.service_request_id = srb.service_request_id                  ");
        sql.append("    LEFT JOIN service_order_battery sob                                        ");
        sql.append("           ON srb.service_order_battery_id = sob.service_order_battery_id      ");
        sql.append("    LEFT JOIN service_order_fault sof                                          ");
        sql.append("           ON ser.service_order_fault_id = sof.service_order_fault_id          ");
        sql.append("    LEFT JOIN battery bat                                                      ");
        sql.append("           ON srb.battery_id = bat.battery_id                                  ");
        sql.append("        WHERE ser.service_request_id = :serviceRequestId                       ");

        params.put("serviceRequestId", serviceRequestId);
        return super.queryForSingle(sql.toString(), params, SVM0201PrintBO.class);
    }
}