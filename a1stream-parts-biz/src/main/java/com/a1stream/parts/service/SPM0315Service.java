package com.a1stream.parts.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import com.a1stream.domain.bo.parts.SPM031501BO;
import com.a1stream.domain.bo.parts.TargetSalesOrderItemBO;
import com.a1stream.domain.entity.ProductInventory;
import com.a1stream.domain.entity.ReceiptManifest;
import com.a1stream.domain.entity.ReceiptPoItemRelation;
import com.a1stream.domain.entity.ReceiptSlip;
import com.a1stream.domain.entity.ReceiptSlipItem;
import com.a1stream.domain.entity.StoringLineItem;
import com.a1stream.domain.form.parts.SPM031501Form;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.PoSoItemRelationRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.PurchaseOrderItemRepository;
import com.a1stream.domain.repository.PurchaseOrderRepository;
import com.a1stream.domain.repository.ReceiptManifestRepository;
import com.a1stream.domain.repository.ReceiptPoItemRelationRepository;
import com.a1stream.domain.repository.ReceiptSlipItemRepository;
import com.a1stream.domain.repository.ReceiptSlipRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.repository.StoringLineItemRepository;
import com.a1stream.domain.repository.StoringLineRepository;
import com.a1stream.domain.repository.StoringListRepository;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.PurchaseOrderItemVO;
import com.a1stream.domain.vo.ReceiptManifestVO;
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
* 功能描述:
*
* mid2287
* 2024年6月13日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/06   Wang Nan      New
*/

@Service
public class SPM0315Service {

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
    private ReceiptPoItemRelationRepository receiptPoItemRelationRepository;

    @Resource
    private PurchaseOrderRepository purchaseOrderRepository;

    @Resource
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Resource
    private ProductStockStatusRepository productStockStatusRepository;

    @Resource
    private ProductInventoryRepository productInventoryRepository;

    @Resource
    private ReceiptSlipRepository receipSlipRepo;

    @Resource
    private PoSoItemRelationRepository poSoItemRelationRepository;

    @Resource
    private LocationRepository locationRepository;

    @Resource
    private StoringManager storingManager;

    @Resource
    private InventoryManager inventoryManager;

    @Resource
    private PurchaseOrderManager purchaseOrderManager;

    @Resource
    private SalesOrderRepository salesOrderRepository;

    @Resource
    private SalesOrderItemRepository salesOrderItemRepository;

    @Resource
    private PartsSalesStockAllocationManager partsSalesStockAllocationManager;

    @Resource
    private RoleManager roleManager;

    @Resource
    private MessageSendManager messageSendManager;

    @Resource
    private ReceiptManifestRepository receiptManifestRepository;

    public List<SPM031501BO> getPartsStockRegisterList(SPM031501Form form) {
        return storingLineRepository.getPartsStockRegisterMultiLinesList(form);
    }

    public StoringLineItemVO getStoringLineItemVO(Long storingLineItemId) {
        return BeanMapUtils.mapTo(storingLineItemRepository.findByStoringLineItemId(storingLineItemId), StoringLineItemVO.class);
    }

    public List<StoringLineVO> getStoringLineVOList(Set<Long> storingLineIds) {
        return BeanMapUtils.mapListTo(storingLineRepository.findByStoringLineIdIn(storingLineIds),StoringLineVO.class);
    }

    public List<ReceiptSlipItemVO> getReceiptSlipItemVOList(Set<Long> receiptSlipItemIds) {
        return BeanMapUtils.mapListTo(receiptSlipItemRepository.findByReceiptSlipItemIdIn(receiptSlipItemIds), ReceiptSlipItemVO.class);
    }

    public List<ReceiptSlipVO> getReceiptSlipVOList(Set<Long> receiptSlipIds) {
        return BeanMapUtils.mapListTo(receiptSlipRepository.findByReceiptSlipIdIn(receiptSlipIds), ReceiptSlipVO.class);
    }

