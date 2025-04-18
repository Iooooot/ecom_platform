package com.db.ecom_platform.entity.dto;

import lombok.Data;
import java.util.Map;

/**
 * 第三方账号绑定数据传输对象
 */
@Data
public class ThirdPartyBindDTO {
    
    private String type;       // 第三方类型（微信、支付宝等）
    private String openId;     // 第三方账号唯一标识
    private String accessToken; // 访问令牌
    private Map<String, Object> userInfo; // 用户信息
} 