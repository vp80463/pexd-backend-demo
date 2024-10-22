package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.parts.SPQ050201BO;
import com.a1stream.domain.custom.SpCustomerDwRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

/**
*
* 功能描述: Parts MI 查询
*
* @author mid2215
*/
@Repository
public class SpCustomerDwRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements SpCustomerDwRepositoryCustom {

    /**
     * author: Tang Tiantian
     */
    @Override
    public List<SPQ050201BO> findPartsMIList(String siteId, String facilityCd, String targetYear, String targetMonth, String customerCd) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT customer_cd   AS customerCd          ");
        sql.append("          ,customer_nm         AS customerNm    ");
        sql.append("          ,sum(so_line       ) AS soLine        ");
        sql.append("          ,sum(allocated_line) AS allocatedLine ");
        sql.append("          ,sum(allocated_amt ) AS allocatedAmt  ");
        sql.append("          ,sum(bo_line       ) AS boLine        ");
        sql.append("          ,sum(bo_amt        ) AS boAmt         ");
        sql.append("          ,sum(so_cancel_line) AS  soCancelLine ");
        sql.append("          ,CAST(sum(allocated_line)::numeric / sum(so_line) AS numeric(10,2)) AS allocatedRate  ");
        sql.append("          ,sum(shipment_line)  AS shipmentLine  ");
        sql.append("          ,sum(return_line  )  AS returnLine    ");
        sql.append("          ,sum(invoice_amt  )  AS invoiceAmt    ");
        sql.append("          ,sum(invoice_cost )  AS invoiceCost   ");
        sql.append("          ,sum(invoice_amt  )  -  sum(invoice_cost ) AS salesProfit  ");
        sql.append("      FROM sp_customer_dw scd             ");
        sql.append("     WHERE target_year = :targetYear            ");
        sql.append("       AND target_month = :targetMonth          ");
        sql.append("       AND facility_cd = :facilityCd            ");
        sql.append("       AND site_id = :siteId                    ");
        sql.append("       AND product_classification = :S001PART   ");

        params.put("S001PART", ProductClsType.PART.getCodeDbid());
        params.put("targetYear",targetYear);
        params.put("targetMonth",targetMonth);
        params.put("facilityCd",facilityCd);
        params.put("siteId",siteId);

        if(StringUtils.isNotEmpty(customerCd)) {

            sql.append("       AND customer_cd = :customerCd            ");
            params.put("customerCd",customerCd);
        }

        sql.append("  GROUP BY site_id                   ");
        sql.append("          ,account_month             ");
        sql.append("          ,target_year               ");
        sql.append("          ,facility_cd               ");
        sql.append("          ,facility_nm               ");
        sql.append("          ,product_classification    ");
        sql.append("          ,customer_cd               ");
        sql.append("          ,customer_nm               ");
        sql.append("  ORDER BY customer_cd               ");
        return super.queryForList(sql.toString(), params, SPQ050201BO.class);
    }
}