    public List<LocationVO> findLocationVOList(String siteId, Long facilityId, List<String> locationCds) {
        return BeanMapUtils.mapListTo(locationRepository.findBySiteIdAndFacilityIdAndLocationCdIn(siteId, facilityId, locationCds), LocationVO.class);
    }

    public List<StoringLineItemVO> getStoringLineItemVOList(List<Long> storingLineItemIds) {
        return BeanMapUtils.mapListTo(storingLineItemRepository.findByStoringLineItemIdIn(storingLineItemIds), StoringLineItemVO.class);
    }

    public ReceiptManifestVO findFirstBySiteIdAndSupplierInvoiceNo(String siteId, String supplierInvoiceNo) {
        return BeanMapUtils.mapTo(receiptManifestRepository.findFirstBySiteIdAndSupplierInvoiceNo(siteId,supplierInvoiceNo), ReceiptManifestVO.class);
    }

    public Integer countBySiteIdAndSupplierInvoiceNo(String siteId, String supplierInvoiceNo) {
        return receiptManifestRepository.countBySiteIdAndSupplierInvoiceNo(siteId,supplierInvoiceNo);
    }

    public Integer countBySiteIdAndSupplierShipmentNo(String siteId, String supplierShipmentNo){
        return receiptManifestRepository.countBySiteIdAndSupplierShipmentNo(siteId,supplierShipmentNo);
    }

