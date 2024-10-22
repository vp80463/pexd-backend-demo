package com.a1stream.parts.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.manager.DeliveryOrderManager;
import com.a1stream.common.manager.GenerateNoManager;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.common.manager.PickingInstructionManager;
import com.a1stream.common.manager.ReceiptSlipManager;
import com.a1stream.common.manager.StoringManager;
import com.a1stream.domain.bo.parts.SPM030602BO;
import com.a1stream.domain.bo.parts.SPM030603BO;
import com.a1stream.domain.entity.DeliveryOrderItem;
import com.a1stream.domain.entity.InventoryTransaction;
import com.a1stream.domain.entity.ReceiptSlip;
import com.a1stream.domain.entity.ReceiptSlipItem;
import com.a1stream.domain.form.parts.SPM030602Form;
import com.a1stream.domain.repository.DeliveryOrderItemRepository;
import com.a1stream.domain.repository.DeliveryOrderRepository;
import com.a1stream.domain.repository.InventoryTransactionRepository;
import com.a1stream.domain.repository.MstOrganizationRepository;
import com.a1stream.domain.repository.OrganizationRelationRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.ReceiptSlipItemRepository;
import com.a1stream.domain.repository.ReceiptSlipRepository;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.InventoryTransactionVO;
import com.a1stream.domain.vo.MstOrganizationVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.ReceiptSlipItemVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.ymsl.solid.base.util.BeanMapUtils;

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
@Service
public class SPM0306Service {

    @Resource
    InventoryManager inventoryManager;

    @Resource
    DeliveryOrderManager deliveryOrderManager;

    @Resource
    private DeliveryOrderRepository deliveryOrderRepository;

    @Resource
    private DeliveryOrderItemRepository deliveryOrderItemRepository;

    @Resource
    private ProductInventoryRepository productInventoryRepository;

    @Resource
    private PickingInstructionManager pickingInstructionManager;

    @Resource
    private OrganizationRelationRepository organizationRelationRepository;

    @Resource
    private ProductStockStatusRepository productStockStatusRepository;

    @Resource
    private ReceiptSlipManager receiptSlipManager;

    @Resource
    private StoringManager storingManager;

    @Resource
    private GenerateNoManager generateNoMgr;

    @Resource
    private ReceiptSlipRepository receiptSlipRepository;

    @Resource
    private InventoryTransactionRepository inventoryTransactionRepository;

    @Resource
    private ReceiptSlipItemRepository receiptSlipItemRepository;

    @Resource
    private GenerateNoManager generateNoManager;

    @Resource
    MstOrganizationRepository mstOrganizationRepository;

//    public Long getToOrganizationId(String siteId, String relationType) {
//        return organizationRelationRepository.findBySiteIdAndRelationType(siteId, relationType).stream()
//                                             .findFirst()
//                                             .map(organizationRelation -> BeanMapUtils.mapTo(organizationRelation, OrganizationRelationVO.class))
//                                             .map(OrganizationRelationVO::getToOrganizationId)
//                                             .orElse(null);
//    }

    public MstOrganizationVO getMstOrganizationVO(String siteId, String relationType) {

        return BeanMapUtils.mapTo(mstOrganizationRepository.getCustomer(siteId, relationType), MstOrganizationVO.class);
    }



    public void savePartsPointTransferInstruction(DeliveryOrderVO dov,
                                                  List<DeliveryOrderItemVO> doItemVoList,
                                                  Long fromPointId) {

        //delivery_order_item新增
        deliveryOrderItemRepository.saveInBatch(BeanMapUtils.mapListTo(doItemVoList, DeliveryOrderItem.class));

        //deliveryOrderManager(delivery_order新增)
        deliveryOrderManager.doRegister(dov, fromPointId);

        //InventoryManager(product_stock_status更新)
        inventoryManager.doShippingRequest(doItemVoList);

        //InventoryManager(picking_list、picking_item和product_stock_status更新)
        List<DeliveryOrderVO> dovList = new ArrayList<>();
        dovList.add(dov);
        pickingInstructionManager.createPickingListInfos(dovList,
                                                         CommonConstants.INTEGER_ZERO,
                                                         CommonConstants.INTEGER_ZERO,
                                                         null,
                                                         dov.getSiteId());

    }

