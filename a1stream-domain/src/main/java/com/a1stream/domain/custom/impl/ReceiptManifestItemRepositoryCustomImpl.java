package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.parts.SPM030102BO;
import com.a1stream.domain.custom.ReceiptManifestItemRepositoryCustom;
import com.a1stream.domain.form.parts.SPM030102Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ReceiptManifestItemRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ReceiptManifestItemRepositoryCustom {

    @Override
    public List<SPM030102BO> getReceiptManifestItemList(SPM030102Form form, PJUserDetails uc) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT rmi.purchase_order_no            AS orderNo               ");
        sql.append("          , rmi.receipt_product_id           AS receiptPartsId        ");
        sql.append("          , rmi.receipt_product_cd           AS receiptPartsNo        ");
        sql.append("          , mp.local_description             AS receiptPartsNm        ");
        sql.append("          , rmi.order_product_id             AS orderPartsId          ");
        sql.append("          , rmi.order_product_cd             AS orderPartsNo          ");
        sql.append("          , mp2.local_description            AS orderPartsNM          ");
        sql.append("          , COALESCE(poi.on_purchase_qty, 0) AS onPurchaseQty         ");
        sql.append("          , po.purchase_order_id             AS purchaseOrderId       ");
        sql.append("          , rmi.receipt_qty                  AS totalReceiptQty       ");
        sql.append("          , rmi.receipt_price                AS receiptCost           ");
        sql.append("          , rmi.error_info                   AS error                 ");
        sql.append("          , rmi.receipt_manifest_item_id     AS receiptManifestItemId ");
        sql.append("          , rmi.receipt_manifest_id          AS receiptManifestId     ");
        sql.append("          , rmi.update_count                 AS updateCount           ");
        sql.append("       FROM receipt_manifest_item rmi                                 ");
        sql.append("  LEFT JOIN mst_product mp                                            ");
        sql.append("         ON mp.product_id = rmi.receipt_product_id                    ");
        sql.append("  LEFT JOIN mst_product mp2                                           ");
        sql.append("         ON mp2.product_id = rmi.receipt_product_id                   ");
        sql.append("  LEFT JOIN purchase_order po                                         ");
        sql.append("         ON po.order_no = rmi.purchase_order_no                       ");
        sql.append("        AND po.site_id = :siteId                                      ");
        sql.append("        AND po.facility_id = :facilityId                              ");
        sql.append("  LEFT JOIN purchase_order_item poi                                   ");
        sql.append("         ON poi.product_id = rmi.order_product_id                     ");
        sql.append("        AND poi.purchase_order_id = po.purchase_order_id              ");
        sql.append("      WHERE rmi.site_id = :siteId                                     ");
        sql.append("        AND rmi.case_no = :caseNo                                     ");
        
        params.put("siteId", uc.getDealerCode());
        params.put("facilityId", form.getPointId());
        params.put("caseNo", form.getCaseNo());

        return super.queryForList(sql.toString(), params, SPM030102BO.class);
    }

}
