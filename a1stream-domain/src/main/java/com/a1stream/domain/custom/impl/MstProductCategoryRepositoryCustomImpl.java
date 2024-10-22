package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.PartsCategory;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.master.CMM050201BO;
import com.a1stream.domain.bo.master.CMM050301BO;
import com.a1stream.domain.custom.MstProductCategoryRepositoryCustom;
import com.a1stream.domain.form.master.CMM050301Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import jodd.util.StringUtil;


public class MstProductCategoryRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements MstProductCategoryRepositoryCustom {

    @Override
    public List<CMM050201BO> searchProductLargeGroupList() {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT mpc.category_cd         as largeGroupCd ");
        sql.append("      , mpc.category_nm         as largeGroupNm ");
        sql.append("      , mpc.product_category_id as largeGroupId ");
        sql.append(" FROM mst_product_category mpc                  ");

        sql.append(" WHERE mpc.site_id = :siteId                               ");
        sql.append("   AND mpc.product_classification = :productClassification ");
        sql.append("   AND mpc.category_type = :categoryType                   ");

        sql.append(" ORDER BY mpc.category_cd asc ");

        params.put("siteId", CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("productClassification", ProductClsType.PART.getCodeDbid());
        params.put("categoryType", PartsCategory.LARGEGROUP);

        return super.queryForList(sql.toString(), params, CMM050201BO.class);
    }

    @Override
    public List<CMM050301BO> searchProductMiddleGroupList(CMM050301Form model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT mpc.parent_category_cd  as largeGroupCd  ");
        sql.append("      , mpc.parent_category_nm  as largeGroupNm  ");
        sql.append("      , mpc.parent_category_id  as largeGroupId  ");
        sql.append("      , mpc.category_cd         as middleGroupCd ");
        sql.append("      , mpc.category_nm         as middleGroupNm ");
        sql.append("      , mpc.product_category_id as middleGroupId ");
        sql.append(" FROM  mst_product_category mpc                  ");

        sql.append(" WHERE mpc.site_id = :siteId                               ");
        sql.append("   AND mpc.product_classification = :productClassification ");
        sql.append("   AND mpc.category_type = :categoryType                   ");

        if(StringUtil.isNotBlank(model.getLargeGroupId())) {
            sql.append(" AND mpc.parent_category_id = :largeGroupId ");
            params.put("largeGroupId", Integer.parseInt(model.getLargeGroupId()));
        }

        sql.append(" ORDER BY mpc.parent_category_cd , mpc.category_cd asc ");

        params.put("siteId", CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("productClassification", ProductClsType.PART.getCodeDbid());
        params.put("categoryType", PartsCategory.MIDDLEGROUP);

        return super.queryForList(sql.toString(), params, CMM050301BO.class);
    }
}