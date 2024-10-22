package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants.MCSalesType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.common.EInvoiceInvoiceBO;
import com.a1stream.domain.bo.common.EInvoiceProductsBO;
import com.a1stream.domain.bo.parts.DIM020401BO;
import com.a1stream.domain.bo.parts.SPM020101BO;
import com.a1stream.domain.bo.parts.SPM020103PrintBO;
import com.a1stream.domain.bo.parts.SPM021301BO;
import com.a1stream.domain.bo.parts.SPQ020101BO;
import com.a1stream.domain.bo.parts.SPQ020201BO;
import com.a1stream.domain.bo.service.SVM0102PrintServicePartBO;
import com.a1stream.domain.bo.unit.SDM030101BO;
import com.a1stream.domain.bo.unit.SDM030103BO;
import com.a1stream.domain.custom.SalesOrderRepositoryCustom;
import com.a1stream.domain.form.parts.SPM020101Form;
import com.a1stream.domain.form.parts.SPM021301Form;
import com.a1stream.domain.form.parts.SPQ020101Form;
import com.a1stream.domain.form.parts.SPQ020201Form;
import com.a1stream.domain.form.unit.SDM030101Form;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;

/**
 * @author dong zhen
 */
public class SalesOrderRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements SalesOrderRepositoryCustom {

    @Override
    public Integer getSalesOrderCountForSp(String siteId, List<String> orderStatusList) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT count(1)                             ");
        sql.append("   FROM sales_order                          ");
        sql.append("  WHERE site_id = :siteId                    ");
        sql.append("    AND order_status IN ( :orderStatusList ) ");

