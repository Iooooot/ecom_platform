package com.db.ecom_platform.service.impl;

import com.db.ecom_platform.entity.User;
import com.db.ecom_platform.entity.UserLoginLog;
import com.db.ecom_platform.entity.dto.*;
import com.db.ecom_platform.entity.vo.UserVO;
import com.db.ecom_platform.mapper.UserLoginLogMapper;
import com.db.ecom_platform.mapper.UserMapper;
import com.db.ecom_platform.service.UserService;
import com.db.ecom_platform.utils.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserLoginLogMapper loginLogMapper;
    
    /**
     * 用户注册
     */
    @Override
    @Transactional
    public Result<Object> register(UserRegisterDTO registerDTO) {
        // TODO: 实现注册逻辑
        return Result.success("注册成功");
    }
    
    /**
     * 用户登录
     */
    @Override
    public Result<Object> login(UserLoginDTO loginDTO, HttpServletRequest request) {
        // TODO: 实现登录逻辑
        return Result.success("登录成功");
    }
    
    /**
     * 发送验证码
     */
    @Override
    public Result<Object> sendVerificationCode(String target, String type) {
        // TODO: 实现发送验证码逻辑
        return Result.success("验证码已发送");
    }
    
    /**
     * 验证验证码
     */
    @Override
    public boolean verifyCode(String target, String code) {
        // TODO: 实现验证码校验逻辑
        return true;
    }
    
    /**
     * 忘记密码
     */
    @Override
    @Transactional
    public Result<Object> forgotPassword(ForgotPasswordDTO forgotPasswordDTO) {
        // TODO: 实现忘记密码逻辑
        return Result.success("验证成功，请重置密码");
    }
    
    /**
     * 重置密码
     */
    @Override
    @Transactional
    public Result<Object> resetPassword(ResetPasswordDTO resetPasswordDTO) {
        // TODO: 实现重置密码逻辑
        return Result.success("密码重置成功");
    }
    
    /**
     * 获取用户信息
     */
    @Override
    public UserVO getUserInfo(Integer userId) {
        // TODO: 实现获取用户信息逻辑
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
    
    /**
     * 更新用户信息
     */
    @Override
    @Transactional
    public Result<Object> updateUserInfo(Integer userId, UserUpdateDTO updateDTO) {
        // TODO: 实现更新用户信息逻辑
        return Result.success("用户信息更新成功");
    }
    
    /**
     * 修改密码
     */
    @Override
    @Transactional
    public Result<Object> changePassword(Integer userId, PasswordChangeDTO passwordChangeDTO) {
        // TODO: 实现修改密码逻辑
        return Result.success("密码修改成功");
    }
    
    /**
     * 绑定手机/邮箱
     */
    @Override
    @Transactional
    public Result<Object> bindAccount(Integer userId, BindAccountDTO bindAccountDTO) {
        // TODO: 实现绑定手机/邮箱逻辑
        return Result.success("绑定成功");
    }
    
    /**
     * 解绑手机/邮箱
     */
    @Override
    @Transactional
    public Result<Object> unbindAccount(Integer userId, UnbindAccountDTO unbindAccountDTO) {
        // TODO: 实现解绑手机/邮箱逻辑
        return Result.success("解绑成功");
    }
    
    /**
     * 绑定第三方账号
     */
    @Override
    @Transactional
    public Result<Object> bindThirdParty(Integer userId, ThirdPartyBindDTO bindDTO) {
        // TODO: 实现绑定第三方账号逻辑
        return Result.success("绑定成功");
    }
    
    /**
     * 解绑第三方账号
     */
    @Override
    @Transactional
    public Result<Object> unbindThirdParty(Integer userId, ThirdPartyUnbindDTO unbindDTO) {
        // TODO: 实现解绑第三方账号逻辑
        return Result.success("解绑成功");
    }
    
    /**
     * 第三方登录
     */
    @Override
    public Result<Object> thirdPartyLogin(ThirdPartyLoginDTO loginDTO) {
        // TODO: 实现第三方登录逻辑
        return Result.success("登录成功");
    }
    
    @Override
    public void recordLoginLog(Integer userId, HttpServletRequest request) {
        // 实现记录登录日志逻辑
        UserLoginLog loginLog = new UserLoginLog();
        loginLog.setLogId(UUID.randomUUID().toString());
        loginLog.setUserId(userId);
        loginLog.setLoginTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        loginLog.setLoginIp(getIpAddress(request));
        loginLog.setDeviceInfo(request.getHeader("User-Agent"));
        
        loginLogMapper.insert(loginLog);
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
} 