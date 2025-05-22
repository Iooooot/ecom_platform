package com.db.ecom_platform.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 优惠券数据传输对象
 */
@Data
public class CouponDTO {
    private String couponId;           // 优惠券ID（更新时使用）
    private String name;               // 优惠券名称
    private String type;               // 优惠券类型（AMOUNT: 固定金额, PERCENT: 折扣百分比, FULL_REDUCTION: 满减）
    private BigDecimal discountValue;  // 折扣值（固定金额或折扣比例）
    private BigDecimal minOrderAmount; // 最低订单金额要求
    private String startTime;          // 优惠券生效时间
    private String endTime;            // 优惠券失效时间
    private Integer status;            // 优惠券状态（0: 未启用, 1: 已启用, 2: 已过期）
} 