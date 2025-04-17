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
    private String startTime;   // 注册开始时间
    private String endTime;     // 注册结束时间
} 