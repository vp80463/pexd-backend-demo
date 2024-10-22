package com.a1stream.service.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.common.constants.PJConstants.SettleType;
import com.a1stream.common.constants.PJConstants.SpecialClaimSymptonCondition;
import com.a1stream.common.logic.ServiceLogic;
import com.a1stream.common.manager.ConsumerManager;
import com.a1stream.common.manager.ServiceOrderManager;
import com.a1stream.common.model.ServiceJobVLBO;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.common.CmmConsumerBO;
import com.a1stream.domain.bo.service.BatteryBO;
import com.a1stream.domain.bo.service.JobDetailBO;
import com.a1stream.domain.bo.service.PartDetailBO;
import com.a1stream.domain.bo.service.SituationBO;
import com.a1stream.domain.bo.service.SpecialClaimBO;
import com.a1stream.domain.entity.ServiceOrder;
import com.a1stream.domain.entity.ServiceOrderBattery;
import com.a1stream.domain.repository.BatteryRepository;
import com.a1stream.domain.repository.CmmBatteryRepository;
import com.a1stream.domain.repository.CmmConditionRepository;
import com.a1stream.domain.repository.CmmConsumerRepository;
import com.a1stream.domain.repository.CmmRegistrationDocumentRepository;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.CmmServiceGroupItemRepository;
import com.a1stream.domain.repository.CmmSpecialClaimRepairRepository;
import com.a1stream.domain.repository.CmmSpecialClaimSerialProRepository;
import com.a1stream.domain.repository.CmmSymptomRepository;
import com.a1stream.domain.repository.CmmWarrantyBatteryRepository;
import com.a1stream.domain.repository.ConsumerPrivacyPolicyResultRepository;
import com.a1stream.domain.repository.ConsumerPrivateDetailRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.repository.ServiceAuthorizationRepository;
import com.a1stream.domain.repository.ServiceOrderBatteryRepository;
import com.a1stream.domain.repository.ServiceOrderFaultRepository;
import com.a1stream.domain.repository.ServiceOrderJobRepository;
import com.a1stream.domain.repository.ServiceOrderRepository;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmConditionVO;
import com.a1stream.domain.vo.CmmConsumerVO;
import com.a1stream.domain.vo.CmmRegistrationDocumentVO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmSpecialClaimRepairVO;
import com.a1stream.domain.vo.CmmSpecialClaimSerialProVO;
import com.a1stream.domain.vo.CmmSymptomVO;
import com.a1stream.domain.vo.CmmWarrantyBatteryVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.ServiceOrderBatteryVO;
import com.a1stream.domain.vo.ServiceOrderFaultVO;
import com.a1stream.domain.vo.ServiceOrderVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

@Service
public class OrderCmmService {

    @Resource
    private ServiceOrderRepository serviceOrderRepo;
    @Resource
    private SalesOrderRepository salesOrderRepo;
    @Resource
    private SalesOrderItemRepository salesOrderItemRepo;
    @Resource
    private ServiceOrderFaultRepository serviceOrderFaultRepo;
    @Resource
    private ServiceAuthorizationRepository serviceAuthorizationRepo;
    @Resource
    private BatteryRepository batteryRepo;
    @Resource
    private CmmBatteryRepository cmmBatteryRepo;
    @Resource
    private ServiceOrderBatteryRepository serviceOrderBatteryRepository;
    @Resource
    private CmmWarrantyBatteryRepository cmmWarrantyBatteryRepo;
    @Resource
    private CmmRegistrationDocumentRepository cmmRegistrationDocumentRepo;
    @Resource
    private ConsumerPrivacyPolicyResultRepository consumerPrivacyPolicyResultRepo;
    @Resource
    private CmmConsumerRepository cmmConsumerRepo;
    @Resource
    private ConsumerManager consumerMgr;
    @Resource
    private ServiceLogic serviceLogic;
    @Resource
    private ServiceOrderManager serviceOrderMgr;
    @Resource
    private CmmServiceGroupItemRepository cmmServiceGroupItemRepo;
    @Resource
    private CmmSymptomRepository cmmSymptomRepo;
    @Resource
    private CmmConditionRepository cmmConditionRepo;
    @Resource
    private CmmSpecialClaimRepairRepository cmmSpecialClaimRepairRepo;
    @Resource
    private ServiceOrderJobRepository serviceOrderJobRepo;
    @Resource
    private CmmSerializedProductRepository cmmSerializedProductRepo;
    @Resource
    private MstProductRepository mstProductRepo;
    @Resource
    private CmmSpecialClaimSerialProRepository cmmSpecialClaimSerialProRepo;
    @Resource
    private ConsumerPrivateDetailRepository consumerPrivateDetailRepo;

