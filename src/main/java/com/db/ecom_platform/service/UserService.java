package com.db.ecom_platform.service;

import com.db.ecom_platform.entity.dto.*;
import com.db.ecom_platform.entity.vo.UserVO;
import com.db.ecom_platform.utils.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    Result<Object> register(UserRegisterDTO registerDTO);
    
    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @param request HTTP请求
     * @return 登录结果
     */
    Result<Object> login(UserLoginDTO loginDTO, HttpServletRequest request);
    
    /**
     * 发送验证码
     * @param target 目标（手机号或邮箱）
     * @param type 类型（手机或邮箱）
     * @return 发送结果
     */
    Result<Object> sendVerificationCode(String target, Integer type);
    
    /**
     * 验证码校验
     * @param target 目标（手机号或邮箱）
     * @param code 验证码
     * @return 是否验证通过
     */
    boolean verifyCode(String target, String code);
    
    /**
     * 忘记密码 - 发送验证码
     * @param forgotPasswordDTO 忘记密码信息
     * @return 处理结果
     */
    Result<Object> forgotPassword(ResetPasswordDTO forgotPasswordDTO);

    
    /**
     * 重置密码
     * @param resetPasswordDTO 重置密码信息
     * @return 处理结果
     */
    Result<Object> resetPassword(ResetPasswordDTO resetPasswordDTO);
    
    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserInfo(Integer userId);
    
    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param updateDTO 更新信息
     * @return 更新结果
     */
    Result<Object> updateUserInfo(Integer userId, UserUpdateDTO updateDTO);
    
    /**
     * 绑定手机号/邮箱
     * @param userId 用户ID
     * @param bindAccountDTO 绑定信息
     * @return 绑定结果
     */
    Result<Object> bindAccount(Integer userId, BindAccountDTO bindAccountDTO);
    
    /**
     * 解绑手机号/邮箱
     * @param userId 用户ID
     * @param unbindAccountDTO 解绑信息
     * @return 解绑结果
     */
    Result<Object> unbindAccount(Integer userId, UnbindAccountDTO unbindAccountDTO);
    
    /**
     * 第三方账号绑定
     * @param userId 用户ID
     * @param bindDTO 绑定信息
     * @return 绑定结果
     */
    Result<Object> bindThirdParty(Integer userId, ThirdPartyBindDTO bindDTO);
    
    /**
     * 第三方账号解绑
     * @param userId 用户ID
     * @param unbindDTO 解绑信息
     * @return 解绑结果
     */
    Result<Object> unbindThirdParty(Integer userId, ThirdPartyUnbindDTO unbindDTO);
    
    /**
     * 第三方登录
     * @param loginDTO 登录信息
     * @param request
     * @param response
     * @return 登录结果
     */
    Result<Object> thirdPartyLogin(ThirdPartyLoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response) throws IOException;
    
    /**
     * 记录登录日志
     * @param userId 用户ID
     * @param request HTTP请求
     */
    void recordLoginLog(Integer userId, HttpServletRequest request);
} 