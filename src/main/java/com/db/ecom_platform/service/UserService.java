package com.db.ecom_platform.service;

import com.db.ecom_platform.entity.dto.*;
import com.db.ecom_platform.entity.vo.UserVO;
import com.db.ecom_platform.utils.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    Result register(UserRegisterDTO registerDTO);
    
    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @param request HTTP请求
     * @return 登录结果
     */
    Result login(UserLoginDTO loginDTO, HttpServletRequest request);
    
    /**
     * 发送验证码
     * @param target 目标（手机号或邮箱）
     * @param type 类型（手机或邮箱）
     * @return 发送结果
     */
    Result sendVerificationCode(String target, String type);
    
    /**
     * 验证码校验
     * @param target 目标（手机号或邮箱）
     * @param code 验证码
     * @return 是否验证通过
     */
    boolean verifyCode(String target, String code);
    
    /**
     * 忘记密码
     * @param forgotPasswordDTO 忘记密码信息
     * @return 处理结果
     */
    Result forgotPassword(ForgotPasswordDTO forgotPasswordDTO);
    
    /**
     * 重置密码
     * @param resetPasswordDTO 重置密码信息
     * @return 处理结果
     */
    Result resetPassword(ResetPasswordDTO resetPasswordDTO);
    
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
    Result updateUserInfo(Integer userId, UserUpdateDTO updateDTO);
    
    /**
     * 修改密码
     * @param userId 用户ID
     * @param passwordChangeDTO 密码修改信息
     * @return 修改结果
     */
    Result changePassword(Integer userId, PasswordChangeDTO passwordChangeDTO);
    
    /**
     * 绑定手机号/邮箱
     * @param userId 用户ID
     * @param bindAccountDTO 绑定信息
     * @return 绑定结果
     */
    Result bindAccount(Integer userId, BindAccountDTO bindAccountDTO);
    
    /**
     * 解绑手机号/邮箱
     * @param userId 用户ID
     * @param unbindAccountDTO 解绑信息
     * @return 解绑结果
     */
    Result unbindAccount(Integer userId, UnbindAccountDTO unbindAccountDTO);
    
    /**
     * 第三方账号绑定
     * @param userId 用户ID
     * @param bindDTO 绑定信息
     * @return 绑定结果
     */
    Result bindThirdParty(Integer userId, ThirdPartyBindDTO bindDTO);
    
    /**
     * 第三方账号解绑
     * @param userId 用户ID
     * @param unbindDTO 解绑信息
     * @return 解绑结果
     */
    Result unbindThirdParty(Integer userId, ThirdPartyUnbindDTO unbindDTO);
    
    /**
     * 第三方登录
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    Result thirdPartyLogin(ThirdPartyLoginDTO loginDTO);
    
    /**
     * 记录登录日志
     * @param userId 用户ID
     * @param request HTTP请求
     */
    void recordLoginLog(Integer userId, HttpServletRequest request);
    
    /**
     * 记录操作日志
     * @param userId 用户ID
     * @param operationType 操作类型
     * @param operationDesc 操作描述
     * @param request HTTP请求
     */
    void recordOperationLog(Integer userId, String operationType, String operationDesc, HttpServletRequest request);
} 