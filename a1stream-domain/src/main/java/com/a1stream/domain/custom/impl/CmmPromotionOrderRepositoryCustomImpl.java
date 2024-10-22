package com.a1stream.domain.custom.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import com.a1stream.common.constants.MstCodeConstants.JudgementStatus;
import com.a1stream.domain.bo.unit.SDM050301BO;
import com.a1stream.domain.bo.unit.SDM050401BO;
import com.a1stream.domain.bo.unit.SDM050501BO;
import com.a1stream.domain.bo.unit.SDM050701BO;
import com.a1stream.domain.bo.unit.SDM050801BO;
import com.a1stream.domain.custom.CmmPromotionOrderRepositoryCustom;
import com.a1stream.domain.form.unit.SDM050301Form;
import com.a1stream.domain.form.unit.SDM050401Form;
import com.a1stream.domain.form.unit.SDM050501Form;
import com.a1stream.domain.form.unit.SDM050701Form;
import com.a1stream.domain.form.unit.SDM050801Form;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;


public class CmmPromotionOrderRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmPromotionOrderRepositoryCustom {

    @Override
    public Page<SDM050701BO> getWaitingScreenList(SDM050701Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder selSql = new StringBuilder();
        StringBuilder sql = new StringBuilder();

        selSql.append("  SELECT cupl.promotion_cd                                                                      AS promotionCode       ");
        selSql.append("       , REPLACE(REPLACE(REPLACE(cupl.promotion_nm, CHR(10), ''), CHR(9), ''), CHR(13), '')     AS promotionName       ");
        selSql.append("       , cpo.site_id                                                                            AS dealerCode          ");
        selSql.append("       , REPLACE(REPLACE(REPLACE(csm.site_nm, CHR(10), ''), CHR(9), ''), CHR(13), '')           AS dealerName          ");
        selSql.append("       , mp.product_cd                                                                          AS modelCode           ");
        selSql.append("       , REPLACE(REPLACE(REPLACE(mp.sales_description, CHR(10), ''), CHR(9), ''), CHR(13), '')  AS modelName           ");
        selSql.append("       , cpo.frame_no                                                                           AS frameNo             ");
        selSql.append("       , cpo.promotion_list_id                                                                  AS promotionListId     ");
        selSql.append("       , cpo.can_enjoy_promotion                                                                AS canEnjoyFlag        ");
        selSql.append("       , cpo.promotion_order_id                                                                 AS promotionOrderId    ");
        sql.append("       FROM cmm_promotion_order cpo                                                                                       ");
        sql.append("  LEFT JOIN cmm_unit_promotion_list cupl                                                                                  ");
        sql.append("         ON cpo.promotion_list_id = cupl.promotion_list_id                                                                ");
        sql.append("  LEFT JOIN cmm_site_master csm                                                                                           ");
        sql.append("         ON csm.site_id = cpo.site_id                                                                                     ");
        sql.append("  LEFT JOIN mst_product mp                                                                                                ");
        sql.append("         ON cpo.cmm_product_id = mp.product_id                                                                            ");
        sql.append("      WHERE 1 = 1                                                                                                         ");

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

        if (!Nulls.isNull(form.getPromotionId())) {
            sql.append(" AND cpo.promotion_list_id = :promotionListId ");
            params.put("promotionListId", form.getPromotionId());
        }

        if (!Nulls.isNull(form.getModelId())) {
            sql.append(" AND cpo.cmm_product_id = :modelId ");
            params.put("modelId", form.getModelId());
        }

        if (StringUtils.isNotBlank(form.getDealerId())) {
            sql.append(" AND cpo.site_id = :dealerId ");
            params.put("dealerId", form.getDealerId());
        }

        if (StringUtils.isNotBlank(form.getFrameNo())) {
            sql.append(" AND cpo.frame_no = :frameNo ");
            params.put("frameNo", form.getFrameNo());
        }

        sql.append(" ORDER BY cpo.site_id ASC ");
        
        String countSql = "SELECT COUNT(*) FROM ( " + "SELECT 1 " + sql.toString() + ") AS subquery; ";
        Pageable pageable = PageRequest.of(form.getCurrentPage() - 1, form.getPageSize());
        if (!Boolean.TRUE.equals(form.getPageFlg())) {
            pageable = Pageable.unpaged();
        }
        Integer count = super.queryForSingle(countSql, params, Integer.class);
        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, SDM050701BO.class, pageable), pageable, count);
    }

    @Override
    public List<SDM050801BO> getCpoPromoRecList(SDM050801Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT cupl.promotion_cd            AS promotionCd          ");
        sql.append("          , cpo.local_order_no           AS orderNo              ");
        sql.append("          , cpo.order_date               AS orderDate            ");
        sql.append("          , cpo.frame_no                 AS frameNo              ");
        sql.append("          , cpo.customer_nm              AS customerNm           ");
        sql.append("          , cpo.local_invoice_no         AS invoiceNo            ");
        sql.append("          , cpo.local_delivery_order_no  AS deliveryNo           ");
        sql.append("          , 'Y'                          AS checkFlg             ");
        sql.append("       FROM cmm_promotion_order cpo                              ");
        sql.append("  LEFT JOIN cmm_unit_promotion_list cupl                         ");
        sql.append("         ON cupl.promotion_list_id      = cpo.promotion_list_id  ");
        sql.append("      WHERE cpo.promotion_list_id       = :promotionListId       ");
        sql.append("        AND cpo.frame_no                = :frameNo               ");
        sql.append("        AND cpo.facility_id             = :facilityId            ");

        params.put("promotionListId", form.getPromotionId());
        params.put("frameNo", form.getFrameNo());
        params.put("facilityId", form.getPointId());
        return super.queryForList(sql.toString(), params, SDM050801BO.class);
    }

    @Override
    public Page<SDM050301BO> getPromotionResult(SDM050301Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        StringBuilder selSql = new StringBuilder();

        selSql.append("     SELECT promotion_order_id        AS promotionOrderId       ");
        selSql.append("          , product_cd                AS modelCode              ");
        selSql.append("          , product_nm                AS modelName              ");
        selSql.append("          , frame_no                  AS frameNo                ");
        selSql.append("          , local_order_no            AS orderNo                ");
        selSql.append("          , local_delivery_order_no   AS duNo                   ");
        selSql.append("          , invoice_memo              AS invoiceNo              ");
        selSql.append("          , customer_nm               AS customer               ");
        selSql.append("          , sales_method              AS salesMethod            ");
        selSql.append("          , sales_pic                 AS salesPic               ");
        selSql.append("          , invoice_upload_path       AS invoiceUploadPath      ");
        selSql.append("          , registration_card_path    AS registrationCardPath   ");
        selSql.append("          , gift_receipt_upload_path1 AS giftReceiptUploadPath1 ");
        selSql.append("          , gift_receipt_upload_path2 AS giftReceiptUploadPath2 ");
        selSql.append("          , gift_receipt_upload_path3 AS giftReceiptUploadPath3 ");
        selSql.append("          , lucky_draw_voucher_path   AS luckyDrawVoucherPath   ");
        selSql.append("          , id_card_path              AS idCardPath             ");
        selSql.append("          , others_path1              AS othersPath1            ");
        selSql.append("          , others_path2              AS othersPath2            ");
        selSql.append("          , others_path3              AS othersPath3            ");
        selSql.append("          , jugement_status           AS judgement              ");
        selSql.append("          , reject_reason             AS rejectReason           ");
        selSql.append("          , promotion_cd              AS promotionCode          ");
        selSql.append("          , promotion_nm              AS promotionName          ");
        selSql.append("          , cpo.promotion_list_id     AS promotionListId        ");
        sql.append("          FROM cmm_promotion_order cpo                             ");
        sql.append("     LEFT JOIN cmm_unit_promotion_list cupl                        ");
        sql.append("            ON cpo.promotion_list_id = cupl.promotion_list_id      ");
        sql.append("         WHERE cpo.site_id = :siteId                               ");
        sql.append("           AND cupl.from_date <= :periodDateTo                     ");
        sql.append("           AND cupl.to_date >= :periodDateFrom                     ");
        sql.append("           AND sales_method IN (:salesMethod)                      ");
        sql.append("           AND can_enjoy_promotion = :canEnjoyPromotion            ");
        
        
        if (StringUtils.isNotBlank(form.getSalesShipmentDateFrom())) {
            sql.append(" AND order_date >= :salesShipmentDateFrom ");
            params.put("salesShipmentDateFrom", form.getSalesShipmentDateFrom());
        }

        if (StringUtils.isNotBlank(form.getSalesShipmentDateTo())) {
            sql.append(" AND order_date <= :salesShipmentDateTo ");
            params.put("salesShipmentDateTo", form.getSalesShipmentDateTo());
        }

        if (!ObjectUtils.isEmpty(form.getPromotionListId())) {
            sql.append(" AND cupl.promotion_list_id = :promotionListId ");
            params.put("promotionListId", form.getPromotionListId());
        }

        if (!ObjectUtils.isEmpty(form.getPointId())) {
            sql.append(" AND facility_id = :facilityId ");
            params.put("facilityId", form.getPointId());
        }

        if (!ObjectUtils.isEmpty(form.getModelId())) {
            sql.append(" AND cmm_product_id = :cmmProductId ");
            params.put("cmmProductId", form.getModelId());
        }

        if (StringUtils.isNotBlank(form.getInvoiceNo())) {
            sql.append(" AND invoice_memo LIKE :invoiceMemo ");
            params.put("invoiceMemo", form.getInvoiceNo());
        }

        if (StringUtils.isNotBlank(form.getDuNo())) {
            sql.append(" AND local_delivery_order_no LIKE :localDeliveryOrderNo ");
            params.put("localDeliveryOrderNo", form.getDuNo());
        }

        if (StringUtils.isNotBlank(form.getFrameNo())) {
            sql.append(" AND frame_no LIKE :frameNo ");
            params.put("frameNo", form.getFrameNo());
        }

        if (StringUtils.isNotBlank(form.getStatus())) {
            if (JudgementStatus.WAITINGYMVNCHECKING.equals(form.getStatus())) {
                sql.append(" AND jugement_status IN (:jugementStatus) ");
                sql.append(" AND jugement_status <> :REJECTBYACCOUNT ");
                List<String> jugementStatus = new ArrayList<>();
                jugementStatus.add(JudgementStatus.WAITINGYMVNCHECKING);
                jugementStatus.add(JudgementStatus.APPROVEDBYSD);
                params.put("jugementStatus", jugementStatus);
                params.put("REJECTBYACCOUNT", JudgementStatus.REJECTBYACCOUNT);
            } else {
                sql.append(" AND jugement_status = :jugementStatus ");
                sql.append(" AND jugement_status <> :REJECTBYACCOUNT ");
                params.put("jugementStatus", form.getStatus());
                params.put("REJECTBYACCOUNT", JudgementStatus.REJECTBYACCOUNT);
            }
        }

        if (StringUtils.isNotBlank(form.getCustomer())) {
            sql.append(" AND customer_nm LIKE :customerNm ");
            params.put("customerNm", form.getCustomer());
        }

        params.put("siteId", form.getSiteId());
        params.put("periodDateTo", form.getPeriodDateTo());
        params.put("periodDateFrom", form.getPeriodDateFrom());
        params.put("salesMethod", form.getSalesMethod());
        params.put("canEnjoyPromotion", CommonConstants.CHAR_Y);

        sql.append(" ORDER BY local_order_no ");

        String countSql = "SELECT COUNT(1) FROM ( " + "SELECT 1 " + sql.toString() + ") AS subquery; ";
        Pageable pageable = PageRequest.of(form.getCurrentPage() - 1, form.getPageSize());
        if (!form.isPageFlg()) {
            pageable = Pageable.unpaged();
        }
        Integer count = super.queryForSingle(countSql, params, Integer.class);

        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, SDM050301BO.class, pageable), pageable, count);
    }

    @Override
    public Page<SDM050401BO> getPromotionJudgement(SDM050401Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        StringBuilder selSql = new StringBuilder();

        selSql.append("     SELECT promotion_order_id        AS promotionOrderId       ");
        selSql.append("          , product_cd                AS modelCode              ");
        selSql.append("          , product_nm                AS modelName              ");
        selSql.append("          , frame_no                  AS frameNo                ");
        selSql.append("          , local_order_no            AS orderNo                ");
        selSql.append("          , local_delivery_order_no   AS duNo                   ");
        selSql.append("          , invoice_memo              AS invoiceNo              ");
        selSql.append("          , customer_nm               AS customer               ");
        selSql.append("          , sales_method              AS salesMethod            ");
        selSql.append("          , sales_pic                 AS salesPic               ");
        selSql.append("          , invoice_upload_path       AS invoiceUploadPath      ");
        selSql.append("          , registration_card_path    AS registrationCardPath   ");
        selSql.append("          , gift_receipt_upload_path1 AS giftReceiptUploadPath1 ");
        selSql.append("          , gift_receipt_upload_path2 AS giftReceiptUploadPath2 ");
        selSql.append("          , gift_receipt_upload_path3 AS giftReceiptUploadPath3 ");
        selSql.append("          , lucky_draw_voucher_path   AS luckyDrawVoucherPath   ");
        selSql.append("          , id_card_path              AS idCardPath             ");
        selSql.append("          , others_path1              AS othersPath1            ");
        selSql.append("          , others_path2              AS othersPath2            ");
        selSql.append("          , others_path3              AS othersPath3            ");
        selSql.append("          , jugement_status           AS judgement              ");
        selSql.append("          , reject_reason             AS rejectReason           ");
        selSql.append("          , promotion_cd              AS promotionCode          ");
        selSql.append("          , promotion_nm              AS promotionName          ");
        selSql.append("          , cpo.promotion_list_id     AS promotionListId        ");
        sql.append("          FROM cmm_promotion_order cpo                             ");
        sql.append("     LEFT JOIN cmm_unit_promotion_list cupl                        ");
        sql.append("            ON cpo.promotion_list_id = cupl.promotion_list_id      ");
        sql.append("     LEFT JOIN mst_facility mf                                     ");
        sql.append("            ON cpo.facility_id = mf.facility_id                    ");
        sql.append("         WHERE cupl.promotion_list_id = :promotionListId           ");
        sql.append("           AND jugement_status = :jugementStatus                   ");

        if (!ObjectUtils.isEmpty(form.getProvinceId())) {
            sql.append(" AND mf.province_id = :provinceId ");
            params.put("provinceId", form.getProvinceId());
        }

        if (StringUtils.isNotBlank(form.getDealerCd())) {
            sql.append(" AND cpo.site_id = :siteId ");
            params.put("siteId", form.getDealerCd());
        }

        if (StringUtils.isNotBlank(form.getFrameNo())) {
            sql.append(" AND frame_no LIKE :frameNo ");
            params.put("frameNo", form.getFrameNo());
        }

        if (StringUtils.isNotBlank(form.getCustomer())) {
            sql.append(" AND customer_nm LIKE :customerNm ");
            String customer = CommonConstants.CHAR_YT00_SITE_ID.equals(form.getCustomer()) ? CommonConstants.CHAR_BLANK : form.getCustomer();
            params.put("customerNm", customer);
        }

        params.put("promotionListId", form.getPromotionListId());
        params.put("jugementStatus", form.getStatus());

        sql.append(" ORDER BY local_order_no ");

        String countSql = "SELECT COUNT(1) FROM ( " + "SELECT 1 " + sql.toString() + ") AS subquery; ";
        Pageable pageable = PageRequest.of(form.getCurrentPage() - 1, form.getPageSize());
        if (!form.isPageFlg()) {
            pageable = Pageable.unpaged();
        }
        Integer count = super.queryForSingle(countSql, params, Integer.class);

        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, SDM050401BO.class, pageable), pageable, count);
    }

    @Override
    public SDM050501BO getInitResult(SDM050501Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT cpo.promotion_order_id        AS promotionOrderId                              ");
        sql.append("         , cpo.promotion_list_id         AS promotionListId                               ");
        sql.append("         , cupl.promotion_nm             AS promotionNm                                   ");
        sql.append("         , cupl.promotion_cd             AS promotionCd                                   ");
        sql.append("         , cupl.from_date                AS fromDate                                      ");
        sql.append("         , cupl.to_date                  AS toDate                                        ");
        sql.append("         , cpo.facility_cd               AS pointCd                                       ");
        sql.append("         , cpo.site_id                   AS siteId                                        ");
        sql.append("         , cpo.facility_nm               AS pointNm                                       ");
        sql.append("         , cpo.facility_id               AS pointId                                       ");
        sql.append("         , cpo.local_order_no            AS orderNo                                       ");
        sql.append("         , cupl.invoice                  AS invoice                                       ");
        sql.append("         , cupl.gift_receipt             AS giftReceipt                                   ");
        sql.append("         , cupl.id_card                  AS idCard                                        ");
        sql.append("         , cupl.registration_card        AS registrationCard                              ");
        sql.append("         , cupl.lucky_draw_voucher       AS luckyDrawVoucher                              ");
        sql.append("         , cupl.others_flag              AS othersFlag                                    ");
        sql.append("         , cupl.gift_nm1                 AS giftNm1                                       ");
        sql.append("         , cupl.gift_nm2                 AS giftNm2                                       ");
        sql.append("         , cupl.gift_nm3                 AS giftNm3                                       ");
        sql.append("         , cupl.gift_max                 AS giftMax                                       ");
        sql.append("         , cupl.gift_receipt_sample_path AS giftReceiptSamplePath                         ");
        sql.append("         , cpo.product_cd                AS modelCd                                       ");
        sql.append("         , cpo.product_nm                AS modelNm                                       ");
        sql.append("         , cpo.cmm_product_id            AS modelId                                       ");
        sql.append("         , cpo.customer_nm               AS customerNm                                    ");
        sql.append("         , cpo.frame_no                  AS frameNo                                       ");
        sql.append("         , cpo.invoice_upload_path       AS invoiceUploadPath                             ");
        sql.append("         , cpo.gift_receipt_upload_path1 AS giftReceiptUploadPath1                        ");
        sql.append("         , cpo.gift_receipt_upload_path2 AS giftReceiptUploadPath2                        ");
        sql.append("         , cpo.gift_receipt_upload_path3 AS giftReceiptUploadPath3                        ");
        sql.append("         , cpo.id_card_path              AS idCardPath                                    ");
        sql.append("         , cpo.registration_card_path    AS registrationCardPath                          ");
        sql.append("         , cpo.others_path1              AS othersPath1                                   ");
        sql.append("         , cpo.others_path2              AS othersPath2                                   ");
        sql.append("         , cpo.others_path3              AS othersPath3                                   ");
        sql.append("         , cpo.lucky_draw_voucher_path   AS luckyDrawVoucherPath                          ");
        sql.append("         , cpo.jugement_status           AS jugementStatus                                ");
        sql.append("         , cupl.upload_end_date          AS uploadEndDate                                 ");
        sql.append("         , cpo.invoice_memo              AS invoiceMemo                                   ");
        sql.append("         , cpo.link_memo                 AS linkMemo                                      ");
        sql.append("         , cpo.verification_code_memo    AS verificationCodeMemo                          ");
        sql.append("         , cpo.winner                    AS winner                                        ");
        sql.append("         , cpo.reject_reason             AS rejectReason                                  ");
        sql.append("      FROM cmm_promotion_order cpo                                                        ");
        sql.append(" LEFT JOIN cmm_unit_promotion_list cupl on cpo.promotion_list_id = cupl.promotion_list_id ");
        sql.append("     WHERE cpo.promotion_order_id =:promotionOrderId                                      ");

        params.put("promotionOrderId", form.getPromotionOrderId());
        return super.queryForSingle(sql.toString(), params, SDM050501BO.class);
    }
}
