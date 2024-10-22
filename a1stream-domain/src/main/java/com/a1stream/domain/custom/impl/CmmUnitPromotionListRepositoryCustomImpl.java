package com.a1stream.domain.custom.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.PromotionVLBO;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.unit.SDM010601BO;
import com.a1stream.domain.bo.unit.SDM050101BO;
import com.a1stream.domain.bo.unit.SDM050102BO;
import com.a1stream.domain.bo.unit.SDM050201BO;
import com.a1stream.domain.bo.unit.SDM050202BO;
import com.a1stream.domain.bo.unit.SDM050202HeaderBO;
import com.a1stream.domain.bo.unit.SDM050601BO;
import com.a1stream.domain.bo.unit.SDM050801BO;
import com.a1stream.domain.bo.unit.SDQ050101BO;
import com.a1stream.domain.custom.CmmUnitPromotionListRepositoryCustom;
import com.a1stream.domain.form.unit.SDM050101Form;
import com.a1stream.domain.form.unit.SDM050102Form;
import com.a1stream.domain.form.unit.SDM050201Form;
import com.a1stream.domain.form.unit.SDM050202Form;
import com.a1stream.domain.form.unit.SDM050601Form;
import com.a1stream.domain.form.unit.SDM050801Form;
import com.a1stream.domain.form.unit.SDQ050101Form;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;

/**
* CmmMstOrganization table customer search function
*
* @author mid1439
*/
public class CmmUnitPromotionListRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmUnitPromotionListRepositoryCustom {

    /**
     * @author Liu Chaoran
     */
    @Override
    public ValueListResultBO findPromotionValueList(BaseVLForm model) {

        int pageSize = model.getPageSize();
        int currentPage = model.getCurrentPage();

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT promotion_cd       AS code        ");
        sql.append("          , promotion_nm       AS name        ");
        sql.append("          , CONCAT(promotion_cd, ' ', promotion_nm)  AS desc  ");
        sql.append("          , promotion_list_id  AS id          ");
        sql.append("          , from_date          AS fromDate    ");
        sql.append("          , to_date            AS toDate      ");
        sql.append("       FROM cmm_unit_promotion_list           ");
        sql.append("      WHERE 1 = 1                             ");

        if (StringUtils.isNotBlank(model.getContent())) {

            sql.append(" AND promotion_retrieve LIKE :content ");
            params.put("content", CommonConstants.CHAR_PERCENT + model.getContent().replaceAll("\\s", "").toUpperCase() + CommonConstants.CHAR_PERCENT);
        }

        sql.append("     ORDER BY promotion_cd               ");

        // pop-up
        String countSql = "SELECT COUNT(1) FROM ( " + sql.toString() + ") AS subquery; ";
        Integer count = super.queryForSingle(countSql, params, Integer.class);

        // pop-over
        if ( pageSize != 0 && currentPage >= 1) {

            sql.append(" OFFSET :offsetValue ");
            sql.append(" LIMIT :limitValue ");

            params.put("offsetValue", pageSize * (currentPage - 1));
            params.put("limitValue", pageSize);
        }

        List<PromotionVLBO> result = super.queryForList(sql.toString(), params, PromotionVLBO.class);

        return new ValueListResultBO(result, count);
    }

