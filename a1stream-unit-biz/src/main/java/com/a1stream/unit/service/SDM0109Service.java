package com.a1stream.unit.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.PJConstants.ReceiptSlipStatus;
import com.a1stream.common.constants.PJConstants.SdStockStatus;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.domain.bo.unit.SDM010901BO;
import com.a1stream.domain.entity.OrderSerializedItem;
import com.a1stream.domain.entity.ReceiptManifest;
import com.a1stream.domain.entity.ReceiptManifestItem;
import com.a1stream.domain.entity.ReceiptManifestSerializedItem;
import com.a1stream.domain.entity.ReceiptSerializedItem;
import com.a1stream.domain.entity.ReceiptSlip;
import com.a1stream.domain.entity.ReceiptSlipItem;
import com.a1stream.domain.entity.SerializedProductTran;
import com.a1stream.domain.entity.StoringLine;
import com.a1stream.domain.entity.StoringLineItem;
import com.a1stream.domain.entity.StoringSerializedItem;
import com.a1stream.domain.form.unit.SDM010901Form;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.OrderSerializedItemRepository;
import com.a1stream.domain.repository.ReceiptManifestItemRepository;
import com.a1stream.domain.repository.ReceiptManifestRepository;
import com.a1stream.domain.repository.ReceiptManifestSerializedItemRepository;
import com.a1stream.domain.repository.ReceiptSerializedItemRepository;
import com.a1stream.domain.repository.ReceiptSlipItemRepository;
import com.a1stream.domain.repository.ReceiptSlipRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.a1stream.domain.repository.SerializedProductTranRepository;
import com.a1stream.domain.repository.StoringLineItemRepository;
import com.a1stream.domain.repository.StoringLineRepository;
import com.a1stream.domain.repository.StoringSerializedItemRepository;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.OrderSerializedItemVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.ReceiptManifestItemVO;
import com.a1stream.domain.vo.ReceiptManifestSerializedItemVO;
import com.a1stream.domain.vo.ReceiptManifestVO;
import com.a1stream.domain.vo.ReceiptSerializedItemVO;
import com.a1stream.domain.vo.ReceiptSlipItemVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.domain.vo.SerializedProductTranVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.domain.vo.StoringLineItemVO;
import com.a1stream.domain.vo.StoringLineVO;
import com.a1stream.domain.vo.StoringSerializedItemVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述: Dealer Return By Vehicle
*
* mid2303
* 2024年8月29日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/08/29   Ruan Hansheng   New
*/
@Service
public class SDM0109Service {

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepository;

    @Resource
    private CmmSerializedProductRepository cmmSerializedProductRepository;

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private SerializedProductRepository serializedProductRepository;

    @Resource
    private ReceiptManifestSerializedItemRepository receiptManifestSerializedItemRepository;

    @Resource
    private ReceiptSerializedItemRepository receiptSerializedItemRepository;

    @Resource
    private ReceiptManifestItemRepository receiptManifestItemRepository;

    @Resource
    private ReceiptManifestRepository receiptManifestRepository;

    @Resource
    private ReceiptSlipItemRepository receiptSlipItemRepository;

    @Resource
    private ReceiptSlipRepository receiptSlipRepository;

    @Resource
    private StoringSerializedItemRepository storingSerializedItemRepository;

    @Resource
    private StoringLineItemRepository storingLineItemRepository;

    @Resource
    private StoringLineRepository storingLineRepository;
    
    @Resource
    private SerializedProductTranRepository serializedProductTranRepository;

    @Resource
    private OrderSerializedItemRepository orderSerializedItemRepository;

    @Resource
    private InventoryManager inventoryManager;

    public List<SDM010901BO> getPointList() {

        return mstFacilityRepository.getPointList();
    }

    public List<CmmSiteMasterVO> getCmmSiteMasterVOList(Set<String> siteIdSet) {

        return BeanMapUtils.mapListTo(cmmSiteMasterRepository.findBySiteIdIn(siteIdSet), CmmSiteMasterVO.class);
    }

    public List<MstFacilityVO> getMstFacilityVOList(Set<String> siteIdSet, Set<String> facilityCdSet) {

        return BeanMapUtils.mapListTo(mstFacilityRepository.findBySiteIdInAndFacilityCdIn(siteIdSet, facilityCdSet), MstFacilityVO.class);
    }

