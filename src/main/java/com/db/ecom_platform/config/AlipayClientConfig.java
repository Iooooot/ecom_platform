package com.db.ecom_platform.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayClientConfig {
    
    @Autowired
    private AlipayConfig alipayConfig;
    
    @Bean
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(
            alipayConfig.getGatewayUrl(),
            alipayConfig.getAppId(),
            alipayConfig.getPrivateKey(),
            alipayConfig.getFormat(),
            alipayConfig.getCharset(),
            alipayConfig.getPublicKey(),
            alipayConfig.getSignType()
        );
    }
} 