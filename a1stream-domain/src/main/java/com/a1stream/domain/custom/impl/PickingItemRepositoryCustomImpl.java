package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.parts.SPM020104BO;
import com.a1stream.domain.custom.PickingItemRepositoryCustom;
import com.a1stream.domain.form.parts.SPM020104Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class PickingItemRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements PickingItemRepositoryCustom {

    @Override
    public List<SPM020104BO> getPickingItemList(SPM020104Form form, PJUserDetails uc) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT pi2.seq_no          AS pickingSeqNo           ");
        sql.append("          , pi2.product_id      AS orderPartsId           ");
        sql.append("          , pi2.product_cd      AS orderPartsCd           ");
        sql.append("          , pi2.product_nm      AS orderPartsNm           ");
        sql.append("          , doi.product_id      AS allocatePartsId        ");
        sql.append("          , doi.product_cd      AS allocatePartsCd        ");
        sql.append("          , doi.product_nm      AS allocatePartsNm        ");
        sql.append("          , pi2.location_id     AS locationId             ");
        sql.append("          , pi2.location_cd     AS location               ");
        sql.append("          , pi2.qty             AS pickingQty             ");
        sql.append("          , doi.selling_price   AS price                  ");
        sql.append("          , pi2.picking_item_id AS pickingItemId          ");
        sql.append("       FROM picking_item pi2                              ");
        sql.append("          , delivery_order_item doi                       ");
        sql.append("      WHERE pi2.site_id = :siteId                         ");
        sql.append("        AND pi2.delivery_order_id = doi.delivery_order_id ");
        sql.append("        AND pi2.delivery_order_id = :deliveryOrderId      ");
        sql.append("        AND pi2.product_id = doi.product_id               ");

        params.put("siteId", uc.getDealerCode());
        params.put("deliveryOrderId", ProductClsType.PART.getCodeDbid());

        return super.queryForList(sql.toString(), params, SPM020104BO.class);
    }
}
