package com.a1stream.service.facade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.SdStockStatus;
import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.common.constants.PJConstants.ServiceOrderStatus;
import com.a1stream.common.constants.PJConstants.SettleType;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.common.logic.ConstantsLogic;
import com.a1stream.common.logic.ConsumerLogic;
import com.a1stream.common.logic.InventoryLogic;
import com.a1stream.common.logic.ValidateLogic;
import com.a1stream.common.model.BaseTableData;
import com.a1stream.common.model.ConstantsBO;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.common.CmmConsumerBO;
import com.a1stream.domain.bo.service.BatteryBO;
import com.a1stream.domain.bo.service.JobDetailBO;
import com.a1stream.domain.bo.service.PartDetailBO;
import com.a1stream.domain.bo.service.SituationBO;
import com.a1stream.domain.bo.service.SpecialClaimBO;
import com.a1stream.domain.bo.service.SummaryBO;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmConsumerVO;
import com.a1stream.domain.vo.CmmRegistrationDocumentVO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmSpecialClaimSerialProVO;
import com.a1stream.domain.vo.CmmWarrantyBatteryVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.ServiceOrderBatteryVO;
import com.a1stream.domain.vo.ServiceOrderFaultVO;
import com.a1stream.domain.vo.ServiceOrderVO;
import com.a1stream.service.service.OrderCmmService;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;

import jakarta.annotation.Resource;
import jodd.util.StringUtil;
import software.amazon.awssdk.utils.StringUtils;

/**
* 功能描述:Service Order 通用的逻辑
*
* @author mid1341
*/
@Component
public class OrderCmmMethod {

    @Resource
    private OrderCmmService orderService;

    @Resource
    private ValidateLogic validateLogic;

    @Resource
    private ConsumerLogic consumerLogic;

    @Resource
    private InventoryLogic inventoryLogic;

