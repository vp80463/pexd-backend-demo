package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.bo.parts.PartsPickingListByOrderPrintBO;
import com.a1stream.domain.bo.parts.SPM030603BO;
import com.a1stream.domain.custom.DeliveryOrderItemRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;
public class DeliveryOrderItemRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements DeliveryOrderItemRepositoryCustom {

    @Override
    public List<PartsPickingListByOrderPrintBO> getPartsPickingListByOrderReportList(Long deliveryOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("      SELECT wz.workzone_cd         AS wz                             ");
        sql.append("           , location.location_cd   AS locationCd                     ");
        sql.append("           , doi.product_cd         AS partsNo                        ");
        sql.append("           , doi.product_nm         AS partsName                      ");
        sql.append("           , COALESCE(pick.qty, 0)  AS pickQty                        ");
        sql.append("           , pick.picking_list_no   AS pickSeqNo                      ");
        sql.append("           , dor.delivery_order_no  AS duNo                           ");
        sql.append("           , dor.delivery_order_id  AS duId                           ");
        sql.append("           , mf.facility_cd || ' / ' || mf.facility_nm  AS point      ");
        sql.append("           , so.order_no            AS orderNo                        ");
        sql.append("      FROM delivery_order_item doi                                    ");
        sql.append("INNER JOIN delivery_order dor                                         ");
        sql.append("        ON doi.delivery_order_id = dor.delivery_order_id              ");
        sql.append(" LEFT JOIN sales_order so                                             ");
        sql.append("   ON so.sales_order_id = doi.sales_order_id                          ");
        sql.append("INNER JOIN mst_facility mf                                            ");
        sql.append("        ON dor.from_facility_id = mf.facility_id                      ");
        sql.append("       AND dor.site_id = mf.site_id                                   ");
        sql.append("INNER JOIN picking_item pick                                          ");
        sql.append("        ON pick.delivery_order_item_id = doi.delivery_order_item_id   ");
        sql.append("INNER JOIN location                                                   ");
        sql.append("        ON location.location_id = pick.location_id                    ");
        sql.append(" LEFT JOIN workzone wz                                                ");
        sql.append("        ON location.workzone_id = wz.workzone_id                      ");
        sql.append("     WHERE dor.delivery_order_id = :deliveryOrderId                   ");

        params.put("deliveryOrderId", deliveryOrderId);
        sql.append(" ORDER BY pick.seq_no, wz.workzone_cd, doi.product_nm, location.location_id ");
        return super.queryForList(sql.toString(), params, PartsPickingListByOrderPrintBO.class);
    }

    @Override
    public List<SPM030603BO> getTransferPartsDetailList(Long deliveryOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT doi.product_cd                             AS partsNo         ");
        sql.append("          , doi.product_nm                             AS partsNm         ");
        sql.append("          , doi.delivery_qty                           AS transferQty     ");
        sql.append("          , frm.facility_cd || ' ' || frm.facility_nm  AS fromPoint       ");
        sql.append("          , dor.from_facility_id                       AS fromPointId     ");
        sql.append("          , tom.facility_cd || ' ' || tom.facility_nm  AS toPoint         ");
        sql.append("          , dor.to_facility_id                         AS toPointId       ");
        sql.append("          , dor.delivery_order_no                      AS duNo            ");
        sql.append("       FROM delivery_order_item doi                                       ");
        sql.append(" INNER JOIN delivery_order dor                                            ");
        sql.append("         ON doi.delivery_order_id = dor.delivery_order_id                 ");
        sql.append(" INNER JOIN mst_facility frm                                              ");
        sql.append("           ON frm.facility_id = dor.from_facility_id                      ");
        sql.append(" INNER JOIN mst_facility tom                                              ");
        sql.append("           ON tom.facility_id = dor.to_facility_id                        ");
        sql.append("     WHERE doi.delivery_order_id = :deliveryOrderId                   ");
        params.put("deliveryOrderId", deliveryOrderId);
        return super.queryForList(sql.toString(), params, SPM030603BO.class);
    }
}