    /**
     * ServiceOrder相关
     */
    public ServiceOrderVO timelySearchOrderById(Long serviceOrderId) {

        return BeanMapUtils.mapTo(serviceOrderRepo.findByServiceOrderId(serviceOrderId), ServiceOrderVO.class);
    }

    public ServiceOrderVO timelySearchServiceOrderByIdOrNo(Long serviceOrderId, String orderNo, String siteId) {
        return BeanMapUtils.mapTo(Objects.isNull(serviceOrderId) ? serviceOrderRepo.findFirstByOrderNoAndSiteId(orderNo, siteId) : serviceOrderRepo.findById(serviceOrderId), ServiceOrderVO.class);
    }

    public ServiceOrderVO timelySearchBatteryClaimOrder(Long pointId, Long serviceOrderId, String orderNo, String siteId) {

        ServiceOrder batteryOrder;
        if (Objects.isNull(serviceOrderId)) {
            batteryOrder = serviceOrderRepo.findBatteryClaimOrder(orderNo, siteId, ServiceCategory.CLAIMBATTERY.getCodeDbid(), CommonConstants.CHAR_Y, pointId);
        } else {
            batteryOrder = serviceOrderRepo.findByServiceOrderIdAndFacilityId(serviceOrderId, pointId);
        }

        return BeanMapUtils.mapTo(batteryOrder, ServiceOrderVO.class);
    }

    /**
     * SalesOrder相关
     */
    public SalesOrderVO findSalesOrderById(Long salesOrderId) {
        return BeanMapUtils.mapTo(salesOrderRepo.findById(salesOrderId), SalesOrderVO.class);
    }

