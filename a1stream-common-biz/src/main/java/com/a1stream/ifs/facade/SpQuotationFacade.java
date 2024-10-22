package com.a1stream.ifs.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.PurchaseOrderStatus;
import com.a1stream.common.constants.PJConstants.OrgRelationType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.PurchaseMethodType;
import com.a1stream.common.constants.PJConstants.PurchaseOrderPriorityType;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstOrganizationVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.OrganizationRelationVO;
import com.a1stream.domain.vo.PurchaseOrderItemVO;
import com.a1stream.domain.vo.PurchaseOrderVO;
import com.a1stream.ifs.bo.SpQuotationItemModelBO;
import com.a1stream.ifs.bo.SpQuotationModelBO;
import com.a1stream.ifs.service.SpQuotationService;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.CollectionUtils;

import jakarta.annotation.Resource;

@Component
public class SpQuotationFacade {

    @Resource
    private SpQuotationService spQuotationService;

        @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepository;

    public void doQuotation(List<SpQuotationModelBO> spQuotationModelBOList){
        
        //文件中的dealerCode若不存在于cmm_site_master表中，则剔除出去-> update by lijiajun
        Set<String> dealerCds = spQuotationModelBOList.stream().map(o -> o.getDealerCode()).collect(Collectors.toSet());
        List<CmmSiteMasterVO> cmmSiteMasters = BeanMapUtils.mapListTo(cmmSiteMasterRepository.findBySiteCdInAndActiveFlag(dealerCds,CommonConstants.CHAR_Y),CmmSiteMasterVO.class);
        Set<String> siteIds = cmmSiteMasters.stream().map(o -> o.getSiteId()).collect(Collectors.toSet());
        spQuotationModelBOList = spQuotationModelBOList.stream()
                                                       .filter(model -> siteIds.contains(model.getDealerCode()))
                                                       .collect(Collectors.toList());

        Map<String, List<SpQuotationModelBO>> sqcImportModelForGroupMap = new HashMap<>();
        for (SpQuotationModelBO item : spQuotationModelBOList) {
            String key = constructKey(item.getDealerCode(), item.getSalesOrderNo(), item.getWarehouseCode(), item.getConsignee());
            if (!sqcImportModelForGroupMap.containsKey(key)) {
                List<SpQuotationModelBO> sqcImportModelForGroupList = new ArrayList<>();
                sqcImportModelForGroupList.add(item);
                sqcImportModelForGroupMap.put(key, sqcImportModelForGroupList);
            } else {
                sqcImportModelForGroupMap.get(key).add(item);
            }
        }

        Set<String> dealerCodeSet = spQuotationModelBOList.stream().map(SpQuotationModelBO::getDealerCode).collect(Collectors.toSet());
        Set<String> consigneeSet = spQuotationModelBOList.stream().map(SpQuotationModelBO::getConsignee).collect(Collectors.toSet());
        Set<String> orderNoSet = spQuotationModelBOList.stream().map(SpQuotationModelBO::getSalesOrderNo).collect(Collectors.toSet());
        Set<String> warehouseCodeSet = spQuotationModelBOList.stream().map(SpQuotationModelBO::getWarehouseCode).collect(Collectors.toSet());

        Set<String> filterSiteIdSet = this.getSiteIdForSqc(dealerCodeSet);
        Set<String> filterConsigneeSet = this.getConsigneeForSqc(filterSiteIdSet, consigneeSet);
        Set<String> filterOrderNoSet = this.getOrderNoForSqc(filterSiteIdSet, orderNoSet, warehouseCodeSet);

        List<List<SpQuotationModelBO>> spQuotationModelForgroupList = new ArrayList<>();
        List<String> partsNoList = new ArrayList<>();
        for (List<SpQuotationModelBO> sqcImportModelForGroupList : sqcImportModelForGroupMap.values()) {

            if (!filterSiteIdSet.contains(sqcImportModelForGroupList.get(0).getDealerCode())
             || !filterConsigneeSet.contains(sqcImportModelForGroupList.get(0).getConsignee())
             || filterOrderNoSet.contains(sqcImportModelForGroupList.get(0).getSalesOrderNo())) {
                continue;
            }

            for (SpQuotationModelBO spQuotationModelBO : sqcImportModelForGroupList) {
                for (SpQuotationItemModelBO spQuotationItemModelBO : spQuotationModelBO.getOrderItemList()) {
                    partsNoList.add(spQuotationItemModelBO.getPartsNo());
                }
            }

            spQuotationModelForgroupList.add(sqcImportModelForGroupList);
        }
        List<String> cmmsiteIds = new ArrayList<>();
        cmmsiteIds.add(CommonConstants.CHAR_DEFAULT_SITE_ID);
        Map<String, MstProductVO> productMap = this.getProductMap(cmmsiteIds, partsNoList);
        List<List<SpQuotationModelBO>> filterSpQuotationModelForgroupList = new ArrayList<>();
        filterSpQuotationModelForgroupList.addAll(spQuotationModelForgroupList);

        for (List<SpQuotationModelBO> modelList : spQuotationModelForgroupList) {
            for (SpQuotationModelBO spQuotationModelBO : modelList) {
                for (SpQuotationItemModelBO spQuotationItemModelBO : spQuotationModelBO.getOrderItemList()) {
                    if (!productMap.containsKey(spQuotationItemModelBO.getPartsNo() + CommonConstants.CHAR_DEFAULT_SITE_ID)) {
                        filterSpQuotationModelForgroupList.remove(modelList);
                    }
                }

                if (CollectionUtils.isEmpty(filterSpQuotationModelForgroupList)) {
                    return;
                }
            }
        }

        List<MstOrganizationVO> supplierList = spQuotationService.getSupplierList(filterSiteIdSet);
        Map<String, MstOrganizationVO> supplierMap = supplierList.stream().collect(Collectors.toMap(MstOrganizationVO::getSiteId, Function.identity()));
        List<MstFacilityVO> facilityList = spQuotationService.getFacilityVOList(filterSiteIdSet, filterConsigneeSet);
        Map<String, MstFacilityVO> facilityMap = facilityList.stream().collect(Collectors.toMap(item -> item.getSiteId() + item.getFacilityCd(), Function.identity()));

        List<PurchaseOrderVO> purchaseOrderVOList = new ArrayList<>();
        List<PurchaseOrderItemVO> purchaseOrderItemVOList = new ArrayList<>();
        for (List<SpQuotationModelBO> sqcImportModelForGroupList : filterSpQuotationModelForgroupList) {
            SpQuotationModelBO firstItem = sqcImportModelForGroupList.get(0);
            for (String siteId : filterSiteIdSet) {
                String purchaseOrderTypeId = this.getPurchaseOrderPriorityTypeId(firstItem.getOrderType());

                MstOrganizationVO supplierInfo = supplierMap.get(siteId);
                MstFacilityVO facilityInfo = facilityMap.get(siteId + firstItem.getConsignee());

                PurchaseOrderVO purchaseOrderVO = PurchaseOrderVO.create();
                purchaseOrderVO.setSiteId(siteId);
                purchaseOrderVO.setOrderNo(spQuotationService.purchaseOrderNo(siteId, facilityInfo.getFacilityId()));
                purchaseOrderVO.setProductClassification(ProductClsType.PART.getCodeDbid());
                purchaseOrderVO.setFacilityId(facilityInfo.getFacilityId());
                purchaseOrderVO.setFacilityCd(facilityInfo.getFacilityCd());
                purchaseOrderVO.setFacilityNm(facilityInfo.getFacilityNm());
                purchaseOrderVO.setSupplierId(supplierInfo.getOrganizationId());
                purchaseOrderVO.setSupplierCd(supplierInfo.getOrganizationCd());
                purchaseOrderVO.setSupplierNm(supplierInfo.getOrganizationNm());
                purchaseOrderVO.setOrderDate(firstItem.getCreatedDate());
                purchaseOrderVO.setOrderStatus(PurchaseOrderStatus.SPWAITINGISSUE);
                purchaseOrderVO.setOrderPriorityType(purchaseOrderTypeId);
                purchaseOrderVO.setOrderMethodType(PurchaseMethodType.POSUPPLIER.getCodeDbid());

                Integer seqNo = CommonConstants.INTEGER_ZERO;
                BigDecimal totalQty = BigDecimal.ZERO;
                BigDecimal totalAmount = BigDecimal.ZERO;

                for (SpQuotationModelBO spQuotationModel : sqcImportModelForGroupList) {
                    for (SpQuotationItemModelBO item : spQuotationModel.getOrderItemList()) {
                        seqNo += 1;
                        MstProductVO productInfo = productMap.get(item.getPartsNo() + CommonConstants.CHAR_DEFAULT_SITE_ID);
                        PurchaseOrderItemVO purchaseOrderItemVO = new PurchaseOrderItemVO();
                        purchaseOrderItemVO.setSiteId(siteId);
                        purchaseOrderItemVO.setPurchaseOrderId(purchaseOrderVO.getPurchaseOrderId());
                        purchaseOrderItemVO.setProductClassification(ProductClsType.PART.getCodeDbid());
                        purchaseOrderItemVO.setProductId(productInfo.getProductId());
                        purchaseOrderItemVO.setProductCd(productInfo.getProductCd());
                        purchaseOrderItemVO.setProductNm(productInfo.getSalesDescription());
                        purchaseOrderItemVO.setOrderQty(NumberUtil.toBigDecimal(item.getOrderQty()));
                        purchaseOrderItemVO.setPurchasePrice(NumberUtil.toBigDecimal(item.getPrice()));
                        purchaseOrderItemVO.setSeqNo(seqNo);

                        totalQty = totalQty.add(NumberUtil.toBigDecimal(item.getOrderQty()));
                        totalAmount = totalAmount.add(NumberUtil.toBigDecimal(item.getOrderQty()).multiply(NumberUtil.toBigDecimal(item.getPrice())));

                        purchaseOrderItemVOList.add(purchaseOrderItemVO);
                    }
                }

                purchaseOrderVO.setTotalQty(totalQty);
                purchaseOrderVO.setTotalAmount(totalAmount);
                purchaseOrderVO.setTotalActualQty(totalQty);
                purchaseOrderVO.setTotalActualAmt(totalAmount);
                purchaseOrderVOList.add(purchaseOrderVO);
            }
        }

        spQuotationService.doQuotation(purchaseOrderVOList, purchaseOrderItemVOList);
    }

