/**
 *
 */
package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;

import com.a1stream.common.bo.SalesReturnBO;
import com.a1stream.common.bo.SalesReturnDetailBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.DeliveryStatus;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.manager.GenerateNoManager;
import com.a1stream.domain.bo.parts.SPM020201BO;
import com.a1stream.domain.bo.parts.SPM020202BO;
import com.a1stream.domain.form.parts.SPM020201Form;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.InvoiceItemVO;
import com.a1stream.domain.vo.InvoiceVO;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.OrganizationRelationVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.parts.service.SPM0202Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;


/**
* 功能描述:
*
* @author mid2259
*/
@Component
public class SPM0202Facade {

    @Resource
    SPM0202Service spm0202Service;

    @Resource
    private GenerateNoManager generateNoManager;

    public SPM020201BO searchInvoiceInfo(@RequestBody final SPM020201Form model) {

        InvoiceVO invoiceVO = spm0202Service.searchInvoiceByInvoiceNo(model);
        SPM020201BO result = new SPM020201BO();

        if (!ObjectUtils.isEmpty(invoiceVO) && StringUtils.equals(invoiceVO.getOrderSourceType(), PJConstants.ProductClsType.PART.getCodeDbid())) {

            List<InvoiceItemVO> invoiceItemVOs = spm0202Service.searchInvoiceItemByInvoiceId(invoiceVO.getInvoiceId());
            //将ProductId和LocationId抽出
            Set<Long> partsIds = invoiceItemVOs.stream().map(InvoiceItemVO::getProductId).collect(Collectors.toSet());

            //从productInventory表中查询是否为主货位
            List<ProductInventoryVO> productInventoryVOs = spm0202Service.findMainProductInventoryList(model, partsIds);
            //将其获取的数据转换为key为productId，value为自身的map
            Map<Long, ProductInventoryVO> productInventoryVOMap = productInventoryVOs.stream()
                    .collect(Collectors.toMap(ProductInventoryVO::getProductId, vo -> vo));

            List<SPM020202BO> content = new ArrayList<>();
            result.setInvoiceNo(invoiceVO.getInvoiceNo());
            result.setInvoiceId(invoiceVO.getInvoiceId());
            result.setFromOrganizationId(invoiceVO.getFromOrganizationId());
            result.setToOrganizationId(invoiceVO.getToOrganizationId());
            result.setReturnByInvoice(CommonConstants.CHAR_Y);
            result.setCustomer(invoiceVO.getCustomerCd() + CommonConstants.CHAR_SPACE + invoiceVO.getCustomerNm());
            result.setCustomerId(invoiceVO.getToOrganizationId());
            result.setCustomerCd(invoiceVO.getCustomerCd());
            result.setCustomerNm(invoiceVO.getCustomerNm());
            result.setInvoiceDate(invoiceVO.getInvoiceDate());
            result.setReturnByInvoiceInvisible(CommonConstants.CHAR_ONE);
            result.setOrderToType(invoiceVO.getOrderToType());

            for (InvoiceItemVO item : invoiceItemVOs) {

                    SPM020202BO bo =new SPM020202BO();
                    bo.setPartsCd(item.getProductCd());
                    bo.setInvoiceItemId(item.getInvoiceItemId());
                    bo.setPartsNm(item.getProductNm());
                    bo.setPartsId(item.getProductId());
                    ProductInventoryVO mainInventory = productInventoryVOMap.get(item.getProductId());
                    if (!ObjectUtils.isEmpty(mainInventory)) {
                        bo.setLocationId(mainInventory.getLocationId());
                        bo.setLocation(item.getLocationCd());
                    }
                    bo.setReturnPrice(item.getSellingPrice());
                    bo.setReturnPriceNotVat(item.getSellingPriceNotVat());
                    bo.setReturnQty(BigDecimal.ZERO);
                    bo.setReturnAmount(BigDecimal.ZERO);
                    bo.setOrderItemId(item.getRelatedSoItemId());
                    bo.setCost(item.getCost());
                    bo.setQty(item.getQty());
                    bo.setTaxRate(item.getTaxRate());
                    bo.setSalesOrderNo(item.getSalesOrderNo());
                    bo.setOrderDate(item.getOrderDate());
                    bo.setSalesOrderId(item.getSalesOrderId());
                    bo.setOrderType(item.getOrderType());
                    bo.setOrderSourceType(item.getOrderSourceType());
                    bo.setCustomerOrderNo(item.getCustomerOrderNo());
                    content.add(bo);

            }

            result.setContent(content);
        }else {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10248"));
        }
        return result;
    }

