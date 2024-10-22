package com.a1stream.common.manager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.constants.PJConstants.BatteryType;
import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.common.constants.PJConstants.ServiceRequestStatus;
import com.a1stream.common.logic.ConstantsLogic;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.batch.SvServiceRequestIFBO;
import com.a1stream.domain.bo.batch.SvServiceRequestJobIFBO;
import com.a1stream.domain.bo.batch.SvServiceRequestPartIFBO;
import com.a1stream.domain.entity.QueueData;
import com.a1stream.domain.entity.ServiceRequest;
import com.a1stream.domain.entity.ServiceRequestBattery;
import com.a1stream.domain.entity.ServiceRequestEditHistory;
import com.a1stream.domain.entity.ServiceRequestJob;
import com.a1stream.domain.entity.ServiceRequestParts;
import com.a1stream.domain.repository.CmmServiceDemandRepository;
import com.a1stream.domain.repository.CmmSpecialClaimRepository;
import com.a1stream.domain.repository.QueueDataRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.ServiceOrderBatteryRepository;
import com.a1stream.domain.repository.ServiceOrderFaultRepository;
import com.a1stream.domain.repository.ServiceOrderJobRepository;
import com.a1stream.domain.repository.ServiceRequestBatteryRepository;
import com.a1stream.domain.repository.ServiceRequestEditHistoryRepository;
import com.a1stream.domain.repository.ServiceRequestJobRepository;
import com.a1stream.domain.repository.ServiceRequestPartsRepository;
import com.a1stream.domain.repository.ServiceRequestRepository;
import com.a1stream.domain.vo.CmmServiceDemandVO;
import com.a1stream.domain.vo.CmmSpecialClaimVO;
import com.a1stream.domain.vo.QueueDataVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.ServiceOrderBatteryVO;
import com.a1stream.domain.vo.ServiceOrderFaultVO;
import com.a1stream.domain.vo.ServiceOrderJobVO;
import com.a1stream.domain.vo.ServiceOrderVO;
import com.a1stream.domain.vo.ServiceRequestBatteryVO;
import com.a1stream.domain.vo.ServiceRequestEditHistoryVO;
import com.a1stream.domain.vo.ServiceRequestJobVO;
import com.a1stream.domain.vo.ServiceRequestPartsVO;
import com.a1stream.domain.vo.ServiceRequestVO;
import com.ymsl.plugins.userauth.util.JsonUtils;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

@Component
public class ServiceRequestManager {

    @Resource
    private ServiceOrderFaultRepository serviceOrderFaultRepo;
    @Resource
    private SalesOrderItemRepository salesOrderItemRepo;
    @Resource
    private ServiceOrderBatteryRepository serviceOrderBatteryRepo;
    @Resource
    private ServiceOrderJobRepository serviceOrderJobRepo;
    @Resource
    private CmmSpecialClaimRepository cmmSpecialClaimRepo;
    @Resource
    private ServiceRequestRepository serviceRequestRepo;
    @Resource
    private ServiceRequestJobRepository serviceRequestJobRepo;
    @Resource
    private ServiceRequestPartsRepository serviceRequestPartsRepo;
    @Resource
    private ServiceRequestBatteryRepository serviceRequestBatteryRepo;
    @Resource
    private ServiceRequestEditHistoryRepository serviceRequestEditHistoryRepo;
    @Resource
    private CmmServiceDemandRepository cmmServiceDemandRepo;
    @Resource
    private QueueDataRepository queueDataRepo;
    @Resource
    private GenerateNoManager generateNoMgr;
    @Resource
    private ConstantsLogic constantsLogic;

