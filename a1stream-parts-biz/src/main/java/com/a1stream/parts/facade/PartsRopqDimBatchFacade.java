package com.a1stream.parts.facade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.parts.service.PartsRopqDimBatchService;
import com.ymsl.solid.base.util.DateUtils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PartsRopqDimBatchFacade {

    @Resource
    private PartsRopqDimBatchService partsBatchService;

    public void doPartsBatchFacade(String siteId
                                  ,List<Long> facilityIdList) {

        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);


        log.info(String.format("Begin processing batch for site [%s]", siteId));
        SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DB_DATE_FORMAT_YMD);
        Date date = new Date();
        try {
            date = sdf.parse(sysDate);
            date = DateUtils.addMonth(date, -1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String lastDate         = sdf.format(date);
        String processDate      = partsBatchService.getAcctMonthInfo(CommonConstants.CHAR_DEFAULT_SITE_ID, lastDate.substring(0, 6));
        String lastDailyDate    = DateUtils.date2String(DateUtils.addMonth(DateUtils.string2Date(sysDate, DateUtils.FORMAT_YMD_NODELIMITER),-1)
                                                        ,DateUtils.FORMAT_YMD_NODELIMITER);
        String lastYYMM         =   DateUtils.date2String(DateUtils.addMonth(DateUtils.string2Date(processDate, DateUtils.FORMAT_YMD_NODELIMITER),1)
                                                        ,DateUtils.FORMAT_YMD_NODELIMITER).substring(0, 6);

        partsBatchService.dimBatch(siteId, processDate, lastDailyDate, lastYYMM, facilityIdList);

        if(log.isInfoEnabled()) {
            log.info("-------------------- OK --------------------\n");
        }
    }
}