    public SPM020201Form confirmSalesReturn(@RequestBody final SPM020201Form model) {
        this.validateData(model);

        DeliveryOrderVO deliveryOrderVO = this.buildDeliveryOrder(model);
        List<DeliveryOrderItemVO> deliveryOrderItemVOs = this.buildDeliveryOrderItem(model,deliveryOrderVO);

        SalesReturnBO salesReturnBO = this.boToSalesReturnBO(model);
        List<SalesReturnDetailBO> detailBOs = this.contentToSalesReturnDetailBO(model);
        salesReturnBO.setDetails(detailBOs);

        return spm0202Service.confirmSalesReturn(model, salesReturnBO, deliveryOrderVO, deliveryOrderItemVOs, model.getPersonId(), model.getPersonNm());
    }

    private List<DeliveryOrderItemVO> buildDeliveryOrderItem(SPM020201Form model,DeliveryOrderVO deliveryOrderVO) {

        List<DeliveryOrderItemVO> result = new ArrayList<>();
        List<SPM020202BO> content = model.getContent();

        Integer seqNo = 1;
        for (SPM020202BO bo : content) {
            DeliveryOrderItemVO deliveryOrderItemVO = DeliveryOrderItemVO.create();
            deliveryOrderItemVO.setSiteId(model.getSiteId());
            deliveryOrderItemVO.setDeliveryOrderId(deliveryOrderVO.getDeliveryOrderId());
            deliveryOrderItemVO.setProductClassification(ProductClsType.PART.getCodeDbid());
            deliveryOrderItemVO.setSeqNo(seqNo++);
            deliveryOrderItemVO.setProductId(bo.getPartsId());
            deliveryOrderItemVO.setProductCd(bo.getPartsCd());
            deliveryOrderItemVO.setProductNm(bo.getPartsNm());
            deliveryOrderItemVO.setOriginalDeliveryQty(bo.getQty());
            deliveryOrderItemVO.setDeliveryQty(bo.getReturnQty());
            deliveryOrderItemVO.setProductCost(bo.getCost());
            deliveryOrderItemVO.setSellingPrice(bo.getReturnPrice());
            deliveryOrderItemVO.setSellingPriceNotVat(bo.getReturnPriceNotVat());
            deliveryOrderItemVO.setAmt(bo.getReturnAmount());
            deliveryOrderItemVO.setOrderItemId(bo.getOrderItemId());
            deliveryOrderItemVO.setSalesOrderId(bo.getSalesOrderId());
            deliveryOrderItemVO.setSalesOrderNo(bo.getSalesOrderNo());
            result.add(deliveryOrderItemVO);
        }

        return result;
    }

    private SalesReturnBO boToSalesReturnBO(SPM020201Form model) {

        List<OrganizationRelationVO> consumerOrg = spm0202Service.findBySiteIdAndRelationType(model.getSiteId(),PJConstants.OrgRelationType.CONSUMER.getCodeDbid());
        List<OrganizationRelationVO> companyOrg =spm0202Service.findBySiteIdAndRelationType(model.getSiteId(),PJConstants.OrgRelationType.COMPANY.getCodeDbid());

        SalesReturnBO result = new SalesReturnBO();
        result.setInvoiceId(model.getInvoiceId());
        result.setInvoiceNo(model.getInvoiceNo());
        result.setCustomerId(model.getCustomerId());
        result.setCustomerCd(model.getCustomerCd());
        result.setCustomerName(model.getCustomerNm());

        if (!ObjectUtils.isEmpty(consumerOrg)) {
            result.setConsumerId(consumerOrg.get(CommonConstants.INTEGER_ZERO).getToOrganizationId());
        }
        if (!ObjectUtils.isEmpty(companyOrg)) {
            result.setFromOrganizationId(companyOrg.get(CommonConstants.INTEGER_ZERO).getToOrganizationId());
        }
        result.setPointId(model.getPointId());
        result.setReason(model.getReason());
        result.setInvoiceDate(model.getInvoiceDate());
        result.setOrderToType(model.getOrderToType());

        return result;
    }

