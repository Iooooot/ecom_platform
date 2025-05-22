package com.db.ecom_platform.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 优惠券-商品关系数据传输对象
 */
@Data
public class CouponProductDTO {
    private String couponId;           // 优惠券ID
    private List<String> productIds;   // 商品ID列表
} 