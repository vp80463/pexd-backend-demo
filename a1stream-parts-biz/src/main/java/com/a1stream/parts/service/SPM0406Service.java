package com.a1stream.parts.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.manager.GenerateNoManager;
import com.a1stream.common.manager.PartsManager;
import com.a1stream.common.model.PartsInfoBO;
import com.a1stream.domain.bo.parts.SPM040601BO;
import com.a1stream.domain.entity.PurchaseOrder;
import com.a1stream.domain.entity.PurchaseOrderItem;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.PurchaseOrderItemRepository;
import com.a1stream.domain.repository.PurchaseOrderRepository;
import com.a1stream.domain.vo.MstProductVO;
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
public class SPM0406Service {

    @Resource
    private PurchaseOrderRepository purchaseOrderRepository;

    @Resource
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Resource
    private GenerateNoManager generateNoManager;

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private PartsManager partsManager;

    public void confirmPurchaseOrder(PurchaseOrderVO purchaseOrderVO, List<PurchaseOrderItemVO> purchaseOrderItemVOList) {

        if (!ObjectUtils.isEmpty(purchaseOrderVO)) {
            purchaseOrderRepository.save(BeanMapUtils.mapTo(purchaseOrderVO, PurchaseOrder.class));
        }
        
        purchaseOrderItemRepository.saveInBatch(BeanMapUtils.mapListTo(purchaseOrderItemVOList, PurchaseOrderItem.class));
    }

    public List<MstProductVO> getMstProductVOList(List<String> partsNoList, List<String> siteIdList) {

        return BeanMapUtils.mapListTo(mstProductRepository.findByProductCdInAndSiteIdIn(partsNoList, siteIdList), MstProductVO.class);
    }

    public List<SPM040601BO> getMstProductPriceList(List<String> partsNoList, String siteId) {

        return mstProductRepository.getMstProductPriceList(partsNoList, siteId);
    }

    public String purchaseOrderNo(String siteId, Long pointId) {

        return generateNoManager.generateNonSerializedItemPurchaseOrderNo(siteId, pointId);
    }

    public List<PartsInfoBO> getPartsInfoList(List<String> partsNoList, PJUserDetails uc) {

        return partsManager.getYamahaPartsInfoList(partsNoList, uc.getDefaultPointId(), uc.getTaxPeriod(),uc.getDealerCode());
    }
}