    private List<SalesReturnDetailBO> contentToSalesReturnDetailBO(SPM020201Form model) {

        List<SalesReturnDetailBO> result = new ArrayList<>();
        for (SPM020202BO bo : model.getContent()) {
            SalesReturnDetailBO data = new SalesReturnDetailBO();
            data.setProductId(bo.getPartsId());
            data.setProductCd(bo.getPartsCd());
            data.setProductNm(bo.getPartsNm());
            data.setLocationId(bo.getLocationId());
            data.setLocationCd(bo.getLocation());
            data.setReturnPrice(bo.getReturnPrice());
            data.setReturnPriceNotVat(bo.getReturnPriceNotVat());
            data.setReturnQty(bo.getReturnQty());
            data.setReturnAmount(bo.getReturnAmount());
            data.setCost(bo.getCost());
            data.setTaxRate(bo.getTaxRate());
            data.setInvoiceItemId(bo.getInvoiceItemId());
            data.setRelatedSoItemId(bo.getOrderItemId());
            data.setSalesOrderNo(bo.getSalesOrderNo());
            data.setOrderDate(bo.getOrderDate());
            data.setOrderType(bo.getOrderType());
            data.setOrderSourceType(bo.getOrderSourceType());
            data.setCustomerOrderNo(bo.getCustomerOrderNo());
            result.add(data);
        }

        return result;
    }

    private DeliveryOrderVO buildDeliveryOrder(SPM020201Form model) {

        List<SPM020202BO> content = model.getContent();

        //合计
        BigDecimal totalQty = content.stream()
                                     .map(SPM020202BO :: getReturnQty)
                                     .reduce(BigDecimal.ZERO, BigDecimal :: add);
        BigDecimal totalAmount = content.stream()
                                        .map(SPM020202BO :: getReturnAmount)
                                        .reduce(BigDecimal.ZERO, BigDecimal :: add);
        BigDecimal totalAmountNotVat = content.stream()
                                              .map(SPM020202BO :: getReturnPriceNotVat)
                                              .reduce(BigDecimal.ZERO, BigDecimal :: add);
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);
        String sysDate = currentDate.format(formatter);

        DeliveryOrderVO deliveryOrderVO = DeliveryOrderVO.create();
        deliveryOrderVO.setSiteId(model.getSiteId());
        deliveryOrderVO.setDeliveryOrderNo(generateNoManager.generateDeliveryNo(model.getSiteId(), model.getPointId()));
        deliveryOrderVO.setInventoryTransactionType(PJConstants.InventoryTransactionType.RETURNIN.getCodeDbid());
        deliveryOrderVO.setDeliveryStatus(DeliveryStatus.INVOICED);
        deliveryOrderVO.setFromOrganizationId(model.getFromOrganizationId());
//      退货设置toFacility不设置toOrg
//        deliveryOrderVO.setToOrganizationId(model.getToOrganizationId());
//        deliveryOrderVO.setCustomerId(model.getCustomerId());
//        deliveryOrderVO.setCustomerNm(model.getCustomerNm());
//        deliveryOrderVO.setCustomerCd(model.getCustomerCd());
        deliveryOrderVO.setFromFacilityId(model.getPointId());
        deliveryOrderVO.setToFacilityId(model.getPointId());
        deliveryOrderVO.setTotalQty(totalQty);
        deliveryOrderVO.setTotalAmt(totalAmount);
        deliveryOrderVO.setTotalAmtNotVat(totalAmountNotVat);
        deliveryOrderVO.setSalesReturnReasonId(model.getReason());
        deliveryOrderVO.setDeliveryOrderDate(sysDate);
        deliveryOrderVO.setProductClassification(PJConstants.ProductClsType.PART.getCodeDbid());
        deliveryOrderVO.setOrderSourceType(PJConstants.ProductClsType.PART.getCodeDbid());
        deliveryOrderVO.setDropShipFlag(CommonConstants.CHAR_N);
        deliveryOrderVO.setActivityFlag(CommonConstants.CHAR_Y);

