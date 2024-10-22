package com.a1stream.common.manager;

import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.logic.ConsumerLogic;
import com.a1stream.common.model.BaseConsumerForm;
import com.a1stream.domain.bo.common.CmmConsumerBO;
import com.a1stream.domain.entity.CmmConsumer;
import com.a1stream.domain.entity.ConsumerPrivacyPolicyResult;
import com.a1stream.domain.entity.ConsumerPrivateDetail;
import com.a1stream.domain.repository.CmmConsumerRepository;
import com.a1stream.domain.repository.ConsumerPrivacyPolicyResultRepository;
import com.a1stream.domain.repository.ConsumerPrivateDetailRepository;
import com.a1stream.domain.vo.CmmConsumerVO;
import com.a1stream.domain.vo.ConsumerParam;
import com.a1stream.domain.vo.ConsumerPrivacyPolicyResultVO;
import com.a1stream.domain.vo.ConsumerPrivateDetailVO;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.DateUtils;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;
/**
* 功能描述:
*
* MID2303
* 2024年6月14日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/14   Ruan Hansheng     New
*/
@Component
public class ConsumerManager {

    @Resource
    private CmmConsumerRepository cmmConsumerRepo;
    @Resource
    private ConsumerPrivateDetailRepository consumerPrivateDetailRepo;
    @Resource
    private ConsumerPrivacyPolicyResultRepository consumerPrivacyPolicyResultRepo;
    @Resource
    private ConsumerLogic consumerLogic;

    public CmmConsumerBO getConsumerById(Long consumerId, String siteId, boolean needPolicyResult) {

        if (Objects.isNull(consumerId)) {return new CmmConsumerBO();}

        CmmConsumerBO result = BeanMapUtils.mapTo(Objects.requireNonNullElse(BeanMapUtils.mapTo(cmmConsumerRepo.findById(consumerId), CmmConsumerBO.class), new CmmConsumerVO()), CmmConsumerBO.class);

        if (StringUtils.isNotBlank(siteId)) {

            ConsumerPrivateDetailVO consumerPrivateDetail = Objects.requireNonNullElse(BeanMapUtils.mapTo(consumerPrivateDetailRepo.findFirstByConsumerIdAndSiteId(consumerId, siteId), ConsumerPrivateDetailVO.class), new ConsumerPrivateDetailVO());

            result.setMobilePhone(consumerPrivateDetail.getMobilePhone());
            result.setMobilePhone2(consumerPrivateDetail.getMobilePhone2());
            result.setMobilePhone3(consumerPrivateDetail.getMobilePhone3());

            if (needPolicyResult) {
                result.setPrivacyPolicyResult(this.getConsumerPolicyInfo(siteId, result.getLastNm(), result.getMiddleNm(), result.getFirstNm(), result.getMobilePhone()));
            }
        }

        return result;
    }

    // 新增或修改consumer有关数据
    public void saveOrUpdateConsumer(BaseConsumerForm form) {

        ConsumerParam param = prepareConsumerData(form);
        if (param != null) {
            CmmConsumerVO cmmConsumerVO = param.getCmmConsumerVO();
            ConsumerPrivateDetailVO consumerPrivateDetailVO = param.getConsumerPrivateDetailVO();

            this.saveOrUpdateConsumer(cmmConsumerVO, consumerPrivateDetailVO);
            form.setConsumerId(cmmConsumerVO.getConsumerId());
        }
    }

