package com.yuanzhy.tools.template.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static final String DF_EN_DATE = "yyyy-MM-dd";
    public static final String DF_EN_TIME = "HH:mm:ss";
    public static final String DF_EN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String DF_EN_DATETIME_WITHOUT_SECOND = "yyyy-MM-dd HH:mm";

    public static final String DF_CN_DATE = "yyyy年MM月dd日";
    public static final String DF_CN_DATETIME = "yyyy年MM月dd日 HH时mm分ss秒";
    public static final String DF_CN_DATETIME_WITHOUT_SECOND = "yyyy年MM月dd日 HH时mm分";

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
        return format(new Date(), DF_EN_DATE);
    }

    /**
     * 获得当前日期字符串：yyyy-MM-dd HH:mm:ss
     */
    public static String curDateTimeStr() {
        return format(new Date(), DF_EN_DATETIME);
    }

    public static String formatDate(Date date) {
        return format(date, DF_EN_DATE);
    }

    public static String formatTime(Date date) {
        return format(date, DF_EN_TIME);
    }

    public static String formatDateTime(Date date) {
        return format(date, DF_EN_DATETIME);
    }

    public static String formatDateTimeWithoutSecond(Date date) {
        return format(date, DF_EN_DATETIME_WITHOUT_SECOND);
    }

    public static String formatCNDate(Date date) {
        return format(date, DF_CN_DATE);
    }

    public static String formatCNDateTime(Date date) {
        return format(date, DF_CN_DATETIME);
    }


    public static String formatCNDateTimeWithoutSecond(Date date) {
        return format(date, DF_CN_DATETIME_WITHOUT_SECOND);
    }

    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
}
