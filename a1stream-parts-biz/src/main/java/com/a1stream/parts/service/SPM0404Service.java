package com.a1stream.parts.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.domain.bo.parts.SPM040401BO;
import com.a1stream.domain.bo.parts.SPM040402BO;
import com.a1stream.domain.entity.PoCancelHistory;
import com.a1stream.domain.entity.PurchaseOrder;
import com.a1stream.domain.entity.PurchaseOrderItem;
import com.a1stream.domain.form.parts.SPM040401Form;
import com.a1stream.domain.form.parts.SPM040402Form;
import com.a1stream.domain.repository.PoCancelHistoryRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.PurchaseOrderItemRepository;
import com.a1stream.domain.repository.PurchaseOrderRepository;
import com.a1stream.domain.vo.PoCancelHistoryVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.PurchaseOrderItemVO;
import com.a1stream.domain.vo.PurchaseOrderVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;
/**
* 功能描述:
*
* MID2303
* 2024年6月14日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/14   Ruan Hansheng     New
*/
@Service
public class SPM0404Service {

    @Resource
    private PurchaseOrderRepository purchaseOrderRepository;

    @Resource
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Resource
    private PoCancelHistoryRepository poCancelHistoryRepository;

    @Resource
    private ProductStockStatusRepository productStockStatusRepository;

    @Resource
    private InventoryManager inventoryManager;

    public List<SPM040401BO> getPurchaseOrderList(SPM040401Form form, PJUserDetails uc) {

        return purchaseOrderRepository.getPurchaseOrderList(form, uc);
    }

    public void cancelPurchaseOrder(PurchaseOrderVO purchaseOrderVO, List<PurchaseOrderItemVO> purchaseOrderItemVOList, List<PoCancelHistoryVO> poCancelHistoryVOList) {

        if (!ObjectUtils.isEmpty(purchaseOrderVO)) {
            purchaseOrderRepository.save(BeanMapUtils.mapTo(purchaseOrderVO, PurchaseOrder.class));
        }
        
        purchaseOrderItemRepository.saveInBatch(BeanMapUtils.mapListTo(purchaseOrderItemVOList, PurchaseOrderItem.class));

        poCancelHistoryRepository.saveInBatch(BeanMapUtils.mapListTo(poCancelHistoryVOList, PoCancelHistory.class));
    }

    public List<PurchaseOrderItemVO> findPurchaseOrderItemVOList(Long purchaseOrderId) {

        return BeanMapUtils.mapListTo(purchaseOrderItemRepository.findByPurchaseOrderId(purchaseOrderId), PurchaseOrderItemVO.class);
    }

    public PurchaseOrderVO findPurchaseOrderVO(Long purchaseOrderId) {

        return BeanMapUtils.mapTo(purchaseOrderRepository.findByPurchaseOrderId(purchaseOrderId), PurchaseOrderVO.class);
    }

    public List<SPM040402BO> getPurchaseOrderItemList(SPM040402Form form, PJUserDetails uc) {

        return purchaseOrderItemRepository.getPurchaseOrderItemList(form, uc);
    }

    public void confirmPurchaseOrderItemList(PurchaseOrderVO purchaseOrderVO, List<PurchaseOrderItemVO> purchaseOrderItemVOList, List<PurchaseOrderItemVO> deletePurchaseOrderItemVOList) {

        if (!ObjectUtils.isEmpty(purchaseOrderVO)) {
            purchaseOrderRepository.save(BeanMapUtils.mapTo(purchaseOrderVO, PurchaseOrder.class));
        }
        
        purchaseOrderItemRepository.saveInBatch(BeanMapUtils.mapListTo(purchaseOrderItemVOList, PurchaseOrderItem.class));

        purchaseOrderItemRepository.deleteAllInBatch(BeanMapUtils.mapListTo(deletePurchaseOrderItemVOList, PurchaseOrderItem.class));
    }

    public List<ProductStockStatusVO> findProductStockStatusVOList(String siteId, Long facilityId, List<String> productStockStatusTypeList, List<Long> productIdList) {

        return BeanMapUtils.mapListTo(productStockStatusRepository.findBySiteIdAndFacilityIdAndProductStockStatusTypeInAndProductIdIn(siteId, facilityId, productStockStatusTypeList, productIdList), ProductStockStatusVO.class);
    }

    public void issuePurchaseOrderItemList(PurchaseOrderVO purchaseOrderVO, List<PurchaseOrderItemVO> purchaseOrderItemVOList, List<PurchaseOrderItemVO> deletePurchaseOrderItemVOList, Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap) {

        inventoryManager.updateProductStockStatusByMap(stockStatusVOChangeMap);

        if (!ObjectUtils.isEmpty(purchaseOrderVO)) {
            purchaseOrderRepository.save(BeanMapUtils.mapTo(purchaseOrderVO, PurchaseOrder.class));
        }
        
        purchaseOrderItemRepository.saveInBatch(BeanMapUtils.mapListTo(purchaseOrderItemVOList, PurchaseOrderItem.class));

        purchaseOrderItemRepository.deleteAllInBatch(BeanMapUtils.mapListTo(deletePurchaseOrderItemVOList, PurchaseOrderItem.class));
    }

    public void deletePurchaseOrder(PurchaseOrderVO purchaseOrderVO, List<PurchaseOrderItemVO> purchaseOrderItemVOList) {

        if (!ObjectUtils.isEmpty(purchaseOrderVO)) {
            purchaseOrderRepository.delete(BeanMapUtils.mapTo(purchaseOrderVO, PurchaseOrder.class));
        }
        
        purchaseOrderItemRepository.deleteAllInBatch(BeanMapUtils.mapListTo(purchaseOrderItemVOList, PurchaseOrderItem.class));
    }

    public SPM040401BO getPurchaseOrder(SPM040402Form form, PJUserDetails uc) {

        return purchaseOrderRepository.getPurchaseOrder(form, uc);
    }

    public void generateStockStatusVOMap(String siteId
                                       , Long facilityId
                                       , Long productId
                                       , String productStockStatusType
                                       , BigDecimal plusQty
                                       , Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap) {
        
        inventoryManager.generateStockStatusVOMap(siteId, facilityId, productId, plusQty, productStockStatusType, stockStatusVOChangeMap);
    }
}
