package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.a1stream.common.constants.MstCodeConstants.McSalesStatus;
import com.a1stream.common.constants.PJConstants.BatteryType;
import com.a1stream.common.model.DemandVLForm;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.common.DemandBO;
import com.a1stream.domain.bo.unit.SDM050801BO;
import com.a1stream.domain.bo.unit.SVM010401BO;
import com.a1stream.domain.bo.unit.SVM010402BO;
import com.a1stream.domain.custom.CmmSerializedProductRepositoryCustom;
import com.a1stream.domain.form.unit.SDM050801Form;
import com.a1stream.domain.form.unit.SVM010401Form;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class CmmSerializedProductRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmSerializedProductRepositoryCustom {

    @Override
    public List<DemandBO> searchSerializedProductList(DemandVLForm model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT csd.service_demand_id as serviceDemandId ");
        sql.append("      , csd.description       as serviceDemand   ");
        sql.append("      , csd.base_date_type    as baseDateType    ");
        sql.append("      , CAST(cbbm.user_month_to AS int) - CAST(cbbm.user_month_from AS int) as duePeriod       ");
        sql.append("      , cbbm.user_month_from   as baseDateAfter   ");
        sql.append("   FROM cmm_serialized_product csp               ");
        sql.append("      , mst_product mp                           ");
        sql.append("      , cmm_big_bike_model cbbm                  ");
        sql.append("      , cmm_service_demand csd                   ");
        sql.append("  WHERE csp.serialized_product_id = :serializedProductId   ");
        sql.append("    AND csp.product_id = mp.product_id                     ");
        sql.append("    AND substring(mp.product_cd ,1,6) = cbbm.model_cd      ");
        sql.append("    AND csd.service_demand_id = cbbm.coup_ctg_level        ");

        params.put("serializedProductId", Integer.parseInt(model.getArg1()));

        return super.queryForList(sql.toString(), params, DemandBO.class);
    }

    /**
     * 判断目标车辆是否为大车型
     */
    @Override
    public boolean isMcBigBike(Long serializedProId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT 1                                                  ");
        sql.append("   FROM cmm_serialized_product csp                         ");
        sql.append("  INNER JOIN mst_product mp                                ");
        sql.append("     ON mp.product_id = csp.product_id                     ");
        sql.append("  WHERE csp.serialized_product_id = :serializedProId       ");
        sql.append("    AND EXISTS (                                           ");
        sql.append("      SELECT 1                                             ");
        sql.append("        FROM cmm_big_bike_model cbbm                       ");
        sql.append("       WHERE substring(mp.product_cd ,1,6) = cbbm.model_cd ");
        sql.append("   )                                                       ");

        params.put("serializedProId", serializedProId);

        return !super.queryForList(sql.toString(), params, String.class).isEmpty();
    }

    /**
     * 当条件部输入frameNo，plateNo或者BatteryId时, 查询local无数据时，查询common
     */
    @Override
    public Page<SVM010401BO> getCommonMcData(SVM010401Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder selSql = new StringBuilder();
        StringBuilder sql = new StringBuilder();

        selSql.append("    SELECT 'S'                       AS p                          ");
        selSql.append("         , mf.facility_nm            AS pointName                  ");
        selSql.append("         , mp.product_cd             AS modelCode                  ");
        selSql.append("         , mp.sales_description      AS modelName                  ");
        selSql.append("         , mp.color_nm               AS colorName                  ");
        selSql.append("         , csp.plate_no              AS plateNo                    ");
        selSql.append("         , csp.frame_no              AS frameNo                    ");
        selSql.append("         , csp.engine_no             AS engineNo                   ");
        selSql.append("         , b1.battery_no             AS batteryId1                 ");
        selSql.append("         , b2.battery_no             AS batteryId2                 ");
        selSql.append("         , csp.stu_date              AS soldDate                   ");
        selSql.append("         , csp.quality_status        AS qualityStatus              ");
        selSql.append("         , mb.brand_nm               AS brandName                  ");
        selSql.append("         , csp.serialized_product_id AS cmmSerializedProductId     ");
        sql.append("         FROM cmm_serialized_product csp                              ");
        sql.append("    LEFT JOIN mst_facility mf                                         ");
        sql.append("           ON mf.facility_id = csp.facility_id                        ");
        sql.append("    LEFT JOIN mst_product mp                                          ");
        sql.append("           ON csp.product_id = mp.product_id                          ");
        sql.append("    LEFT JOIN mst_brand mb                                            ");
        sql.append("           ON mp.brand_id = mb.brand_id                               ");
        sql.append("    LEFT JOIN cmm_battery b1                                          ");
        sql.append("           ON b1.serialized_product_id = csp.serialized_product_id    ");
        sql.append("          AND b1.position_sign = :battery1                            ");
        sql.append("          AND b1.to_date > :sysDate                                   ");
        sql.append("    LEFT JOIN cmm_battery b2                                          ");
        sql.append("           ON b2.serialized_product_id = csp.serialized_product_id    ");
        sql.append("          AND b2.position_sign = :battery2                            ");
        sql.append("          AND b2.to_date > :sysDate                                   ");
        sql.append("        WHERE (csp.sales_status = '' OR sales_status= :S040SALESTOUSER) ");

        params.put("battery1", BatteryType.TYPE1.getCodeDbid());
        params.put("battery2", BatteryType.TYPE2.getCodeDbid());
        params.put("sysDate", ComUtil.nowLocalDate());
        params.put("S040SALESTOUSER", McSalesStatus.SALESTOUSER);

        List<Long> serializedProductIds = form.getSerializedProductIdList();
        if (serializedProductIds != null && !serializedProductIds.isEmpty()) {
            sql.append(" AND csp.serialized_product_id IN (:serializedProductIds) ");
            params.put("serializedProductIds", serializedProductIds);
        }

        if (!Nulls.isNull(form.getPointId())) {
            sql.append(" AND csp.facility_id = :pointId ");
            params.put("pointId", form.getPointId());
        }

        if (!Nulls.isNull(form.getModelId())) {
            sql.append(" AND csp.product_id   = :modelId ");
            params.put("modelId", form.getModelId());
        }

        if (StringUtils.isNotBlank(form.getPlateNo())) {
            sql.append(" AND csp.plate_no = :plateNo ");
            params.put("plateNo", form.getPlateNo());
        }

        if (!Nulls.isNull(form.getBrandId())) {
            sql.append(" AND mb.brand_id = :brandId ");
            params.put("brandId", form.getBrandId());
        }

        if (StringUtils.isNotBlank(form.getFrameNo())) {
            sql.append(" AND csp.frame_no = :frameNo ");
            params.put("frameNo", form.getFrameNo());
        }

        if (StringUtils.isNotBlank(form.getBatteryId())) {
            sql.append(" AND (b1.battery_no  = :batteryId OR b2.battery_no = :batteryId) ");
            params.put("batteryId", form.getBatteryId());
        }

        String countSql = " SELECT COUNT(*) FROM ( " + "SELECT 1 " + sql.toString() + ") AS subquery; ";
        Pageable pageable = PageRequest.of(form.getCurrentPage() - 1, form.getPageSize());
        if (!Boolean.TRUE.equals(form.getPageFlg())) {
            pageable = Pageable.unpaged();
        }
        Integer count = super.queryForSingle(countSql, params, Integer.class);
        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, SVM010401BO.class, pageable), pageable, count);
    }

    @Override
    public SVM010402BO getMcBasicInfo(Long cmmSerializedProductId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mf.facility_nm                     AS pointName          ");
        sql.append("          , sp.ev_flag                         AS evFlag             ");
        sql.append("          , mp.product_cd                      AS modelCode          ");
        sql.append("          , mp.sales_descriptiON               AS modelName          ");
        sql.append("          , mp.color_cd||mp.color_nm           AS color              ");
        sql.append("          , sp.plate_no                        AS plateNo            ");
        sql.append("          , sp.frame_no                        AS frameNo            ");
        sql.append("          , sp.engine_no                       AS engineNo           ");
        sql.append("          , b1.battery_no                      AS batteryId1         ");
        sql.append("          , b2.battery_no                      AS batteryId2         ");
        sql.append("          , sp.stu_date                        AS soldDate           ");
        sql.append("          , sp.manufacturing_date              AS manufacturingDate  ");
        sql.append("          , sp.quality_status                  AS qualityStatus      ");
        sql.append("          , mb.brand_nm                        AS brandName          ");
        sql.append("          , mpc.category_cd || mpc.category_nm AS motorcycleCategory ");
        sql.append("          , sp.sales_status                    AS salesStatus        ");
        sql.append("          , sp.serialized_product_id           AS serializedProductId ");
        sql.append("      FROM cmm_serialized_product sp                                 ");
        sql.append(" LEFT JOIN mst_facility mf                                           ");
        sql.append("        ON mf.facility_id = sp.facility_id                           ");
        sql.append(" LEFT JOIN mst_product mp                                            ");
        sql.append("        ON sp.product_id = mp.product_id                             ");
        sql.append(" LEFT JOIN mst_brand mb                                              ");
        sql.append("        ON mp.brand_id = mb.brand_id                                 ");
        sql.append(" LEFT JOIN cmm_battery b1                                            ");
        sql.append("        ON b1.serialized_product_id = sp.serialized_product_id       ");
        sql.append("       AND b1.positiON_sign = :battery1                              ");
        sql.append(" LEFT JOIN cmm_battery b2                                            ");
        sql.append("        ON b2.serialized_product_id = sp.serialized_product_id       ");
        sql.append("       AND b2.positiON_sign = :battery2                              ");
        sql.append(" LEFT JOIN mst_product_category mpc                                  ");
        sql.append("        ON mp.product_category_id = mpc.product_category_id          ");
        sql.append("     WHERE sp.serialized_product_id = :cmmSerializedProductId        ");

        params.put("battery1", BatteryType.TYPE1.getCodeDbid());
        params.put("battery2", BatteryType.TYPE2.getCodeDbid());
        params.put("cmmSerializedProductId", cmmSerializedProductId);

        return super.queryForSingle(sql.toString(), params, SVM010402BO.class);
    }

    @Override
    public SDM050801BO getPromotionProduct(SDM050801Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT sp.serialized_product_id AS serializedProductId            ");
        sql.append("      FROM cmm_serialized_product sp                                  ");
        sql.append(" LEFT JOIN mst_product mp                                             ");
        sql.append("        ON sp.product_id  = mp.product_id                             ");
        sql.append(" LEFT JOIN cmm_unit_promotion_item cupi                               ");
        sql.append("        ON cupi.cmm_serialized_product_id = sp.serialized_product_id  ");
        sql.append(" LEFT JOIN cmm_unit_promotion_model cupm                              ");
        sql.append("        ON  mp.product_id = cupm.product_id                           ");
        sql.append("       AND cupi.promotion_list_id  = cupm.promotion_list_id           ");
        sql.append("       AND cupm.promotion_list_id = :promotionListId                  ");
        sql.append("     WHERE sp.frame_no  = :frameNo                                    ");

        params.put("promotionListId", form.getPromotionId());
        params.put("frameNo", form.getFrameNo());
        return super.queryForSingle(sql.toString(), params, SDM050801BO.class);
    }
}
