package com.a1stream.common.manager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.bo.ModifyPurchaseOrderBO;
import com.a1stream.common.bo.ModifyPurchaseOrderItemBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.PurchaseOrderStatus;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.OrgRelationType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.PurchaseMethodType;
import com.a1stream.common.constants.PJConstants.PurchaseOrderPriorityType;
import com.a1stream.domain.entity.CmmMstOrganization;
import com.a1stream.domain.entity.MstProduct;
import com.a1stream.domain.entity.OrganizationRelation;
import com.a1stream.domain.entity.PoSoItemRelation;
import com.a1stream.domain.entity.PurchaseOrder;
import com.a1stream.domain.entity.PurchaseOrderItem;
import com.a1stream.domain.repository.CmmMstOrganizationRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.OrganizationRelationRepository;
import com.a1stream.domain.repository.PoSoItemRelationRepository;
import com.a1stream.domain.repository.PurchaseOrderItemRepository;
import com.a1stream.domain.repository.PurchaseOrderRepository;
import com.a1stream.domain.repository.ReceiptPoItemRelationRepository;
import com.a1stream.domain.repository.ReceiptSlipRepository;
import com.a1stream.domain.vo.PoSoItemRelationVO;
import com.a1stream.domain.vo.PurchaseOrderItemVO;
import com.a1stream.domain.vo.PurchaseOrderVO;
import com.a1stream.domain.vo.ReceiptPoItemRelationVO;
import com.a1stream.domain.vo.ReceiptSlipItemVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Component
public class PurchaseOrderManager {

    @Resource
    private PurchaseOrderRepository purchaseOrderRepo;

    @Resource
    private PurchaseOrderItemRepository poItemRepo;

    @Resource
    private ReceiptPoItemRelationRepository receiptPoItemRelaRepo;

    @Resource
    private OrganizationRelationRepository organizationRelationRepository;

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private InventoryManager inventoryManager;

    @Resource
    private GenerateNoManager generateNoManager;

    @Resource
    private PoSoItemRelationRepository poSoItemRelationRepository;

    @Resource
    private CmmMstOrganizationRepository cmmMstOrganizationRepository;

    @Resource
    private ReceiptSlipRepository receipSlipRepo;


    /**
     *
     * doRegisterPurchaseOrder mid1100 2024年4月18日
     */
    public void doRegisterPurchaseOrder() {

    }

    /**
     *
     * doModifyPurchaseOrder mid1100 2024年4月18日
     */
    public void doModifyPurchaseOrder(ModifyPurchaseOrderBO model) {

        if (model.getPurchaseOrderId() == null) {
            throw new BusinessCodedException("model.getPurchaseOrderId() is null");
        }

        PurchaseOrderVO purchaseOrderVO = BeanMapUtils.mapTo(purchaseOrderRepo.findByPurchaseOrderId(model.getPurchaseOrderId()), PurchaseOrderVO.class);
        if (purchaseOrderVO == null) {
            throw new BusinessCodedException("Purchase Order does not exist");
        }
        List<ModifyPurchaseOrderItemBO> purchaseOrderItems = model.getPurchaseOrderItems();

        // add purchaseOrderItem
        List<ModifyPurchaseOrderItemBO> newItems = purchaseOrderItems.stream()
                .filter(order -> order.getPurchaseOrderItemId() == null).collect(Collectors.toList());

        List<PurchaseOrderItemVO> newPoItemList = newPurchaseOrderItemVO(purchaseOrderVO, newItems);

        // delete purchaseOrderItem
        List<ModifyPurchaseOrderItemBO> deleteItems = purchaseOrderItems.stream()
                .filter(order -> StringUtils.equals(order.getDeleteFlag(), CommonConstants.CHAR_ONE)).collect(Collectors.toList());

        List<Long> deletePoItemIdList = deleteItems.stream().map(ModifyPurchaseOrderItemBO::getPurchaseOrderItemId).collect(Collectors.toList());

        // update purchaseOrderItem
        List<ModifyPurchaseOrderItemBO> updateItems = purchaseOrderItems.stream()
                .filter(order -> !(StringUtils.equals(order.getDeleteFlag(), CommonConstants.CHAR_ONE)) && order.getPurchaseOrderItemId() != null)
                .collect(Collectors.toList());

        List<PurchaseOrderItemVO> updatePoItemList = getUpdatePurchaseOrderVOList(updateItems);

        // maintain db records
        poItemRepo.saveAll(BeanMapUtils.mapListTo(newPoItemList, PurchaseOrderItem.class));
        poItemRepo.saveAll(BeanMapUtils.mapListTo(updatePoItemList, PurchaseOrderItem.class));
        poItemRepo.deletePurchaseOrderItemByKey(deletePoItemIdList);

        // update purchase order total
        List<PurchaseOrderItemVO> allPurchaseOrderItemsVOList = BeanMapUtils.mapListTo(poItemRepo.findByPurchaseOrderId(model.getPurchaseOrderId()), PurchaseOrderItemVO.class);

        setPurchaseOrderSum(purchaseOrderVO, allPurchaseOrderItemsVOList);
        purchaseOrderVO.setDeliverPlanDate(model.getPlanDate());
        purchaseOrderVO.setMemo(model.getComment());

        purchaseOrderRepo.save(BeanMapUtils.mapTo(purchaseOrderVO, PurchaseOrder.class));
    }

