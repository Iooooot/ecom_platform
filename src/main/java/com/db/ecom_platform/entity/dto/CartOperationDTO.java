package com.db.ecom_platform.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 购物车批量操作数据传输对象
 */
@Data
@ApiModel(value = "购物车批量操作数据", description = "对购物车进行批量操作的数据传输对象")
public class CartOperationDTO {
    
    @ApiModelProperty(value = "购物车项ID列表", required = true, example = "[1, 2, 3]")
    private List<Long> itemIds;    // 购物车项ID列表
    
    @ApiModelProperty(value = "操作类型", required = true, example = "delete", allowableValues = "delete,check,uncheck")
    private String operation;      // 操作类型：delete-删除，check-选中，uncheck-取消选中
} 