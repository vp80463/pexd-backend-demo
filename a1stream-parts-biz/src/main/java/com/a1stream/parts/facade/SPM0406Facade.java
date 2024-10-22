package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.PurchaseOrderStatus;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.PurchaseMethodType;
import com.a1stream.common.model.PartsInfoBO;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.SPM040601BO;
import com.a1stream.domain.form.parts.SPM040601Form;
import com.a1stream.domain.vo.PurchaseOrderItemVO;
import com.a1stream.domain.vo.PurchaseOrderVO;
import com.a1stream.parts.service.SPM0406Service;
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
public class SPM0406Facade {

    @Resource
    private SPM0406Service spm0406Service;

     private void validateData(SPM040601Form model,String siteId){

        List<String> productCdList = model.getGridDataList().stream()
                                          .map(SPM040601BO::getPartsNo) 
                                          .collect(Collectors.toList());
        for (SPM040601BO member : model.getGridDataList()) {

            if (ObjectUtils.isEmpty(member.getPartsId())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[]{ CodedMessageUtils.getMessage("label.productNumber"),member.getPartsNo(),CodedMessageUtils.getMessage("label.productInformation")}));
            }

            //检查数据本身是否重复
            if (productCdList.indexOf(member.getPartsNo()) != productCdList.lastIndexOf(member.getPartsNo())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00301", new String[]{ CodedMessageUtils.getMessage("label.productNumber")}));
            }

        }
     }

    public void confirmPurchaseOrder(SPM040601Form form, PJUserDetails uc) {

        this.validateData(form, uc.getDealerCode());

        // 提交的数据
        List<SPM040601BO> gridDataList = form.getGridDataList();

        PurchaseOrderVO purchaseOrderVO = this.buildPurchaseOrder(form, uc);

        List<PurchaseOrderItemVO> purchaseOrderItemVOList = this.buildPurchaseOrderItem(uc, gridDataList, purchaseOrderVO);

        this.getPurchaseOrderTotal(purchaseOrderVO, purchaseOrderItemVOList);

        spm0406Service.confirmPurchaseOrder(purchaseOrderVO, purchaseOrderItemVOList);
    }

    private void getPurchaseOrderTotal(PurchaseOrderVO purchaseOrderVO, List<PurchaseOrderItemVO> purchaseOrderItemVOList) {

        BigDecimal sumOrderQty = purchaseOrderItemVOList.stream().map(item -> item.getOrderQty() != null ? item.getOrderQty() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumAmount = purchaseOrderItemVOList.stream().map(item -> item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumActualQty = purchaseOrderItemVOList.stream().map(item -> item.getActualQty() != null ? item.getActualQty() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumActualAmt = purchaseOrderItemVOList.stream().map(item -> item.getActualAmt() != null ? item.getActualAmt() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);

        purchaseOrderVO.setTotalQty(sumOrderQty);
        purchaseOrderVO.setTotalAmount(sumAmount);
        purchaseOrderVO.setTotalActualQty(sumActualQty);
        purchaseOrderVO.setTotalActualAmt(sumActualAmt);
    }

    private List<PurchaseOrderItemVO> buildPurchaseOrderItem(PJUserDetails uc, List<SPM040601BO> gridDataList, PurchaseOrderVO purchaseOrderVO) {

        List<PurchaseOrderItemVO> purchaseOrderItemVOList = new ArrayList<>();

        Integer seqNo = 1;
        for (SPM040601BO bo : gridDataList) {
             // 新增purchaseOrderItem
            PurchaseOrderItemVO purchaseOrderItemVO = PurchaseOrderItemVO.create();
            purchaseOrderItemVO.setSiteId(uc.getDealerCode());
            purchaseOrderItemVO.setPurchaseOrderId(purchaseOrderVO.getPurchaseOrderId());
            purchaseOrderItemVO.setProductId(bo.getPartsId());
            purchaseOrderItemVO.setProductCd(PartNoUtil.formaForDB(bo.getPartsNo()));
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
            purchaseOrderItemVO.setSeqNo(seqNo++);

            purchaseOrderItemVOList.add(purchaseOrderItemVO);
        }
        return purchaseOrderItemVOList;
    }

    private PurchaseOrderVO buildPurchaseOrder(SPM040601Form form, PJUserDetails uc) {
        PurchaseOrderVO purchaseOrderVO = PurchaseOrderVO.create();
        purchaseOrderVO.setSiteId(uc.getDealerCode());
        purchaseOrderVO.setOrderNo(spm0406Service.purchaseOrderNo(uc.getDealerCode(), form.getPointId()));
        purchaseOrderVO.setProductClassification(ProductClsType.PART.getCodeDbid());
        purchaseOrderVO.setFacilityId(form.getPointId());
        purchaseOrderVO.setCmmFacilityId(null);
        purchaseOrderVO.setFacilityCd(form.getPointCd());
        purchaseOrderVO.setFacilityNm(form.getPointNm());
        purchaseOrderVO.setSupplierId(form.getSupplierId());
        purchaseOrderVO.setSupplierCd(form.getSupplierCd());
        purchaseOrderVO.setSupplierNm(form.getSupplierNm());
        purchaseOrderVO.setOrderDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
        purchaseOrderVO.setOrderPicId(uc.getPersonId());
        purchaseOrderVO.setOrderPicNm(uc.getPersonName());
        purchaseOrderVO.setApproveDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
        purchaseOrderVO.setApprovePicId(uc.getPersonId());
        purchaseOrderVO.setApprovePicNm(uc.getPersonName());
        purchaseOrderVO.setOrderStatus(PurchaseOrderStatus.SPWAITINGISSUE);
        purchaseOrderVO.setBoCancelFlag(CommonConstants.CHAR_N);
        purchaseOrderVO.setOrderPriorityType(form.getType());
        purchaseOrderVO.setOrderMethodType(PurchaseMethodType.POINDIVIDUAL.getCodeDbid());
        purchaseOrderVO.setDeliverPlanDate(form.getDeliveryPlanDate());
        purchaseOrderVO.setSalesOrderNo(null);
        purchaseOrderVO.setSupplierOrderNo(null);
        purchaseOrderVO.setConsigneeId(null);
        purchaseOrderVO.setMemo(form.getMemo());
        return purchaseOrderVO;
    }

    public SPM040601Form checkFile(SPM040601Form form, PJUserDetails uc) {

        // 上传的数据
        List<SPM040601BO> importList = form.getImportList();
        if (CollectionUtils.isEmpty(importList)) {
            return form;
        }

        for (SPM040601BO importItem : importList) {
            importItem.setPartsNo(PartNoUtil.formaForDB(importItem.getPartsNo()));
        }

        List<String> partsNoList = importList.stream().map(SPM040601BO::getPartsNo).toList();
        List<PartsInfoBO> partsInfoBOList = spm0406Service.getPartsInfoList(partsNoList, uc);
        Map<String, PartsInfoBO> partsInfoMap = partsInfoBOList.stream().collect(Collectors.toMap(PartsInfoBO::getPartsNo, Function.identity()));
        for (SPM040601BO bo : importList) {
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
