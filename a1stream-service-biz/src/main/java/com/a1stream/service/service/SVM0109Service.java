package com.a1stream.service.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.manager.ConsumerManager;
import com.a1stream.common.manager.GenerateNoManager;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.common.manager.PartsSalesStockAllocationManager;
import com.a1stream.common.manager.PurchaseOrderManager;
import com.a1stream.common.manager.ServiceOrderManager;
import com.a1stream.common.model.BaseConsumerForm;
import com.a1stream.domain.bo.service.SVM0109PrintBO;
import com.a1stream.domain.entity.Battery;
import com.a1stream.domain.entity.CmmBattery;
import com.a1stream.domain.entity.CmmRegistrationDocument;
import com.a1stream.domain.entity.ConsumerPrivacyPolicyResult;
import com.a1stream.domain.entity.SalesOrder;
import com.a1stream.domain.entity.SalesOrderItem;
import com.a1stream.domain.entity.ServiceOrder;
import com.a1stream.domain.entity.ServiceOrderBattery;
import com.a1stream.domain.entity.ServiceOrderFault;
import com.a1stream.domain.parameter.service.SVM010901Parameter;
import com.a1stream.domain.repository.BatteryRepository;
import com.a1stream.domain.repository.CmmBatteryRepository;
import com.a1stream.domain.repository.CmmRegistrationDocumentRepository;
import com.a1stream.domain.repository.ConsumerPrivacyPolicyResultRepository;
import com.a1stream.domain.repository.InvoiceItemRepository;
import com.a1stream.domain.repository.InvoiceRepository;
import com.a1stream.domain.repository.PoSoItemRelationRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.repository.ServiceOrderBatteryRepository;
import com.a1stream.domain.repository.ServiceOrderFaultRepository;
import com.a1stream.domain.repository.ServiceOrderRepository;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.ServiceOrderBatteryVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

/**
* 功能描述: Claim For Battery
*
* @author mid1341
*/
@Service
public class SVM0109Service {

    @Resource
    private ServiceOrderRepository orderRepo;

    @Resource
    private SalesOrderRepository salesOrderRepo;

    @Resource
    private SalesOrderItemRepository salesOrderItemRepo;

    @Resource
    private ServiceOrderBatteryRepository orderBatteryRepo;

    @Resource
    private ServiceOrderFaultRepository faultRepo;

    @Resource
    private CmmBatteryRepository cmmBatteryRepo;

    @Resource
    private BatteryRepository batteryRepo;

    @Resource
    private CmmRegistrationDocumentRepository registDocRepo;

    @Resource
    private ConsumerPrivacyPolicyResultRepository policyResultRepo;

    @Resource
    private PoSoItemRelationRepository poSoItemRelationRepo;
    @Resource
    private InvoiceRepository invoiceRepo;

    @Resource
    private InvoiceItemRepository invoiceItemRepo;

    @Resource
    private PurchaseOrderManager purchaseOrderMgr;

    @Resource
    private PartsSalesStockAllocationManager allocationManager;

    @Resource
    private InventoryManager inventoryMgr;

    @Resource
    private ConsumerManager consumerMgr;

    @Resource
    private GenerateNoManager generateNoMgr;

    @Resource
    private ServiceOrderManager serviceOrderMgr;

    @Resource
    private ServiceOrderRepository serviceOrderRepo;

    @Resource
    private ServiceOrderBatteryRepository serviceOrderBatteryRepo;

    public void maintainOrderData(SVM010901Parameter params) {

        saveOrderData2DB(params);

        // 调用预定逻辑, SalesOrderItem需从DB获取（保存后才生成主键）
        Long salesOrderId = params.getSalesOrderVO().getSalesOrderId();
        SalesOrderVO salesOrderInfo = BeanMapUtils.mapTo(salesOrderRepo.findBySalesOrderId(salesOrderId), SalesOrderVO.class);
        List<SalesOrderItemVO> orderItemVoList = BeanMapUtils.mapListTo(salesOrderItemRepo.findBySalesOrderId(salesOrderId), SalesOrderItemVO.class);
        allocationManager.executeStockAllocation(salesOrderInfo, orderItemVoList);

        // 根据预定后的情况，自动采购BO
        serviceOrderMgr.autoPurchaseBoPart(BeanMapUtils.mapTo(salesOrderRepo.findBySalesOrderId(salesOrderId), SalesOrderVO.class));
        //计算job/part Amount 至 serviceOrder
        Long serviceOrderId = params.getServiceOrderVO().getServiceOrderId();
        serviceOrderMgr.calculateOrderSummary(serviceOrderId, salesOrderId);
    }