    public void generateServiceRequest(ServiceOrderVO serviceOrder) {

        List<ServiceRequestVO> serviceRequestSaveList = new ArrayList<>();
        List<ServiceRequestJobVO> serviceRequestJobSaveList = new ArrayList<>();
        List<ServiceRequestPartsVO> serviceRequestPartsSaveList = new ArrayList<>();
        List<ServiceRequestBatteryVO> serviceRequestBatterySaveList = new ArrayList<>();
        List<ServiceRequestEditHistoryVO> serviceRequestEditSaveList = new ArrayList<>();
        List<SvServiceRequestIFBO> serviceRequestIFBOList = new ArrayList<>();

        //电池Claim + 普通Claim + 特殊召回
        if (StringUtils.equals(serviceOrder.getServiceCategoryId(), ServiceCategory.CLAIMBATTERY.getCodeDbid())
                || StringUtils.equals(serviceOrder.getServiceCategoryId(), ServiceCategory.CLAIM.getCodeDbid())
                || StringUtils.equals(serviceOrder.getServiceCategoryId(), ServiceCategory.SPECIALCLAIM.getCodeDbid())) {

            this.prepareServiceRequestForClaim(serviceOrder, serviceRequestSaveList, serviceRequestJobSaveList, serviceRequestPartsSaveList, serviceRequestBatterySaveList, serviceRequestEditSaveList, serviceRequestIFBOList);
            //生成Q表待发送数据
            if (!serviceRequestIFBOList.isEmpty()) {queueDataRepo.save(BeanMapUtils.mapTo(QueueDataVO.create(serviceOrder.getSiteId(), InterfCode.OX_SV_CLAIMREQ, "service_order", serviceOrder.getServiceOrderId(), JsonUtils.toString(serviceRequestIFBOList.stream().toList())), QueueData.class));}
        }
        else if (StringUtils.equals(serviceOrder.getServiceCategoryId(), ServiceCategory.FREECOUPON.getCodeDbid())) {

            //Coupon
            this.prepareServiceRequestForCoupon(serviceOrder, serviceRequestSaveList, serviceRequestJobSaveList, serviceRequestPartsSaveList, serviceRequestEditSaveList, serviceRequestIFBOList);
            //生成Q表待发送数据
            if (!serviceRequestIFBOList.isEmpty()) {queueDataRepo.save(BeanMapUtils.mapTo(QueueDataVO.create(serviceOrder.getSiteId(), InterfCode.OX_SV_COUPONREQ, "service_order", serviceOrder.getServiceOrderId(), JsonUtils.toString(serviceRequestIFBOList.stream().toList())), QueueData.class));}
        }

        if (!serviceRequestSaveList.isEmpty()) {serviceRequestRepo.saveInBatch(BeanMapUtils.mapListTo(serviceRequestSaveList, ServiceRequest.class));}
        if (!serviceRequestJobSaveList.isEmpty()) {serviceRequestJobRepo.saveInBatch(BeanMapUtils.mapListTo(serviceRequestJobSaveList, ServiceRequestJob.class));}
        if (!serviceRequestPartsSaveList.isEmpty()) {serviceRequestPartsRepo.saveInBatch(BeanMapUtils.mapListTo(serviceRequestPartsSaveList, ServiceRequestParts.class));}
        if (!serviceRequestBatterySaveList.isEmpty()) {serviceRequestBatteryRepo.saveInBatch(BeanMapUtils.mapListTo(serviceRequestBatterySaveList, ServiceRequestBattery.class));}
        if (!serviceRequestEditSaveList.isEmpty()) {serviceRequestEditHistoryRepo.saveInBatch(BeanMapUtils.mapListTo(serviceRequestEditSaveList, ServiceRequestEditHistory.class));}
    }

