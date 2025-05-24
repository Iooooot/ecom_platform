package com.db.ecom_platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("products")
public class Product {
    @TableId(value = "product_id", type = IdType.INPUT)
    private String productId;           // 商品ID
    private String name;                // 商品名称
    private String description;         // 商品描述
    private BigDecimal price;           // 价格
    private Integer stock;              // 库存数量
    private Integer salesVolume;        // 销量
    private String categoryId;          // 分类ID
    private String categoryName;        // 分类名称（冗余字段，便于前端展示）
    private String image;               // 商品主图
    private Integer status;             // 商品状态：0-下架，1-上架
    private Date createTime;            // 创建时间
    private Date updateTime;            // 更新时间
} 