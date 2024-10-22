package com.a1stream.master.facade;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Component;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.constants.PJConstants.ConsumerType;
import com.a1stream.common.constants.PJConstants.GenderType;
import com.a1stream.common.facade.ConsumerFacade;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.common.logic.ConsumerLogic;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.batch.SvVinCodeTelIFBO;
import com.a1stream.domain.bo.master.CMM010301BO;
import com.a1stream.domain.bo.master.CMM010301ExportBO;
import com.a1stream.domain.bo.master.CMM010302BO;
import com.a1stream.domain.form.master.CMM010301Form;
import com.a1stream.domain.form.master.CMM010302Form;
import com.a1stream.domain.parameter.service.ConsumerPolicyParameter;
import com.a1stream.domain.vo.CmmConsumerVO;
import com.a1stream.domain.vo.ConsumerPrivacyPolicyResultVO;
import com.a1stream.domain.vo.ConsumerPrivateDetailVO;
import com.a1stream.domain.vo.QueueDataVO;
import com.a1stream.master.service.CMM0103Service;
import com.ymsl.plugins.userauth.util.JsonUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;


/**
* 功能描述:
*
* @author mid1966
*/
@Component
public class CMM0103Facade {

    @Resource
    private CMM0103Service cmm0103Service;
    @Resource
    private HelperFacade helperFacade;
    @Resource
    private ConsumerLogic consumerLogic;
    @Resource
    private ConsumerFacade consumerFacade;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

    public List<CMM010301BO> findConsumerInfoList(CMM010301Form form, String siteId) {

        form.setHeaderFlag(getDistinguishFlag(form));

        this.validateData(form);

        List<CMM010301BO> resultList = cmm0103Service.findConsumerInfoList(form, siteId);

        // 数据库为codeDbid 前台显示为codeData1
        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(ConsumerType.CODE_ID);

        for(CMM010301BO bo : resultList) {

            bo.setConsumerType(codeMap.get(bo.getConsumerType()));
        }
        return resultList;
    }

