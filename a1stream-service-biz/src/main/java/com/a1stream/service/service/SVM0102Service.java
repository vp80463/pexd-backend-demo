package com.a1stream.service.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.bo.FilesUploadUtilBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ReservationStatus;
import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.common.constants.PJConstants.SettleType;
import com.a1stream.common.logic.ConstantsLogic;
import com.a1stream.common.logic.ConsumerLogic;
import com.a1stream.common.logic.ServiceLogic;
import com.a1stream.common.manager.ConsumerManager;
import com.a1stream.common.manager.FileUploadManager;
import com.a1stream.common.manager.GenerateNoManager;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.common.manager.InvoiceManager;
import com.a1stream.common.manager.PartsSalesStockAllocationManager;
import com.a1stream.common.manager.PurchaseOrderManager;
import com.a1stream.common.manager.RemindManager;
import com.a1stream.common.manager.ServiceOrderManager;
import com.a1stream.common.manager.ServiceRequestManager;
import com.a1stream.common.model.BaseConsumerForm;
import com.a1stream.common.model.ServiceJobVLBO;
import com.a1stream.domain.bo.common.CmmConsumerBO;
import com.a1stream.domain.bo.service.SVM010201BO;
import com.a1stream.domain.bo.service.SVM010201HistoryBO;
import com.a1stream.domain.bo.service.SVM010201ServiceHistoryBO;
import com.a1stream.domain.bo.service.SVM0102PrintBO;
import com.a1stream.domain.bo.service.SVM0102PrintServiceHistoryBO;
import com.a1stream.domain.bo.service.SVM0102PrintServiceJobBO;
import com.a1stream.domain.bo.service.SVM0102PrintServicePartBO;
import com.a1stream.domain.entity.Battery;
import com.a1stream.domain.entity.CmmBattery;
import com.a1stream.domain.entity.CmmConsumerSerialProRelation;
import com.a1stream.domain.entity.CmmRegistrationDocument;
import com.a1stream.domain.entity.CmmSerializedProduct;
import com.a1stream.domain.entity.CmmServiceHistory;
import com.a1stream.domain.entity.CmmSpecialClaimSerialPro;
import com.a1stream.domain.entity.ConsumerPrivacyPolicyResult;
import com.a1stream.domain.entity.QueueData;
import com.a1stream.domain.entity.RemindSchedule;
import com.a1stream.domain.entity.SalesOrder;
import com.a1stream.domain.entity.SalesOrderItem;
import com.a1stream.domain.entity.SerializedProduct;
import com.a1stream.domain.entity.ServiceOrder;
import com.a1stream.domain.entity.ServiceOrderBattery;
import com.a1stream.domain.entity.ServiceOrderEditHistory;
import com.a1stream.domain.entity.ServiceOrderFault;
import com.a1stream.domain.entity.ServiceOrderJob;
import com.a1stream.domain.entity.ServiceSchedule;
import com.a1stream.domain.form.service.SVM010201Form;
import com.a1stream.domain.parameter.service.SVM010201Parameter;
import com.a1stream.domain.repository.BatteryRepository;
import com.a1stream.domain.repository.CmmBatteryRepository;
import com.a1stream.domain.repository.CmmBigBikeModelRepository;
import com.a1stream.domain.repository.CmmConditionRepository;
import com.a1stream.domain.repository.CmmConsumerRepository;
import com.a1stream.domain.repository.CmmConsumerSerialProRelationRepository;
import com.a1stream.domain.repository.CmmRegistrationDocumentRepository;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.CmmServiceDemandDetailRepository;
import com.a1stream.domain.repository.CmmServiceDemandRepository;
import com.a1stream.domain.repository.CmmServiceGroupItemRepository;
import com.a1stream.domain.repository.CmmServiceHistoryRepository;
import com.a1stream.domain.repository.CmmServiceJobForDORepository;
import com.a1stream.domain.repository.CmmSpecialClaimRepairRepository;
import com.a1stream.domain.repository.CmmSpecialClaimRepository;
import com.a1stream.domain.repository.CmmSpecialClaimSerialProRepository;
import com.a1stream.domain.repository.CmmSymptomRepository;
import com.a1stream.domain.repository.CmmWarrantyBatteryRepository;
import com.a1stream.domain.repository.CmmWarrantyModelPartRepository;
import com.a1stream.domain.repository.CmmWarrantySerializedProductRepository;
import com.a1stream.domain.repository.ConsumerPrivacyPolicyResultRepository;
import com.a1stream.domain.repository.DeliveryOrderItemRepository;
import com.a1stream.domain.repository.MstProductRelationRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.PoSoItemRelationRepository;
import com.a1stream.domain.repository.PurchaseOrderItemRepository;
import com.a1stream.domain.repository.QueueDataRepository;
import com.a1stream.domain.repository.RemindScheduleRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.a1stream.domain.repository.ServiceAuthorizationRepository;
import com.a1stream.domain.repository.ServiceOrderBatteryRepository;
import com.a1stream.domain.repository.ServiceOrderEditHistoryRepository;
import com.a1stream.domain.repository.ServiceOrderFaultRepository;
import com.a1stream.domain.repository.ServiceOrderJobRepository;
import com.a1stream.domain.repository.ServiceOrderRepository;
import com.a1stream.domain.repository.ServicePackageItemRepository;
import com.a1stream.domain.repository.ServiceScheduleRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmConsumerSerialProRelationVO;
import com.a1stream.domain.vo.CmmRegistrationDocumentVO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmServiceDemandDetailVO;
import com.a1stream.domain.vo.CmmServiceDemandVO;
import com.a1stream.domain.vo.CmmSpecialClaimSerialProVO;
import com.a1stream.domain.vo.CmmSpecialClaimVO;
import com.a1stream.domain.vo.CmmWarrantyBatteryVO;
import com.a1stream.domain.vo.CmmWarrantyModelPartVO;
import com.a1stream.domain.vo.CmmWarrantySerializedProductVO;
import com.a1stream.domain.vo.ConsumerPrivacyPolicyResultVO;
import com.a1stream.domain.vo.ConsumerPrivateDetailVO;
import com.a1stream.domain.vo.RemindScheduleVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.domain.vo.ServiceOrderBatteryVO;
import com.a1stream.domain.vo.ServiceOrderEditHistoryVO;
import com.a1stream.domain.vo.ServiceOrderJobVO;
import com.a1stream.domain.vo.ServicePackageItemVO;
import com.a1stream.domain.vo.ServiceScheduleVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

