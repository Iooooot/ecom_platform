package com.db.ecom_platform.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车项视图对象
 */
@Data
@ApiModel(description = "购物车项视图数据")
public class CartItemVO {
    
    @ApiModelProperty(value = "购物车项ID", example = "1")
    private Long id;
    
    @ApiModelProperty(value = "商品ID", example = "p-001")
    private String productId;
    
    @ApiModelProperty(value = "商品名称", example = "智能手机")
    private String productName;
    
    @ApiModelProperty(value = "商品单价", example = "2999.00")
    private BigDecimal price;
    
    @ApiModelProperty(value = "商品数量", example = "2")
    private Integer quantity;
    
    @ApiModelProperty(value = "商品图片", example = "/images/products/smartphone.jpg")
    private String image;
    
    @ApiModelProperty(value = "商品库存", example = "100")
    private Integer stock;
    
    @ApiModelProperty(value = "是否有效", example = "true")
    private Boolean isAvailable;
} 