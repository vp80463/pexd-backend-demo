package com.a1stream.parts.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.MstCodeConstants.SalesOrderStatus;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.manager.DeliveryOrderManager;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.common.manager.InvoiceManager;
import com.a1stream.common.manager.PickingInstructionManager;
import com.a1stream.common.manager.SalesOrderManager;
import com.a1stream.domain.bo.parts.SPM020104BO;
import com.a1stream.domain.entity.DeliveryOrder;
import com.a1stream.domain.entity.DeliveryOrderItem;
import com.a1stream.domain.entity.PickingItem;
import com.a1stream.domain.entity.PickingList;
import com.a1stream.domain.entity.SalesOrder;
import com.a1stream.domain.entity.SalesOrderItem;
import com.a1stream.domain.form.parts.SPM020104Form;
import com.a1stream.domain.repository.DeliveryOrderItemRepository;
import com.a1stream.domain.repository.DeliveryOrderRepository;
import com.a1stream.domain.repository.PickingItemRepository;
import com.a1stream.domain.repository.PickingListRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.PickingItemVO;
import com.a1stream.domain.vo.PickingListVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;
/**
* 功能描述:
*
* MID2303
* 2024年07月04日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/07/04   Ruan Hansheng     New
*/
@Service
public class SPM020104Service {

    @Resource
    private PickingItemRepository pickingItemRepository;

    @Resource
    private SalesOrderRepository salesOrderRepository;

    @Resource
    private InventoryManager inventoryManager;

    @Resource
    private ProductInventoryRepository productInventoryRepository;

    @Resource
    private DeliveryOrderItemRepository deliveryOrderItemRepository;

    @Resource
    private PickingListRepository pickingListRepository;

    @Resource
    private DeliveryOrderRepository deliveryOrderRepository;

    @Resource
    private SalesOrderItemRepository salesOrderItemRepository;

    @Resource
    private PickingInstructionManager pickingInstructionManager;

    @Resource
    private DeliveryOrderManager deliveryOrderManager;

    @Resource
    private SalesOrderManager salesOrderManager;

    @Resource
    private InvoiceManager invoiceManager;

    public List<SPM020104BO> getPickingItemList(SPM020104Form form, PJUserDetails uc) {

        return pickingItemRepository.getPickingItemList(form, uc);
    }

    public SalesOrderVO getSalesOrderVO(Long salesOrderId) {
        
        return BeanMapUtils.mapTo(salesOrderRepository.findById(salesOrderId), SalesOrderVO.class);
    }

    public void shipment(SPM020104Form form, PJUserDetails uc) {

        List<SPM020104BO> allTableDataList = form.getAllTableDataList();
        List<Long> pickingItemIdList = allTableDataList.stream().map(SPM020104BO::getPickingItemId).toList();
        for (SPM020104BO bo : allTableDataList) {
            ProductInventoryVO productInventoryVO = BeanMapUtils.mapTo(productInventoryRepository.findByFacilityIdAndProductIdAndSiteIdAndLocationId(form.getPointId()
                                                                                                                                                   , bo.getAllocatePartsId()
                                                                                                                                                   , uc.getDealerCode()
                                                                                                                                                   , bo.getLocationId()), ProductInventoryVO.class);
            inventoryManager.doInventoryPickingLineCancel(uc.getDealerCode()
                                                        , form.getPointId()
                                                        , bo.getAllocatePartsId()
                                                        , bo.getPickingQty()
                                                        , bo.getLocationId()
                                                        , productInventoryVO);

            Map<DeliveryOrderItemVO, BigDecimal> deliveryOrderItemVOMap = this.doPickingLineCancel(pickingItemIdList);
            List<SalesOrderItemVO> salesOrderItemVOList = this.doDoPickingLineCancel(deliveryOrderItemVOMap);
            this.doSoPickingLineCancel(salesOrderItemVOList, deliveryOrderItemVOMap);
            this.updateDeliveryOrder(form, uc);
        }
    }