    /**
     * SalesOrderItem相关
     */
    public SalesOrderItemVO timelySearchSoItemNo0ActQty(Long salesOrderId) {

        List<SalesOrderItemVO> orderItemVoList = BeanMapUtils.mapListTo(salesOrderItemRepo.findBySalesOrderId(salesOrderId), SalesOrderItemVO.class);
        orderItemVoList = orderItemVoList.stream()
                .filter(part -> part.getActualQty().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        Optional<SalesOrderItemVO> maxSeqItem = orderItemVoList.stream().max(Comparator.comparingInt(SalesOrderItemVO::getSeqNo));
        return maxSeqItem.isPresent()? maxSeqItem.get() : null;
    }

    /**
     *
     * ServiceOrderFault相关
     */
    public List<SituationBO> timelySearchOrderFaults(Long serviceOrderId) {
        return serviceOrderMgr.searchServiceFaultByOrderId(serviceOrderId);
    }

    public List<JobDetailBO> timelySearchServiceJobByOrderId(Long serviceOrderId){
        return serviceOrderMgr.searchServiceJobByOrderId(serviceOrderId);
    }

    public List<PartDetailBO> timelySearchServicePartByOrderId(Long serviceOrderId){
        return serviceOrderMgr.searchServicePartByOrderId(serviceOrderId);
    }

    public CmmWarrantyBatteryVO findCmmWarrantyBatteryByBatteryId(Long batteryId) {
        return BeanMapUtils.mapTo(cmmWarrantyBatteryRepo.findFirstByBatteryId(batteryId), CmmWarrantyBatteryVO.class);
    }

    public List<ServiceOrderFaultVO> findFaultListByIds(List<Long> serviceOrderFaultIds) {
        return BeanMapUtils.mapListTo(serviceOrderFaultRepo.findAllById(serviceOrderFaultIds), ServiceOrderFaultVO.class);
    }

    public boolean findAuthorizationNoIsUsed(String siteId, Long serviceOrderId, List<String> authorizationNoList) {

        if (Objects.isNull(serviceOrderId)) {

            return serviceOrderFaultRepo.existsBySiteIdAndAuthorizationNoIn(siteId, authorizationNoList);
        } else {

            return serviceOrderFaultRepo.existOrderFault(siteId, authorizationNoList, serviceOrderId);
        }
    }

    /**
     *
     * ServiceAuthorization相关
     */
    public boolean findAuthorizationNoIsVaild(String siteId, Long pointId, String serializedItemNo, List<String> authorizationNoList) {

        return serviceAuthorizationRepo.existAuthorization(siteId, pointId, serializedItemNo, ComUtil.nowDate(), ComUtil.nowDate(), authorizationNoList);
    }

    /**
     *
     * Battery相关
     */
    public BatteryVO findBatteryByNo(String batteryNo, String siteId) {

        return BeanMapUtils.mapTo(batteryRepo.findFirstByBatteryNoAndSiteId(batteryNo, siteId), BatteryVO.class);
    }

    public List<BatteryBO> findServiceBatteryByMotorId(Long cmmSerializedProductId){
        return cmmBatteryRepo.listServiceBatteryByMotorId(cmmSerializedProductId);
    }

    /**
     *
     * CmmBattery相关
     */
    public CmmBatteryVO findCmmBatteryByNo(String batteryNo) {

        return BeanMapUtils.mapTo(cmmBatteryRepo.findFirstByBatteryNo(batteryNo), CmmBatteryVO.class);
    }

    /**
     * ServiceOrderBattery相关
     */
    public ServiceOrderBatteryVO timelySearchOrderBattery(Long orderId) {

        List<ServiceOrderBattery> orderBatteryList = serviceOrderBatteryRepository.findByServiceOrderIdOrderByBatteryType(orderId);

        return orderBatteryList.isEmpty() ? null : BeanMapUtils.mapTo(orderBatteryList.get(0), ServiceOrderBatteryVO.class);
    }

    /**
     * CmmRegistrationDocument相关
     */
    public CmmRegistrationDocumentVO findRegDocForBattery(Long batteryId, String siteId) {

        return BeanMapUtils.mapTo(cmmRegistrationDocumentRepo.findByBatteryIdAndSiteId(batteryId, siteId), CmmRegistrationDocumentVO.class);
    }

    /**
     * MstProduct相关
     */
    public MstProductVO findProductByCd(String productCd, String siteId) {

        return BeanMapUtils.mapTo(mstProductRepo.findBySiteIdAndProductCd(siteId, productCd), MstProductVO.class);
    }

    public MstProductVO getProductById(Long productId) {
        return BeanMapUtils.mapTo(mstProductRepo.findById(productId), MstProductVO.class);
    }

    /**
     * VO相关
     */
    public CmmSpecialClaimSerialProVO findCmmSpecialClaimSerialProBySpcialClaimIdAndMotorId(Long cmmSpecialClaimId, Long cmmSerializedProId) {
        return BeanMapUtils.mapTo(cmmSpecialClaimSerialProRepo.findFirstBySpecialClaimIdAndSerializedProductId(cmmSpecialClaimId, cmmSerializedProId), CmmSpecialClaimSerialProVO.class);
    }

    public CmmSerializedProductVO findCmmSerializedProductByFrameOrPlate(String frameNo, String plateNo) {
        return BeanMapUtils.mapTo(StringUtils.isNotBlank(frameNo) ? cmmSerializedProductRepo.findFirstByFrameNo(frameNo) : cmmSerializedProductRepo.findFirstByPlateNo(plateNo), CmmSerializedProductVO.class);
    }

    public CmmSerializedProductVO findCmmSerializedProductById(Long serializedProductId) {
        return BeanMapUtils.mapTo(cmmSerializedProductRepo.findById(serializedProductId), CmmSerializedProductVO.class);
    }

    public CmmConsumerVO findCmmConsumerById(Long consumerId) {
        return BeanMapUtils.mapTo(cmmConsumerRepo.findById(consumerId), CmmConsumerBO.class);
    }

    /**
     *
     * Consumer和隐私政策结果相关
     */
    public String getConsumerPolicyInfo(String siteId, String lastNm, String middleNm, String firstNm, String mobilePhone) {

        return consumerMgr.getConsumerPolicyInfo(siteId, lastNm, middleNm, firstNm, mobilePhone);
    }

    public CmmConsumerBO findConsumerPrivateDetailByConsumerId(Long consumerId, String siteId) {

        return consumerPrivateDetailRepo.findConsumerPrivateDetailByConsumerId(siteId, consumerId);
    }

    /**
    *
    * Campaign相关
    */
    public SpecialClaimBO findSpecialClaimRepairList(Long cmmSpecialClaimId, Long cmmSerializedProId, String modelCd, String taxPeriod) {

        SpecialClaimBO result = new SpecialClaimBO();

        //获取全部的明细:job+part
        List<CmmSpecialClaimRepairVO> cmmSpecialClaimRepairList = this.getCmmSpecialClaimRepairListByClaimId(cmmSpecialClaimId, cmmSerializedProId);

        //过滤其中的job部分,拼成valueList结果
        List<String> jobCdList = cmmSpecialClaimRepairList.stream()
                                                 .filter(repaireDtl -> StringUtils.equals(repaireDtl.getProductClassification(), ProductClsType.SERVICE.getCodeDbid()))
                                                 .map(CmmSpecialClaimRepairVO :: getProductCd)
                                                 .toList();

        if (!jobCdList.isEmpty()) {

            List<ServiceJobVLBO> serviceJobList = cmmServiceGroupItemRepo.findJobListByModel(serviceLogic.generateModelCdListForServiceGroup(modelCd), jobCdList, new ArrayList<>());

            serviceOrderMgr.setPriceValueForJobFromModelCd(SettleType.FACTORY.getCodeDbid(), taxPeriod, serviceJobList);

            result.setServiceJobList(serviceJobList);
        }

        Optional<CmmSpecialClaimRepairVO> mainDamagePart = cmmSpecialClaimRepairList.stream()
                                                                                    .filter(repaireDtl -> StringUtils.equals(repaireDtl.getProductClassification(), ProductClsType.PART.getCodeDbid()) && StringUtils.equals(repaireDtl.getMainDamagePartsFlag(), CommonConstants.CHAR_Y))
                                                                                    .findFirst();

        if (mainDamagePart.isPresent()) {

            CmmSymptomVO campaignSymptom = BeanMapUtils.mapTo(cmmSymptomRepo.findFirstBySymptomCd(SpecialClaimSymptonCondition.KEY_DEFAULT_SYMPTON), CmmSymptomVO.class);

            if (!Objects.isNull(campaignSymptom)) {

                result.setSymptomCd(campaignSymptom.getSymptomCd());
                result.setSymptomId(campaignSymptom.getSymptomId());
                result.setSymptomNm(campaignSymptom.getDescription());
                result.setSymptom(new StringBuilder().append(campaignSymptom.getSymptomCd()).append(CommonConstants.CHAR_SPACE).append(campaignSymptom.getDescription()).toString());
            }

            CmmConditionVO campaignCondition = BeanMapUtils.mapTo(cmmConditionRepo.findFirstByConditionCd(SpecialClaimSymptonCondition.KEY_DEFAULT_CONDITION), CmmConditionVO.class);

            if (!Objects.isNull(campaignCondition)) {

                result.setConditionCd(campaignCondition.getConditionCd());
                result.setConditionId(campaignCondition.getConditionId());
                result.setConditionNm(campaignCondition.getDescription());
                result.setCondition(new StringBuilder().append(campaignCondition.getConditionCd()).append(CommonConstants.CHAR_SPACE).append(campaignCondition.getDescription()).toString());
            }

            //Special Claim的parts数据一定属于666N
            result.setDamagePartCd(mainDamagePart.get().getProductCd());

            MstProductVO demagePart = this.getProductByCd(mainDamagePart.get().getProductCd(), Arrays.asList(CommonConstants.CHAR_DEFAULT_SITE_ID));

            if (!Objects.isNull(demagePart)) {

                result.setDamagePartId(demagePart.getProductId());
                result.setDamagePartNm(demagePart.getSalesDescription());
            }
        }

        return result;
    }

    private MstProductVO getProductByCd(String productCd, List<String> siteIdList) {
        return BeanMapUtils.mapTo(mstProductRepo.findFirstByProductCdAndSiteIdIn(productCd, siteIdList), MstProductVO.class);
    }

    private List<CmmSpecialClaimRepairVO> getCmmSpecialClaimRepairListByClaimId(Long cmmSpecialClaimId, Long cmmSerializedProId){

        //查询Campaign下所有的明细：part+job
        List<CmmSpecialClaimRepairVO> result = BeanMapUtils.mapListTo(cmmSpecialClaimRepairRepo.findBySpecialClaimId(cmmSpecialClaimId), CmmSpecialClaimRepairVO.class);

        //判断是否要添加pattern过滤
        CmmSpecialClaimSerialProVO cmmSpecialClaimSerialPro = this.findCmmSpecialClaimSerialProBySpcialClaimIdAndMotorId(cmmSpecialClaimId, cmmSerializedProId);

        if (!Objects.isNull(cmmSpecialClaimSerialPro) && StringUtils.isNotBlank(cmmSpecialClaimSerialPro.getRepairPattern())) {

            result = result.stream().filter(repaireDtl -> StringUtils.equals(repaireDtl.getRepairPattern(), cmmSpecialClaimSerialPro.getRepairPattern())).toList();
        }

        return result;
    }
}
