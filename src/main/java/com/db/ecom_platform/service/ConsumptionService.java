package com.db.ecom_platform.service;

import com.db.ecom_platform.entity.vo.ConsumptionStatVO;
import com.db.ecom_platform.entity.vo.ConsumptionTrendVO;

import java.util.List;
import java.util.Map;

/**
 * 消费统计服务接口
 */
public interface ConsumptionService {
    
    /**
     * 获取消费统计（按时间段）
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param timeUnit 时间单位（day/week/month/year）
     * @return 消费统计
     */
    ConsumptionStatVO getConsumptionStats(Integer userId, String startTime, String endTime, String timeUnit);
    
    /**
     * 获取消费趋势
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param timeUnit 时间单位（day/week/month/year）
     * @return 消费趋势
     */
    List<ConsumptionTrendVO> getConsumptionTrend(Integer userId, String startTime, String endTime, String timeUnit);
    
    /**
     * 获取分类消费占比
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分类消费占比
     */
    Map<String, Double> getCategoryConsumption(Integer userId, String startTime, String endTime);
    
    /**
     * 导出消费明细（Excel/PDF）
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param format 格式（excel/pdf）
     * @return 导出文件的字节数组
     */
    byte[] exportConsumptionDetails(Integer userId, String startTime, String endTime, String format);
} 