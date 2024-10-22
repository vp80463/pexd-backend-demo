package com.a1stream.parts.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.bo.SpManifestItemBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.InOutType;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.constants.PJConstants.LocationType;
import com.a1stream.common.constants.PJConstants.ManifestItemStatus;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.ProductStockStatusType;
import com.a1stream.common.constants.PJConstants.ReceiptSlipStatus;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.common.manager.CallNewIfsManager;
import com.a1stream.common.manager.CostManager;
import com.a1stream.common.manager.GenerateNoManager;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.common.manager.ProductManager;
import com.a1stream.common.manager.PurchaseOrderManager;
import com.a1stream.common.manager.ReceiptSlipManager;
import com.a1stream.common.model.BaseInfResponse;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.parts.PartsStoringListForFinancePrintBO;
import com.a1stream.domain.bo.parts.PartsStoringListForFinancePrintDetailBO;
import com.a1stream.domain.bo.parts.SPM030101BO;
import com.a1stream.domain.bo.parts.SPM030102BO;
import com.a1stream.domain.entity.InventoryTransaction;
import com.a1stream.domain.entity.ReceiptManifest;
import com.a1stream.domain.entity.ReceiptManifestItem;
import com.a1stream.domain.entity.ReceiptPoItemRelation;
import com.a1stream.domain.entity.ReceiptSlip;
import com.a1stream.domain.entity.ReceiptSlipItem;
import com.a1stream.domain.entity.StoringLine;
import com.a1stream.domain.entity.StoringLineItem;
import com.a1stream.domain.entity.StoringList;
import com.a1stream.domain.form.parts.SPM030101Form;
import com.a1stream.domain.form.parts.SPM030102Form;
import com.a1stream.domain.repository.InventoryTransactionRepository;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.ProductCostRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.PurchaseOrderItemRepository;
import com.a1stream.domain.repository.PurchaseOrderRepository;
import com.a1stream.domain.repository.ReceiptManifestItemRepository;
import com.a1stream.domain.repository.ReceiptManifestRepository;
import com.a1stream.domain.repository.ReceiptPoItemRelationRepository;
import com.a1stream.domain.repository.ReceiptSlipItemRepository;
import com.a1stream.domain.repository.ReceiptSlipRepository;
import com.a1stream.domain.repository.StoringLineItemRepository;
import com.a1stream.domain.repository.StoringLineRepository;
import com.a1stream.domain.repository.StoringListRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.InventoryTransactionVO;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductCostVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.PurchaseOrderItemVO;
import com.a1stream.domain.vo.PurchaseOrderVO;
import com.a1stream.domain.vo.ReceiptManifestItemVO;
import com.a1stream.domain.vo.ReceiptManifestVO;
import com.a1stream.domain.vo.ReceiptPoItemRelationVO;
import com.a1stream.domain.vo.ReceiptSlipItemVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.domain.vo.StoringLineItemVO;
import com.a1stream.domain.vo.StoringLineVO;
import com.a1stream.domain.vo.StoringListVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.CollectionUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;
/**
* 功能描述:
*
* MID2303
* 2024年6月14日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/14   Ruan Hansheng     New
*/
@Service
public class SPM0301Service {

    @Resource
    private ReceiptManifestRepository receiptManifestRepository;

    @Resource
    private ReceiptManifestItemRepository receiptManifestItemRepository;

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private ProductStockStatusRepository productStockStatusRepository;

    @Resource
    private LocationRepository locationRepository;

    @Resource
    private ProductInventoryRepository productInventoryRepository;

    @Resource
    private PurchaseOrderRepository purchaseOrderRepository;

    @Resource
    ProductCostRepository productCostRepository;

    @Resource
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Resource
    private ReceiptSlipRepository receiptSlipRepository;

    @Resource
    private StoringListRepository storingListRepository;

    @Resource
    private ReceiptSlipItemRepository receiptSlipItemRepository;

    @Resource
    private StoringLineRepository storingLineRepository;

    @Resource
    private StoringLineItemRepository storingLineItemRepository;

    @Resource
    private GenerateNoManager generateNoManager;

    @Resource
    private InventoryManager inventoryManager;

    @Resource
    private CostManager costManager;

    @Resource
    private CallNewIfsManager callNewIfsManager;

    @Resource
    private PurchaseOrderManager purchaseOrderManager;

    @Resource
    private ReceiptSlipManager receiptSlipManager;

    @Resource
    private ProductManager productManager;

    @Resource
    private ReceiptPoItemRelationRepository relationRepository;

    @Resource
    private InventoryTransactionRepository inventoryTransactionRepository;

    @Resource
    SystemParameterRepository systemParameterRepository;
    
    @Value("${ifs.request.url}")
    private String ifsRequestUrl;

    public SystemParameterVO getSystemParameter(String paramCode) {

        return BeanMapUtils.mapTo(systemParameterRepository.findBySystemParameterTypeId(paramCode), SystemParameterVO.class);
    }

    public void doManifestImports(List<SpManifestItemBO> items){

        receiptSlipManager.doManifestImports(items);
    }

    public List<SPM030101BO> getReceiptManifestList(SPM030101Form form, PJUserDetails uc) {

        return receiptManifestRepository.getReceiptManifestList(form, uc);
    }

    public List<ReceiptManifestVO> getReceiptManifestVOList(Set<Long> receiptManifestIdSet) {

        return BeanMapUtils.mapListTo(receiptManifestRepository.findByReceiptManifestIdIn(receiptManifestIdSet), ReceiptManifestVO.class);
    }

