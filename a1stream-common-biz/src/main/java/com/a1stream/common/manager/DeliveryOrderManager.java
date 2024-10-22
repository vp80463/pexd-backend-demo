package com.a1stream.common.manager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.MstCodeConstants.DeliveryStatus;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.CostType;
import com.a1stream.common.constants.PJConstants.OrgRelationType;
import com.a1stream.common.exception.PJCustomException;
import com.a1stream.domain.entity.DeliveryOrder;
import com.a1stream.domain.entity.DeliveryOrderItem;
import com.a1stream.domain.entity.DeliverySerializedItem;
import com.a1stream.domain.entity.OrderSerializedItem;
import com.a1stream.domain.entity.OrganizationRelation;
import com.a1stream.domain.entity.ProductCost;
import com.a1stream.domain.entity.SalesOrderItem;
import com.a1stream.domain.repository.DeliveryOrderItemRepository;
import com.a1stream.domain.repository.DeliveryOrderRepository;
import com.a1stream.domain.repository.DeliverySerializedItemRepository;
import com.a1stream.domain.repository.OrderSerializedItemRepository;
import com.a1stream.domain.repository.OrganizationRelationRepository;
import com.a1stream.domain.repository.ProductCostRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.DeliverySerializedItemVO;
import com.a1stream.domain.vo.OrganizationRelationVO;
import com.a1stream.domain.vo.ProductCostVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DeliveryOrderManager {

    @Resource
    private DeliveryOrderRepository deliveryOrderRepo;

    @Resource
    private DeliveryOrderItemRepository deliveryOrderItemRepo;

    @Resource
    private ProductCostRepository productCostRepo;

    @Resource
    private OrganizationRelationRepository orgRelaRepo;

    @Resource
    private GenerateNoManager generateNoMgr;

    @Resource
    private SalesOrderItemRepository salesOrderItemRepository;

    @Resource
    private DeliverySerializedItemRepository deliverySerializedItemRepository;

    @Resource
    private OrderSerializedItemRepository orderSerializedItemRepository;

    public void doReceiptSlipRegister(List<Long> deliveryOrderIds) {

        if (deliveryOrderIds == null || deliveryOrderIds.isEmpty()) {
            throw new BusinessCodedException("no deliveryOrderIds exist");
        }
        List<DeliveryOrderVO> deliveryOrderVOList = BeanMapUtils.mapListTo(deliveryOrderRepo.findByDeliveryOrderIdIn(deliveryOrderIds), DeliveryOrderVO.class);
        for (DeliveryOrderVO item : deliveryOrderVOList) {
            item.setActivityFlag(CommonConstants.CHAR_Y);
        }

        deliveryOrderRepo.saveInBatch(BeanMapUtils.mapListTo(deliveryOrderVOList, DeliveryOrder.class));
    }

//     更新出库单(DeliveryOrder，DeliveryOrderItem)状态为出库完成
    public List<DeliveryOrderVO> doShippingCompletion(List<Long> deliveryOrderIds) {

        if (deliveryOrderIds == null || deliveryOrderIds.isEmpty()) {
            throw new BusinessCodedException("no deliveryOrderIds exist");
        }
        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);

        List<DeliveryOrderVO> deliveryOrderVOList = BeanMapUtils.mapListTo(deliveryOrderRepo.findByDeliveryOrderIdInAndDeliveryStatus(deliveryOrderIds, DeliveryStatus.ON_PICKING), DeliveryOrderVO.class);
        if (deliveryOrderVOList.isEmpty()) {
            throw new BusinessCodedException("no deliveryOrder exist");
        }

        List<DeliveryOrderItemVO> deliveryOrderItemVOList = BeanMapUtils.mapListTo(deliveryOrderItemRepo.findByDeliveryOrderIdIn(new HashSet<>(deliveryOrderIds)), DeliveryOrderItemVO.class);

        String siteId = deliveryOrderVOList.get(0).getSiteId();
        Set<Long> productIds = deliveryOrderItemVOList.stream().map(DeliveryOrderItemVO::getProductId).collect(Collectors.toSet());
        List<ProductCostVO> productCostVOList = BeanMapUtils.mapListTo(productCostRepo.findByProductIdInAndCostTypeAndSiteId(productIds, CostType.AVERAGE_COST, siteId), ProductCostVO.class);
        Map<String, ProductCostVO> productCostMap = productCostVOList.stream().collect(Collectors.toMap(v -> v.getProductId() + "#" + v.getSiteId(), Function.identity()));

        for (DeliveryOrderVO deliveryOrder : deliveryOrderVOList) {

            deliveryOrder.setDeliveryStatus(DeliveryStatus.DISPATCHED);
            deliveryOrder.setFinishDate(sysDate);
        }

        for (DeliveryOrderItemVO item : deliveryOrderItemVOList) {
            String key = item.getProductId() + "#" + item.getSiteId();
            item.setProductCost(productCostMap.containsKey(key)? productCostMap.get(key).getCost() : BigDecimal.ZERO);
        }

        deliveryOrderRepo.saveInBatch(BeanMapUtils.mapListTo(deliveryOrderVOList, DeliveryOrder.class));
        deliveryOrderItemRepo.saveInBatch(BeanMapUtils.mapListTo(deliveryOrderItemVOList, DeliveryOrderItem.class));

        return deliveryOrderVOList;
    }

