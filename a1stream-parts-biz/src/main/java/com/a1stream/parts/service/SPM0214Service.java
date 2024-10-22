package com.a1stream.parts.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.SalesOrderStatus;
import com.a1stream.common.constants.PJConstants.DropShipType;
import com.a1stream.common.constants.PJConstants.OrgRelationType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.ReturnRequestStatus;
import com.a1stream.common.constants.PJConstants.ReturnRequestType;
import com.a1stream.common.constants.PJConstants.SalesOrderPriorityType;
import com.a1stream.common.manager.DeliveryOrderManager;
import com.a1stream.common.manager.GenerateNoManager;
import com.a1stream.common.manager.PartsSalesStockAllocationManager;
import com.a1stream.common.manager.PickingInstructionManager;
import com.a1stream.common.manager.SalesOrderManager;
import com.a1stream.common.model.PartsInfoBO;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.parts.SPM021401BO;
import com.a1stream.domain.bo.parts.SPM021402BO;
import com.a1stream.domain.entity.ReturnRequestItem;
import com.a1stream.domain.entity.ReturnRequestList;
import com.a1stream.domain.entity.SalesOrder;
import com.a1stream.domain.entity.SalesOrderItem;
import com.a1stream.domain.form.parts.SPM021401Form;
import com.a1stream.domain.form.parts.SPM021402Form;
import com.a1stream.domain.repository.DeliveryOrderItemRepository;
import com.a1stream.domain.repository.DeliveryOrderRepository;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstOrganizationRepository;
import com.a1stream.domain.repository.PartsRopqMonthlyRepository;
import com.a1stream.domain.repository.ProductCostRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.ProductTaxRepository;
import com.a1stream.domain.repository.ReorderGuidelineRepository;
import com.a1stream.domain.repository.ReturnRequestItemRepository;
import com.a1stream.domain.repository.ReturnRequestListRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstOrganizationVO;
import com.a1stream.domain.vo.PartsRopqMonthlyVO;
import com.a1stream.domain.vo.ProductCostVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.ReorderGuidelineVO;
import com.a1stream.domain.vo.ReturnRequestItemVO;
import com.a1stream.domain.vo.ReturnRequestListVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;

import jakarta.annotation.Resource;
/**
* 功能描述:
*
* MID2303
* 2024年6月24日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/24   Ruan Hansheng     New
*/
@Service
public class SPM0214Service {

    @Resource
    private ReturnRequestListRepository returnRequestListRepository;

    @Resource
    private ReturnRequestItemRepository returnRequestItemRepository;

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    @Resource
    private ProductCostRepository productCostRepository;

    @Resource
    private ProductInventoryRepository productInventoryRepository;

    @Resource
    private ProductStockStatusRepository productStockStatusRepository;

    @Resource
    private ReorderGuidelineRepository reorderGuidelineRepository;

    @Resource
    private PartsRopqMonthlyRepository partsRopqMonthlyRepository;

    @Resource
    private GenerateNoManager generateNoManager;

    @Resource
    private MstOrganizationRepository mstOrganizationRepository;

    @Resource
    private ProductTaxRepository productTaxRepository;

    @Resource
    private SalesOrderRepository salesOrderRepository;

    @Resource
    private SalesOrderItemRepository salesOrderItemRepository;

    @Resource
    private PartsSalesStockAllocationManager partsSalesStockAllocationManager;

    @Resource
    private DeliveryOrderManager deliveryOrderManager;

    @Resource
    private DeliveryOrderRepository deliveryOrderRepository;

    @Resource
    private DeliveryOrderItemRepository deliveryOrderItemRepository;

    @Resource
    private PickingInstructionManager pickingInstructionManager;

    @Resource
    private SalesOrderManager salesOrderManager;

    @Resource
    private LocationRepository locationRepository;

