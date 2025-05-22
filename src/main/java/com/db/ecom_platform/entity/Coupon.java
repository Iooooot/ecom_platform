package com.db.ecom_platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠券实体类
 */
@Data
@TableName("coupons")
public class Coupon {
    
    @TableId(value = "coupon_id", type = IdType.ASSIGN_UUID)
    private String couponId;           // 优惠券ID
    
    private String name;               // 优惠券名称
    private String type;               // 优惠券类型（AMOUNT: 固定金额, PERCENT: 折扣百分比, FULL_REDUCTION: 满减）
    private BigDecimal discountValue;  // 折扣值（固定金额或折扣比例）
    private BigDecimal minOrderAmount; // 最低订单金额要求
    private String startTime;          // 优惠券生效时间
    private String endTime;            // 优惠券失效时间
    private Integer status;            // 优惠券状态（0: 未启用, 1: 已启用, 2: 已过期）
    private String createTime;         // 优惠券创建时间
    private String updateTime;         // 优惠券更新时间
    
    // 非数据库字段，用于前端展示
    @TableField(exist = false)
    private Boolean isExpired;         // 是否已过期
    
    @TableField(exist = false)
    private Integer availableCount;    // 可用数量
    
    @TableField(exist = false)
    private Integer usedCount;         // 已使用数量
} 