    private void prepareServiceRequestForCoupon(ServiceOrderVO serviceOrder, List<ServiceRequestVO> serviceRequestSaveList, List<ServiceRequestJobVO> serviceRequestJobSaveList, List<ServiceRequestPartsVO> serviceRequestPartsSaveList, List<ServiceRequestEditHistoryVO> serviceRequestEditSaveList, List<SvServiceRequestIFBO> serviceRequestIFBOList) {

        List<ServiceOrderJobVO> jobItemList = BeanMapUtils.mapListTo(serviceOrderJobRepo.findByServiceOrderId(serviceOrder.getServiceOrderId()), ServiceOrderJobVO.class)
                                                        .stream()
                                                        .filter(job -> StringUtils.equals(job.getServiceCategoryId(), ServiceCategory.FREECOUPON.getCodeDbid()))
                                                        .toList();

        List<SalesOrderItemVO> partItemList = BeanMapUtils.mapListTo(salesOrderItemRepo.findBySalesOrderId(serviceOrder.getRelatedSalesOrderId()), SalesOrderItemVO.class)
                                                        .stream()
                                                        .filter(part -> part.getActualQty().compareTo(BigDecimal.ZERO) > 0 && StringUtils.equals(part.getServiceCategoryId(), ServiceCategory.FREECOUPON.getCodeDbid()))
                                                        .toList();

        BigDecimal partAmt = BigDecimal.ZERO;
        BigDecimal jobAmt = BigDecimal.ZERO;

        ServiceRequestVO serviceRequest = ServiceRequestVO.create();

        serviceRequest.setSiteId(serviceOrder.getSiteId());
        serviceRequest.setRequestDealerCd(serviceOrder.getSiteId());
        serviceRequest.setRequestNo(generateNoMgr.generateServiceRequestNo(serviceOrder.getSiteId(), serviceOrder.getFacilityId()));
        serviceRequest.setRequestDate(ComUtil.nowLocalDate());
        serviceRequest.setRequestType(serviceOrder.getServiceCategoryId());
        serviceRequest.setRequestStatus(ServiceRequestStatus.NEW.getCodeDbid());
        serviceRequest.setSerializedProductId(serviceOrder.getCmmSerializedProductId());
        serviceRequest.setSerializedItemNo(serviceOrder.getFrameNo());
        serviceRequest.setSoldDate(serviceOrder.getSoldDate());
        serviceRequest.setServiceOrderId(serviceOrder.getServiceOrderId());
        serviceRequest.setServiceDate(serviceOrder.getSettleDate());
        serviceRequest.setExpiryDate(CommonConstants.MAX_DATE);
        serviceRequest.setRequestFacilityId(serviceOrder.getFacilityId());
        serviceRequest.setMileage(serviceOrder.getMileage());
        serviceRequest.setServiceDemandId(serviceOrder.getServiceDemandId());

        for (ServiceOrderJobVO jobItem : jobItemList) {

            jobAmt = jobAmt.add(jobItem.getSellingPrice());

            serviceRequestJobSaveList.add(this.prepareServiceRequestJob(serviceRequest, jobItem));
        }

        for (SalesOrderItemVO partItem : partItemList) {

            partAmt = partAmt.add(partItem.getActualAmtNotVat());

            serviceRequestPartsSaveList.add(this.prepareServiceRequestParts(serviceRequest, partItem));
        }

        //赋值金额合计
        serviceRequest.setPaymentJobAmt(jobAmt);
        serviceRequest.setPaymentPartsAmt(partAmt);
        serviceRequest.setPaymentTotalAmt(jobAmt.add(partAmt));

        //生成履历
        serviceRequestEditSaveList.add(this.prepareServiceRequestEditHistory(serviceRequest, serviceOrder.getCashierId(), serviceOrder.getCashierCd(), serviceOrder.getCashierNm()));

        serviceRequestSaveList.add(serviceRequest);

        //生成Q表待发送数据
        SvServiceRequestIFBO requestBO = new SvServiceRequestIFBO();

        requestBO.setApplicationNo(serviceRequest.getRequestNo());
        requestBO.setApplicationPointCode(serviceOrder.getFacilityCd());
        requestBO.setApplicationDealerCode(serviceOrder.getSiteId());
        requestBO.setFrameNo(serviceRequest.getSerializedItemNo());
        requestBO.setMileage(serviceRequest.getMileage().toString());
        requestBO.setServiceCompletionDate(serviceRequest.getServiceDate());
        requestBO.setSalesDate(serviceRequest.getSoldDate());
        requestBO.setCouponApplicationNo(serviceRequest.getRequestNo());
        requestBO.setCouponServiceDate(serviceRequest.getServiceDate());
        requestBO.setCouponLevel(this.getCouponLevel(serviceRequest));

        serviceRequestIFBOList.add(requestBO);
    }

    private String getCouponLevel(ServiceRequestVO serviceRequest) {

        String result = CommonConstants.CHAR_ZERO;

        if (!Objects.isNull(serviceRequest.getServiceDemandId())) {

            Integer coupon = 0;

            List<CmmServiceDemandVO> serviceDemandList = BeanMapUtils.mapListTo(cmmServiceDemandRepo.findByServiceCategory(ServiceCategory.FREECOUPON.getCodeDbid()), CmmServiceDemandVO.class)
                                                                     .stream()
                                                                     .sorted(Comparator.comparingInt(CmmServiceDemandVO::getBaseDateAfter))
                                                                     .toList();

            for (CmmServiceDemandVO cmmServiceDemand : serviceDemandList) {

                if (serviceRequest.getServiceDemandId().equals(cmmServiceDemand.getServiceDemandId())) {

                    coupon = serviceDemandList.indexOf(cmmServiceDemand) + 1;
                    break;
                }
            }

            result = coupon.toString();
        }

        return result;
    }