    public List<CmmSerializedProductVO> getCmmSerializedProductVOList(Set<String> frameNoSet) {

        return BeanMapUtils.mapListTo(cmmSerializedProductRepository.findByFrameNoIn(frameNoSet), CmmSerializedProductVO.class);
    }

    public List<MstProductVO> getMstProductVOList(Set<Long> productIdSet) {

        return BeanMapUtils.mapListTo(mstProductRepository.findByProductIdIn(productIdSet), MstProductVO.class);
    }

    public List<SerializedProductVO> getSerializedProductVOList(String siteId, Set<String> frameNoSet) {

        return BeanMapUtils.mapListTo(serializedProductRepository.findBySiteIdAndFrameNoIn(siteId, frameNoSet), SerializedProductVO.class);
    }

    public List<ReceiptManifestSerializedItemVO> getReceiptManifestSerializedItemVOList(Set<Long> serializedProductIdSet, String siteId) {

        return BeanMapUtils.mapListTo(receiptManifestSerializedItemRepository.findBySerializedProductIdInAndSiteId(serializedProductIdSet, siteId), ReceiptManifestSerializedItemVO.class);
    }

    public List<ReceiptSerializedItemVO> getReceiptSerializedItemVOList(Set<Long> serializedProductIdSet, String siteId) {

        return BeanMapUtils.mapListTo(receiptSerializedItemRepository.findBySerializedProductIdInAndSiteId(serializedProductIdSet, siteId), ReceiptSerializedItemVO.class);
    }

    public List<ReceiptManifestItemVO> getReceiptManifestItemVOList(Set<Long> receiptManifestItemIdSet) {

        return BeanMapUtils.mapListTo(receiptManifestItemRepository.findByReceiptManifestItemIdIn(receiptManifestItemIdSet), ReceiptManifestItemVO.class);
    }

    public List<ReceiptManifestVO> getReceiptManifestVOList(Set<Long> receiptManifestIdSet) {

        return BeanMapUtils.mapListTo(receiptManifestRepository.findByReceiptManifestIdIn(receiptManifestIdSet), ReceiptManifestVO.class);
    }

    public List<ReceiptSlipItemVO> getReceiptSlipItemVOList(Set<Long> receiptSlipItemIdSet) {

        return BeanMapUtils.mapListTo(receiptSlipItemRepository.findByReceiptSlipItemIdIn(receiptSlipItemIdSet), ReceiptSlipItemVO.class);
    }

    public List<ReceiptSlipVO> getReceiptSlipVOList(Set<Long> receiptSlipIdSet) {

        return BeanMapUtils.mapListTo(receiptSlipRepository.findByReceiptSlipIdIn(receiptSlipIdSet), ReceiptSlipVO.class);
    }

    public List<StoringSerializedItemVO> getStoringSerializedItemVOList(String siteId, Set<Long> serializedProductIdSet) {

        return BeanMapUtils.mapListTo(storingSerializedItemRepository.findBySiteIdAndSerializedProductIdIn(siteId, serializedProductIdSet), StoringSerializedItemVO.class);
    }

    public List<StoringLineItemVO> getStoringLineItemVOList(Set<Long> storingLineItemIdSet) {

        return BeanMapUtils.mapListTo(storingLineItemRepository.findByStoringLineItemIdIn(storingLineItemIdSet), StoringLineItemVO.class);
    }

    public List<StoringLineVO> getStoringLineVOList(Set<Long> storingLineIdSet) {

        return BeanMapUtils.mapListTo(storingLineRepository.findByStoringLineIdIn(storingLineIdSet), StoringLineVO.class);
    }

    public List<SerializedProductTranVO> getSerializedProductTranVOList(Set<Long> serializedProductIdSet) {

        return BeanMapUtils.mapListTo(serializedProductTranRepository.findBySerializedProductIdIn(serializedProductIdSet), SerializedProductTranVO.class);
    }

    public List<OrderSerializedItemVO> getOrderSerializedItemVOList(Set<Long> serializedProductIdSet) {

        return BeanMapUtils.mapListTo(orderSerializedItemRepository.findBySerializedProductIdIn(serializedProductIdSet), OrderSerializedItemVO.class);
    }

