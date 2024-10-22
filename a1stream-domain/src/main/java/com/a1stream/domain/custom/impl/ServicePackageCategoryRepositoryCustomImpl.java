package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.master.CMM060202Detail;
import com.a1stream.domain.custom.ServicePackageCategoryRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ServicePackageCategoryRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ServicePackageCategoryRepositoryCustom {

    @Override
    public List<CMM060202Detail> getCategoryDetails(Long servicePackageId, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT spc.product_category_id          as  categoryId         ");
        sql.append("          , spc.service_package_category_id  as  svPackageCtgId  ");
        sql.append("          , mpc.category_cd                  as  categoryCd         ");
        sql.append("          , mpc.category_nm                  as  categoryNm         ");

        sql.append("       FROM service_package_category spc                                   ");
        sql.append("  LEFT JOIN mst_product_category mpc                                       ");
        sql.append("         ON spc.product_category_id =  mpc.product_category_id             ");

        sql.append("      WHERE spc.site_id = :siteId                                          ");
        sql.append("        AND spc.service_package_id = :servicePackageId                     ");
        sql.append("        AND mpc.product_classification = :productClassification            ");

        sql.append("   ORDER BY mpc.category_cd");

        params.put("siteId", siteId);
        params.put("servicePackageId", servicePackageId);
        params.put("productClassification", ProductClsType.GOODS.getCodeDbid());

        return super.queryForList(sql.toString(), params, CMM060202Detail.class);
    }
}