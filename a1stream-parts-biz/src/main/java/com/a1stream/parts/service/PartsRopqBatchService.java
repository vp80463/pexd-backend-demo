/******************************************************************************/
/* SYSTEM     : Stream                                                        */
/*                                                                            */
/* SUBSYSTEM  : Xm03                                                          */
/******************************************************************************/
package com.a1stream.parts.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.jdbc.BatchFailedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.PartsSafetyFactory;
import com.a1stream.common.constants.MstCodeConstants.SystemParameterType;
import com.a1stream.common.constants.PJConstants.RopRoqParameter;
import com.a1stream.common.exception.PJCustomException;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.common.utils.TimerUtil;
import com.a1stream.domain.batchdao.BatchSelectJdbcDao;
import com.a1stream.domain.batchdao.BatchUpdateJdbcDao;
import com.a1stream.domain.bo.batch.CuAbcDefinitionInfoBatchBO;
import com.a1stream.domain.bo.batch.CuProductInfoBatchBO;
import com.a1stream.domain.bo.batch.DemandForecastBatchBO;
import com.a1stream.domain.bo.batch.PartsBatchBO;
import com.a1stream.domain.bo.batch.ProductAbcBatchBO;
import com.a1stream.domain.bo.batch.ProductOrderResultSummaryBatchBO;
import com.a1stream.domain.bo.batch.RrDemandForecastMonthItemBatchBO;
import com.a1stream.domain.bo.batch.SiSeasonIndexInfoModelBO;
import com.a1stream.domain.bo.batch.SsmProductAbcBatchBO;
import com.a1stream.domain.entity.MstCodeInfo;
import com.a1stream.domain.repository.MstCodeInfoRepository;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.IdUtils;
import com.ymsl.solid.base.util.StringUtils;
import com.ymsl.solid.base.util.uuid.SnowflakeIdWorker;

import jakarta.annotation.Resource;

@Service
public class PartsRopqBatchService {

    @Resource
    private BatchUpdateJdbcDao                  updateJdbcDao;
    @Resource
    private BatchSelectJdbcDao                  selectJdbcDao;
    @Resource
    private MstCodeInfoRepository               mstCodeInfoRepository;

    /** Initialization */
	/** Log */
    private Log log = LogFactory.getLog(this.getClass());
    private final static String IDX_COLUMN_NAME = "nindex";

    BigDecimal[] demand = new BigDecimal[12];

    public Map<String, DemandForecastBatchBO> doJ1J2Collection(String siteId
                                                            ,String processDate
                                                            ,List<Long> facilityIdList
                                                            ,Map<Long, Long> fromProductIdMap
                                                            ,List<PartsBatchBO> productIdList) {

        log.info("********Start J1J2 Collection Process********");
        StopWatch timer = new StopWatch();
        TimerUtil.startTask(timer, "Prepare data");

        String thisMonth    = processDate.substring(4, 6);
        String thisYear     = processDate.substring(0, 4);

        String j1Year       = DateUtils.date2String(DateUtils.addMonth(DateUtils.string2Date(processDate, DateUtils.FORMAT_YMD_NODELIMITER),-11)
                                                   ,DateUtils.FORMAT_YMD_NODELIMITER).substring(0, 4);
        String j2Year       = DateUtils.date2String(DateUtils.addMonth(DateUtils.string2Date(processDate, DateUtils.FORMAT_YMD_NODELIMITER),-23)
                                                   ,DateUtils.FORMAT_YMD_NODELIMITER).substring(0, 4);

        Map<String, String> productFeatureMap = selectJdbcDao.getPartsRopqParameter(siteId);

        // Prepare ProductOrderResultSummary data: thisYear;j1Year;j2Year
        Map<String, ProductOrderResultSummaryBatchBO> thisYearMap = selectJdbcDao.getProductOrderResultSummaryByTargetYear(siteId
                                                                                                                         , null
                                                                                                                         , thisYear);
        Map<String, ProductOrderResultSummaryBatchBO> j1YearMap = null;

        Map<String, ProductOrderResultSummaryBatchBO> j2YearMap = selectJdbcDao.getProductOrderResultSummaryByTargetYear(siteId
                                                                                                                       , null
                                                                                                                       , j2Year);

        Map<String, BigDecimal> j1TotalMap = new HashMap<String, BigDecimal>();
        Map<String, BigDecimal> j2TotalMap = new HashMap<String, BigDecimal>();

        Map<String, Object[]> resultMap    = new HashMap<String, Object[]>();
        //Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
        Object[] maps ;
        String productId = null;
        String registerDate = null;
        String firstOrderDate = null;
        String key = null;
        int prdSize = productIdList.size(), facilitiesSize = facilityIdList.size();
        if(log.isInfoEnabled()) {
            log.info("Collect Year " + thisYear );
            log.info("Facility size:" + facilitiesSize + "," +  "Product size:" + prdSize);
            log.info(thisYearMap.size() + " ProductOrderResultSummary records found to be collected");
        }
        TimerUtil.endTask(timer, log);

        TimerUtil.startTask(timer, "Collect J1J2 on each products[%d] in every facility[%d]", prdSize, facilitiesSize);
        boolean isJustThisYear = StringUtils.equals(j1Year, thisYear);
        // Do j1Year;j2Year month quantity calculate
        int fc = 0, prdc = 1, logBatch = 5000, preIdx = 1;
        for (Long facilityId : facilityIdList) {

            ++ fc;
            for (PartsBatchBO partsBatchBO : productIdList) {

                ProductOrderResultSummaryBatchBO proResultSummary = new ProductOrderResultSummaryBatchBO();
                maps = new Object[16];
                productId = partsBatchBO.getProductid().toString();
                registerDate = partsBatchBO.getRegistrationdate();
                key = productId + CommonConstants.CHAR_COLON + facilityId;
                firstOrderDate = productFeatureMap.get(key);

                if (isJustThisYear) {

                    maps = calMonthQty(1
                              , 12
                              , thisYearMap
                              , j2YearMap
                              , j1TotalMap
                              , j2TotalMap
                              , proResultSummary
                              , key
                              , registerDate
                              , firstOrderDate);
                } else {

                    if(j1YearMap == null) {
                        j1YearMap = selectJdbcDao.getProductOrderResultSummaryByTargetYear(siteId, null,j1Year);
                    }

                    maps = calMonthQty(1
                              , NumberUtil.toInteger(thisMonth)
                              , thisYearMap
                              , j1YearMap
                              , j1TotalMap
                              , j2TotalMap
                              , proResultSummary
                              , key
                              , registerDate
                              , firstOrderDate);

                    maps = calMonthQty(NumberUtil.toInteger(thisMonth)+1
                              , 12
                              , j1YearMap
                              , j2YearMap
                              , (Map<String, BigDecimal>)maps[0]
                              , (Map<String, BigDecimal>)maps[1]
                              , proResultSummary
                              , key
                              , registerDate
                              , firstOrderDate);
                }

                if(prdc %logBatch == 0 || prdc==prdSize) {
                    log.info(String.format("Finish  product from %d to %d", preIdx, prdc++));
                    preIdx = prdc + 1;
                }
                resultMap.put(key, maps);
            }
        }

        if (productFeatureMap != null) {
            productFeatureMap.clear();
            productFeatureMap = null;
        }

        thisYearMap = null;
        j1TotalMap = null;
        j1YearMap = null;
        j2TotalMap = null;
        j2YearMap = null;

        TimerUtil.endTask(timer, log);

        TimerUtil.startTask(timer, "Update DB with new 'J1J2' facility feature, size[%s]", resultMap.size());
        deleteAndInsertProductFacilityFeature(siteId, resultMap);
        TimerUtil.endTask(timer, log);

        TimerUtil.startTask(timer, "Calc demand forcast, size[%s]", resultMap.size());
        Map<String, DemandForecastBatchBO> faprDemandFcTmpMap = calcDemanForcasts(siteId
                                                                                 ,fromProductIdMap
                                                                                 , resultMap);
        TimerUtil.endTask(timer, log);

        TimerUtil.startTask(timer, "Release resources.");

        resultMap = null;
        TimerUtil.endTask(timer, log);

        if(log.isInfoEnabled()) {
            log.info("********End of J1J2 collection process, during time: " + timer.getTotalTimeMillis() + "ms********");
        }
        timer = null;

        return faprDemandFcTmpMap;
    }

