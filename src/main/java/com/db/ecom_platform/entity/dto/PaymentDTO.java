package com.db.ecom_platform.entity.dto;

import lombok.Data;

/**
 * 支付数据传输对象
 */
@Data
public class PaymentDTO {
    
    private String orderId;         // 订单ID
    private String password;        // 用户密码，用于验证
} 