    private ConsumerParam prepareConsumerData(BaseConsumerForm form) {

        if (StringUtils.isBlank(form.getLastNm()) && StringUtils.isBlank(form.getMiddleNm()) && StringUtils.isBlank(form.getFirstNm()) && StringUtils.isBlank(form.getMobilePhone())) {
            return null;
        }
        String consumerRetrieve = consumerLogic.getConsumerRetrieve(form.getLastNm(), form.getMiddleNm(), form.getFirstNm(), form.getMobilePhone());

        CmmConsumerVO cmmConsumerVO;
        ConsumerPrivateDetailVO consumerPrivateDetailVO;

        // consumerId为空则新增consumer
        if (ObjectUtils.isEmpty(form.getConsumerId())) {
            // 用consumerRetrieve全siteId查询consumerPrivateDetail
            consumerPrivateDetailVO = this.getConsumerPrivateDetailVO(consumerRetrieve);
            // 没有则新建consumer
            if (null == consumerPrivateDetailVO) {
                cmmConsumerVO = CmmConsumerVO.create(form.getConsumerType());
                this.buildCmmConsumerVO(cmmConsumerVO, form);

                form.setConsumerId(cmmConsumerVO.getConsumerId());

                consumerPrivateDetailVO = ConsumerPrivateDetailVO.create();
                this.buildConsumerPrivateDetailVO(consumerPrivateDetailVO, form);

            } else {
                // 有则取第一条 修改对应cmmConsumer
                cmmConsumerVO = getCmmConsumerVO(consumerPrivateDetailVO.getConsumerId());
                this.buildCmmConsumerVO(cmmConsumerVO, form);

                form.setConsumerId(cmmConsumerVO.getConsumerId());

                // 之后按consumerId + siteId查询consumerPrivateDetail
                consumerPrivateDetailVO = this.getConsumerPrivateDetailVO(form.getConsumerId(), form.getSiteId());

                // 没有则新建consumerPrivateDetail
                if (null == consumerPrivateDetailVO) {
                    consumerPrivateDetailVO = ConsumerPrivateDetailVO.create();
                    this.buildConsumerPrivateDetailVO(consumerPrivateDetailVO, form);
                } else {
                    // 有则修改consumerPrivateDetail
                    this.buildConsumerPrivateDetailVO(consumerPrivateDetailVO, form);
                }
            }
        } else {
            // 不为空则修改consumer

            // 按consumerId查询cmmConsumer，查询当前site下所属数据
            cmmConsumerVO = this.getCmmConsumerVO(form.getConsumerId());
            consumerPrivateDetailVO = this.getConsumerPrivateDetailVO(form.getConsumerId(), form.getSiteId());
            // 判断画面retrieve与cmmConsumer是否一致
            if (!Objects.isNull(cmmConsumerVO) && consumerRetrieve.equals(Objects.isNull(consumerPrivateDetailVO) ? cmmConsumerVO.getConsumerRetrieve() : consumerPrivateDetailVO.getConsumerRetrieve())) {
                // 一致则说明没有更改名字和电话 直接更新对应cmmConsumer
                this.buildCmmConsumerVO(cmmConsumerVO, form);

                // 没有则新建consumerPrivateDetail
                if (null == consumerPrivateDetailVO) {
                    consumerPrivateDetailVO = ConsumerPrivateDetailVO.create();
                    this.buildConsumerPrivateDetailVO(consumerPrivateDetailVO, form);
                } else {
                    // 有则修改consumerPrivateDetail
                    this.buildConsumerPrivateDetailVO(consumerPrivateDetailVO, form);
                }
            } else {
                // 用consumerRetrieve全siteId查询consumerPrivateDetail
                consumerPrivateDetailVO = this.getConsumerPrivateDetailVO(consumerRetrieve);
                // 没有则新建consumer
                if (null == consumerPrivateDetailVO) {
                    cmmConsumerVO = CmmConsumerVO.create(form.getConsumerType());
                    this.buildCmmConsumerVO(cmmConsumerVO, form);

                    form.setConsumerId(cmmConsumerVO.getConsumerId());

                    consumerPrivateDetailVO = ConsumerPrivateDetailVO.create();
                    this.buildConsumerPrivateDetailVO(consumerPrivateDetailVO, form);

                } else {
                    // 有则取第一条 修改对应cmmConsumer
                    cmmConsumerVO = getCmmConsumerVO(consumerPrivateDetailVO.getConsumerId());
                    this.buildCmmConsumerVO(cmmConsumerVO, form);

                    form.setConsumerId(cmmConsumerVO.getConsumerId());

                    // 按consumerId + siteId查询consumerPrivateDetail
                    consumerPrivateDetailVO = this.getConsumerPrivateDetailVO(form.getConsumerId(), form.getSiteId());

                    // 没有则新建consumerPrivateDetail
                    if (null == consumerPrivateDetailVO) {
                        consumerPrivateDetailVO = ConsumerPrivateDetailVO.create();
                        this.buildConsumerPrivateDetailVO(consumerPrivateDetailVO, form);
                    } else {
                        // 有则修改consumerPrivateDetail
                        this.buildConsumerPrivateDetailVO(consumerPrivateDetailVO, form);
                    }
                }
            }
        }

        return new ConsumerParam(cmmConsumerVO, consumerPrivateDetailVO, cmmConsumerVO.getConsumerId());
    }

    public void saveOrUpdateConsumerPrivacyPolicyResult(BaseConsumerForm form) {
        String consumerRetrieve = consumerLogic.getConsumerRetrieve(form.getLastNm(), form.getMiddleNm(), form.getFirstNm(), form.getMobilePhone());
        ConsumerPrivacyPolicyResultVO consumerPrivacyPolicyResultVO = consumerPrivacyPolicyResultRepo.getConsumerPrivacyPolicyResultVO(form.getSiteId(), consumerRetrieve, CommonConstants.CHAR_N);
        // 没有则新建
        if (null == consumerPrivacyPolicyResultVO) {
            consumerPrivacyPolicyResultVO = new ConsumerPrivacyPolicyResultVO();
        }
        form.setConsumerId(form.getConsumerId());
        this.buildConsumerPrivacyPolicyResultVO(consumerPrivacyPolicyResultVO, form);
        this.saveOrUpdateConsumerPrivacyPolicyResult(consumerPrivacyPolicyResultVO);
    }

