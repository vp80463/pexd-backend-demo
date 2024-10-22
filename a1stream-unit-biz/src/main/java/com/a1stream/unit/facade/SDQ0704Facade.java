package com.a1stream.unit.facade;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.unit.SDQ070401BO;
import com.a1stream.domain.form.unit.SDQ070401Form;
import com.a1stream.unit.service.SDQ0704Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Component
public class SDQ0704Facade {

    @Resource
    private SDQ0704Service sdq0704Service;

    public Page<SDQ070401BO> findSdPsiDwList(SDQ070401Form model) {

        validateMonths(model.getFromMonth(), model.getToMonth());

        return sdq0704Service.findSdPsiDwList(model);
    }

    public static void validateMonths(String fromMonth, String toMonth) {
        YearMonth start = parseYearMonth(fromMonth);
        YearMonth end = parseYearMonth(toMonth);

        // 检查结束日期是否在开始日期之前
        if (end.isBefore(start)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00205", new String[] {CodedMessageUtils.getMessage("label.toMonth"), CodedMessageUtils.getMessage("label.fromMonth")}));
        }

        // 计算月份差
        int monthsBetween = (end.getYear() - start.getYear()) * 12 + (end.getMonthValue() - start.getMonthValue());

        // 校验月份差是否超过6个月
        if (monthsBetween > 6) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00339", new String[] {CodedMessageUtils.getMessage("label.fromMonth"),CodedMessageUtils.getMessage("label.toMonth")}));
        }
    }

    private static YearMonth parseYearMonth(String yearMonthStr) {
        return YearMonth.parse(yearMonthStr, DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YM));
    }
}
