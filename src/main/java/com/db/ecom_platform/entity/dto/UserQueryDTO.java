package com.db.ecom_platform.entity.dto;

import lombok.Data;

/**
 * 用户查询数据传输对象
 */
@Data
public class UserQueryDTO {
    
    private String username;    // 用户名
    private String email;       // 电子邮箱
    private String phone;       // 手机号码
    private Boolean isVip;      // 是否为VIP会员
    private Integer role;       // 用户角色
    private Boolean isDisabled; // 账号是否被禁用
    private String createTime;  // 创建时间
    private Double minTotalConsumption; // 最小消费总额
    private Double maxTotalConsumption; // 最大消费总额
}