package com.db.ecom_platform.entity.dto;

import lombok.Data;

/**
 * 忘记密码数据传输对象
 */
@Data
public class ForgotPasswordDTO {
    
    private String type;     // 验证类型（手机号、邮箱）
    private String account;  // 账号（手机号或邮箱）
    private String code;     // 验证码
} 