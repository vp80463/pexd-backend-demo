package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.parts.SPM040402BO;
import com.a1stream.domain.custom.PurchaseOrderItemRepositoryCustom;
import com.a1stream.domain.form.parts.SPM040402Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class PurchaseOrderItemRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements PurchaseOrderItemRepositoryCustom {

    @Override
    public List<SPM040402BO> getPurchaseOrderItemList(SPM040402Form form, PJUserDetails uc) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT poi.product_cd             AS partsNo               ");
        sql.append("          , poi.product_nm             AS partsNm               ");
        sql.append("          , poi.product_id             AS partsId               ");
        sql.append("          , poi.purchase_price         AS price                 ");
        sql.append("          , mp.min_pur_qty             AS minPurchaseQty        ");
        sql.append("          , mp.pur_lot_size            AS purchaseLot           ");
        sql.append("          , poi.order_qty              AS orderQty              ");
        sql.append("          , poi.amount                 AS orderAmount           ");
        sql.append("          , poi.bo_cancel_flag         AS cancelFlag            ");
        sql.append("          , poi.purchase_order_item_id AS purchaseOrderItemId   ");
        sql.append("          , poi.purchase_order_id      AS purchaseOrderId       ");
        sql.append("          , poi.seq_no                 AS seqNo                 ");
        sql.append("          , poi.update_count           AS updateCount           ");
        sql.append("       FROM purchase_order_item poi                             ");
        sql.append("  LEFT JOIN mst_product mp ON poi.product_id = mp.product_id    ");
        sql.append("      WHERE poi.site_id = :siteId                               ");
        sql.append("        AND poi.product_classification = :productClassification ");
        
        params.put("siteId", uc.getDealerCode());
        params.put("productClassification", ProductClsType.PART.getCodeDbid());
        
        if (!ObjectUtils.isEmpty(form.getPurchaseOrderId())) {
            sql.append(" AND poi.purchase_order_id = :purchaseOrderId ");
            params.put("purchaseOrderId", form.getPurchaseOrderId());
        }

        sql.append(" ORDER BY poi.seq_no ");

        return super.queryForList(sql.toString(), params, SPM040402BO.class);
    }
}