    /**
     * TODO uc doPurchaseOrderIssue mid1100 2024年4月18日
     */
    public void doPurchaseOrderIssue(List<Long> purchaseOrderIds, PJUserDetails uc) {

        // 获取对应List<PurchaseOrder>及List<PurchaseOrderItem>
        List<PurchaseOrderVO> purchaseOrderVOList = BeanMapUtils.mapListTo(purchaseOrderRepo.findByPurchaseOrderIdIn(purchaseOrderIds), PurchaseOrderVO.class);
        if (purchaseOrderVOList.isEmpty()) {
            throw new BusinessCodedException("Can not find any purchase order data");
        }
        List<PurchaseOrderItemVO> purchaseOrderItemVOList = BeanMapUtils.mapListTo(poItemRepo.findByPurchaseOrderIdIn(purchaseOrderIds), PurchaseOrderItemVO.class);

        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);
        String orderStatus = PurchaseOrderStatus.SPONPURCHASE;

        // 更新
        for (PurchaseOrderVO po : purchaseOrderVOList) {
            po.setApproveDate(sysDate);
//            po.setApprovePicId(uc == null ? "" : uc.getUserCode());
            po.setApprovePicNm(uc == null ? "" : uc.getUsername());
            po.setOrderStatus(orderStatus);
            po.setTotalActualQty(po.getTotalQty());
            po.setTotalActualAmt(po.getTotalAmount());
        }

        for (PurchaseOrderItemVO item : purchaseOrderItemVOList) {
            item.setOnPurchaseQty(item.getOrderQty());
        }