        return deliveryOrderVO;
    }

    private void validateData(SPM020201Form model){

        List<SPM020202BO> content = model.getContent();
        Set<Long> locationIds = content.stream().map(SPM020202BO::getLocationId).collect(Collectors.toList()).stream().collect(Collectors.toSet());
        List<LocationVO> locationList = spm0202Service.findByLocationIdIn(locationIds, model.getPointId());
        Map<Long, LocationVO> locationMap = locationList.stream()
                .collect(Collectors.toMap(LocationVO::getLocationId, vo -> vo));

        Set<Long> invoiceItemIds = content.stream().map(SPM020202BO::getInvoiceItemId).collect(Collectors.toList()).stream().collect(Collectors.toSet());
        List<InvoiceItemVO> returnInvoiceItemVOs = spm0202Service.searchReturnInvoiceItem(invoiceItemIds);

        Map<Long, BigDecimal> quantityMap = returnInvoiceItemVOs.stream()
                .collect(Collectors.toMap(InvoiceItemVO::getRelatedInvoiceItemId, InvoiceItemVO::getQty, BigDecimal::add));


        for (SPM020202BO bo : content) {

            //判断fromLocation是否存在
            if(ObjectUtils.isEmpty(bo.getLocationId())){
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.location"), bo.getLocation(),CodedMessageUtils.getMessage("label.tableLocationInfo") }));
            }

            if (bo.getReturnPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00200", new String[]{ CodedMessageUtils.getMessage("label.returnPrice"),CommonConstants.CHAR_ZERO}));
            }

            if (bo.getReturnQty().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00200", new String[]{ CodedMessageUtils.getMessage("label.returnQuantity"),CommonConstants.CHAR_ZERO}));
            }

            //代表该location不属于该point
            if (ObjectUtils.isEmpty(locationMap.get(bo.getLocationId()))) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10158", new String[] {CodedMessageUtils.getMessage("label.location"), bo.getLocation(), CodedMessageUtils.getMessage("label.point"), model.getPoint()}));
            }

            //如果存在location则需要判断其type，如果不符合以下三种，则抛出异常
            if (Arrays.asList(PJConstants.LocationType.TENTATIVE.getCodeDbid()
                            , PJConstants.LocationType.NORMAL.getCodeDbid()
                            , PJConstants.LocationType.FROZEN.getCodeDbid()).stream().noneMatch(locationMap.get(bo.getLocationId()).getLocationType()::equals)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10055", new String[] {CodedMessageUtils.getMessage("label.location"), bo.getLocation(),CodedMessageUtils.getMessage("label.servcieWorkLocation")}));
            }

            //检查partsId是否存在
            if(ObjectUtils.isEmpty(bo.getPartsId())){
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.partsNo"), bo.getPartsCd(), CodedMessageUtils.getMessage("label.product")}));
            }

            //还需要拿到以前退货过的数量做比较
            BigDecimal returnedQty = quantityMap.get(bo.getInvoiceItemId());
            if(returnedQty == null) {
                returnedQty = BigDecimal.ZERO;
            }
            if((bo.getReturnQty().add(returnedQty)).compareTo(bo.getQty()) > 0) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00215", new String[] {CodedMessageUtils.getMessage("label.parts"),bo.getPartsCd(),CodedMessageUtils.getMessage("label.returnQuantity"),CodedMessageUtils.getMessage("label.returnedQuantity"),CodedMessageUtils.getMessage("label.salesQuantity")}));
            }

        }

        //存在性验证
        if (ObjectUtils.isEmpty(model.getCustomerId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.customer"), model.getCustomer(), CodedMessageUtils.getMessage("label.tablePartyInfo")}));
        }

        //检查pointId是否存在
        if(ObjectUtils.isEmpty(model.getPointId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.point"), model.getPoint(), CodedMessageUtils.getMessage("label.tableFacilityInfo")}));
        }

    }

}
