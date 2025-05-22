package com.db.ecom_platform.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 用户-优惠券关系数据传输对象
 */
@Data
public class UserCouponDTO {
    private String couponId;         // 优惠券ID
    private List<Integer> userIds;   // 用户ID列表
} 