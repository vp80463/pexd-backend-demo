package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.bo.parts.SPM030301BO;
import com.a1stream.domain.bo.parts.SPQ030101BO;
import com.a1stream.domain.custom.StoringLineItemRepositoryCustom;
import com.a1stream.domain.form.parts.SPM030301Form;
import com.a1stream.domain.form.parts.SPQ030101Form;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class StoringLineItemRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements StoringLineItemRepositoryCustom {

    @Override
    public List<SPQ030101BO> getPartsReceiveListDetail(SPQ030101Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT sli.location_cd          AS registerLocation    ");
        sql.append("          , mci.code_data1           AS locationType        ");
        sql.append("          , sli.stored_qty           AS registerQty         ");
        sql.append("          , sli.completed_date       AS registerDate        ");
        sql.append("       FROM storing_line_item sli                           ");
        sql.append("  LEFT JOIN location l                                      ");
        sql.append("         ON l.location_id = sli.location_id                 ");
        sql.append("        AND l.site_id = sli.site_id                         ");
        sql.append("        AND l.facility_id = sli.facility_id                 ");
        sql.append("  LEFT JOIN mst_code_info mci                               ");
        sql.append("         ON mci.code_dbid = l.location_type                 ");
        sql.append("      WHERE sli.site_id = :siteId                           ");
        sql.append("        AND sli.storing_line_id = :storingLineId            ");

        if (!Nulls.isNull(form.getPointId())) {
            sql.append("    AND sli.facility_id = :pointId                      ");
            params.put("pointId", form.getPointId());
        }

        params.put("siteId", siteId);
        params.put("storingLineId", form.getStoringLineId());
        return super.queryForList(sql.toString(), params, SPQ030101BO.class);
    }

    @Override
    public List<SPM030301BO> getPartsStockRegisterList(SPM030301Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT sli.location_cd           AS location                ");
        sql.append("          , sli.location_id           AS locationId              ");
        sql.append("          , l.bin_type_id             AS BinTypeId               ");
        sql.append("          , mci.code_data1            AS locationType            "); 
        sql.append("          , l.location_type           AS locationTypeCd          ");
        sql.append("          , sli.instuction_qty        AS qty                     ");
        sql.append("          , sli.storing_line_item_id  AS storingLineItemId       ");
        sql.append("          , slt.storing_list_id       AS storingListId           ");
        sql.append("          , sle.receipt_slip_item_id  AS receiptSlipItemId       ");
        sql.append("          , rs.receipt_slip_id        AS receiptSlipId           ");
        sql.append("          , rsi.update_count          AS rsiUpdateCount          ");
        sql.append("          , rs.update_count           AS rsUpdateCount           ");
        sql.append("          , sli.update_count          AS sliUpdateCount          ");
        sql.append("          , sle.update_count          AS slUpdateCount           ");
        sql.append("       FROM storing_line_item sli                                ");
        sql.append("  LEFT JOIN location l                                           ");
        sql.append("         ON l.location_id = sli.location_id                      ");
        sql.append("        AND l.site_id = sli.site_id                              ");
        sql.append("        AND l.facility_id = sli.facility_id                      ");
        sql.append("  LEFT JOIN mst_code_info mci                                    ");
        sql.append("         ON mci.code_dbid = l.location_type                      ");
        sql.append("  LEFT JOIN storing_line sle                                     ");
        sql.append("         ON sle.storing_line_id = sli.storing_line_id            ");
        sql.append("  LEFT JOIN storing_list slt                                     ");
        sql.append("         ON slt.storing_list_id = sle.storing_list_id            ");
        sql.append("  LEFT JOIN receipt_slip_item rsi                                ");
        sql.append("         ON rsi.receipt_slip_item_id = sle.receipt_slip_item_id  ");
        sql.append("  LEFT JOIN receipt_slip rs                                      ");
        sql.append("         ON rs.receipt_slip_id = rsi.receipt_slip_id             ");
        sql.append("      WHERE sli.site_id = :siteId                                ");
        sql.append("        AND sli.storing_line_id = :storingLineId                 ");

        params.put("siteId", form.getSiteId());
        params.put("storingLineId", form.getStoringLineId());

        return super.queryForList(sql.toString(), params, SPM030301BO.class);
    }

}
