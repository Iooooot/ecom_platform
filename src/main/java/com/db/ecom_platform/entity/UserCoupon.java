package com.db.ecom_platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户-优惠券关系实体类
 */
@Data
@TableName("user_coupons")
public class UserCoupon {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;          // 主键ID
    
    private Integer userId;      // 用户ID
    private String couponId;     // 优惠券ID
    private Integer status;      // 状态（0: 未使用, 1: 已使用, 2: 已过期）
    private String createTime;   // 发放时间
    private String useTime;      // 使用时间
    private String orderId;      // 使用该优惠券的订单ID
    
    // 非数据库字段
    @TableField(exist = false)
    private Coupon coupon;       // 关联的优惠券信息
    
    @TableField(exist = false)
    private User user;           // 关联的用户信息
} 