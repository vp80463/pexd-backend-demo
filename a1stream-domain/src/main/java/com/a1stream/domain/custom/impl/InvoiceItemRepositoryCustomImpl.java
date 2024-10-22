/**
 *
 */
package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.parts.SPM020202FunctionBO;
import com.a1stream.domain.bo.parts.SPQ020302BO;
import com.a1stream.domain.bo.parts.SPQ020403BO;
import com.a1stream.domain.custom.InvoiceItemRepositoryCustom;
import com.a1stream.domain.form.parts.SPM020202Form;
import com.a1stream.domain.form.parts.SPQ020302Form;
import com.a1stream.domain.form.parts.SPQ020401Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;

/**
* 功能描述:
*
* @author mid2259
*/
public class InvoiceItemRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements InvoiceItemRepositoryCustom {

    @Override
    public List<SPQ020403BO> searchInvoiceInfoDetail(SPQ020401Form model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();


        sql.append("    SELECT CASE WHEN item.order_source_type = 'S001PART'    ");
        sql.append("                THEN 'SP'                                   ");
        sql.append("                WHEN item.order_source_type = 'S001SERVICE' ");
        sql.append("                THEN 'SV' END AS sign                       ");

        sql.append("         , item.order_date            AS orderDate   ");
        sql.append("         , item.sales_order_no        AS orderNo     ");
        sql.append("         , inv.invoice_date           AS invoiceDate ");
        sql.append("         , inv.invoice_no             AS invoiceNo   ");
        sql.append("         , ROUND(item.qty)            AS qty         ");
        sql.append("         , ROUND(item.selling_price)  AS retailPrice ");
        sql.append("         , ROUND(item.amt)            AS amount      ");
        sql.append("         , item.tax_rate              AS taxRate     ");
        sql.append("         , item.product_cd            AS partsNo     ");
        sql.append("         , item.product_nm            AS partsNm     ");

        sql.append("      FROM invoice_item item                ");
        sql.append(" LEFT JOIN invoice inv                      ");
        sql.append("        ON inv.invoice_id = item.invoice_id ");

        sql.append(" WHERE item.site_id = :siteId ");
        sql.append("   AND inv.from_facility_id = :pointId ");
        params.put("siteId", model.getSiteId());
        params.put("pointId", model.getPointId());

        if(StringUtils.isNotBlank(model.getDateFrom()) && StringUtils.isBlank(model.getInvoiceNo())) {

            sql.append(" AND (inv.invoice_date > :dateFrom OR (inv.invoice_date = :dateFrom AND inv.invoice_datetime >= :timeFrom)) ");

            params.put("dateFrom", model.getDateFrom().substring(CommonConstants.INTEGER_ZERO, CommonConstants.INTEGER_EIGHT));
            params.put("timeFrom", model.getDateFrom().substring(CommonConstants.INTEGER_EIGHT, CommonConstants.INTEGER_TWELVE));
        }

        if (StringUtils.isNotBlank(model.getDateTo()) && StringUtils.isBlank(model.getInvoiceNo())) {

            sql.append(" AND (inv.invoice_date < :dateTo OR (inv.invoice_date = :dateTo AND inv.invoice_datetime <= :timeTo)) ");

            params.put("dateTo", model.getDateTo().substring(CommonConstants.INTEGER_ZERO, CommonConstants.INTEGER_EIGHT));
            params.put("timeTo", model.getDateTo().substring(CommonConstants.INTEGER_EIGHT, CommonConstants.INTEGER_TWELVE));
        }

        if(StringUtils.isNotBlank(model.getInvoiceNo())) {
            sql.append(" AND inv.invoice_no = :invoiceNo ");
            params.put("invoiceNo", model.getInvoiceNo());
        }
        sql.append("  ORDER BY item.order_date,item.sales_order_no ");

        return super.queryForList(sql.toString(), params, SPQ020403BO.class);
    }

    @Override
    public Page<SPQ020302BO> getInvoiceItemList(SPQ020302Form form, PJUserDetails uc) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        StringBuilder selSql = new StringBuilder();

        selSql.append("     SELECT sales_order_no        AS orderNo           ");
        selSql.append("          , customer_order_no     AS customerOrderNo   ");
        selSql.append("          , product_cd            AS allocatedPartsNo  ");
        selSql.append("          , ordered_product_cd    AS orderPartsNo      ");
        selSql.append("          , ordered_product_nm    AS orderPartsNm      ");
        selSql.append("          , case_no               AS caseNo            ");
        selSql.append("          , selling_price_not_vat AS sellingPrice      ");
        selSql.append("          , qty                   AS shippedQty        ");
        selSql.append("          , amt_not_vat           AS shipmentAmount    ");
        selSql.append("          , sales_order_id        AS salesOrderId      ");
        sql.append("          FROM invoice_item                               ");
        sql.append("         WHERE site_id = :siteId                          ");
        sql.append("           AND invoice_id = :invoiceId                    ");

        sql.append(" ORDER BY sales_order_no, product_cd, ordered_product_cd ");

        params.put("siteId", uc.getDealerCode());
        params.put("invoiceId", form.getInvoiceId());

        String countSql = "SELECT COUNT(1) FROM ( " + "SELECT 1 " + sql.toString() + ") AS subquery; ";
        Pageable pageable = PageRequest.of(form.getCurrentPage() - 1, form.getPageSize());
        if (!form.isPageFlg()) {
            pageable = Pageable.unpaged();
        }
        Integer count = super.queryForSingle(countSql, params, Integer.class);

        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, SPQ020302BO.class, pageable), pageable, count);
    }

    @Override
    public List<SPM020202FunctionBO> getSalesReturnHistoryDetailList(SPM020202Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT ii.product_cd       AS partsCd             ");
        sql.append("          , ii.product_nm       AS partsNm             ");
        sql.append("          , ii.qty              AS returnQty           ");
        sql.append("          , ii.selling_price    AS returnPrice         ");
        sql.append("          , ii.amt              AS returnAmount        ");
        sql.append("          , ii.cost             AS cost                ");
        sql.append("          , ii.tax_rate         AS taxRate             ");
        sql.append("       FROM invoice_item ii                            ");
        sql.append("      WHERE ii.site_id = :siteId                       ");
        params.put("siteId", siteId);

        if(!ObjectUtils.isEmpty(form.getReturnInvoiceId())) {
            sql.append("        AND ii.invoice_id = :returnInvoiceId       ");
            params.put("returnInvoiceId", form.getReturnInvoiceId());
        }
        sql.append("   ORDER BY ii.product_cd                              ");

        return super.queryForList(sql.toString(), params, SPM020202FunctionBO.class);
    }

}