//      更新出库单(DeliveryOrder，DeliveryOrderItem)状态为已打印发票
    public void doInvoicing(List<Long> deliveryOrderIds) {

        if (deliveryOrderIds == null || deliveryOrderIds.isEmpty()) {
            throw new BusinessCodedException("no deliveryOrderIds exist");
        }
//        List<DeliveryOrderVO> deliveryOrderVOList = BeanMapUtils.mapListTo(deliveryOrderRepo.findByDeliveryOrderIdInAndDeliveryStatus(deliveryOrderIds, DeliveryStatus.DISPATCHED), DeliveryOrderVO.class);
        for (Long deliveryId : deliveryOrderIds) {
            // 多用户校验当delivery_status<>S029DISPATCHED时报错
            DeliveryOrderVO deliveryOrder = BeanMapUtils.mapTo(deliveryOrderRepo.findByDeliveryOrderId(deliveryId), DeliveryOrderVO.class);
            if (!StringUtils.equals(deliveryOrder.getDeliveryStatus(), DeliveryStatus.DISPATCHED)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00314", new String[]{
                        CodedMessageUtils.getMessage("label.deliveryOrderNo")
                        , deliveryOrder.getDeliveryOrderNo()
                        , CodedMessageUtils.getMessage("menu.SPM0211_01") }));
            }

            deliveryOrder.setDeliveryStatus(DeliveryStatus.INVOICED);
            deliveryOrder.setActivityFlag(CommonConstants.CHAR_Y);

            deliveryOrderRepo.save(BeanMapUtils.mapTo(deliveryOrder, DeliveryOrder.class));
        }
    }

    //  为调拨更新出库单(DeliveryOrder)ActivityFlag为N
    public void doComplete(List<Long> deliveryOrderIds) {

        if (deliveryOrderIds == null || deliveryOrderIds.isEmpty()) {
            throw new BusinessCodedException("no deliveryOrderIds exist");
        }
        for (Long deliveryId : deliveryOrderIds) {
            // 多用户校验当delivery_status<>S029DISPATCHED时报错
            DeliveryOrderVO deliveryOrder = BeanMapUtils.mapTo(deliveryOrderRepo.findByDeliveryOrderId(deliveryId), DeliveryOrderVO.class);
            if (!StringUtils.equals(deliveryOrder.getDeliveryStatus(), DeliveryStatus.DISPATCHED)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00314", new String[]{
                        CodedMessageUtils.getMessage("label.deliveryOrderNo")
                        , deliveryOrder.getDeliveryOrderNo()
                        , CodedMessageUtils.getMessage("menu.SPM0211_01") }));
            }

            deliveryOrder.setActivityFlag(CommonConstants.CHAR_N);

            deliveryOrderRepo.save(BeanMapUtils.mapTo(deliveryOrder, DeliveryOrder.class));
        }
    }

    //  生成更新出库单状态为完成正在拣货

     public void doSalesReturn(DeliveryOrderVO deliveryOrder, Long stockPointId) {

         this.doRegister(deliveryOrder, stockPointId);
         deliveryOrder.setDeliveryStatus(DeliveryStatus.INVOICED);
         deliveryOrder.setActivityFlag(CommonConstants.CHAR_Y);

         deliveryOrderRepo.save(BeanMapUtils.mapTo(deliveryOrder, DeliveryOrder.class));
     }

