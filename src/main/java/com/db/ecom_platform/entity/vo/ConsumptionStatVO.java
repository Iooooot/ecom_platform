package com.db.ecom_platform.entity.vo;

import lombok.Data;
import java.util.Map;

/**
 * 消费统计视图对象
 */
@Data
public class ConsumptionStatVO {
    
    private Integer userId;       // 用户ID
    private String timeRange;     // 时间范围（如：2023-01-01 至 2023-01-31）
    private String timeUnit;      // 时间单位（day/week/month/year）
    private Double totalAmount;   // 总消费金额
    private Integer orderCount;   // 订单数量
    private Double avgAmount;     // 平均消费金额
    private Map<String, Double> categoryConsumption; // 按类别的消费统计
} 