package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.BatteryType;
import com.a1stream.common.constants.PJConstants.ConsumerSerialProRelationType;
import com.a1stream.common.constants.PJConstants.SerialproductStockStatus;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.unit.SDM040103BO;
import com.a1stream.domain.bo.unit.SDM050801BO;
import com.a1stream.domain.bo.unit.SDQ010702DetailBO;
import com.a1stream.domain.bo.unit.SDQ011302BO;
import com.a1stream.domain.bo.unit.SVM010401BO;
import com.a1stream.domain.custom.SerializedProductRepositoryCustom;
import com.a1stream.domain.form.service.SVM010702Form;
import com.a1stream.domain.form.service.SVM011001Form;
import com.a1stream.domain.form.unit.SDM050801Form;
import com.a1stream.domain.form.unit.SDQ011301Form;
import com.a1stream.domain.form.unit.SDQ011302Form;
import com.a1stream.domain.form.unit.SVM010401Form;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class SerializedProductRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements SerializedProductRepositoryCustom {

    @Override
    public SVM011001Form getModelInfoByFrameNo(SVM011001Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mp.product_cd             as  modelCd              ");
        sql.append("          , mp.sales_description      as  modelNm              ");
        sql.append("          , mp.color_nm               as  colorNm              ");
        sql.append("          , mp.product_id             as  productId            ");
        sql.append("          , mp.to_product_id          as  toProductId          ");
        sql.append("          , sp.serialized_product_id  as  serializedProductId  ");
        sql.append("       FROM serialized_product sp                              ");
        sql.append("  LEFT JOIN mst_product mp                                     ");
        sql.append("         ON mp.product_id = sp.product_id                      ");
        sql.append("      WHERE sp.site_id = :siteId                               ");
        sql.append("        AND sp.frame_no = :frameNo                             ");

        params.put("siteId", form.getSiteId());
        params.put("frameNo", form.getFrameNo());

        return super.queryForSingle(sql.toString(), params, SVM011001Form.class);
    }

    @Override
    public SVM010702Form getInfoByPlateNo(SVM010702Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT csp.serialized_product_id  as  serializedProductId       ");
        sql.append("          , mp.product_cd              as  modelCd                   ");
        sql.append("          , mp.sales_description       as  modelNm                   ");
        sql.append("          , mp.product_id              as  productId                 ");
        sql.append("          , cc.consumer_id             as  consumerId                ");
        sql.append("          , cc.consumer_full_nm        as  consumerNm                ");
        sql.append("          , cd.mobile_phone            as  mobilePhone               ");
        sql.append("       FROM cmm_serialized_product csp                               ");
        sql.append(" INNER JOIN cmm_consumer_serial_pro_relation ccspr                   ");
        sql.append("         ON csp.serialized_product_id = ccspr.serialized_product_id  ");
        sql.append("        AND ccspr.consumer_serialized_product_relation_type_id = :owner  ");
        sql.append("  LEFT JOIN cmm_consumer cc                                          ");
        sql.append("         ON cc.consumer_id = ccspr.consumer_id                       ");
        sql.append("  LEFT JOIN consumer_private_detail cd                               ");
        sql.append("         ON cd.site_id = :siteId                                     ");
        sql.append("        AND cd.consumer_id = cc.consumer_id                          ");
        sql.append("  LEFT JOIN mst_product mp                                           ");
        sql.append("         ON mp.product_id = csp.product_id                           ");
        sql.append("      WHERE csp.plate_no  = :plateNo                                 ");

        params.put("siteId", form.getSiteId());
        params.put("plateNo", form.getPlateNo());
        params.put("owner", ConsumerSerialProRelationType.OWNER.getCodeDbid());

        return super.queryForSingle(sql.toString(), params, SVM010702Form.class);
    }

    @Override
    public List<SDQ011302BO> findExportByMotorcycle(SDQ011301Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT sp.frame_no               AS frameNo                   ");
        sql.append("          , sp.engine_no              AS engineNo                  ");
        sql.append("          , mp.color_nm               AS colorNm                   ");
        sql.append("          , sp.received_date          AS receivedDate              ");
        sql.append("          , (TO_DATE(TO_CHAR(CURRENT_DATE, 'YYYYMMDD'), 'YYYYMMDD') - TO_DATE(sp.received_date, 'YYYYMMDD')) AS stockAge     ");
        sql.append("          , :Available                AS stockStatus               ");
        sql.append("          , b1.battery_no             AS batteryNo1                ");
        sql.append("          , b2.battery_no             AS batteryNo2                ");
        sql.append("       FROM serialized_product sp                                  ");
        sql.append("  LEFT JOIN mst_product mp                                         ");
        sql.append("         ON mp.product_id = sp.product_id                          ");
        sql.append("  LEFT JOIN battery b1                                             ");
        sql.append("         ON sp.serialized_product_id = b1.serialized_product_id    ");
        sql.append("        AND b1.position_sign = :BATTERYONE                         ");
        sql.append("        AND b1.to_date > TO_CHAR(CURRENT_DATE,'YYYYMMDD')          ");
        sql.append("        AND b1.from_date <= TO_CHAR(CURRENT_DATE,'YYYYMMDD')       ");
        sql.append("  LEFT JOIN battery b2                                             ");
        sql.append("         ON sp.serialized_product_id = b2.serialized_product_id    ");
        sql.append("        AND b2.position_sign = :BATTERYTWO                         ");
        sql.append("        AND b2.to_date > TO_CHAR(CURRENT_DATE,'YYYYMMDD')          ");
        sql.append("        AND b2.from_date <= TO_CHAR(CURRENT_DATE,'YYYYMMDD')       ");
        sql.append("      WHERE sp.site_id = :siteId                                   ");
        sql.append("        AND sp.stock_status = :ONHAND                              ");
        sql.append("        AND sp.facility_id = :pointId                              ");

        if (!ObjectUtils.isEmpty(form.getModelId())) {
            sql.append("      AND serialized_product.product_id = :modelId             ");
            params.put("modelId",form.getModelId());
        }

        params.put("siteId", form.getSiteId());
        params.put("pointId", form.getPointId());
        params.put("Available", CommonConstants.AVAILABLE);
        params.put("ONHAND", SerialproductStockStatus.ONHAND);
        params.put("BATTERYONE", BatteryType.TYPE1.getCodeDbid());
        params.put("BATTERYTWO", BatteryType.TYPE2.getCodeDbid());

        return super.queryForList(sql.toString(), params, SDQ011302BO.class);
    }

    @Override
    public List<SDQ011302BO> findStockInformationInquiryDetail(SDQ011302Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT sp.frame_no               AS frameNo                   ");
        sql.append("          , sp.engine_no              AS engineNo                  ");
        sql.append("          , mp.color_nm               AS colorNm                   ");
        sql.append("          , sp.received_date          AS receivedDate              ");
        sql.append("          , (TO_DATE(TO_CHAR(CURRENT_DATE, 'YYYYMMDD'), 'YYYYMMDD') - TO_DATE(sp.received_date, 'YYYYMMDD')) AS stockAge     ");
        sql.append("          , :Available                AS stockStatus               ");
        sql.append("          , b1.battery_no             AS batteryNo1                ");
        sql.append("          , b2.battery_no             AS batteryNo2                ");
        sql.append("       FROM serialized_product sp                                  ");
        sql.append("  LEFT JOIN mst_product mp                                         ");
        sql.append("         ON mp.product_id = sp.product_id                          ");
        sql.append("        AND mp.product_id = :modelId                               ");
        sql.append("  LEFT JOIN battery b1                                             ");
        sql.append("         ON sp.serialized_product_id = b1.serialized_product_id    ");
        sql.append("        AND b1.position_sign = :BATTERYONE                         ");
        sql.append("        AND b1.to_date > TO_CHAR(CURRENT_DATE,'YYYYMMDD')          ");
        sql.append("        AND b1.from_date <= TO_CHAR(CURRENT_DATE,'YYYYMMDD')       ");
        sql.append("  LEFT JOIN battery b2                                             ");
        sql.append("         ON sp.serialized_product_id = b2.serialized_product_id    ");
        sql.append("        AND b2.position_sign = :BATTERYTWO                         ");
        sql.append("        AND b2.to_date > TO_CHAR(CURRENT_DATE,'YYYYMMDD')          ");
        sql.append("        AND b2.from_date <= TO_CHAR(CURRENT_DATE,'YYYYMMDD')       ");
        sql.append("      WHERE sp.site_id = :siteId                                   ");
        sql.append("        AND sp.stock_status = :ONHAND                              ");
        sql.append("        AND sp.facility_id = :pointId                              ");
        sql.append("        AND mp.product_id = :modelId                               ");

        params.put("siteId", form.getSiteId());
        params.put("pointId", form.getPointId());
        params.put("modelId", form.getModelId());
        params.put("Available", CommonConstants.AVAILABLE);
        params.put("ONHAND", SerialproductStockStatus.ONHAND);
        params.put("BATTERYONE", BatteryType.TYPE1.getCodeDbid());
        params.put("BATTERYTWO", BatteryType.TYPE2.getCodeDbid());

        return super.queryForList(sql.toString(), params, SDQ011302BO.class);
    }

    @Override
    public Page<SVM010401BO> getLocalMcData(SVM010401Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder selSql = new StringBuilder();
        StringBuilder sql = new StringBuilder();

        selSql.append("    SELECT 'L'                          AS p                      ");
        selSql.append("         , mf.facility_nm               AS pointName              ");
        selSql.append("         , mp.product_cd                AS modelCode              ");
        selSql.append("         , mp.sales_descriptiON         AS modelName              ");
        selSql.append("         , mp.color_nm                  AS colorName              ");
        selSql.append("         , sp.plate_no                  AS plateNo                ");
        selSql.append("         , sp.frame_no                  AS frameNo                ");
        selSql.append("         , sp.engine_no                 AS engineNo               ");
        selSql.append("         , b1.battery_no                AS batteryId1             ");
        selSql.append("         , b2.battery_no                AS batteryId2             ");
        selSql.append("         , sp.stu_date                  AS soldDate               ");
        selSql.append("         , sp.quality_status            AS qualityStatus          ");
        selSql.append("         , mb.brand_nm                  AS brandName              ");
        selSql.append("         , sp.serialized_product_id     AS serializedProductId    ");
        selSql.append("         , sp.cmm_serialized_product_id AS cmmSerializedProductId ");
        selSql.append("         , sp.stock_status              AS stockStatus            ");
        sql.append("         FROM serialized_product sp                                  ");
        sql.append("    LEFT JOIN mst_facility mf                                        ");
        sql.append("           ON mf.facility_id = sp.facility_id                        ");
        sql.append("    LEFT JOIN mst_product mp                                         ");
        sql.append("           ON sp.product_id = mp.product_id                          ");
        sql.append("    LEFT JOIN mst_brand mb                                           ");
        sql.append("           ON mp.brand_id = mb.brand_id                              ");
        sql.append("    LEFT JOIN battery b1                                             ");
        sql.append("           ON b1.serialized_product_id = sp.serialized_product_id    ");
        sql.append("          AND b1.position_sign = :battery1                           ");
        sql.append("          AND b1.to_date > :sysDate                                  ");
        sql.append("    LEFT JOIN battery b2                                             ");
        sql.append("           ON b2.serialized_product_id = sp.serialized_product_id    ");
        sql.append("          AND b2.position_sign = :battery2                           ");
        sql.append("          AND b2.to_date > :sysDate                                  ");
        sql.append("        WHERE sp.site_id = :siteId                                   ");
        params.put("battery1", BatteryType.TYPE1.getCodeDbid());
        params.put("battery2", BatteryType.TYPE2.getCodeDbid());
        params.put("sysDate", ComUtil.nowLocalDate());
        params.put("siteId", siteId);

        List<Long> serializedProductIds = form.getSerializedProductIdList();
        if (serializedProductIds != null && !serializedProductIds.isEmpty()) {
            sql.append(" AND sp.serialized_product_id IN (:serializedProductIds) ");
            params.put("serializedProductIds", serializedProductIds);
        }

        if (!Nulls.isNull(form.getPointId())) {
            sql.append(" AND sp.facility_id = :pointId ");
            params.put("pointId", form.getPointId());
        }

        if (!Nulls.isNull(form.getModelId())) {
            sql.append(" AND sp.product_id = :modelId ");
            params.put("modelId", form.getModelId());
        }

        if (StringUtils.isNotBlank(form.getPlateNo())) {
            sql.append(" AND sp.plate_no = :plateNo ");
            params.put("plateNo", form.getPlateNo());
        }

        if (!Nulls.isNull(form.getBrandId())) {
            sql.append(" AND mb.brand_id = :brandId ");
            params.put("brandId", form.getBrandId());
        }

        if (StringUtils.isNotBlank(form.getFrameNo())) {
            sql.append(" AND sp.frame_no = :frameNo ");
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
    public SDM040103BO getMotorCycleInfo(Long serializedProductId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT sp.bar_code          AS barcode ");
        sql.append("          , mp.sales_description AS modelNm ");
        sql.append("          , sp.frame_no          AS frameNo ");
        sql.append("          , sp.plate_no          AS plateNo ");
        sql.append("          , mp.color_nm          AS colorNm ");
        sql.append("          , sp.engine_no         AS engineNo");
        sql.append("       FROM serialized_product sp           ");
        sql.append(" INNER JOIN mst_product mp on mp.product_id = sp.product_id");
        sql.append("     WHERE serialized_product_id =:serializedProductId   ");

        params.put("serializedProductId", serializedProductId);

        return super.queryForSingle(sql.toString(), params, SDM040103BO.class);
    }

    @Override
    public List<SDM050801BO> getSpPromoRecList(SDM050801Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT sp.site_id                           AS siteId                  ");
        sql.append("         , csm.site_nm                          AS siteNm                  ");
        sql.append("         , mf.facility_cd                       AS facilityCd              ");
        sql.append("         , mf.facility_nm                       AS facilityNm              ");
        sql.append("         , mf.facility_id                       AS facilityId              ");
        sql.append("         , so.order_no                          AS orderNo                 ");
        sql.append("         , mp.product_id                        AS productId               ");
        sql.append("         , mp.product_cd                        AS productCd               ");
        sql.append("         , mp.sales_description                 AS productNm               ");
        sql.append("         , sp.serialized_product_id             AS serializedProductId     ");
        sql.append("         , sp.cmm_serialized_product_id         AS cmmSerializedProductId  ");
        sql.append("         , cc.consumer_id                       AS consumerId              ");
        sql.append("         , cc.consumer_full_nm                  AS consumerFullNm          ");
        sql.append("         , sp.frame_no                          AS frameNo                 ");
        sql.append("         , so.order_date                        AS orderDate               ");
        sql.append("         , i.invoice_no                         AS invoiceNo               ");
        sql.append("         , i.invoice_id                         AS invoiceId               ");
        sql.append("         , do2.delivery_order_id                AS deliveryOrderId         ");
        sql.append("         , do2.delivery_order_no                AS deliveryOrderNo         ");
        sql.append("         , so.sales_pic_nm                      AS salesPicNm              ");
        sql.append("         , 'N'                                  AS checkFlg                ");
        sql.append("         , so.sales_order_id                    AS salesOrderId            ");
        sql.append("         , so.sales_pic_id                      AS salesPicId              ");
        sql.append("      FROM serialized_product sp                                           ");
        sql.append(" LEFT JOIN order_serialized_item osi                                       ");
        sql.append("        ON osi.serialized_product_id  = sp.serialized_product_id           ");
        sql.append(" LEFT JOIN sales_order so                                                  ");
        sql.append("        ON so.sales_order_id  = osi.sales_order_id                         ");
        sql.append(" LEFT JOIN invoice_serialized_item isi                                     ");
        sql.append("        ON isi.serialized_product_id  = sp.serialized_product_id           ");
        sql.append(" LEFT JOIN delivery_serialized_item dsi                                    ");
        sql.append("        ON dsi.serialized_product_id = sp.serialized_product_id            ");
        sql.append(" LEFT JOIN cmm_site_master csm                                             ");
        sql.append("        ON sp.site_id  = csm.site_id                                       ");
        sql.append(" LEFT JOIN mst_facility mf                                                 ");
        sql.append("        ON mf.facility_id  = so.facility_id                                ");
        sql.append(" LEFT JOIN mst_product mp                                                  ");
        sql.append("        ON mp.product_id  = sp.product_id                                  ");
        sql.append(" LEFT JOIN cmm_consumer cc                                                 ");
        sql.append("        ON cc.consumer_id  = so.cmm_consumer_id                            ");
        sql.append(" LEFT JOIN invoice i                                                       ");
        sql.append("        ON i.invoice_id  = isi.invoice_id                                  ");
        sql.append(" LEFT JOIN delivery_order do2                                              ");
        sql.append("        ON do2.delivery_order_id  = dsi.delivery_order_id                  ");
        sql.append("     WHERE sp.frame_no     = :frameNo                                      ");
        sql.append("       AND sp.facility_id  = :facilityId                                   ");

        params.put("frameNo", form.getFrameNo());
        params.put("facilityId", form.getPointId());
        return super.queryForList(sql.toString(), params, SDM050801BO.class);
    }

    @Override
    public List<SDQ010702DetailBO> getRecManMaintDetail(Long receiptManifestId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT sp.frame_no                AS frameNo                         ");
        sql.append("         , sp.engine_no               AS engineNo                        ");
        sql.append("         , sp.bar_code                AS barCd                           ");
        sql.append("         , sp.serialized_product_id   AS serializedProductId             ");
        sql.append("         , mp.product_cd              AS modelCd                         ");
        sql.append("         , mp.sales_description       AS modelNm                         ");
        sql.append("         , mp.product_id              AS modelId                         ");
        sql.append("         , mp.color_nm                AS colorNm                         ");
        sql.append("         , ROUND(SUM(sp.stu_price))   AS purchasePrice                   ");
        sql.append("      FROM serialized_product sp                                         ");
        sql.append(" LEFT JOIN receipt_manifest_serialized_item rmsi                         ");
        sql.append("        ON rmsi.serialized_product_id  = sp.serialized_product_id        ");
        sql.append(" LEFT JOIN receipt_manifest_item rmi                                     ");
        sql.append("        ON rmi.receipt_manifest_item_id = rmsi.receipt_manifest_item_id  ");
        sql.append(" LEFT JOIN receipt_manifest rm                                           ");
        sql.append("        ON rm.receipt_manifest_id = rmi.receipt_manifest_id              ");
        sql.append(" LEFT JOIN mst_product mp                                                ");
        sql.append("        ON sp.product_id = mp.product_id                                 ");
        sql.append(" LEFT JOIN cmm_mst_organization cmo                                      ");
        sql.append("        ON rm.from_organization  = cmo.organization_id                   ");
        sql.append("     WHERE rm.receipt_manifest_id = :receiptManifestId                   ");
        sql.append("  GROUP BY sp.frame_no, sp.engine_no, sp.bar_code, sp.serialized_product_id, mp.product_cd, mp.product_id, mp.color_nm");
        sql.append("  ORDER BY sp.frame_no, sp.engine_no, sp.bar_code, mp.product_cd, mp.product_id, mp.color_nm");

        params.put("receiptManifestId", receiptManifestId);
        return super.queryForList(sql.toString(), params, SDQ010702DetailBO.class);
    }
}