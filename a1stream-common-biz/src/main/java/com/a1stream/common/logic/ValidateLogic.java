package com.a1stream.common.logic;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.utils.ComUtil;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import software.amazon.awssdk.utils.StringUtils;

/**
* @author mid1341
*/
@Component
public class ValidateLogic {

    public void validateIsRequired(String value, String labelName) {

        if (StringUtils.isBlank(value)) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10326", new String[] {labelName}));
        }
    }

    public void validateIsRequired(Long value, String labelName) {

        if (Objects.isNull(value)) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10326", new String[] {labelName}));
        }
    }

    public void validateIsRequired(BigDecimal value, String labelName) {

        if (Objects.isNull(value) || value.compareTo(BigDecimal.ZERO) == 0) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10326", new String[] {labelName}));
        }
    }

    public void validateNumberLtZero(BigDecimal value, String labelName) {

        if (Objects.isNull(value) || value.compareTo(BigDecimal.ZERO) <= 0) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00200", new String[] {labelName, CommonConstants.CHAR_ZERO}));
        }
    }

    public void validateEntityNotExist(String code, Long id, String entityName) {

        if (StringUtils.isNotBlank(code) && id == null) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10237", new String[] {code, entityName}));
        }
    }

    public void validateEmail(String email) {
        if (StringUtils.isNotBlank(email) && !email.matches(CommonConstants.EMAIL_REG_FORMAT)) {
            throw new BusinessCodedException(ComUtil.t("M.E.10311", new String[] {email}));
        }
    }

    /**
     * date为yyyyMMdd格式
     */
    public void validateDateRange(String dateFrom, String dateTo, int maxMonthRange) {

        if (StringUtils.isBlank(dateFrom) && StringUtils.isBlank(dateTo)){return;}

        dateFrom = StringUtils.isBlank(dateFrom) ? CommonConstants.MIN_DATE : dateFrom;
        dateTo = StringUtils.isBlank(dateTo) ? CommonConstants.MAX_DATE : dateTo;

        //from <= to
        if (ComUtil.str2date(dateFrom).compareTo(ComUtil.str2date(dateTo)) > 0) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00205", new String[] {CodedMessageUtils.getMessage("label.toDate"), CodedMessageUtils.getMessage("label.fromDate")}));
        }

        //区间不超过X个月
        if (ComUtil.str2date(dateTo).compareTo(ComUtil.str2date(dateFrom).plusMonths(maxMonthRange)) > 0) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10339", new String[] {CodedMessageUtils.getMessage("label.fromDate"), CodedMessageUtils.getMessage("label.toDate"), String.valueOf(maxMonthRange)}));
        }
    }


    public boolean isValidDate(String dateStr, String format) {

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // 设置严格解析日期

        try {
            sdf.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
