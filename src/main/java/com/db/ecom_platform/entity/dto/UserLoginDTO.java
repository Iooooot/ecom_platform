package com.db.ecom_platform.entity.dto;

import lombok.Data;

/**
 * 用户登录数据传输对象
 */
@Data
public class UserLoginDTO {
    
    private String username;      // 用户名/手机号/邮箱
    private String password;      // 密码
    private String loginType;     // 登录类型（用户名、手机号、邮箱）
} 