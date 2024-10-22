package com.a1stream.ifs.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.PurchaseOrderItemVO;
import com.a1stream.domain.vo.PurchaseOrderVO;
import com.a1stream.ifs.bo.SpPurchaseCancelModelBO;
import com.a1stream.ifs.service.SpPurchaseCancelService;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Component
public class SpPurchaseCancelFacade {

    @Resource
    private SpPurchaseCancelService spPurchaseCancelService;
    
    @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepository;

    public void doPurchaseCancel(List<SpPurchaseCancelModelBO> spPurchaseCancelModelBOList){

        //文件中的dealerCode若不存在于cmm_site_master表中，则剔除出去-> update by lijiajun
        Set<String> dealerCds = spPurchaseCancelModelBOList.stream().map(o -> o.getDealerCode()).collect(Collectors.toSet());
        List<CmmSiteMasterVO> cmmSiteMasters = BeanMapUtils.mapListTo(cmmSiteMasterRepository.findBySiteCdInAndActiveFlag(dealerCds,CommonConstants.CHAR_Y),CmmSiteMasterVO.class);
        Set<String> siteIds = cmmSiteMasters.stream().map(o -> o.getSiteId()).collect(Collectors.toSet());
        spPurchaseCancelModelBOList = spPurchaseCancelModelBOList.stream()
                                                                   .filter(model -> siteIds.contains(model.getDealerCode()))
                                                                   .collect(Collectors.toList());

        Set<String> dealerCdSet = spPurchaseCancelModelBOList.stream().map(SpPurchaseCancelModelBO::getDealerCode).collect(Collectors.toSet());
        List<CmmSiteMasterVO> cmmSiteMasterVOList = spPurchaseCancelService.getCmmSiteMasterVOList(dealerCdSet);

        List<String> siteIdList = cmmSiteMasterVOList.stream().map(CmmSiteMasterVO::getSiteId).collect(Collectors.toList());
        List<String> orderNoList = spPurchaseCancelModelBOList.stream().map(SpPurchaseCancelModelBO::getYourOrderNo).collect(Collectors.toList());
        List<PurchaseOrderVO> purchaseOrderVOList = spPurchaseCancelService.getPurchaseOrderVOList(siteIdList, orderNoList);
        Map<String, PurchaseOrderVO> purchaseOrderIdMap = purchaseOrderVOList.stream().collect(Collectors.toMap(PurchaseOrderVO::getOrderNo, Function.identity()));

        List<String> cmmSiteIdList = new ArrayList<>();
        cmmSiteIdList.add(CommonConstants.CHAR_DEFAULT_SITE_ID);
        List<String> partsNoList = spPurchaseCancelModelBOList.stream().map(SpPurchaseCancelModelBO::getPartNo).collect(Collectors.toList());
        List<MstProductVO> mstProductVOList = spPurchaseCancelService.getMstProductVOList(partsNoList, cmmSiteIdList);

        Set<Long> purcahseOrderIdSet = purchaseOrderVOList.stream().map(PurchaseOrderVO::getPurchaseOrderId).collect(Collectors.toSet());
        Set<Long> productIdSet = mstProductVOList.stream().map(MstProductVO::getProductId).collect(Collectors.toSet());
        List<PurchaseOrderItemVO> purchaseOrderItemVOList = spPurchaseCancelService.getPurchaseOrderItemVOList(purcahseOrderIdSet, productIdSet);
        Map<String, PurchaseOrderItemVO> purchaseOrderItemVOMap = purchaseOrderItemVOList.stream().collect(Collectors.toMap(item -> item.getPurchaseOrderId().toString() + item.getProductCd(), Function.identity()));

        for (SpPurchaseCancelModelBO item : spPurchaseCancelModelBOList) {
            PurchaseOrderVO purchaseOrderVO = purchaseOrderIdMap.get(item.getYourOrderNo());
            PurchaseOrderItemVO purchaseOrderItemVO = purchaseOrderItemVOMap.get(purchaseOrderVO.getPurchaseOrderId().toString() + item.getPartNo());

            if (null != purchaseOrderVO && null != purchaseOrderItemVO) {
                item.setSiteId(purchaseOrderItemVO.getSiteId());
                item.setConsigneeId(purchaseOrderVO.getFacilityId());
                item.setEoRoType(purchaseOrderVO.getOrderPriorityType());
                item.setPoId(purchaseOrderVO.getPurchaseOrderId());
                item.setPoItemId(purchaseOrderItemVO.getPurchaseOrderItemId());
                item.setProductId(purchaseOrderItemVO.getProductId());
                item.setMinusQuantity(purchaseOrderItemVO.getOnPurchaseQty().compareTo(NumberUtil.toBigDecimal(item.getCancelQty())) >= 0 ? NumberUtil.toBigDecimal(item.getCancelQty()) : purchaseOrderItemVO.getOnPurchaseQty());
                item.setPoNo(purchaseOrderVO.getOrderNo());
                item.setPartsNo(purchaseOrderItemVO.getProductCd());
                item.setPartsNm(purchaseOrderItemVO.getProductNm());
                item.setPointId(purchaseOrderVO.getFacilityId());
                item.setPointNm(purchaseOrderVO.getFacilityNm());
                item.setPointNo(purchaseOrderVO.getFacilityCd());
                item.setSupplierId(purchaseOrderVO.getSupplierId());
                item.setSupplierCd(purchaseOrderVO.getSupplierCd());
                item.setSupplierNm(purchaseOrderVO.getSupplierNm());
            }
        }

        spPurchaseCancelService.doPurchaseCancel(spPurchaseCancelModelBOList);
    }
}