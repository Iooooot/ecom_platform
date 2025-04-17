package com.db.ecom_platform.entity.dto;

import lombok.Data;

/**
 * 重置密码数据传输对象
 * 同时支持忘记密码和修改密码功能
 */
@Data
public class ResetPasswordDTO {
    
    private Integer type;           // 类型（0:手机号，1:邮箱）
    private String target;          // 目标（手机号或邮箱）
    private String code;            // 验证码
    private String newPassword;     // 新密码
    private String confirmPassword; // 确认密码
} 