    @Override
    public Page<SDQ050101BO> findPromotionMCList(SDQ050101Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT cupl.promotion_cd                   AS promoCd                                                   ");
        sql.append("          , REPLACE(REPLACE(REPLACE(cupl.promotion_nm,CHR(10),''),CHR(9),''),CHR(13),'')      AS promoNm     ");
        sql.append("          , cupl.from_date                      AS effectiveDate                                             ");
        sql.append("          , cupl.to_date                        AS expiredDate                                               ");
        sql.append("          , REPLACE(REPLACE(REPLACE(mf.province_nm ,CHR(10),''),CHR(9),''),CHR(13),'')        AS province    ");
        sql.append("          , csm.site_cd                         AS dealerCd                                                  ");
        sql.append("          , csm.site_nm                         AS dealerNm                                                  ");
        sql.append("          , CONCAT(cupl.gift_nm1, :SEMICOLON, cupl.gift_nm2, :SEMICOLON, cupl.gift_nm3)       AS giftNm      ");
        sql.append("          , mf.facility_cd                      AS pointCd                                                   ");
        sql.append("          , REPLACE(REPLACE(REPLACE(mf.facility_nm,CHR(10),''),CHR(9),''),CHR(13),'')         AS pointNm     ");
        sql.append("          , mp.product_cd                       AS modelCd                                                   ");
        sql.append("          , REPLACE(REPLACE(REPLACE(mp.sales_description ,CHR(10),''),CHR(9),''),CHR(13),'')  AS modelNm     ");
        sql.append("          , cupi.frame_no                       AS frameNo                                                   ");
        sql.append("          , cupi.date_created                   AS uploadDate                                                ");
        sql.append("          , csp.received_date                   AS receivedDate                                              ");
        sql.append("       FROM cmm_unit_promotion_list cupl                                                                     ");
        sql.append("  LEFT JOIN cmm_unit_promotion_item cupi        ON cupi.promotion_list_id = cupl.promotion_list_id           ");
        sql.append("  LEFT JOIN mst_facility mf                     ON cupi.facility_id = mf.facility_id                         ");
        sql.append("  LEFT JOIN mst_product mp                      ON cupi.product_id = mp.product_id                           ");
        sql.append("        AND mp.product_classification = :GOODS                                                               ");
        sql.append("  LEFT JOIN cmm_serialized_product csp          ON csp.serialized_product_id = cupi.cmm_serialized_product_id");
        sql.append("  LEFT JOIN cmm_site_master csm                 ON csm.site_id = cupi.site_id                                ");
        sql.append("      WHERE ((cupl.from_date  >= :fromDate and cupl.from_date <= :toDate )                                   ");
        sql.append("         OR (cupl.to_date >= :fromDate and cupl.to_date <= :toDate ))                                        ");

        if(CommonConstants.CHAR_FALSE.equals(form.getSdOrAccountFlag())) {
            //当前用户不属于666N中的SD和财务
            sql.append("    AND cupl.from_date <= TO_CHAR(CURRENT_DATE, 'YYYYMMDD') ");
        }

        //promotion
        if (!ObjectUtils.isEmpty(form.getPromotionId())) {
            sql.append("    AND cupl.promotion_list_id = :promotionId    ");
            params.put("promotionId", form.getPromotionId());
        }

        //province
        if(!ObjectUtils.isEmpty(form.getProvince())) {
            sql.append("    AND mf.province_id = :provinceId    ");
            params.put("provinceId", form.getProvince());
        }

        //dealer
        if (!ObjectUtils.isEmpty(form.getDealerId())) {
            sql.append("    AND cupi.site_id = :dealerId     ");
            params.put("dealerId", form.getDealerId());
        }

        //point
        if(!ObjectUtils.isEmpty(form.getPointId())) {
            sql.append("    AND cupi.facility_id = :pointId     ");
            params.put("pointId", form.getPointId());
        }

        //model
        if (!ObjectUtils.isEmpty(form.getModelId())) {
            sql.append("    AND cupi.product_id = :modelId     ");
            params.put("modelId", form.getModelId());
        }

        //frameNo
        if (!StringUtils.isEmpty(form.getFrameNo())) {
            sql.append("    AND cupi.frame_no = :frameNO                  ");
            params.put("frameNO", form.getFrameNo());
        }

        sql.append("    ORDER BY cupl.promotion_cd                        ");

        params.put("fromDate", form.getDateFrom());
        params.put("toDate", form.getDateTo());
        params.put("SEMICOLON", CommonConstants.CHAR_SEMICOLON);
        params.put("GOODS", ProductClsType.GOODS.getCodeDbid());

        Integer count = this.getCountPromotionMCList(form);
        Pageable pageable = PageRequest.of(form.getCurrentPage() - 1, form.getPageSize());
        return new PageImpl<>(super.queryForPagingList(sql.toString(), params, SDQ050101BO.class, pageable), pageable, count);
    }

