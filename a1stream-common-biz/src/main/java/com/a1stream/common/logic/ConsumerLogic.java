package com.a1stream.common.logic;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.alibaba.excel.util.StringUtils;

@Component
public class ConsumerLogic {

    public String getConsumerFullNm(String lastNm, String middleNm, String firstNm) {

        return new StringBuilder()
                  .append(lastNm)
                  .append(CommonConstants.CHAR_SPACE)
                  .append(middleNm)
                  .append(CommonConstants.CHAR_SPACE)
                  .append(firstNm)
                  .toString();
    }

    public String getConsumerRetrieve(String lastNm, String middleNm, String firstNm, String mobilePhone) {

        return new StringBuilder()
                  .append(lastNm)
                  .append(middleNm)
                  .append(firstNm)
                  .append(mobilePhone)
                  .toString()
                  .replace(CommonConstants.CHAR_SPACE, CommonConstants.CHAR_BLANK)
                  .toUpperCase();
    }

    public String getAgeByBirthYear(String birthYear) {

        //当生日年份为空，不是4位全数字时，直接返回空串
        if (StringUtils.isBlank(birthYear) || !birthYear.matches("\\d{4}")) {return CommonConstants.CHAR_BLANK;}

        // 计算岁数差异
        Period age = Period.between(LocalDate.of(Integer.parseInt(birthYear), 1, 1), LocalDate.now());

        return Integer.toString(age.getYears());
    }
}