    public List<SPM021401BO> getReturnRequestListList(SPM021401Form form, PJUserDetails uc) {

        return returnRequestListRepository.getReturnRequestListList(form, uc);
    }

    public SPM021401BO getReturnRequestList(SPM021402Form form, PJUserDetails uc) {

        return returnRequestListRepository.getReturnRequestList(form, uc);
    }

    public List<SPM021402BO> getReturnRequestItemList(SPM021402Form form, PJUserDetails uc) {

        return returnRequestItemRepository.getReturnRequestItemList(form, uc);
    }

    public MstFacilityVO getMstFacilityVO(Long pointId) {

        return BeanMapUtils.mapTo(mstFacilityRepository.findByFacilityId(pointId), MstFacilityVO.class);
    }

    public List<ProductCostVO> getProductCostVOList(List<Long> productIdList, String costType, List<String> siteIdList) {

        return BeanMapUtils.mapListTo(productCostRepository.findByProductIdInAndCostTypeAndSiteIdIn(productIdList, costType, siteIdList), ProductCostVO.class);
    }

    public List<ProductInventoryVO> getProductInventoryVOList(String siteId, Long facilityId, List<Long> productIdList) {

        return BeanMapUtils.mapListTo(productInventoryRepository.findBySiteIdAndFacilityIdAndProductIdIn(siteId, facilityId, productIdList), ProductInventoryVO.class);
    }

    public List<ProductStockStatusVO> getProductStockStatusVOList(String siteId, Long facilityId, List<Long> productIdList) {

        return BeanMapUtils.mapListTo(productStockStatusRepository.findBySiteIdAndFacilityIdAndProductIdIn(siteId, facilityId, productIdList), ProductStockStatusVO.class);
    }

    public List<ReorderGuidelineVO> getGuidelineVOList(String siteId, Long facilityId, List<Long> productIdList) {

        return BeanMapUtils.mapListTo(reorderGuidelineRepository.findBySiteIdAndFacilityIdAndProductIdIn(siteId, facilityId, productIdList), ReorderGuidelineVO.class);
    }

    public List<PartsRopqMonthlyVO> getPartsRopqMonthlyVOList(String siteId, List<Long> productIdList, String ropqType) {

        return BeanMapUtils.mapListTo(partsRopqMonthlyRepository.findBySiteIdAndProductIdInAndRopqType(siteId, productIdList, ropqType), PartsRopqMonthlyVO.class);
    }

    public List<ReturnRequestItemVO> getReturnRequestItemVOList(List<Long> returnRequestItemIdList) {

        return BeanMapUtils.mapListTo(returnRequestItemRepository.findByReturnRequestItemIdIn(returnRequestItemIdList), ReturnRequestItemVO.class);
    }

    public void confirm(List<ReturnRequestItemVO> returnRequestItemVOList) {
        
        returnRequestItemRepository.saveInBatch(BeanMapUtils.mapListTo(returnRequestItemVOList, ReturnRequestItem.class));
    }

    public ReturnRequestListVO getReturnRequestListVO(Long returnRequestListId) {

        return BeanMapUtils.mapTo(returnRequestListRepository.findByReturnRequestListId(returnRequestListId), ReturnRequestListVO.class);
    }

    public void issue(List<ReturnRequestItemVO> returnRequestItemVOList, ReturnRequestListVO returnRequestListVO) {
        
        returnRequestItemRepository.saveInBatch(BeanMapUtils.mapListTo(returnRequestItemVOList, ReturnRequestItem.class));

        if (null != returnRequestListVO) {
            returnRequestListRepository.save(BeanMapUtils.mapTo(returnRequestListVO, ReturnRequestList.class));
        }
    }

    public String salesOrderNo(String siteId, Long pointId) {
        
        return generateNoManager.generateNonSerializedItemSalesOrderNo(siteId, pointId);
    }

    public MstOrganizationVO getPartSupplier(String siteId) {

        return mstOrganizationRepository.getPartSupplier(siteId);
    }