    public void doCostUsageCalculate(String siteId
                                    , String processDate
                                    , List<Long> facilityIdList
                                    , Map<Long, Long> fromProductIdMap
                                    , Map<String, DemandForecastBatchBO> demfortempMap
                                    , List<PartsBatchBO> productIdFeatureListData) {

        log.info("********Start Cost Usage Process********");
        StopWatch timer = new StopWatch();
        timer.start();

        // Initial Map and List
        Map<String, CuAbcDefinitionInfoBatchBO> abcDefinitionMap = new HashMap<String, CuAbcDefinitionInfoBatchBO>();

        // come from overall situation para
        //        List<String> facilityIdList = new ArrayList<String>();
        CuAbcDefinitionInfoBatchBO abcDefinitionListDifType = null;
        CuAbcDefinitionInfoBatchBO abcDefinitionListAType = null;
        CuAbcDefinitionInfoBatchBO abcDefinitionListBType = null;
        CuAbcDefinitionInfoBatchBO abcDefinitionListCType = null;

        String collectDate  = DateUtils.date2String(DateUtils.addMonth(DateUtils.string2Date(processDate, DateUtils.FORMAT_YMD_NODELIMITER),-18)
                                                    ,DateUtils.FORMAT_YMD_NODELIMITER).substring(0, 6);

        // Set Map and List
        List<CuAbcDefinitionInfoBatchBO> abcDefinitionList = selectJdbcDao.getAbcDefinition(CommonConstants.CHAR_DEFAULT_SITE_ID);
        // facilityIdList = costUsageBatchDao.getFacilityList(conn, siteId, processDate);
        Map<Long,List<CuProductInfoBatchBO>> orgModelMapList = this.getProductsGroupbyFacility(facilityIdList,CommonConstants.CHAR_DEFAULT_SITE_ID);
        // get product cost
        Map<Long,String> productCost = selectJdbcDao.getProductCost(siteId);

        // product feature type is "PARTSALESSTATUS" stringvalue
        Map<Long, String> productFeatureMap = new HashMap<Long, String>();
        for (PartsBatchBO partSalesstatus : productIdFeatureListData) {

            productFeatureMap.put(partSalesstatus.getProductid(), partSalesstatus.getSalesstatustype());
        }

        for (CuAbcDefinitionInfoBatchBO abcDefinitionInfoModel : abcDefinitionList) {

            abcDefinitionMap.put(abcDefinitionInfoModel.getAbctype()
                    + abcDefinitionInfoModel.getProductcategoryid(), abcDefinitionInfoModel);

        }

        Map<String,String[]> resultMap = new HashMap<String,String[]>();

        log.info("Cost Usage " + orgModelMapList.size() + " OrgModelMapList to be processed");
        StopWatch taskTimer = new StopWatch();
        for (Long facilityId : orgModelMapList.keySet()) {

            TimerUtil.startTask(taskTimer, "Collection J1J2Total in facility [%s]", facilityId);
            Map<Long,List<CuProductInfoBatchBO>> orgModelCategoryMap = new HashMap<Long, List<CuProductInfoBatchBO>>();

            for (CuProductInfoBatchBO organizationFacilityInfoModel : orgModelMapList.get(facilityId)) {

                String key = organizationFacilityInfoModel.getFacilityid()+CommonConstants.CHAR_COLON+organizationFacilityInfoModel.getProductid();
                DemandForecastBatchBO dfctm = demfortempMap.get(key) ;
                organizationFacilityInfoModel.setDoublevalue1(dfctm == null ? BigDecimal.ZERO :NumberUtil.toBigDecimal(dfctm.getJ1total()));
                organizationFacilityInfoModel.setDoublevalue2(dfctm == null ? BigDecimal.ZERO :NumberUtil.toBigDecimal(dfctm.getJ2total()));
                organizationFacilityInfoModel.setIntroductiondate(dfctm == null ? null :dfctm.getRegisterdate());

                if (orgModelCategoryMap.containsKey(organizationFacilityInfoModel.getCategoryid())) {
                    orgModelCategoryMap.get(organizationFacilityInfoModel.getCategoryid()).add(organizationFacilityInfoModel);
                } else {
                    List<CuProductInfoBatchBO> productInfoModel = new ArrayList<CuProductInfoBatchBO>();
                    productInfoModel.add(organizationFacilityInfoModel);
                    orgModelCategoryMap.put(organizationFacilityInfoModel.getCategoryid(), productInfoModel);
                }
            }

            TimerUtil.endTask(taskTimer, log);
            if(log.isInfoEnabled()) {
                log.info("The product category group size is "+ orgModelCategoryMap.size());
            }

            // GET ALL TYPE J1TOTAL SUM
            for (Entry<Long,List<CuProductInfoBatchBO>> item : orgModelCategoryMap.entrySet()) {

                //for (String key : orgModelCategoryMap.keySet()) {

                BigDecimal sumJ1Total = BigDecimal.ZERO;
                BigDecimal j1Total = BigDecimal.ZERO;
                String productcategoryId = null;
                for (CuProductInfoBatchBO organizationFacilityInfo : item.getValue()) {

                    // add all new parts jitotal
                    //if (organizationFacilityInfo.getFromproductid() == null) {
                    if (fromProductIdMap.get(organizationFacilityInfo.getProductid()) == null) {

                        sumJ1Total = sumJ1Total.add(organizationFacilityInfo.getDoublevalue1());
                    }
                }

                //Sort by J1Total desc.
                List<CuProductInfoBatchBO> orderlyList = sortByJ1TotalDesc(item);

                // GET DIFFERENT TYPE ABCID AND PRODUCTCATEGORYID
                String abcId =null, introductionDate=null ;
                Long prdId = (long)0;
                String abcType = null ;
                for (CuProductInfoBatchBO organizationFacilityInfoModel : orderlyList) {

                    String[] resultObject = new String[3];

                    abcId = null;
                    prdId = organizationFacilityInfoModel.getProductid();

                    //String Stringvalue1Date = organizationFacilityInfoModel.getStringvalue1() != null ?organizationFacilityInfoModel.getStringvalue1().substring(0, 6) : Xm03BatchConstants.STRING_ZERO;
                    introductionDate = organizationFacilityInfoModel.getIntroductiondate() != null ?organizationFacilityInfoModel.getIntroductiondate().substring(0, 6) : CommonConstants.CHAR_ZERO;
                    // PUT DIFFERENT TYPE DATA TO DATEBASE
                    if (fromProductIdMap.containsKey(prdId)){

                        abcId = abcDefinitionMap.get(CommonConstants.CHAR_YY + organizationFacilityInfoModel.getCategoryid()).getAbcid();
                        productcategoryId = abcDefinitionMap.get(CommonConstants.CHAR_YY + organizationFacilityInfoModel.getCategoryid()).getProductcategoryid();
                        abcType = CommonConstants.CHAR_YY;
                        resultObject[0] = abcId;
                        resultObject[1] = productcategoryId;
                        resultObject[2] = abcType;
                        resultMap.put(organizationFacilityInfoModel.getFacilityid() + CommonConstants.CHAR_COLON
                                        + prdId, resultObject);
                        continue;
                    }

                    j1Total = j1Total.add(organizationFacilityInfoModel.getDoublevalue1());

                    if(StringUtils.equals(productFeatureMap.get(prdId), "2")) {

                        abcId = abcDefinitionMap.get(CommonConstants.CHAR_XX + organizationFacilityInfoModel.getCategoryid()).getAbcid();
                        productcategoryId = abcDefinitionMap.get(CommonConstants.CHAR_XX + organizationFacilityInfoModel.getCategoryid()).getProductcategoryid();
                        abcType = CommonConstants.CHAR_XX;
                    } else if (StringUtils.compare(collectDate, introductionDate, true) < 0 ) {

                        abcDefinitionListDifType = getDiffTypeModel(abcDefinitionList
                                                                ,CommonConstants.CHAR_N
                                                                ,productCost.get(prdId)
                                                                ,organizationFacilityInfoModel.getCategoryid());

                        if (abcDefinitionListDifType.getAbcid() != null) {
                            abcId = abcDefinitionListDifType.getAbcid();
                            productcategoryId = abcDefinitionListDifType.getProductcategoryid();
                            abcType = abcDefinitionListDifType.getAbctype();
                        }
                    } else if (yearNoSale(organizationFacilityInfoModel.getDoublevalue1()
                                     , organizationFacilityInfoModel.getDoublevalue2())) {

                        abcDefinitionListDifType = getDiffTypeModel(abcDefinitionList
                                                                ,CommonConstants.CHAR_E
                                                                ,productCost.get(prdId)
                                                                ,organizationFacilityInfoModel.getCategoryid());

                        if (abcDefinitionListDifType.getAbcid() != null) {
                            abcId = abcDefinitionListDifType.getAbcid();
                            productcategoryId = abcDefinitionListDifType.getProductcategoryid();
                            abcType = abcDefinitionListDifType.getAbctype();
                        }
                    } else if (yearNoSale(organizationFacilityInfoModel.getDoublevalue1(), BigDecimal.ZERO)) {

                        abcDefinitionListDifType = getDiffTypeModel(abcDefinitionList
                                                                ,CommonConstants.CHAR_D
                                                                ,productCost.get(prdId)
                                                                ,organizationFacilityInfoModel.getCategoryid());

                        if (abcDefinitionListDifType.getAbcid() != null) {
                            abcId = abcDefinitionListDifType.getAbcid();
                            productcategoryId = abcDefinitionListDifType.getProductcategoryid();
                            abcType = abcDefinitionListDifType.getAbctype();
                        }
                    } else  {

                         BigDecimal percentage = productABC(j1Total, sumJ1Total).multiply(CommonConstants.BIGDECIMAL_HUNDRED_ROUND2);
                         abcDefinitionListAType = getDiffTypeABCModel( abcDefinitionList
                                                                      , CommonConstants.CHAR_A
                                                                      , productCost.get(prdId)
                                                                      , percentage
                                                                      , organizationFacilityInfoModel.getCategoryid());
                         abcDefinitionListBType = getDiffTypeABCModel( abcDefinitionList
                                                                       , CommonConstants.CHAR_B
                                                                       , productCost.get(prdId)
                                                                       , percentage
                                                                       , organizationFacilityInfoModel.getCategoryid());
                         abcDefinitionListCType = getDiffTypeABCModel( abcDefinitionList
                                                                       , CommonConstants.CHAR_C
                                                                       , productCost.get(prdId)
                                                                       , percentage
                                                                       , organizationFacilityInfoModel.getCategoryid());

                         if (abcDefinitionListAType.getAbcid() != null) {

                             abcId = abcDefinitionListAType.getAbcid();
                             productcategoryId = abcDefinitionListAType.getProductcategoryid();
                             abcType = abcDefinitionListAType.getAbctype();
                         } else if (abcDefinitionListBType.getAbcid() != null) {

                             abcId = abcDefinitionListBType.getAbcid();
                             productcategoryId = abcDefinitionListBType.getProductcategoryid();
                             abcType = abcDefinitionListBType.getAbctype();
                         } else if (abcDefinitionListCType.getAbcid() != null) {

                             abcId = abcDefinitionListCType.getAbcid();
                             productcategoryId = abcDefinitionListCType.getProductcategoryid();
                             abcType = abcDefinitionListCType.getAbctype();
                         }
                    }

                    resultObject[0] = abcId;
                    resultObject[1] = productcategoryId;
                    resultObject[2] = abcType;
                    resultMap.put(organizationFacilityInfoModel.getFacilityid() + CommonConstants.CHAR_COLON
                                                                                + prdId, resultObject);
                }
            }
        }

        //Insert rows into product_abc_info
        TimerUtil.startTask(taskTimer, "Insert rows into product_abc_info with size:%d", resultMap.size());
        insertProductAbc(siteId, resultMap);
        TimerUtil.endTask(taskTimer, log);

        //Release resources in used.
        if(resultMap !=null) {
            resultMap.clear();
            resultMap = null;
        }

        if(productFeatureMap !=null) {
            productFeatureMap.clear();
            productFeatureMap = null;
        }
        if(orgModelMapList !=null) {
            orgModelMapList.clear();
            orgModelMapList = null;
        }
        if(abcDefinitionMap !=null) {
            abcDefinitionMap.clear();
            abcDefinitionMap = null;
        }
        if(abcDefinitionList !=null) {
            abcDefinitionList.clear();
            abcDefinitionList = null;
        }
        if(productCost !=null) {
            productCost.clear();
            productCost = null;
        }

        abcDefinitionListDifType = null;
        abcDefinitionListAType = null;
        abcDefinitionListBType = null;
        abcDefinitionListCType = null;

        timer.stop();
        if(log.isInfoEnabled()) {
            log.info("********End of Cost Usage Process, during time: " + timer.getTotalTimeMillis() + "ms********");
        }
    }

    public void doAverageDemandCalculate(String siteId
                                       , String processDate
                                       , List<ProductAbcBatchBO> productFacilityFeatureList
                                       , Map<Long,String> keyProductIdMap) {

        log.info("********Start Average Demand Calculate Process********");
        StopWatch timer = new StopWatch();
        timer.start();

        // initial Map and List
        String collectDate  = processDate.substring(0, 6);
        String collectMonth = processDate.substring(4, 6);
        String collectYear  = processDate.substring(0, 4);
        String firstOrderDate = "";

        int salesMonth = 0;

        Map<String,Object[]> resultMap = new HashMap<String,Object[]>();
        log.info(productFacilityFeatureList.size() + " ProductFeature records to be calculated");

        for (ProductAbcBatchBO adProductFeatureModel : productFacilityFeatureList) {

            BigDecimal percentage = BigDecimal.ZERO;
            Object[] resultObject = new Object[2];
            firstOrderDate = adProductFeatureModel.getFirstorderdate();
            if (StringUtils.isBlankText(firstOrderDate)) {
                if (StringUtils.equals(adProductFeatureModel.getAbctype().substring(0, 1), CommonConstants.CHAR_N)) {
                    if (StringUtils.compare(DateUtils.date2String(DateUtils.addMonth(DateUtils.string2Date(processDate, DateUtils.FORMAT_YMD_NODELIMITER),
                                                                    -CommonConstants.INTEGER_TWELVE +CommonConstants.INTEGER_ONE)
                                                    ,DateUtils.FORMAT_YMD_NODELIMITER).substring(0, 6)
                                          , keyProductIdMap.get(adProductFeatureModel.getProductid()).substring(0, 6), true) >= 0) {
                        salesMonth = CommonConstants.INTEGER_TWELVE;
                    } else {
                        salesMonth = compareMonth(collectYear,collectMonth,adProductFeatureModel.getRegisterdate().substring(0, 4)
                                              ,keyProductIdMap.get(adProductFeatureModel.getProductid()).substring(4, 6));
                    }
                } else {

                    salesMonth = 12;
                }
            } else {
                if (StringUtils.equals(adProductFeatureModel.getAbctype().substring(0, 1), CommonConstants.CHAR_N)) {

                    if (StringUtils.compare(DateUtils.date2String(DateUtils.addMonth(DateUtils.string2Date(processDate, DateUtils.FORMAT_YMD_NODELIMITER),
                                                                                            -CommonConstants.INTEGER_TWELVE +CommonConstants.INTEGER_ONE)
                                            ,DateUtils.FORMAT_YMD_NODELIMITER).substring(0, 6)
                            , firstOrderDate.substring(0, 6), true) >= 0) {
                        salesMonth = CommonConstants.INTEGER_TWELVE;
                    } else {
                        salesMonth = compareMonth(collectYear,collectMonth,firstOrderDate.substring(0, 4),firstOrderDate.substring(4, 6));
                    }
                } else {
                    salesMonth = 12;
                }
            }

            if (salesMonth == 0) {
                salesMonth = 1;
            }

            if (nullObjectFlag(adProductFeatureModel.getJ1total())) {
                percentage = CommonConstants.BIGDECIMAL_ZERO_ROUND2;
            } else {
                percentage = new BigDecimal(adProductFeatureModel.getJ1total()).divide(NumberUtil.toBigDecimal(salesMonth),4,RoundingMode.HALF_EVEN);
            }

            resultObject[0] = StringUtils.toString(percentage);
            resultMap.put(adProductFeatureModel.getFacilityid() + CommonConstants.CHAR_COLON
                                                                + adProductFeatureModel.getProductid(), resultObject);
        }

        insertPartsRopqMonthlyFacPro(siteId, resultMap,RopRoqParameter.KEY_PARTSAVERAGEDEMAND);

        // clear
        resultMap = null;

        timer.stop();
        if(log.isInfoEnabled()) {
            log.info("********End of Average Demand Calculate, during time: " + timer.getTotalTimeMillis() + "ms********");
        }
    }

