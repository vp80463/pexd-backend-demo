package com.a1stream.unit.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.bo.ModifyPurchaseOrderBO;
import com.a1stream.common.bo.SalesReturnBO;
import com.a1stream.common.bo.SpManifestItemBO;
import com.a1stream.common.manager.CostManager;
import com.a1stream.common.manager.DeliveryOrderManager;
import com.a1stream.common.manager.GenerateNoManager;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.common.manager.InvoiceManager;
import com.a1stream.common.manager.PickingInstructionManager;
import com.a1stream.common.manager.PurchaseOrderManager;
import com.a1stream.common.manager.ReceiptSlipManager;
import com.a1stream.common.manager.SalesOrderManager;
import com.a1stream.common.manager.StoringManager;
import com.a1stream.domain.repository.DeliveryOrderItemRepository;
import com.a1stream.domain.repository.DeliveryOrderRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.ReceiptManifestItemRepository;
import com.a1stream.domain.repository.ReceiptSlipItemRepository;
import com.a1stream.domain.repository.ReceiptSlipRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.repository.StoringLineRepository;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.InvoiceVO;
import com.a1stream.domain.vo.PickingListVO;
import com.a1stream.domain.vo.ProductCostVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.ReceiptManifestItemVO;
import com.a1stream.domain.vo.ReceiptSlipItemVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.StoringLineVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class MyTestService {

    @Resource
    private GenerateNoManager generateNoManager;

    @Resource
    private PurchaseOrderManager purchaseOrderManager;

    @Resource
    private ReceiptSlipManager receiptSlipManager;

    @Resource
    private InvoiceManager invoiceManager;

    @Resource
    private DeliveryOrderManager deliveryOrderManager;

    @Resource
    private InventoryManager inventoryManager;

    @Resource
    private CostManager costManager;

    @Resource
    private StoringManager storingManager;

    @Resource
    private SalesOrderManager salesOrderManager;

    @Resource
    private PickingInstructionManager pickingInstMgr;

    @Resource
    private ReceiptManifestItemRepository receiptManifestItemRepo;

    @Resource
    private ReceiptSlipItemRepository receiptSlipItemRepo;

    @Resource
    private ReceiptSlipRepository receiptSlipRepo;

    @Resource
    private DeliveryOrderRepository deliveryOrderRepo;

    @Resource
    private SalesOrderRepository salesOrderRepo;

    @Resource
    private SalesOrderItemRepository salesOrderItemRepo;

    @Resource
    private StoringLineRepository storingLineRepo;

    @Resource
    private DeliveryOrderItemRepository deliveryOrderItemRepo;

    @Resource
    private ProductInventoryRepository productInventoryRepo;

    @Resource
    private MstProductRepository productRepo;

    @Resource
    private MstFacilityRepository facilityRepo;

    public void doStockAdjustMinus(String siteId
                , Long pointId
                , String adjustType
                , Long fromLocationId
                , Long toLocationId
                , Long productId
                , String reasonId
                , BigDecimal adjustedQty) {

        inventoryManager.doStockAdjustMinus(siteId, pointId, adjustType, fromLocationId, toLocationId, productId, reasonId, adjustedQty,null,null);
    }

    public void doStockAdjustPlus(String siteId
            , Long pointId
            , String adjustType
            , Long fromLocationId
            , Long toLocationId
            , Long productId
            , String reasonId
            , BigDecimal adjustedQty
            , BigDecimal productCost) {
        inventoryManager.doStockAdjustPlus(siteId, pointId, adjustType, fromLocationId, toLocationId, productId, reasonId, adjustedQty, productCost,null,null);
    }

    public void doLocationStockMovement(String siteId
            , Long productId
            , Long stockPointId
            , Long fromLocationId
            , Long toLocationId
            , BigDecimal targetQty
            , Long picId) {
        inventoryManager.doLocationStockMovement(siteId, productId, stockPointId, fromLocationId, toLocationId, targetQty, picId);
    }

    public List<PickingListVO> createPickingListInfos(List<Long> deliveryOrderIds
            , int maxDU
            , int maxLine
            , String batchNo
            , String siteId) {

        List<DeliveryOrderVO> deliveryOrders = BeanMapUtils.mapListTo(deliveryOrderRepo.findByDeliveryOrderIdIn(deliveryOrderIds), DeliveryOrderVO.class);

        return pickingInstMgr.createPickingListInfos(deliveryOrders, maxDU, maxLine, batchNo, siteId);
    }

    public List<Long> doPickingCompletion(List<Long> deliveryOrderIdToReports) {

        return pickingInstMgr.doPickingCompletion(deliveryOrderIdToReports);
    }

    public void updateForFast(Long salesOrderId, List<SalesOrderItemVO> salesOrderItemList) {

        SalesOrderVO salesOrderVO = BeanMapUtils.mapTo(salesOrderRepo.findBySalesOrderId(salesOrderId), SalesOrderVO.class);
        salesOrderManager.updateForFast(salesOrderVO, salesOrderItemList);
    }

    public void doCancel(Long salesOrderItemId) {

        SalesOrderItemVO salesOrderItemVO = BeanMapUtils.mapTo(salesOrderItemRepo.findBySalesOrderItemId(salesOrderItemId), SalesOrderItemVO.class);

        salesOrderManager.doCancel(salesOrderItemVO);
    }

    public void doShippingCompletion_so(List<DeliveryOrderItemVO> deliveryOrderItems) {

        salesOrderManager.doShippingCompletion(deliveryOrderItems);
    }

    public void updateProductOrderResultSummary(String allocateDueDate, Long facilityId, Long productId, String siteId,BigDecimal qty) {

        salesOrderManager.updateProductOrderResultSummary(allocateDueDate, facilityId, productId, siteId, qty);
    }

    public ProductCostVO doCostCalculation(String siteId
            , Long receiptProductId
            , BigDecimal receiptPrice
            , BigDecimal receiptQty
            , BigDecimal receivePercent
            , Long receiveFacilityId) {

        return costManager.doCostCalculation(siteId, receiptProductId, receiptPrice, receiptQty, receivePercent, receiveFacilityId);
    }

    public void doUpdateProductStockStatusMinusQty(String siteId
            , Long facilityId
            , Long productId
            , String productStockStatusType
            , BigDecimal minusQty) {

        inventoryManager.doUpdateProductStockStatusMinusQty(siteId, facilityId, productId, productStockStatusType, minusQty);
    }

    public void doUpdateProductStockStatusPlusQty(String siteId
            , Long facilityId
            , Long productId
            , String productStockStatusType
            , BigDecimal plusQty) {

        inventoryManager.doUpdateProductStockStatusPlusQty(siteId, facilityId, productId, productStockStatusType, plusQty);
    }

    public void doInventoryReceipt(List<Long> receiptSlipItemIds, Long targetReceiptPointId, String inventoryTransactionType, Long personId, String personNm) {

        List<ReceiptSlipItemVO> receiptSlipItemVOs = BeanMapUtils.mapListTo(receiptSlipItemRepo.findByReceiptSlipItemIdIn(receiptSlipItemIds), ReceiptSlipItemVO.class);
        inventoryManager.doInventoryReceipt(receiptSlipItemVOs, targetReceiptPointId, inventoryTransactionType, personId, personNm);
    }

    public void doSalesReturn(Long receiptSlipId, Long personId, String personNm) {

        ReceiptSlipVO receiptSlipVO = BeanMapUtils.mapTo(receiptSlipRepo.findByReceiptSlipId(receiptSlipId), ReceiptSlipVO.class);
        inventoryManager.doSalesReturn(receiptSlipVO, personId, personNm);
    }
    public void doStoringReportInventory(Set<Long> storingLineIds) {

        List<StoringLineVO> storingLines = BeanMapUtils.mapListTo(storingLineRepo.findByStoringLineIdIn(storingLineIds), StoringLineVO.class);
        inventoryManager.doStoringReport(storingLines);
    }

    public void doShippingCompletionInventory(String inventoryTransactionType, List<Long> deliveryOrderItemIds, Long personId, String personNm) {

        List<DeliveryOrderItemVO> deliveryOrderItems = BeanMapUtils.mapListTo(deliveryOrderItemRepo.findByDeliveryOrderItemIdIn(deliveryOrderItemIds), DeliveryOrderItemVO.class);
        inventoryManager.doShippingCompletion(deliveryOrderItems, inventoryTransactionType, personId, personNm);
    }

    public void doUpdateProductInventoryQtyByCondition(String siteId, Long facilityId, Long productId, Long locationId, BigDecimal changeQty, Long productInventoryId, String changeType) {

        ProductInventoryVO productInventoryVO = BeanMapUtils.mapTo(productInventoryRepo.findByProductInventoryId(productInventoryId), ProductInventoryVO.class);
        inventoryManager.doUpdateProductInventoryQtyByCondition(siteId, facilityId, productId, locationId, changeQty, productInventoryVO, changeType);
    }

    public void doShippingRequest(List<Long> deliveryOrderItemIds) {

        List<DeliveryOrderItemVO> deliveryOrderItems = BeanMapUtils.mapListTo(deliveryOrderItemRepo.findByDeliveryOrderItemIdIn(deliveryOrderItemIds), DeliveryOrderItemVO.class);
        inventoryManager.doShippingRequest(deliveryOrderItems);
    }

    public void updateMainLocationOfProductInventory(Long locationId, String siteId, Long productId, Long facilityId, String locationCd, Long productInventoryId) {

        ProductInventoryVO productInventoryVO = BeanMapUtils.mapTo(productInventoryRepo.findByProductInventoryId(productInventoryId), ProductInventoryVO.class);
        inventoryManager.updateMainLocationOfProductInventory(locationId, siteId, productId, facilityId, locationCd, productInventoryVO);
    }

    public void doReceiptSlipRegister(List<Long> deliveryOrderIds) {

        deliveryOrderManager.doReceiptSlipRegister(deliveryOrderIds);
    }

    public List <DeliveryOrderVO> doShippingCompletion(List<Long> deliveryOrderIdList) {

        return deliveryOrderManager.doShippingCompletion(deliveryOrderIdList);
    }

    public void doInvoicing(List<Long> deliveryOrderIdList) {

        deliveryOrderManager.doInvoicing(deliveryOrderIdList);
    }

    public void doRegister(Long deliveryOrderId, Long stockPointId) {

        DeliveryOrderVO deliveryOrder = BeanMapUtils.mapTo(deliveryOrderRepo.findByDeliveryOrderId(deliveryOrderId), DeliveryOrderVO.class);
        deliveryOrderManager.doRegister(deliveryOrder, stockPointId);
    }

    public ReceiptSlipVO doSalesReturn(DeliveryOrderVO delieryOrder) {
        return receiptSlipManager.doSalesReturn(delieryOrder);
    }

    public List <ReceiptSlipVO> createReceiptSlipBySupplierInvoice(List<Long> selectedReceiptManifestItemIds, String receiptSlipType) {
        return receiptSlipManager.createReceiptSlipBySupplierInvoice(selectedReceiptManifestItemIds, receiptSlipType);
    }

    public boolean checkManifestItems(List<Long> receiptManifestItemId) {

        List<ReceiptManifestItemVO> receiptManifestItemVOList = BeanMapUtils.mapListTo(receiptManifestItemRepo.findByReceiptManifestItemIdIn(receiptManifestItemId), ReceiptManifestItemVO.class);

        return receiptSlipManager.checkManifestItems(receiptManifestItemVOList);
    }

    public void doManifestImports(List<SpManifestItemBO> spManifestItemBOList) {
        receiptSlipManager.doManifestImports(spManifestItemBOList);
    }

    public List<InvoiceVO> doCreateInvoice(List<Long> deliveryOrderIds) {

        List<DeliveryOrderVO> deliveryOrderList = BeanMapUtils.mapListTo(deliveryOrderRepo.findByDeliveryOrderIdIn(deliveryOrderIds), DeliveryOrderVO.class);

        return invoiceManager.doCreateInvoice(deliveryOrderList);
    }

    public InvoiceVO doSalesReturn(SalesReturnBO model, String siteId) {
        return invoiceManager.doSalesReturn(model, siteId);
    }

    public String generateNonSerializedItemPurchaseOrderNo(String siteId, Long stockPointId) {
    	return generateNoManager.generateNonSerializedItemPurchaseOrderNo(siteId, stockPointId);
    }

    public void doModifyPurchaseOrder(ModifyPurchaseOrderBO model) {
        purchaseOrderManager.doModifyPurchaseOrder(model);
    }

    public void doPurchaseOrderIssue(List<Long> purchaseOrderIds, PJUserDetails uc) {
        purchaseOrderManager.doPurchaseOrderIssue(purchaseOrderIds, uc);
    }

    public void doPurchaseOrderReceipt(List<ReceiptSlipItemVO> receiptSlipItemList, String siteId) {
        purchaseOrderManager.doPurchaseOrderReceipt(receiptSlipItemList, siteId);
    }

    public void doStoringReport(List<ReceiptSlipItemVO> receiptSlipItemList, String siteId) {
        purchaseOrderManager.doStoringReport(receiptSlipItemList, siteId);
    }

    public List<Long> doStoringInstruction(Long storingPointId, String inventoryTransactionType, List<ReceiptSlipVO> receiptSlipInfos) {
        return storingManager.doStoringInstruction(storingPointId, inventoryTransactionType, receiptSlipInfos);
    }

    public void doStoringReportStoringManager(StoringLineVO storingLineInfo) {
        storingManager.doStoringReport(storingLineInfo);
    }

    public List<Long> doSalesReturnStoringManager(Long storingPointId, List<ReceiptSlipVO> receiptSlipInfos) {
        return storingManager.doSalesReturn(storingPointId, receiptSlipInfos);
    }
}
