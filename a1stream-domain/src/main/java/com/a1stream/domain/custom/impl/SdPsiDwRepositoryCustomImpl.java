package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.bo.unit.SDQ070401BO;
import com.a1stream.domain.bo.unit.SDQ070601BO;
import com.a1stream.domain.custom.SdPsiDwRepositoryCustom;
import com.a1stream.domain.form.unit.SDQ070401Form;
import com.a1stream.domain.form.unit.SDQ070601Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

/**
*
* 功能描述: Parts MI 查询
*
* @author mid2215
*/
@Repository
public class SdPsiDwRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements SdPsiDwRepositoryCustom {

    /**
     * author: Tang Tiantian
     */
    @Override
    public PageImpl<SDQ070401BO> findSdPsiDwList(SDQ070401Form model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        StringBuilder selSql = new StringBuilder();

        selSql.append("    SELECT SUBSTR(spd.psi_date , 1, 6)             as psiMonth      ");
        selSql.append("                , spd.facility_cd                  as point         ");
        selSql.append("                , sum(spd.begining_stock)          as initialStock  ");
        selSql.append("                , sum(spd.ymvn_in)                 as ymvn          ");
        selSql.append("                , sum(spd.whole_sales_in)          as wholeSalesIn  ");
        selSql.append("                , sum(spd.transfer_in)             as transferIn    ");
        selSql.append("                , sum(spd.retail_out)              as retail        ");
        selSql.append("                , sum(spd.whole_sales_out)         as wholeSalesOut ");
        selSql.append("                , sum(spd.transfer_out)            as transferOut   ");
        selSql.append("                , sum(spd.in_transit)              as inTransit     ");
        selSql.append("                , sum(spd.begining_stock) + sum(spd.whole_sales_in) + sum(spd.ymvn_in) + sum(spd.transfer_in) ");
        selSql.append("                - sum(spd.retail_out) - sum(spd.whole_sales_out) - sum(spd.transfer_out) as endStock");
        
        sql.append("     FROM sd_psi_dw spd                                ");
        sql.append("    WHERE spd.site_id = :siteId                        ");
        sql.append("      AND SUBSTR(spd.psi_date , 1, 6) >= :fromMonth    ");
        sql.append("      AND SUBSTR(spd.psi_date , 1, 6) <= :targetMonth  ");

        if(model.getPointId()!=null){
            sql.append("      AND spd.facility_id = :facilityId            ");
            params.put("facilityId",model.getPointId());
        }

        params.put("siteId",model.getSiteId());
        params.put("fromMonth", model.getFromMonth());
        params.put("targetMonth",model.getToMonth());

        sql.append("  GROUP BY spd.facility_cd , SUBSTR(spd.psi_date , 1, 6)                   ");
        sql.append("  ORDER BY spd.facility_cd asc, SUBSTR(spd.psi_date , 1, 6) asc            ");

        String countSql = "SELECT COUNT(1) FROM ( " + "SELECT 1 " + sql.toString() + ") AS subquery; ";
        Pageable pageable = PageRequest.of(model.getCurrentPage() - 1, model.getPageSize());

        if (!model.isPageFlg()) {
            pageable = Pageable.unpaged();
        }

        Integer count = super.queryForSingle(countSql, params, Integer.class);
        params.putAll(params);

        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, SDQ070401BO.class, pageable), pageable, count);
    }


    /**
     * author: Tang Tiantian
     */
    @Override
    public PageImpl<SDQ070601BO> findSdPsiDwList(SDQ070601Form model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        StringBuilder selSql = new StringBuilder();

        selSql.append("    SELECT spd.psi_date                     as psiDate       ");
        selSql.append("         , spd.facility_cd                  as point         ");
        selSql.append("         , spd.facility_nm                  as pointNm       ");
        selSql.append("         , spd.product_cd                   as modelCd       ");
        selSql.append("         , spd.product_nm                   as modelNm       ");
        selSql.append("         , spd.color_cd                     as colorCd       ");
        selSql.append("         , spd.color_nm                     as colorNm       ");
        selSql.append("         , sum(spd.begining_stock)          as initialStock  ");
        selSql.append("         , sum(spd.ymvn_in)                 as ymvn          ");
        selSql.append("         , sum(spd.whole_sales_in)          as wholeSalesIn  ");
        selSql.append("         , sum(spd.transfer_in)             as transferIn    ");
        selSql.append("         , sum(spd.retail_out)              as retail        ");
        selSql.append("         , sum(spd.whole_sales_out)         as wholeSalesOut ");
        selSql.append("         , sum(spd.transfer_out)            as transferOut   ");
        selSql.append("         , sum(spd.in_transit)              as inTransit     ");
        
        sql.append("         FROM sd_psi_dw spd                  ");
        sql.append("        WHERE spd.site_id = :siteId          ");
        sql.append("          AND spd.psi_date >= :dateFrom      ");
        sql.append("          AND spd.psi_date <= :dateTo        ");
        sql.append("          AND spd.facility_id = :facilityId  ");

        params.put("siteId",model.getSiteId());
        params.put("dateFrom", model.getDateFrom());
        params.put("dateTo",model.getDateTo());
        params.put("facilityId",model.getPointId());

        sql.append("  GROUP BY spd.psi_date, spd.facility_cd, spd.facility_nm, spd.product_cd, spd.product_nm, spd.color_cd, spd.color_nm ");
        sql.append("  ORDER BY spd.facility_cd asc, spd.product_cd asc ");

        String countSql = "SELECT COUNT(1) FROM ( " + "SELECT 1 " + sql.toString() + ") AS subquery; ";
        Pageable pageable = PageRequest.of(model.getCurrentPage() - 1, model.getPageSize());

        if (!model.isPageFlg()) {
            pageable = Pageable.unpaged();
        }

        Integer count = super.queryForSingle(countSql, params, Integer.class);
        params.putAll(params);

        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, SDQ070601BO.class, pageable), pageable, count);
    }
}
