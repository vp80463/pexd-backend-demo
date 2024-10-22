/**
 *
 */
package com.a1stream.parts.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.CostType;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.constants.PJConstants.StockAdjustReasonType;
import com.a1stream.common.manager.CostManager;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.domain.bo.parts.SPM020901BO;
import com.a1stream.domain.entity.DeliveryOrder;
import com.a1stream.domain.entity.DeliveryOrderItem;
import com.a1stream.domain.entity.PickingItem;
import com.a1stream.domain.entity.SalesOrder;
import com.a1stream.domain.form.parts.SPM020901Form;
import com.a1stream.domain.repository.DeliveryOrderItemRepository;
import com.a1stream.domain.repository.DeliveryOrderRepository;
import com.a1stream.domain.repository.PickingItemRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.InventoryTransactionVO;
import com.a1stream.domain.vo.PickingItemVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.ymsl.solid.base.util.BeanMapUtils;

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
@Service
public class SPM0209Service {

    @Resource
    private DeliveryOrderRepository deliveryOrderRepository;

    @Resource
    private PickingItemRepository pickingItemRepository;

    @Resource
    private DeliveryOrderItemRepository deliveryOrderItemRepository;

    @Resource
    private SalesOrderItemRepository salesOrderItemRepository;

    @Resource
    private SalesOrderRepository salesOrderRepository;

    @Resource
    private InventoryManager inventoryManager;

    @Resource
    private CostManager costManager;

    public List<SPM020901BO> findPickingDiscEntryList(SPM020901Form form) {
        return deliveryOrderRepository.findPickingDiscEntryList(form);
    }

    public DeliveryOrderVO findDuNo(String siteId, Long facilityId, String duNo) {

        return BeanMapUtils.mapTo(deliveryOrderRepository.findBySiteIdAndFromFacilityIdAndDeliveryOrderNo(siteId, facilityId, duNo), DeliveryOrderVO.class);
    }

    public DeliveryOrderVO findDeliveryOrder(String siteId, Long facilityId, String seqNo) {

        return deliveryOrderRepository.findDeliveryOrder(siteId, facilityId, seqNo);
    }

    public PickingItemVO getPickingItem(String seqNo, Long facilityId, String siteId) {

        return BeanMapUtils.mapTo(pickingItemRepository.findBySeqNoAndFacilityIdAndSiteId(seqNo,facilityId,siteId), PickingItemVO.class);
    }

    public void updatePickingItem(PickingItemVO pickingItemVO) {

        pickingItemRepository.save(BeanMapUtils.mapTo(pickingItemVO, PickingItem.class));
    }

    public DeliveryOrderItemVO getDeliveryOrderItem(Long deliveryOrderItemId) {

        return BeanMapUtils.mapTo(deliveryOrderItemRepository.findByDeliveryOrderItemId(deliveryOrderItemId), DeliveryOrderItemVO.class);
    }

    public void updateDeliveryOrderItem(DeliveryOrderItemVO deliveryOrderItemVO) {

        deliveryOrderItemRepository.save(BeanMapUtils.mapTo(deliveryOrderItemVO, DeliveryOrderItem.class));
    }

    public DeliveryOrderVO getDeliveryOrder(Long deliveryOrderId) {

        return BeanMapUtils.mapTo(deliveryOrderRepository.findByDeliveryOrderId(deliveryOrderId), DeliveryOrderVO.class);
    }

    public void updateDeliveryOrder(DeliveryOrderItemVO deliveryOrderItemVO, DeliveryOrderVO deliveryOrderVO) {

        deliveryOrderItemRepository.save(BeanMapUtils.mapTo(deliveryOrderItemVO, DeliveryOrderItem.class));
        deliveryOrderRepository.save(BeanMapUtils.mapTo(deliveryOrderVO, DeliveryOrder.class));
    }

    public List<SalesOrderItemVO> getSalesOrderItem(Long deliveryOrderId) {

        return BeanMapUtils.mapListTo(salesOrderItemRepository.findBySalesOrderId(deliveryOrderId), SalesOrderItemVO.class);
    }

    public List<SalesOrderItemVO> getSalesOrderItem(Long deliveryOrderId, String siteId, Integer seqNo) {

        return BeanMapUtils.mapListTo(salesOrderItemRepository.getLastSupersedingOrderItem(siteId, seqNo, deliveryOrderId), SalesOrderItemVO.class);
    }

    public SalesOrderVO getSalesOrder(Long salesOrderId) {

        return BeanMapUtils.mapTo(salesOrderRepository.findBySalesOrderId(salesOrderId), SalesOrderVO.class);
    }

