package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.parts.SPQ050101BO;
import com.a1stream.domain.bo.parts.SPQ050301BO;
import com.a1stream.domain.custom.SpGeneralyyyyRepositoryCustom;
import com.a1stream.domain.form.parts.SPQ050101Form;
import com.a1stream.domain.form.parts.SPQ050301Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

/**
*
* 功能描述: Parts MI 查询
*
* @author mid2215
*/
@Repository
public class SpGeneralyyyyRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements SpGeneralyyyyRepositoryCustom {

    /**
     * author: Tang Tiantian
     */
    @Override
    public List<SPQ050101BO> findPartsMIList(SPQ050101Form model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("  SELECT sg.facility_cd          AS  pointCd      ");
        sql.append("        ,sg.facility_nm          AS pointNm       ");
        sql.append("        ,sg.middle_group_nm      AS middleGroupNm ");
        sql.append("        ,sg.middle_group_cd      AS middleGroupCd ");
        sql.append("        ,sum(sg.so_line      )   AS soLine        ");
        sql.append("        ,sum(sg.allocate_line)   AS allocateLine  ");
        sql.append("        ,sum(sg.bo_line      )   AS boLine        ");
        sql.append("        ,sum(sg.cancel_line  )   AS cancelLine    ");
        sql.append("        ,sum(sg.shipment_line)   AS shipmentLine  ");
        sql.append("        ,sum(sg.return_line  )   AS returnLine    ");
        sql.append("        ,sum(sg.po_line      )   AS poLine        ");
        sql.append("        ,sum(sg.po_eo_line   )   AS poEoLine      ");
        sql.append("        ,sum(sg.po_ro_line   )   AS poRoLine      ");
        sql.append("        ,sum(sg.po_ho_line   )   AS poHoLine      ");
        sql.append("        ,sum(sg.po_wo_line   )   AS poWoLine      ");
        sql.append("        ,sum(sg.receipt_line )   AS receiptLine   ");
        sql.append("        ,sum(sg.so_amt       )   AS soAmt         ");
        sql.append("        ,sum(sg.allocate_amt )   AS allocatedAmt  ");
        sql.append("        ,sum(sg.bo_amt       )   AS boAmt         ");
        sql.append("        ,sum(sg.cancel_amt   )   AS cancelAmt     ");
        sql.append("        ,sum(sg.shipment_amt )   AS shipmentAmt   ");
        sql.append("        ,sum(sg.return_amt   )   AS returnAmt     ");
        sql.append("        ,sum(sg.po_amt       )   AS poAmt         ");
        sql.append("        ,sum(sg.po_eo_amt    )   AS poEoAmt       ");
        sql.append("        ,sum(sg.po_ro_amt    )   AS poRoAmt       ");
        sql.append("        ,sum(sg.po_ho_amt    )   AS poHoAmt       ");
        sql.append("        ,sum(sg.po_wo_amt    )   AS poWoAmt       ");
        sql.append("        ,sum(sg.receive_amt  )   AS receiveAmt    ");
        sql.append("        ,CAST(sum(sg.allocate_line::numeric) / NULLIF(sum(sg.so_line),0) AS numeric(10,2))    AS allocateRate      ");
        sql.append("        ,CAST(sum(sg.po_eo_line::numeric   ) / NULLIF(sum(sg.po_line),0) AS numeric(10,2))    AS poEoRate          ");
        sql.append("        ,CAST(sum(sg.po_ro_line::numeric   ) / NULLIF(sum(sg.po_line),0) AS numeric(10,2))    AS poRoRate          ");
        sql.append("        ,CAST(sum(sg.po_ho_line::numeric   ) / NULLIF(sum(sg.po_line),0) AS numeric(10,2))    AS poHoRate          ");
        sql.append("        ,CAST(sum(sg.po_wo_line::numeric   ) / NULLIF(sum(sg.po_line),0) AS numeric(10,2))    AS poWoRate          ");
        sql.append("        ,CAST(sum(sg.allocate_amt::numeric ) / NULLIF(sum(sg.so_amt),0) AS numeric(10,2))    AS allocateAmtRate   ");
        sql.append("        ,CAST(sum(sg.po_eo_amt::numeric    ) / NULLIF(sum(sg.po_amt ),0) AS numeric(10,2))    AS poEoAmtRate       ");
        sql.append("        ,CAST(sum(sg.po_ro_amt::numeric    ) / NULLIF(sum(sg.po_amt ),0) AS numeric(10,2))    AS poRoAmtRate       ");
        sql.append("        ,CAST(sum(sg.po_ho_amt::numeric    ) / NULLIF(sum(sg.po_amt ),0) AS numeric(10,2))    AS poHoAmtRate       ");
        sql.append("        ,CAST(sum(sg.po_wo_amt::numeric    ) / NULLIF(sum(sg.po_amt ),0) AS numeric(10,2))    AS poWoAmtRate       ");
        sql.append("        FROM sp_general_"+ StringUtils.substring(model.getTargetMonth(), 0, 4) + " sg              ");
        sql.append("   WHERE sg.target_year = :targetYear     ");
        sql.append("     AND sg.target_month = :targetMonth   ");
        sql.append("     AND sg.site_id = :siteId             ");
        sql.append("     AND product_classification = :S001PART ");

        params.put("targetYear",StringUtils.substring(model.getTargetMonth(), 0, 4));
        params.put("targetMonth",StringUtils.substring(model.getTargetMonth(), 4, 6));
        params.put("S001PART", ProductClsType.PART.getCodeDbid());
        params.put("siteId",model.getSiteId());

        if(StringUtils.isNotEmpty(model.getPointCd())) {
            sql.append("     AND sg.facility_cd = :pointCd        ");
            params.put("pointCd",model.getPointCd());
        }

        if(StringUtils.isNotEmpty(model.getMiddleGroupCd())) {
            sql.append("       AND sg.middle_group_cd = :middleGroupCd            ");
            params.put("middleGroupCd",model.getMiddleGroupCd());
        }

        if(StringUtils.isNotEmpty(model.getAbcType())) {
            sql.append("       AND sg.abc_type = :abcType            ");
            params.put("abcType",model.getAbcType());
        }

        sql.append("        GROUP BY facility_cd ,facility_nm ,middle_group_cd ,middle_group_nm  ");
        sql.append("        ORDER BY sg.facility_cd ,sg.middle_group_cd         ");

        return super.queryForList(sql.toString(), params, SPQ050101BO.class);
    }

