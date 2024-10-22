package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.StockTakingTypeVietnamese;
import com.a1stream.domain.bo.parts.SPM030801BO;
import com.a1stream.domain.bo.parts.SPM030801PrintBO;
import com.a1stream.domain.bo.parts.SPM031001BO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.ProductStockTakingVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.a1stream.parts.service.SPM0308Service;
import com.a1stream.parts.service.SPM0310Service;
import com.ymsl.plugins.userauth.util.ListSortUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:start stockTaking
*
* @author mid2178
*/
@Component
public class SPM0308Facade {

    @Resource
    private SPM0308Service spm0308Service;

    @Resource
    private SPM0310Service spm0310Service;

    public void doStartStockTaking(Long facilityId, String stockTakingRange, String siteId) {

        // 校验是否在盘点中
        validateBeforeStockTaking(siteId, facilityId);

        // insert or update SystemParameter
        SystemParameterVO systemParameterUpdate = updateSystemParameter(siteId, facilityId);

        // delete product_staock_tacking
        List<ProductStockTakingVO> productStockTakingDelete = spm0308Service.getProductStockTakingInfo(siteId, facilityId);

        // insert product_staock_tacking
        List<ProductStockTakingVO> productStockTakingInsertList = generateProductStockTaking(siteId, facilityId, stockTakingRange);

        // save all
        spm0308Service.updateData(systemParameterUpdate,productStockTakingDelete,productStockTakingInsertList);
    }

