package com.db.ecom_platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单项实体类
 */
@Data
@TableName("order_items")
public class OrderItem {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;                // 订单项ID
    
    private String orderId;         // 订单ID
    private String productId;       // 商品ID
    private String productName;     // 商品名称
    private String productImage;    // 商品图片
    private BigDecimal price;       // 商品单价
    private Integer quantity;       // 购买数量
    private BigDecimal subtotal;    // 小计金额
    private Date createTime;        // 创建时间
    
    // 非数据库字段
    @TableField(exist = false)
    private Product product;        // 商品信息
} 