package com.db.ecom_platform.controller;

import com.db.ecom_platform.entity.dto.*;
import com.db.ecom_platform.entity.vo.UserVO;
import com.db.ecom_platform.service.UserService;
import com.db.ecom_platform.utils.Result;
import com.db.ecom_platform.utils.UserUtils;
import com.db.ecom_platform.utils.VerificationCodeUtils;
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
    
    @Autowired
    private VerificationCodeUtils verificationCodeUtils;
    
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
     * @param type 类型（0:手机，1:邮箱）
     */
    @ApiOperation(value = "发送验证码", notes = "向指定的手机号或邮箱发送验证码")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "target", value = "目标（手机号或邮箱）", required = true, paramType = "query", dataTypeClass = String.class),
        @ApiImplicitParam(name = "type", value = "类型（0:手机，1:邮箱）", required = true, paramType = "query", dataTypeClass = Integer.class, 
            allowableValues = "0,1", example = "0")
    })
    @GetMapping("/code/send")
    public Result sendVerificationCode(@RequestParam String target, @RequestParam Integer type) {
        // 直接使用VerificationCodeUtils的generateAndSendCode方法
        boolean success = verificationCodeUtils.generateAndSendCode(target, type);
        
        if (!success) {
            return Result.error("验证码发送失败");
        }
        
        return Result.success("验证码已发送");
    }

    
    /**
     * 忘记密码
     */
    @ApiOperation(value = "忘记密码", notes = "通过验证手机号或邮箱找回密码，包含验证码验证和密码重置")
    @PostMapping("/password/forgot")
    public Result forgotPassword(@RequestBody ResetPasswordDTO forgotPasswordDTO) {
        // 如果提供了验证码和新密码，验证验证码并重置密码
        if (forgotPasswordDTO.getCode() != null && forgotPasswordDTO.getNewPassword() != null) {
            // 先验证验证码是否正确
            boolean verified = verificationCodeUtils.verifyCodeWithoutMarkUsed(
                    forgotPasswordDTO.getTarget(),
                    forgotPasswordDTO.getCode()
            );

            if (!verified) {
                return Result.error("验证码不正确或已过期");
            }

            // 验证新密码和确认密码是否一致
            if (!forgotPasswordDTO.getNewPassword().equals(forgotPasswordDTO.getConfirmPassword())) {
                return Result.error("两次输入的密码不一致");
            }

            // 重置密码
            Result result = userService.forgotPassword(forgotPasswordDTO);

            // 如果重置成功，标记验证码为已使用
            if (result.getSuccess()) {
                verificationCodeUtils.markCodeAsUsed(forgotPasswordDTO.getTarget());
            }

            return result;
        } else {
            return Result.error("参数错误");
        }
    }
    
    /**
     * 重置密码
     */
    @ApiOperation(value = "重置密码", notes = "使用验证码重置密码，适用于忘记密码或修改密码")
    @PostMapping("/password/reset")
    public Result resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        // 先验证验证码是否正确
        boolean verified = verificationCodeUtils.verifyCodeWithoutMarkUsed(
                resetPasswordDTO.getTarget(), 
                resetPasswordDTO.getCode()
        );
        
        if (!verified) {
            return Result.error("验证码不正确或已过期");
        }
        
        // 重置密码
        Result result = userService.resetPassword(resetPasswordDTO);
        
        // 如果重置成功，标记验证码为已使用
        if (result.getSuccess()) {
            verificationCodeUtils.markCodeAsUsed(resetPasswordDTO.getTarget());
        }
        
        return result;
    }
    
    /**
     * 获取用户信息
     */
    @ApiOperation(value = "获取用户信息", notes = "获取当前登录用户的基本信息")
    @GetMapping("/info")
    public Result getUserInfo() {
        // 从工具类获取当前登录用户ID
        Integer userId = UserUtils.getCurrentUserId();
        UserVO userInfo = userService.getUserInfo(userId);
        return Result.success(userInfo);
    }
    
    /**
     * 更新用户信息
     */
    @ApiOperation(value = "更新用户信息", notes = "更新当前登录用户的个人资料")
    @PutMapping("/info")
    public Result updateUserInfo(@RequestBody UserUpdateDTO updateDTO) {
        Integer userId = UserUtils.getCurrentUserId();
        return userService.updateUserInfo(userId, updateDTO);
    }
    
    /**
     * 绑定手机号/邮箱
     */
    @ApiOperation(value = "绑定手机号/邮箱", notes = "为当前登录用户绑定手机号或邮箱")
    @PostMapping("/bind")
    public Result bindAccount(@RequestBody BindAccountDTO bindAccountDTO) {
        // 先验证验证码是否正确
        boolean verified = verificationCodeUtils.verifyCodeWithoutMarkUsed(
                bindAccountDTO.getTarget(), 
                bindAccountDTO.getCode()
        );
        
        if (!verified) {
            return Result.error("验证码不正确或已过期");
        }
        
        Integer userId = UserUtils.getCurrentUserId();
        Result result = userService.bindAccount(userId, bindAccountDTO);
        
        // 如果绑定成功，标记验证码为已使用
        if (result.getSuccess()) {
            verificationCodeUtils.markCodeAsUsed(bindAccountDTO.getTarget());
        }
        
        return result;
    }
    
    /**
     * 解绑手机号/邮箱
     */
    @ApiOperation(value = "解绑手机号/邮箱", notes = "解除当前登录用户绑定的手机号或邮箱")
    @PostMapping("/unbind")
    public Result unbindAccount(@RequestBody UnbindAccountDTO unbindAccountDTO) {
        // 先验证验证码是否正确
        boolean verified = verificationCodeUtils.verifyCode(
                unbindAccountDTO.getTarget(), 
                unbindAccountDTO.getCode()
        );
        
        if (!verified) {
            return Result.error("验证码不正确或已过期");
        }
        
        Integer userId = UserUtils.getCurrentUserId();
        return userService.unbindAccount(userId, unbindAccountDTO);
    }
    
    /**
     * 第三方账号绑定
     */
    @ApiOperation(value = "第三方账号绑定", notes = "为当前登录用户绑定第三方账号（微信、支付宝等）")
    @PostMapping("/third-party/bind")
    public Result bindThirdParty(@RequestBody ThirdPartyBindDTO bindDTO) {
        Integer userId = UserUtils.getCurrentUserId();
        return userService.bindThirdParty(userId, bindDTO);
    }
    
    /**
     * 第三方账号解绑
     */
    @ApiOperation(value = "第三方账号解绑", notes = "解除当前登录用户绑定的第三方账号")
    @PostMapping("/third-party/unbind")
    public Result unbindThirdParty(@RequestBody ThirdPartyUnbindDTO unbindDTO) {
        Integer userId = UserUtils.getCurrentUserId();
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
} 