package com.db.ecom_platform.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 退货申请数据传输对象
 */
@Data
public class RefundApplyDTO {
    
    private String orderId;           // 订单ID
    private String reason;            // 退货原因
    private String description;       // 问题描述
    private String refundType;        // 退款类型：REFUND-仅退款，REFUND_WITH_RETURN-退货退款
    private BigDecimal refundAmount;  // 申请退款金额
    private String contactPhone;      // 联系电话
    private String[] imageUrls;       // 图片证明（可选）
} 