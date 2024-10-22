package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ConsumerSerialProRelationType;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.batch.SvVinCodeTelIFBO;
import com.a1stream.domain.bo.master.CMM010302BO;
import com.a1stream.domain.bo.unit.SVM010402BO;
import com.a1stream.domain.bo.unit.SVM010402ConsumerInfoBO;
import com.a1stream.domain.custom.CmmConsumerSerialProRelationRepositoryCustom;
import com.a1stream.domain.form.master.CMM010302Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class CmmConsumerSerialProRelationRepositoryCustomImpl extends JpaNativeQuerySupportRepository
        implements CmmConsumerSerialProRelationRepositoryCustom {

    @Override
    public List<CMM010302BO> getMotorcycleInfoList(CMM010302Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT csp.plate_no               as  plateNo                   ");
        sql.append("          , mb.brand_nm                as  brandNm                   ");
        sql.append("          , mp.sales_description       as  modelNm                   ");
        sql.append("          , csp.frame_no               as  frameNo                   ");
        sql.append("          , csp.engine_no              as  engineNo                  ");
        sql.append("          , ccspr.consumer_id          as  consumerId                ");
        sql.append("          , csp.serialized_product_id  as  serializedProductId       ");

        sql.append("       FROM cmm_consumer_serial_pro_relation ccspr                   ");
        sql.append("  LEFT JOIN cmm_serialized_product csp                               ");
        sql.append("         ON csp.serialized_product_id = ccspr.serialized_product_id  ");
        sql.append("  LEFT JOIN mst_product mp                                           ");
        sql.append("         ON mp.product_id = csp.serialized_product_id                ");
        sql.append("  LEFT JOIN mst_brand mb                                             ");
        sql.append("         ON mb.brand_id = mp.brand_id                                ");

        sql.append("      WHERE ccspr.consumer_id = :consumerId                          ");
        sql.append("   ORDER BY csp.plate_no                                             ");

        params.put("consumerId", form.getConsumerId());

        return super.queryForList(sql.toString(), params, CMM010302BO.class);
    }

    @Override
    public List<SVM010402ConsumerInfoBO> getMcConsumerData(Long cmmSerializedProductId, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT ccspr.consumer_serialized_product_relation_type_id AS relationType ");
        sql.append("         , cc.consumer_full_nm                                AS consumer     ");
        sql.append("         , cc.gender                                          AS gender       ");
        sql.append("         , cc.telephone                                       AS telephone    ");
        sql.append("         , cd.mobile_phone                                    AS mobilePhone  ");
        sql.append("         , cc.address                                         AS address      ");
        sql.append("         , cc.consumer_id                                     AS consumerId   ");
        sql.append("      FROM cmm_consumer_serial_pro_relation ccspr                             ");
        sql.append(" LEFT JOIN cmm_consumer cc                                                    ");
        sql.append("        ON ccspr.consumer_id = cc.consumer_id                                 ");
        sql.append(" LEFT JOIN consumer_private_detail cd                                         ");
        sql.append("        ON cd.consumer_id = cc.consumer_id                                    ");
        sql.append("       AND cd.site_id = :siteId                                               ");
        sql.append("     WHERE ccspr.serialized_product_id = :cmmSerializedProductId              ");
        sql.append("       AND ccspr.from_date <= :sysDate                                        ");
        sql.append("       AND ccspr.to_date >= :sysDate                                          ");

        params.put("siteId", siteId);
        params.put("cmmSerializedProductId", cmmSerializedProductId);
        params.put("sysDate", ComUtil.nowLocalDate());

        return super.queryForList(sql.toString(), params, SVM010402ConsumerInfoBO.class);
    }

    @Override
    public SVM010402BO getOwnerConsumerByMc(Long cmmSerializedProductId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT cc.consumer_full_nm    AS currentOwner                           ");
        sql.append("      FROM cmm_consumer_serial_pro_relation ccspr                           ");
        sql.append(" LEFT JOIN cmm_consumer cc                                                  ");
        sql.append("        ON ccspr.consumer_id = cc.consumer_id                               ");
        sql.append("     WHERE ccspr.serialized_product_id = :cmmSerializedProductId            ");
        sql.append("       AND ccspr.owner_flag = :ownerFlag                                    ");
        sql.append("       AND ccspr.consumer_serialized_product_relation_type_id = :S022OWNER  ");

        params.put("cmmSerializedProductId", cmmSerializedProductId);
        params.put("ownerFlag", CommonConstants.CHAR_Y);
        params.put("S022OWNER", ConsumerSerialProRelationType.OWNER.getCodeDbid());

        return super.queryForSingle(sql.toString(), params, SVM010402BO.class);
    }

    @Override
    public List<SvVinCodeTelIFBO> getAllOwnerMcByConsumerId(Long consumerId){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT csp.frame_no as VinHin                                              ");
        sql.append("      , ccspr.consumer_serialized_product_relation_id as queueId            ");
        sql.append("      , csp.stu_date as SalesDate                                           ");
        sql.append("      , ccspr.last_updated as OwnerChangedDate                              ");
        sql.append("   FROM cmm_consumer_serial_pro_relation ccspr                              ");
        sql.append("      LEFT JOIN cmm_serialized_product csp                                  ");
        sql.append("            ON csp.serialized_product_id = ccspr.serialized_product_id      ");
        sql.append("  WHERE ccspr.consumer_id = :consumerId                                     ");
        sql.append("    AND ccspr.consumer_serialized_product_relation_type_id = :S022OWNER     ");

        params.put("consumerId", consumerId);
        params.put("S022OWNER", ConsumerSerialProRelationType.OWNER.getCodeDbid());
        return super.queryForList(sql.toString(), params, SvVinCodeTelIFBO.class);
    }
}