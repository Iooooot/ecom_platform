package com.db.ecom_platform.controller;

import com.db.ecom_platform.entity.dto.*;
import com.db.ecom_platform.entity.vo.UserVO;
import com.db.ecom_platform.service.UserService;
import com.db.ecom_platform.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户控制器
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     */
    @ApiOperation(value = "用户注册", notes = "支持邮箱/手机号注册")
    @PostMapping("/register")
    public Result register(@RequestBody UserRegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }
    
    /**
     * 用户登录
     */
    @ApiOperation(value = "用户登录", notes = "支持用户名+密码登录")
    @PostMapping("/login")
    public Result login(@RequestBody UserLoginDTO loginDTO, HttpServletRequest request) {
        return userService.login(loginDTO, request);
    }
    
    /**
     * 发送验证码
     * @param target 目标（手机号或邮箱）
     * @param type 类型（手机或邮箱）
     */
    @ApiOperation(value = "发送验证码", notes = "向指定的手机号或邮箱发送验证码")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "target", value = "目标（手机号或邮箱）", required = true, paramType = "query", dataTypeClass = String.class),
        @ApiImplicitParam(name = "type", value = "类型（phone或email）", required = true, paramType = "query", dataTypeClass = String.class, 
            allowableValues = "phone,email", example = "phone")
    })
    @GetMapping("/code/send")
    public Result sendVerificationCode(@RequestParam String target, @RequestParam String type) {
        return userService.sendVerificationCode(target, type);
    }
    
    /**
     * 验证码校验
     */
    @ApiOperation(value = "验证码校验", notes = "校验目标手机号或邮箱收到的验证码是否正确")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "target", value = "目标（手机号或邮箱）", required = true, paramType = "query", dataTypeClass = String.class),
        @ApiImplicitParam(name = "code", value = "验证码", required = true, paramType = "query", dataTypeClass = String.class)
    })
    @PostMapping("/code/verify")
    public Result verifyCode(@RequestParam String target, @RequestParam String code) {
        boolean verified = userService.verifyCode(target, code);
        return verified ? Result.success() : Result.error("验证码不正确");
    }
    
    /**
     * 忘记密码
     */
    @ApiOperation(value = "忘记密码", notes = "通过验证手机号或邮箱找回密码")
    @PostMapping("/password/forgot")
    public Result forgotPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        return userService.forgotPassword(forgotPasswordDTO);
    }
    
    /**
     * 重置密码
     */
    @ApiOperation(value = "重置密码", notes = "使用验证码重置密码")
    @PostMapping("/password/reset")
    public Result resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        return userService.resetPassword(resetPasswordDTO);
    }
    
    /**
     * 获取用户信息
     */
    @ApiOperation(value = "获取用户信息", notes = "获取当前登录用户的基本信息")
    @GetMapping("/info")
    public Result getUserInfo() {
        // 这里应该从当前登录用户的上下文中获取用户ID
        Integer userId = getCurrentUserId();
        UserVO userInfo = userService.getUserInfo(userId);
        return Result.success(userInfo);
    }
    
    /**
     * 更新用户信息
     */
    @ApiOperation(value = "更新用户信息", notes = "更新当前登录用户的个人资料")
    @PutMapping("/info")
    public Result updateUserInfo(@RequestBody UserUpdateDTO updateDTO) {
        Integer userId = getCurrentUserId();
        return userService.updateUserInfo(userId, updateDTO);
    }
    
    /**
     * 修改密码
     */
    @ApiOperation(value = "修改密码", notes = "修改当前登录用户的密码")
    @PutMapping("/password")
    public Result changePassword(@RequestBody PasswordChangeDTO passwordChangeDTO) {
        Integer userId = getCurrentUserId();
        return userService.changePassword(userId, passwordChangeDTO);
    }
    
    /**
     * 绑定手机号/邮箱
     */
    @ApiOperation(value = "绑定手机号/邮箱", notes = "为当前登录用户绑定手机号或邮箱")
    @PostMapping("/bind")
    public Result bindAccount(@RequestBody BindAccountDTO bindAccountDTO) {
        Integer userId = getCurrentUserId();
        return userService.bindAccount(userId, bindAccountDTO);
    }
    
    /**
     * 解绑手机号/邮箱
     */
    @ApiOperation(value = "解绑手机号/邮箱", notes = "解除当前登录用户绑定的手机号或邮箱")
    @PostMapping("/unbind")
    public Result unbindAccount(@RequestBody UnbindAccountDTO unbindAccountDTO) {
        Integer userId = getCurrentUserId();
        return userService.unbindAccount(userId, unbindAccountDTO);
    }
    
    /**
     * 第三方账号绑定
     */
    @ApiOperation(value = "第三方账号绑定", notes = "为当前登录用户绑定第三方账号（微信、支付宝等）")
    @PostMapping("/third-party/bind")
    public Result bindThirdParty(@RequestBody ThirdPartyBindDTO bindDTO) {
        Integer userId = getCurrentUserId();
        return userService.bindThirdParty(userId, bindDTO);
    }
    
    /**
     * 第三方账号解绑
     */
    @ApiOperation(value = "第三方账号解绑", notes = "解除当前登录用户绑定的第三方账号")
    @PostMapping("/third-party/unbind")
    public Result unbindThirdParty(@RequestBody ThirdPartyUnbindDTO unbindDTO) {
        Integer userId = getCurrentUserId();
        return userService.unbindThirdParty(userId, unbindDTO);
    }
    
    /**
     * 第三方登录
     */
    @ApiOperation(value = "第三方登录", notes = "使用第三方账号（微信、支付宝等）登录")
    @PostMapping("/third-party/login")
    public Result thirdPartyLogin(@RequestBody ThirdPartyLoginDTO loginDTO) {
        return userService.thirdPartyLogin(loginDTO);
    }
    
    /**
     * 用户登出
     */
    @ApiOperation(value = "用户登出", notes = "退出当前登录用户")
    @PostMapping("/logout")
    public Result logout() {
        // 清除用户登录状态
        // 实现取决于你的身份验证系统（如基于令牌的身份验证、会话等）
        return Result.success("登出成功");
    }
    
    /**
     * 获取当前登录用户ID
     * 实际实现应该从安全上下文中获取
     */
    private Integer getCurrentUserId() {
        // 这里只是占位符，实际实现应该基于你的身份验证系统
        return 1; // 假设用户ID为1
    }
} 