        purchaseOrderRepo.saveInBatch(BeanMapUtils.mapListTo(purchaseOrderVOList, PurchaseOrder.class));
        poItemRepo.saveInBatch(BeanMapUtils.mapListTo(purchaseOrderItemVOList, PurchaseOrderItem.class));
    }

    /**
     *
     * doPurchaseOrderReceipt mid1100 2024年4月18日
     */
    public void doPurchaseOrderReceipt(List<ReceiptSlipItemVO> receiptSlipItemList, String siteId) {


            this.doPurchaseOrderReceiptOrStoring(receiptSlipItemList, siteId, "doPurchaseOrderReceipt");

    }

    /**
     *
     * doStoringReport mid1100 2024年4月18日
     */
    public void doStoringReport(List<ReceiptSlipItemVO> receiptSlipItemList, String siteId) {

        //抽出item的receipt_slip_id并查出来
        Set<Long> receiptSlipIds = receiptSlipItemList.stream().map(ReceiptSlipItemVO::getReceiptSlipId).collect(Collectors.toSet());
        List<ReceiptSlipVO> receiptSlips = BeanMapUtils.mapListTo(receipSlipRepo.findByReceiptSlipIdIn(receiptSlipIds),ReceiptSlipVO.class);
        //过滤出TranscationType不是PURCHASESTOCKIN的数据
        receiptSlips = receiptSlips.stream().filter(receiptSlip -> !PJConstants.InventoryTransactionType.PURCHASESTOCKIN.getCodeDbid().equals(receiptSlip.getInventoryTransactionType())).collect(Collectors.toList());
        Set<Long> filterIds = receiptSlips.stream().map(ReceiptSlipVO::getReceiptSlipId).collect(Collectors.toSet());
        receiptSlipItemList = receiptSlipItemList.stream().filter(item -> !filterIds.contains(item.getReceiptSlipId())).collect(Collectors.toList());
        if(!receiptSlipItemList.isEmpty()) {
            this.doPurchaseOrderReceiptOrStoring(receiptSlipItemList, siteId, "doStoringReport");
        }
    }

    private void doPurchaseOrderReceiptOrStoring(List<ReceiptSlipItemVO> receiptSlipItemList, String siteId, String methodName) {

        // 取得receiptSlipItem
        List<Long> receiptSlipItemIds = receiptSlipItemList.stream().map(ReceiptSlipItemVO::getReceiptSlipItemId).collect(Collectors.toList());

        if (receiptSlipItemIds.isEmpty()) {
            throw new BusinessCodedException("does not exist any receiptSlipItem id");
        }
        // ReceiptPoItemRelation
        List<ReceiptPoItemRelationVO> relationVOList = BeanMapUtils.mapListTo(receiptPoItemRelaRepo.findByReceiptSlipItemIdIn(receiptSlipItemIds), ReceiptPoItemRelationVO.class);
        if (relationVOList.isEmpty()) {
            throw new BusinessCodedException("does not exist any receiptPoItemRelation info.");
        }
        // find List<purchaseOrderItem>
        List<Long> orderItemIds = relationVOList.stream().map(ReceiptPoItemRelationVO::getOrderItemId).collect(Collectors.toList());
        List<PurchaseOrderItemVO> purchaseOrderItemVOList = BeanMapUtils.mapListTo(poItemRepo.findByPurchaseOrderItemIdIn(orderItemIds), PurchaseOrderItemVO.class);

        // find List<purchaseOrder>
        Set<String> purchaseOrderNos = relationVOList.stream().map(ReceiptPoItemRelationVO::getPurchaseOrderNo).collect(Collectors.toSet());
        List<PurchaseOrderVO> purchaseOrderVOList = BeanMapUtils.mapListTo(purchaseOrderRepo.findBySiteIdAndOrderNoIn(siteId, purchaseOrderNos), PurchaseOrderVO.class);

        // prepare Map parameter
        // key = <接收单明细ID>
        Map<Long, ReceiptSlipItemVO> receiptSlipItemVOMap = receiptSlipItemList.stream().collect(Collectors.toMap(ReceiptSlipItemVO::getReceiptSlipItemId, Function.identity()));

        // Map<企业采购单明细ID, List>
        Map<Long, List<ReceiptPoItemRelationVO>> slipItemIdRelationMap = relationVOList.stream().collect(Collectors.groupingBy(ReceiptPoItemRelationVO::getOrderItemId));
        // 更新:PurchaseOrderItem
        for (PurchaseOrderItemVO poItem : purchaseOrderItemVOList) {
            if (slipItemIdRelationMap.containsKey(poItem.getPurchaseOrderItemId())) {

                List<ReceiptPoItemRelationVO> relationList = slipItemIdRelationMap.get(poItem.getPurchaseOrderItemId());
                BigDecimal receiptQty = BigDecimal.ZERO;
                BigDecimal frozenQty = BigDecimal.ZERO;
                for (ReceiptPoItemRelationVO relation : relationList) {
                    if (receiptSlipItemVOMap.containsKey(relation.getReceiptSlipItemId())) {
                        ReceiptSlipItemVO receiptSlipItem = receiptSlipItemVOMap.get(relation.getReceiptSlipItemId());

                        receiptQty = receiptQty.add(receiptSlipItem.getReceiptQty() == null ? BigDecimal.ZERO : receiptSlipItem.getReceiptQty());
                        frozenQty = frozenQty.add(receiptSlipItem.getFrozenQty() == null ? BigDecimal.ZERO : receiptSlipItem.getFrozenQty());
                    }
                }
                switch (methodName) {
                case "doPurchaseOrderReceipt":
                    BigDecimal onPurchaseQty = poItem.getOnPurchaseQty().subtract(receiptQty);
                    BigDecimal receiveQty = poItem.getReceiveQty().add(receiptQty).subtract(frozenQty);
                    if (onPurchaseQty.compareTo(BigDecimal.ZERO) < 0 || receiveQty.compareTo(BigDecimal.ZERO) < 0) {
                        throw new BusinessCodedException("onPurchaseQty/receiveQty is becoming less than 0, please check");
                    }
                    poItem.setOnPurchaseQty(onPurchaseQty);
                    poItem.setReceiveQty(receiveQty);
                    break;
                case "doStoringReport":
                    BigDecimal receivedQty = poItem.getReceiveQty().subtract(receiptQty);
                    if (receivedQty.compareTo(BigDecimal.ZERO) < 0) {
                        throw new BusinessCodedException("receiveQty is becoming less than 0, please check");
                    }
                    poItem.setReceiveQty(receivedQty);
                    poItem.setStoredQty(poItem.getStoredQty().add(receiptQty));
                    break;
                }
            }
        }
        poItemRepo.saveInBatch(BeanMapUtils.mapListTo(purchaseOrderItemVOList, PurchaseOrderItem.class));

        // doPurchaseOrderReceipt:
        // 更新item后，如果所有purchaseOrderItems.onPurchaseQty=0, 设置为：S042SPONRECEIVING
        // doStoringReport:
        // 更新item后，如果所有purchaseOrderItems.receiveQty=0,设置为：S042SPREGISTERED
        List<Long> purchaseOrderIds = purchaseOrderVOList.stream().map(PurchaseOrderVO::getPurchaseOrderId).collect(Collectors.toList());
        List<PurchaseOrderItemVO> allPurchaseOrderItemsVOList = BeanMapUtils.mapListTo(poItemRepo.findByPurchaseOrderIdIn(purchaseOrderIds), PurchaseOrderItemVO.class);

        Map<Long, List<PurchaseOrderItemVO>> purchaseOrderItemsMap = allPurchaseOrderItemsVOList.stream().collect(Collectors.groupingBy(PurchaseOrderItemVO::getPurchaseOrderId));

        for (PurchaseOrderVO po : purchaseOrderVOList) {
            if (purchaseOrderItemsMap.containsKey(po.getPurchaseOrderId())) {
                List<PurchaseOrderItemVO> poItems = purchaseOrderItemsMap.get(po.getPurchaseOrderId());
                switch (methodName) {
                case "doPurchaseOrderReceipt":
                    List<PurchaseOrderItemVO> onPurchaseQtyZeroItems = poItems.stream().filter(item -> item.getOnPurchaseQty().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());

                    if (onPurchaseQtyZeroItems.isEmpty()) {
                        po.setOrderStatus(PurchaseOrderStatus.SPONRECEIVING);
                    }
                    break;
                case "doStoringReport":
                    List<PurchaseOrderItemVO> receiveQtyZeroItems = poItems.stream().filter(item -> item.getReceiveQty().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());

                    if (receiveQtyZeroItems.isEmpty()) {
                        po.setOrderStatus(PurchaseOrderStatus.SPREGISTERED);
                    }
                    break;
                }
            }
        }
        purchaseOrderRepo.saveInBatch(BeanMapUtils.mapListTo(purchaseOrderVOList, PurchaseOrder.class));
    }

    private void setPurchaseOrderSum(PurchaseOrderVO purchaseOrderVO, List<PurchaseOrderItemVO> newOrUpdatePoItemVO) {

        BigDecimal totalQty = newOrUpdatePoItemVO.stream().map(item -> item.getOrderQty() != null ? item.getOrderQty() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmt = newOrUpdatePoItemVO.stream().map(item -> item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalActualQty = newOrUpdatePoItemVO.stream().map(item -> item.getActualQty() != null ? item.getActualQty() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalActualAmt = newOrUpdatePoItemVO.stream().map(item -> item.getActualAmt() != null ? item.getActualAmt() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        purchaseOrderVO.setTotalQty(totalQty);
        purchaseOrderVO.setTotalAmount(totalAmt);
        purchaseOrderVO.setTotalActualQty(totalActualQty);
        purchaseOrderVO.setTotalActualAmt(totalActualAmt);
    }

    private void buildUpdatePurchaseOrderItemFields(ModifyPurchaseOrderItemBO item, PurchaseOrderItemVO poItemVO) {

        poItemVO.setOrderQty(item.getPurchaseQty());
        poItemVO.setActualQty(item.getPurchaseQty());
        poItemVO.setPurchasePrice(item.getPurchasePrice());

        BigDecimal amt = item.getPurchasePrice().multiply(item.getPurchaseQty());
        poItemVO.setAmount(amt);
        poItemVO.setActualAmt(amt);
        poItemVO.setBoCancelFlag(item.getBoCancelFlag());
    }

    private PurchaseOrderItemVO buildNewPurchaseOrderItemFields(PurchaseOrderVO purchaseOrder, ModifyPurchaseOrderItemBO item) {

        PurchaseOrderItemVO poItemVO = new PurchaseOrderItemVO();

        poItemVO.setSiteId(purchaseOrder.getSiteId());
        poItemVO.setPurchaseOrderId(purchaseOrder.getPurchaseOrderId());
        poItemVO.setProductId(item.getPartsId());
        poItemVO.setProductCd(item.getPartsNo());
        poItemVO.setProductNm(item.getPartsName());

        poItemVO.setOnPurchaseQty(BigDecimal.ZERO);
        poItemVO.setTransQty(BigDecimal.ZERO);
        poItemVO.setReceiveQty(BigDecimal.ZERO);
        poItemVO.setStoredQty(BigDecimal.ZERO);
        poItemVO.setCancelledQty(BigDecimal.ZERO);

        poItemVO.setStandardPrice(item.getStdPrice());
        poItemVO.setProductClassification(ProductClsType.PART.getCodeDbid());

        return poItemVO;
    }

    private List<PurchaseOrderItemVO> getUpdatePurchaseOrderVOList(List<ModifyPurchaseOrderItemBO> updateItems) {

        List<PurchaseOrderItemVO> updatePoItemList = new ArrayList<>();

        if (!updateItems.isEmpty()) {
            List<Long> updatePoItemIdList = updateItems.stream().map(ModifyPurchaseOrderItemBO::getPurchaseOrderItemId).collect(Collectors.toList());

            List<PurchaseOrderItemVO> poItemVOList = BeanMapUtils.mapListTo(poItemRepo.findByPurchaseOrderItemIdIn(updatePoItemIdList), PurchaseOrderItemVO.class);
            Map<Long, PurchaseOrderItemVO> poItemVOMap = poItemVOList.stream().collect(Collectors.toMap(PurchaseOrderItemVO::getPurchaseOrderItemId, Function.identity()));

            Iterator<ModifyPurchaseOrderItemBO> updateItem = updateItems.iterator();
            while (updateItem.hasNext()) {
                ModifyPurchaseOrderItemBO item = updateItem.next();
                if (poItemVOMap.containsKey(item.getPurchaseOrderItemId())) {
                    PurchaseOrderItemVO poItemVO = poItemVOMap.get(item.getPurchaseOrderItemId());
                    buildUpdatePurchaseOrderItemFields(item, poItemVO);

                    updatePoItemList.add(poItemVO);
                }
            }
        }

        return updatePoItemList;
    }

    private List<PurchaseOrderItemVO> newPurchaseOrderItemVO(PurchaseOrderVO purchaseOrder, List<ModifyPurchaseOrderItemBO> newItems) {

        List<PurchaseOrderItemVO> newPoItemList = new ArrayList<>();
        Iterator<ModifyPurchaseOrderItemBO> newItem = newItems.iterator();
        while (newItem.hasNext()) {
            ModifyPurchaseOrderItemBO item = newItem.next();

            PurchaseOrderItemVO poItemVO = buildNewPurchaseOrderItemFields(purchaseOrder, item);

            buildUpdatePurchaseOrderItemFields(item, poItemVO);

            poItemVO.setUpdateCount(0);
            newPoItemList.add(poItemVO);
        }

        return newPoItemList;
    }

    public void savePurchaseOrder(Map<Long, Long> allocatedPartsIdWithOrderItemIdMap,
                                Map<Long, BigDecimal> allocatePartsIdWithBoCancelQtyMap,
                                Long entryFacility,
                                Long deliveryFacilityId,
                                String consigneeId,
                                String deliveryPlanDate,
                                String siteId) {

        List<OrganizationRelation> OrganizationRelations =organizationRelationRepository.findBySiteIdAndRelationType(siteId, OrgRelationType.SUPPLIER.getCodeDbid());
        Long supplierId = null;
        if (!OrganizationRelations.isEmpty()) {
            supplierId = OrganizationRelations.get(0).getToOrganizationId();
        } else {
            return;
        }
        Map<String, List<MstProduct>> largeGroupWithProductMap = getPartsLargeGroup(allocatedPartsIdWithOrderItemIdMap.keySet());
        for (String largeGroup : largeGroupWithProductMap.keySet()) {

            PurchaseOrderVO pOrderVO = PurchaseOrderVO.create();
            pOrderVO.setOrderMethodType(PurchaseMethodType.POPOS.getCodeDbid());
            pOrderVO.setOrderPriorityType(PurchaseOrderPriorityType.POEO.getCodeDbid());
            pOrderVO.setOrderDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
            pOrderVO.setFacilityId(deliveryFacilityId);
            pOrderVO.setDeliverPlanDate(deliveryPlanDate);
            pOrderVO.setSiteId(siteId);
            pOrderVO.setConsigneeId(consigneeId);
            pOrderVO.setSupplierId(supplierId);
            pOrderVO.setProductClassification(ProductClsType.PART.getCodeDbid());
//            pOrderVO.setOrderPicId("");
            pOrderVO.setOrderPicNm(siteId);

            boolean savePurchaseOrderFlag = false;
            PurchaseOrderItemVO itemVO;
            PoSoItemRelationVO itemRelationVO;
            List<PurchaseOrderItemVO> itemVoList = new ArrayList<>();
            List<PoSoItemRelationVO> itemRelationVOList = new ArrayList<>();

            CmmMstOrganization cmmMstOrganization = cmmMstOrganizationRepository.findBySiteIdAndOrganizationCd(CommonConstants.CHAR_DEFAULT_SITE_ID, siteId);
            for (MstProduct product : largeGroupWithProductMap.get(largeGroup)) {

                BigDecimal lotQty = product != null ? product.getPurLotSize() : BigDecimal.ZERO;
                BigDecimal purchaseQty = calculatePurchaseRecommendationQtyFor(allocatePartsIdWithBoCancelQtyMap.get(product.getProductId()), lotQty);
                savePurchaseOrderFlag = true;
                itemVO = PurchaseOrderItemVO.create();
                itemVO.setPurchaseOrderId(pOrderVO.getPurchaseOrderId());
                itemVO.setSiteId(siteId);
                itemVO.setProductClassification(ProductClsType.PART.getCodeDbid());
                itemVO.setProductId(product.getProductId());
                itemVO.setProductCd(product.getProductCd());
                itemVO.setProductNm(product.getLocalDescription());
                itemVO.setPurchasePrice(product.getStdWsPrice());
                itemVO.setOrderQty(purchaseQty);
                itemVO.setActualQty(purchaseQty);
                itemVO.setBoCancelFlag(CommonConstants.CHAR_N);
                itemVO.setOnPurchaseQty(purchaseQty);
                itemVoList.add(itemVO);

                itemRelationVO = new PoSoItemRelationVO();
                itemRelationVO.setSiteId(siteId);
                itemRelationVO.setSalesOrderItemId(allocatedPartsIdWithOrderItemIdMap.get(product.getProductId()));
                itemRelationVO.setPurchaseOrderItemId(itemVO.getPurchaseOrderItemId());
                itemRelationVOList.add(itemRelationVO);
            }

            if (savePurchaseOrderFlag) {
                this.doRegisterPurchaseOrderForAuto(pOrderVO, largeGroup);
                inventoryManager.doPurchaseOrderRegister(PurchaseOrderPriorityType.POEO.getCodeDbid()
                                                        , itemVoList
                                                        , deliveryFacilityId
                                                        , siteId);

                this.purchaseOrderRepo.save(BeanMapUtils.mapTo(pOrderVO, PurchaseOrder.class) );
                this.poItemRepo.saveInBatch(BeanMapUtils.mapListTo(itemVoList, PurchaseOrderItem.class));
                this.poSoItemRelationRepository.saveInBatch(BeanMapUtils.mapListTo(itemRelationVOList, PoSoItemRelation.class));

                //TODO 发送采购数据文件，需要调用IFS，待补充
//                List<Long> purchaseOrderIds = new ArrayList<>();
//                purchaseOrderIds.add(pOrderVO.getPurchaseOrderId());
//                this.generalNonSerialPOIFDataAuto(purchaseOrderIds, siteId);
            }
        }
    }

    public void doRegisterPurchaseOrderForAuto(PurchaseOrderVO newPurchaseOrder, String largeGroup) {

        newPurchaseOrder.setOrderNo(generateNoManager.generateNonSerializedItemPurchaseOrderNo(newPurchaseOrder.getSiteId(), newPurchaseOrder.getFacilityId()));
        newPurchaseOrder.setOrderStatus(PurchaseOrderStatus.SPONPURCHASE);
    }

    public Map<String, List<MstProduct>> getPartsLargeGroup(Set<Long> partsIdSet) {

        Map<String, List<MstProduct>> largeGroupWithProductMap = new HashMap<>();
        List<MstProduct> productListM = mstProductRepository.findByProductIdIn(partsIdSet);

        List<MstProduct> productList = null;
        String largroup;
        for (MstProduct member : productListM) {
            largroup = member.getAllPath().split("|")[0];
            if (largeGroupWithProductMap.keySet().contains(largroup)) {
                largeGroupWithProductMap.get(largroup).add(member);
                //
            } else {
                productList = new ArrayList<MstProduct>();
                productList.add(member);
                largeGroupWithProductMap.put(largroup, productList);
            }
        }

        return largeGroupWithProductMap;
    }
    private BigDecimal calculatePurchaseRecommendationQtyFor(BigDecimal purchaseRecommendationQty, BigDecimal lotQty) {
        if (lotQty != null && lotQty.signum() == 1) { // lotQty >0
            BigDecimal lotCount = purchaseRecommendationQty.divide(lotQty, 0, RoundingMode.UP);
            BigDecimal returnQty = lotQty;
            if (lotCount.compareTo(new BigDecimal(1)) > 0) { //
                returnQty = lotCount.multiply(lotQty);
            }
            return returnQty;
        } else {
            BigDecimal lotCount = purchaseRecommendationQty.divide(BigDecimal.ONE, 0, RoundingMode.UP);
            return lotCount;
        }
    }
    public void doPurchaseOrderReceipt(List<ReceiptSlipItemVO> receiptSlipItemList, List<ReceiptPoItemRelationVO> relationVOList, String siteId) {

        this.doPurchaseOrderReceiptOrStoring(receiptSlipItemList, relationVOList, siteId, "doPurchaseOrderReceipt");
    }

    private void doPurchaseOrderReceiptOrStoring(List<ReceiptSlipItemVO> receiptSlipItemList, List<ReceiptPoItemRelationVO> relationVOList, String siteId, String methodName) {

        // find List<purchaseOrderItem>
        List<Long> orderItemIds = relationVOList.stream().map(ReceiptPoItemRelationVO::getOrderItemId).collect(Collectors.toList());
        List<PurchaseOrderItemVO> purchaseOrderItemVOList = BeanMapUtils.mapListTo(poItemRepo.findByPurchaseOrderItemIdIn(orderItemIds), PurchaseOrderItemVO.class);

        // find List<purchaseOrder>
        Set<String> purchaseOrderNos = relationVOList.stream().map(ReceiptPoItemRelationVO::getPurchaseOrderNo).collect(Collectors.toSet());
        List<PurchaseOrderVO> purchaseOrderVOList = BeanMapUtils.mapListTo(purchaseOrderRepo.findBySiteIdAndOrderNoIn(siteId, purchaseOrderNos), PurchaseOrderVO.class);

        // prepare Map parameter
        // key = <接收单明细ID>
        Map<Long, ReceiptSlipItemVO> receiptSlipItemVOMap = receiptSlipItemList.stream().collect(Collectors.toMap(ReceiptSlipItemVO::getReceiptSlipItemId, Function.identity()));

        // Map<企业采购单明细ID, List>
        Map<Long, List<ReceiptPoItemRelationVO>> slipItemIdRelationMap = relationVOList.stream().collect(Collectors.groupingBy(ReceiptPoItemRelationVO::getOrderItemId));
        // 更新:PurchaseOrderItem
        for (PurchaseOrderItemVO poItem : purchaseOrderItemVOList) {
            if (slipItemIdRelationMap.containsKey(poItem.getPurchaseOrderItemId())) {

                List<ReceiptPoItemRelationVO> relationList = slipItemIdRelationMap.get(poItem.getPurchaseOrderItemId());
                BigDecimal receiptQty = BigDecimal.ZERO;
                BigDecimal frozenQty = BigDecimal.ZERO;
                for (ReceiptPoItemRelationVO relation : relationList) {
                    if (receiptSlipItemVOMap.containsKey(relation.getReceiptSlipItemId())) {
                        ReceiptSlipItemVO receiptSlipItem = receiptSlipItemVOMap.get(relation.getReceiptSlipItemId());

                        receiptQty = receiptQty.add(receiptSlipItem.getReceiptQty() == null ? BigDecimal.ZERO : receiptSlipItem.getReceiptQty());
                        frozenQty = frozenQty.add(receiptSlipItem.getFrozenQty() == null ? BigDecimal.ZERO : receiptSlipItem.getFrozenQty());
                    }
                }
                switch (methodName) {
                case "doPurchaseOrderReceipt":
                    BigDecimal onPurchaseQty = poItem.getOnPurchaseQty().subtract(receiptQty);
                    BigDecimal receiveQty = poItem.getReceiveQty().add(receiptQty).subtract(frozenQty);
                    if (onPurchaseQty.compareTo(BigDecimal.ZERO) < 0 || receiveQty.compareTo(BigDecimal.ZERO) < 0) {
                        throw new BusinessCodedException("onPurchaseQty/receiveQty is becoming less than 0, please check");
                    }
                    poItem.setOnPurchaseQty(onPurchaseQty);
                    poItem.setReceiveQty(receiveQty);
                    break;
                case "doStoringReport":
                    BigDecimal receivedQty = poItem.getReceiveQty().subtract(receiptQty);
                    if (receivedQty.compareTo(BigDecimal.ZERO) < 0) {
                        throw new BusinessCodedException("receiveQty is becoming less than 0, please check");
                    }
                    poItem.setReceiveQty(receivedQty);
                    poItem.setStoredQty(poItem.getStoredQty().add(receiptQty));
                    break;
                }
            }
        }
        poItemRepo.saveInBatch(BeanMapUtils.mapListTo(purchaseOrderItemVOList, PurchaseOrderItem.class));

        // doPurchaseOrderReceipt:
        // 更新item后，如果所有purchaseOrderItems.onPurchaseQty=0, 设置为：S042SPONRECEIVING
        // doStoringReport:
        // 更新item后，如果所有purchaseOrderItems.receiveQty=0,设置为：S042SPREGISTERED
        List<Long> purchaseOrderIds = purchaseOrderVOList.stream().map(PurchaseOrderVO::getPurchaseOrderId).collect(Collectors.toList());
        List<PurchaseOrderItemVO> allPurchaseOrderItemsVOList = BeanMapUtils.mapListTo(poItemRepo.findByPurchaseOrderIdIn(purchaseOrderIds), PurchaseOrderItemVO.class);

        Map<Long, List<PurchaseOrderItemVO>> purchaseOrderItemsMap = allPurchaseOrderItemsVOList.stream().collect(Collectors.groupingBy(PurchaseOrderItemVO::getPurchaseOrderId));

        for (PurchaseOrderVO po : purchaseOrderVOList) {
            if (purchaseOrderItemsMap.containsKey(po.getPurchaseOrderId())) {
                List<PurchaseOrderItemVO> poItems = purchaseOrderItemsMap.get(po.getPurchaseOrderId());
                switch (methodName) {
                case "doPurchaseOrderReceipt":
                    List<PurchaseOrderItemVO> onPurchaseQtyZeroItems = poItems.stream().filter(item -> item.getOnPurchaseQty().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());

                    if (onPurchaseQtyZeroItems.isEmpty()) {
                        po.setOrderStatus(PurchaseOrderStatus.SPONRECEIVING);
                    }
                    break;
                case "doStoringReport":
                    List<PurchaseOrderItemVO> receiveQtyZeroItems = poItems.stream().filter(item -> item.getReceiveQty().compareTo(BigDecimal.ZERO) > 0 || item.getOnPurchaseQty().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());

                    if (receiveQtyZeroItems.isEmpty()) {
                        po.setOrderStatus(PurchaseOrderStatus.SPREGISTERED);
                    }
                    break;
                }
            }
        }
        purchaseOrderRepo.saveInBatch(BeanMapUtils.mapListTo(purchaseOrderVOList, PurchaseOrder.class));
    }
}