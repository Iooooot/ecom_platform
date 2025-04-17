package com.db.ecom_platform.entity.vo;

import lombok.Data;

/**
 * 消费同环比对比视图对象
 */
@Data
public class ConsumptionCompareVO {

    private Integer userId;            // 用户ID
    private String timeRange;          // 时间范围（week/month/year）
    private String compareType;        // 对比类型（mom-环比，yoy-同比）
    private String currentPeriod;      // 当前周期
    private String previousPeriod;     // 对比周期
    
    private Double currentAmount;      // 当前周期消费金额
    private Integer currentOrderCount; // 当前周期订单数量
    private Double previousAmount;     // 对比周期消费金额
    private Integer previousOrderCount;// 对比周期订单数量
    
    private Double amountChangeRate;   // 消费金额变化率（百分比）
    private Double countChangeRate;    // 订单数量变化率（百分比）
} 