/**
* 功能描述:Service Order明细画面
*
* @author mid1341
*/
@Service
public class SVM0102Service {

    @Resource
    private ServiceOrderRepository serviceOrderRepo;
    @Resource
    private ServiceOrderFaultRepository serviceOrderFaultRepo;
    @Resource
    private ServiceOrderJobRepository serviceOrderJobRepo;
    @Resource
    private SalesOrderItemRepository salesOrderItemRepo;
    @Resource
    private ServiceOrderBatteryRepository serviceOrderBatteryRepo;
    @Resource
    private ServiceOrderEditHistoryRepository serviceOrderEditHistoryRepo;
    @Resource
    private ConsumerManager consumerMgr;
    @Resource
    private CmmBigBikeModelRepository cmmBigBikeModelRepo;
    @Resource
    private CmmServiceDemandRepository cmmServiceDemandRepo;
    @Resource
    private ServiceAuthorizationRepository serviceAuthorizationRepo;
    @Resource
    private CmmWarrantyBatteryRepository cmmWarrantyBatteryRepo;
    @Resource
    private CmmWarrantySerializedProductRepository cmmWarrantySerializedProductRepo;
    @Resource
    private CmmSerializedProductRepository cmmSerializedProductRepo;
    @Resource
    private SerializedProductRepository serializedProductRepo;
    @Resource
    private MstProductRepository mstProductRepo;
    @Resource
    private CmmConsumerRepository cmmConsumerRepo;
    @Resource
    private CmmRegistrationDocumentRepository cmmRegistrationDocumentRepo;
    @Resource
    private CmmBatteryRepository cmmBatteryRepo;
    @Resource
    private BatteryRepository batteryRepo;
    @Resource
    private SalesOrderRepository salesOrderRepo;
    @Resource
    private CmmWarrantyModelPartRepository cmmWarrantyModelPartRepo;
    @Resource
    private CmmSpecialClaimSerialProRepository cmmSpecialClaimSerialProRepo;
    @Resource
    private CmmSpecialClaimRepairRepository cmmSpecialClaimRepairRepo;
    @Resource
    private CmmSpecialClaimRepository cmmSpecialClaimRepos;
    @Resource
    private CmmServiceDemandDetailRepository cmmServiceDemandDetailRepo;
    @Resource
    private ConsumerPrivacyPolicyResultRepository consumerPrivacyPolicyResultRepo;
    @Resource
    private ServiceScheduleRepository serviceScheduleRepo;
    @Resource
    private GenerateNoManager generateNoMgr;
    @Resource
    private PartsSalesStockAllocationManager allocationManager;
    @Resource
    private PoSoItemRelationRepository poSoItemRelationRepo;
    @Resource
    private PurchaseOrderManager purchaseOrderMgr;
    @Resource
    private PurchaseOrderItemRepository purchaseOrderItemRepo;
    @Resource
    private InventoryManager inventoryMgr;
    @Resource
    private CmmServiceGroupItemRepository cmmServiceGroupItemRepo;
    @Resource
    private SystemParameterRepository systemParameterRepo;
    @Resource
    private CmmSymptomRepository cmmSymptomRepo;
    @Resource
    private CmmConditionRepository cmmConditionRepo;
    @Resource
    private InvoiceManager invoiceMgr;
    @Resource
    private MstProductRelationRepository mstProductRelationRepo;
    @Resource
    private ServiceLogic serviceLogic;
    @Resource
    private CmmConsumerSerialProRelationRepository cmmConsumerSerialProRelationRepo;
    @Resource
    private DeliveryOrderItemRepository deliveryOrderItemRepo;
    @Resource
    private CmmServiceHistoryRepository cmmServiceHistoryRepo;
    @Resource
    private ServiceOrderManager serviceOrderMgr;
    @Resource
    private ServicePackageItemRepository servicePackageItemRepo;
    @Resource
    private CmmServiceJobForDORepository cmmServiceJobForDORepo;
    @Resource
    private ConstantsLogic constantsLogic;
    @Resource
    private FileUploadManager fileUploadManager;
    @Resource
    private RemindManager remindMgr;
    @Resource
    private RemindScheduleRepository remindScheduleRepo;
    @Resource
    private ServiceRequestManager serviceRequestMgr;
    @Resource
    private QueueDataRepository queueDataRepo;
    @Resource
    private ConsumerLogic consumerLogic;

