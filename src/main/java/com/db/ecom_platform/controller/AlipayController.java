package com.db.ecom_platform.controller;

import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.db.ecom_platform.config.AlipayConfig;
import com.db.ecom_platform.entity.dto.ThirdPartyBindDTO;
import com.db.ecom_platform.entity.dto.ThirdPartyLoginDTO;
import com.db.ecom_platform.entity.dto.ThirdPartyUnbindDTO;
import com.db.ecom_platform.service.UserService;
import com.db.ecom_platform.utils.Result;
import com.db.ecom_platform.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;

@Api(tags = "支付宝接口")
@RestController
@RequestMapping("/api/alipay")
public class AlipayController {
    
    @Autowired
    private AlipayConfig alipayConfig;
    
    @Autowired
    private AlipayClient alipayClient;
    
    @Autowired
    private UserService userService;
    
    @ApiOperation(value = "获取支付宝授权URL", notes = "获取支付宝授权登录的URL")
    @GetMapping("/auth/url")
    public Result<String> getAuthUrl(@RequestParam(required = false) Boolean bind) {
        try {
            // 构建授权URL
            String url = String.format(
                "%s?app_id=%s&scope=auth_user&redirect_uri=%s&state=%s",
                "https://openauth.alipaydev.com/oauth2/publicAppAuthorize.htm",
                alipayConfig.getAppId(),
                URLEncoder.encode(alipayConfig.getAuthCallbackUrl() + (bind != null && bind ? "?bind=true" : ""), "UTF-8")
            );
            return Result.success(url);
        } catch (Exception e) {
            return Result.error("获取授权URL失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "支付宝授权回调", notes = "处理支付宝授权登录的回调")
    @GetMapping("/auth/callback")
    public Result<Object> authCallback(
            @RequestParam("auth_code") String authCode,
            @RequestParam(value = "bind", required = false) Boolean bind
    ) {
        try {
            // 1. 使用auth_code获取访问令牌
            AlipaySystemOauthTokenRequest tokenRequest = new AlipaySystemOauthTokenRequest();
            tokenRequest.setCode(authCode);
            tokenRequest.setGrantType("authorization_code");
            
            AlipaySystemOauthTokenResponse tokenResponse = alipayClient.execute(tokenRequest);
            if (!tokenResponse.isSuccess()) {
                return Result.error("获取访问令牌失败：" + tokenResponse.getMsg());
            }

            // 2. 获取用户信息
            AlipayUserInfoShareRequest userInfoRequest = new AlipayUserInfoShareRequest();
            AlipayUserInfoShareResponse userInfoResponse = alipayClient.execute(
                userInfoRequest, 
                tokenResponse.getAccessToken()
            );

            if (!userInfoResponse.isSuccess()) {
                return Result.error("获取用户信息失败：" + userInfoResponse.getMsg());
            }

            // 3. 处理用户信息
            ThirdPartyLoginDTO loginDTO = new ThirdPartyLoginDTO();
            loginDTO.setType("alipay");
            loginDTO.setOpenId(userInfoResponse.getUserId());
            loginDTO.setAccessToken(tokenResponse.getAccessToken());
            
            if (bind != null && bind) {
                // 4. 如果是绑定操作，获取当前登录用户ID
                Integer userId = UserUtils.getCurrentUserId();
                if (userId == null) {
                    return Result.error("请先登录后再绑定支付宝账号");
                }
                
                // 5. 调用绑定服务
                ThirdPartyBindDTO bindDTO = new ThirdPartyBindDTO();
                bindDTO.setType("alipay");
                bindDTO.setOpenId(userInfoResponse.getUserId());
                bindDTO.setAccessToken(tokenResponse.getAccessToken());
                return userService.bindThirdParty(userId, bindDTO);
            } else {
                // 6. 调用第三方登录服务
                return userService.thirdPartyLogin(loginDTO);
            }
            
        } catch (Exception e) {
            return Result.error("授权回调处理失败：" + e.getMessage());
        }
    }
    
    @ApiOperation(value = "绑定支付宝账号", notes = "为当前登录用户绑定支付宝账号")
    @PostMapping("/bind")
    public Result<Object> bindAlipay(@RequestBody ThirdPartyBindDTO bindDTO) {
        try {
            Integer userId = UserUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("请先登录");
            }
            bindDTO.setType("alipay");
            return userService.bindThirdParty(userId, bindDTO);
        } catch (Exception e) {
            return Result.error("绑定支付宝账号失败：" + e.getMessage());
        }
    }
    
    @ApiOperation(value = "解绑支付宝账号", notes = "解除当前用户绑定的支付宝账号")
    @PostMapping("/unbind")
    public Result<Object> unbindAlipay() {
        try {
            Integer userId = UserUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("请先登录");
            }
            ThirdPartyUnbindDTO unbindDTO = new ThirdPartyUnbindDTO();
            unbindDTO.setType("alipay");
            return userService.unbindThirdParty(userId, unbindDTO);
        } catch (Exception e) {
            return Result.error("解绑支付宝账号失败：" + e.getMessage());
        }
    }
}