    private void prepareServiceRequestForClaim(ServiceOrderVO serviceOrder, List<ServiceRequestVO> serviceRequestSaveList, List<ServiceRequestJobVO> serviceRequestJobSaveList, List<ServiceRequestPartsVO> serviceRequestPartsSaveList, List<ServiceRequestBatteryVO> serviceRequestBatterySaveList, List<ServiceRequestEditHistoryVO> serviceRequestEditSaveList, List<SvServiceRequestIFBO> serviceRequestIFBOList) {

        List<ServiceOrderFaultVO> faultList = BeanMapUtils.mapListTo(serviceOrderFaultRepo.findByServiceOrderId(serviceOrder.getServiceOrderId()), ServiceOrderFaultVO.class);

        Map<Long, List<ServiceOrderJobVO>> jobItemMap = BeanMapUtils.mapListTo(serviceOrderJobRepo.findByServiceOrderId(serviceOrder.getServiceOrderId()), ServiceOrderJobVO.class)
                                                            .stream()
                                                            .filter(job -> !Objects.isNull(job.getServiceOrderFaultId()))
                                                            .collect(Collectors.groupingBy(ServiceOrderJobVO::getServiceOrderFaultId));

        Map<Long, List<SalesOrderItemVO>> partItemMap = BeanMapUtils.mapListTo(salesOrderItemRepo.findBySalesOrderId(serviceOrder.getRelatedSalesOrderId()), SalesOrderItemVO.class)
                                                                .stream()
                                                                .filter(part -> part.getActualQty().compareTo(BigDecimal.ZERO) > 0 && !Objects.isNull(part.getServiceOrderFaultId()))
                                                                .collect(Collectors.groupingBy(SalesOrderItemVO::getServiceOrderFaultId));

        Map<String, ServiceOrderBatteryVO> batteryItemMap = BeanMapUtils.mapListTo(serviceOrderBatteryRepo.findByServiceOrderIdOrderByBatteryType(serviceOrder.getRelatedSalesOrderId()), ServiceOrderBatteryVO.class)
                                                              .stream()
                                                              .filter(battery -> StringUtils.isNotBlank(battery.getNewBatteryNo()) && StringUtils.isNotBlank(battery.getBatteryType()))
                                                              .collect(Collectors.toMap(ServiceOrderBatteryVO::getBatteryType, value -> value));

        BigDecimal partAmt;
        BigDecimal jobAmt;

        for (ServiceOrderFaultVO faultInfo : faultList) {

            if (!StringUtils.equals(faultInfo.getWarrantyClaimFlag(), CommonConstants.CHAR_Y)) {continue;}

            partAmt = BigDecimal.ZERO;
            jobAmt = BigDecimal.ZERO;

            //每一条WarrantyClaimFlag=Y的Fault都会生成一笔ServiceRequest
            ServiceRequestVO serviceRequest = ServiceRequestVO.create();

            serviceRequest.setSiteId(serviceOrder.getSiteId());
            serviceRequest.setRequestDealerCd(serviceOrder.getSiteId());
            serviceRequest.setRequestNo(generateNoMgr.generateServiceRequestNo(serviceOrder.getSiteId(), serviceOrder.getFacilityId()));
            serviceRequest.setRequestDate(ComUtil.nowLocalDate());
            serviceRequest.setRequestType(serviceOrder.getServiceCategoryId());
            serviceRequest.setRequestStatus(ServiceRequestStatus.NEW.getCodeDbid());
            serviceRequest.setSerializedProductId(serviceOrder.getCmmSerializedProductId());
            serviceRequest.setSerializedItemNo(serviceOrder.getFrameNo());
            serviceRequest.setSoldDate(serviceOrder.getSoldDate());
            serviceRequest.setServiceOrderId(serviceOrder.getServiceOrderId());
            serviceRequest.setServiceDate(serviceOrder.getSettleDate());
            serviceRequest.setExpiryDate(CommonConstants.MAX_DATE);
            serviceRequest.setRequestFacilityId(serviceOrder.getFacilityId());
            serviceRequest.setSituationHappenDate(faultInfo.getFaultStartDate());
            serviceRequest.setMileage(serviceOrder.getMileage());
            serviceRequest.setSymptomId(faultInfo.getSymptomId());
            serviceRequest.setConditionId(faultInfo.getConditionId());
            serviceRequest.setProblemComment(faultInfo.getProcessComment());
            serviceRequest.setReasonComment(faultInfo.getConditionComment());
            serviceRequest.setRepairComment(faultInfo.getRepairDescription());
            serviceRequest.setShopComment(faultInfo.getDealerComment());
            serviceRequest.setMainDamagePartsId(faultInfo.getProductId());
            serviceRequest.setAuthorizationNo(faultInfo.getAuthorizationNo());
            serviceRequest.setServiceOrderFaultId(faultInfo.getServiceOrderFaultId());
            serviceRequest.setCampaignNo(Objects.isNull(serviceOrder.getCmmSpecialClaimId()) ? CommonConstants.CHAR_BLANK : BeanMapUtils.mapTo(cmmSpecialClaimRepo.findById(serviceOrder.getCmmSpecialClaimId()), CmmSpecialClaimVO.class).getCampaignNo());
            serviceRequest.setBulletinNo(serviceOrder.getBulletinNo());

            //每一个ServiceRequest同步生成Q表body
            SvServiceRequestIFBO requestBO = new SvServiceRequestIFBO();

            requestBO.setApplicationNo(serviceRequest.getRequestNo());
            requestBO.setApplicationPointCode(serviceOrder.getFacilityCd());
            requestBO.setApplicationDealerCode(serviceOrder.getSiteId());
            requestBO.setFrameNo(serviceRequest.getSerializedItemNo());
            requestBO.setMileage(serviceRequest.getMileage().toString());
            requestBO.setServiceCompletionDate(serviceRequest.getServiceDate());
            requestBO.setSalesDate(serviceRequest.getSoldDate());

            requestBO.setApplicationDate(serviceRequest.getRequestDate());
            requestBO.setAuthorizationNo(serviceRequest.getAuthorizationNo());
            requestBO.setSymptomCode(faultInfo.getSymptomCd());
            requestBO.setConditionCode(faultInfo.getConditionCd());
            requestBO.setProblemDescription(serviceRequest.getProblemComment());
            requestBO.setCauseDescription(serviceRequest.getReasonComment());
            requestBO.setRepairDescription(serviceRequest.getRepairComment());
            requestBO.setDealerComment(serviceRequest.getShopComment());
            requestBO.setPrimaryFailedPartNo(faultInfo.getProductCd());
            requestBO.setFailureDate(serviceRequest.getSituationHappenDate());
            requestBO.setCampaignNumber(serviceRequest.getCampaignNo());
            requestBO.setClaimType(constantsLogic.getConstantsByCodeDbId(ServiceCategory.class.getDeclaredFields(), serviceRequest.getRequestType()).getCodeData3());

            //归属当前Fault的Job明细
            for (ServiceOrderJobVO jobItem : jobItemMap.get(faultInfo.getServiceOrderFaultId())) {

                serviceRequestJobSaveList.add(this.prepareServiceRequestJob(serviceRequest, jobItem));
                requestBO.getJobDetail().add(this.prepareSerivceRequestIFJob(jobItem));

                jobAmt = jobAmt.add(jobItem.getSellingPrice());
            }

            //归属当前Fault的Part明细 和 对应电池
            for (SalesOrderItemVO partItem : partItemMap.get(faultInfo.getServiceOrderFaultId())) {

                serviceRequestPartsSaveList.add(this.prepareServiceRequestParts(serviceRequest, partItem));
                requestBO.getPartsDetail().add(this.prepareServiceRequestIFParts(partItem));

                partAmt = partAmt.add(partItem.getActualAmtNotVat());

                if (StringUtils.isNotBlank(partItem.getBatteryType())
                        && batteryItemMap.containsKey(partItem.getBatteryType())) {

                    serviceRequestBatterySaveList.add(this.prepareServiceRequestBattery(serviceRequest, batteryItemMap.get(partItem.getBatteryType())));
                    this.prepareSerivceRequestIFBattery(requestBO, batteryItemMap.get(partItem.getBatteryType()));
                }
            }

            //赋值金额合计
            serviceRequest.setPaymentJobAmt(jobAmt);
            serviceRequest.setPaymentPartsAmt(partAmt);
            serviceRequest.setPaymentTotalAmt(jobAmt.add(partAmt));

            //生成履历
            serviceRequestEditSaveList.add(this.prepareServiceRequestEditHistory(serviceRequest, serviceOrder.getCashierId(), serviceOrder.getCashierCd(), serviceOrder.getCashierNm()));

            serviceRequestSaveList.add(serviceRequest);
            serviceRequestIFBOList.add(requestBO);
        }
    }

