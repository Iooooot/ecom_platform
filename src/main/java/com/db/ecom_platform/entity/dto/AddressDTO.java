package com.db.ecom_platform.entity.dto;

import lombok.Data;

/**
 * 地址数据传输对象
 */
@Data
public class AddressDTO {
    
    private String recipientName; // 收货人姓名
    private String phone;         // 收货人手机号
    private String addressLine1;  // 地址行1
    private String addressLine2;  // 地址行2
    private String city;          // 城市
    private String state;         // 省份/州
    private String postalCode;    // 邮政编码
    private String country;       // 国家
    private Boolean isDefault;    // 是否为默认地址
} 