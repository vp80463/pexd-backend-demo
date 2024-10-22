package com.a1stream.unit.facade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.McSalesStatus;
import com.a1stream.common.constants.PJConstants.CostType;
import com.a1stream.common.constants.PJConstants.InOutType;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.ReceiptSlipStatus;
import com.a1stream.common.constants.PJConstants.SdStockStatus;
import com.a1stream.common.constants.PJConstants.SerialproductStockStatus;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.domain.bo.unit.SDM010601BO;
import com.a1stream.domain.form.unit.SDM010601Form;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmUnitPromotionItemVO;
import com.a1stream.domain.vo.InventoryTransactionVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductCostVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.ReceiptSerializedItemVO;
import com.a1stream.domain.vo.ReceiptSlipItemVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.domain.vo.SerializedProductTranVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.unit.service.SDM0106Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.CollectionUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
* 功能描述: Fast Receipt Report
*
* mid2287
* 2024年8月28日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/09/03   Wang Nan      New
*/
@Component
public class SDM0106Facade {

    @Resource
    private SDM0106Service sdm0106Service;

    @Resource
    private HelperFacade helperFacade;

    public List<SDM010601BO> getFastReceiptReportList(SDM010601Form form) {

        return this.dataEdit(sdm0106Service.getFastReceiptReportList(form));
    }

    private List<SDM010601BO> dataEdit(List<SDM010601BO> list) {

        Map<String, String> map = helperFacade.getMstCodeInfoMap(InventoryTransactionType.CODE_ID);

        list.forEach(bo -> {

            //codeDbid -> codeData1
            bo.setTransactionType(map.get(bo.getTransactionTypeCd()));

            //checkFlg控制checkBox是否勾选
            if (StringUtils.equals(ReceiptSlipStatus.STORED.getCodeDbid(), bo.getReceiptSlipStatus())) {

                //若receiptStatus = S049STORED, 设置checkFlg为Y
                bo.setCheckFlg(CommonConstants.CHAR_Y);

            } else if (StringUtils.equals(ReceiptSlipStatus.ONTRANSIT.getCodeDbid(), bo.getReceiptSlipStatus())){

                //若receiptStatus = S049ONTRANSIT, 设置checkFlg为N
                bo.setCheckFlg(CommonConstants.CHAR_N);

            } else {

                bo.setCheckFlg(CommonConstants.CHAR_N);

            }
        });

        return list;
    }

