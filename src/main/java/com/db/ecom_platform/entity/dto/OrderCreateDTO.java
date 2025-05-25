package com.db.ecom_platform.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 创建订单数据传输对象
 */
@Data
@ApiModel(description = "订单创建数据")
public class OrderCreateDTO {
    
    @ApiModelProperty(value = "购物车项ID列表", required = true, example = "[1, 2, 3]")
    private List<Long> cartItemIds;
    
    @ApiModelProperty(value = "收货地址ID", required = true, example = "addr_123456")
    private String addressId;
    
    @ApiModelProperty(value = "优惠券ID", example = "COUPON123")
    private String couponId;
    
    @ApiModelProperty(value = "订单备注", example = "请在工作日送货")
    private String remark;
} 