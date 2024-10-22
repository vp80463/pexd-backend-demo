package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.domain.bo.common.EInvoiceProductsBO;
import com.a1stream.domain.bo.parts.SPM020103PrintDetailBO;
import com.a1stream.domain.bo.parts.TargetSalesOrderItemBO;
import com.a1stream.domain.bo.service.PartDetailBO;
import com.a1stream.domain.bo.unit.SDM030103DetailBO;
import com.a1stream.domain.custom.SalesOrderItemRepositoryCustom;
import com.ymsl.solid.base.util.StringUtils;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class SalesOrderItemRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements SalesOrderItemRepositoryCustom {

    @Override
    public List<PartDetailBO> listServicePartByOrderId(Long serviceOrderId){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT soi.service_category_id    as serviceCategoryId          ");
        sql.append("      , soi.service_demand_id      as serviceDemandId            ");
        sql.append("      , soi.service_demand_content as serviceDemandContent       ");
        sql.append("      , soi.settle_type_id         as settleTypeId               ");
        sql.append("      , soi.symptom_id             as symptomId                  ");
        sql.append("      , soi.allocated_product_id   as partId                     ");
        sql.append("      , soi.allocated_product_cd   as partCd                     ");
        sql.append("      , soi.allocated_product_nm   as partNm                     ");
        sql.append("      , COALESCE (soi.standard_price, 0)     as standardPrice    ");
        sql.append("      , soi.discount_amt                    as discountAmt       ");
        sql.append("      , soi.discount_off_rate               as discount          ");
        sql.append("      , soi.special_price                   as specialPrice      ");
        sql.append("      , COALESCE(soi.selling_price, 0)      as sellingPrice      ");
        sql.append("      , COALESCE(soi.actual_qty, 0)         as qty               ");
        sql.append("      , COALESCE(soi.actual_amt, 0)         as amount            ");
        sql.append("      , COALESCE(onhand.quantity, 0)        as onHandQty         ");
        sql.append("      , COALESCE(soi.bo_qty, 0)             as boQty             ");
        sql.append("      , COALESCE(soi.shipment_qty, 0)       as shippedQty        ");
        sql.append("      , COALESCE(soi.allocated_qty, 0)      as allocatedQty      ");
        sql.append("      , soi.service_package_id     as servicePackageId           ");
        sql.append("      , soi.service_package_cd     as servicePackageCd           ");
        sql.append("      , soi.service_package_nm     as servicePackageNm           ");
        sql.append("      , soi.sales_order_item_id    as salesOrderItemId           ");
        sql.append("      , soi.tax_rate               as taxRate                    ");
        sql.append("      , soi.battery_flag           as batteryFlag                ");
        sql.append("      , soi.battery_type           as batteryType                ");
        sql.append("      , soi.seq_no                 as seqNo                      ");
        sql.append("      , soi.update_count           as updateCounter              ");
        sql.append("      , COALESCE(soi.selling_price_not_vat, 0) as sellingPriceNotVat ");
        sql.append("      , COALESCE(soi.actual_amt_not_vat, 0) as actualAmtNotVat   ");
        sql.append("   FROM service_order so                                         ");
        sql.append(" INNER JOIN sales_order_item soi                                 ");
        sql.append("         ON soi.sales_order_id = so.related_sales_order_id       ");
        sql.append("  LEFT JOIN product_stock_status onhand                          ");
        sql.append("         ON onhand.site_id = so.site_id                          ");
        sql.append("        AND onhand.facility_id = so.facility_id                  ");
        sql.append("        AND onhand.product_id = soi.allocated_product_id         ");
        sql.append("        AND onhand.product_stock_status_type = :onHandStatusType ");
        sql.append(" WHERE so.service_order_id = :serviceOrderId                     ");
        sql.append("   AND COALESCE(soi.actual_qty, 0) > 0                           ");
        sql.append(" ORDER BY soi.seq_no                                             ");

        params.put("onHandStatusType", SpStockStatus.ONHAND_QTY.getCodeDbid());
        params.put("serviceOrderId", serviceOrderId);

        return super.queryForList(sql.toString(), params, PartDetailBO.class);
    }

    @Override
    public List<SPM020103PrintDetailBO> getFastSalesOrderReportList(Long salesOrderId){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT soi.allocated_product_cd                     AS parts           ");
        sql.append("          , soi.product_cd                               AS partsNo         ");
        sql.append("          , soi.product_nm                               AS partsName       ");
        sql.append("          , soi.battery_id                               AS batteryId       ");
        sql.append("          , soi.discount_amt                             AS discountAmount  ");
        sql.append("          , so.order_date                                AS orderDate       ");
        sql.append("          , reason.code_data1                            AS cancelReason    ");
        sql.append("          , soi.tax_rate                                 AS productTax      ");
        sql.append("          , coalesce(soi.order_qty, 0)                   AS originalQty     ");
        sql.append("          , coalesce(soi.selling_price, 0)               AS sellingPrice    ");
        sql.append("          , coalesce(soi.special_price, 0)               AS specialPrice    ");
        sql.append("          , coalesce(soi.standard_price, 0)              AS standardPrice   ");
        sql.append("          , coalesce(soi.discount_off_rate, 0) || '%'    AS discountOffRate ");
        sql.append("          , coalesce(soi.allocated_qty, 0)               AS allocatedQty    ");
        sql.append("          , coalesce(soi.bo_qty, 0)                      AS boQty           ");
        sql.append("          , coalesce(soi.cancel_qty, 0)                  AS cancelQty       ");
        sql.append("          , round(soi.order_qty * soi.selling_price, 0)  AS amount          ");
        sql.append("       FROM sales_order_item soi                                            ");
        sql.append(" INNER JOIN sales_order so                                                  ");
        sql.append("         ON so.sales_order_id = soi.sales_order_id                          ");
        sql.append("  LEFT JOIN mst_code_info reason                                            ");
        sql.append("         ON reason.code_dbid = soi.cancel_reason_type                       ");
        sql.append("      WHERE soi.sales_order_id = :salesOrderId                              ");
        sql.append("   ORDER BY soi.seq_no                                                      ");

        params.put("salesOrderId", salesOrderId);
        return super.queryForList(sql.toString(), params, SPM020103PrintDetailBO.class);
    }

    @Override
    public List<TargetSalesOrderItemBO> getWaitingAllocateSoItemList(String siteId, Long deliveryPointId, Long productId){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT so.site_id                                   AS siteId             ");
        sql.append("          , so.facility_id                               AS deliveryPointId     ");
        sql.append("          , so.sales_order_id                            AS salesOrderId       ");
        sql.append("          , so.order_no                                  AS orderNo             ");
        sql.append("          , so.order_priority_type                       AS orderPriorityType   ");
        sql.append("          , soi.sales_order_item_id                      AS salesOrderItemId    ");
        sql.append("          , soi.seq_no                                   AS itemSeqNo           ");
        sql.append("          , soi.order_priority_seq                       AS prioritySeqNo       ");
        sql.append("          , so.allocate_due_date                         AS allocateDueDate     ");
        sql.append("          , soi.product_id                               AS productId           ");
        sql.append("          , soi.allocated_product_id                     AS allocateProdId      ");
        sql.append("          , soi.order_qty                                AS originalQty         ");
        sql.append("          , soi.waiting_allocate_qty                     AS waitingAllocateQty  ");
        sql.append("          , soi.bo_qty                                   AS boQty               ");
        sql.append("       FROM sales_order so                                                      ");
        sql.append(" INNER JOIN sales_order_item soi                                                ");
        sql.append("         ON so.sales_order_id = soi.sales_order_id                              ");
        sql.append("        AND so.site_id = soi.site_id                                            ");
        sql.append("      WHERE so.site_id = :siteId                                                ");
        sql.append("        AND so.facility_id = :deliveryPointId                                   ");
        sql.append("        AND soi.allocated_product_id = :productId                               ");
        sql.append("        AND so.order_status = 'S015SPWAITINGALLOCATE'                           ");
        sql.append("        AND (soi.bo_qty > 0 OR soi.waiting_allocate_qty > 0)                    ");
        sql.append("   ORDER BY soi.seq_no                                                          ");

        params.put("siteId", siteId);
        params.put("deliveryPointId", deliveryPointId);
        params.put("productId", productId);

        return super.queryForList(sql.toString(), params, TargetSalesOrderItemBO.class);
    }

    @Override
    public List<SDM030103DetailBO> getDealerWholeSODetails(Long orderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT soi.product_cd                    AS modelCd           ");
        sql.append("          , soi.product_nm                    AS modelNm           ");
        sql.append("          , mp.color_nm                       AS colorNm           ");
        sql.append("          , so.order_date                     AS deliveryDate      ");
        sql.append("          , sp.frame_no                       AS frameNo           ");
        sql.append("       FROM sales_order_item soi                                   ");
        sql.append(" LEFT JOIN sales_order so                                          ");
        sql.append("         ON soi.sales_order_id = so.sales_order_id                 ");
        sql.append(" LEFT JOIN mst_product mp                                          ");
        sql.append("         ON mp.product_id = soi.product_id                         ");
        sql.append(" LEFT JOIN order_serialized_item osi                               ");
        sql.append("         ON osi.order_item_id = soi.sales_order_item_id            ");
        sql.append(" LEFT JOIN serialized_product sp                                   ");
        sql.append("         ON osi.serialized_product_id = sp.serialized_product_id   ");
        sql.append("      WHERE soi.sales_order_id = :orderId                          ");
        sql.append("      ORDER BY soi.product_cd                                      ");

        params.put("orderId", orderId);

        return super.queryForList(sql.toString(), params, SDM030103DetailBO.class);
    }

    @Override
    public List<EInvoiceProductsBO> getServiceProductsModelsForParts(List<Long> productIds,String type, Long orderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("   SELECT product_cd AS code,                                                                                             ");
        sql.append("   product_nm AS prodName,                                                                                                ");
        sql.append("   (SELECT parameter_value                                                                                                ");
        sql.append("    FROM system_parameter sp                                                                                              ");
        sql.append("    WHERE system_parameter_type_id = 'S074EINVOICEPRODUNIT') AS prodUnit,                                                 ");
        sql.append("   (CASE                                                                                                                  ");
        sql.append("       WHEN (so.order_for_employee_flag = 'Y') THEN ROUND(soi.standard_price * 0.75, 0)                                   ");
        sql.append("       ELSE (                                                                                                             ");
        sql.append("           CASE                                                                                                           ");
        sql.append("               WHEN soi.special_price > 0 THEN ROUND(soi.special_price / soi.tax_rate, 0)                                 ");
        sql.append("               ELSE soi.standard_price                                                                                    ");
        sql.append("           END                                                                                                            ");
        sql.append("       )                                                                                                                  ");
        sql.append("   END) AS prodPrice,                                                                                                     ");
        sql.append("   ROUND(soi.actual_qty * soi.selling_price_not_vat, 0) AS amount,                                                        ");
        sql.append("   soi.actual_qty AS prodQuantity,                                                                                        ");
        sql.append("   ROUND(soi.discount_off_rate, 0) AS discount,                                                                           ");
        sql.append("   (CASE                                                                                                                  ");
        sql.append("       WHEN soi.discount_off_rate > 0 THEN 2                                                                              ");
        sql.append("       ELSE 0                                                                                                             ");
        sql.append("   END) AS isSum,                                                                                                         ");
        sql.append("   (CASE                                                                                                                  ");
        sql.append("       WHEN soi.discount_off_rate > 0 OR soi.discount_amt > 0 THEN ROUND(soi.discount_amt / (1 + soi.tax_rate / 100), 0)  ");
        sql.append("       ELSE 0                                                                                                             ");
        sql.append("   END) AS discountAmount                                                                                                 ");
        sql.append("   FROM sales_order_item soi                                                                                              ");
        sql.append("   LEFT JOIN service_order so                                                                                             ");
        sql.append("   ON so.related_sales_order_id = soi.sales_order_id                                                                      ");
        sql.append("   WHERE soi.sales_order_id = :orderId                                                                                    ");
        sql.append("     AND soi.product_id IN (:productIds)                                                                                  ");
        if(StringUtils.equals(type, CommonConstants.CHAR_ONE)){
            sql.append("     AND soi.settle_type_id = 'S013CUSTOMER'                                                                              ");
        }

        params.put("orderId", orderId);
        params.put("productIds",productIds);

        return super.queryForList(sql.toString(), params, EInvoiceProductsBO.class);
    }


}
