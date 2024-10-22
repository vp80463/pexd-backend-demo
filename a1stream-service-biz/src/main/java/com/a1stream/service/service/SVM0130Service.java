package com.a1stream.service.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.manager.GenerateNoManager;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.common.manager.PartsSalesStockAllocationManager;
import com.a1stream.common.manager.ServiceOrderManager;
import com.a1stream.domain.bo.service.SVM013001BO;
import com.a1stream.domain.bo.service.SVM0130PrintBO;
import com.a1stream.domain.bo.service.SVM0130PrintServiceJobBO;
import com.a1stream.domain.bo.service.SVM0130PrintServicePartBO;
import com.a1stream.domain.entity.Battery;
import com.a1stream.domain.entity.CmmBattery;
import com.a1stream.domain.entity.CmmSerializedProduct;
import com.a1stream.domain.entity.CmmSpecialClaimSerialPro;
import com.a1stream.domain.entity.SalesOrder;
import com.a1stream.domain.entity.SalesOrderItem;
import com.a1stream.domain.entity.SerializedProduct;
import com.a1stream.domain.entity.ServiceOrder;
import com.a1stream.domain.entity.ServiceOrderBattery;
import com.a1stream.domain.entity.ServiceOrderFault;
import com.a1stream.domain.entity.ServiceOrderJob;
import com.a1stream.domain.parameter.service.SVM013001Parameter;
import com.a1stream.domain.repository.BatteryRepository;
import com.a1stream.domain.repository.CmmBatteryRepository;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.CmmSpecialClaimRepository;
import com.a1stream.domain.repository.CmmSpecialClaimSerialProRepository;
import com.a1stream.domain.repository.CmmWarrantyBatteryRepository;
import com.a1stream.domain.repository.CmmWarrantyModelPartRepository;
import com.a1stream.domain.repository.CmmWarrantySerializedProductRepository;
import com.a1stream.domain.repository.MstProductRelationRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.a1stream.domain.repository.ServiceOrderBatteryRepository;
import com.a1stream.domain.repository.ServiceOrderFaultRepository;
import com.a1stream.domain.repository.ServiceOrderJobRepository;
import com.a1stream.domain.repository.ServiceOrderRepository;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmSpecialClaimSerialProVO;
import com.a1stream.domain.vo.CmmSpecialClaimVO;
import com.a1stream.domain.vo.CmmWarrantyBatteryVO;
import com.a1stream.domain.vo.CmmWarrantyModelPartVO;
import com.a1stream.domain.vo.CmmWarrantySerializedProductVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.domain.vo.ServiceOrderBatteryVO;
import com.a1stream.domain.vo.ServiceOrderJobVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class SVM0130Service {

    @Resource
    private ServiceOrderRepository serviceOrderRepo;
    @Resource
    private ServiceOrderJobRepository serviceOrderJobRepo;
    @Resource
    private SalesOrderItemRepository salesOrderItemRepo;
    @Resource
    private ServiceOrderFaultRepository serviceOrderFaultRepo;
    @Resource
    private ServiceOrderBatteryRepository serviceOrderBatteryRepo;
    @Resource
    private MstProductRelationRepository mstProductRelationRepo;
    @Resource
    private ProductInventoryRepository productInventoryRepo;
    @Resource
    private GenerateNoManager generateNoMgr;
    @Resource
    private SalesOrderRepository salesOrderRepo;
    @Resource
    private InventoryManager inventoryMgr;
    @Resource
    private ServiceOrderManager serviceOrderMgr;
    @Resource
    private PartsSalesStockAllocationManager allocationManager;
    @Resource
    private CmmSpecialClaimSerialProRepository cmmSpecialClaimSerialProRepo;
    @Resource
    private CmmSerializedProductRepository cmmSerializedProductRepo;
    @Resource
    private SerializedProductRepository serializedProductRepo;
    @Resource
    private CmmBatteryRepository cmmBatteryRepo;
    @Resource
    private BatteryRepository batteryRepo;
    @Resource
    private CmmSpecialClaimRepository cmmSpecialClaimRepo;
    @Resource
    private CmmWarrantySerializedProductRepository cmmWarrantySerializedProductRepo;
    @Resource
    private CmmWarrantyModelPartRepository cmmWarrantyModelPartRepo;
    @Resource
    private CmmWarrantyBatteryRepository cmmWarrantyBatteryRepo;

    public SVM013001BO timelySearchServiceDetailByOrderId(SVM013001BO result, String siteId) {

        //获取各项明细
        result.setSituationList(serviceOrderMgr.searchServiceFaultByOrderId(result.getServiceOrderId()));
        result.setJobList(serviceOrderMgr.searchServiceJobByOrderId(result.getServiceOrderId()));
        result.setPartList(serviceOrderMgr.searchServicePartByOrderId(result.getServiceOrderId()));
        result.setBatteryList(serviceOrderMgr.searchServiceBatteryByOrderId(result.getServiceOrderId()));

        //获取part明细的替代件,主库位
        serviceOrderMgr.setAdditionalInfoForPartDetail(result.getPartList(), siteId, result.getPointId());
        //根据BO情况重新设值BoFlag
        result.setBoFlag(result.getPartList().stream().anyMatch(part -> part.getBoQty().compareTo(BigDecimal.ZERO) > 0) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);

        return result;
    }

    public void registerServiceOrder(SVM013001Parameter para) {

        this.generateServiceOrderNo(para);

        //新建时，关联预定数据 + 锁死specialClaim master
        if (!para.getCmmSpecialClaimSerialProList().isEmpty()) {cmmSpecialClaimSerialProRepo.saveInBatch(BeanMapUtils.mapListTo(para.getCmmSpecialClaimSerialProList(), CmmSpecialClaimSerialPro.class));}

        this.saveServiceOrderBasicInfo(para);

        //调用预定逻辑, SalesOrderItem需从DB获取（保存后才生成主键）
        allocationManager.executeStockAllocation(BeanMapUtils.mapTo(salesOrderRepo.findBySalesOrderId(para.getSalesOrder().getSalesOrderId()),SalesOrderVO.class), BeanMapUtils.mapListTo(salesOrderItemRepo.findBySalesOrderId(para.getSalesOrder().getSalesOrderId()), SalesOrderItemVO.class));
        //根据预定后的情况，自动采购BO
        serviceOrderMgr.autoPurchaseBoPart(BeanMapUtils.mapTo(salesOrderRepo.findBySalesOrderId(para.getSalesOrder().getSalesOrderId()),SalesOrderVO.class));
        //计算job/part Amount 至 serviceOrder
        serviceOrderMgr.calculateOrderSummary(para.getServiceOrder().getServiceOrderId(), para.getServiceOrder().getRelatedSalesOrderId());
    }

    private void saveServiceOrderBasicInfo(SVM013001Parameter para) {

        if (!Objects.isNull(para.getServiceOrder())) {serviceOrderRepo.save(BeanMapUtils.mapTo(para.getServiceOrder(), ServiceOrder.class));}
        if (!Objects.isNull(para.getSalesOrder())) {salesOrderRepo.save(BeanMapUtils.mapTo(para.getSalesOrder(), SalesOrder.class));}
        if (!Objects.isNull(para.getCmmSerializedProduct())) {cmmSerializedProductRepo.save(BeanMapUtils.mapTo(para.getCmmSerializedProduct(), CmmSerializedProduct.class));}
        if (!para.getSituationListForDelete().isEmpty()) {serviceOrderFaultRepo.deleteAllByIdInBatch(para.getSituationListForDelete());}
        if (!para.getSituationListForSave().isEmpty()) {serviceOrderFaultRepo.saveInBatch(BeanMapUtils.mapListTo(para.getSituationListForSave(), ServiceOrderFault.class));}
        if (!para.getJobListForDelete().isEmpty()) {serviceOrderJobRepo.deleteAllByIdInBatch(para.getJobListForDelete());}
        if (!para.getJobListForSave().isEmpty()) {serviceOrderJobRepo.saveInBatch(BeanMapUtils.mapListTo(para.getJobListForSave(), ServiceOrderJob.class));}
        if (!para.getPartListForSave().isEmpty()) {salesOrderItemRepo.saveInBatch(BeanMapUtils.mapListTo(para.getPartListForSave(), SalesOrderItem.class));}
        if (!para.getBatteryList().isEmpty()) {serviceOrderBatteryRepo.saveInBatch(BeanMapUtils.mapListTo(para.getBatteryList(), ServiceOrderBattery.class));}

        //处理part stock变更相关
        inventoryMgr.updateProductStockStatusByMap(para.getStockStatusVOChangeMap());
    }

    public void cancelServiceOrder(SVM013001Parameter para) {

        if (!para.getCmmSpecialClaimSerialProList().isEmpty()) {cmmSpecialClaimSerialProRepo.saveInBatch(BeanMapUtils.mapListTo(para.getCmmSpecialClaimSerialProList(), CmmSpecialClaimSerialPro.class));}

        if (!Objects.isNull(para.getServiceOrder())) {serviceOrderRepo.save(BeanMapUtils.mapTo(para.getServiceOrder(), ServiceOrder.class));}
        if (!Objects.isNull(para.getSalesOrder())) {BeanMapUtils.mapTo(salesOrderRepo.save(BeanMapUtils.mapTo(para.getSalesOrder(), SalesOrder.class)), SalesOrderVO.class);}
        if (!Objects.isNull(para.getCmmSerializedProduct())) {cmmSerializedProductRepo.save(BeanMapUtils.mapTo(para.getCmmSerializedProduct(), CmmSerializedProduct.class));}
        if (!para.getPartListForSave().isEmpty()) {salesOrderItemRepo.saveInBatch(BeanMapUtils.mapListTo(para.getPartListForSave(), SalesOrderItem.class));}

        //处理part stock变更相关
        inventoryMgr.updateProductStockStatusByMap(para.getStockStatusVOChangeMap());
    }

    public void updateServiceOrder(SVM013001Parameter para) {

        this.saveServiceOrderBasicInfo(para);

        //调用预定逻辑, SalesOrderItem需从DB获取（保存后才生成主键）
        allocationManager.executeStockAllocation(BeanMapUtils.mapTo(salesOrderRepo.findBySalesOrderId(para.getSalesOrder().getSalesOrderId()),SalesOrderVO.class), BeanMapUtils.mapListTo(salesOrderItemRepo.findBySalesOrderId(para.getSalesOrder().getSalesOrderId()).stream().filter(salesOrderItem -> salesOrderItem.getActualQty().compareTo(BigDecimal.ZERO) > 0).toList(), SalesOrderItemVO.class));
        //根据预定后的情况，自动采购BO
        serviceOrderMgr.autoPurchaseBoPart(BeanMapUtils.mapTo(salesOrderRepo.findBySalesOrderId(para.getSalesOrder().getSalesOrderId()),SalesOrderVO.class));
        //计算job/part Amount 至 serviceOrder
        serviceOrderMgr.calculateOrderSummary(para.getServiceOrder().getServiceOrderId(), para.getServiceOrder().getRelatedSalesOrderId());
    }

    public void settleServiceOrder(SVM013001Parameter para, PJUserDetails uc) {

        //1.更新-创建 车辆及电池数据
        if (!Objects.isNull(para.getSerializedProduct())) {serializedProductRepo.save(BeanMapUtils.mapTo(para.getSerializedProduct(), SerializedProduct.class));}
        if (!para.getCmmBatteryMstList().isEmpty()) {cmmBatteryRepo.saveInBatch(BeanMapUtils.mapListTo(para.getCmmBatteryMstList(), CmmBattery.class));}
        if (!para.getBatteryMstList().isEmpty()) {batteryRepo.saveInBatch(BeanMapUtils.mapListTo(para.getBatteryMstList(), Battery.class));}

        //4.更新业务表
        this.saveServiceOrderBasicInfo(para);
        //5.对部品明细做picking-Shipment处理。 当部品没有全部allocated时，报错回滚
        serviceOrderMgr.doPickingAndShipment(BeanMapUtils.mapTo(salesOrderRepo.findById(para.getSalesOrder().getSalesOrderId()), SalesOrderVO.class), uc.getPersonId(), uc.getPersonName());
        //6.创建Invoice
        serviceOrderMgr.doInvoice(para.getServiceOrder(), para.getSalesOrder());
        //计算job/part Amount 至 serviceOrder
        serviceOrderMgr.calculateOrderSummary(para.getServiceOrder().getServiceOrderId(), para.getServiceOrder().getRelatedSalesOrderId());
    }

    public List<CmmSpecialClaimSerialProVO> findSpecialClaimSerialProListByBulletinNo(String bulletinNo, Long cmmSerializedProId){

        List<CmmSpecialClaimVO> specialClaimList = BeanMapUtils.mapListTo(cmmSpecialClaimRepo.findByBulletinNo(bulletinNo), CmmSpecialClaimVO.class);

        return BeanMapUtils.mapListTo(cmmSpecialClaimSerialProRepo.findBySpecialClaimIdInAndSerializedProductId(specialClaimList.stream().map(CmmSpecialClaimVO::getSpecialClaimId).toList(), cmmSerializedProId), CmmSpecialClaimSerialProVO.class);
    }

    public List<ServiceOrderBatteryVO> findServiceOrderBatteryListByIds(List<Long> serviceOrderBatteryIds) {
        return BeanMapUtils.mapListTo(serviceOrderBatteryRepo.findAllById(serviceOrderBatteryIds), ServiceOrderBatteryVO.class);
    }

    public CmmSerializedProductVO findCmmSerializedProductByFrame(String frameNo) {
        return BeanMapUtils.mapTo(cmmSerializedProductRepo.findFirstByFrameNo(frameNo), CmmSerializedProductVO.class);
    }

    public CmmWarrantyBatteryVO findCmmWarrantyBatteryByBatteryId(Long batteryId) {
        return BeanMapUtils.mapTo(cmmWarrantyBatteryRepo.findFirstByBatteryId(batteryId), CmmWarrantyBatteryVO.class);
    }

    public List<CmmWarrantyModelPartVO> findWarrantyModelPartByModelCd(String modelCd){
        return cmmWarrantyModelPartRepo.findModelPartByModelCd(modelCd);
    }

    public SerializedProductVO findSerializedProductByCmmId(Long cmmSerializedProductId, String siteId) {
        return BeanMapUtils.mapTo(serializedProductRepo.findFirstByCmmSerializedProductIdAndSiteId(cmmSerializedProductId, siteId), SerializedProductVO.class);
    }

    public CmmWarrantySerializedProductVO findCmmWarrantySerializedProductBySerializedId(Long serializedProductId) {
        return BeanMapUtils.mapTo(cmmWarrantySerializedProductRepo.findFirstBySerializedProductId(serializedProductId), CmmWarrantySerializedProductVO.class);
    }

    public List<ServiceOrderJobVO> findServiceOrderJobListByIds(List<Long> serviceOrderJobIds) {
        return BeanMapUtils.mapListTo(serviceOrderJobRepo.findAllById(serviceOrderJobIds), ServiceOrderJobVO.class);
    }

    public CmmSerializedProductVO findCmmSerializedProductById(Long serializedProductId) {
        return BeanMapUtils.mapTo(cmmSerializedProductRepo.findById(serializedProductId), CmmSerializedProductVO.class);
    }

    public List<CmmBatteryVO> findCmmBatteryByBatteryNos(List<String> batteryNoList){
        return BeanMapUtils.mapListTo(cmmBatteryRepo.findByBatteryNoIn(batteryNoList), CmmBatteryVO.class);
    }

    public CmmBatteryVO findCmmBatteryById(Long cmmBatteryId) {
        return BeanMapUtils.mapTo(cmmBatteryRepo.findById(cmmBatteryId), CmmBatteryVO.class);
    }

    public List<SalesOrderItemVO> timelyFindServiceOrderPartListByIds(List<Long> serviceOrderPartIds) {
        return BeanMapUtils.mapListTo(salesOrderItemRepo.findAllById(serviceOrderPartIds), SalesOrderItemVO.class);
    }

    public List<SalesOrderItemVO> timelyFindServiceOrderPartListBySalesOrderId(Long salesOrderId) {
        return BeanMapUtils.mapListTo(salesOrderItemRepo.findBySalesOrderId(salesOrderId), SalesOrderItemVO.class);
    }

    public BatteryVO findBatteryByCmmId(String siteId, Long cmmBatteryId) {
        return BeanMapUtils.mapTo(batteryRepo.findFirstBySiteIdAndCmmBatteryInfoId(siteId, cmmBatteryId), BatteryVO.class);
    }

    public SVM0130PrintBO get0KmJobCardHeaderData(Long serviceOrderId) {
        return serviceOrderRepo.get0KmJobCardHeaderData(serviceOrderId);
    }

    public List<SVM0130PrintServiceJobBO> get0KmJobCardJobList(Long serviceOrderId) {
        return serviceOrderJobRepo.get0KmJobCardJobList(serviceOrderId);
    }

    public List<SVM0130PrintServicePartBO> get0KmJobCardPartList(Long serviceOrderId) {
        return serviceOrderRepo.get0KmJobCardPartList(serviceOrderId);
    }

    public SVM0130PrintBO get0KmServicePaymentHeaderData(Long serviceOrderId) {
        return serviceOrderRepo.get0KmServicePaymentHeaderData(serviceOrderId);
    }

    public List<SVM0130PrintServiceJobBO> get0KmServicePaymentJobList(Long serviceOrderId) {
        return serviceOrderJobRepo.get0KmServicePaymentJobList(serviceOrderId);
    }

    public List<SVM0130PrintServicePartBO> get0KmServicePaymentPartList(Long serviceOrderId) {
        return serviceOrderRepo.get0KmServicePaymentPartList(serviceOrderId);
    }

    private void generateServiceOrderNo(SVM013001Parameter para) {

        String orderNo = generateNoMgr.generateNonSerializedItemSalesOrderNo(para.getServiceOrder().getSiteId(), para.getServiceOrder().getFacilityId());

        para.getServiceOrder().setOrderNo(orderNo);
        para.getSalesOrder().setOrderNo(orderNo);
    }
}
