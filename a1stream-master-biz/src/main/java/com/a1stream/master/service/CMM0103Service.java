package com.a1stream.master.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.manager.ConsumerManager;
import com.a1stream.domain.bo.batch.SvVinCodeTelIFBO;
import com.a1stream.domain.bo.master.CMM010301BO;
import com.a1stream.domain.bo.master.CMM010301ExportBO;
import com.a1stream.domain.bo.master.CMM010302BO;
import com.a1stream.domain.entity.CmmConsumer;
import com.a1stream.domain.entity.ConsumerPrivacyPolicyResult;
import com.a1stream.domain.entity.ConsumerPrivateDetail;
import com.a1stream.domain.entity.QueueData;
import com.a1stream.domain.form.master.CMM010301Form;
import com.a1stream.domain.form.master.CMM010302Form;
import com.a1stream.domain.repository.CmmConsumerRepository;
import com.a1stream.domain.repository.CmmConsumerSerialProRelationRepository;
import com.a1stream.domain.repository.ConsumerPrivacyPolicyResultRepository;
import com.a1stream.domain.repository.ConsumerPrivateDetailRepository;
import com.a1stream.domain.repository.QueueDataRepository;
import com.a1stream.domain.repository.ServiceOrderRepository;
import com.a1stream.domain.vo.CmmConsumerVO;
import com.a1stream.domain.vo.ConsumerPrivacyPolicyResultVO;
import com.a1stream.domain.vo.ConsumerPrivateDetailVO;
import com.a1stream.domain.vo.QueueDataVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid1966
*/
@Service
public class CMM0103Service {

    @Resource
    private CmmConsumerRepository cmmConsumerRepository;

    @Resource
    private CmmConsumerSerialProRelationRepository cmmConsumerSerialProRelationRepository;

    @Resource
    private ServiceOrderRepository serviceOrderRepository;

    @Resource
    private ConsumerPrivateDetailRepository consumerPrivateDetailRepository;

    @Resource
    private ConsumerManager consumerMgr;
    @Resource
    private QueueDataRepository queueDataRepo;

    @Resource
    private ConsumerPrivacyPolicyResultRepository consumerPrivacyPolicyResultRepository;

    public List<CMM010301BO> findConsumerInfoList(CMM010301Form form, String siteId) {

        return cmmConsumerRepository.findConsumerInfoList(form, siteId);
    }

    public List<CMM010301ExportBO> findConsumerExportList(CMM010301Form form, PJUserDetails uc) {

        return cmmConsumerRepository.findConsumerExportList(form, uc);
    }

    public CMM010302Form getConsumerMaintenanceInfo(CMM010302Form form) {

        return cmmConsumerRepository.getConsumerMaintenanceInfo(form);
    }

    public List<CMM010302BO> getMotorcycleInfoList(CMM010302Form form) {

        return cmmConsumerSerialProRelationRepository.getMotorcycleInfoList(form);
    }

    public List<CMM010302BO> getServiceDetailList(CMM010302Form form, String siteId) {

        return serviceOrderRepository.getServiceDetailList(form, siteId);
    }

    public CmmConsumerVO findCmmConsumerVO(Long consumerId) {

        return BeanMapUtils.mapTo(cmmConsumerRepository.findByConsumerId(consumerId), CmmConsumerVO.class);
    }

    public ConsumerPrivateDetailVO findConsumerPrivateDetailVO(Long consumerId, String siteId) {

        return BeanMapUtils.mapTo(consumerPrivateDetailRepository.findFirstByConsumerIdAndSiteId(consumerId, siteId), ConsumerPrivateDetailVO.class);
    }

    public ConsumerPrivacyPolicyResultVO findConsumerPrivacyPolicyResultVO(Long consumerId, String siteId) {

        return BeanMapUtils.mapTo(consumerPrivacyPolicyResultRepository.findFirstByConsumerIdAndSiteId(consumerId, siteId), ConsumerPrivacyPolicyResultVO.class);
    }

    public Long getMainConsumerId(CMM010302Form form) {

        return cmmConsumerRepository.getMainConsumerId(form);
    }

    public ConsumerPrivateDetailVO findConsumerPrivateDetailByRetrieveStr(String consumerRetrieve) {
        return consumerMgr.getConsumerPrivateDetailVO(consumerRetrieve);
    }

    public Long getResultConsumerId(CMM010302Form form) {

        return consumerPrivacyPolicyResultRepository.getResultConsumerId(form);
    }

    public void updateConfirm(CmmConsumerVO cmmConsumerVO, ConsumerPrivateDetailVO consumerPrivateDetailVO, ConsumerPrivacyPolicyResultVO consumerPrivacyPolicyResultVO, List<QueueDataVO> queueDataList) {

        if (!ObjectUtils.isEmpty(cmmConsumerVO)) {
            cmmConsumerRepository.save(BeanMapUtils.mapTo(cmmConsumerVO, CmmConsumer.class));
        }

        if (!ObjectUtils.isEmpty(consumerPrivateDetailVO)) {
            consumerPrivateDetailRepository.save(BeanMapUtils.mapTo(consumerPrivateDetailVO, ConsumerPrivateDetail.class));
        }

        if (!ObjectUtils.isEmpty(consumerPrivacyPolicyResultVO)) {
            consumerPrivacyPolicyResultRepository.save(BeanMapUtils.mapTo(consumerPrivacyPolicyResultVO, ConsumerPrivacyPolicyResult.class));
        }

        if (!queueDataList.isEmpty()) {queueDataRepo.saveInBatch(BeanMapUtils.mapListTo(queueDataList, QueueData.class));}
    }

    public List<SvVinCodeTelIFBO> getAllOwnerMcByConsumerId(Long consumerId){
        return cmmConsumerSerialProRelationRepository.getAllOwnerMcByConsumerId(consumerId);
    }
}