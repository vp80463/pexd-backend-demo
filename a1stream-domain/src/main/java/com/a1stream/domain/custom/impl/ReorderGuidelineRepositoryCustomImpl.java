package com.a1stream.domain.custom.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.domain.bo.batch.PartsRecommendationBO;
import com.a1stream.domain.bo.parts.SPM040201BO;
import com.a1stream.domain.custom.ReorderGuidelineRepositoryCustom;
import com.a1stream.domain.form.parts.SPM040201Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ReorderGuidelineRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ReorderGuidelineRepositoryCustom {

    @Override
    public PageImpl<SPM040201BO> searchProductROPROQInfo(SPM040201Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        StringBuilder selSql = new StringBuilder();

        selSql.append(" SELECT mp.product_cd                     as partsCd                                      ");
        selSql.append("      , mp.local_description              as partsNm                                      ");
        selSql.append("      , rg.reorder_guideline_id           as reorderGuidelineId                           ");
        selSql.append("      , rg.product_id                     as partsId                                      ");
        selSql.append("      , rg.reorder_point                  as rop                                          ");
        selSql.append("      , rg.reorder_qty                    as roq                                          ");
        selSql.append("      , rg.rop_roq_manual_flag            as sign                                         ");
        selSql.append("      , rg.rop_roq_manual_flag            as beforeSign                                   ");
        selSql.append("      , split_part(mp.all_path,'|',1) || ' ' || split_part(mp.all_nm,'|',1) as largeGroup ");
        selSql.append("      , pai.abc_type                      as costUsage                                    ");
        selSql.append("      , coalesce(pc.cost,0)               as averageCost                                  ");
        selSql.append("      , rg.update_count                   as updateCount                                  ");

        sql.append("   FROM reorder_guideline rg                               ");
        sql.append("   LEFT JOIN mst_product mp                                ");
        sql.append("     ON rg.product_id   = mp.product_id                    ");
        sql.append("   LEFT JOIN product_abc_info pai                          ");
        sql.append("     ON pai.product_id   = mp.product_id                   ");
        sql.append("    AND pai.facility_id  = :pointId                        ");
        sql.append("    AND pai.site_id     = :siteId                          ");
        sql.append("   LEFT JOIN product_cost pc                               ");
        sql.append("     ON pc.product_id   = mp.product_id                    ");
        sql.append("    AND pc.site_id     = :siteId                           ");
        sql.append("    AND pc.cost_type    = :cost                            ");

        if (!ObjectUtils.isEmpty(model.getLargeGroupId())) {
            sql.append(" LEFT JOIN mst_product_category mpc                    ");
            sql.append("   ON mp.product_category_id = mpc.product_category_id ");
        }

        sql.append("  WHERE rg.site_id = :siteId                               ");
        sql.append("    AND rg.facility_id = :pointId                          ");

        if (!ObjectUtils.isEmpty(model.getPartsId())) {
            sql.append(" AND mp.product_id             = :productId            ");
            params.put("productId", model.getPartsId());
        }

        if (!ObjectUtils.isEmpty(model.getLargeGroupId())) {
            sql.append(" AND mpc.parent_category_id    = :largeGroupId         ");
            params.put("largeGroupId", model.getLargeGroupId());
        }

        sql.append(" ORDER BY mp.product_cd ");

        params.put("siteId", siteId);
        params.put("pointId", model.getPointId());
        params.put("cost", PJConstants.CostType.AVERAGE_COST);

        String countSql = "SELECT COUNT(1) FROM ( " + "SELECT * " + sql.toString() + ") AS subquery; ";
        Pageable pageable = PageRequest.of(model.getCurrentPage() - 1, model.getPageSize());

        Integer count = super.queryForSingle(countSql, params, Integer.class);
        params.putAll(params);

        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, SPM040201BO.class, pageable), pageable, count);

    }

    @Override
    public List<PartsRecommendationBO> getReorderGuidelineByConditions(Long pointId
                                                                     , String siteId
                                                                     , String processDate) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        List<String> spStockStatusList = new ArrayList<>();
        spStockStatusList.add(SpStockStatus.ONHAND_QTY.getCodeDbid());
        spStockStatusList.add(SpStockStatus.BO_QTY.getCodeDbid());
        spStockStatusList.add(SpStockStatus.ONRECEIVING_QTY.getCodeDbid());
        spStockStatusList.add(SpStockStatus.EO_ONPURCHASE_QTY.getCodeDbid());
        spStockStatusList.add(SpStockStatus.RO_ONPURCHASE_QTY.getCodeDbid());
        spStockStatusList.add(SpStockStatus.HO_ONPURCHASE_QTY.getCodeDbid());
        spStockStatusList.add(SpStockStatus.WO_ONPURCHASE_QTY.getCodeDbid());
        spStockStatusList.add(SpStockStatus.ONTRANSFER_IN_QTY.getCodeDbid());
        spStockStatusList.add(SpStockStatus.ONSERVICE_QTY.getCodeDbid());
        spStockStatusList.add(SpStockStatus.ONCANVASSING_QTY.getCodeDbid());

        sql.append(" SELECT p.product_id                  AS productId                  ");
        sql.append("      , p.pur_lot_size                AS purLotQty                  ");
        sql.append("      , rl.reorder_point                AS reorderPoint             ");
        sql.append("      , rl.reorder_qty                  AS reorderQty               ");
        sql.append("      , ss.quantity                     AS quantity                 ");
        sql.append("      , ss.product_stock_status_type    AS productStockStatusType   ");
        sql.append(String.format(", psm.month%s_quantity", processDate.substring(4, 6)));
        sql.append("      as monthQuantity                                              ");
        sql.append("   FROM reorder_guideline as rl                                     ");
        sql.append("   LEFT JOIN  mst_product as p on rl.product_id = p.product_id      ");
        sql.append("   LEFT JOIN  product_stock_status as ss on ss.product_id = p.product_id                ");
        sql.append("         AND  ss.product_stock_status_type in (:spStockStatusList)  ");
        sql.append("         AND  ss.facility_id = :pointId                             ");
        sql.append("         AND  ss.site_id = rl.site_id                               ");
        sql.append("   LEFT JOIN  product_order_result_summary as psm on psm.product_id = p.product_id      ");
        sql.append("         AND  psm.facility_id = :pointId                            ");
        sql.append("         AND  psm.target_year = :targetYear                         ");
        sql.append("         AND  psm.site_id = rl.site_id                              ");
        sql.append("   LEFT JOIN  parts_ropq_parameter as prp on prp.product_id = p.product_id              ");
        sql.append("         AND  prp.facility_id = :pointId                            ");
        sql.append("         AND  prp.site_id = rl.site_id                              ");
        sql.append("  WHERE rl.site_id = :siteId                                        ");
        sql.append("    AND rl.facility_id = :pointId                                   ");
        sql.append("    AND prp.ropq_exception_sign != :chary                           ");
        sql.append("    order by p.product_id                                           ");

        params.put("pointId", pointId);
        params.put("siteId", siteId);
        params.put("chary", CommonConstants.CHAR_Y);
        params.put("targetYear", processDate.substring(0, 4));
        params.put("spStockStatusList", spStockStatusList);

        return super.queryForList(sql.toString(), params, PartsRecommendationBO.class);
    }
}
