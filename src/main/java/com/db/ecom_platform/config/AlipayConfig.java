package com.db.ecom_platform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {
    private String appId;           // 支付宝应用ID
    private String privateKey;      // 应用私钥
    private String publicKey;       // 支付宝公钥
    private String gatewayUrl;      // 支付宝网关（沙箱环境：https://openapi.alipaydev.com/gateway.do）
    private String charset;         // 编码格式
    private String format;          // 格式
    private String signType;        // 签名方式
    private String authCallbackUrl; // 授权回调地址
} 