    public void doDeviationMonthCalculate(String siteId
                                        , String processDate
                                        , Map<String, ProductAbcBatchBO> productAbcMap
                                        , Map<String, DemandForecastBatchBO> demandForecastTempMap) {

        log.info("********Start Deviation Month Calculate Process********");
        StopWatch timer = new StopWatch();
        timer.start();

        NumberFormat doubleFormat = new DecimalFormat("0.00");
        Double deviationDoubleValue = Double.valueOf(0);

        // Get all A,B,C,N product by siteId
        //Map<String, ProductAbcModel> productAbcMap = deviationMonthCalculateDao.getABCNProductMap(conn, siteId);

        List<Object[]> batchArgs = new ArrayList<Object[]>();

        log.info(productAbcMap.size() + " ProcudtAbc records to be calculated");

        Map<String, String> averageMonthDemandMap = selectJdbcDao.getPartsRopqMonthlyMapString(siteId,null,RopRoqParameter.KEY_PARTSAVERAGEDEMAND);

        //common para
        //Map<String, DemandForecastTmpModel> demandForecastTempMap = batchCommonDao.selectDemandForecastTempData(conn, siteId);
        String[] proFac = new String[2];

        String productId = "";
        String facilityId = "";
        List<String> typeList = this.getTypeList();
        SnowflakeIdWorker snowflakeIdWorker = IdUtils.getSnowflakeIdWorker();
        for (String key : productAbcMap.keySet()) {

            ProductAbcBatchBO productAbcModel = productAbcMap.get(key);
            if(StringUtils.isNotBlankText(productAbcModel.getRopmonth()) || typeList.contains(productAbcModel.getAbctype())) {
                continue;
            }

            BigDecimal STDDeviation = BigDecimal.ZERO;

            BigDecimal[] demand = new BigDecimal[12];

            proFac = key.split(CommonConstants.CHAR_COLON);

            productId = proFac[1];
            facilityId = proFac[0];

            DemandForecastBatchBO demandForecastTmpModel = demandForecastTempMap.get(key);

            if (demandForecastTmpModel != null) {

                for (int i = 1; i <= 12; i++) {

                    String month = StringUtils.toString(i);

                    if (month.length() == 1) {

                        month = CommonConstants.CHAR_ZERO + month;
                    }

                    try {
                        demand[i-1] = NumberUtil.toBigDecimal(BeanUtils.getProperty(demandForecastTmpModel, "month" + month));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            } else {

                for (int i = 12; i > 0; i--) {

                    demand[12 - i] = BigDecimal.ZERO;
                }
            }

            BigDecimal demandTotal = BigDecimal.ZERO;
            BigDecimal averageDemand = BigDecimal.ZERO;

            for (int i = 0; i < 12; i++) {

                demand[i] = demand[i] == null ? BigDecimal.ZERO : demand[i];
                demandTotal = demandTotal.add(demand[i]);
            }

            if (demandTotal == BigDecimal.ZERO) {

                STDDeviation = BigDecimal.ZERO;
            } else {

            // Get sales month
            Integer salesMonth = 0;
            if (StringUtils.equals(productAbcMap.get(key).getAbctype().substring(0, 1), CommonConstants.CHAR_N)) {
                String firstOrderDate = demandForecastTmpModel.getFirstorderdate();

                Integer monthBetween;

                if (StringUtils.isBlankText(firstOrderDate)) {

                    monthBetween = 12;
                    salesMonth = 12;
                } else {

                    monthBetween = calculateMonthBetween(firstOrderDate, processDate);
                }

                if (firstOrderDate != null) {

                    if (monthBetween >= 12) {

                        salesMonth = 12;
                    } else {
                        salesMonth = monthBetween;
                    }
                }
            } else{
                salesMonth = 12;
            }
            averageDemand = NumberUtil.toBigDecimal(nullToCharZero(averageMonthDemandMap.get(facilityId+CommonConstants.CHAR_COLON+productId)));

            BigDecimal deviation[] = new BigDecimal[12];
            BigDecimal deviationTotal = BigDecimal.ZERO;

            for (int i = 0; i < salesMonth; i++) {

                deviation[i] = averageDemand.subtract(demand[i]).abs();
                deviationTotal = deviationTotal.add(deviation[i].pow(2));
            }

            if (!NumberUtil.equals(deviationTotal,CommonConstants.BIGDECIMAL_ZERO)) {

                STDDeviation = deviationTotal.divide(NumberUtil.toBigDecimal(salesMonth)
                                                       , 5
                                                       , RoundingMode.HALF_UP);
                }
            }

            deviationDoubleValue = Math.sqrt(STDDeviation.doubleValue());

            String stringValue = "0";

            if (NumberUtil.equals(averageDemand, CommonConstants.BIGDECIMAL_ZERO)) {
                stringValue = "0";
            } else {
                BigDecimal sTDDeviationDec = NumberUtil.toBigDecimal(nullToCharZero(doubleFormat.format(deviationDoubleValue)))
                                                        .divide(averageDemand,2,RoundingMode.HALF_EVEN);
                stringValue = StringUtils.toString(sTDDeviationDec);
            }

            batchArgs.add(new Object[]{snowflakeIdWorker.nextId()
                                     , siteId
                                     , toLong(facilityId)
                                     , toLong(productId)
                                     , RopRoqParameter.KEY_PARTSDEVIATIONMONTH
                                     , stringValue
                                     , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                                     , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                                     , CommonConstants.ROPQBATCH_UPDATE_PROGRAM});
        }

        if (batchArgs.size() != 0) {

            updateJdbcDao.insertPartsRopqMonthlyData(batchArgs);
        }
        if(batchArgs !=null) {
            batchArgs.clear();
            batchArgs = null;
        }

        if(averageMonthDemandMap !=null) {
            averageMonthDemandMap.clear();
            averageMonthDemandMap = null;
        }

        timer.stop();
        if(log.isInfoEnabled()) {
            log.info("********End of Deviation Month Calculate Process, during time: " + timer.getTotalTimeMillis() + "ms********");
        }
    }

    public void doSafetyStockMonthCalculate(String siteId
                                        , String processDate
                                        , List<ProductAbcBatchBO> ssmProductAbcModelList) {

        log.info("********Start Safety Stock Month Calculate Process********");
        StopWatch timer = new StopWatch();
        timer.start();

        //
        Map<String,String> mstCodeC086Map = this.getCodeMstS054();

        // initial Map and List
        Map<String,SsmProductAbcBatchBO> reorderGuidelineMap = new HashMap<String, SsmProductAbcBatchBO>();
        Map<String,SsmProductAbcBatchBO> reorderGuidelineMapFlagOne = new HashMap<String, SsmProductAbcBatchBO>();
        Map<String,SsmProductAbcBatchBO> productFacilityFeatureMap = new HashMap<String, SsmProductAbcBatchBO>();
        String partsLeadtime = "0";
        String sTDDeviation = "0";
        SsmProductAbcBatchBO reorderGuidelineModle = new SsmProductAbcBatchBO();
        SsmProductAbcBatchBO productFacilityFeatureModel = new SsmProductAbcBatchBO();

        Map<String, Object[]> resultMap    = new HashMap<String, Object[]>();
        Map<String, Object[]> ropmonthResultMap    = new HashMap<String, Object[]>();
        Map<String, Object[]> realStockMontMap = new HashMap<String, Object[]>();

        // GET PRODUCTTPE WITH A,B,C,N DATA
        //common para
        //SsmProductAbcBatchBOList = safetyStockMonthCalculateBatchDao.getProductAbcInfo(conn, siteId);
        reorderGuidelineMap        = selectJdbcDao.getReorderGuideline(siteId,null);
        reorderGuidelineMapFlagOne = selectJdbcDao.getReorderGuidelineFlagIsOne(siteId,null);
        productFacilityFeatureMap  = selectJdbcDao.getPartsRopqMonthlyMap(siteId,null,RopRoqParameter.KEY_PARTSDEVIATIONMONTH);

        log.info(ssmProductAbcModelList.size() + " ProductAbc records to be calculated");
        BigDecimal safetyStockMonthData = new BigDecimal(0.00);
        List<String> typeList = this.getTypeList();
        for (ProductAbcBatchBO ssmProductAbcModel : ssmProductAbcModelList) {

            if(StringUtils.isNotBlankText(ssmProductAbcModel.getRopmonth()) || typeList.contains(ssmProductAbcModel.getAbctype())) {
                continue;
            }
            if (!reorderGuidelineMapFlagOne.containsKey(ssmProductAbcModel.getFacilityid().toString() + ssmProductAbcModel.getProductid().toString())){

                Object[] maps = new Object[2];
                Object[] ropmontMaps = new Object[2];
                Object[] realStockMontMaps = new Object[2];

                BigDecimal realSafetyStockMonthData = new BigDecimal(0.00);
                reorderGuidelineModle = reorderGuidelineMap.get(ssmProductAbcModel.getFacilityid().toString() + ssmProductAbcModel.getProductid().toString());
                productFacilityFeatureModel = productFacilityFeatureMap.get(ssmProductAbcModel.getFacilityid().toString() + ssmProductAbcModel.getProductid().toString());

                if (reorderGuidelineModle != null) {
                    partsLeadtime = reorderGuidelineModle.getPartsleadtime();
                }

                if (productFacilityFeatureModel != null) {
                    sTDDeviation = productFacilityFeatureModel.getStringvalue();
                }

                safetyStockMonthData = safetyStockMonthCount(partsLeadtime
                                               ,ssmProductAbcModel.getAddleadtime()
                                               ,sTDDeviation
                                               ,mstCodeC086Map.get(ssmProductAbcModel.getTargetsupplyrate())
                                               ,ssmProductAbcModel.getSsmlower()
                                               ,ssmProductAbcModel.getSsmupper());


                if (NumberUtil.larger(safetyStockMonthData, NumberUtil.toBigDecimal(nullObject(ssmProductAbcModel.getSsmupper())))) {
                    realSafetyStockMonthData = NumberUtil.toBigDecimal(nullObject(ssmProductAbcModel.getSsmupper()));
                } else if (NumberUtil.lt(safetyStockMonthData, NumberUtil.toBigDecimal(nullObject(ssmProductAbcModel.getSsmlower())))) {
                    realSafetyStockMonthData = NumberUtil.toBigDecimal(nullObject(ssmProductAbcModel.getSsmlower()));
                } else {
                    realSafetyStockMonthData = safetyStockMonthData;
                }

                // set different map
                maps[0] = StringUtils.toString(realSafetyStockMonthData);

                ropmontMaps[0] = (NumberUtil.toBigDecimal(nullObject(partsLeadtime))
                                .add(NumberUtil.toBigDecimal(nullObject(ssmProductAbcModel.getAddleadtime()))))
                                                .divide(CommonConstants.BIG_DECIMAL_THIRTY,2,RoundingMode.HALF_EVEN)
                                .add(realSafetyStockMonthData);

                realStockMontMaps[0] = StringUtils.toString(safetyStockMonthData);

                // updata to productfacilityfeature
                resultMap.put(ssmProductAbcModel.getFacilityid() + CommonConstants.CHAR_COLON
                                + ssmProductAbcModel.getProductid(), maps);
                ropmonthResultMap.put(ssmProductAbcModel.getFacilityid() + CommonConstants.CHAR_COLON
                                      + ssmProductAbcModel.getProductid(), ropmontMaps);
                realStockMontMap.put(ssmProductAbcModel.getFacilityid() + CommonConstants.CHAR_COLON
                                      + ssmProductAbcModel.getProductid(), realStockMontMaps);
            }
        }

        //Release resources.
        if(reorderGuidelineMap != null) {
            reorderGuidelineMap.clear();
            reorderGuidelineMap = null;
        }
        if(reorderGuidelineMapFlagOne != null) {
            reorderGuidelineMapFlagOne.clear();
            reorderGuidelineMapFlagOne = null;
        }
        if(productFacilityFeatureMap != null) {
            productFacilityFeatureMap.clear();
            productFacilityFeatureMap = null;
        }

        //Process inserting features.
        insertPartsRopqMonthlyFacPro(siteId, resultMap,RopRoqParameter.KEY_SPARTSSAFETYSTOCKMONTH);
        //Release resources.
        if(resultMap != null) {
            resultMap.clear();
            resultMap = null;
        }

        //Process inserting features.
        insertPartsRopqMonthlyByRopMonth(siteId, ropmonthResultMap);
        //Release resources.
        if(ropmonthResultMap != null) {
            ropmonthResultMap.clear();
            ropmonthResultMap = null;
        }

        //Process inserting features.
        //insertProductFacilityFeatureRealStockMont(siteId, realStockMontMap);
        //Release resources.
        if(realStockMontMap != null) {
            realStockMontMap.clear();
            realStockMontMap = null;
        }

        //Release resources.
        if(mstCodeC086Map != null) {
            mstCodeC086Map.clear();
            mstCodeC086Map = null;
        }

        timer.stop();
        if(log.isInfoEnabled()) {
            log.info("********End of Safety Stock Month Calculate, during time: " + timer.getTotalTimeMillis() + "ms********");
        }
    }

    public void doTrendIndexCalculate(String siteId
                                    , String processDate
                                    , List<ProductAbcBatchBO> trendIndexMap){

        log.info("********Start Trend Index Calculate Process********");


        StopWatch timer = new StopWatch();
        timer.start();

        // Get TrendIndexUpper and TrendIndexLower
        // Waiting for development, user temp data first;
        calculateTrendIndex(siteId, trendIndexMap);

        timer.stop();
        if(log.isInfoEnabled()) {
            log.info("********End of Trend Index Calculate, during time: " + timer.getTotalTimeMillis() + "ms********");
        }
    }

    public void doSeasonIndexCalculate(String siteId
                                     , String processDate
                                     , Map<String, DemandForecastBatchBO> demfortempMap) {

        log.info("********Start Season Index Calculate Process********");
        StopWatch timer = new StopWatch();
        timer.start();

        List<DemandForecastBatchBO> newDemandForecastTmpModel = new ArrayList<DemandForecastBatchBO>();
        BigDecimal monthTotal = BigDecimal.ZERO;
        List<String> facprocate = selectJdbcDao.getSeasonIndex(siteId,null);
        List<PartsBatchBO> productOrderResultSummaryMapList = selectJdbcDao.getProductABCCategory(siteId,null);
        Map<String, Long> siProductOrderResultSummaryMap = new HashMap<String, Long>();
        String facProdGroupKey = "";
        String facProdCateGroupKey = "";
        Long faciId = (long)0,prodId = (long)0;
        for (PartsBatchBO siProductOrderResultSummaryModel : productOrderResultSummaryMapList) {

            faciId= siProductOrderResultSummaryModel.getFacilityid();
            prodId= siProductOrderResultSummaryModel.getProductid();
            facProdGroupKey = faciId + CommonConstants.CHAR_COLON + prodId;
            siProductOrderResultSummaryMap.put(facProdGroupKey , siProductOrderResultSummaryModel.getProductcategoryid());
        }

        Map<String, List<DemandForecastBatchBO>> demandForecastTmpModelMap = new HashMap<String, List<DemandForecastBatchBO>>();
        for (DemandForecastBatchBO demandForecastTmpModel : demfortempMap.values()) {

            faciId = NumberUtil.toLong(demandForecastTmpModel.getFacilityid());
            prodId = NumberUtil.toLong(demandForecastTmpModel.getProductid());

            facProdGroupKey = faciId + CommonConstants.CHAR_COLON + prodId;
            if (!siProductOrderResultSummaryMap.containsKey(facProdGroupKey)) {
                continue;
            }
            //
            demandForecastTmpModel.setProductcategoryid(siProductOrderResultSummaryMap.get(facProdGroupKey).toString());
            newDemandForecastTmpModel.add(demandForecastTmpModel);

            //Group by FacilityId + ProductCategoryId
            facProdCateGroupKey = demandForecastTmpModel.getFacilityid() + CommonConstants.CHAR_COLON + demandForecastTmpModel.getProductcategoryid();
            if (demandForecastTmpModelMap.containsKey(facProdCateGroupKey)) {
                demandForecastTmpModelMap.get(facProdCateGroupKey).add(demandForecastTmpModel);

            } else {
                List<DemandForecastBatchBO> demandForecastTmpModelModellist = new ArrayList<DemandForecastBatchBO>();
                demandForecastTmpModelModellist.add(demandForecastTmpModel);
                demandForecastTmpModelMap.put(facProdCateGroupKey, demandForecastTmpModelModellist);
            }
        }

        if(siProductOrderResultSummaryMap !=null) {
            siProductOrderResultSummaryMap.clear();
            siProductOrderResultSummaryMap = null;
        }

        Map<String,Object[]> resultMap = new HashMap<String,Object[]>();

        log.info(newDemandForecastTmpModel.size() + " DemandForecastTmpModel records to be calculated");

        for (String  key : demandForecastTmpModelMap.keySet()) {

            if (!facprocate.contains(key)) {

                Object[] resultObject = new Object[12];

                BigDecimal month01SeasonIndex = BigDecimal.ZERO;
                BigDecimal month02SeasonIndex = BigDecimal.ZERO;
                BigDecimal month03SeasonIndex = BigDecimal.ZERO;
                BigDecimal month04SeasonIndex = BigDecimal.ZERO;
                BigDecimal month05SeasonIndex = BigDecimal.ZERO;
                BigDecimal month06SeasonIndex = BigDecimal.ZERO;
                BigDecimal month07SeasonIndex = BigDecimal.ZERO;
                BigDecimal month08SeasonIndex = BigDecimal.ZERO;
                BigDecimal month09SeasonIndex = BigDecimal.ZERO;
                BigDecimal month10SeasonIndex = BigDecimal.ZERO;
                BigDecimal month11SeasonIndex = BigDecimal.ZERO;
                BigDecimal month12SeasonIndex = BigDecimal.ZERO;

                for (int i=0;i<12;i++) {
                    demand[i] = BigDecimal.ZERO;
                }

                this.getDiffMonthData(demandForecastTmpModelMap.get(key));

                monthTotal = demand[0].add(demand[1]).add(demand[2]).add(demand[3]).add(demand[4]).add(demand[5]).add(demand[6])
                           .add(demand[7]).add(demand[8]).add(demand[9]).add(demand[10]).add(demand[11]);

                if (!NumberUtil.equals(monthTotal, CommonConstants.BIGDECIMAL_ZERO_ROUND2)) {

                    month01SeasonIndex = demand[11].divide(monthTotal,4,RoundingMode.HALF_EVEN).multiply(CommonConstants.BIGDECIMAL_TWELVE).setScale(2, RoundingMode.HALF_UP);
                    month02SeasonIndex = demand[10].divide(monthTotal,4,RoundingMode.HALF_EVEN).multiply(CommonConstants.BIGDECIMAL_TWELVE).setScale(2, RoundingMode.HALF_UP);
                    month03SeasonIndex = demand[9].divide(monthTotal,4,RoundingMode.HALF_EVEN).multiply(CommonConstants.BIGDECIMAL_TWELVE).setScale(2, RoundingMode.HALF_UP);
                    month04SeasonIndex = demand[8].divide(monthTotal,4,RoundingMode.HALF_EVEN).multiply(CommonConstants.BIGDECIMAL_TWELVE).setScale(2, RoundingMode.HALF_UP);
                    month05SeasonIndex = demand[7].divide(monthTotal,4,RoundingMode.HALF_EVEN).multiply(CommonConstants.BIGDECIMAL_TWELVE).setScale(2, RoundingMode.HALF_UP);
                    month06SeasonIndex = demand[6].divide(monthTotal,4,RoundingMode.HALF_EVEN).multiply(CommonConstants.BIGDECIMAL_TWELVE).setScale(2, RoundingMode.HALF_UP);
                    month07SeasonIndex = demand[5].divide(monthTotal,4,RoundingMode.HALF_EVEN).multiply(CommonConstants.BIGDECIMAL_TWELVE).setScale(2, RoundingMode.HALF_UP);
                    month08SeasonIndex = demand[4].divide(monthTotal,4,RoundingMode.HALF_EVEN).multiply(CommonConstants.BIGDECIMAL_TWELVE).setScale(2, RoundingMode.HALF_UP);
                    month09SeasonIndex = demand[3].divide(monthTotal,4,RoundingMode.HALF_EVEN).multiply(CommonConstants.BIGDECIMAL_TWELVE).setScale(2, RoundingMode.HALF_UP);
                    month10SeasonIndex = demand[2].divide(monthTotal,4,RoundingMode.HALF_EVEN).multiply(CommonConstants.BIGDECIMAL_TWELVE).setScale(2, RoundingMode.HALF_UP);
                    month11SeasonIndex = demand[1].divide(monthTotal,4,RoundingMode.HALF_EVEN).multiply(CommonConstants.BIGDECIMAL_TWELVE).setScale(2, RoundingMode.HALF_UP);
                    month12SeasonIndex = demand[0].divide(monthTotal,4,RoundingMode.HALF_EVEN).multiply(CommonConstants.BIGDECIMAL_TWELVE).setScale(2, RoundingMode.HALF_UP);
                }

                resultObject = getObject( resultObject
                                        , month01SeasonIndex
                                        , month02SeasonIndex
                                        , month03SeasonIndex
                                        , month04SeasonIndex
                                        , month05SeasonIndex
                                        , month06SeasonIndex
                                        , month07SeasonIndex
                                        , month08SeasonIndex
                                        , month09SeasonIndex
                                        , month10SeasonIndex
                                        , month11SeasonIndex
                                        , month12SeasonIndex);

                resultMap.put(key , resultObject);
            }
        }

        insertSeasonIndex(siteId, resultMap);


        //Release resources.
        if(demandForecastTmpModelMap !=null) {
            demandForecastTmpModelMap.clear();
            demandForecastTmpModelMap = null;
        }
        if(newDemandForecastTmpModel !=null) {
            newDemandForecastTmpModel.clear();
            newDemandForecastTmpModel = null;
        }

        if(productOrderResultSummaryMapList !=null) {
            productOrderResultSummaryMapList.clear();
            productOrderResultSummaryMapList = null;
        }

        if(facprocate !=null) {
            facprocate.clear();
            facprocate = null;
        }

        if(resultMap !=null) {
            resultMap.clear();
            resultMap = null;
        }

        timer.stop();
        if(log.isInfoEnabled()) {
            log.info("********End of season Index Calculate Process, during time: " + timer.getTotalTimeMillis() + "ms********");
        }
    }

    public void doDemandForecastCalculate(String siteId
                                        , String processDate
                                        , Map<Long, Long> fromProductIdMap
                                        , List<ProductAbcBatchBO> productFacilityFeatureList) {

        log.info("********Start Demand Forecast Calculate Process********");
        StopWatch timer = new StopWatch();
        timer.start();

        StopWatch taskTimer = new StopWatch();
        TimerUtil.startTask(taskTimer, "Prepare data");

        String targetMonth = processDate.substring(0, 6);
        List<ProductAbcBatchBO> productAbcModelList = selectJdbcDao.getAbcType(siteId,null);
        Map<String, Long> productCategoryMap = new HashMap<String, Long>();
        Map<String, String> averageDemandMap = new HashMap<String, String>();
        Map<String, String> trendIndexMap    = new HashMap<String, String>();
        String month                         = DateUtils.date2String(DateUtils.addMonth(DateUtils.string2Date(processDate, DateUtils.FORMAT_YMD_NODELIMITER),1)
                                                                    ,DateUtils.FORMAT_YMD_NODELIMITER).substring(4, 6);
        selectJdbcDao.searchFacilityPartsAvgMonthAndTrendIdx(siteId, averageDemandMap, trendIndexMap);

        Map<String, SiSeasonIndexInfoModelBO> seasonIndexMap = selectJdbcDao.getSeasonIndexInfoMap(siteId,null);
        Map<String, SiSeasonIndexInfoModelBO> seasonIndexManualMap = selectJdbcDao.getSeasonIndexManualInfoMap(siteId,null);

        for (ProductAbcBatchBO adProductFeatureModel : productFacilityFeatureList) {
            productCategoryMap.put(adProductFeatureModel.getFacilityid()+ CommonConstants.CHAR_COLON + adProductFeatureModel.getProductid()
                                 , adProductFeatureModel.getProductcategoryid());
        }
        int rowCount = productAbcModelList.size();
        log.info(rowCount + " ProductAbc records to be calculated");

        TimerUtil.endTask(taskTimer, log, "productAbcModelList:%d, averageDemandMap:%d, trendIndexMap:%d, seasonIndexMap:%d, productCategoryMap:%d" ,
                                            productAbcModelList.size(), averageDemandMap.size(), trendIndexMap.size(), seasonIndexMap.size(), productCategoryMap.size()
                                          );

        Long productId            = null;
        Long facilityId           = null;
        String facPrdIdGroupKey     = null;
        String facPrdCateIdGroupKey = null;
        Long fromProductId        = null;
        String averageDemand        = null;
        String trendIndex           = null;
        Long productCategoryId    = null;
        String seasonIndex          = null;
        String abcType              = null;
        SiSeasonIndexInfoModelBO seasonIndexModel = null;
        SiSeasonIndexInfoModelBO seasonIndexManualModel = null;

        int listSize = (rowCount > CommonConstants.DB_OP_BATCH_SIZE) ? CommonConstants.DB_OP_BATCH_SIZE : rowCount;
        List<Object[]> demandForecastArgs = new ArrayList<Object[]>(listSize);
        int batchIdx = 1;
        TimerUtil.startTask(taskTimer, "Insert batch %d of %d rows into demand forecase table.", batchIdx , rowCount);
        SnowflakeIdWorker snowflakeIdWorker = IdUtils.getSnowflakeIdWorker();
        for (ProductAbcBatchBO model : productAbcModelList) {

            abcType              = model.getAbctype();
            productId            = model.getProductid();
            facilityId           = model.getFacilityid();
            facPrdIdGroupKey     = facilityId + CommonConstants.CHAR_COLON + productId;
            fromProductId        = fromProductIdMap.get(productId);
            averageDemand        = averageDemandMap.get(facPrdIdGroupKey);
            trendIndex           = trendIndexMap.get(facPrdIdGroupKey);
            productCategoryId    = productCategoryMap.get(facPrdIdGroupKey);
            facPrdCateIdGroupKey = facilityId + CommonConstants.CHAR_COLON + productCategoryId;
            seasonIndexModel     = seasonIndexMap.get(facPrdCateIdGroupKey);
            seasonIndexManualModel = seasonIndexManualMap.get(facPrdCateIdGroupKey);

            averageDemand   = nullToCharZero(averageDemand);
            trendIndex      = nullToCharOne(trendIndex);
            BigDecimal monthDemand = null;
            if (seasonIndexModel != null) {

                try {
                    seasonIndex = BeanUtils.getProperty(seasonIndexModel, IDX_COLUMN_NAME);
                } catch (Exception e) {

                    String msg  = String.format("GET seasonIndex value fail!");
                    log.error(msg, e);
                }
            }

            if (seasonIndexManualModel != null) {

                try {
                    seasonIndex = BeanUtils.getProperty(seasonIndexManualModel,  "n" + month + "index");
                } catch (Exception e) {

                    String msg  = String.format("GET seasonIndex value fail!");
                    log.error(msg, e);
                }
            }

            if (StringUtils.isNotBlankText(abcType) && StringUtils.equals(abcType.substring(CommonConstants.PRODUCT_TYPE_MODEL, CommonConstants.PRODUCT_TYPE_PARTS),"N")) {

                monthDemand = NumberUtil.toBigDecimal(averageDemand);
            } else {

                monthDemand = calMonthDemand(averageDemand
                                           , trendIndex
                                           , nullToCharOne(seasonIndex));
            }

            demandForecastArgs.add(new Object[]{snowflakeIdWorker.nextId()
                                              , siteId
                                              , facilityId
                                              , targetMonth
                                              , productId
                                              , fromProductId
                                              , productCategoryId
                                              , monthDemand
                                              , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                                              , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                                              , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                                              });

            if(demandForecastArgs.size() % CommonConstants.DB_OP_BATCH_SIZE == 0) { //It's the batch size.

                updateJdbcDao.insertDemandForecast(demandForecastArgs);
                demandForecastArgs.clear();
            }
        }

        if (demandForecastArgs!=null && !demandForecastArgs.isEmpty()) {

            updateJdbcDao.insertDemandForecast(demandForecastArgs);
            demandForecastArgs.clear();
        }

        TimerUtil.endTask(taskTimer, log);
        if(log.isInfoEnabled()) {
          log.info("Batch insert data, total during time:" + taskTimer.getTotalTimeMillis() + "ms");
        }


        if(demandForecastArgs != null) {
          demandForecastArgs.clear();
          demandForecastArgs = null;
        }

        if(averageDemandMap != null) {
          averageDemandMap.clear();
          averageDemandMap = null;
        }

        if(trendIndexMap != null) {
          trendIndexMap.clear();
          trendIndexMap = null;
        }

        seasonIndexMap = null;
        productCategoryMap = null;

        timer.stop();
        if(log.isInfoEnabled()) {
          log.info("********End of Demand Forecast Calculate Process, during time: " + timer.getTotalTimeMillis() + "ms********");
        }
    }

    public void doRopRoqCalculate(String siteId
                                , String processDate
                                , List<ProductAbcBatchBO> productAbcModelList
                                , Map<String, DemandForecastBatchBO> demandForecastTmpMap){

        log.info("********Start RopRoq Calculate Process********");
        StopWatch timer = new StopWatch();
        timer.start();

        String thisMonth    = processDate.substring(4, 6);
        Long productId = null;
        Long facilityId = null;
        String corrRopmonthStr = null;

        StopWatch taskTimer = new StopWatch();
        TimerUtil.startTask(taskTimer, "Prepare data");
        // get type is A,B,C,N data
        Map<String, ProductAbcBatchBO> productAbcModelMap = getFacPrdKeyAbcMap(productAbcModelList);
        Set<String> hasFromProductFacToProds = new HashSet<String>();
        Map<String, ProductAbcBatchBO> rrDemandForecastMonthItemMap = new HashMap<String, ProductAbcBatchBO>();
        List<RrDemandForecastMonthItemBatchBO> demandForecastList = getDemandForcastList(siteId, productAbcModelMap, hasFromProductFacToProds, rrDemandForecastMonthItemMap);

        Map<String,Object[]> resultMap = new HashMap<String,Object[]>();
        log.info(rrDemandForecastMonthItemMap.size() + " ROP ROQ to be calculated");

        //Get monthly ROP by parts'.
        Map<String, SsmProductAbcBatchBO> ropMonthMap = selectJdbcDao.getPartsRopqMonthlyMap(siteId, null,RopRoqParameter.KEY_PARTSROPMONTH);

        // get reorderGuideline data
        Set<String> facproList = selectJdbcDao.getReorderGuidelineMap(siteId,null);

        TimerUtil.endTask(taskTimer, log, "demandForecastList:%d, rrDemandForecastMonthItemMap:%d, ropMonthMap:%d, facproList:%d",
          demandForecastList.size(), rrDemandForecastMonthItemMap.size(), ropMonthMap.size(), facproList.size());

        // no newProduct product
        String facToPrdKeyWithColon = null;
        String facPrdMapKey = null;
        BigDecimal month01 = BigDecimal.ZERO;

        String roqmonthStr = null;
        BigDecimal ropmonth = null;
        BigDecimal roqmonth = null;
        ProductAbcBatchBO item = null;

        TimerUtil.startTask(taskTimer, "Collect newest products' reorder guide line ");
        for (RrDemandForecastMonthItemBatchBO rrDemandForecastMonthItemModel : demandForecastList) {

            int number=0;
            facilityId = rrDemandForecastMonthItemModel.getFacilityid();
            productId = rrDemandForecastMonthItemModel.getToproductid();
            facToPrdKeyWithColon = longToStr(facilityId) + CommonConstants.CHAR_COLON + longToStr(productId);
            facPrdMapKey = longToStr(facilityId) + longToStr(productId);

            if (!facproList.contains(facPrdMapKey)//It has not any reorder guide line info yet!
                    && rrDemandForecastMonthItemMap.containsKey(facPrdMapKey)) { //It's the old one and has ABC definition.

                if (!hasFromProductFacToProds.contains(facToPrdKeyWithColon)) {//It's the newest product.

                    Object[] resultObject = new Object[2];
                    month01 = BigDecimal.ZERO;

                    String featureRopmonth  = "0";

                    item = rrDemandForecastMonthItemMap.get(facPrdMapKey);
                    corrRopmonthStr = item.getRopmonth();
                    if (ropMonthMap.containsKey(facPrdMapKey)) {
                        featureRopmonth = ropMonthMap.get(facPrdMapKey).getStringvalue();
                    }

                    String ropmonthStr = "0";
                    if (NumberUtil.toBigDecimal(nullObject(corrRopmonthStr)).signum() == 1){ //ROP of month is positive.
                        ropmonthStr = corrRopmonthStr;
                    } else if (NumberUtil.toBigDecimal(nullObject(featureRopmonth)).signum() ==1){ //ROP of month is positive.
                        ropmonthStr = featureRopmonth;
                    } else {
                        continue;
                    }

                    roqmonthStr = item.getRoqmonth();
                    ropmonth = new BigDecimal(ropmonthStr);
                    roqmonth = new BigDecimal(roqmonthStr);
                    month01 = month01.add(NumberUtil.toBigDecimal(nullObject(rrDemandForecastMonthItemModel.getDemandqty())));

                    BigDecimal roqBigDecimal = getDecPartQty(BigDecimal.ZERO,roqmonth, month01);
                    BigDecimal ropBigDecimal = getDecPartQty(BigDecimal.ZERO,ropmonth, month01);

                    if (NumberUtil.le(roqBigDecimal,BigDecimal.ONE)) {
                        roqBigDecimal = BigDecimal.ONE;
                    }

                    number = getMonthQty(1
                                   , 3
                                   , demandForecastTmpMap.get(facToPrdKeyWithColon)
                                   , thisMonth
                                   , number);

                    if (number >=2) {
                        ropBigDecimal = CommonConstants.BIGDECIMAL_ZERO;
                    }


                    // get rop ,roq
                    resultObject[0] = roqBigDecimal;
                    resultObject[1] = ropBigDecimal;

                    resultMap.put(facToPrdKeyWithColon, resultObject);
                }
            }
        }
        int normal = resultMap.size();
        TimerUtil.endTask(taskTimer, log, " size:" + normal);

        TimerUtil.startTask(taskTimer, "Collect 'D,E,XX,YY' type products ");
        Set<String> facPrdIds = selectJdbcDao.getProductTypeDEXY(siteId,null); //Type is D,E,XX,YY
        for (String facPrdId : facPrdIds) {
            if (facproList.contains(facPrdId.replace(CommonConstants.CHAR_COLON, CommonConstants.CHAR_BLANK))) {
                continue;
            }
            Object[] resultObject = new Object[2];
            resultObject[0] = BigDecimal.ONE;
            resultObject[1] = BigDecimal.ZERO;
            resultMap.put(facPrdId, resultObject);
        }
        TimerUtil.endTask(taskTimer, log, " size:" + (resultMap.size() - normal));

        //Delete the reorder guide line data and insert new rows.
        TimerUtil.startTask(taskTimer, "Insert new reorder guide line, size:" + resultMap.size());
        insertReorderGuideline(siteId,resultMap);
        TimerUtil.endTask(taskTimer, log);

        //Release resources.

        rrDemandForecastMonthItemMap = null;
        if(hasFromProductFacToProds !=null) {
        hasFromProductFacToProds.clear();
        hasFromProductFacToProds = null;
        }
        if(ropMonthMap !=null) {
        ropMonthMap.clear();
        ropMonthMap = null;
        }
        if(demandForecastList !=null) {
        demandForecastList.clear();
        demandForecastList = null;
        }

        if(resultMap !=null) {
        resultMap.clear();
        resultMap = null;
        }


        timer.stop();
        if(log.isInfoEnabled()) {
        log.info("********End of RopRoq Calculate Process, during time: " + timer.getTotalTimeMillis() + "ms********");
        }
    }

    public List<String> getSiteList(String apServerUrl) {

        List<String> siteList = selectJdbcDao.getCmmSiteList(apServerUrl);
        return siteList;
    }

    public String getSystemParDate(String siteId
                                  ,String sysParamTypeId) {

        String lastDailyBatchDate = selectJdbcDao.getSystemParDate(siteId, sysParamTypeId);
        return lastDailyBatchDate;
    }

    public String getAcctMonthInfo(String siteId
                                    ,String accountMonth) {

        String dateTo = selectJdbcDao.getAcctMonthInfo(siteId, accountMonth);
        return dateTo;
    }

    public void truncateTable(String siteId) {

        updateJdbcDao.deleteTableDemandforecast(siteId);
        updateJdbcDao.deleteTableProductAbc(siteId);
        updateJdbcDao.deleteTablePartsRopqMonthly(siteId);
        updateJdbcDao.deleteTableReorderGuideline(siteId);
    }

    public List<Long> getFacilityList(String siteId,String processDate) {

        List<Long> siteList = selectJdbcDao.getFacilityList(siteId,processDate);
        return siteList;
    }

    public Map<Long, Long> getfromProductId(String siteId){

        // product relation
        return selectJdbcDao.getReplaceProductIdMap(siteId);
    }

    public List<PartsBatchBO> getProductRegisterdate(String siteId,Map<Long,String> keyProductIdMap){

        List<PartsBatchBO> producList = selectJdbcDao.getAllProductId(siteId);
        for (PartsBatchBO partsBatchBO : producList) {

            keyProductIdMap.put(partsBatchBO.getProductid(), partsBatchBO.getRegistrationdate());
        }

        return producList;
    }

    public void insertReorderGuideline(String siteId
                                     , Map<String, Object[]> resultMap) {


        //Delete rows.
        //ropRoqBatchDao.deleteReorderGuideline(siteId);

        int rowCount = resultMap.size();
        if(log.isDebugEnabled()) {
        log.info("Inserting " + rowCount + " reorder guide line data.");
        }

        int batchSize = CommonConstants.DB_OP_BATCH_SIZE;
        int listSize = (rowCount > batchSize) ? batchSize : rowCount;
        List<Object[]> batchArgs = new ArrayList<Object[]>(listSize);
        //int count = 0;
        //Timestamp currentTS = new Timestamp(new Date().getTime());
        SnowflakeIdWorker snowflakeIdWorker = IdUtils.getSnowflakeIdWorker();
        for (String key : resultMap.keySet()) {

            String[] proFac = key.split(CommonConstants.CHAR_COLON);
            Object[] map    = resultMap.get(key);
            batchArgs.add(new Object[]{snowflakeIdWorker.nextId()
                       , siteId
                       , toLong(proFac[0])
                       , toLong(proFac[1])
                       , map[1]
                       , map[0]
                       , CommonConstants.CHAR_N
                       , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                       , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                       , CommonConstants.ROPQBATCH_UPDATE_PROGRAM});

            if(batchArgs.size() % CommonConstants.DB_OP_BATCH_SIZE == 0) { //It's the batch size.

                updateJdbcDao.insertReorderGuideLineInfo(batchArgs);
                batchArgs.clear();
            }
        }

        //Insert with copy to command.
        if (batchArgs!=null && !batchArgs.isEmpty()) {

            updateJdbcDao.insertReorderGuideLineInfo(batchArgs);
            batchArgs.clear();
        }
    }

    public List<ProductAbcBatchBO> getProductTypeABCN(String siteId ,
                                                        Map<String, ProductAbcBatchBO> productAbcMap,
                                                        Map<String, DemandForecastBatchBO> demfortempMap,
                                                        List<ProductAbcBatchBO> productFacilityFeatureList) {

        List<ProductAbcBatchBO> productAbcModelList =  selectJdbcDao.getProductProductAbcTypeABCN(siteId,null);

        //get productAbcMap key<facilityId:productId> from product_abc_info and abc_definition_info
        for (ProductAbcBatchBO productAbcModelBO : productAbcModelList) {

            String key = productAbcModelBO.getFacilityid() + CommonConstants.CHAR_COLON + productAbcModelBO.getProductid();
            //1.
            if(demfortempMap.containsKey(key)) {

                DemandForecastBatchBO item = demfortempMap.get(key);
                productAbcModelBO.setRegisterdate(item.getRegisterdate());
                productAbcModelBO.setFirstorderdate(item.getFirstorderdate());
                productAbcModelBO.setJ1total(item.getJ1total());
                productAbcModelBO.setJ2total(item.getJ2total());
                productFacilityFeatureList.add(productAbcModelBO);
            }
            //2.
            productAbcMap.put(key, productAbcModelBO);
        }

        return productAbcModelList;
    }


    private Map<String, ProductAbcBatchBO> getFacPrdKeyAbcMap(  List<ProductAbcBatchBO> productAbcModelList) {

        Map<String, ProductAbcBatchBO> productAbcModelMap = new HashMap<String, ProductAbcBatchBO>();
        for (ProductAbcBatchBO productAbcModel : productAbcModelList) {
            productAbcModelMap.put(longToStr(productAbcModel.getFacilityid())+longToStr(productAbcModel.getProductid())
                                   , productAbcModel);
        }
        return productAbcModelMap;
    }

    private List<RrDemandForecastMonthItemBatchBO> getDemandForcastList(String siteId,
                                                                        final Map<String, ProductAbcBatchBO> productAbcModelMap,
                                                                        final Set<String> facToProd2FromPrdMap,
                                                                        final Map<String, ProductAbcBatchBO> rrDemandForecastMonthItemMap) {

        List<RrDemandForecastMonthItemBatchBO>  demandForecastList = selectJdbcDao.getDemandForecast(siteId,null);
        for (RrDemandForecastMonthItemBatchBO rrDemandForBO : demandForecastList) {

            if(rrDemandForBO.getProductid() != null && rrDemandForBO.getProductid() != 0) {
                facToProd2FromPrdMap.add(longToStr(rrDemandForBO.getFacilityid())
                                            + CommonConstants.CHAR_COLON
                                            + longToStr(rrDemandForBO.getToproductid()));
            }

            String key2 = longToStr(rrDemandForBO.getFacilityid())+longToStr(rrDemandForBO.getToproductid());
            if(productAbcModelMap.containsKey(key2)) {
                rrDemandForecastMonthItemMap.put(key2, productAbcModelMap.get(key2));
            }
        }

        return demandForecastList;
    }

    private Integer getMonthQty(Integer begin
                              , Integer end
                              , DemandForecastBatchBO demandForecastTmpModel
                              , String thisMonth
                              , int number) {

        String monthQuantity = null, month = null;
        for ( int i = begin; i <= end ;i++) {

            month = padCharLeft(StringUtils.toString(i), 2, '0'); //'2' -> '02'; '12' -> '12'.
            if (demandForecastTmpModel != null) {
                try {
                    // Get each month quantity
                    monthQuantity = BeanUtils.getProperty(demandForecastTmpModel,  "month" + month);
                } catch (Exception e) {
                    String msg = String.format("Fail to get property '' of Object[%s]", ("month" + month));
                    log.error(msg, e);
                    throw new PJCustomException(msg);
                }
                if (StringUtils.isBlankText(monthQuantity) || NumberUtil.toBigDecimal(monthQuantity).signum() ==0) { //
                    ++number;
                }
            }
        }
        return number;
    }

    private BigDecimal getDecPartQty(BigDecimal bigDecimal,BigDecimal ropmonthDec, BigDecimal month01) {

        bigDecimal = bigDecimal.add(month01.multiply(ropmonthDec)).add(CommonConstants.BIGDECIMAL_UP).setScale(2, RoundingMode.HALF_UP); ;
        return bigDecimal;
    }

    private BigDecimal calMonthDemand(String averageDemand
                                    , String trendIndex
                                    , String seasonIndex) {

        BigDecimal monthDemand = NumberUtil.toBigDecimal(averageDemand)
                                            .multiply(NumberUtil.toBigDecimal(trendIndex))
                                            .multiply(NumberUtil.toBigDecimal(seasonIndex));
        return monthDemand;
    }

    private void insertSeasonIndex(String siteId
                                 , Map<String, Object[]> resultMap) {

        //Delete rows.
        updateJdbcDao.deleteSeasonIndexInfo(siteId);

        //Insert new rows.
        List<Object[]> batchArgs = new ArrayList<Object[]>();
        SnowflakeIdWorker snowflakeIdWorker = IdUtils.getSnowflakeIdWorker();
        for (String key : resultMap.keySet()) {

            String[] proFac = key.split(CommonConstants.CHAR_COLON);
            Object[] map = resultMap.get(key);

            batchArgs.add(new Object[]{snowflakeIdWorker.nextId()
                            , siteId
                            , toLong(proFac[0])
                            , toLong(proFac[1])
                            , map[0]
                            , map[1]
                            , map[2]
                            , map[3]
                            , map[4]
                            , map[5]
                            , map[6]
                            , map[7]
                            , map[8]
                            , map[9]
                            , map[10]
                            , map[11]
                            , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                            , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                            , CommonConstants.ROPQBATCH_UPDATE_PROGRAM});
        }

        updateJdbcDao.insertSeasonIndexInfo(batchArgs);
    }

    private Object[] getObject(Object[] resultObject
                            , BigDecimal month01SeasonIndex
                            , BigDecimal month02SeasonIndex
                            , BigDecimal month03SeasonIndex
                            , BigDecimal month04SeasonIndex
                            , BigDecimal month05SeasonIndex
                            , BigDecimal month06SeasonIndex
                            , BigDecimal month07SeasonIndex
                            , BigDecimal month08SeasonIndex
                            , BigDecimal month09SeasonIndex
                            , BigDecimal month10SeasonIndex
                            , BigDecimal month11SeasonIndex
                            , BigDecimal month12SeasonIndex) {

        resultObject [0] = month01SeasonIndex;
        resultObject [1] = month02SeasonIndex;
        resultObject [2] = month03SeasonIndex;
        resultObject [3] = month04SeasonIndex;
        resultObject [4] = month05SeasonIndex;
        resultObject [5] = month06SeasonIndex;
        resultObject [6] = month07SeasonIndex;
        resultObject [7] = month08SeasonIndex;
        resultObject [8] = month09SeasonIndex;
        resultObject [9] = month10SeasonIndex;
        resultObject [10] = month11SeasonIndex;
        resultObject [11] = month12SeasonIndex;

        return resultObject;
    }

    private void getDiffMonthData(List<DemandForecastBatchBO> siProductOrderResultSummaryModelList) {

        Map<String,List<DemandForecastBatchBO>> productOrderResultSummaryMapList = this.sameProductIdList(siProductOrderResultSummaryModelList);
        for (String key : productOrderResultSummaryMapList.keySet()) {

            for (DemandForecastBatchBO siProductOrderResultSummaryModel : productOrderResultSummaryMapList.get(key)) {

                // get this year data from January to this month subtract one
//                if (StringUtil.equals(siProductOrderResultSummaryModel.getTargetyear(), yearDate)) {

                for(int i = 1; i <= 12; i++) {

                    String j = StringUtils.toString(i);
                    String k = CommonConstants.CHAR_ZERO;
                    if (i<10) {
                       j = CommonConstants.CHAR_ZERO + StringUtils.toString(i);
                    }
                    try {
                        k = BeanUtils.getProperty(siProductOrderResultSummaryModel, "month" + j);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }

                    k = nullToCharZero(k);
                    demand[i-1] = demand[i-1].add(NumberUtil.toBigDecimal(k));
                }
            }
        }
    }

    // GET PRODUCT INFORMATION FROM SAME FACILITYID AND CATEGORYID
    private Map<String,List<DemandForecastBatchBO>> sameProductIdList(List<DemandForecastBatchBO> siProductOrderResultSummaryModelList) {

        Map<String,List<DemandForecastBatchBO>> productOrderResultSummaryMap = new HashMap<String,List<DemandForecastBatchBO>>();

        for (DemandForecastBatchBO siProductOrderResultSummaryModel : siProductOrderResultSummaryModelList) {

            if (productOrderResultSummaryMap.containsKey(siProductOrderResultSummaryModel.getProductid())) {

                productOrderResultSummaryMap.get(siProductOrderResultSummaryModel.getProductid()).add(siProductOrderResultSummaryModel);

              } else {

                  List<DemandForecastBatchBO> siProductOrderResultSummaryModellist = new ArrayList<DemandForecastBatchBO>();
                  siProductOrderResultSummaryModellist.add(siProductOrderResultSummaryModel);
                  productOrderResultSummaryMap.put(siProductOrderResultSummaryModel.getProductid(), siProductOrderResultSummaryModellist);
              }
        }

        return productOrderResultSummaryMap;
    }

    /**
    * @param siteId
    * @param trendIndexMap
    */
    private void calculateTrendIndex(String siteId, List<ProductAbcBatchBO> trendIndexMap) {

        BigDecimal trendIndex = CommonConstants.BIGDECIMAL_ZERO;

        Map<String, String>  trendIdxLU = selectJdbcDao.getTrendIndexUpperAndLowerValues(siteId);
        BigDecimal trendIndexUpper = nullToZero(trendIdxLU.get(SystemParameterType.GROWTHUPPER));
        BigDecimal trendIndexLower = nullToZero(trendIdxLU.get(SystemParameterType.GROWTHLOWER));

        //common para
        List<Object[]> batchArgs = new ArrayList<Object[]>();

        log.info(trendIndexMap.size() + " ProductAbc records to be calculated");

        // Get calculate product infos and insert into temp Product Facility Feature table
        int count = 0;
        SnowflakeIdWorker snowflakeIdWorker = IdUtils.getSnowflakeIdWorker();
        for (ProductAbcBatchBO trendIndexModel : trendIndexMap) {

            //1. Prepare for inserting.
            if (StringUtils.equals(trendIndexModel.getAbctype(), CommonConstants.CHAR_N)) {
                trendIndexModel.setTrendindex(CommonConstants.BIGDECIMAL_ONE);
            } else {
                if (StringUtils.isBlankText(trendIndexModel.getJ2total()) || StringUtils.compare(trendIndexModel.getJ2total(), CommonConstants.STRING_EIGHT_ZERO, true)<=0) {
                    trendIndexModel.setTrendindex(trendIndexUpper);
                } else {
                    trendIndex = nullToZero(trendIndexModel.getJ1total())
                                      .divide(nullToZero(trendIndexModel.getJ2total(), false),2,RoundingMode.HALF_EVEN);
                    trendIndexModel.setTrendindex(trendIndex);
                    if (NumberUtil.ge(trendIndex, trendIndexUpper)) {
                        trendIndexModel.setTrendindex(trendIndexUpper);
                    } else if (NumberUtil.bigDecimalLe(trendIndex, trendIndexLower)) {
                        trendIndexModel.setTrendindex(trendIndexLower);
                    }
                }
            }

            //2.Insert into DB in batches.
            batchArgs.add(new Object[]{snowflakeIdWorker.nextId()
                                    , siteId
                                    , trendIndexModel.getFacilityid()
                                    , trendIndexModel.getProductid()
                                    , RopRoqParameter.KEY_PARTSTRENDINDEX
                                    , trendIndexModel.getTrendindex()
                                    , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                                    , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                                    , CommonConstants.ROPQBATCH_UPDATE_PROGRAM});

            if ((++count) % CommonConstants.DB_OP_BATCH_SIZE == 0) {
                updateJdbcDao.insertPartsRopqMonthlyData(batchArgs);
                batchArgs.clear();
            }
        }

        if(!batchArgs.isEmpty()) {
            updateJdbcDao.insertPartsRopqMonthlyData(batchArgs);
        }

        if(batchArgs !=null) {
            batchArgs.clear();
            batchArgs = null;
        }
    }

    private BigDecimal nullToZero(String v) {

        return nullToZero(v, true);
    }

    private BigDecimal nullToZero(String v, boolean isZeroNotAllow) {

        v =  StringUtils.isBlankText(v) ? CommonConstants.CHAR_ZERO : v;
        BigDecimal r = NumberUtil.toBigDecimal(v);
        if(!isZeroNotAllow && r.signum()==0) {
          throw new BatchFailedException("The input string '" + v + "' is equal BigDecimal.Zero, that is not allowed!");
        }
        return r;
    }

    private void insertPartsRopqMonthlyByRopMonth(String siteId
                                                , Map<String, Object[]> realStockMontMap) {

        //1. Delete rows.
        updateJdbcDao.deletePartsRopqMonthlyData(siteId, RopRoqParameter.KEY_PARTSROPMONTH);

        //2. Insert new rows in batches;
        int count = 0;
        List<Object[]> batchArgs = new ArrayList<Object[]>();
        SnowflakeIdWorker snowflakeIdWorker = IdUtils.getSnowflakeIdWorker();
        for (String key : realStockMontMap.keySet()) {

            String[] proFac = key.split(CommonConstants.CHAR_COLON);
            Object[] map = realStockMontMap.get(key);

            batchArgs.add(new Object[]{snowflakeIdWorker.nextId()
                                    , siteId
                                    , toLong(proFac[0])
                                    , toLong(proFac[1])
                                    , RopRoqParameter.KEY_PARTSROPMONTH
                                    , map[0].toString()
                                    , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                                    , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                                    , CommonConstants.ROPQBATCH_UPDATE_PROGRAM});

            if( (++count) % CommonConstants.DB_OP_BATCH_SIZE == 0) {

                updateJdbcDao.insertPartsRopqMonthlyData(batchArgs);
                batchArgs.clear();
            }
        }


        if(!batchArgs.isEmpty()) {

            updateJdbcDao.insertPartsRopqMonthlyData(batchArgs);
        }
        batchArgs.clear();
        batchArgs = null;
    }

    private BigDecimal safetyStockMonthCount (String partsLeadtime
                                            , String addLeadtime
                                            , String sTDDeviation
                                            , String safetyFactor
                                            , String ssmLowe
                                            , String ssmupp) {

        // Math.sqrt(a)
        Double sqrtData = 0.00;
        BigDecimal safetyStockMonth = new BigDecimal(0.00);
        BigDecimal partsLeadtimeDecimal = new BigDecimal(0.00);
        BigDecimal addLeadtimeDecimal = new BigDecimal(0.00);
        BigDecimal sqrtDataTwoDec = new BigDecimal(0.00);
        BigDecimal sum = new BigDecimal(0.00);
        partsLeadtimeDecimal = NumberUtil.toBigDecimal(nullObject(partsLeadtime));
        addLeadtimeDecimal = NumberUtil.toBigDecimal(nullObject(addLeadtime));
        sum = partsLeadtimeDecimal.add(addLeadtimeDecimal);
        BigDecimal percentage = sum.divide(CommonConstants.BIG_DECIMAL_THIRTY,2,RoundingMode.HALF_EVEN);

        sqrtData = Math.sqrt(NumberUtil.toDouble(StringUtils.toString(percentage)));
        sqrtDataTwoDec = NumberUtil.toBigDecimal(nullObject(StringUtils.toString(sqrtData)));

        safetyStockMonth = sqrtDataTwoDec.multiply(NumberUtil.toBigDecimal(nullObject(sTDDeviation)))
                                         .multiply(NumberUtil.toBigDecimal(nullObject(safetyFactor))).setScale(2, RoundingMode.HALF_UP);

        return safetyStockMonth;
    }

    private Map<String, String> getCodeMstS054() {

        List<MstCodeInfo> mstCodeList =  mstCodeInfoRepository.findByCodeIdAndSiteId(PartsSafetyFactory.CODE_ID,CommonConstants.CHAR_DEFAULT_SITE_ID);

        // get mstcodeinfo type is S054
        Map<String, String> mstCodeS054Map = new HashMap<String, String>();
        for (MstCodeInfo value : mstCodeList) {

            mstCodeS054Map.put(value.getCodeData1(),value.getCodeData2());
        }

        return mstCodeS054Map;
    }

    private Integer calculateMonthBetween(String startDate, String endDate) {

        Integer monthBetween = NumberUtil.toInteger(endDate.substring(0, 4)) * 12
                                                     + NumberUtil.toInteger(endDate.substring(4, 6))
                                                     - NumberUtil.toInteger(startDate.substring(0, 4)) * 12
                                                     - NumberUtil.toInteger(startDate.substring(4, 6)) + 1;

        return monthBetween;
    }

    //D,E,XX,YY
    private List<String> getTypeList(){

        List<String> typeList = new ArrayList<String>();
        typeList.add(CommonConstants.CHAR_D);
        typeList.add(CommonConstants.CHAR_E);
        typeList.add(CommonConstants.CHAR_XX);
        typeList.add(CommonConstants.CHAR_YY);

        return typeList;
    }

    private void insertPartsRopqMonthlyFacPro(String siteId
                                               , Map<String, Object[]> resultMap
                                               , String ropqType) {

        //1. Delete rows in DB.
        updateJdbcDao.deletePartsRopqMonthlyData(siteId, ropqType);

        //2. Insert new rows in DB.
        List<Object[]> batchArgs = new ArrayList<Object[]>();
        int count = 0;
        SnowflakeIdWorker snowflakeIdWorker = IdUtils.getSnowflakeIdWorker();
        for (String key : resultMap.keySet()) {

            String[] proFac = key.split(CommonConstants.CHAR_COLON);
            Object[] map = resultMap.get(key);
            batchArgs.add(new Object[]{snowflakeIdWorker.nextId()
                                       , siteId
                                       , toLong(proFac[0])
                                       , toLong(proFac[1])
                                       , ropqType
                                       , map[0].toString()
                                       , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                                       , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                                       , CommonConstants.ROPQBATCH_UPDATE_PROGRAM});

            if( (++count) % CommonConstants.DB_OP_BATCH_SIZE == 0) {

                updateJdbcDao.insertPartsRopqMonthlyData(batchArgs);
                batchArgs.clear();
            }
        }

        if( !batchArgs.isEmpty()) {

            updateJdbcDao.insertPartsRopqMonthlyData(batchArgs);
        }
        batchArgs.clear();
        batchArgs = null;
    }

    // now date subtract firstdate surplus month
    private int compareMonth (String salesYear,String salesMonth,String firstOrderYear,String firstOrderMonth) {

        int salesSumMonth = NumberUtil.toInteger(salesYear) * 12 +  NumberUtil.toInteger(salesMonth) + CommonConstants.INTEGER_ONE;
        int firstOrderSumMonth = NumberUtil.toInteger(firstOrderYear) * 12 +  NumberUtil.toInteger(firstOrderMonth);

        return salesSumMonth - firstOrderSumMonth;
    }

    private void insertProductAbc(String siteId,
                                 Map<String, String[]> resultObject) {

        //2.Insert rows in batch.
        doInsertProductAbcRows(siteId, resultObject);
    }

    /**
     *
     * @param siteId
     * @param resultObject
     */
    private void doInsertProductAbcRows(String siteId, Map<String, String[]> resultObject) {


        //1.Delete rows in DB.
        if(log.isInfoEnabled()) {
            log.info("Deleting rows in table 'product_abc_info' by site '" + siteId + "'");
        }
        int batchSize = CommonConstants.DB_OP_BATCH_SIZE;
        List<Object[]> batchArgs = new ArrayList<Object[]>();
        Set<String> faciProdIds =  resultObject.keySet();
        String[] map = null;
        String[] proFac = null;
        if(log.isInfoEnabled()) {
            log.info("The quality of products for this dealer is " + faciProdIds.size());
        }

        StopWatch taskTimer = new StopWatch();
        int batchIdx = 1;
        TimerUtil.startTask(taskTimer, "Insert rows with batch %d", batchIdx);
        SnowflakeIdWorker snowflakeIdWorker = IdUtils.getSnowflakeIdWorker();
        for (String faciProdId : faciProdIds) {

            proFac = faciProdId.split(CommonConstants.CHAR_COLON);
            map = resultObject.get(faciProdId);
            batchArgs.add(new Object[]{snowflakeIdWorker.nextId()
                                   , siteId
                                   , toLong(proFac[0])
                                   , toLong(proFac[1])
                                   , toLong(map[1])
                                   , toLong(map[0])
                                   , map[2]
                                   , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                                   , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                                   , CommonConstants.ROPQBATCH_UPDATE_PROGRAM});

            if (batchArgs.size() % batchSize == 0) {

                updateJdbcDao.insertProductAbc(batchArgs);
                batchArgs.clear();
            }
        }

        if (!batchArgs.isEmpty()) { //There're some one left, continue to insert operation.

            updateJdbcDao.insertProductAbc(batchArgs);
            batchArgs.clear();
        }

        batchArgs = null;
        TimerUtil.endTask(taskTimer, log);
    }

 // GET TYPE IN KEEPING WITH A,B,C DATE
    private CuAbcDefinitionInfoBatchBO getDiffTypeABCModel( List<CuAbcDefinitionInfoBatchBO> abcDefinitionList
                                                        , String type
                                                        , String cost
                                                        , BigDecimal percentage
                                                        , Long categoryId) {

        CuAbcDefinitionInfoBatchBO abcDefinitionListFinal = new CuAbcDefinitionInfoBatchBO();

        for (CuAbcDefinitionInfoBatchBO cuAbcDefinitionInfoModel : abcDefinitionList) {
            if(StringUtils.equals(cuAbcDefinitionInfoModel.getAbctype().substring(0, 1), type)
               && NumberUtil.equals(NumberUtil.toLong(cuAbcDefinitionInfoModel.getProductcategoryid()), categoryId) ) {

                if (NumberUtil.bigDecimalLe(percentage, NumberUtil.toBigDecimal(nullToCharZero(cuAbcDefinitionInfoModel.getPercentage())))
                    && NumberUtil.ge(NumberUtil.toBigDecimal(nullToCharZero(cost)), NumberUtil.toBigDecimal(nullToCharZero(cuAbcDefinitionInfoModel.getCostfrom())))
                    && NumberUtil.bigDecimalLe(NumberUtil.toBigDecimal(nullToCharZero(cost)), NumberUtil.toBigDecimal(nullToCharZero(cuAbcDefinitionInfoModel.getCostto())))) {

                    abcDefinitionListFinal = cuAbcDefinitionInfoModel;

                    break;
                }
            }
        }

        return abcDefinitionListFinal;
    }

    // GET TYPE IN KEEPING WITH E,D,N DATE
    private CuAbcDefinitionInfoBatchBO getDiffTypeModel( List<CuAbcDefinitionInfoBatchBO> abcDefinitionList
                                                     , String type
                                                     , String cost
                                                     , Long categoryId) {

        CuAbcDefinitionInfoBatchBO abcDefinitionListFinal = new CuAbcDefinitionInfoBatchBO();

        for (CuAbcDefinitionInfoBatchBO cuAbcDefinitionInfoModel : abcDefinitionList) {
             if(StringUtils.equals(cuAbcDefinitionInfoModel.getAbctype().substring(0, 1), type)
                && NumberUtil.equals(NumberUtil.toLong(cuAbcDefinitionInfoModel.getProductcategoryid()), categoryId) ) {

                 if (NumberUtil.ge(NumberUtil.toBigDecimal(nullToCharZero(cost)), NumberUtil.toBigDecimal(nullToCharZero(cuAbcDefinitionInfoModel.getCostfrom())))
                     && NumberUtil.bigDecimalLe(NumberUtil.toBigDecimal(nullToCharZero(cost)), NumberUtil.toBigDecimal(nullToCharZero(cuAbcDefinitionInfoModel.getCostto())))) {

                     abcDefinitionListFinal = cuAbcDefinitionInfoModel;

                     break;
                 }
             }
        }

        return abcDefinitionListFinal;
    }

    private Map<Long,List<CuProductInfoBatchBO>> getProductsGroupbyFacility(List<Long> facilityIdList
                                                                           ,String siteId){

        final Map<Long,List<CuProductInfoBatchBO>> organizationFacilityInfoMap = new HashMap<Long, List<CuProductInfoBatchBO>>();
        List<PartsBatchBO> partsBatchBOs = selectJdbcDao.getProductsGroupbyFacility(siteId);

        for (Long facilityId : facilityIdList) {

            List<CuProductInfoBatchBO> copyedList = new ArrayList<CuProductInfoBatchBO>();
            for (PartsBatchBO origOne : partsBatchBOs) {

                CuProductInfoBatchBO newOne = new CuProductInfoBatchBO();
                newOne.setProductid(origOne.getProductid());
                newOne.setCategoryid(origOne.getCategoryid());
                newOne.setFacilityid(facilityId);
                copyedList.add(newOne);
            }
            organizationFacilityInfoMap.put(facilityId, copyedList);
        }

        return organizationFacilityInfoMap;
    }

    private Map<String, DemandForecastBatchBO> calcDemanForcasts(String siteId
                                                                , Map<Long, Long> fromProductIdMap
                                                                , Map<String, Object[]> resultMap) {

        Map<Long, List<Long>> toProductIdMap = selectJdbcDao.getReplaceToProductIdMap(CommonConstants.CHAR_DEFAULT_SITE_ID);
        BigDecimal sumJ1total = CommonConstants.BIGDECIMAL_ZERO;
        BigDecimal sumJ2total = CommonConstants.BIGDECIMAL_ZERO;
        BigDecimal month01qty = CommonConstants.BIGDECIMAL_ZERO;
        BigDecimal month02qty = CommonConstants.BIGDECIMAL_ZERO;
        BigDecimal month03qty = CommonConstants.BIGDECIMAL_ZERO;
        BigDecimal month04qty = CommonConstants.BIGDECIMAL_ZERO;
        BigDecimal month05qty = CommonConstants.BIGDECIMAL_ZERO;
        BigDecimal month06qty = CommonConstants.BIGDECIMAL_ZERO;
        BigDecimal month07qty = CommonConstants.BIGDECIMAL_ZERO;
        BigDecimal month08qty = CommonConstants.BIGDECIMAL_ZERO;
        BigDecimal month09qty = CommonConstants.BIGDECIMAL_ZERO;
        BigDecimal month10qty = CommonConstants.BIGDECIMAL_ZERO;
        BigDecimal month11qty = CommonConstants.BIGDECIMAL_ZERO;
        BigDecimal month12qty = CommonConstants.BIGDECIMAL_ZERO;

        Map<String, DemandForecastBatchBO> j1j2totalMap = new HashMap<String, DemandForecastBatchBO>();
        String facilityId = null;
        String productId = null;
        Long fromProductId = (long)0;
        String registerDate = null;
        String firstOrderDate = null;
        String key  =null;
        String[] fs = null;
        Object[] map = null;

        for (Map.Entry<String,Object[]> entry : resultMap.entrySet()) {

            Object[] resultObject = new Object[16];
            key  = entry.getKey();
            fs = key.split(CommonConstants.CHAR_COLON);
            facilityId = fs[1];
            productId = fs[0];
            fromProductId = fromProductIdMap.get(NumberUtil.toLong(productId));
            map = resultMap.get(key);
            Map<String, BigDecimal> j0Map = (Map<String, BigDecimal>)map[0];
            Map<String, BigDecimal> j1Map = (Map<String, BigDecimal>)map[1];

            sumJ1total = j0Map.containsKey(key) ? j0Map.get(key) : BigDecimal.ZERO;
            sumJ2total = j1Map.containsKey(key) ? j1Map.get(key) : BigDecimal.ZERO;
            month01qty = NumberUtil.toBigDecimal(nullObject(map[2].toString()));
            month02qty = NumberUtil.toBigDecimal(nullObject(map[3].toString()));
            month03qty = NumberUtil.toBigDecimal(nullObject(map[4].toString()));
            month04qty = NumberUtil.toBigDecimal(nullObject(map[5].toString()));
            month05qty = NumberUtil.toBigDecimal(nullObject(map[6].toString()));
            month06qty = NumberUtil.toBigDecimal(nullObject(map[7].toString()));
            month07qty = NumberUtil.toBigDecimal(nullObject(map[8].toString()));
            month08qty = NumberUtil.toBigDecimal(nullObject(map[9].toString()));
            month09qty = NumberUtil.toBigDecimal(nullObject(map[10].toString()));
            month10qty = NumberUtil.toBigDecimal(nullObject(map[11].toString()));
            month11qty = NumberUtil.toBigDecimal(nullObject(map[12].toString()));
            month12qty = NumberUtil.toBigDecimal(nullObject(map[13].toString()));
            registerDate = map[14].toString();
            firstOrderDate = map[15] == null ? null : map[15].toString();

            if (fromProductId == null) {

                List<Long> olderProIdList = toProductIdMap.get(NumberUtil.toLong(productId));
                String okey = null;
                while (olderProIdList != null && olderProIdList.size() > 0) {

                    List<Long> olderProIdDemIdList = new ArrayList<Long>();

                    for (Long olderProId : olderProIdList) {

                        okey = olderProId + CommonConstants.CHAR_COLON + facilityId;
                        Object[] oldMap = resultMap.get(okey);

                        Map<String, BigDecimal> oldJ0Map = (Map<String, BigDecimal>)oldMap[0];
                        Map<String, BigDecimal> oldJ1Map = (Map<String, BigDecimal>)oldMap[1];

                        List<Long> olderProIdDemIdTwoList = toProductIdMap.get(olderProId);
                        if (olderProIdDemIdTwoList != null && olderProIdDemIdTwoList.size() > 0) {
                            olderProIdDemIdList.addAll(olderProIdDemIdTwoList);
                        }
                        sumJ1total = sumJ1total.add(oldJ0Map.containsKey(okey) ? oldJ0Map.get(okey) : BigDecimal.ZERO);
                        sumJ2total = sumJ2total.add(oldJ1Map.containsKey(okey) ? oldJ1Map.get(okey) : BigDecimal.ZERO);

                        month01qty = month01qty.add(NumberUtil.toBigDecimal(nullObject(oldMap[2].toString())));
                        month02qty = month02qty.add(NumberUtil.toBigDecimal(nullObject(oldMap[3].toString())));
                        month03qty = month03qty.add(NumberUtil.toBigDecimal(nullObject(oldMap[4].toString())));
                        month04qty = month04qty.add(NumberUtil.toBigDecimal(nullObject(oldMap[5].toString())));
                        month05qty = month05qty.add(NumberUtil.toBigDecimal(nullObject(oldMap[6].toString())));
                        month06qty = month06qty.add(NumberUtil.toBigDecimal(nullObject(oldMap[7].toString())));
                        month07qty = month07qty.add(NumberUtil.toBigDecimal(nullObject(oldMap[8].toString())));
                        month08qty = month08qty.add(NumberUtil.toBigDecimal(nullObject(oldMap[9].toString())));
                        month09qty = month09qty.add(NumberUtil.toBigDecimal(nullObject(oldMap[10].toString())));
                        month10qty = month10qty.add(NumberUtil.toBigDecimal(nullObject(oldMap[11].toString())));
                        month11qty = month11qty.add(NumberUtil.toBigDecimal(nullObject(oldMap[12].toString())));
                        month12qty = month12qty.add(NumberUtil.toBigDecimal(nullObject(oldMap[13].toString())));
                        if (StringUtils.compare(registerDate, oldMap[14].toString(),true)>0) {
                            registerDate = oldMap[14].toString();
                        }
                        if (StringUtils.compare(firstOrderDate, oldMap[15] == null ? CommonConstants.CHAR_A : oldMap[15].toString(), false) > 0) {
                            firstOrderDate = oldMap[15] == null ? null : oldMap[15].toString();
                        } else if (firstOrderDate == null){
                            firstOrderDate = oldMap[15] == null ? null : oldMap[15].toString();
                        }

                        olderProIdList = olderProIdDemIdList;
                    }
                }
            }

            resultObject[0] = sumJ1total;
            resultObject[1] = sumJ2total;
            if (NumberUtil.bigDecimalLe(sumJ1total, BigDecimal.ZERO)) {
                resultObject[0] = BigDecimal.ZERO;
            }
            if (NumberUtil.bigDecimalLe(sumJ2total, BigDecimal.ZERO)) {
                resultObject[1] = BigDecimal.ZERO;
            }
            resultObject[2] = month01qty;
            resultObject[3] = month02qty;
            resultObject[4] = month03qty;
            resultObject[5] = month04qty;
            resultObject[6] = month05qty;
            resultObject[7] = month06qty;
            resultObject[8] = month07qty;
            resultObject[9] = month08qty;
            resultObject[10] = month09qty;
            resultObject[11] = month10qty;
            resultObject[12] = month11qty;
            resultObject[13] = month12qty;
            resultObject[14] = registerDate;
            resultObject[15] = firstOrderDate;

            j1j2totalMap.put(facilityId + CommonConstants.CHAR_COLON + productId, new DemandForecastBatchBO(resultObject, siteId, facilityId,productId));

        }

        if (toProductIdMap != null) {

            toProductIdMap.clear();
            toProductIdMap = null;
        }

        map = null;
        return j1j2totalMap;
    }

    private void deleteAndInsertProductFacilityFeature(String siteId
                                                     , Map<String, Object[]> resultMap) {

        int batchSize = CommonConstants.DB_OP_BATCH_SIZE;
        int lsize = (batchSize >resultMap.size()) ? resultMap.size() : batchSize;
        List<Object[]> j1BatchArgs = new ArrayList<Object[]>(lsize);

        SnowflakeIdWorker snowflakeIdWorker = IdUtils.getSnowflakeIdWorker();
        for (String key : resultMap.keySet()) {

            String[] proFac = key.split(CommonConstants.CHAR_COLON);
            Object[] map = resultMap.get(key);

            Map<String, BigDecimal> j1Map = (Map<String, BigDecimal>)map[0];
            Map<String, BigDecimal> j2Map = (Map<String, BigDecimal>)map[1];
            BigDecimal j1total = j1Map.get(key);
            BigDecimal j2total = j2Map.get(key);

            if (NumberUtil.bigDecimalLe(j1total, BigDecimal.ZERO) && NumberUtil.bigDecimalLe(j2total, BigDecimal.ZERO)) {
                continue;
            }
            if (NumberUtil.bigDecimalLe(j1total, BigDecimal.ZERO)) {
                j1total = BigDecimal.ZERO;
            }
            if (NumberUtil.bigDecimalLe(j2total, BigDecimal.ZERO)) {
                j2total = BigDecimal.ZERO;
            }

            j1BatchArgs.add(new Object[]{snowflakeIdWorker.nextId()
                            , siteId
                            , toLong(proFac[1])
                            , toLong(proFac[0])
                            , RopRoqParameter.KEY_PARTSJ1TOTAL
                            , StringUtils.toString(j1total.intValue())
                            , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                            , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                            , CommonConstants.ROPQBATCH_UPDATE_PROGRAM});

            j1BatchArgs.add(new Object[]{snowflakeIdWorker.nextId()
                            , siteId
                            , toLong(proFac[1])
                            , toLong(proFac[0])
                            , RopRoqParameter.KEY_PARTSJ2TOTAL
                            , StringUtils.toString(j2total.intValue())
                            , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                            , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                            , CommonConstants.ROPQBATCH_UPDATE_PROGRAM});

            if (j1BatchArgs.size() % batchSize == 0) {

                updateJdbcDao.insertPartsRopqMonthlyData(j1BatchArgs);
                j1BatchArgs.clear();
            }
        }

        if (!j1BatchArgs.isEmpty()) { //There're some one left, continue to insert operation.

            updateJdbcDao.insertPartsRopqMonthlyData(j1BatchArgs);
            j1BatchArgs.clear();
        }

        j1BatchArgs = null;
    }

    private Object[] calMonthQty(Integer beginMonth
                                , Integer endMonth
                                , Map<String, ProductOrderResultSummaryBatchBO> firstMap
                                , Map<String, ProductOrderResultSummaryBatchBO> secondMap
                                , Map<String, BigDecimal> j1TotalMap
                                , Map<String, BigDecimal> j2TotalMap
                                , ProductOrderResultSummaryBatchBO proResultSummary
                                , String key
                                , String registerDate
                                , String firstOrderDate) {

        String j1Quantity   = CommonConstants.CHAR_BLANK;
        String j2Quantity   = CommonConstants.CHAR_BLANK;

        BigDecimal j1Total = (j1TotalMap != null) ? j1TotalMap.get(key) : null;
        BigDecimal j2Total = (j2TotalMap != null) ? j2TotalMap.get(key) : null;

        if (j1Total == null) {

            j1Total = BigDecimal.ZERO;
        }

        if (j2Total == null) {

            j2Total = BigDecimal.ZERO;
        }

        Object[] maps = new Object[16];
        int intMonthDate = endMonth;
        for (int i = beginMonth; i <= endMonth; i++) {

            String month = StringUtils.toString(i);
            String monthDate = "";
            if (month.length() == 1) {
                month = CommonConstants.CHAR_ZERO + month;
            }

            monthDate = StringUtils.toString(intMonthDate--);

            if (monthDate.length() == 1) {
            monthDate = CommonConstants.CHAR_ZERO + monthDate;
            }

            if (firstMap.containsKey(key)) {

                try {
                 // Get each month quantity
                 j1Quantity = BeanUtils.getProperty(firstMap.get(key),  "month" + month + "quantity");
                } catch (Exception e) {

                    String msg  = String.format("Get property '%s' value fail!","month" + month + "quantity");
                    log.error(msg, e);
                    throw new PJCustomException(msg);
                }

                try {
                    BeanUtils.setProperty(proResultSummary, "month" + monthDate + "quantity", j1Quantity);
                } catch (Exception e) {

                    String msg  = String.format("Set property '%s' value fail!","month" + month + "quantity");
                    log.error(msg, e);
                    throw new PJCustomException(msg);
                }
            }

            if (secondMap.containsKey(key)) {

                try {
                    // Get each month quantity
                    j2Quantity = BeanUtils.getProperty(secondMap.get(key), "month" + month + "quantity");
                } catch (Exception e) {

                    String msg  = String.format("Get property '%s' value fail!","month" + month + "quantity");
                    log.error(msg, e);
                    throw new PJCustomException(msg);
                }
            }

            // Sum each month quantity(j1Total, j2Total)
            j1Total = j1Total.add(NumberUtil.toBigDecimal(nullToCharZero(j1Quantity)));
            j2Total = j2Total.add(NumberUtil.toBigDecimal(nullToCharZero(j2Quantity)));
        }
        j1TotalMap.put(key, j1Total);
        j2TotalMap.put(key, j2Total);
        maps[2] = NumberUtil.toBigDecimal(nullToCharZero(proResultSummary.getMonth01quantity()));
        maps[3] = NumberUtil.toBigDecimal(nullToCharZero(proResultSummary.getMonth02quantity()));
        maps[4] = NumberUtil.toBigDecimal(nullToCharZero(proResultSummary.getMonth03quantity()));
        maps[5] = NumberUtil.toBigDecimal(nullToCharZero(proResultSummary.getMonth04quantity()));
        maps[6] = NumberUtil.toBigDecimal(nullToCharZero(proResultSummary.getMonth05quantity()));
        maps[7] = NumberUtil.toBigDecimal(nullToCharZero(proResultSummary.getMonth06quantity()));
        maps[8] = NumberUtil.toBigDecimal(nullToCharZero(proResultSummary.getMonth07quantity()));
        maps[9] = NumberUtil.toBigDecimal(nullToCharZero(proResultSummary.getMonth08quantity()));
        maps[10] = NumberUtil.toBigDecimal(nullToCharZero(proResultSummary.getMonth09quantity()));
        maps[11] = NumberUtil.toBigDecimal(nullToCharZero(proResultSummary.getMonth10quantity()));
        maps[12] = NumberUtil.toBigDecimal(nullToCharZero(proResultSummary.getMonth11quantity()));
        maps[13] = NumberUtil.toBigDecimal(nullToCharZero(proResultSummary.getMonth12quantity()));
        maps[14] = registerDate;
        maps[15] = firstOrderDate;
        maps[0] = j1TotalMap;
        maps[1] = j2TotalMap;

        return maps;
    }

    private String nullToCharZero(String string) {

        if (StringUtils.isBlankText(string)) {
            return CommonConstants.CHAR_ZERO;
        } else {
            return string;
        }
    }

    private String nullToCharOne(String string) {

        if (StringUtils.isBlankText(string)) {
            return CommonConstants.CHAR_ONE;
        } else {
            return string;
        }
    }

    private boolean nullObjectFlag(String total) {

        boolean flag = false;
        if (StringUtils.isBlankText(total)) {
            flag = true;
        }
        return flag;
    }

    private String nullObject(String total) {

        if (StringUtils.isBlankText(total)) {
            total = CommonConstants.CHAR_ZERO;
        }
        return total;
    }

    private boolean yearNoSale(BigDecimal j1
                              ,BigDecimal j2) {
        BigDecimal sum = j1.add(j2);
        boolean flag1 = false;
        if (NumberUtil.equals(sum, BigDecimal.ZERO)) {
            flag1 = true;
        }
        return flag1;
    }

    // GET THIS PRODUCT IN ALL SUM PERCENTAGE
    private BigDecimal productABC(BigDecimal sum1
                                , BigDecimal sum2) {

        BigDecimal percentage = NumberUtil.divide(sum1, sum2, 4, RoundingMode.HALF_EVEN);

        return percentage;
    }

    private String longToStr(Long value) {

        String returnValueString = "";
        if (value != null) {

            returnValueString = value.toString();
        }

        return returnValueString;
    }

    public int setSpecParameterValue(String siteId
                                    , String sysParamTypeId
                                    , String parameterValue) {

       return updateJdbcDao.setSpecParameterValue(siteId, sysParamTypeId, parameterValue);
    }

    public static String padCharLeft(String str, int len, char padChar) {

        if (str == null || str.length() == 0) {
            return str;
        }

        if (str.length() >= len) {
            return str;
        }

        StringBuffer sb = new StringBuffer(str);
        int max = len - str.length();

        for (int i = 0; i < max; i++) {
            sb.insert(0, padChar);
        }

        return sb.toString();
    }

    /**
     * Sort in descendly
     */
    private final static Comparator<CuProductInfoBatchBO> CMP_SORT_BY_J1TOTAL_DESC = new Comparator<CuProductInfoBatchBO>(){

        /**
         * @param o1
         * @param o2
         * @return
         */
        @Override
        public int compare(CuProductInfoBatchBO o1,
                CuProductInfoBatchBO o2) {

            BigDecimal d1 = (o1.getDoublevalue1() ==null) ? BigDecimal.ZERO : o1.getDoublevalue1();
            BigDecimal d2 = (o2.getDoublevalue1() ==null) ? BigDecimal.ZERO : o2.getDoublevalue1();
            int c = d1.compareTo(d2);
            return (c ==0) ? (0) :((c>0) ? -1 : 1); //Descend
        }

    };

    private Long toLong(String stringValue){

        return stringValue == null ? null: NumberUtil.toLong(stringValue);
    }
    /**
     * @param item
     * @return
     */
    private List<CuProductInfoBatchBO> sortByJ1TotalDesc(Entry<Long, List<CuProductInfoBatchBO>> item) {

        List<CuProductInfoBatchBO> orderlyList = item.getValue();
        Collections.sort(orderlyList, CMP_SORT_BY_J1TOTAL_DESC);
        return orderlyList;
    }
}