    private ServiceRequestBatteryVO prepareServiceRequestBattery(ServiceRequestVO serviceRequest, ServiceOrderBatteryVO batteryItem) {

        ServiceRequestBatteryVO result = new ServiceRequestBatteryVO();

        result.setSiteId(serviceRequest.getSiteId());
        result.setServiceRequestId(serviceRequest.getServiceRequestId());
        result.setServiceOrderBatteryId(batteryItem.getServiceOrderBatteryId());
        result.setBatteryProductId(batteryItem.getNewProductId());
        result.setBatteryCd(batteryItem.getNewProductCd());
        result.setBatteryNo(batteryItem.getNewBatteryNo());
        result.setBatteryId(batteryItem.getNewBatteryId());
        result.setUsedQty(BigDecimal.ONE);
        result.setSelectFlag(CommonConstants.CHAR_Y);

        return result;
    }

    private void prepareSerivceRequestIFBattery(SvServiceRequestIFBO requestBO, ServiceOrderBatteryVO batteryItem) {

        if (StringUtils.equals(batteryItem.getBatteryType(), BatteryType.TYPE1.getCodeDbid())) {

            requestBO.setOriginalBatteryId1(batteryItem.getBatteryNo());
            requestBO.setNewBatteryId1(batteryItem.getNewBatteryNo());

            //if not EV ,set FrameNo is batteryID
            requestBO.setFrameNo(StringUtils.isBlank(requestBO.getFrameNo()) ? batteryItem.getBatteryNo() : requestBO.getFrameNo());
        }
        else if (StringUtils.equals(batteryItem.getBatteryType(), BatteryType.TYPE2.getCodeDbid())) {

            requestBO.setOriginalBatteryId2(batteryItem.getBatteryNo());
            requestBO.setNewBatteryId2(batteryItem.getNewBatteryNo());
        }
    }