    private Integer getCountPromotionMCList(SDQ050101Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT COUNT(*)                                                                                         ");
        sql.append("       FROM cmm_unit_promotion_list cupl                                                                     ");
        sql.append("  LEFT JOIN cmm_unit_promotion_item cupi        ON cupi.promotion_list_id = cupl.promotion_list_id           ");
        sql.append("  LEFT JOIN mst_facility mf                     ON cupi.facility_id = mf.facility_id                         ");
        sql.append("  LEFT JOIN mst_product mp                      ON cupi.product_id = mp.product_id                           ");
        sql.append("        AND mp.product_classification = :GOODS                                                               ");
        sql.append("  LEFT JOIN cmm_serialized_product csp          ON csp.serialized_product_id = cupi.cmm_serialized_product_id");
        sql.append("  LEFT JOIN cmm_site_master csm                 ON csm.site_id = cupi.site_id                                ");
        sql.append("      WHERE ((cupl.from_date  >= :fromDate and cupl.from_date <= :toDate )                                   ");
        sql.append("         OR (cupl.to_date >= :fromDate and cupl.to_date <= :toDate ))                                        ");

        if(CommonConstants.CHAR_FALSE.equals(form.getSdOrAccountFlag())) {
            //当前用户不属于666N中的SD和财务
            sql.append("    AND cupl.from_date <= TO_CHAR(CURRENT_DATE, 'YYYYMMDD') ");
        }

        //promotion
        if (!ObjectUtils.isEmpty(form.getPromotionId())) {
            sql.append("    AND cupl.promotion_list_id = :promotionId    ");
            params.put("promotionId", form.getPromotionId());
        }

        //province
        if(!ObjectUtils.isEmpty(form.getProvince())) {
            sql.append("    AND mf.province_id = :provinceId    ");
            params.put("provinceId", form.getProvince());
        }

        //dealer
        if (!ObjectUtils.isEmpty(form.getDealerId())) {
            sql.append("    AND cupi.site_id = :dealerId        ");
            params.put("dealerId", form.getDealerId());
        }

        //point
        if(!ObjectUtils.isEmpty(form.getPointId())) {
            sql.append("    AND cupi.facility_id = :pointId     ");
            params.put("pointId", form.getPointId());
        }

        //model
        if (!ObjectUtils.isEmpty(form.getModelId())) {
            sql.append("    AND cupi.product_id = :modelId      ");
            params.put("modelId", form.getModelId());
        }

        //frameNo
        if (!StringUtils.isEmpty(form.getFrameNo())) {
            sql.append("    AND cupi.frame_no = :frameNO                  ");
            params.put("frameNO", form.getFrameNo());
        }

        params.put("fromDate", form.getDateFrom());
        params.put("toDate", form.getDateTo());
        params.put("GOODS", ProductClsType.GOODS.getCodeDbid());

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }

    @Override
    public List<SDQ050101BO> findPromotionMCListExport(SDQ050101Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT cupl.promotion_cd                   AS promoCd                                                   ");
        sql.append("          , REPLACE(REPLACE(REPLACE(cupl.promotion_nm,CHR(10),''),CHR(9),''),CHR(13),'')      AS promoNm     ");
        sql.append("          , cupl.from_date                      AS effectiveDate                                             ");
        sql.append("          , cupl.to_date                        AS expiredDate                                               ");
        sql.append("          , REPLACE(REPLACE(REPLACE(mf.province_nm ,CHR(10),''),CHR(9),''),CHR(13),'')        AS province    ");
        sql.append("          , csm.site_cd                         AS dealerCd                                                  ");
        sql.append("          , csm.site_nm                         AS dealerNm                                                  ");
        sql.append("          , CONCAT(cupl.gift_nm1, :SEMICOLON, cupl.gift_nm2, :SEMICOLON, cupl.gift_nm3)       AS giftNm      ");
        sql.append("          , mf.facility_cd                      AS pointCd                                                   ");
        sql.append("          , REPLACE(REPLACE(REPLACE(mf.facility_nm,CHR(10),''),CHR(9),''),CHR(13),'')         AS pointNm     ");
        sql.append("          , mp.product_cd                       AS modelCd                                                   ");
        sql.append("          , REPLACE(REPLACE(REPLACE(mp.sales_description ,CHR(10),''),CHR(9),''),CHR(13),'')  AS modelNm     ");
        sql.append("          , cupi.frame_no                       AS frameNo                                                   ");
        sql.append("          , cupi.date_created                   AS uploadDate                                                ");
        sql.append("          , csp.received_date                   AS receivedDate                                              ");
        sql.append("       FROM cmm_unit_promotion_list cupl                                                                     ");
        sql.append("  LEFT JOIN cmm_unit_promotion_item cupi        ON cupi.promotion_list_id = cupl.promotion_list_id           ");
        sql.append("  LEFT JOIN mst_facility mf                     ON cupi.facility_id = mf.facility_id                         ");
        sql.append("  LEFT JOIN mst_product mp                      ON cupi.product_id = mp.product_id                           ");
        sql.append("        AND mp.product_classification = :GOODS                                                               ");
        sql.append("  LEFT JOIN cmm_serialized_product csp          ON csp.serialized_product_id = cupi.cmm_serialized_product_id");
        sql.append("  LEFT JOIN cmm_site_master csm                 ON csm.site_id = cupi.site_id                                ");
        sql.append("      WHERE ((cupl.from_date  >= :fromDate and cupl.from_date <= :toDate )                                   ");
        sql.append("         OR (cupl.to_date >= :fromDate and cupl.to_date <= :toDate ))                                        ");

        if(CommonConstants.CHAR_FALSE.equals(form.getSdOrAccountFlag())) {
            //当前用户不属于666N中的SD和财务
            sql.append("    AND cupl.from_date <= TO_CHAR(CURRENT_DATE, 'YYYYMMDD') ");
        }

        //promotion
        if (!ObjectUtils.isEmpty(form.getPromotionId())) {
            sql.append("    AND cupl.promotion_list_id = :promotionId    ");
            params.put("promotionId", form.getPromotionId());
        }

        //province
        if(!ObjectUtils.isEmpty(form.getProvince())) {
            sql.append("    AND mf.province_id = :provinceId    ");
            params.put("provinceId", form.getProvince());
        }

        //dealer
        if (!ObjectUtils.isEmpty(form.getDealerId())) {
            sql.append("    AND cupi.site_id = :dealerId     ");
            params.put("dealerId", form.getDealerId());
        }

        //point
        if(!ObjectUtils.isEmpty(form.getPointId())) {
            sql.append("    AND cupi.facility_id = :pointId     ");
            params.put("pointId", form.getPointId());
        }

        //model
        if (!ObjectUtils.isEmpty(form.getModelId())) {
            sql.append("    AND cupi.product_id = :modelId     ");
            params.put("modelId", form.getModelId());
        }

        //frameNo
        if (!StringUtils.isEmpty(form.getFrameNo())) {
            sql.append("    AND cupi.frame_no = :frameNO                  ");
            params.put("frameNO", form.getFrameNo());
        }

        sql.append("    ORDER BY cupl.promotion_cd                        ");

        params.put("fromDate", form.getDateFrom());
        params.put("toDate", form.getDateTo());
        params.put("SEMICOLON", CommonConstants.CHAR_SEMICOLON);
        params.put("GOODS", ProductClsType.GOODS.getCodeDbid());

        return super.queryForList(sql.toString(), params, SDQ050101BO.class);
    }

