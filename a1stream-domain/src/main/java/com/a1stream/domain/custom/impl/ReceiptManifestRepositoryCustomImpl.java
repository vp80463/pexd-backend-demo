package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ManifestItemStatus;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.parts.SPM030101BO;
import com.a1stream.domain.bo.unit.SDQ010701BO;
import com.a1stream.domain.bo.unit.SDQ010702HeaderBO;
import com.a1stream.domain.custom.ReceiptManifestRepositoryCustom;
import com.a1stream.domain.form.parts.SPM030101Form;
import com.a1stream.domain.form.unit.SDQ010701Form;
import com.ymsl.solid.base.util.StringUtils;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ReceiptManifestRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ReceiptManifestRepositoryCustom {

    @Override
    public List<SPM030101BO> getReceiptManifestList(SPM030101Form form, PJUserDetails uc) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT rmi.case_no                                             AS caseNo            ");
        sql.append("          , rm.supplier_shipment_no                                 AS shipmentNo        ");
        sql.append("          , rm.supplier_invoice_no                                  AS invoiceNo         ");
        sql.append("          , COUNT(rmi.receipt_manifest_item_id)                     AS lines             ");
        sql.append("          , COALESCE(SUM(rmi.receipt_qty), 0)                       AS receiptQty        ");
        sql.append("          , SUM(COALESCE(rmi.receipt_qty, 0) * COALESCE(rmi.receipt_price, 0))   AS receiptAmount     ");
        sql.append("          , rm.import_date                                          AS importDate        ");
        sql.append("          , COUNT(CASE WHEN rmi.error_flag = :errorFlag THEN 1 END) AS errorLines        ");
        sql.append("          , rm.receipt_manifest_id                                  AS receiptManifestId ");
        sql.append("          , rm.update_count                                         AS updateCount       ");
        sql.append("       FROM receipt_manifest rm                                                          ");
        sql.append("  LEFT JOIN receipt_manifest_item rmi                                                    ");
        sql.append("         ON rm.receipt_manifest_id = rmi.receipt_manifest_id                             ");
        sql.append("      WHERE rm.site_id = :siteId                                                         ");
        sql.append("        AND rm.product_classification = :productClassification                           ");
        sql.append("        AND rmi.manifest_item_status = :manifestitemstatus                               ");

        params.put("errorFlag", CommonConstants.CHAR_Y);
        params.put("siteId", uc.getDealerCode());
        params.put("manifestitemstatus",ManifestItemStatus.WAITING_RECEIVE.getCodeDbid());
        params.put("productClassification", ProductClsType.PART.getCodeDbid());

        if (ObjectUtils.isNotEmpty(form.getPointId())) {
            sql.append(" AND rm.to_facility_id = :toFacilityId ");
            params.put("toFacilityId", form.getPointId());
        }

        if (ObjectUtils.isNotEmpty(form.getSupplierId())) {
            sql.append(" AND rm.from_organization = :fromOrganization ");
            params.put("fromOrganization", form.getSupplierId());
        }

        if (StringUtils.isNotBlankText(form.getShipmentNo())) {
            sql.append(" AND rm.supplier_shipment_no = :supplierShipmentNo ");
            params.put("supplierShipmentNo", form.getShipmentNo());
        }

        if (StringUtils.isNotBlankText(form.getInvoiceNo())) {
            sql.append(" AND rm.supplier_invoice_no = :supplierInvoiceNo ");
            params.put("supplierInvoiceNo", form.getInvoiceNo());
        }

        if (StringUtils.isNotBlankText(form.getCaseNo())) {
            sql.append(" AND rmi.case_no = :caseNo  ");
            params.put("caseNo", form.getCaseNo());
        }

        sql.append(" GROUP BY rmi.case_no, rm.supplier_shipment_no, rm.supplier_invoice_no, rm.import_date, rm.receipt_manifest_id, rm.update_count ");
        sql.append(" ORDER BY rmi.case_no  ");
        return super.queryForList(sql.toString(), params, SPM030101BO.class);
    }

    @Override
    public List<SPM030101BO> getInoviceNoAndCaseNoList(List<String> orderNo) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT rm.supplier_invoice_no AS invoiceNo               ");
        sql.append("         , rmi.case_no            AS caseNo                  ");
        sql.append("         , rm.site_id             AS siteId                  ");

        sql.append("      FROM receipt_manifest rm                               ");
        sql.append(" LEFT JOIN receipt_manifest_item rmi                         ");
        sql.append("        ON rm.receipt_manifest_id  = rmi.receipt_manifest_id ");

        sql.append("     WHERE  rm.supplier_invoice_no IN (:orderNo)             ");

        params.put("orderNo", orderNo);

        return super.queryForList(sql.toString(), params, SPM030101BO.class);
    }

    @Override
    public List<SDQ010701BO> getReceiptManifestListForSD(SDQ010701Form model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT rm.receipt_manifest_id   AS receiptManifestId    ");
        sql.append("      , rm.manifest_status       AS manifestStatus       ");
        sql.append("      , cmo.organization_cd      AS supplierCd           ");
        sql.append("      , cmo.organization_nm      AS supplierNm           ");
        sql.append("      , rm.supplier_shipped_date AS supplierDeliveryDate ");
        sql.append("      , rm.supplier_shipment_no  AS deliveryNoteNo       ");
        sql.append("      , sum(rmi.receipt_qty)     AS qty                  ");

        sql.append("      FROM receipt_manifest rm                               ");
        sql.append(" LEFT JOIN cmm_mst_organization cmo                          ");
        sql.append("        ON rm.from_organization = cmo.organization_id        ");
        sql.append(" LEFT JOIN receipt_manifest_item rmi                         ");
        sql.append("        ON rm.receipt_manifest_id  = rmi.receipt_manifest_id ");
        sql.append("     WHERE rm.site_id = :siteId ");
        sql.append("       AND product_classification = :productClassification   ");
        params.put("siteId", model.getSiteId());
        params.put("productClassification",ProductClsType.GOODS.getCodeDbid());

        if (!ObjectUtils.isEmpty(model.getSupplierId())){
            sql.append(" AND rm.from_organization = :supplierId ");
            params.put("supplierId", model.getSupplierId());
        }

        if (!ObjectUtils.isEmpty(model.getDateFrom())){
            sql.append(" AND rm.supplier_shipped_date >= :dateFrom ");
            params.put("dateFrom", model.getDateFrom());
        }

        if (!ObjectUtils.isEmpty(model.getDateTo())){
            sql.append(" AND rm.supplier_shipped_date <= :dateTo ");
            params.put("dateTo", model.getDateTo());
        }

        if (!ObjectUtils.isEmpty(model.getManifestStatus())){
            sql.append(" AND rm.manifest_status = :manifestStatus ");
            params.put("manifestStatus", model.getManifestStatus());
        }

        if (!ObjectUtils.isEmpty(model.getDeliveryNoteNo())){
            sql.append(" AND rm.supplier_shipment_no = :deliveryNoteNumber ");
            params.put("deliveryNoteNumber", model.getDeliveryNoteNo());
        }

        sql.append(" GROUP BY rm.manifest_status, rm.receipt_manifest_id, cmo.organization_cd, cmo.organization_nm, rm.supplier_shipped_date, rm.supplier_invoice_no ");

        return super.queryForList(sql.toString(), params, SDQ010701BO.class);
    }

    @Override
    public SDQ010702HeaderBO getRecManMaintHeader(Long receiptManifestId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT mf.facility_cd                                     AS pointCd           ");
        sql.append("         , rm.to_facility_id                                  AS pointId           ");
        sql.append("         , mf.facility_nm                                     AS pointNm           ");
        sql.append("         , mf.facility_cd || ' ' || mf.facility_nm            AS point             ");
        sql.append("         , rm.manifest_status                                 AS manifestStatus    ");
        sql.append("         , cmo.organization_cd                                AS supplierCd        ");
        sql.append("         , cmo.organization_nm                                AS supplierNm        ");
        sql.append("         , cmo.organization_cd || ' ' || cmo.organization_nm  AS supplier          ");
        sql.append("         , rm.supplier_shipment_no                            AS deliveryNoteNo    ");
        sql.append("         , rm.supplier_shipped_date                           AS deliveryDate      ");
        sql.append("      FROM receipt_manifest rm                                                     ");
        sql.append(" LEFT JOIN mst_facility mf                                                         ");
        sql.append("        ON mf.facility_id  = rm.to_facility_id                                     ");
        sql.append(" LEFT JOIN cmm_mst_organization cmo                                                ");
        sql.append("        ON rm.from_organization  = cmo.organization_id                             ");
        sql.append("     WHERE rm.receipt_manifest_id = :receiptManifestId                             ");

        params.put("receiptManifestId", receiptManifestId);

        return super.queryForSingle(sql.toString(), params, SDQ010702HeaderBO.class);
    }
}
