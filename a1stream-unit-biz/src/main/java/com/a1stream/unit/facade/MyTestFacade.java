package com.a1stream.unit.facade;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.bo.ModifyPurchaseOrderBO;
import com.a1stream.common.bo.SalesReturnBO;
import com.a1stream.common.bo.SpManifestItemBO;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.InvoiceVO;
import com.a1stream.domain.vo.PickingListVO;
import com.a1stream.domain.vo.ProductCostVO;
import com.a1stream.domain.vo.ReceiptSlipItemVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.StoringLineVO;
import com.a1stream.unit.service.MyTestService;

import jakarta.annotation.Resource;

@Component
public class MyTestFacade {

    @Resource
    private MyTestService testService;

    public void doStockAdjustMinus(String siteId
            , Long pointId
            , String adjustType
            , Long fromLocationId
            , Long toLocationId
            , Long productId
            , String reasonId
            , BigDecimal adjustedQty) {

            testService.doStockAdjustMinus(siteId, pointId, adjustType, fromLocationId, toLocationId, productId, reasonId, adjustedQty);
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
        testService.doStockAdjustPlus(siteId, pointId, adjustType, fromLocationId, toLocationId, productId, reasonId, adjustedQty, productCost);
    }

    public void doLocationStockMovement(String siteId
            , Long productId
            , Long stockPointId
            , Long fromLocationId
            , Long toLocationId
            , BigDecimal targetQty
            , Long picId) {
        testService.doLocationStockMovement(siteId, productId, stockPointId, fromLocationId, toLocationId, targetQty, picId);
    }

    public List<PickingListVO> createPickingListInfos(List<Long> deliveryOrderIds
            , int maxDU
            , int maxLine
            , String batchNo
            , String siteId) {

        return testService.createPickingListInfos(deliveryOrderIds, maxDU, maxLine, batchNo, siteId);
    }

    public List<Long> doPickingCompletion(List<Long> deliveryOrderIdToReports) {

        return testService.doPickingCompletion(deliveryOrderIdToReports);
    }

    public void updateForFast(Long salesOrderId, List<SalesOrderItemVO> salesOrderItemList) {

        testService.updateForFast(salesOrderId, salesOrderItemList);
    }

    public void doCancel(Long salesOrderItemId) {

        testService.doCancel(salesOrderItemId);
    }

    public void doShippingCompletion_so(List<DeliveryOrderItemVO> deliveryOrderItems) {

        testService.doShippingCompletion_so(deliveryOrderItems);
    }

    public void updateProductOrderResultSummary(String allocateDueDate, Long facilityId, Long productId, String siteId,BigDecimal qty) {

        testService.updateProductOrderResultSummary(allocateDueDate, facilityId, productId, siteId, qty);
    }

    public ProductCostVO doCostCalculation(String siteId
            , Long receiptProductId
            , BigDecimal receiptPrice
            , BigDecimal receiptQty
            , BigDecimal receivePercent
            , Long receiveFacilityId) {

        return testService.doCostCalculation(siteId, receiptProductId, receiptPrice, receiptQty, receivePercent, receiveFacilityId);
    }

    public void doUpdateProductStockStatusMinusQty(String siteId
            , Long facilityId
            , Long productId
            , String productStockStatusType
            , BigDecimal minusQty) {

        testService.doUpdateProductStockStatusMinusQty(siteId, facilityId, productId, productStockStatusType, minusQty);
    }

    public void doUpdateProductStockStatusPlusQty(String siteId
            , Long facilityId
            , Long productId
            , String productStockStatusType
            , BigDecimal plusQty) {

        testService.doUpdateProductStockStatusPlusQty(siteId, facilityId, productId, productStockStatusType, plusQty);
    }

    public void doInventoryReceipt(List<Long> receiptSlipItemIds, Long targetReceiptPointId, String inventoryTransactionType, Long personId, String personNm) {
        testService.doInventoryReceipt(receiptSlipItemIds, targetReceiptPointId, inventoryTransactionType, personId, personNm);
    }

    public void doSalesReturn(Long receiptSlipId, Long personId, String personNm) {
        testService.doSalesReturn(receiptSlipId, personId, personNm);
    }

    public void doReceiptSlipRegister(List<Long> deliveryOrderIds) {

        testService.doReceiptSlipRegister(deliveryOrderIds);
    }

