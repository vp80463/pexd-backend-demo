package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.PurchaseOrderStatus;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.ProductStockStatusType;
import com.a1stream.common.constants.PJConstants.PurchaseMethodType;
import com.a1stream.common.constants.PJConstants.PurchaseOrderPriorityType;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.parts.SPM040401BO;
import com.a1stream.domain.bo.parts.SPM040402BO;
import com.a1stream.domain.form.parts.SPM040401Form;
import com.a1stream.domain.form.parts.SPM040402Form;
import com.a1stream.domain.vo.PoCancelHistoryVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.PurchaseOrderItemVO;
import com.a1stream.domain.vo.PurchaseOrderVO;
import com.a1stream.parts.service.SPM0404Service;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;

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
@Component
public class SPM0404Facade {

    @Resource
    private SPM0404Service spm0404Service;

    @Resource
    private HelperFacade helperFacade;

    public List<SPM040401BO> getPurchaseOrderList(SPM040401Form form, PJUserDetails uc) {

        List<SPM040401BO> resultList = spm0404Service.getPurchaseOrderList(form, uc);
        // 数据库为codeDbid 前台显示为codeData1
        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(PurchaseOrderStatus.CODE_ID, PurchaseOrderPriorityType.CODE_ID, PurchaseMethodType.CODE_ID);
        // 设置purchaseOrder是否可以delete
        for (SPM040401BO bo : resultList) {
            if (PurchaseOrderStatus.SPWAITINGISSUE.equals(bo.getOrderStatus()) && PurchaseOrderPriorityType.POWO.getCodeDbid().equals(bo.getOrderType())) {
                bo.setDeleteFlag(CommonConstants.TRUE_CODE);
            } else {
                bo.setDeleteFlag(CommonConstants.FALSE_CODE);
            }
            bo.setOrderStatus(codeMap.get(bo.getOrderStatus()));
            bo.setOrderType(codeMap.get(bo.getOrderType()));
            bo.setMethod(codeMap.get(bo.getMethod()));
        }
        return resultList;
    }

