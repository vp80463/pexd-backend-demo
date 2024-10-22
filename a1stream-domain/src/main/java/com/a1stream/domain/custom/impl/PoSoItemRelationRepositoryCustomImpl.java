package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.custom.PoSoItemRelationRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class PoSoItemRelationRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements PoSoItemRelationRepositoryCustom {

    /**
     * Service BO part自动生成采购单用
     * author: He Xiaochuan
     */
    @Override
    public List<Map> getPoQtyBySalesOrderItem(List<Long> salesOrderItemIds) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT psir.sales_order_item_id  as salesorderitemid              ");
        sql.append("         , sum(COALESCE(poi.on_purchase_qty, 0) + COALESCE(poi.receive_qty, 0)) as sumqty ");
        sql.append("      FROM po_so_item_relation psir                                   ");
        sql.append(" LEFT JOIN purchase_order_item poi                                    ");
        sql.append("        ON poi.purchase_order_item_id = psir.purchase_order_item_id   ");
        sql.append("     WHERE psir.sales_order_item_id IN (:salesOrderItemIds)           ");
        sql.append("  GROUP BY psir.sales_order_item_id                                   ");

        params.put("salesOrderItemIds", salesOrderItemIds);

        return super.queryForList(sql.toString(), params, Map.class);
    }
}
