package com.a1stream.parts.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.manager.GenerateNoManager;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.common.manager.PartsManager;
import com.a1stream.common.model.PartsInfoBO;
import com.a1stream.domain.entity.PurchaseOrder;
import com.a1stream.domain.entity.PurchaseOrderItem;
import com.a1stream.domain.repository.MstOrganizationRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.PurchaseOrderItemRepository;
import com.a1stream.domain.repository.PurchaseOrderRepository;
import com.a1stream.domain.vo.MstOrganizationVO;
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
public class SPM0413Service {

    @Resource
    private PurchaseOrderRepository purchaseOrderRepository;

    @Resource
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Resource
    private ProductStockStatusRepository productStockStatusRepository;

    @Resource
    private MstOrganizationRepository mstOrganizationRepository;

    @Resource
    private GenerateNoManager generateNoManager;

    @Resource
    private InventoryManager inventoryManager;

    @Resource
    private PartsManager partsManager;

    public List<ProductStockStatusVO> findProductStockStatusVOList(String siteId, Long facilityId, List<String> productStockStatusTypeList, List<Long> productIdList) {

        return BeanMapUtils.mapListTo(productStockStatusRepository.findBySiteIdAndFacilityIdAndProductStockStatusTypeInAndProductIdIn(siteId, facilityId, productStockStatusTypeList, productIdList), ProductStockStatusVO.class);
    }

    public void confirmPurchaseOrder(PurchaseOrderVO purchaseOrderVO, List<PurchaseOrderItemVO> purchaseOrderItemVOList, Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap) {

        inventoryManager.updateProductStockStatusByMap(stockStatusVOChangeMap);

        if (!ObjectUtils.isEmpty(purchaseOrderVO)) {
            purchaseOrderRepository.save(BeanMapUtils.mapTo(purchaseOrderVO, PurchaseOrder.class));
        }
        
        purchaseOrderItemRepository.saveInBatch(BeanMapUtils.mapListTo(purchaseOrderItemVOList, PurchaseOrderItem.class));
    }

    public MstOrganizationVO getPartSupplier(String siteId) {

        return mstOrganizationRepository.getPartSupplier(siteId);
    }

    public String purchaseOrderNo(String siteId, Long pointId) {

        return generateNoManager.generateNonSerializedItemPurchaseOrderNo(siteId, pointId);
    }

    public void generateStockStatusVOMap(String siteId
                                       , Long facilityId
                                       , Long productId
                                       , String productStockStatusType
                                       , BigDecimal plusQty
                                       , Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap) {
        
        inventoryManager.generateStockStatusVOMap(siteId, facilityId, productId, plusQty, productStockStatusType, stockStatusVOChangeMap);
    }

    public List<PartsInfoBO> getPartsInfoList(List<String> partsNoList, PJUserDetails uc) {

        return partsManager.getYamahaPartsInfoList(partsNoList, uc.getDefaultPointId(), uc.getTaxPeriod(),uc.getDealerCode());
    }
}
