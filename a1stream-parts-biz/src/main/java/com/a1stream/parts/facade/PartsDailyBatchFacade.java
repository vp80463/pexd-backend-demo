package com.a1stream.parts.facade;

import java.util.List;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.SystemParameterType;
import com.a1stream.domain.bo.batch.ParameterBO;
import com.a1stream.parts.service.PartsDailyBatchtService;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PartsDailyBatchFacade {

    @Resource
    private PartsDailyBatchtService partsBatchService;

    public void doPartsDailyBatchFacade(ParameterBO model) {

        String flag = model.getFlag();
        List<String> siteList = partsBatchService.getSiteList(flag);
        String inventoryFlag = model.getInventoryFlag();

        for (String siteId : siteList) {

            log.info("**************"+DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER)+" :Start Daily Process " + siteId+"**************");

            String lastDailyBatchDate       = partsBatchService.getSystemParDate(siteId, SystemParameterType.LASTDAILYBATCH);
            String nowDate                  = DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER);
            String processDate              = DateUtils.date2String(DateUtils.addDay(DateUtils.string2Date(lastDailyBatchDate, DateUtils.FORMAT_YMD_NODELIMITER),1)
                                                                                ,DateUtils.FORMAT_YMD_NODELIMITER);
            if (StringUtils.compare(processDate, nowDate, true) >= 0) {
                log.info("return this site " + siteId);
                return;
            }

            String dateTo  = partsBatchService.getAcctMonthInfo(CommonConstants.CHAR_DEFAULT_SITE_ID, lastDailyBatchDate.substring(0, 6));

            // Start Batch Process, Set Batch Process Flag on
            partsBatchService.setSpecParameterValue(siteId
                                                  , SystemParameterType.BATCHPROCESSFLAG
                                                  , CommonConstants.CHAR_ONE);

            // Start Daily Process
            partsBatchService.doDailyProcess(siteId, processDate, dateTo, inventoryFlag);

            log.info("**************"+DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER)+" :End Daily Process " + siteId+"**************");
        }
    }
}
