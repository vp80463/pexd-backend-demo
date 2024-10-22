package com.a1stream.web.app.controller.test;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.bo.ModifyPurchaseOrderBO;
import com.a1stream.common.bo.SalesReturnBO;
import com.a1stream.common.bo.SpManifestItemBO;
import com.a1stream.domain.form.master.MyTestForm;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.InvoiceVO;
import com.a1stream.domain.vo.PickingListVO;
import com.a1stream.domain.vo.ProductCostVO;
import com.a1stream.domain.vo.ReceiptSlipItemVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.domain.vo.StoringLineVO;
import com.a1stream.unit.facade.MyTestFacade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/***
 * 注意：这是测试接口用的，实际开发中controller中参数应为Form
 */
@RestController
@RequestMapping("public/mytest")
public class MyTestController implements RestProcessAware{

    @Resource
    private MyTestFacade testFacade;

    @PostMapping(value = "/doStockAdjustMinus.json")
    public void doStockAdjustMinus(@RequestBody final MyTestForm model) {

        String siteId = model.getSiteId();
        Long pointId = model.getLong1();
        String adjustType = model.getString1();
        Long fromLocationId = model.getLong2();
        Long toLocationId = model.getLocationId();
        Long productId = model.getProductId();
        String reasonId = model.getString2();
        BigDecimal adjustedQty = model.getDecimal1();

        testFacade.doStockAdjustMinus(siteId, pointId, adjustType, fromLocationId, toLocationId, productId, reasonId, adjustedQty);
    }

    @PostMapping(value = "/doStockAdjustPlus.json")
    public void doStockAdjustPlus(@RequestBody final MyTestForm model) {

        String siteId = model.getSiteId();
        Long pointId = model.getLong1();
        String adjustType = model.getString1();
        Long fromLocationId = model.getLong2();
        Long toLocationId = model.getLocationId();
        Long productId = model.getProductId();
        String reasonId = model.getString2();
        BigDecimal adjustedQty = model.getDecimal1();
        BigDecimal productCost = model.getDecimal2();

        testFacade.doStockAdjustPlus(siteId, pointId, adjustType, fromLocationId, toLocationId, productId, reasonId, adjustedQty, productCost);
    }

    @PostMapping(value = "/doLocationStockMovement.json")
    public void doLocationStockMovement(@RequestBody final MyTestForm model) {

        String siteId = model.getSiteId();
        Long stockPointId = model.getFacilityId();
        Long fromLocationId = model.getLong2();
        Long toLocationId = model.getLocationId();
        Long productId = model.getProductId();
        Long picId = model.getLong1();
        BigDecimal targetQty = model.getDecimal1();

        testFacade.doLocationStockMovement(siteId, productId, stockPointId, fromLocationId, toLocationId, targetQty, picId);
    }

    @PostMapping(value = "/createPickingListInfos.json")
    public List<PickingListVO> createPickingListInfos(@RequestBody final MyTestForm model) {

        List<Long> deliveryOrderIds = model.getIds();
        int maxDU = model.getInt1();
        int maxLine = model.getInt2();
        String batchNo = model.getString1();
        String siteId = model.getSiteId();

        return testFacade.createPickingListInfos(deliveryOrderIds, maxDU, maxLine, batchNo, siteId);
    }

    @PostMapping(value = "/doPickingCompletion.json")
    public List<Long> doPickingCompletion(@RequestBody final List<Long> deliveryOrderIdToReports) {

        return testFacade.doPickingCompletion(deliveryOrderIdToReports);
    }

    @PostMapping(value = "/updateForFast.json")
    public void updateForFast(@RequestBody final MyTestForm model) {

        testFacade.updateForFast(model.getLong1(), model.getSalesOrderItemList());
    }

    @PostMapping(value = "/doCancel.json")
    public void doCancel(@RequestBody final MyTestForm model) {

        testFacade.doCancel(model.getLong1());
    }

    @PostMapping(value = "/doShippingCompletion_so.json")
    public void doShippingCompletion_so(@RequestBody final List<DeliveryOrderItemVO> deliveryOrderItems) {

        testFacade.doShippingCompletion_so(deliveryOrderItems);
    }

    @PostMapping(value = "/updateProductOrderResultSummary.json")
    public void updateProductOrderResultSummary(@RequestBody final MyTestForm model) {

        // String allocateDueDate, Long facilityId, Long productId, String siteId,BigDecimal qty
        testFacade.updateProductOrderResultSummary(model.getReceiptSlipType(), model.getLong1(), model.getLong2(), model.getSiteId(), model.getDecimal1());
    }


