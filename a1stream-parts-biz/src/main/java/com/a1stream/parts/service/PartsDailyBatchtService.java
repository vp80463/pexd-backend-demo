/******************************************************************************/
/* SYSTEM     : Stream                                                        */
/*                                                                            */
/* SUBSYSTEM  : Xm03                                                          */
/******************************************************************************/
package com.a1stream.parts.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.a1stream.common.bo.PartsAutoPurchaseOrderBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.constants.MstCodeConstants.PurchaseOrderStatus;
import com.a1stream.common.constants.MstCodeConstants.SystemParameterType;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.CostType;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.constants.PJConstants.OrgRelationType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.PurchaseMethodType;
import com.a1stream.common.constants.PJConstants.PurchaseOrderPriorityType;
import com.a1stream.common.constants.PJConstants.SerialproductStockStatus;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.common.logic.IfsPrepareMessageHeaderLogic;
import com.a1stream.common.manager.CallNewIfsManager;
import com.a1stream.common.manager.GenerateNoManager;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.common.manager.PurchaseOrderManager;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.common.utils.TimerUtil;
import com.a1stream.domain.batchdao.BatchSelectJdbcDao;
import com.a1stream.domain.batchdao.BatchUpdateJdbcDao;
import com.a1stream.domain.bo.batch.BSProductStockStatusBO;
import com.a1stream.domain.bo.batch.PartsPurchaseOrderModelBO;
import com.a1stream.domain.bo.batch.PartsPurchaseOrderModelXmlBO;
import com.a1stream.domain.bo.batch.PartsRecommendationBO;
import com.a1stream.domain.bo.batch.ProductOrderResultSummaryBatchBO;
import com.a1stream.domain.entity.MstProduct;
import com.a1stream.domain.entity.MstProductCategory;
import com.a1stream.domain.entity.OrganizationRelation;
import com.a1stream.domain.entity.PurchaseOrder;
import com.a1stream.domain.entity.PurchaseOrderItem;
import com.a1stream.domain.entity.PurchaseRecommendation;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstProductCategoryRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.OrganizationRelationRepository;
import com.a1stream.domain.repository.PurchaseOrderItemRepository;
import com.a1stream.domain.repository.PurchaseOrderRepository;
import com.a1stream.domain.repository.PurchaseRecommendationRepository;
import com.a1stream.domain.repository.ReorderGuidelineRepository;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.PurchaseOrderItemVO;
import com.a1stream.domain.vo.PurchaseOrderVO;
import com.a1stream.domain.vo.PurchaseRecommendationVO;
import com.alibaba.fastjson.JSON;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.IdUtils;
import com.ymsl.solid.base.util.StringUtils;
import com.ymsl.solid.base.util.uuid.SnowflakeIdWorker;

import jakarta.annotation.Resource;

@Service
public class PartsDailyBatchtService {

    @Resource
    private BatchUpdateJdbcDao                  updateJdbcDao;
    @Resource
    private BatchSelectJdbcDao                  selectJdbcDao;
    @Resource
    private OrganizationRelationRepository      organizationRelationRepository;
    @Resource
    private PurchaseRecommendationRepository    purchaseRecommendationRepository;
    @Resource
    private MstProductRepository                mstProductRepository;
    @Resource
    private MstFacilityRepository               mstFacilityRepository;
    @Resource
    private MstProductCategoryRepository        mstProductCategoryRepository;
    @Resource
    private GenerateNoManager                   generateNoManager;
    @Resource
    private InventoryManager                    inventoryManager;
    @Resource
    private PurchaseOrderManager                purchaseOrderManager;
    @Resource
    private PurchaseOrderRepository             purchaseOrderRepo;
    @Resource
    private PurchaseOrderItemRepository         poItemRepo;
    @Resource
    private ReorderGuidelineRepository          reorderGuidelineRepository;
    @Resource
    private BatchSelectJdbcDao                  partsBatchService;
    @Resource
    private CallNewIfsManager                   callNewIfsManager;
    @Resource
    private IfsPrepareMessageHeaderLogic        ifsPrepareMessageHeaderLogic;

    @Value("${ifs.request.url}")
    private String ifsRequestUrl;
    /** Initialization */
    /** Log */
    private Log log = LogFactory.getLog(this.getClass());


    public void doDailyProcess(String siteId
                             , String processDate
                             , String dateTo
                             , String inventoryFlag) {

        log.info(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER)+" 1 :Start SalesResultCollect");
        this.doSalesResultCollect(siteId, processDate);

