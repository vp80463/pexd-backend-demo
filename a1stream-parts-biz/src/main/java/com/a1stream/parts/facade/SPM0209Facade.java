package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.domain.bo.parts.SPM020901BO;
import com.a1stream.domain.form.parts.SPM020901Form;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.PickingItemVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.parts.service.SPM0209Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
* 功能描述: Picking Discrepancy Entry
*
* mid2330
* 2024年7月4日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/07/04   Liu Chaoran   New
*/
@Component
public class SPM0209Facade {

    @Resource
    SPM0209Service spm0209Service;

    public List<SPM020901BO> findPickingDiscEntryList(SPM020901Form form) {
        this.retrieveCheck(form);
        return spm0209Service.findPickingDiscEntryList(form);
    }

    private void retrieveCheck(SPM020901Form form) {

        //校验duNo
        DeliveryOrderVO deliveryOrderVO= spm0209Service.findDuNo(form.getSiteId(), form.getPointId(), form.getDuNo());
        if(deliveryOrderVO == null) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[]{CodedMessageUtils.getMessage("label.duNo"), form.getDuNo(), CodedMessageUtils.getMessage("label.tableDeliveryOrder")}));
        }else if(!MstCodeConstants.DeliveryStatus.ON_PICKING.equals(deliveryOrderVO.getDeliveryStatus())
                 && !MstCodeConstants.DeliveryStatus.CREATED.equals(deliveryOrderVO.getDeliveryStatus())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10148", new String[]{CodedMessageUtils.getMessage("label.duNo"), form.getDuNo()}));
        }

        //校验pickingSeqNo
        if (StringUtils.isNotEmpty(form.getPickingSeqNo())) {
            DeliveryOrderVO deliveryOrder= spm0209Service.findDeliveryOrder(form.getSiteId(), form.getPointId(), form.getPickingSeqNo());
            if(deliveryOrder == null) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[]{CodedMessageUtils.getMessage("label.pickingSeqNo"), form.getPickingSeqNo(), CodedMessageUtils.getMessage("label.tableDeliveryOrder")}));
            }
        }else {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10317", new String[] {CodedMessageUtils.getMessage("label.pickingSeqNo")}));
        }
    }

    public SPM020901Form comfirmPickingDiscEntryData(SPM020901Form form) {
        this.confirmCheck(form);

        //查询结果必定唯一
        List<SPM020901BO> detail = form.getAllTableDataList();

        Integer piUpdateCount = spm0209Service.findPickingDiscEntryList(form).get(0).getPiUpdateCount();

        if(StringUtils.isNotEmpty(form.getPickingSeqNo().trim())) {
            PickingItemVO pickingItemVO = spm0209Service.getPickingItem(form.getPickingSeqNo().trim(),form.getPointId(),form.getSiteId());

            if(pickingItemVO != null) {

                DeliveryOrderItemVO deliveryOrderItemVO = spm0209Service.getDeliveryOrderItem(pickingItemVO.getDeliveryOrderItemId());
                doDeliveryDiscrepReport(detail, deliveryOrderItemVO);

                Long deliveryOrderId = pickingItemVO.getDeliveryOrderId();
                DeliveryOrderVO deliveryOrderVO = spm0209Service.getDeliveryOrder(deliveryOrderId);
                Long partsId = detail.get(0).getPartsId();
                if(PJConstants.InventoryTransactionType.TRANSFEROUT.getCodeDbid().equals(deliveryOrderVO.getInventoryTransactionType())) {
                    pickingItemVO.setQty(detail.get(0).getActualQty());

                    spm0209Service.doUpdateProductStockStatusMinusQty(form
                                                                    , detail
                                                                    , pickingItemVO
                                                                    , partsId
                                                                    , piUpdateCount
                                                                    , deliveryOrderItemVO
                                                                    , deliveryOrderVO);
                }else {
                    this.doSalesOrderDiscrepancyReport(form, detail, pickingItemVO, piUpdateCount, deliveryOrderItemVO, deliveryOrderVO );
                }
            }
        }
        return form;
    }

    private void doDeliveryDiscrepReport(List<SPM020901BO> detail, DeliveryOrderItemVO deliveryOrderItemVO) {
        BigDecimal deliveryQty;
        if(deliveryOrderItemVO != null) {

            deliveryQty = deliveryOrderItemVO.getDeliveryQty();

            if(deliveryQty.compareTo(BigDecimal.ZERO) != 0) {
                deliveryOrderItemVO.setDeliveryQty(deliveryOrderItemVO.getDeliveryQty().subtract(detail.get(0).getInstructionQty().subtract(detail.get(0).getActualQty())));
            }else {
                DeliveryOrderVO deliveryOrderVO = spm0209Service.getDeliveryOrder(deliveryOrderItemVO.getDeliveryOrderId());
                if(deliveryOrderVO != null) {
                    deliveryOrderVO.setActivityFlag(CommonConstants.CHAR_Y);
                    if(PJConstants.InventoryTransactionType.TRANSFERIN.getCodeDbid().equals(deliveryOrderVO.getInventoryTransactionType())) {
                        deliveryOrderVO.setInventoryTransactionType(MstCodeConstants.DeliveryStatus.DISPATCHED);
                    }else if(PJConstants.InventoryTransactionType.SALESTOCKOUT.getCodeDbid().equals(deliveryOrderVO.getInventoryTransactionType())) {
                        deliveryOrderVO.setInventoryTransactionType(MstCodeConstants.DeliveryStatus.INVOICED);
                    }
                }
                deliveryOrderItemVO.setDeliveryQty(deliveryOrderItemVO.getDeliveryQty().subtract(detail.get(0).getInstructionQty().subtract(detail.get(0).getActualQty())));
            }
        }
    }

    private void confirmCheck(SPM020901Form form) {

        //校验point
        List<SPM020901BO> allTableDataList = form.getAllTableDataList();
        for (SPM020901BO bo : allTableDataList) {
            // 验证有效性
            if (bo.getActualQty().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00200", new String[]{CodedMessageUtils.getMessage("label.actualQuantity"), CommonConstants.CHAR_ZERO}));
            }

            if (bo.getActualQty().compareTo(bo.getInstructionQty()) > 0) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00202", new String[]{CodedMessageUtils.getMessage("label.actualQuantity"), CodedMessageUtils.getMessage("label.instructQuantity")}));
            }
        }
    }

    private void doSalesOrderDiscrepancyReport(SPM020901Form form
                                             , List<SPM020901BO> detail
                                             , PickingItemVO pickingItemVO
                                             , Integer piUpdateCount
                                             , DeliveryOrderItemVO deliveryOrderItemVO
                                             , DeliveryOrderVO deliveryOrderVO) {

        pickingItemVO.setQty(detail.get(0).getActualQty());

        //根据delivery_order_id取salesOrderItem
        List<SalesOrderItemVO> salesOrderItemAllList = spm0209Service.getSalesOrderItem(pickingItemVO.getDeliveryOrderId());

        List<SalesOrderItemVO> salesOrderItemList = spm0209Service.getSalesOrderItem(pickingItemVO.getDeliveryOrderId()
                , pickingItemVO.getSiteId()
                , Integer.parseInt(pickingItemVO.getSeqNo()));

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal discrepancyQty = detail.get(0).getInstructionQty().subtract(detail.get(0).getActualQty());
        for(SalesOrderItemVO salesOrderItem:salesOrderItemAllList) {
            SalesOrderItemVO boPlusItem = (!salesOrderItemList.isEmpty()) ? salesOrderItem : salesOrderItemList.get(0);
            Long pickingItemId = pickingItemVO.getDeliveryOrderId();
            Long boPlusItemId = boPlusItem.getSalesOrderId();

            if(salesOrderItem.getSalesOrderItemId().equals(pickingItemId)) {
                if(salesOrderItem.getInstructionQty().compareTo(discrepancyQty) < 0) {
                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00314", new String[]{CodedMessageUtils.getMessage("label.instructQuantity"), CodedMessageUtils.getMessage("label.tableSalesOrderItem")}));
                }
            }else if(salesOrderItem.getSalesOrderItemId().equals(boPlusItemId) && salesOrderItem.getBoQty().add(discrepancyQty).compareTo(BigDecimal.ZERO) < 0) {
                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00314", new String[]{CodedMessageUtils.getMessage("label.boQuantity"), CodedMessageUtils.getMessage("label.tableSalesOrderItem")}));
            }

            totalAmount = totalAmount.add(salesOrderItem.getActualAmt());
            totalQty = totalQty.add(salesOrderItem.getActualAmt());
        }

        SalesOrderVO salesOrderVO = spm0209Service.getSalesOrder(pickingItemVO.getDeliveryOrderId());
        salesOrderVO.setTotalAmt(totalAmount);
        salesOrderVO.setTotalQty(totalQty);
        salesOrderVO.setTotalLine(salesOrderItemAllList.size());
        spm0209Service.updateSalesOrderAndDoRegisterInvenrtoryTransaction(salesOrderVO, form, detail, pickingItemVO, piUpdateCount, deliveryOrderItemVO, deliveryOrderVO);
    }
}
