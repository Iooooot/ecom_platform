package com.db.ecom_platform.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 管理员数据统计服务接口
 */
public interface AdminStatisticsService {
    
    /**
     * 获取实时销售统计数据
     * @return 实时销售数据（销售额、订单量等）
     */
    Map<String, Object> getRealTimeStatistics();
    
    /**
     * 获取热销商品
     * @param limit 获取数量限制
     * @return 热销商品列表
     */
    List<Map<String, Object>> getHotProducts(int limit);
    
    /**
     * 按地区分析消费数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 地区消费数据
     */
    Map<String, Object> getRegionAnalysis(LocalDate startDate, LocalDate endDate);
    
    /**
     * 按用户年龄段分析消费偏好
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 年龄段消费偏好数据
     */
    Map<String, Object> getAgeGroupAnalysis(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取销售数据看板
     * @param period 时间周期（day/week/month/year）
     * @return 销售数据看板
     */
    Map<String, Object> getDashboardData(String period);
    
    /**
     * 生成销售报表
     * @param reportType 报表类型（daily/weekly/monthly）
     * @param format 格式（pdf/excel）
     * @param date 指定日期
     * @return 报表数据的字节数组
     */
    byte[] generateReport(String reportType, String format, LocalDate date);
} 