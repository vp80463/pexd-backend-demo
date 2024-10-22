package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.parts.SPQ050401BO;
import com.a1stream.domain.custom.SpWhDwRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Repository
public class SpWhDwRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements SpWhDwRepositoryCustom {

    @Override
    public List<SPQ050401BO> findWhList(String siteId, String facilityCd, String targetYear, String targetMonth) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("         SELECT swd.target_day       AS targetDay            ");
        sql.append("              , swd.facility_cd      AS facilityCd           ");
        sql.append("              , swd.facility_nm      AS facility_nm          ");
        sql.append("              , sum(swd.receive_line    ) AS receiveLine     ");
        sql.append("              , sum(swd.picking_so_line ) AS pickingSoLine   ");
        sql.append("              , sum(swd.shipment_line   ) AS shipmentLine    ");
        sql.append("              , sum(swd.stocktaking_line) AS stocktakingLine ");
        sql.append("              , sum(swd.transfer_in_line) + sum(swd.transfer_out_line    ) AS transferLine ");
        sql.append("              , sum(swd.adjust_in_line  ) + sum(swd.adjust_out_line      ) AS adjustLine   ");
        sql.append("              , sum(swd.stored_po_line  ) + sum(swd.stored_so_return_line) AS storedLine   ");
        sql.append("           FROM sp_wh_operation swd                             ");
        sql.append("          WHERE swd.target_year = :targetYear            ");
        sql.append("            AND swd.target_month = :targetMonth          ");
        sql.append("            AND swd.site_id = :siteId                    ");
        sql.append("            AND swd.product_classification = :S001PART   ");

        params.put("S001PART", ProductClsType.PART.getCodeDbid());
        params.put("targetYear",targetYear);
        params.put("targetMonth",targetMonth);
        params.put("siteId",siteId);

        if (StringUtils.isNotBlank(facilityCd)) {
            sql.append("       AND swd.facility_cd = :facilityCd            ");
            params.put("facilityCd",facilityCd);
        }

        sql.append("        GROUP BY swd.target_day, swd.facility_cd, swd.facility_nm  ");
        sql.append("        ORDER BY swd.target_day ,swd.facility_cd         ");

        return super.queryForList(sql.toString(), params, SPQ050401BO.class);
    }
}
