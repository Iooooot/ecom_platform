package com.db.ecom_platform.entity;

import lombok.Data;
import java.util.Map;

/**
 * 消费统计实体类
 */
@Data
public class ConsumptionStat {
    
    private Integer userId;       // 用户ID
    private String timeRange;     // 时间范围
    private Double totalAmount;   // 总消费金额
    private Integer orderCount;   // 订单数量
    private Map<String, Double> categoryConsumption; // 按类别的消费统计
} 