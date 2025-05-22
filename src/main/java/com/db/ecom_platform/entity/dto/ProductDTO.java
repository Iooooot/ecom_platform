package com.db.ecom_platform.entity.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 商品数据传输对象
 */
@Data
public class ProductDTO {
    private String productId;           // 商品ID（更新时使用）
    private String name;                // 商品名称
    private String description;         // 商品描述
    private BigDecimal price;           // 价格
    private Integer stock;              // 库存数量
    private String categoryId;          // 分类ID
    private String image;               // 商品主图URL
    private Integer status;             // 商品状态：0-下架，1-上架
} 