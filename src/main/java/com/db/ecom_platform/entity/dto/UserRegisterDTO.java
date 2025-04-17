package com.db.ecom_platform.entity.dto;

import lombok.Data;

/**
 * 用户注册数据传输对象
 */
@Data
public class UserRegisterDTO {
    
    private String username;      // 用户名
    private String password;      // 密码
    private String email;         // 电子邮箱
    private String phone;         // 手机号码
    private String verificationCode; // 验证码
} 