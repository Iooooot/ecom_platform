package com.db.ecom_platform.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * 日期工具类
 */
public class DateUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取指定日期所在周的开始日期（周一）
     * @param date 指定日期
     * @return 所在周的开始日期
     */
    public static LocalDate getWeekStart(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * 获取指定日期所在周的结束日期（周日）
     * @param date 指定日期
     * @return 所在周的结束日期
     */
    public static LocalDate getWeekEnd(LocalDate date) {
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    /**
     * 获取指定日期所在月的开始日期
     * @param date 指定日期
     * @return 所在月的开始日期
     */
    public static LocalDate getMonthStart(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    /**
     * 获取指定日期所在月的结束日期
     * @param date 指定日期
     * @return 所在月的结束日期
     */
    public static LocalDate getMonthEnd(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取指定日期所在年的开始日期
     * @param date 指定日期
     * @return 所在年的开始日期
     */
    public static LocalDate getYearStart(LocalDate date) {
        return date.withDayOfYear(1);
    }

    /**
     * 获取指定日期所在年的结束日期
     * @param date 指定日期
     * @return 所在年的结束日期
     */
    public static LocalDate getYearEnd(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfYear());
    }

    /**
     * 格式化日期为字符串（yyyy-MM-dd）
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    /**
     * 格式化时间为字符串（HH:mm:ss）
     * @param time 时间
     * @return 格式化后的字符串
     */
    public static String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_FORMATTER) : "";
    }

    /**
     * 格式化日期时间为字符串（yyyy-MM-dd HH:mm:ss）
     * @param dateTime 日期时间
     * @return 格式化后的字符串
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : "";
    }

    /**
     * 解析字符串为日期（yyyy-MM-dd）
     * @param dateStr 日期字符串
     * @return 解析后的日期
     */
    public static LocalDate parseDate(String dateStr) {
        return dateStr != null && !dateStr.isEmpty() ? LocalDate.parse(dateStr, DATE_FORMATTER) : null;
    }

    /**
     * 解析字符串为时间（HH:mm:ss）
     * @param timeStr 时间字符串
     * @return 解析后的时间
     */
    public static LocalTime parseTime(String timeStr) {
        return timeStr != null && !timeStr.isEmpty() ? LocalTime.parse(timeStr, TIME_FORMATTER) : null;
    }

    /**
     * 解析字符串为日期时间（yyyy-MM-dd HH:mm:ss）
     * @param dateTimeStr 日期时间字符串
     * @return 解析后的日期时间
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return dateTimeStr != null && !dateTimeStr.isEmpty() ? LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER) : null;
    }
} 