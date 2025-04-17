package com.db.ecom_platform.entity.dto;

import lombok.Data;

/**
 * 重置密码数据传输对象
 */
@Data
public class ResetPasswordDTO {
    
    private String type;           // 重置类型（手机号、邮箱）
    private String account;        // 账号（手机号或邮箱）
    private String code;           // 验证码
    private String newPassword;    // 新密码
    private String confirmPassword; // 确认密码
} 