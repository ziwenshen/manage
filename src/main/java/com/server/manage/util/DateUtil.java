package com.server.manage.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtil {

    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss";

    /**
     * 获取当前时间戳
     *
     * @return 当前时间戳
     */
    public static long currentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前日期时间字符串
     *
     * @return 日期时间字符串
     */
    public static String currentDateTime() {
        return formatDateTime(new Date());
    }

    /**
     * 格式化日期时间
     *
     * @param date 日期时间
     * @return 格式化后的字符串
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return formatDateTime(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    /**
     * 格式化日期时间
     *
     * @param localDateTime 日期时间
     * @return 格式化后的字符串
     */
    public static String formatDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.format(DateTimeFormatter.ofPattern(DEFAULT_PATTERN));
    }

    /**
     * 格式化日期
     *
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return formatDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    /**
     * 格式化日期
     *
     * @param localDate 日期
     * @return 格式化后的字符串
     */
    public static String formatDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    /**
     * 解析日期时间字符串
     *
     * @param dateTimeStr 日期时间字符串
     * @return LocalDateTime对象
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (StringUtil.isBlank(dateTimeStr)) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(DEFAULT_PATTERN));
    }

    /**
     * 解析日期字符串
     *
     * @param dateStr 日期字符串
     * @return LocalDate对象
     */
    public static LocalDate parseDate(String dateStr) {
        if (StringUtil.isBlank(dateStr)) {
            return null;
        }
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    /**
     * 计算两个日期之间的天数
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 天数
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end);
    }
}