    public SVM010201BO timelySearchServiceDetailByOrderId(SVM010201BO result, String siteId) {

        //获取各项明细
        result.setSituationList(serviceOrderMgr.searchServiceFaultByOrderId(result.getServiceOrderId()));
        result.setJobList(serviceOrderMgr.searchServiceJobByOrderId(result.getServiceOrderId()));
        result.setPartList(serviceOrderMgr.searchServicePartByOrderId(result.getServiceOrderId()));
        result.setBatteryList(serviceOrderMgr.searchServiceBatteryByOrderId(result.getServiceOrderId()));
        result.setHistoryList(this.searchServiceEditHistoryByOrderId(result.getServiceOrderId()));

        //获取part明细的替代件,主库位
        serviceOrderMgr.setAdditionalInfoForPartDetail(result.getPartList(), siteId, result.getPointId());
        //根据BO情况重新设值BoFlag
        result.setBoFlag(result.getPartList().stream().anyMatch(part -> part.getBoQty().compareTo(BigDecimal.ZERO) > 0) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);

        return result;
    }

    public void registerServiceOrder(SVM010201Parameter para) {

        this.generateServiceOrderNo(para);

        //新建时，关联预定数据 + 锁死specialClaim master
        if (!Objects.isNull(para.getServiceSchedule())) {serviceScheduleRepo.save(BeanMapUtils.mapTo(para.getServiceSchedule(), ServiceSchedule.class));}
        if (!para.getCmmSpecialClaimSerialProList().isEmpty()) {cmmSpecialClaimSerialProRepo.saveInBatch(BeanMapUtils.mapListTo(para.getCmmSpecialClaimSerialProList(), CmmSpecialClaimSerialPro.class));}

        this.saveServiceOrderBasicInfo(para);

        //调用预定逻辑, SalesOrderItem需从DB获取（保存后才生成主键）
        allocationManager.executeStockAllocation(BeanMapUtils.mapTo(salesOrderRepo.findBySalesOrderId(para.getSalesOrder().getSalesOrderId()),SalesOrderVO.class), BeanMapUtils.mapListTo(salesOrderItemRepo.findBySalesOrderId(para.getSalesOrder().getSalesOrderId()), SalesOrderItemVO.class));
        //根据预定后的情况，自动采购BO
        serviceOrderMgr.autoPurchaseBoPart(BeanMapUtils.mapTo(salesOrderRepo.findBySalesOrderId(para.getSalesOrder().getSalesOrderId()),SalesOrderVO.class));
        //计算job/part Amount 至 serviceOrder
        serviceOrderMgr.calculateOrderSummary(para.getServiceOrder().getServiceOrderId(), para.getServiceOrder().getRelatedSalesOrderId());
    }

