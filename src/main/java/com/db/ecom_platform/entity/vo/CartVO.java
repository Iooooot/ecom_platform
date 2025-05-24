package com.db.ecom_platform.entity.vo;

import com.db.ecom_platform.entity.CartItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车视图对象
 */
@Data
public class CartVO {
    
    private List<CartItem> items;          // 购物车项列表
    private Integer totalQuantity;         // 总数量
    private BigDecimal totalAmount;        // 总金额
    private BigDecimal discountAmount;     // 优惠金额
    private BigDecimal payableAmount;      // 应付金额
    private Integer invalidItemCount;      // 失效商品数量
    private List<CartItem> invalidItems;   // 失效商品列表
} 