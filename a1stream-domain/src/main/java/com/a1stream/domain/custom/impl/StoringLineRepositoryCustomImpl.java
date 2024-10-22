package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.bo.parts.SPM031501BO;
import com.a1stream.domain.custom.StoringLineRepositoryCustom;
import com.a1stream.domain.form.parts.SPM031501Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class StoringLineRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements StoringLineRepositoryCustom {

    @Override
    public List<SPM031501BO> getPartsStockRegisterMultiLinesList(SPM031501Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT sl.storing_line_no             AS lineNo                    ");
        sql.append("          , sl.case_no                     AS caseNo                    ");
        sql.append("          , sl.product_cd                  AS partsNo                   ");
        sql.append("          , sl.product_id                  AS partsId                   ");
        sql.append("          , sli.instuction_qty             AS receiptedQty              ");
        sql.append("          , sli.location_cd                AS location                  ");
        sql.append("          , sli.location_id                AS locationId                ");
        sql.append("          , l.bin_type_id                  AS binTypeId                 ");
        sql.append("          , mci.code_data1                 AS locationType              ");
        sql.append("          , l.location_type                AS locationTypeId            ");
        sql.append("          , pi.primary_flag                AS mainLocationSign          ");
        sql.append("          , sli.instuction_qty             AS instructionQty            ");
        sql.append("          , sli.instuction_qty             AS registerQty               ");
        sql.append("          , sli.storing_line_id            AS storingLineId             ");
        sql.append("          , sli.storing_line_item_id       AS storingLineItemId         ");
        sql.append("          , sl.storing_list_id             AS storingListId             ");
        sql.append("          , sl.receipt_slip_item_id        AS receiptSlipItemId         ");
        sql.append("          , rsi.receipt_slip_id            AS receiptSlipId             ");
        sql.append("          , rsi.receipt_slip_id            AS receiptSlipId             ");
        sql.append("          , sl.stored_qty                  AS storedQty                 ");
        sql.append("          , sl.frozen_qty                  AS frozenQty                 ");
        sql.append("          , rs.inventory_transaction_type  AS inventoryTransactionType  ");
        sql.append("          , sl.completed_date              AS completedDate             ");
        sql.append("          , rsi.update_count               AS rsiUpdateCount            ");
        sql.append("          , rs.update_count                AS rsUpdateCount             ");
        sql.append("          , sli.update_count               AS sliUpdateCount            ");
        sql.append("          , sl.update_count                AS slUpdateCount             ");
        sql.append("       FROM storing_line sl                                             ");
        sql.append("  LEFT JOIN storing_line_item sli                                       ");
        sql.append("         ON sli.storing_line_id = sl.storing_line_id                    ");
        sql.append("        AND sli.site_id  = sl.site_id                                   ");
        sql.append("        AND sli.facility_id  = sl.facility_id                           ");
        sql.append("  LEFT JOIN location l                                                  ");
        sql.append("         ON l.location_id = sli.location_id                             ");
        sql.append("        AND l.site_id = sli.site_id                                     ");
        sql.append("        AND l.facility_id = sli.facility_id                             ");
        sql.append("  LEFT JOIN mst_code_info mci                                           ");
        sql.append("         ON mci.code_dbid = l.location_type                             ");
        sql.append("  LEFT JOIN product_inventory pi                                        ");
        sql.append("         ON pi.site_id = sl.site_id                                     ");
        sql.append("        AND pi.facility_id = sl.facility_id                             ");
        sql.append("        AND pi.product_id  = sl.product_id                              ");
        sql.append("        AND pi.location_id = sli.location_id                            ");
        sql.append("  LEFT JOIN receipt_slip_item rsi                                       ");
        sql.append("         ON rsi.receipt_slip_item_id = sl.receipt_slip_item_id          ");
        sql.append("  LEFT JOIN receipt_slip rs                                             ");
        sql.append("         ON rs.receipt_slip_id = rsi.receipt_slip_id                    ");
        sql.append("      WHERE sl.site_id = :siteId                                        ");
        sql.append("        AND sl.facility_id = :facilityId                                ");
        sql.append("        AND sl.receipt_slip_no = :receiptNo                             ");

        params.put("siteId", form.getSiteId());
        params.put("facilityId", form.getPointId());
        params.put("receiptNo", form.getReceiptNo());

        return super.queryForList(sql.toString(), params, SPM031501BO.class);
    }

}
