package com.db.ecom_platform.entity.dto;

import lombok.Data;

/**
 * 管理员订单查询数据传输对象
 */
@Data
public class AdminOrderQueryDTO {
    
    private String keyword;         // 关键字（订单号、用户名、商品名称）
    private Integer userId;         // 用户ID
    private Integer status;         // 订单状态
    private String paymentMethod;   // 支付方式
    private String startTime;       // 开始时间
    private String endTime;         // 结束时间
    private Integer page = 1;       // 页码
    private Integer size = 10;      // 每页条数
    private Boolean hasRefund;      // 是否有退款申请（用于筛选退货/退款订单）
} 