    public void updateSalesOrderAndDoRegisterInvenrtoryTransaction(SalesOrderVO salesOrderVO
                                                                 , SPM020901Form form, List<SPM020901BO> detail
                                                                 , PickingItemVO pickingItemVO
                                                                 , Integer piUpdateCount
                                                                 , DeliveryOrderItemVO deliveryOrderItemVO
                                                                 , DeliveryOrderVO deliveryOrderVO) {

        //更新PickingItem表
        if(form.getPiUpdateCount().equals(piUpdateCount)) {
            this.updatePickingItem(pickingItemVO);

            if(deliveryOrderItemVO != null && deliveryOrderItemVO.getDeliveryQty().compareTo(BigDecimal.ZERO) != 0) {
                this.updateDeliveryOrderItem(deliveryOrderItemVO);
            }else {
                this.updateDeliveryOrder(deliveryOrderItemVO, deliveryOrderVO);
            }

            salesOrderRepository.save(BeanMapUtils.mapTo(salesOrderVO, SalesOrder.class));
            //doRegisterInventoryTransaction
            this.doRegisterInventoryTransaction(form,detail,pickingItemVO.getDeliveryOrderId(),detail.get(0).getPartsId());
        }
    }

    public InventoryTransactionVO doUpdateProductStockStatusMinusQty(SPM020901Form form
                                                                   , List<SPM020901BO> detail
                                                                   , PickingItemVO pickingItemVO
                                                                   , Long partsId
                                                                   , Integer piUpdateCount
                                                                   , DeliveryOrderItemVO deliveryOrderItemVO
                                                                   , DeliveryOrderVO deliveryOrderVO) {
        //更新PickingItem表
        if(form.getPiUpdateCount().equals(piUpdateCount)) {
            this.updatePickingItem(pickingItemVO);

            if(deliveryOrderItemVO != null && deliveryOrderItemVO.getDeliveryQty().compareTo(BigDecimal.ZERO) != 0) {
                this.updateDeliveryOrderItem(deliveryOrderItemVO);
            }else {
                this.updateDeliveryOrder(deliveryOrderItemVO, deliveryOrderVO);
            }

            //doUpdateProductStockStatusMinusQty
            inventoryManager.doUpdateProductStockStatusMinusQty(form.getSiteId()
                                                              , form.getPointId()
                                                              , detail.get(0).getPartsId()
                                                              , PJConstants.SpStockStatus.ONPICKING_QTY.getCodeDbid()
                                                              , detail.get(0).getInstructionQty().subtract(detail.get(0).getActualQty()));
            //doRegisterInventoryTransaction
            Set<Long> partsIdSet = new HashSet<>();
            partsIdSet.add(partsId);
            BigDecimal partsIdBigDecimal = costManager.getProductCostInBulk(form.getSiteId(), CostType.AVERAGE_COST, partsIdSet).get(partsId);

            return inventoryManager.doRegisterInventoryTransaction(form.getSiteId()
                    , form.getPointId()
                    , form.getPointId()
                    , form.getPointId()
                    , detail.get(0).getPartsId()
                    , InventoryTransactionType.ADJUSTOUT.getCodeDbid()
                    , detail.get(0).getInstructionQty().subtract(detail.get(0).getActualQty())
                    , partsIdBigDecimal
                    , pickingItemVO.getDeliveryOrderId()
                    , form.getDuNo()
                    , null
                    , null
                    , detail.get(0).getLocationId()
                    , StockAdjustReasonType.FIXED.getCodeDbid());
        }else {
            return null;
        }
    }

    public InventoryTransactionVO doRegisterInventoryTransaction(SPM020901Form form,
            List<SPM020901BO> detail, Long deliveryOrderId, Long partsId) {

        Set<Long> partsIdSet = new HashSet<>();
        partsIdSet.add(partsId);
        BigDecimal partsIdBigDecimal = costManager.getProductCostInBulk(form.getSiteId(), CostType.AVERAGE_COST, partsIdSet).get(partsId);

        return inventoryManager.doRegisterInventoryTransaction(form.getSiteId()
                , form.getPointId()
                , form.getPointId()
                , form.getPointId()
                , detail.get(0).getPartsId()
                , InventoryTransactionType.ADJUSTOUT.getCodeDbid()
                , detail.get(0).getInstructionQty().subtract(detail.get(0).getActualQty())
                , partsIdBigDecimal
                , deliveryOrderId
                , form.getDuNo()
                , null
                , null
                , detail.get(0).getLocationId()
                , StockAdjustReasonType.FIXED.getCodeDbid());
    }
}
