package com.db.ecom_platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 购物车项实体类
 */
@Data
@TableName("cart_items")
public class CartItem {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;             // 主键ID
    
    private Integer userId;      // 用户ID
    private String productId;    // 商品ID
    private Integer quantity;    // 商品数量
    private Date createTime;     // 创建时间
    private Date updateTime;     // 更新时间
    
    // 非数据库字段
    @TableField(exist = false)
    private Product product;     // 关联的商品信息
    
    @TableField(exist = false)
    private BigDecimal subtotal; // 小计金额
    
    @TableField(exist = false)
    private Boolean checked;     // 是否选中
    
    @TableField(exist = false)
    private Boolean isInvalid;   // 是否失效（库存不足或已下架）
} 