    @Override
    public List<SDM050101BO> findSetUpPromotionList(SDM050101Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT cupl.promotion_cd       AS promoCd                              ");
        sql.append("          , cupl.promotion_nm       AS promoNm                              ");
        sql.append("          , cupl.promotion_list_id  AS promotionListId                      ");
        sql.append("          , CONCAT(cupl.gift_nm1, :SEMICOLON, cupl.gift_nm2, :SEMICOLON, cupl.gift_nm3)  AS giftNm   ");
        sql.append("          , cupm.product_id         AS modelId                              ");
        sql.append("          , cupm.product_cd         AS modelCd                              ");
        sql.append("          , cupm.product_nm         AS modelNm                              ");
        sql.append("          , cupl.invoice            AS invoiceFlag                          ");
        sql.append("          , cupl.registration_card  AS regCardFlag                          ");
        sql.append("          , cupl.gift_receipt       AS giftReceiptFlag                      ");
        sql.append("          , cupl.lucky_draw_voucher AS luckyDrawFlag                        ");
        sql.append("          , cupl.id_card            AS idCardFlag                           ");
        sql.append("          , cupl.others_flag        AS otherFlag                            ");
        sql.append("          , cupl.from_date          AS fromDate                             ");
        sql.append("          , cupl.to_date            AS toDate                               ");
        sql.append("       FROM cmm_unit_promotion_list cupl                                    ");
        sql.append("  LEFT JOIN cmm_unit_promotion_model cupm                                   ");
        sql.append("         ON cupl.promotion_list_id = cupm.promotion_list_id                 ");
        sql.append("      WHERE ((cupl.from_date  >= :fromDate AND cupl.from_date <= :toDate )  ");
        sql.append("         OR (cupl.to_date >= :fromDate AND cupl.to_date <= :toDate ))       ");

        if(CommonConstants.CHAR_FALSE.equals(form.getSdOrAccountFlag())) {
            //当前用户不属于666N中的SD和财务
            sql.append("    AND cupl.from_date <= TO_CHAR(CURRENT_DATE, 'YYYYMMDD') ");
        }

        //promotion
        if (!ObjectUtils.isEmpty(form.getPromotionId())) {
            sql.append("    AND cupl.promotion_list_id = :promotionId    ");
            params.put("promotionId", form.getPromotionId());
        }

        //model
        if (!ObjectUtils.isEmpty(form.getModelId())) {
            sql.append("    AND cupm.product_id = :modelId               ");
            params.put("modelId", form.getModelId());
        }

        //giftName
        if (!StringUtils.isEmpty(form.getGiftNm())) {
            sql.append("    AND (cupl.gift_nm1 LIKE :giftNm OR cupl.gift_nm2 LIKE :giftNm OR cupl.gift_nm3 LIKE :giftNm)      ");
            params.put("giftNm",CommonConstants.CHAR_PERCENT + form.getGiftNm() + CommonConstants.CHAR_PERCENT);
        }

        sql.append("    ORDER BY cupl.promotion_cd                        ");

        params.put("fromDate", form.getDateFrom());
        params.put("toDate", form.getDateTo());
        params.put("SEMICOLON", CommonConstants.CHAR_SEMICOLON);

        return super.queryForList(sql.toString(), params, SDM050101BO.class);
    }