    public ReturnRequestListVO getReturnRequestListVO(Long returnRequestListId, String requestStatus, String siteId) {

        return BeanMapUtils.mapTo(returnRequestListRepository.findByReturnRequestListIdAndRequestStatusNotAndSiteId(returnRequestListId, requestStatus, siteId), ReturnRequestListVO.class);
    }

    public String deliveryOrderNo(String siteId, Long pointId) {
        
        return generateNoManager.generateDeliveryNo(siteId, pointId);
    }

    public MstOrganizationVO getMstOrganizationVO(String siteId, String organizationCd) {

        return BeanMapUtils.mapTo(mstOrganizationRepository.findBySiteIdAndOrganizationCd(siteId, organizationCd), MstOrganizationVO.class);
    }

    public List<PartsInfoBO> findProductTaxList(List<Long> productIdList) {

        return BeanMapUtils.mapListTo(productTaxRepository.findProductTaxList(productIdList), PartsInfoBO.class);
    }

    public SPM021402Form picking(SPM021402Form form, PJUserDetails uc) {
        
        if (ReturnRequestType.KEY_SCRAP.getCodeDbid().equals(form.getSelectPicking())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.W.10259"));
        }

        List<SPM021402BO> tableDataList = form.getAllTableDataList();
        BigDecimal totalApproveQty = tableDataList.stream().map(SPM021402BO::getApproveQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalApproveQty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.W.10259"));
        }
        
        List<Long> returnRequestItemIdList = tableDataList.stream().map(SPM021402BO::getReturnRequestItemId).collect(Collectors.toList());
        List<ReturnRequestItemVO> returnRequestItemVOList = this.getReturnRequestItemVOList(returnRequestItemIdList);
        Map<Long, ReturnRequestItemVO> returnRequestItemVOMap = returnRequestItemVOList.stream().collect(Collectors.toMap(ReturnRequestItemVO::getReturnRequestItemId, Function.identity(), (existingValue, newValue) -> newValue));
        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);
        String siteId = uc.getDealerCode();
        Long pointId = form.getPointId();

        // salesOrder
        SalesOrderVO salesOrderVO = SalesOrderVO.create();
        salesOrderVO.setSiteId(siteId);
        salesOrderVO.setOrderNo(this.salesOrderNo(siteId, pointId));
        salesOrderVO.setOrderDate(sysDate);
        salesOrderVO.setDeliveryPlanDate(sysDate);
        salesOrderVO.setShipDate(sysDate);
        salesOrderVO.setAllocateDueDate(sysDate);
        salesOrderVO.setOrderStatus(SalesOrderStatus.SP_CREATED);
        salesOrderVO.setOrderToType(OrgRelationType.CONSUMER.getCodeDbid() + CommonConstants.CHAR_SPACE + OrgRelationType.SUPPLIER.getCodeDbid());
        salesOrderVO.setProductClassification(ProductClsType.PART.getCodeDbid());
        salesOrderVO.setOrderPriorityType(SalesOrderPriorityType.SORO.getCodeDbid());
        salesOrderVO.setOrderSourceType(ProductClsType.PART.getCodeDbid());
        salesOrderVO.setEntryFacilityId(pointId);
        salesOrderVO.setFacilityId(pointId);
        salesOrderVO.setCustomerIfFlag(CommonConstants.CHAR_N);
        salesOrderVO.setDropShipType(DropShipType.NOTDROPSHIP);
        salesOrderVO.setBoCancelFlag(CommonConstants.CHAR_Y);
        salesOrderVO.setShipCompleteFlag(CommonConstants.CHAR_Y);
        salesOrderVO.setDemandExceptionFlag(CommonConstants.CHAR_Y);

        MstOrganizationVO mstOrganizationVO = this.getPartSupplier(siteId);

