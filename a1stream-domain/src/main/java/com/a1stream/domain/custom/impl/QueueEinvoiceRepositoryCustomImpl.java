package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.master.CMM071601BO;
import com.a1stream.domain.custom.QueueEinvoiceRepositoryCustom;
import com.a1stream.domain.form.master.CMM071601Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class QueueEinvoiceRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements QueueEinvoiceRepositoryCustom {

    @Override
    public List<CMM071601BO> getInvoiceCheckResultByInvoice(CMM071601Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT :charInvoice                        AS type                 ");
        sql.append("         , qe.invoice_date                     AS date                 ");
        sql.append("         , qe.interf_code                      AS interfaceType        ");

        sql.append("         , i.invoice_no                        AS invoiceNo            ");
        sql.append("         , ii.sales_order_no                   AS orderNo              ");
        sql.append("         , qe.status                           AS status               ");
        sql.append("         , qe.send_times                       AS sendTimes            ");
        sql.append("         , qe.status_message                   AS returnMessage        ");
        sql.append("         , qe.related_invoice_id               AS relatedInvoiceId     ");
        sql.append("         , :charN                              AS statusChangeFlag     ");
        sql.append("      FROM queue_einvoice qe                                           ");
        sql.append(" LEFT JOIN invoice_item ii                                             ");
        sql.append("        ON qe.related_invoice_id = ii.invoice_id                       ");
        sql.append("       AND qe.related_order_id = ii.sales_order_id                     ");
        sql.append(" LEFT JOIN invoice i                                                   ");
        sql.append("        ON i.invoice_id = ii.invoice_id                                ");
        sql.append("     WHERE qe.invoice_date BETWEEN :dateFrom AND :dateTo               ");

        params.put("charInvoice", CommonConstants.CHAR_INVOICE);
        params.put("dateFrom", form.getDateFrom());
        params.put("dateTo", form.getDateTo());
        params.put("charN", CommonConstants.CHAR_N);

        if (StringUtils.isNotBlank(form.getStatus())) {
            sql.append(" AND qe.status = :status ");
            params.put("status", form.getStatus());
        }

        return super.queryForList(sql.toString(), params, CMM071601BO.class);
    }

    @Override
    public List<CMM071601BO> getInvoiceCheckResult(CMM071601Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT :charInvoice                        AS type                 ");
        sql.append("         , qe.invoice_date                     AS date                 ");
        sql.append("         , qe.interf_code                      AS interfaceType        ");
        sql.append("         , i.invoice_no                        AS invoiceNo            ");
        sql.append("         , ii.sales_order_no                   AS orderNo              ");
        sql.append("         , qe.status                           AS status               ");
        sql.append("         , qe.send_times                       AS sendTimes            ");
        sql.append("         , qe.status_message                   AS returnMessage        ");
        sql.append("         , qe.related_invoice_id               AS relatedInvoiceId     ");
        sql.append("         , :charN                              AS statusChangeFlag     ");
        sql.append("      FROM queue_einvoice qe                                           ");
        sql.append(" LEFT JOIN invoice_item ii                                             ");
        sql.append("        ON qe.related_invoice_id = ii.invoice_id                       ");
        sql.append("       AND qe.related_order_id = ii.sales_order_id                     ");
        sql.append(" LEFT JOIN invoice i                                                   ");
        sql.append("        ON i.invoice_id = ii.invoice_id                                ");
        sql.append("     WHERE qe.invoice_date BETWEEN :dateFrom AND :dateTo               ");

        if (StringUtils.isNotBlank(form.getStatus())) {
            sql.append("   AND qe.status = :status                                         ");
        }

        sql.append("     UNION ALL                                                         ");
        sql.append("    SELECT :charTaxAuthority                 AS type                   ");
        sql.append("         , qa.invoice_date                   AS date                   ");
        sql.append("         , qa.interf_code                    AS interfaceType          ");
        sql.append("         , i.invoice_no                      AS invoiceNo              ");
        sql.append("         , ii.sales_order_no                 AS orderNo                ");
        sql.append("         , qa.status                         AS status                 ");
        sql.append("         , qa.send_times                     AS sendTimes              ");
        sql.append("         , qa.status_message                 AS returnMessage          ");
        sql.append("         , qa.related_invoice_id             AS relatedInvoiceId       ");
        sql.append("         , :charN                            AS statusChangeFlag       ");
        sql.append("      FROM queue_tax_authority qa                                      ");
        sql.append(" LEFT JOIN invoice_item ii                                             ");
        sql.append("        ON qa.related_invoice_id = ii.invoice_id                       ");
        sql.append("       AND qa.related_order_id = ii.sales_order_id                     ");
        sql.append(" LEFT JOIN invoice i                                                   ");
        sql.append("        ON i.invoice_id = ii.invoice_id                                ");
        sql.append("     WHERE qa.invoice_date BETWEEN :dateFrom AND :dateTo               ");

        if (StringUtils.isNotBlank(form.getStatus())) {
            sql.append("   AND qa.status = :status                                         ");
        }

        params.put("charInvoice", CommonConstants.CHAR_INVOICE);
        params.put("charTaxAuthority", CommonConstants.CHAR_TAXAUTHORITY);
        params.put("charN", CommonConstants.CHAR_N);
        params.put("dateFrom", form.getDateFrom());
        params.put("dateTo", form.getDateTo());

        if (StringUtils.isNotBlank(form.getStatus())) {
            params.put("status", form.getStatus());
        }

        return super.queryForList(sql.toString(), params, CMM071601BO.class);
    }

}
