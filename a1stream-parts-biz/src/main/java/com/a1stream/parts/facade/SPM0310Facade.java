package com.a1stream.parts.facade;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.common.constants.PJConstants.StockTakingType;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.SPM031001BO;
import com.a1stream.domain.bo.parts.SPM031001PrintBO;
import com.a1stream.domain.form.parts.SPM031001Form;
import com.a1stream.domain.vo.InventoryTransactionVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.ProductStockTakingHistoryVO;
import com.a1stream.domain.vo.ProductStockTakingVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.a1stream.parts.service.SPM0310Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ymsl.plugins.userauth.util.ListSortUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.report.exporter.PdfReportExporter;

import jakarta.annotation.Resource;


/**
* 功能描述:cancel or finish stockTaking
*
* @author mid2178
*/
@Component
public class SPM0310Facade {

    @Resource
    private SPM0310Service spm0310Service;

    @Resource
    public void setPdfReportExporter(PdfReportExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
    }

    private PdfReportExporter pdfExporter;

    public List<SPM031001BO> getStockTakingList(Long facilityId, String siteId) {


        Map<String,SPM031001BO> productStockTakingMap = spm0310Service.getProductStockTakingByType(siteId
                                                                                                 , facilityId
                                                                                                 , ProductClsType.PART.getCodeDbid());

        List<SPM031001BO> detailList = new ArrayList<>(productStockTakingMap.values());

        SPM031001BO accuracyPercent = new SPM031001BO();
        SPM031001BO qtyEqualModel = productStockTakingMap.get(StockTakingType.QTYEQUAL);
        SPM031001BO actualTotalModel = productStockTakingMap.get(StockTakingType.ACTUAL_TOTAL);

        // Accuracy% = (Actual Qty = System Qty)/Actual Total
        accuracyPercent.setType(StockTakingType.ACCURACY_PERCCENT);
        accuracyPercent.setLines(getPercent(qtyEqualModel.getLines(), actualTotalModel.getLines()));
        accuracyPercent.setItems(getPercent(qtyEqualModel.getItems(), actualTotalModel.getItems()));
        accuracyPercent.setQty(getPercent(qtyEqualModel.getQty(), actualTotalModel.getQty()));
        accuracyPercent.setAmount(getPercent(qtyEqualModel.getAmount(), actualTotalModel.getAmount()));
        accuracyPercent.setSeq(CommonConstants.CHAR_SIX);

        detailList.add(accuracyPercent);
        ListSortUtils.sort(detailList, new String[] { "seq" });

        return detailList;
    }

    private BigDecimal getPercent(BigDecimal arg1, BigDecimal arg2) {

        if (arg2.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO.setScale(2);
        }

        return arg1.divide(arg2, 4, RoundingMode.HALF_UP)
                   .multiply(BigDecimal.valueOf(100))
                   .setScale(2, RoundingMode.HALF_UP);
    }

    public void doPrint(SPM031001Form form, String siteId) {

        this.validateBeforePrint(form.getPointId(), siteId);
    }

