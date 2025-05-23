package com.db.ecom_platform.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 退款处理数据传输对象
 */
@Data
public class RefundProcessDTO {
    
    private String refundId;          // 退款申请ID
    private String orderId;           // 订单ID
    private String decision;          // 处理决定：APPROVED-同意，REJECTED-拒绝
    private BigDecimal refundAmount;  // 实际退款金额
    private String rejectReason;      // 拒绝原因（拒绝时必填）
    private String remarks;           // 管理员备注
} 