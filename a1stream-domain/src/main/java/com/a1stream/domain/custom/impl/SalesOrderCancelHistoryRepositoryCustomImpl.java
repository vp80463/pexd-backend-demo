package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.a1stream.domain.bo.parts.SPQ020701BO;
import com.a1stream.domain.custom.SalesOrderCancelHistoryRepositoryCustom;
import com.a1stream.domain.form.parts.SPQ020701Form;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;

public class SalesOrderCancelHistoryRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements SalesOrderCancelHistoryRepositoryCustom {

    @Override
    public Page<SPQ020701BO> findPartsCancelHisList(SPQ020701Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT soch.order_no              AS orderNo       ");
        sql.append("          , soch.order_date            AS orderDate     ");
        sql.append("          , soch.product_cd            AS partsCd       ");
        sql.append("          , soch.product_nm            AS partsNm       ");
        sql.append("          , soch.product_id            AS partsId       ");
        sql.append("          , soch.order_qty             AS orderQty      ");
        sql.append("          , soch.cancel_qty            AS cancelQty     ");
        sql.append("          , soch.cancel_date           AS cancelDate    ");
        sql.append("          , soch.cancel_time           AS cancelTime    ");
        sql.append("          , soch.cancel_pic_nm         AS cancelPic     ");
        sql.append("       FROM sales_order_cancel_history soch             ");
        sql.append("      WHERE soch.site_id = :siteId                      ");
        sql.append("        AND soch.cancel_date >= :dateFrom               ");
        sql.append("        AND soch.cancel_date <= :dateTo                 ");
        sql.append("        AND soch.facility_id = :pointId                 ");

        params.put("siteId", siteId);
        params.put("dateFrom", form.getDateFrom());
        params.put("dateTo", form.getDateTo());
        params.put("pointId", form.getPointId());

        if (!Nulls.isNull(form.getPartsId())) {
            sql.append("    AND soch.product_id = :partsId ");
            params.put("partsId",form.getPartsId());
        }

        if (StringUtils.isNotBlank(form.getOrderNo())) {
            sql.append("    AND soch.order_no = :orderNo ");
            params.put("orderNo",form.getOrderNo());
        }

        if (StringUtils.isNotBlank(form.getOrderDateFrom())) {
            sql.append("    AND soch.order_date >= :orderDateFrom       ");
            sql.append("    AND soch.order_date <= :orderDateTo         ");
            params.put("orderDateFrom", form.getOrderDateFrom());
            params.put("orderDateTo", form.getOrderDateTo());
        }

        sql.append(" ORDER BY soch.order_no, soch.product_cd");

        Integer count = getCountForPartsCancelHis(form,siteId);
        Pageable pageable = PageRequest.of(form.getCurrentPage() - 1, form.getPageSize());
        return new PageImpl<>(super.queryForPagingList(sql.toString(), params, SPQ020701BO.class, pageable), pageable, count);
    }

    private Integer getCountForPartsCancelHis(SPQ020701Form form, String siteId) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT COUNT(*)                                                         ");
        sql.append("       FROM sales_order_cancel_history soch                                ");
        sql.append("      WHERE soch.site_id = :siteId                                    ");
        sql.append("        AND soch.cancel_date >= :dateFrom       ");
        sql.append("        AND soch.cancel_date <= :dateTo                             ");
        sql.append("        AND soch.facility_id = :pointId                           ");

        params.put("siteId", siteId);
        params.put("dateFrom", form.getDateFrom());
        params.put("dateTo", form.getDateTo());
        params.put("pointId", form.getPointId());

        if (!Nulls.isNull(form.getPartsId())) {
            sql.append("    AND soch.product_id = :partsId ");
            params.put("partsId",form.getPartsId());
        }

        if (StringUtils.isNotBlank(form.getOrderNo())) {
            sql.append("    AND soch.order_no = :orderNo ");
            params.put("orderNo",form.getOrderNo());
        }

        if (StringUtils.isNotBlank(form.getOrderDateFrom())) {
            sql.append("    AND soch.order_date >= :orderDateFrom       ");
            sql.append("    AND soch.order_date <= :orderDateTo         ");
            params.put("orderDateFrom", form.getOrderDateFrom());
            params.put("orderDateTo", form.getOrderDateTo());
        }

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }

    @Override
    public List<SPQ020701BO> findPartsCancelHisExport(SPQ020701Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT soch.order_no              AS orderNo       ");
        sql.append("          , soch.order_date            AS orderDate     ");
        sql.append("          , soch.product_cd            AS partsCd       ");
        sql.append("          , soch.product_nm            AS partsNm       ");
        sql.append("          , soch.product_id            AS partsId       ");
        sql.append("          , soch.order_qty             AS orderQty      ");
        sql.append("          , soch.cancel_qty            AS cancelQty     ");
        sql.append("          , soch.cancel_date           AS cancelDate    ");
        sql.append("          , soch.cancel_time           AS cancelTime    ");
        sql.append("          , soch.cancel_pic_nm         AS cancelPic     ");
        sql.append("       FROM sales_order_cancel_history soch             ");
        sql.append("      WHERE soch.site_id = :siteId                      ");
        sql.append("        AND soch.cancel_date >= :dateFrom               ");
        sql.append("        AND soch.cancel_date <= :dateTo                 ");
        sql.append("        AND soch.facility_id = :pointId                 ");

        params.put("siteId", siteId);
        params.put("dateFrom", form.getDateFrom());
        params.put("dateTo", form.getDateTo());
        params.put("pointId", form.getPointId());

        if (!Nulls.isNull(form.getPartsId())) {
            sql.append("    AND soch.product_id = :partsId ");
            params.put("partsId",form.getPartsId());
        }

        if (StringUtils.isNotBlank(form.getOrderNo())) {
            sql.append("    AND soch.order_no = :orderNo ");
            params.put("orderNo",form.getOrderNo());
        }

        if (StringUtils.isNotBlank(form.getOrderDateFrom())) {
            sql.append("    AND soch.order_date >= :orderDateFrom       ");
            sql.append("    AND soch.order_date <= :orderDateTo         ");
            params.put("orderDateFrom", form.getOrderDateFrom());
            params.put("orderDateTo", form.getOrderDateTo());
        }

        sql.append(" ORDER BY soch.order_no, soch.product_cd");

        return super.queryForList(sql.toString(), params, SPQ020701BO.class);
    }
}