    public Map<DeliveryOrderItemVO, BigDecimal> doPickingLineCancel(List<Long> pickingItemIdList) {

        PickingItemVO pickingItemVO = null;
        DeliveryOrderItemVO deliveryOrderItemVO = null;
        List<PickingItemVO> pickingItemVOList = new ArrayList<>();
        Map<DeliveryOrderItemVO, BigDecimal> deliveryOrderItemVOMap = new HashMap<>();
        List<PickingItemVO> dbPickingItemVOList = BeanMapUtils.mapListTo(pickingItemRepository.findByPickingItemIdIn(pickingItemIdList), PickingItemVO.class);
        Map<Long, PickingItemVO> dbPickingItemVOMap = dbPickingItemVOList.stream().collect(Collectors.toMap(PickingItemVO::getPickingItemId, Function.identity()));

        Set<Long> deliveryOrderItemIdSet = dbPickingItemVOList.stream().map(PickingItemVO::getDeliveryOrderItemId).collect(Collectors.toSet());
        List<DeliveryOrderItemVO> dbDeliveryOrderItemVOList = BeanMapUtils.mapListTo(deliveryOrderItemRepository.findByDeliveryOrderIdIn(deliveryOrderItemIdSet), DeliveryOrderItemVO.class);
        Map<Long, DeliveryOrderItemVO> dbDeliveryOrderItemVOMap = dbDeliveryOrderItemVOList.stream().collect(Collectors.toMap(DeliveryOrderItemVO::getDeliveryOrderItemId, Function.identity()));
        for (Long pickingItemId : pickingItemIdList) {
            pickingItemVO = dbPickingItemVOMap.get(pickingItemId);
            if (null != pickingItemVO) {
                pickingItemVOList.add(pickingItemVO);
                deliveryOrderItemVO = dbDeliveryOrderItemVOMap.get(pickingItemVO.getDeliveryOrderItemId());
                deliveryOrderItemVOMap.put(deliveryOrderItemVO, pickingItemVO.getQty());
            }
        }


        if (pickingItemIdList.size() == pickingItemVOList.size()) {
            PickingListVO pickingListVO = BeanMapUtils.mapTo(pickingItemVOList.get(0).getPickingListId(), PickingListVO.class);
            pickingListRepository.delete(BeanMapUtils.mapTo(pickingListVO, PickingList.class));
            pickingItemRepository.deleteAllInBatch(BeanMapUtils.mapListTo(dbPickingItemVOList, PickingItem.class));
        } else {
            pickingItemRepository.deleteAllInBatch(BeanMapUtils.mapListTo(dbPickingItemVOList, PickingItem.class));
        }

        return deliveryOrderItemVOMap;
    }

    public List<SalesOrderItemVO> doDoPickingLineCancel(Map<DeliveryOrderItemVO, BigDecimal> deliveryOrderItemVOMap) {

        BigDecimal subTotalAmt = BigDecimal.ZERO;
        BigDecimal subTotalQty = BigDecimal.ZERO;
        List<DeliveryOrderItemVO> deliveryOrderItemVOList = deliveryOrderItemVOMap.keySet().stream().collect(Collectors.toList());
        List<SalesOrderItemVO> salesOrderItemVOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(deliveryOrderItemVOList)) {
            return salesOrderItemVOList;
        }

        Set<Long> salesOrderItemIdSet = deliveryOrderItemVOList.stream().map(DeliveryOrderItemVO::getOrderItemId).collect(Collectors.toSet());
        List<SalesOrderItemVO> dbSalesOrderItemVOList = BeanMapUtils.mapListTo(salesOrderItemRepository.findBySalesOrderItemIdIn(salesOrderItemIdSet), SalesOrderItemVO.class);
        Map<Long, SalesOrderItemVO> dbSalesOrderItemVOMap = dbSalesOrderItemVOList.stream().collect(Collectors.toMap(SalesOrderItemVO::getSalesOrderItemId, Function.identity()));
        
        DeliveryOrderVO deliveryOrderVO = BeanMapUtils.mapTo(deliveryOrderRepository.findByDeliveryOrderId(deliveryOrderItemVOList.get(0).getDeliveryOrderId()), DeliveryOrderVO.class);
        List<DeliveryOrderItemVO> deleteList = new ArrayList<>();
        for (Map.Entry<DeliveryOrderItemVO, BigDecimal> entry : deliveryOrderItemVOMap.entrySet()) {
            DeliveryOrderItemVO deliveryOrderItemVO = entry.getKey();
            BigDecimal qty = entry.getValue();
            deliveryOrderItemVO.setDeliveryQty(deliveryOrderItemVO.getDeliveryQty().subtract(qty));
            if (BigDecimal.ZERO.compareTo(deliveryOrderItemVO.getDeliveryQty()) == 0) {
                deleteList.add(deliveryOrderItemVO);
                deliveryOrderItemVOMap.remove(deliveryOrderItemVO);
            }
            subTotalAmt = subTotalAmt.add(deliveryOrderItemVO.getSellingPrice().multiply(qty));
            subTotalQty = subTotalQty.add(qty);
            SalesOrderItemVO salesOrderItemVO = dbSalesOrderItemVOMap.get(deliveryOrderItemVO.getOrderItemId());
            salesOrderItemVOList.add(salesOrderItemVO);
        }

        if (deliveryOrderVO.getTotalAmt().compareTo(subTotalAmt) > 0) {
            deliveryOrderVO.setTotalAmt(deliveryOrderVO.getTotalAmt().subtract(subTotalAmt));
        }

        if (deliveryOrderVO.getTotalQty().compareTo(subTotalQty) > 0) {
            deliveryOrderVO.setTotalQty(deliveryOrderVO.getTotalQty().subtract(subTotalQty));
        }

        deliveryOrderItemRepository.deleteAllInBatch(BeanMapUtils.mapListTo(deleteList, DeliveryOrderItem.class));
        deliveryOrderRepository.save(BeanMapUtils.mapTo(deliveryOrderVO, DeliveryOrder.class));

