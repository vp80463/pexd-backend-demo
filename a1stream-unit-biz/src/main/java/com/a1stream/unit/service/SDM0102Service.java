package com.a1stream.unit.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.domain.bo.unit.SDM010202Param;
import com.a1stream.domain.entity.Battery;
import com.a1stream.domain.entity.CmmPromotionOrder;
import com.a1stream.domain.entity.DeliveryOrder;
import com.a1stream.domain.entity.DeliveryOrderItem;
import com.a1stream.domain.entity.DeliverySerializedItem;
import com.a1stream.domain.entity.InventoryTransaction;
import com.a1stream.domain.entity.Invoice;
import com.a1stream.domain.entity.InvoiceItem;
import com.a1stream.domain.entity.InvoiceSerializedItem;
import com.a1stream.domain.entity.QueueEinvoice;
import com.a1stream.domain.entity.ReceiptSerializedItem;
import com.a1stream.domain.entity.ReceiptSlip;
import com.a1stream.domain.entity.ReceiptSlipItem;
import com.a1stream.domain.entity.SalesOrder;
import com.a1stream.domain.entity.SalesOrderItem;
import com.a1stream.domain.entity.SerializedProduct;
import com.a1stream.domain.entity.SerializedProductTran;
import com.a1stream.domain.repository.BatteryRepository;
import com.a1stream.domain.repository.CmmMstOrganizationRepository;
import com.a1stream.domain.repository.CmmPromotionOrderRepository;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.repository.CmmUnitPromotionItemRepository;
import com.a1stream.domain.repository.DeliveryOrderItemRepository;
import com.a1stream.domain.repository.DeliveryOrderRepository;
import com.a1stream.domain.repository.DeliverySerializedItemRepository;
import com.a1stream.domain.repository.InventoryTransactionRepository;
import com.a1stream.domain.repository.InvoiceItemRepository;
import com.a1stream.domain.repository.InvoiceRepository;
import com.a1stream.domain.repository.InvoiceSerializedItemRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.ProductCostRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.QueueEinvoiceRepository;
import com.a1stream.domain.repository.ReceiptSerializedItemRepository;
import com.a1stream.domain.repository.ReceiptSlipItemRepository;
import com.a1stream.domain.repository.ReceiptSlipRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.a1stream.domain.repository.SerializedProductTranRepository;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmMstOrganizationVO;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.CmmUnitPromotionItemVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductCostVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

/**
* 功能描述: Fast Shipping Report
*
* mid2303
* 2024年8月19日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/08/19   Ruan Hansheng   New
*/
@Service
public class SDM0102Service {

    @Resource
    private CmmMstOrganizationRepository cmmMstOrgRepo;

    @Resource
    private CmmSiteMasterRepository cmmSiteMstRepo;

    @Resource
    private MstFacilityRepository mstFacilityRepo;

    @Resource
    private MstProductRepository mstProdRepo;

    @Resource
    private ProductCostRepository productCostRepo;

    @Resource
    private SerializedProductRepository serialProductRepo;

    @Resource
    private ProductStockStatusRepository prodStockStsRepo;

    @Resource
    private BatteryRepository batteryRepo;

    @Resource
    private SalesOrderRepository soRepo;

    @Resource
    private SalesOrderItemRepository soItemRepo;

    @Resource
    private DeliveryOrderRepository doRepo;

    @Resource
    private DeliveryOrderItemRepository doItemRepo;

    @Resource
    private DeliverySerializedItemRepository deliverySerialItemRepo;

    @Resource
    private InventoryTransactionRepository invTranRepo;

    @Resource
    private SerializedProductTranRepository serialProdTranRepo;

    @Resource
    private InvoiceRepository invoiceRepo;

    @Resource
    private InvoiceItemRepository invoiceItemRepo;

    @Resource
    private InvoiceSerializedItemRepository invoiceSerialItemRepo;

    @Resource
    private QueueEinvoiceRepository queueEInvoiceRepo;

    @Resource
    private ReceiptSlipRepository receiptSlipRepo;

    @Resource
    private ReceiptSlipItemRepository receiptSlipItemRepo;

    @Resource
    private ReceiptSerializedItemRepository receiptSerialItemRepo;

    @Resource
    private CmmPromotionOrderRepository cmmUnitPromOrderRepo;

    @Resource
    private CmmUnitPromotionItemRepository cmmUnitPromItemRepo;

    @Resource
    private InventoryManager inventoryMgr;

    public Map<String, SerializedProductVO> getSerialProductMap(Long facilityId, Set<String> frameNoSet) {

        List<SerializedProductVO> serialProductList = BeanMapUtils.mapListTo(serialProductRepo.findByFacilityIdAndFrameNoIn(facilityId, frameNoSet), SerializedProductVO.class);

        return serialProductList.stream().collect(Collectors.toMap(SerializedProductVO::getFrameNo, Function.identity()));
    }

    public Map<Long, MstProductVO> getMstProductMap(Set<Long> productIds) {

        List<MstProductVO> mstProductList = BeanMapUtils.mapListTo(mstProdRepo.findByProductIdIn(productIds), MstProductVO.class);

        return mstProductList.stream().collect(Collectors.toMap(MstProductVO::getProductId, Function.identity()));
    }

    public Map<Long, ProductStockStatusVO> getProductStockStsMap(String siteId, Long facilityId, Set<Long> productIds, String productStockStatusType) {

        List<ProductStockStatusVO> productStockStsList = BeanMapUtils.mapListTo(prodStockStsRepo.findProductStockStatusList(siteId, facilityId, productIds, productStockStatusType), ProductStockStatusVO.class);

        return productStockStsList.stream().collect(Collectors.toMap(ProductStockStatusVO::getProductId, Function.identity()));
    }

