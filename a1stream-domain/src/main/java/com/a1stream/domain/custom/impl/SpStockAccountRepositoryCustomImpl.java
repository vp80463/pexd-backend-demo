package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.parts.SPQ050801BO;
import com.a1stream.domain.bo.parts.SPQ050802BO;
import com.a1stream.domain.custom.SpStockAccountRepositoryCustom;
import com.a1stream.domain.form.parts.SPQ050801Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

@Repository
public class SpStockAccountRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements SpStockAccountRepositoryCustom {

    /**
     * 功能描述:spq050802初始化查出数据
     *
     * @author mid2178
     */
    @Override
    public List<SPQ050802BO> getAccountReportData(String siteId, String targetMonth, String pointCd, String largeGroupCd){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT   target_day                           AS   date                 ");
        sql.append("        , ROUND(sum(begin_month_stock_amt), 0) AS   beginningStockAmount ");
        sql.append("        , ROUND(sum(receipt_amt), 0)           AS   receiptAmount        ");
        sql.append("        , ROUND(sum(sales_cost_amt), 0)        AS   salesCost            ");
        sql.append("        , ROUND(sum(return_cost_amt), 0)       AS   returnCost           ");
        sql.append("        , ROUND(sum(disposal_amt), 0)          AS   disposalCost         ");
        sql.append("        , ROUND(sum(adjust_minus_amt), 0)      AS   adjustmentNegative   ");
        sql.append("        , ROUND(sum(adjust_plus_amt), 0)       AS   adjustmentPositive   ");
        sql.append("        , ROUND(sum(transfer_in_amt), 0)       AS   transferAmountOut    ");
        sql.append("        , ROUND(sum(transfer_out_amt), 0)      AS   transferAmountIn     ");
        sql.append("        , ROUND(sum(balance_cost_amt), 0)      AS   balanceOfCost        ");
        sql.append("        , ROW_NUMBER() OVER (ORDER BY target_day) AS seq                 ");
        sql.append("     FROM sp_stock_account                                               ");
        sql.append("    WHERE site_id =:siteId                                               ");

        if(StringUtils.isNotBlank(targetMonth)) {
            sql.append("      AND account_month  =:targetMonth                     ");
            params.put("targetMonth",targetMonth);
        }
        if(StringUtils.isNotBlank(pointCd)) {
            sql.append("      AND facility_cd =:pointCd                            ");
            params.put("pointCd",pointCd);
        }
        if(StringUtils.isNotBlank(largeGroupCd)) {
            sql.append("      AND large_group_cd =:largeGroupCd                    ");
            params.put("largeGroupCd",largeGroupCd);
        }

        sql.append(" GROUP BY site_id , account_month , facility_cd , product_classification , target_day  ");
        sql.append(" ORDER BY target_day                                           ");

        params.put("siteId",siteId);

        return super.queryForList(sql.toString(), params, SPQ050802BO.class);
    }

    /**
     * author: Tang Tiantian
     */
    @Override
    public List<SPQ050801BO> findStockAccountList(SPQ050801Form model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT facility_cd AS pointCd                           ");
        sql.append("           ,facility_nm AS pointNm                           ");
        sql.append("           ,sum(begin_month_stock_amt) AS beginningStockAmt     ");
        sql.append("           ,sum(receipt_amt)      AS receiptAmt                 ");
        sql.append("           ,sum(sales_cost_amt)   AS salesCostAmt               ");
        sql.append("           ,sum(return_cost_amt)  AS returnCostAmt              ");
        sql.append("           ,sum(disposal_amt)     AS disposalAmt                ");
        sql.append("           ,sum(adjust_plus_amt)  AS adjustmentPlusAmt          ");
        sql.append("           ,sum(adjust_minus_amt) AS adjustmentMinusAmt         ");
        sql.append("           ,sum(transfer_in_amt)  AS transferInAmt              ");
        sql.append("           ,sum(transfer_out_amt) AS transferOutAmt             ");
        sql.append("           ,sum(balance_cost_amt) AS balanceOfCostAmt           ");
        sql.append("           ,sum(begin_month_stock_amt) + sum(receipt_amt) - sum(sales_cost_amt) + sum(return_cost_amt)   ");
        sql.append("          - sum(disposal_amt) - sum(adjust_minus_amt) + sum(adjust_plus_amt) - sum(transfer_in_amt)      ");
        sql.append("          + sum(transfer_out_amt) + sum(balance_cost_amt) AS logicStockAmt                               ");
        sql.append("           ,0.00 AS stockMonthTarget            ");
        sql.append("       FROM sp_stock_account ssa                ");
        sql.append("      WHERE ssa.target_year = :targetYear       ");
        sql.append("        AND ssa.target_month = :targetMonth     ");
        sql.append("        AND ssa.facility_cd = :pointCd          ");
        sql.append("        AND site_id =:siteId                    ");
        sql.append("        AND product_classification = :S001PART  ");

        if(StringUtils.isNoneBlank(model.getLargeGroupCd())) {

            sql.append("        AND ssa.large_group_cd = :largeGroupCd                    ");
            params.put("largeGroupCd",model.getLargeGroupCd());
        }

        params.put("siteId",model.getSiteId());
        params.put("targetYear",StringUtils.substring(model.getTargetMonth(), 0, 4));
        params.put("targetMonth",StringUtils.substring(model.getTargetMonth(), 4, 6));
        params.put("pointCd",model.getPointCd());
        params.put("S001PART", ProductClsType.PART.getCodeDbid());

        sql.append("   GROUP BY site_id ,account_month ,facility_cd ,facility_nm, product_classification  ");
        sql.append("   ORDER BY ssa.facility_cd         ");

        return super.queryForList(sql.toString(), params, SPQ050801BO.class);
    }
}
