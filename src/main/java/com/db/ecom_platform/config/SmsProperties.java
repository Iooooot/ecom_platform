package com.db.ecom_platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.sms")
public class SmsProperties {

    private String accessKeyId;
    private String accessKeySecret;
    private String signName;
    private String endpoint = "dysmsapi.aliyuncs.com";
    private String templateCode;  // 验证码短信模板
}