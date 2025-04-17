package com.db.ecom_platform.entity.dto;

import lombok.Data;

/**
 * 账号绑定数据传输对象
 */
@Data
public class BindAccountDTO {
    
    private String type;      // 绑定类型（手机号、邮箱）
    private String account;   // 账号值（手机号或邮箱）
    private String code;      // 验证码
} 