package com.db.ecom_platform.service;

import com.db.ecom_platform.entity.ConsumptionStat;
import com.db.ecom_platform.entity.vo.ConsumptionCompareVO;
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
     * 获取消费统计（简化版，通过时间范围）
     * @param userId 用户ID
     * @param timeRange 时间范围（week/month/year/all）
     * @return 消费统计
     */
    ConsumptionStat getConsumptionStats(Integer userId, String timeRange);
    
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
     * 获取消费趋势（简化版，通过时间范围）
     * @param userId 用户ID
     * @param timeRange 时间范围（week/month/year）
     * @return 消费趋势数据
     */
    Map<String, Object> getConsumptionTrend(Integer userId, String timeRange);
    
    /**
     * 获取分类消费占比
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分类消费占比
     */
    Map<String, Double> getCategoryConsumption(Integer userId, String startTime, String endTime);
    
    /**
     * 获取消费品类分布（简化版，通过时间范围）
     * @param userId 用户ID
     * @param timeRange 时间范围（week/month/year/all）
     * @return 消费品类分布
     */
    List<Map<String, Object>> getCategoryDistribution(Integer userId, String timeRange);
    
    /**
     * 获取消费同环比对比
     * @param userId 用户ID
     * @param timeRange 时间范围（week/month/year）
     * @param compareType 对比类型（mom-环比，yoy-同比）
     * @return 对比结果
     */
    ConsumptionCompareVO getConsumptionCompare(Integer userId, String timeRange, String compareType);
    
    /**
     * 获取用户消费排名
     * @param userId 用户ID
     * @param timeRange 时间范围（month/year/all）
     * @return 排名数据
     */
    Map<String, Object> getConsumptionRank(Integer userId, String timeRange);
    
    /**
     * 获取平均消费数据
     * @param userId 用户ID
     * @param timeRange 时间范围（month/year/all）
     * @return 平均消费数据
     */
    Map<String, Object> getAverageConsumption(Integer userId, String timeRange);
    
    /**
     * 获取消费明细记录（分页）
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 页码
     * @param size 每页大小
     * @return 消费明细数据
     */
    Map<String, Object> getConsumptionDetails(Integer userId, String startTime, String endTime, Integer page, Integer size);
    
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