    public List<CMM010301ExportBO> findConsumerExportList(CMM010301Form form, PJUserDetails uc) {

        form.setHeaderFlag(getDistinguishFlag(form));

        List<CMM010301ExportBO> resultList = cmm0103Service.findConsumerExportList(form, uc);

        // 数据库为codeDbid 前台显示为codeData1
        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(ConsumerType.CODE_ID, GenderType.CODE_ID);

        for(CMM010301ExportBO bo : resultList) {

            if(bo.getSalesDate() != null) {

                bo.setSalesDate(LocalDate.parse(bo.getSalesDate(), DATE_FORMATTER).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
            }

            bo.setConsumerType(codeMap.get(bo.getConsumerType()));
            bo.setGender(codeMap.get(bo.getGender()));
        }
        return resultList;
    }

    public CMM010302Form getConsumerMaintenanceInfo(CMM010302Form form) {

        CMM010302Form initForm = cmm0103Service.getConsumerMaintenanceInfo(form);
        List<CMM010302BO> motorcycleInfoList = cmm0103Service.getMotorcycleInfoList(form);
        List<CMM010302BO> serviceDetailList = cmm0103Service.getServiceDetailList(form, form.getSiteId());

        initForm.setMotorcycleInfoList(motorcycleInfoList);
        initForm.setServiceDetailList(serviceDetailList);

        return initForm;
    }

    public CMM010302Form confirmConsumerInfoList(CMM010302Form form, String pic) {

        String consumerRetrieve = consumerLogic.getConsumerRetrieve(form.getLastNm(), form.getMiddleNm(), form.getFirstNm(), form.getMobilePhone());

        CmmConsumerVO cmmConsumerVO;
        ConsumerPrivateDetailVO consumerPrivateDetail = null;
        List<QueueDataVO> queueDataList = new ArrayList<>();

        //当consumerId为空，走新建逻辑
        if (Objects.isNull(form.getConsumerId())) {
            //验证consumer是否存在
            this.checkConsumerExist(form, consumerRetrieve);

            cmmConsumerVO = CmmConsumerVO.create(form.getConsumerType());
            cmmConsumerVO.setUpdateCount(0);
        }
        //当consumerId有值，走更新逻辑
        else {

            cmmConsumerVO = cmm0103Service.findCmmConsumerVO(form.getConsumerId());
            consumerPrivateDetail = cmm0103Service.findConsumerPrivateDetailVO(form.getConsumerId(), form.getSiteId());

            if (Objects.isNull(cmmConsumerVO)) {throw new BusinessCodedException(ComUtil.t("M.E.10449"));}

            //当name + mobile发生更改时，验证consumer是否存在
            if (!consumerRetrieve.equals(Objects.isNull(consumerPrivateDetail) ? cmmConsumerVO.getConsumerRetrieve() : consumerPrivateDetail.getConsumerRetrieve())) {

                this.checkConsumerExist(form, consumerRetrieve);
            }

            //当mobile发生变更时，将所属为owner的人车关系插入Q表
            if (!StringUtils.equals(cmmConsumerVO.getMobilePhone(), form.getMobilePhone())) {

                //查询当前consumer所有的owner,生成Q表
                List<SvVinCodeTelIFBO> ownerList = cmm0103Service.getAllOwnerMcByConsumerId(form.getConsumerId());

                for (SvVinCodeTelIFBO ownerMc : ownerList) {

                    queueDataList.add(QueueDataVO.create(form.getSiteId(), InterfCode.DMS_TO_MYY_SV_VINCODETEL, "cmm_consumer_serial_pro_relation", Long.valueOf(ownerMc.getQueueId()), JsonUtils.toString(List.of(this.prepareVinCodeTelIFBOByOwnerMc(ownerMc, form.getMobilePhone(), pic)).stream().toList())));
                }
            }
        }

        cmmConsumerVO.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);
        cmmConsumerVO.setVipNo(form.getVipNo());
        cmmConsumerVO.setLastNm(form.getLastNm());
        cmmConsumerVO.setMiddleNm(form.getMiddleNm());
        cmmConsumerVO.setFirstNm(form.getFirstNm());
        cmmConsumerVO.setConsumerFullNm(consumerLogic.getConsumerFullNm(form.getLastNm(), form.getMiddleNm(), form.getFirstNm()));
        cmmConsumerVO.setConsumerRetrieve(consumerRetrieve);
        cmmConsumerVO.setGender(form.getGender());
        cmmConsumerVO.setIdNo(form.getIdNo());
        cmmConsumerVO.setIdClassificationNo(form.getConsumerIdentification());
        cmmConsumerVO.setVisaNo(form.getVisaNo());
        cmmConsumerVO.setEmail(form.getEmail1());
        cmmConsumerVO.setEmail2(form.getEmail2());
        cmmConsumerVO.setSns(form.getMobile2());
        cmmConsumerVO.setTelephone(form.getTelephone());
        cmmConsumerVO.setFaxNo(form.getFaxNo());
        cmmConsumerVO.setPostCode(form.getPostCd());
        cmmConsumerVO.setProvinceGeographyId(form.getProvince());
        cmmConsumerVO.setCityGeographyId(form.getCityId());
        cmmConsumerVO.setAddress(form.getAddr1());
        cmmConsumerVO.setAddress2(form.getAddr2());
        cmmConsumerVO.setBirthDate(form.getBirthday().substring(4));
        cmmConsumerVO.setBirthYear(form.getBirthday().substring(0,4));
        cmmConsumerVO.setOccupation(form.getOccupation());
        cmmConsumerVO.setInterestModel(form.getInterestModel());
        cmmConsumerVO.setMcBrand(form.getCurrentBikeBrand());
        cmmConsumerVO.setMcPurchaseDate(form.getCurrentBikePurchase());
        cmmConsumerVO.setComment(form.getComment());
        cmmConsumerVO.setRegistrationDate(form.getRegDate());
        cmmConsumerVO.setRegistrationReason(form.getRegistrationReason());
        cmmConsumerVO.setMobilePhone(form.getMobilePhone());

        //consumer_private_detail 插入、更新
        if (Objects.isNull(consumerPrivateDetail)) {

            consumerPrivateDetail = new ConsumerPrivateDetailVO();
            consumerPrivateDetail.setUpdateCount(0);
            consumerPrivateDetail.setConsumerId(cmmConsumerVO.getConsumerId());
        }

        consumerPrivateDetail.setSiteId(form.getSiteId());
        consumerPrivateDetail.setLastNm(form.getLastNm());
        consumerPrivateDetail.setMiddleNm(form.getMiddleNm());
        consumerPrivateDetail.setFirstNm(form.getFirstNm());
        consumerPrivateDetail.setConsumerFullNm(consumerLogic.getConsumerFullNm(form.getLastNm(), form.getMiddleNm(), form.getFirstNm()));
        consumerPrivateDetail.setConsumerRetrieve(consumerRetrieve);
        consumerPrivateDetail.setMobilePhone(form.getMobilePhone());
        consumerPrivateDetail.setMobilePhone2(form.getMobile2());
        consumerPrivateDetail.setMobilePhone3(form.getMobile3());

        // consumer_privacy_policy_result 插入、更新
        ConsumerPrivacyPolicyResultVO consumerPrivacyPolicyResult = consumerFacade.prepareConsumerPrivacyPolicyResult(new ConsumerPolicyParameter(form.getSiteId()
                , null
                , form.getLastNm()
                , form.getMiddleNm()
                , form.getFirstNm()
                , form.getMobilePhone()
                , form.getPolicyResultFlag()
                , form.getPolicyFileName()));

        cmm0103Service.updateConfirm(cmmConsumerVO, consumerPrivateDetail, consumerPrivacyPolicyResult, queueDataList);

        form.setConsumerId(cmmConsumerVO.getConsumerId());

        return form;
    }

