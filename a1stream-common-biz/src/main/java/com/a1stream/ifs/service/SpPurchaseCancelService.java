package com.a1stream.ifs.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.PurchaseOrderStatus;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.common.manager.MessageSendManager;
import com.a1stream.domain.entity.PoCancelHistory;
import com.a1stream.domain.entity.PurchaseOrder;
import com.a1stream.domain.entity.PurchaseOrderItem;
import com.a1stream.domain.repository.CmmMessageRemindRepository;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.PoCancelHistoryRepository;
import com.a1stream.domain.repository.PurchaseOrderItemRepository;
import com.a1stream.domain.repository.PurchaseOrderRepository;
import com.a1stream.domain.repository.SysRoleRepository;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.PoCancelHistoryVO;
import com.a1stream.domain.vo.PurchaseOrderItemVO;
import com.a1stream.domain.vo.PurchaseOrderVO;
import com.a1stream.ifs.bo.SpPurchaseCancelModelBO;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Service
public class SpPurchaseCancelService {

    @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepository;

    @Resource
    private PurchaseOrderRepository purchaseOrderRepository;

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Resource
    private InventoryManager inventoryManager;

    @Resource
    private SysRoleRepository sysRoleRepository;

    @Resource
    private CmmMessageRemindRepository cmmMessageRemindRepository;

    @Resource
    private MessageSendManager  messageSendManager;

    @Resource
    private PoCancelHistoryRepository  poCancelHistoryRepository;

    public List<CmmSiteMasterVO> getCmmSiteMasterVOList(Set<String> siteIdSet) {

        return BeanMapUtils.mapListTo(cmmSiteMasterRepository.findBySiteIdIn(siteIdSet), CmmSiteMasterVO.class);
    }

    public List<PurchaseOrderVO> getPurchaseOrderVOList(List<String> siteIdList, List<String> orderNoList) {

        return BeanMapUtils.mapListTo(purchaseOrderRepository.findBySiteIdInAndOrderNoIn(siteIdList, orderNoList), PurchaseOrderVO.class);
    }

    public List<MstProductVO> getMstProductVOList(List<String> productCdList, List<String> siteIdList) {

        return BeanMapUtils.mapListTo(mstProductRepository.findByProductCdInAndSiteIdIn(productCdList, siteIdList), MstProductVO.class);
    }

    public List<PurchaseOrderItemVO> getPurchaseOrderItemVOList(Set<Long> purchaseOrderIdSet, Set<Long> productIdSet) {

        return BeanMapUtils.mapListTo(purchaseOrderItemRepository.findByPurchaseOrderIdInAndProductIdIn(purchaseOrderIdSet, productIdSet), PurchaseOrderItemVO.class);
    }

    public List<PurchaseOrderItemVO> getPurchaseOrderItemVOList(List<Long> purchaseOrderItemIdList) {

        return BeanMapUtils.mapListTo(purchaseOrderItemRepository.findByPurchaseOrderItemIdIn(purchaseOrderItemIdList), PurchaseOrderItemVO.class);
    }

    public List<PurchaseOrderVO> getPurchaseOrderVOList(List<Long> purchaseOrderIdList) {

        return BeanMapUtils.mapListTo(purchaseOrderRepository.findByPurchaseOrderIdIn(purchaseOrderIdList), PurchaseOrderVO.class);
    }

    public List<PurchaseOrderItemVO> getPurchaseOrderItemVOListByPoId(List<Long> purchaseOrderIdList) {

        return BeanMapUtils.mapListTo(purchaseOrderItemRepository.findByPurchaseOrderIdIn(purchaseOrderIdList), PurchaseOrderItemVO.class);
    }