        return salesOrderItemVOList;
    }

    public void doSoPickingLineCancel(List<SalesOrderItemVO> salesOrderItemVOList, Map<DeliveryOrderItemVO, BigDecimal> deliveryOrderItemVOMap) {

        if (CollectionUtils.isEmpty(salesOrderItemVOList)) {
            return;
        }

        SalesOrderVO salesOrderVO = BeanMapUtils.mapTo(salesOrderRepository.findBySalesOrderId(salesOrderItemVOList.get(0).getSalesOrderId()), SalesOrderVO.class);
        List<DeliveryOrderItemVO> deliveryOrderItemVOList = deliveryOrderItemVOMap.keySet().stream().collect(Collectors.toList());
        Map<Long, DeliveryOrderItemVO> deliveryOrderItemIdVOMap = deliveryOrderItemVOList.stream().collect(Collectors.toMap(DeliveryOrderItemVO::getDeliveryOrderItemId, Function.identity()));
        Map<Long, Long> salesOrderItemIdDeliveryOrderItemIdMap = deliveryOrderItemVOList.stream().collect(Collectors.toMap(DeliveryOrderItemVO::getOrderItemId, DeliveryOrderItemVO::getDeliveryOrderItemId));
        List<Long> deliveryOrderItemIdList = deliveryOrderItemVOList.stream().map(DeliveryOrderItemVO::getDeliveryOrderItemId).collect(Collectors.toList());
        
        List<PickingItemVO> pickingItemVOList = BeanMapUtils.mapListTo(pickingItemRepository.findByDeliveryOrderItemIdIn(deliveryOrderItemIdList), PickingItemVO.class);
        Map<Long, PickingItemVO> pickingItemVOMap = pickingItemVOList.stream().collect(Collectors.toMap(PickingItemVO::getDeliveryOrderItemId, Function.identity()));

        for (SalesOrderItemVO salesOrderItemVO : salesOrderItemVOList) {
            Long deliveryOrderItemId = salesOrderItemIdDeliveryOrderItemIdMap.get(salesOrderItemVO.getSalesOrderItemId());
            PickingItemVO pickingItemVO = pickingItemVOMap.get(deliveryOrderItemId);
            BigDecimal oldPickingQty = pickingItemVO.getQty();

            DeliveryOrderItemVO deliveryOrderItemVO = deliveryOrderItemIdVOMap.get(deliveryOrderItemId);
            BigDecimal pickedQty = deliveryOrderItemVO.getDeliveryQty();
            
            BigDecimal changeQty = oldPickingQty.subtract(pickedQty);
            salesOrderItemVO.setAllocatedQty(salesOrderItemVO.getAllocatedQty().add(changeQty));
            if (salesOrderItemVO.getAllocatedQty().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00314", new String[] {
                                                 CodedMessageUtils.getMessage("label.allocatedQuantity"),
                                                 salesOrderItemVO.getAllocatedQty().toString(),
                                                 salesOrderVO.getOrderNo() }));
            }

            salesOrderItemVO.setInstructionQty(salesOrderItemVO.getInstructionQty().subtract(changeQty));
            if (salesOrderItemVO.getInstructionQty().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00314", new String[] {
                                                 CodedMessageUtils.getMessage("label.allocatedQuantity"),
                                                 salesOrderItemVO.getInstructionQty().toString(),
                                                 salesOrderVO.getOrderNo() }));
            }
        }

        salesOrderVO.setOrderStatus(SalesOrderStatus.SP_SHIPMENTED);
        
        salesOrderItemRepository.saveInBatch(BeanMapUtils.mapListTo(salesOrderItemVOList, SalesOrderItem.class));
        salesOrderRepository.save(BeanMapUtils.mapTo(salesOrderVO, SalesOrder.class));
    }

    public void updateDeliveryOrder(SPM020104Form form, PJUserDetails uc) {

        List<SPM020104BO> allTableDataList = form.getAllTableDataList();

        if (CollectionUtils.isEmpty(allTableDataList)) {
            return;
        }

        Long invoiceId = null;
        List<Long> deliveryOrderIdList = new ArrayList<>();
        deliveryOrderIdList.add(form.getDeliveryOrderId());
        
        pickingInstructionManager.doPickingCompletion(deliveryOrderIdList);
        List<DeliveryOrderItemVO> deliveryOrderItemVOList = BeanMapUtils.mapListTo(deliveryOrderItemRepository.findByDeliveryOrderId(form.getDeliveryOrderId()), DeliveryOrderItemVO.class);
        List<DeliveryOrderVO> deliveryOrderVOList = BeanMapUtils.mapListTo(deliveryOrderManager.doShippingCompletion(deliveryOrderIdList), DeliveryOrderVO.class);

        if (CollectionUtils.isEmpty(deliveryOrderItemVOList)) {
            return;
        }

        salesOrderManager.doShippingCompletion(deliveryOrderItemVOList);
        inventoryManager.doShippingCompletion(deliveryOrderItemVOList, InventoryTransactionType.RETURNIN.getCodeDbid(), uc.getPersonId(), uc.getPersonName());
        deliveryOrderManager.doInvoicing(deliveryOrderIdList);
        invoiceManager.doCreateInvoice(deliveryOrderVOList);
    }
    
}
