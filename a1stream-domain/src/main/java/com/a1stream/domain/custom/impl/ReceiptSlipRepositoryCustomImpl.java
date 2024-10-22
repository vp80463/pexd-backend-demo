package com.a1stream.domain.custom.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.BatteryType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.ReceiptSlipStatus;
import com.a1stream.domain.bo.parts.PartsStoringListForFinancePrintBO;
import com.a1stream.domain.bo.parts.PartsStoringListForFinancePrintDetailBO;
import com.a1stream.domain.bo.parts.PartsStoringListForWarehousePrintDetailBO;
import com.a1stream.domain.bo.parts.SPQ030101BO;
import com.a1stream.domain.bo.unit.SDM010601BO;
import com.a1stream.domain.bo.unit.SDQ010602BO;
import com.a1stream.domain.bo.unit.SDQ010602DetailBO;
import com.a1stream.domain.custom.ReceiptSlipRepositoryCustom;
import com.a1stream.domain.form.parts.SPQ030101Form;
import com.a1stream.domain.form.unit.SDM010601Form;
import com.a1stream.domain.form.unit.SDQ010602Form;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ReceiptSlipRepositoryCustomImpl  extends JpaNativeQuerySupportRepository implements ReceiptSlipRepositoryCustom {

    @Override
    public List<SPQ030101BO> getReceiptSlipList(SPQ030101Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT rs.slip_no                           AS receiptNo          ");
        sql.append("          , rsi.case_no                          AS firstCaseNo        ");
        sql.append("          , rsi.product_cd                       AS parts              ");
        sql.append("          , rsi.product_nm                       AS partsName          ");
        sql.append("          , rsi.product_id                       AS partsId            ");
        sql.append("          , rsi.purchase_order_no                AS orderNo            ");
        sql.append("          , rsi.supplier_invoice_no              AS ymvnInvoiceNo      ");
        sql.append("          , rs.received_date                     AS receiptDate        ");
        sql.append("          , rsi.receipt_qty                      AS receiptQty         ");
        sql.append("          , rsi.receipt_price                    AS receiptPrice       ");
        sql.append("          , rsi.receipt_qty * rsi.receipt_price  AS total              ");
        sql.append("          , rs.inventory_transaction_type        AS transactionTypeCd  ");
        sql.append("          , sl2.completed_date                   AS completedDate      ");
        sql.append("          , sl2.completed_time                   AS completedTime      ");
        sql.append("          , sl2.instruction_date                 AS instructionDate    ");
        sql.append("          , sl2.instruction_time                 AS instructionTime    ");
        sql.append("          , sl.storing_line_id                   AS storingLineId      ");
        sql.append("          , rs.receipt_slip_id                   AS receiptSlipId      ");
        sql.append("       FROM receipt_slip rs                                            ");
        sql.append("  LEFT JOIN receipt_slip_item rsi                                      ");
        sql.append("         ON rsi.receipt_slip_id = rs.receipt_slip_id                   ");
        sql.append("  LEFT JOIN storing_line sl                                            ");
        sql.append("         ON sl.receipt_slip_item_id = rsi.receipt_slip_item_id         ");
        sql.append("  LEFT JOIN storing_list sl2                                           ");
        sql.append("         ON sl2.storing_list_id = sl.storing_list_id                   ");
        sql.append("      WHERE rs.received_facility_id = :pointId                         ");
        sql.append("        AND rs.site_id      = :siteId                                  ");

        params.put("pointId", form.getPointId());
        params.put("siteId", siteId);

        if (!Nulls.isNull(form.getSupplierId())) {
            sql.append("   AND rs.from_organization_id = :supplierId                       ");
            params.put("supplierId", form.getSupplierId());
        }
        if (StringUtils.isNotBlank(form.getDateFrom()) && StringUtils.isNotBlank(form.getDateTo())) {
            sql.append("   AND rs.received_date >= :dateFrom                               ");
            sql.append("   AND rs.received_date <= :dateTo                                 ");
            params.put("dateFrom", form.getDateFrom());
            params.put("dateTo", form.getDateTo());
        }
        if (!Nulls.isNull(form.getPartsId())) {
            sql.append("   AND rsi.product_id = :partsId                                   ");
            params.put("partsId", form.getPartsId());
        }
        if (StringUtils.isNotBlank(form.getReceiptNo())) {
            sql.append("   AND rs.slip_no = :receiptNo                                     ");
            params.put("receiptNo", form.getReceiptNo());
        }
        if (StringUtils.isNotBlank(form.getPurchaseOrderNo())) {
            sql.append("   AND rsi.purchase_order_no = :purchaseOrderNo                    ");
            params.put("purchaseOrderNo", form.getPurchaseOrderNo());
        }
        if (StringUtils.isNotBlank(form.getTransactionType())) {
            sql.append("   AND rs.inventory_transaction_type = :transactionType            ");
            params.put("transactionType", form.getTransactionType());
        }
        sql.append(" ORDER BY rs.slip_no, rsi.case_no, rsi.product_cd                      ");
        return super.queryForList(sql.toString(), params, SPQ030101BO.class);
    }

    @Override
    public List<SPQ030101BO> getReceiptSlipListByReceiptSlipItemId(List<Long> list, String siteId) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("      SELECT rs.slip_no                           AS receiptNo         ");
        sql.append("           , rsi.case_no                          AS firstCaseNo       ");
        sql.append("           , rsi.product_cd                       AS parts             ");
        sql.append("           , rsi.product_nm                       AS partsName         ");
        sql.append("           , rsi.product_id                       AS partsId           ");
        sql.append("           , rsi.purchase_order_no                AS orderNo           ");
        sql.append("           , rsi.supplier_invoice_no              AS ymvnInvoiceNo     ");
        sql.append("           , rs.received_date                     AS receiptDate       ");
        sql.append("           , rsi.receipt_qty                      AS receiptQty        ");
        sql.append("           , rsi.receipt_price                    AS receiptPrice      ");
        sql.append("           , rsi.receipt_qty * rsi.receipt_price  AS total             ");
        sql.append("           , rs.inventory_transaction_type        AS transactionType   ");
        sql.append("           , sl2.completed_date                   AS completedDate     ");
        sql.append("           , sl2.completed_time                   AS completedTime     ");
        sql.append("           , sl2.instruction_date                 AS instructionDate   ");
        sql.append("           , sl2.instruction_time                 AS instructionTime   ");
        sql.append("           , sl.storing_line_id                   AS storingLineId     ");
        sql.append("        FROM receipt_slip rs                                           ");
        sql.append("   LEFT JOIN receipt_slip_item rsi                                     ");
        sql.append("          ON rsi.receipt_slip_id = rs.receipt_slip_id                  ");
        sql.append("         AND rsi.site_id = rs.site_id                                  ");
        sql.append("   LEFT JOIN storing_line sl                                           ");
        sql.append("          ON sl.receipt_slip_item_id = rsi.receipt_slip_item_id        ");
        sql.append("         AND sl.site_id = rsi.site_id                                  ");
        sql.append("   LEFT JOIN storing_list sl2                                          ");
        sql.append("          ON sl2.storing_list_id = sl.storing_list_id                  ");
        sql.append("         AND sl2.site_id = sl.site_id                                  ");
        sql.append("         AND sl2.facility_id = sl.facility_id                          ");
        sql.append("       WHERE rsi.receipt_slip_item_id IN (:receiptSlipItemId)          ");
        sql.append("         AND rs.site_id = :siteId                                      ");

        params.put("receiptSlipItemId", list);
        params.put("siteId", siteId);
        sql.append(" ORDER BY rs.slip_no, rsi.case_no, rsi.product_cd                      ");
        return super.queryForList(sql.toString(), params, SPQ030101BO.class);
    }

    @Override
    public List<SPQ030101BO> getPartsReceiveAndRegisterPrintList(SPQ030101Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT rs.slip_no                                     AS receiptNo        ");
        sql.append("          , rs.received_date                               AS receiptDate      ");
        sql.append("          , rsi.product_cd                                 AS partsNo          ");
        sql.append("          , rsi.product_nm                                 AS partsName        ");
        sql.append("          , mf.facility_cd || ' / ' || mf.facility_nm      AS pointAbbr        ");
        sql.append("          , mo.organization_nm                             AS supplier         ");
        sql.append("          , mf2.facility_cd || ' / ' || mf2.facility_nm    AS fromPoint        ");
        sql.append("          , line.instuction_qty                            AS receiptQty       ");
        sql.append("          , line.storing_line_no                           AS lineNo           ");
        sql.append("          , line.case_no                                   AS caseNo           ");
        sql.append("          , line.stored_qty                                AS instrQty         ");
        sql.append("          , mci.code_data1                                 AS locationType     ");
        sql.append("          , inventory.primary_flag                         AS mainSign         ");
        sql.append("          , inventory.quantity                             AS locationStockQty ");
        sql.append("          , stock.quantity                                 AS boQty            ");
        sql.append("          , sli.location_cd                                AS locationCd       ");
        sql.append("          , so.order_no                                    AS orderNo          ");
        sql.append("          , line.storing_line_id                           AS storingLineId    ");
        sql.append("       FROM receipt_slip rs                                                    ");
        sql.append("  LEFT JOIN receipt_slip_item rsi                                              ");
        sql.append("         ON rs.receipt_slip_id = rsi.receipt_slip_id                           ");
        sql.append("  LEFT JOIN receipt_po_item_relation rpir                                      ");
        sql.append("         ON rpir.receipt_slip_item_id = rsi.receipt_slip_item_id               ");
        sql.append("  LEFT JOIN po_so_item_relation psir                                           ");
        sql.append("         ON rpir.order_item_id = psir.sales_order_item_id                      ");
        sql.append("  LEFT JOIN sales_order so                                                     ");
        sql.append("         ON so.sales_order_id = psir.sales_order_id                            ");
        sql.append("  LEFT JOIN mst_organization mo                                                ");
        sql.append("         ON mo.organization_id = rs.from_organization_id                       ");
        sql.append("  LEFT JOIN mst_facility mf                                                    ");
        sql.append("         ON mf.facility_id = rs.from_organization_id                           ");
        sql.append("  LEFT JOIN storing_line line                                                  ");
        sql.append("         ON line.receipt_slip_item_id = rsi.receipt_slip_item_id               ");
        sql.append("  LEFT JOIN storing_list list                                                  ");
        sql.append("         ON list.storing_list_id = line.storing_list_id                        ");
        sql.append("  LEFT JOIN mst_facility mf2                                                   ");
        sql.append("         ON mf2.facility_id = list.facility_id                                 ");
        sql.append("  LEFT JOIN storing_line_item sli                                              ");
        sql.append("         ON line.storing_line_id = sli.storing_line_id                         ");
        sql.append("  LEFT JOIN location loc                                                       ");
        sql.append("         ON loc.location_id = sli.location_id                                  ");
        sql.append("        AND (loc.location_type = :normal                                       ");
        sql.append("         OR loc.location_type = :tentative )                                   ");
        sql.append("  LEFT JOIN mst_code_info mci                                                  ");
        sql.append("         ON mci.code_dbid = loc.location_type                                  ");
        sql.append("  LEFT JOIN product_inventory inventory                                        ");
        sql.append("         ON inventory.product_id = rsi.product_id                              ");
        sql.append("  and inventory.location_id = sli.location_id                                  ");
        sql.append("  and inventory.facility_id = list.facility_id                                 ");
        sql.append("  LEFT JOIN product_stock_status stock                                         ");
        sql.append("         ON stock.product_id = rsi.product_id                                  ");
        sql.append("  and stock.facility_id = list.facility_id                                     ");
        sql.append("  and stock.product_stock_status_type = :boQty                                 ");
        sql.append("      WHERE rs.site_id      = :siteId                                          ");
        params.put("siteId", siteId);
        params.put("normal", PJConstants.LocationType.NORMAL.getCodeDbid());
        params.put("tentative", PJConstants.LocationType.TENTATIVE.getCodeDbid());
        params.put("boQty", PJConstants.SpStockStatus.BO_QTY.getCodeDbid());

        if (!Nulls.isNull(form.getPointId())) {
            sql.append("   AND rs.received_facility_id = :pointId                       ");
            params.put("pointId", form.getPointId());
        }

        if (!Nulls.isNull(form.getSupplierId())) {
            sql.append("   AND rs.from_organization_id = :supplierId                       ");
            params.put("supplierId", form.getSupplierId());
        }

        if (StringUtils.isNotBlank(form.getDateFrom()) && StringUtils.isNotBlank(form.getDateTo())) {
            sql.append("   AND rs.received_date >= :dateFrom                               ");
            sql.append("   AND rs.received_date <= :dateTo                                 ");
            params.put("dateFrom", form.getDateFrom());
            params.put("dateTo", form.getDateTo());
        }

        if (!Nulls.isNull(form.getPartsId())) {
            sql.append("   AND rsi.product_id = :partsId                                   ");
            params.put("partsId", form.getPartsId());
        }

        if (StringUtils.isNotBlank(form.getReceiptNo())) {
            sql.append("   AND rs.slip_no = :receiptNo                                     ");
            params.put("receiptNo", form.getReceiptNo());
        }

        if (StringUtils.isNotBlank(form.getPurchaseOrderNo())) {
            sql.append("   AND rsi.purchase_order_no = :purchaseOrderNo                    ");
            params.put("purchaseOrderNo", form.getPurchaseOrderNo());
        }

        if (StringUtils.isNotBlank(form.getTransactionType())) {
            sql.append("   AND rs.inventory_transaction_type = :transactionType            ");
            params.put("transactionType", form.getTransactionType());
        }

        sql.append(" ORDER BY rs.slip_no, line.storing_line_no, line.case_no, stock.quantity, line.instuction_qty, line.stored_qty desc                      ");
        return super.queryForList(sql.toString(), params, SPQ030101BO.class);
    }

    /**
    * 功能描述:如果count有数据，则提示check信息
    *
    *@author mid2178
    */
    @Override
    public Integer countPartsStoring(Long facilityId, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" select count(1)                                               ");
        sql.append("       from receipt_slip rs                                    ");
        sql.append(" inner join receipt_slip_item rsi                              ");
        sql.append("         on rs.site_id = rsi.site_id                           ");
        sql.append("        and rs.receipt_slip_id = rsi.receipt_slip_id           ");
        sql.append(" inner join storing_line sl                                    ");
        sql.append("         on sl.site_id = rsi.site_id                           ");
        sql.append("        and sl.receipt_slip_item_id = rsi.receipt_slip_item_id ");
        sql.append("      where rs.site_id = :siteId                               ");
        sql.append("        and rs.received_facility_id = :facilityId              ");
        sql.append("        and rs.product_classification = :productClassification ");
        sql.append("        and sl.stored_qty = 0                                  ");

        params.put("siteId", siteId);
        params.put("facilityId", facilityId);
        params.put("productClassification", ProductClsType.PART.getCodeDbid());

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }

    @Override
    public List<PartsStoringListForWarehousePrintDetailBO> getPrintPartsStoringListForWarehouseData(List<Long> receiptSlipIds) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT rs.slip_no                                     AS receiptNo        ");
        sql.append("          , rs.received_date                               AS receiptDate      ");
        sql.append("          , rsi.product_cd                                 AS partsNo          ");
        sql.append("          , rsi.product_nm                                 AS partsName        ");
        sql.append("          , mf.facility_cd || ' / ' || mf.facility_nm      AS pointAbbr        ");
        sql.append("          , mo.organization_nm                             AS supplier         ");
        sql.append("          , mf2.facility_cd || ' / ' || mf2.facility_nm    AS fromPoint        ");
        sql.append("          , COALESCE(line.instuction_qty, 0)               AS receiptQty       ");
        sql.append("          , line.storing_line_no                           AS lineNo           ");
        sql.append("          , line.case_no                                   AS caseNo           ");
        sql.append("          , line.instuction_qty                            AS instrQty         ");
        sql.append("          , mci.code_data1                                 AS locationType     ");
        sql.append("          , inventory.primary_flag                         AS mainSign         ");
        sql.append("          , COALESCE(inventory.quantity, 0)                AS locationStockQty ");
        sql.append("          , COALESCE(stock.quantity, 0)                    AS boQty            ");
        sql.append("          , sli.location_cd                                AS locationCd       ");
        sql.append("          , so.order_no                                    AS orderNo          ");
        sql.append("       FROM receipt_slip rs                                                    ");
        sql.append(" INNER JOIN receipt_slip_item rsi                                              ");
        sql.append("         ON rs.receipt_slip_id = rsi.receipt_slip_id                           ");
        sql.append("  LEFT JOIN receipt_po_item_relation rpir                                      ");
        sql.append("         ON rpir.receipt_slip_item_id = rsi.receipt_slip_item_id               ");
        sql.append("  LEFT JOIN po_so_item_relation psir                                           ");
        sql.append("         ON rpir.order_item_id = psir.sales_order_item_id                      ");
        sql.append("  LEFT JOIN sales_order so                                                     ");
        sql.append("         ON so.sales_order_id = psir.sales_order_id                            ");
        sql.append("  LEFT JOIN mst_organization mo                                                ");
        sql.append("         ON mo.organization_id = rs.from_organization_id                       ");
        sql.append("  LEFT JOIN mst_facility mf                                                    ");
        sql.append("         ON mf.facility_id = rs.from_organization_id                           ");
        sql.append("  LEFT JOIN storing_line line                                                  ");
        sql.append("         ON line.receipt_slip_item_id = rsi.receipt_slip_item_id               ");
        sql.append("  LEFT JOIN storing_list list                                                  ");
        sql.append("         ON list.storing_list_id = line.storing_list_id                        ");
        sql.append("  LEFT JOIN mst_facility mf2                                                   ");
        sql.append("         ON mf2.facility_id = list.facility_id                                 ");
        sql.append("  LEFT JOIN storing_line_item sli                                              ");
        sql.append("         ON line.storing_line_id = sli.storing_line_id                         ");
        sql.append("  LEFT JOIN location loc                                                       ");
        sql.append("         ON loc.location_id = sli.location_id                                  ");
        sql.append("        AND (loc.location_type = :normal                                       ");
        sql.append("         OR loc.location_type = :tentative )                                   ");
        sql.append("  LEFT JOIN mst_code_info mci                                                  ");
        sql.append("         ON mci.code_dbid = loc.location_type                                  ");
        sql.append("  LEFT JOIN product_inventory inventory                                        ");
        sql.append("         ON inventory.product_id = rsi.product_id                              ");
        sql.append("        AND inventory.location_id = sli.location_id                            ");
        sql.append("        AND inventory.facility_id = list.facility_id                           ");
        sql.append("  LEFT JOIN product_stock_status stock                                         ");
        sql.append("         ON stock.product_id = rsi.product_id                                  ");
        sql.append("        AND stock.facility_id = list.facility_id                               ");
        sql.append("        AND stock.product_stock_status_type = :boQty                           ");
        sql.append("      WHERE rs.receipt_slip_id IN (:receiptSlipIds)                            ");

        params.put("normal", PJConstants.LocationType.NORMAL.getCodeDbid());
        params.put("tentative", PJConstants.LocationType.TENTATIVE.getCodeDbid());
        params.put("boQty", PJConstants.SpStockStatus.BO_QTY.getCodeDbid());

        params.put("receiptSlipIds", receiptSlipIds);
        sql.append(" ORDER BY rs.slip_no, line.storing_line_no, line.case_no, stock.quantity, line.instuction_qty, line.stored_qty desc ");
        return super.queryForList(sql.toString(), params, PartsStoringListForWarehousePrintDetailBO.class);
    }

    @Override
    public List<PartsStoringListForFinancePrintDetailBO> getPartsStoringListForFinanceReport(List<Long> receiptSlipIds) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT line.storing_line_no                 AS lineNo        ");
        sql.append("          , line.case_no                         AS caseNo        ");
        sql.append("          , product.product_cd                   AS partsNo       ");
        sql.append("          , product.local_description            AS partsName     ");
        sql.append("          , CASE WHEN line.instuction_qty is NULL THEN 0          ");
        sql.append("                 ELSE line.instuction_qty end    AS receiptQty    ");
        sql.append("          , CASE WHEN item.receipt_price is NULL THEN 0           ");
        sql.append("                 ELSE item.receipt_price end     AS receiptCost   ");
        sql.append("       FROM receipt_slip slip                                     ");
        sql.append(" INNER JOIN receipt_slip_item item                                ");
        sql.append("         ON item.receipt_slip_id = slip.receipt_slip_id           ");
        sql.append(" INNER JOIN storing_line line                                     ");
        sql.append("         ON line.receipt_slip_item_id = item.receipt_slip_item_id ");
        sql.append(" INNER JOIN storing_list list                                     ");
        sql.append("         ON list.storing_list_id = line.storing_list_id           ");
        sql.append(" INNER JOIN mst_product product                                   ");
        sql.append("         ON product.product_id = line.product_id                  ");
        sql.append("      WHERE slip.receipt_slip_id IN (:receiptSlipIds)             ");

        params.put("receiptSlipIds", receiptSlipIds);
        sql.append(" ORDER BY line.storing_line_no, line.case_no , product.product_cd ");
        return super.queryForList(sql.toString(), params, PartsStoringListForFinancePrintDetailBO.class);
    }


    @Override
    public PartsStoringListForFinancePrintBO getPartsStoringListForFinanceReportHeader(List<Long> receiptSlipIds) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT facility.facility_cd          AS pointCd                ");
        sql.append("          , facility.facility_nm          AS pointNm                ");
        sql.append("          , mo.organization_nm            AS supplier               ");
        sql.append("          , slip.slip_no                  AS receiptNo              ");
        sql.append("          , slip.received_date            AS receiptDate            ");
        sql.append("          , facility.facility_cd || ' / ' || facility.facility_nm    AS pointAbbr  ");
        sql.append("       FROM receipt_slip slip                                       ");
        sql.append(" INNER JOIN mst_organization mo                                     ");
        sql.append("         ON slip.from_organization_id = mo.organization_id          ");
        sql.append(" INNER JOIN receipt_slip_item item                                  ");
        sql.append("         ON item.receipt_slip_id = slip.receipt_slip_id             ");
        sql.append(" INNER JOIN storing_line line                                       ");
        sql.append("         ON line.receipt_slip_item_id = item.receipt_slip_item_id   ");
        sql.append(" INNER JOIN storing_list list                                       ");
        sql.append("         ON list.storing_list_id = line.storing_list_id             ");
        sql.append(" INNER JOIN mst_facility facility                                   ");
        sql.append("         ON list.facility_id = facility.facility_id                 ");
        sql.append("      WHERE slip.receipt_slip_id IN (:receiptSlipIds)               ");
        sql.append(      "LIMIT 1                                                       ");

        params.put("receiptSlipIds", receiptSlipIds);
        return super.queryForSingle(sql.toString(), params, PartsStoringListForFinancePrintBO.class);
    }

    @Override
    public List<SDM010601BO> getFastReceiptReportList(SDM010601Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT rs.receipt_slip_status           AS receiptSlipStatus                         ");
        sql.append("         , rs.commercial_invoice_no         AS deliveryNoteNo                            ");
        sql.append("         , rs.supplier_delivery_date        AS deliveryDate                              ");
        sql.append("         , rs.inventory_transaction_type    AS transactionTypeCd                         ");
        sql.append("         , cmo.organization_cd              AS supplierCd                                ");
        sql.append("         , cmo.organization_nm              AS supplierNm                                ");
        sql.append("         , mf.facility_cd                   AS fromPointCd                               ");
        sql.append("         , rs.received_date                 AS receiptDate                               ");
        sql.append("         , sum(rsi.receipt_qty)             AS receiptQty                                ");
        sql.append("         , rs.receipt_slip_id               AS receiptSlipId                             ");
        sql.append("      FROM receipt_slip rs                                                               ");
        sql.append(" LEFT JOIN receipt_slip_item rsi                                                         ");
        sql.append("        ON rs.receipt_slip_id = rsi.receipt_slip_id                                      ");
        sql.append(" LEFT JOIN cmm_mst_organization cmo                                                      ");
        sql.append("        ON cmo.organization_id = rs.from_organization_id                                 ");
        sql.append(" LEFT JOIN mst_facility mf                                                               ");
        sql.append("        ON mf.facility_id  = rs.from_facility_id                                         ");
        sql.append("     WHERE rs.site_id = :siteId                                                          ");
        sql.append("       AND rs.received_facility_id = :receiptPointId                                     ");
        sql.append("       AND rs.product_classification = :productClassification                            ");

        params.put("siteId", form.getSiteId());
        params.put("receiptPointId", form.getReceiptPointId());
        params.put("productClassification", ProductClsType.GOODS.getCodeDbid());

        if (StringUtils.isNotBlank(form.getDeliveryDateFrom())) {
            sql.append(" AND rs.supplier_delivery_date >= :deliveryDateFrom ");
            params.put("deliveryDateFrom", form.getDeliveryDateFrom());
        }

        if (StringUtils.isNotBlank(form.getDeliveryDateTo())) {
            sql.append(" AND rs.supplier_delivery_date <= :deliveryDateTo ");
            params.put("deliveryDateTo", form.getDeliveryDateTo());
        }

        if (StringUtils.isNotBlank(form.getReceiptStatus())) {
            sql.append(" AND rs.receipt_slip_status = :receiptStatus ");
            params.put("receiptStatus", form.getReceiptStatus());
        } else {
            sql.append(" AND rs.receipt_slip_status IN (:receiptStatus) ");
            params.put("receiptStatus", Arrays.asList(ReceiptSlipStatus.STORED.getCodeDbid(), ReceiptSlipStatus.ONTRANSIT.getCodeDbid()));
        }

        if (StringUtils.isNotBlank(form.getDeliveryNoteNo())) {
            sql.append(" AND rs.commercial_invoice_no = :deliveryNoteNo ");
            params.put("deliveryNoteNo", form.getDeliveryNoteNo());
        }

        if (StringUtils.isNotBlank(form.getTransactionType())) {
            sql.append(" AND rs.inventory_transaction_type = :transactionType ");
            params.put("transactionType", form.getTransactionType());
        }

        if (!Nulls.isNull(form.getSupplierId())) {
            sql.append(" AND rs.from_organization_id = :supplierId ");
            params.put("supplierId", form.getSupplierId());
        }

        if (!Nulls.isNull(form.getFromPointId())) {
            sql.append(" AND rs.from_facility_id = :fromPointId ");
            params.put("fromPointId", form.getFromPointId());
        }

        if (StringUtils.isNotBlank(form.getReceiptDateFrom())) {
            sql.append(" AND rs.received_date >= :receiptDateFrom ");
            params.put("receiptDateFrom", form.getReceiptDateFrom());
        }

        if (StringUtils.isNotBlank(form.getReceiptDateTo())) {
            sql.append(" AND rs.received_date <= :receiptDateTo ");
            params.put("receiptDateTo", form.getReceiptDateTo());
        }

        sql.append("  GROUP BY rs.receipt_slip_status, rs.commercial_invoice_no, rs.supplier_delivery_date ");
        sql.append("         , rs.inventory_transaction_type, cmo.organization_cd, cmo.organization_nm     ");
        sql.append("         , mf.facility_cd, rs.from_facility_id, rs.received_date, rs.receipt_slip_id   ");
        sql.append("  ORDER BY commercial_invoice_no, rs.received_date                                     ");
        return super.queryForList(sql.toString(), params, SDM010601BO.class);
    }

    @Override
    public List<SDQ010602DetailBO> getFastReceiptReportDetailList(SDQ010602Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT rsi.product_cd      AS modelCd                       ");
        sql.append("         , rsi.product_nm      AS modelNm                       ");
        sql.append("         , rsi.color_nm        AS colorNm                       ");
        sql.append("         , sp.frame_no         AS frameNo                       ");
        sql.append("         , sp.engine_no        AS engineNo                      ");
        sql.append("         , b1.battery_no       AS batteryId1                    ");
        sql.append("         , b2.battery_no       AS batteryId2                    ");
        sql.append("    FROM receipt_slip rs                                        ");
        sql.append("    LEFT JOIN receipt_slip_item rsi                             ");
        sql.append("      ON rs.receipt_slip_id = rsi.receipt_slip_id               ");
        sql.append("    LEFT JOIN receipt_serialized_item rsi2                      ");
        sql.append("      ON rsi.receipt_slip_item_id = rsi2.receipt_slip_item_id   ");
        sql.append("    LEFT JOIN serialized_product sp                             ");
        sql.append("      ON sp.serialized_product_id = rsi2.serialized_product_id  ");
        sql.append("    LEFT JOIN battery b1                                        ");
        sql.append("      ON b1.serialized_product_id = sp.serialized_product_id    ");
        sql.append("     AND b1.position_sign = :battery1                           ");
        sql.append("    LEFT JOIN battery b2                                        ");
        sql.append("      ON b2.serialized_product_id = sp.serialized_product_id    ");
        sql.append("     AND b2.position_sign = :battery2                           ");
        sql.append("   WHERE rsi.receipt_slip_id = :receiptSlipId                   ");


        params.put("battery1", BatteryType.TYPE1.getCodeDbid());
        params.put("battery2", BatteryType.TYPE2.getCodeDbid());
        params.put("receiptSlipId", form.getReceiptSlipId());
        return super.queryForList(sql.toString(), params, SDQ010602DetailBO.class);
    }

    @Override
    public SDQ010602BO getFastReceiptReportDetail(SDQ010602Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT mf2.facility_cd  || ' ' ||  mf2.facility_nm     AS receiptPoint           ");
        sql.append("         , rs.received_date                                AS receiptDate            ");
        sql.append("         , rs.receipt_slip_status                          AS receiptStatus          ");
        sql.append("         , co.organization_cd || ' ' || co.organization_nm AS supplier               ");
        sql.append("         , rs.commercial_invoice_no                        AS deliveryNoteNo         ");
        sql.append("         , rs.supplier_delivery_date                       AS supplierDeliveryDate   ");
        sql.append("         , mf1.facility_cd || ' ' || mf1.facility_nm       AS fromPoint              ");
        sql.append("         , rs.inventory_transaction_type                   AS TransactionType        ");
        sql.append("         , rs.received_pic_nm                              AS pic                    ");
        sql.append("      FROM receipt_slip rs                                                           ");
        sql.append(" LEFT JOIN mst_facility mf1                                                          ");
        sql.append("        ON mf1.facility_id  = rs.from_facility_id                                    ");
        sql.append(" LEFT JOIN mst_facility mf2                                                          ");
        sql.append("        ON mf2.facility_id  = rs.received_facility_id                                ");
        sql.append(" LEFT JOIN cmm_mst_organization co                                                   ");
        sql.append("        ON co.organization_id  = rs.from_organization_id                             ");
        sql.append("     WHERE rs.receipt_slip_id = :receiptSlipId                                       ");

        params.put("receiptSlipId", form.getReceiptSlipId());
        return super.queryForSingle(sql.toString(), params, SDQ010602BO.class);
    }
}
