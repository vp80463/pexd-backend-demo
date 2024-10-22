package com.a1stream.domain.custom.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.ConsumerSerialProRelationType;
import com.a1stream.common.model.ConsumerVLBO;
import com.a1stream.common.model.ConsumerVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.common.CmmConsumerBO;
import com.a1stream.domain.bo.master.CMM010301BO;
import com.a1stream.domain.bo.master.CMM010301ExportBO;
import com.a1stream.domain.bo.unit.SVM010402ConsumerInfoBO;
import com.a1stream.domain.custom.CmmConsumerRepositoryCustom;
import com.a1stream.domain.form.master.CMM010301Form;
import com.a1stream.domain.form.master.CMM010302Form;
import com.a1stream.domain.form.service.SVM011001Form;
import com.a1stream.domain.logic.RepositoryLogic;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

public class CmmConsumerRepositoryCustomImpl extends JpaNativeQuerySupportRepository
        implements CmmConsumerRepositoryCustom {

    @Resource
    private RepositoryLogic repositoryLogic;

    @Override
    public ValueListResultBO findConsumerByUnitList(ConsumerVLForm model, String siteId) {

        List<ConsumerVLBO> result = new ArrayList<>();
        Integer count = CommonConstants.INTEGER_ZERO;

        //FrameNo与MobilePhone为仅有的两种必入力情况，如设计变更，自行添加查询方法
        if (StringUtils.isNotBlank(model.getFrameNo())) {

            result = this.findConsumerByFrame(model, siteId);
            count = this.countConsumerByFrame(model, siteId);
        }
        else if (StringUtils.isNotBlank(model.getMobilePhone()) || StringUtils.isNotBlank(model.getContent())) {

            String mobilephone = StringUtils.isNotBlank(model.getMobilePhone())? model.getMobilePhone() : model.getContent();
            model.setMobilePhone(mobilephone);
            result = this.findConsumerByMobilePhone(model, siteId);
            count = this.countConsumerByMobilePhone(model, siteId);
        }

        return new ValueListResultBO(result, count);
    }

    /**
     * @author HXC
     */
    @Override
    public CmmConsumerBO findOwnerBySerialProId(String siteId, Long serialProId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("  SELECT cc.consumer_id   as consumerId    ");
        sql.append("       , cc.first_nm      as firstNm       ");
        sql.append("       , cc.middle_nm     as middleNm      ");
        sql.append("       , cc.last_nm       as lastNm        ");
        sql.append("       , cpd.mobile_phone as mobilePhone   ");
        sql.append("       , cc.email         as email         ");
        sql.append("   FROM cmm_consumer cc                    ");
        sql.append("   LEFT JOIN consumer_private_detail cpd                 ");
        sql.append("          ON cpd.consumer_id = cc.consumer_id            ");
        sql.append("         AND cpd.site_id = :siteId                       ");
        sql.append("  WHERE cc.consumer_id in (                              ");
        sql.append("       SELECT consumer_id                                ");
        sql.append("         FROM cmm_consumer_serial_pro_relation ccspr     ");
        sql.append("        WHERE ccspr.serialized_product_id = :serialProId ");
        sql.append("          AND ccspr.consumer_serialized_product_relation_type_id = :relationType ");
        sql.append("         LIMIT 1 )                                       ");

        params.put("siteId", siteId);
        params.put("serialProId", serialProId);
        params.put("relationType", PJConstants.ConsumerSerialProRelationType.OWNER.getCodeDbid());

        return super.queryForSingle(sql.toString(), params, CmmConsumerBO.class);
    }

    @Override
    public List<CMM010301BO> findConsumerInfoList(CMM010301Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("      SELECT cc.consumer_type                        as consumerType  ");
        sql.append("           , cc.consumer_full_nm                     as consumerNm    ");
        sql.append("           , cc.vip_no                               as vipNo         ");
        sql.append("           , (SELECT mobile_phone                                     ");
        sql.append("                FROM consumer_private_detail                          ");
        sql.append("               WHERE consumer_id = cc.consumer_id                     ");
        sql.append("                 AND site_id = :siteId)              as phone         ");
        sql.append("           , cc.id_no                                as idNo          ");
        sql.append("           , (SELECT count(1)                                         ");
        sql.append("                FROM cmm_consumer_serial_pro_relation                 ");
        sql.append("               WHERE consumer_id = cc.consumer_id )  as qty           ");
        sql.append("           , cc.consumer_id                          as consumerId    ");

        sql.append("        FROM cmm_consumer cc                                          ");
        sql.append("       WHERE 1 = 1                                                    ");
        //header search
        if(form.isHeaderFlag()) {
            if(StringUtils.isNotBlank(form.getLastNm())) {
                sql.append("     AND UPPER(cc.last_nm) like :lastNm                       ");
                params.put("lastNm", StringUtils.upperCase(form.getLastNm()) + "%");
            }

            if(StringUtils.isNotBlank(form.getMiddleNm())) {
                sql.append("     AND UPPER(cc.middle_nm) like :middleNm                   ");
                params.put("middleNm", StringUtils.upperCase(form.getMiddleNm()) + "%");
            }

            if(StringUtils.isNotBlank(form.getFirstNm())) {
                sql.append("     AND UPPER(cc.first_nm) like :firstNm                     ");
                params.put("firstNm", StringUtils.upperCase(form.getFirstNm()) + "%");
            }

            if(StringUtils.isNotBlank(form.getPhone())) {
                sql.append("     AND exists(select 1                                      ");
                sql.append("                  from consumer_private_detail                ");
                sql.append("                 where cc.consumer_id = consumer_id           ");
                sql.append("                   and mobile_phone = :phone)                 ");
                params.put("phone", form.getPhone());
            }

            if(StringUtils.isNotBlank(form.getIdNo())) {
                sql.append("     AND cc.id_no = :idNo                                     ");
                params.put("idNo", form.getIdNo());
            }

            if(StringUtils.isNotBlank(form.getVipNo())) {
                sql.append("     AND cc.vip_no = :vipNo                                   ");
                params.put("vipNo", form.getVipNo());
            }
        }//condition search
        else {
            sql.append("     AND cc.consumer_type = :consumerType                         ");

            if(StringUtils.isNotBlank(form.getGender())) {
                sql.append("     AND cc.gender = :gender                                  ");
                params.put("gender", form.getGender());
            }

            if(StringUtils.isNotBlank(form.getBirthday())) {
                sql.append("     AND substring(cc.birth_date,1,2) = :birthday             ");
                params.put("birthday", form.getBirthday());
            }

            if(StringUtils.isNotBlank(form.getAgeDateFrom())) {
                sql.append("     AND cc.birth_year||birth_date  >= :ageDateFrom           ");
                params.put("ageDateFrom", form.getAgeDateFrom());
            }

            if(StringUtils.isNotBlank(form.getAgeDateTo())) {
                sql.append("     AND cc.birth_year||birth_date  <= :ageDateTo             ");
                params.put("ageDateTo", form.getAgeDateTo());
            }

            if(form.getProvince() != null) {
                sql.append("     AND cc.province_geography_id = :province                 ");
                params.put("province", form.getProvince());
            }

            if(form.getCityId() != null) {
                sql.append("     AND cc.city_geography_id = :cityId                       ");
                params.put("cityId", form.getCityId());
            }

            if(StringUtils.isNotBlank(form.getRegDateFrom())) {
                sql.append("     AND cc.registration_date >= :registrationDateFrom        ");
                params.put("registrationDateFrom", form.getRegDateFrom());
            }

            if(StringUtils.isNotBlank(form.getRegDateTo())) {
                sql.append("     AND cc.registration_date <= :registrationDateTo          ");
                params.put("registrationDateTo", form.getRegDateTo());
            }

            if(form.getVipNoHolder().equals(CommonConstants.CHAR_Y)) {
                sql.append(" AND cc.vip_no <> ''                                          ");
            }
            params.put("consumerType", form.getConsumerType());

            if(StringUtils.isNotBlank(form.getSalesDateFrom()) && StringUtils.isNotBlank(form.getSalesDateTo())
               || form.getModelId() != null) {

                sql.append(" AND EXISTS(SELECT 1                                          ");
                sql.append("             FROM cmm_consumer_serial_pro_relation ccspr      ");
                sql.append("       INNER JOIN cmm_serialized_product csp                  ");
                sql.append("               ON ccspr.serialized_product_id = csp.serialized_product_id ");
                sql.append("       INNER JOIN cmm_registration_document crd               ");
                sql.append("               ON crd.serialized_product_id = csp.serialized_product_id   ");
                sql.append("              AND crd.site_id = :siteId                       ");
                sql.append("            WHERE ccspr.consumer_id = cc.consumer_id          ");
                if(StringUtils.isNotBlank(form.getSalesDateFrom()) && StringUtils.isNotBlank(form.getSalesDateTo())) {

                    sql.append("          AND csp.stu_date >=  :salesDateFrom             ");
                    sql.append("          AND csp.stu_date <=  :salesDateTo               ");
                    params.put("salesDateFrom", form.getSalesDateFrom());
                    params.put("salesDateTo", form.getSalesDateTo());
                }

                if(form.getModelId() != null) {

                    sql.append("         AND csp.product_id = :modelId                   ");
                    params.put("modelId", form.getModelId());
                }
                sql.append("    )                                                        ");
                params.put("siteId", siteId);
            }

            if(StringUtils.isNotBlank(form.getServiceDateFrom()) && StringUtils.isNotBlank(form.getServiceDateTo())) {

                sql.append(" AND EXISTS(SELECT 1                                          ");
                sql.append("             FROM cmm_service_history csh                     ");
                sql.append("            WHERE csh.site_id     = :siteId                   ");
                sql.append("              AND csh.consumer_id = cc.consumer_id            ");
                sql.append("              AND csh.order_date >=  :serviceDateFrom         ");
                sql.append("              AND csh.order_date <=  :serviceDateTo)          ");
                params.put("siteId", siteId);
                params.put("serviceDateFrom", form.getServiceDateFrom());
                params.put("serviceDateTo", form.getServiceDateTo());
            }
        }
        params.put("siteId", siteId);

        return super.queryForList(sql.toString(), params, CMM010301BO.class);
    }

    @Override
    public List<CMM010301ExportBO> findConsumerExportList(CMM010301Form form, PJUserDetails uc) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("      SELECT distinct :dealerCd            as dealer                  ");
        sql.append("           , :pointCd                      as point                   ");
        sql.append("           , cc.id_no                      as idNo                    ");
        sql.append("           , cc.consumer_type              as consumerType            ");
        sql.append("           , cc.last_nm                    as lastNm                  ");
        sql.append("           , cc.middle_nm                  as middleNm                ");
        sql.append("           , cc.first_nm                   as firstNm                 ");
        sql.append("           , (SELECT mobile_phone                                     ");
        sql.append("                FROM consumer_private_detail                          ");
        sql.append("               WHERE consumer_id = cc.consumer_id                     ");
        sql.append("                 AND site_id = :siteId)    as phone1                  ");
        sql.append("           , (SELECT mobile_phone_2                                   ");
        sql.append("                FROM consumer_private_detail                          ");
        sql.append("               WHERE consumer_id = cc.consumer_id                     ");
        sql.append("                 AND site_id = :siteId)    as phone2                  ");
        sql.append("           , (SELECT mobile_phone_3                                   ");
        sql.append("                FROM consumer_private_detail                          ");
        sql.append("               WHERE consumer_id = cc.consumer_id                     ");
        sql.append("                 AND site_id = :siteId)    as phone3                  ");
        sql.append("           , cc.email                      as email                   ");
        sql.append("           , cc.gender                     as gender                  ");
        sql.append("           , cc.vip_no                     as vipNo                   ");
        sql.append("           , cc.birth_year||cc.birth_date  as birthday                ");
        sql.append("           , (EXTRACT(YEAR FROM CURRENT_DATE) - cc.birth_year::INTEGER)::VARCHAR as age  ");
        sql.append("           , cc.occupation                 as occupation              ");
        sql.append("           , cc.province_geography_id      as province                ");
        sql.append("           , cc.city_geography_id          as district                ");
        sql.append("           , cc.address||cc.address_2      as address                 ");
        sql.append("           , so.payment_method_type        as paymentMethod           ");
        sql.append("           , cc.comment                    as comment                 ");
        sql.append("           , mp.product_cd                 as modelCd                 ");
        sql.append("           , mp.sales_description          as modelNm                 ");
        sql.append("           , mp.color_nm                   as colorNm                 ");
        sql.append("           , csp.frame_no                  as frameNo                 ");
        sql.append("           , csp.engine_no                 as engineNo                ");
        sql.append("           , csp.stu_date                  as salesDate               ");
        sql.append("           , crd.use_type                  as userType                ");
        sql.append("           , crd.purchase_type             as purchaseType            ");
        sql.append("           , crd.psv_brand_nm              as psvBrandNm              ");
        sql.append("           , crd.p_bike_nm                 as pBikeNm                 ");
        sql.append("           , crd.mt_at_id                  as mtatId                  ");
        sql.append("           , crd.family_num                as familyNum               ");
        sql.append("           , crd.num_bike                  as numBike                 ");
        sql.append("           , cc.mc_brand                   as currentBikeBrand        ");
        sql.append("           , cc.mc_purchase_date           as currentBikePurchase     ");
        sql.append("           , cc.interest_model             as interestModel           ");
        sql.append("           , cc.registration_reason        as registrationReason      ");

        sql.append("        FROM cmm_consumer cc                                          ");
        sql.append("   LEFT JOIN cmm_consumer_serial_pro_relation ccspr                   ");
        sql.append("          ON ccspr.consumer_id = cc.consumer_id                       ");
        sql.append("   LEFT JOIN cmm_serialized_product csp                               ");
        sql.append("          ON ccspr.serialized_product_id = csp.serialized_product_id  ");
        sql.append("   LEFT JOIN mst_product mp                                           ");
        sql.append("          ON csp.product_id = mp.product_id                           ");
        sql.append("   LEFT JOIN cmm_registration_document crd                            ");
        sql.append("          ON ccspr.consumer_id = crd.consumer_id                      ");
        sql.append("         AND ccspr.serialized_product_id = crd.serialized_product_id  ");
        sql.append("         AND crd.site_id = :siteId                                    ");
        sql.append("   LEFT JOIN sales_order so                                           ");
        sql.append("          ON so.sales_order_id = crd.sales_order_id                   ");

        sql.append("       WHERE 1 = 1                                                    ");
        //header search
        if(form.isHeaderFlag()) {
            if(StringUtils.isNotBlank(form.getLastNm())) {
                sql.append("     AND UPPER(cc.last_nm) like :lastNm                       ");
                params.put("lastNm", StringUtils.upperCase(form.getLastNm()) + "%");
            }

            if(StringUtils.isNotBlank(form.getMiddleNm())) {
                sql.append("     AND UPPER(cc.middle_nm) like :middleNm                   ");
                params.put("middleNm", StringUtils.upperCase(form.getMiddleNm()) + "%");
            }

            if(StringUtils.isNotBlank(form.getFirstNm())) {
                sql.append("     AND UPPER(cc.first_nm) like :firstNm                     ");
                params.put("firstNm", StringUtils.upperCase(form.getFirstNm()) + "%");
            }

            if(StringUtils.isNotBlank(form.getPhone())) {
                sql.append("     AND exists(select 1                                      ");
                sql.append("                  from consumer_private_detail                ");
                sql.append("                 where cc.consumer_id = consumer_id           ");
                sql.append("                   and mobile_phone = :phone)                 ");
                params.put("phone", form.getPhone());
            }

            if(StringUtils.isNotBlank(form.getIdNo())) {
                sql.append("     AND cc.id_no = :idNo                                     ");
                params.put("idNo", form.getIdNo());
            }

            if(StringUtils.isNotBlank(form.getVipNo())) {
                sql.append("     AND cc.vip_no = :vipNo                                   ");
                params.put("vipNo", form.getVipNo());
            }
        }//condition search
        else {
            sql.append("     AND cc.consumer_type = :consumerType                         ");

            if(StringUtils.isNotBlank(form.getGender())) {
                sql.append("     AND cc.gender = :gender                                  ");
                params.put("gender", form.getGender());
            }

            if(StringUtils.isNotBlank(form.getBirthday())) {
                sql.append("     AND substring(cc.birth_date,1,2) = :birthday             ");
                params.put("birthday", form.getBirthday());
            }

            if(StringUtils.isNotBlank(form.getAgeDateFrom())) {
                sql.append("     AND cc.birth_year||birth_date  >= :ageDateFrom           ");
                params.put("ageDateFrom", form.getAgeDateFrom());
            }

            if(StringUtils.isNotBlank(form.getAgeDateTo())) {
                sql.append("     AND cc.birth_year||birth_date  <= :ageDateTo             ");
                params.put("ageDateTo", form.getAgeDateTo());
            }

            if(form.getProvince() != null) {
                sql.append("     AND cc.province_geography_id = :province                 ");
                params.put("province", form.getProvince());
            }

            if(form.getCityId() != null) {
                sql.append("     AND cc.city_geography_id = :cityId                       ");
                params.put("cityId", form.getCityId());
            }

            if(StringUtils.isNotBlank(form.getRegDateFrom())) {
                sql.append("     AND cc.registration_date >= :registrationDateFrom        ");
                params.put("registrationDateFrom", form.getRegDateFrom());
            }

            if(StringUtils.isNotBlank(form.getRegDateTo())) {
                sql.append("     AND cc.registration_date <= :registrationDateTo          ");
                params.put("registrationDateTo", form.getRegDateTo());
            }

            if(form.getVipNoHolder().equals(CommonConstants.CHAR_Y)) {
                sql.append(" AND cc.vip_no <> ''                                          ");
            }
            params.put("consumerType", form.getConsumerType());

            if(StringUtils.isNotBlank(form.getSalesDateFrom()) && StringUtils.isNotBlank(form.getSalesDateTo())
               || form.getModelId() != null) {

                sql.append(" AND EXISTS(SELECT 1                                          ");
                sql.append("              FROM cmm_consumer_serial_pro_relation ccspr     ");
                sql.append("        INNER JOIN cmm_serialized_product csp                 ");
                sql.append("                ON ccspr.serialized_product_id = csp.serialized_product_id ");
                sql.append("        INNER JOIN cmm_registration_document crd              ");
                sql.append("                ON crd.serialized_product_id = csp.serialized_product_id   ");
                sql.append("               AND crd.site_id = :siteId                      ");
                sql.append("             WHERE ccspr.consumer_id = cc.consumer_id         ");
                if(StringUtils.isNotBlank(form.getSalesDateFrom()) && StringUtils.isNotBlank(form.getSalesDateTo())) {

                    sql.append("           AND csp.stu_date  >=  :salesDateFrom           ");
                    sql.append("           AND csp.stu_date  <=  :salesDateTo             ");
                    params.put("salesDateFrom", form.getSalesDateFrom());
                    params.put("salesDateTo", form.getSalesDateTo());
                }

                if(form.getModelId() != null) {

                    sql.append("           AND csp.product_id =  :modelId                 ");
                    params.put("modelId", form.getModelId());
                }
                sql.append("    )                                                         ");
                params.put("siteId", uc.getDealerCode());
            }

            if(StringUtils.isNotBlank(form.getServiceDateFrom()) && StringUtils.isNotBlank(form.getServiceDateTo())) {

                sql.append(" AND EXISTS(SELECT 1                                          ");
                sql.append("             FROM cmm_service_history csh                     ");
                sql.append("            WHERE csh.site_id = :siteId                       ");
                sql.append("              AND csh.consumer_id = cc.consumer_id            ");
                sql.append("              AND csh.order_date >=  :serviceDateFrom         ");
                sql.append("              AND csh.order_date <=  :serviceDateTo)          ");
                params.put("siteId", uc.getDealerCode());
                params.put("serviceDateFrom", form.getServiceDateFrom());
                params.put("serviceDateTo", form.getServiceDateTo());
            }
        }
        params.put("siteId", uc.getDealerCode());
        params.put("dealerCd", uc.getDealerCode());
        params.put("pointCd", uc.getDefaultPointCd());

        return super.queryForList(sql.toString(), params, CMM010301ExportBO.class);
    }

    @Override
    public CMM010302Form getConsumerMaintenanceInfo(CMM010302Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT cc.consumer_type                                                     as  consumerType            ");
        sql.append("          , cc.last_nm                                                           as  lastNm                  ");
        sql.append("          , cc.middle_nm                                                         as  middleNm                ");
        sql.append("          , cc.first_nm                                                          as  firstNm                 ");
        sql.append("          , cppr.agreement_result                                                as  privacyPolicyResult     ");
        sql.append("          , cc.gender                                                            as  gender                  ");
        sql.append("          , cpd.mobile_phone                                                     as  mobilePhone             ");
        sql.append("          , cpd.mobile_phone_2                                                   as  mobile2                 ");
        sql.append("          , cpd.mobile_phone_3                                                   as  mobile3                 ");
        sql.append("          , cc.birth_year||cc.birth_date                                         as  birthday                ");
        sql.append("          , (EXTRACT(YEAR FROM CURRENT_DATE) - cc.birth_year::INTEGER)::VARCHAR  as  age                     ");
        sql.append("          , cc.registration_date                                                 as  regDate                 ");
        sql.append("          , cc.vip_no                                                            as  vipNo                   ");
        sql.append("          , cc.province_geography_id                                             as  province                ");
        sql.append("          , cc.city_geography_id                                                 as  cityId                  ");
        sql.append("          , cc.address                                                           as  addr1                   ");
        sql.append("          , cc.address_2                                                         as  addr2                   ");
        sql.append("          , cc.email                                                             as  email1                  ");
        sql.append("          , cc.email_2                                                           as  email2                  ");
        sql.append("          , cc.occupation                                                        as  occupation              ");
        sql.append("          , cc.comment                                                           as  comment                 ");
        sql.append("          , cc.interest_model                                                    as  interestModel           ");
        sql.append("          , cc.mc_brand                                                          as  currentBikeBrand        ");
        sql.append("          , cc.mc_purchase_date                                                  as  currentBikePurchase     ");
        sql.append("          , cc.registration_reason                                               as  registrationReason      ");
        sql.append("          , cc.id_classification_no                                              as  consumerIdentification  ");
        sql.append("          , cc.id_no                                                             as  idNo                    ");
        sql.append("          , cc.visa_no                                                           as  visaNo                  ");
        sql.append("          , cc.fax_no                                                            as  faxNo                   ");
        sql.append("          , cc.post_code                                                         as  postCd                  ");
        sql.append("          , cc.telephone                                                         as  telephone               ");

        sql.append("       FROM cmm_consumer cc                                                                                  ");
        sql.append(" INNER JOIN consumer_private_detail cpd                                                                      ");
        sql.append("         ON cc.consumer_id = cpd.consumer_id                                                                 ");
        sql.append("  LEFT JOIN consumer_privacy_policy_result cppr                                                              ");
        sql.append("         ON cc.consumer_id = cppr.consumer_id                                                                ");

        sql.append("      WHERE cc.consumer_id = :consumerId                                                                     ");

        params.put("consumerId", form.getConsumerId());

        return super.queryForSingle(sql.toString(), params, CMM010302Form.class);
    }

    @Override
    public Long getMainConsumerId(CMM010302Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("   SELECT consumer_id                            ");
        sql.append("     FROM cmm_consumer                           ");
        sql.append("    WHERE consumer_retrieve = :consumerRetrieve  ");
        sql.append(" ORDER BY last_updated desc                      ");
        sql.append("    LIMIT 1                                      ");

        params.put("consumerRetrieve", (form.getLastNm() + form.getMiddleNm() + form.getFirstNm() + form.getMobilePhone()).replaceAll("\\s", "").toUpperCase());

        return super.queryForSingle(sql.toString(), params, Long.class);
    }

    @Override
    public SVM011001Form getConsumerInfoByFrameNo(SVM011001Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("  SELECT consumer_id       as  consumerId                                                ");
        sql.append("       , vip_no            as  consumerCd                                                ");
        sql.append("       , consumer_full_nm  as  consumerNm                                                ");
        sql.append("    FROM cmm_consumer                                                                    ");
        sql.append("   WHERE consumer_id in (SELECT consumer_id                                              ");
        sql.append("                           FROM cmm_consumer_serial_pro_relation                         ");
        sql.append("                          WHERE serialized_product_id = :serializedProductId             ");
        sql.append("                            AND consumer_serialized_product_relation_type_id = :typeId)  ");

        params.put("serializedProductId", form.getSerializedProductId());
        params.put("typeId", ConsumerSerialProRelationType.OWNER.getCodeDbid());

        return super.queryForSingle(sql.toString(), params, SVM011001Form.class);
    }

    @Override
    public List<Long> getSerialProductIds(Long consumerId, String mobilephone, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("       SELECT ccspr.serialized_product_id            ");
        sql.append("         FROM cmm_consumer cc                        ");
        sql.append("    LEFT JOIN cmm_consumer_serial_pro_relation ccspr ");
        sql.append("           ON cc.consumer_id = ccspr.consumer_id     ");
        sql.append("    LEFT JOIN consumer_private_detail cd             ");
        sql.append("           ON cd.consumer_id = cc.consumer_id        ");
        sql.append("          AND cd.site_id = :siteId                   ");
        sql.append("        WHERE 1 = 1                                  ");

        params.put("siteId", siteId);

        if (!Nulls.isNull(consumerId)) {
            sql.append(" AND cc.consumer_id = :consumerId ");
            params.put("consumerId", consumerId);
        }

        if (StringUtils.isNotBlank(mobilephone)) {
            sql.append(" AND cd.mobile_phone = :mobilePhone ");
            params.put("mobilePhone", mobilephone);
        }

        return super.queryForList(sql.toString(), params, Long.class);
    }

    private List<ConsumerVLBO> findConsumerByMobilePhone(ConsumerVLForm model, String siteId){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        this.addHeadColumnForVL(sql, params);

        sql.append("         , :mobilePhone  AS mobilePhone                  ");
        sql.append("      FROM cmm_consumer cc                              ");
        sql.append(" LEFT JOIN consumer_private_detail cd                   ");
        sql.append("        ON cd.consumer_id = cc.consumer_id              ");
        sql.append("       AND cd.site_id = :siteId                         ");
        sql.append(" LEFT JOIN consumer_privacy_policy_result cr            ");
        sql.append("        ON cr.consumer_retrieve = cd.consumer_retrieve  ");
        sql.append("       AND cr.site_id = :siteId                         ");
        sql.append("     WHERE cc.consumer_id in (                          ");
        sql.append("           SELECT consumer_id                           ");
        sql.append("             FROM consumer_private_detail               ");
        sql.append("            WHERE mobile_phone = :mobilePhone           ");
        sql.append("          )                                             ");

        params.put("siteId", siteId);
        params.put("mobilePhone", model.getMobilePhone());

        if (StringUtils.isNotBlank(model.getVipNo())) {
            sql.append("   AND cc.vip_no LIKE :vipNo      ");
            params.put("vipNo", CommonConstants.CHAR_PERCENT + model.getVipNo() + CommonConstants.CHAR_PERCENT);
        }

        if (StringUtils.isNotBlank(model.getConsumerName())) {
            sql.append("   AND cc.consumer_retrieve LIKE :consumerName      ");
            params.put("consumerName", model.getConsumerName().replaceAll("\\s", "").toUpperCase().concat(CommonConstants.CHAR_PERCENT));
        }

        sql.append(" ORDER BY cc.consumer_full_nm ");

        repositoryLogic.appendPagePara(model.getPageSize(), model.getCurrentPage(), sql, params);

        return super.queryForList(sql.toString(), params, ConsumerVLBO.class);
    }

    private List<ConsumerVLBO> findConsumerByFrame(ConsumerVLForm model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        this.addHeadColumnForVL(sql, params);

        sql.append("         , cd.mobile_phone                  AS mobilePhone  ");
        sql.append("         , ccspr.owner_flag                 AS ownerFlag    ");
        sql.append("         , CASE ccspr.consumer_serialized_product_relation_type_id  ");
        sql.append("             WHEN '").append(PJConstants.ConsumerSerialProRelationType.OWNER.getCodeDbid()).append("' THEN '").append(PJConstants.ConsumerSerialProRelationType.OWNER.getCodeData1()).append("' ");
        sql.append("             ELSE '").append(PJConstants.ConsumerSerialProRelationType.USER.getCodeData1()).append("' ");
        sql.append("           END as type                   ");

        sql.append("        FROM cmm_serialized_product csp                              ");
        sql.append("  INNER JOIN cmm_consumer_serial_pro_relation ccspr                  ");
        sql.append("          ON ccspr.serialized_product_id = csp.serialized_product_id ");
        sql.append("  INNER JOIN cmm_consumer cc                                         ");
        sql.append("          ON cc.consumer_id = ccspr.consumer_id                      ");
        sql.append("   LEFT JOIN consumer_private_detail cd                              ");
        sql.append("          ON cd.site_id = :siteId                                    ");
        sql.append("         AND cd.consumer_id = cc.consumer_id                         ");
        sql.append("   LEFT JOIN consumer_privacy_policy_result cr                       ");
        sql.append("          ON cr.site_id = :siteId                                    ");
        sql.append("         AND cr.consumer_retrieve = cd.consumer_retrieve             ");
        sql.append("       WHERE csp.frame_no = :frameNo                                 ");

        params.put("siteId", siteId);
        params.put("frameNo", model.getFrameNo());

        this.addConditionForVLByFrame(model, params, sql);

        sql.append(" ORDER BY cc.consumer_full_nm ");

        repositoryLogic.appendPagePara(model.getPageSize(), model.getCurrentPage(), sql, params);

        return super.queryForList(sql.toString(), params, ConsumerVLBO.class);
    }

    private Integer countConsumerByMobilePhone(ConsumerVLForm model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT COUNT(DISTINCT consumer_id)   ");
        sql.append("        FROM consumer_private_detail  ");
        sql.append("        WHERE mobile_phone = :mobilePhone and site_id = :siteId    ");

        if (StringUtils.isNotBlank(model.getConsumerName())) {
            sql.append("   AND consumer_retrieve LIKE :consumerName      ");
            params.put("consumerName", model.getConsumerName().replaceAll("\\s", "").toUpperCase().concat(CommonConstants.CHAR_PERCENT));
        }

        params.put("mobilePhone", model.getMobilePhone());
        params.put("siteId", siteId);
        return super.queryForSingle(sql.toString(), params, Integer.class);
    }

    private Integer countConsumerByFrame(ConsumerVLForm model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT COUNT(1)   ");
        sql.append("        FROM cmm_serialized_product csp                              ");
        sql.append("  INNER JOIN cmm_consumer_serial_pro_relation ccspr                  ");
        sql.append("          ON ccspr.serialized_product_id = csp.serialized_product_id ");
        sql.append("  INNER JOIN cmm_consumer cc                                         ");
        sql.append("          ON cc.consumer_id = ccspr.consumer_id                      ");

        if (StringUtils.isNotBlank(model.getMobilePhone())) {
            sql.append("   LEFT JOIN consumer_private_detail cd                          ");
            sql.append("          ON cd.site_id = :siteId                                ");
            sql.append("         AND cd.consumer_id = cc.consumer_id                     ");
            params.put("siteId", siteId);
        }

        sql.append("        WHERE csp.frame_no = :frameNo                                ");
        params.put("frameNo", model.getFrameNo());

        this.addConditionForVLByFrame(model, params, sql);

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }


    private void addHeadColumnForVL(StringBuilder sql, Map<String, Object> params) {

        sql.append("    SELECT cc.consumer_id                   AS consumerId   ");
        sql.append("         , cc.consumer_full_nm              AS consumerName ");
        sql.append("         , cc.consumer_type                 AS consumerType ");
        sql.append("         , cc.vip_no                        AS vipNo        ");
        sql.append("         , cc.first_nm                      AS firstNm      ");
        sql.append("         , cc.middle_nm                     AS middleNm     ");
        sql.append("         , cc.last_nm                       AS lastNm       ");
        sql.append("         , cc.gender                        AS gender       ");
        sql.append("         , cc.province_geography_id         AS province     ");
        sql.append("         , cc.city_geography_id             AS city         ");
        sql.append("         , COALESCE(cc.email, cc.email_2)   AS email        ");
        sql.append("         , cc.birth_date                    AS birthDate    ");
        sql.append("         , cc.birth_year                    AS birthYear    ");
        sql.append("         , cc.address                       AS address      ");
        sql.append("         , cc.address_2                     AS address2     ");
        sql.append("         , CAST(DATE_PART('year', AGE(CURRENT_DATE, TO_DATE(CONCAT(cc.birth_year, LPAD(cc.birth_date, 4, '0')), 'YYYYMMDD'))) AS INT) AS age ");
        sql.append("         , cr.agreement_result              AS agreementResult  ");
        sql.append("         , CASE WHEN cd.consumer_id IS NOT NULL THEN :charL ELSE :charS END AS position ");

        params.put("charL", CommonConstants.CHAR_L);
        params.put("charS", CommonConstants.CHAR_S);
    }

    private void addConditionForVLByFrame(ConsumerVLForm model, Map<String, Object> params, StringBuilder sql) {

        if (StringUtils.isNotBlank(model.getMobilePhone())) {
            sql.append("   AND cd.mobile_phone = :mobilePhone                            ");
            params.put("mobilePhone", model.getMobilePhone());
        }

        if (StringUtils.isNotBlank(model.getConsumerName())) {
            sql.append("   AND cc.consumer_retrieve LIKE :consumerName                   ");
            params.put("consumerName", model.getConsumerName().replaceAll("\\s", "").toUpperCase().concat(CommonConstants.CHAR_PERCENT));
        }
    }

    @Override
    public SVM010402ConsumerInfoBO getConsumerBasicInfoByConsumerId(Long cmmSerializedProductId, Long consumerId, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT ccspr.consumer_serialized_product_relation_type_id     AS relationType   ");
        sql.append("         , cc.consumer_full_nm                                    AS consumer       ");
        sql.append("         , cc.first_nm                                            AS firstNm        ");
        sql.append("         , cc.middle_nm                                           AS middleNm       ");
        sql.append("         , cc.last_nm                                             AS lastNm         ");
        sql.append("         , cc.gender                                              AS gender         ");
        sql.append("         , cc.province_geography_id                               AS province       ");
        sql.append("         , cc.city_geography_id                                   AS city           ");
        sql.append("         , cd.mobile_phone                                        AS mobilePhone    ");
        sql.append("         , cc.telephone                                           AS telephone      ");
        sql.append("         , cc.address                                             AS address        ");
        sql.append("         , cc.address_2                                           AS address2       ");
        sql.append("         , cc.consumer_id                                         AS consumerId     ");
        sql.append("      FROM cmm_consumer cc                                                          ");
        sql.append(" LEFT JOIN cmm_consumer_serial_pro_relation ccspr                                   ");
        sql.append("        ON ccspr.consumer_id = cc.consumer_id                                       ");
        sql.append(" LEFT JOIN consumer_private_detail cd                                               ");
        sql.append("        ON cd.consumer_id = cc.consumer_id                                          ");
        sql.append("       AND cd.site_id = :siteId                                                     ");
        sql.append("     WHERE ccspr.consumer_id = :consumerId                                          ");
        sql.append("       AND ccspr.serialized_product_id = :cmmSerializedProductId                    ");

        params.put("siteId", siteId);
        params.put("consumerId", consumerId);
        params.put("cmmSerializedProductId", cmmSerializedProductId);

        return super.queryForSingle(sql.toString(), params, SVM010402ConsumerInfoBO.class);
    }
}
