package com.a1stream.unit.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.JudgementStatus;
import com.a1stream.common.constants.MstCodeConstants.McSalesStatus;
import com.a1stream.domain.bo.unit.SDM050801BO;
import com.a1stream.domain.form.unit.SDM050801Form;
import com.a1stream.domain.vo.CmmPromotionOrderVO;
import com.a1stream.domain.vo.CmmUnitPromotionItemVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.unit.service.SDM0508Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
* 功能描述: Promotion Data Recovery
*
* mid2287
* 2024年8月28日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/29   Wang Nan      New
*/

@Component
public class SDM0508Facade {

    @Resource
    private SDM0508Service sdm0508Service;

    public List<SDM050801BO> getPromoRecList(SDM050801Form form) {

        //检索前Check
        this.validateBeforeRetrieve(form);

        //先检索
        List<SDM050801BO> promoRecList = sdm0508Service.getCpoPromoRecList(form);

        if (promoRecList.isEmpty()) {

            promoRecList = sdm0508Service.getSpPromoRecList(form);

            promoRecList.forEach(bo -> {
                bo.setPromotionCd(form.getPromotionCd());
            });
        }

        return promoRecList;
    }

    private void validateBeforeRetrieve(SDM050801Form form) {

        //非空Check
        if (Nulls.isNull(form.getPointId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10326", new String[] {
                                             CodedMessageUtils.getMessage("label.point")}));
        }

        if (StringUtils.isBlank(form.getPromotion())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10326", new String[] {
                                             CodedMessageUtils.getMessage("label.promotion")}));
        }

        if (StringUtils.isBlank(form.getFrameNo())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10326", new String[] {
                                             CodedMessageUtils.getMessage("label.frameNo")}));
        }

        //存在性Check
        if(StringUtils.isNotBlank(form.getPromotion()) && Nulls.isNull(form.getPromotionId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.promotion"),
                                             form.getPromotion(),
                                             CodedMessageUtils.getMessage("label.tablePromotionList")}));
        }

        //判断当前促销是否过期
        if (Nulls.isNull(sdm0508Service.getActivePromotionList(form))) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00210", new String[] {
                                             CodedMessageUtils.getMessage("label.promotion"),
                                             CodedMessageUtils.getMessage("label.effectiveDate"),
                                             CodedMessageUtils.getMessage("label.expiredDate")}));
        }

        //判断当前车辆的车型是否在当前促销车型中
        if (Nulls.isNull(sdm0508Service.getPromotionProduct(form))) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10302", new String[] {
                                             CodedMessageUtils.getMessage("label.frameNumber"),
                                             form.getFrameNo()}));
        }

    }

    public void confirm(SDM050801Form form) {

        List<SDM050801BO> updList = form.getGridData();

        if (updList.isEmpty()) {
            return;
        }

        //保存逻辑
        this.save(form);

    }

    private void save(SDM050801Form form) {

        List<SDM050801BO> list = form.getGridData();

        List<CmmUnitPromotionItemVO> saveCmmUnitPromotionItemVOList = new ArrayList<>();
        List<CmmPromotionOrderVO> saveCmmPromotionOrderVOList = new ArrayList<>();

        //1.若明细部的车辆在CmmUnitPromotionItem中存在
        List<CmmUnitPromotionItemVO> cmmUnitPromotionItemVOList = sdm0508Service.getCmmUnitPromotionItemVOList(form);

        if (!cmmUnitPromotionItemVOList.isEmpty()) {

            //若StockMcFlag为N, 则更新StockMcFlag为Y
            cmmUnitPromotionItemVOList.forEach(item -> {

                if (StringUtils.equals(CommonConstants.CHAR_N, item.getStockMcFlag())) {
                    item.setStockMcFlag(CommonConstants.CHAR_Y);
                    saveCmmUnitPromotionItemVOList.add(item);
                }

            });

            //2.判断该车是否已经卖出
            this.prepareCmmPromotionOrder(form, list, saveCmmPromotionOrderVOList);

        } else {

            for (SDM050801BO bo : list) {
                saveCmmUnitPromotionItemVOList.add(this.createCmmUnitPromotionItemVO(form, bo));
            }

        }

        sdm0508Service.save(saveCmmUnitPromotionItemVOList, saveCmmPromotionOrderVOList);
    }

    private CmmUnitPromotionItemVO createCmmUnitPromotionItemVO(SDM050801Form form, SDM050801BO bo) {

        CmmUnitPromotionItemVO item = CmmUnitPromotionItemVO.create();
        item.setPromotionListId(form.getPromotionId());
        item.setSiteId(bo.getSiteId());
        item.setFacilityId(bo.getFacilityId());
        item.setProductId(bo.getProductId());
        item.setCmmSerializedProductId(bo.getCmmSerializedProductId());
        item.setFrameNo(bo.getFrameNo());
        item.setStockMcFlag(CommonConstants.CHAR_Y);
        return item;

    }

    private void prepareCmmPromotionOrder(SDM050801Form form,
                                          List<SDM050801BO> list,
                                          List<CmmPromotionOrderVO> cmmPromotionOrderVOList) {

        List<Long> serializedProductIds = list.stream()
                                              .map(SDM050801BO::getSerializedProductId)
                                              .toList();

        //获取已卖出的车
        List<SerializedProductVO> serializedProductVOList = sdm0508Service.getSerializedProductVOList(serializedProductIds,
                                                                                                      McSalesStatus.SALESTOUSER);

        Map<Long, SerializedProductVO> serializedProductVOMap = serializedProductVOList
                                                                .stream()
                                                                .collect(Collectors.toMap(SerializedProductVO::getSerializedProductId,
                                                                                          serializedProductVO -> serializedProductVO));

        list.forEach(bo -> Optional.ofNullable(serializedProductVOMap.get(bo.getSerializedProductId()))
                                   .ifPresent(serializedProduct -> cmmPromotionOrderVOList.add(this.createCmmPromotionOrderVO(form, bo))));

    }

    private CmmPromotionOrderVO createCmmPromotionOrderVO(SDM050801Form form, SDM050801BO bo) {

        CmmPromotionOrderVO promotionOrder = CmmPromotionOrderVO.create();
        promotionOrder.setPromotionListId(form.getPromotionId());
        promotionOrder.setSiteId(bo.getSiteId());
        promotionOrder.setSiteNm(bo.getSiteNm());
        promotionOrder.setFacilityId(bo.getFacilityId());
        promotionOrder.setFacilityCd(bo.getFacilityCd());
        promotionOrder.setFacilityNm(bo.getFacilityNm());
        promotionOrder.setLocalOrderId(bo.getSalesOrderId());
        promotionOrder.setLocalOrderNo(bo.getOrderNo());
        promotionOrder.setCmmProductId(bo.getProductId());
        promotionOrder.setProductCd(bo.getProductCd());
        promotionOrder.setProductNm(bo.getProductNm());
        promotionOrder.setLocalSerialProductId(bo.getSerializedProductId());
        promotionOrder.setCmmSerialProductId(bo.getCmmSerializedProductId());
        promotionOrder.setCmmCustomerId(bo.getConsumerId());
        promotionOrder.setCustomerNm(bo.getConsumerFullNm());
        promotionOrder.setFrameNo(bo.getFrameNo());
        promotionOrder.setOrderDate(bo.getOrderDate());
        promotionOrder.setLocalInvoiceId(bo.getInvoiceId());
        promotionOrder.setLocalInvoiceNo(bo.getInvoiceNo());
        promotionOrder.setLocalDeliveryOrderId(bo.getDeliveryOrderId());
        promotionOrder.setLocalDeliveryOrderNo(bo.getDeliveryOrderNo());
        promotionOrder.setSalesMethod(CommonConstants.SALES_TO_CUSTOMER);
        promotionOrder.setSalesPic(bo.getSalesPicId());
        promotionOrder.setJugementStatus(JudgementStatus.WAITINGUPLOAD);
        promotionOrder.setCanEnjoyPromotion(CommonConstants.CHAR_Y);
        return promotionOrder;
    }

}