    private SvVinCodeTelIFBO prepareVinCodeTelIFBOByOwnerMc(SvVinCodeTelIFBO ownerMc, String mobilePhone, String pic) {

        ownerMc.setTelNo(mobilePhone);
        ownerMc.setTelLastDigits(ownerMc.getTelNo().length() >= 4 ? ownerMc.getTelNo().substring(ownerMc.getTelNo().length() - 4) : ownerMc.getTelNo());
        ownerMc.setOwnerChangedFlg(CommonConstants.CHAR_ZERO);
        ownerMc.setUpdateAuthor(pic);
        ownerMc.setUpdateDate(LocalDateTime.now().toString());
        ownerMc.setCreateAuthor(pic);
        ownerMc.setCreateDate(LocalDateTime.now().toString());
        ownerMc.setUpdateProgram("CMM0103");
        ownerMc.setUpdateCounter(CommonConstants.CHAR_ZERO);

        return ownerMc;
    }

    private void checkConsumerExist(CMM010302Form form, String consumerRetrieve) {

         ConsumerPrivateDetailVO consumerPrivateDetailVO = cmm0103Service.findConsumerPrivateDetailByRetrieveStr(consumerRetrieve);
        //用consumerRetrieve全siteId查询consumerPrivateDetail
        //如果consumer已存在，报错,
        if (!Objects.isNull(consumerPrivateDetailVO) && (Objects.isNull(form.getConsumerId()) || !consumerPrivateDetailVO.getConsumerId().equals(form.getConsumerId()))) {
            throw new BusinessCodedException(ComUtil.t("M.E.10506", new String[] {consumerLogic.getConsumerFullNm(form.getLastNm(), form.getMiddleNm(), form.getFirstNm()), form.getMobilePhone()}));
        }
    }

    private void validateData(CMM010301Form form) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