    public List<ReceiptManifestItemVO> getReceiptManifestItemVOList(List<String> caseNoList) {

        return BeanMapUtils.mapListTo(receiptManifestItemRepository.findByCaseNoIn(caseNoList), ReceiptManifestItemVO.class);
    }

    public List<MstProductVO> getMstProductVOList(Set<Long> productIdSet) {

        return BeanMapUtils.mapListTo(mstProductRepository.findByProductIdIn(productIdSet), MstProductVO.class);
    }

    public List<ProductStockStatusVO> getProductStockStatusVOList(String siteId, Long facilityId, Set<Long> productIdSet, String productClassification, Set<String> productStockStatusTypes) {

        return BeanMapUtils.mapListTo(productStockStatusRepository.findStockStatusList(siteId, facilityId, productIdSet, productClassification, productStockStatusTypes), ProductStockStatusVO.class);
    }

    public LocationVO getLocationVO(String siteId, Long facilityId, String locationType) {

        return BeanMapUtils.mapTo(locationRepository.getLocationVO(siteId, facilityId, locationType), LocationVO.class);
    }

    public List<SPM030101BO> getProductInventoryList(String siteId, Long facilityId, Set<Long> productIdSet, String productClassification, String primaryFlag,String locationType) {

        return productInventoryRepository.getProductInventoryList(siteId, facilityId, productIdSet, productClassification, primaryFlag,locationType);
    }

    public List<PurchaseOrderVO> getPurchaseOrderVOList(String siteId, Set<String> purchaseOrderNoSet) {

        return BeanMapUtils.mapListTo(purchaseOrderRepository.findBySiteIdAndOrderNoIn(siteId, purchaseOrderNoSet), PurchaseOrderVO.class);
    }

    public List<PurchaseOrderItemVO> getPurchaseOrderItemVOList(String siteId, Set<Long> purchaseOrderIdSet) {

        return BeanMapUtils.mapListTo(purchaseOrderItemRepository.findBySiteIdAndPurchaseOrderIdIn(siteId, purchaseOrderIdSet), PurchaseOrderItemVO.class);
    }

    public void confirmReceiptManifest(List<ReceiptManifestVO> receiptManifestVOList, List<ReceiptSlipVO> receiptSlipVOList, List<StoringListVO> storingListVOList, List<ReceiptSlipItemVO> receiptSlipItemVOList, List<ReceiptPoItemRelationVO> receiptPoItemRelationVOList, List<StoringLineVO> storingLineVOList, List<StoringLineItemVO> storingLineItemVOList, List<ReceiptManifestItemVO> manifestItemVOs, List<InventoryTransactionVO> inventoryTransactionVOs) {

        receiptManifestRepository.saveInBatch(BeanMapUtils.mapListTo(receiptManifestVOList, ReceiptManifest.class));

        receiptManifestItemRepository.saveInBatch(BeanMapUtils.mapListTo(manifestItemVOs, ReceiptManifestItem.class));

        receiptSlipRepository.saveInBatch(BeanMapUtils.mapListTo(receiptSlipVOList, ReceiptSlip.class));

        storingListRepository.saveInBatch(BeanMapUtils.mapListTo(storingListVOList, StoringList.class));

        receiptSlipItemRepository.saveInBatch(BeanMapUtils.mapListTo(receiptSlipItemVOList, ReceiptSlipItem.class));

        relationRepository.saveInBatch(BeanMapUtils.mapListTo(receiptPoItemRelationVOList, ReceiptPoItemRelation.class));

        storingLineRepository.saveInBatch(BeanMapUtils.mapListTo(storingLineVOList, StoringLine.class));

        storingLineItemRepository.saveInBatch(BeanMapUtils.mapListTo(storingLineItemVOList, StoringLineItem.class));

        inventoryTransactionRepository.saveInBatch(BeanMapUtils.mapListTo(inventoryTransactionVOs, InventoryTransaction.class));

    }

    public List<SPM030102BO> getReceiptManifestItemList(SPM030102Form form, PJUserDetails uc) {

        return receiptManifestItemRepository.getReceiptManifestItemList(form, uc);
    }

    public ReceiptManifestVO getReceiptManifestVO(Long receiptManifestId) {

        return BeanMapUtils.mapTo(receiptManifestRepository.findByReceiptManifestId(receiptManifestId), ReceiptManifestVO.class);
    }

    public List<ReceiptManifestItemVO> getReceiptManifestItemVOListByItemId(List<Long> receiptManifestItemIdList) {

        return BeanMapUtils.mapListTo(receiptManifestItemRepository.findByReceiptManifestItemIdIn(receiptManifestItemIdList), ReceiptManifestItemVO.class);
    }

    public void confirmReceiptManifestItem(List<ReceiptManifestItemVO> receiptManifestItemVOList) {

        receiptManifestItemRepository.saveInBatch(BeanMapUtils.mapListTo(receiptManifestItemVOList, ReceiptManifestItem.class));
    }

    public void deleteReceiptManifestItem(ReceiptManifestVO receiptManifestVO, ReceiptManifestItemVO receiptManifestItemVO) {

        if (ObjectUtils.isNotEmpty(receiptManifestVO)) {
            receiptManifestRepository.delete(BeanMapUtils.mapTo(receiptManifestVO, ReceiptManifest.class));
        }

        if (ObjectUtils.isNotEmpty(receiptManifestItemVO)) {
            receiptManifestItemRepository.delete(BeanMapUtils.mapTo(receiptManifestItemVO, ReceiptManifestItem.class));
        }
    }

