package com.a1stream.parts.facade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.SystemParameterType;
import com.a1stream.domain.bo.batch.DemandForecastBatchBO;
import com.a1stream.domain.bo.batch.PartsBatchBO;
import com.a1stream.domain.bo.batch.ProductAbcBatchBO;
import com.a1stream.parts.service.PartsRopqBatchService;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PartsRopqBatchFacade {

    @Resource
    private PartsRopqBatchService partsBatchService;


    public void doPartsBatchFacade(String groupNo) {

        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);

        //查出所有已导入部品模块的可用经销商site
        List<String> siteList = partsBatchService.getSiteList(groupNo);
//        siteList = new ArrayList<>(); //测试
//        siteList.add("YT35"); //测试
//        siteList.add("002A58");
        int idx =1;
        int numberOfSites = siteList.size();
        //get product relation fromproductid
        Map<Long,String> keyProductIdMap = new HashMap<Long, String>();
        Map<Long, Long> fromProductIdMap = partsBatchService.getfromProductId(CommonConstants.CHAR_DEFAULT_SITE_ID);

        //Connection product_abc_info,abc_definition_info type ABCN
        List<PartsBatchBO> productIdMapListData = partsBatchService.getProductRegisterdate(CommonConstants.CHAR_DEFAULT_SITE_ID, keyProductIdMap);
        for (String siteId : siteList) {

            String lastDailyBatchDate = partsBatchService.getSystemParDate(siteId, SystemParameterType.LASTDAILYBATCH);

            SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DB_DATE_FORMAT_YMD);
            Date date = new Date();
            try {
                date = sdf.parse(lastDailyBatchDate);
                date = DateUtils.addDay(date, 1);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String processDate = sdf.format(date);
            String dateTo  = partsBatchService.getAcctMonthInfo(CommonConstants.CHAR_DEFAULT_SITE_ID, lastDailyBatchDate.substring(0, 6));

            if(log.isInfoEnabled()) {
                log.info(String.format("Last daily batch date:%s, process date:%s", lastDailyBatchDate, processDate));
            }

            if (StringUtils.compare(processDate, sysDate,true) >=0) {
                log.info(String.format("The batch process date for site '%s' is expired, it will be ignored!", siteId));
                return;
            }

            if(log.isInfoEnabled()) {
                log.info(String.format("Account month:%s", dateTo));
            }

            // Judge month end or not to TruncateTable
            if (StringUtils.equals(processDate, dateTo) && StringUtils.compare(processDate, sysDate,true) < 0) {

                log.info("delete table Demandforecast,ProductAbc,PartsRopqMonthly and delete ReorderGuideline");
                partsBatchService.truncateTable(siteId);

                log.info(String.format("(%d/%d). Begin processing batch for site [%s]", idx, numberOfSites,siteId));
                //Timer
                StopWatch timer = new StopWatch();
                timer.start("Preparing data");


                // get facilityList
                List<Long> facilityIdList = partsBatchService.getFacilityList(siteId,processDate);


                timer.stop();
                // J1,J2 calculate
                timer.start("1. J1J2Collection");
                Map<String, DemandForecastBatchBO> demfortempMap = partsBatchService.doJ1J2Collection(siteId
                                                                                                       , processDate
                                                                                                       , facilityIdList
                                                                                                       , fromProductIdMap
                                                                                                       , productIdMapListData);
                timer.stop();

                timer.start("2. CostUsageCalculate");
                partsBatchService.doCostUsageCalculate(siteId
                                                       , processDate
                                                       , facilityIdList
                                                       , fromProductIdMap
                                                       , demfortempMap
                                                       , productIdMapListData);
                timer.stop();

                //Connection product_abc_info,abc_definition_info,demand_forecast_temp type ABCN
                timer.start("Get product ABC definition");
                Map<String, ProductAbcBatchBO> productAbcMap = new HashMap<String, ProductAbcBatchBO>();
                List<ProductAbcBatchBO>  productFacilityFeatureList = new ArrayList<ProductAbcBatchBO>();
                List<ProductAbcBatchBO> productAbcModelList  = partsBatchService.getProductTypeABCN(siteId, productAbcMap, demfortempMap, productFacilityFeatureList);
                timer.stop();
//
                //Average demand calculate
                timer.start("3. Average Demand Calculate");
                partsBatchService.doAverageDemandCalculate(siteId
                                                          , processDate
                                                          , productFacilityFeatureList
                                                          , keyProductIdMap);
                timer.stop();

                //Deviation month calculate
                timer.start("4. Deviation Month Calculate");
                partsBatchService.doDeviationMonthCalculate(siteId
                                                         , processDate
                                                         , productAbcMap
                                                         , demfortempMap);
                timer.stop();

                //Safety stock month calculate
                timer.start("5. Safety Stock Month Calculate");
                partsBatchService.doSafetyStockMonthCalculate(siteId
                                                             , processDate
                                                             , productAbcModelList);
                timer.stop();

                //Trend index calculate
                timer.start("6. Trend Index Calculate");
                partsBatchService.doTrendIndexCalculate(siteId, processDate, productFacilityFeatureList);
                timer.stop();

                //Season index calculate
                timer.start("7. Season Index Calculate");
                partsBatchService.doSeasonIndexCalculate(siteId
                                                       , processDate
                                                       , demfortempMap);
                timer.stop();
//
                //Demand forecast calculate
                timer.start("8. Demand Forecast Calculate");
                partsBatchService.doDemandForecastCalculate(siteId
                                                          , processDate
                                                          , fromProductIdMap
                                                          , productFacilityFeatureList);
                timer.stop();

                //RopRoq calculate
                timer.start("9. Rop & Roq Calculate");
                partsBatchService.doRopRoqCalculate(siteId
                                                     , processDate
                                                     , productAbcModelList
                                                     , demfortempMap);
                timer.stop();

              //Release the variables in this method.
                timer.start("Release resouces process.");
                if(productAbcModelList != null) {
                    productAbcModelList.clear();
                    productAbcModelList = null;
                }
                if(productFacilityFeatureList != null) {
                    productFacilityFeatureList.clear();
                    productFacilityFeatureList = null;
                }

                if(facilityIdList != null) {
                    facilityIdList.clear();
                    facilityIdList = null;
                }

                if(productAbcMap != null) {
                    productAbcMap.clear();
                    productAbcMap = null;
                }

                timer.stop();

//                partsBatchService.setSpecParameterValue(siteId, SystemParameterType.LASTDAILYBATCH, processDate);
                log.info(String.format("Summary of monthly batch of site "+siteId+" is following: End of Monthly Batch"));
            } else {

                log.info("It's not the time for monthly batch!");
            }
        }

        if(log.isInfoEnabled()) {
            log.info("-------------------- OK --------------------\n");
        }

        if(keyProductIdMap != null) {
            keyProductIdMap.clear();
            keyProductIdMap = null;
        }

        if(fromProductIdMap != null) {
            fromProductIdMap.clear();
            fromProductIdMap = null;
        }

        if(productIdMapListData != null) {
            productIdMapListData.clear();
            productIdMapListData = null;
        }

    }
}