    @Override
    public List<SDM050101BO> findSetUpPromotionListExport(SDM050101Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT cupl.promotion_cd                   AS promoCd                                                   ");
        sql.append("          , REPLACE(REPLACE(REPLACE(cupl.promotion_nm,CHR(10),''),CHR(9),''),CHR(13),'')      AS promoNm     ");
        sql.append("          , cupl.from_date                      AS fromDate                                                  ");
        sql.append("          , cupl.to_date                        AS toDate                                                    ");
        sql.append("          , REPLACE(REPLACE(REPLACE(mf.province_nm ,CHR(10),''),CHR(9),''),CHR(13),'')        AS province    ");
        sql.append("          , csm.site_cd                         AS dealerCd                                                  ");
        sql.append("          , csm.site_nm                         AS dealerNm                                                  ");
        sql.append("          , CONCAT(cupl.gift_nm1, :SEMICOLON, cupl.gift_nm2, :SEMICOLON, cupl.gift_nm3)       AS giftNm      ");
        sql.append("          , mf.facility_cd                      AS pointCd                                                   ");
        sql.append("          , REPLACE(REPLACE(REPLACE(mf.facility_nm,CHR(10),''),CHR(9),''),CHR(13),'')         AS pointNm     ");
        sql.append("          , cupm.product_cd                     AS modelCd                                                   ");
        sql.append("          , REPLACE(REPLACE(REPLACE(cupm.product_nm ,CHR(10),''),CHR(9),''),CHR(13),'')       AS modelNm     ");
        sql.append("          , cupi.frame_no                       AS frameNo                                                   ");
        sql.append("       FROM cmm_unit_promotion_list cupl                                                                     ");
        sql.append("  LEFT JOIN cmm_unit_promotion_item cupi                                                                     ");
        sql.append("         ON cupi.promotion_list_id = cupl.promotion_list_id                                                  ");
        sql.append("  LEFT JOIN mst_facility mf                                                                                  ");
        sql.append("         ON cupi.facility_id = mf.facility_id                                                                ");
        sql.append("  LEFT JOIN cmm_unit_promotion_model cupm                                                                    ");
        sql.append("         ON cupl.promotion_list_id = cupm.promotion_list_id                                                  ");
        sql.append("  LEFT JOIN cmm_site_master csm                                                                              ");
        sql.append("         ON csm.site_id = cupi.site_id                                                                       ");
        sql.append("      WHERE ((cupl.from_date  >= :fromDate and cupl.from_date <= :toDate )                                   ");
        sql.append("         OR (cupl.to_date >= :fromDate and cupl.to_date <= :toDate ))                                        ");

        if(CommonConstants.CHAR_FALSE.equals(form.getSdOrAccountFlag())) {
            //当前用户不属于666N中的SD和财务
            sql.append("    AND cupl.from_date <= TO_CHAR(CURRENT_DATE, 'YYYYMMDD') ");
        }

        //promotion
        if (!ObjectUtils.isEmpty(form.getPromotionId())) {
            sql.append("    AND cupl.promotion_list_id = :promotionId    ");
            params.put("promotionId", form.getPromotionId());
        }

        //model
        if (!ObjectUtils.isEmpty(form.getModelId())) {
            sql.append("    AND cupm.product_id = :modelId               ");
            params.put("modelId", form.getModelId());
        }

        //giftName
        if (!StringUtils.isEmpty(form.getGiftNm())) {
            sql.append("    AND (cupl.gift_nm1 LIKE :giftNm OR cupl.gift_nm2 LIKE :giftNm OR cupl.gift_nm3 LIKE :giftNm)      ");
            params.put("giftNm",CommonConstants.CHAR_PERCENT + form.getGiftNm() + CommonConstants.CHAR_PERCENT);
        }

        sql.append("    ORDER BY cupl.promotion_cd                        ");

        params.put("fromDate", form.getDateFrom());
        params.put("toDate", form.getDateTo());
        params.put("SEMICOLON", CommonConstants.CHAR_SEMICOLON);

        return super.queryForList(sql.toString(), params, SDM050101BO.class);
    }

