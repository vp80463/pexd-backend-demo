package com.a1stream.service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.domain.bo.service.SVM020101BO;
import com.a1stream.domain.bo.service.SVM020102BO;
import com.a1stream.domain.bo.service.SVM0201PrintBO;
import com.a1stream.domain.bo.service.SVM0201PrintDetailBO;
import com.a1stream.domain.entity.ServiceRequest;
import com.a1stream.domain.entity.ServiceRequestBattery;
import com.a1stream.domain.entity.ServiceRequestJob;
import com.a1stream.domain.entity.ServiceRequestParts;
import com.a1stream.domain.form.service.SVM020101Form;
import com.a1stream.domain.form.service.SVM020102Form;
import com.a1stream.domain.repository.CmmConditionRepository;
import com.a1stream.domain.repository.CmmServiceDemandRepository;
import com.a1stream.domain.repository.CmmSymptomRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.a1stream.domain.repository.ServiceOrderRepository;
import com.a1stream.domain.repository.ServiceRequestBatteryRepository;
import com.a1stream.domain.repository.ServiceRequestEditHistoryRepository;
import com.a1stream.domain.repository.ServiceRequestJobRepository;
import com.a1stream.domain.repository.ServiceRequestPartsRepository;
import com.a1stream.domain.repository.ServiceRequestRepository;
import com.a1stream.domain.vo.CmmConditionVO;
import com.a1stream.domain.vo.CmmServiceDemandVO;
import com.a1stream.domain.vo.CmmSymptomVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.domain.vo.ServiceOrderVO;
import com.a1stream.domain.vo.ServiceRequestBatteryVO;
import com.a1stream.domain.vo.ServiceRequestJobVO;
import com.a1stream.domain.vo.ServiceRequestPartsVO;
import com.a1stream.domain.vo.ServiceRequestVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid1966
*/
@Service
public class SVM0201Service {

    @Resource
    private ServiceRequestRepository serviceRequestRepository;

    @Resource
    private SerializedProductRepository serializedProductRepository;

    @Resource
    private ServiceOrderRepository serviceOrderRepository;

    @Resource
    private ServiceRequestJobRepository serviceRequestJobRepository;

    @Resource
    private ServiceRequestPartsRepository serviceRequestPartsRepository;

    @Resource
    private ServiceRequestBatteryRepository serviceRequestBatteryRepository;

    @Resource
    private ServiceRequestEditHistoryRepository serviceRequestEditHistoryRepository;

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    @Resource
    private CmmSymptomRepository cmmSymptomRepository;

    @Resource
    private CmmConditionRepository cmmConditionRepository;

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private CmmServiceDemandRepository cmmServiceDemandRepository;

    public List<SVM020101BO> findServiceRequestList(SVM020101Form form) {

        return serviceRequestRepository.findServiceRequestList(form);
    }

    public List<ServiceRequestVO> getServiceRequestList(List<Long> serviceRequestIdList) {

        return BeanMapUtils.mapListTo(serviceRequestRepository.findByServiceRequestIdIn(serviceRequestIdList), ServiceRequestVO.class);
    }

    public List<ServiceRequestJobVO> getServiceRequestJobList(List<Long> serviceRequestJobIdList) {

        return BeanMapUtils.mapListTo(serviceRequestJobRepository.findByServiceRequestJobIdIn(serviceRequestJobIdList), ServiceRequestJobVO.class);
    }

    public List<ServiceRequestPartsVO> getServiceRequestPartsList(List<Long> serviceRequestPartsIdList) {

        return BeanMapUtils.mapListTo(serviceRequestPartsRepository.findByServiceRequestPartsIdIn(serviceRequestPartsIdList), ServiceRequestPartsVO.class);
    }

    public List<ServiceRequestBatteryVO> getServiceRequestBatteryList(List<Long> serviceRequestBatteryIdList) {

        return BeanMapUtils.mapListTo(serviceRequestBatteryRepository.findByServiceRequestBatteryIdIn(serviceRequestBatteryIdList), ServiceRequestBatteryVO.class);
    }

    public void updateIssue(List<ServiceRequestVO> serviceRequestVOs) {

        serviceRequestRepository.saveInBatch(BeanMapUtils.mapListTo(serviceRequestVOs, ServiceRequest.class));
    }

    public ServiceOrderVO findServiceOrderVO(SVM020101Form form) {

        return BeanMapUtils.mapTo(serviceOrderRepository.findByFacilityIdAndSiteIdAndOrderNo(form.getPointId(), form.getSiteId(), form.getServiceOrderNo()), ServiceOrderVO.class);
    }