    public void doPurchaseCancel(List<SpPurchaseCancelModelBO> spPurchaseCancelModelBOList) {

        List<Long> purchaseOrderItemIdList = spPurchaseCancelModelBOList.stream().map(SpPurchaseCancelModelBO::getPoItemId).collect(Collectors.toList());
        List<PurchaseOrderItemVO> purchaseOrderItemList = this.getPurchaseOrderItemVOList(purchaseOrderItemIdList);
        Map<Long, PurchaseOrderItemVO> purchaseOrderItemMap = purchaseOrderItemList.stream().collect(Collectors.toMap(PurchaseOrderItemVO::getPurchaseOrderItemId, Function.identity()));

        for (SpPurchaseCancelModelBO item : spPurchaseCancelModelBOList) {
            PurchaseOrderItemVO purchaseOrderItemVO = purchaseOrderItemMap.get(item.getPoItemId());
            purchaseOrderItemVO.setOnPurchaseQty(purchaseOrderItemVO.getOnPurchaseQty().subtract(item.getMinusQuantity()));
            purchaseOrderItemVO.setActualQty(purchaseOrderItemVO.getActualQty().subtract(item.getMinusQuantity()));
            purchaseOrderItemVO.setCancelledQty(purchaseOrderItemVO.getCancelledQty().add(item.getMinusQuantity()));
        }

        purchaseOrderItemRepository.saveInBatch(BeanMapUtils.mapListTo(purchaseOrderItemList, PurchaseOrderItem.class));

        List<Long> purchaeOrderIdList = spPurchaseCancelModelBOList.stream().map(SpPurchaseCancelModelBO::getPoId).collect(Collectors.toList());
        List<PurchaseOrderVO> purchaseOrderVOList = this.getPurchaseOrderVOList(purchaeOrderIdList);
        purchaseOrderItemList = this.getPurchaseOrderItemVOListByPoId(purchaeOrderIdList);
        Map<Long, List<PurchaseOrderItemVO>> purchaseOrderItemListMap = purchaseOrderItemList.stream().collect(Collectors.groupingBy(PurchaseOrderItemVO::getPurchaseOrderId));

        for (PurchaseOrderVO purchaseOrderVO : purchaseOrderVOList) {
            List<PurchaseOrderItemVO> purchaseOrderItemVOList = purchaseOrderItemListMap.get(purchaseOrderVO.getPurchaseOrderId());

            BigDecimal sumOrderQty = purchaseOrderItemVOList.stream().map(item -> item.getOrderQty() != null ? item.getOrderQty() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal sumCancelledQty = purchaseOrderItemVOList.stream().map(item -> item.getCancelledQty() != null ? item.getCancelledQty() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (sumOrderQty.compareTo(sumCancelledQty) == 0) {
                purchaseOrderVO.setOrderStatus(PurchaseOrderStatus.SPCANCELLED);
            }
        }

        purchaseOrderRepository.saveInBatch(BeanMapUtils.mapListTo(purchaseOrderVOList, PurchaseOrder.class));

        inventoryManager.doPurchaseOrderCancellation(spPurchaseCancelModelBOList);

	    List<String> list = new ArrayList<>();
        list.add(PJConstants.RoleCode.SPAREPART);
        List<Long> roleIds = sysRoleRepository.getIdByRoleCdIn(list);

        List<PoCancelHistoryVO> poCancelHistoryVOS = new ArrayList<>();

        for (SpPurchaseCancelModelBO bo : spPurchaseCancelModelBOList) {

            String message = "Parts(" + bo.getPartNo()+") of Purchase Order(" + bo.getPoNo()+") been cancelled ("+StringUtils.toString(bo.getMinusQuantity()) + ") pcs on(" + LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD))+")";
            messageSendManager.notifyUserRoles(bo.getSiteId(),roleIds,null,PJConstants.ProductClsType.PART.getCodeDbid(),message,PJConstants.MessageCategoryType.REPORTREADY,CommonConstants.CHAR_IFS);
        
            PoCancelHistoryVO poCancelHistoryVO = new PoCancelHistoryVO();
            poCancelHistoryVO.setSiteId(bo.getSiteId());
            poCancelHistoryVO.setOrderNo(bo.getYourOrderNo());
            poCancelHistoryVO.setProductId(bo.getProductId());
            poCancelHistoryVO.setProductCd(bo.getPartsNo());
            poCancelHistoryVO.setCancelQty(new BigDecimal(bo.getCancelQty()));
            poCancelHistoryVO.setPurchaseOrderId(bo.getPoId());
            poCancelHistoryVO.setCancelDate(LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)));
            poCancelHistoryVO.setProductNm(bo.getPartsNm());
            poCancelHistoryVO.setProductClassification(PJConstants.ProductClsType.PART.getCodeDbid());
            poCancelHistoryVO.setFacilityId(bo.getPointId());
            poCancelHistoryVO.setFacilityCd(bo.getPointNo());
            poCancelHistoryVO.setFacilityNm(bo.getPointNm());
            poCancelHistoryVO.setSupplierId(bo.getSupplierId());
            poCancelHistoryVO.setSupplierCd(bo.getSupplierCd());
            poCancelHistoryVO.setSupplierNm(bo.getSupplierNm());

            poCancelHistoryVOS.add(poCancelHistoryVO);
        }

        poCancelHistoryRepository.saveInBatch(BeanMapUtils.mapListTo(poCancelHistoryVOS, PoCancelHistory.class));
    }

    public void setOrderStatus(PurchaseOrderVO purchaseOrderVO, List<PurchaseOrderItemVO> purchaseOrderItemVOList) {

        BigDecimal sumActualQty = purchaseOrderItemVOList.stream().map(item -> item.getActualQty() != null ? item.getActualQty() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumReceiveQty = purchaseOrderItemVOList.stream().map(item -> item.getReceiveQty() != null ? item.getReceiveQty() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumStoredQty = purchaseOrderItemVOList.stream().map(item -> item.getStoredQty() != null ? item.getStoredQty() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumCancelledQty = purchaseOrderItemVOList.stream().map(item -> item.getCancelledQty() != null ? item.getCancelledQty() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sumActualQty.compareTo(sumReceiveQty) == 0) {
            purchaseOrderVO.setOrderStatus(PurchaseOrderStatus.SPONRECEIVING);
        } else if (sumActualQty.compareTo(sumStoredQty) == 0) {
            purchaseOrderVO.setOrderStatus(PurchaseOrderStatus.SPREGISTERED);
        } else if (sumActualQty.compareTo(sumCancelledQty) == 0) {
            purchaseOrderVO.setOrderStatus(PurchaseOrderStatus.SPCANCELLED);
        } else {
            purchaseOrderVO.setOrderStatus(PurchaseOrderStatus.SPONPURCHASE);
        }
    }
}