    // 上传成功时执行
    public void consumerUploadPrivacy(BaseConsumerForm form) {

        String consumerRetrieve = consumerLogic.getConsumerRetrieve(form.getLastNm(), form.getMiddleNm(), form.getFirstNm(), form.getMobilePhone());
        ConsumerPrivacyPolicyResultVO consumerPrivacyPolicyResultVO = consumerPrivacyPolicyResultRepo.getConsumerPrivacyPolicyResultVO(form.getSiteId(), consumerRetrieve, CommonConstants.CHAR_N);
        // 没有则新建
        if (null == consumerPrivacyPolicyResultVO) {
            consumerPrivacyPolicyResultVO = new ConsumerPrivacyPolicyResultVO();
        }
        this.buildConsumerPrivacyPolicyResultVO(consumerPrivacyPolicyResultVO, form);
        consumerPrivacyPolicyResultVO.setResultUploadDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
        consumerPrivacyPolicyResultRepo.save(BeanMapUtils.mapTo(consumerPrivacyPolicyResultVO, ConsumerPrivacyPolicyResult.class));
    }

    public ConsumerPrivateDetailVO getConsumerPrivateDetailVO(String consumerRetrieve) {

        return BeanMapUtils.mapTo(consumerPrivateDetailRepo.findByConsumerRetrieve(consumerRetrieve), ConsumerPrivateDetailVO.class);
    }

    public CmmConsumerVO getCmmConsumerVO(Long consumerId) {

        return BeanMapUtils.mapTo(cmmConsumerRepo.findByConsumerId(consumerId), CmmConsumerVO.class);
    }

    public void buildCmmConsumerVO(CmmConsumerVO cmmConsumerVO, BaseConsumerForm form) {

        String consumerFullNm = consumerLogic.getConsumerFullNm(form.getLastNm(), form.getMiddleNm(), form.getFirstNm());
        String consumerRetrieve = consumerLogic.getConsumerRetrieve(form.getLastNm(), form.getMiddleNm(), form.getFirstNm(), form.getMobilePhone());

        cmmConsumerVO.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);
        cmmConsumerVO.setLastNm(form.getLastNm());
        cmmConsumerVO.setMiddleNm(form.getMiddleNm());
        cmmConsumerVO.setFirstNm(form.getFirstNm());
        cmmConsumerVO.setConsumerFullNm(consumerFullNm);
        cmmConsumerVO.setConsumerRetrieve(consumerRetrieve);
        cmmConsumerVO.setMobilePhone(form.getMobilePhone());