    public void updateServiceOrder(SVM010201Parameter para) {

        this.saveServiceOrderBasicInfo(para);

        //调用预定逻辑, SalesOrderItem需从DB获取（保存后才生成主键）
        allocationManager.executeStockAllocation(BeanMapUtils.mapTo(salesOrderRepo.findBySalesOrderId(para.getSalesOrder().getSalesOrderId()),SalesOrderVO.class), BeanMapUtils.mapListTo(salesOrderItemRepo.findBySalesOrderId(para.getSalesOrder().getSalesOrderId()).stream().filter(salesOrderItem -> salesOrderItem.getActualQty().compareTo(BigDecimal.ZERO) > 0).toList(), SalesOrderItemVO.class));
        //根据预定后的情况，自动采购BO
        serviceOrderMgr.autoPurchaseBoPart(BeanMapUtils.mapTo(salesOrderRepo.findBySalesOrderId(para.getSalesOrder().getSalesOrderId()),SalesOrderVO.class));
        //计算job/part Amount 至 serviceOrder
        serviceOrderMgr.calculateOrderSummary(para.getServiceOrder().getServiceOrderId(), para.getServiceOrder().getRelatedSalesOrderId());
    }

    public void cancelServiceOrder(SVM010201Parameter para) {

        if (!para.getCmmSpecialClaimSerialProList().isEmpty()) {cmmSpecialClaimSerialProRepo.saveInBatch(BeanMapUtils.mapListTo(para.getCmmSpecialClaimSerialProList(), CmmSpecialClaimSerialPro.class));}

        if (!Objects.isNull(para.getServiceOrder())) {serviceOrderRepo.save(BeanMapUtils.mapTo(para.getServiceOrder(), ServiceOrder.class));}
        if (!Objects.isNull(para.getSalesOrder())) {BeanMapUtils.mapTo(salesOrderRepo.save(BeanMapUtils.mapTo(para.getSalesOrder(), SalesOrder.class)), SalesOrderVO.class);}
        if (!Objects.isNull(para.getServiceOrderEditHistory())) {serviceOrderEditHistoryRepo.save(BeanMapUtils.mapTo(para.getServiceOrderEditHistory(), ServiceOrderEditHistory.class));}
        if (!para.getPartListForSave().isEmpty()) {salesOrderItemRepo.saveInBatch(BeanMapUtils.mapListTo(para.getPartListForSave(), SalesOrderItem.class));}

        //处理part stock变更相关
        inventoryMgr.updateProductStockStatusByMap(para.getStockStatusVOChangeMap());
    }

    public void settleServiceOrder(SVM010201Parameter para, PJUserDetails uc) {

        //1.由于可能出现更换consumerId的情况，优先更新consumer，并刷新原order中的ID
        this.saveOrUpdateConsumer(para.getConsumerBaseInfo());
        para.getServiceOrder().setConsumerId(para.getConsumerBaseInfo().getConsumerId());
        para.getSalesOrder().setCmmConsumerId(para.getConsumerBaseInfo().getConsumerId());

        //2.更新-创建 车辆及电池数据
        if (!Objects.isNull(para.getCmmSerializedProduct())) {cmmSerializedProductRepo.save(BeanMapUtils.mapTo(para.getCmmSerializedProduct(), CmmSerializedProduct.class));}
        if (!Objects.isNull(para.getSerializedProduct())) {serializedProductRepo.save(BeanMapUtils.mapTo(para.getSerializedProduct(), SerializedProduct.class));}
        if (!para.getCmmBatteryMstList().isEmpty()) {cmmBatteryRepo.saveInBatch(BeanMapUtils.mapListTo(para.getCmmBatteryMstList(), CmmBattery.class));}
        if (!para.getBatteryMstList().isEmpty()) {batteryRepo.saveInBatch(BeanMapUtils.mapListTo(para.getBatteryMstList(), Battery.class));}

        //3.结束预约信息
        if (!Objects.isNull(para.getServiceSchedule())) {serviceScheduleRepo.save(BeanMapUtils.mapTo(para.getServiceSchedule(), ServiceSchedule.class));}

        //4.更新业务表
        this.saveServiceOrderBasicInfo(para);
        //5.对部品明细做picking-Shipment处理。 当部品没有全部allocated时，报错回滚
        serviceOrderMgr.doPickingAndShipment(BeanMapUtils.mapTo(salesOrderRepo.findById(para.getSalesOrder().getSalesOrderId()), SalesOrderVO.class), uc.getPersonId(), uc.getPersonName());
        //6.创建Invoice
        serviceOrderMgr.doInvoice(para.getServiceOrder(), para.getSalesOrder());
        //计算job/part Amount 至 serviceOrder
        serviceOrderMgr.calculateOrderSummary(para.getServiceOrder().getServiceOrderId(), para.getServiceOrder().getRelatedSalesOrderId());
    }