    public String constructKey(String ...value) {

        StringBuilder res = new StringBuilder();
        for (String item : value) {
            res.append(item != null ? item : "").append("_");
        }
        return res.toString();
    }

    public Set<String> getSiteIdForSqc(Set<String> dealerCodeSet) {
        
        List<OrganizationRelationVO> organizationRelationVOList = spQuotationService.getOrganizationRelationVOList(dealerCodeSet, OrgRelationType.COMPANY.getCodeDbid());
        return organizationRelationVOList.stream().map(OrganizationRelationVO::getSiteId).collect(Collectors.toSet());
    }

    public Set<String> getConsigneeForSqc(Set<String> siteIdSet, Set<String> consigneeSet) {

        List<MstFacilityVO> mstFacilityVOList = spQuotationService.getMstFacilityVOList(siteIdSet, consigneeSet, CommonConstants.CHAR_Y, CommonConstants.CHAR_Y);
        return mstFacilityVOList.stream().map(MstFacilityVO::getFacilityCd).collect(Collectors.toSet());
    }

    public Set<String> getOrderNoForSqc(Set<String> siteIdSet, Set<String> orderNoSet, Set<String> warehouseCodeSet) {

        List<PurchaseOrderVO> purchaseOrderVOList = spQuotationService.getPurchaseOrderVOList(siteIdSet, orderNoSet, warehouseCodeSet);
        return purchaseOrderVOList.stream().map(PurchaseOrderVO::getOrderNo).collect(Collectors.toSet());
    }

    public Map<String, MstProductVO> getProductMap(List<String> siteIdList, List<String> productCdList) {

        List<MstProductVO> mstProductVOList = spQuotationService.getMstProductVOList(siteIdList, productCdList);
        return mstProductVOList.stream().collect(Collectors.toMap(item -> item.getProductCd() + item.getSiteId(), Function.identity()));
    }

    public String getPurchaseOrderPriorityTypeId(String orderType) {
        if (CommonConstants.CHAR_E.equals(orderType)) {
            return PurchaseOrderPriorityType.POEO.getCodeDbid();
        } else if (CommonConstants.CHAR_H.equals(orderType)) {
            return PurchaseOrderPriorityType.POHO.getCodeDbid();
        } else if (CommonConstants.CHAR_R.equals(orderType)) {
            return PurchaseOrderPriorityType.PORO.getCodeDbid();
        } else if (CommonConstants.CHAR_W.equals(orderType)) {
            return PurchaseOrderPriorityType.POWO.getCodeDbid();
        } else {
            return PurchaseOrderPriorityType.PORO.getCodeDbid();
        }
    }
}