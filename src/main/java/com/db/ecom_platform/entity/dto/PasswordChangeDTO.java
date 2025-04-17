package com.db.ecom_platform.entity.dto;

import lombok.Data;

/**
 * 密码修改数据传输对象
 */
@Data
public class PasswordChangeDTO {
    
    private String oldPassword;     // 旧密码
    private String newPassword;     // 新密码
    private String confirmPassword; // 确认密码
} 