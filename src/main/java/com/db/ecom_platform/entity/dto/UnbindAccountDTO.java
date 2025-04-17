package com.db.ecom_platform.entity.dto;

import lombok.Data;

/**
 * 账号解绑数据传输对象
 */
@Data
public class UnbindAccountDTO {
    
    private Integer type;    // 类型（0:手机号，1:邮箱）
    private String target;   // 目标（手机号或邮箱）
    private String code;     // 验证码
} 