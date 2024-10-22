package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.PurchaseOrderStatus;
import com.a1stream.common.constants.PJConstants.FinishStatus;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.parts.SPM030102BO;
import com.a1stream.domain.bo.parts.SPM040401BO;
import com.a1stream.domain.bo.parts.SPQ040101BO;
import com.a1stream.domain.custom.PurchaseOrderRepositoryCustom;
import com.a1stream.domain.form.parts.SPM030102Form;
import com.a1stream.domain.form.parts.SPM040401Form;
import com.a1stream.domain.form.parts.SPM040402Form;
import com.a1stream.domain.form.parts.SPQ040101Form;
import com.ymsl.solid.base.util.CollectionUtils;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;

public class PurchaseOrderRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements PurchaseOrderRepositoryCustom {

    @Override
    public List<SPM040401BO> getPurchaseOrderList(SPM040401Form form, PJUserDetails uc) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT po.order_no                                   AS poNo                   ");
        sql.append("          , po.purchase_order_id                          AS purchaseOrderId        ");
        sql.append("          , po.supplier_order_no                          AS supplierOrderNo        ");
        sql.append("          , po.order_date                                 AS orderDate              ");
        sql.append("          , po.approve_date                               AS confirmDate            ");
        sql.append("          , po.order_status                               AS orderStatus            ");
        sql.append("          , po.order_priority_type                        AS orderType              ");
        sql.append("          , po.order_method_type                          AS method                 ");
        sql.append("          , po.supplier_cd                                AS supplierCd             ");
        sql.append("          , po.supplier_nm                                AS supplierNm             ");
        sql.append("          , po.facility_cd                                AS pointCd                ");
        sql.append("          , po.facility_nm                                AS pointNm                ");
        sql.append("          , po.deliver_plan_date                          AS deliveryPlanDate       ");
        sql.append("          , COUNT(poi.purchase_order_item_id)             AS orderLines             ");
        sql.append("          , SUM(poi.amount)                               AS amt                    ");
        sql.append("          , po.order_pic_nm                               AS orderPic               ");
        sql.append("          , po.memo                                       AS memo                   ");
        sql.append("          , CONCAT(po.supplier_cd, ' ', po.supplier_nm)   AS supplier               ");
        sql.append("          , CONCAT(po.facility_cd, ' ', po.facility_nm)   AS point                  ");
        sql.append("          , po.update_count                               AS updateCount            ");
        sql.append("       FROM purchase_order po                                                       ");
        sql.append("  LEFT JOIN purchase_order_item poi ON po.purchase_order_id = poi.purchase_order_id ");
        sql.append("      WHERE po.site_id = :siteId                                                    ");
        sql.append("        AND po.product_classification = :productClassification                      ");
        sql.append("        AND po.order_date >= :dateFrom                                              ");
        sql.append("        AND po.order_date <= :dateTo                                                ");

        params.put("siteId", uc.getDealerCode());
        params.put("productClassification", ProductClsType.PART.getCodeDbid());
        params.put("dateFrom", form.getDateFrom());
        params.put("dateTo", form.getDateTo());

        if (StringUtils.isNotBlank(form.getPoNo())) {
            sql.append(" AND po.order_no = :poNo ");
            params.put("poNo", form.getPoNo());
        }

        if (CollectionUtils.isNotEmpty(form.getStatus())) {
            sql.append(" AND po.order_status IN (:status) ");
            params.put("status", form.getStatus());
        }

        if (CollectionUtils.isNotEmpty(form.getType())) {
            sql.append(" AND po.order_priority_type IN (:type) ");
            params.put("type", form.getType());
        }

        if (CollectionUtils.isNotEmpty(form.getMethod())) {
            sql.append(" AND po.order_method_type IN (:method) ");
            params.put("method", form.getMethod());
        }

        if (StringUtils.isNotBlank(form.getSalesOrderNo())) {
            sql.append(" AND po.sales_order_no LIKE :salesOrderNo ");
            params.put("salesOrderNo", CommonConstants.CHAR_PERCENT + form.getSalesOrderNo() + CommonConstants.CHAR_PERCENT);
        }

        if (!ObjectUtils.isEmpty(form.getOrderPicId())) {
            sql.append(" AND po.order_pic_id = :orderPicId ");
            params.put("orderPicId", form.getOrderPicId());
        }