    public void cancelPurchaseOrder(SPM040401Form form) {

        PurchaseOrderVO purchaseOrderVO = spm0404Service.findPurchaseOrderVO(form.getPurchaseOrderId());

        if (null == purchaseOrderVO || !form.getUpdateCount().equals(purchaseOrderVO.getUpdateCount())) {
            throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[] {ComUtil.t("label.purchaseOrderNumber"), form.getOrderNo(), ComUtil.t("title.partsPurchaseOrderMaintenance_01")}));
        }

        purchaseOrderVO.setOrderStatus(PurchaseOrderStatus.SPCANCELLED);
        purchaseOrderVO.setTotalActualQty(BigDecimal.ZERO);
        purchaseOrderVO.setTotalActualAmt(BigDecimal.ZERO);

        List<PurchaseOrderItemVO> purchaseOrderItemVOList = spm0404Service.findPurchaseOrderItemVOList(form.getPurchaseOrderId());
        List<PoCancelHistoryVO> poCancelHistoryVOList = this.buildCancelPurchaseOrder(purchaseOrderVO, purchaseOrderItemVOList);

        spm0404Service.cancelPurchaseOrder(purchaseOrderVO, purchaseOrderItemVOList, poCancelHistoryVOList);
    }

    private List<PoCancelHistoryVO> buildCancelPurchaseOrder(PurchaseOrderVO purchaseOrderVO, List<PurchaseOrderItemVO> purchaseOrderItemVOList) {

        List<PoCancelHistoryVO> poCancelHistoryVOList = new ArrayList<>();
        for (PurchaseOrderItemVO item : purchaseOrderItemVOList) {
            item.setActualQty(BigDecimal.ZERO);
            item.setCancelledQty(item.getOrderQty());
            item.setActualAmt(BigDecimal.ZERO);

            PoCancelHistoryVO poCancelHistory = PoCancelHistoryVO.create();
            poCancelHistory.setSiteId(purchaseOrderVO.getSiteId());
            poCancelHistory.setFacilityId(purchaseOrderVO.getFacilityId());
            poCancelHistory.setFacilityCd(purchaseOrderVO.getFacilityCd());
            poCancelHistory.setFacilityNm(purchaseOrderVO.getFacilityNm());
            poCancelHistory.setSupplierId(purchaseOrderVO.getSupplierId());
            poCancelHistory.setSupplierCd(purchaseOrderVO.getSupplierCd());
            poCancelHistory.setSupplierNm(purchaseOrderVO.getSupplierNm());
            poCancelHistory.setOrderNo(purchaseOrderVO.getOrderNo());
            poCancelHistory.setPurchaseOrderId(purchaseOrderVO.getPurchaseOrderId());
            poCancelHistory.setProductId(item.getProductId());
            poCancelHistory.setProductCd(item.getProductCd());
            poCancelHistory.setProductNm(item.getProductNm());
            poCancelHistory.setCancelQty(item.getCancelledQty());
            poCancelHistory.setCancelDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
            poCancelHistory.setCancelComment(null);
            poCancelHistory.setProductClassification(ProductClsType.PART.getCodeDbid());
            poCancelHistory.setUpdateCount(0);

            poCancelHistoryVOList.add(poCancelHistory);
        }
        return poCancelHistoryVOList;
    }

    public SPM040401BO getPurchaseOrderItemList(SPM040402Form form, PJUserDetails uc) {

        List<SPM040402BO> tableDataList = spm0404Service.getPurchaseOrderItemList(form, uc);
        SPM040401BO purchaseOrder = spm0404Service.getPurchaseOrder(form, uc);
        // 设置purchaseOrder是否可以issue、confirm、delete purchaseOrderItem是否可以delete
        if (PurchaseOrderStatus.SPWAITINGISSUE.equals(purchaseOrder.getOrderStatus())) {
            purchaseOrder.setDeleteFlag(CommonConstants.TRUE_CODE);
            purchaseOrder.setOtherButtonDisabled(CommonConstants.FALSE_CODE);
            if (PurchaseMethodType.POFOQ.getCodeDbid().equals(purchaseOrder.getMethod()) || PurchaseMethodType.POINDIVIDUAL.getCodeDbid().equals(purchaseOrder.getMethod())) {
                purchaseOrder.setDeleteDisabled(CommonConstants.FALSE_CODE);
            } else {
                purchaseOrder.setDeleteDisabled(CommonConstants.TRUE_CODE);
            }
        } else {
            purchaseOrder.setDeleteFlag(CommonConstants.FALSE_CODE);
            purchaseOrder.setDeleteDisabled(CommonConstants.TRUE_CODE);
            purchaseOrder.setOtherButtonDisabled(CommonConstants.TRUE_CODE);
        }
        // 数据库为codeDbid 前台显示为codeData1
        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(PurchaseOrderStatus.CODE_ID, PurchaseOrderPriorityType.CODE_ID, PurchaseMethodType.CODE_ID);
        purchaseOrder.setOrderStatus(codeMap.get(purchaseOrder.getOrderStatus()));
        purchaseOrder.setOrderType(codeMap.get(purchaseOrder.getOrderType()));
        purchaseOrder.setMethod(codeMap.get(purchaseOrder.getMethod()));
        purchaseOrder.setTableDataList(tableDataList);

        return purchaseOrder;
    }

    public void confirmPurchaseOrderItemList(SPM040402Form form, PJUserDetails uc) {

        // 判断deliveryPlanData是否小于当前年月
        if (form.getDeliveryPlanDate().compareTo(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD)) < 0) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("error.dateEqAfter", new String[]{ CodedMessageUtils.getMessage("label.deliveryPlanDate")
                                                                                                           , DateUtils.getCurrentDate().toString() }));
        }

        // 提交的数据
        List<SPM040402BO> gridDataList = form.getGridDataList();

        Long purchaseOrderId = form.getPurchaseOrderId();

        // 数据库数据
        PurchaseOrderVO purchaseOrderVO = spm0404Service.findPurchaseOrderVO(purchaseOrderId);

        if (null == purchaseOrderVO || !form.getUpdateCount().equals(purchaseOrderVO.getUpdateCount())) {
            throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[] {ComUtil.t("label.purchaseOrderNumber"), form.getOrderNo(), ComUtil.t("title.partsPurchaseOrderDetailModify_02")}));
        }

        purchaseOrderVO.setDeliverPlanDate(form.getDeliveryPlanDate());

        List<PurchaseOrderItemVO> purchaseOrderItemVOList = spm0404Service.findPurchaseOrderItemVOList(purchaseOrderId);
        if (CollectionUtils.isEmpty(purchaseOrderItemVOList)) {
            purchaseOrderItemVOList = new ArrayList<>();
        }
        List<PurchaseOrderItemVO> deletePurchaseOrderItemVOList = new ArrayList<>(purchaseOrderItemVOList);

        this.buildConfirmPurchaseOrderItem(uc, gridDataList, purchaseOrderId, purchaseOrderItemVOList, deletePurchaseOrderItemVOList, form.getOrderNo());

        purchaseOrderItemVOList.removeAll(deletePurchaseOrderItemVOList);

        this.getPurchaseOrderTotal(purchaseOrderVO, purchaseOrderItemVOList);

        spm0404Service.confirmPurchaseOrderItemList(purchaseOrderVO, purchaseOrderItemVOList, deletePurchaseOrderItemVOList);
    }

    private void buildConfirmPurchaseOrderItem(PJUserDetails uc
                                             , List<SPM040402BO> gridDataList
                                             , Long purchaseOrderId
                                             , List<PurchaseOrderItemVO> purchaseOrderItemVOList
                                             , List<PurchaseOrderItemVO> deletePurchaseOrderItemVOList
                                             , String orderNo) {

        Map<Long, PurchaseOrderItemVO> purchaseOrderItemVOMap = purchaseOrderItemVOList.stream().collect(Collectors.toMap(PurchaseOrderItemVO::getPurchaseOrderItemId, Function.identity()));

        Integer maxSeqNo = gridDataList.stream().map(SPM040402BO::getSeqNo).filter(seqNo -> seqNo != null).mapToInt(Integer::intValue).max().orElse(0);
        for (SPM040402BO bo : gridDataList) {
            // 更新purchaseOrderItem
            if (purchaseOrderItemVOMap.containsKey(bo.getPurchaseOrderItemId())) {
                PurchaseOrderItemVO purchaseOrderItemVO = purchaseOrderItemVOMap.get(bo.getPurchaseOrderItemId());
                if (!bo.getUpdateCount().equals(purchaseOrderItemVO.getUpdateCount())) {
                    throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[] {ComUtil.t("label.purchaseOrderNumber"), orderNo, ComUtil.t("title.partsPurchaseOrderDetailModify_02")}));
                }
                purchaseOrderItemVO.setOrderQty(bo.getOrderQty());
                purchaseOrderItemVO.setActualQty(bo.getOrderQty());
                purchaseOrderItemVO.setAmount(bo.getOrderQty().multiply(bo.getPrice()));
                purchaseOrderItemVO.setActualAmt(bo.getOrderQty().multiply(bo.getPrice()));
                purchaseOrderItemVO.setBoCancelFlag(bo.getCancelFlag());

                deletePurchaseOrderItemVOList.remove(purchaseOrderItemVO);
            } else {
                // 新增purchaseOrderItem
                PurchaseOrderItemVO purchaseOrderItemVO = PurchaseOrderItemVO.create();
                purchaseOrderItemVO.setSiteId(uc.getDealerCode());
                purchaseOrderItemVO.setPurchaseOrderId(purchaseOrderId);
                purchaseOrderItemVO.setProductId(bo.getPartsId());
                purchaseOrderItemVO.setProductCd(bo.getPartsNo());
                purchaseOrderItemVO.setProductNm(bo.getPartsNm());
                purchaseOrderItemVO.setOrderQty(bo.getOrderQty());
                purchaseOrderItemVO.setOnPurchaseQty(BigDecimal.ZERO);
                purchaseOrderItemVO.setActualQty(bo.getOrderQty());
                purchaseOrderItemVO.setTransQty(BigDecimal.ZERO);
                purchaseOrderItemVO.setReceiveQty(BigDecimal.ZERO);
                purchaseOrderItemVO.setStoredQty(BigDecimal.ZERO);
                purchaseOrderItemVO.setCancelledQty(BigDecimal.ZERO);
                purchaseOrderItemVO.setStandardPrice(bo.getStdRetailPrice());
                purchaseOrderItemVO.setPurchasePrice(bo.getPrice());
                purchaseOrderItemVO.setAmount(bo.getOrderQty().multiply(bo.getPrice()));
                purchaseOrderItemVO.setActualAmt(bo.getOrderQty().multiply(bo.getPrice()));
                purchaseOrderItemVO.setBoCancelFlag(bo.getCancelFlag());
                purchaseOrderItemVO.setProductClassification(ProductClsType.PART.getCodeDbid());
                purchaseOrderItemVO.setSeqNo(++maxSeqNo);

                purchaseOrderItemVOList.add(purchaseOrderItemVO);
            }
        }
    }

    public void issuePurchaseOrderItemList(SPM040402Form form, PJUserDetails uc) {

        // 判断deliveryPlanData是否小于当前年月
        if (form.getDeliveryPlanDate().compareTo(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD)) < 0) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("error.dateEqAfter", new String[]{ CodedMessageUtils.getMessage("label.deliveryPlanDate")
                                                                                                           , DateUtils.getCurrentDate().toString() }));
        }

        // 提交的数据
        List<SPM040402BO> gridDataList = form.getGridDataList();

        Long purchaseOrderId = form.getPurchaseOrderId();

        // 数据库数据
        PurchaseOrderVO purchaseOrderVO = spm0404Service.findPurchaseOrderVO(purchaseOrderId);

        if (null == purchaseOrderVO || !form.getUpdateCount().equals(purchaseOrderVO.getUpdateCount())) {
            throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[] {ComUtil.t("label.purchaseOrderNumber"), form.getOrderNo(), ComUtil.t("title.partsPurchaseOrderDetailModify_02")}));
        }

        List<PurchaseOrderItemVO> purchaseOrderItemVOList = spm0404Service.findPurchaseOrderItemVOList(purchaseOrderId);
        if (CollectionUtils.isEmpty(purchaseOrderItemVOList)) {
            purchaseOrderItemVOList = new ArrayList<>();
        }
        List<PurchaseOrderItemVO> deletePurchaseOrderItemVOList = new ArrayList<>(purchaseOrderItemVOList);
        Map<Long, PurchaseOrderItemVO> purchaseOrderItemVOMap = purchaseOrderItemVOList.stream().collect(Collectors.toMap(PurchaseOrderItemVO::getPurchaseOrderItemId, Function.identity()));

        Map<String, String> productStockStatusTypeMap = ProductStockStatusType.map;
        Integer maxSeqNo = gridDataList.stream().map(SPM040402BO::getSeqNo).filter(seqNo -> seqNo != null).mapToInt(Integer::intValue).max().orElse(0);
        Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = new HashMap<>();
        for (SPM040402BO bo : gridDataList) {
            this.buildIssuePurchaseOrderItem(uc, purchaseOrderId, purchaseOrderItemVOList, deletePurchaseOrderItemVOList, purchaseOrderItemVOMap, bo, maxSeqNo, form.getOrderNo());
            // 更新productStockStatus
            String productStockStatusType = productStockStatusTypeMap.get(purchaseOrderVO.getOrderPriorityType());
            spm0404Service.generateStockStatusVOMap(uc.getDealerCode()
                                                  , purchaseOrderVO.getFacilityId()
                                                  , bo.getPartsId()
                                                  , productStockStatusType
                                                  , bo.getOrderQty()
                                                  , stockStatusVOChangeMap);
        }

        purchaseOrderVO.setOrderStatus(PurchaseOrderStatus.SPONPURCHASE);
        purchaseOrderVO.setDeliverPlanDate(form.getDeliveryPlanDate());
        this.getPurchaseOrderTotal(purchaseOrderVO, purchaseOrderItemVOList);

        spm0404Service.issuePurchaseOrderItemList(purchaseOrderVO, purchaseOrderItemVOList, deletePurchaseOrderItemVOList, stockStatusVOChangeMap);
    }

    private void buildIssuePurchaseOrderItem(PJUserDetails uc
                                           , Long purchaseOrderId
                                           , List<PurchaseOrderItemVO> purchaseOrderItemVOList
                                           , List<PurchaseOrderItemVO> deletePurchaseOrderItemVOList
                                           , Map<Long, PurchaseOrderItemVO> purchaseOrderItemVOMap
                                           , SPM040402BO bo
                                           , Integer maxSeqNo
                                           , String orderNo) {

        // 更新purchaseOrderItem
        if (purchaseOrderItemVOMap.containsKey(bo.getPurchaseOrderItemId())) {
            PurchaseOrderItemVO purchaseOrderItemVO = purchaseOrderItemVOMap.get(bo.getPurchaseOrderItemId());
            if (!bo.getUpdateCount().equals(purchaseOrderItemVO.getUpdateCount())) {
                throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[] {ComUtil.t("label.purchaseOrderNumber"), orderNo, ComUtil.t("title.partsPurchaseOrderDetailModify_02")}));
            }
            purchaseOrderItemVO.setOrderQty(bo.getOrderQty());
            purchaseOrderItemVO.setActualQty(bo.getOrderQty());
            purchaseOrderItemVO.setAmount(bo.getOrderQty().multiply(bo.getPrice()));
            purchaseOrderItemVO.setActualAmt(bo.getOrderQty().multiply(bo.getPrice()));
            purchaseOrderItemVO.setBoCancelFlag(bo.getCancelFlag());
            purchaseOrderItemVO.setOnPurchaseQty(bo.getOrderQty());
            deletePurchaseOrderItemVOList.remove(purchaseOrderItemVO);
        } else {
            // 新增purchaseOrderItem
            PurchaseOrderItemVO purchaseOrderItemVO = PurchaseOrderItemVO.create();
            purchaseOrderItemVO.setSiteId(uc.getDealerCode());
            purchaseOrderItemVO.setPurchaseOrderId(purchaseOrderId);
            purchaseOrderItemVO.setProductId(bo.getPartsId());
            purchaseOrderItemVO.setProductCd(bo.getPartsNo());
            purchaseOrderItemVO.setProductNm(bo.getPartsNm());
            purchaseOrderItemVO.setOrderQty(bo.getOrderQty());
            purchaseOrderItemVO.setOnPurchaseQty(bo.getOrderQty());
            purchaseOrderItemVO.setActualQty(bo.getOrderQty());
            purchaseOrderItemVO.setTransQty(BigDecimal.ZERO);
            purchaseOrderItemVO.setReceiveQty(BigDecimal.ZERO);
            purchaseOrderItemVO.setStoredQty(BigDecimal.ZERO);
            purchaseOrderItemVO.setCancelledQty(BigDecimal.ZERO);
            purchaseOrderItemVO.setStandardPrice(bo.getStdRetailPrice());
            purchaseOrderItemVO.setPurchasePrice(bo.getPrice());
            purchaseOrderItemVO.setAmount(bo.getOrderQty().multiply(bo.getPrice()));
            purchaseOrderItemVO.setActualAmt(bo.getOrderQty().multiply(bo.getPrice()));
            purchaseOrderItemVO.setBoCancelFlag(bo.getCancelFlag());
            purchaseOrderItemVO.setProductClassification(ProductClsType.PART.getCodeDbid());
            purchaseOrderItemVO.setSeqNo(++maxSeqNo);

            purchaseOrderItemVOList.add(purchaseOrderItemVO);
        }
    }

    public void getPurchaseOrderTotal(PurchaseOrderVO purchaseOrderVO, List<PurchaseOrderItemVO> purchaseOrderItemVOList) {

        BigDecimal sumOrderQty = purchaseOrderItemVOList.stream().map(item -> item.getOrderQty() != null ? item.getOrderQty() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumAmount = purchaseOrderItemVOList.stream().map(item -> item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumActualQty = purchaseOrderItemVOList.stream().map(item -> item.getActualQty() != null ? item.getActualQty() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumActualAmt = purchaseOrderItemVOList.stream().map(item -> item.getActualAmt() != null ? item.getActualAmt() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);

        purchaseOrderVO.setTotalQty(sumOrderQty);
        purchaseOrderVO.setTotalAmount(sumAmount);
        purchaseOrderVO.setTotalActualQty(sumActualQty);
        purchaseOrderVO.setTotalActualAmt(sumActualAmt);
    }

    public void deletePurchaseOrder(SPM040401Form form) {

        PurchaseOrderVO purchaseOrderVO = spm0404Service.findPurchaseOrderVO(form.getPurchaseOrderId());

        if (null == purchaseOrderVO || !form.getUpdateCount().equals(purchaseOrderVO.getUpdateCount())) {
            throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[] {ComUtil.t("label.purchaseOrderNumber"), form.getOrderNo(), ComUtil.t("title.partsPurchaseOrderMaintenance_01")}));
        }

        List<PurchaseOrderItemVO> purchaseOrderItemVOList = spm0404Service.findPurchaseOrderItemVOList(form.getPurchaseOrderId());

        spm0404Service.deletePurchaseOrder(purchaseOrderVO, purchaseOrderItemVOList);
    }
}