    public Map<Long, ProductCostVO> getProductCostList(Set<Long> productIdSet, String costType, String siteId) {

        List<ProductCostVO> productCostList = BeanMapUtils.mapListTo(productCostRepo.findByProductIdInAndCostTypeAndSiteId(productIdSet, costType, siteId), ProductCostVO.class);

        return productCostList.stream().collect(Collectors.toMap(ProductCostVO::getProductId, Function.identity()));
    }

    public List<BatteryVO> getBatteryList(String siteId, Set<Long> serializedProductIdSet) {

        return BeanMapUtils.mapListTo(batteryRepo.findBySiteIdAndSerializedProductIdIn(siteId, serializedProductIdSet), BatteryVO.class);
    }

    public CmmMstOrganizationVO getMstOrgInfoByCd(String orgCd) {

        return BeanMapUtils.mapTo(cmmMstOrgRepo.findBySiteIdAndOrganizationCd(CommonConstants.CHAR_DEFAULT_SITE_ID, orgCd), CmmMstOrganizationVO.class);
    }

    public MstFacilityVO getMstFacility(Long facilityId) {

        return BeanMapUtils.mapTo(mstFacilityRepo.findByFacilityId(facilityId), MstFacilityVO.class);
    }

    public Map<Long, CmmUnitPromotionItemVO> getCmmUnitPromotionItemMap(Set<Long> cmmSerializedProductIdSet) {

        List<CmmUnitPromotionItemVO> unitPromoItems = BeanMapUtils.mapListTo(cmmUnitPromItemRepo.findByCmmSerializedProductIdIn(cmmSerializedProductIdSet), CmmUnitPromotionItemVO.class);

        return unitPromoItems.stream().collect(Collectors.toMap(CmmUnitPromotionItemVO::getCmmSerializedProductId, Function.identity()));
    }

    public CmmSiteMasterVO getCmmSiteMaster(String siteId) {

        return BeanMapUtils.mapTo(cmmSiteMstRepo.findFirstBySiteId(siteId), CmmSiteMasterVO.class);
    }


    public void maintainData(SDM010202Param param, String shippingType, boolean isYT00) {

        /**
         * 更新到DB
         */
        // SalesOrderInfo
        if (param.getSalesOrder() != null) {
            soRepo.save(BeanMapUtils.mapTo(param.getSalesOrder(), SalesOrder.class));
        }
        soItemRepo.saveInBatch(BeanMapUtils.mapListTo(param.getSoItemList(), SalesOrderItem.class));
        // DeliveryOrderInfo
        doRepo.save(BeanMapUtils.mapTo(param.getDeliveryOrder(), DeliveryOrder.class));
        doItemRepo.saveInBatch(BeanMapUtils.mapListTo(param.getDoItemList(), DeliveryOrderItem.class));
        // ProductStockStatus
        inventoryMgr.updateProductStockStatusByMapForSD(param.getStockStatusChangeMap());
        // DeliverySerializedItem
        deliverySerialItemRepo.saveInBatch(BeanMapUtils.mapListTo(param.getDeliverySeraialItemList(), DeliverySerializedItem.class));
        // Invoice
        invoiceRepo.save(BeanMapUtils.mapTo(param.getInvoice(), Invoice.class));
        // InvoiceItem
        invoiceItemRepo.saveInBatch(BeanMapUtils.mapListTo(param.getInvoiceItemList(), InvoiceItem.class));
        // InvoiceSerializedItem
        invoiceSerialItemRepo.saveInBatch(BeanMapUtils.mapListTo(param.getInvoiceSerialItemList(), InvoiceSerializedItem.class));
        // InventoryTransction
        invTranRepo.saveInBatch(BeanMapUtils.mapListTo(param.getInvTransList(), InventoryTransaction.class));
        // SerializedProduct
        serialProductRepo.saveInBatch(BeanMapUtils.mapListTo(param.getSerialProductList(), SerializedProduct.class));
        // Battery
        batteryRepo.saveInBatch(BeanMapUtils.mapListTo(param.getBatteryList(), Battery.class));
        // SerializedProductTran
        serialProdTranRepo.saveInBatch(BeanMapUtils.mapListTo(param.getSerialProductList(), SerializedProductTran.class));
        // QueueEinvoice
        if (param.getQueueEInvoice() != null) {
            queueEInvoiceRepo.save(BeanMapUtils.mapTo(param.getQueueEInvoice(), QueueEinvoice.class));
        }
        if (StringUtils.equals(shippingType, InventoryTransactionType.TRANSFEROUT.getCodeDbid())) {
            // ReceiptSlip
            receiptSlipRepo.save(BeanMapUtils.mapTo(param.getReceiptSlip(), ReceiptSlip.class));
            // ReceiptSlipItem
            receiptSlipItemRepo.saveInBatch(BeanMapUtils.mapListTo(param.getReceiptSlipItemList(), ReceiptSlipItem.class));
            // ReceiptSerializedItem
            receiptSerialItemRepo.saveInBatch(BeanMapUtils.mapListTo(param.getReceiptSerialItemList(), ReceiptSerializedItem.class));
        } else if (StringUtils.equals(shippingType, InventoryTransactionType.SALESTOCKOUT.getCodeDbid()) && isYT00) {
            // CmmPromotionOrder
            cmmUnitPromOrderRepo.saveInBatch(BeanMapUtils.mapListTo(param.getCmmPromotOrderList(), CmmPromotionOrder.class));
        }
    }
}
