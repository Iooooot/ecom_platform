package com.db.ecom_platform.entity.dto;

import lombok.Data;

/**
 * 第三方登录数据传输对象
 */
@Data
public class ThirdPartyLoginDTO {
    
    private String type;       // 第三方类型（微信、支付宝等）
    private String code;       // 授权码
    private String openId;     // 第三方账号唯一标识
    private String accessToken; // 访问令牌
} 