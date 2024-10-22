package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.DeliveryStatus;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.constants.PJConstants.PartyRoleGroupType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.ReceiptSlipStatus;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.SPM030601BO;
import com.a1stream.domain.bo.parts.SPM030602BO;
import com.a1stream.domain.bo.parts.SPM030603BO;
import com.a1stream.domain.form.parts.SPM030601Form;
import com.a1stream.domain.form.parts.SPM030602Form;
import com.a1stream.domain.form.parts.SPM030603Form;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.ReceiptSlipItemVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.parts.service.SPM0306Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
* 功能描述: Parts Point Transfer Instruction
*
* mid2287
* 2024年7月1日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/07/01   Wang Nan      New
*/
@Component
public class SPM0306Facade {

    @Resource
    private SPM0306Service spm0306Service;

    public Long confirmPartsPointTransferInstruction(SPM030601Form form) {

        this.checkInstruction(form);
        return this.savePartsPointTransferInstruction(form);

    }

    private void checkInstruction(SPM030601Form form) {

        List<SPM030601BO> list = form.getGridList();

        //必入力check(From Point)
        if (Nulls.isNull(form.getFromPointId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("errors.required", new String[]{
                                             CodedMessageUtils.getMessage("label.fromPoint")}));
        }

        //必入力check(To Point)
        if (Nulls.isNull(form.getToPointId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("errors.required", new String[]{
                                             CodedMessageUtils.getMessage("label.toPoint")}));
        }

        //FromPoint <> ToPoint
        if (form.getFromPointId().equals(form.getToPointId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00214", new String[] {
                                             CodedMessageUtils.getMessage("label.fromPoint"),
                                             form.getFromPoint(),
                                             CodedMessageUtils.getMessage("label.toPoint"),
                                             form.getToPoint()}));
        }

        for (SPM030601BO row : list) {

            //必入力check(parts)
            if (StringUtils.isBlank(row.getPartsNo())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("errors.required", new String[] {
                                                 CodedMessageUtils.getMessage("label.partsNo")}));
            }

            //检查parts是否存在
            if (StringUtils.isNotBlank(row.getPartsNo()) && Nulls.isNull(row.getPartsId())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                                 CodedMessageUtils.getMessage("label.partsNo"),
                                                 row.getPartsNo(),
                                                 CodedMessageUtils.getMessage("label.tableProduct")}));
            }

            //TransferQty > 0
            if (!NumberUtil.larger(row.getTransferQty(), BigDecimal.ZERO)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00200", new String[]{
                                                 CodedMessageUtils.getMessage("label.transferQuantity"),
                                                 CommonConstants.CHAR_ZERO}));
            }

            //TransferQty < On Hand Qty
            if (NumberUtil.larger(row.getTransferQty(), row.getOnHandQty())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00203", new String[]{
                                                 CodedMessageUtils.getMessage("label.transferQuantity"),
                                                 CodedMessageUtils.getMessage("label.onHandQuantity")}));
            }
        }

        //检查PartsId是否重复
        if (list.size() > 1) {
            List<String> duplicates = this.findDuplicates(list);
            if (!duplicates.isEmpty()) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00304", new String[] {
                                                 CodedMessageUtils.getMessage("label.partsNo"),
                                                 String.join(CommonConstants.CHAR_COMMA, duplicates)}));
            }
        }

        //检查parts在product_stock_status表中是否存在productStockStatusType = S018ONHANDQTY 且 qty <= 0的数据
        List<Long> parsIds = list.stream().map(SPM030601BO::getPartsId).toList();
        List<ProductStockStatusVO> pssList = spm0306Service.getProductStockStatusVOList(form.getSiteId(),
                                                                                        form.getFromPointId(),
                                                                                        SpStockStatus.ONHAND_QTY.getCodeDbid(),
                                                                                        parsIds);

        //如果存在则报错
        if (!pssList.isEmpty()) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00200", new String[]{
                                             CodedMessageUtils.getMessage("label.onHandQuantity"),
                                             CommonConstants.CHAR_ZERO}));
        }
    }

    public Long savePartsPointTransferInstruction(SPM030601Form form) {

        List<SPM030601BO> insertList = form.getGridList();
        String siteId = form.getSiteId();
        Long fromPointId = form.getFromPointId();
        Long toPointId = form.getToPointId();

//        MstOrganizationVO mstOrganizationVO =spm0306Service.getMstOrganizationVO(siteId, OrgRelationType.SUPPLIER.getCodeDbid());
//        Long customerId = mstOrganizationVO.getOrganizationId();
//        String CustomerNm =mstOrganizationVO.getOrganizationNm();
//        String CustomerCd =mstOrganizationVO.getOrganizationCd();

        //准备DeliveryOrder,调拨设置toFacility不设置toOrg
        DeliveryOrderVO dov = DeliveryOrderVO.create();
        dov.setSiteId(siteId);
        dov.setInventoryTransactionType(InventoryTransactionType.TRANSFEROUT.getCodeDbid());
//        dov.setToOrganizationId(customerId);
//        dov.setCustomerId(customerId);
//        dov.setCustomerCd(CustomerCd);
//        dov.setCustomerNm(CustomerNm);
        dov.setDeliveryStatus(DeliveryStatus.DISPATCHED);
        dov.setFromFacilityId(fromPointId);
        dov.setToFacilityId(toPointId);
        dov.setProductClassification(ProductClsType.PART.getCodeDbid());
        dov.setOrderSourceType(ProductClsType.PART.getCodeDbid());
        dov.setDropShipFlag(CommonConstants.CHAR_N);
        dov.setDeliveryOrderDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
        dov.setOrderToType(PartyRoleGroupType.CUSTOMER.getCodeDbid());
//        dov.setCustomerId(toPointId);
//        dov.setCustomerCd(siteId);
//        dov.setCustomerNm(siteId);

        //准备DeliveryOrderItem
        List<DeliveryOrderItemVO> doItemVoList = new ArrayList<>();
        for (SPM030601BO bo : insertList) {

            DeliveryOrderItemVO doItem = new DeliveryOrderItemVO();
            doItem.setSiteId(siteId);
            doItem.setDeliveryOrderId(dov.getDeliveryOrderId());
            doItem.setProductClassification(ProductClsType.PART.getCodeDbid());
            doItem.setProductId(bo.getPartsId());
            doItem.setProductCd(PartNoUtil.formaForDB(bo.getPartsNo()) );
            doItem.setProductNm(bo.getPartsNm());
            doItem.setOriginalDeliveryQty(bo.getTransferQty());
            doItem.setDeliveryQty(bo.getTransferQty());
            // BigDecimal temp = NumberUtil.divide(bo.getTaxRate(), CommonConstants.BIGDECIMAL_HUNDRED_ROUND1, 2, RoundingMode.HALF_UP);
            // BigDecimal sellingPrice = NumberUtil.multiply(bo.getStdPrice(), temp).setScale(2, RoundingMode.HALF_UP);
            // doItem.setSellingPrice(sellingPrice);
            // doItem.setSellingPriceNotVat(bo.getStdPrice());
            // doItem.setTaxRate(bo.getTaxRate());
            // doItem.setAmt(NumberUtil.multiply(sellingPrice, bo.getTransferQty()).setScale(0, RoundingMode.HALF_UP));
            // doItem.setAmtNotVat(NumberUtil.multiply(doItem.getSellingPriceNotVat(), bo.getTransferQty()).setScale(0, RoundingMode.HALF_UP));
            doItemVoList.add(doItem);
        }

        spm0306Service.savePartsPointTransferInstruction(dov, doItemVoList, fromPointId);
        return dov.getDeliveryOrderId();
    }

    public List<SPM030602BO> getPartsPointTransferReceiptList(SPM030602Form form) {
        this.checkReceipt(form);
        return spm0306Service.getPartsPointTransferReceiptList(form);
    }

    public List<Long> confirmPartsPointTransferReceipt(SPM030602Form form, PJUserDetails uc) {

        //数据准备
        String siteId = uc.getDealerCode();
        Long toFacilityId = form.getToPointId();
        Long fromFacilityId = form.getFromPointId();
        Long personId = uc.getPersonId();
        String personNm = uc.getPersonName();
        Long userOrgId = uc.getUserOrgId();
        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);

        //准备返回前台的报表数据
        List<Long> receiptSlipIds = new ArrayList<>();

        //过滤勾选的数据
        List<SPM030602BO> list = form.getGridList().stream().filter(item -> StringUtils.equals(item.getReceipt(), CommonConstants.CHAR_Y)).toList();

        //获取每一行的deliveryOrderId
        List<Long> deliveryOrderIds = list.stream().map(SPM030602BO::getDeliveryOrderId).toList();

        //获取delivery_order
        List<DeliveryOrderVO> doList = spm0306Service.getDeliveryOrderVOList(deliveryOrderIds);
        Map<Long, DeliveryOrderVO> doMap = doList.stream()
                                                 .collect(Collectors.toMap(DeliveryOrderVO::getDeliveryOrderId,
                                                                           deliveryOrderVO -> deliveryOrderVO));

        //乐观锁Check
        for (SPM030602BO bo : list) {
            DeliveryOrderVO doVO = doMap.get(bo.getDeliveryOrderId());
            if (Nulls.isNull(doVO) || !bo.getUpdateCount().equals(doVO.getUpdateCount())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10449"));
            }
        }

        //获取delivery_order_item
        List<DeliveryOrderItemVO> doiVOList = spm0306Service.getDeliveryOrderItemVOList(deliveryOrderIds.stream().collect(Collectors.toSet()));
        Map<Long, List<DeliveryOrderItemVO>> doiMap = new HashMap<>();
        if (!doiVOList.isEmpty()) {
            doiMap = doiVOList.stream().collect(Collectors.groupingBy(DeliveryOrderItemVO::getDeliveryOrderId));
        }

        //准备receipt_slip
        List<ReceiptSlipVO> rsList = new ArrayList<>();
        List<ReceiptSlipItemVO> rsiList = new ArrayList<>();

        for (SPM030602BO bo : list) {
            ReceiptSlipVO rs = ReceiptSlipVO.create();
            rs.setSiteId(siteId);
            rs.setSlipNo(spm0306Service.generateSlipNo(siteId, toFacilityId));
            rs.setReceivedDate(sysDate);
            rs.setReceiptSlipTotalAmt(bo.getTransferQty());
            rs.setReceivedFacilityId(toFacilityId);
            rs.setReceivedOrganizationId(userOrgId);
            rs.setReceivedPicId(personId);
            rs.setReceivedPicNm(personNm);
            rs.setSupplierDeliveryDate(sysDate);
            rs.setReceiptSlipStatus(ReceiptSlipStatus.RECEIPTED.getCodeDbid());
            rs.setInventoryTransactionType(InventoryTransactionType.TRANSFERIN.getCodeDbid());
            rs.setProductClassification(ProductClsType.PART.getCodeDbid());
            rs.setStoringPicNm(sysDate);
            rs.setStoringPicId(sysDate);
            rsList.add(rs);
            receiptSlipIds.add(rs.getReceiptSlipId());

            //准备receipt_slip_item
            List<DeliveryOrderItemVO> doiList = doiMap.get(bo.getDeliveryOrderId());
            for (DeliveryOrderItemVO doi : doiList) {
                ReceiptSlipItemVO rsi = new ReceiptSlipItemVO();
                rsi.setSiteId(siteId);
                rsi.setReceiptSlipId(rs.getReceiptSlipId());
                rsi.setProductId(doi.getProductId());
                rsi.setProductCd(doi.getProductCd());
                rsi.setProductNm(doi.getProductNm());
                rsi.setReceiptQty(doi.getDeliveryQty());
                rsi.setReceiptPrice(doi.getSellingPrice());
                rsi.setProductClassification(ProductClsType.PART.getCodeDbid());
                rsiList.add(rsi);
            }
        }

        //根据ReceiptSlipId分组
        Map<Long, List<ReceiptSlipItemVO>> rsiMap = new HashMap<>();
        if (!rsiList.isEmpty()) {
            rsiMap = rsiList.stream().collect(Collectors.groupingBy(ReceiptSlipItemVO::getReceiptSlipId));
        }

        //调用共通方法进行更新
        spm0306Service.confirmPartsPointTransferReceipt(rsList,
                                                        rsiList,
                                                        rsiMap,
                                                        deliveryOrderIds,
                                                        siteId,
                                                        toFacilityId,
                                                        fromFacilityId,
                                                        personId,
                                                        personNm);
        return receiptSlipIds;
    }

    private void checkReceipt(SPM030602Form form) {

        //检查toPoint
        if (Nulls.isNull(form.getToPointId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("errors.required", new String[]{
                                             CodedMessageUtils.getMessage("label.toPoint")}));
        }

    }

    public List<SPM030603BO> getTransferPartsDetailList(SPM030603Form form) {
        return spm0306Service.getTransferPartsDetailList(form.getDeliveryOrderId());
    }

    /**
     * 检查list中是否有partsId重复的数据
     */
    private List<String> findDuplicates(List<SPM030601BO> list) {
        Set<Long> duplicateIds = list.stream()
                                     .collect(Collectors.groupingBy(SPM030601BO::getPartsId, Collectors.counting()))
                                     .entrySet().stream()
                                     .filter(entry -> entry.getValue() > 1)
                                     .map(Map.Entry::getKey)
                                     .collect(Collectors.toSet());

        return list.stream().filter(item -> duplicateIds.contains(item.getPartsId()))
                            .map(SPM030601BO::getPartsNo).distinct().toList();
    }

}