        log.info(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER)+" 2 :Start PartsAutoPurchaseOrde");
        this.doPartsAutoPurchaseOrder(siteId, processDate);

        // Judge month end or not
        if (StringUtils.equals(inventoryFlag, CommonConstants.CHAR_ONE) && StringUtils.equals(processDate, dateTo)) {

            String lastYYMM   =   DateUtils.date2String(DateUtils.addDay(DateUtils.string2Date(processDate, DateUtils.FORMAT_YMD_NODELIMITER),1)
                                            ,DateUtils.FORMAT_YMD_NODELIMITER).substring(0, 6);

            //SD beginning stock collect
            log.info(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER)+" 3 :Start SD beginning stock collect ");
            this.doSDBeginningStockCollectCalculate(siteId, processDate);

            //SP beginning stock collect
            log.info(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER)+" 4 :Start SP beginning stock collect ");
            this.doBeginningStockCollectCalculate(siteId, processDate);

            // Set last monthly batch date
            int monthlyUpdateCount = this.setSpecParameterValue(siteId
                                                              , SystemParameterType.LASTMONTHLYBATCH
                                                              , processDate);

            if (monthlyUpdateCount == 0) {


                SnowflakeIdWorker snowflakeIdWorker = IdUtils.getSnowflakeIdWorker();
                Long systemParameterId = snowflakeIdWorker.nextId();

                this.insertParameterValue(siteId
                                        , systemParameterId
                                        , SystemParameterType.LASTMONTHLYBATCH
                                        , processDate
                                        , CommonConstants.CHAR_BATCH_USER_ID);

            }

            int currentmonthUpdateCount = this.setSpecParameterValue(siteId
                                                                   , SystemParameterType.CURRENTFINANCEMONTH
                                                                   , lastYYMM);

            if (currentmonthUpdateCount == 0) {

                SnowflakeIdWorker snowflakeIdWorker = IdUtils.getSnowflakeIdWorker();
                Long systemParameterId = snowflakeIdWorker.nextId();

                this.insertParameterValue(siteId
                                        , systemParameterId
                                        , SystemParameterType.CURRENTFINANCEMONTH
                                        , lastYYMM
                                        , CommonConstants.CHAR_BATCH_USER_ID);
            }

        } else {

             // Set last daily batch date
            int dailyUpdateCount = this.setSpecParameterValue(siteId
                                                            , SystemParameterType.LASTDAILYBATCH
                                                            , processDate);

            if (dailyUpdateCount == 0) {

                SnowflakeIdWorker snowflakeIdWorker = IdUtils.getSnowflakeIdWorker();
                Long systemParameterId = snowflakeIdWorker.nextId();

                this.insertParameterValue(siteId
                                        , systemParameterId
                                        , SystemParameterType.LASTDAILYBATCH
                                        , processDate
                                        , CommonConstants.CHAR_BATCH_USER_ID);
            }
        }
    }

    public void doSalesResultCollect(String siteId
                                   , String collectDate) {

        String targetYear = collectDate.substring(0, 4);
        String collectMonth = collectDate.substring(4, 6);

        Map<String, ProductOrderResultSummaryBatchBO> productMap = this.getSalesResultProductList(siteId
                                                                                                , collectDate);

        Map<String, String> firstOrderDateMap = selectJdbcDao.getPartsRopqParameter(siteId);

        Map<String, Long> productOrderResultSummaryMap = selectJdbcDao.getProductOrderResultSummary(siteId, targetYear);
        log.info("CollectDate: " + collectDate + ", ProductOrderResultSummary " + productMap.size() + " records found");

        List<Object[]> pfInsertBatchArgs = new ArrayList<Object[]>();
        List<Object[]> pfUpdateBatchArgs = new ArrayList<Object[]>();
        List<Object[]> porsInsertBatchArgs = new ArrayList<Object[]>();
        List<Object[]> porsUpdateBatchArgs = new ArrayList<Object[]>();
        String firstOrderDate = "";
        String productId = "";
        String facilityId = "";
        SnowflakeIdWorker snowflakeIdWorker = IdUtils.getSnowflakeIdWorker();
        for (String key : productMap.keySet()) {

            productId = key.split(CommonConstants.CHAR_COLON)[0];
            facilityId = key.split(CommonConstants.CHAR_COLON)[1];

            Long productOrderResultSummaryUid =
                productOrderResultSummaryMap.get(productId
                                               + CommonConstants.CHAR_COLON
                                               + facilityId);

            if (productOrderResultSummaryUid == null) {

                porsInsertBatchArgs.add(new Object[]{snowflakeIdWorker.nextId()
                                                   , siteId
                                                   , targetYear
                                                   , toLong(facilityId)
                                                   , toLong(productId)
                                                   , NumberUtil.toBigDecimal(productMap.get(key).getOriginalquantity())
                                                   , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                                                   , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                                                   , CommonConstants.ROPQBATCH_UPDATE_PROGRAM});
            } else {

                porsUpdateBatchArgs.add(new Object[]{NumberUtil.toBigDecimal(productMap.get(key).getOriginalquantity())
                                                   , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                                                   , productOrderResultSummaryUid});
            }

            //first order date
            firstOrderDate = firstOrderDateMap.get(key);
            if (firstOrderDate == null) {

                pfInsertBatchArgs.add(new Object[]{snowflakeIdWorker.nextId()
                                                 , siteId
                                                 , toLong(facilityId)
                                                 , toLong(productId)
                                                 , collectDate
                                                 , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                                                 , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                                                 , CommonConstants.ROPQBATCH_UPDATE_PROGRAM});
            } else if (StringUtils.isBlankText(firstOrderDate)) {

                pfUpdateBatchArgs.add(new Object[]{collectDate
                                                 , CommonConstants.ROPQBATCH_UPDATE_PROGRAM
                                                 , toLong(productId)
                                                 , toLong(facilityId)
                                                 , siteId});
            }
        }

        if (pfInsertBatchArgs.size() != 0) {

            updateJdbcDao.insertPartsRopqParameter(pfInsertBatchArgs);
        }

        if (pfUpdateBatchArgs.size() != 0) {

            updateJdbcDao.updatePartsRopqParameter(pfUpdateBatchArgs);
        }

        if (porsInsertBatchArgs.size() != 0) {

            updateJdbcDao.insertProductOrderResultSummaryData(collectMonth, porsInsertBatchArgs);
        }

        if (porsUpdateBatchArgs.size() != 0) {

            updateJdbcDao.updateProductOrderResultSummaryData(collectMonth, porsUpdateBatchArgs);
        }

        productMap = null;
        firstOrderDateMap = null;
        productOrderResultSummaryMap = null;
        pfInsertBatchArgs = null;
        pfUpdateBatchArgs = null;
        porsInsertBatchArgs = null;
        porsUpdateBatchArgs = null;
    }

    public void doBeginningStockCollectCalculate(String siteId
                                               , String processDate) {

        log.info("********Start SP Beginning Stock Collection********");
        StopWatch timer = new StopWatch();
        timer.start();
        StopWatch taskTimer = new StopWatch();
        String startDate  = DateUtils.date2String(DateUtils.addDay(DateUtils.string2Date(processDate, DateUtils.FORMAT_YMD_NODELIMITER),1)
                 ,DateUtils.FORMAT_YMD_NODELIMITER);
        // initial Map and List
        List<BSProductStockStatusBO> productStockStatus = new ArrayList<BSProductStockStatusBO>();

        // Set Map and List
        List<String> typeIds = new ArrayList<>();
        typeIds.add(SpStockStatus.ONHAND_QTY.getCodeDbid());
        typeIds.add(SpStockStatus.ALLOCATED_QTY.getCodeDbid());
        typeIds.add(SpStockStatus.ONPICKING_QTY.getCodeDbid());
        typeIds.add(SpStockStatus.ONTRANSFER_IN_QTY.getCodeDbid());
        typeIds.add(SpStockStatus.ONCANVASSING_QTY.getCodeDbid());
        typeIds.add(SpStockStatus.ONSERVICE_QTY.getCodeDbid());
        typeIds.add(SpStockStatus.ONFROZEN_QTY.getCodeDbid());
        typeIds.add(SpStockStatus.ONRECEIVING_QTY.getCodeDbid());

        productStockStatus = selectJdbcDao.getProductStockStatus(siteId, CostType.AVERAGE_COST, ProductClsType.PART.getCodeDbid(),null, typeIds);

        Map<String,Object[]> resultMap = new HashMap<String,Object[]>();
        log.info(productStockStatus.size() + " SP Beginning Stock Collect to be calculated");

        for (BSProductStockStatusBO productStockStatusModel : productStockStatus) {

            Object[] resultObject = new Object[5];

            resultObject[0] = NumberUtil.toBigDecimal(nullObject(productStockStatusModel.getSumquantity()));
            resultObject[1] = NumberUtil.toBigDecimal(nullObject(productStockStatusModel.getCost()));
            resultObject[2] = productStockStatusModel.getProductcd();
            resultObject[3] = productStockStatusModel.getProductnm();
            resultObject[4] = ProductClsType.PART.getCodeDbid();

            resultMap.put(productStockStatusModel.getFacilityid() + CommonConstants.CHAR_COLON
                                     + productStockStatusModel.getProductid(), resultObject);
        }

        TimerUtil.startTask(taskTimer, "Insert rows into inventory_transaction with size:%d", resultMap.size());
        insertInventoryTransaction(siteId, startDate, startDate, resultMap);
        TimerUtil.endTask(taskTimer,log.toString());

        if(productStockStatus !=null) {
            productStockStatus.clear();
            productStockStatus = null;
        }
        if(resultMap !=null) {
            resultMap.clear();
            resultMap = null;
        }

        timer.stop();
        if(log.isInfoEnabled()) {
            log.info("********End of SP Beginning Stock Process, during time: " + timer.getTotalTimeMillis() + "ms********");
        }
    }

    public void doSDBeginningStockCollectCalculate(String siteId
                                                 , String processDate) {

        log.info("********Start SD Beginning Stock Collection********");
        StopWatch timer = new StopWatch();
        timer.start();
        StopWatch taskTimer = new StopWatch();
        String startDate  = DateUtils.date2String(DateUtils.addDay(DateUtils.string2Date(processDate, DateUtils.FORMAT_YMD_NODELIMITER),1)
                                                      ,DateUtils.FORMAT_YMD_NODELIMITER);
        // initial Map and List
        List<BSProductStockStatusBO> productStockStatus = new ArrayList<BSProductStockStatusBO>();
        // Set Map and List
        List<String> statusIds = new ArrayList<>();
        statusIds.add(SerialproductStockStatus.ONHAND);
//        statusIds.add(SdStockStatus.ONHAND_QTY.getCodeDbid());
//        statusIds.add(SdStockStatus.ALLOCATED_QTY.getCodeDbid());
//        statusIds.add(SdStockStatus.SHIPPING_REQUEST.getCodeDbid());
//        statusIds.add(SdStockStatus.ONSHIPPING.getCodeDbid());
//        statusIds.add(SdStockStatus.ONTRANSFER_IN_QTY.getCodeDbid());
        List<Long> seriaSet = selectJdbcDao.getSerializedProductId(siteId,statusIds);
        // Set Map and List
        productStockStatus = selectJdbcDao.getSerializdeProduct(siteId, null ,seriaSet);

        Map<String,Object[]> resultMap = new HashMap<String,Object[]>();
        log.info(productStockStatus.size() + " SD Beginning Stock Collect to be calculated");

        for (BSProductStockStatusBO productStockStatusModel : productStockStatus) {

            Object[] resultObject = new Object[5];

            resultObject[0] = NumberUtil.toBigDecimal(nullObject(productStockStatusModel.getSumquantity()));
            resultObject[1] = NumberUtil.toBigDecimal(nullObject(productStockStatusModel.getCost()));
            resultObject[2] = productStockStatusModel.getProductcd();
            resultObject[3] = productStockStatusModel.getProductnm();
            resultObject[4] = ProductClsType.GOODS.getCodeDbid();

            resultMap.put(productStockStatusModel.getFacilityid() + CommonConstants.CHAR_COLON
                                     + productStockStatusModel.getProductid(), resultObject);
        }

        TimerUtil.startTask(taskTimer, "Insert rows into inventory_transaction with size:%d", resultMap.size());
        insertInventoryTransaction(siteId, startDate, startDate, resultMap);
        TimerUtil.endTask(taskTimer, log.toString());

        if(productStockStatus !=null) {
            productStockStatus.clear();
            productStockStatus = null;
        }
        if(resultMap !=null) {
            resultMap.clear();
            resultMap = null;
        }
    }

    private void insertInventoryTransaction(String siteId
                                          , String processDate
                                          , String startDate
                                          , Map<String, Object[]> resultMap) {
        List<Object[]> batchArgs = new ArrayList<Object[]>();
        StopWatch taskTimer = new StopWatch();
        int batchIdx = 1;
        int count = 0;
        TimerUtil.startTask(taskTimer, "Insert rows with batch %d", batchIdx);
        SnowflakeIdWorker snowflakeIdWorker = IdUtils.getSnowflakeIdWorker();
        for (String key : resultMap.keySet()) {

            String[] proFac = key.split(CommonConstants.CHAR_COLON);
            Object[] map = resultMap.get(key);

            batchArgs.add(new Object[]{snowflakeIdWorker.nextId()
            , siteId
            , processDate
            , CommonConstants.CHAR_010101
            , toLong(proFac[1])
            , map[2]
            , map[3]
            , toLong(proFac[0])
            , map[0]
            , map[1]
            , InventoryTransactionType.BEGINNINGMONTHSTOCK.getCodeDbid()
            , InventoryTransactionType.BEGINNINGMONTHSTOCK.getCodeData1()
            , map[4]
            , CommonConstants.CHAR_BATCH_USER_ID
            , CommonConstants.CHAR_BATCH_USER_ID
            , CommonConstants.CHAR_BATCH_USER_ID});

            if( (++count) % CommonConstants.DB_OP_BATCH_SIZE == 0) {

                updateJdbcDao.insertInvTransaction(batchArgs);
                batchArgs.clear();
            }
        }

        if (!batchArgs.isEmpty()) { //There're some one left, continue to insert operation.

            updateJdbcDao.insertInvTransaction(batchArgs);
            batchArgs.clear();
        }

        TimerUtil.endTask(taskTimer, log.toString());

        batchArgs.clear();
        batchArgs = null;
    }

    public void insertParameterValue(String siteId
                                  , Long systemParameterId
                                  , String sysParamTypeId
                                  , String parameterValue
                                  , String updateProgram) {

        updateJdbcDao.insertSpecParameterValue(siteId, systemParameterId, sysParamTypeId, parameterValue, updateProgram);;
    }

    private Map<String, ProductOrderResultSummaryBatchBO> getSalesResultProductList(String siteId,String collectDate) {

        List<ProductOrderResultSummaryBatchBO> salesResultSP = selectJdbcDao.getSalesResultProductListSP(siteId, collectDate);
        List<ProductOrderResultSummaryBatchBO> salesResultSV = selectJdbcDao.getSalesResultProductListSV(siteId, collectDate);

        //// 将可能为null的List salesResultSP转换成map
        Map<String, ProductOrderResultSummaryBatchBO> salesResultMap = Optional.ofNullable(salesResultSP)
                                                                      .orElseGet(Collections::emptyList)
                                                                      .stream()
                                                                      .collect(Collectors.toMap(item -> (item.getProductid()+CommonConstants.CHAR_COLON+item.getDeliveryfacility()),
                                                                              item -> item));

//        for (ProductOrderResultSummaryBatchBO orderResult : salesResultSP) {
//
//            if (map.containsKey(orderResult.getProductid()+CommonConstants.CHAR_COLON+orderResult.getDeliveryfacility())) {
//                map.get(orderResult.getProductid()+CommonConstants.CHAR_COLON+orderResult.getDeliveryfacility())
//                   .setOriginalquantity(StringUtils.toString(NumberUtil.toBigDecimal(map.get(orderResult.getProductid()+CommonConstants.CHAR_COLON+orderResult.getDeliveryfacility()).getOriginalquantity())
//                           .add(NumberUtil.toBigDecimal(orderResult.getOriginalquantity()))));
//            } else {
//                map.put(orderResult.getProductid()+CommonConstants.CHAR_COLON+orderResult.getDeliveryfacility()
//                        , orderResult);
//            }
//        }

        for (ProductOrderResultSummaryBatchBO orderResult : salesResultSV) {

            if (salesResultMap.containsKey(orderResult.getProductid()+CommonConstants.CHAR_COLON+orderResult.getFacilityid())) {
                salesResultMap.get(orderResult.getProductid()+CommonConstants.CHAR_COLON+orderResult.getDeliveryfacility())
                   .setOriginalquantity(StringUtils.toString(NumberUtil.toBigDecimal(salesResultMap.get(orderResult.getProductid()+CommonConstants.CHAR_COLON+orderResult.getDeliveryfacility()).getOriginalquantity())
                           .add(NumberUtil.toBigDecimal(orderResult.getOriginalquantity()))));
            } else {
                salesResultMap.put(orderResult.getProductid()+CommonConstants.CHAR_COLON+orderResult.getDeliveryfacility()
                        , orderResult);
            }
        }

        return salesResultMap;
    }

    public void doPartsAutoPurchaseOrder(String siteId
                                       , String collectDate) {

        List<MstFacilityVO> facilityVOList = mstFacilityRepository.getFacilityByDateList(siteId, collectDate);
        Set<Long> supplierIdSet = this.getSupplierList(siteId);
        List<Long> purchaseOrderIdList = new ArrayList<>();
        String autoPruchaseFlag = selectJdbcDao.getSystemParDate(siteId, SystemParameterType.AUTOPOFLAG);
        String purchaseMethodType = PurchaseMethodType.POFOQ.getCodeDbid();
        List<PartsPurchaseOrderModelBO> returnRequsts = new ArrayList<>();
        for (MstFacilityVO facilityVo : facilityVOList) {

            long pointId = facilityVo.getFacilityId();
            Map<String,List<PurchaseOrderItemVO>> registerOrderItemVOMap = new HashMap<>();
            List<PurchaseOrderVO> poVoList = new ArrayList<>();
            List<PurchaseOrderItemVO> poItemHoVoList = new ArrayList<>();
            List<PurchaseOrderItemVO> poItemRoVoList = new ArrayList<>();
            for (Long supplierId : supplierIdSet) {

                List<Long> purchaseRecommendIds = new ArrayList<>();
                Map<Long,PartsAutoPurchaseOrderBO> partsAutoPuchaseDetailMap = new HashMap<>();
                Map<String,List<MstProductVO>> productMapHo = new HashMap<>();
                Map<String,List<MstProductVO>> productMapRo = new HashMap<>();

                //1. Do purchase recommend operation.
                this.doPurchaseRecommend(siteId, pointId, supplierId);
                List<PartsAutoPurchaseOrderBO> detailModel = this.findProductListByStockPointAndSupplier(siteId, pointId, supplierId);

                //Check orderQuantity enable to create order info
                boolean eoFlag = false;
                boolean roFlag = false;
                Set<Long> productIdSetForHo = new HashSet<>();
                Set<Long> productIdSetForRo = new HashSet<>();
                for(PartsAutoPurchaseOrderBO member: detailModel){

                    BigDecimal eoQty = member.getEoQuantity();
                    BigDecimal roQty = member.getRoQuantity();
                    if(eoQty.signum() == 1){

                        productIdSetForHo.add(member.getProductId());
                        eoFlag = true;
                    }
                    if(roQty.signum() == 1){

                        productIdSetForRo.add(member.getProductId());
                        roFlag = true;
                    }

                    partsAutoPuchaseDetailMap.put(member.getProductId(), member);
                }

                //get HO lager group
                if (productIdSetForHo != null && productIdSetForHo.size() > 0 ) {

                    productMapHo = this.getPartsLargeGroup(productIdSetForHo);
                } else {

                    productMapHo = null;
                }

                //get RO lager group
                if (productIdSetForRo != null && productIdSetForRo.size() > 0 ) {

                    productMapRo = this.getPartsLargeGroup(productIdSetForRo);
                } else {

                    productMapRo = null;
                }

                //Create Order Info (Regular)
                if(eoFlag == true){

                    for (String largerGroup : productMapHo.keySet()) {

                        BigDecimal totalQty = CommonConstants.BIGDECIMAL_ZERO;
                        BigDecimal totalAmount = CommonConstants.BIGDECIMAL_ZERO;
                        String orderType = PurchaseOrderPriorityType.POHO.getCodeDbid();

                        PurchaseOrderVO purchaseOrderVO = PurchaseOrderVO.create();
                        purchaseOrderVO.setSiteId(siteId);
                        purchaseOrderVO.setOrderPriorityType(orderType);
                        purchaseOrderVO.setFacilityId(pointId);
                        purchaseOrderVO.setSupplierId(supplierId);
                        purchaseOrderVO.setOrderMethodType(purchaseMethodType);
                        purchaseOrderVO.setOrderDate(collectDate);
                        purchaseOrderVO.setProductClassification(ProductClsType.PART.getCodeDbid());
                        purchaseOrderVO.setDeliverPlanDate(collectDate);
                        purchaseOrderVO.setFacilityCd(facilityVo.getFacilityCd());
                        purchaseOrderVO.setFacilityNm(facilityVo.getFacilityNm());
                        purchaseOrderVO = this.setOrderNo(purchaseOrderVO, largerGroup);

                        Integer eoSeqNo = CommonConstants.INTEGER_ZERO;

                        for (MstProductVO productVO : productMapHo.get(largerGroup)) {

                            PartsAutoPurchaseOrderBO member = partsAutoPuchaseDetailMap.get(productVO.getProductId());

                            if(member.getEoQuantity().signum() == 1){

                                BigDecimal eoQty = member.getEoQuantity();

                                if(eoQty.signum() == 1 ) { //eoQty>0

                                    eoSeqNo = eoSeqNo + 1;
                                    PurchaseOrderItemVO poitemvo= createItems(siteId, purchaseOrderVO.getPurchaseOrderId(), member, eoQty, eoSeqNo);
                                    totalQty = NumberUtil.add(poitemvo.getOrderQty(), totalQty);
                                    totalAmount = NumberUtil.add(poitemvo.getAmount(), totalAmount);
                                    if (StringUtils.equals(autoPruchaseFlag, CommonConstants.CHAR_ONE)) {
                                        poitemvo.setOnPurchaseQty(poitemvo.getActualQty());
                                    }
                                    poItemHoVoList.add(poitemvo);
                                    //set ifs data
                                    returnRequsts.add(setPODetailModelData(purchaseOrderVO, poitemvo));
                                }

                                purchaseRecommendIds.add(member.getPurchaseRecommendationEoId());
                            }
                        }
                        purchaseOrderVO.setTotalActualAmt(totalAmount);
                        purchaseOrderVO.setTotalActualQty(totalQty);
                        purchaseOrderVO.setTotalQty(totalQty);
                        purchaseOrderVO.setTotalAmount(totalAmount);

                        //Register po.
                        purchaseOrderVO.setOrderStatus(PurchaseOrderStatus.SPWAITINGISSUE);
                        purchaseOrderIdList.add(purchaseOrderVO.getPurchaseOrderId());
                        poVoList.add(purchaseOrderVO);
                    }
                }

                //Create Order Info (Regular)
                if(roFlag == true){

                    for (String largerGroup : productMapRo.keySet()) {

                        BigDecimal totalQty = CommonConstants.BIGDECIMAL_ZERO;
                        BigDecimal totalAmount = CommonConstants.BIGDECIMAL_ZERO;
                        String orderType = PurchaseOrderPriorityType.PORO.getCodeDbid();
                        PurchaseOrderVO purchaseOrderVO = PurchaseOrderVO.create();
                        purchaseOrderVO.setSiteId(siteId);
                        purchaseOrderVO.setOrderPriorityType(orderType);
                        purchaseOrderVO.setSupplierId(supplierId);
                        purchaseOrderVO.setOrderMethodType(purchaseMethodType);
                        purchaseOrderVO.setOrderDate(collectDate);
                        purchaseOrderVO.setProductClassification(ProductClsType.PART.getCodeDbid());
                        purchaseOrderVO.setDeliverPlanDate(collectDate);
                        purchaseOrderVO.setFacilityCd(facilityVo.getFacilityCd());
                        purchaseOrderVO = this.setOrderNo(purchaseOrderVO, largerGroup);

                        Integer roSeqNo = CommonConstants.INTEGER_ZERO;
                        for (MstProductVO productVO : productMapRo.get(largerGroup)) {

                            PartsAutoPurchaseOrderBO member = partsAutoPuchaseDetailMap.get(productVO.getProductId());
                            if(member.getRoQuantity().signum() == 1){

                                BigDecimal roQty = member.getRoQuantity();

                                if(roQty.signum() == 1 ) { //roQty>0

                                    roSeqNo = roSeqNo + 1;
                                    PurchaseOrderItemVO poitemvo = createItems(siteId, purchaseOrderVO.getPurchaseOrderId(), member, roQty, roSeqNo);
                                    totalQty = NumberUtil.add(poitemvo.getOrderQty(), totalQty);
                                    totalAmount = NumberUtil.add(poitemvo.getAmount(), totalAmount);
                                    if (StringUtils.equals(autoPruchaseFlag, CommonConstants.CHAR_ONE)) {
                                        poitemvo.setOnPurchaseQty(poitemvo.getActualQty());
                                    }
                                    poItemRoVoList.add(poitemvo);

                                    //set ifs data
                                    returnRequsts.add(setPODetailModelData(purchaseOrderVO, poitemvo));
                                }

                                purchaseRecommendIds.add(member.getPurchaseRecommendationRoId());
                            }
                        }
                        purchaseOrderVO.setTotalActualAmt(totalAmount);
                        purchaseOrderVO.setTotalActualQty(totalQty);
                        purchaseOrderVO.setTotalQty(totalQty);
                        purchaseOrderVO.setTotalAmount(totalAmount);

                        //Register po.
                        purchaseOrderVO = this.setOrderNo(purchaseOrderVO, largerGroup);
                        purchaseOrderVO.setOrderStatus(PurchaseOrderStatus.SPWAITINGISSUE);
                        purchaseOrderIdList.add(purchaseOrderVO.getPurchaseOrderId());
                        poVoList.add(purchaseOrderVO);
                    }
                }

                this.doRecommendationDeletion(purchaseRecommendIds);
            }

            //save order
            purchaseOrderRepo.saveInBatch(BeanMapUtils.mapListTo(poVoList, PurchaseOrder.class));
            if (poItemHoVoList.size() > 0) {
                poItemRepo.saveInBatch(BeanMapUtils.mapListTo(poItemHoVoList, PurchaseOrderItem.class));
                registerOrderItemVOMap.put(PurchaseOrderPriorityType.POHO.getCodeDbid(), poItemHoVoList);
            }
            if (poItemRoVoList.size() > 0) {
                poItemRepo.saveInBatch(BeanMapUtils.mapListTo(poItemRoVoList, PurchaseOrderItem.class));
                registerOrderItemVOMap.put(PurchaseOrderPriorityType.PORO.getCodeDbid(), poItemRoVoList);
            }
            // issue or not
            if (StringUtils.equals(autoPruchaseFlag, CommonConstants.CHAR_ONE) && purchaseOrderIdList.size() > 0) {

                purchaseOrderManager.doPurchaseOrderIssue(purchaseOrderIdList,null);
                for (String orderPriorityType : registerOrderItemVOMap.keySet()) {

                    inventoryManager.doPurchaseOrderRegister(orderPriorityType
                                                            ,registerOrderItemVOMap.get(orderPriorityType)
                                                            ,pointId
                                                            ,siteId);
                }
            }
        }

        //send po ifs
        if (returnRequsts.size() > 0) {
            this.exportPartReturnRequstFile(returnRequsts);
        }
    }

    public void doPurchaseRecommend(String siteId, Long stockPointId, Long supplierId) {

        // 1. Delete this dealer facility relate recommendation records.
        List<PurchaseRecommendation> prs = purchaseRecommendationRepository.findBySiteIdAndFacilityIdAndOrganizationId(siteId, stockPointId, supplierId);
        purchaseRecommendationRepository.deleteAllInBatch(prs);

        // 2. Get the lines: 0:product,1:roq, 2:rop, 3:StatusTypeId, 4:Quantity of this
        // status type,5.productId
        String targetDate  = partsBatchService.getSystemParDate(siteId, SystemParameterType.LASTDAILYBATCH);
        String processDate = DateUtils.date2String(DateUtils.addDay(DateUtils.string2Date(targetDate, DateUtils.FORMAT_YMD_NODELIMITER),1)
                                                                    ,DateUtils.FORMAT_YMD_NODELIMITER);
        List<PartsRecommendationBO> lines = reorderGuidelineRepository.getReorderGuidelineByConditions(stockPointId, siteId, processDate);

        // 3.
        int lineCount = lines.size();
        boolean isEndOfLines = false;
        BigDecimal onHandQty = BigDecimal.ZERO;
        BigDecimal onReceivingQty = BigDecimal.ZERO;
        BigDecimal onEoPurchaseQty = BigDecimal.ZERO;
        BigDecimal onRoPurchaseQty = BigDecimal.ZERO;
        BigDecimal onHoPurchaseQty = BigDecimal.ZERO;
        // Add by cxf for whole sale point
        BigDecimal onWoPurchaseQty = BigDecimal.ZERO;
        BigDecimal onTransferQty = BigDecimal.ZERO;
        BigDecimal onBoQty = BigDecimal.ZERO;
        BigDecimal onService = BigDecimal.ZERO;
        BigDecimal onCanvassing = BigDecimal.ZERO;
        /* BigDecimal onAllocateQty = BigDecimal.ZERO; */
        List<PurchaseRecommendationVO> newRecommends = new ArrayList<>();

        int lineIdx = 0;
        for (PartsRecommendationBO partsRecommendation : lines) {

            Long currentProductId = partsRecommendation.getProductId();
            BigDecimal roq = partsRecommendation.getReorderQty();
            BigDecimal rop = partsRecommendation.getReorderPoint();
            String stockStatusTypeId = partsRecommendation.getProductStockStatusType();
            BigDecimal statusQty = partsRecommendation.getQuantity();
            BigDecimal productPurchaseLotQuantity = partsRecommendation.getPurLotQty();
            BigDecimal productMonthSummary = partsRecommendation.getMonthQuantity();

            // New a product
            MstProductVO currentProduct = new MstProductVO();
            currentProduct.setProductId(currentProductId);
            currentProduct.setPurLotSize(productPurchaseLotQuantity);

            // Set the quantity by status type.
            if (SpStockStatus.ONHAND_QTY.getCodeDbid().equals(stockStatusTypeId)) {
                onHandQty = statusQty;
            } else if (SpStockStatus.BO_QTY.getCodeDbid().equals(stockStatusTypeId)) {
                onBoQty = statusQty;
            } else if (SpStockStatus.ONRECEIVING_QTY.getCodeDbid().equals(stockStatusTypeId)) {
                onReceivingQty = statusQty;
            } else if (SpStockStatus.EO_ONPURCHASE_QTY.getCodeDbid().equals(stockStatusTypeId)) {
                onEoPurchaseQty = statusQty;
            } else if (SpStockStatus.RO_ONPURCHASE_QTY.getCodeDbid().equals(stockStatusTypeId)) {
                onRoPurchaseQty = statusQty;
            }
            // Add by cxf for whole sale point
            else if (SpStockStatus.WO_ONPURCHASE_QTY.getCodeDbid().equals(stockStatusTypeId)) {
                onWoPurchaseQty = statusQty;
            } else if (SpStockStatus.HO_ONPURCHASE_QTY.getCodeDbid().equals(stockStatusTypeId)) {
                onHoPurchaseQty = statusQty;
            } else if (SpStockStatus.ONTRANSFER_IN_QTY.getCodeDbid().equals(stockStatusTypeId)) {
                onTransferQty = statusQty;
            } else if (SpStockStatus.ONSERVICE_QTY.getCodeDbid().equals(stockStatusTypeId)) {
                onService = statusQty;
            } else if (SpStockStatus.ONCANVASSING_QTY.getCodeDbid().equals(stockStatusTypeId)) {
                onCanvassing = statusQty;
            }

            //
            isEndOfLines = (lineIdx + 1) == lineCount;
            PartsRecommendationBO partsRecommendationLast = isEndOfLines ? null : lines.get(lineIdx+1);
            lineIdx++;
            if (isEndOfLines || !NumberUtil.equals(partsRecommendation.getProductId(), partsRecommendationLast.getProductId())) {// Is end of group

                // logger.info("Process group {}. product[{}]", groupIndx, currentProductId);
                // Make a recommend for product in guide line.
                newRecommends.addAll(this.makeRecommendsForProduct(currentProduct,
                                                                   stockPointId,
                                                                   supplierId,
                                                                   siteId,
                                                                   rop,
                                                                   roq,
                                                                   onHandQty,
                                                                   onReceivingQty,
                                                                   onEoPurchaseQty,
                                                                   onRoPurchaseQty,
                                                                   onHoPurchaseQty,
                                                                   onWoPurchaseQty, // Add by cxf for whole sale point
                                                                   onTransferQty,
                                                                   onBoQty,
                                                                   onService,
                                                                   onCanvassing,
                                                                   productMonthSummary == null ? BigDecimal.ZERO : productMonthSummary)// When
                                                                                                                                       // productMonthSummary
                                                                                                                                       // is null or
                                                                                                                                       // equal 0
                );
                // Reset qty
                onHandQty = BigDecimal.ZERO;
                onReceivingQty = BigDecimal.ZERO;
                onEoPurchaseQty = BigDecimal.ZERO;
                onRoPurchaseQty = BigDecimal.ZERO;
                onHoPurchaseQty = BigDecimal.ZERO;
                // Add by cxf for whole sale point
                onWoPurchaseQty = BigDecimal.ZERO;
                onTransferQty = BigDecimal.ZERO;
                onBoQty = BigDecimal.ZERO;
                onService = BigDecimal.ZERO;
                onCanvassing = BigDecimal.ZERO;
            }
        }

        // Batch register..
        purchaseRecommendationRepository.saveInBatch(BeanMapUtils.mapListTo(newRecommends, PurchaseRecommendation.class));
    }



    private List<PurchaseRecommendationVO> makeRecommendsForProduct(MstProductVO product
                                                                , Long stockPointId
                                                                , Long supplierId
                                                                , String siteId
                                                                , BigDecimal rop
                                                                , BigDecimal roq
                                                                , BigDecimal onHandQty
                                                                , BigDecimal onReceivingQty
                                                                , BigDecimal onEoPurchaseQty
                                                                , BigDecimal onRoPurchaseQty
                                                                , BigDecimal onHoPurchaseQty
                                                                , BigDecimal onWoPurchaseQty
                                                                , BigDecimal onTransferQty
                                                                , BigDecimal onBoQty
                                                                , BigDecimal onService
                                                                , BigDecimal onCanvassing
                                                                , BigDecimal nowResult) {

        List<PurchaseRecommendationVO> newRecommends = new ArrayList<PurchaseRecommendationVO>();
        // For emergency order .
        // Update by cxf for whole sale point
        BigDecimal futurezStockQty = onHandQty.add(onReceivingQty).add(onEoPurchaseQty).add(onTransferQty).add(onCanvassing).add(onRoPurchaseQty)
                                              .add(onHoPurchaseQty).add(onWoPurchaseQty).subtract(onBoQty);

        if (NumberUtil.ge(futurezStockQty, rop)) {
            return newRecommends;
        }

        BigDecimal purchaseRecommendationQtyForEo = BigDecimal.ZERO;

        if (NumberUtil.lt(futurezStockQty, BigDecimal.ZERO) || (nowResult.signum() == 1 && NumberUtil.lt(futurezStockQty, roq))) {// futurezEOStockQty
                                                                                                                                  // < 0 || Result N00
                                                                                                                                  // > 0

            // Calculate the emergency purchase recommendation qty of product

            // purchaseRecommendationQtyForEo = futurezEOStockQty.negate(); //
            // purchaseRecommendationQty = -futurezEOStockQty;
            purchaseRecommendationQtyForEo = roq.add(rop).subtract(CommonConstants.BIGDECIMAL_ONE).subtract(futurezStockQty);

            if (NumberUtil.lt(purchaseRecommendationQtyForEo, CommonConstants.BIGDECIMAL_ZERO)) {
                purchaseRecommendationQtyForEo = CommonConstants.BIGDECIMAL_ZERO;
            }

            BigDecimal lotQty = product.getPurLotSize();

            // Add the lot qty minus qty to recommendation qty
            purchaseRecommendationQtyForEo = calculatePurchaseRecommendationQtyFor(purchaseRecommendationQtyForEo, lotQty);

            // Add an EO recommendation
            newRecommends.add(this.getNewPurchaseRecommond(product
                                                          ,stockPointId
                                                          ,supplierId
                                                          ,PurchaseOrderPriorityType.POHO.getCodeDbid()
                                                          ,siteId
                                                          ,purchaseRecommendationQtyForEo));
        } else if (NumberUtil.ge(futurezStockQty, roq) && nowResult.signum() == 1) {

            // For regular order.
            // update by ticket#2717
            BigDecimal poRecommendQty = roq.add(rop).subtract(CommonConstants.BIGDECIMAL_ONE).subtract(futurezStockQty);
            if (NumberUtil.lt(poRecommendQty, CommonConstants.BIGDECIMAL_ZERO)) {
                poRecommendQty = CommonConstants.BIGDECIMAL_ZERO;
            }

            // Increase qty to the lot qty folder.
            BigDecimal lotQty = product.getPurLotSize();
            poRecommendQty = this.calculatePurchaseRecommendationQtyFor(poRecommendQty, lotQty);

            // Add an regular recommendation
            newRecommends.add(this.getNewPurchaseRecommond(product
                                                         , stockPointId
                                                         , supplierId
                                                         , PurchaseOrderPriorityType.PORO.getCodeDbid()
                                                         , siteId
                                                         , poRecommendQty));
        }

        return newRecommends;
    }

    private BigDecimal calculatePurchaseRecommendationQtyFor(BigDecimal purchaseRecommendationQty, BigDecimal lotQty) {
        if (lotQty != null && lotQty.signum() == 1) { // lotQty >0
            BigDecimal lotCount = purchaseRecommendationQty.divide(lotQty, 0, RoundingMode.UP);
            BigDecimal returnQty = lotQty;
            if (lotCount.compareTo(new BigDecimal(1)) > 0) { //
                returnQty = lotCount.multiply(lotQty);
            }
            return returnQty;
        } else {
            BigDecimal lotCount = purchaseRecommendationQty.divide(BigDecimal.ONE, 0, RoundingMode.UP);
            return lotCount;
        }
    }

    private PurchaseRecommendationVO getNewPurchaseRecommond(MstProductVO product
                                                            ,Long stockPointId
                                                            ,Long supplierId
                                                            ,String orderTypeId
                                                            ,String siteId
                                                            ,BigDecimal recommendQty) {

        PurchaseRecommendationVO pr = new PurchaseRecommendationVO();
        pr.setSiteId(siteId);
        pr.setProductId(product.getProductId());
        pr.setFacilityId(stockPointId);
        pr.setOrganizationId(supplierId);
        pr.setOrderTypeId(orderTypeId);
        pr.setRecommendQty(recommendQty);

        return pr;
    }

    private PurchaseOrderItemVO createItems(String siteId
                                          , Long purchaseOrderId
                                          , PartsAutoPurchaseOrderBO member
                                          , BigDecimal quantity
                                          , Integer seqNo) {

        PurchaseOrderItemVO purchaseOrderItemVO = new PurchaseOrderItemVO();

        purchaseOrderItemVO.setSiteId(siteId);
        purchaseOrderItemVO.setProductId(member.getProductId());
        purchaseOrderItemVO.setProductCd(member.getProductCode());
        purchaseOrderItemVO.setProductNm(member.getProductName());
        purchaseOrderItemVO.setPurchasePrice(member.getPrice());
        purchaseOrderItemVO.setOrderQty(quantity);
        purchaseOrderItemVO.setActualQty(quantity);
        purchaseOrderItemVO.setAmount(NumberUtil.multiply(member.getPrice(), quantity));
        purchaseOrderItemVO.setActualAmt(NumberUtil.multiply(member.getPrice(), quantity));
        purchaseOrderItemVO.setBoCancelFlag(CommonConstants.CHAR_N);
        purchaseOrderItemVO.setSeqNo(seqNo);
        purchaseOrderItemVO.setPurchaseOrderId(purchaseOrderId);

        return purchaseOrderItemVO;
    }

    private void doRecommendationDeletion(List<Long> purchaseRecommendIds) {

        if (purchaseRecommendIds == null || purchaseRecommendIds.size() == 0) {
            return;
        }

        List<PurchaseRecommendation> rs = purchaseRecommendationRepository.findByPurchaseRecommendationIdIn(purchaseRecommendIds);
        purchaseRecommendationRepository.deleteAllInBatch(rs);
    }

    private String nullObject(String total) {

        if (StringUtils.isNotBlankText(total)) {
            return total;
        } else {
            return CommonConstants.CHAR_ZERO;
        }
    }

    private List<PartsAutoPurchaseOrderBO> findProductListByStockPointAndSupplier(String siteId
                                                                                   , Long stockPointId
                                                                                   , Long supplierId) {

        List<PartsRecommendationBO> purchaseRecommendations = purchaseRecommendationRepository.getPurchaseRecommendationList(stockPointId
                                                                                                                           , supplierId
                                                                                                                           , siteId);


        List<PartsAutoPurchaseOrderBO> details = new ArrayList<PartsAutoPurchaseOrderBO>();
        int size = purchaseRecommendations.size();
        BigDecimal roq = BigDecimal.ZERO;
        BigDecimal eoq = BigDecimal.ZERO;
        Long roId = null;
        Long eoId = null;

        int i = 0;
        for (PartsRecommendationBO partsRecommendation : purchaseRecommendations) {

            //1. get relations parameter values
            Long rcId = partsRecommendation.getPurchaseRecommendationId();
            BigDecimal rcQty = partsRecommendation.getRecommendQty();
            String ordertypeId = partsRecommendation.getOrderTypeId();
            BigDecimal cost = partsRecommendation.getCost();
            if(PurchaseOrderPriorityType.PORO.getCodeDbid().equals(ordertypeId)) {
                roq = rcQty;
                roId = rcId;
            }else if(PurchaseOrderPriorityType.POHO.getCodeDbid().equals(ordertypeId)) {
                eoq = rcQty;
                eoId = rcId;
            }
            //2. Judge to group
            boolean isLastOne = ((i+1)==size);
            PartsRecommendationBO partsRecommendationLast = isLastOne ? null : purchaseRecommendations.get(i+1);
            i++;
            //Last one or current product id is not equal to next one.
            if(isLastOne || !NumberUtil.equals(partsRecommendation.getProductId(), partsRecommendationLast.getProductId())) {
                //Construct screen row.
                roq = (roq==null) ? BigDecimal.ZERO : roq;
                eoq = (eoq==null) ? BigDecimal.ZERO : eoq;
                cost = (cost==null) ? BigDecimal.ZERO : cost;
                details.add(constructDetailMode(partsRecommendation, roq, eoq, cost, roId, eoId, siteId, supplierId, ordertypeId));

                //Rest
                roq = BigDecimal.ZERO;
                eoq = BigDecimal.ZERO;
                roId = null;
                eoId = null;
            }
        }

        return details;
    }

    private PartsAutoPurchaseOrderBO constructDetailMode(PartsRecommendationBO item
                                                       , BigDecimal recRoQty
                                                       , BigDecimal recEoQty
                                                       , BigDecimal prdCost
                                                       , Long roRecommendId
                                                       , Long eoRecommendId
                                                       , String siteId
                                                       , Long supplierId
                                                       , String ordertypeId) {
        PartsAutoPurchaseOrderBO detailModel = new PartsAutoPurchaseOrderBO();
        detailModel.setProductCode(item.getProductCd());
        detailModel.setProductName(item.getProductNm());
        detailModel.setProductId(item.getProductId());
        detailModel.setMinimumPurchaseQuantity(item.getMinPurQty());
        detailModel.setPurchaseLotQuantity(item.getPurLotQty());

        BigDecimal basePrice = item.getStdWsPrice();
        detailModel.setPrice(item.getStdWsPrice());
        detailModel.setRecommendRo(recRoQty);
        detailModel.setRecommendEo(recEoQty);
        detailModel.setRoQuantity(recRoQty);
        detailModel.setEoQuantity(recEoQty);
        detailModel.setEoAmount(recEoQty.multiply(basePrice));
        detailModel.setRoAmount(recRoQty.multiply(basePrice));
        detailModel.setPurchaseRecommendationEoId(eoRecommendId);
        detailModel.setPurchaseRecommendationRoId(roRecommendId);

        return detailModel;
    }

    private PurchaseOrderVO setOrderNo(PurchaseOrderVO purchaseOrderVO, String largeGroup) {

        // 1. Set the screen to newPurchaseOrder
        String purchaseOrderNo = generateNoManager.generateNonSerializedItemPurchaseOrderNo(purchaseOrderVO.getSiteId(), purchaseOrderVO.getFacilityId());
        if (StringUtils.equals(CommonConstants.CHAR_LUB, largeGroup)) {

            purchaseOrderNo = CommonConstants.CHAR_L + purchaseOrderNo;
        } else {

            purchaseOrderNo = CommonConstants.CHAR_N + purchaseOrderNo;
        }

        purchaseOrderVO.setOrderNo(purchaseOrderNo);

        return purchaseOrderVO;
    }

    private Map<String, List<MstProductVO>> getPartsLargeGroup(Set<Long> partsIdSet) {

        Map<String, List<MstProductVO>> largeGroupWithProductMap = new HashMap<>();
        List<MstProduct> productListM =  mstProductRepository.findByProductIdIn(partsIdSet);
        List<MstProductVO> productListMVO = BeanMapUtils.mapListTo(productListM, MstProductVO.class);
        List<MstProductCategory> mstProductCategoryM = mstProductCategoryRepository.findBySiteIdAndCategoryType(CommonConstants.CHAR_DEFAULT_SITE_ID
                                                                                                              , PJConstants.PartsCategory.MIDDLEGROUP);

        Map<Long, String> midToLargerMap = new HashMap<>();
        for (MstProductCategory mstProductCategory : mstProductCategoryM) {
            midToLargerMap.put(mstProductCategory.getProductCategoryId(), mstProductCategory.getParentCategoryCd());
        }

        for (MstProductVO member : productListMVO) {

            if (largeGroupWithProductMap.keySet().contains(midToLargerMap.get(member.getProductCategoryId()))) {
                largeGroupWithProductMap.get(midToLargerMap.get(member.getProductCategoryId())).add(member);
            } else {
                List<MstProductVO> productList = new ArrayList<MstProductVO>();
                productList.add(member);
                largeGroupWithProductMap.put(midToLargerMap.get(member.getProductCategoryId()), productList);
            }

        }

        return largeGroupWithProductMap;
    }

    private PartsPurchaseOrderModelBO setPODetailModelData(PurchaseOrderVO purchaseOrderVO
                                                          ,PurchaseOrderItemVO purchaseOrderItemVO) {

        PartsPurchaseOrderModelBO detailModel = new PartsPurchaseOrderModelBO();
        detailModel.setDealerCode(purchaseOrderVO.getSiteId());
        detailModel.setBoCancelSign(StringUtils.equals(purchaseOrderItemVO.getBoCancelFlag(), CommonConstants.CHAR_Y)
                                                     ? CommonConstants.CHAR_ONE : CommonConstants.CHAR_ZERO);
        detailModel.setOrderDate(purchaseOrderVO.getOrderDate());
        String orderType = "";
        if (PurchaseOrderPriorityType.PORO.getCodeDbid().equals(purchaseOrderVO.getOrderPriorityType())) {
            orderType = "R";
        } else if (PurchaseOrderPriorityType.POEO.getCodeDbid().equals(purchaseOrderVO.getOrderPriorityType())) {
            orderType = "E";
        } else if (PurchaseOrderPriorityType.POHO.getCodeDbid().equals(purchaseOrderVO.getOrderPriorityType())) {
            orderType = "H";
        } else if (PurchaseOrderPriorityType.POWO.getCodeDbid().equals(purchaseOrderVO.getOrderPriorityType())) {
            orderType = "W";
        }
        detailModel.setOrderType(orderType);
        detailModel.setOrderNo(purchaseOrderVO.getOrderNo());
        detailModel.setOrderLineNo(StringUtils.toString(purchaseOrderItemVO.getSeqNo()));
        detailModel.setPartNo(purchaseOrderItemVO.getProductCd());
        detailModel.setOrderQty(purchaseOrderItemVO.getActualAmt());
        detailModel.setCreatedDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
        detailModel.setConsignee(purchaseOrderVO.getFacilityCd());


        return detailModel;
    }

    public void exportPartReturnRequstFile(List<PartsPurchaseOrderModelBO> returnRequsts) {

        //Export to interface file.
        if(returnRequsts.size() > 0) {

            List<PartsPurchaseOrderModelXmlBO> sendList = new ArrayList<>();
            String ifsCode = InterfCode.OX_SPPURCHASEORDER;
            PartsPurchaseOrderModelXmlBO sendDataBo = new PartsPurchaseOrderModelXmlBO();
            sendDataBo.setHeader(ifsPrepareMessageHeaderLogic.setHeaderBo(ifsCode));
            sendDataBo.setOrderItems(returnRequsts);
            sendList.add(sendDataBo);

            String jsonStr = JSON.toJSON(sendList).toString();
            callNewIfsManager.callNewIfsService(ifsRequestUrl, ifsCode, jsonStr);
            log.info("Async Message Send Success...");
        }
    }

    private Set<Long> getSupplierList(String siteId) {

        Set<Long> supplierIdList = new HashSet<>();
        List<OrganizationRelation> supplierIdSet = organizationRelationRepository.findBySiteIdAndRelationTypeAndProductClassification(siteId
                                                                                    ,OrgRelationType.SUPPLIER.getCodeDbid()
                                                                                    ,ProductClsType.PART.getCodeDbid());
        for (OrganizationRelation organizationRelation : supplierIdSet) {

            supplierIdList.add(organizationRelation.getToOrganizationId());
        }

        return supplierIdList;
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

    public int setSpecParameterValue(String siteId
                                    , String sysParamTypeId
                                    , String parameterValue) {

        return updateJdbcDao.setSpecParameterValue(siteId, sysParamTypeId, parameterValue);
    }

    public List<String> getSiteList(String apServerUrl) {

        List<String> siteList = selectJdbcDao.getCmmSiteList(apServerUrl);
        return siteList;
    }

    private Long toLong(String stringValue){

        return stringValue == null ? null: NumberUtil.toLong(stringValue);
    }

}