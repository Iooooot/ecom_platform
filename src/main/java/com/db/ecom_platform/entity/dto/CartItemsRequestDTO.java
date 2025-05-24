package com.db.ecom_platform.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 购物车项列表请求DTO
 */
@Data
@ApiModel(description = "购物车项列表请求")
public class CartItemsRequestDTO {
    
    @ApiModelProperty(value = "购物车项ID列表", required = true, example = "[1, 2, 3]")
    private List<Long> cartItemIds;
} 