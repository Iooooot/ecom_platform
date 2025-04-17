package com.db.ecom_platform.controller;

import com.db.ecom_platform.entity.dto.*;
import com.db.ecom_platform.entity.vo.UserVO;
import com.db.ecom_platform.service.UserService;
import com.db.ecom_platform.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody UserRegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody UserLoginDTO loginDTO, HttpServletRequest request) {
        return userService.login(loginDTO, request);
    }
    
    /**
     * 发送验证码
     * @param target 目标（手机号或邮箱）
     * @param type 类型（手机或邮箱）
     */
    @GetMapping("/code/send")
    public Result sendVerificationCode(@RequestParam String target, @RequestParam String type) {
        return userService.sendVerificationCode(target, type);
    }
    
    /**
     * 验证码校验
     */
    @PostMapping("/code/verify")
    public Result verifyCode(@RequestParam String target, @RequestParam String code) {
        boolean verified = userService.verifyCode(target, code);
        return verified ? Result.success() : Result.error("验证码不正确");
    }
    
    /**
     * 忘记密码
     */
    @PostMapping("/password/forgot")
    public Result forgotPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        return userService.forgotPassword(forgotPasswordDTO);
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/password/reset")
    public Result resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        return userService.resetPassword(resetPasswordDTO);
    }
    
    /**
     * 获取用户信息
     */
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
    @PutMapping("/info")
    public Result updateUserInfo(@RequestBody UserUpdateDTO updateDTO) {
        Integer userId = getCurrentUserId();
        return userService.updateUserInfo(userId, updateDTO);
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result changePassword(@RequestBody PasswordChangeDTO passwordChangeDTO) {
        Integer userId = getCurrentUserId();
        return userService.changePassword(userId, passwordChangeDTO);
    }
    
    /**
     * 绑定手机号/邮箱
     */
    @PostMapping("/bind")
    public Result bindAccount(@RequestBody BindAccountDTO bindAccountDTO) {
        Integer userId = getCurrentUserId();
        return userService.bindAccount(userId, bindAccountDTO);
    }
    
    /**
     * 解绑手机号/邮箱
     */
    @PostMapping("/unbind")
    public Result unbindAccount(@RequestBody UnbindAccountDTO unbindAccountDTO) {
        Integer userId = getCurrentUserId();
        return userService.unbindAccount(userId, unbindAccountDTO);
    }
    
    /**
     * 第三方账号绑定
     */
    @PostMapping("/third-party/bind")
    public Result bindThirdParty(@RequestBody ThirdPartyBindDTO bindDTO) {
        Integer userId = getCurrentUserId();
        return userService.bindThirdParty(userId, bindDTO);
    }
    
    /**
     * 第三方账号解绑
     */
    @PostMapping("/third-party/unbind")
    public Result unbindThirdParty(@RequestBody ThirdPartyUnbindDTO unbindDTO) {
        Integer userId = getCurrentUserId();
        return userService.unbindThirdParty(userId, unbindDTO);
    }
    
    /**
     * 第三方登录
     */
    @PostMapping("/third-party/login")
    public Result thirdPartyLogin(@RequestBody ThirdPartyLoginDTO loginDTO) {
        return userService.thirdPartyLogin(loginDTO);
    }
    
    /**
     * 用户登出
     */
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