    /**
     * prepareReceiptReport
     */
    public void prepareReceiptReport(SDM010601Form form) {

        List<SDM010601BO> list = form.getGridData();

        if (list.isEmpty()) {
            return;
        }

        String siteId = form.getSiteId();
        Long facilityId = form.getReceiptPointId();
        Long personId = form.getPersonId();
        String personNm = form.getPersonNm();

        for (SDM010601BO bo : list) {

            //若接收日不为空则报错
            if (StringUtils.isNotBlank(bo.getReceiptDate())) {

                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00314", new String[] {
                                                 CodedMessageUtils.getMessage("label.deliveryNoteNo"),
                                                 bo.getDeliveryNoteNo(),
                                                 CodedMessageUtils.getMessage("label.tableReceiptSlipInfo")}));
            }
        }

        //获取receiptSlip
        Set<Long> receiptSlipIds = list.stream().map(SDM010601BO::getReceiptSlipId).collect(Collectors.toSet());
        List<ReceiptSlipVO> receiptSlipVOList = sdm0106Service.getReceiptSlipVOList(receiptSlipIds);

        //获取ReceiptSlipItem
        List<ReceiptSlipItemVO> receiptSlipItemVOList = sdm0106Service.getReceiptSlipItemVOList(receiptSlipIds);
        Map<Long, List<ReceiptSlipItemVO>> slipItemMap = Optional.ofNullable(receiptSlipItemVOList).orElse(Collections.emptyList()).stream().collect(Collectors.groupingBy(ReceiptSlipItemVO::getReceiptSlipId));

        //获取ReceiptSerializedItem
        List<ReceiptSerializedItemVO> receiptSerializedItemVOList = sdm0106Service.getReceiptSerializedItemVOList(receiptSlipIds);

        //去重
        receiptSerializedItemVOList = this.filterDuplicateItems(receiptSerializedItemVOList);
        Map<Long, List<ReceiptSerializedItemVO>> serialItemMap = Optional.ofNullable(receiptSerializedItemVOList)
                                                                 .orElse(Collections.emptyList())
                                                                 .stream()
                                                                 .collect(Collectors.groupingBy(ReceiptSerializedItemVO::getReceiptSlipId));

        for (ReceiptSlipVO rsVO : receiptSlipVOList) {

            this.doReceiptReport(rsVO,
                                 slipItemMap.getOrDefault(rsVO.getReceiptSlipId(), Collections.emptyList()),
                                 serialItemMap.getOrDefault(rsVO.getReceiptSlipId(), Collections.emptyList()),
                                 siteId,
                                 facilityId,
                                 personId,
                                 personNm);
        }
    }

    /**
     * doReceiptReport
     */
    private void doReceiptReport(ReceiptSlipVO receiptSlipVO,
                                 List<ReceiptSlipItemVO> receiptSlipItemVOList,
                                 List<ReceiptSerializedItemVO> receiptSerializedItemVOList,
                                 String siteId,
                                 Long facilityId,
                                 Long personId,
                                 String personNm) {

        //创建用于保存的list
        List<ReceiptSerializedItemVO> saveReceiptSerializedItemVOList = new ArrayList<>();
        List<ReceiptSlipVO> saveReceiptSlipVOList = new ArrayList<>();
        List<ProductCostVO> saveProductCostVOList = new ArrayList<>();
        List<ProductStockStatusVO> saveProductStockStatusVOList = new ArrayList<>();
        List<InventoryTransactionVO> saveInventoryTransactionVOList = new ArrayList<>();
        List<SerializedProductVO> saveSerializedProductVOList = new ArrayList<>();
        List<CmmSerializedProductVO> saveCmmSerializedProductVOList = new ArrayList<>();
        List<BatteryVO> saveBatteryVOList = new ArrayList<>();
        List<CmmBatteryVO> saveCmmBatteryVOList = new ArrayList<>();
        List<SerializedProductTranVO> saveSerializedProductTranVOList = new ArrayList<>();
        List<CmmUnitPromotionItemVO> saveCmmUnitPromotionItemVOList = new ArrayList<>();

        /** 0.数据准备 */
        Set<Long> productIds = receiptSlipItemVOList.stream().map(ReceiptSlipItemVO::getProductId).collect(Collectors.toSet());

        //获取CostType = S011RECEIVECOST的ProductCostMap
        List<ProductCostVO> productCostVOList = sdm0106Service.getProductCostVOList(productIds, CostType.RECEIVE_COST);
        Map<Long, ProductCostVO> receiveProductCostVOMap = Optional.ofNullable(productCostVOList)
                                                                   .orElse(Collections.emptyList())
                                                                   .stream()
                                                                   .collect(Collectors.toMap(ProductCostVO::getProductId,
                                                                                             productCostVO -> productCostVO));

        //获取product_stock_status_type = S084PURCHASESTOCKINQTY的productStockStatusMap
        Map<Long, ProductStockStatusVO> purchaseStockInMap = this.getProductStockStatusMap(siteId,
                                                                                           facilityId,
                                                                                           productIds,
                                                                                           SdStockStatus.ONTRANSIT_QTY.getCodeDbid());

        //获取product_stock_status_type = S084ONTRANSFERINQTY的productStockStatusMap
        Map<Long, ProductStockStatusVO> onTransferInMap = this.getProductStockStatusMap(siteId,
                                                                                        facilityId,
                                                                                        productIds,
                                                                                        SdStockStatus.ONTRANSFER_IN_QTY.getCodeDbid());

        //获取product_stock_status_type = S084ONHANDQTY的productStockStatusMap
        Map<Long, ProductStockStatusVO> onHandMap = this.getProductStockStatusMap(siteId,
                                                                                  facilityId,
                                                                                  productIds,
                                                                                  SdStockStatus.ONHAND_QTY.getCodeDbid());

        //获取MstProductMap
        List<MstProductVO> mstProductVOList = sdm0106Service.getByProductIdIn(productIds);
        Map<Long, MstProductVO> mstProductMap = Optional.ofNullable(mstProductVOList)
                                                        .orElse(Collections.emptyList())
                                                        .stream()
                                                        .collect(Collectors.toMap(MstProductVO::getProductId, vo -> vo));

        //获取inventoryTransactionTypeMap
        Map<String, String> inventoryTransactionTypeMap = helperFacade.getMstCodeInfoMap(InventoryTransactionType.CODE_ID);
        Map<String, String> codeMapWithCodeData2 = sdm0106Service.getCodeMstS027(InventoryTransactionType.CODE_ID);

        //获取serializedProductMap
        Set<Long> serialProIds = receiptSerializedItemVOList.stream().map(ReceiptSerializedItemVO::getSerializedProductId).collect(Collectors.toSet());
        List<SerializedProductVO> serializedProductVOList = sdm0106Service.getSerializedProductVOList(serialProIds, facilityId);
        Map<Long, SerializedProductVO> serializedProductMap = Optional.ofNullable(serializedProductVOList)
                                                                      .orElse(Collections.emptyList())
                                                                      .stream()
                                                                      .collect(Collectors.toMap(SerializedProductVO::getSerializedProductId,
                                                                                                serializedProductVO -> serializedProductVO));

        //获取cmmSerializedProductMap
        Set<Long> cmmSerialProIds = serializedProductVOList.stream().map(SerializedProductVO::getCmmSerializedProductId).collect(Collectors.toSet());
        List<CmmSerializedProductVO> cmmSerializedProductVOList = sdm0106Service.getCmmSerializedProductVOList(cmmSerialProIds);
        Map<Long, CmmSerializedProductVO> cmmSerializedProductMap = Optional.ofNullable(cmmSerializedProductVOList)
                                                                            .orElse(Collections.emptyList())
                                                                            .stream()
                                                                            .collect(Collectors.toMap(CmmSerializedProductVO::getSerializedProductId ,
                                                                                                      cmmSerializedProductVO -> cmmSerializedProductVO));

        //获取batteryMap
        List<BatteryVO> batteryVOList = sdm0106Service.getBatteryVOList(serialProIds, this.formatCurrentDate());
        Map<Long, List<BatteryVO>> batteryMap = Optional.ofNullable(batteryVOList)
                                                        .orElse(Collections.emptyList())
                                                        .stream()
                                                        .collect(Collectors.groupingBy(BatteryVO::getSerializedProductId));

        //获取cmmBatteryMap
        List<Long> cmmBatteryIds = batteryVOList.stream().map(BatteryVO::getCmmBatteryInfoId).toList();
        List<CmmBatteryVO> cmmBatteryVOList = sdm0106Service.getCmmBatteryVOList(cmmBatteryIds);
        Map<Long, CmmBatteryVO> cmmBatteryMap = Optional.ofNullable(cmmBatteryVOList)
                                                        .orElse(Collections.emptyList())
                                                        .stream()
                                                        .collect(Collectors.toMap(CmmBatteryVO::getBatteryId,
                                                                                  cmmBatteryVO -> cmmBatteryVO));

        //获取当前的促销信息promotionModelMap
        List<SDM010601BO> promotionInfoList = sdm0106Service.getEffectivePromotionInfoList(this.formatCurrentDate());
        Map<Long, Long> promotionModelMap = Optional.ofNullable(promotionInfoList)
                                                    .orElse(Collections.emptyList())
                                                    .stream()
                                                    .collect(Collectors.toMap(SDM010601BO::getPromotionModelId,
                                                                              SDM010601BO::getPromotionListId,
                                                                              (existingValue, newValue) -> existingValue));

        //获取促销车辆信息
        List<String> frameNos = serializedProductVOList.stream().map(SerializedProductVO::getFrameNo).toList();
        List<SDM010601BO> promotionModelList = sdm0106Service.getPromotionInfoByFrameNoList(frameNos);
        Map<String, SDM010601BO> frameNoMap = Optional.ofNullable(promotionModelList)
                                                      .orElse(Collections.emptyList())
                                                      .stream()
                                                      .collect(Collectors.toMap(SDM010601BO::getFrameNo, bo -> bo));

        //获取促销明细信息
        List<Long> promotionItemIds = promotionModelList.stream().map(SDM010601BO::getPromotionItemId).toList();
        List<CmmUnitPromotionItemVO> cmmUnitPromotionItemVOList = sdm0106Service.getCmmUnitPromotionItemVOList(promotionItemIds);
        Map<Long, CmmUnitPromotionItemVO> promotionItemMap = Optional.ofNullable(cmmUnitPromotionItemVOList)
                                                                     .orElse(Collections.emptyList())
                                                                     .stream()
                                                                     .collect(Collectors.toMap(CmmUnitPromotionItemVO::getCmmSerializedProductId,
                                                                                               vo -> vo));

        /** 1.接收车辆数据更新 */
        //1.1过滤出ReceiptSerializedItemList中CompleteFlag为N的数据
        List<ReceiptSerializedItemVO> filteredList = Optional.ofNullable(receiptSerializedItemVOList)
                                                             .orElse(Collections.emptyList())
                                                             .stream()
                                                             .filter(vo -> StringUtils.equals(CommonConstants.CHAR_N, vo.getCompleteFlag()))
                                                             .toList();

        //1.2若ReceiptSlipItemList的数量和过滤后的数量一致则更新ReceiptSerializedItem和ReceiptSlip
        BigDecimal totalQuantity = receiptSlipItemVOList.stream()
                                                        .map(ReceiptSlipItemVO::getReceiptQty) // 提取每个对象的receiptQty
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalQuantity.compareTo(new BigDecimal(CollectionUtils.size(filteredList))) == CommonConstants.INTEGER_ZERO) {

            //更新ReceiptSerializedItem
            for (ReceiptSerializedItemVO serialItemVO : receiptSerializedItemVOList) {

                serialItemVO.setCompleteFlag(CommonConstants.CHAR_Y);
                saveReceiptSerializedItemVOList.add(serialItemVO);
            }

            //更新ReceiptSlip
            receiptSlipVO.setReceiptSlipStatus(ReceiptSlipStatus.STORED.getCodeDbid());
            receiptSlipVO.setReceivedDate(this.formatCurrentDate());
            receiptSlipVO.setReceivedPicId(personId);
            receiptSlipVO.setReceivedPicNm(personNm);
            saveReceiptSlipVOList.add(receiptSlipVO);
        } else {

            //更新ReceiptSerializedItem
            for (ReceiptSerializedItemVO serialItemVO : receiptSerializedItemVOList) {

                serialItemVO.setCompleteFlag(CommonConstants.CHAR_Y);
                saveReceiptSerializedItemVOList.add(serialItemVO);
            }
        }

        /** 2.库存接收 */
        for (ReceiptSlipItemVO recSlipItemVO : receiptSlipItemVOList) {

            //2.1产品成本更新保存
            ProductCostVO productCostVO = receiveProductCostVOMap.get(recSlipItemVO.getProductId());

            //更新ProductCost
            if (!Nulls.isNull(productCostVO)) {

                productCostVO.setCost(recSlipItemVO.getReceiptPrice());
                saveProductCostVOList.add(productCostVO);

            } else {

                productCostVO = ProductCostVO.create();
                productCostVO.setSiteId(siteId);
                productCostVO.setProductId(recSlipItemVO.getProductId());
                productCostVO.setProductCd(recSlipItemVO.getProductCd());
                productCostVO.setProductNm(recSlipItemVO.getProductNm());
                productCostVO.setCost(recSlipItemVO.getReceiptPrice());
                productCostVO.setCostType(CostType.RECEIVE_COST);
                saveProductCostVOList.add(productCostVO);
                receiveProductCostVOMap.put(productCostVO.getProductId(), productCostVO);
            }

            //获取当前receiptSlip的出入库类型
            String inventoryTransactionType = receiptSlipVO.getInventoryTransactionType();

            //2.2若inventoryTransactionType = S027PURCHASESTOCKIN
            if (StringUtils.equals(InventoryTransactionType.PURCHASESTOCKIN.getCodeDbid(), inventoryTransactionType)) {

                ProductStockStatusVO productStockStatusVO = purchaseStockInMap.get(recSlipItemVO.getProductId());

                if (!Nulls.isNull(productStockStatusVO)) {

                    //扣减recSlipItem的数量
                    productStockStatusVO.setQuantity(NumberUtil.subtract(productStockStatusVO.getQuantity(), recSlipItemVO.getReceiptQty()));
                    saveProductStockStatusVOList.add(productStockStatusVO);
                }
            }

            //2.3若inventoryTransactionType = S027TRANSFERIN
            if (StringUtils.equals(InventoryTransactionType.TRANSFERIN.getCodeDbid(), inventoryTransactionType)) {

                ProductStockStatusVO productStockStatusVO = onTransferInMap.get(recSlipItemVO.getProductId());

                if (!Nulls.isNull(productStockStatusVO)) {

                    //扣减recSlipItem的数量
                    productStockStatusVO.setQuantity(NumberUtil.subtract(productStockStatusVO.getQuantity(), recSlipItemVO.getReceiptQty()));
                    saveProductStockStatusVOList.add(productStockStatusVO);
                }
            }

            //2.4增加S084ONHANDQTY的数量
            ProductStockStatusVO productStockStatusVO = onHandMap.get(recSlipItemVO.getProductId());
            if (!Nulls.isNull(productStockStatusVO)) {

                productStockStatusVO.setQuantity(NumberUtil.add(productStockStatusVO.getQuantity(), recSlipItemVO.getReceiptQty()));
                saveProductStockStatusVOList.add(productStockStatusVO);
            }else{

                productStockStatusVO = ProductStockStatusVO.create();
                productStockStatusVO.setSiteId(siteId);
                productStockStatusVO.setFacilityId(facilityId);
                productStockStatusVO.setProductId(recSlipItemVO.getProductId());
                productStockStatusVO.setProductClassification(ProductClsType.GOODS.getCodeDbid());
                productStockStatusVO.setProductStockStatusType(SdStockStatus.ONHAND_QTY.getCodeDbid());
                productStockStatusVO.setQuantity(recSlipItemVO.getReceiptQty());
                saveProductStockStatusVOList.add(productStockStatusVO);
            }

            //2.5创建InventoryTransaction
            this.prepareInventoryTransaction(receiptSlipVO,
                                             recSlipItemVO,
                                             productCostVO,
                                             mstProductMap,
                                             inventoryTransactionTypeMap,
                                             codeMapWithCodeData2,
                                             siteId,
                                             personId,
                                             personNm,
                                             saveInventoryTransactionVOList,
                                             productStockStatusVO);
        }

        /** 3.车辆接收  */
        for (ReceiptSerializedItemVO recSeriItem : receiptSerializedItemVOList) {

            //3.1更新车辆的状态
            SerializedProductVO serProVO = serializedProductMap.get(recSeriItem.getSerializedProductId());

            if (!Nulls.isNull(serProVO)) {

                serProVO.setStockStatus(SerialproductStockStatus.ONHAND);
                saveSerializedProductVOList.add(serProVO);

                CmmSerializedProductVO cmmSerializedProductVO = cmmSerializedProductMap.get(serProVO.getCmmSerializedProductId());

                if (!Nulls.isNull(cmmSerializedProductVO)) {

                    cmmSerializedProductVO.setStockStatus(SerialproductStockStatus.ONHAND);
                    saveCmmSerializedProductVOList.add(cmmSerializedProductVO);
                }

                //若为电车(EvFlag = Y), 则追加更新电池的状态
                if (StringUtils.equals(CommonConstants.CHAR_Y, serProVO.getEvFlag())) {

                    List<BatteryVO> batList = batteryMap.getOrDefault(recSeriItem.getSerializedProductId(), new ArrayList<>());

                    for (BatteryVO batteryVO : batList) {

                        //更新battery
                        batteryVO.setBatteryStatus(SerialproductStockStatus.ONHAND);
                        saveBatteryVOList.add(batteryVO);

                        //更新cmmBattery
                        CmmBatteryVO cmmBatteryVO = cmmBatteryMap.get(batteryVO.getCmmBatteryInfoId());
                        if (!Nulls.isNull(cmmBatteryVO)) {

                            cmmBatteryVO.setBatteryStatus(SerialproductStockStatus.ONHAND);
                            saveCmmBatteryVOList.add(cmmBatteryVO);
                        }
                    }
                }
            }

            //3.2创建车辆履历表serializedProductTransaction
            this.prepareSerializedProductTran(receiptSlipVO,
                                              receiptSlipItemVOList,
                                              receiveProductCostVOMap,
                                              recSeriItem,
                                              siteId,
                                              personNm,
                                              saveSerializedProductTranVOList);

            /** 4.是否加入促销车  */
            SerializedProductVO serializedProductVO = serializedProductMap.get(recSeriItem.getSerializedProductId());

            if (!Nulls.isNull(serializedProductVO)) {

                Long productId = serializedProductVO.getProductId();

                if (promotionModelMap.containsKey(productId)) {

                    //4.1获取促销车辆信息
                    SDM010601BO promotionModel = frameNoMap.get(serializedProductVO.getFrameNo());

                    //4.2若存在则更新CmmUnitPromotionItem, 反之则创建CmmUnitPromotionItem
                    if (!Nulls.isNull(promotionModel)) {

                        CmmUnitPromotionItemVO cmmUnitPromotionItemVO = promotionItemMap.get(promotionModel.getPromotionItemId());

                        if (!Nulls.isNull(cmmUnitPromotionItemVO)) {

                            cmmUnitPromotionItemVO.setPromotionListId(promotionModelMap.get(productId));
                            cmmUnitPromotionItemVO.setSiteId(siteId);
                            cmmUnitPromotionItemVO.setFacilityId(facilityId);
                            cmmUnitPromotionItemVO.setProductId(productId);
                            cmmUnitPromotionItemVO.setCmmSerializedProductId(!Nulls.isNull(serializedProductVO) ? serializedProductVO.getCmmSerializedProductId() : null);
                            cmmUnitPromotionItemVO.setFrameNo(serializedProductVO.getFrameNo());
                            cmmUnitPromotionItemVO.setStockMcFlag(CommonConstants.CHAR_Y);
                            saveCmmUnitPromotionItemVOList.add(cmmUnitPromotionItemVO);
                        }

                    } else {

                        CmmUnitPromotionItemVO cmmUnitPromotionItemVO = CmmUnitPromotionItemVO.create();
                        cmmUnitPromotionItemVO.setPromotionListId(promotionModelMap.get(productId));
                        cmmUnitPromotionItemVO.setSiteId(siteId);
                        cmmUnitPromotionItemVO.setFacilityId(facilityId);
                        cmmUnitPromotionItemVO.setProductId(productId);
                        cmmUnitPromotionItemVO.setCmmSerializedProductId(!Nulls.isNull(serializedProductVO) ? serializedProductVO.getCmmSerializedProductId() : null);
                        cmmUnitPromotionItemVO.setFrameNo(serializedProductVO.getFrameNo());
                        cmmUnitPromotionItemVO.setStockMcFlag(CommonConstants.CHAR_Y);
                        saveCmmUnitPromotionItemVOList.add(cmmUnitPromotionItemVO);
                    }
                }
            }
        }

        //save
        sdm0106Service.saveOrUpdate(saveReceiptSerializedItemVOList,
                                    saveReceiptSlipVOList,
                                    productCostVOList,
                                    saveProductStockStatusVOList,
                                    saveInventoryTransactionVOList,
                                    serializedProductVOList,
                                    cmmSerializedProductVOList,
                                    batteryVOList,
                                    cmmBatteryVOList,
                                    saveSerializedProductTranVOList,
                                    saveCmmUnitPromotionItemVOList);
    }

    /**
     * 创建SerializedProductTran
     */
    private void prepareSerializedProductTran(ReceiptSlipVO receiptSlipVO,
                                              List<ReceiptSlipItemVO> receiptSlipItemVOList,
                                              Map<Long, ProductCostVO> prodCostVOMap,
                                              ReceiptSerializedItemVO recSeriItem,
                                              String siteId,
                                              String personNm,
                                              List<SerializedProductTranVO> saveSerializedProductTranVOList) {

        String fromStatus = this.getFromStatus(receiptSlipVO.getInventoryTransactionType());

        Optional<ReceiptSlipItemVO> optionalReceiptSlipItem = receiptSlipItemVOList
                                                              .stream()
                                                              .filter(vo -> vo.getReceiptSlipItemId().equals(recSeriItem.getReceiptSlipItemId()))
                                                              .findFirst();

        ProductCostVO productCostVO = optionalReceiptSlipItem
                                      .map(receiptSlipItemVO -> prodCostVOMap.get(receiptSlipItemVO.getProductId()))
                                      .orElse(null);

        SerializedProductTranVO serializedProductTranVO = SerializedProductTranVO.create();
        serializedProductTranVO.setSiteId(siteId);
        serializedProductTranVO.setSerializedProductId(recSeriItem.getSerializedProductId());
        serializedProductTranVO.setTransactionDate(this.formatCurrentDate());
        serializedProductTranVO.setTransactionTime(this.formatCurrentTime());
        serializedProductTranVO.setRelatedSlipNo(receiptSlipVO.getCommercialInvoiceNo());
        serializedProductTranVO.setReporterNm(personNm);
        serializedProductTranVO.setTransactionTypeId(receiptSlipVO.getInventoryTransactionType());
        serializedProductTranVO.setFromStatus(fromStatus);
        serializedProductTranVO.setToStatus(SerialproductStockStatus.ONHAND);
        serializedProductTranVO.setProductId(optionalReceiptSlipItem.map(ReceiptSlipItemVO::getProductId).orElse(null));
        serializedProductTranVO.setFromPartyId(receiptSlipVO.getFromOrganizationId());
        serializedProductTranVO.setToPartyId(receiptSlipVO.getReceivedOrganizationId());
        serializedProductTranVO.setTargetFacilityId(receiptSlipVO.getFromFacilityId());
        serializedProductTranVO.setFromFacilityId(receiptSlipVO.getFromFacilityId());
        serializedProductTranVO.setToFacilityId(receiptSlipVO.getReceivedFacilityId());
        serializedProductTranVO.setInCost(Optional.ofNullable(productCostVO).map(ProductCostVO::getCost).orElse(BigDecimal.ZERO));
        serializedProductTranVO.setOutCost(BigDecimal.ZERO);
        serializedProductTranVO.setOutPrice(BigDecimal.ZERO);
        saveSerializedProductTranVOList.add(serializedProductTranVO);
    }

    /**
     * 创建InventoryTransaction
     */
    private void prepareInventoryTransaction(ReceiptSlipVO receiptSlipVO,
                                             ReceiptSlipItemVO recSlipItemVO,
                                             ProductCostVO productCostVO,
                                             Map<Long, MstProductVO> mstProductMap,
                                             Map<String, String> inventoryTransactionTypeMap,
                                             Map<String, String> codeMapWithCodeData2,
                                             String siteId,
                                             Long personId,
                                             String personNm,
                                             List<InventoryTransactionVO> saveInventoryTransactionVOList,
                                             ProductStockStatusVO productStockStatusVO) {

        String inventoryTransactionTypeCd = receiptSlipVO.getInventoryTransactionType();
        String inventoryTransactionTypeNm = inventoryTransactionTypeMap.get(inventoryTransactionTypeCd);

        //入库和出库数量
        String codeData2 = codeMapWithCodeData2.get(inventoryTransactionTypeCd);
        BigDecimal inQty = StringUtils.equals(InOutType.IN, codeData2) ? recSlipItemVO.getReceiptQty() : BigDecimal.ZERO;
        BigDecimal outQty = StringUtils.equals(InOutType.OUT, codeData2) ? recSlipItemVO.getReceiptQty() : BigDecimal.ZERO;

        //获取mstProduct
        MstProductVO mstProduct = mstProductMap.get(recSlipItemVO.getProductId());

        //创建inventoryTransactionVO
        InventoryTransactionVO inventoryTransactionVO = InventoryTransactionVO.create();
        inventoryTransactionVO.setSiteId(siteId);
        inventoryTransactionVO.setPhysicalTransactionDate(this.formatCurrentDate());
        inventoryTransactionVO.setPhysicalTransactionTime(this.formatCurrentTime());
        inventoryTransactionVO.setFromFacilityId(receiptSlipVO.getFromFacilityId());
        inventoryTransactionVO.setFromOrganizationId(receiptSlipVO.getFromOrganizationId());
        inventoryTransactionVO.setToOrganizationId(receiptSlipVO.getReceivedOrganizationId());
        inventoryTransactionVO.setToFacilityId(receiptSlipVO.getReceivedFacilityId());
        inventoryTransactionVO.setProductId(recSlipItemVO.getProductId());

        if (!Nulls.isNull(mstProduct)) {

            inventoryTransactionVO.setProductCd(mstProduct.getProductCd());
            inventoryTransactionVO.setProductNm(mstProduct.getSalesDescription());
        }

        inventoryTransactionVO.setTargetFacilityId(receiptSlipVO.getReceivedFacilityId());
        inventoryTransactionVO.setRelatedSlipId(receiptSlipVO.getReceiptSlipId());
        inventoryTransactionVO.setRelatedSlipNo(receiptSlipVO.getCommercialInvoiceNo());
        inventoryTransactionVO.setInventoryTransactionType(inventoryTransactionTypeCd);
        inventoryTransactionVO.setInventoryTransactionNm(inventoryTransactionTypeNm);
        inventoryTransactionVO.setInQty(inQty);
        inventoryTransactionVO.setOutQty(outQty);
        inventoryTransactionVO.setCurrentQty(productStockStatusVO.getQuantity());

        if (!Nulls.isNull(productCostVO)) {

            BigDecimal inCost = StringUtils.equals(InOutType.IN, codeData2) ? productCostVO.getCost() : BigDecimal.ZERO;
            BigDecimal outCost = StringUtils.equals(InOutType.OUT, codeData2) ? productCostVO.getCost() : BigDecimal.ZERO;
            inventoryTransactionVO.setInCost(inCost);
            inventoryTransactionVO.setOutCost(outCost);
            inventoryTransactionVO.setCurrentAverageCost(productCostVO.getCost());
        }

        inventoryTransactionVO.setReporterId(personId);
        inventoryTransactionVO.setReporterNm(personNm);
        inventoryTransactionVO.setProductClassification(ProductClsType.GOODS.getCodeDbid());
        saveInventoryTransactionVOList.add(inventoryTransactionVO);
    }

    /**
     * 过滤ReceiptSerializedItemList
     */
    private List<ReceiptSerializedItemVO> filterDuplicateItems(List<ReceiptSerializedItemVO> list) {

        if (list.isEmpty()) {
            return list;
        }

        //去掉重复数据和重复车辆(根据SerializedProductId)
        List<ReceiptSerializedItemVO> filterList = list.stream()
                                                       .distinct()
                                                       .collect(Collectors.toMap(ReceiptSerializedItemVO::getSerializedProductId,
                                                                                 receiptSerializedItemVO -> receiptSerializedItemVO,
                                                                                 (existing, replacement) -> existing))
                                                       .values()
                                                       .stream()
                                                       .collect(Collectors.toList());//返回可修改的集合

        //去掉已经是库存的车辆
        Set<Long> serialProIds = filterList.stream().map(ReceiptSerializedItemVO::getSerializedProductId).collect(Collectors.toSet());
        List<CmmSerializedProductVO> cmmSerializedProductVOList = sdm0106Service.getCmmSerializedProductVOList(serialProIds, McSalesStatus.STOCK);
        Map<Long, CmmSerializedProductVO> serProMap = Optional.ofNullable(cmmSerializedProductVOList)
                                                              .orElse(Collections.emptyList())
                                                              .stream()
                                                              .collect(Collectors.toMap(CmmSerializedProductVO::getSerializedProductId,
                                                                                        cmmSerializedProductVO -> cmmSerializedProductVO));

        //若存在有库存的车辆则去掉该条数据
        filterList.removeIf(item -> serProMap.containsKey(item.getSerializedProductId()));

        return filterList;
    }

    /**
     * 获取ProductStockStatusMap
     */
    private Map<Long, ProductStockStatusVO> getProductStockStatusMap(String siteId,
                                                                     Long facilityId,
                                                                     Set<Long> productIds,
                                                                     String stockStatusType) {

        List<ProductStockStatusVO> stockStatusList = sdm0106Service.getProductStockStatusVO(siteId, facilityId, productIds, stockStatusType);

        return Optional.ofNullable(stockStatusList)
                       .orElse(Collections.emptyList())
                       .stream()
                       .collect(Collectors.toMap(ProductStockStatusVO::getProductId, vo -> vo));
    }

    /**
     * S027PURCHASESTOCKIN -> S033ONTRANSIT
     * S027TRANSFERIN -> S033ONTRANSFERIN
     */
    private String getFromStatus(String inventoryTransactionType) {

        if (StringUtils.equals(InventoryTransactionType.PURCHASESTOCKIN.getCodeDbid(),inventoryTransactionType)) {

            return SerialproductStockStatus.ONTRANSFER;

        } else if (StringUtils.equals(InventoryTransactionType.TRANSFERIN.getCodeDbid(), inventoryTransactionType)) {

            return SerialproductStockStatus.ONTRANSFERIN;
        }

        return StringUtils.EMPTY;
    }

    /**
     * 获取系统日期
     */
    private String formatCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD));
    }

    /**
     * 获取系统时间
     */
    private String formatCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_H_M_S));
    }
}