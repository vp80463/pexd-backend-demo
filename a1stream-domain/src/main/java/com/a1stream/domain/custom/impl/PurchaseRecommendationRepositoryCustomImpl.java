package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.PJConstants.CostType;
import com.a1stream.domain.bo.batch.PartsRecommendationBO;
import com.a1stream.domain.custom.PurchaseRecommendationRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class PurchaseRecommendationRepositoryCustomImpl  extends JpaNativeQuerySupportRepository implements PurchaseRecommendationRepositoryCustom {

    @Override
    public List<PartsRecommendationBO> getPurchaseRecommendationList(Long pointId
                                                                   , Long supplierId
                                                                   , String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT a.purchase_recommendation_id    AS purchaseRecommendationId     ");
        sql.append("      , a.recommend_qty                 AS recommendQty                 ");
        sql.append("      , a.order_type_id                 AS orderTypeId                  ");
        sql.append("      , p.product_id                    AS productId                    ");
        sql.append("      , p.product_cd                    AS productCd                    ");
        sql.append("      , p.local_description             AS productNm                    ");
        sql.append("      , p.pur_lot_size                  AS purLotQty                    ");
        sql.append("      , p.min_pur_qty                   AS minPurQty                    ");
        sql.append("      , p.std_ws_price                  AS stdWsPrice                   ");
        sql.append("      , c.cost                          AS cost                         ");
        sql.append("   FROM purchase_recommendation as a                                    ");
        sql.append("   LEFT JOIN  mst_product as p on a.product_id = p.product_id           ");
        sql.append("   LEFT JOIN  product_cost as c on c.product_id = p.product_id          ");
        sql.append("   AND  c.cost_type = :costType              ");
        sql.append("  WHERE a.site_id = :siteId                  ");
        sql.append("    AND a.facility_id = :pointId             ");
        sql.append("    AND a.organization_id = :supplierId      ");
        sql.append("    order by p.product_cd                    ");

        params.put("costType", CostType.RECEIVE_COST);
        params.put("pointId", pointId);
        params.put("supplierId", supplierId);
        params.put("siteId", siteId);

        return super.queryForList(sql.toString(), params, PartsRecommendationBO.class);
    }
}
