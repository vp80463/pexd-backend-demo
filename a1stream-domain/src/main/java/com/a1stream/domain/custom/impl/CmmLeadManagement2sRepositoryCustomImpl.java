package com.a1stream.domain.custom.impl;

import com.a1stream.domain.bo.service.SVM010604BO;
import com.a1stream.domain.custom.CmmLeadManagement2sRepositoryCustom;
import com.a1stream.domain.form.service.SVM010604Form;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dong zhen
 */
public class CmmLeadManagement2sRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmLeadManagement2sRepositoryCustom {

    @Override
    public List<SVM010604BO> retrieveSalesLeadList(SVM010604Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT clm2.scoring_data_export_date  AS scoringDate               ");
        sql.append("          , clm2.lead_status               AS scoring                   ");
        sql.append("          , clm2.first_nm                  AS firstNm                   ");
        sql.append("          , clm2.middle_nm                 AS middleNm                  ");
        sql.append("          , clm2.last_nm                   AS lastNm                    ");
        sql.append("          , clm2.province                  AS province                  ");
        sql.append("          , clm2.district_town_city        AS districtTownCity          ");
        sql.append("          , clm2.mobile_phone              AS mobilePhone               ");
        sql.append("          , clm2.email                     AS email                     ");
        sql.append("          , clm2.last_visit_dealer_cd      AS shopCd                    ");
        sql.append("          , clm2.last_visit_dealer_nm      AS shopNm                    ");
        sql.append("          , clm2.last_visit_date           AS visitDate                 ");
        sql.append("          , clm2.frame_no                  AS frameNo                   ");
        sql.append("          , clm2.plate_no                  AS plateNo                   ");
        sql.append("          , clm2.current_mc_model          AS modelCd                   ");
        sql.append("          , mp.sales_description           AS modelNm                   ");
        sql.append("          , clm2.fsc_use_history           AS fscUseHistory             ");
        sql.append("          , clm2.current_voucher           AS currentVoucher            ");
        sql.append("          , clm2.next_fsc_expire_date      AS nextFscExpireDate         ");
        sql.append("          , clm2.last_oil_change_date      AS lastOilChangeDate         ");
        sql.append("          , clm2.oil_nm                    AS oilNm                     ");
        sql.append("          , clm2.contact_status            AS telemarketingResultTypeId ");
        sql.append("          , clm2.call_date_by_dealer       AS callDateByDealer          ");
        sql.append("          , clm2.note                      AS note                      ");
        sql.append("          , clm2.time_stamp                AS timeStamp                 ");
        sql.append("          , clm2.lead_management_result_id AS leadManagementResultId    ");
        sql.append("          , clm2.oil_flag                  AS category                  ");
        sql.append("          , clm2.last_visit_dealer_nm      AS dealerNm                  ");
        sql.append("       FROM cmm_lead_management_2s clm2                                 ");
        sql.append(" INNER JOIN mst_product mp                                              ");
        sql.append("         ON clm2.current_mc_model = mp.product_cd                       ");
        sql.append("      WHERE clm2.site_id = :siteId                                      ");
        sql.append("        AND clm2.facility_cd = :facilityCd                              ");
        sql.append("        AND clm2.scoring_data_export_date >= :fromDate                  ");
        sql.append("        AND clm2.scoring_data_export_date <= :toDate                    ");
        sql.append("        AND clm2.contact_status IN ( :telemarketingResultList )         ");

        if (!StringUtils.isEmpty(form.getMobilePhone())){
            sql.append("    AND clm2.mobile_phone LIKE :mobilePhone                         ");
            params.put("mobilePhone", "%" + form.getMobilePhone() + "%");
        }

        if (!StringUtils.isEmpty(form.getScoring())){
            sql.append("    AND clm2.lead_status = :scoring                                 ");
            params.put("scoring", form.getScoring());
        }

        if (!StringUtils.isEmpty(form.getCategory())){
            sql.append("    AND clm2.oil_flag = :category                                   ");
            params.put("category", form.getCategory());
        }

        sql.append("   ORDER BY clm2.scoring_data_export_date, clm2.mobile_phone            ");

        params.put("siteId", form.getSiteId());
        params.put("facilityCd", form.getPointCd());
        params.put("fromDate", form.getScoringDateFrom());
        params.put("toDate", form.getScoringDateTo());
        params.put("telemarketingResultList", form.getTelemarketingResultList() != null ? form.getTelemarketingResultList() : new ArrayList<>());

        return super.queryForList(sql.toString(), params, SVM010604BO.class);
    }
}
