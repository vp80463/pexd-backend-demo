package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.PartsVLBO;
import com.a1stream.domain.custom.MstProductRelationRepositoryCustom;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class MstProductRelationRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements MstProductRelationRepositoryCustom {

    @Override
    public List<PartsVLBO> findSupersedingPartsIdList(List<Long> list) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("      SELECT mpr.to_product_id    AS id                 ");
        sql.append("           , mpr.from_product_id  AS supersedingPartsId ");
        sql.append("           , mp.product_cd        AS supersedingPartsCd ");
        sql.append("           , mp.local_description AS supersedingPartsNm ");
        sql.append("        FROM mst_product_relation mpr                   ");
        sql.append("  INNER JOIN mst_product mp                             ");
        sql.append("          ON mpr.from_product_id = mp.product_id        ");
        sql.append("       WHERE mpr.site_id = :siteId                      ");
        sql.append("         AND mpr.to_product_id IN (:productIds)         ");
        sql.append("         AND mpr.from_date <= :sysDate                  ");
        sql.append("         AND mpr.to_date   >= :sysDate                  ");

        params.put("siteId", CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("productIds", list);
        params.put("sysDate", DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
        return super.queryForList(sql.toString(), params, PartsVLBO.class);
    }

}