    private ServiceRequestJobVO prepareServiceRequestJob(ServiceRequestVO serviceRequest, ServiceOrderJobVO jobItem) {

        ServiceRequestJobVO result = new ServiceRequestJobVO();

        result.setSiteId(serviceRequest.getSiteId());
        result.setServiceRequestId(serviceRequest.getServiceRequestId());
        result.setServiceOrderJobId(jobItem.getServiceOrderJobId());
        result.setJobId(jobItem.getJobId());
        result.setStdManhour(jobItem.getStdManhour());
        result.setSelectFlag(CommonConstants.CHAR_Y);

        return result;
    }

    private SvServiceRequestJobIFBO prepareSerivceRequestIFJob(ServiceOrderJobVO jobItem) {

        SvServiceRequestJobIFBO result = new SvServiceRequestJobIFBO();

        result.setJobCode(jobItem.getJobCd());

        return result;
    }

    private ServiceRequestPartsVO prepareServiceRequestParts(ServiceRequestVO serviceRequest, SalesOrderItemVO partItem) {

        ServiceRequestPartsVO result = new ServiceRequestPartsVO();

        result.setSiteId(serviceRequest.getSiteId());
        result.setServiceRequestId(serviceRequest.getServiceRequestId());
        result.setServiceOrderPartsId(partItem.getSalesOrderItemId());
        result.setProductId(partItem.getAllocatedProductId());
        result.setUsedQty(partItem.getActualQty());
        result.setSelectFlag(CommonConstants.CHAR_Y);

        return result;
    }

    private SvServiceRequestPartIFBO prepareServiceRequestIFParts(SalesOrderItemVO partItem) {

        SvServiceRequestPartIFBO result = new SvServiceRequestPartIFBO();

        result.setExchangePartNo(partItem.getAllocatedProductCd());
        result.setExchangePartQuantity(partItem.getActualQty());

        return result;
    }

    private ServiceRequestEditHistoryVO prepareServiceRequestEditHistory(ServiceRequestVO serviceRequest, Long picId, String picCd, String picNm) {

        ServiceRequestEditHistoryVO result = new ServiceRequestEditHistoryVO();

        result.setSiteId(serviceRequest.getSiteId());
        result.setServiceRequestId(serviceRequest.getServiceRequestId());
        result.setRequestStatus(serviceRequest.getRequestStatus());
        result.setChangeDate(LocalDateTime.now());
        result.setReportPicId(picId);
        result.setReportPicCd(picCd);
        result.setReportPicNm(picNm);

        return result;
    }
}