        params.put("siteId", siteId);
        params.put("orderStatusList", orderStatusList);

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }

    @Override
    public Integer getSalesOrderCountForSd(String siteId, String orderStatus) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT count(1)                    ");
        sql.append("   FROM sales_order                 ");
        sql.append("  WHERE site_id = :siteId           ");
        sql.append("    AND order_status = :orderStatus ");

        params.put("siteId", siteId);
        params.put("orderStatus", orderStatus);

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }

    @Override
    public SPM020103PrintBO getFastSalesOrderReportData(Long salesOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT so.order_no                                            AS orderNo            ");
        sql.append("          , mo.organization_nm                                     AS consumerName       ");
        sql.append("          , so.order_date                                          AS orderDate          ");
        sql.append("          , so.sales_order_id                                      AS orderId            ");
        sql.append("          , mf.facility_cd                                         AS orderPointCd       ");
        sql.append("          , mf.facility_nm                                         AS orderPointNm       ");
        sql.append("          , mf.multi_address_flag                                  AS multiAddressFlag   ");
        sql.append("          , so.comment                                             AS memo               ");
        sql.append("          , COALESCE(mf.address1, '') || COALESCE(mf.address2, '') AS companyAddress     ");
        sql.append("          , ordertype.code_data1                                   AS orderType          ");
        sql.append("          , so.mobile_phone                                        AS mobilePhone        ");
        sql.append("          , mf.contact_tel                                         AS phoneNo            ");
        sql.append("          , so.facility_multi_addr                                 AS pointAddress       ");
        sql.append("          , so.deposit_amt                                         AS depositAmount      ");
        sql.append("          , so.sales_pic_nm                                        AS salesPicNm         ");
        sql.append("          , so.entry_pic_nm                                        AS entryPicNm        ");
        sql.append("       FROM sales_order so                                                               ");
        sql.append(" INNER JOIN mst_organization mo                                                          ");
        sql.append("         ON so.customer_id = mo.organization_id                                          ");
        sql.append(" INNER JOIN mst_facility mf                                                              ");
        sql.append("         ON so.facility_id = mf.facility_id                                              ");
        sql.append("  LEFT JOIN mst_code_info ordertype                                                      ");
        sql.append("         ON ordertype.code_dbid = so.order_priority_type                                 ");
        sql.append("      WHERE so.sales_order_id = :salesOrderId                                            ");
        sql.append("      LIMIT 1                                                                            ");

        params.put("salesOrderId", salesOrderId);
        return super.queryForSingle(sql.toString(), params, SPM020103PrintBO.class);
    }

    @Override
    public PageImpl<SPM020101BO> searchSalesOrderList(SPM020101Form model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        StringBuilder selSql = new StringBuilder();

        selSql.append(" SELECT so.order_no          AS orderNo                    ");
        selSql.append("      , so.order_status      AS orderStatus                ");
        selSql.append("      , mci.code_data1       AS status                     ");
        selSql.append("      , so.sales_order_id    AS salesOrderId               ");
        selSql.append("      , so.consumer_nm_full  AS consumer                   ");
        selSql.append("      , so.comment           AS memo                       ");
        selSql.append("      , mf.facility_nm       AS shop                       ");
        selSql.append("      , so.order_date        AS orderDate                  ");
        selSql.append("      , so.allocate_due_date AS dueDate                    ");

        sql.append("      FROM sales_order so                            ");
        sql.append(" LEFT JOIN mst_facility mf                           ");
        sql.append("        ON so.facility_id = mf.facility_id           ");
        sql.append(" LEFT JOIN mst_code_info mci                         ");
        sql.append("        ON so.order_status = mci.code_dbid           ");
        sql.append("       AND mci.code_id = :codeId                     ");
        sql.append("  WHERE so.site_id = :siteId                         ");
        sql.append("   AND  so.order_source_type = :type                 ");
        params.put("siteId",model.getSiteId());
        params.put("type", ProductClsType.PART.getCodeDbid());
        params.put("codeId", MstCodeConstants.SalesOrderStatus.CODE_ID);
        if (!ObjectUtils.isEmpty(model.getDeliveryPointId())) {
            sql.append(" AND so.facility_id = :deliveryPointId ");
            params.put("deliveryPointId", model.getDeliveryPointId());
        }

        if (!ObjectUtils.isEmpty(model.getPointId())) {
            sql.append(" AND so.entry_facility_id = :pointId ");
            params.put("pointId", model.getPointId());
        }

        if (StringUtils.isNotBlank(model.getDateFrom())) {
            sql.append(" AND so.order_date >= :dateFrom ");
            params.put("dateFrom", model.getDateFrom());
        }

        if (StringUtils.isNotBlank(model.getDateTo())) {
            sql.append(" AND so.order_date <= :dateTo ");
            params.put("dateTo", model.getDateTo());
        }


        if (StringUtils.isNotBlank(model.getDateStart())) {
            sql.append(" AND so.allocate_due_date >= :dateStart ");
            params.put("dateStart", model.getDateStart());
        }

        if (StringUtils.isNotBlank(model.getDateEnd())) {
            sql.append(" AND so.allocate_due_date <= :dateEnd ");
            params.put("dateEnd", model.getDateEnd());
        }

        if (StringUtils.isNotBlank(model.getOrderNo())) {
            sql.append(" AND so.order_no = :orderNo ");
            params.put("orderNo", model.getOrderNo());
        }

        if (model.getStatus() != null && !model.getStatus().isEmpty()) {
            sql.append(" AND so.order_status in (:orderStatus) ");
            params.put("orderStatus", model.getStatus());
        }

        if (!ObjectUtils.isEmpty(model.getCustomerId())) {
            sql.append(" AND so.customer_id = :customerId ");
            params.put("customerId", model.getCustomerId());
        }

        sql.append(" ORDER BY so.order_no ");

        String countSql = "SELECT COUNT(1) FROM ( " + "SELECT * " + sql.toString() + ") AS subquery; ";
        Pageable pageable = PageRequest.of(model.getCurrentPage() - 1, model.getPageSize());
        if (!model.isPageFlg()) {
            pageable = Pageable.unpaged();
        }
        Integer count = super.queryForSingle(countSql, params, Integer.class);
        params.putAll(params);

        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, SPM020101BO.class, pageable), pageable, count);
    }

    /**
    * 功能描述:DIM0204画面获取下载所需数据
    *
    *@author mid2178
    */
    @Override
    public List<DIM020401BO> getSparePartsDownloadInfo(Long facilityId, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT so.site_id                               AS dealerCode             ");
        sql.append("           ,mf.facility_cd || '__' || mf.facility_nm AS pointCode              ");
        sql.append("           ,so.order_no                              AS orderNo                ");
        sql.append("           ,so.order_date                            AS orderDate              ");
        sql.append("           ,so.order_status                          AS orderStatus            ");
        sql.append("           ,mp.product_cd || '__' || mp.sales_description AS orderParts        ");
        sql.append("           ,mp2.product_cd || '__' || mp2.sales_description AS allocatedParts  ");
        sql.append("           ,soi.order_qty                            AS orderQty               ");
        sql.append("           ,soi.allocated_qty                        AS allocateQty            ");
        sql.append("           ,soi.bo_qty                               AS boQty                  ");
        sql.append("           ,so.sales_order_id                        AS salesOrderId           ");
        sql.append("       FROM sales_order so                                                     ");
        sql.append("  LEFT JOIN mst_facility mf                                                    ");
        sql.append("         ON mf.site_id = so.site_id                                            ");
        sql.append("        AND mf.facility_id = so.facility_id                                    ");
        sql.append(" INNER JOIN sales_order_item soi                                               ");
        sql.append("         ON so.site_id = soi.site_id                                           ");
        sql.append("        AND so.sales_order_id = soi.sales_order_id                             ");
        sql.append("  LEFT JOIN mst_product mp                                                     ");
        sql.append("         ON mp.product_classification = :part                                  ");
        sql.append("        AND mp.product_id = soi.product_id                                     ");
        sql.append("  LEFT JOIN mst_product mp2                                                    ");
        sql.append("         ON mp2.product_classification = :part                                 ");
        sql.append("        AND mp2.product_id = soi.allocated_product_id                          ");
        sql.append("      WHERE so.site_id = :siteId                                               ");
        sql.append("        AND so.facility_id = :ucFacilityId                                     ");
        sql.append("        AND so.product_classification = :part                                  ");
        sql.append("        AND so.order_status not in (:cancelled,:shipmented )                   ");
        sql.append("        AND so.order_source_type = :part                                       ");
        sql.append("   ORDER BY so.order_no                                                        ");

        params.put("siteId", siteId);
        params.put("ucFacilityId", facilityId);
        params.put("part", ProductClsType.PART.getCodeDbid());
        params.put("cancelled", MstCodeConstants.SalesOrderStatus.SP_CANCELLED);
        params.put("shipmented", MstCodeConstants.SalesOrderStatus.SP_SHIPMENTED);

        return super.queryForList(sql.toString(), params, DIM020401BO.class);
    }

    /**
    * 功能描述:DIM0204画面获取下载所需数据
    *
    *@author mid2178
    */
    @Override
    public List<DIM020401BO> getServiceDownloadInfo(Long facilityId, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT so.site_id                               AS dealerCode               ");
        sql.append("           ,mf.facility_cd || '__' || mf.facility_nm AS pointCode                ");
        sql.append("           ,so.order_no                              AS orderNo                  ");
        sql.append("           ,mci.code_data1                           AS serviceCategory          ");
        sql.append("           ,so.order_date                            AS orderDate                ");
        sql.append("           ,so.order_status                          AS orderStatus              ");
        sql.append("           ,mp.product_cd || '__' || mp.sales_description AS orderParts          ");
        sql.append("           ,mp2.product_cd || '__' || mp2.sales_description AS allocatedParts    ");
        sql.append("           ,soi.order_qty                            AS orderQty                 ");
        sql.append("           ,soi.allocated_qty                        AS allocateQty              ");
        sql.append("           ,soi.bo_qty                               AS boQty                    ");
        sql.append("           ,so.sales_order_id                        AS salesOrderId             ");
        sql.append("       FROM sales_order so                                                       ");
        sql.append("  LEFT JOIN mst_facility mf                                                      ");
        sql.append("         ON mf.site_id = so.site_id                                              ");
        sql.append("        AND mf.facility_id = so.facility_id                                      ");
        sql.append(" INNER JOIN sales_order_item soi                                                 ");
        sql.append("         ON so.site_id = soi.site_id                                             ");
        sql.append("        AND so.sales_order_id = soi.sales_order_id                               ");
        sql.append("  LEFT JOIN mst_product mp                                                       ");
        sql.append("         ON mp.product_classification = :part                                    ");
        sql.append("        AND mp.product_id = soi.product_id                                       ");
        sql.append("  LEFT JOIN mst_product mp2                                                      ");
        sql.append("         ON mp2.product_classification = :part                                   ");
        sql.append("        AND mp2.product_id = soi.allocated_product_id                            ");
        sql.append("  LEFT JOIN mst_code_info mci                                                    ");
        sql.append("         ON mci.site_id = :comSiteId                                             ");
        sql.append("        AND mci.code_dbid = soi.service_category_id                              ");
        sql.append("      WHERE so.site_id = :siteId                                                 ");
        sql.append("        AND so.facility_id = :ucFacilityId                                       ");
        sql.append("        AND so.product_classification = :part                                    ");
        sql.append("        AND so.order_status not in (:cancelled,:shipmented)                      ");
        sql.append("        AND so.order_source_type = :orderSourceType                              ");
        sql.append("   ORDER BY so.order_no                                                          ");

        params.put("siteId", siteId);
        params.put("comSiteId", CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("ucFacilityId", facilityId);
        params.put("part", ProductClsType.PART.getCodeDbid());
        params.put("orderSourceType", ProductClsType.SERVICE.getCodeDbid());
        params.put("cancelled", MstCodeConstants.SalesOrderStatus.SP_CANCELLED);
        params.put("shipmented", MstCodeConstants.SalesOrderStatus.SP_SHIPMENTED);

        return super.queryForList(sql.toString(), params, DIM020401BO.class);
    }

    /**
    * 功能描述:DIM0204画面获取下载所需数据
    *
    *@author mid2178
    */
    @Override
    public List<DIM020401BO> getPartsStoringDownloadInfo(Long facilityId, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT rs.site_id                       AS dealerCode       ");
        sql.append("           ,mf.facility_cd                   AS pointCode        ");
        sql.append("           ,rs.slip_no                       AS receiptNo      ");
        sql.append("           ,rs.received_date                 AS receivedDate   ");
        sql.append("           ,rsi.product_cd || rsi.product_nm AS orderParts     ");
        sql.append("           ,rsi.receipt_qty                  AS receiptQty     ");
        sql.append("           ,sl.stored_qty                    AS storedQty      ");
        sql.append("           ,sl.storing_line_id               AS storingLineId  ");
        sql.append("       FROM receipt_slip rs                                    ");
        sql.append(" INNER JOIN receipt_slip_item rsi                              ");
        sql.append("         ON rs.site_id = rsi.site_id                           ");
        sql.append("        AND rs.receipt_slip_id = rsi.receipt_slip_id           ");
        sql.append(" INNER JOIN storing_line sl                                    ");
        sql.append("         ON sl.site_id = rsi.site_id                           ");
        sql.append("        AND sl.receipt_slip_item_id = rsi.receipt_slip_item_id ");
        sql.append("  LEFT JOIN mst_facility mf                                    ");
        sql.append("         ON mf.site_id = rs.site_id                            ");
        sql.append("        AND mf.facility_id = rs.received_facility_id           ");
        sql.append("      WHERE rs.site_id = :siteId                               ");
        sql.append("        AND rs.received_facility_id = :ucFacilityId            ");
        sql.append("        AND rs.product_classification = :part                  ");
        sql.append("        AND sl.stored_qty = 0                                  ");

        params.put("siteId", siteId);
        params.put("ucFacilityId", facilityId);
        params.put("part", ProductClsType.PART.getCodeDbid());

        return super.queryForList(sql.toString(), params, DIM020401BO.class);
    }

    /**
    * 功能描述:如果count有数据，则提示check信息
    *
    *@author mid2178
    */
    @Override
    public Integer countSalesOrder(Long facilityId, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     select count(1)                                              ");
        sql.append("       from sales_order so                                        ");
        sql.append(" inner join sales_order_item soi                                  ");
        sql.append("         on so.site_id = soi.site_id                              ");
        sql.append("        and so.sales_order_id = soi.sales_order_id                ");
        sql.append("      where so.site_id = :siteId                                  ");
        sql.append("        and so.facility_id = :facilityId                        ");
        sql.append("        and so.product_classification = :productClassification    ");
        sql.append("        and so.order_status not in (:cancelled,:shipmented)       ");
        sql.append("        and so.order_source_type = :productClassification         ");

        params.put("siteId", siteId);
        params.put("facilityId", facilityId);
        params.put("productClassification", ProductClsType.PART.getCodeDbid());
        params.put("cancelled", MstCodeConstants.SalesOrderStatus.SP_CANCELLED);
        params.put("shipmented", MstCodeConstants.SalesOrderStatus.SP_SHIPMENTED);

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }

    /**
    * 功能描述:如果count有数据，则提示check信息
    *
    *@author mid2178
    */
    @Override
    public Integer countServiceOrder(Long facilityId, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" select count(1)                                               ");
        sql.append("       from sales_order so                                     ");
        sql.append(" inner join sales_order_item soi                               ");
        sql.append("         on so.site_id = soi.site_id                           ");
        sql.append("        and so.sales_order_id = soi.sales_order_id             ");
        sql.append("      where so.site_id = :siteId                               ");
        sql.append("        and so.facility_id = :facilityId                     ");
        sql.append("        and so.product_classification = :productClassification ");
        sql.append("        and so.order_status not in (:cancelled,:shipmented)    ");
        sql.append("        and so.order_source_type = :orderSourceType            ");

        params.put("siteId", siteId);
        params.put("facilityId", facilityId);
        params.put("productClassification", ProductClsType.PART.getCodeDbid());
        params.put("cancelled", MstCodeConstants.SalesOrderStatus.SP_CANCELLED);
        params.put("shipmented", MstCodeConstants.SalesOrderStatus.SP_SHIPMENTED);
        params.put("orderSourceType", ProductClsType.SERVICE.getCodeDbid());

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }

    @Override
    public Page<SPQ020101BO> findSalesOrderCustomerList(SPQ020101Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT so.sales_order_id                         AS salesOrderId      ");
        sql.append("          , so.related_sv_order_id                    AS relatedSvOrderId  ");
        sql.append("          , so.customer_cd                            AS customerCd        ");
        sql.append("          , so.customer_nm                            AS customerNm        ");
        sql.append("          , so.customer_id                            AS customerId        ");
        sql.append("          , so.facility_id                            AS facilityId        ");
        sql.append("          , mf.facility_cd                            AS pointCd           ");
        sql.append("          , mf.facility_cd || ' ' || mf.facility_nm   AS facilityAbbr      ");
        sql.append("          , so.order_source_type                      AS sourceTypeCode    ");
        sql.append("          , so.order_date                             AS orderDate         ");
        sql.append("          , so.order_no                               AS orderNo           ");
        sql.append("          , COUNT(CASE WHEN soi.actual_qty > 0 AND soi.bo_qty = 0          ");
        sql.append("                 THEN soi.sales_order_item_id END) AS allocatedLines       ");
        sql.append("          , COUNT(CASE WHEN soi.actual_qty > 0                             ");
        sql.append("                  THEN soi.sales_order_item_id END) AS orderLines          ");
        sql.append("          , COALESCE(SUM(CASE WHEN soi.actual_qty > 0                      ");
        sql.append("                THEN soi.actual_qty * ROUND(soi.selling_price, 0) END), 0) AS orderAmt   ");
        sql.append("       FROM sales_order so                                                 ");
        sql.append("  LEFT JOIN mst_facility mf                                                ");
        sql.append("         ON mf.facility_id = so.facility_id                                ");
        sql.append("  LEFT JOIN sales_order_item soi                                           ");
        sql.append("         ON so.sales_order_id = soi.sales_order_id                         ");
        sql.append("        AND so.site_id = soi.site_id                                       ");
        sql.append("      WHERE so.site_id = :siteId                                           ");
        sql.append("        AND so.order_date >= :dateFrom                                     ");
        sql.append("        AND so.order_date <= :dateTo                                       ");

        params.put("siteId",siteId);
        params.put("dateFrom",form.getDateFrom());
        params.put("dateTo",form.getDateTo());

        if (!Nulls.isNull(form.getPointId())) {
            sql.append("    AND so.facility_id = :facilityId ");
            params.put("facilityId",form.getPointId());
        }

        if (!Nulls.isNull(form.getCustomerId())) {
            sql.append("    AND so.customer_id = :customerId ");
            params.put("customerId",form.getCustomerId());
        }

        if (StringUtils.isNotBlank(form.getOrderNo())) {
            sql.append("    AND so.order_no = :orderNo       ");
            params.put("orderNo",form.getOrderNo());
        }

        if (StringUtils.isNotBlank(form.getOrderStatus())) {
            sql.append("    AND so.order_status = :orderStatusId ");
            params.put("orderStatusId",form.getOrderStatus());
        }

        if(StringUtils.isNotBlank(form.getOrderSourceType())) {
            sql.append("    AND so.order_source_type = :orderStatusType ");
            params.put("orderStatusType",form.getOrderSourceType());
        }else {
            sql.append("    AND so.order_source_type IN (:PART, :SERVICE) ");
            params.put("PART",ProductClsType.PART.getCodeDbid());
            params.put("SERVICE",ProductClsType.SERVICE.getCodeDbid());
        }

        sql.append("   GROUP BY so.sales_order_id, so.related_sv_order_id, so.customer_cd, so.customer_nm   ");
        sql.append("          , so.customer_id, so.facility_id, mf.facility_cd, mf.facility_nm              ");
        sql.append("          , so.order_source_type, so.order_date, so.order_no                            ");
        sql.append("   ORDER BY so.order_no                                                                 ");

        Integer count = this.getCountForSalesOrderCustomer(form,siteId);
        Pageable pageable = PageRequest.of(form.getCurrentPage() - 1, form.getPageSize());
        return new PageImpl<>(super.queryForPagingList(sql.toString(), params, SPQ020101BO.class, pageable), pageable, count);
    }

    private Integer getCountForSalesOrderCustomer(SPQ020101Form form, String siteId) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT COUNT(*) FROM(                                                             ");
        sql.append("     SELECT so.sales_order_id                                AS salesOrderId      ");
        sql.append("          , COUNT(CASE WHEN soi.actual_qty > 0 AND soi.bo_qty = 0                 ");
        sql.append("                 THEN soi.sales_order_item_id END)           AS allocatedLines    ");
        sql.append("          , COUNT(CASE WHEN soi.actual_qty > 0                                    ");
        sql.append("                  THEN soi.sales_order_item_id END)          AS orderLines        ");
        sql.append("          , COALESCE(SUM(CASE WHEN soi.actual_qty > 0                             ");
        sql.append("                THEN soi.actual_qty * ROUND(soi.selling_price, 0) END), 0) AS orderAmt   ");
        sql.append("       FROM sales_order so                                                        ");
        sql.append("  LEFT JOIN mst_facility mf                                                       ");
        sql.append("         ON mf.facility_id = so.facility_id                                       ");
        sql.append("  LEFT JOIN sales_order_item soi                                                  ");
        sql.append("         ON so.sales_order_id = soi.sales_order_id                                ");
        sql.append("        AND so.site_id = soi.site_id                                              ");
        sql.append("      WHERE so.site_id = :siteId                                                  ");
        sql.append("        AND so.order_date >= :dateFrom                                            ");
        sql.append("        AND so.order_date <= :dateTo                                              ");

        params.put("siteId",siteId);
        params.put("dateFrom",form.getDateFrom());
        params.put("dateTo",form.getDateTo());

        if (!Nulls.isNull(form.getPointId())) {
            sql.append("    AND so.facility_id = :facilityId ");
            params.put("facilityId",form.getPointId());
        }

        if (!Nulls.isNull(form.getCustomerId())) {
            sql.append("    AND so.customer_id = :customerId ");
            params.put("customerId",form.getCustomerId());
        }

        if (StringUtils.isNotBlank(form.getOrderNo())) {
            sql.append("    AND so.order_no = :orderNo       ");
            params.put("orderNo",form.getOrderNo());
        }

        if (StringUtils.isNotBlank(form.getOrderStatus())) {
            sql.append("    AND so.order_status = :orderStatusId ");
            params.put("orderStatusId",form.getOrderStatus());
        }

        if(StringUtils.isNotBlank(form.getOrderSourceType())) {
            sql.append("    AND so.order_source_type = :orderStatusType ");
            params.put("orderStatusType",form.getOrderSourceType());
        }else {
            sql.append("    AND so.order_source_type IN (:PART, :SERVICE) ");
            params.put("PART",ProductClsType.PART.getCodeDbid());
            params.put("SERVICE",ProductClsType.SERVICE.getCodeDbid());
        }
        sql.append("   GROUP BY so.sales_order_id       ) AS  list      ");

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }

    @Override
    public List<SPQ020101BO> findSalesOrderCustomerExportList(SPQ020101Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT so.order_source_type                      AS sourceType        ");
        sql.append("          , so.order_date                             AS orderDate         ");
        sql.append("          , so.order_no                               AS orderNo           ");
        sql.append("          , COUNT(CASE WHEN soi.actual_qty > 0 AND soi.bo_qty = 0          ");
        sql.append("                 THEN soi.sales_order_item_id END) AS allocatedLines       ");
        sql.append("          , COUNT(CASE WHEN soi.actual_qty > 0                             ");
        sql.append("                  THEN soi.sales_order_item_id END) AS orderLines          ");
        sql.append("          , COALESCE(SUM(CASE WHEN soi.actual_qty > 0                      ");
        sql.append("                THEN soi.actual_qty * ROUND(soi.selling_price, 0) END), 0) AS orderAmt   ");
        sql.append("       FROM sales_order so                                                 ");
        sql.append("  LEFT JOIN mst_facility mf                                                ");
        sql.append("         ON mf.facility_id = so.facility_id                                ");
        sql.append("  LEFT JOIN sales_order_item soi                                           ");
        sql.append("         ON so.sales_order_id = soi.sales_order_id                         ");
        sql.append("        AND so.site_id = soi.site_id                                       ");
        sql.append("      WHERE so.site_id = :siteId                                           ");
        sql.append("        AND so.order_date >= :dateFrom                                     ");
        sql.append("        AND so.order_date <= :dateTo                                       ");

        params.put("siteId",siteId);
        params.put("dateFrom",form.getDateFrom());
        params.put("dateTo",form.getDateTo());

        if (!Nulls.isNull(form.getPointId())) {
            sql.append("    AND so.facility_id = :facilityId ");
            params.put("facilityId",form.getPointId());
        }

        if (!Nulls.isNull(form.getCustomerId())) {
            sql.append("    AND so.customer_id = :customerId ");
            params.put("customerId",form.getCustomerId());
        }

        if (StringUtils.isNotBlank(form.getOrderNo())) {
            sql.append("    AND so.order_no = :orderNo      ");
            params.put("orderNo",form.getOrderNo());
        }

        if (StringUtils.isNotBlank(form.getOrderStatus())) {
            sql.append("    AND so.order_status = :orderStatusId ");
            params.put("orderStatusId",form.getOrderStatus());
        }

        if(StringUtils.isNotBlank(form.getOrderSourceType())) {
            sql.append("    AND so.order_source_type = :orderStatusType ");
            params.put("orderStatusType",form.getOrderSourceType());
        }else {
            sql.append("    AND so.order_source_type IN (:PART, :SERVICE) ");
            params.put("PART",ProductClsType.PART.getCodeDbid());
            params.put("SERVICE",ProductClsType.SERVICE.getCodeDbid());
        }

        sql.append("   GROUP BY so.order_source_type, so.order_date, so.order_no            ");
        sql.append("   ORDER BY so.order_no                                                 ");

        return super.queryForList(sql.toString(), params, SPQ020101BO.class);
    }

    @Override
    public Page<SPQ020201BO> findSalesOrderPartsList(SPQ020201Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT so.sales_order_id                                 AS salesOrderId      ");
        sql.append("          , so.facility_id                                    AS pointId           ");
        sql.append("          , mf.facility_cd                                    AS pointCd           ");
        sql.append("          , mf.facility_nm                                    AS pointNm           ");
        sql.append("          , so.order_no                                       AS orderNo           ");
        sql.append("          , soi.product_id                                    AS orderPartsId      ");
        sql.append("          , soi.product_cd                                    AS orderPartsCd      ");
        sql.append("          , soi.product_nm                                    AS orderPartsNm      ");
        sql.append("          , soi.allocated_product_id                          AS allocatedPartsId  ");
        sql.append("          , soi.allocated_product_cd                          AS allocatedPartsCd  ");
        sql.append("          , soi.allocated_product_nm                          AS allocatedPartsNm  ");
        sql.append("          , COALESCE(ROUND(soi.standard_price, 0), 0)         AS retailPrice       ");
        sql.append("          , COALESCE(ROUND(soi.selling_price_not_vat, 0), 0)  AS sellingPrice      ");
        sql.append("          , so.customer_cd                                    AS customerCd        ");
        sql.append("          , COALESCE(soi.order_qty, 0)                        AS orderQty          ");
        sql.append("          , COALESCE(soi.allocated_qty, 0)                    AS allocatedQty      ");
        sql.append("          , COALESCE(soi.bo_qty, 0)                           AS boQty             ");
        sql.append("          , COALESCE(soi.instruction_qty, 0)                  AS pickingQty        ");
        sql.append("          , COALESCE(soi.shipment_qty, 0)                     AS shippedQty        ");
        sql.append("          , COALESCE(soi.cancel_qty, 0)                       AS cancelQty         ");
        sql.append("          , soi.cancel_date                                   AS cancelDate        ");
        sql.append("          , soi.cancel_pic_nm                                 AS cancelPic         ");
        sql.append("          , so.order_date                                     AS orderDate         ");
        sql.append("          , soi.order_priority_seq                            AS priority          ");
        sql.append("       FROM sales_order so                                                         ");
        sql.append("  LEFT JOIN mst_facility mf                                                        ");
        sql.append("         ON mf.facility_id = so.facility_id                                        ");
        sql.append("  LEFT JOIN sales_order_item soi                                                   ");
        sql.append("         ON so.sales_order_id = soi.sales_order_id                                 ");
        sql.append("        AND so.site_id = soi.site_id                                               ");
        sql.append("      WHERE so.site_id = :siteId                                                   ");
        sql.append("        AND so.order_date >= :dateFrom                                             ");
        sql.append("        AND so.order_date <= :dateTo                                               ");

        params.put("siteId",siteId);
        params.put("dateFrom",form.getDateFrom());
        params.put("dateTo",form.getDateTo());

        if (!Nulls.isNull(form.getPartsId())) {
            sql.append("    AND (soi.product_id = :partsId OR soi.allocated_product_id = :partsId) ");
            params.put("partsId",form.getPartsId());
        }

        if (!Nulls.isNull(form.getPointId())) {
            sql.append("    AND so.facility_id = :pointId");
            params.put("pointId",form.getPointId());
        }

        if (CommonConstants.CHAR_Y.equals(form.getAllocationOnly())) {
            sql.append("    AND soi.allocated_qty > 0 ");
        }

        if (CommonConstants.CHAR_Y.equals(form.getInProcessOnly())) {
            sql.append("    AND soi.actual_qty > soi.shipment_qty ");
        }

        sql.append("   ORDER BY mf.facility_cd, so.order_no, soi.product_cd, soi.allocated_product_cd    ");

        Integer count = getCountForSalesOrderParts(form,siteId);
        Pageable pageable = PageRequest.of(form.getCurrentPage() - 1, form.getPageSize());
        return new PageImpl<>(super.queryForPagingList(sql.toString(), params, SPQ020201BO.class, pageable), pageable, count);
    }

    private Integer getCountForSalesOrderParts(SPQ020201Form form, String siteId) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT COUNT(*)                                             ");
        sql.append("       FROM sales_order so                                  ");
        sql.append("  LEFT JOIN mst_facility mf                                 ");
        sql.append("         ON mf.facility_id = so.facility_id                 ");
        sql.append("  LEFT JOIN sales_order_item soi                            ");
        sql.append("         ON so.sales_order_id = soi.sales_order_id          ");
        sql.append("        AND so.site_id = soi.site_id                        ");
        sql.append("      WHERE so.site_id = :siteId                            ");
        sql.append("        AND so.order_date >= :dateFrom                      ");
        sql.append("        AND so.order_date <= :dateTo                        ");

        params.put("siteId",siteId);
        params.put("dateFrom",form.getDateFrom());
        params.put("dateTo",form.getDateTo());

        if (!Nulls.isNull(form.getPartsId())) {
            sql.append("    AND (soi.product_id = :partsId OR soi.allocated_product_id = :partsId) ");
            params.put("partsId",form.getPartsId());
        }

        if (!Nulls.isNull(form.getPointId())) {
            sql.append("    AND so.facility_id = :pointId");
            params.put("pointId",form.getPointId());
        }

        if (CommonConstants.CHAR_Y.equals(form.getAllocationOnly())) {
            sql.append("    AND soi.allocated_qty > 0 ");
        }

        if (CommonConstants.CHAR_Y.equals(form.getInProcessOnly())) {
            sql.append("    AND soi.actual_qty > soi.shipment_qty ");
        }

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }

    @Override
    public List<SPQ020201BO> findSalesOrderPartsExportList(SPQ020201Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT so.sales_order_id                                 AS salesOrderId      ");
        sql.append("          , so.facility_id                                    AS pointId           ");
        sql.append("          , mf.facility_cd                                    AS pointCd           ");
        sql.append("          , mf.facility_nm                                    AS pointNm           ");
        sql.append("          , so.order_no                                       AS orderNo           ");
        sql.append("          , soi.product_id                                    AS orderPartsId      ");
        sql.append("          , soi.product_cd                                    AS orderPartsCd      ");
        sql.append("          , soi.product_nm                                    AS orderPartsNm      ");
        sql.append("          , soi.allocated_product_id                          AS allocatedPartsId  ");
        sql.append("          , soi.allocated_product_cd                          AS allocatedPartsCd  ");
        sql.append("          , soi.allocated_product_nm                          AS allocatedPartsNm  ");
        sql.append("          , COALESCE(ROUND(soi.standard_price, 0), 0)         AS retailPrice       ");
        sql.append("          , COALESCE(ROUND(soi.selling_price_not_vat, 0), 0)  AS sellingPrice      ");
        sql.append("          , so.customer_cd                                    AS customerCd        ");
        sql.append("          , COALESCE(soi.order_qty, 0)                        AS orderQty          ");
        sql.append("          , COALESCE(soi.allocated_qty, 0)                    AS allocatedQty      ");
        sql.append("          , COALESCE(soi.bo_qty, 0)                           AS boQty             ");
        sql.append("          , COALESCE(soi.instruction_qty, 0)                  AS pickingQty        ");
        sql.append("          , COALESCE(soi.shipment_qty, 0)                     AS shippedQty        ");
        sql.append("          , COALESCE(soi.cancel_qty, 0)                       AS cancelQty         ");
        sql.append("          , soi.cancel_date                                   AS cancelDate        ");
        sql.append("          , soi.cancel_pic_nm                                 AS cancelPic         ");
        sql.append("          , so.order_date                                     AS orderDate         ");
        sql.append("          , soi.order_priority_seq                            AS priority          ");
        sql.append("       FROM sales_order so                                                         ");
        sql.append("  LEFT JOIN mst_facility mf                                                        ");
        sql.append("         ON mf.facility_id = so.facility_id                                        ");
        sql.append("  LEFT JOIN sales_order_item soi                                                   ");
        sql.append("         ON so.sales_order_id = soi.sales_order_id                                 ");
        sql.append("        AND so.site_id = soi.site_id                                               ");
        sql.append("      WHERE so.site_id = :siteId                                                   ");
        sql.append("        AND so.order_date >= :dateFrom                                             ");
        sql.append("        AND so.order_date <= :dateTo                                               ");

        params.put("siteId",siteId);
        params.put("dateFrom",form.getDateFrom());
        params.put("dateTo",form.getDateTo());

        if (!Nulls.isNull(form.getPartsId())) {
            sql.append("    AND (soi.product_id = :partsId OR soi.allocated_product_id = :partsId) ");
            params.put("partsId",form.getPartsId());
        }

        if (!Nulls.isNull(form.getPointId())) {
            sql.append("    AND so.facility_id = :pointId");
            params.put("pointId",form.getPointId());
        }

        if (CommonConstants.CHAR_Y.equals(form.getAllocationOnly())) {
            sql.append("    AND soi.allocated_qty > 0 ");
        }

        if (CommonConstants.CHAR_Y.equals(form.getInProcessOnly())) {
            sql.append("    AND soi.actual_qty > soi.shipment_qty ");
        }

        sql.append("   ORDER BY mf.facility_cd, so.order_no, soi.product_cd, soi.allocated_product_cd    ");

        return super.queryForList(sql.toString(), params, SPQ020201BO.class);
    }


    @Override
    public Page<SPM021301BO> searchBackOrderList(SPM021301Form model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        StringBuilder selSql = new StringBuilder();

        selSql.append(" SELECT so.order_no            AS orderNo                      ");
        selSql.append("      , so.sales_order_id      AS orderId                      ");
        selSql.append("      , so.related_sv_order_id AS serviceId                    ");
        selSql.append("      , so.order_date          AS orderDate                    ");
        selSql.append("      , so.delivery_plan_date  AS customerPromiseDate          ");
        selSql.append("      , so.bo_contact_date     AS contactDate                  ");
        selSql.append("      , so.consumer_nm_full    AS consumer                     ");
        selSql.append("      , so.cmm_consumer_id     AS consumerId                   ");
        selSql.append("      , so.mobile_phone        AS mobile                       ");
        selSql.append("      , so.order_source_type   AS orderSourceType              ");

        selSql.append("      , COUNT(CASE WHEN soi.actual_qty > 0                    ");
        selSql.append("              THEN soi.sales_order_item_id END) AS orderLines ");
        selSql.append("      , COUNT(CASE WHEN soi.bo_qty > 0                        ");
        selSql.append("              THEN soi.sales_order_item_id END) AS boLines    ");

        sql.append(" FROM sales_order so ");
        sql.append(" LEFT JOIN sales_order_item soi ");
        sql.append("        ON soi.sales_order_id = so.sales_order_id ");
        sql.append(" WHERE so.site_id = :siteId ");
        sql.append("   AND so.facility_id = :pointId ");
        sql.append("   AND so.bo_flag = :flag ");
        params.put("siteId", model.getSiteId());
        params.put("pointId", model.getPointId());
        params.put("flag", CommonConstants.CHAR_Y);

        if (StringUtils.isNotBlank(model.getDateFrom())) {
            sql.append(" AND so.allocate_due_date >= :dateFrom ");
            params.put("dateFrom", model.getDateFrom());
        }

        if (StringUtils.isNotBlank(model.getDateTo())) {
            sql.append(" AND so.allocate_due_date <= :dateTo ");
            params.put("dateTo", model.getDateTo());
        }

        if(StringUtils.equals(model.getNotContact(), CommonConstants.CHAR_Y)) {
            sql.append(" AND so.bo_contact_date is null ");
        }

        if(StringUtils.equals(model.getNoneBOOnly(), CommonConstants.CHAR_Y)) {
            sql.append(" AND 0 = ( SELECT sum(soi.bo_qty) FROM sales_order_item soi ");
            sql.append("            WHERE soi.sales_order_id = so.sales_order_id )  ");
        }

        if (!ObjectUtils.isEmpty(model.getConsumerId())) {
            sql.append(" AND so.cmm_consumer_id = :consumerId ");
            params.put("consumerId", model.getConsumerId());
        }

        if (StringUtils.isNotBlank(model.getMobile())) {
            sql.append(" AND so.mobile_phone = :mobile ");
            params.put("mobile", model.getMobile());
        }

        if (StringUtils.isNotBlank(model.getOrderNo())) {
            sql.append(" AND so.order_no = :orderNo ");
            params.put("orderNo", model.getOrderNo());
        }

        sql.append(" GROUP BY so.order_no, so.sales_order_id, so.order_date, so.delivery_plan_date, so.bo_contact_date, so.consumer_nm_full, so.cmm_consumer_id, so.mobile_phone, so.order_source_type ");

        sql.append(" ORDER BY so.order_no ");

        String countSql = "SELECT COUNT(1) FROM ( " + "SELECT 1 " + sql.toString() + ") AS subquery; ";
        Pageable pageable = PageRequest.of(model.getCurrentPage() - 1, model.getPageSize());
        if (!model.isPageFlg()) {
            pageable = Pageable.unpaged();
        }
        Integer count = super.queryForSingle(countSql, params, Integer.class);
        params.putAll(params);

        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, SPM021301BO.class, pageable), pageable, count);
    }

    @Override
    public List<SVM0102PrintServicePartBO> getServicePartPrintList(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT soi.allocated_product_cd AS partCd                  ");
        sql.append("         , soi.allocated_product_nm AS partNm                  ");
        sql.append("         , ''                       AS uom                     ");
        sql.append("         , soi.actual_qty           AS qty                     ");
        sql.append("         , (                                                   ");
        sql.append("            SELECT lo.location_cd FROM product_inventory pi    ");
        sql.append("            LEFT JOIN location lo                              ");
        sql.append("            ON lo.location_id = pi.location_id                 ");
        sql.append("            WHERE pi.site_id = so.site_id                      ");
        sql.append("            AND pi.facility_id = so.facility_id                ");
        sql.append("            AND pi.product_id = soi.allocated_product_id       ");
        sql.append("            ORDER BY pi.primary_flag DESC, pi.quantity         ");
        sql.append("            LIMIT 1                                            ");
        sql.append("           )                        AS locationNo              ");
        sql.append("      FROM sales_order so                                      ");
        sql.append(" LEFT JOIN sales_order_item soi                                ");
        sql.append("        ON soi.sales_order_id = so.sales_order_id              ");
        sql.append("     WHERE so.related_sv_order_id = :serviceOrderId            ");
        sql.append("       AND soi.actual_qty > 0                                  ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForList(sql.toString(), params, SVM0102PrintServicePartBO.class);
    }

    @Override
    public List<SVM0102PrintServicePartBO> getServicePaymentPartPrintList(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT soi.allocated_product_cd AS partCd          ");
        sql.append("         , soi.allocated_product_nm AS partNm          ");
        sql.append("         , ''                       AS uom             ");
        sql.append("         , soi.selling_price        AS sellingPrice    ");
        sql.append("         , soi.actual_qty           AS qty             ");
        sql.append("         , soi.actual_amt           AS amount          ");
        sql.append("         , soi.settle_type_id       AS settleTypeId    ");
        sql.append("      FROM sales_order so                              ");
        sql.append(" LEFT JOIN sales_order_item soi                        ");
        sql.append("        ON soi.sales_order_id = so.sales_order_id      ");
        sql.append("     WHERE so.related_sv_order_id = :serviceOrderId    ");
        sql.append("       AND soi.actual_qty > 0                          ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForList(sql.toString(), params, SVM0102PrintServicePartBO.class);
    }

    @Override
    public List<SVM0102PrintServicePartBO> getServicePaymentPartForDoPrintList(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT soi.allocated_product_cd AS partCd            ");
        sql.append("         , soi.allocated_product_nm AS partNm            ");
        sql.append("         , soi.actual_qty           AS qty               ");
        sql.append("         , soi.allocated_qty        AS allocatedQty      ");
        sql.append("         , soi.bo_qty               AS boQty             ");
        sql.append("         , soi.tax_rate             AS taxRate           ");
        sql.append("         , soi.selling_price        AS sellingPrice      ");
        sql.append("         , soi.discount_amt         AS discountAmt       ");
        sql.append("         , soi.discount_off_rate    AS discount          ");
        sql.append("         , soi.actual_amt           AS amount            ");
        sql.append("         , soi.settle_type_id       AS settleTypeId      ");
        sql.append("      FROM sales_order so                                ");
        sql.append(" LEFT JOIN sales_order_item soi                          ");
        sql.append("        ON soi.sales_order_id = so.sales_order_id        ");
        sql.append("     WHERE so.related_sv_order_id = :serviceOrderId      ");
        sql.append("       AND soi.actual_qty > 0                            ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForList(sql.toString(), params, SVM0102PrintServicePartBO.class);
    }

    @Override
    public List<SDM030101BO> findSalesOrderData(SDM030101Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT CASE WHEN so.order_type = :wholesalesOrder THEN mf.facility_nm ELSE so.consumer_nm_full END AS consumerNm ");
        sql.append("         , CASE WHEN so.order_type = :wholesalesOrder THEN so.customer_cd ELSE NULL END AS dealerCd ");
        sql.append("         , CASE WHEN so.order_type = :wholesalesOrder THEN so.customer_nm ELSE NULL END AS dealerNm ");
        sql.append("         , CASE WHEN so.order_type = :wholesalesOrder THEN :unChecked ELSE :checked END AS pdiCheck ");
        sql.append("         , so.order_no         AS orderNo                            ");
        sql.append("         , so.sales_order_id   AS orderId                            ");
        sql.append("         , so.order_status     AS orderStatus                        ");
        sql.append("         , so.order_date       AS orderDate                          ");
        sql.append("         , so.order_type       AS salesTypeDbid                      ");
        sql.append("         , so.total_qty        AS totalQty                           ");
        sql.append("         , so.facility_id      AS facilityId                         ");
        sql.append("         , mf.facility_nm      AS facilityNm                         ");
        // 有输入车架号查询时
        if(!StringUtils.isEmpty(form.getFrameNo())) {
            sql.append("      FROM serialized_product sp                                 ");
            sql.append("INNER JOIN order_serialized_item osi                             ");
            sql.append("       ON osi.serialized_product_id = sp.serialized_product_id   ");
            sql.append("       AND osi.site_id = :siteId                                 ");
            sql.append("INNER JOIN sales_order so                                        ");
            sql.append("        ON so.sales_order_id = osi.sales_order_id                ");
            sql.append("       AND so.site_id = :siteId                                  ");
            sql.append("INNER JOIN mst_facility mf                                       ");
            sql.append("        ON mf.facility_id = so.facility_id                       ");
            sql.append("     WHERE sp.frame_no = :frameNo                                ");
            sql.append("       AND sp.site_id = :siteId                                  ");

            params.put("frameNo", form.getFrameNo());
        } else {
            sql.append("      FROM sales_order so                                        ");
            sql.append(" LEFT JOIN mst_facility mf                                       ");
            sql.append("        ON mf.facility_id = so.facility_id                       ");
            sql.append("     WHERE so.order_source_type = :GOODS                         ");
            sql.append("       AND so.site_id = :siteId                                  ");

            params.put("GOODS", ProductClsType.GOODS.getCodeDbid());
        }

        sql.append("       AND so.entry_facility_id = :pointId                           ");
        sql.append("       AND substring(so.order_date, 1, 6) = :month                   ");

        if (!StringUtils.isEmpty(form.getOrderNo())) {
            sql.append("    AND so.order_no = :orderNo                                   ");
            params.put("orderNo", form.getOrderNo());
        }

        if (!StringUtils.isEmpty(form.getOrderStatus())) {
            sql.append("    AND so.order_status = :orderStatus                           ");
            params.put("orderStatus", form.getOrderStatus());
        }

        if (!StringUtils.isEmpty(form.getSalesType())) {
            sql.append("    AND so.order_type = :salesType                               ");
            params.put("salesType", form.getSalesType());
        }

        if (!ObjectUtils.isEmpty(form.getDealerId())) {
            sql.append("    AND so.customer_id = :dealerId                               ");
            params.put("dealerId", form.getDealerId());
        }

        if (!ObjectUtils.isEmpty(form.getConsumerId())) {
            sql.append("    AND so.cmm_consumer_id = :consumerId                         ");
            params.put("consumerId", form.getConsumerId());
        } else if (!StringUtils.isEmpty(form.getConsumer())) {
            sql.append("    AND so.consumer_nm_full like :consumer                          ");
            params.put("consumer", "%" + form.getConsumer() + "%");
        }
        sql.append("    ORDER BY so.order_no asc                                         ");

        params.put("siteId", siteId);
        params.put("pointId", form.getPointId());
        params.put("month", form.getSalesMonth());
        params.put("wholesalesOrder", MCSalesType.WHOLESALESORDER);
        params.put("checked", CommonConstants.CHAR_Y);
        params.put("unChecked", CommonConstants.CHAR_N);

        return super.queryForList(sql.toString(), params, SDM030101BO.class);
    }

    @Override
    public SDM030103BO getDealerWholeSOBasicInfo(Long orderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT so.entry_facility_id                        AS entryFacilityId  ");
        sql.append("          , mf.facility_cd || mf.facility_nm            AS entryFacality    ");
        sql.append("          , mf.facility_cd || mf.facility_nm            AS deliveryPoint    ");
        sql.append("          , mf2.facility_cd || mf2.facility_nm          AS consignee        ");
        sql.append("          , cmo.organization_cd  || cmo.organization_nm AS dealer           ");
        sql.append("          , so.order_no                                 AS orderNo          ");
        sql.append("          , so.customer_id                              AS dealerId         ");
        sql.append("          , so.consignee_id                             AS consigneeId      ");
        sql.append("          , so.order_date                               AS orderDate        ");
        sql.append("       FROM sales_order so                                                  ");
        sql.append(" LEFT JOIN mst_facility mf                                                 ");
        sql.append("         ON so.entry_facility_id = mf.facility_id                           ");
        sql.append(" LEFT JOIN cmm_mst_organization cmo                                        ");
        sql.append("         ON so.customer_id = cmo.organization_id                            ");
        sql.append(" LEFT JOIN mst_facility mf2                                                ");
        sql.append("         ON so.facility_id = mf2.facility_id                                ");
        sql.append("      WHERE so.sales_order_id = :orderId                                    ");

        params.put("orderId", orderId);

        return super.queryForSingle(sql.toString(), params, SDM030103BO.class);
    }

    @Override
    public List<EInvoiceProductsBO> getMCSpecialProductsInfo(Long orderId, String flag) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT so.frame_no AS code,  ");
        if ( flag != null && flag.equals(CommonConstants.CHAR_Y)){
            sql.append("  'Xe gắn máy hai bánh (điện) Yamaha ; Số loại:' || (case when so.mode_Type is null then '' else so.mode_Type end) ||  ");
        }else{
            sql.append("  'Xe mô tô hai bánh Yamaha ; Số loại:' || (case when so.mode_Type is null then '' else so.mode_Type end) ||  ");
        }

        sql.append("  '; Mới 100%' ||  ");
        sql.append("  '; SK:' || (case when so.frame_no is null then '' else so.frame_no end) ||     ");
        sql.append("  '; SM:' || (case when so.engine_no is null then '' else so.engine_no end) ||   ");
        sql.append("  '; Màu:' || (case when so.color_nm is null then '' else so.color_nm end) ||    ");
        sql.append("  '; Phiên bản:' || (case when so.model_nm is null then '' else so.model_nm end) AS prodName, ");
        sql.append("  (SELECT parameter_value_    ");
        sql.append("  FROM system_parameter_info  ");
        sql.append("  WHERE system_parameter_type_id_ = 'S074EINVOICEPRODUNIT') AS prodUnit, ");
        sql.append("  '1' AS prodQuantity, ");
        sql.append(" (case when (oi.standard_price-oi.special_price_)>0 then (round(cast(oi.standard_price as int)/tax_rate,0) else 0 end) AS prodPrice, ");
        sql.append(" (case when (oi.standard_price-oi.special_price_)>0 then ");
        sql.append(" (round(cast(oi.standard_price as int)/tax_rate,0)-round(cast(oi.special_price_ as int)/tax_rate,0) ) else 0 end) AS amount, ");
        sql.append(" round(oi.discount_,0) AS discount, ");
        sql.append(" (case when oi.special_price_ > 0 then 2 else 0 end) AS isSum, ");
        sql.append(" (case when (oi.standard_price-oi.special_price_)>0 then ");
        sql.append(" (round(cast(oi.special_price_ as int)/tax_rate,0) ) else 0 end) AS discountAmount ");
        sql.append(" FROM sales_order so ");
        sql.append(" WHERE so.order_id_= :orderId ");

        params.put("orderId", orderId);

        return super.queryForList(sql.toString(), params, EInvoiceProductsBO.class);
    }

    @Override
    public List<EInvoiceProductsBO> getMCProductsInfo(Long orderId, String flag) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT so.frame_no AS code,  ");
        if ( flag != null && flag.equals(CommonConstants.CHAR_Y)){
            sql.append("'Xe gắn máy hai bánh (điện) Yamaha ; Số loại:' || (case when so.mode_Type is null then '' else so.mode_Type end) || ");
        }else{
            sql.append("'Xe mô tô hai bánh Yamaha ; Số loại:' || (case when so.mode_Type is null then '' else so.mode_Type end) || ");
        }

        sql.append(" 'Mới 100%' || ");
        sql.append(" ' SK:' || (case when so.frame_no is null then '' else so.frame_no end) ||  ");
        sql.append(" ' SM:' || (case when so.engine_no is null then '' else so.engine_no end) ||  ");
        sql.append(" ' Màu:' || (case when so.color_nm is null then '' else so.color_nm end) || ");
        sql.append(" 'Phiên bản:' || (case when so.model_nm is null then '' else so.model_nm end) AS prodName,  ");
        sql.append(" (SELECT parameter_value_  ");
        sql.append(" FROM system_parameter_info ");
        sql.append(" WHERE system_parameter_type_id_ = 'S074EINVOICEPRODUNIT') AS prodUnit,  ");
        sql.append(" '1' AS prodQuantity, ");
        sql.append("  (case when so.amount_ is null then '0' else round(cast(oiir.so as int)/tax_rate,0) end) AS prodPrice, ");
        sql.append("  (case when so.amount_ is null then '0' else round(cast(oiir.so as int)/tax_rate,0) end) AS amount, ");
        sql.append("  round(so.discount_,0) AS discount, ");
        sql.append("  (case when so.special_price_ > 0 then 2 else 0 end) AS isSum, ");
        sql.append(" (case when so.amount_ is null then '0' else round(cast(oi2.special_price_ as int)/tax_rate,0) end) AS discountAmount ");
        sql.append("  FROM sales_order so ");
        sql.append("  WHERE so.order_id_= :orderId ");

        params.put("orderId", orderId);

        return super.queryForList(sql.toString(), params, EInvoiceProductsBO.class);
    }

    @Override
    public EInvoiceInvoiceBO getInvoiceinfoForParts(Long orderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT (so.site_id || so.order_no) AS cusCode, ");
        sql.append(" (CASE WHEN so.customer_nm is null THEN 'KHÁCH HÀNG KHÔNG LẤY HĐ' ELSE replace(so.customer_nm,'_',' ') END) AS cusName, ");
        sql.append(" invoice.invoice_no                                       AS sophieuthu, ");
        sql.append(" (CASE WHEN so.email IS NULL THEN '' ELSE so.email END)   AS extra,      ");
        sql.append(" (CASE WHEN so.email IS NULL THEN '' ELSE so.email END)   AS extra3,     ");
        sql.append(" (CASE WHEN so.customer_tax_code IS NULL THEN '' ELSE so.customer_tax_code END)   AS cusTaxCode,        ");
        sql.append(" (CASE WHEN so.customer_tax_code IS NULL THEN '' ELSE so.customer_tax_code END)   AS extra4,            ");
        sql.append(" (CASE WHEN so.payment_method_type IS NULL THEN '' ELSE so.payment_method_type END)  AS paymentMethod,  ");
        sql.append(" so.mobile_phone              AS cusPhone,     ");
        sql.append(" so.order_no                  AS lenhDieuDong, ");
        sql.append(" so.order_no                  AS saleOrder,    ");
        sql.append(" so.order_no                  AS billWay,      ");
        sql.append(" (CASE WHEN so.customer_id is null then null else (select cmm_consumer.address from cmm_consumer where cmm_consumer.consumer_id=so.customer_id) end)    AS cusAddress, ");
        sql.append(" '0'                          AS sstSign,      ");
        sql.append(" invoice_item.tax_rate       AS vatRate,      ");
        sql.append(" substring(invoice.invoice_date,7,2) || '/'|| substring(invoice.invoice_date,5,2) || '/' || substring(invoice.invoice_date,1,4) AS arisingDate,  ");
        sql.append(" invoice.last_updated_by      AS userID        ");
        sql.append(" FROM   sales_order so ");
        sql.append("  left join invoice_item on invoice_item.sales_order_id=so.sales_order_id ");
        sql.append("  left join invoice on invoice.invoice_id=invoice_item.invoice_id ");
        sql.append("     WHERE so.sales_order_id = :orderId     ");
        sql.append("           GROUP BY cusCode, cusName, cusAddress, cusPhone, paymentMethod, arisingDate, vatRate, lenhDieuDong, saleOrder, sstSign, userID, billWay, extra,cusTaxCode,extra3,extra4,sophieuthu  ");

        params.put("orderId", orderId);

        return super.queryForSingle(sql.toString(), params, EInvoiceInvoiceBO.class);
    }

    @Override
    public EInvoiceInvoiceBO getSpecialMCInvoiceinfo(Long orderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT (so.site_id_ || so.order_no) AS cusCode,                                                       ");
        sql.append(" replace(so.customer_nm,'_',' ') AS cusName,                                                           ");
        sql.append(" (CASE WHEN so.customer_id='__' then '' else (select cmm_consumer.address from cmm_consumer where      ");
        sql.append(" cmm_consumer.consumer_id=so.customer_id)) AS cusAddress,                                              ");
        sql.append(" so.mobile_phone AS cusPhone,                                                                             ");
        sql.append(" (CASE WHEN so.customer_tax_code IS NULL THEN '' ELSE so.customer_tax_code END) AS cusTaxCode,         ");
        sql.append(" (CASE WHEN so.payment_method_type IS NULL THEN '' ELSE so.payment_method_type END) AS paymentMethod,  ");
        sql.append(" (CASE WHEN so.email IS NULL THEN '' ELSE so.email END) AS extra,                                      ");
        sql.append(" (CASE WHEN so.email IS NULL THEN '' ELSE so.email END) AS extra3,                                     ");
        sql.append(" (CASE WHEN so.customer_tax_code IS NULL THEN '' ELSE so.customer_tax_code END) AS extra4,             ");
        sql.append(" so.order_no AS sophieuthu,                                                                            ");
        sql.append(" so.tax_rate AS vatRate ,                                                                              ");
        sql.append(" (case when (soi.selling_price-so.special_price_)>0 then                                               ");
        sql.append(" (round(cast(soi.selling_price as int)/tax_rate,0)-round(cast(soi.special_price_ as int)/tax_rate,0))  ");
        sql.append(" else 0 end) AS total,                                                                                 ");
        sql.append(" (case when (soi.selling_price-so.special_price_)>0 then                                               ");
        sql.append(" (round(cast(soi.sselling_price as int)/tax_rate,0)-round(cast(soi.special_price_ as int)/tax_rate,0)) ");
        sql.append(" else 0 end) AS vatAmount,                                                                             ");
        sql.append(" (case when (soi.selling_price-so.special_price_)>0 then                                               ");
        sql.append(" (round(cast(soi.selling_price-so.special_price_ as int)/tax_rate0)                                    ");
        sql.append(" +round(cast(soi.selling_price-so.special_price_ as int)/tax_rate*0.1,0) )                             ");
        sql.append(" else 0 end) AS amount,                                                                                ");
        sql.append(" substring(invoice.invoice_date,7,2) || '/'|| substring(invoice.invoice_date,5,2) || '/' ||            ");
        sql.append(" substring(invoice.invoice_date,1,4) AS arisingDate,                                                   ");
        sql.append(" so.order_no AS lenhDieuDong,                                                                          ");
        sql.append(" (CASE WHEN so.displacement>'125' THEN '1' ELSE '0' END) AS sstSign,                                   ");
        sql.append(" so.order_no AS saleOrder,                                                                             ");
        sql.append(" so.order_no AS billWay,                                                                               ");
        sql.append(" invoice.last_updated_by AS userID                                                                     ");
        sql.append(" FROM sales_order so                                                                                   ");
        sql.append(" left join invoice_item on invoice_item.sales_order_id=so.order_id                                     ");
        sql.append(" left join invoice on invoice.invoice_id=invoice_item.invoice_id                                       ");
        sql.append(" WHERE so.order_id_ = :orderId                                                                         ");

        params.put("orderId", orderId);
        return super.queryForSingle(sql.toString(), params, EInvoiceInvoiceBO.class);
    }

    @Override
    public EInvoiceInvoiceBO getMCInvoiceinfo(Long orderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT (so.site_id_ || so.order_no) AS cusCode,                                                      ");
        sql.append(" replace(so.customer_nm,'',' ') AS cusName,                                                           ");
        sql.append(" (CASE WHEN so.customer_id='__' then '' else (select cmm_consumer.address from cmm_consumer where     ");
        sql.append(" cmm_consumer.consumer_id=so.customer_id)) AS cusAddress,                                             ");
        sql.append(" so.mobile_phone AS cusPhone,                                                                            ");
        sql.append(" (CASE WHEN so.customer_tax_code IS NULL THEN '' ELSE so.customer_tax_code END) AS cusTaxCode,        ");
        sql.append(" (CASE WHEN so.payment_method_type IS NULL THEN '' ELSE so.payment_method_type END) AS paymentMethod, ");
        sql.append(" (CASE WHEN so.email IS NULL THEN '' ELSE so.email END) AS extra,                                     ");
        sql.append(" (CASE WHEN so.email IS NULL THEN '' ELSE so.email END) AS extra3,                                    ");
        sql.append(" (CASE WHEN so.customer_tax_code IS NULL THEN '' ELSE so.customer_tax_code END) AS extra4,            ");
        sql.append(" so.order_no AS sophieuthu,                                                                           ");
        sql.append(" so.tax_rate AS vatRate,                                                                              ");
        sql.append(" round(cast(soi.amount_ as int)/tax_rate,0) AS total,                                                 ");
        sql.append(" round(cast(soi.amount_ as int),0) -round(cast(soi.amount_ as int)/tax_rate,0) AS vatAmount,          ");
        sql.append(" round(cast(soi.amount_ as int)/tax_rate,0) AS amount,                                                ");
        sql.append(" substring(invoice.invoice_date,7,2) || '/'|| substring(invoice.invoice_date,5,2) || '/' ||           ");
        sql.append(" substring(invoice.invoice_date,1,4) AS arisingDate,                                                  ");
        sql.append(" so.order_no AS lenhDieuDong,                                                                         ");
        sql.append(" (CASE WHEN so.displacement>'125' THEN '1' ELSE '0' END) AS sstSign,                                  ");
        sql.append(" so.order_no AS saleOrder,                                                                            ");
        sql.append(" so.order_no AS billWay,                                                                              ");
        sql.append(" invoice.last_updated_by AS userID                                                                    ");
        sql.append(" FROM sales_order so                                                                                  ");
        sql.append(" left join invoice_item on invoice_item.sales_order_id=so.order_id                                    ");
        sql.append(" left join invoice on invoice.invoice_id=invoice_item.invoice_id                                      ");
        sql.append(" WHERE so.order_id_ = :orderId                                                                        ");

        params.put("orderId", orderId);

        return super.queryForSingle(sql.toString(), params, EInvoiceInvoiceBO.class);
    }
}