    public void confirm(SDM010901Form form
                      , List<ReceiptManifestSerializedItemVO> receiptManifestSerializedItemVOList
                      , List<ReceiptManifestItemVO> receiptManifestItemVOList
                      , List<ReceiptManifestVO> receiptManifestVOList
                      , List<ReceiptSerializedItemVO> receiptSerializedItemVOList
                      , List<ReceiptSlipItemVO> receiptSlipItemVOList
                      , List<ReceiptSlipVO> receiptSlipVOList
                      , List<StoringSerializedItemVO> storingSerializedItemVOList
                      , List<StoringLineItemVO> storingLineItemVOList
                      , List<StoringLineVO> storingLineVOList
                      , List<SerializedProductTranVO> serializedProductTranVOList
                      , List<OrderSerializedItemVO> orderSerializedItemVOList) {

        receiptManifestSerializedItemRepository.deleteAllInBatch(BeanMapUtils.mapListTo(receiptManifestSerializedItemVOList, ReceiptManifestSerializedItem.class));
        receiptManifestItemRepository.deleteAllInBatch(BeanMapUtils.mapListTo(receiptManifestItemVOList, ReceiptManifestItem.class));
        receiptManifestRepository.deleteAllInBatch(BeanMapUtils.mapListTo(receiptManifestVOList, ReceiptManifest.class));
        receiptSerializedItemRepository.deleteAllInBatch(BeanMapUtils.mapListTo(receiptSerializedItemVOList, ReceiptSerializedItem.class));
        receiptSlipItemRepository.deleteAllInBatch(BeanMapUtils.mapListTo(receiptSlipItemVOList, ReceiptSlipItem.class));
        receiptSlipRepository.deleteAllInBatch(BeanMapUtils.mapListTo(receiptSlipVOList, ReceiptSlip.class));
        storingSerializedItemRepository.deleteAllInBatch(BeanMapUtils.mapListTo(storingSerializedItemVOList, StoringSerializedItem.class));
        storingLineItemRepository.deleteAllInBatch(BeanMapUtils.mapListTo(storingLineItemVOList, StoringLineItem.class));
        storingLineRepository.deleteAllInBatch(BeanMapUtils.mapListTo(storingLineVOList, StoringLine.class));
        serializedProductTranRepository.deleteAllInBatch(BeanMapUtils.mapListTo(serializedProductTranVOList, SerializedProductTran.class));
        orderSerializedItemRepository.deleteAllInBatch(BeanMapUtils.mapListTo(orderSerializedItemVOList, OrderSerializedItem.class));

        Map<Long, ReceiptSerializedItemVO> receiptSerializedItemVOMap = receiptSerializedItemVOList.stream().collect(Collectors.toMap(ReceiptSerializedItemVO::getSerializedProductId, Function.identity()));
        Map<Long, ReceiptSlipVO> receiptSlipVOMap = receiptSlipVOList.stream().collect(Collectors.toMap(ReceiptSlipVO::getReceiptSlipId, Function.identity()));
        List<SDM010901BO> importList = form.getImportList();
        Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = new HashMap<>();
        for (SDM010901BO importData : importList) {
            ReceiptSerializedItemVO receiptSerializedItemVO = receiptSerializedItemVOMap.get(importData.getSerializedProductId());
            ReceiptSlipVO receiptSlipVO = receiptSlipVOMap.get(receiptSerializedItemVO.getReceiptSlipId());
            if (ReceiptSlipStatus.ONTRANSIT.getCodeDbid().equals(receiptSlipVO.getReceiptSlipStatus())) {
                // S084ONTRANSITQTY - 1
                inventoryManager.generateStockStatusVOMapForSD(importData.getDealerCd()
                                                             , form.getPointId()
                                                             , importData.getProductId()
                                                             , BigDecimal.ONE.negate()
                                                             , SdStockStatus.ONTRANSIT_QTY.getCodeDbid()
                                                             , stockStatusVOChangeMap);
            } else {
                // S084ONHANDQTY - 1
                inventoryManager.generateStockStatusVOMapForSD(importData.getSiteId()
                                                             , form.getPointId()
                                                             , importData.getProductId()
                                                             , BigDecimal.ONE.negate()
                                                             , SdStockStatus.ONHAND_QTY.getCodeDbid()
                                                             , stockStatusVOChangeMap);
            }
        }

        inventoryManager.updateProductStockStatusByMapForSD(stockStatusVOChangeMap);
    }
}
