package com.db.ecom_platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 地址实体类
 */
@Data
@TableName("addresses")
public class Address {
    
    @TableId(value = "address_id", type = IdType.ASSIGN_UUID)
    private String addressId;     // 地址ID
    
    private String userId;        // 用户ID
    private String recipientName; // 收货人姓名
    private String phone;         // 收货人手机号
    private String addressLine1;  // 地址行1
    private String addressLine2;  // 地址行2
    private String city;          // 城市
    private String state;         // 省份/州
    private String postalCode;    // 邮政编码
    private String country;       // 国家
    
    @TableField("is_default")
    private Boolean isDefault;    // 是否为默认地址
    
    private String createTime;    // 创建时间
} 