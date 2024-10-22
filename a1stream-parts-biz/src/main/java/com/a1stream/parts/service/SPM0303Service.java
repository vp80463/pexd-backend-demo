package com.a1stream.parts.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.common.manager.MessageSendManager;
import com.a1stream.common.manager.PartsSalesStockAllocationManager;
import com.a1stream.common.manager.PurchaseOrderManager;
import com.a1stream.common.manager.RoleManager;
import com.a1stream.common.manager.StoringManager;
import com.a1stream.domain.bo.parts.SPM030301BO;
import com.a1stream.domain.bo.parts.TargetSalesOrderItemBO;
import com.a1stream.domain.entity.ReceiptPoItemRelation;
import com.a1stream.domain.entity.ReceiptSlip;
import com.a1stream.domain.entity.ReceiptSlipItem;
import com.a1stream.domain.entity.StoringLineItem;
import com.a1stream.domain.form.parts.SPM030301Form;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.PoSoItemRelationRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.PurchaseOrderItemRepository;
import com.a1stream.domain.repository.PurchaseOrderRepository;
import com.a1stream.domain.repository.ReceiptPoItemRelationRepository;
import com.a1stream.domain.repository.ReceiptSlipItemRepository;
import com.a1stream.domain.repository.ReceiptSlipRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.repository.StoringLineItemRepository;
import com.a1stream.domain.repository.StoringLineRepository;
import com.a1stream.domain.repository.StoringListRepository;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.PurchaseOrderItemVO;
import com.a1stream.domain.vo.PurchaseOrderVO;
import com.a1stream.domain.vo.ReceiptSlipItemVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.StoringLineItemVO;
import com.a1stream.domain.vo.StoringLineVO;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
 *
* 功能描述: Parts Stock Register
*
* mid2287
* 2024年6月11日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/11   Wang Nan      New
 */
@Service
public class SPM0303Service {

    @Resource
    private StoringLineRepository storingLineRepository;

    @Resource
    private StoringLineItemRepository storingLineItemRepository;

    @Resource
    private StoringListRepository storingListRepository;

    @Resource
    private ReceiptSlipItemRepository receiptSlipItemRepository;

    @Resource
    private ReceiptSlipRepository receiptSlipRepository;

    @Resource
    private PurchaseOrderRepository purchaseOrderRepository;

    @Resource
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Resource
    private ProductStockStatusRepository productStockStatusRepository;

    @Resource
    private ProductInventoryRepository productInventoryRepository;

    @Resource
    private LocationRepository locationRepository;

    @Resource
    private ReceiptPoItemRelationRepository receiptPoItemRelationRepository;

    @Resource
    private StoringManager storingManager;

    @Resource
    private InventoryManager inventoryManager;

    @Resource
    private PurchaseOrderManager purchaseOrderManager;

    @Resource
    private PoSoItemRelationRepository poSoItemRelationRepository;

    @Resource
    private SalesOrderItemRepository salesOrderItemRepository;

    @Resource
    private SalesOrderRepository salesOrderRepository;

    @Resource
    private PartsSalesStockAllocationManager partsSalesStockAllocationManager;

    @Resource
    private RoleManager roleManager;

    @Resource
    private MessageSendManager messageSendManager;

    public StoringLineVO getStoringLineVO(SPM030301Form form) {
       return BeanMapUtils.mapTo(storingLineRepository.findFirstBySiteIdAndFacilityIdAndStoringLineNo(form.getSiteId(), form.getPointId(), form.getLineNo()), StoringLineVO.class);
    }

    public List<SPM030301BO> getStoringLineItemVOList(SPM030301Form form) {
        return storingLineItemRepository.getPartsStockRegisterList(form);
    }

    public List<LocationVO> findLocationVOList(String siteId, Long facilityId, List<String> locationCds) {
        return BeanMapUtils.mapListTo(locationRepository.findBySiteIdAndFacilityIdAndLocationCdIn(siteId, facilityId, locationCds), LocationVO.class);
    }

    public List<StoringLineItemVO> getStoringLineItemVOList(List<Long> storingLineItemIds) {
        return BeanMapUtils.mapListTo(storingLineItemRepository.findByStoringLineItemIdIn(storingLineItemIds), StoringLineItemVO.class);
    }