    public void settleOderData(SVM010901Parameter params, PJUserDetails uc) {

        //1.由于可能出现更换consumerId的情况，优先更新consumer，并刷新原order中的ID
        BaseConsumerForm consumerForm = params.getConsumerBaseInfo();
        consumerMgr.saveOrUpdateConsumer(consumerForm);
        params.getServiceOrderVO().setConsumerId(consumerForm.getConsumerId());
        params.getSalesOrderVO().setCmmConsumerId(consumerForm.getConsumerId());

        saveOrderData2DB(params);

        //5.对部品明细做picking-Shipment处理。 当部品没有全部allocated时，报错回滚
        Long salesOrderId = params.getSalesOrderVO().getSalesOrderId();
        SalesOrderVO salesOrderInfo = BeanMapUtils.mapTo(salesOrderRepo.findBySalesOrderId(salesOrderId), SalesOrderVO.class);
        serviceOrderMgr.doPickingAndShipment(salesOrderInfo, uc.getPersonId(), uc.getPersonName());
        //6.创建Invoice
        serviceOrderMgr.doInvoice(params.getServiceOrderVO(), params.getSalesOrderVO());
        //计算job/part Amount 至 serviceOrder
        Long serviceOrderId = params.getServiceOrderVO().getServiceOrderId();
        serviceOrderMgr.calculateOrderSummary(serviceOrderId, salesOrderId);
    }

    public void cancelOrderData(SVM010901Parameter params) {

        saveOrderData2DB(params);
    }

    public void insertBatteryInfo(BatteryVO battery) {

        batteryRepo.save(BeanMapUtils.mapTo(battery, Battery.class));
    }

    private void saveOrderData2DB(SVM010901Parameter params) {

        if (StringUtils.equals(params.getAction(), CommonConstants.OPERATION_STATUS_NEW)) {

            String orderNo = generateNoMgr.generateBarreryServiceOrderNo(params.getSiteId(), params.getPointId());

            params.getServiceOrderVO().setOrderNo(orderNo);
            params.getSalesOrderVO().setOrderNo(orderNo);
        }

        // service_order
        orderRepo.save(BeanMapUtils.mapTo(params.getServiceOrderVO(), ServiceOrder.class));

        // sales_order
        salesOrderRepo.save(BeanMapUtils.mapTo(params.getSalesOrderVO(), SalesOrder.class));

        // sales_order_item
        salesOrderItemRepo.saveInBatch(BeanMapUtils.mapListTo(params.getSalesOrderItemVO(), SalesOrderItem.class));

        // cmm_battery
        cmmBatteryRepo.saveInBatch(BeanMapUtils.mapListTo(params.getCmmBatteryList(), CmmBattery.class));

        // battery
        batteryRepo.saveInBatch(BeanMapUtils.mapListTo(params.getBatteryList(), Battery.class));

        // cmm_registration_document
        if (!Objects.isNull(params.getCmmRegistDocument())) {
            registDocRepo.save(BeanMapUtils.mapTo(params.getCmmRegistDocument(), CmmRegistrationDocument.class));
        }

        // service_order_battery
        if (!Objects.isNull(params.getServiceBatteryVO())) {
            orderBatteryRepo.save(BeanMapUtils.mapTo(params.getServiceBatteryVO(), ServiceOrderBattery.class));
        }

        // service_order_fault
        if (!params.getRemoveFaultIds().isEmpty()) {
            faultRepo.deleteAllByIdInBatch(params.getRemoveFaultIds());
        }
        faultRepo.saveInBatch(BeanMapUtils.mapListTo(params.getSaveFaultList(), ServiceOrderFault.class));

        // consumer_privacy_policy_result
        if (!Objects.isNull(params.getPolicyResultVO())) {
            policyResultRepo.save(BeanMapUtils.mapTo(params.getPolicyResultVO(), ConsumerPrivacyPolicyResult.class));
        }

        // 处理part stock变更相关
        inventoryMgr.updateProductStockStatusByMap(params.getStockStatusVOChangeMap());
    }

    public SVM0109PrintBO getJobCardData(Long serviceOrderId) {
        return serviceOrderRepo.getJobCardData(serviceOrderId);
    }

    public SVM0109PrintBO getJobCardForDoData(Long serviceOrderId) {
        return serviceOrderRepo.getJobCardForDoData(serviceOrderId);
    }

    public SVM0109PrintBO getServicePaymentData(Long serviceOrderId) {
        return serviceOrderRepo.getServicePaymentData(serviceOrderId);
    }

    public SVM0109PrintBO getServicePaymentForDoData(Long serviceOrderId) {
        return serviceOrderRepo.getServicePaymentForDoData(serviceOrderId);
    }

    public ServiceOrderBatteryVO getClaimForBatteryReportDetailData(Long serviceOrderId) {
        return BeanMapUtils.mapTo(serviceOrderBatteryRepo.findFirstByServiceOrderId(serviceOrderId), ServiceOrderBatteryVO.class);
    }

}