        if(!form.isHeaderFlag()) {

            // 1.如果Header部所有字段都为空,consumerType不得为空
            if(StringUtil.isBlank(form.getConsumerType())) {

                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10326", new String[] {CodedMessageUtils.getMessage("label.consumerType")}));
            }

            // 2.如果ConsumerType <> 'S0212SCUSTOMER',salesDate不得为空
            if(!form.getConsumerType().equals(ConsumerType.TWOSCUSTOMER.getCodeDbid())) {

                if(StringUtil.isBlank(form.getSalesDateFrom())) {

                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10326", new String[] {CodedMessageUtils.getMessage("label.salesDateFrom")}));
                }

                if(StringUtil.isBlank(form.getSalesDateTo())) {

                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10326", new String[] {CodedMessageUtils.getMessage("label.salesDateTo")}));
                }
            }

            // 3.SalesDateFrom和SalesDateTo不为空的情况，两个日期的天数差必须大于184天
            if(StringUtil.isNotBlank(form.getSalesDateFrom()) && StringUtil.isNotBlank(form.getSalesDateTo())) {

                LocalDate salesDateFrom = LocalDate.parse(form.getSalesDateFrom(), formatter);
                LocalDate salesDateTo = LocalDate.parse(form.getSalesDateTo(), formatter);
                if(ChronoUnit.DAYS.between(salesDateFrom, salesDateTo) > 184) {

                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00339", new String[] {CodedMessageUtils.getMessage("label.salesDateFrom"),CodedMessageUtils.getMessage("label.salesDateTo")}));
                }
            }

            // 4.如果ConsumerType = 'S0212SCUSTOMER',ServiceDateFrom,ServiceDateTo不得为空
            if(form.getConsumerType().equals(ConsumerType.TWOSCUSTOMER.getCodeDbid())) {

                if(StringUtil.isBlank(form.getServiceDateFrom())) {

                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10326", new String[] {CodedMessageUtils.getMessage("label.serviceDateFrom")}));
                }

                if(StringUtil.isBlank(form.getServiceDateTo())) {

                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10326", new String[] {CodedMessageUtils.getMessage("label.serviceDateTo")}));
                }
            }

            // 5.如果ServiceDateFrom,ServiceDateTo都不为空，ConsumerType必须等于'S0212SCUSTOMER'
            if(StringUtil.isNotBlank(form.getServiceDateFrom()) && StringUtil.isNotBlank(form.getServiceDateTo())) {

                if(!form.getConsumerType().equals(ConsumerType.TWOSCUSTOMER.getCodeDbid())) {

                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10326", new String[] {ConsumerType.TWOSCUSTOMER.getCodeData1()}));
                }
            }

            // 6.如果ConsumerType = 'S0212SCUSTOMER'，ServiceDateFrom,ServiceDateTo都不为空,ServiceDateTo不能大于ServiceDateFrom+1个月
            if(form.getConsumerType().equals(ConsumerType.TWOSCUSTOMER.getCodeDbid()) && StringUtil.isNotBlank(form.getServiceDateFrom()) && StringUtil.isNotBlank(form.getServiceDateTo())) {

                LocalDate serviceDateFrom = LocalDate.parse(form.getServiceDateFrom(), formatter);
                LocalDate serviceDateTo = LocalDate.parse(form.getServiceDateTo(), formatter);
                LocalDate serviceDateFromPlusOneMonth = serviceDateFrom.plus(1, ChronoUnit.MONTHS);

                if (serviceDateTo.isAfter(serviceDateFromPlusOneMonth)) {

                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00355", new String[] {CodedMessageUtils.getMessage("label.serviceDateFrom"),CodedMessageUtils.getMessage("label.serviceDateTo")}));
                }
            }
        }
    }

    private boolean getDistinguishFlag(CMM010301Form form) {

        // (header or condition)
        return (StringUtil.isNotBlank(form.getLastNm()) || StringUtil.isNotBlank(form.getMiddleNm())
                || StringUtil.isNotBlank(form.getFirstNm()) || StringUtil.isNotBlank(form.getPhone())
                || StringUtil.isNotBlank(form.getIdNo()) || StringUtil.isNotBlank(form.getVipNo()));
    }
}