    public void deleteReceiptManifestItem(ReceiptManifestVO receiptManifestVO, List<ReceiptManifestItemVO> receiptManifestItemVOList) {

        if (ObjectUtils.isNotEmpty(receiptManifestVO)) {
            receiptManifestRepository.delete(BeanMapUtils.mapTo(receiptManifestVO, ReceiptManifest.class));
        }

        receiptManifestItemRepository.deleteAllInBatch(BeanMapUtils.mapListTo(receiptManifestItemVOList, ReceiptManifestItem.class));
    }

    public ReceiptManifestItemVO getReceiptManifestItemVO(Long receiptManifestItemId) {

        return BeanMapUtils.mapTo(receiptManifestItemRepository.findByReceiptManifestItemId(receiptManifestItemId), ReceiptManifestItemVO.class);
    }

    public List<ReceiptManifestItemVO> getReceiptManifestItemVOListById(Long receiptManifestId) {

        return BeanMapUtils.mapListTo(receiptManifestItemRepository.findByReceiptManifestId(receiptManifestId), ReceiptManifestItemVO.class);
    }

    public PurchaseOrderVO getPurchaseOrderVO(String siteId, Long facilityId, String orderNo) {

        return BeanMapUtils.mapTo(purchaseOrderRepository.findBySiteIdAndFacilityIdAndOrderNo(siteId, facilityId, orderNo), PurchaseOrderVO.class);
    }

    public PurchaseOrderItemVO getPurchaseOrderItemVO(String siteId, Long purchaseOrderId, Long productId) {

        return BeanMapUtils.mapTo(purchaseOrderItemRepository.findBySiteIdAndPurchaseOrderIdAndProductId(siteId, purchaseOrderId, productId), PurchaseOrderItemVO.class);
    }

    public SPM030102BO getPurchaseOrderData(SPM030102Form form, PJUserDetails uc) {

        return purchaseOrderRepository.getPurchaseOrderData(form, uc);
    }

    public String slipNo(String siteId, Long pointId) {

        return generateNoManager.generateSlipNo(siteId, pointId);
    }

    public String storingListNo(String siteId, Long pointId) {

        return generateNoManager.generateNonSerializedItemStoringListNo(siteId, pointId);
    }

    public String storingLineNo(String siteId, Long pointId) {

        return generateNoManager.generateNonSerializedItemStoringLineNo(siteId, pointId);
    }

    public void generateStockStatusVOMap(String siteId
                                       , Long facilityId
                                       , Long productId
                                       , String productStockStatusType
                                       , BigDecimal qty
                                       , Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap) {

        inventoryManager.generateStockStatusVOMap(siteId, facilityId, productId, qty, productStockStatusType, stockStatusVOChangeMap);
    }

    public void doCostCalculation(String siteId
                                , Long receiptProductId
                                , BigDecimal receiptPrice
                                , BigDecimal receiptQty
                                , BigDecimal receivePercent
                                , Long receiveFacilityId) {

        costManager.doCostCalculation(siteId
                                    , receiptProductId
                                    , receiptPrice
                                    , receiptQty
                                    , receivePercent
                                    , receiveFacilityId);
    }

    public void doPurchaseOrderReceipt(List<ReceiptSlipItemVO> receiptSlipItemList, List<ReceiptPoItemRelationVO> relationVOList, String siteId) {

        purchaseOrderManager.doPurchaseOrderReceipt(receiptSlipItemList, relationVOList, siteId);
    }

    public void checkManifestItems(List<ReceiptManifestItemVO> receiptManifestItemVOList) {

        receiptSlipManager.checkManifestItems(receiptManifestItemVOList);
    }

    public boolean checkPartsSupersedingRelation(Long toProductId, Long fromProductId) {

        return productManager.checkPartsSupersedingRelation(toProductId, fromProductId);
    }