    public void saveOrUpdateData(List<StoringLineVO> storingLineVOList,
                                 List<StoringLineItemVO> storingLineItemVOList,
                                 List<ReceiptSlipItemVO> receiptSlipItemVOList,
                                 List<ReceiptSlipVO> receiptSlipVOList,
                                 String siteId,
                                 Long pointId,
                                 List<SPM031501BO> list) {

        //storing_line_item更新
        storingLineItemRepository.saveInBatch(BeanMapUtils.mapListTo(storingLineItemVOList, StoringLineItem.class));

        //StoringManager(doStoringReport更新storing_line和storing_list)
        for (StoringLineVO storingLineVO : storingLineVOList) {
            storingManager.doStoringReport(storingLineVO);
        }

        //receipt_slip_item更新
        receiptSlipItemRepository.saveInBatch(BeanMapUtils.mapListTo(receiptSlipItemVOList, ReceiptSlipItem.class));

        //receipt_slip更新
        receiptSlipRepository.saveInBatch(BeanMapUtils.mapListTo(receiptSlipVOList, ReceiptSlip.class));

        //PurchaseOrderManager(doStoringReport更新purchase_order和purchase_order_item)
        purchaseOrderManager.doStoringReport(receiptSlipItemVOList, siteId);

        //InventoryManager(doStoringReport更新product_stock_status和product_inventory)
        inventoryManager.doStoringReport(storingLineVOList);

        //缺货解除
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

        List<SPM031501BO> mainLocationList = list.stream().filter(bo -> CommonConstants.CHAR_Y.equals(bo.getSetAsMainLocation())).collect(Collectors.toList());

        //收集要设置主货位的partsId
        Set<Long> partsIds = mainLocationList.stream().map(SPM031501BO::getPartsId).collect(Collectors.toSet());

        Set<Long> locationIds = mainLocationList.stream().map(SPM031501BO::getLocationId).collect(Collectors.toSet());

        //待取消的主货位
        List<ProductInventoryVO> cancelProductInventoryVOs = BeanMapUtils.mapListTo(productInventoryRepository.findMainProductInventoryList(siteId,partsIds,pointId,CommonConstants.CHAR_Y),ProductInventoryVO.class);

        Map<Long, ProductInventoryVO> cancelProductInventoryVOMap = new HashMap<>();
        if (!ObjectUtils.isEmpty(cancelProductInventoryVOs)) {
            cancelProductInventoryVOMap = cancelProductInventoryVOs.stream().collect(Collectors.toMap(ProductInventoryVO::getProductId, Function.identity()));
        }

        //找到要更新的product_inventory
        Map<String, ProductInventoryVO> updateProductInventoryVOMap = new HashMap<>();
        List<ProductInventoryVO> updateProductInventoryVOs = BeanMapUtils.mapListTo(productInventoryRepository.findProductInventoryList(siteId,partsIds,pointId,locationIds),ProductInventoryVO.class);
        if (!ObjectUtils.isEmpty(updateProductInventoryVOs)) {

            updateProductInventoryVOMap = updateProductInventoryVOs.stream().collect(Collectors.toMap(
                vo -> vo.getProductId() + CommonConstants.CHAR_UNDERSCORE + vo.getLocationId(), // 组合 key
                vo -> vo // 值为 ProductInventoryVO 对象
            ));
        }

        //最后保存的值
        List<ProductInventoryVO> result = new ArrayList<>();
        //需要删除非主货位且数量为0的货位
        List<ProductInventoryVO> deleteResult = new ArrayList<>();
        Set<Long> beforeLocationIds = new HashSet<>();
        Set<Long> afterLocationIds = new HashSet<>();

        //更新主货位->(不会有新增的情况，inventoryManager.doStoringReport已经新增了)
        for (SPM031501BO item : mainLocationList) {

            ProductInventoryVO productInventoryVO = cancelProductInventoryVOMap.get(item.getPartsId());

            if (!ObjectUtils.isEmpty(productInventoryVO)) {
                //代表该产品已存在主货位
                if(productInventoryVO.getLocationId().equals(item.getLocationId())){
                    //代表此次设置的主货位本身已经是主货位了，则无需进行任何设置
                }else{
                    //代表需要取消原本的主货位然后设置
                    ProductInventoryVO updateVO = updateProductInventoryVOMap.get(item.getPartsId() + CommonConstants.CHAR_UNDERSCORE + item.getLocationId());
                    if (!ObjectUtils.isEmpty(updateVO)) {
                        //更新->将原本的主货位取消，设置新的主货位
                        updateVO.setPrimaryFlag(CommonConstants.CHAR_Y);
                        productInventoryVO.setPrimaryFlag(CommonConstants.CHAR_N);
                        beforeLocationIds.add(productInventoryVO.getLocationId());
                        afterLocationIds.add(item.getLocationId());
                        result.add(updateVO);
                        if (productInventoryVO.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                            deleteResult.add(productInventoryVO);
                        }else{
                            result.add(productInventoryVO);
                        }

                    }
                }
            }else{
                //代表该产品不存在其他主货位
                ProductInventoryVO updateVO = updateProductInventoryVOMap.get(item.getPartsId() + CommonConstants.CHAR_UNDERSCORE + item.getLocationId());
                if (!ObjectUtils.isEmpty(updateVO)) {
                    //更新
                    updateVO.setPrimaryFlag(CommonConstants.CHAR_Y);
                    afterLocationIds.add(item.getLocationId());
                    result.add(updateVO);
                }
            }
        }
        productInventoryRepository.saveInBatch(BeanMapUtils.mapListTo(result, ProductInventory.class));
        productInventoryRepository.deleteAllInBatch(BeanMapUtils.mapListTo(deleteResult, ProductInventory.class));
        inventoryManager.doUpdateLocationMainFlag(beforeLocationIds, afterLocationIds);

    }

    public Long getSalesOrderIdByPurchaseOrderItemId(String siteId,Long purchaseOrderItemId) {
        return poSoItemRelationRepository.getSalesOrderIdByPurchaseOrderItemId(siteId, purchaseOrderItemId);
    }

    public Long getSalesOrderItemIdByPurchaseOrderItemId(String siteId,Long purchaseOrderItemId) {
        return poSoItemRelationRepository.getSalesOrderItemIdByPurchaseOrderItemId(siteId, purchaseOrderItemId);
    }

    public SalesOrderVO getSalesOrderById(Long salesOrderId) {
        return BeanMapUtils.mapTo(salesOrderRepository.findBySalesOrderId(salesOrderId), SalesOrderVO.class);
    }

    public SalesOrderItemVO getSalesOrderItemById(Long salesOrderItemId) {
        return BeanMapUtils.mapTo(salesOrderItemRepository.findBySalesOrderItemId(salesOrderItemId), SalesOrderItemVO.class);
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
