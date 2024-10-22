package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.common.constants.PJConstants.ServiceRequestStatus;
import com.a1stream.domain.bo.service.SVM020201BO;
import com.a1stream.domain.bo.service.SVM020202BO;
import com.a1stream.domain.bo.service.SVM0202PrintBO;
import com.a1stream.domain.custom.ServicePaymentRepositoryCustom;
import com.a1stream.domain.form.service.SVM020201Form;
import com.a1stream.domain.form.service.SVM020202Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ServicePaymentRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ServicePaymentRepositoryCustom {

    @Override
    public List<SVM020201BO> findServicePaymentList(SVM020201Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT payment_category            as  category                ");
        sql.append("          , factory_budget_settle_date  as  fixDate                 ");
        sql.append("          , factory_payment_control_no  as  paymentControlNo        ");
        sql.append("          , payment_amt                 as  paymentAmount           ");
        sql.append("          , payment_status              as  status                  ");
        sql.append("          , confirm_date                as  confirmDate             ");
        sql.append("          , CASE                                                    ");
        sql.append("              WHEN confirm_date <> ''                               ");
        sql.append("              THEN 'System'                                         ");
        sql.append("              ELSE ''                                               ");
        sql.append("            END                         as  confirmPerson           ");
        sql.append("          , bulletin_no                 as  bulletinNo              ");
        sql.append("          , receipt_date                as  receiptDate             ");
        sql.append("          , payment_id                  as  serviceRequestPaymentId ");

        sql.append("       FROM service_payment                                         ");

        sql.append("      WHERE site_id = :siteId                                       ");
        sql.append("        AND factory_budget_settle_date >= :fixDate                  ");
        sql.append("        AND factory_budget_settle_date <= :fixDateTo                ");

        if (StringUtils.isNotBlank(form.getPaymentControlNo())) {
            sql.append("    AND factory_payment_control_no = :paymentControlNo          ");
            params.put("paymentControlNo", form.getPaymentControlNo());
        }

        if (StringUtils.isNotBlank(form.getRequestCategory())) {
            sql.append("    AND payment_category = :requestCategory                     ");
            params.put("requestCategory", form.getRequestCategory());
        }

        sql.append("        AND payment_status in (:status)                             ");
        sql.append("   ORDER BY factory_budget_settle_date                              ");

        params.put("siteId", siteId);
        params.put("fixDate", form.getFixDate());
        params.put("fixDateTo", form.getFixDateTo());
        params.put("status", form.getStatus());

        return super.queryForList(sql.toString(), params, SVM020201BO.class);
    }

    @Override
    public SVM020201BO getSpDetailHeader(SVM020202Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT factory_budget_settle_date        as  fixDate                     ");
        sql.append("          , bulletin_no                       as  bulletinNo                  ");
        sql.append("          , payment_status                    as  status                      ");
        sql.append("          , receipt_date                      as  receiptDate                 ");
        sql.append("          , confirm_date                      as  confirmDate                 ");
        sql.append("          , CASE                                                              ");
        sql.append("              WHEN confirm_date <> ''                                         ");
        sql.append("              THEN 'System'                                                   ");
        sql.append("              ELSE ''                                                         ");
        sql.append("            END                               as  confirmPerson               ");
        sql.append("          , factory_payment_control_no        as  paymentControlNo            ");
        sql.append("          , payment_amt_free_coupon_total     as  freeCouponPaymentAmount     ");
        sql.append("          , payment_amt_warranty_claim_total  as  warrantyClaimPaymentAmount  ");
        sql.append("          , payment_amt_battery_warranty      as  batteryClaimPaymentAmount   ");
        sql.append("          , payment_amt                       as  paymentTotalAmount          ");
        sql.append("          , factory_doc_receipt_date          as  statementDocReceiptDate     ");
        sql.append("          , vat_cd                            as  vatCd                       ");
        sql.append("          , invoice_no                        as  invoiceNo                   ");
        sql.append("          , invoice_date                      as  invoiceDate                 ");
        sql.append("          , serial_no                         as  serialNo                    ");

        sql.append("       FROM service_payment                                                   ");

        sql.append("      WHERE site_id = :siteId                                                 ");
        sql.append("        AND payment_id = :paymentId                                           ");

        params.put("siteId", form.getSiteId());
        params.put("paymentId", form.getPaymentId());

        return super.queryForSingle(sql.toString(), params, SVM020201BO.class);
    }

    @Override
    public List<SVM020202BO> getSpDetailList(SVM020202Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        Set<String> status = Set.of(ServiceRequestStatus.APPROVED.getCodeDbid()
                                   ,ServiceRequestStatus.CONFIRMED.getCodeDbid());

        sql.append("        SELECT sr.request_type                        as  category          ");
        sql.append("             , sr.request_no                          as  requestNo         ");
        sql.append("             , sr.request_date                        as  requestDate       ");
        sql.append("             , sr.payment_total_amt                   as  paymentAmount     ");
        sql.append("             ,  (SELECT request_comment                                     ");
        sql.append("                   FROM service_request_edit_history                        ");
        sql.append("                  WHERE service_request_id = sr.service_request_id          ");
        sql.append("               ORDER BY date_created DESC LIMIT 1)    as  requestComment    ");
        sql.append("             , sr.service_request_id                  as  serviceRequestId  ");
        sql.append("          FROM service_request sr                                           ");

        if (form.getCategory().equals(ServiceCategory.SPECIALCLAIM.getCodeDbid())) {
            sql.append(" LEFT JOIN service_order so                                             ");
            sql.append("        ON so.service_order_id = sr.service_order_id                    ");
            sql.append("     WHERE ((:bulletinNo <> '' AND so.bulletin_no = :bulletinNo)        ");
            sql.append("           OR (:bulletinNo = '' AND so.bulletin_no IS NULL))            ");
            sql.append("       AND sr.site_id = :siteId                                         ");
            sql.append("       AND sr.payment_month = :paymentMonth                             ");
            sql.append("       AND sr.request_type = :paymentCategory                           ");
            params.put("bulletinNo", form.getBulletinNo());
        } else {
            sql.append("     WHERE sr.site_id = :siteId                                         ");
            sql.append("       AND sr.request_status in (:status)                               ");
            sql.append("       AND sr.payment_month = :paymentMonth                             ");
            sql.append("       AND sr.request_type = :paymentCategory                           ");
            params.put("status", status);
        }

        params.put("siteId", form.getSiteId());
        params.put("paymentMonth", form.getPaymentMonth());
        params.put("paymentCategory", form.getPaymentCategory());

        return super.queryForList(sql.toString(), params, SVM020202BO.class);
    }

    @Override
    public Integer getCheckCountByInvoiceNoAndSerialNo(SVM020202Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT count(1)                                  ");
        sql.append("   FROM service_payment                           ");
        sql.append("  WHERE site_id = :siteId                         ");
        sql.append("    AND invoice_no = :invoiceNo                   ");
        sql.append("    AND serial_no = :serialNo                     ");
        sql.append("    AND payment_id <> :paymentId                  ");

        params.put("siteId", siteId);
        params.put("invoiceNo", form.getInvoiceNo());
        params.put("serialNo", form.getSerialNo());
        params.put("paymentId", form.getPaymentId());

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }

    @Override
    public Integer getCheckCountByInvoiceNo(SVM020202Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT count(1)                                  ");
        sql.append("   FROM service_payment                           ");
        sql.append("  WHERE site_id = :siteId                         ");
        sql.append("    AND invoice_no = :invoiceNo                   ");
        sql.append("    AND payment_id <> :paymentId                  ");

        params.put("siteId", siteId);
        params.put("invoiceNo", form.getInvoiceNo());
        params.put("paymentId", form.getPaymentId());

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }

    @Override
    public SVM0202PrintBO getServiceExpensesClaimStatementPrintData(Long paymentId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT sv.factory_payment_control_no      AS ctlNo              ");
        sql.append("          , org.organization_cd                AS dealerCode         ");
        sql.append("          , org.organization_nm                AS dealerName         ");
        sql.append("          , org.address1 || org.address2       AS address            ");
        sql.append("          , sv.target_month                    AS claimMonth         ");
        sql.append("          , sv.payment_amt_warranty_claim_part AS partClaimAmount    ");
        sql.append("          , sv.payment_amt_warranty_claim_job  AS jobClaimAmount     ");
        sql.append("       FROM service_payment sv                                       ");
        sql.append("  LEFT JOIN mst_organization org                                     ");
        sql.append("         ON org.site_id = sv.site_id                                 ");
        sql.append("        AND org.organization_cd = sv.site_id                         ");
        sql.append("      WHERE sv.payment_id= :paymentId                                ");

        params.put("paymentId", paymentId);
        return super.queryForSingle(sql.toString(), params, SVM0202PrintBO.class);
    }

    @Override
    public SVM0202PrintBO getServiceExpensesClaimStatementForEVPrintData(Long paymentId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT sv.factory_payment_control_no    AS ctlNo                ");
        sql.append("         , org.organization_cd              AS dealerCode           ");
        sql.append("         , org.organization_nm              AS dealerName           ");
        sql.append("         , org.address1 || org.address2     AS address              ");
        sql.append("         , sv.target_month                  AS claimMonth           ");
        sql.append("         , sv.payment_amt_battery_warranty  AS batteryClaimAmount   ");
        sql.append("         , sv.bulletin_no                   AS bulletinNo           ");
        sql.append("      FROM service_payment sv                                       ");
        sql.append(" LEFT JOIN mst_organization org                                     ");
        sql.append("        ON org.site_id = sv.site_id                                 ");
        sql.append("       AND org.organization_cd = sv.site_id                         ");
        sql.append("      WHERE sv.payment_id= :paymentId                               ");

        params.put("paymentId", paymentId);
        return super.queryForSingle(sql.toString(), params, SVM0202PrintBO.class);
    }

    @Override
    public SVM0202PrintBO getServiceExpensesCouponStatementPrintData(Long paymentId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT sv.factory_payment_control_no     AS ctlNo                 ");
        sql.append("         , org.organization_cd               AS dealerName            ");
        sql.append("         , org.organization_nm               AS dealerCode            ");
        sql.append("         , org.address1 || org.address2      AS address               ");
        sql.append("         , sv.target_month                   AS claimMonth            ");
        sql.append("         , sv.payment_amt_free_coupon_level1 AS couponAmountLevel1    ");
        sql.append("         , sv.payment_amt_free_coupon_level2 AS couponAmountLevel2    ");
        sql.append("         , sv.payment_amt_free_coupon_level3 AS couponAmountLevel3    ");
        sql.append("      FROM service_payment sv                                         ");
        sql.append(" LEFT JOIN mst_organization org                                       ");
        sql.append("        ON org.site_id = sv.site_id                                   ");
        sql.append("       AND org.organization_cd = sv.site_id                           ");
        sql.append("     WHERE sv.payment_id = :paymentId                                 ");

        params.put("paymentId", paymentId);
        return super.queryForSingle(sql.toString(), params, SVM0202PrintBO.class);
    }
}