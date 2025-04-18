package com.db.ecom_platform.controller;

import com.db.ecom_platform.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 第三方回调控制器
 * 处理各种第三方平台的回调请求，不带/api前缀，匹配应用配置中的回调URL
 */
@Api(tags = "第三方回调接口")
@RestController
public class CallbackController {
    
    @Autowired
    private AlipayController alipayController;
    
    /**
     * 支付宝授权回调
     * 匹配应用配置中的回调URL: http://localhost:8088/auth/alipay/callback
     */
    @ApiOperation(value = "支付宝授权回调", notes = "处理支付宝授权登录的回调")
    @GetMapping("/auth/alipay/callback")
    public Result<Object> alipayAuthCallback(
            @RequestParam("auth_code") String authCode,
            @RequestParam(value = "state", required = false) String state
    ) {
        // 转发到AlipayController处理
        return alipayController.authCallback(authCode, state);
    }
} 