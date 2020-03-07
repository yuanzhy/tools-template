package com.yuanzhy.tools.template.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static final String DF_DATE = "yyyy-MM-dd";
    public static final String DF_TIME = "HH:mm:ss";
    public static final String DF_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String DF_DATETIME_WITHOUT_SECOND = "yyyy-MM-dd HH:mm";

    /**
     * 获得当前日期
     */
    public static Date curDate() {
        return new Date();
    }

    /**
     * 获得当前日期，SQL日期类型
     */
    public static java.sql.Date curSqlDate() {
        return new java.sql.Date(System.currentTimeMillis());
    }

    /**
     * 获得当前日期字符串: yyyy-MM-dd
     */
    public static String curDateStr() {
        return format(new Date(), DF_DATE);
    }

    /**
     * 获得当前日期字符串：yyyy-MM-dd HH:mm:ss
     */
    public static String curDateTimeStr() {
        return format(new Date(), DF_DATETIME);
    }

    public static String formatDate(Date date) {
        return format(date, DF_DATE);
    }

    public static String formatTime(Date date) {
        return format(date, DF_TIME);
    }

    public static String formatDateTime(Date date) {
        return format(date, DF_DATETIME);
    }

    public static String formatDateTimeWithoutSecond(Date date) {
        return format(date, DF_DATETIME_WITHOUT_SECOND);
    }

    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static Date parseDatetime(String datetime) {
        return parse(datetime, DF_DATETIME);
    }

    public static Date parseDate(String date) {
        return parse(date, DF_DATE);
    }

    public static Date parse(String date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