//     生成更新出库单状态为正在拣货

    public void doRegister(DeliveryOrderVO deliveryOrder, Long stockPointId) {

        String deliveryNo = generateNoMgr.generateDeliveryNo(deliveryOrder.getSiteId(), stockPointId);

        List<OrganizationRelationVO> orgRelationVOList = BeanMapUtils.mapListTo(orgRelaRepo.findBySiteIdAndRelationType(deliveryOrder.getSiteId(), OrgRelationType.COMPANY.getCodeDbid()), OrganizationRelationVO.class);
        Long fromOrgId = orgRelationVOList.isEmpty()? -1 : orgRelationVOList.get(0).getFromOrganizationId();

        List<DeliveryOrderItemVO> deliveryOrderItemVOList = BeanMapUtils.mapListTo(deliveryOrderItemRepo.findByDeliveryOrderId(deliveryOrder.getDeliveryOrderId()), DeliveryOrderItemVO.class);

        BigDecimal totalDeliveryQty = deliveryOrderItemVOList.stream().map(item -> item.getDeliveryQty() != null ? item.getDeliveryQty() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmt = deliveryOrderItemVOList.stream().map(item -> item.getAmt() != null ? item.getAmt() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        deliveryOrder.setDeliveryOrderNo(deliveryNo);
        deliveryOrder.setDeliveryStatus(DeliveryStatus.ON_PICKING);
        deliveryOrder.setFromOrganizationId(fromOrgId);
        deliveryOrder.setActivityFlag(CommonConstants.CHAR_N);
        deliveryOrder.setTotalQty(totalDeliveryQty);
        deliveryOrder.setTotalAmt(totalAmt);
        deliveryOrderRepo.save(BeanMapUtils.mapTo(deliveryOrder, DeliveryOrder.class));
    }

    /**
     * 单店零售创建发货单
     *
     * @param salesOrderVO 销售订单信息
     * @return 创建的发货单信息
     */
    public DeliveryOrderVO doUnitRetailCreateDeliveryOrder(SalesOrderVO salesOrderVO, SalesOrderItemVO salesOrderItemVO, DeliveryOrderVO deliveryOrderVO, DeliveryOrderItemVO deliveryOrderItemVO){

        if (salesOrderVO == null || salesOrderVO.getSalesOrderId() == null) {
            log.error("输入的销售订单信息无效");
            throw new PJCustomException("销售订单信息不可为空");
        }

        this.createDeliveryOrderVO(salesOrderVO, deliveryOrderVO);
        this.doUnitRetailCreateDeliveryOrder(deliveryOrderVO, salesOrderVO, salesOrderItemVO, deliveryOrderItemVO);

        this.doUnitRetailCreateDeliverySerializedItem(salesOrderItemVO, deliveryOrderItemVO);
        log.info("deliveryOrderItemVO创建成功");

        return deliveryOrderVO;
    }

    private void createDeliveryOrderVO(SalesOrderVO salesOrderVO, DeliveryOrderVO deliveryOrderVO) {

        OrganizationRelation orgRelation = orgRelaRepo.findByRelationTypeAndSiteId(OrgRelationType.COMPANY.getCodeDbid(), salesOrderVO.getSiteId());

        deliveryOrderVO.setDeliveryOrderNo(generateNoMgr.generateDeliveryNo(salesOrderVO.getSiteId(), salesOrderVO.getFacilityId()));
        deliveryOrderVO.setInventoryTransactionType(PJConstants.InventoryTransactionType.SALESTOCKOUT.getCodeDbid());
        deliveryOrderVO.setDeliveryStatus(MstCodeConstants.DeliveryStatus.SHIPPING_INSTRUCTION);
        deliveryOrderVO.setFromOrganizationId(orgRelation.getFromOrganizationId());
        deliveryOrderVO.setFromFacilityId(salesOrderVO.getFacilityId());
        deliveryOrderVO.setToConsumerId(salesOrderVO.getCmmConsumerId());
        deliveryOrderVO.setTotalQty(salesOrderVO.getTotalActualQty());
        deliveryOrderVO.setTotalAmt(salesOrderVO.getTotalActualAmt());
        deliveryOrderVO.setTotalAmtNotVat(salesOrderVO.getTotalActualAmtNotVat());
        deliveryOrderVO.setActivityFlag(CommonConstants.CHAR_N);
        deliveryOrderVO.setDeliveryOrderDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
        deliveryOrderVO.setConsigneeId(salesOrderVO.getConsigneeId());
        deliveryOrderVO.setProductClassification(PJConstants.ProductClsType.GOODS.getCodeDbid());
        deliveryOrderVO.setOrderSourceType(PJConstants.ProductClsType.GOODS.getCodeDbid());
        deliveryOrderVO.setOrderToType(salesOrderVO.getOrderToType());
        deliveryOrderVO.setCustomerId(salesOrderVO.getCustomerId());
        deliveryOrderVO.setCustomerNm(salesOrderVO.getCustomerNm());
        deliveryOrderVO.setConsigneePerson(salesOrderVO.getConsigneePerson());
        deliveryOrderVO.setConsigneeMobilePhone(salesOrderVO.getConsigneeMobilePhone());
        deliveryOrderVO.setConsigneeAddr(salesOrderVO.getConsigneeAddr());
        deliveryOrderVO.setCmmConsumerId(salesOrderVO.getCmmConsumerId());
        deliveryOrderVO.setConsumerVipNo(salesOrderVO.getConsumerVipNo());
        deliveryOrderVO.setConsumerNmFirst(salesOrderVO.getConsumerNmFirst());
        deliveryOrderVO.setConsumerNmMiddle(salesOrderVO.getConsumerNmMiddle());
        deliveryOrderVO.setConsumerNmLast(salesOrderVO.getConsumerNmLast());
        deliveryOrderVO.setConsumerNmFull(salesOrderVO.getConsumerNmFull());
        deliveryOrderVO.setEmail(salesOrderVO.getEmail());
        deliveryOrderVO.setMobilePhone(salesOrderVO.getMobilePhone());
        deliveryOrderVO.setEntryPicId(salesOrderVO.getEntryPicId());
        deliveryOrderVO.setEntryPicNm(salesOrderVO.getEntryPicNm());
        deliveryOrderVO.setCustomerCd(salesOrderVO.getCustomerCd());
        deliveryOrderVO.setSiteId(salesOrderVO.getSiteId());
    }

    /**
     * 根据单位零售创建发货单
     * 此方法主要负责根据销售订单和销售订单项创建发货单明细
     * 它将检索产品的成本信息，并创建一个发货单明细对象
     * 最后，这个发货单明细对象会被保存到数据库中
     *
     */
    public void doUnitRetailCreateDeliveryOrder(DeliveryOrderVO deliveryOrderVO, SalesOrderVO salesOrderVO, SalesOrderItemVO salesOrderItemVO, DeliveryOrderItemVO deliveryOrderItemVO){

        ProductCost productCost = productCostRepo
                .findBySiteIdAndProductIdAndCostTypeAndProductClassification(salesOrderItemVO.getSiteId()
                        , salesOrderItemVO.getProductId()
                        , CostType.RECEIVE_COST
                        , PJConstants.ProductClsType.GOODS.getCodeDbid());
        
        BigDecimal cost = null == productCost ? BigDecimal.ZERO : productCost.getCost();

        this.createDeliveryOrderItemVO(salesOrderItemVO, cost, deliveryOrderVO.getDeliveryOrderId(), salesOrderVO.getOrderNo(), deliveryOrderItemVO);
    }

    private void createDeliveryOrderItemVO(SalesOrderItemVO salesOrderItemVO, BigDecimal cost, Long deliveryOrderId, String salesOrderNo, DeliveryOrderItemVO deliveryOrderItemVO) {

        deliveryOrderItemVO.setDeliveryOrderId(deliveryOrderId);
        deliveryOrderItemVO.setProductClassification(PJConstants.ProductClsType.GOODS.getCodeDbid());
        deliveryOrderItemVO.setSeqNo(1);
        deliveryOrderItemVO.setProductId(salesOrderItemVO.getProductId());
        deliveryOrderItemVO.setProductCd(salesOrderItemVO.getProductCd());
        deliveryOrderItemVO.setProductNm(salesOrderItemVO.getProductNm());
        deliveryOrderItemVO.setOriginalDeliveryQty(salesOrderItemVO.getActualQty());
        deliveryOrderItemVO.setDeliveryQty(salesOrderItemVO.getActualQty());
        deliveryOrderItemVO.setProductCost(cost);
        deliveryOrderItemVO.setSellingPrice(salesOrderItemVO.getSellingPrice());
        deliveryOrderItemVO.setAmt(salesOrderItemVO.getActualAmt());
        deliveryOrderItemVO.setOrderItemId(salesOrderItemVO.getSalesOrderItemId());
        deliveryOrderItemVO.setSalesOrderId(salesOrderItemVO.getSalesOrderId());
        deliveryOrderItemVO.setSalesOrderNo(salesOrderNo);
        deliveryOrderItemVO.setSellingPriceNotVat(salesOrderItemVO.getSellingPriceNotVat());
        deliveryOrderItemVO.setTaxRate(salesOrderItemVO.getTaxRate());
        deliveryOrderItemVO.setAmtNotVat(salesOrderItemVO.getActualAmtNotVat());
        deliveryOrderItemVO.setStandardPrice(salesOrderItemVO.getStandardPrice());
        deliveryOrderItemVO.setSiteId(salesOrderItemVO.getSiteId());

    }

    /**
     * 根据销售订单项和配送订单项创建配送序列化项
     *
     */
    public DeliverySerializedItemVO doUnitRetailCreateDeliverySerializedItem(SalesOrderItemVO salesOrderItemVO, DeliveryOrderItemVO deliveryOrderItemVO){

        List<OrderSerializedItem> orderSerializedItemList = orderSerializedItemRepository.findByOrderItemId(salesOrderItemVO.getSalesOrderItemId());

        DeliverySerializedItemVO deliverySerializedItemVO = DeliverySerializedItemVO.create();
        deliverySerializedItemVO.setSerializedProductId(salesOrderItemVO.getProductId());
        deliverySerializedItemVO.setOrderSerializedItemId(orderSerializedItemList.get(0).getOrderSerializedItemId());
        deliverySerializedItemVO.setDeliveryOrderItemId(deliveryOrderItemVO.getDeliveryOrderItemId());
        deliverySerializedItemVO.setDeliveryOrderId(deliveryOrderItemVO.getDeliveryOrderId());
        deliverySerializedItemVO.setOutCost(deliveryOrderItemVO.getProductCost());
        deliverySerializedItemVO.setSiteId(salesOrderItemVO.getSiteId());

        deliverySerializedItemRepository.save(BeanMapUtils.mapTo(deliverySerializedItemVO, DeliverySerializedItem.class));
        return deliverySerializedItemVO;
    }

    /**
     * 处理单元零售发货完成状态更新
     * <p>
     * 此方法用于更新配送订单的状态，标识发货已完成它通过设置配送状态、活动标志和完成日期来实现
     *
     * @param deliveryOrderVO 配送订单信息的视图对象，包含需要更新的状态信息
     */
    public void doUnitRetailShippingCompletion(DeliveryOrderVO deliveryOrderVO){

        deliveryOrderVO.setDeliveryStatus(DeliveryStatus.SHIPPING_COMPLETION);
        deliveryOrderVO.setActivityFlag(CommonConstants.CHAR_Y);
        deliveryOrderVO.setFinishDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));

    }
}