    @Override
    public List<SDM050102BO> findPromotionMC(SDM050102Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT cupl.promotion_cd                   AS promoCd                                                   ");
        sql.append("          , REPLACE(REPLACE(REPLACE(cupl.promotion_nm,CHR(10),''),CHR(9),''),CHR(13),'')      AS promoNm     ");
        sql.append("          , cupl.from_date                      AS fromDate                                                  ");
        sql.append("          , cupl.to_date                        AS toDate                                                    ");
        sql.append("          , REPLACE(REPLACE(REPLACE(mf.province_nm ,CHR(10),''),CHR(9),''),CHR(13),'')        AS province    ");
        sql.append("          , csm.site_cd                         AS dealerCd                                                  ");
        sql.append("          , csm.site_nm                         AS dealerNm                                                  ");
        sql.append("          , CONCAT(cupl.gift_nm1, :SEMICOLON, cupl.gift_nm2, :SEMICOLON, cupl.gift_nm3)       AS giftNm      ");
        sql.append("          , mf.facility_cd                      AS pointCd                                                   ");
        sql.append("          , REPLACE(REPLACE(REPLACE(mf.facility_nm,CHR(10),''),CHR(9),''),CHR(13),'')         AS pointNm     ");
        sql.append("          , mp.product_cd                       AS modelCd                                                   ");
        sql.append("          , REPLACE(REPLACE(REPLACE(mp.sales_description ,CHR(10),''),CHR(9),''),CHR(13),'')  AS modelNm     ");
        sql.append("          , cupi.frame_no                       AS frameNo                                                   ");
        sql.append("       FROM cmm_unit_promotion_list cupl                                                                     ");
        sql.append("  LEFT JOIN cmm_unit_promotion_item cupi        ON cupi.promotion_list_id = cupl.promotion_list_id           ");
        sql.append("  LEFT JOIN mst_facility mf                     ON cupi.facility_id = mf.facility_id                         ");
        sql.append("  LEFT JOIN mst_product mp                      ON cupi.product_id = mp.product_id                           ");
        sql.append("        AND mp.product_classification = :GOODS                                                               ");
        sql.append("  LEFT JOIN cmm_site_master csm                 ON csm.site_id = cupi.site_id                                ");
        sql.append("      WHERE cupl.promotion_list_id = :promotionId                                                            ");

        if(CommonConstants.CHAR_FALSE.equals(form.getSdOrAccountFlag())) {
            //当前用户不属于666N中的SD和财务
            sql.append("    AND cupl.from_date <= TO_CHAR(CURRENT_DATE, 'YYYYMMDD') ");
        }

        //province
        if(!ObjectUtils.isEmpty(form.getProvince())) {
            sql.append("    AND mf.province_id = :provinceId    ");
            params.put("provinceId", form.getProvince());
        }

        //dealer
        if (!ObjectUtils.isEmpty(form.getDealerId())) {
            sql.append("    AND cupi.site_id = :dealerId     ");
            params.put("dealerId", form.getDealerId());
        }

        //point
        if(!ObjectUtils.isEmpty(form.getPointId())) {
            sql.append("    AND cupi.facility_id = :pointId     ");
            params.put("pointId", form.getPointId());
        }

        //model
        if (!ObjectUtils.isEmpty(form.getModelId())) {
            sql.append("    AND cupi.product_id = :modelId     ");
            params.put("modelId", form.getModelId());
        }

        //frameNo
        if (!StringUtils.isEmpty(form.getFrameNo())) {
            sql.append("    AND cupi.frame_no = :frameNO                  ");
            params.put("frameNO", form.getFrameNo());
        }

        sql.append("    ORDER BY cupl.promotion_cd                        ");

        params.put("promotionId", form.getPromotionListId());
        params.put("SEMICOLON", CommonConstants.CHAR_SEMICOLON);
        params.put("GOODS", ProductClsType.GOODS.getCodeDbid());

        return super.queryForList(sql.toString(), params, SDM050102BO.class);
    }