        // 有的页面如SPM020103没有输入以下参数 需要作判断
        if (StringUtils.isNotBlank(form.getGender())) {
            cmmConsumerVO.setGender(form.getGender());
        }
        if (StringUtils.isNotBlank(form.getEmail())) {
            cmmConsumerVO.setEmail(form.getEmail());
        }
        if (!ObjectUtils.isEmpty(form.getProvince())) {
            cmmConsumerVO.setProvinceGeographyId(form.getProvince());
        }
        if (!ObjectUtils.isEmpty(form.getDistrict())) {
            cmmConsumerVO.setCityGeographyId(form.getDistrict());
        }
        if (StringUtils.isNotBlank(form.getAddress())) {
            cmmConsumerVO.setAddress(form.getAddress());
        }
        if (StringUtils.isNotBlank(form.getBirthDate())) {
            cmmConsumerVO.setBirthDate(form.getBirthDate());
        }
        if (StringUtils.isNotBlank(form.getBirthYear())) {
            cmmConsumerVO.setBirthYear(form.getBirthYear());
        }
        if (StringUtils.isNotBlank(form.getSns())) {
            cmmConsumerVO.setSns(form.getSns());
        }
        if (StringUtils.isNotBlank(form.getTaxCode())) {
            cmmConsumerVO.setTaxCode(form.getTaxCode());
        }
        if (StringUtils.isNotBlank(form.getRegistDate())) {
            cmmConsumerVO.setRegistrationDate(form.getRegistDate());
        }
        if (StringUtils.isNotBlank(form.getComment())) {
            cmmConsumerVO.setComment(form.getComment());
        }
        if (StringUtils.isNotBlank(form.getBusinessNm())) {
            cmmConsumerVO.setBusinessNm(form.getBusinessNm());
        }
        if (StringUtils.isNotBlank(form.getAddress2())) {
            cmmConsumerVO.setAddress2(form.getAddress2());
        }
        if (StringUtils.isNotBlank(form.getTelephone())) {
            cmmConsumerVO.setTelephone(form.getTelephone());
        }
        if (StringUtils.isNotBlank(form.getEmail2())) {
            cmmConsumerVO.setEmail2(form.getEmail2());
        }
        if (StringUtils.isNotBlank(form.getOccupation())) {
            cmmConsumerVO.setOccupation(form.getOccupation());
        }
    }

    public void buildConsumerPrivateDetailVO(ConsumerPrivateDetailVO consumerPrivateDetailVO, BaseConsumerForm form) {

        String consumerFullNm = consumerLogic.getConsumerFullNm(form.getLastNm(), form.getMiddleNm(), form.getFirstNm());
        String consumerRetrieve = consumerLogic.getConsumerRetrieve(form.getLastNm(), form.getMiddleNm(), form.getFirstNm(), form.getMobilePhone());

        consumerPrivateDetailVO.setSiteId(form.getSiteId());
        consumerPrivateDetailVO.setConsumerId(form.getConsumerId());
        consumerPrivateDetailVO.setLastNm(form.getLastNm());
        consumerPrivateDetailVO.setMiddleNm(form.getMiddleNm());
        consumerPrivateDetailVO.setFirstNm(form.getFirstNm());
        consumerPrivateDetailVO.setConsumerFullNm(consumerFullNm);
        consumerPrivateDetailVO.setConsumerRetrieve(consumerRetrieve);
        consumerPrivateDetailVO.setMobilePhone(form.getMobilePhone());
    }

    public void buildConsumerPrivacyPolicyResultVO(ConsumerPrivacyPolicyResultVO consumerPrivacyPolicyResultVO, BaseConsumerForm form) {

        String consumerFullNm = consumerLogic.getConsumerFullNm(form.getLastNm(), form.getMiddleNm(), form.getFirstNm());
        String consumerRetrieve = consumerLogic.getConsumerRetrieve(form.getLastNm(), form.getMiddleNm(), form.getFirstNm(), form.getMobilePhone());

        consumerPrivacyPolicyResultVO.setSiteId(form.getSiteId());
        // 防止前台传来consumerId为null时 恰好输入有consumerRetrieve对应的值 将该值consumerId更新为null
        if (!ObjectUtils.isEmpty(form.getConsumerId())) {
            consumerPrivacyPolicyResultVO.setConsumerId(form.getConsumerId());
        }
        consumerPrivacyPolicyResultVO.setConsumerFullNm(consumerFullNm);
        consumerPrivacyPolicyResultVO.setMobilePhone(form.getMobilePhone());
        consumerPrivacyPolicyResultVO.setAgreementResult(form.getPrivacyResult());
        consumerPrivacyPolicyResultVO.setDeleteFlag(CommonConstants.CHAR_N);
        consumerPrivacyPolicyResultVO.setConsumerRetrieve(consumerRetrieve);
    }

    public ConsumerPrivateDetailVO getConsumerPrivateDetailVO(Long consumerId, String siteId) {

        return BeanMapUtils.mapTo(consumerPrivateDetailRepo.findFirstByConsumerIdAndSiteId(consumerId, siteId), ConsumerPrivateDetailVO.class);
    }

    public void saveOrUpdateConsumer(CmmConsumerVO cmmConsumerVO, ConsumerPrivateDetailVO consumerPrivateDetailVO) {

        if (null != cmmConsumerVO) {
            cmmConsumerRepo.save(BeanMapUtils.mapTo(cmmConsumerVO, CmmConsumer.class));
        }

        if (null != consumerPrivateDetailVO) {
            consumerPrivateDetailRepo.save(BeanMapUtils.mapTo(consumerPrivateDetailVO, ConsumerPrivateDetail.class));
        }

    }

    public void saveOrUpdateConsumerPrivacyPolicyResult(ConsumerPrivacyPolicyResultVO consumerPrivacyPolicyResultVO) {

        if (null != consumerPrivacyPolicyResultVO) {
            consumerPrivacyPolicyResultRepo.save(BeanMapUtils.mapTo(consumerPrivacyPolicyResultVO, ConsumerPrivacyPolicyResult.class));
        }
    }

    public String getConsumerPolicyInfo(String siteId, String lastNm, String middleNm, String firstNm, String mobilePhone) {

        String consumerRetrieve = consumerLogic.getConsumerRetrieve(lastNm, middleNm, firstNm, mobilePhone);

        return consumerPrivacyPolicyResultRepo.getPrivacyPolicyResultList(siteId, consumerRetrieve, CommonConstants.CHAR_N);
    }
}