        if (!ObjectUtils.isEmpty(form.getPointId())) {
            sql.append(" AND po.facility_id = :pointId ");
            params.put("pointId", form.getPointId());
        }

        if (StringUtils.isNotBlank(form.getSupplierOrderNo())) {
            sql.append(" AND po.supplier_order_no LIKE :supplierOrderNo ");
            params.put("supplierOrderNo", CommonConstants.CHAR_PERCENT + form.getSupplierOrderNo() + CommonConstants.CHAR_PERCENT);
        }

        if (!ObjectUtils.isEmpty(form.getSupplierId())) {
            sql.append(" AND po.supplier_id = :supplierId ");
            params.put("supplierId", form.getSupplierId());
        }

        sql.append(" GROUP BY po.purchase_order_id ");

        return super.queryForList(sql.toString(), params, SPM040401BO.class);
    }

    @Override
    public PageImpl<SPQ040101BO> getPurchaseOrderList(SPQ040101Form model,String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        StringBuilder selSql = new StringBuilder();

        selSql.append(" SELECT po.order_no            as orderNo                    ");
        selSql.append("      , po.purchase_order_id   as purchaseOrderId            ");
        selSql.append("      , po.order_priority_type as orderType                  ");
        selSql.append("      , po.order_method_type   as method                     ");
        selSql.append("      , po.sales_order_no      as salesOrderNo               ");
        selSql.append("      , mf.facility_cd || ' ' || mf.facility_nm as pointCd   ");
        selSql.append("      , po.order_date          as orderDate                  ");
        selSql.append("      , poi.product_cd         as partsNo                    ");
        selSql.append("      , poi.product_nm         as partsNm                    ");
        selSql.append("      , poi.product_id         as partsId                    ");
        selSql.append("      , poi.order_qty          as orderQty                   ");
        selSql.append("      , poi.purchase_price     as purchasePrice              ");
        selSql.append("      , poi.amount             as orderAmount                ");
        selSql.append("      , poi.on_purchase_qty    as onPurchaseQty              ");
        selSql.append("      , poi.receive_qty        as receiptQty                 ");
        selSql.append("      , poi.stored_qty         as registerQty                ");
        selSql.append("      , poi.cancelled_qty      as cancelQty                  ");

        sql.append("   FROM purchase_order po                            ");
        sql.append("   LEFT JOIN purchase_order_item poi                 ");
        sql.append("     ON poi.purchase_order_id = po.purchase_order_id ");
        sql.append("   LEFT JOIN mst_facility mf                         ");
        sql.append("     ON po.facility_id = mf.facility_id              ");
        sql.append("  WHERE po.site_id = :siteId                         ");
        params.put("siteId",siteId);

        if (!ObjectUtils.isEmpty(model.getPointId())) {
            sql.append(" AND po.facility_id = :pointId ");
            params.put("pointId", model.getPointId());
        }

        if (StringUtils.isNotBlank(model.getDateFrom())) {
            sql.append(" AND po.order_date >= :dateFrom ");
            params.put("dateFrom", model.getDateFrom());
        }

        if (StringUtils.isNotBlank(model.getDateTo())) {
            sql.append(" AND po.order_date <= :dateTo ");
            params.put("dateTo", model.getDateTo());
        }

        if (StringUtils.isNotBlank(model.getOrderNo())) {
            sql.append(" AND po.order_no = :orderNo ");
            params.put("orderNo", model.getOrderNo());
        }

        if (StringUtils.isNotBlank(model.getSalesOrderNo())) {
            sql.append(" AND po.sales_order_no = :salesOrderNo ");
            params.put("salesOrderNo", model.getSalesOrderNo());
        }

        if (!ObjectUtils.isEmpty(model.getPartId())) {
            sql.append(" AND poi.product_id = :partId ");
            params.put("partId", model.getPartId());
        }

        if (model.getOrderStatus() != null && !model.getOrderStatus().isEmpty()) {
            sql.append(" AND po.order_status in (:orderStatus) ");
            params.put("orderStatus", model.getOrderStatus());
        }

        if (StringUtils.equals(model.getUnfinishOnlySign(), CommonConstants.CHAR_Y)) {
            sql.append(" AND po.order_status in "
                    + "(select code_dbid "
                    + "from mst_code_info mci where code_id = :codeId "
                    + " and code_data2 = :codeData2 "
                    + " and code_data3 = :codeData3 )");
            params.put("codeId", PurchaseOrderStatus.CODE_ID);
            params.put("codeData2", ProductClsType.PART.getCodeDbid());
            params.put("codeData3", FinishStatus.UN_FINISHED.getCodeDbid());
        }

        sql.append(" ORDER BY po.order_no ");

        String countSql = "SELECT COUNT(1) FROM ( " + "SELECT * " + sql.toString() + ") AS subquery; ";
        Pageable pageable = PageRequest.of(model.getCurrentPage() - 1, model.getPageSize());
        if (!model.isPageFlg()) {
            pageable = Pageable.unpaged();
        }
        Integer count = super.queryForSingle(countSql, params, Integer.class);
        params.putAll(params);

        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, SPQ040101BO.class, pageable), pageable, count);
    }

    @Override
    public SPM040401BO getPurchaseOrder(SPM040402Form form, PJUserDetails uc) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT po.order_no                                   AS poNo             ");
        sql.append("          , po.purchase_order_id                          AS purchaseOrderId  ");
        sql.append("          , po.supplier_order_no                          AS supplierOrderNo  ");
        sql.append("          , po.order_date                                 AS orderDate        ");
        sql.append("          , po.approve_date                               AS confirmDate      ");
        sql.append("          , po.order_status                               AS orderStatus      ");
        sql.append("          , po.order_priority_type                        AS orderType        ");
        sql.append("          , po.order_method_type                          AS method           ");
        sql.append("          , po.supplier_cd                                AS supplierCd       ");
        sql.append("          , po.supplier_nm                                AS supplierNm       ");
        sql.append("          , po.facility_cd                                AS pointCd          ");
        sql.append("          , po.facility_nm                                AS pointNm          ");
        sql.append("          , po.deliver_plan_date                          AS deliveryPlanDate ");
        sql.append("          , CONCAT(po.order_pic_id, ' ', po.order_pic_nm) AS orderPic         ");
        sql.append("          , po.memo                                       AS memo             ");
        sql.append("          , CONCAT(po.supplier_cd, ' ', po.supplier_nm)   AS supplier         ");
        sql.append("          , CONCAT(po.facility_cd, ' ', po.facility_nm)   AS point            ");
        sql.append("          , po.update_count                               AS updateCount      ");
        sql.append("       FROM purchase_order po                                                 ");
        sql.append("      WHERE po.site_id = :siteId                                              ");
        sql.append("        AND po.product_classification = :productClassification                ");
        sql.append("        AND po.purchase_order_id = :purchaseOrderId                           ");

        params.put("siteId", uc.getDealerCode());
        params.put("productClassification", ProductClsType.PART.getCodeDbid());
        params.put("purchaseOrderId", form.getPurchaseOrderId());

        return super.queryForSingle(sql.toString(), params, SPM040401BO.class);
    }

    @Override
    public Integer getPurchaseOrderSize(String siteId, String orderStatus) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT count(1)                    ");
        sql.append("   FROM purchase_order              ");
        sql.append("  WHERE site_id = :siteId           ");
        sql.append("    AND order_status = :orderStatus ");

        params.put("siteId", siteId);
        params.put("orderStatus", orderStatus);

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }

    @Override
    public SPM030102BO getPurchaseOrderData(SPM030102Form form, PJUserDetails uc) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT po.purchase_order_id             AS purchaseOrderId ");
        sql.append("          , COALESCE(poi.on_purchase_qty, 0) AS onPurchaseQty   ");
        sql.append("       FROM purchase_order po                                   ");
        sql.append("          , purchase_order_item poi                             ");
        sql.append("      WHERE po.site_id = :siteId                                ");
        sql.append("        AND po.product_classification = :productClassification  ");
        sql.append("        AND po.facility_id = :facilityId                        ");
        sql.append("        AND po.order_no = :orderNo                              ");
        sql.append("        AND po.purchase_order_id = poi.purchase_order_id        ");
        sql.append("        AND poi.product_id = :productId                         ");

        params.put("siteId", uc.getDealerCode());
        params.put("productClassification", ProductClsType.PART.getCodeDbid());
        params.put("facilityId", form.getPointId());
        params.put("orderNo", form.getOrderNo());
        params.put("productId", form.getOrderPartsId());

        return super.queryForSingle(sql.toString(), params, SPM030102BO.class);
    }
}
