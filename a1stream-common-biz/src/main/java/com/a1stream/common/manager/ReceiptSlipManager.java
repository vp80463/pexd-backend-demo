package com.a1stream.common.manager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.bo.SdManifestItemBO;
import com.a1stream.common.bo.SpManifestItemBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.MstCodeConstants.ManifestStatus;
import com.a1stream.common.constants.MstCodeConstants.McSalesStatus;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.BatteryType;
import com.a1stream.common.constants.PJConstants.CrmApiType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.QueueStatus;
import com.a1stream.common.constants.PJConstants.ReceiptManifestErrorType;
import com.a1stream.common.constants.PJConstants.ReceiptSlipStatus;
import com.a1stream.common.constants.PJConstants.SerialProQualityStatus;
import com.a1stream.common.constants.PJConstants.SerialproductStockStatus;
import com.a1stream.domain.entity.Battery;
import com.a1stream.domain.entity.CmmBattery;
import com.a1stream.domain.entity.CmmSerializedProduct;
import com.a1stream.domain.entity.QueueApiData;
import com.a1stream.domain.entity.ReceiptManifest;
import com.a1stream.domain.entity.ReceiptManifestItem;
import com.a1stream.domain.entity.ReceiptManifestSerializedItem;
import com.a1stream.domain.entity.ReceiptPoItemRelation;
import com.a1stream.domain.entity.ReceiptSlip;
import com.a1stream.domain.entity.ReceiptSlipItem;
import com.a1stream.domain.entity.SerializedProduct;
import com.a1stream.domain.repository.BatteryRepository;
import com.a1stream.domain.repository.CmmBatteryRepository;
import com.a1stream.domain.repository.CmmMstOrganizationRepository;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.repository.DeliveryOrderItemRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstOrganizationRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.PurchaseOrderItemRepository;
import com.a1stream.domain.repository.PurchaseOrderRepository;
import com.a1stream.domain.repository.QueueApiDataRepository;
import com.a1stream.domain.repository.ReceiptManifestItemRepository;
import com.a1stream.domain.repository.ReceiptManifestRepository;
import com.a1stream.domain.repository.ReceiptManifestSerializedItemRepository;
import com.a1stream.domain.repository.ReceiptPoItemRelationRepository;
import com.a1stream.domain.repository.ReceiptSlipItemRepository;
import com.a1stream.domain.repository.ReceiptSlipRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmMstOrganizationVO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstOrganizationVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.PurchaseOrderItemVO;
import com.a1stream.domain.vo.PurchaseOrderVO;
import com.a1stream.domain.vo.QueueApiDataVO;
import com.a1stream.domain.vo.ReceiptManifestItemVO;
import com.a1stream.domain.vo.ReceiptManifestSerializedItemVO;
import com.a1stream.domain.vo.ReceiptManifestVO;
import com.a1stream.domain.vo.ReceiptPoItemRelationVO;
import com.a1stream.domain.vo.ReceiptSlipItemVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;
@Component
public class ReceiptSlipManager {

    @Resource
    private GenerateNoManager generateNoMgr;

    @Resource
    private ProductManager productMgr;

    @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepository;

    @Resource
    private DeliveryOrderItemRepository doItemRepo;

    @Resource
    private ReceiptSlipRepository receiptSlipRepo;

    @Resource
    private ReceiptSlipItemRepository receiptSlipItemRepo;

    @Resource
    private ReceiptPoItemRelationRepository receiptPoItemRelaRepo;

    @Resource
    private ReceiptManifestItemRepository receiptManifestItemRepo;

    @Resource
    private ReceiptManifestRepository receiptManifestRepo;

    @Resource
    private PurchaseOrderRepository purchaseOrderRepo;

    @Resource
    private PurchaseOrderItemRepository poItemRepo;

    @Resource
    private MstProductRepository mstProductRepo;

    @Resource
    private MstOrganizationRepository mstOrgRepo;

    @Resource
    private CmmSerializedProductRepository cmmSerializedProductRepo;

    @Resource
    private MstFacilityRepository mstFacilityRepo;

    @Resource
    private CmmBatteryRepository cmmBatteryRepo;

    @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepo;

    @Resource
    private ReceiptManifestSerializedItemRepository receiptManifestSerializedItemRepo;

    @Resource
    private SerializedProductRepository serializedProductRepo;

    @Resource
    private MstOrganizationRepository mstOrganizationRepo;

    @Resource
    private BatteryRepository batteryRepo;

    @Resource
    private QueueApiDataRepository queueApiDataRepo;

    @Resource
    private CmmMstOrganizationRepository cmmMstOrganizationRepo;

    String POERROR          = ReceiptManifestErrorType.POERROR.getCodeData1();
    String ODPARTERROR      = ReceiptManifestErrorType.ODPARTERROR.getCodeData1();
    String RECEIPTQTYERROR  = ReceiptManifestErrorType.RECEIPTQTYERROR.getCodeData1();
    String RECEIPTCOSTERROR = ReceiptManifestErrorType.RECEIPTCOSTERROR.getCodeData1();
    String QTYERROR         = ReceiptManifestErrorType.QTYERROR.getCodeData1();
    String RECEIPTPARTERROR = ReceiptManifestErrorType.RECEIPTPARTERROR.getCodeData1();
    String SUPERSEDINGERROR = ReceiptManifestErrorType.SUPERSEDINGERROR.getCodeData1();

    /**
     *
     * doSalesReturn mid1100 2024年4月18日
     */
    public ReceiptSlipVO doSalesReturn(DeliveryOrderVO delieryOrder) {

        if (delieryOrder == null || delieryOrder.getDeliveryOrderId() == null) {
            throw new BusinessCodedException("Delivery Order does not exist");
        }

        ReceiptSlipVO receiptSlipVO = buildReceipSlipVO_SalesReturn(delieryOrder);
        ReceiptSlip receiptSlip = BeanMapUtils.mapTo(receiptSlipVO, ReceiptSlip.class);
        receiptSlipRepo.save(receiptSlip);

        List<DeliveryOrderItemVO> deliveryOrderItemsVO = BeanMapUtils.mapListTo(doItemRepo.findByDeliveryOrderId(delieryOrder.getDeliveryOrderId()), DeliveryOrderItemVO.class);
        List<ReceiptSlipItemVO> receiptSlipItemVOList = buildReceiptSlipItemVO_SalesReturn(receiptSlip.getReceiptSlipId(), deliveryOrderItemsVO);

        receiptSlipItemRepo.saveInBatch(BeanMapUtils.mapListTo(receiptSlipItemVOList, ReceiptSlipItem.class));

        return receiptSlipVO;
    }

    /**
     * createReceiptSlipBySupplierInvoice mid1100 2024年4月18日
     */
    public List<ReceiptSlipVO> createReceiptSlipBySupplierInvoice(List<Long> selectedReceiptManifestItemIds, String receiptSlipType) {

        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);

        if (selectedReceiptManifestItemIds.size() <= 0) {
            throw new BusinessCodedException("no ReceiptManifestItemId selected");
        }
        // get ReceiptManifestItemVOs
        List<ReceiptManifestItemVO> receiptManifestItemVOList = BeanMapUtils.mapListTo(receiptManifestItemRepo.findByReceiptManifestItemIdIn(selectedReceiptManifestItemIds), ReceiptManifestItemVO.class);
        if (receiptManifestItemVOList.size() <= 0) {
            throw new BusinessCodedException("ReceiptManifestItem does not exist");
        }
        // get ReceiptManifestVOs
        Set<Long> receiptManifestIds = receiptManifestItemVOList.stream().map(ReceiptManifestItemVO::getReceiptManifestId).collect(Collectors.toSet());
        List<ReceiptManifestVO> receiptManifestVOList = BeanMapUtils.mapListTo(receiptManifestRepo.findByReceiptManifestIdIn(receiptManifestIds), ReceiptManifestVO.class);
        Map<Long, ReceiptManifestVO> receiptManifestVOMap = receiptManifestVOList.stream().collect(Collectors.toMap(ReceiptManifestVO::getReceiptManifestId, Function.identity()));

        // 根据siteId,purchaseOrderNo,ReceiptManifestVO.getToFacilityId find purchaseOrderVO
        Set<String> siteIds = receiptManifestVOList.stream().map(ReceiptManifestVO::getSiteId).collect(Collectors.toSet());
        Set<Long> toFacilityIds = receiptManifestVOList.stream().map(ReceiptManifestVO::getToFacilityId).collect(Collectors.toSet());
        Set<String> purchaseOrderNos = receiptManifestItemVOList.stream().map(ReceiptManifestItemVO::getPurchaseOrderNo).collect(Collectors.toSet());