    public SVM020102Form getInitList(SVM020102Form form, Map<String, String> codeMap) {

        SVM020102Form initForm = new SVM020102Form();
        List<SVM020102BO> processHistroyFormatList = new ArrayList<>();

        if(!form.getCategoryId().equals(ServiceCategory.FREECOUPON.getCodeDbid())) {

            initForm.setRepairJobDetailList(serviceRequestJobRepository.getRepairJobDetailList(form));
            initForm.setRepairPartsDetailList(serviceRequestPartsRepository.getRepairPartsDetailList(form));
            initForm.setRepairBatteryDetailList(serviceRequestBatteryRepository.getRepairBatteryDetailList(form));
        }

        List<SVM020102BO> processHistroyList = serviceRequestEditHistoryRepository.getProcessHistoryList(form);

        for(SVM020102BO bo : processHistroyList) {

            bo.setStatus(codeMap.get(bo.getStatus()));
            processHistroyFormatList.add(bo);
        }

        initForm.setProcessHistoryList(processHistroyFormatList);

        return initForm;
    }

    public MstFacilityVO findByFacilityId(Long pointId) {

        return BeanMapUtils.mapTo(mstFacilityRepository.findByFacilityId(pointId), MstFacilityVO.class);
    }

    public ServiceRequestVO findServiceRequestVO(Long serviceRequestId) {

        return BeanMapUtils.mapTo(serviceRequestRepository.findById(serviceRequestId), ServiceRequestVO.class);
    }

    public ServiceOrderVO findServiceOrderVO(Long serviceOrderId) {

        return BeanMapUtils.mapTo(serviceOrderRepository.findById(serviceOrderId), ServiceOrderVO.class);
    }

    public SerializedProductVO findSerializedProductVO(Long SerializedProductId) {

        return BeanMapUtils.mapTo(serializedProductRepository.findById(SerializedProductId), SerializedProductVO.class);
    }

    public CmmSymptomVO findCmmSymptomVO(Long symptomId) {

        return BeanMapUtils.mapTo(cmmSymptomRepository.findById(symptomId), CmmSymptomVO.class);
    }

    public CmmConditionVO findCmmConditionVO(Long conditionId) {

        return BeanMapUtils.mapTo(cmmConditionRepository.findById(conditionId), CmmConditionVO.class);
    }

    public MstProductVO findMstProductVO(Long mainDamagePartsId) {

        return BeanMapUtils.mapTo(mstProductRepository.findById(mainDamagePartsId), MstProductVO.class);
    }

    public void updateConfirm(ServiceRequestVO serviceRequestVO
                             ,List<ServiceRequestJobVO> jobUpdateList
                             ,List<ServiceRequestPartsVO> partsUpdateList
                             ,List<ServiceRequestBatteryVO> batteryUpdateList) {

        if (!ObjectUtils.isEmpty(serviceRequestVO)) {
            serviceRequestRepository.save(BeanMapUtils.mapTo(serviceRequestVO, ServiceRequest.class));
        }

        serviceRequestJobRepository.saveInBatch(BeanMapUtils.mapListTo(jobUpdateList, ServiceRequestJob.class));
        serviceRequestPartsRepository.saveInBatch(BeanMapUtils.mapListTo(partsUpdateList, ServiceRequestParts.class));
        serviceRequestBatteryRepository.saveInBatch(BeanMapUtils.mapListTo(batteryUpdateList, ServiceRequestBattery.class));
    }

    public List<CmmServiceDemandVO> findCmmServiceDemandVOList() {

        return BeanMapUtils.mapListTo(cmmServiceDemandRepository.findAllByOrderByBaseDateAfter(), CmmServiceDemandVO.class);
    }

    public SVM0201PrintBO getPartsClaimTagPrintHeaderData(Long serviceRequestId) {
        return serviceRequestRepository.getPartsClaimTagPrintHeaderData(serviceRequestId);
    }

    public List<SVM0201PrintDetailBO> getPartsClaimTagPrintDetailList(Long serviceRequestId){
        return serviceRequestRepository.getPartsClaimTagPrintDetailList(serviceRequestId);
    }

    public SVM0201PrintBO getPartsClaimForBatteryClaimTagPrintHeaderData(Long serviceRequestId) {
        return serviceRequestRepository.getPartsClaimForBatteryClaimTagPrintHeaderData(serviceRequestId);
    }

}