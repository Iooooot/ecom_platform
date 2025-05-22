package com.db.ecom_platform.entity.vo;

import com.db.ecom_platform.entity.Product;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券视图对象
 */
@Data
public class CouponVO {
    private String couponId;           // 优惠券ID
    private String name;               // 优惠券名称
    private String type;               // 优惠券类型
    private String typeDesc;           // 优惠券类型描述
    private BigDecimal discountValue;  // 折扣值
    private BigDecimal minOrderAmount; // 最低订单金额
    private String startTime;          // 开始时间
    private String endTime;            // 结束时间
    private Integer status;            // 状态
    private String statusDesc;         // 状态描述
    private String createTime;         // 创建时间
    private Boolean isExpired;         // 是否已过期
    private Integer availableCount;    // 可用数量
    private Integer usedCount;         // 已使用数量
    private List<Product> products;    // 关联的商品列表
    private Boolean isAllProducts;     // 是否适用于所有商品
    
    // 根据类型获取优惠券描述
    public String getDiscountDesc() {
        if (type == null) {
            return "";
        }
        
        switch (type) {
            case "AMOUNT":
                return "满减券: ¥" + discountValue;
            case "PERCENT":
                return "折扣券: " + discountValue + "折";
            case "FULL_REDUCTION":
                return "满" + minOrderAmount + "减" + discountValue;
            default:
                return "";
        }
    }
} 