        List<PurchaseOrderVO> purchaseOrderVOList = BeanMapUtils.mapListTo(purchaseOrderRepo.findBySiteIdInAndOrderNoInAndFacilityIdIn(siteIds, purchaseOrderNos, toFacilityIds), PurchaseOrderVO.class);
        if (purchaseOrderVOList.isEmpty()) {
            throw new BusinessCodedException("PurchaseOrder does not exist");
        }
        Map<String, PurchaseOrderVO> purchaseOrderVOMap = purchaseOrderVOList.stream()
                .collect(Collectors.toMap(item -> (item.getSiteId() + "#" + item.getOrderNo() + "#" + item.getFacilityId()), Function.identity()));

        // 根据purchaseOrderId、ReceiptManifestItem.getOrderProductId找到对应的purchaseOrderItemVO
        Set<Long> purchaseOrderIds = purchaseOrderVOList.stream().map(PurchaseOrderVO::getPurchaseOrderId).collect(Collectors.toSet());
        Set<Long> orderProductIds = receiptManifestItemVOList.stream().map(ReceiptManifestItemVO::getOrderProductId).collect(Collectors.toSet());

        List<PurchaseOrderItemVO> poItemVOList = BeanMapUtils.mapListTo(poItemRepo.findByPurchaseOrderIdInAndProductIdIn(purchaseOrderIds, orderProductIds), PurchaseOrderItemVO.class);
        if (poItemVOList.isEmpty()) {
            throw new BusinessCodedException("PurchaseOrderItem does not exist");
        }
        Map<String, PurchaseOrderItemVO> purchaseOrderItemVOMap = poItemVOList.stream().collect(Collectors.toMap(item -> (item.getPurchaseOrderId() + "#" + item.getProductId()), Function.identity()));

        // product info
        Set<Long> receiptProductIds = receiptManifestItemVOList.stream().map(ReceiptManifestItemVO::getReceiptProductId).collect(Collectors.toSet());
        List<MstProductVO> receiptProductVOList = BeanMapUtils.mapListTo(mstProductRepo.findByProductIdIn(receiptProductIds), MstProductVO.class);
        Map<Long, MstProductVO> receiptProductInfoMap = receiptProductVOList.stream().collect(Collectors.toMap(MstProductVO::getProductId, Function.identity()));

        // prepare buildEntity
        Map<Long, ReceiptSlipVO> buildReceiptSlipVOMap = new HashMap<>();
        List<ReceiptSlipItemVO> buildReceiptSlipItemVOList = new ArrayList<>();
        List<ReceiptPoItemRelationVO> buildReceiptPoItemRelationVOList = new ArrayList<>();

        for (ReceiptManifestItemVO item : receiptManifestItemVOList) {

            ReceiptManifestVO receiptManifestVO = receiptManifestVOMap.get(item.getReceiptManifestId());
            // create ReceiptSlip
            Long receiptSlipId = buildReceiptSlipVO_SupplierInvoice(receiptSlipType, buildReceiptSlipVOMap, item, receiptManifestVO, sysDate);
            // create ReceiptSlipItem
            ReceiptSlipItemVO receiptSlipItemVO = buildReceiptSlipItemVO_SupplierInvoice(item, receiptManifestVO, receiptSlipId, receiptProductInfoMap);
            buildReceiptSlipItemVOList.add(receiptSlipItemVO);
            // create ReceiptPoItemRelation
            ReceiptPoItemRelationVO receiptPoItemRelaVO = buildReceiptPoItemRelationVO_SupplierInvoice(purchaseOrderVOMap, purchaseOrderItemVOMap, item, receiptManifestVO, receiptSlipItemVO.getReceiptSlipItemId());
            if (receiptPoItemRelaVO != null) {
                buildReceiptPoItemRelationVOList.add(receiptPoItemRelaVO);
            }
        }

        // 保存更新
        List<ReceiptSlipVO> receiptSlipVOList = new ArrayList<>(buildReceiptSlipVOMap.values());
        receiptSlipRepo.saveInBatch(BeanMapUtils.mapListTo(receiptSlipVOList, ReceiptSlip.class));

        receiptSlipItemRepo.saveInBatch(BeanMapUtils.mapListTo(buildReceiptSlipItemVOList, ReceiptSlipItem.class));

        receiptPoItemRelaRepo.saveInBatch(BeanMapUtils.mapListTo(buildReceiptPoItemRelationVOList, ReceiptPoItemRelation.class));