        salesOrderVO.setCustomerId(mstOrganizationVO.getOrganizationId());
        salesOrderVO.setCustomerNm(mstOrganizationVO.getOrganizationNm());
        salesOrderVO.setEntryPicId(uc.getPersonId());
        salesOrderVO.setEntryPicNm(uc.getPersonName());
        salesOrderVO.setInvoicePrintFlag(CommonConstants.CHAR_N);

        Integer seqNo = 0;
        List<SalesOrderItemVO> salesOrderItemVOList = new ArrayList<>();
        List<Long> productIdList = tableDataList.stream().map(SPM021402BO::getPartsId).collect(Collectors.toList());
        List<PartsInfoBO> productTaxList= this.findProductTaxList(productIdList);
        Map<Long, PartsInfoBO> productTaxMap = productTaxList.stream().collect(Collectors.toMap(PartsInfoBO::getPartsId, Function.identity(), (existingValue, newValue) -> newValue));
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmt = BigDecimal.ZERO;
        BigDecimal totalActualQty = BigDecimal.ZERO;
        BigDecimal totalActualAmt = BigDecimal.ZERO;
        for (SPM021402BO bo : tableDataList) {
            // salesOrderItem
            SalesOrderItemVO salesOrderItemVO = SalesOrderItemVO.create(siteId, salesOrderVO.getSalesOrderId());
            salesOrderItemVO.setSiteId(siteId);
            salesOrderItemVO.setProductId(bo.getPartsId());
            salesOrderItemVO.setProductCd(bo.getPartsNo());
            salesOrderItemVO.setProductNm(bo.getPartsNm());
            salesOrderItemVO.setStandardPrice(bo.getReturnPrice());
            salesOrderItemVO.setSpecialPrice(bo.getReturnPrice());
            salesOrderItemVO.setSellingPrice(bo.getReturnPrice());

            PartsInfoBO partsInfoBO = productTaxMap.get(bo.getPartsId());
            salesOrderItemVO.setTaxRate(null != partsInfoBO ? partsInfoBO.getTaxRate() : BigDecimal.TEN);

            salesOrderItemVO.setSellingPriceNotVat(BigDecimal.ZERO);
            salesOrderItemVO.setProductClassification(ProductClsType.PART.getCodeDbid());
            salesOrderItemVO.setOrderQty(bo.getRequestQty());
            salesOrderItemVO.setActualQty(bo.getRequestQty());
            salesOrderItemVO.setWaitingAllocateQty(bo.getRequestQty());
            salesOrderItemVO.setAllocatedQty(BigDecimal.ZERO);
            salesOrderItemVO.setInstructionQty(BigDecimal.ZERO);
            salesOrderItemVO.setShipmentQty(BigDecimal.ZERO);
            salesOrderItemVO.setBoQty(BigDecimal.ZERO);
            salesOrderItemVO.setCancelQty(BigDecimal.ZERO);
            salesOrderItemVO.setAllocatedProductId(bo.getPartsId());
            salesOrderItemVO.setAllocatedProductCd(bo.getPartsNo());
            salesOrderItemVO.setAllocatedProductNm(bo.getPartsNm());
            salesOrderItemVO.setSeqNo(++seqNo);
            salesOrderItemVO.setOrderPrioritySeq(CommonConstants.INTEGER_FIVE);
            salesOrderItemVO.setBoCancelFlag(CommonConstants.CHAR_Y);

            salesOrderItemVOList.add(salesOrderItemVO);
            bo.setSalesOrderItemId(salesOrderItemVO.getSalesOrderItemId());

            totalQty = totalQty.add(salesOrderItemVO.getAllocatedQty());
            totalAmt = totalAmt.add(salesOrderItemVO.getAllocatedQty().multiply(salesOrderItemVO.getSellingPrice()));
            totalActualQty = totalActualQty.add(salesOrderItemVO.getActualQty());
            totalActualAmt = totalActualAmt.add(salesOrderItemVO.getActualQty().multiply(salesOrderItemVO.getSellingPrice()));

            ReturnRequestItemVO returnRequestItemVO = returnRequestItemVOMap.get(bo.getReturnRequestItemId());
            if (!bo.getUpdateCount().equals(returnRequestItemVO.getUpdateCount())) {
                throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[] {ComUtil.t("label.partsNo"), bo.getPartsNo(), ComUtil.t("title.partsReturnRequest_02")}));
            }
            returnRequestItemVO.setSalesOrderItemId(salesOrderVO.getSalesOrderId());
            returnRequestItemVO.setSalesOrderItemId(salesOrderItemVO.getSalesOrderItemId());
            returnRequestItemVO.setRequestStatus(ReturnRequestStatus.COMPLETED.getCodeDbid());
        }

        salesOrderVO.setTotalQty(totalQty);
        salesOrderVO.setTotalAmt(totalAmt);
        salesOrderVO.setTotalActualQty(totalActualQty);
        salesOrderVO.setTotalActualAmt(totalActualAmt);

        // 调用共通
        salesOrderRepository.save(BeanMapUtils.mapTo(salesOrderVO, SalesOrder.class));
        salesOrderItemRepository.saveInBatch(BeanMapUtils.mapListTo(salesOrderItemVOList, SalesOrderItem.class));
        salesOrderVO = BeanMapUtils.mapTo(salesOrderRepository.findBySalesOrderId(salesOrderVO.getSalesOrderId()),SalesOrderVO.class);
        salesOrderItemVOList = BeanMapUtils.mapListTo(salesOrderItemRepository.findBySalesOrderId(salesOrderVO.getSalesOrderId()), SalesOrderItemVO.class);
        
        partsSalesStockAllocationManager.executeStockAllocation(salesOrderVO, salesOrderItemVOList);

        // returnRequestList
        ReturnRequestListVO returnRequestListVO = this.getReturnRequestListVO(form.getReturnRequestListId());
        returnRequestListVO.setRequestStatus(null == this.getReturnRequestListVO(form.getReturnRequestListId(), ReturnRequestStatus.COMPLETED.getCodeDbid(), siteId) ? ReturnRequestStatus.COMPLETED.getCodeDbid() : ReturnRequestStatus.ONPICKING.getCodeDbid());

        Map<Long, SalesOrderItemVO> salesOrderItemVOMap = salesOrderItemVOList.stream().collect(Collectors.toMap(SalesOrderItemVO::getSalesOrderItemId, Function.identity(), (existingValue, newValue) -> newValue));
        for (SPM021402BO bo : tableDataList) {
            // salesOrderItem
            SalesOrderItemVO salesOrderItemVO = salesOrderItemVOMap.get(bo.getSalesOrderItemId());

            salesOrderItemVO.setAllocatedQty(salesOrderItemVO.getAllocatedQty().add(bo.getRequestQty()));
        }
        // 调用共通
        salesOrderManager.doPickingForSalesOrder(salesOrderVO, salesOrderItemVOList);
        
        returnRequestListRepository.save(BeanMapUtils.mapTo(returnRequestListVO, ReturnRequestList.class));
        returnRequestItemRepository.saveInBatch(BeanMapUtils.mapListTo(returnRequestItemVOList, ReturnRequestItem.class));

        List<DeliveryOrderItemVO> deliveryOrderItemVOList = BeanMapUtils.mapListTo(deliveryOrderItemRepository.findBySalesOrderId(salesOrderVO.getSalesOrderId()), DeliveryOrderItemVO.class);
        form.setDeliveryOrderId(deliveryOrderItemVOList.get(0).getDeliveryOrderId());

        return form;
    }

    public List<LocationVO> getLocationVOList(Set<Long> locationIdSet) {

        return BeanMapUtils.mapListTo(locationRepository.findByLocationIdIn(locationIdSet), LocationVO.class);
    }
}
