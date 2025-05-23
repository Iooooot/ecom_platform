package com.db.ecom_platform.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 购物车项数据传输对象
 */
@Data
@ApiModel(value = "购物车项数据", description = "添加或修改购物车项的数据传输对象")
public class CartItemDTO {
    
    @ApiModelProperty(value = "商品ID", required = true, example = "p-001")
    private String productId;    // 商品ID
    
    @ApiModelProperty(value = "商品数量", required = true, example = "2")
    private Integer quantity;    // 商品数量
    
    @ApiModelProperty(value = "是否选中", example = "true")
    private Boolean checked;     // 是否选中
} 