    private void validateBeforePrint(Long facilityId, String siteId) {

        List<ProductStockTakingVO> processingList = spm0310Service.getStockTakingProcessing(siteId, facilityId, ProductClsType.PART.getCodeDbid());

        if(processingList.isEmpty()) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10237"
                                           , new String[] {CodedMessageUtils.getMessage("label.stocktakingLines")
                                           , CodedMessageUtils.getMessage("label.point")}));
        }
    }

    public void doCancelStockTaking(Long facilityId, String siteId) {

        SystemParameterVO systemParameterUpdate = spm0310Service.getProcessingSystemParameter(siteId
                                                                                            , facilityId
                                                                                            , MstCodeConstants.SystemParameterType.STOCKTAKING
                                                                                            , CommonConstants.CHAR_ONE);

        if(systemParameterUpdate == null) {//validate

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.C.10143"));
        }else {// update system_parameter_info

            systemParameterUpdate.setParameterValue(CommonConstants.CHAR_ZERO);
        }

        // delete product_staock_tacking
        List<ProductStockTakingVO> productStockTakingDelete = spm0310Service.getProductStockTakingInfo(siteId, facilityId);
        spm0310Service.doCancelData(systemParameterUpdate, productStockTakingDelete);
    }

    public void doFinishStockTaking(Long facilityId, String siteId, List<SPM031001BO> detailList, Long personId, String personName) {

        SystemParameterVO systemParameterUpdate = spm0310Service.getProcessingSystemParameter(siteId
                                                                                            , facilityId
                                                                                            , MstCodeConstants.SystemParameterType.STOCKTAKING
                                                                                            , CommonConstants.CHAR_ONE);

        if(systemParameterUpdate == null) {//validate

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.C.10143"));
        }else {// update system_parameter_info

            systemParameterUpdate.setParameterValue(CommonConstants.CHAR_ZERO);
        }

        // update product_staock_tacking
        List<ProductStockTakingVO> productStockTakingUpdateList = spm0310Service.getProductStockTakingInfo(siteId, facilityId);
        for(ProductStockTakingVO stockTaking : productStockTakingUpdateList) {

            stockTaking.setFinishedDate(LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)));
            stockTaking.setFinishedTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_H_M_S)));
            stockTaking.setProductClassification(ProductClsType.PART.getCodeDbid());
        }

        Map<String, SPM031001BO> detailMap = detailList.stream().collect(Collectors.toMap(v -> v.getType(),Function.identity()));

        // insert product_staock_tacking_history
        ProductStockTakingHistoryVO productStockTakingHistoryInsert = insertProductStockTakingHistory(facilityId
                                                                                                    , siteId
                                                                                                    , productStockTakingUpdateList.get(0).getStartedDate()
                                                                                                    , productStockTakingUpdateList.get(0).getStartedTime()
                                                                                                    , detailMap);

        // 仅针对 pst.actual_qty <> pst.expected_qty的行，进行以下更新处理
        // 调整数量： qty + pst.actual_qty - pst.expected_qty

        // 创建/更新： product_stock_status
        List<ProductStockStatusVO> productStockStatusUpdateList = updateProductStockStatus(siteId, facilityId);

        // 获取productStockTaking数据用于下面的更新
        List<SPM031001BO> productStockTakingList = spm0310Service.getProductStockTakingList(siteId, facilityId);

        // 创建/更新： product_inventory
        List<ProductInventoryVO> productInventoryUpdateList = updateProductInventory(siteId, facilityId, productStockTakingList);

        // 创建： inventory_transaction
        List<InventoryTransactionVO> inventoryTransactionInsertList = insertInventoryTransaction(siteId
                                                                                               , facilityId
                                                                                               , productStockTakingList
                                                                                               , personId
                                                                                               , personName);

        // save all
        spm0310Service.doFinishData(systemParameterUpdate
                                  , productStockTakingUpdateList
                                  , productStockTakingHistoryInsert
                                  , productStockStatusUpdateList
                                  , productInventoryUpdateList
                                  , inventoryTransactionInsertList);
    }

    private ProductStockTakingHistoryVO insertProductStockTakingHistory(Long facilityId
                                                                      , String siteId
                                                                      , String startedDate
                                                                      , String startedTime
                                                                      , Map<String, SPM031001BO> detailMap) {

        ProductStockTakingHistoryVO productStockTakingHistoryInsert = new ProductStockTakingHistoryVO();

        SPM031001BO systemTotalBO = detailMap.get(StockTakingType.SYSTEM_TOTAL);
        SPM031001BO actualTotalBO = detailMap.get(StockTakingType.ACTUAL_TOTAL);
        SPM031001BO qtyEqualBO = detailMap.get(StockTakingType.QTYEQUAL);
        SPM031001BO qtyExceedBO = detailMap.get(StockTakingType.QTYEXCEED);
        SPM031001BO qtyLackBO = detailMap.get(StockTakingType.QTYLACK);

        productStockTakingHistoryInsert.setSiteId(siteId);
        productStockTakingHistoryInsert.setFacilityId(facilityId);
        productStockTakingHistoryInsert.setStartedDate(startedDate);
        productStockTakingHistoryInsert.setStartedTime(startedTime);
        productStockTakingHistoryInsert.setFinishedDate(LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)));
        productStockTakingHistoryInsert.setFinishedTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_H_M_S)));
        productStockTakingHistoryInsert.setLogicAmt(systemTotalBO.getAmount());
        productStockTakingHistoryInsert.setPhysicalAmt(actualTotalBO.getAmount());
        productStockTakingHistoryInsert.setGapAmt(actualTotalBO.getAmount().subtract(systemTotalBO.getAmount()));

        productStockTakingHistoryInsert.setEqualLines(qtyEqualBO.getLines().longValue());
        productStockTakingHistoryInsert.setEqualItems(qtyEqualBO.getItems().longValue());
        productStockTakingHistoryInsert.setEqualQty(qtyEqualBO.getQty());
        productStockTakingHistoryInsert.setEqualAmt(qtyEqualBO.getAmount());

        productStockTakingHistoryInsert.setExceedLines(qtyExceedBO.getLines().longValue());
        productStockTakingHistoryInsert.setExceedItems(qtyExceedBO.getItems().longValue());
        productStockTakingHistoryInsert.setExceedQty(qtyExceedBO.getQty());
        productStockTakingHistoryInsert.setExceedAmt(qtyExceedBO.getAmount());

        productStockTakingHistoryInsert.setLackLines(qtyLackBO.getLines().longValue());
        productStockTakingHistoryInsert.setLackItems(qtyLackBO.getItems().longValue());
        productStockTakingHistoryInsert.setLackQty(qtyLackBO.getQty());
        productStockTakingHistoryInsert.setLackAmt(qtyLackBO.getAmount());

        productStockTakingHistoryInsert.setProductClassification(ProductClsType.PART.getCodeDbid());

        return productStockTakingHistoryInsert;
    }

    private List<ProductStockStatusVO> updateProductStockStatus(String siteId, Long facilityId) {

        List<ProductStockStatusVO> productStockStatusUpdateList = new ArrayList<>();

        // 对同productid+StockStatusType的qty集计
        List<SPM031001BO> stockTakingSummaryList = spm0310Service.getStockTakingSummary(siteId, facilityId);

        // 获取库存状态数据
        Set<Long> productIds = stockTakingSummaryList.stream().map(SPM031001BO::getProductId).collect(Collectors.toSet());
        Set<String> productStockStatusTypes = new HashSet<>(Arrays.asList(SpStockStatus.ONHAND_QTY.getCodeDbid(),SpStockStatus.ONFROZEN_QTY.getCodeDbid()));
        List<ProductStockStatusVO> productStockStsVOs = spm0310Service.getProductStockStatus(siteId
                                                                                           , facilityId
                                                                                           , productIds
                                                                                           , productStockStatusTypes);

        // productStockStsVOMap <productId+'|'+productStockStatusType , ProductStockStatusVO>
        Map<String, ProductStockStatusVO> productStockStsVOMap = productStockStsVOs.stream().collect(Collectors.toMap(
                                                                                    v -> v.getProductId().toString() + CommonConstants.CHAR_VERTICAL_BAR + v.getProductStockStatusType(),
                                                                                    Function.identity()));

        // 准备完上面的数据，下面开始对ProductStockStatus表进行新增或更新
        for(SPM031001BO BO : stockTakingSummaryList) {

            ProductStockStatusVO productStockStsVO = productStockStsVOMap.get(BO.getProductId().toString() + CommonConstants.CHAR_VERTICAL_BAR + BO.getStockStatusType());

            if(productStockStsVO == null) {// insert

                if(BigDecimal.ZERO.compareTo(BO.getActualQty()) < 0) {

                    productStockStsVO = new ProductStockStatusVO();
                    productStockStsVO.setSiteId(siteId);
                    productStockStsVO.setFacilityId(facilityId);
                    productStockStsVO.setProductId(BO.getProductId());
                    productStockStsVO.setProductStockStatusType(BO.getStockStatusType());
                    productStockStsVO.setQuantity(BO.getActualQty());
                    productStockStsVO.setProductClassification(ProductClsType.PART.getCodeDbid());
                    productStockStatusUpdateList.add(productStockStsVO);
                }
            }else {// update

                BigDecimal quantity = productStockStsVO.getQuantity() == null ? BigDecimal.ZERO : productStockStsVO.getQuantity();
                BigDecimal qty = quantity.add(BO.getActualQty().subtract(BO.getExpectedQty()));

                // 更新时，【qty + pst.actual_qty - pst.expected_qty】必须大于等于零。
                if(qty.compareTo(BigDecimal.ZERO) >= 0) {

                    productStockStsVO.setQuantity(qty);
                    productStockStatusUpdateList.add(productStockStsVO);
                }else {

                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00325", new String[] {CodedMessageUtils.getMessage("label.tableProductStockStatus")}));
                }
            }
        }
        return productStockStatusUpdateList;
    }

    private List<ProductInventoryVO> updateProductInventory(String siteId, Long facilityId, List<SPM031001BO> productStockTakingList) {

        List<ProductInventoryVO> productInventoryUpdateList = new ArrayList<>();

        // 准备数据
        Set<Long> productInventoryIds = productStockTakingList.stream().map(SPM031001BO::getProductInventoryId).collect(Collectors.toSet());
        List<ProductInventoryVO> productInventoryVOs = spm0310Service.findByProductInventoryIdIn(productInventoryIds);

        //productInventoryVOMap <productId+'|'+locationId , ProductInventoryVO>
        Map<String, ProductInventoryVO> productInventoryVOMap = productInventoryVOs.stream().collect(Collectors.toMap(
                                                                                    v -> v.getProductId().toString() + CommonConstants.CHAR_VERTICAL_BAR + v.getLocationId().toString(),
                                                                                    Function.identity()));

        // 准备好上面的数据，下面开始对ProductInventory表进行新增或更新
        for(SPM031001BO BO : productStockTakingList) {

            ProductInventoryVO productInventoryVO = productInventoryVOMap.get(BO.getProductId().toString() + CommonConstants.CHAR_VERTICAL_BAR + BO.getLocationId().toString());

            if(productInventoryVO == null) {// insert

                if(BigDecimal.ZERO.compareTo(BO.getActualQty()) < 0) {

                    productInventoryVO = new ProductInventoryVO();
                    productInventoryVO.setSiteId(siteId);
                    productInventoryVO.setFacilityId(facilityId);
                    productInventoryVO.setProductId(BO.getProductId());
                    productInventoryVO.setLocationId(BO.getLocationId());
                    productInventoryVO.setPrimaryFlag(CommonConstants.CHAR_N);
                    productInventoryVO.setQuantity(BO.getActualQty());
                    productInventoryVO.setProductClassification(ProductClsType.PART.getCodeDbid());
                    productInventoryUpdateList.add(productInventoryVO);
                }
            }else {// update

                BigDecimal quantity = productInventoryVO.getQuantity() == null ? BigDecimal.ZERO : productInventoryVO.getQuantity();
                BigDecimal qty = quantity.add(BO.getActualQty().subtract(BO.getExpectedQty()));

                // 更新时，【qty + pst.actual_qty - pst.expected_qty】必须大于等于零。
                if(qty.compareTo(BigDecimal.ZERO) >= 0) {

                    productInventoryVO.setQuantity(qty);
                    productInventoryUpdateList.add(productInventoryVO);
                }else {

                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00467", new String[] {BO.getProductCd()}));
                }
            }
        }
        return productInventoryUpdateList;
    }

        private List<InventoryTransactionVO> insertInventoryTransaction(String siteId
                                                                  , Long facilityId
                                                                  , List<SPM031001BO> productStockTakingList
                                                                  , Long personId
                                                                  , String personName) {

        List<InventoryTransactionVO> inventoryTransactionInsertList = new ArrayList<>();

        // 准备数据
        // 取得所有productStockStatusVO.quantity之和为stockQty
        Map<Long, BigDecimal> productStockQtyMap = new HashMap<>();
        Set<Long> productIds = productStockTakingList.stream().map(SPM031001BO::getProductId).collect(Collectors.toSet());
        List<ProductStockStatusVO> productStockStatusVOs = spm0310Service.getProductStockStatus(siteId
                                                                                              , facilityId
                                                                                              , productIds
                                                                                              , paramStockStatusTypeIn());

        Map<Long, List<ProductStockStatusVO>> stockStatusGroupByProduct = productStockStatusVOs.stream().collect(Collectors.groupingBy(ProductStockStatusVO::getProductId));

        for (Entry<Long, List<ProductStockStatusVO>> entry : stockStatusGroupByProduct.entrySet()) {

            List<ProductStockStatusVO> stockStatusListByProduct = stockStatusGroupByProduct.get(entry.getKey());
            BigDecimal stockQty = stockStatusListByProduct.stream().map(item -> item.getQuantity() != null ? item.getQuantity() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
            productStockQtyMap.put(entry.getKey(), stockQty);
        }

        // 准备好上面的数据，下面新增InventoryTransaction表
        for(SPM031001BO BO : productStockTakingList) {

            String type;
            String typeNm;
            BigDecimal inQty = BigDecimal.ZERO;
            BigDecimal outQty = BigDecimal.ZERO;

            // 获取指定ProductId的库存数量
            BigDecimal quantity = productStockQtyMap.containsKey(BO.getProductId()) ? productStockQtyMap.get(BO.getProductId()): BigDecimal.ZERO;
            BigDecimal qty = quantity.add(BO.getActualQty().subtract(BO.getExpectedQty()));

            if(BO.getActualQty().compareTo(BO.getExpectedQty()) >= 0) {

                type = InventoryTransactionType.ADJUSTIN.getCodeDbid();
                typeNm = InventoryTransactionType.ADJUSTIN.getCodeData1();
                inQty = BO.getActualQty().subtract(BO.getExpectedQty());
            }else {

                type = InventoryTransactionType.ADJUSTOUT.getCodeDbid();
                typeNm = InventoryTransactionType.ADJUSTIN.getCodeData1();
                outQty = BO.getExpectedQty().subtract(BO.getActualQty());
            }


            // 新增
            InventoryTransactionVO invTranVO = new InventoryTransactionVO();
            invTranVO.setSiteId(siteId);
            invTranVO.setPhysicalTransactionDate(LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)));
            invTranVO.setPhysicalTransactionTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_H_M_S)));
            invTranVO.setToFacilityId(facilityId);
            invTranVO.setProductId(BO.getProductId());
            invTranVO.setProductCd(BO.getProductCd());
            invTranVO.setProductNm(BO.getProductNm());
            invTranVO.setTargetFacilityId(facilityId);
            invTranVO.setInventoryTransactionType(type);
            invTranVO.setInventoryTransactionNm(typeNm);
            invTranVO.setInQty(inQty);
            invTranVO.setOutQty(outQty);
            invTranVO.setCurrentQty(qty);
            invTranVO.setInCost(BO.getInCost());
            invTranVO.setOutCost(BigDecimal.ZERO);
            invTranVO.setCurrentAverageCost(BO.getCurrentAverageCost());
            invTranVO.setStockAdjustmentReasonType(type);
            invTranVO.setStockAdjustmentReasonNm(typeNm);
            invTranVO.setLocationId(BO.getLocationId());
            invTranVO.setLocationCd(BO.getLocationCd());
            invTranVO.setReporterId(personId);
            invTranVO.setReporterNm(getUserNameValue(personName));
            invTranVO.setProductClassification(ProductClsType.PART.getCodeDbid());

            inventoryTransactionInsertList.add(invTranVO);
        }
        return inventoryTransactionInsertList;
    }

    private String getUserNameValue(String userName) {

        // 因为uc.username是Json型，所以要转化一下来拿到username对应的值
        String usernameValue = CommonConstants.CHAR_BLANK;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(userName);

            // 检查是否为对象
            if (jsonNode.isObject() && jsonNode.has("username")) {

                JsonNode usernameNode = jsonNode.get("username");
                if (usernameNode.isTextual()) {

                    usernameValue = usernameNode.asText();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return usernameValue;
    }

    private Set<String> paramStockStatusTypeIn() {

        return new HashSet<>(Arrays.asList(  SpStockStatus.ONHAND_QTY.getCodeDbid()         // S018ONHANDQTY
                                            ,SpStockStatus.ONCANVASSING_QTY.getCodeDbid()  // S018ONCANVASSINGQTY
                                            ,SpStockStatus.ONTRANSFER_IN_QTY.getCodeDbid()  // S018ONTRANSFERINQTY
                                            ,SpStockStatus.ONFROZEN_QTY.getCodeDbid()       // S018ONFROZENQTY
                                            ,SpStockStatus.ALLOCATED_QTY.getCodeDbid()      // S018ALLOCATEDQTY
                                            ,SpStockStatus.SHIPPING_REQUEST.getCodeDbid()   // S018SHIPPINGREQUEST
                                            ,SpStockStatus.ONSHIPPING.getCodeDbid()         // S018ONSHIPPING
                                            ,SpStockStatus.ONBORROWING_QTY.getCodeDbid()    // S018ONBORROWINGQTY
                                            ,SpStockStatus.ONSERVICE_QTY.getCodeDbid()      // S018ONSERVICEQTY
                                            ,SpStockStatus.ONPICKING_QTY.getCodeDbid()      // S018ONPICKINGQTY
                                            ,SpStockStatus.ONRECEIVING_QTY.getCodeDbid()    // S018ONRECEIVINGQTY
                                          ));
    }

    public List<SPM031001PrintBO> getPartsReceiveAndRegisterPrintList(SPM031001Form form, String siteId) {
        List<SPM031001BO> printList =spm0310Service.getPartsStocktakingResultStatisticsList(siteId, form.getPointId());
        MstFacilityVO mstFacility = spm0310Service.getFacilityCdAndFacilityNm(form.getPointId());

        List<SPM031001PrintBO> returnList = new ArrayList<>();

        SPM031001PrintBO printBO = new SPM031001PrintBO();
        if(mstFacility != null) {
            printBO.setPointAbbr(mstFacility.getFacilityCd() + " / " + mstFacility.getFacilityCd());
        }
        printBO.setDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMDHMS));
        if(printList.isEmpty()) {
            SPM031001BO bo = new SPM031001BO();
            printBO.setDetailPrintResultStatisticsList(Collections.singletonList(bo));
        }else {
            printBO.setDetailPrintResultStatisticsList(printList);
        }
        returnList.add(printBO);

        return returnList;
    }

    public List<SPM031001PrintBO> getPartsStocktakingGapPrintList(SPM031001Form form, String siteId) {
        List<SPM031001BO> printList =spm0310Service.getPrintPartsStocktakingGapList(siteId, form.getPointId());
        MstFacilityVO mstFacility = spm0310Service.getFacilityCdAndFacilityNm(form.getPointId());

        List<SPM031001PrintBO> returnList = new ArrayList<>();
        for(SPM031001BO bo: printList) {
            bo.setProductCd(PartNoUtil.format(bo.getProductCd()));
            bo.setGainQty(bo.getExpectedQty().subtract(bo.getActualQty()).compareTo(BigDecimal.ZERO) < 0
                          ? BigDecimal.ZERO : bo.getExpectedQty().subtract(bo.getActualQty()));

            bo.setGainActQty(bo.getActualQty().subtract(bo.getExpectedQty()).compareTo(BigDecimal.ZERO) < 0
                    ? BigDecimal.ZERO : bo.getActualQty().subtract(bo.getExpectedQty()));
        }

        SPM031001PrintBO printBO = new SPM031001PrintBO();
        if(mstFacility != null) {
            printBO.setPointAbbr(mstFacility.getFacilityCd() + " / " + mstFacility.getFacilityCd());
        }
        printBO.setDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMDHMS));
        if(printList.isEmpty()) {
            SPM031001BO bo = new SPM031001BO();
            printBO.setDetailPrintGapList(Collections.singletonList(bo));
        }else {
            printBO.setDetailPrintGapList(printList);
        }
        returnList.add(printBO);

        return returnList;
    }

    public List<SPM031001PrintBO> getPrintPartsStocktakingLedger(SPM031001Form form, String siteId) {
        List<SPM031001BO> printList =spm0310Service.getPrintPartsStocktakingLedger(siteId, form.getPointId());
        MstFacilityVO mstFacility = spm0310Service.getFacilityCdAndFacilityNm(form.getPointId());

        List<SPM031001PrintBO> returnList = new ArrayList<>();
        for(SPM031001BO bo: printList) {
            bo.setProductCd(PartNoUtil.format(bo.getProductCd()));
        }

        SPM031001PrintBO printBO = new SPM031001PrintBO();
        if(mstFacility != null) {
            printBO.setPointAbbr(mstFacility.getFacilityCd() + " / " + mstFacility.getFacilityCd());
        }
        printBO.setDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMDHMS));
        if(printList.isEmpty()) {
            SPM031001BO bo = new SPM031001BO();
            printBO.setDetailPrintLedgerList(Collections.singletonList(bo));
        }else {
            printBO.setDetailPrintLedgerList(printList);
        }
        returnList.add(printBO);

        return returnList;
    }
}