    public List<ReceiptSlipVO> confirmReceiptManifest(SPM030101Form form, PJUserDetails uc) {

    	String PARTS = ProductClsType.PART.getCodeDbid();
        List<SPM030101BO> allTableDataList = form.getAllTableDataList();
        List<String> caseNoList = allTableDataList.stream().map(SPM030101BO::getCaseNo).collect(Collectors.toList());
        // 以CaseNo获取receiptManifestItem
        List<ReceiptManifestItemVO> receiptManifestItemVOList = this.getReceiptManifestItemVOList(caseNoList);
        Map<String, List<ReceiptManifestItemVO>> receiptManifestItemVOMap = receiptManifestItemVOList.stream().collect(Collectors.groupingBy(ReceiptManifestItemVO::getCaseNo));
        Set<Long> receiptManifestIdSet = receiptManifestItemVOList.stream().map(ReceiptManifestItemVO::getReceiptManifestId).collect(Collectors.toSet());

        // 以receiptManifestId获取receiptManifest
        List<ReceiptManifestVO> receiptManifestVOList = this.getReceiptManifestVOList(receiptManifestIdSet);
        Map<Long, ReceiptManifestVO> receiptManifestVOMap = receiptManifestVOList.stream().collect(Collectors.toMap(ReceiptManifestVO::getReceiptManifestId, Function.identity()));

        for(SPM030101BO bo : allTableDataList) {
            ReceiptManifestVO receiptManifestVO = receiptManifestVOMap.get(bo.getReceiptManifestId());
            if (!bo.getUpdateCount().equals(receiptManifestVO.getUpdateCount())) {
                throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[] {ComUtil.t("label.caseNo"), bo.getCaseNo(), ComUtil.t("title.partsReciptEntryByCaseNo_01")}));
            }
        }

        // 部品id
        Set<Long> productIdSet = receiptManifestItemVOList.stream().map(ReceiptManifestItemVO::getReceiptProductId).collect(Collectors.toSet());

        // 部品信息
        List<MstProductVO> mstProductVOList = this.getMstProductVOList(productIdSet);
        Map<Long, MstProductVO> mstProductVOMap = mstProductVOList.stream().collect(Collectors.toMap(MstProductVO::getProductId, Function.identity()));

        // 缺货库存
        Set<String> productStockStatusTypes = Set.of(SpStockStatus.BO_QTY.getCodeDbid());
        List<ProductStockStatusVO> productStockStatusVOList = this.getProductStockStatusVOList(uc.getDealerCode(), form.getPointId(), productIdSet, PARTS, productStockStatusTypes);
        Map<Long, ProductStockStatusVO> productStockStatusVOMap = productStockStatusVOList.stream().collect(Collectors.toMap(ProductStockStatusVO::getProductId, Function.identity()));

        // 缺货库位
        LocationVO locationVO = this.getLocationVO(uc.getDealerCode(), form.getPointId(), LocationType.TENTATIVE.getCodeDbid());

        // 主库位
        List<SPM030101BO> primaryList = this.getProductInventoryList(uc.getDealerCode(), form.getPointId(), productIdSet, PARTS, CommonConstants.CHAR_Y,null);
        Map<Long, SPM030101BO> primaryMap = primaryList.stream().collect(Collectors.toMap(SPM030101BO::getProductId, Function.identity()));

        // 普通库位
        List<SPM030101BO> generalList = this.getProductInventoryList(uc.getDealerCode(), form.getPointId(), productIdSet, PARTS, CommonConstants.CHAR_N,LocationType.NORMAL.getCodeDbid());
        //存在多个主货位,需要根据productid进行分组管理
        Map<Long, List<SPM030101BO>> generalMap = generalList.stream().collect(Collectors.groupingBy(SPM030101BO::getProductId));

        // purchaseOrder
        Set<String> purchaseOrderNoList = receiptManifestItemVOList.stream().map(ReceiptManifestItemVO::getPurchaseOrderNo).distinct().collect(Collectors.toSet());
        List<PurchaseOrderVO> purchaseOrderVOList = this.getPurchaseOrderVOList(uc.getDealerCode(), purchaseOrderNoList);
        Set<Long> purchaseOrderIdSet = purchaseOrderVOList.stream().map(PurchaseOrderVO::getPurchaseOrderId).collect(Collectors.toSet());
        Map<Long, PurchaseOrderVO> purchaseOrderVOMap = purchaseOrderVOList.stream().collect(Collectors.toMap(PurchaseOrderVO::getPurchaseOrderId, Function.identity()));

        // purchaseOrderNoMap
        Map<String, Long> purchaseOrderNoMap = purchaseOrderVOList.stream().collect(Collectors.toMap(PurchaseOrderVO::getOrderNo, PurchaseOrderVO::getPurchaseOrderId));

        // purchaseOrderItem
        List<PurchaseOrderItemVO> purchaseOrderItemVOList = this.getPurchaseOrderItemVOList(uc.getDealerCode(), purchaseOrderIdSet);

        // 以purchaseOrderId和productId组合成key
        Map<String, PurchaseOrderItemVO> purchaseOrderItemVOMap = purchaseOrderItemVOList.stream().collect(Collectors.toMap(item -> item.getPurchaseOrderId().toString() + item.getProductId().toString(), Function.identity()));

        List<ReceiptSlipVO> receiptSlipVOList = new ArrayList<>();
        List<StoringListVO> storingListVOList = new ArrayList<>();
        List<ReceiptSlipItemVO> receiptSlipItemVOList = new ArrayList<>();
        List<ReceiptPoItemRelationVO> receiptPoItemRelationVOList = new ArrayList<>();
        List<StoringLineVO> storingLineVOList = new ArrayList<>();
        List<StoringLineItemVO> storingLineItemVOList = new ArrayList<>();
        List<ReceiptManifestItemVO> manifestItemVOs = new ArrayList<>();
        List<InventoryTransactionVO> inventoryTransactionVOs = new ArrayList<>();
        // 循环选择的caseNo
        Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = new HashMap<>();
        for (SPM030101BO bo : allTableDataList) {
            // 以caseNo获取receiptManifestItem
            List<ReceiptManifestItemVO> curReceiptManifestItemVOList = receiptManifestItemVOMap.get(bo.getCaseNo());

            Long receiptManifestId = curReceiptManifestItemVOList.get(0).getReceiptManifestId();

            // 以receiptManifestId获取receiptManifest
            ReceiptManifestVO receiptManifestVO = receiptManifestVOMap.get(receiptManifestId);

            // 更新receiptManifest
            // if (receiptManifestVO.getManifestStatus().equals(ManifestStatus.WAITING_ISSUE.getCodeDbid())) {
            //     receiptManifestVO.setManifestStatus(ManifestStatus.ISSUED.getCodeDbid());
            // }

            // 新增receiptSlip
            ReceiptSlipVO receiptSlipVO = ReceiptSlipVO.create();
            receiptSlipVO.setSiteId(uc.getDealerCode());
            receiptSlipVO.setSlipNo(this.slipNo(uc.getDealerCode(), form.getPointId()));
            receiptSlipVO.setReceivedDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
            // 计算并设置receiptSlipTotalAmt
            BigDecimal totalAmt = receiptManifestItemVOList.stream().map(item -> item.getReceiptQty().multiply(item.getReceiptPrice())).reduce(BigDecimal.ZERO, BigDecimal::add);
            receiptSlipVO.setReceiptSlipTotalAmt(totalAmt);
            receiptSlipVO.setReceivedFacilityId(receiptManifestVO.getToFacilityId());
            receiptSlipVO.setReceivedPicId(uc.getPersonId());
            receiptSlipVO.setReceivedPicNm(uc.getPersonName());
            receiptSlipVO.setFromOrganizationId(receiptManifestVO.getFromOrganization());
            receiptSlipVO.setReceiptSlipStatus(ReceiptSlipStatus.RECEIPTED.getCodeDbid());
            receiptSlipVO.setInventoryTransactionType(InventoryTransactionType.PURCHASESTOCKIN.getCodeDbid());
            receiptSlipVO.setProductClassification(PARTS);
            receiptSlipVOList.add(receiptSlipVO);

            // 新增storingList
            StoringListVO storingListVO = StoringListVO.create();
            storingListVO.setSiteId(uc.getDealerCode());
            storingListVO.setFacilityId(receiptManifestVO.getToFacilityId());
            storingListVO.setStoringListNo(this.storingListNo(uc.getDealerCode(), form.getPointId()));
            storingListVO.setReceiptDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
            storingListVO.setInstructionDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
            storingListVO.setInstructionTime(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_H_M_S));
            storingListVO.setInventoryTransactionType(InventoryTransactionType.PURCHASESTOCKIN.getCodeDbid());
            storingListVO.setProductClassification(PARTS);
            storingListVOList.add(storingListVO);

            for (ReceiptManifestItemVO receiptManifestItemVO : receiptManifestItemVOList) {
                // 获取对应mstProduct
                MstProductVO mstProductVO = mstProductVOMap.get(receiptManifestItemVO.getReceiptProductId());

                // 对应的purchaseOrderId
                Long purchaseOrderId = purchaseOrderNoMap.get(receiptManifestItemVO.getPurchaseOrderNo());

                // 对应的purchaseOrder
                PurchaseOrderVO purchaseOrderVO = purchaseOrderVOMap.get(purchaseOrderId);

                // 对应的purchaseOrderItem
                PurchaseOrderItemVO purchaseOrderItemVO = purchaseOrderItemVOMap.get(purchaseOrderId.toString() + receiptManifestItemVO.getOrderProductId());

                // 新增receiptSlipItem
                ReceiptSlipItemVO receiptSlipItemVO = ReceiptSlipItemVO.create();
                receiptSlipItemVO.setSiteId(uc.getDealerCode());
                receiptSlipItemVO.setReceiptSlipId(receiptSlipVO.getReceiptSlipId());
                receiptSlipItemVO.setProductId(receiptManifestItemVO.getReceiptProductId());
                receiptSlipItemVO.setProductCd(mstProductVO.getProductCd());
                receiptSlipItemVO.setProductNm(mstProductVO.getSalesDescription());
                receiptSlipItemVO.setReceiptQty(receiptManifestItemVO.getReceiptQty());
                receiptSlipItemVO.setFrozenQty(receiptManifestItemVO.getFrozenQty());
                receiptSlipItemVO.setReceiptPrice(receiptManifestItemVO.getReceiptPrice());
                receiptSlipItemVO.setSupplierInvoiceNo(receiptManifestVO.getSupplierInvoiceNo());
                receiptSlipItemVO.setPurchaseOrderNo(receiptManifestItemVO.getPurchaseOrderNo());
                receiptSlipItemVO.setCaseNo(receiptManifestItemVO.getCaseNo());
                receiptSlipItemVO.setProductClassification(PARTS);
                receiptSlipItemVOList.add(receiptSlipItemVO);

                // 新增receiptPoItemRelation
                ReceiptPoItemRelationVO receiptPoItemRelationVO = new ReceiptPoItemRelationVO();
                receiptPoItemRelationVO.setSiteId(uc.getDealerCode());
                receiptPoItemRelationVO.setFacilityId(receiptManifestVO.getToFacilityId());
                receiptPoItemRelationVO.setOrderItemId(purchaseOrderItemVO.getPurchaseOrderItemId());
                receiptPoItemRelationVO.setReceiptSlipItemId(receiptSlipItemVO.getReceiptSlipItemId());
                receiptPoItemRelationVO.setPurchaseOrderNo(receiptManifestItemVO.getPurchaseOrderNo());
                receiptPoItemRelationVO.setSupplierInvoiceNo(receiptManifestVO.getSupplierInvoiceNo());
                receiptPoItemRelationVO.setReceiptQty(receiptManifestItemVO.getReceiptQty());
                receiptPoItemRelationVOList.add(receiptPoItemRelationVO);

                // 新增storingLine
                StoringLineVO storingLineVO = StoringLineVO.create();
                storingLineVO.setSiteId(uc.getDealerCode());
                storingLineVO.setFacilityId(receiptManifestVO.getToFacilityId());
                storingLineVO.setStoringLineNo(this.storingLineNo(uc.getDealerCode(), form.getPointId()));
                storingLineVO.setStoringListId(storingListVO.getStoringListId());
                storingLineVO.setReceiptSlipItemId(receiptSlipItemVO.getReceiptSlipItemId());
                storingLineVO.setReceiptSlipNo(receiptSlipVO.getSlipNo());
                storingLineVO.setProductId(receiptManifestItemVO.getReceiptProductId());
                storingLineVO.setProductCd(mstProductVO.getProductCd());
                storingLineVO.setProductNm(mstProductVO.getSalesDescription());
                storingLineVO.setCaseNo(receiptManifestItemVO.getCaseNo());
                storingLineVO.setOriginalInstQty(receiptManifestItemVO.getReceiptQty());
                storingLineVO.setInstuctionQty(receiptManifestItemVO.getReceiptQty());
                storingLineVO.setStoredQty(BigDecimal.ZERO);
                storingLineVO.setFrozenQty(BigDecimal.ZERO);
                storingLineVO.setInventoryTransactionType(InventoryTransactionType.PURCHASESTOCKIN.getCodeDbid());
                storingLineVO.setProductClassification(PARTS);
                storingLineVOList.add(storingLineVO);

                // 新增storingLineItem
                StoringLineItemVO storingLineItemVO = new StoringLineItemVO();
                storingLineItemVO.setSiteId(uc.getDealerCode());
                storingLineItemVO.setFacilityId(receiptManifestVO.getToFacilityId());
                storingLineItemVO.setStoringLineId(storingLineVO.getStoringLineId());
                storingLineItemVO.setStoredQty(BigDecimal.ZERO);

                //获取当前库存数量
                List<String> statusType = new ArrayList<>();
                statusType.add(PJConstants.SpStockStatus.ONHAND_QTY.getCodeDbid());
                statusType.add(PJConstants.SpStockStatus.ONSERVICE_QTY.getCodeDbid());
                statusType.add(PJConstants.SpStockStatus.ONFROZEN_QTY.getCodeDbid());
                statusType.add(PJConstants.SpStockStatus.ALLOCATED_QTY.getCodeDbid());
                statusType.add(PJConstants.SpStockStatus.ONRECEIVING_QTY.getCodeDbid());
                statusType.add(PJConstants.SpStockStatus.ONPICKING_QTY.getCodeDbid());
                statusType.add(PJConstants.SpStockStatus.ONTRANSFER_IN_QTY.getCodeDbid());
                List<ProductStockStatusVO> productStockStatusVOs = this.findProductStockStatusIn(uc.getDealerCode(), form.getPointId(), receiptManifestItemVO.getReceiptProductId(), statusType);
                BigDecimal stockQuantity = productStockStatusVOs.stream().map(ProductStockStatusVO :: getQuantity).reduce(BigDecimal.ZERO,BigDecimal::add);

                //新增库存调整记录
                ProductCostVO productCostVO = this.findProductCostByProductId(receiptManifestItemVO.getReceiptProductId(), PJConstants.CostType.AVERAGE_COST, uc.getDealerCode());

                InventoryTransactionVO inventoryTransactionVO = inventoryManager.generateInventoryTransactionVO(uc.getDealerCode(), InOutType.IN, receiptManifestVO.getToFacilityId(), receiptManifestVO.getFromFacilityId(), receiptManifestVO.getToFacilityId(), receiptManifestItemVO.getReceiptProductId(), mstProductVO.getProductCd(), mstProductVO.getSalesDescription(), PJConstants.InventoryTransactionType.PURCHASESTOCKIN.getCodeDbid(), receiptManifestItemVO.getReceiptQty(), stockQuantity, receiptSlipItemVO.getReceiptPrice(), receiptSlipVO.getReceiptSlipId(), receiptSlipVO.getSlipNo(), receiptManifestVO.getFromOrganization(), receiptManifestVO.getToOrganization(), null, productCostVO, null, uc.getPersonId(), uc.getPersonName(), PJConstants.ProductClsType.PART.getCodeDbid());
                inventoryTransactionVOs.add(inventoryTransactionVO);

                // 缺货部品
                ProductStockStatusVO productStockStatusVO = productStockStatusVOMap.get(receiptManifestItemVO.getReceiptProductId());

                // 缺货数量
                BigDecimal boQty = productStockStatusVO == null ? BigDecimal.ZERO : productStockStatusVO.getQuantity();
                // 如果有缺货库位且缺货数量 >= 接收数量则上架到这个库位
                if (null != locationVO && null != productStockStatusVO && boQty.compareTo(receiptManifestItemVO.getReceiptQty()) >= 0) {
                    storingLineItemVO.setLocationId(locationVO.getLocationId());
                    storingLineItemVO.setLocationCd(locationVO.getLocationCd());
                    storingLineItemVO.setInstuctionQty(receiptManifestItemVO.getReceiptQty());
                    storingLineItemVOList.add(storingLineItemVO);
                } else if (null != locationVO && null != productStockStatusVO && boQty.compareTo(receiptManifestItemVO.getReceiptQty()) < 0) {
                    // 如果有缺货库位且缺货数量 < 接收数量则上架到这个库位一部分 剩下的上架主库位或普通库位
                    // 二次上架的部分也需要新增一条数据
                    storingLineItemVO.setLocationId(locationVO.getLocationId());
                    storingLineItemVO.setLocationCd(locationVO.getLocationCd());
                    storingLineItemVO.setInstuctionQty(productStockStatusVO.getQuantity());
                    storingLineItemVOList.add(storingLineItemVO);
                    BigDecimal instuctionQty = receiptManifestItemVO.getReceiptQty().subtract(productStockStatusVO.getQuantity());

                    storingLineItemVO = this.buildStoringLineItem(uc, receiptManifestVO, primaryMap, generalMap, receiptManifestItemVO, storingLineVO, instuctionQty);
                    storingLineItemVOList.add(storingLineItemVO);
                } else {
                    // 没有缺货库位则上架到主库位或普通库位
                    storingLineItemVO = this.buildStoringLineItem(uc, receiptManifestVO, primaryMap, generalMap, receiptManifestItemVO, storingLineVO, receiptManifestItemVO.getReceiptQty());
                    storingLineItemVOList.add(storingLineItemVO);
                }

                manifestItemVOs.add(receiptManifestItemVO);
                // 更新productStockStatus
                Map<String, String> productStockStatusTypeMap = ProductStockStatusType.map;
                String productStockStatusType = productStockStatusTypeMap.get(purchaseOrderVO.getOrderPriorityType());
                if (StringUtils.isNotBlankText(productStockStatusType)) {
                    this.generateStockStatusVOMap(uc.getDealerCode()
                                                , receiptManifestVO.getToFacilityId()
                                                , receiptManifestItemVO.getOrderProductId()
                                                , productStockStatusType
                                                , receiptManifestItemVO.getReceiptQty().negate()
                                                , stockStatusVOChangeMap);
                }

                this.generateStockStatusVOMap(uc.getDealerCode()
                                            , receiptManifestVO.getToFacilityId()
                                            , receiptManifestItemVO.getReceiptProductId()
                                            , SpStockStatus.ONRECEIVING_QTY.getCodeDbid()
                                            , receiptManifestItemVO.getReceiptQty()
                                            , stockStatusVOChangeMap);

                // 更新productCost
                this.doCostCalculation(uc.getDealerCode()
                                     , receiptManifestItemVO.getReceiptProductId()
                                     , receiptManifestItemVO.getReceiptPrice()
                                     , receiptManifestItemVO.getReceiptQty()
                                     , BigDecimal.ZERO
                                     , receiptManifestVO.getToFacilityId());
            }
        }

        inventoryManager.updateProductStockStatusByMap(stockStatusVOChangeMap);
        manifestItemVOs.forEach(item -> item.setManifestItemStatus(ManifestItemStatus.RECEIVING.getCodeDbid()));

        // 更新purchaseOrder purchaseOrderItem
        this.doPurchaseOrderReceipt(receiptSlipItemVOList, receiptPoItemRelationVOList, uc.getDealerCode());
        this.confirmReceiptManifest(receiptManifestVOList, receiptSlipVOList, storingListVOList, receiptSlipItemVOList, receiptPoItemRelationVOList, storingLineVOList, storingLineItemVOList, manifestItemVOs,inventoryTransactionVOs);

        return receiptSlipVOList;
    }

    private StoringLineItemVO buildStoringLineItem(PJUserDetails uc
                                                 , ReceiptManifestVO receiptManifestVO
                                                 , Map<Long, SPM030101BO> primaryMap
                                                 , Map<Long, List<SPM030101BO>> generalMap
                                                 , ReceiptManifestItemVO receiptManifestItemVO
                                                 , StoringLineVO storingLineVO
                                                 , BigDecimal instuctionQty) {

        StoringLineItemVO storingLineItemVO = new StoringLineItemVO();
        storingLineItemVO.setSiteId(uc.getDealerCode());
        storingLineItemVO.setFacilityId(receiptManifestVO.getToFacilityId());
        storingLineItemVO.setStoringLineId(storingLineVO.getStoringLineId());
        storingLineItemVO.setStoredQty(BigDecimal.ZERO);

        SPM030101BO primaryVO = primaryMap.get(receiptManifestItemVO.getReceiptProductId());
        List<SPM030101BO> generalVO = generalMap.get(receiptManifestItemVO.getReceiptProductId());

        if (null != primaryVO) {
            // 如果主库位存在则上架主库位
            storingLineItemVO.setLocationId(primaryVO.getLocationId());
            storingLineItemVO.setLocationCd(primaryVO.getLocationCd());
            storingLineItemVO.setInstuctionQty(instuctionQty);
        } else if(null != generalVO) {
            // 主库位不存在则上架普通库位中数量最多的库位
            SPM030101BO maxQtyBO = generalVO.stream()
                                            .reduce((bo1, bo2) -> bo1.getQty().compareTo(bo2.getQty()) >= 0 ? bo1 : bo2)
                                            .orElse(null);
            storingLineItemVO.setLocationId(maxQtyBO.getLocationId());
            storingLineItemVO.setLocationCd(maxQtyBO.getLocationCd());
            storingLineItemVO.setInstuctionQty(instuctionQty);
        } else {
            // 均不存在则设为null
            storingLineItemVO.setLocationId(null);
            storingLineItemVO.setLocationCd(null);
            storingLineItemVO.setInstuctionQty(instuctionQty);
        }
        return storingLineItemVO;
    }

    public void confirmReceiptManifestItem(SPM030102Form form) {

        // 删除改为点击confirm后进行
        List<SPM030102BO> deleteList = form.getTableDataList().getRemoveRecords();

        // 删除数据
        if (CollectionUtils.isNotEmpty(deleteList)) {

            List<Long> receiptManifestItemIdList = deleteList.stream().map(SPM030102BO::getReceiptManifestItemId).collect(Collectors.toList());
            List<ReceiptManifestItemVO> receiptManifestItemVOList = this.getReceiptManifestItemVOListByItemId(receiptManifestItemIdList);
            Map<Long, ReceiptManifestItemVO> receiptManifestItemVOMap = receiptManifestItemVOList.stream().collect(Collectors.toMap(ReceiptManifestItemVO::getReceiptManifestItemId, Function.identity()));

            List<ReceiptManifestItemVO> deleteVOList = new ArrayList<>();
            Long receiptManifestId = deleteList.get(0).getReceiptManifestId();
            for (SPM030102BO bo : deleteList) {
                Long receiptManifestItemId = bo.getReceiptManifestItemId();
                ReceiptManifestItemVO receiptManifestItemVO = receiptManifestItemVOMap.get(receiptManifestItemId);
                if (!bo.getUpdateCount().equals(receiptManifestItemVO.getUpdateCount())) {
                    throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[] {ComUtil.t("label.purchaseOrderNumber"), form.getOrderNo(), ComUtil.t("title.partsManifestModify_02")}));
                }
                deleteVOList.add(receiptManifestItemVO);
            }

            // 获取对应receiptManifestId下的receiptManifestItem
            List<ReceiptManifestItemVO> curReceiptManifestItemVOList = this.getReceiptManifestItemVOListById(receiptManifestId);
            ReceiptManifestVO receiptManifestVO = null;
            // 如果该receiptManifestId下receiptManifestItem和删除的数据一致，则删除receiptManifest
            if (deleteVOList.size() == curReceiptManifestItemVOList.size()) {
                receiptManifestVO = this.getReceiptManifestVO(receiptManifestId);
            }

            this.deleteReceiptManifestItem(receiptManifestVO, deleteVOList);
        }

        List<SPM030102BO> updateList = form.getTableDataList().getUpdateRecords();

        List<Long> receiptManifestItemIdList = updateList.stream().map(SPM030102BO::getReceiptManifestItemId).collect(Collectors.toList());
        List<ReceiptManifestItemVO> receiptManifestItemVOList = this.getReceiptManifestItemVOListByItemId(receiptManifestItemIdList);
        Map<Long, ReceiptManifestItemVO> receiptManifestItemVOMap = receiptManifestItemVOList.stream().collect(Collectors.toMap(ReceiptManifestItemVO::getReceiptManifestItemId, Function.identity()));

        // 更新数据
        for (SPM030102BO bo : updateList) {
            Long receiptManifestItemId = bo.getReceiptManifestItemId();
            ReceiptManifestItemVO receiptManifestItemVO = receiptManifestItemVOMap.get(receiptManifestItemId);
            if (!bo.getUpdateCount().equals(receiptManifestItemVO.getUpdateCount())) {
                throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[] {ComUtil.t("label.purchaseOrderNumber"), form.getOrderNo(), ComUtil.t("title.partsManifestModify_02")}));
            }
            receiptManifestItemVO.setReceiptProductCd(bo.getReceiptPartsNo());
            receiptManifestItemVO.setReceiptProductId(bo.getReceiptPartsId());
            receiptManifestItemVO.setOrderProductCd(bo.getOrderPartsNo());
            receiptManifestItemVO.setOrderProductId(bo.getOrderPartsId());
            receiptManifestItemVO.setReceiptQty(bo.getTotalReceiptQty());
            receiptManifestItemVO.setPurchaseOrderNo(bo.getOrderNo());
        }

        // 更新errorInfo
        this.checkManifestItems(receiptManifestItemVOList);
        //this.confirmReceiptManifestItem(receiptManifestItemVOList);
    }

    public List<SPM030101BO> getInoviceNoAndCaseNoList(List<String> orderNo) {

        return receiptManifestRepository.getInoviceNoAndCaseNoList(orderNo);
    }

    public List<PartsStoringListForFinancePrintDetailBO> getPartsStoringListForFinanceReport(List<Long> receiptSlipIds) {

        return receiptSlipRepository.getPartsStoringListForFinanceReport(receiptSlipIds);
    }

    public PartsStoringListForFinancePrintBO getPartsStoringListForFinanceReportHeader(List<Long> receiptSlipIds) {

        return receiptSlipRepository.getPartsStoringListForFinanceReportHeader(receiptSlipIds);
    }

    public List<ProductStockStatusVO>  findProductStockStatusIn(String siteId , Long facilityId, Long productId, List<String> productStockStatusType){

        return BeanMapUtils.mapListTo(productStockStatusRepository.findProductStockStatusIn(siteId,facilityId,productId,productStockStatusType), ProductStockStatusVO.class);
    }

    public ProductCostVO findProductCostByProductId(Long productId, String costType, String siteId) {

        return BeanMapUtils.mapTo(productCostRepository.findByProductIdAndCostTypeAndSiteId(productId,costType,siteId), ProductCostVO.class);
    }

    public String getManifestData(String[][] ps) {

        LinkedHashMap<String, Object> map = Arrays.stream(ps).collect(LinkedHashMap::new, (m, v) -> m.put(v[0], v[1]), HashMap::putAll);
        String ifsCode = InterfCode.DMSTOSP_SPMANIFEST_INQ;
        BaseInfResponse callNewIfsService = callNewIfsManager.callGetMethodNewIfsService(ifsRequestUrl, ifsCode, map);
        return callNewIfsService.getData();
    }

    public void doManifestImports(String detail){
        receiptSlipManager.doManifestImports(JSONArray.parseArray(detail, SpManifestItemBO.class));
    }

}
