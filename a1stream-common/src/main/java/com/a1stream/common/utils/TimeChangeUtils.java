package com.a1stream.common.utils;

import com.a1stream.common.constants.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间转换工具类
 *
 * @author dong zhen
 */
public class TimeChangeUtils {

    private static final Logger log = LoggerFactory.getLogger(TimeChangeUtils.class);

    // 添加私有构造函数以防止实例化
    private TimeChangeUtils() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    /**
     * 根据指定的格式转换日期字符串。
     * 该方法接收一个日期字符串和两个格式字符串，尝试将日期字符串从源格式转换为目标格式。
     * 如果转换失败，将记录错误并返回空字符串。
     *
     * @param createDate 待转换的日期字符串，使用源格式。
     * @param formatFrom 源日期格式字符串，用于解析输入的日期字符串。
     * @param formatTo 目标日期格式字符串，用于格式化转换后的日期。
     * @return 转换后的日期字符串，如果转换失败则返回空字符串。
     */
    public static String changeDateFormat(String createDate, String formatFrom, String formatTo) {
        String formateDate = CommonConstants.CHAR_BLANK;
        SimpleDateFormat originalFormat = new SimpleDateFormat(formatFrom);
        Date lastLoginDateTime;
        try {
            lastLoginDateTime = originalFormat.parse(createDate);
            SimpleDateFormat newFormat = new SimpleDateFormat(formatTo);
            formateDate = newFormat.format(lastLoginDateTime);
        } catch (ParseException e) {
            log.error("时间转换失败", e);
        }
        return formateDate;
    }
}
