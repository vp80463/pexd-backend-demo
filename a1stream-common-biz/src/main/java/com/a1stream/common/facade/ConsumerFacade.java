package com.a1stream.common.facade;

import java.util.Objects;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.logic.ConsumerLogic;
import com.a1stream.common.model.BaseConsumerForm;
import com.a1stream.common.service.ConsumerService;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.common.CmmConsumerBO;
import com.a1stream.domain.parameter.service.ConsumerPolicyParameter;
import com.a1stream.domain.vo.ConsumerPrivacyPolicyResultVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

@Component
public class ConsumerFacade {

    @Resource
    private ConsumerService consumerService;
    @Resource
    private ConsumerLogic consumerLogic;

    public void consumerUploadPrivacy(BaseConsumerForm form, PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        consumerService.consumerUploadPrivacy(form);
    }

    public void saveOrUpdateConsumerInfo(@RequestBody final BaseConsumerForm form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        consumerService.saveOrUpdateConsumer(form);
        consumerService.saveOrUpdateConsumerPrivacyPolicyResult(form);
    }

    public CmmConsumerBO getConsumerDetail(BaseConsumerForm model, String siteId) {

        CmmConsumerBO result;

        if (Objects.isNull(model.getConsumerId())) {
            //基本信息从主画面带入的数值
            result = BeanMapUtils.mapTo(model, CmmConsumerBO.class);
        }
        else {

            result = consumerService.getConsumerById(model.getConsumerId(), siteId);
            //基本数据取主画面带入的数值
            result.setLastNm(model.getLastNm());
            result.setMiddleNm(model.getMiddleNm());
            result.setFirstNm(model.getFirstNm());
            result.setMobilePhone(model.getMobilePhone());
            result.setEmail(model.getEmail());
            result.setAge(consumerLogic.getAgeByBirthYear(result.getBirthYear()));
        }

        return result;
    }

    public ConsumerPrivacyPolicyResultVO prepareConsumerPrivacyPolicyResult(ConsumerPolicyParameter param) {

        String consumerRetrieve = consumerLogic.getConsumerRetrieve(param.getLastNm(), param.getMiddleNm(), param.getFirstNm(), param.getMobilephone());
        ConsumerPrivacyPolicyResultVO consumerPrivacyPolicyResult = consumerService.timelyFindConsumerPrivacyPolicyResult(param.getSiteId(), consumerRetrieve);

        //画面未授权，需要将原数据的deleteFlag更新为Y
        if (StringUtils.isBlank(param.getPolicyResultFlag())) {

            if (!Objects.isNull(consumerPrivacyPolicyResult)) {
                consumerPrivacyPolicyResult.setDeleteFlag(CommonConstants.CHAR_Y);

                return consumerPrivacyPolicyResult;
            }
        } else {
            //画面有授权
            if (Objects.isNull(consumerPrivacyPolicyResult)) {//新增

                return setConsumerPrivacyPolicyResult(new ConsumerPrivacyPolicyResultVO(), param, CommonConstants.OPERATION_STATUS_NEW);
            } else if (StringUtils.isNotBlank(param.getPolicyFileName())) {//修改

                return setConsumerPrivacyPolicyResult(consumerPrivacyPolicyResult, param, CommonConstants.OPERATION_STATUS_UPDATE);
            }
        }

        return consumerPrivacyPolicyResult;
    }

    public void saveConsumerDetail(CmmConsumerBO model, String siteId) {

        BaseConsumerForm consumerModel = this.generateConsumerModelByScreen(model, siteId);

        consumerService.saveOrUpdateConsumer(consumerModel);

        model.setConsumerId(consumerModel.getConsumerId());
    }

    private ConsumerPrivacyPolicyResultVO setConsumerPrivacyPolicyResult(ConsumerPrivacyPolicyResultVO result, ConsumerPolicyParameter model, String action) {

        if (StringUtils.equals(action, CommonConstants.OPERATION_STATUS_NEW)) {

            result.setSiteId(model.getSiteId());
            result.setConsumerFullNm(consumerLogic.getConsumerFullNm(model.getLastNm(), model.getMiddleNm(), model.getFirstNm()));
            result.setMobilePhone(model.getMobilephone());
            result.setConsumerRetrieve(consumerLogic.getConsumerRetrieve(model.getLastNm(), model.getMiddleNm(), model.getFirstNm(), model.getMobilephone()));
            result.setDeleteFlag(CommonConstants.CHAR_N);
        }

        result.setAgreementResult(model.getPolicyResultFlag());
        result.setFacilityCd(model.getPointCd());
        result.setResultUploadDate(ComUtil.nowLocalDate());

        return result;
    }

    private BaseConsumerForm generateConsumerModelByScreen(CmmConsumerBO model, String siteId) {

        BaseConsumerForm result = BeanMapUtils.mapTo(model, BaseConsumerForm.class);

        result.setSiteId(siteId);
        result.setProvince(model.getProvinceGeographyId());
        result.setDistrict(model.getCityGeographyId());
        result.setMobilePhone(model.getMobilePhone());

        return result;
    }
}