    @PostMapping(value = "/doCostCalculation.json")
    public ProductCostVO doCostCalculation(@RequestBody final MyTestForm model) {

//        testService.doCostCalculation(siteId, receiptProductId, receiptPrice, receiptQty, receivePercent, receiveFacilityId);
        return testFacade.doCostCalculation(model.getSiteId(), model.getLong1(), model.getDecimal1(), model.getDecimal2(), model.getDecimal3(), model.getLong2());
    }

    /**
     * InventoryManager
     */
    @PostMapping(value = "/doUpdateProductStockStatusMinusQty.json")
    public void doUpdateProductStockStatusMinusQty(@RequestBody final MyTestForm model) {

        testFacade.doUpdateProductStockStatusMinusQty(model.getSiteId(), model.getFacilityId(), model.getProductId(), model.getProductStockStatusType(), model.getMinusQty());
    }

    @PostMapping(value = "/doUpdateProductStockStatusPlusQty.json")
    public void doUpdateProductStockStatusPlusQty(@RequestBody final MyTestForm model) {

        testFacade.doUpdateProductStockStatusPlusQty(model.getSiteId(), model.getFacilityId(), model.getProductId(), model.getProductStockStatusType(), model.getMinusQty());
    }

    @PostMapping(value = "/doInventoryReceipt.json")
    public void doInventoryReceipt(@RequestBody final MyTestForm model, @AuthenticationPrincipal final PJUserDetails uc) {

        testFacade.doInventoryReceipt(model.getSelectedReceiptManifestItemIds(), model.getFacilityId(), model.getReceiptSlipType(), uc.getPersonId(), uc.getPersonName());
    }

    @PostMapping(value = "/doSalesReturn_inventory.json")
    public void doSalesReturn(@RequestBody final MyTestForm model, @AuthenticationPrincipal final PJUserDetails uc) {

        testFacade.doSalesReturn(model.getLong1(), uc.getPersonId(), uc.getPersonName());
    }

    @PostMapping(value = "/doStoringReport_Inventory.json")
    public void doStoringReport(@RequestBody final MyTestForm model) {

        testFacade.doStoringReportInventory(model.getStoringLineIds());
    }

    @PostMapping(value = "/doShippingCompletion_Inventory.json")
    public void doShippingCompletionInventory(@RequestBody final MyTestForm model, @AuthenticationPrincipal final PJUserDetails uc) {

        testFacade.doShippingCompletionInventory(model.getInventoryTransactionType(), model.getDeliveryOrderItemIds(), uc.getPersonId(), uc.getPersonName());
    }

    @PostMapping(value = "/doUpdateProductInventoryQtyByCondition.json")
    public void doUpdateProductInventoryQtyByCondition(@RequestBody final MyTestForm model) {

        testFacade.doUpdateProductInventoryQtyByCondition(model.getSiteId()
                                                        , model.getFacilityId()
                                                        , model.getProductId()
                                                        , model.getLocationId()
                                                        , model.getDecimal1()
                                                        , model.getProductInventoryId()
                                                        , model.getChangeType());
    }

    @PostMapping(value = "/doShippingRequest.json")
    public void doShippingRequest(@RequestBody final MyTestForm model) {

        testFacade.doShippingRequest(model.getDeliveryOrderItemIds());
    }

    @PostMapping(value = "/updateMainLocationOfProductInventory.json")
    public void updateMainLocationOfProductInventory(@RequestBody final MyTestForm model) {

        testFacade.updateMainLocationOfProductInventory(model.getLocationId()
                                                      , model.getSiteId()
                                                      , model.getProductId()
                                                      , model.getFacilityId()
                                                      , model.getLocationCd()
                                                      , model.getProductInventoryId());
    }

    /**
     * DeliveryOrderManager
     */
    @PostMapping(value = "/doReceiptSlipRegister.json")
    public void doReceiptSlipRegister(@RequestBody final List<Long> deliveryOrderIds) {

        testFacade.doReceiptSlipRegister(deliveryOrderIds);
    }

    @PostMapping(value = "/doShippingCompletion.json")
    public List <DeliveryOrderVO> doShippingCompletion(@RequestBody final List<Long> deliveryOrderIdList) {

        return testFacade.doShippingCompletion(deliveryOrderIdList);
    }

