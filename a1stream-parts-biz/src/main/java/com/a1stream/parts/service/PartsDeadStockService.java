/******************************************************************************/
/* SYSTEM     : Stream                                                        */
/*                                                                            */
/* SUBSYSTEM  : Xm03                                                          */
/******************************************************************************/
package com.a1stream.parts.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.constants.MstCodeConstants.SystemParameterType;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.constants.PJConstants.ReturnRequestStatus;
import com.a1stream.common.constants.PJConstants.RopRoqParameter;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.common.manager.CallNewIfsManager;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.domain.bo.batch.HeaderBO;
import com.a1stream.domain.bo.batch.PartsDeadStockItemBO;
import com.a1stream.domain.bo.batch.PartsDeadStockItemXmlBO;
import com.a1stream.domain.entity.CmmSiteMaster;
import com.a1stream.domain.entity.ReturnRequestList;
import com.a1stream.domain.entity.SystemParameter;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.repository.InventoryTransactionRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.ReturnRequestListRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.alibaba.fastjson.JSON;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.IdUtils;
import com.ymsl.solid.base.util.StringUtils;
import com.ymsl.solid.base.util.uuid.SnowflakeIdWorker;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PartsDeadStockService {

    @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepository;

    @Resource
    private SystemParameterRepository systemParameterRepository;

    @Resource
    private ReturnRequestListRepository returnRequestListRepository;

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private InventoryTransactionRepository inventoryTransactionRepository;

    @Resource
    private CallNewIfsManager callNewIfsManager;

    @Value("${ifs.request.url}")
    private String ifsRequestUrl;

    public List<PartsDeadStockItemBO> doDeadStockCalculate(String inputDealerCode) {

        List<CmmSiteMaster> sites = null;
        if(StringUtils.isBlankText(inputDealerCode)) {

            sites = getAllLocalSites(null);
        }else {

            sites = getAllLocalSites(inputDealerCode);
        }
        //
        if(!sites.isEmpty()) {

            BigDecimal deadStockBaseMonth = getDeadStockBaseMonth();
            List<PartsDeadStockItemBO> spDeadStockList = new ArrayList<PartsDeadStockItemBO>();
            for (CmmSiteMaster site : sites) {

               List<PartsDeadStockItemBO> founds = this.processDeadStockCalcBySite( site.getSiteId()
                                                                                   ,site.getSiteCd()
                                                                                   ,deadStockBaseMonth);
               if(founds !=null && !founds.isEmpty()) {
                   spDeadStockList.addAll(founds);
               }
            }

            //Export to interface file.

            if(spDeadStockList.size() > 0) {

                String ifsCode = InterfCode.OX_SPDEADSTOCK;
                PartsDeadStockItemXmlBO sendDataBo = new PartsDeadStockItemXmlBO();
                sendDataBo.setHeader(this.setHeaderBo());
                sendDataBo.setOrderItems(spDeadStockList);

                String jsonStr = JSON.toJSON(sendDataBo).toString();
                callNewIfsManager.callNewIfsService(ifsRequestUrl, ifsCode, jsonStr);
                log.info("Async Message Send Success...");
            }

            return spDeadStockList;
        }else {
            return null;
        }
    }

    public List<PartsDeadStockItemBO> processDeadStockCalcBySite(String siteId, String siteCode, BigDecimal baseMonthsOfUserSetting) {

        StopWatch timer = new StopWatch();
        timer.start("Prepare data");

        baseMonthsOfUserSetting = (baseMonthsOfUserSetting ==null) ? BigDecimal.ZERO : baseMonthsOfUserSetting;
        this.writeInfoLog("**Dead stock process for site:%s, base month:%s", siteId, baseMonthsOfUserSetting.toString());

        //1. Update the status of return-request to 'DENNY'
        updateStatusOfReturnRequestToDennyIfNecceassay(siteId);

        //2. Get all the 'warehouse' type and the suitable status of Return-Request points
        List<PartsDeadStockItemBO> warehouses = mstFacilityRepository.getAllNoneReturnWarehousePoints(siteId);
        this.writeInfoLog("The number of warehouses: %d", warehouses.size());
        this.stopTimerAndwriteLastTaskTimeLog(timer);

         //3. Loop the got points.
        List<PartsDeadStockItemBO> result = new LinkedList<PartsDeadStockItemBO>();
        for(PartsDeadStockItemBO oneWarehouse : warehouses) {

            Long facilityId = oneWarehouse.getFacilityId();
            String facilityCode = oneWarehouse.getFacilityCd();
            timer.start("Calculate deadStock for point:" + facilityCode);

            //3.1 Get the 'Parts' classification and not 'New' parts.
            Set<Long> foundPartsIds = new HashSet<Long>();
            List<PartsDeadStockItemBO> stockParts = mstProductRepository.getAllNoneNewAndHasStockParts(siteId, facilityId);
            List<PartsDeadStockItemBO> futureStockParts =  this.getFutureStockParts(stockParts, foundPartsIds);
            Map<Long,Integer> deadStockProductIndexMap = new HashMap<Long,Integer>();
            this.writeInfoLog("The number of none new parts: %d for point: %s", futureStockParts.size(), facilityCode);

            if (foundPartsIds.size() == 0) {
                continue;
            }
            Map<Long, String[]> j1TotalAndAvgDemandMap = this.getAllJ1TotalAndAvgDemandMap(siteId, facilityId, foundPartsIds);
            //3.2 Loop the got parts.
            for(PartsDeadStockItemBO product : futureStockParts) {

                PartsDeadStockItemBO item = this.calcDeadStockQtyWithParts(siteId,
                                                                          product.getProductId(),
                                                                          baseMonthsOfUserSetting,
                                                                          j1TotalAndAvgDemandMap,
                                                                          foundPartsIds,
                                                                          product.getQuantity(),
                                                                          product.getAbcType());
                if(item !=null){
                    item.setDealerCode(siteCode);
                    item.setConsigneeCode(oneWarehouse.getFacilityCd());
                    item.setPartNo(product.getProductCd());
                    result.add(item);
                    deadStockProductIndexMap.put(product.getProductId(), result.size()-1);
                }else {
                    foundPartsIds.remove(product.getProductId());
                }
            }
            this.writeInfoLog("Found dead stock parts:%d for point:%s", deadStockProductIndexMap.size()
                                                                      , facilityCode);

            //3.3 Set the stock adjustment flag for each found.
            if(!foundPartsIds.isEmpty()) {
                processStockAjustSettingWithResult(siteId,facilityId, result, deadStockProductIndexMap, foundPartsIds);
            }

            foundPartsIds.clear();
            foundPartsIds = null;
            if (stockParts != null) {
                stockParts.clear();
                stockParts = null;
            }
            if (futureStockParts != null) {
                futureStockParts.clear();
                futureStockParts = null;
            }
            if (deadStockProductIndexMap != null) {
                deadStockProductIndexMap.clear();
                deadStockProductIndexMap = null;
            }
            this.stopTimerAndwriteLastTaskTimeLog(timer);
        }

        //Release resources.
        timer.start("Release resources");
        this.stopTimerAndwriteLastTaskTimeLog(timer);
        this.writeInfoLog("**Final site %s get rows:%d, duration:%dms", siteId, result.size(), timer.getTotalTimeMillis());
        return result;
    }

    private void processStockAjustSettingWithResult(String siteId
                                                  , Long facilityId
                                                  , List<PartsDeadStockItemBO> result
                                                  , Map<Long,Integer> deadStockProductIndexMap
                                                  , Set<Long> includingPartsIds) {


        for (Long partsId : includingPartsIds) {

            int indexOfParts = deadStockProductIndexMap.get(partsId);
            boolean add = this.isStockAddOccurseOnParts(siteId,facilityId, partsId, includingPartsIds);
            result.get(indexOfParts).setStockAdjustExistSign(add ? CommonConstants.CHAR_ONE: CommonConstants.CHAR_ZERO);
        }
    }

    public boolean isStockAddOccurseOnParts(String siteId, Long facilityId, Long currentPartsId, Set<Long> includingPartsIds) {

        String transactionDate =  DateUtils.date2String(DateUtils.addMonth(DateUtils.getCurrentDate(),-12),DateUtils.FORMAT_YMD_NODELIMITER);
        List<Long> partsIds = inventoryTransactionRepository.getAllStockAjustPartsOnPoint(siteId
                                                                                          , transactionDate
                                                                                          , InventoryTransactionType.ADJUSTIN.getCodeDbid()
                                                                                          , facilityId
                                                                                          , includingPartsIds);
        if (partsIds.size() > 0) {

            return partsIds.contains(currentPartsId);
        } else {

            return false;
        }
    }

    private PartsDeadStockItemBO calcDeadStockQtyWithParts(String siteId,
                                                           Long productId,
                                                           BigDecimal baseMonthsOfUserSetting,
                                                           Map<Long, String[]> j1TotalAndAvgDemandMap,
                                                           Set<Long> partsIds,
                                                           BigDecimal futureStockQty,
                                                           String abcType ) {


        BigDecimal deadStockQty = calcDeadStockQty( baseMonthsOfUserSetting,
                                                      futureStockQty,
                                                      abcType,
                                                      productId,
                                                      j1TotalAndAvgDemandMap,
                                                      siteId);
        if(deadStockQty == null) { //Skip
            return null;
        }

        deadStockQty = deadStockQty.setScale(0, RoundingMode.DOWN); // example: 0.01->0, 1.9-> 1
        PartsDeadStockItemBO item = new PartsDeadStockItemBO();
        item.setDeadStockQty(deadStockQty.toString());

        /*item.setStockAdjustExistSign(helper.isStockAddOccurseOnParts(facilityId, productId, partsIds)
        ? Xm03GeneralConstants.CHAR_ONE
        : Xm03GeneralConstants.CHAR_ZERO);*/
        return item;
    }

    private BigDecimal calcDeadStockQty(BigDecimal baseMonthsOfUserSetting,
                                        BigDecimal futureStockQty,
                                        String abcType,
                                        Long productId,
                                        Map<Long, String[]> j1TotalAndAvgDemandMap,
                                        String siteId) {

        if(isNoForSalesType(abcType)) {
            return futureStockQty;
        }

        BigDecimal avgDemandQty = this.calcAverageDemand(productId, abcType, j1TotalAndAvgDemandMap);
        BigDecimal deadStockQty = null;

        if (avgDemandQty == null || avgDemandQty.signum() <= 0) { // avgDemand<=0
            this.writeTraceLog("Average demand is negative or zero, it get futrue stock qty.");
            deadStockQty = futureStockQty;
        } else {// avgDemand >0
            BigDecimal futureStockMonths = futureStockQty.divide(avgDemandQty,2, RoundingMode.HALF_UP);
            // BigDecimal futureStockMonths =
            // this.calcFutureStockMonths(futureStockQty,
            // sumOfAllSubstsJ1Total);
            BigDecimal remainingDeadStockMonths = futureStockMonths.subtract(baseMonthsOfUserSetting);
            if (remainingDeadStockMonths.signum() <= 0) { // There is not any
                                                        // months left, it
                                                        // will skip.
                return null;
            } else {
                deadStockQty = remainingDeadStockMonths.multiply(avgDemandQty);
                if(deadStockQty.compareTo(BigDecimal.ONE) <0) { //  deadStockQty<1, skip.
                    return null;
                }else if (deadStockQty.signum() >0 && deadStockQty.compareTo(futureStockQty) > 0) { //deadStockQty>0 && deadStockQty>futureStockQty
                    deadStockQty = futureStockQty;
                }
            }
        }
        return deadStockQty;
    }

    public List<PartsDeadStockItemBO> getFutureStockParts(List<PartsDeadStockItemBO>  parts
                                                         ,Set<Long> foundDeadStockPartsId) {

        Long oldProductId = null;
        String oldProductCode = "";
        String oldAbcType = "";
        Map<String, BigDecimal> stockStatusQtyMap = new HashMap<String, BigDecimal>();
        List<PartsDeadStockItemBO> foundDeadStockProducts = new ArrayList<PartsDeadStockItemBO>();
        int removedCount = 0;
        int qualifyCount = 0;
        int count        = 0;
        for (PartsDeadStockItemBO partsDeadStockItemBO : parts) {

            count++;
            if(!(oldProductId == null || NumberUtil.equals(oldProductId, partsDeadStockItemBO.getProductId()))) {

                PartsDeadStockItemBO returnDeadStock = this.processProductWithStock(oldProductId, oldProductCode, stockStatusQtyMap, oldAbcType,removedCount,qualifyCount,foundDeadStockPartsId,foundDeadStockProducts);
                if(returnDeadStock != null) {
                    foundDeadStockProducts.add(returnDeadStock);
                }

                oldProductId = partsDeadStockItemBO.getProductId();
                oldProductCode = partsDeadStockItemBO.getProductCd();
                oldAbcType = partsDeadStockItemBO.getAbcType();
                stockStatusQtyMap = new HashMap<String, BigDecimal>();
                stockStatusQtyMap.put(partsDeadStockItemBO.getProductStockStatusTypeDbId(), partsDeadStockItemBO.getQuantity());
            }else {

                stockStatusQtyMap.put(partsDeadStockItemBO.getProductStockStatusTypeDbId(), partsDeadStockItemBO.getQuantity());
                if(parts.size() == count) {
                    PartsDeadStockItemBO returnDeadStock = this.processProductWithStock(oldProductId, oldProductCode, stockStatusQtyMap, oldAbcType,removedCount,qualifyCount,foundDeadStockPartsId,foundDeadStockProducts);
                    if(returnDeadStock != null) {
                        foundDeadStockProducts.add(returnDeadStock);
                    }
                }
                if(oldProductId == null) {
                    oldProductId = partsDeadStockItemBO.getProductId();;
                    oldProductCode = partsDeadStockItemBO.getProductCd();;
                    oldAbcType = partsDeadStockItemBO.getAbcType();;
                }
            }
        }

        if(log.isDebugEnabled()) {
            log.debug(String.format("Found qualify:%d, removed:%d", qualifyCount, removedCount));
        }

        return foundDeadStockProducts;
    }

    private PartsDeadStockItemBO processProductWithStock(Long productId
                                                      , String productCode
                                                      , Map<String, BigDecimal> stocks
                                                      , String abcType
                                                      , int removedCount
                                                      , int qualifyCount
                                                      , Set<Long> foundDeadStockPartsId
                                                      , List<PartsDeadStockItemBO> foundDeadStockProducts) {

        BigDecimal onHandQty = getStockQty(stocks, SpStockStatus.ONHAND_QTY.getCodeDbid());
        BigDecimal onRecQty = getStockQty(stocks, SpStockStatus.ONRECEIVING_QTY.getCodeDbid());
        BigDecimal onEoOnPurQty = getStockQty(stocks, SpStockStatus.EO_ONPURCHASE_QTY.getCodeDbid());
        BigDecimal onRoOnPurQty = getStockQty(stocks, SpStockStatus.RO_ONPURCHASE_QTY.getCodeDbid());
        BigDecimal onHoOnPurQty = getStockQty(stocks, SpStockStatus.HO_ONPURCHASE_QTY.getCodeDbid());
        BigDecimal onTranfQty = getStockQty(stocks, SpStockStatus.ONTRANSFER_IN_QTY.getCodeDbid());
        BigDecimal boQty = getStockQty(stocks, SpStockStatus.BO_QTY.getCodeDbid());

        //futureStockQty= onHand + onRec + onEoPur + onRoPur + onHoPur + onTranf - onBo
        BigDecimal futureStockQty = onHandQty.add(onRecQty)
                             .add(onEoOnPurQty)
                             .add(onRoOnPurQty)
                             .add(onHoOnPurQty)
                             .add(onTranfQty)
                             .subtract(boQty);
        if(futureStockQty.signum() > 0) { //futureStock >0

            PartsDeadStockItemBO partsDeadStockItemBO = new PartsDeadStockItemBO();
            partsDeadStockItemBO.setProductId(productId);
            partsDeadStockItemBO.setProductCd(productCode);
            partsDeadStockItemBO.setQuantity(futureStockQty);
            partsDeadStockItemBO.setAbcType(abcType);
            foundDeadStockPartsId.add(productId);
            qualifyCount ++;

            return partsDeadStockItemBO;
        }else {
            if(log.isTraceEnabled()) {
                log.trace("Future stock is negative, remove it:" + productId);
            }
            removedCount ++;
            return null;
        }
    }

    private BigDecimal calcAverageDemand(Long productId,
                                        String abcType,
                                        Map<Long, String[]> j1TotalAndAvgDemandMap) {

        if(isNormalSalesType(abcType)) {

            return NumberUtil.toBigDecimal(j1TotalAndAvgDemandMap.get(productId)[0]);
        }else if(isSupersededPartsSalesType(abcType)) {

            return NumberUtil.toBigDecimal(j1TotalAndAvgDemandMap.get(productId)[1]).divide(CommonConstants.BIGDECIMAL_TWELVE, 2, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    private Map<Long, String[]> getAllJ1TotalAndAvgDemandMap(String siteId,Long facilityId,Set<Long> partsIds){

        List<PartsDeadStockItemBO> J1TotalAndAvgDemands = mstProductRepository.getAllJ1TotalAndAvgDemandOnParts(siteId, facilityId, partsIds);
        Long oldProductId = null;
        String[] aliases = new String[2];
        String j1Total = "0";
        String avgDemand = "0";
        int count        = 0;
        Map<Long, String[]> j1TotalAndAvgDemandMap = new HashMap<Long, String[]>();
        for (PartsDeadStockItemBO partsDeadStockItemBO : J1TotalAndAvgDemands) {

            count++;
            if(!(oldProductId == null || NumberUtil.equals(oldProductId, partsDeadStockItemBO.getProductId()))) {

                aliases = new String[2];
                aliases[0] = avgDemand;
                aliases[1] = j1Total;
                j1TotalAndAvgDemandMap.put(oldProductId, aliases);

                j1Total = "0";
                avgDemand = "0";
                if(StringUtils.equals(RopRoqParameter.KEY_PARTSJ1TOTAL, partsDeadStockItemBO.getRopqType())) {
                    j1Total = partsDeadStockItemBO.getStringValue();
                }else if(StringUtils.equals(RopRoqParameter.KEY_PARTSAVERAGEDEMAND, partsDeadStockItemBO.getRopqType())) {
                    avgDemand = partsDeadStockItemBO.getStringValue();;
                }

                oldProductId = partsDeadStockItemBO.getProductId();
            }else {

                if(StringUtils.equals(RopRoqParameter.KEY_PARTSJ1TOTAL, partsDeadStockItemBO.getRopqType())) {
                    j1Total = partsDeadStockItemBO.getStringValue();
                }else if(StringUtils.equals(RopRoqParameter.KEY_PARTSAVERAGEDEMAND, partsDeadStockItemBO.getRopqType())) {
                    avgDemand = partsDeadStockItemBO.getStringValue();;
                }
                if (J1TotalAndAvgDemands.size() == count) {

                    aliases = new String[2];
                    aliases[0] = avgDemand;
                    aliases[1] = j1Total;
                    j1TotalAndAvgDemandMap.put(oldProductId, aliases);
                }
                oldProductId = partsDeadStockItemBO.getProductId();
            }
        }

        return j1TotalAndAvgDemandMap;
    }

    private BigDecimal getStockQty( Map<String, BigDecimal> stocks, String type) {

        BigDecimal qty = stocks.get(type);
        return (qty ==null) ? BigDecimal.ZERO : qty;
    }

    private void updateStatusOfReturnRequestToDennyIfNecceassay(String siteId) {

        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);
        List<ReturnRequestList> returnRequestList = returnRequestListRepository.getReturnRequestListForExpdateAndStatus(siteId
                                                                                                                       ,sysDate
                                                                                                                       ,ReturnRequestStatus.RECOMMENDED.getCodeDbid());
        int s = returnRequestList.size();
        this.writeInfoLog("Got %d 'Recommended' return-requests.", s);
        if(s > 1) {
            log.warn(s + "'Recommended' return-requests found!");
        }
        if (s > 0) {
           this.returnRequestListRepository.save(returnRequestList.iterator().next());
        }

    }

    private BigDecimal getDeadStockBaseMonth() {

        SystemParameter systemParameter = systemParameterRepository.findBySystemParameterTypeId(SystemParameterType.DEADSTOCKMONTHS);
        return new BigDecimal(systemParameter.getParameterValue());
    }

    private List<CmmSiteMaster> getAllLocalSites(String siteCode) {

        if (StringUtils.isBlankText(siteCode)) {
            return cmmSiteMasterRepository.findByActiveFlag(CommonConstants.CHAR_Y);
        } else {

            Set<String> siteCodes = Set.of(siteCode);
            return cmmSiteMasterRepository.findBySiteIdInAndActiveFlag(siteCodes, CommonConstants.CHAR_Y);
        }
    }

    private boolean isNoForSalesType(String type) {

        List<String> typeList = new ArrayList<String>();
        typeList.add("D");
        typeList.add("E");
        typeList.add("X");
        if(type ==null || type.length()==0) {
            return false;
        }else {
            return  typeList.contains(type.substring(0, 1));
        }
    }

    private boolean isNormalSalesType(String type) {

        List<String> typeList = new ArrayList<String>();
        typeList.add(CommonConstants.CHAR_A);
        typeList.add(CommonConstants.CHAR_B);
        typeList.add(CommonConstants.CHAR_C);
        if(type ==null || type.length()==0) {
            return false;
        }else {
            return  typeList.contains(type.substring(0, 1));
        }
    }

    private boolean isSupersededPartsSalesType(String abcType) {

        List<String> typeList = new ArrayList<String>();
        typeList.add(CommonConstants.CHAR_Y);
        if(abcType ==null || abcType.length()==0) {
            return false;
        }else {
            return  typeList.contains(abcType.substring(0, 1));
        }
    }

    protected void writeInfoLog(String logMessageTemplate, Object ... templateArgs) {

        if(log.isInfoEnabled()) {
            log.info(String.format(logMessageTemplate, templateArgs));
        }
    }

    protected void writeTraceLog(String logMessageTemplate, Object ... templateArgs) {

        if(log.isTraceEnabled()) {
            log.trace(String.format(logMessageTemplate, templateArgs));
        }
    }

    public HeaderBO setHeaderBo() {

        SnowflakeIdWorker snowflakeIdWorker = IdUtils.getSnowflakeIdWorker();

        String currentDateString = DateUtils.getCurrentDateString(DateUtils.FORMAT_YMDHMSS_HYPHEN);
        HeaderBO headerBO = new HeaderBO();
        headerBO.setRequestId(snowflakeIdWorker.nextIdStr());
        headerBO.setMessageType(InterfCode.OX_SPDEADSTOCK);
        headerBO.setSenderCode(CommonConstants.CHAR_DMS);
        headerBO.setReceiverCode(CommonConstants.CHAR_YAMAHA);
        headerBO.setCreateDateTime(currentDateString);

        return headerBO;
    }


    /**
     * @param timer
     */
    private void stopTimerAndwriteLastTaskTimeLog(StopWatch timer) {

        if(timer.isRunning()) {
            timer.stop();
            this.writeInfoLog("Task '%s', duration:%dms", timer.lastTaskInfo().getTaskName(), timer.lastTaskInfo().getTimeMillis());
        }
    }
}