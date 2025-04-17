package com.db.ecom_platform.entity.vo;

import lombok.Data;

/**
 * 消费趋势视图对象
 */
@Data
public class ConsumptionTrendVO {
    
    private String timePoint;    // 时间点（如：2023-01-01、2023-01 或 2023-W01）
    private Double amount;       // 消费金额
    private Integer count;       // 订单数量
} 