    @Override
    public List<SDM050601BO> getUpdPeriodMaintList(SDM050601Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT cupl.promotion_cd    AS promotionCode            ");
        sql.append("         , REPLACE(                                         ");
        sql.append("               REPLACE(                                     ");
        sql.append("                   REPLACE(cupl.promotion_nm, CHR(10), ''), ");
        sql.append("                   CHR(9), ''                               ");
        sql.append("                ),                                          ");
        sql.append("            CHR(13), ''                                     ");
        sql.append("            ) AS promotionName                              ");
        sql.append("         , cupl.from_date       AS effectiveDate            ");
        sql.append("         , cupl.to_date         AS expiredDate              ");
        sql.append("         , cupl.upload_end_date AS uploadEndDate            ");
        sql.append("         , cupl.promotion_list_id AS promotionListId        ");
        sql.append("      FROM cmm_unit_promotion_list cupl                     ");
        sql.append(" LEFT JOIN cmm_unit_promotion_item cupi                     ");
        sql.append("        ON cupi.promotion_list_id = cupl.promotion_list_id  ");
        sql.append("    WHERE cupi.site_id = :siteId                            ");
        sql.append("      AND cupi.facility_id = :facilityId                    ");

        params.put("siteId", form.getSiteId());
        params.put("facilityId", form.getDefaultPointId());

        if (!Nulls.isNull(form.getPromotionId())) {
            sql.append(" AND cupl.promotion_list_id = :promotionListId ");
            params.put("promotionListId", form.getPromotionId());
        }

        //若当前用户属于666N中的SD或财务
        if (Boolean.TRUE.equals(form.getUserFlag())) {

            if (StringUtils.isNotBlank(form.getDateFrom()) && StringUtils.isNotBlank(form.getDateTo())) {

                sql.append(" AND ((cupl.from_date >= :fromDate AND cupl.from_date <= :toDate ) ");
                sql.append("  OR (cupl.to_date >= :fromDate AND cupl.to_date <= :toDate))      ");
                params.put("fromDate", form.getDateFrom());
                params.put("toDate", form.getDateTo());
            }

        } else {

            sql.append(" AND cupl.from_date <= :sysDate ");
            params.put("sysDate", LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)));
        }

        sql.append(" ORDER BY cupl.promotion_cd ASC ");
        return super.queryForList(sql.toString(), params, SDM050601BO.class);
    }

    @Override
    public List<SDM050201BO> findSetUpPromotionTerms(SDM050201Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mp.product_id                       AS modelId                                                   ");
        sql.append("          , mp.product_cd                       AS modelCd                                                   ");
        sql.append("          , REPLACE(REPLACE(REPLACE(mp.sales_description ,CHR(10),''),CHR(9),''),CHR(13),'') AS modelNm      ");
        sql.append("          , CASE WHEN cupi.promotion_list_id IS NOT NULL THEN :Y ELSE :N END AS deleteFlag                   ");
        sql.append("       FROM cmm_unit_promotion_list cupl                                                                     ");
        sql.append("  LEFT JOIN cmm_unit_promotion_model cupm       ON cupm.promotion_list_id = cupl.promotion_list_id           ");
        sql.append("  LEFT JOIN mst_product mp                      ON cupm.product_id = mp.product_id                           ");
        sql.append("        AND mp.product_classification = :GOODS                                                               ");
        sql.append("  LEFT JOIN (SELECT DISTINCT promotion_list_id, product_id                                                   ");
        sql.append("       FROM cmm_unit_promotion_item WHERE promotion_list_id = :promotionId) cupi                             ");
        sql.append("         ON cupi.promotion_list_id = cupl.promotion_list_id AND cupi.product_id = mp.product_id              ");
        sql.append("      WHERE cupl.promotion_list_id = :promotionId                                                            ");
        sql.append("      ORDER BY mp.product_cd                        ");
        params.put("promotionId", form.getPromotionListId());
        params.put("GOODS", ProductClsType.GOODS.getCodeDbid());
        params.put("Y", CommonConstants.CHAR_Y);
        params.put("N", CommonConstants.CHAR_N);

        return super.queryForList(sql.toString(), params, SDM050201BO.class);
    }

    @Override
    public List<SDM050202BO> findUploadPromoQC(SDM050202Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT cupl.promotion_cd                   AS promoCd                                                   ");
        sql.append("          , REPLACE(REPLACE(REPLACE(cupl.promotion_nm,CHR(10),''),CHR(9),''),CHR(13),'')      AS promoNm     ");
        sql.append("          , csm.site_id                         AS dealerId                                                  ");
        sql.append("          , csm.site_cd                         AS dealerCd                                                  ");
        sql.append("          , csm.site_nm                         AS dealerNm                                                  ");
        sql.append("          , cupl.from_date                      AS fromDate                                                  ");
        sql.append("          , cupl.to_date                        AS toDate                                                    ");
        sql.append("          , mf.facility_id                      AS pointId                                                   ");
        sql.append("          , mf.facility_cd                      AS pointCd                                                   ");
        sql.append("          , REPLACE(REPLACE(REPLACE(mf.facility_nm,CHR(10),''),CHR(9),''),CHR(13),'')         AS pointNm     ");
        sql.append("          , mp.product_id                       AS modelId                                                   ");
        sql.append("          , mp.product_cd                       AS modelCd                                                   ");
        sql.append("          , REPLACE(REPLACE(REPLACE(mp.sales_description ,CHR(10),''),CHR(9),''),CHR(13),'')  AS modelNm     ");
        sql.append("          , cupi.frame_no                       AS frameNo                                                   ");
        sql.append("          , cupi.stock_mc_flag                  AS stockMcFlag                                               ");
        sql.append("       FROM cmm_unit_promotion_list cupl                                                                     ");
        sql.append(" INNER JOIN cmm_unit_promotion_item cupi        ON cupi.promotion_list_id = cupl.promotion_list_id           ");
        sql.append("  LEFT JOIN mst_facility mf                     ON cupi.facility_id = mf.facility_id                         ");
        sql.append("  LEFT JOIN mst_product mp                      ON cupi.product_id = mp.product_id                           ");
        sql.append("        AND mp.product_classification = :GOODS                                                               ");
        sql.append("  LEFT JOIN cmm_site_master csm                 ON csm.site_id = cupi.site_id                                ");
        sql.append("      WHERE cupl.promotion_list_id = :promotionId                                                            ");

        sql.append("    ORDER BY cupl.promotion_cd                        ");

        params.put("promotionId", form.getPromotionListId());
        params.put("GOODS", ProductClsType.GOODS.getCodeDbid());

        return super.queryForList(sql.toString(), params, SDM050202BO.class);
    }

    @Override
    public SDM050202HeaderBO findUploadPromoQCHeader(SDM050202Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT cupl.promotion_cd                   AS promoCd                                                   ");
        sql.append("          , REPLACE(REPLACE(REPLACE(cupl.promotion_nm,CHR(10),''),CHR(9),''),CHR(13),'')      AS promoNm     ");
        sql.append("          , cupl.from_date                      AS fromDate                                                  ");
        sql.append("          , cupl.to_date                        AS toDate                                                    ");
        sql.append("       FROM cmm_unit_promotion_list cupl                                                                     ");
        sql.append("      WHERE cupl.promotion_list_id = :promotionId                                                            ");

        sql.append("    ORDER BY cupl.promotion_cd                        ");

        params.put("promotionId", form.getPromotionListId());

        return super.queryForSingle(sql.toString(), params, SDM050202HeaderBO.class);
    }

    @Override
    public SDM050801BO getActivePromotionList(SDM050801Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT promotion_list_id AS promotionlistId  ");
        sql.append("      FROM cmm_unit_promotion_list               ");
        sql.append("     WHERE promotion_list_id = :promotionListId  ");
        sql.append("       AND from_date <= :sysDate                 ");
        sql.append("       AND to_date >= :sysDate                   ");

        params.put("promotionListId", form.getPromotionId());
        params.put("sysDate", LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)));
        return super.queryForSingle(sql.toString(), params, SDM050801BO.class);
    }

    @Override
    public List<SDM010601BO> getEffectivePromotionInfoList(String sysDate) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT cupl.promotion_list_id AS promotionListId         ");
        sql.append("          , cupm.product_id        AS promotionModelId        ");
        sql.append("       FROM cmm_unit_promotion_list cupl                      ");
        sql.append(" INNER JOIN cmm_unit_promotion_model cupm                     ");
        sql.append("         ON cupl.promotion_list_id = cupm.promotion_list_id   ");
        sql.append("        AND cupl.from_date <= :sysDate                        ");
        sql.append("        AND cupl.to_date >=  :sysDate                         ");

        params.put("sysDate", sysDate);
        return super.queryForList(sql.toString(), params, SDM010601BO.class);
    }

}
