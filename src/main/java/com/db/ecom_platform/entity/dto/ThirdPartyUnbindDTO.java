package com.db.ecom_platform.entity.dto;

import lombok.Data;

/**
 * 第三方账号解绑数据传输对象
 */
@Data
public class ThirdPartyUnbindDTO {
    
    private String type;       // 第三方类型（微信、支付宝等）
} 