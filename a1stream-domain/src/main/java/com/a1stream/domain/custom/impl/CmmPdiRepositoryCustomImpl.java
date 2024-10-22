package com.a1stream.domain.custom.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.bo.service.SVM011001BO;
import com.a1stream.domain.custom.CmmPdiRepositoryCustom;
import com.a1stream.domain.form.service.SVM011001Form;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class CmmPdiRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmPdiRepositoryCustom {

    @Override
    public List<SVM011001BO> getPdiInfoListByProductCategoryId(SVM011001Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT cps.item_cd         as  itemCd                             ");
        sql.append("          , cps.description     as  itemNm                             ");
        sql.append("          , cps.pdi_setting_id  as  pdiSettingId                       ");
        sql.append("       FROM cmm_pdi cp                                                 ");
        sql.append(" INNER JOIN cmm_pdi_setting cps                                        ");
        sql.append("         ON cp.pdi_id = cps.pdi_id                                     ");
        sql.append("      WHERE cp.product_category_id = (SELECT product_category_id       ");
        sql.append("                                        FROM mst_product               ");
        sql.append("                                       WHERE product_id = :productId)  ");
        sql.append("        AND cp.from_date <= :sysDate                                   ");
        sql.append("        AND cp.to_date >= :sysDate                                     ");
        sql.append("   ORDER BY cps.item_cd                                                ");

        params.put("productId", form.getToProductId());
        params.put("sysDate", LocalDate.now().format(DateTimeFormatter.ofPattern(DateUtils.FORMAT_YMD_NODELIMITER)));

        return super.queryForList(sql.toString(), params, SVM011001BO.class);
    }

    @Override
    public List<SVM011001BO> getPdiInfoList(SVM011001Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT cps.item_cd         as  itemCd        ");
        sql.append("          , cps.description     as  itemNm        ");
        sql.append("          , cps.pdi_setting_id  as  pdiSettingId  ");
        sql.append("       FROM cmm_pdi cp                            ");
        sql.append(" INNER JOIN cmm_pdi_setting cps                   ");
        sql.append("         ON cp.pdi_id = cps.pdi_id                ");
        sql.append("      WHERE cp.product_category_id is null        ");
        sql.append("   ORDER BY cps.item_cd                           ");

        return super.queryForList(sql.toString(), params, SVM011001BO.class);
    }
}