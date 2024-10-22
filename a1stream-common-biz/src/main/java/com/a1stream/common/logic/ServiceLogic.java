package com.a1stream.common.logic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.BaseDateType;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.vo.CmmServiceDemandVO;
import com.a1stream.domain.vo.RemindSettingVO;

import jodd.util.StringUtil;
import software.amazon.awssdk.utils.StringUtils;

@Component
public class ServiceLogic {

    public BigDecimal calculateStdRetailPriceForJob(BigDecimal stdRetailPrice, BigDecimal jobTaxRate, BigDecimal defaultTaxRate) {
        return stdRetailPrice.divide(CommonConstants.BIGDECIMAL_ONE.add(defaultTaxRate.multiply(CommonConstants.BIGDECIMAL_ONE_PERSENT)), CommonConstants.INTEGER_TWO, RoundingMode.HALF_UP).multiply(CommonConstants.BIGDECIMAL_ONE.add(jobTaxRate.multiply(CommonConstants.BIGDECIMAL_ONE_PERSENT)));
    }


    public List<String> generateModelCdListForServiceGroup(String modelCd){

        List<String> result = new ArrayList<>();

        if (StringUtils.isNotBlank(modelCd)) {

            int modelCodeLength = modelCd.length();

            //modelCd长度不足3位时，直接使用自身查询
            if (modelCodeLength <= 3){result.add(modelCd);}

            //modelCd长度超过3位时，逐个添加
            for (int i = 3, j = modelCodeLength; i < j; i++) {
                result.add(modelCd.substring(0, i));
            }
        }
        result.add(CommonConstants.CHAR_ALL);

        return result;
    }

    public String calculateRemindDueDate(String baseDateType, Long days, String eventStartDate, String eventEndDate) {

        String sourceDate = CommonConstants.CHAR_BLANK;

        if (StringUtils.equals(baseDateType, BaseDateType.KEY_SERVICESETTLEDATE.getCodeDbid())
                || StringUtils.equals(baseDateType, BaseDateType.KEY_STUDATE.getCodeDbid())
                || StringUtils.equals(baseDateType, BaseDateType.KEY_DEMANDSTARTDATE.getCodeDbid())) {

            sourceDate = eventStartDate;
        }
        else if (StringUtils.equals(baseDateType, BaseDateType.KEY_DEMANDENDDATE.getCodeDbid())) {

            sourceDate = eventEndDate;
        }

        return ComUtil.date2str(ComUtil.str2date(sourceDate).plusDays(days));
    }

    public String calculateRemindExpireDate(String baseDateType, Long days, Long continueDays, String eventStartDate, String eventEndDate) {

        String sourceDate = CommonConstants.CHAR_BLANK;

        if (StringUtils.equals(baseDateType, BaseDateType.KEY_SERVICESETTLEDATE.getCodeDbid())
                || StringUtils.equals(baseDateType, BaseDateType.KEY_STUDATE.getCodeDbid())
                || StringUtils.equals(baseDateType, BaseDateType.KEY_DEMANDSTARTDATE.getCodeDbid())) {

            sourceDate = eventStartDate;
        }
        else if (StringUtils.equals(baseDateType, BaseDateType.KEY_DEMANDENDDATE.getCodeDbid())) {

            sourceDate = eventEndDate;
        }

        return ComUtil.date2str(ComUtil.str2date(sourceDate).plusDays(days).plusDays(continueDays));
    }

    public CmmServiceDemandVO getNextServiceDemand(List<CmmServiceDemandVO> demandList, String stuDate, Long serviceDemandId) {

        for (CmmServiceDemandVO svDemand : demandList) {

            LocalDate fromDate = ComUtil.str2date(stuDate).plusMonths(svDemand.getBaseDateAfter());

            //如果当前订单使用这枚demand，或当前Demand生效日已过,则跳过
            if ((!Objects.isNull(serviceDemandId) && StringUtil.equals(svDemand.getServiceDemandId().toString(), serviceDemandId.toString()))
                    || fromDate.compareTo(LocalDate.now()) < 0) {continue;}

            svDemand.setFromDate(ComUtil.date2str(fromDate));
            svDemand.setToDate(ComUtil.date2str(fromDate.plusMonths(svDemand.getDuePeriod()).minusDays(CommonConstants.INTEGER_ONE)));

            return svDemand;
        }

        return null;
    }

    public String calculateNextRemindGenDate(CmmServiceDemandVO nextServiceDemand, List<RemindSettingVO> routineInspectionRemindSettingList) {

        String earliestRemindDueDate = CommonConstants.CHAR_BLANK;
        String remindDueDate;

        for (RemindSettingVO remindSetting : routineInspectionRemindSettingList) {

            remindDueDate = this.calculateRemindDueDate(remindSetting.getBaseDateType(), remindSetting.getDays().longValue(), nextServiceDemand.getFromDate(), nextServiceDemand.getToDate());

            earliestRemindDueDate = StringUtils.isBlank(earliestRemindDueDate) ? remindDueDate : earliestRemindDueDate;

            if (earliestRemindDueDate.compareTo(remindDueDate) > 0){

                earliestRemindDueDate = remindDueDate;
            }
        }

        //因此返回的生成时间，为最早的RemindDueDate再往前推3天
        LocalDate remindGenDate = ComUtil.str2date(earliestRemindDueDate).plusDays(-3);
        //如提醒时间早于当前时间，则取当前时间
        return remindGenDate.compareTo(LocalDate.now()) < 0 ? ComUtil.date2str(LocalDate.now()) : ComUtil.date2str(remindGenDate);
    }
}