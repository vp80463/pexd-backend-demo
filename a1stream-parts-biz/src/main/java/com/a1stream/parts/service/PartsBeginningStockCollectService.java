/******************************************************************************/
/* SYSTEM     : Stream                                                        */
/*                                                                            */
/* SUBSYSTEM  : Xm03                                                          */
/******************************************************************************/
package com.a1stream.parts.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.CostType;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.SdStockStatus;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.common.utils.TimerUtil;
import com.a1stream.domain.batchdao.BatchSelectJdbcDao;
import com.a1stream.domain.batchdao.BatchUpdateJdbcDao;
import com.a1stream.domain.bo.batch.BSProductStockStatusBO;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.IdUtils;
import com.ymsl.solid.base.util.uuid.SnowflakeIdWorker;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PartsBeginningStockCollectService {

    @Resource
    private BatchUpdateJdbcDao updateJdbcDao;
    @Resource
    private BatchSelectJdbcDao selectJdbcDao;

    public void doBeginningStockCollectCalculateByPoint(String siteId
                                                      , String processDate
                                                      , Long facilityId) {

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

        productStockStatus = selectJdbcDao.getProductStockStatus(siteId, CostType.AVERAGE_COST, ProductClsType.PART.getCodeDbid(),facilityId, typeIds);

        Map<String,Object[]> resultMap = new HashMap<String,Object[]>();
        log.info(productStockStatus.size() + " SP Beginning Stock Collect to be calculated");

        for (BSProductStockStatusBO productStockStatusModel : productStockStatus) {

            Object[] resultObject = new Object[4];

            resultObject[0] = NumberUtil.toBigDecimal(nullObject(productStockStatusModel.getSumquantity()));
            resultObject[1] = NumberUtil.toBigDecimal(nullObject(productStockStatusModel.getCost()));
            resultObject[2] = productStockStatusModel.getProductcd();
            resultObject[3] = productStockStatusModel.getProductnm();
            resultMap.put(facilityId + CommonConstants.CHAR_COLON
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
                                                 , Long facilityId
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
        statusIds.add(SdStockStatus.ONHAND_QTY.getCodeDbid());
        statusIds.add(SdStockStatus.ALLOCATED_QTY.getCodeDbid());
        statusIds.add(SdStockStatus.SHIPPING_REQUEST.getCodeDbid());
        statusIds.add(SdStockStatus.ONSHIPPING.getCodeDbid());
        statusIds.add(SdStockStatus.ONTRANSFER_IN_QTY.getCodeDbid());
        List<Long> seriaSet = selectJdbcDao.getSerializedProductId(siteId,statusIds);
        // Set Map and List
        productStockStatus = selectJdbcDao.getSerializdeProduct(siteId, facilityId ,seriaSet);

        Map<String,Object[]> resultMap = new HashMap<String,Object[]>();
        log.info(productStockStatus.size() + " SD Beginning Stock Collect to be calculated");

        for (BSProductStockStatusBO productStockStatusModel : productStockStatus) {

            Object[] resultObject = new Object[2];

            resultObject[0] = NumberUtil.toBigDecimal(nullObject(productStockStatusModel.getSumquantity()));
            resultObject[1] = NumberUtil.toBigDecimal(nullObject(productStockStatusModel.getCost()));
            resultObject[2] = productStockStatusModel.getProductcd();
            resultObject[3] = productStockStatusModel.getProductnm();

            resultMap.put(facilityId + CommonConstants.CHAR_COLON
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
                                     , CommonConstants.CHAR_BATCH_USER_ID
                                     , CommonConstants.CHAR_BATCH_USER_ID
                                     , CommonConstants.CHAR_BATCH_USER_ID});

            if( (++count) % CommonConstants.DB_OP_BATCH_SIZE == 0) {

                updateJdbcDao.insertInvTransaction(batchArgs);;
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

    private String nullObject(String total) {

        if (StringUtils.isBlank(total)) {
            return CommonConstants.CHAR_ZERO;
        } else {
            return total;
        }
    }

    private Long toLong(String stringValue){

        return stringValue == null ? null: NumberUtil.toLong(stringValue);
    }
}