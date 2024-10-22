package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.a1stream.common.constants.PJConstants.PurchaseMethodType;
import com.a1stream.common.constants.PJConstants.PurchaseOrderPriorityType;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.common.model.PartsInfoBO;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.SPM041301BO;
import com.a1stream.domain.form.parts.SPM041301Form;
import com.a1stream.domain.vo.MstOrganizationVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.PurchaseOrderItemVO;
import com.a1stream.domain.vo.PurchaseOrderVO;
import com.a1stream.parts.service.SPM0413Service;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
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
public class SPM0413Facade {

    @Resource
    private SPM0413Service spm0413Service;

    public void confirmPurchaseOrder(SPM041301Form form, PJUserDetails uc) {

        // 提交的数据
        List<SPM041301BO> gridDataList = form.getGridDataList();

        PurchaseOrderVO purchaseOrderVO = this.buildPurchaseOrderData(form, uc);

        List<PurchaseOrderItemVO> purchaseOrderItemVOList = new ArrayList<>();
        Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = new HashMap<>();
        this.buildIssuePurchaseOrderItem(uc, gridDataList, purchaseOrderVO, purchaseOrderItemVOList, stockStatusVOChangeMap);

        this.getSumActualAmt(purchaseOrderVO, purchaseOrderItemVOList);

        spm0413Service.confirmPurchaseOrder(purchaseOrderVO, purchaseOrderItemVOList, stockStatusVOChangeMap);
    }

    private PurchaseOrderVO buildPurchaseOrderData(SPM041301Form form, PJUserDetails uc) {

        PurchaseOrderVO purchaseOrderVO = PurchaseOrderVO.create();
        purchaseOrderVO.setSiteId(uc.getDealerCode());
        purchaseOrderVO.setOrderNo(spm0413Service.purchaseOrderNo(uc.getDealerCode(), form.getPointId()));
        purchaseOrderVO.setProductClassification(ProductClsType.PART.getCodeDbid());
        purchaseOrderVO.setFacilityId(form.getPointId());
        purchaseOrderVO.setCmmFacilityId(null);
        purchaseOrderVO.setFacilityCd(form.getPointCd());
        purchaseOrderVO.setFacilityNm(form.getPointNm());
        MstOrganizationVO supplier = spm0413Service.getPartSupplier(uc.getDealerCode());
        purchaseOrderVO.setSupplierId(supplier.getOrganizationId());
        purchaseOrderVO.setSupplierCd(CommonConstants.CHAR_DEFAULT_SITE_ID);
        purchaseOrderVO.setSupplierNm(supplier.getOrganizationNm());
        purchaseOrderVO.setOrderDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
        purchaseOrderVO.setOrderPicId(uc.getPersonId());
        purchaseOrderVO.setOrderPicNm(uc.getPersonName());
        purchaseOrderVO.setApproveDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
        purchaseOrderVO.setApprovePicId(uc.getPersonId());
        purchaseOrderVO.setApprovePicNm(uc.getPersonName());
        purchaseOrderVO.setOrderStatus(PurchaseOrderStatus.SPONPURCHASE);
        purchaseOrderVO.setBoCancelFlag(CommonConstants.CHAR_N);
        purchaseOrderVO.setOrderPriorityType(PurchaseOrderPriorityType.POWO.getCodeDbid());
        purchaseOrderVO.setOrderMethodType(PurchaseMethodType.POINDIVIDUAL.getCodeDbid());
        purchaseOrderVO.setDeliverPlanDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
        purchaseOrderVO.setSalesOrderNo(null);
        purchaseOrderVO.setSupplierOrderNo(null);
        purchaseOrderVO.setConsigneeId(null);
        purchaseOrderVO.setMemo(form.getMemo());
        return purchaseOrderVO;
    }

    private void buildIssuePurchaseOrderItem(PJUserDetails uc
                                           , List<SPM041301BO> gridDataList
                                           , PurchaseOrderVO purchaseOrderVO
                                           , List<PurchaseOrderItemVO> purchaseOrderItemVOList
                                           , Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap) {

        Integer seqNo = 1;
        for (SPM041301BO bo : gridDataList) {
             // 新增purchaseOrderItem
            PurchaseOrderItemVO purchaseOrderItemVO = PurchaseOrderItemVO.create();
            purchaseOrderItemVO.setSiteId(uc.getDealerCode());
            purchaseOrderItemVO.setPurchaseOrderId(purchaseOrderVO.getPurchaseOrderId());
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
            purchaseOrderItemVO.setSeqNo(seqNo++);

            purchaseOrderItemVOList.add(purchaseOrderItemVO);

            // 更新productStockStatus
            spm0413Service.generateStockStatusVOMap(uc.getDealerCode()
                                                  , purchaseOrderVO.getFacilityId()
                                                  , bo.getPartsId()
                                                  , SpStockStatus.WO_ONPURCHASE_QTY.getCodeDbid()
                                                  , bo.getOrderQty()
                                                  , stockStatusVOChangeMap);

        }
    }

