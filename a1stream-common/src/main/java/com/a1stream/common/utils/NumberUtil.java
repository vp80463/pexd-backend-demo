package com.a1stream.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.a1stream.common.constants.CommonConstants;
import com.ymsl.solid.base.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dong zhen
 */
@Slf4j
public class NumberUtil extends org.springframework.util.NumberUtils {

    public static boolean larger(BigDecimal a, BigDecimal b) {
        if (a != null && b != null) {
            return a.compareTo(b) > 0;
        }
        return a != null;
    }

    public static boolean equals(BigDecimal a, BigDecimal b) {

        if (a != null && b != null) {
            return a.compareTo(b) == 0;
        } else {
            return false;
        }
    }

    public static boolean equals(Long a, Long b) {

        if (a != null && b != null) {
            return a.equals(b);
        } else {
            return false;
        }
    }

    public static boolean le(BigDecimal a, BigDecimal b) {
        if (a != null && b != null) {
            return a.compareTo(b) <= 0;
        }
        return b == null;
    }

    public static boolean ge(BigDecimal a, BigDecimal b) {
        if (a != null && b != null) {
            return a.compareTo(b) >= 0;
        }
        return a == null;
    }

    public static boolean bigDecimalLe(BigDecimal a, BigDecimal b) {
        if (a != null && b != null) {
            return a.compareTo(b) <= 0;
        }
        return a == null && b != null;
    }

    public static boolean lt(BigDecimal a, BigDecimal b) {
        if (a != null && b != null) {
            return a.compareTo(b) < 0;
        }
        return a == null && b != null;
    }

    public static BigDecimal toBigDecimal(Object a) {

        try {
            return parseNumber(StringUtils.toString(a), BigDecimal.class);
        } catch (Exception e) {
            log.error("toBigDecimal error!", e);
            return BigDecimal.ZERO;
        }
    }

    public static Integer toInteger(Object a) {

        String num = StringUtils.toString(a).split(CommonConstants.CHARACTER_DOT)[0];
        try {
            return Integer.parseInt(StringUtils.toString(num));
        } catch (Exception e) {
            log.error("toInteger error!", e);
            return 0;
        }
    }

    public static Long toLong(Object a) {

        String num = StringUtils.toString(a).split(CommonConstants.CHARACTER_DOT)[0];
        try {
            return Long.parseLong(StringUtils.toString(num));
        } catch (Exception e) {
            log.error("toLong error!", e);
            return null;
        }
    }

    public static BigDecimal add(Object a, Object b) {

        BigDecimal num1 = toBigDecimal(a);
        BigDecimal num2 = toBigDecimal(b);

        return num1.add(num2);
    }

    public static BigDecimal multiply(Object a, Object b) {

        BigDecimal num1 = toBigDecimal(a);
        BigDecimal num2 = toBigDecimal(b);

        return num1.multiply(num2);
    }

    public static BigDecimal divide(Object a, Object b, Integer scale, RoundingMode roundingMode) {

        BigDecimal num1 = toBigDecimal(a);
        BigDecimal num2 = toBigDecimal(b);

        if (num1.equals(BigDecimal.ZERO) || num2.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        return num1.divide(num2, scale, roundingMode);
    }

    public static BigDecimal subtract(Object a, Object b) {

        BigDecimal num1 = toBigDecimal(a);
        BigDecimal num2 = toBigDecimal(b);

        return num1.subtract(num2);
    }

    public static String getNumericFromString(String value) {
        StringBuilder numeric = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            if(value.charAt(i) >= 48 && value.charAt(i) <= 57){
                numeric.append(value.charAt(i));
            }
        }
        return numeric.toString();
    }

    /**
     * 比较 str1、str2 的大小
     * 例：
     *     str1 = str2 : NumberUtils.compareTo(str1,str2) == 0
     *     str1 > str2 : NumberUtils.compareTo(str1,str2) == 1
     *     str1 ≥ str2 : NumberUtils.compareTo(str1,str2) ≥ 0
     *     str1 ≤ str2 : NumberUtils.compareTo(str1,str2) ≤ 0
     */

    public static Integer compareTo(String str1, String str2) {

        if (StringUtils.isEmpty(str1)) {
            str1 = "0";
        }
        if (StringUtils.isEmpty(str2)) {
            str2 = "0";
        }

        return Integer.compare(Integer.parseInt(str1), Integer.parseInt(str2));
    }

    public static Double toDouble(String str) {

        if (str == null) {
            return null;
        }

        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException nfe) {
            return null;
        }
    }
}