    public void doAfterServiceOrder(SVM010201Parameter para) {

        if (!Objects.isNull(para.getCmmRegistrationDocument())) {cmmRegistrationDocumentRepo.save(BeanMapUtils.mapTo(para.getCmmRegistrationDocument(), CmmRegistrationDocument.class));}
        if (!Objects.isNull(para.getCmmServiceHistory())) {cmmServiceHistoryRepo.save(BeanMapUtils.mapTo(para.getCmmServiceHistory(), CmmServiceHistory.class));}
        if (!para.getCmmConsumerSerialProRelationListForSave().isEmpty()) {cmmConsumerSerialProRelationRepo.saveInBatch(BeanMapUtils.mapListTo(para.getCmmConsumerSerialProRelationListForSave(), CmmConsumerSerialProRelation.class));}
        if (!para.getQueueDataList().isEmpty()) {queueDataRepo.saveInBatch(BeanMapUtils.mapListTo(para.getQueueDataList(), QueueData.class));}

        //生成serviceFollowUp + 待关闭的CouponRemind(仅Free Coupon时)
        List<RemindScheduleVO> remindList = Stream.concat(remindMgr.generateServiceFollowUpRemind(para.getServiceOrder()).stream(), remindMgr.getWaitingCloseRemindSchedule(para.getServiceOrder()).stream()).toList();
        if (!remindList.isEmpty()) {remindScheduleRepo.saveInBatch(BeanMapUtils.mapListTo(remindList, RemindSchedule.class));}

        //修改或生成车辆FscRemind标志表
        remindMgr.setupSerializedProFscRemind(para.getServiceOrder().getSiteId(), para.getServiceOrder().getFacilityId(), para.getServiceOrder().getServiceOrderId(), para.getServiceOrder().getCmmSerializedProductId(), para.getServiceOrder().getSoldDate(), para.getServiceOrder().getServiceDemandId());

        //生成ServiceRequest并插入Q表，toSW, toCRM
        serviceRequestMgr.generateServiceRequest(para.getServiceOrder());

        //TODO 生成E-Invoice数据
    }

    private void generateServiceOrderNo(SVM010201Parameter para) {

        String orderNo = generateNoMgr.generateNonSerializedItemSalesOrderNo(para.getServiceOrder().getSiteId(), para.getServiceOrder().getFacilityId());

        para.getServiceOrder().setOrderNo(orderNo);
        para.getSalesOrder().setOrderNo(orderNo);
    }

    private void saveServiceOrderBasicInfo(SVM010201Parameter para) {

        if (!Objects.isNull(para.getServiceOrder())) {serviceOrderRepo.save(BeanMapUtils.mapTo(para.getServiceOrder(), ServiceOrder.class));}
        if (!Objects.isNull(para.getSalesOrder())) {salesOrderRepo.save(BeanMapUtils.mapTo(para.getSalesOrder(), SalesOrder.class));}
        if (!Objects.isNull(para.getConsumerPrivacyPolicyResult())) {consumerPrivacyPolicyResultRepo.save(BeanMapUtils.mapTo(para.getConsumerPrivacyPolicyResult(), ConsumerPrivacyPolicyResult.class));}
        if (!Objects.isNull(para.getServiceOrderEditHistory())) {serviceOrderEditHistoryRepo.save(BeanMapUtils.mapTo(para.getServiceOrderEditHistory(), ServiceOrderEditHistory.class));}
        if (!para.getSituationListForDelete().isEmpty()) {serviceOrderFaultRepo.deleteAllByIdInBatch(para.getSituationListForDelete());}
        if (!para.getSituationListForSave().isEmpty()) {serviceOrderFaultRepo.saveInBatch(BeanMapUtils.mapListTo(para.getSituationListForSave(), ServiceOrderFault.class));}
        if (!para.getJobListForDelete().isEmpty()) {serviceOrderJobRepo.deleteAllByIdInBatch(para.getJobListForDelete());}
        if (!para.getJobListForSave().isEmpty()) {serviceOrderJobRepo.saveInBatch(BeanMapUtils.mapListTo(para.getJobListForSave(), ServiceOrderJob.class));}
        if (!para.getPartListForSave().isEmpty()) {salesOrderItemRepo.saveInBatch(BeanMapUtils.mapListTo(para.getPartListForSave(), SalesOrderItem.class));}
        if (!para.getBatteryList().isEmpty()) {serviceOrderBatteryRepo.saveInBatch(BeanMapUtils.mapListTo(para.getBatteryList(), ServiceOrderBattery.class));}

        //处理part stock变更相关
        inventoryMgr.updateProductStockStatusByMap(para.getStockStatusVOChangeMap());
    }

