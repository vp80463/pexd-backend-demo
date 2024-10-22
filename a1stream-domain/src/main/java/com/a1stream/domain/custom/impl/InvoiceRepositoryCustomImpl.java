package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.domain.bo.parts.PartsSalesReturnInvoiceForFinancePrintBO;
import com.a1stream.domain.bo.parts.PartsSalesReturnInvoiceForFinancePrintDetailBO;
import com.a1stream.domain.bo.parts.SPM020103PrintBO;
import com.a1stream.domain.bo.parts.SPM020201PrintBO;
import com.a1stream.domain.bo.parts.SPM020201PrintDetailBO;
import com.a1stream.domain.bo.parts.SPM020202FunctionBO;
import com.a1stream.domain.bo.parts.SPQ020301BO;
import com.a1stream.domain.bo.parts.SPQ020402BO;
import com.a1stream.domain.custom.InvoiceRepositoryCustom;
import com.a1stream.domain.form.parts.SPM020202Form;
import com.a1stream.domain.form.parts.SPQ020301Form;
import com.a1stream.domain.form.parts.SPQ020401Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class InvoiceRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements InvoiceRepositoryCustom {

    @Override
    public SPM020201PrintBO getPartsSalesReturnInvoiceForFinanceData(Long invoiceId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("       SELECT returninv.invoice_no                               AS returnInvoiceNo   ");
        sql.append("            , inv.invoice_no                                     AS invoiceNo         ");
        sql.append("            , mf.facility_cd || ' / ' || mf.facility_nm          AS point             ");
        sql.append("            , mo.organization_cd || ' / ' || mo.organization_nm  AS customer          ");
        sql.append("            , returninv.invoice_date                             AS returnDate        ");
        sql.append("       FROM invoice returninv                                                         ");
        sql.append(" INNER JOIN invoice inv                                                               ");
        sql.append("         ON returninv.related_invoice_id = inv.invoice_id                             ");
        sql.append(" INNER JOIN mst_facility mf                                                           ");
        sql.append("         ON mf.facility_id = returninv.from_facility_id                               ");
        sql.append(" INNER JOIN mst_organization mo                                                       ");
        sql.append("         ON mo.organization_id = returninv.consumer_id                                ");
        sql.append(" WHERE returninv.invoice_type = :S038SALESRETURNINVOICE                               ");
        sql.append("   AND inv.invoice_type = :S038SALESINVOICE                                           ");
        sql.append("   AND returninv.invoice_id = :invoiceId                                              ");

        params.put("S038SALESRETURNINVOICE", PJConstants.InvoiceType.SALES_RETURN_INVOICE.getCodeDbid());
        params.put("S038SALESINVOICE", PJConstants.InvoiceType.SALES_INVOICE.getCodeDbid());
        params.put("invoiceId", invoiceId);
        return super.queryForSingle(sql.toString(), params, SPM020201PrintBO.class);
    }

    @Override
    public List<SPM020201PrintDetailBO> getPartsSalesReturnInvoiceForFinanceDetailList(Long invoiceId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT invitem.product_cd     AS partsNo                                                                ");
        sql.append("          , invitem.product_nm     AS partsNm                                                                ");
        sql.append("          , invitem.qty            AS returnQty                                                              ");
        sql.append("          , ROUND(invitem.tax_rate / 100 * invitem.selling_price + invitem.selling_price, 0) AS returnPrice  ");
        sql.append("       FROM invoice_item invitem                                                                             ");
        sql.append(" INNER JOIN invoice inv                                                                                      ");
        sql.append("         ON invitem.invoice_id = inv.invoice_id                                                              ");
        sql.append("      WHERE inv.invoice_id = :invoiceId                                                                      ");

        params.put("invoiceId", invoiceId);
        return super.queryForList(sql.toString(), params, SPM020201PrintDetailBO.class);
    }

    @Override
    public List<SPM020103PrintBO> getPartsSalesInvoiceForDOList(List<Long> invoiceIds) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT so.order_no                                            AS orderNo                    ");
        sql.append("          , mf.facility_cd                                         AS orderPointCd               ");
        sql.append("          , mf.facility_nm                                         AS orderPointNm               ");
        sql.append("          , COALESCE(mf.address1, '') || COALESCE(mf.address2, '') AS companyAddress             ");
        sql.append("          , inv.invoice_no                                         AS invoiceNo                  ");
        sql.append("          , mf.contact_tel                                         AS phoneNo                    ");
        sql.append("          , mo.organization_nm                                     AS consumerName               ");
        sql.append("          , paytype.code_data1                                     AS paymentType                ");
        sql.append("          , inv.mobile_phone                                       AS mobilePhone                ");
        sql.append("          , so.employee_cd                                         AS employeeCd                 ");
        sql.append("          , relationship.code_data1                                AS relationShip               ");
        sql.append("          , so.ticket_no                                           AS ticketNo                   ");
        sql.append("          , so.facility_multi_addr                                 AS receivingPointAddress      ");
        sql.append("          , so.COMMENT                                             AS memo                       ");
        sql.append("          , so.deposit_amt                                         AS depositAmount              ");
        sql.append("          , so.entry_pic_nm                                        AS entryPicNm                 ");
        sql.append("          , soi.product_cd                                         AS partsNo                    ");
        sql.append("          , soi.product_nm                                         AS partsName                  ");
        sql.append("          , soi.battery_id                                         AS batteryId                  ");
        sql.append("          , soi.tax_rate                                           AS productTax                 ");
        sql.append("          , soi.selling_price                                      AS sellingPrice               ");
        sql.append("          , soi.discount_amt                                       AS discountAmt                ");
        sql.append("          , coalesce(soi.discount_off_rate, 0) || '%'              AS discountOffRate            ");
        sql.append("          , soi.shipment_qty                                       AS sl                         ");
        sql.append("          , ROUND(soi.selling_price, 0) * soi.shipment_qty        AS currencyVat                 ");
        sql.append("          , so.order_date                                          AS orderDate                  ");
        sql.append("          , mf.multi_address_flag                                  AS multiAddressFlag           ");
        sql.append("          , so.order_for_employee_flag                             AS orderForEmployeeFlag       ");
        sql.append("       FROM invoice inv                                                                          ");
        sql.append(" INNER JOIN invoice_item invitem                                                                 ");
        sql.append("         ON inv.invoice_id = invitem.invoice_id                                                  ");
        sql.append(" INNER JOIN mst_facility mf                                                                      ");
        sql.append("         ON inv.from_facility_id = mf.facility_id                                                ");
        sql.append(" INNER JOIN sales_order_item soi                                                                 ");
        sql.append("         ON soi.sales_order_item_id = invitem.related_so_item_id                                 ");
        sql.append(" INNER JOIN sales_order so                                                                       ");
        sql.append("         ON soi.sales_order_id = so.sales_order_id                                               ");
        sql.append(" INNER JOIN mst_organization mo                                                                  ");
        sql.append("         ON so.customer_id = mo.organization_id                                                  ");
        sql.append("  LEFT JOIN mst_code_info paytype                                                                ");
        sql.append("         ON paytype.code_dbid = so.payment_method_type                                           ");
        sql.append("  LEFT JOIN mst_code_info relationship                                                           ");
        sql.append("         ON relationship.code_dbid = so.employee_relation_ship                                   ");
        sql.append("      WHERE inv.invoice_id IN (:invoiceIds)                                                      ");
        sql.append("ORDER BY inv.invoice_no, soi.seq_no , invitem.product_cd, so.order_no                            ");
        params.put("invoiceIds", invoiceIds);
        return super.queryForList(sql.toString(), params, SPM020103PrintBO.class);
    }

    @Override
    public Page<SPQ020301BO> getInvoiceList(SPQ020301Form form, PJUserDetails uc) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        StringBuilder selSql = new StringBuilder();

        selSql.append("     SELECT i.invoice_id                         AS invoiceId            ");
        selSql.append("          , i.customer_cd                        AS customerCd           ");
        selSql.append("          , i.customer_nm                        AS customerNm           ");
        selSql.append("          , i.to_organization_id                 AS customerId           ");
        selSql.append("          , i.invoice_no                         AS invoiceNo            ");
        selSql.append("          , i.invoice_date                       AS invoiceDate          ");
        selSql.append("          , i.invoice_type                       AS invoiceType          ");
        selSql.append("          , ROUND(i.invoice_actual_amt)          AS invoiceAmountWithVAT ");
        selSql.append("          , ROUND(i.invoice_actual_amt_not_vat)  AS invoiceAmount        ");
        sql.append("          FROM invoice i                                                    ");
        sql.append("         WHERE i.site_id = :siteId                                          ");
        sql.append("           AND i.to_facility_id = :toFacilityId                             ");
        sql.append("           AND i.invoice_date >= :dateFrom                                  ");
        sql.append("           AND i.invoice_date <= :dateTo                                    ");

        if (!ObjectUtils.isEmpty(form.getCustomerId())) {
            sql.append(" AND i.to_organization_id = :toOrganizationId ");
            params.put("toOrganizationId", form.getCustomerId());
        }

        sql.append(" ORDER BY i.invoice_no ");

        params.put("siteId", uc.getDealerCode());
        params.put("toFacilityId", form.getPointId());
        params.put("dateFrom", form.getDateFrom());
        params.put("dateTo", form.getDateTo());

        String countSql = "SELECT COUNT(1) FROM ( " + "SELECT 1 " + sql.toString() + ") AS subquery; ";
        Pageable pageable = PageRequest.of(form.getCurrentPage() - 1, form.getPageSize());
        if (!form.isPageFlg()) {
            pageable = Pageable.unpaged();
        }
        Integer count = super.queryForSingle(countSql, params, Integer.class);

        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, SPQ020301BO.class, pageable), pageable, count);
    }

    @Override
    public List<SPQ020402BO> searchInvoiceInfo(SPQ020401Form model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT item.order_date      AS orderDate   ");
        sql.append("         , inv.invoice_id       AS invoiceId   ");
        sql.append("         , item.sales_order_no  AS orderNo     ");
        sql.append("         , inv.invoice_date     AS invoiceDate ");
        sql.append("         , inv.invoice_no       AS invoiceNo   ");
        sql.append("         , inv.vat_no           AS vatNo       ");
        sql.append("         , inv.serial_no        AS serialNo    ");
        sql.append("         , inv.customer_nm      AS customer    ");
        sql.append("         , inv.vat_mobile_phone AS phoneNo     ");
        sql.append("         , inv.delivery_address AS address     ");
        sql.append("         , inv.tax_code         AS taxCode     ");
        sql.append("         , inv.cashier_nm       AS cashier     ");

        sql.append("      , COUNT(item.invoice_item_id) AS shipmentLines                      ");
        sql.append("      , ROUND(SUM(CASE WHEN item.order_source_type = 'S001PART'           ");
        sql.append("                  AND item.product_classification = 'S001PART'            ");
        sql.append("                 THEN item.selling_price ELSE 0 END)) AS salesOrder       ");

        sql.append("      , ROUND(SUM(CASE WHEN item.order_source_type = 'S001SERVICE'        ");
        sql.append("                  AND item.product_classification = 'S001PART'            ");
        sql.append("                 THEN item.selling_price ELSE 0 END)) AS serviceOrder     ");

        sql.append("      , ROUND(SUM(CASE WHEN item.order_source_type = 'S001SERVICE'        ");
        sql.append("                  AND item.product_classification = 'S001SERVICE'         ");
        sql.append("                 THEN item.selling_price ELSE 0 END)) AS serviceCharge    ");

        sql.append("      , ROUND(SUM(item.amt)) AS amount                                    ");


        sql.append("      FROM invoice inv                      ");
        sql.append(" LEFT JOIN invoice_item item                ");
        sql.append("        ON inv.invoice_id = item.invoice_id ");

        sql.append(" WHERE inv.site_id = :siteId ");
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

        sql.append("   GROUP BY item.order_date, item.sales_order_no, inv.invoice_date, inv.invoice_id ");
        sql.append("          , inv.invoice_no, inv.vat_no, inv.serial_no, inv.customer_nm ");
        sql.append("          , inv.vat_mobile_phone, inv.delivery_address, inv.tax_code, inv.cashier_nm ");

        sql.append("  ORDER BY item.order_date,item.sales_order_no ");

        return super.queryForList(sql.toString(), params, SPQ020402BO.class);
    }

    @Override
    public List<SPM020202FunctionBO> getSalesReturnHistoryHeaderList(SPM020202Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT i.from_facility_id           AS pointId            ");
        sql.append("          , mf.facility_cd               AS pointCd            ");
        sql.append("          , mf.facility_nm               AS pointNm            ");
        sql.append("          , i.related_invoice_no         AS invoiceNo          ");
        sql.append("          , i.invoice_no                 AS returnInvoiceNo    ");
        sql.append("          , i.to_organization_id         AS customerId         ");
        sql.append("          , i.customer_cd                AS customerCd         ");
        sql.append("          , i.customer_nm                AS customerNm         ");
        sql.append("          , sum(ii.amt)                  AS returnAmt          ");
        sql.append("          , i.invoice_date               AS returnDate         ");
        sql.append("          , i.invoice_id                 AS returnInvoiceId    ");
        sql.append("       FROM invoice i                                          ");
        sql.append(" INNER JOIN invoice_item ii                                    ");
        sql.append("         ON i.invoice_id = ii.invoice_id                       ");
        sql.append("        AND i.site_id = ii.site_id                             ");
        sql.append(" INNER JOIN mst_facility mf                                    ");
        sql.append("         ON i.from_facility_id = mf.facility_id                ");
//        sql.append("        AND i.site_id = mf.site_id                             ");
        sql.append("      WHERE i.site_id = :siteId                                ");
        sql.append("        AND i.invoice_date >= :fromDate                        ");
        sql.append("        AND i.invoice_date <= :toDate                          ");
        sql.append("        AND i.invoice_type = :S038SALESRETURNINVOICE           ");

        params.put("S038SALESRETURNINVOICE",PJConstants.InvoiceType.SALES_RETURN_INVOICE.getCodeDbid());
        if(!ObjectUtils.isEmpty(form.getPointId())) {
            sql.append("        AND i.from_facility_id = :pointId                        ");
            params.put("pointId", form.getPointId());
        }

        if(!ObjectUtils.isEmpty(form.getCustomerId())) {
            sql.append("        AND i.to_organization_id = :customerId                          ");
            params.put("customerId", form.getCustomerId());
        }

        if(StringUtils.isNotBlank(form.getInvoiceNo())) {
            sql.append("        AND i.related_invoice_no = :invoiceNo                    ");
            params.put("invoiceNo", form.getInvoiceNo());
        }

        if(!ObjectUtils.isEmpty(form.getReturnInvoiceNo())) {
            sql.append("        AND i.invoice_no = :returnInvoiceNo                      ");
            params.put("returnInvoiceNo", form.getReturnInvoiceNo());
        }

        sql.append("   GROUP BY i.from_facility_id, mf.facility_cd, mf.facility_nm, i.related_invoice_no ");
        sql.append("          , i.invoice_no, i.to_organization_id, i.customer_nm, i.customer_cd, i.invoice_date, i.invoice_id");
        sql.append("   ORDER BY i.from_facility_id, i.related_invoice_no, i.invoice_no                   ");

        params.put("siteId", siteId);
        params.put("fromDate", form.getDateFrom());
        params.put("toDate", form.getDateTo());

        return super.queryForList(sql.toString(), params, SPM020202FunctionBO.class);
    }

    @Override
    public PartsSalesReturnInvoiceForFinancePrintBO getCommonPartsSalesReturnInvoiceForFinanceData(Long invoiceId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("       SELECT returninv.invoice_no                               AS returnInvoiceNo   ");
        sql.append("            , inv.invoice_no                                     AS invoiceNo         ");
        sql.append("            , mf.facility_cd || ' / ' || mf.facility_nm          AS point             ");
        sql.append("            , mo.organization_cd || ' / ' || mo.organization_nm  AS customer          ");
        sql.append("            , returninv.invoice_date                             AS returnDate        ");
        sql.append("       FROM invoice returninv                                                         ");
        sql.append(" INNER JOIN invoice inv                                                               ");
        sql.append("         ON returninv.related_invoice_id = inv.invoice_id                             ");
        sql.append(" INNER JOIN mst_facility mf                                                           ");
        sql.append("         ON mf.facility_id = returninv.from_facility_id                               ");
        sql.append(" INNER JOIN mst_organization mo                                                       ");
        sql.append("         ON mo.organization_id = returninv.consumer_id                                ");
        sql.append(" WHERE returninv.invoice_type = :S038SALESRETURNINVOICE                               ");
        sql.append("   AND inv.invoice_type = :S038SALESINVOICE                                           ");
        sql.append("   AND returninv.invoice_id = :invoiceId                                              ");

        params.put("S038SALESRETURNINVOICE", PJConstants.InvoiceType.SALES_RETURN_INVOICE.getCodeDbid());
        params.put("S038SALESINVOICE", PJConstants.InvoiceType.SALES_INVOICE.getCodeDbid());
        params.put("invoiceId", invoiceId);
        return super.queryForSingle(sql.toString(), params, PartsSalesReturnInvoiceForFinancePrintBO.class);
    }

    @Override
    public List<PartsSalesReturnInvoiceForFinancePrintDetailBO> getCommonPartsSalesReturnInvoiceForFinanceDetailList(Long invoiceId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT invitem.product_cd     AS partsNo                                                                ");
        sql.append("          , invitem.product_nm     AS partsNm                                                                ");
        sql.append("          , invitem.qty            AS returnQty                                                              ");
        sql.append("          , ROUND(invitem.selling_price, 0) AS returnPrice  ");
        sql.append("       FROM invoice_item invitem                                                                             ");
        sql.append(" INNER JOIN invoice inv                                                                                      ");
        sql.append("         ON invitem.invoice_id = inv.invoice_id                                                              ");
        sql.append("      WHERE inv.invoice_id = :invoiceId                                                                      ");

        params.put("invoiceId", invoiceId);
        return super.queryForList(sql.toString(), params, PartsSalesReturnInvoiceForFinancePrintDetailBO.class);
    }
}