    @Resource
    private ConstantsLogic constantsLogic;
    /**
     * 校验 区域
     */
    public ServiceOrderVO orderExistence(Long serviceOrderId, String orderNo, String updateCounter) {

        //更新时，进行DB验证
        ServiceOrderVO serviceOrder = orderService.timelySearchOrderById(serviceOrderId);

        //在DB中不存在时
        if (serviceOrder == null) {

            throw new BusinessCodedException(ComUtil.t("M.E.10237", new String[] {orderNo, ComUtil.t("title.serviceOrderNewSOP_01")}));
        }
        //多用户操作 或 状态不正确时
        if (!StringUtil.equals(serviceOrder.getUpdateCount().toString(), updateCounter)
                || !StringUtils.equals(serviceOrder.getOrderStatusId(), ServiceOrderStatus.WAITFORSETTLE.getCodeDbid())){

            throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[] {ComUtil.t("label.serviceOrderNo"), orderNo, ComUtil.t("title.serviceOrderNewSOP_01")}));
        }

        return serviceOrder;
    }

    public CmmBatteryVO cmmBatteryExistence(String batteryNo, String siteId) {

        CmmBatteryVO cmmBattery = findCmmBatteryByNo(batteryNo);
        if (cmmBattery == null) {
            throw new BusinessCodedException(ComUtil.t("M.E.00303", new String[] { ComUtil.t("label.batteryId"), batteryNo, ComUtil.t("label.tableCmmBatteryInfo") }));
        }
        if (!StringUtils.equals(cmmBattery.getBatteryStatus(), SdStockStatus.SHIPPED.getCodeDbid())) {
            throw new BusinessCodedException(ComUtil.t("M.E.10255"));
        }

        return cmmBattery;
    }

    public List<SituationBO> validateSituationDetail(Long serviceOrderId, BaseTableData<SituationBO> situations, String soldDate) {

        List<SituationBO> resultFaultList = situations.getNewUpdateRecords();
        for (SituationBO situation : resultFaultList) {

            validateLogic.validateEntityNotExist(situation.getSymptomCd(), situation.getSymptomId(), ComUtil.t("label.symptom"));
            validateLogic.validateEntityNotExist(situation.getConditionCd(), situation.getConditionId(), ComUtil.t("label.condition"));
            validateLogic.validateEntityNotExist(situation.getProductCd(), situation.getProductId(), ComUtil.t("label.parts"));

            validateFaultStartDate(situation.getFaultStartDate(), soldDate);

            if (StringUtils.equals(situation.getWarrantyClaimFlag(), CommonConstants.CHAR_Y)) {
                validateLogic.validateIsRequired(situation.getSymptomId(), ComUtil.t("label.symptom"));
                validateLogic.validateIsRequired(situation.getConditionId(), ComUtil.t("label.condition"));
                validateLogic.validateIsRequired(situation.getFaultStartDate(), ComUtil.t("label.faultStartDate"));
                validateLogic.validateIsRequired(situation.getProductId(), ComUtil.t("label.mainDamageParts"));
                validateLogic.validateIsRequired(situation.getRepairDescription(), ComUtil.t("label.repairDescription"));
                validateLogic.validateIsRequired(situation.getSymptomComment(), ComUtil.t("label.symptomComment"));
                validateLogic.validateIsRequired(situation.getConditionComment(), ComUtil.t("label.conditionComment"));
                validateLogic.validateIsRequired(situation.getProcessComment(), ComUtil.t("label.processComment"));
            }
        }

        return validateDuplicateSituationDetail(serviceOrderId, situations);
    }

    public List<JobDetailBO> validateJobDetail(Long serviceOrderId, BaseTableData<JobDetailBO> jobs) {

        //存在和必入力验证
        for (JobDetailBO job : jobs.getNewUpdateRecords()) {

            //验证VL存在性
            if (StringUtils.equals(job.getServiceCategoryId(), ServiceCategory.FREECOUPON.getCodeDbid())) {
                validateLogic.validateEntityNotExist(job.getServiceDemandContent(), job.getServiceDemandId(), ComUtil.t("label.serviceDemand"));
            }
            validateLogic.validateEntityNotExist(job.getJobCd(), job.getJobId(), ComUtil.t("label.job"));

            //验证特殊条件
            this.validateSymptomInDetail(job.getServiceCategoryId(), job.getSymptomId());
        }
        //重复数据验证
        return this.validateDuplicateJobDetail(serviceOrderId, jobs);
    }

    public List<PartDetailBO> validatePartDetail(Long serviceOrderId, BaseTableData<PartDetailBO> parts, String evFlag) {

        //存在和必入力验证
        for (PartDetailBO part : parts.getNewUpdateRecords()) {

            //验证VL存在性
            if (StringUtils.equals(part.getServiceCategoryId(), ServiceCategory.FREECOUPON.getCodeDbid())) {
                validateLogic.validateEntityNotExist(part.getServiceDemandContent(), part.getServiceDemandId(), ComUtil.t("label.serviceDemand"));
            }
            validateLogic.validateEntityNotExist(part.getPartCd(), part.getPartId(), ComUtil.t("label.part"));

            //验证特殊条件
            this.validateSymptomInDetail(part.getServiceCategoryId(), part.getSymptomId());
            this.validateBatteryPart(part, evFlag);
        }
        //重复数据验证
        return this.validateDuplicatePartDetail(serviceOrderId, parts);
    }

    public void validateBatteryDetail(String evFlag, List<BatteryBO> batterys) {

        //当非电车 或 battery明细没有数据时，跳过此验证
        if (StringUtils.equals(evFlag, CommonConstants.CHAR_N) || batterys.isEmpty()) {return;}

        for (BatteryBO battery : batterys) {
            //newPartCd为空，标识不需要更换电池，跳过后续验证
            if (StringUtils.isBlank(battery.getNewPartCd())) {continue;}
            //验证特殊条件
            this.validateWarrantyStartDate(battery);
            this.validateBatteryWarrantyDatePeriod(battery);
        }
        //重复数据验证
        this.validateDuplicateBatteryDetail(batterys);
    }

    public void validateAuthorizationNoExistence(List<SituationBO> resultFaultList, String warrantyTerm) {

        if(!resultFaultList.isEmpty()) {
            String authorizationNo = resultFaultList.get(0).getAuthorizationNo();
            if(StringUtils.isBlank(authorizationNo)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10276", new String[] { warrantyTerm }));
            }
        } else {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10276", new String[] { warrantyTerm }));
        }
    }

    public void validateAuthorizationNo(BaseTableData<SituationBO> situations, String siteId, Long pointId,
            String serializedItemNo, Long serviceOrderId) {

        if (situations.getNewUpdateRecords().isEmpty()) {return;}

        //获取填写的AuthorizationNo
        List<String> authorizationNoList = situations.getNewUpdateRecords().stream()
                .filter(situation -> StringUtils.isNotBlank(situation.getAuthorizationNo()))
                .map(SituationBO::getAuthorizationNo).distinct().toList();

        if (authorizationNoList.isEmpty()) {return;}

        //如果AuthorizationNo在mst表中不存在，则报错
        boolean existAuthInfo = orderService.findAuthorizationNoIsVaild(siteId, pointId, serializedItemNo, authorizationNoList);
        if (!existAuthInfo) {
            throw new BusinessCodedException(ComUtil.t("M.E.10213"));
        }

        //如果AuthorizationNo在别的service中使用过
        if (orderService.findAuthorizationNoIsUsed(siteId, serviceOrderId, authorizationNoList)) {
            throw new BusinessCodedException(ComUtil.t("M.E.10213"));
        }
    }

    public void validateOperationTime(String startTime, String operationStart, String operationFinish, String action) {

        //start时间不得晚于finish时间
        if(!Objects.isNull(operationStart)
            && !Objects.isNull(operationFinish)
            && operationStart.compareTo(operationFinish) > 0) {

            throw new BusinessCodedException(ComUtil.t("M.E.00206", new String[] {ComUtil.t("label.operationStartDate"), ComUtil.t("label.operationFinishDate")}));
        }

        //Settle验证内容
        if (StringUtils.equals(action, CommonConstants.OPERATION_STATUS_FINISH)) {

            if (startTime.compareTo(operationStart) > 0) {

                throw new BusinessCodedException(ComUtil.t("M.E.00205", new String[] { ComUtil.t("label.operationStartDate"), ComUtil.str2DateTimeStr(startTime) }));
            }

            if (LocalDateTime.now().compareTo(ComUtil.str2DateTime(operationFinish)) < 0) {

                throw new BusinessCodedException(ComUtil.t("M.E.00205", new String[] {ComUtil.t("label.operationFinishDate"), ComUtil.t("label.currentTime")}));
            }
        }
    }

    public void validateMobilePhone(String mobilephone) {

        if (StringUtils.isNotBlank(mobilephone)) {
            if (mobilephone.length() != 10) {

                throw new BusinessCodedException(ComUtil.t("M.E.10322", new String[] {ComUtil.t("label.mobilePhone"), "10"}));
            }
        }
    }

    public void validateEmail(String email) {
        validateLogic.validateEmail(email);
    }

    /**
     * 组装VO 区域
     */
    public List<ServiceOrderFaultVO> prepareNewOrUpdateFaultList(BaseTableData<SituationBO> situations, String siteId, Long serviceOrderId) {

        List<ServiceOrderFaultVO> saveList = new ArrayList<>();

        //处理新增行
        for (SituationBO situation : situations.getInsertRecords()) {

            ServiceOrderFaultVO serviceOrderFault = ServiceOrderFaultVO.create(siteId, serviceOrderId);
            buildServiceOrderFaultVO(serviceOrderFault, situation);

            saveList.add(serviceOrderFault);
        }

        //处理修改行
        Map<Long, ServiceOrderFaultVO> situationInDBMap;
        if (situations.getUpdateRecords().isEmpty()) {
            situationInDBMap = new HashMap<>();
        } else {
            List<Long> faultIds = situations.getUpdateRecords().stream().map(SituationBO::getServiceOrderFaultId).toList();
            situationInDBMap = orderService.findFaultListByIds(faultIds).stream()
                    .collect(Collectors.toMap(ServiceOrderFaultVO::getServiceOrderFaultId, value -> value));
        }

         for (SituationBO situation : situations.getUpdateRecords()) {

             ServiceOrderFaultVO serviceOrderFault = situationInDBMap.get(situation.getServiceOrderFaultId());

            if (!Objects.isNull(serviceOrderFault)) {

                buildServiceOrderFaultVO(serviceOrderFault, situation);

                saveList.add(serviceOrderFault);
            }
        }

        return saveList;
    }

    public void doPartDetailDelete(String siteId, Long pointId,
            Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap, SalesOrderItemVO servicePart) {

        servicePart.setCancelQty(servicePart.getCancelQty().add(servicePart.getActualQty()));
        servicePart.setActualQty(BigDecimal.ZERO);
        servicePart.setWaitingAllocateQty(BigDecimal.ZERO);

        if (servicePart.getBoQty().compareTo(BigDecimal.ZERO) > 0) {

            inventoryLogic.generateStockStatusVOMap(siteId, pointId, servicePart.getAllocatedProductId(),
                    servicePart.getBoQty().negate(), SpStockStatus.BO_QTY.getCodeDbid(), stockStatusVOChangeMap);
            servicePart.setBoQty(BigDecimal.ZERO);
        }

        if (servicePart.getAllocatedQty().compareTo(BigDecimal.ZERO) > 0) {

            inventoryLogic.generateStockStatusVOMap(siteId, pointId, servicePart.getAllocatedProductId(),
                    servicePart.getAllocatedQty().negate(), SpStockStatus.ALLOCATED_QTY.getCodeDbid(),
                    stockStatusVOChangeMap);
            inventoryLogic.generateStockStatusVOMap(siteId, pointId, servicePart.getAllocatedProductId(),
                    servicePart.getAllocatedQty(), SpStockStatus.ONHAND_QTY.getCodeDbid(), stockStatusVOChangeMap);
            servicePart.setAllocatedQty(BigDecimal.ZERO);
        }
    }

    public Map<String, Map<Long, ProductStockStatusVO>> prepareServiceOrderPartListForCancel(List<SalesOrderItemVO> salesOrderItemList, String siteId, Long pointId){

        Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = new HashMap<>();

        // 参数中salesOrderItemList已符合该条件的记录
//        List<SalesOrderItemVO> saveList = salesOrderItemList.stream()
//                                                        .filter(part -> part.getActualQty().compareTo(BigDecimal.ZERO) > 0)
//                                                        .toList();
        for (SalesOrderItemVO part : salesOrderItemList) {

            if (part.getWaitingAllocateQty().compareTo(BigDecimal.ZERO) > 0) {
                part.setCancelQty(part.getCancelQty().add(part.getWaitingAllocateQty()));
                part.setWaitingAllocateQty(BigDecimal.ZERO);
            }

            if (part.getBoQty().compareTo(BigDecimal.ZERO) > 0) {
                inventoryLogic.generateStockStatusVOMap(siteId, pointId, part.getAllocatedProductId(), part.getBoQty().negate(), SpStockStatus.BO_QTY.getCodeDbid(), stockStatusVOChangeMap);
                part.setCancelQty(part.getCancelQty().add(part.getBoQty()));
                part.setBoQty(BigDecimal.ZERO);
            }
            if (part.getAllocatedQty().compareTo(BigDecimal.ZERO) > 0) {
                inventoryLogic.generateStockStatusVOMap(siteId, pointId, part.getAllocatedProductId(), part.getAllocatedQty().negate(), SpStockStatus.ALLOCATED_QTY.getCodeDbid(), stockStatusVOChangeMap);
                inventoryLogic.generateStockStatusVOMap(siteId, pointId, part.getAllocatedProductId(), part.getAllocatedQty(), SpStockStatus.ONHAND_QTY.getCodeDbid(), stockStatusVOChangeMap);
                part.setCancelQty(part.getCancelQty().add(part.getAllocatedQty()));
                part.setAllocatedQty(BigDecimal.ZERO);
            }
        }

        // 在画面逻辑中设值到ParamModel
//      para.setPartListForSave(saveList);
//      para.setStockStatusVOChangeMap(stockStatusVOChangeMap);

        return stockStatusVOChangeMap;
    }

    /**
     *
     * Consumer和隐私政策结果相关
     */
    public String getConsumerFullNm(String lastNm, String middleNm, String firstNm) {

        return consumerLogic.getConsumerFullNm(lastNm, middleNm, firstNm);
    }



    public String getConsumerPolicyInfo(String siteId, String lastNm, String middleNm, String firstNm, String mobilePhone) {

        return orderService.getConsumerPolicyInfo(siteId, lastNm, middleNm, firstNm, mobilePhone);
    }

    public CmmConsumerBO findConsumerPrivateDetailByConsumerId(Long consumerId, String siteId) {

        return orderService.findConsumerPrivateDetailByConsumerId(consumerId, siteId);
    }

    /**
     * 组装BO 区域
     */
    public List<SummaryBO> generateSummaryList(List<JobDetailBO> jobList, List<PartDetailBO> partList){

        Map<String, BigDecimal> jobSummary = jobList.stream().collect(Collectors.groupingBy(JobDetailBO::getSettleTypeId, Collectors.mapping(JobDetailBO::getSellingPrice, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
        Map<String, BigDecimal> partSummary = partList.stream().filter(detail -> !StringUtils.equals(detail.getBatteryFlag(), CommonConstants.CHAR_Y)).collect(Collectors.groupingBy(PartDetailBO::getSettleTypeId, Collectors.mapping(PartDetailBO::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
        Map<String, BigDecimal> batterySummary = partList.stream().filter(detail -> StringUtils.equals(detail.getBatteryFlag(), CommonConstants.CHAR_Y)).collect(Collectors.groupingBy(PartDetailBO::getSettleTypeId, Collectors.mapping(PartDetailBO::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        List<SummaryBO> result = new ArrayList<>();

        SummaryBO summaryBO;

        for (ConstantsBO settleType : constantsLogic.getConstantsData(SettleType.class.getDeclaredFields())) {

            summaryBO = new SummaryBO();

            summaryBO.setSettleTypeId(settleType.getCodeDbid());
            summaryBO.setSettleType(settleType.getCodeData1());
            summaryBO.setJobDetail(jobSummary.containsKey(settleType.getCodeDbid()) ? jobSummary.get(settleType.getCodeDbid()) : BigDecimal.ZERO);
            summaryBO.setPartDetail(partSummary.containsKey(settleType.getCodeDbid()) ? partSummary.get(settleType.getCodeDbid()) : BigDecimal.ZERO);
            summaryBO.setBatteryDetail(batterySummary.containsKey(settleType.getCodeDbid()) ? batterySummary.get(settleType.getCodeDbid()) : BigDecimal.ZERO);
            summaryBO.setTotal(summaryBO.getJobDetail().add(summaryBO.getPartDetail()).add(summaryBO.getBatteryDetail()));

            result.add(summaryBO);
        }

        return result;
    }

    /**
     * 获取VO 区域
     */
    public ServiceOrderVO timelyFindServiceOrderById(Long serviceOrderId) {
        return orderService.timelySearchOrderById(serviceOrderId);
    }

    public MstProductVO findProductByCd(String productCd, String siteId) {

        return orderService.findProductByCd(productCd, siteId);
    }

    public ServiceOrderVO timelySearchBatteryClaimOrder(Long pointId, Long serviceOrderId, String orderNo, String siteId) {

        return orderService.timelySearchBatteryClaimOrder(pointId, serviceOrderId, orderNo, siteId);
    }

    public SalesOrderVO findSalesOrderById(Long salesOrderId) {

        return orderService.findSalesOrderById(salesOrderId);
    }

    public SalesOrderItemVO timelySearchSoItemNo0ActQty(Long salesOrderId) {

        return orderService.timelySearchSoItemNo0ActQty(salesOrderId);
    }

    public ServiceOrderBatteryVO timelySearchOrderBattery(Long serviceOrderId) {

        return orderService.timelySearchOrderBattery(serviceOrderId);
    }

    public BatteryVO findBatteryByNo(String batteryNo, String siteId) {

        return orderService.findBatteryByNo(batteryNo, siteId);
    }

    public CmmBatteryVO findCmmBatteryByNo(String batteryNo) {

        return orderService.findCmmBatteryByNo(batteryNo);
    }

    public CmmRegistrationDocumentVO findRegDocForBattery(Long batteryId, String siteId) {

        return orderService.findRegDocForBattery(batteryId, siteId);
    }

    public CmmWarrantyBatteryVO findCmmWarrantyBatteryById(Long batteryId) {

        return orderService.findCmmWarrantyBatteryByBatteryId(batteryId);
    }

    public List<SituationBO> timelySearchOrderFaults(Long serviceOrderId) {

        return orderService.timelySearchOrderFaults(serviceOrderId);
    }

    public ServiceOrderVO timelySearchServiceOrderByIdOrNo(Long serviceOrderId, String orderNo, String siteId) {
        return orderService.timelySearchServiceOrderByIdOrNo(serviceOrderId, orderNo, siteId);
    }

    public MstProductVO getProductById(Long productId) {
        return orderService.getProductById(productId);
    }

    public CmmSerializedProductVO findCmmSerializedProductByFrameOrPlate(String frameNo, String plateNo) {
        return orderService.findCmmSerializedProductByFrameOrPlate(frameNo, plateNo);
    }

    public CmmSerializedProductVO findCmmSerializedProductById(Long cmmSerializedProId) {
        return orderService.findCmmSerializedProductById(cmmSerializedProId);
    }

    public CmmConsumerVO findCmmConsumerById(Long consumerId) {
        return orderService.findCmmConsumerById(consumerId);
    }

    /**
     * 获取BO 区域
     */
    public SpecialClaimBO findSpecialClaimRepairList(Long specialClaimId, Long cmmSerializedProId, String modelCd, String taxPeriod) {
        return orderService.findSpecialClaimRepairList(specialClaimId, cmmSerializedProId, modelCd, taxPeriod);
    }

    public CmmSpecialClaimSerialProVO findCmmSpecialClaimSerialProBySpcialClaimIdAndMotorId(Long cmmSpecialClaimId, Long cmmSerializedProId) {
        return orderService.findCmmSpecialClaimSerialProBySpcialClaimIdAndMotorId(cmmSpecialClaimId, cmmSerializedProId);
    }

    public List<BatteryBO> findServiceBatteryByMotorId(Long cmmSerializedProductId){
        return orderService.findServiceBatteryByMotorId(cmmSerializedProductId);
    }

    /**
     * 私有方法 区域
     */
    private ServiceOrderFaultVO buildServiceOrderFaultVO(ServiceOrderFaultVO result, SituationBO situation) {

        result.setSymptomId(situation.getSymptomId());
        result.setSymptomCd(situation.getSymptomCd());
        result.setSymptomNm(situation.getSymptomNm());
        result.setConditionId(situation.getConditionId());
        result.setConditionCd(situation.getConditionCd());
        result.setConditionNm(situation.getConditionNm());
        result.setWarrantyClaimFlag(situation.getWarrantyClaimFlag());
        result.setFaultStartDate(situation.getFaultStartDate());
        result.setProductId(situation.getProductId());
        result.setProductCd(situation.getProductCd());
        result.setProductNm(situation.getProductNm());
        result.setAuthorizationNo(situation.getAuthorizationNo());
        result.setRepairDescription(situation.getRepairDescription());
        result.setSymptomComment(situation.getSymptomComment());
        result.setConditionComment(situation.getConditionComment());
        result.setProcessComment(situation.getProcessComment());
        result.setDealerComment(situation.getDealerComment());

        return result;
    }

    private List<SituationBO> validateDuplicateSituationDetail(Long serviceOrderId, BaseTableData<SituationBO> situations) {

        //获取全数据
        List<SituationBO> situationBOList = generateSituationCheckList(serviceOrderId, situations);

        if (!situations.getNewUpdateRecords().isEmpty()) {
            //symptomCd不得重复
            Optional<String> duplicatesSymptomCd = situationBOList
                                                       .stream()
                                                       .map(SituationBO::getSymptomCd)
                                                       .collect(Collectors.groupingBy(symptomCd -> symptomCd, Collectors.counting()))
                                                       .entrySet().stream()
                                                       .filter(entry -> entry.getValue() > 1)
                                                       .map(Map.Entry::getKey).findFirst();

            if (duplicatesSymptomCd.isPresent()) {

                throw new BusinessCodedException(ComUtil.t("M.E.00304", new String[] {ComUtil.t("label.symptom"), duplicatesSymptomCd.get()}));
            }
        }

        return situationBOList;
    }

    private List<JobDetailBO> validateDuplicateJobDetail(Long serviceOrderId, BaseTableData<JobDetailBO> jobs) {

        //获取全数据
        List<JobDetailBO> jobBOList = this.generateJobCheckList(serviceOrderId, jobs);

        if (!jobs.getNewUpdateRecords().isEmpty()) {

            //JobCd不得重复
            Optional<String> duplicatesJob = jobBOList.stream()
                                                      .map(JobDetailBO::getJobCd)
                                                      .collect(Collectors.groupingBy(jobCd -> jobCd, Collectors.counting()))
                                                      .entrySet().stream()
                                                      .filter(entry -> entry.getValue() > 1)
                                                      .map(Map.Entry::getKey).findFirst();

            if (duplicatesJob.isPresent()) {

                throw new BusinessCodedException(ComUtil.t("M.E.00304", new String[] {ComUtil.t("label.job"), duplicatesJob.get()}));
            }

            //Claim时，相同Symptom不得创建超过3行的job
            if (jobBOList.stream()
                         .filter(job -> StringUtils.equals(job.getServiceCategoryId(), ServiceCategory.CLAIM.getCodeDbid()))
                         .map(JobDetailBO::getSymptomId)
                         .collect(Collectors.groupingBy(symptomId -> symptomId, Collectors.counting()))
                         .entrySet().stream()
                         .filter(entry -> entry.getValue() > 3)
                         .map(Map.Entry::getKey)
                         .findFirst()
                         .isPresent()) {

                throw new BusinessCodedException(ComUtil.t("M.E.10202", new String[] {ComUtil.t("label.symptom")}));
            }
        }

        return jobBOList;
    }

    private List<PartDetailBO> validateDuplicatePartDetail(Long serviceOrderId, BaseTableData<PartDetailBO> parts) {

        //获取全数据
        List<PartDetailBO> partBOList = this.generatePartCheckList(serviceOrderId, parts);

        if (!parts.getNewUpdateRecords().isEmpty()) {

            //非电池时，PartCd不得重复
            Optional<String> duplicatesPart = partBOList.stream()
                                                        .filter(part -> !StringUtils.equals(part.getBatteryFlag(), CommonConstants.CHAR_Y))
                                                        .map(PartDetailBO::getPartCd)
                                                        .collect(Collectors.groupingBy(partCd -> partCd, Collectors.counting()))
                                                        .entrySet().stream()
                                                        .filter(entry -> entry.getValue() > 1)
                                                        .map(Map.Entry::getKey).findFirst();

            if (duplicatesPart.isPresent()) {

                throw new BusinessCodedException(ComUtil.t("M.E.00304", new String[] {ComUtil.t("label.parts"), PartNoUtil.format(duplicatesPart.get())}));
            }
        }

        return partBOList;
    }

    private void validateDuplicateBatteryDetail(List<BatteryBO> batterys) {

        //验证newBatteryNo不得重复
        Optional<String> duplicatesBatteryNo = batterys.stream()
                                                       .filter(battery -> StringUtils.isNotBlank(battery.getNewBatteryNo()))
                                                       .map(BatteryBO::getNewBatteryNo)
                                                       .collect(Collectors.groupingBy(newBatteryNo -> newBatteryNo, Collectors.counting()))
                                                       .entrySet().stream()
                                                       .filter(entry -> entry.getValue() > 1)
                                                       .map(Map.Entry::getKey).findFirst();

        if (duplicatesBatteryNo.isPresent()) {

            throw new BusinessCodedException(ComUtil.t("M.E.00304", new String[] {ComUtil.t("label.newBatteryId"), duplicatesBatteryNo.get()}));
        }
    }

    private void validateFaultStartDate(String faultStartDate, String soldDate) {

        if(StringUtils.isBlank(faultStartDate)) {
            throw new BusinessCodedException(ComUtil.t("M.E.00206", new String[] {ComUtil.t("label.faultStartDate"), ComUtil.t("label.sysDate")}));
        }
        if (StringUtils.isNotBlank(faultStartDate)) {
            //faultStartDate <= 当前日期
            if (faultStartDate.compareTo(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD)) > 0) {

                throw new BusinessCodedException(ComUtil.t("M.E.00206", new String[] {ComUtil.t("label.faultStartDate"), ComUtil.t("label.sysDate")}));
            }

            //faultStartDate >= 销售日期
            if(StringUtils.isNotBlank(soldDate) && faultStartDate.compareTo(soldDate) < 0) {

                throw new BusinessCodedException(ComUtil.t("M.E.00206", new String[] {ComUtil.t("label.salesDate"), ComUtil.t("label.faultStartDate")}));
            }
        }
    }

    private List<SituationBO> generateSituationCheckList(Long serviceOrderId, BaseTableData<SituationBO> situations){
        //新增时，只需check插入行；更新时，需整合所有数据。
        if (Objects.isNull(serviceOrderId)) {
            return situations.getInsertRecords();
        } else {
            return generateSituationCheckListFromDB(serviceOrderId, situations);
        }
    }

    private List<JobDetailBO> generateJobCheckList(Long serviceOrderId, BaseTableData<JobDetailBO> jobs){
        //新增时，只需check插入行；更新时，需整合所有数据。
        return Objects.isNull(serviceOrderId)
                ? jobs.getInsertRecords()
                : this.generateJobCheckListFromDB(serviceOrderId, jobs);
    }

    private List<PartDetailBO> generatePartCheckList(Long serviceOrderId, BaseTableData<PartDetailBO> parts){
        //新增时，只需check插入行；更新时，需整合所有数据。
        return Objects.isNull(serviceOrderId)
                ? parts.getInsertRecords()
                : this.generatePartCheckListFromDB(serviceOrderId, parts);
    }

    private List<SituationBO> generateSituationCheckListFromDB(Long serviceOrderId, BaseTableData<SituationBO> situations){

        // 获取DB当前的List
        Map<Long, SituationBO> result = orderService.timelySearchOrderFaults(serviceOrderId).stream()
                .collect(Collectors.toMap(SituationBO::getServiceOrderFaultId, value -> value));

        // result中移除removeList数据
        situations.getRemoveRecords().stream().map(SituationBO::getServiceOrderFaultId).forEach(result::remove);

        // result中替换updateList数据
        situations.getUpdateRecords().stream()
                .collect(Collectors.toMap(SituationBO::getServiceOrderFaultId, value -> value))
                .forEach((key, value) -> {
                    if (!result.containsKey(key)
                            || !StringUtils.equals(result.get(key).getUpdateCount(), value.getUpdateCount())) {
                        throw new BusinessCodedException(ComUtil.t("M.E.10449"));
                    } else {
                        result.put(key, value);
                    }
                });

        // result中拼接insertList数据
        return Stream.concat(result.values().stream(), situations.getInsertRecords().stream()).toList();
    }

    private List<JobDetailBO> generateJobCheckListFromDB(Long serviceOrderId, BaseTableData<JobDetailBO> jobs){

        //获取DB当前的List
        Map<Long, JobDetailBO> result = orderService.timelySearchServiceJobByOrderId(serviceOrderId)
                                                         .stream()
                                                         .collect(Collectors.toMap(JobDetailBO::getServiceOrderJobId, value -> value));
        //result中移除removeList数据
        jobs.getRemoveRecords().stream().map(JobDetailBO::getServiceOrderJobId).forEach(result :: remove);
        //result中替换updateList数据
        jobs.getUpdateRecords().stream().collect(Collectors.toMap(JobDetailBO::getServiceOrderJobId, value -> value))
                            .forEach((key, value) -> {
                                if (!result.containsKey(key) || !StringUtils.equals(result.get(key).getUpdateCounter(), value.getUpdateCounter())) {
                                    throw new BusinessCodedException(ComUtil.t("M.E.10449"));
                                }
                                else {
                                    result.put(key, value);
                                }
                            });
        //result中拼接insertList数据
        return Stream.concat(result.values().stream(), jobs.getInsertRecords().stream()).toList();
    }

    private List<PartDetailBO> generatePartCheckListFromDB(Long serviceOrderId, BaseTableData<PartDetailBO> parts){

        //获取DB当前的List
        Map<Long, PartDetailBO> result = orderService.timelySearchServicePartByOrderId(serviceOrderId)
                                                          .stream()
                                                          .filter(part -> part.getQty().compareTo(BigDecimal.ZERO) > 0)
                                                          .collect(Collectors.toMap(PartDetailBO::getSalesOrderItemId, value -> value));
        //result中移除removeList数据
        parts.getRemoveRecords().stream().map(PartDetailBO::getSalesOrderItemId).forEach(result :: remove);
        //result中替换updateList数据
        parts.getUpdateRecords().stream().collect(Collectors.toMap(PartDetailBO::getSalesOrderItemId, value -> value))
                            .forEach((key, value) -> {
                                if (!result.containsKey(key) || !StringUtils.equals(result.get(key).getUpdateCounter(), value.getUpdateCounter())) {
                                    throw new BusinessCodedException(ComUtil.t("M.E.10449"));
                                }
                                else {
                                    result.put(key, value);
                                }
                            });
        //result中拼接insertList数据
        return Stream.concat(result.values().stream(), parts.getInsertRecords().stream()).toList();
    }

    private void validateSymptomInDetail(String serviceCategoryId, Long symptomId) {

        //Claim时，symptom必入力
        if (StringUtils.equals(serviceCategoryId, ServiceCategory.CLAIM.getCodeDbid())
                && Objects.isNull(symptomId)){

            throw new BusinessCodedException(ComUtil.t("M.E.10190", new String[] {ComUtil.t("label.symptom")}));
        }
    }

    private void validateBatteryPart(PartDetailBO part, String evFlag) {

        if (!StringUtils.equals(part.getBatteryFlag(), CommonConstants.CHAR_Y)) {return;}

        //只有电车维修时，才能选择电池类部品
        if (!StringUtils.equals(evFlag, CommonConstants.CHAR_Y)) {

            throw new BusinessCodedException(ComUtil.t("M.E.10323"));
        }

        //电池类部品的battery type必入力
        if (StringUtils.isBlank(part.getBatteryType())) {

            throw new BusinessCodedException(ComUtil.t("M.E.00117", new String[] {ComUtil.t("label.batteryType")}));
        }

        //电池类部品的category只能选择Claim
        if (!StringUtils.equals(part.getServiceCategoryId(), ServiceCategory.CLAIM.getCodeDbid())) {
            throw new BusinessCodedException(ComUtil.t("M.E.10402"));
        }
    }

    private void validateWarrantyStartDate(BatteryBO battery) {

        //需要更换电池时，WarrantyStartDate不得为空（雅马哈销售电池）
        if (StringUtils.isBlank(battery.getWarttryStartDate())) {

            throw new BusinessCodedException(ComUtil.t("M.E.10317", new String[] {ComUtil.t("label.warttryStartDate")}));
        }
    }

    private void validateBatteryWarrantyDatePeriod(BatteryBO battery) {

        CmmWarrantyBatteryVO cmmWarrantyBattery = orderService.findCmmWarrantyBatteryByBatteryId(battery.getCurrentBatteryId());

        if (Objects.isNull(cmmWarrantyBattery)) {

            throw new BusinessCodedException(ComUtil.t("M.E.10403", new String[] {ComUtil.t("label.batteryId")}));
        }

        if (battery.getWarttryStartDate().compareTo(cmmWarrantyBattery.getFromDate()) < 0
                || battery.getWarttryStartDate().compareTo(cmmWarrantyBattery.getToDate()) > 0) {

            throw new BusinessCodedException(ComUtil.t("M.E.10408", new String[] {LocalDate.parse(battery.getWarttryStartDate(), DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)).format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DATE_FORMAT))}));
        }
    }
}
