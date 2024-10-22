package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.parts.SPQ050501BO;
import com.a1stream.domain.custom.SpSalesDwRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

/**
*
* 功能描述: Parts MI 查询
*
* @author mid2215
*/
@Repository
public class SpSalesDwRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements SpSalesDwRepositoryCustom {

    /**
     * author: Tang Tiantian
     */
    @Override
    public List<SPQ050501BO> findPartsRetailPriceMIList(String siteId, String facilityCd, String year, String month, String customerCd) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("   SELECT product_cd AS productCd                ");
        sql.append("         ,product_nm AS productNm                ");
        sql.append("         ,standard_price AS standardPrice        ");
        sql.append("         ,sum(so_line) AS soLine                 ");
        sql.append("         ,sum(compliance_line) AS complianceLine ");
        sql.append("     FROM sp_sales_dw                            ");
        sql.append("    WHERE facility_cd = :facilityCd              ");
        sql.append("      AND target_year = :year                    ");
        sql.append("      AND target_month = :month                  ");
        sql.append("      AND customer_cd = :customerCd              ");
        sql.append("      AND site_id = :siteId                      ");
        sql.append("      AND product_classification = :S001PART     ");
        sql.append(" GROUP BY product_cd,product_nm,standard_price   ");
        sql.append(" ORDER BY product_cd                             ");

        params.put("S001PART", ProductClsType.PART.getCodeDbid());
        params.put("facilityCd",facilityCd);
        params.put("year",year);
        params.put("month",month);
        params.put("customerCd",customerCd);
        params.put("siteId",siteId);

        return super.queryForList(sql.toString(), params, SPQ050501BO.class);
    }
}
