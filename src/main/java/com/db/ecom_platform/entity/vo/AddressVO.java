package com.db.ecom_platform.entity.vo;

import lombok.Data;

/**
 * 地址视图对象
 */
@Data
public class AddressVO {
    
    private String addressId;     // 地址ID
    private String recipientName; // 收货人姓名
    private String phone;         // 收货人手机号
    private String addressLine1;  // 地址行1
    private String addressLine2;  // 地址行2
    private String city;          // 城市
    private String state;         // 省份/州
    private String postalCode;    // 邮政编码
    private String country;       // 国家
    private Boolean isDefault;    // 是否为默认地址
    private String createTime;    // 创建时间
    
    /**
     * 获取完整地址
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(state).append(" ").append(city).append(" ");
        sb.append(addressLine1);
        if (addressLine2 != null && !addressLine2.isEmpty()) {
            sb.append(", ").append(addressLine2);
        }
        sb.append(" (").append(postalCode).append(")");
        return sb.toString();
    }
} 