    /**
     * author: Tang Tiantian
     */
    @Override
    public List<SPQ050301BO> findPartsMIList(SPQ050301Form model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT sg.facility_cd          AS pointCd        ");
        sql.append("           ,sg.facility_nm          AS pointNm        ");
        sql.append("           ,sum(sg.so_line      )   AS soLine         ");
        sql.append("           ,sum(sg.allocate_line)   AS allocateLine   ");
        sql.append("           ,sum(sg.bo_line      )   AS boLine         ");
        sql.append("           ,sum(sg.bo_amt       )   AS boAmt          ");
        sql.append("           ,sum(sg.shipment_amt )   AS shipmentAmt    ");
        sql.append("           ,sum(sg.shipment_cost)   AS shipmentCost   ");
        sql.append("           ,sum(sg.return_amt   )   AS returnAmt      ");
        sql.append("           ,sum(sg.return_Cost  )   AS returnCost     ");
        sql.append("      FROM sp_general_"+ StringUtils.substring(model.getTargetMonth(), 0, 4) + " sg              ");
        sql.append("      WHERE sg.target_year = :targetYear       ");
        sql.append("        AND sg.target_month = :targetMonth     ");
        sql.append("        AND sg.site_id = :siteId               ");
        sql.append("        AND product_classification = :S001PART ");

        params.put("siteId",model.getSiteId());
        params.put("S001PART", ProductClsType.PART.getCodeDbid());
        params.put("targetYear",StringUtils.substring(model.getTargetMonth(), 0, 4));
        params.put("targetMonth",StringUtils.substring(model.getTargetMonth(), 4, 6));

        if (StringUtils.isNotEmpty(model.getPointCd())) {
            sql.append("       AND sg.facility_cd = :pointCd            ");
            params.put("pointCd",model.getPointCd());
        }

        if(StringUtils.isNotEmpty(model.getLargeGroupCd())) {
            sql.append("       AND sg.large_group_cd = :largeGroupCd            ");
            params.put("largeGroupCd",model.getLargeGroupCd());
        }

        sql.append("   GROUP BY facility_cd, facility_nm, account_month ,target_year  ");
        sql.append("        ORDER BY sg.facility_cd         ");

        return super.queryForList(sql.toString(), params, SPQ050301BO.class);
    }
}