    public List <DeliveryOrderVO> doShippingCompletion(List<Long> deliveryOrderIdList) {

        return testService.doShippingCompletion(deliveryOrderIdList);
    }

    public void doInvoicing(List<Long> deliveryOrderIdList) {

        testService.doInvoicing(deliveryOrderIdList);
    }

    public void doRegister(Long deliveryOrderId, Long stockPointId) {

        testService.doRegister(deliveryOrderId, stockPointId);
    }

    public ReceiptSlipVO doSalesReturn(DeliveryOrderVO delieryOrder) {
        return testService.doSalesReturn(delieryOrder);
    }

    public List <ReceiptSlipVO> createReceiptSlipBySupplierInvoice(List<Long> selectedReceiptManifestItemIds, String receiptSlipType) {
        return testService.createReceiptSlipBySupplierInvoice(selectedReceiptManifestItemIds, receiptSlipType);
    }

    public boolean checkManifestItems(List<Long> receiptManifestItemId) {
        return testService.checkManifestItems(receiptManifestItemId);
    }

    public void doManifestImports(List<SpManifestItemBO> spManifestItemBOList) {
        testService.doManifestImports(spManifestItemBOList);
    }

    public List<InvoiceVO> doCreateInvoice(List<Long> deliveryOrderIds) {
        return testService.doCreateInvoice(deliveryOrderIds);
    }

    public InvoiceVO doSalesReturn(SalesReturnBO model, String siteId) {
        return testService.doSalesReturn(model, siteId);
    }

    public String generateNonSerializedItemPurchaseOrderNo(String siteId, Long stockPointId) {

    	return testService.generateNonSerializedItemPurchaseOrderNo(siteId, stockPointId);
    }

    public void doModifyPurchaseOrder(ModifyPurchaseOrderBO model) {
        testService.doModifyPurchaseOrder(model);
    }

    public void doPurchaseOrderIssue(List<Long> purchaseOrderIds, PJUserDetails uc) {
        testService.doPurchaseOrderIssue(purchaseOrderIds, uc);
    }

    public void doPurchaseOrderReceipt(List<ReceiptSlipItemVO> receiptSlipItemList, String siteId) {
        testService.doPurchaseOrderReceipt(receiptSlipItemList, siteId);
    }

    public void doStoringReport(List<ReceiptSlipItemVO> receiptSlipItemList, String siteId) {
        testService.doStoringReport(receiptSlipItemList, siteId);
    }

    public List<Long> doStoringInstruction(Long storingPointId, String inventoryTransactionType, List<ReceiptSlipVO> receiptSlipInfos) {
        return testService.doStoringInstruction(storingPointId, inventoryTransactionType, receiptSlipInfos);
    }

    public List<Long> doSalesReturnStoringManager(Long storingPointId, List<ReceiptSlipVO> receiptSlipInfos) {
        return testService.doSalesReturnStoringManager(storingPointId, receiptSlipInfos);
    }

    public void doStoringReportStoringManager(StoringLineVO storingLineInfo) {
        storingLineInfo.setDateCreated(LocalDateTime.now());
        testService.doStoringReportStoringManager(storingLineInfo);
    }

    public void doStoringReportInventory(Set<Long> storingLineIds) {

        testService.doStoringReportInventory(storingLineIds);
    }

    public void doShippingCompletionInventory(String inventoryTransactionType, List<Long> deliveryOrderItemIds, Long personId, String personNm) {

        testService.doShippingCompletionInventory(inventoryTransactionType,deliveryOrderItemIds, personId, personNm);
    }

    public void doUpdateProductInventoryQtyByCondition(String siteId, Long facilityId, Long productId, Long locationId, BigDecimal changeQty, Long productInventoryId, String changeType) {

        testService.doUpdateProductInventoryQtyByCondition(siteId, facilityId, productId, locationId, changeQty, productInventoryId, changeType);
    }

    public void doShippingRequest(List<Long> deliveryOrderItemIds) {

        testService.doShippingRequest(deliveryOrderItemIds);
    }

    public void updateMainLocationOfProductInventory(Long LocationId, String SiteId, Long ProductId, Long FacilityId, String LocationCd, Long ProductInventoryId) {

        testService.updateMainLocationOfProductInventory(LocationId, SiteId, ProductId, FacilityId, LocationCd, ProductInventoryId);
    }
}
