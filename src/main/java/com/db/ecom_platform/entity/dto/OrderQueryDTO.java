package com.db.ecom_platform.entity.dto;

import lombok.Data;

/**
 * 订单查询数据传输对象
 */
@Data
public class OrderQueryDTO {
    
    private String keyword;       // 关键字（订单号、商品名称）
    private Integer status;       // 订单状态
    private String startTime;     // 开始时间
    private String endTime;       // 结束时间
    private Integer page = 1;     // 页码
    private Integer size = 10;    // 每页条数
} 