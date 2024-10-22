package com.a1stream.common.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

import com.a1stream.common.constants.CommonConstants;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;

public class ComUtil {

    public static String concat(String ...keys) {

        return StringUtils.joinString(keys, "@", 10000, null);
    }

    public static String t(String key) {
        return CodedMessageUtils.getMessage(key);
    }

    public static String t(String key, String[] args) {
        return CodedMessageUtils.getMessage(key, args);
    }

    public static String concatFull(String code, String name) {

    	if (StringUtils.isNotEmpty(code) && StringUtils.isNotEmpty(name)) {
    	    return code + CommonConstants.CHAR_SPACE + name;
    	} else if (StringUtils.isNotEmpty(code)) {
    	    return code;
    	} else if (StringUtils.isNotEmpty(name)) {
    	    return name;
    	} else {
    	    return CommonConstants.CHAR_BLANK;
    	}
	}

    /**
     * yyyyMMdd转LocalDate
     * @param value
     * @return
     */
    public static LocalDate str2date(String value) {
        return LocalDate.parse(value, DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD));
    }

    /**
     * LocalDate转yyyyMMdd
     * @param date
     * @return
     */
    public static String date2str(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD));
    }

    /**
     * LocalDateTime转HHmmss
     * @param date
     * @return
     */
    public static String date2timeHMS(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_HMS));
    }

    public static String date2str(Date date) {
    	SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DB_DATE_FORMAT_YMD);
        return sdf.format(date);
    }

    public static Date str2Date(String dateString) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);
        // 将字符串转换为LocalDate
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        // 如果你确实需要java.util.Date对象
        // 注意：这种转换可能会丢失时区信息（如果原始字符串没有提供）
        Date date = Date.from(localDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());

        return date;
    }

    /**
     * LocalDateTime转HHmm
     * @param date
     * @return
     */
    public static String date2timeHM(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_HM));
    }

    /**
     * LocalDateTime转自定义format
     */
    public static String date2timeFormat(LocalDateTime date, String format) {
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    public static String formatDateTime(LocalDateTime date) {
	    return Objects.isNull(date) ? null : ComUtil.date2timeFormat(date, CommonConstants.DB_DATE_FORMAT_YMDHM);
	}

    public static String nowDateTime() {
    	return LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMDHM));
    }

    public static String nowTime() {
    	return LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_HM));
    }

    public static String nowLocalDate() {
    	return LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD));
    }

    public static String nowDate() {
    	return DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);
    }

    public static String str2DateTimeStr(String datetime) {

    	return LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMDHM))
		.format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DATE_FORMAT
				+ CommonConstants.CHAR_SPACE + CommonConstants.DB_DATE_FORMAT_H_M));
    }

    public static LocalDateTime str2DateTime(String datetime) {

    	return LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMDHM));
    }

    public static String changeFormat(String date) {
        if (StringUtils.isEmpty(date)) {
            return CommonConstants.CHAR_BLANK;
        }
        return DateUtils.changeFormat(date, CommonConstants.DB_DATE_FORMAT_YMD, CommonConstants.DEFAULT_DATE_FORMAT);
    }
}