    @PostMapping(value = "/doInvoicing.json")
    public void doInvoicing(@RequestBody final List<Long> deliveryOrderIdList) {

        testFacade.doInvoicing(deliveryOrderIdList);
    }

    @PostMapping(value = "/doRegister.json")
    public void doRegister(@RequestBody final MyTestForm model) {

        testFacade.doRegister(model.getLong1(), model.getLong2());
    }

    /**
     * ReceiptSlipManager
     */
    @PostMapping(value = "/doSalesReturn.json")
    public ReceiptSlipVO doSalesReturn(@RequestBody final DeliveryOrderVO delieryOrder) {

        return testFacade.doSalesReturn(delieryOrder);
    }

    @PostMapping(value = "/createReceiptSlipBySupplierInvoice.json")
    public List <ReceiptSlipVO> createReceiptSlipBySupplierInvoice(@RequestBody final MyTestForm model) {

        return testFacade.createReceiptSlipBySupplierInvoice(model.getSelectedReceiptManifestItemIds(), model.getReceiptSlipType());
    }

    @PostMapping(value = "/checkManifestItems.json")
    public boolean checkManifestItems(@RequestBody List<Long> receiptManifestItemId) {

        return testFacade.checkManifestItems(receiptManifestItemId);
    }

    @PostMapping(value = "/doManifestImports.json")
    public void doManifestImports(@RequestBody List<SpManifestItemBO> spManifestItemBOList) {

        testFacade.doManifestImports(spManifestItemBOList);
    }

    /**
     * invoice
     */
    @PostMapping(value = "/doCreateInvoice.json")
    public List<InvoiceVO> doCreateInvoice(@RequestBody List<Long> deliveryOrderIds) {

        return testFacade.doCreateInvoice(deliveryOrderIds);
    }

    @PostMapping(value = "/doSalesReturn_invoice.json")
    public InvoiceVO doSalesReturn(@RequestBody SalesReturnBO model, @AuthenticationPrincipal final PJUserDetails uc) {

        String siteId = "YT350"; // uc.getDealerCode()
        return testFacade.doSalesReturn(model, siteId);
    }

    /**
     * PurchaseOrderManager
     */
    @PostMapping(value = "/doModifyPurchaseOrder.json")
    public void doModifyPurchaseOrder(@RequestBody final ModifyPurchaseOrderBO model) {
        testFacade.doModifyPurchaseOrder(model);
    }

    @PostMapping(value = "/doPurchaseOrderIssue.json")
    public void doPurchaseOrderIssue(@RequestBody final List<Long> purchaseOrderIds, @AuthenticationPrincipal final PJUserDetails uc) {

        testFacade.doPurchaseOrderIssue(purchaseOrderIds, uc);
    }

    @PostMapping(value = "/doPurchaseOrderReceipt.json")
    public void doPurchaseOrderReceipt(@RequestBody final List<ReceiptSlipItemVO> receiptSlipItemList, @AuthenticationPrincipal final PJUserDetails uc) {
        String siteId = "YT350"; // uc.getSiteId
        testFacade.doPurchaseOrderReceipt(receiptSlipItemList, siteId);
    }

    @PostMapping(value = "/doStoringReport.json")
    public void doStoringReport(@RequestBody final List<ReceiptSlipItemVO> receiptSlipItemList, @AuthenticationPrincipal final PJUserDetails uc) {
        String siteId = "YT350"; // uc.getSiteId
        testFacade.doStoringReport(receiptSlipItemList, siteId);
    }

    @PostMapping(value = "/generateOrderNo.json")
    public String generateOrderNo(@AuthenticationPrincipal final PJUserDetails uc) {

        return testFacade.generateNonSerializedItemPurchaseOrderNo("YT350", Long.valueOf(10000));
    }

    @PostMapping(value = "/doStoringInstruction.json")
    public List<Long> doStoringInstruction(@RequestBody final MyTestForm model) {
        return testFacade.doStoringInstruction(model.getLong1(), model.getString1(), model.getReceiptSlipInfos());
    }

    @PostMapping(value = "/doStoringReportStoringManager.json")
    public void doStoringReportStoringManager(@RequestBody final StoringLineVO storingLineInfo) {
        testFacade.doStoringReportStoringManager(storingLineInfo);
    }

    @PostMapping(value = "/doSalesReturnStoringManager.json")
    public List<Long> doSalesReturnStoringManager(@RequestBody final MyTestForm model) {
        return testFacade.doSalesReturnStoringManager(model.getLong1(), model.getReceiptSlipInfos());
    }
}