    public StoringLineVO getStoringLineVO(Long storingLineId) {
        return BeanMapUtils.mapTo(storingLineRepository.findFirstByStoringLineId(storingLineId), StoringLineVO.class);
    }

    public ReceiptSlipItemVO getReceiptSlipItemVO(Long receiptSlipItemId) {
        return BeanMapUtils.mapTo(receiptSlipItemRepository.findByReceiptSlipItemId(receiptSlipItemId), ReceiptSlipItemVO.class);
    }

    public ReceiptSlipVO getReceiptSlipVO(Long receiptSlipId) {
        return BeanMapUtils.mapTo(receiptSlipRepository.findByReceiptSlipId(receiptSlipId), ReceiptSlipVO.class);
    }

    public void saveOrUpdateData(StoringLineVO storingLineInfo,
                                 List<StoringLineItemVO> storingLineItemVOList,
                                 List<ReceiptSlipItemVO> receiptSlipItemVOList,
                                 ReceiptSlipVO receiptSlipVO,
                                 String siteId,
                                 Long pointId) {

        //storing_line_item新增或更新
        storingLineItemRepository.saveInBatch(BeanMapUtils.mapListTo(storingLineItemVOList, StoringLineItem.class));

        //StoringManager(doStoringReport更新storing_line和storing_list)
        storingManager.doStoringReport(storingLineInfo);

        //receipt_slip_item更新
        for(ReceiptSlipItemVO receiptSlipItemVO : receiptSlipItemVOList) {
            receiptSlipItemRepository.save(BeanMapUtils.mapTo(receiptSlipItemVO, ReceiptSlipItem.class));
        }

        //receipt_slip更新
        receiptSlipRepository.save(BeanMapUtils.mapTo(receiptSlipVO, ReceiptSlip.class));

        //purchaseOrderManager(doStoringReport更新purchase_order和purchase_order_item)
        purchaseOrderManager.doStoringReport(receiptSlipItemVOList, siteId);

        //InventoryManager(doStoringReport更新product_stock_status和product_inventory)
        List<StoringLineVO> storingLineVOs = new ArrayList<>();
        storingLineVOs.add(storingLineInfo);
        inventoryManager.doStoringReport(storingLineVOs);


        for(ReceiptSlipItemVO receiptSlipItemVO : receiptSlipItemVOList){

            ReceiptPoItemRelation receiptPoItemRelation = BeanMapUtils.mapTo(receiptPoItemRelationRepository.findByReceiptSlipItemId(receiptSlipItemVO.getReceiptSlipItemId()),ReceiptPoItemRelation.class);
            if(ObjectUtils.isEmpty(receiptPoItemRelation)) {
                continue;
            }

            PurchaseOrderItemVO purchaseOrderItemVo = BeanMapUtils.mapTo(purchaseOrderItemRepository.findByPurchaseOrderItemId(receiptPoItemRelation.getOrderItemId()),PurchaseOrderItemVO.class);

            // 取出salesOrderId 和 salesOrderItemId
            Long salesOrderId = this.getSalesOrderIdByPurchaseOrderItemId(siteId, purchaseOrderItemVo.getPurchaseOrderItemId());
            Long salesOrderItemId = this.getSalesOrderItemIdByPurchaseOrderItemId(siteId, purchaseOrderItemVo.getPurchaseOrderItemId());

            //如果不存在则跳过
            if (ObjectUtils.isEmpty(salesOrderId) || ObjectUtils.isEmpty(salesOrderItemId)) {
                continue;
            }

            // 取出salesOrderVO 和 salesOrderItemVO
            SalesOrderVO salesOrderVO = this.getSalesOrderById(salesOrderId);
            SalesOrderItemVO salesOrderItemVo = this.getSalesOrderItemById(salesOrderItemId);

            // 转换成TargetSalesOrderItemBO
            TargetSalesOrderItemBO targetSalesOrderItemBo = convertToTargetSalesOrderItemBO(salesOrderVO, salesOrderItemVo);

            //缺货解除
            this.doBoRelease(salesOrderVO.getSiteId(), salesOrderVO.getFacilityId(), List.of(targetSalesOrderItemBo));

            // 重新查询sales_order_item，如果BO_Qty<=0, 则添加信息到homePage
            if(this.getSalesOrderItemById(salesOrderItemId).getBoQty().compareTo(CommonConstants.BIGDECIMAL_ZERO) < 0){

                String sparepartCd = PJConstants.RoleCode.SPAREPART;
                String roleType = roleManager.getRoleTypeByDealerCode(sparepartCd);
                Long roleId = roleManager.getRoleIdByDealerCodeAndDealerType(sparepartCd, roleType);
                String message = CodedMessageUtils.getMessage("M.E.10148",new String[]{"Order allocate",salesOrderVO.getOrderNo()});

                List<Long> roleIds = new ArrayList<>();
                Map<Long,String> roleCdMap = new HashMap<>();

                roleIds.add(roleId);
                roleCdMap.put(roleId,sparepartCd);

                messageSendManager.notifyUserRoles(salesOrderVO.getSiteId(),roleIds,roleType,PJConstants.ProductClsType.PART.getCodeDbid(),message,"S098REPORTREADY","");
            }
        }
    }