    private List<ProductStockTakingVO> insertProductStockTaking(List<SPM030801BO> insertData
                                                              , String siteId
                                                              , Long facilityId
                                                              , String rangeType) {

        List<ProductStockTakingVO> productStockTakingInsertList = new ArrayList<>();
        Integer seqNo = CommonConstants.INTEGER_ONE;
        for(SPM030801BO data : insertData) {

            ProductStockTakingVO productStockTakingInsert = new ProductStockTakingVO();
            productStockTakingInsert.setSiteId(siteId);
            productStockTakingInsert.setFacilityId(facilityId);
            productStockTakingInsert.setRangeType(rangeType);
            productStockTakingInsert.setSeqNo(seqNo);
            productStockTakingInsert.setWorkzoneId(data.getWorkzoneId());
            productStockTakingInsert.setLocationId(data.getLocationId());
            productStockTakingInsert.setProductId(data.getProductId() == null ? null : data.getProductId());
            productStockTakingInsert.setExpectedQty(data.getQty() == null ? BigDecimal.ZERO : data.getQty());
            productStockTakingInsert.setActualQty(data.getQty() == null ? BigDecimal.ZERO : data.getQty());
            productStockTakingInsert.setInputFlag(CommonConstants.CHAR_N);
            productStockTakingInsert.setNewFoundFlag(CommonConstants.CHAR_N);
            productStockTakingInsert.setCurrentAverageCost(data.getCost() == null ? BigDecimal.ZERO : data.getCost());
            productStockTakingInsert.setStartedDate(LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)));
            productStockTakingInsert.setStartedTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_H_M_S)));
            productStockTakingInsert.setProductClassification(ProductClsType.PART.getCodeDbid());
            seqNo++;
            productStockTakingInsertList.add(productStockTakingInsert);
        }
        return productStockTakingInsertList;
    }

    private void validateBeforeStockTaking(String siteId, Long facilityId) {

        SystemParameterVO processing = spm0308Service.getProcessingSystemParameter(siteId
                                                                                 , facilityId
                                                                                 , MstCodeConstants.SystemParameterType.STOCKTAKING
                                                                                 , CommonConstants.CHAR_ONE);

        if(processing != null) {
            // 该point在盘点时，提示错误信息
            throw new BusinessCodedException(CodedMessageUtils.getMessage("errors.stockTaking", new String[] {}));
        }
    }

    private SystemParameterVO updateSystemParameter(String siteId, Long facilityId) {

        SystemParameterVO systemParameterUpdate = spm0308Service.getSystemParameterInfo(siteId
                                                                                    , facilityId
                                                                                    , MstCodeConstants.SystemParameterType.STOCKTAKING);

        if(systemParameterUpdate == null) {// insert

            systemParameterUpdate = new SystemParameterVO();
            systemParameterUpdate.setSiteId(siteId);
            systemParameterUpdate.setFacilityId(facilityId);
            systemParameterUpdate.setParameterValue(CommonConstants.CHAR_ONE);
            systemParameterUpdate.setSystemParameterTypeId(MstCodeConstants.SystemParameterType.STOCKTAKING);
        }else {// update

            systemParameterUpdate.setParameterValue(CommonConstants.CHAR_ONE);
        }
        return systemParameterUpdate;
    }

    private List<ProductStockTakingVO> generateProductStockTaking(String siteId, Long facilityId, String stockTakingRange) {

        List<SPM030801BO> insertData = null;
        String rangeType = CommonConstants.CHAR_BLANK;
        String parts = ProductClsType.PART.getCodeDbid();

        // 查出库存信息stockOnlyList
        // stockOnlyMap <locationId , SPM030801BO>
        List<SPM030801BO> stockOnlyList = spm0308Service.getStockOnlyLocationInfo(siteId
                                                                                , facilityId
                                                                                , PJConstants.CostType.AVERAGE_COST
                                                                                , parts);

        Map<Long, SPM030801BO> stockOnlyMap = stockOnlyList.stream().collect(Collectors.toMap( v -> v.getProductInventoryId(), Function.identity()));

        if(CommonConstants.CHAR_ZERO.equals(stockTakingRange)) {// stock only

            insertData = stockOnlyList;
            rangeType = PJConstants.StockTakingRangeType.STOCKONLY.getCodeDbid();

        }else if (CommonConstants.CHAR_ONE.equals(stockTakingRange)){// all location

            // 查出所有location
            List<SPM030801BO> allLocationList = spm0308Service.getAllLocationInfo(siteId, facilityId, parts);

            // 将allLocationList中有productInventory的库存信息stockOnlyList整合进去
            for(SPM030801BO allLocation : allLocationList) {

                if(allLocation.getProductInventoryId() != null && stockOnlyMap.containsKey(allLocation.getProductInventoryId())) {

                    allLocation.setProductId(stockOnlyMap.get(allLocation.getProductInventoryId()).getProductId());
                    allLocation.setQty(stockOnlyMap.get(allLocation.getProductInventoryId()).getQty());
                    allLocation.setCost(stockOnlyMap.get(allLocation.getProductInventoryId()).getCost());
                }
            }

            insertData = allLocationList;
            rangeType = PJConstants.StockTakingRangeType.ALLLOCATION.getCodeDbid();
        }
        return insertProductStockTaking(insertData, siteId, facilityId, rangeType);
    }

    /**
    * 功能描述:spm030801 打印功能
    * @author mid2330
    */
    public List<SPM030801PrintBO> getPrintPartsStocktakingResultList(Long pointId, String siteId) {
        Map<String,SPM031001BO> productStockTakingMap = spm0310Service.getPrintPartsStocktakingResultList(pointId, siteId);
        List<SPM031001BO> detailList = new ArrayList<>(productStockTakingMap.values());
        MstFacilityVO mstFacility = spm0308Service.getFacilityCdAndFacilityNm(pointId);

        DecimalFormat df = new DecimalFormat("#,###");

        detailList.forEach(item -> item.setLinesPrecent(df.format(item.getLines().setScale(0, RoundingMode.DOWN))));
        detailList.forEach(item -> item.setItemsPrecent(df.format(item.getItems().setScale(0, RoundingMode.DOWN))));
        detailList.forEach(item -> item.setQtyPrecent(df.format(item.getQty().setScale(0, RoundingMode.DOWN))));
        detailList.forEach(item -> item.setAmountPrecent(df.format(item.getAmount().setScale(0, RoundingMode.DOWN))));

        SPM031001BO accuracyPercent = new SPM031001BO();
        SPM031001BO qtyEqualModel = productStockTakingMap.get(StockTakingTypeVietnamese.QTYEQUAL);
        SPM031001BO actualTotalModel = productStockTakingMap.get(StockTakingTypeVietnamese.ACTUAL_TOTAL);

        // Accuracy% = (Actual Qty = System Qty)/Actual Total
        accuracyPercent.setType(StockTakingTypeVietnamese.ACCURACY_PERCCENT);
        accuracyPercent.setLinesPrecent(getPercent(qtyEqualModel.getLines(), actualTotalModel.getLines())+CommonConstants.CHAR_PERCENT);
        accuracyPercent.setItemsPrecent(getPercent(qtyEqualModel.getItems(), actualTotalModel.getItems())+CommonConstants.CHAR_PERCENT);
        accuracyPercent.setQtyPrecent(getPercent(qtyEqualModel.getQty(), actualTotalModel.getQty())+CommonConstants.CHAR_PERCENT);
        accuracyPercent.setAmountPrecent(getPercent(qtyEqualModel.getAmount(), actualTotalModel.getAmount())+CommonConstants.CHAR_PERCENT);
        accuracyPercent.setSeq(CommonConstants.CHAR_SIX);

        detailList.add(accuracyPercent);
        ListSortUtils.sort(detailList, new String[] { "seq" });
        List<SPM030801PrintBO> returnList = new ArrayList<>();
        SPM030801PrintBO printBO = new SPM030801PrintBO();

        if(mstFacility != null) {
            printBO.setPointAbbr(mstFacility.getFacilityCd() + " / " + mstFacility.getFacilityCd());
        }
        printBO.setDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMDHMS));
        List<SPM031001BO> detailPrintList = detailList;
        printBO.setDetailPrintList(detailPrintList);
        returnList.add(printBO);
        return returnList;
    }

    private BigDecimal getPercent(BigDecimal arg1, BigDecimal arg2) {

        if (arg2.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO.setScale(2);
        }

        return arg1.divide(arg2, 4, RoundingMode.HALF_UP)
                   .multiply(BigDecimal.valueOf(100))
                   .setScale(2, RoundingMode.HALF_UP);
    }
}
