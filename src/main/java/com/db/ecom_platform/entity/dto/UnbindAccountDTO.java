package com.db.ecom_platform.entity.dto;

import lombok.Data;

/**
 * 账号解绑数据传输对象
 */
@Data
public class UnbindAccountDTO {
    
    private String type;      // 解绑类型（手机号、邮箱）
    private String code;      // 验证码
} 