    public PurchaseOrderVO getByPurchaseOrderNo(String siteId, Long facilityId, String purchaseOrderNo) {
        return BeanMapUtils.mapTo(purchaseOrderRepository.findBySiteIdAndFacilityIdAndOrderNo(siteId, facilityId, purchaseOrderNo),PurchaseOrderVO.class);
    }

    public PurchaseOrderItemVO getByPurchaseOrderIdAndProductId(Long purchaseOrderId, Long productId) {
        return BeanMapUtils.mapTo(purchaseOrderItemRepository.findByPurchaseOrderIdAndProductId(purchaseOrderId,productId), PurchaseOrderItemVO.class);
    }

    public Long getSalesOrderIdByPurchaseOrderItemId(String siteId,Long purchaseOrderItemId) {
        return poSoItemRelationRepository.getSalesOrderIdByPurchaseOrderItemId(siteId, purchaseOrderItemId);
    }

    public Long getSalesOrderItemIdByPurchaseOrderItemId(String siteId,Long purchaseOrderItemId) {
        return poSoItemRelationRepository.getSalesOrderItemIdByPurchaseOrderItemId(siteId, purchaseOrderItemId);
    }

    public SalesOrderItemVO getSalesOrderItemById(Long salesOrderItemId) {
        return BeanMapUtils.mapTo(salesOrderItemRepository.findBySalesOrderItemId(salesOrderItemId), SalesOrderItemVO.class);
    }

    public SalesOrderVO getSalesOrderById(Long salesOrderId) {
        return BeanMapUtils.mapTo(salesOrderRepository.findBySalesOrderId(salesOrderId), SalesOrderVO.class);
    }

    public void doBoRelease(String siteId, Long facilityId, List<TargetSalesOrderItemBO> targetList) {
        partsSalesStockAllocationManager.doBoRelease(siteId, facilityId, targetList);
    }


    private TargetSalesOrderItemBO convertToTargetSalesOrderItemBO(SalesOrderVO so, SalesOrderItemVO soi){

        TargetSalesOrderItemBO bo = new TargetSalesOrderItemBO();

        bo.setSiteId(so.getSiteId());
        bo.setDeliveryPointId(so.getFacilityId());
        bo.setSalesOrderId(so.getSalesOrderId());
        bo.setOrderNo(so.getOrderNo());
        bo.setOrderPriorityType(so.getOrderPriorityType());
        bo.setSalesOrderItemId(soi.getSalesOrderItemId());
        bo.setItemSeqNo(String.valueOf(soi.getSeqNo()));
        bo.setPrioritySeqNo(soi.getOrderPrioritySeq());
        bo.setAllocateDueDate(so.getAllocateDueDate());
        bo.setProductId(soi.getProductId());
        bo.setAllocateProdId(soi.getAllocatedProductId());
        bo.setOriginalQty(soi.getOrderQty());
        bo.setWaitingAllocateQty(soi.getWaitingAllocateQty());
        bo.setBoQty(soi.getBoQty());

        return bo;
    }
}
