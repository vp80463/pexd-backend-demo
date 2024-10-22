package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.bo.master.CMM060202Detail;
import com.a1stream.domain.custom.ServicePackageItemRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ServicePackageItemRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ServicePackageItemRepositoryCustom {

    @Override
    public List<CMM060202Detail> getSvPackageItemList(Long servicePackageId, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT spi.product_id               as  productId           ");
        sql.append("          , spi.service_package_item_id  as  packageItemId       ");
        sql.append("          , spi.qty                      as  qty                 ");
        sql.append("          , spi.product_classification   as  productClsType      ");
        sql.append("          , mp.product_cd                as  productCd           ");
        sql.append("          , mp.sales_description         as  productNm           ");

        sql.append("       FROM service_package_item spi                             ");
        sql.append("  LEFT JOIN mst_product mp                                       ");
        sql.append("         ON spi.product_id =  mp.product_id                      ");

        sql.append("      WHERE spi.site_id = :siteId                                ");
        sql.append("        AND spi.service_package_id = :servicePackageId           ");

        sql.append("   ORDER BY mp.product_cd                                        ");

        params.put("siteId", siteId);
        params.put("servicePackageId", servicePackageId);

        return super.queryForList(sql.toString(), params, CMM060202Detail.class);
    }
}