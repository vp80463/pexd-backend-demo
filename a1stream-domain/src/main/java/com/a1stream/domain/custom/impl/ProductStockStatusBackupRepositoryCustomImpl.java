package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.Set;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.custom.ProductStockStatusBackupRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ProductStockStatusBackupRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ProductStockStatusBackupRepositoryCustom {

    /**
     * 功能描述:dim0206 copy purchase product stock status
     *
     * @author mid2178
     */
    @Override
    public void insertCopy(String siteId, Long facilityId, Set<String> statusTypes){

        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> param = new HashMap<>();

        sql.append("  INSERT INTO product_stock_status_backup            ");
        sql.append("       ( product_stock_status_id                     ");
        sql.append("       , site_id                                     ");
        sql.append("       , facility_id                                 ");
        sql.append("       , product_id                                  ");
        sql.append("       , product_stock_status_type                   ");
        sql.append("       , qty                                         ");
        sql.append("       , product_classification                      ");
        sql.append("       , update_program                              ");
        sql.append("       , last_updated_by                             ");
        sql.append("       , last_updated                                ");
        sql.append("       , created_by                                  ");
        sql.append("       , date_created                                ");
        sql.append("       , update_count                                ");
        sql.append("       , backup_date)                                ");
        sql.append("  SELECT product_stock_status_id                     ");
        sql.append("       , site_id                                     ");
        sql.append("       , facility_id                                 ");
        sql.append("       , product_id                                  ");
        sql.append("       , product_stock_status_type                   ");
        sql.append("       , quantity                                    ");
        sql.append("       , product_classification                      ");
        sql.append("       , update_program                              ");
        sql.append("       , last_updated_by                             ");
        sql.append("       , last_updated                                ");
        sql.append("       , created_by                                  ");
        sql.append("       , date_created                                ");
        sql.append("       , update_count                                ");
        sql.append("       , TO_CHAR(now(),:DateFormate)                 ");
        sql.append("    FROM product_stock_status                        ");
        sql.append("   WHERE site_id =:siteId                            ");
        sql.append("     AND facility_id =:facilityId                    ");
        sql.append("     AND product_classification =:productClsType     ");
        sql.append("     AND quantity <> 0                               ");
        sql.append("     AND product_stock_status_type in (:statusTypes) ");

        param.put("siteId", siteId);
        param.put("facilityId", facilityId);
        param.put("statusTypes", statusTypes);
        param.put("productClsType", ProductClsType.PART.getCodeDbid());
        param.put("DateFormate", CommonConstants.DB_DATE_FORMAT_YMD);

        super.createSqlQuery(sql.toString(), param).executeUpdate();
    }
}