    public List<ProductStockStatusVO> getProductStockStatusVOList(String siteId, Long facilityId, String productStockStatusType, List<Long> productIds){
        return BeanMapUtils.mapListTo(productStockStatusRepository.getPartStockStatusQtyleZero(siteId, facilityId, productStockStatusType, productIds), ProductStockStatusVO.class);
    }

    public List<SPM030602BO> getPartsPointTransferReceiptList(SPM030602Form form) {
        return deliveryOrderRepository.getPartsPointTransferReceiptList(form);
    }

    public List<SPM030603BO> getTransferPartsDetailList(Long deliveryOrderId) {
        return deliveryOrderItemRepository.getTransferPartsDetailList(deliveryOrderId);
    }

    public List<DeliveryOrderItemVO> getDeliveryOrderItemVOList(Set<Long> deliveryOrderIds) {
        return BeanMapUtils.mapListTo(deliveryOrderItemRepository.findByDeliveryOrderIdIn(deliveryOrderIds), DeliveryOrderItemVO.class);
    }

    public String generateSlipNo(String siteId, Long pointId) {
        return generateNoManager.generateSlipNo(siteId, pointId);
    }

    public List<DeliveryOrderVO> getDeliveryOrderVOList(List<Long> deliveryOrderIds) {
        return BeanMapUtils.mapListTo(deliveryOrderRepository.findByDeliveryOrderIdIn(deliveryOrderIds), DeliveryOrderVO.class);
    }

    public void confirmPartsPointTransferReceipt(List<ReceiptSlipVO> rsList,
                                                 List<ReceiptSlipItemVO> rsiList,
                                                 Map<Long, List<ReceiptSlipItemVO>> rsiMap,
                                                 List<Long> deliveryOrderIds,
                                                 String siteId,
                                                 Long toFacilityId,
                                                 Long fromFacilityId,
                                                 Long personId,
                                                 String personNm) {

        List<InventoryTransactionVO> itVOList = new ArrayList<>();

        for (ReceiptSlipVO rs : rsList) {

            //根据receiptSlipId获取receipt_slip_item的集合
            List<ReceiptSlipItemVO> receiptSlipItemVOList = rsiMap.get(rs.getReceiptSlipId());

            //InventoryManager(doRegisterInventoryTransaction生成inventory_transaction)
            for (ReceiptSlipItemVO rsi : receiptSlipItemVOList) {
                InventoryTransactionVO it = inventoryManager.doRegisterInventoryTransaction(siteId,
                                                                                            toFacilityId,
                                                                                            fromFacilityId,
                                                                                            toFacilityId,
                                                                                            rsi.getProductId(),
                                                                                            InventoryTransactionType.TRANSFERIN.getCodeDbid(),
                                                                                            rsi.getReceiptQty(),
                                                                                            rsi.getReceiptPrice(),
                                                                                            rsi.getReceiptSlipId(),
                                                                                            rs.getSlipNo(),
                                                                                            rs.getFromOrganizationId(),
                                                                                            rs.getReceivedOrganizationId(),
                                                                                            null,
                                                                                            null);
                itVOList.add(it);
            }
        }

        //receipt_slip新增
        receiptSlipRepository.saveInBatch(BeanMapUtils.mapListTo(rsList, ReceiptSlip.class));

        //receipt_slip_item新增
        receiptSlipItemRepository.saveInBatch(BeanMapUtils.mapListTo(rsiList, ReceiptSlipItem.class));

        //inventory_transaction新增
        inventoryTransactionRepository.saveInBatch(BeanMapUtils.mapListTo(itVOList, InventoryTransaction.class));

        //InventoryManager(doInventoryReceipt更新product_stock_status和Inventory_transaction)
        inventoryManager.doInventoryReceipt(rsiList,
                                            toFacilityId,
                                            InventoryTransactionType.TRANSFERIN.getCodeDbid(),
                                            personId,
                                            personNm);

        //StoringManager(doStoringInstruction更新storing_list、storing_line和storing_line_item)
        storingManager.doStoringInstruction(toFacilityId,
                                            InventoryTransactionType.TRANSFERIN.getCodeDbid(),
                                            rsList);

        //DeliveryOrderManager(doReceiptSlipRegister更新delivery_order)
        deliveryOrderManager.doReceiptSlipRegister(deliveryOrderIds);

    }

}
