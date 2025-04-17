package com.db.ecom_platform.entity.vo;

import lombok.Data;

/**
 * 消费明细视图对象
 */
@Data
public class ConsumptionDetailVO {
    
    private String orderId;        // 订单ID
    private String createTime;     // 创建时间
    private Double amount;         // 消费金额
    private Integer itemCount;     // 商品数量
    private String categoryName;   // 主要商品类别
    private String productNames;   // 商品名称
} 