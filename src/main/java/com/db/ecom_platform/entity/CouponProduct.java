package com.db.ecom_platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 优惠券-商品关系实体类
 */
@Data
@TableName("coupon_products")
public class CouponProduct {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;          // 主键ID
    
    private String couponId;     // 优惠券ID
    private String productId;    // 商品ID
    private String createTime;   // 创建时间
    
    // 非数据库字段
    @TableField(exist = false)
    private Coupon coupon;       // 关联的优惠券信息
    
    @TableField(exist = false)
    private Product product;     // 关联的商品信息
} 