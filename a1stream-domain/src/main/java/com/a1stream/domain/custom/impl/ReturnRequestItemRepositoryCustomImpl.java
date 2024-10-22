package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.parts.SPM021402BO;
import com.a1stream.domain.custom.ReturnRequestItemRepositoryCustom;
import com.a1stream.domain.form.parts.SPM021402Form;
import com.ymsl.solid.base.util.StringUtils;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ReturnRequestItemRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ReturnRequestItemRepositoryCustom {

    @Override
    public List<SPM021402BO> getReturnRequestItemList(SPM021402Form form, PJUserDetails uc) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT rri.return_request_item_id     AS returnRequestItemId     ");
        sql.append("          , rri.product_cd                 AS partsNo                 ");
        sql.append("          , rri.product_nm                 AS partsNm                 ");
        sql.append("          , rri.product_id                 AS partsId                 ");
        sql.append("          , rri.request_type               AS requestType             ");
        sql.append("          , rri.recommend_qty              AS recommendQty            ");
        sql.append("          , rri.request_qty                AS requestQty              ");
        sql.append("          , rri.approved_qty               AS approveQty              ");
        sql.append("          , rri.return_price               AS returnPrice             ");
        sql.append("          , rri.yamaha_invoice_seq_no      AS yamahaInvoiceSeqNo      ");
        sql.append("          , rri.yamaha_external_invoice_no AS yamahaExternalInvoiceNo ");
        sql.append("          , rri.sales_order_id             AS salesorderId            ");
        sql.append("          , doi.delivery_order_id          AS deliveryOrderId         ");
        sql.append("          , rri.update_count               AS updateCount             ");
        sql.append("       FROM return_request_item rri                                   ");
        sql.append("  LEFT JOIN delivery_order_item doi                                   ");
        sql.append("         ON doi.sales_order_id = rri.sales_order_id                   ");
        sql.append("      WHERE rri.site_id = :siteId                                     ");
        sql.append("        AND rri.product_classification = :productClassification       ");
        sql.append("        AND rri.facility_id = :facilityId                             ");
        sql.append("        AND rri.request_type = :requestType                           ");

        params.put("siteId", uc.getDealerCode());
        params.put("productClassification", ProductClsType.PART.getCodeDbid());
        params.put("facilityId", form.getPointId());
        params.put("requestType", form.getSelectPicking());

        if (StringUtils.isNotBlankText(form.getStatus())) {
            sql.append(" AND rri.request_status = :status ");
            params.put("status", form.getStatus());
        }

        if (!ObjectUtils.isEmpty(form.getReturnRequestListId())){
            sql.append(" AND rri.return_request_list_id = :listId ");
            params.put("listId", form.getReturnRequestListId());
        }

        sql.append(" ORDER BY rri.request_status ");

        return super.queryForList(sql.toString(), params, SPM021402BO.class);
    }
}