        return receiptSlipVOList;
    }

    /**
     * checkManifestItems mid1100 2024年4月18日
     */
    public boolean checkManifestItems(List<ReceiptManifestItemVO> receiptManifestItemVOList) {

        if (receiptManifestItemVOList == null || receiptManifestItemVOList.isEmpty()) {
            return true;
        }

        String siteId = receiptManifestItemVOList.get(0).getSiteId();

        Set<String> purchaseOrderNos = receiptManifestItemVOList.stream().map(ReceiptManifestItemVO::getPurchaseOrderNo).collect(Collectors.toSet());
        // 查询得到相应的List<PurchaseOrderVO>
        List<PurchaseOrderVO> purchaseOrderVOList = BeanMapUtils.mapListTo(purchaseOrderRepo.findBySiteIdAndOrderNoIn(siteId, purchaseOrderNos), PurchaseOrderVO.class);
        if (purchaseOrderVOList.isEmpty()) {
            throw new BusinessCodedException("no any PurchaseOrderVO exist");
        }
        Map<String, PurchaseOrderVO> purchaseOrderVOMap = purchaseOrderVOList.stream().collect(Collectors.toMap(PurchaseOrderVO::getOrderNo, Function.identity()));

        // 查询得到相应的List<PurchaseOrderItemVO>
        Set<Long> purchaseOrderIds = purchaseOrderVOList.stream().map(PurchaseOrderVO::getPurchaseOrderId).collect(Collectors.toSet());
        Set<Long> orderProductIds = receiptManifestItemVOList.stream().map(ReceiptManifestItemVO::getOrderProductId).collect(Collectors.toSet());

        List<PurchaseOrderItemVO> purchaseOrderItemVOList = BeanMapUtils.mapListTo(poItemRepo.findByPurchaseOrderIdInAndProductIdIn(purchaseOrderIds, orderProductIds), PurchaseOrderItemVO.class);
        if (purchaseOrderItemVOList.isEmpty()) {
            throw new BusinessCodedException("no any PurchaseOrderItemVO exist");
        }
        Map<String, PurchaseOrderItemVO> purchaseOrderItemVOMap = new HashMap<>();
        for (PurchaseOrderItemVO poItem : purchaseOrderItemVOList) {
            String key = poItem.getSiteId() + "#" + poItem.getPurchaseOrderId() + "#" + poItem.getProductId();
            purchaseOrderItemVOMap.put(key, poItem);
        }

        // check item
        List<Boolean> existErrorLine = new ArrayList<>();
        for (ReceiptManifestItemVO item : receiptManifestItemVOList) {
            // Clear the error info in item.
            item.setErrorInfo("");
            item.setErrorFlag("");

            StringBuilder errorInfo = new StringBuilder();

            if (!purchaseOrderVOMap.containsKey(item.getPurchaseOrderNo())) {
                errorInfo.append(POERROR).append(CommonConstants.CHAR_SEMICOLON);
            } else {
                PurchaseOrderVO purchaseOrderVO = purchaseOrderVOMap.get(item.getPurchaseOrderNo());
                String itemKey = item.getSiteId() + "#" + purchaseOrderVO.getPurchaseOrderId() + "#" + item.getOrderProductId();
                if (!purchaseOrderItemVOMap.containsKey(itemKey)) {
                    errorInfo.append(ODPARTERROR).append(CommonConstants.CHAR_SEMICOLON);
                } else {
                    PurchaseOrderItemVO purchaseOrderItemVO = purchaseOrderItemVOMap.get(itemKey);
                    if (item.getReceiptQty() != null && item.getReceiptQty().compareTo(purchaseOrderItemVO.getOnPurchaseQty()) > 0) {
                        errorInfo.append(RECEIPTQTYERROR).append(CommonConstants.CHAR_SEMICOLON);
                    }
                }
            }
            if (item.getReceiptPrice() == null || item.getReceiptPrice().compareTo(BigDecimal.ZERO) <= 0) {
                errorInfo.append(RECEIPTCOSTERROR).append(CommonConstants.CHAR_SEMICOLON);
            }
            if (item.getReceiptQty() == null || item.getReceiptQty().compareTo(BigDecimal.ZERO) <= 0) {
                errorInfo.append(QTYERROR).append(CommonConstants.CHAR_SEMICOLON);
            }
            if (item.getReceiptProductId() == null) {
                errorInfo.append(RECEIPTPARTERROR).append(CommonConstants.CHAR_SEMICOLON);
            }
            if (item.getOrderProductId() == null) {
                errorInfo.append(ODPARTERROR).append(CommonConstants.CHAR_SEMICOLON);
            }
            if (item.getReceiptProductId() != null && !item.getReceiptProductId().equals(item.getOrderProductId())) {
                boolean checkResult = productMgr.checkPartsSupersedingRelation(item.getOrderProductId(), item.getReceiptProductId());
                if (!checkResult) {
                    errorInfo.append(SUPERSEDINGERROR).append(CommonConstants.CHAR_SEMICOLON);
                }
            }
            if (StringUtils.isNotEmpty(errorInfo.toString())) {
                item.setErrorFlag(CommonConstants.CHAR_Y);
                item.setErrorInfo(errorInfo.toString());
                existErrorLine.add(false);
            }
        }

        receiptManifestItemRepo.saveInBatch(BeanMapUtils.mapListTo(receiptManifestItemVOList, ReceiptManifestItem.class));

        return existErrorLine.isEmpty();
    }

    /**
     *
     * @param spManifestItemBOList
     */
    public void doManifestImports(List<SpManifestItemBO> spManifestItemBOList) {

        if (spManifestItemBOList == null || spManifestItemBOList.isEmpty()) {
            return;
        }

        //文件中的dealerCode若不存在于cmm_site_master表中，则剔除出去-> update by lijiajun
        Set<String> dealerCds = spManifestItemBOList.stream().map(o -> o.getDealerCode()).collect(Collectors.toSet());
        List<CmmSiteMasterVO> cmmSiteMasters = BeanMapUtils.mapListTo(cmmSiteMasterRepository.findBySiteCdInAndActiveFlag(dealerCds,CommonConstants.CHAR_Y),CmmSiteMasterVO.class);
        Set<String> siteIds = cmmSiteMasters.stream().map(o -> o.getSiteId()).collect(Collectors.toSet());
        spManifestItemBOList = spManifestItemBOList.stream()
                                                   .filter(model -> siteIds.contains(model.getDealerCode()))
                                                   .collect(Collectors.toList());

        List<ReceiptManifestVO> receiptManifestVOList = new ArrayList<>();
        List<ReceiptManifestItemVO> receiptManifestItemVOList = new ArrayList<>();

        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);
        String sysTime = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_H_M);

        String dealerCode = spManifestItemBOList.get(0).getDealerCode();
        Set<String> yourOrderNos = spManifestItemBOList.stream().map(SpManifestItemBO::getYourOrderNo).collect(Collectors.toSet());

        // 根据dealerCode+yourOrderNo找到相应的PurchaseOrderVO
        List<PurchaseOrderVO> poVOList = BeanMapUtils.mapListTo(purchaseOrderRepo.findBySiteIdAndOrderNoIn(dealerCode, yourOrderNos), PurchaseOrderVO.class);
        Map<String, PurchaseOrderVO> poVOMap = poVOList.stream().collect(Collectors.toMap(PurchaseOrderVO::getOrderNo, Function.identity()));

        // 根据supplier_id+facility_id，进行分组
        Map<String, List<PurchaseOrderVO>> suppAndFacGroupMap = poVOList.stream().collect(Collectors.groupingBy(v -> v.getSupplierId() + "#" + v.getFacilityId()));

        // MstProduct
        Set<String> partNoSet = spManifestItemBOList.stream().flatMap(item -> Stream.of(item.getPartNo(), item.getPartNoOrdered())).filter(Objects::nonNull).collect(Collectors.toSet());
        if (partNoSet.isEmpty()) {
            throw new BusinessCodedException("no partNoSet exist");
        }
        List<MstProductVO> partNoList = BeanMapUtils.mapListTo(mstProductRepo.findByProductCdInAndProductClassification(partNoSet, ProductClsType.PART.getCodeDbid()), MstProductVO.class);
        Map<String, MstProductVO> partNoMap = partNoList.stream().collect(Collectors.toMap(MstProductVO::getProductCd, Function.identity()));

        // MstOrganization
        MstOrganizationVO mstOrgInfo = BeanMapUtils.mapTo(mstOrgRepo.findBySiteIdAndOrganizationCd(dealerCode, dealerCode), MstOrganizationVO.class);
        if (mstOrgInfo == null) {
            throw new BusinessCodedException("no MstOrganization exist: " + dealerCode);
        }
        Long toOrganizationId = mstOrgInfo.getOrganizationId();
        // 循环
        for (String suppAndFacKey : suppAndFacGroupMap.keySet()) {

            Long supplierId = Long.valueOf(suppAndFacKey.split("#")[0]);
            Long facilityId = Long.valueOf(suppAndFacKey.split("#")[1]);

            List<PurchaseOrderVO> suppAndFacGroupList = suppAndFacGroupMap.get(suppAndFacKey);

            // 根据OrderNo过滤出符合group(supplier+facility)的List<SpManifestItemBO>
            Set<String> orderNos = suppAndFacGroupList.stream().map(PurchaseOrderVO::getOrderNo).collect(Collectors.toSet());
            List<SpManifestItemBO> groupManifestItemList = spManifestItemBOList.stream()
                    .filter(item -> orderNos.contains(item.getYourOrderNo())).collect(Collectors.toList());

            // 按照invoiceNo+shipmentNo分组
            Map<String, List<SpManifestItemBO>> invAndShipGroupMap = groupManifestItemList.stream().collect(Collectors.groupingBy(v -> v.getInvoiceNo() + "#" + v.getShipmentNo()));

            for(String invAndShipKey : invAndShipGroupMap.keySet()) {

                List<SpManifestItemBO> manifestItemList = invAndShipGroupMap.get(invAndShipKey);
                // 新建receiptManifest
                String shippedDate = manifestItemList.get(0).getShippedDate();
                Long receiptManifestId = buildReceiptManifestVO(receiptManifestVOList, sysDate, sysTime, dealerCode, toOrganizationId, supplierId, facilityId, invAndShipKey, shippedDate);
                // 新建receiptManifestItem
                buildReceiptManifestItemVO(receiptManifestItemVOList, dealerCode, poVOMap, partNoMap, manifestItemList, receiptManifestId);
            }
        }

        receiptManifestRepo.saveInBatch(BeanMapUtils.mapListTo(receiptManifestVOList, ReceiptManifest.class));

        checkManifestItems(receiptManifestItemVOList);
    }

    public void importDataForSD(List<SdManifestItemBO> sdManifestItemBOList,String siteId) {

        if (sdManifestItemBOList == null || sdManifestItemBOList.isEmpty()) {
            return;
        }

        //准备数据
        Set<String> frameNos = sdManifestItemBOList.stream().map(SdManifestItemBO::getFrameNo).collect(Collectors.toSet());
        Set<String> siteIDList = sdManifestItemBOList.stream().map(item -> item.getDealerCd().length() >= CommonConstants.INTEGER_FOUR
                                                                         ? item.getDealerCd().substring(0, 4)
                                                                         : item.getDealerCd())
                                                                         .collect(Collectors.toSet());
        Set<String> pointCdList = sdManifestItemBOList.stream().map(SdManifestItemBO::getConsigneeCd).collect(Collectors.toSet());

        Set<String> modelCds = sdManifestItemBOList.stream().map(item -> item.getModelCd() + item.getColorCd()).collect(Collectors.toSet());

        List<CmmSerializedProductVO> cmmSerializedProductVOs = BeanMapUtils.mapListTo(cmmSerializedProductRepo.findByFrameNoIn(frameNos), CmmSerializedProductVO.class);
        Map<String, CmmSerializedProductVO> frameNoMap = cmmSerializedProductVOs.stream().collect(Collectors.toMap(CmmSerializedProductVO::getFrameNo, Function.identity()));

        List<MstFacilityVO> mstFacilityVOs = BeanMapUtils.mapListTo(mstFacilityRepo.findBySiteIdInAndFacilityCdIn(siteIDList,pointCdList), MstFacilityVO.class);
        Map<String, MstFacilityVO> facilityMap = mstFacilityVOs.stream().collect(Collectors.toMap(
                                                                                    item -> item.getSiteId() + CommonConstants.CHAR_UNDERSCORE + item.getFacilityCd(),
                                                                                    item -> item
                                                                                ));
        //准备电池信息
        List<String> batteryIds = sdManifestItemBOList.stream()
                                                     .flatMap(item -> {
                                                         return Stream.of(item.getBatteryId1(), item.getBatteryId2());
                                                     }).toList();
        List<CmmBatteryVO> batteryList = BeanMapUtils.mapListTo(cmmBatteryRepo.findByBatteryNoIn(batteryIds), CmmBatteryVO.class);
        Map<String, CmmBatteryVO> batteryMap = batteryList.stream().collect(Collectors.toMap(CmmBatteryVO::getBatteryNo, Function.identity()));

        //准备经销商信息
        Set<String> siteIds = sdManifestItemBOList.stream().map(item -> {
            String dealerCd = item.getDealerCd();
            return dealerCd != null && dealerCd.length() >= CommonConstants.INTEGER_FOUR ? dealerCd.substring(CommonConstants.INTEGER_ZERO, CommonConstants.INTEGER_FOUR) : null;
        }).filter(dealerCd -> dealerCd != null).collect(Collectors.toSet());

        List<CmmSiteMasterVO> siteList = BeanMapUtils.mapListTo(cmmSiteMasterRepo.findBySiteIdInAndActiveFlag(siteIds, CommonConstants.CHAR_Y), CmmSiteMasterVO.class);
        Map<String, CmmSiteMasterVO> siteMap = siteList.stream().collect(Collectors.toMap(CmmSiteMasterVO::getSiteId, Function.identity()));

        //准备product信息
        List<MstProductVO> modelList = BeanMapUtils.mapListTo(mstProductRepo.findByProductCdInAndProductClassification(modelCds,ProductClsType.GOODS.getCodeDbid()), MstProductVO.class);
        Map<String, MstProductVO> modelCdMap = modelList.stream().collect(Collectors.toMap(MstProductVO::getProductCd, Function.identity()));

        //根据InternalDeliveryNoteNo准备ReceiptManifest信息
        Set<String> noteNO = sdManifestItemBOList.stream().map(SdManifestItemBO::getInternalDeliveryNoteNo).collect(Collectors.toSet());
        List<ReceiptManifestVO> receiptManifestList = BeanMapUtils.mapListTo(receiptManifestRepo.findBySiteIdAndSupplierShipmentNoIn(siteId, noteNO),ReceiptManifestVO.class);

        //供应商信息
        Set<String> supplierCds = sdManifestItemBOList.stream().map(SdManifestItemBO::getSupplierCd).collect(Collectors.toSet());
        List<CmmMstOrganizationVO> supplierCdList = BeanMapUtils.mapListTo(cmmMstOrganizationRepo.findByOrganizationCdIn(supplierCds), CmmMstOrganizationVO.class);
        Map<String, CmmMstOrganizationVO> supplierCdMap = supplierCdList.stream().collect(Collectors.toMap(CmmMstOrganizationVO::getOrganizationCd, Function.identity()));

        //数据校验
        ArrayList<String> frameNoKeys = new ArrayList<>();
        List<CmmSerializedProductVO> cmmSerializedProductVOList = BeanMapUtils.mapListTo(cmmSerializedProductRepo.findByFrameNoIn(frameNos), CmmSerializedProductVO.class);
        Map<String, CmmSerializedProductVO> cmmSerProductMap = cmmSerializedProductVOList.stream()
                                                              .collect(Collectors.toMap(
                                                                  product -> product.getFrameNo() + "-" + product.getSiteId(),
                                                                  Function.identity()
                                                              ));
        for(SdManifestItemBO item:sdManifestItemBOList) {
            //check1 车型不存在
            if(ObjectUtils.isEmpty(modelCdMap.get(item.getModelCd() + item.getColorCd()))) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303"
                                               , new Object[]{CodedMessageUtils.getMessage("label.modelCode")
                                               , item.getModelCd()
                                               , CodedMessageUtils.getMessage("label.tableProduct")}));
            }

            //check2 单价小于0
            if(item.getSalesPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("error.invalid.numericalMin"
                                               , new String[] {CodedMessageUtils.getMessage("label.retailSalesPriceExcludeVAT")
                                               , CommonConstants.CHAR_ZERO}));
            }

            //check3 供应商不存在
            if(ObjectUtils.isEmpty(supplierCdMap.get(item.getSupplierCd()))) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303"
                        , new Object[]{CodedMessageUtils.getMessage("label.supplier")
                        , item.getSupplierCd()
                        , CodedMessageUtils.getMessage("label.tableOrganizationInfo")}));
            }

            //check4 车辆重复
            if(!frameNoKeys.isEmpty() && frameNoKeys.contains(item.getFrameNo())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00301"
                                               , new Object[]{CodedMessageUtils.getMessage("label.frameNumber")}));
            }
            frameNoKeys.add(item.getFrameNo());

            //check5 车辆已卖给用户
            if(cmmSerProductMap.containsKey(item.getFrameNo()+"-"+(item.getDealerCd().substring(0, 4)))
                    && McSalesStatus.SALESTOUSER.equals(cmmSerProductMap.get(item.getFrameNo()+"-"+(item.getDealerCd().substring(0, 4))).getSalesStatus())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("error.frameNoSold"
                        , new Object[]{CodedMessageUtils.getMessage("label.frameNumber")
                        , item.getFrameNo()}));
            }

            //check6 车辆在其他经销商库存
            cmmSerializedProductVOList.stream()
                                      .filter(product -> product.getFrameNo().equals(item.getFrameNo())
                                              && !product.getSiteId().equals(item.getDealerCd().substring(0, 4)))
                                      .findFirst()
                                      .ifPresent(product -> {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("error.frameNoForOtherDealers",
                        new Object[]{CodedMessageUtils.getMessage("label.frameNumber"), item.getFrameNo()}));
            });
        }

        //保存cmmSerializedProduct和CmmBattery
        List<CmmSerializedProductVO> cmmSerializedProductVOs2 = this.doManifestsImportCmmDb(sdManifestItemBOList, siteId, frameNoMap, facilityMap, modelCdMap, batteryMap);
        Map<String, CmmSerializedProductVO> cmmSerializedProductMap = cmmSerializedProductVOs2.stream().collect(Collectors.toMap(CmmSerializedProductVO::getFrameNo, Function.identity()));

        //整理manifest数据
        Set<String> outIssuedInvoiceNos = new HashSet<>(); //本地不需要导入的数据
        List<ReceiptManifestVO> outReimportManifests = new ArrayList<>(); //删除的数据
        this.checkIssuedWithInputManifests(sdManifestItemBOList,outIssuedInvoiceNos,outReimportManifests,receiptManifestList,siteMap);

        //删除已经存在数据库且依旧可以导入的数据
        this.deleteExistenceManifests(outReimportManifests);

        List<SdManifestItemBO> importData = sdManifestItemBOList.stream().filter(item -> !outIssuedInvoiceNos.contains(item.getInternalDeliveryNoteNo())).collect(Collectors.toList());

        //将过滤的数据导入至数据库中
        this.saveImportData(importData,siteId,facilityMap,modelCdMap,frameNoMap,cmmSerializedProductMap);

    }

    public void saveImportData(List<SdManifestItemBO> importData
                             , String siteId
                             , Map<String, MstFacilityVO> facilityMap
                             , Map<String, MstProductVO> modelCdMap
                             , Map<String, CmmSerializedProductVO> frameNoMap
                             , Map<String, CmmSerializedProductVO> cmmSerializedProductMap){

        LocalDate localDate = LocalDate.now(); // 获取当前日期
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD); // 定义yyyyMMDD格式
        String formattedDate = localDate.format(formatterDate); // 格式化日期

        LocalDateTime localDateTime = LocalDateTime.now(); // 获取当前日期时间
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_H_M_S); // 定义HH:mm:ss格式
        String formattedTime = localDateTime.format(formatterTime); // 格式化时间
        //由于设计改定从MstOrganization变为从CmmMstOrganization拿值。这里site_id固定为666N，此时不再需要传递set<String> siteIds过来。即只会拿固定一条数据
        CmmMstOrganizationVO mstOrganizationVO = BeanMapUtils.mapTo(cmmMstOrganizationRepo.findBySiteIdAndOrganizationCd(CommonConstants.CHAR_DEFAULT_SITE_ID, CommonConstants.CHAR_DEFAULT_SITE_ID), CmmMstOrganizationVO.class);

        //新增ReceiptManifest
        ArrayList<ReceiptManifestVO> receiptManifestVOs = new ArrayList<>();
        ArrayList<String> manifestKeys = new ArrayList<>();
        Map<String,Long> manifestMap = new HashMap<>();
        for(SdManifestItemBO item : importData) {

            //根据pointId和noteNo进行分类
            if(manifestKeys.contains(item.getConsigneeCd()+CommonConstants.CHAR_UNDERSCORE+item.getInternalDeliveryNoteNo())){
                continue;
            }
            manifestKeys.add(item.getConsigneeCd()+CommonConstants.CHAR_UNDERSCORE+item.getInternalDeliveryNoteNo());

            String originaSiteId = item.getDealerCd().substring(CommonConstants.INTEGER_ZERO, CommonConstants.INTEGER_FOUR);
            MstFacilityVO mstFacilityVO = facilityMap.get(originaSiteId+CommonConstants.CHAR_UNDERSCORE+item.getConsigneeCd());

            ReceiptManifestVO manifest = ReceiptManifestVO.create();
            manifest.setSiteId(siteId);
            manifest.setImportDate(formattedDate);
            manifest.setImportTime(formattedTime);
            manifest.setSupplierShipmentNo(item.getInternalDeliveryNoteNo());
            manifest.setSupplierShippedDate(item.getShippingDate());
            manifest.setManifestStatus(ManifestStatus.WAITING_ISSUE.getCodeDbid());
            manifest.setProductClassification(ProductClsType.GOODS.getCodeDbid());
            manifest.setFromOrganization(mstOrganizationVO.getOrganizationId());
            manifest.setToFacilityId(mstFacilityVO.getFacilityId());

            receiptManifestVOs.add(manifest);
            manifestMap.put(item.getConsigneeCd()+CommonConstants.CHAR_UNDERSCORE+item.getInternalDeliveryNoteNo(), manifest.getReceiptManifestId());
        }
        receiptManifestRepo.saveInBatch(BeanMapUtils.mapListTo(receiptManifestVOs, ReceiptManifest.class));

        //新增ReceiptManifestItem
        ArrayList<ReceiptManifestItemVO> receiptManifestItemVOs = new ArrayList<>();
        ArrayList<String> manifestItemKeys = new ArrayList<>();
        Map<String,Long> manifestItemMap = new HashMap<>();

        //判断重复次数
        Map<String, BigDecimal> duplicatesCountMap = importData.stream().collect(Collectors.groupingBy(
                                                item -> item.getConsigneeCd() + CommonConstants.CHAR_UNDERSCORE
                                                 + item.getInternalDeliveryNoteNo() + CommonConstants.CHAR_UNDERSCORE + item.getModelCd()
                                                 + CommonConstants.CHAR_UNDERSCORE + item.getColorCd(),
                                                 Collectors.reducing(BigDecimal.ZERO, e -> BigDecimal.ONE, BigDecimal::add)));

        for(SdManifestItemBO item : importData) {

            //根据pointId,noteNo,ModelCd进行分类
            if(manifestItemKeys.contains(item.getConsigneeCd()+CommonConstants.CHAR_UNDERSCORE
                                       + item.getInternalDeliveryNoteNo()+CommonConstants.CHAR_UNDERSCORE
                                       + item.getModelCd()+CommonConstants.CHAR_UNDERSCORE
                                       + item.getColorCd())) {
                continue;
            }
            manifestItemKeys.add(item.getConsigneeCd()+CommonConstants.CHAR_UNDERSCORE
                               + item.getInternalDeliveryNoteNo()+CommonConstants.CHAR_UNDERSCORE
                               + item.getModelCd()+CommonConstants.CHAR_UNDERSCORE
                               + item.getColorCd());

            Long receiptManifestId = manifestMap.get(item.getConsigneeCd()+CommonConstants.CHAR_UNDERSCORE+item.getInternalDeliveryNoteNo());
            MstProductVO mstProductVO = modelCdMap.get(item.getModelCd()+item.getColorCd());
            ReceiptManifestItemVO manifestItem = ReceiptManifestItemVO.create();
            manifestItem.setReceiptManifestId(receiptManifestId);
            manifestItem.setSiteId(siteId);
            manifestItem.setReceiptProductCd(item.getModelCd()+item.getColorCd());
            manifestItem.setReceiptProductId(mstProductVO.getProductId());
            manifestItem.setReceiptQty(duplicatesCountMap.get(item.getConsigneeCd()+CommonConstants.CHAR_UNDERSCORE
                                     + item.getInternalDeliveryNoteNo()+CommonConstants.CHAR_UNDERSCORE
                                     + item.getModelCd()+CommonConstants.CHAR_UNDERSCORE
                                     + item.getColorCd()));
            manifestItem.setReceiptPrice(item.getSalesPrice());

            //check 可能需要后续调整,这里的验证提前到初始检测数据时进行，不对error_info列进行报错提示
            receiptManifestItemVOs.add(manifestItem);
            manifestItemMap.put(item.getConsigneeCd()+CommonConstants.CHAR_UNDERSCORE
                              + item.getInternalDeliveryNoteNo()+CommonConstants.CHAR_UNDERSCORE
                              + item.getModelCd()+CommonConstants.CHAR_UNDERSCORE
                              + item.getColorCd(), manifestItem.getReceiptManifestItemId());
        }
        receiptManifestItemRepo.saveInBatch(BeanMapUtils.mapListTo(receiptManifestItemVOs, ReceiptManifestItem.class));

        //新增serialized_product
        ArrayList<SerializedProductVO> serializedProductVOs = new ArrayList<>();
        for(SdManifestItemBO item : importData) {
            SerializedProductVO serializedProduct = SerializedProductVO.create();
            serializedProduct.setSiteId(siteId);
            serializedProduct.setReceivedDate(formattedDate);//当前时间 本方法开始时定义
            serializedProduct.setManufacturingDate(item.getAssemblyDate());
            serializedProduct.setProductId(modelCdMap.get(item.getModelCd()+item.getColorCd()).getProductId());
            serializedProduct.setFacilityId(facilityMap.get(siteId + CommonConstants.CHAR_UNDERSCORE + item.getConsigneeCd()).getFacilityId());
            serializedProduct.setFrameNo(item.getFrameNo());
            serializedProduct.setBarCode(item.getBarCd());
            serializedProduct.setEngineNo(item.getEngineNo());
            serializedProduct.setFromDate(item.getAssemblyDate());
            serializedProduct.setToDate(CommonConstants.END_DATE);
            serializedProduct.setEvFlag(item.getEvFlag());
            serializedProduct.setQualityStatus(SerialProQualityStatus.NORMAL);
            serializedProduct.setStockStatus(SerialproductStockStatus.ONTRANSFER);
            serializedProduct.setCmmSerializedProductId(frameNoMap.containsKey(item.getFrameNo())
                                                       ?frameNoMap.get(item.getFrameNo()).getSerializedProductId()
                                                       :cmmSerializedProductMap.get(item.getFrameNo()).getSerializedProductId());
            serializedProductVOs.add(serializedProduct);
        }
        serializedProductRepo.saveInBatch(BeanMapUtils.mapListTo(serializedProductVOs, SerializedProduct.class));

        //新增battery
        List<String> batteryIds = importData.stream()
                                  .flatMap(item -> {
                                      return Stream.of(item.getBatteryId1(), item.getBatteryId2());
                                  }).toList();
        List<BatteryVO> batteryList = BeanMapUtils.mapListTo(batteryRepo.findByBatteryNoIn(batteryIds), BatteryVO.class);
        Map<String, BatteryVO> batteryMap = batteryList.stream().collect(Collectors.toMap(BatteryVO::getBatteryNo, Function.identity()));
        Map<String, SerializedProductVO> frameNoMaps = serializedProductVOs.stream().collect(Collectors.toMap(SerializedProductVO::getFrameNo, Function.identity()));
        ArrayList<BatteryVO> batteryVOs = new ArrayList<>();
        for(SdManifestItemBO item : importData) {

            String originaSiteId = item.getDealerCd().substring(CommonConstants.INTEGER_ZERO,CommonConstants.INTEGER_FOUR);
            SerializedProductVO frameVO = frameNoMaps.get(item.getFrameNo());

            BatteryVO battery1 = batteryMap.get(item.getBatteryId1());
            //电池1是否存在
            if (!ObjectUtils.isEmpty(battery1)) {
                battery1.setSiteId(originaSiteId);
                battery1.setBatteryStatus(PJConstants.SerialproductStockStatus.ONTRANSFER);
                battery1.setOriginalFlag(CommonConstants.CHAR_ZERO);
                battery1.setPositionSign(BatteryType.TYPE1.getCodeDbid());
                battery1.setSerializedProductId(frameVO.getSerializedProductId());
                batteryVOs.add(battery1);
            }else if(battery1 == null && item.getBatteryId1().strip().length()>0){
                battery1 = new BatteryVO();
                battery1.setBatteryNo(item.getBatteryId1());
                battery1.setBatteryCd(item.getBatteryCd1());
                battery1.setBatteryStatus(PJConstants.SerialproductStockStatus.ONTRANSFER);
                battery1.setSiteId(originaSiteId);
                battery1.setOriginalFlag(CommonConstants.CHAR_ZERO);
                battery1.setSerializedProductId(frameVO.getSerializedProductId());
                battery1.setFromDate(formattedDate);
                battery1.setToDate(CommonConstants.MAX_DATE);
                battery1.setPositionSign(BatteryType.TYPE1.getCodeDbid());
                battery1.setProductId(modelCdMap.get(item.getModelCd()+item.getColorCd()).getProductId());
                batteryVOs.add(battery1);
            }

            BatteryVO battery2 = batteryMap.get(item.getBatteryId2());
            //电池2是否存在
            if (!ObjectUtils.isEmpty(battery2)) {
                battery2.setSiteId(originaSiteId);
                battery2.setBatteryStatus(PJConstants.SerialproductStockStatus.ONTRANSFER);
                battery2.setOriginalFlag(CommonConstants.CHAR_ZERO);
                battery2.setPositionSign(BatteryType.TYPE2.getCodeDbid());
                battery2.setSerializedProductId(frameVO.getSerializedProductId());
                batteryVOs.add(battery2);
            }else if(battery2 == null && item.getBatteryId2().strip().length()>0){
                battery2 = new BatteryVO();
                battery2.setBatteryNo(item.getBatteryId2());
                battery2.setBatteryCd(item.getBatteryCd2());
                battery2.setBatteryStatus(PJConstants.SerialproductStockStatus.ONTRANSFER);
                battery2.setSiteId(originaSiteId);
                battery2.setOriginalFlag(CommonConstants.CHAR_ZERO);
                battery2.setSerializedProductId(frameVO.getSerializedProductId());
                battery2.setFromDate(formattedDate);
                battery2.setToDate(CommonConstants.MAX_DATE);
                battery2.setPositionSign(BatteryType.TYPE2.getCodeDbid());
                battery2.setProductId(modelCdMap.get(item.getModelCd()+item.getColorCd()).getProductId());
                batteryVOs.add(battery2);
            }
        }
        batteryRepo.saveInBatch(BeanMapUtils.mapListTo(batteryVOs, Battery.class));

        ArrayList<ReceiptManifestSerializedItemVO> receiptManifestSerializedItemVOs = new ArrayList<>();
        ArrayList<String> manifestSerializedItemKeys = new ArrayList<>();
        for(SdManifestItemBO item : importData) {

            //根据pointId,noteNo,ModelCd,frameNo进行分类
            if(manifestSerializedItemKeys.contains(item.getConsigneeCd()+CommonConstants.CHAR_UNDERSCORE
                                                  +item.getInternalDeliveryNoteNo()+CommonConstants.CHAR_UNDERSCORE
                                                  +item.getModelCd()+CommonConstants.CHAR_UNDERSCORE
                                                  +item.getColorCd()+CommonConstants.CHAR_UNDERSCORE
                                                  +item.getFrameNo())){
                continue;
            }
            manifestSerializedItemKeys.add(item.getConsigneeCd()+CommonConstants.CHAR_UNDERSCORE
                                          +item.getInternalDeliveryNoteNo()+CommonConstants.CHAR_UNDERSCORE
                                          +item.getModelCd()+CommonConstants.CHAR_UNDERSCORE
                                          +item.getColorCd()+CommonConstants.CHAR_UNDERSCORE
                                          +item.getFrameNo());

            ReceiptManifestSerializedItemVO vo = ReceiptManifestSerializedItemVO.create();
            vo.setSiteId(siteId);
            vo.setReceiptManifestItemId(manifestItemMap.get(item.getConsigneeCd()+CommonConstants.CHAR_UNDERSCORE
                                                            +item.getInternalDeliveryNoteNo()+CommonConstants.CHAR_UNDERSCORE
                                                            +item.getModelCd()+CommonConstants.CHAR_UNDERSCORE
                                                            +item.getColorCd()));
            vo.setSerializedProductId(frameNoMaps.get(item.getFrameNo()).getSerializedProductId());
            receiptManifestSerializedItemVOs.add(vo);
        }
        receiptManifestSerializedItemRepo.saveInBatch(BeanMapUtils.mapListTo(receiptManifestSerializedItemVOs, ReceiptManifestSerializedItem.class));

        //给CRM 送manifest 后续需修改 改用其他方式
        ArrayList<QueueApiDataVO> queueApiDataVOs = new ArrayList<>();
        ArrayList<String> frameNoKeys = new ArrayList<>();
        for(SdManifestItemBO item : importData) {
            //根据ModelCd进行分类
            if(frameNoKeys.contains(item.getModelCd())){
                continue;
            }
            frameNoKeys.add(item.getModelCd());

            QueueApiDataVO queueApiDataVO = QueueApiDataVO.create();
            queueApiDataVO.setSiteId(siteId);
            queueApiDataVO.setApiCd(CrmApiType.DMSSDMANIFEST.getCodeDbid());
            queueApiDataVO.setApiDate(formattedDate);
            queueApiDataVO.setProcessKey(CommonConstants.INSERT);
            queueApiDataVO.setActionType(CommonConstants.AUTO);
            queueApiDataVO.setSendTimes(CommonConstants.INTEGER_ZERO);
            queueApiDataVO.setStatus(QueueStatus.WAITINGSEND.getCodeDbid());
            queueApiDataVO.setTableNm(CommonConstants.CMMSERIALIZEDPRODUCTINFO);
            queueApiDataVO.setTablePk(frameNoMap.containsKey(item.getFrameNo())
                                     ?frameNoMap.get(item.getFrameNo()).getSerializedProductId()
                                     :cmmSerializedProductMap.get(item.getFrameNo()).getSerializedProductId());
            queueApiDataVOs.add(queueApiDataVO);
        }
        queueApiDataRepo.saveInBatch(BeanMapUtils.mapListTo(queueApiDataVOs, QueueApiData.class));

    }

    public void deleteExistenceManifests(List<ReceiptManifestVO>  deleteManifests){

        if(deleteManifests ==null || deleteManifests.isEmpty()) {
            return;
        }
        //找到item
        Set<Long> manifestId = deleteManifests.stream().map(ReceiptManifestVO::getReceiptManifestId).collect(Collectors.toSet());

        List<ReceiptManifestItemVO> deleteItemList = BeanMapUtils.mapListTo(receiptManifestItemRepo.findByReceiptManifestIdIn(manifestId),ReceiptManifestItemVO.class);

        //找到serializedProduct
        Set<Long> manifestItemId = deleteItemList.stream().map(ReceiptManifestItemVO::getReceiptManifestItemId).collect(Collectors.toSet());
        List<ReceiptManifestSerializedItemVO> relationItemList = BeanMapUtils.mapListTo(receiptManifestSerializedItemRepo.findByReceiptManifestItemIdIn(manifestItemId),ReceiptManifestSerializedItemVO.class);
        Set<Long> serializedProductIds = relationItemList.stream().map(ReceiptManifestSerializedItemVO::getSerializedProductId).collect(Collectors.toSet());
        List<SerializedProductVO> serializedProductList = BeanMapUtils.mapListTo(serializedProductRepo.findBySerializedProductIdIn(serializedProductIds),SerializedProductVO.class);

        receiptManifestSerializedItemRepo.deleteAllInBatch(BeanMapUtils.mapListTo(relationItemList,ReceiptManifestSerializedItem.class));
        receiptManifestRepo.deleteAllInBatch(BeanMapUtils.mapListTo(deleteManifests,ReceiptManifest.class));
        receiptManifestItemRepo.deleteAllInBatch(BeanMapUtils.mapListTo(deleteItemList,ReceiptManifestItem.class));
        serializedProductRepo.deleteAllInBatch(BeanMapUtils.mapListTo(serializedProductList,SerializedProduct.class));

    }

    public void checkIssuedWithInputManifests(List<SdManifestItemBO> manifests, Set<String> outIssuedInvoiceNos,List<ReceiptManifestVO>  outReimportManifests,List<ReceiptManifestVO> receiptManifestList,Map<String, CmmSiteMasterVO> siteMap){

        List<Long> fromOrganizations = receiptManifestList.stream()
                .map(ReceiptManifestVO::getFromOrganization)
                .toList();

        List<CmmMstOrganizationVO> cmmMstOrganizaitonVOs = BeanMapUtils.mapListTo(cmmMstOrganizationRepo.findByOrganizationIdIn(fromOrganizations), CmmMstOrganizationVO.class);
        Map<Long, String> organizationMap = cmmMstOrganizaitonVOs.stream()
                .collect(Collectors.toMap(
                    CmmMstOrganizationVO::getOrganizationId,
                    CmmMstOrganizationVO::getOrganizationCd
                ));

        for (SdManifestItemBO item : manifests) {
            String originaSiteId = item.getDealerCd().substring(CommonConstants.INTEGER_ZERO, CommonConstants.INTEGER_FOUR);
            //判断上传的经销商是否存在

            if (!ObjectUtils.isEmpty(siteMap.get(originaSiteId))) {
                for(ReceiptManifestVO manifestVO:receiptManifestList) {
                    if(!ObjectUtils.isEmpty(manifestVO)) {
                        //判断manifest的供应商是否和接收到供应商一致
                        if(StringUtils.equals(item.getSupplierCd(), organizationMap.get(manifestVO.getFromOrganization()))){
                            //判断判断当前manifest是否已经承认，承认则不需要导入
                            if(StringUtils.equals(manifestVO.getManifestStatus(),ManifestStatus.ISSUED.getCodeDbid())){
                                outIssuedInvoiceNos.add(manifestVO.getSupplierShipmentNo());
                            }else{
                                //导入前需要先把数据库的数据删除掉，避免重复导入
                                outReimportManifests.add(manifestVO);
                            }
                        }
                    }else{
                        //代表是新增的manifest，需要导入，无需删除数据
                        continue;
                    }
                }
            }
        }
    }


    public List<CmmSerializedProductVO> doManifestsImportCmmDb(List<SdManifestItemBO> manifests,String siteId,Map<String, CmmSerializedProductVO> frameNoMap,Map<String, MstFacilityVO> facilityMap,Map<String, MstProductVO> modelCdMap,Map<String, CmmBatteryVO> batteryMap) {

        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);
        List<CmmSerializedProductVO> cmmSerializedProductData = new ArrayList<>();
        List<CmmBatteryVO> cmmBatteryData = new ArrayList<>();
        for (SdManifestItemBO item : manifests) {

            String originaSiteId = item.getDealerCd().substring(CommonConstants.INTEGER_ZERO, CommonConstants.INTEGER_FOUR);
            //根据frameNo判断车辆是否存在
            CmmSerializedProductVO frameVO = frameNoMap.get(item.getFrameNo());
            //更新 or 新增
            if (!ObjectUtils.isEmpty(frameVO)) {
                frameVO.setSiteId(siteId);
                frameVO.setReceivedDate(sysDate);
                frameVO.setOriginalSiteId(originaSiteId);
                frameVO.setOriginalFacilityId(facilityMap.get(originaSiteId+CommonConstants.CHAR_UNDERSCORE+item.getConsigneeCd()).getFacilityId());
                frameVO.setProductId(modelCdMap.get(item.getModelCd()+item.getColorCd()).getProductId());

            }else{
                frameVO = CmmSerializedProductVO.create();
                frameVO.setSiteId(siteId);
                frameVO.setReceivedDate(sysDate);
                frameVO.setManufacturingDate(item.getAssemblyDate());
                frameVO.setProductId(modelCdMap.get(item.getModelCd()+item.getColorCd()).getProductId());

                frameVO.setOriginalSiteId(originaSiteId);
                frameVO.setFacilityId(facilityMap.get(originaSiteId+CommonConstants.CHAR_UNDERSCORE+item.getConsigneeCd()).getFacilityId());
                frameVO.setOriginalFacilityId(facilityMap.get(originaSiteId+CommonConstants.CHAR_UNDERSCORE+item.getConsigneeCd()).getFacilityId());
                frameVO.setFrameNo(item.getFrameNo());
                frameVO.setBarCode(item.getBarCd());
                frameVO.setEngineNo(item.getEngineNo());
                frameVO.setFromDate(sysDate);
                frameVO.setToDate(CommonConstants.MAX_DATE);

            }
            frameVO.setEvFlag(item.getEvFlag());

            //判断当前车不是库存车且不是已售车
            if(!StringUtils.equals(frameVO.getSalesStatus(), MstCodeConstants.McSalesStatus.STOCK) && !StringUtils.equals(frameVO.getSalesStatus(), MstCodeConstants.McSalesStatus.SALESTOUSER)){
                frameVO.setStockStatus(PJConstants.SerialproductStockStatus.ONTRANSFER);
            }
            cmmSerializedProductData.add(frameVO);

            CmmBatteryVO battery1 = batteryMap.get(item.getBatteryId1());
            //电池1是否存在
            if (!ObjectUtils.isEmpty(battery1)) {
                battery1.setSiteId(originaSiteId);
                battery1.setBatteryStatus(PJConstants.SerialproductStockStatus.ONTRANSFER);
                battery1.setOriginalFlag(CommonConstants.CHAR_ZERO);
                battery1.setPositionSign(BatteryType.TYPE1.getCodeDbid());
                battery1.setSerializedProductId(frameVO.getSerializedProductId());
                cmmBatteryData.add(battery1);
            }else if(battery1 == null && item.getBatteryId1().strip().length()>0){
                battery1 = new CmmBatteryVO();
                battery1.setBatteryNo(item.getBatteryId1());
                battery1.setBatteryCd(item.getBatteryCd1());
                battery1.setBatteryStatus(PJConstants.SerialproductStockStatus.ONTRANSFER);
                battery1.setSiteId(originaSiteId);
                battery1.setOriginalFlag(CommonConstants.CHAR_ZERO);
                battery1.setSerializedProductId(frameVO.getSerializedProductId());
                battery1.setFromDate(sysDate);
                battery1.setToDate(CommonConstants.MAX_DATE);
                battery1.setPositionSign(BatteryType.TYPE1.getCodeDbid());
                battery1.setProductId(modelCdMap.get(item.getModelCd()+item.getColorCd()).getProductId());
                cmmBatteryData.add(battery1);
            }

            CmmBatteryVO battery2 = batteryMap.get(item.getBatteryId2());
            //电池2是否存在
            if (!ObjectUtils.isEmpty(battery2)) {
                battery2.setSiteId(originaSiteId);
                battery2.setBatteryStatus(PJConstants.SerialproductStockStatus.ONTRANSFER);
                battery2.setOriginalFlag(CommonConstants.CHAR_ZERO);
                battery2.setPositionSign(BatteryType.TYPE2.getCodeDbid());
                battery2.setSerializedProductId(frameVO.getSerializedProductId());
                cmmBatteryData.add(battery2);
            }else if(battery2 != null && item.getBatteryId2().strip().length()>0){
                battery2 = new CmmBatteryVO();
                battery2.setBatteryNo(item.getBatteryId2());
                battery2.setBatteryCd(item.getBatteryCd2());
                battery2.setBatteryStatus(PJConstants.SerialproductStockStatus.ONTRANSFER);
                battery2.setSiteId(originaSiteId);
                battery2.setOriginalFlag(CommonConstants.CHAR_ZERO);
                battery2.setSerializedProductId(frameVO.getSerializedProductId());
                battery2.setFromDate(sysDate);
                battery2.setToDate(CommonConstants.MAX_DATE);
                battery2.setPositionSign(BatteryType.TYPE2.getCodeDbid());
                battery2.setProductId(modelCdMap.get(item.getModelCd()+item.getColorCd()).getProductId());
                cmmBatteryData.add(battery2);
            }
        }

        cmmSerializedProductRepo.saveInBatch(BeanMapUtils.mapListTo(cmmSerializedProductData,CmmSerializedProduct.class));
        cmmBatteryRepo.saveInBatch(BeanMapUtils.mapListTo(cmmBatteryData,CmmBattery.class));

        return cmmSerializedProductData;
    }

    private void buildReceiptManifestItemVO(List<ReceiptManifestItemVO> receiptManifestItemVOList
                                            , String dealerCode
                                            , Map<String, PurchaseOrderVO> poVOMap
                                            , Map<String, MstProductVO> partNoMap
                                            , List<SpManifestItemBO> manifestItemList
                                            , Long receiptManifestId) {

        for (SpManifestItemBO item : manifestItemList) {

            ReceiptManifestItemVO receiptManifestItemVO = new ReceiptManifestItemVO();

            receiptManifestItemVO.setSiteId(dealerCode);
            receiptManifestItemVO.setReceiptManifestId(receiptManifestId);
            receiptManifestItemVO.setReceiptProductCd(item.getPartNo());
            receiptManifestItemVO.setReceiptProductId(partNoMap.containsKey(item.getPartNo())? partNoMap.get(item.getPartNo()).getProductId() : -1);
            receiptManifestItemVO.setOrderProductCd(item.getPartNoOrdered());
            receiptManifestItemVO.setOrderProductId(partNoMap.containsKey(item.getPartNoOrdered())? partNoMap.get(item.getPartNoOrdered()).getProductId() : -1);
            receiptManifestItemVO.setReceiptQty(item.getPackingQty());
            receiptManifestItemVO.setReceiptPrice(item.getReceiptPrice());
            receiptManifestItemVO.setStoredQty(BigDecimal.ZERO);
            receiptManifestItemVO.setFrozenQty(BigDecimal.ZERO);
            receiptManifestItemVO.setPurchaseOrderNo(item.getYourOrderNo());
            receiptManifestItemVO.setOrderPriorityType(poVOMap.containsKey(item.getYourOrderNo())? poVOMap.get(item.getYourOrderNo()).getOrderPriorityType() : StringUtils.EMPTY);
            receiptManifestItemVO.setManifestItemStatus(PJConstants.ManifestItemStatus.WAITING_RECEIVE.getCodeDbid());
            receiptManifestItemVO.setOrderMethodType(poVOMap.containsKey(item.getYourOrderNo())? poVOMap.get(item.getYourOrderNo()).getOrderMethodType() : StringUtils.EMPTY);
            receiptManifestItemVO.setCaseNo(item.getCaseNo());

            receiptManifestItemVOList.add(receiptManifestItemVO);
        }
    }

    private Long buildReceiptManifestVO(List<ReceiptManifestVO> receiptManifestVOList
                                        , String sysDate, String sysTime
                                        , String dealerCode
                                        , Long toOrganizationId
                                        , Long supplierId
                                        , Long facilityId
                                        , String invAndShipKey
                                        , String shippedDate) {

        ReceiptManifestVO receiptManifestVO = ReceiptManifestVO.create();

        receiptManifestVO.setSiteId(dealerCode);
        receiptManifestVO.setImportDate(sysDate);
        receiptManifestVO.setImportTime(sysTime);
        receiptManifestVO.setSupplierInvoiceNo(invAndShipKey.split("#")[0]);
        receiptManifestVO.setSupplierShipmentNo(invAndShipKey.split("#")[1]);
        receiptManifestVO.setSupplierShippedDate(shippedDate);
        receiptManifestVO.setFromOrganization(supplierId);
        receiptManifestVO.setToOrganization(toOrganizationId);
        receiptManifestVO.setToFacilityId(facilityId);
        //receiptManifestVO.setManifestStatus(ManifestStatus.WAITING_RECEIVE.getCodeDbid());
        receiptManifestVO.setProductClassification(ProductClsType.PART.getCodeDbid());
        receiptManifestVO.setUpdateCount(0);

        receiptManifestVOList.add(receiptManifestVO);

        return receiptManifestVO.getReceiptManifestId();
    }

    private ReceiptPoItemRelationVO buildReceiptPoItemRelationVO_SupplierInvoice(Map<String, PurchaseOrderVO> purchaseOrderVOMap
                                                                                , Map<String, PurchaseOrderItemVO> purchaseOrderItemVOMap
                                                                                , ReceiptManifestItemVO item
                                                                                , ReceiptManifestVO receiptManifestVO
                                                                                , Long receiptSlipItemId) {

        ReceiptPoItemRelationVO receiptPoItemRelaVO = null;

        String poKey = item.getSiteId() + "#" + item.getPurchaseOrderNo() + "#" + receiptManifestVO.getToFacilityId();
        if (purchaseOrderVOMap.containsKey(poKey)) {
            PurchaseOrderVO purchaseOrderVO = purchaseOrderVOMap.get(poKey);

            String poItemKey = purchaseOrderVO.getPurchaseOrderId() + "#" + item.getOrderProductId();
            if (purchaseOrderItemVOMap.containsKey(poItemKey)) {
                PurchaseOrderItemVO purchaseOrderItemVO = purchaseOrderItemVOMap.get(poItemKey);

                receiptPoItemRelaVO = new ReceiptPoItemRelationVO();

                receiptPoItemRelaVO.setSiteId(item.getSiteId());
                receiptPoItemRelaVO.setFacilityId(receiptManifestVO.getToFacilityId());
                receiptPoItemRelaVO.setOrderItemId(purchaseOrderItemVO.getPurchaseOrderItemId());
                receiptPoItemRelaVO.setReceiptSlipItemId(receiptSlipItemId);
                receiptPoItemRelaVO.setPurchaseOrderNo(item.getPurchaseOrderNo());
                receiptPoItemRelaVO.setSupplierInvoiceNo(receiptManifestVO.getSupplierInvoiceNo());
                receiptPoItemRelaVO.setReceiptQty(item.getReceiptQty());
                receiptPoItemRelaVO.setUpdateCount(0);
            }
        }

        return receiptPoItemRelaVO;
    }

    private ReceiptSlipItemVO buildReceiptSlipItemVO_SupplierInvoice(ReceiptManifestItemVO item
                                                                    , ReceiptManifestVO receiptManifestVO
                                                                    , Long receiptSlipId
                                                                    , Map<Long, MstProductVO> receiptProductInfoMap) {

        ReceiptSlipItemVO receiptSlipItemVO = ReceiptSlipItemVO.create();

        receiptSlipItemVO.setSiteId(item.getSiteId());
        receiptSlipItemVO.setReceiptSlipId(receiptSlipId);
        receiptSlipItemVO.setProductId(item.getReceiptProductId());
        receiptSlipItemVO.setProductCd(item.getReceiptProductCd());
        if (receiptProductInfoMap.containsKey(item.getReceiptProductId())) {
            receiptSlipItemVO.setProductNm(receiptProductInfoMap.get(item.getReceiptProductId()).getLocalDescription());
        }
        receiptSlipItemVO.setReceiptQty(item.getReceiptQty());
        receiptSlipItemVO.setFrozenQty(BigDecimal.ZERO);
        receiptSlipItemVO.setReceiptPrice(item.getReceiptPrice());
        receiptSlipItemVO.setSupplierInvoiceNo(receiptManifestVO.getSupplierInvoiceNo());
        receiptSlipItemVO.setPurchaseOrderNo(item.getPurchaseOrderNo());
        receiptSlipItemVO.setCaseNo(item.getCaseNo());
        receiptSlipItemVO.setProductClassification(ProductClsType.PART.getCodeDbid());
        receiptSlipItemVO.setUpdateCount(0);

        return receiptSlipItemVO;
    }

    private Long buildReceiptSlipVO_SupplierInvoice(String receiptSlipType
                                                    , Map<Long, ReceiptSlipVO> buildReceiptSlipVOMap
                                                    , ReceiptManifestItemVO item
                                                    , ReceiptManifestVO receiptManifestVO
                                                    , String sysDate) {

        ReceiptSlipVO receiptSlipVO;
        BigDecimal amt;
        try {
            amt = item.getReceiptQty().multiply(item.getReceiptPrice());
        } catch (Exception e) {
            amt = BigDecimal.ZERO;
        }

        if (!buildReceiptSlipVOMap.containsKey(item.getReceiptManifestId())) {
            receiptSlipVO = ReceiptSlipVO.create();

            String slipNo = generateNoMgr.generateSlipNo(receiptManifestVO.getSiteId(), receiptManifestVO.getToFacilityId());

            // Long receiptSlipId = worker.nextId();
            // receiptSlipVO.setReceiptSlipId(receiptSlipId);
            receiptSlipVO.setSiteId(receiptManifestVO.getSiteId());
            receiptSlipVO.setSlipNo(slipNo);
            receiptSlipVO.setReceivedDate(sysDate);
            receiptSlipVO.setReceiptSlipTotalAmt(amt);
            receiptSlipVO.setReceivedFacilityId(receiptManifestVO.getToFacilityId());
            receiptSlipVO.setReceivedOrganizationId(receiptManifestVO.getToOrganization());
            receiptSlipVO.setFromOrganizationId(receiptManifestVO.getFromOrganization());
            receiptSlipVO.setFromFacilityId(receiptManifestVO.getFromFacilityId());
            receiptSlipVO.setReceiptSlipStatus(ReceiptSlipStatus.RECEIPTED.getCodeDbid());
            receiptSlipVO.setInventoryTransactionType(receiptSlipType);
            receiptSlipVO.setProductClassification(ProductClsType.PART.getCodeDbid());
            receiptSlipVO.setUpdateCount(0);
        } else {
            receiptSlipVO = buildReceiptSlipVOMap.get(item.getReceiptManifestId());

            receiptSlipVO.setReceiptSlipTotalAmt(receiptSlipVO.getReceiptSlipTotalAmt().add(amt));
        }
        buildReceiptSlipVOMap.put(item.getReceiptManifestId(), receiptSlipVO);

        return receiptSlipVO.getReceiptSlipId();
    }

    private ReceiptSlipVO buildReceipSlipVO_SalesReturn(DeliveryOrderVO delieryOrder) {

        ReceiptSlipVO receiptSlipVO = ReceiptSlipVO.create();

        receiptSlipVO.setSiteId(delieryOrder.getSiteId());
        receiptSlipVO.setSlipNo(generateNoMgr.generateSlipNo(delieryOrder.getSiteId(), delieryOrder.getFromFacilityId()));
        receiptSlipVO.setReceivedDate(delieryOrder.getDeliveryOrderDate());
        receiptSlipVO.setReceiptSlipTotalAmt(delieryOrder.getTotalAmt());
        receiptSlipVO.setReceivedFacilityId(delieryOrder.getFromFacilityId());
        receiptSlipVO.setReceivedOrganizationId(delieryOrder.getFromOrganizationId());
        receiptSlipVO.setFromOrganizationId(delieryOrder.getToOrganizationId());
        receiptSlipVO.setFromFacilityId(delieryOrder.getToFacilityId());
        receiptSlipVO.setReceiptSlipStatus(ReceiptSlipStatus.RECEIPTED.getCodeDbid());
        receiptSlipVO.setInventoryTransactionType(delieryOrder.getInventoryTransactionType());
        receiptSlipVO.setProductClassification(ProductClsType.PART.getCodeDbid());

        return receiptSlipVO;
    }

    private List<ReceiptSlipItemVO> buildReceiptSlipItemVO_SalesReturn(Long receiptSlipId, List<DeliveryOrderItemVO> deliveryOrderItemsVO) {

        List<ReceiptSlipItemVO> receiptSlipItemVOList = new ArrayList<>();

        for (DeliveryOrderItemVO item : deliveryOrderItemsVO) {

            ReceiptSlipItemVO receiptSlipItemVO = new ReceiptSlipItemVO();

            receiptSlipItemVO.setSiteId(item.getSiteId());
            receiptSlipItemVO.setReceiptSlipId(receiptSlipId);
            receiptSlipItemVO.setProductId(item.getProductId());
            receiptSlipItemVO.setProductCd(item.getProductCd());
            receiptSlipItemVO.setProductNm(item.getProductNm());
            receiptSlipItemVO.setReceiptQty(item.getDeliveryQty());
            receiptSlipItemVO.setFrozenQty(BigDecimal.ZERO);
            receiptSlipItemVO.setReceiptPrice(item.getProductCost());
            receiptSlipItemVO.setProductClassification(ProductClsType.PART.getCodeDbid());

            receiptSlipItemVOList.add(receiptSlipItemVO);
        }

        return receiptSlipItemVOList;
    }
}