    private void getSumActualAmt(PurchaseOrderVO purchaseOrderVO, List<PurchaseOrderItemVO> purchaseOrderItemVOList) {

        BigDecimal sumOrderQty = purchaseOrderItemVOList.stream().map(item -> item.getOrderQty() != null ? item.getOrderQty() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumAmount = purchaseOrderItemVOList.stream().map(item -> item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumActualQty = purchaseOrderItemVOList.stream().map(item -> item.getActualQty() != null ? item.getActualQty() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumActualAmt = purchaseOrderItemVOList.stream().map(item -> item.getActualAmt() != null ? item.getActualAmt() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);

        purchaseOrderVO.setTotalQty(sumOrderQty);
        purchaseOrderVO.setTotalAmount(sumAmount);
        purchaseOrderVO.setTotalActualQty(sumActualQty);
        purchaseOrderVO.setTotalActualAmt(sumActualAmt);
    }

        public SPM041301Form checkFile(SPM041301Form form, PJUserDetails uc) {

        // 上传的数据
        List<SPM041301BO> importList = form.getImportList();
        if (CollectionUtils.isEmpty(importList)) {
            return form;
        }

        for (SPM041301BO importItem : importList) {
            importItem.setPartsNo(PartNoUtil.formaForDB(importItem.getPartsNo()));
        }

        List<String> partsNoList = importList.stream().map(SPM041301BO::getPartsNo).toList();
        List<PartsInfoBO> partsInfoBOList = spm0413Service.getPartsInfoList(partsNoList, uc);
        Map<String, PartsInfoBO> partsInfoMap = partsInfoBOList.stream().collect(Collectors.toMap(PartsInfoBO::getPartsNo, Function.identity()));
        for (SPM041301BO bo : importList) {
            List<String> error          = new ArrayList<>();
            List<Object[]> errorParam   = new ArrayList<>();
            List<String> warning        = new ArrayList<>();
            List<Object[]> warningParam = new ArrayList<>();

            // partsNo是否存在
            if (!partsInfoMap.containsKey(bo.getPartsNo())) {
                error.add("M.E.00303");
                errorParam.add(new Object[]{"label.Parts", bo.getPartsNo(), "label.tableProduct"});
            }

            // partsNo是否重复
            if (partsNoList.indexOf(bo.getPartsNo()) != partsNoList.lastIndexOf(bo.getPartsNo())) {
                error.add("M.E.00301");
                errorParam.add(new Object[]{"label.Parts No"});
            }

            // orderQty是否小于等于0
            if (bo.getOrderQty().compareTo(BigDecimal.ZERO) <= 0) {
                error.add("M.E.00200");
                errorParam.add(new Object[]{"label.Order Qty", CommonConstants.CHAR_ZERO});
            }

            // 填充其余信息
            PartsInfoBO partsInfoBO = partsInfoMap.get(bo.getPartsNo());
            bo.setPartsNo(PartNoUtil.format(partsInfoBO.getPartsNo()));
            bo.setPartsId(partsInfoBO.getPartsId());
            bo.setPartsNm(partsInfoBO.getPartsNm());
            bo.setPrice(partsInfoBO.getPrice());
            bo.setMinPurchaseQty(partsInfoBO.getMinPurQty());
            bo.setPurchaseLot(partsInfoBO.getPurLotSize());
            bo.setCancelFlag(CommonConstants.CHAR_N);
            bo.setStdRetailPrice(partsInfoBO.getStdRetailPrice());

            bo.setError(error);
            bo.setErrorParam(errorParam);
            bo.setWarning(warning);
            bo.setWarningParam(warningParam);

            BigDecimal orderQty = bo.getOrderQty();
            BigDecimal minPurchaseQty = bo.getMinPurchaseQty();
            BigDecimal purchaseLot = bo.getPurchaseLot();

            if (orderQty.compareTo(minPurchaseQty) <= 0) {
                bo.setOrderQty(minPurchaseQty);
                continue;
            }

            BigDecimal excessQty = orderQty.subtract(minPurchaseQty);
            BigDecimal adjustedExcessQty = purchaseLot.multiply(excessQty.divide(purchaseLot, 0, RoundingMode.CEILING));
            BigDecimal adjustedOrderQty = minPurchaseQty.add(adjustedExcessQty);
            bo.setOrderQty(adjustedOrderQty);
        }

        return form;
    }
}