    public ServiceScheduleVO findTodayCompleteServiceScheduleByPlateNo(String siteId, Long facilityId, String plateNo) {
        return BeanMapUtils.mapTo(serviceScheduleRepo.findFirstBySiteIdAndFacilityIdAndScheduleDateAndPlateNoAndReservationStatus(siteId, facilityId, LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)), plateNo, ReservationStatus.CONFIRMED.getCodeDbid()), ServiceScheduleVO.class);
    }

    public ServiceScheduleVO findServiceScheduleByServiceOrderId(Long serviceOrderId) {
        return BeanMapUtils.mapTo(serviceScheduleRepo.findFirstByServiceOrderId(serviceOrderId), ServiceScheduleVO.class);
    }

    public List<CmmServiceDemandDetailVO> findDemandHistoryBySerialProId(Long cmmSerializedProId){
        return BeanMapUtils.mapListTo(cmmServiceDemandDetailRepo.findBySerializedProductId(cmmSerializedProId), CmmServiceDemandDetailVO.class);
    }

    public List<CmmServiceDemandVO> findDemandMstByServiceCategory(String serviceCategoryId){
        return BeanMapUtils.mapListTo(cmmServiceDemandRepo.findByServiceCategory(serviceCategoryId), CmmServiceDemandVO.class);
    }

    public CmmConsumerSerialProRelationVO findCMRelationByMotorIdAndConsumerId(Long serializedProId, Long consumerId) {
        return BeanMapUtils.mapTo(cmmConsumerSerialProRelationRepo.findFirstBySerializedProductIdAndConsumerId(serializedProId, consumerId), CmmConsumerSerialProRelationVO.class);
    }

    public CmmConsumerSerialProRelationVO findOwnerRelationByMotorId(Long serializedProId) {
        return BeanMapUtils.mapTo(cmmConsumerSerialProRelationRepo.findOwnerBySerializedProductId(serializedProId), CmmConsumerSerialProRelationVO.class);
    }

    public List<SVM010201HistoryBO> searchServiceEditHistoryByOrderId(Long serviceOrderId) {
        return serviceOrderEditHistoryRepo.listServiceEditHistoryByOrderId(serviceOrderId);
    }

    public ServiceJobVLBO getFreeCouponDetail(String jobCd, String modelCd, String taxPeriod) {

        List<ServiceJobVLBO> serviceJobList = cmmServiceGroupItemRepo.findJobListByModel(serviceLogic.generateModelCdListForServiceGroup(modelCd), Arrays.asList(jobCd), new ArrayList<>());

        serviceOrderMgr.setPriceValueForJobFromModelCd(SettleType.FACTORY.getCodeDbid(), taxPeriod, serviceJobList);

        return serviceJobList.isEmpty() ? new ServiceJobVLBO() : serviceJobList.get(0);
    }

    public List<ServicePackageItemVO> getServicePackageDetail(Long servicePackageId) {

        return BeanMapUtils.mapListTo(servicePackageItemRepo.findByServicePackageId(servicePackageId), ServicePackageItemVO.class) ;
    }

    public List<CmmSpecialClaimSerialProVO> findSpecialClaimSerialProListByBulletinNo(String bulletinNo, Long cmmSerializedProId){

        List<CmmSpecialClaimVO> specialClaimList = BeanMapUtils.mapListTo(cmmSpecialClaimRepos.findByBulletinNo(bulletinNo), CmmSpecialClaimVO.class);

        return BeanMapUtils.mapListTo(cmmSpecialClaimSerialProRepo.findBySpecialClaimIdInAndSerializedProductId(specialClaimList.stream().map(CmmSpecialClaimVO::getSpecialClaimId).toList(), cmmSerializedProId), CmmSpecialClaimSerialProVO.class);
    }

    public CmmConsumerBO getOwnerRelationBySerialProId(String siteId, Long serializedProductId) {

        CmmConsumerBO result = cmmConsumerRepo.findOwnerBySerialProId(siteId, serializedProductId);

        if (!Objects.isNull(result)) {
            result.setPrivacyPolicyResult(consumerMgr.getConsumerPolicyInfo(siteId, result.getLastNm(), result.getMiddleNm(), result.getFirstNm(), result.getMobilePhone()));
        }

        return result;
    }

    public Long getModelCategoryIdByLvOneModelId(Long modelId) {
        return mstProductRepo.getModelCategoryIdByLvOneModelId(modelId);
    }

    public CmmRegistrationDocumentVO findRegisterDocByMotorId(Long serializedProductId) {
        return BeanMapUtils.mapTo(cmmRegistrationDocumentRepo.findFirstBySerializedProductId(serializedProductId), CmmRegistrationDocumentVO.class);
    }

    public Integer getMaxBaseDateByMcId(Long serializedProductId) {
        return cmmServiceHistoryRepo.getMaxBaseDateByMcId(serializedProductId);
    }

    public CmmServiceDemandVO getCmmServiceDemandById(Long cmmServiceDemandId) {
        return BeanMapUtils.mapTo(cmmServiceDemandRepo.findById(cmmServiceDemandId), CmmServiceDemandVO.class);
    }

    public boolean findBigBikeExistByModleCd(String modelCd) {
        return cmmBigBikeModelRepo.existsByModelCd(modelCd);
    }

    public CmmSerializedProductVO findCmmSerializedProductByFrameOrPlate(String frameNo, String plateNo) {
        return BeanMapUtils.mapTo(StringUtils.isNotBlank(frameNo) ? cmmSerializedProductRepo.findFirstByFrameNo(frameNo) : cmmSerializedProductRepo.findFirstByPlateNo(plateNo), CmmSerializedProductVO.class);
    }

    public void saveOrUpdateConsumer(BaseConsumerForm form) {
        consumerMgr.saveOrUpdateConsumer(form);
    }

    public ConsumerPrivateDetailVO getConsumerPrivateDetailVO(String consumerRetrieve) {
        return consumerMgr.getConsumerPrivateDetailVO(consumerRetrieve);
    }

    public List<CmmBatteryVO> findCmmBatteryByBatteryNos(List<String> batteryNoList){
        return BeanMapUtils.mapListTo(cmmBatteryRepo.findByBatteryNoIn(batteryNoList), CmmBatteryVO.class);
    }

    public CmmBatteryVO findCmmBatteryById(Long cmmBatteryId) {
        return BeanMapUtils.mapTo(cmmBatteryRepo.findById(cmmBatteryId), CmmBatteryVO.class);
    }

    public BatteryVO findBatteryByCmmId(String siteId, Long cmmBatteryId) {
        return BeanMapUtils.mapTo(batteryRepo.findFirstBySiteIdAndCmmBatteryInfoId(siteId, cmmBatteryId), BatteryVO.class);
    }

    public boolean findAuthorizationNoIsUsed(String siteId, Long serviceOrderId, List<String> authorizationNoList) {
        return Objects.isNull(serviceOrderId) ? serviceOrderFaultRepo.existsBySiteIdAndAuthorizationNoIn(siteId, authorizationNoList) : serviceOrderFaultRepo.existsBySiteIdAndAuthorizationNoInAndServiceOrderIdNot(siteId, authorizationNoList, serviceOrderId);
    }

    public List<ServiceOrderBatteryVO> findServiceOrderBatteryListByIds(List<Long> serviceOrderBatteryIds) {
        return BeanMapUtils.mapListTo(serviceOrderBatteryRepo.findAllById(serviceOrderBatteryIds), ServiceOrderBatteryVO.class);
    }

    public List<ServiceOrderJobVO> findServiceOrderJobListByIds(List<Long> serviceOrderJobIds) {
        return BeanMapUtils.mapListTo(serviceOrderJobRepo.findAllById(serviceOrderJobIds), ServiceOrderJobVO.class);
    }

    public List<ServiceOrderJobVO> timelyFindServiceOrderJobListByOrderId(Long serviceOrderId) {
        return BeanMapUtils.mapListTo(serviceOrderJobRepo.findByServiceOrderId(serviceOrderId), ServiceOrderJobVO.class);
    }

    public List<SalesOrderItemVO> timelyFindServiceOrderPartListByIds(List<Long> serviceOrderPartIds) {
        return BeanMapUtils.mapListTo(salesOrderItemRepo.findAllById(serviceOrderPartIds), SalesOrderItemVO.class);
    }

    public List<SalesOrderItemVO> timelyFindServiceOrderPartListBySalesOrderId(Long salesOrderId) {
        return BeanMapUtils.mapListTo(salesOrderItemRepo.findBySalesOrderId(salesOrderId), SalesOrderItemVO.class);
    }

    public CmmWarrantyBatteryVO findCmmWarrantyBatteryByBatteryId(Long batteryId) {
        return BeanMapUtils.mapTo(cmmWarrantyBatteryRepo.findFirstByBatteryId(batteryId), CmmWarrantyBatteryVO.class);
    }

    public CmmWarrantySerializedProductVO findCmmWarrantySerializedProductBySerializedId(Long serializedProductId) {
        return BeanMapUtils.mapTo(cmmWarrantySerializedProductRepo.findFirstBySerializedProductId(serializedProductId), CmmWarrantySerializedProductVO.class);
    }

    public SerializedProductVO findSerializedProductByCmmId(Long cmmSerializedProductId, String siteId) {
        return BeanMapUtils.mapTo(serializedProductRepo.findFirstByCmmSerializedProductIdAndSiteId(cmmSerializedProductId, siteId), SerializedProductVO.class);
    }

    public List<CmmWarrantyModelPartVO> findWarrantyModelPartByModelCd(String modelCd){
        return cmmWarrantyModelPartRepo.findModelPartByModelCd(modelCd);
    }

    public List<SVM010201ServiceHistoryBO> findServiceHistoryByMotorId(Long cmmSerializedProId, String siteId){
        return cmmServiceHistoryRepo.findServiceHistoryByMotorId(cmmSerializedProId, siteId);
    }

    public SVM0102PrintBO getServiceJobCardHeaderData(Long serviceOrderId) {
        return serviceOrderRepo.getServiceJobCardHeaderData(serviceOrderId);
    }

    public List<SVM0102PrintServiceHistoryBO> getServiceHistoryPrintList(Long serviceOrderId, String siteId) {
        return cmmServiceHistoryRepo.getServiceHistoryPrintList(serviceOrderId, siteId);
    }

    public List<SVM0102PrintServiceJobBO> getServiceJobPrintList(Long serviceOrderId) {
        return serviceOrderJobRepo.getServiceJobPrintList(serviceOrderId);
    }

    public List<SVM0102PrintServicePartBO> getServicePartPrintList(Long serviceOrderId) {
        return salesOrderRepo.getServicePartPrintList(serviceOrderId);
    }

    public SVM0102PrintBO getServiceJobCardForDoHeaderData(Long serviceOrderId) {
        return serviceOrderRepo.getServiceJobCardForDoHeaderData(serviceOrderId);
    }

    public SVM0102PrintBO getServicePaymentHederData(Long serviceOrderId) {
        return serviceOrderRepo.getServicePaymentHederData(serviceOrderId);
    }

    public List<SVM0102PrintServiceJobBO> getServicePaymentJobPrintList(Long serviceOrderId) {
        return serviceOrderJobRepo.getServicePaymentJobPrintList(serviceOrderId);
    }

    public List<SVM0102PrintServicePartBO> getServicePaymentPartPrintList(Long serviceOrderId) {
        return salesOrderRepo.getServicePaymentPartPrintList(serviceOrderId);
    }

    public SVM0102PrintBO getServicePaymentForDoHederData(Long serviceOrderId) {
        return serviceOrderRepo.getServicePaymentForDoHederData(serviceOrderId);
    }

    public List<SVM0102PrintServiceJobBO> getServicePaymentJobForDoPrintList(Long serviceOrderId) {
        return serviceOrderJobRepo.getServicePaymentJobForDoPrintList(serviceOrderId);
    }

    public List<SVM0102PrintServicePartBO> getServicePaymentPartForDoPrintList(Long serviceOrderId) {
        return salesOrderRepo.getServicePaymentPartForDoPrintList(serviceOrderId);
    }

    public void saveServiceOrderEditHistoryVO(ServiceOrderEditHistoryVO serviceOrderEditHistoryVO) {
        serviceOrderEditHistoryRepo.save(BeanMapUtils.mapTo(serviceOrderEditHistoryVO, ServiceOrderEditHistory.class));
    }

    public List<ServiceJobVLBO> findServiceJobByModelTypeListWithJobId(String serviceCatgeoryId, Long modelType, String taxPeriod, List<Long> jobIdList) {
        List<ServiceJobVLBO> result = cmmServiceJobForDORepo.findServiceJobByModelTypeListWithJobId(serviceCatgeoryId, modelType, jobIdList);
        serviceOrderMgr.setPriceValueForJobFromModelType(taxPeriod, result);

        return result;
    }

    public List<ServiceJobVLBO> findServiceJobByModelCdListWithJobId(String serviceCatgeoryId, String modelcd, String taxPeriod, List<Long> jobIdList) {
        List<ServiceJobVLBO> result = cmmServiceGroupItemRepo.findJobListByModel(serviceLogic.generateModelCdListForServiceGroup(modelcd), new ArrayList<>(), jobIdList);
        serviceOrderMgr.setPriceValueForJobFromModelCd(constantsLogic.getConstantsByCodeDbId(ServiceCategory.class.getDeclaredFields(), serviceCatgeoryId).getCodeData2(), taxPeriod, result);

        return result;
    }

    public String privacyPolicyResultsFileUpload(SVM010201Form form, MultipartFile[] files) {

        FilesUploadUtilBO fileUploadUtil = new FilesUploadUtilBO();
        fileUploadUtil.setBusinessType("S074PRIVACYPOLICYRESULTS");
        fileUploadUtil.setSiteId(form.getSiteId());
        fileUploadUtil.setMultipleFiles(files);
        fileUploadUtil.setBusinessRulesName(form.getOrderInfo().getMobilephone());
        Map<String, String> oldAndNewFileNameMap = fileUploadManager.multipleFileUpload(fileUploadUtil);
        return oldAndNewFileNameMap.get(files[0].getOriginalFilename());
    }

}
