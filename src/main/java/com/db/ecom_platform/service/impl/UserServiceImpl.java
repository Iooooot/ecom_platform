package com.db.ecom_platform.service.impl;

import com.db.ecom_platform.entity.User;
import com.db.ecom_platform.entity.UserLoginLog;
import com.db.ecom_platform.entity.dto.*;
import com.db.ecom_platform.entity.vo.UserVO;
import com.db.ecom_platform.mapper.UserLoginLogMapper;
import com.db.ecom_platform.mapper.UserMapper;
import com.db.ecom_platform.service.UserService;
import com.db.ecom_platform.utils.Result;
import com.db.ecom_platform.utils.VerificationCodeUtils;
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
    
    @Autowired
    private VerificationCodeUtils verificationCodeUtils;
    
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
    public Result<Object> sendVerificationCode(String target, Integer type) {
        // 使用VerificationCodeUtils的generateAndSendCode方法
        boolean success = verificationCodeUtils.generateAndSendCode(target, type);
        if (!success) {
            return Result.error("验证码发送失败");
        }
        return Result.success("验证码已发送");
    }
    
    /**
     * 验证验证码
     */
    @Override
    public boolean verifyCode(String target, String code) {
        // 使用VerificationCodeUtils验证
        return verificationCodeUtils.verifyCode(target, code);
    }
    
    /**
     * 忘记密码
     */
    @Override
    @Transactional
    public Result<Object> forgotPassword(ResetPasswordDTO forgotPasswordDTO) {
        return resetPassword(forgotPasswordDTO);
    }
    
    /**
     * 重置密码（同时支持忘记密码和修改密码）
     */
    @Override
    @Transactional
    public Result<Object> resetPassword(ResetPasswordDTO resetPasswordDTO) {
        // 查找用户
        User user = null;
        if (resetPasswordDTO.getType() == 0) {
            // 通过手机号查找
            user = userMapper.getUserByPhone(resetPasswordDTO.getTarget());
        } else if (resetPasswordDTO.getType() == 1) {
            // 通过邮箱查找
            user = userMapper.getUserByEmail(resetPasswordDTO.getTarget());
        } else {
            return Result.error("无效的验证类型");
        }
        
        if (user == null) {
            return Result.error("未找到对应的用户账号");
        }
        
        // 更新密码
        User userToUpdate = new User();
        userToUpdate.setUserId(user.getUserId());
        userToUpdate.setPassword(resetPasswordDTO.getNewPassword()); // 实际应用中应对密码进行加密处理
        userToUpdate.setUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        int result = userMapper.updateById(userToUpdate);
        
        if (result > 0) {
            return Result.success("密码重置成功");
        } else {
            return Result.error("密码重置失败");
        }
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
        // 1. 查询用户是否存在
        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        
        // 2. 验证用户名是否重复（如果修改了用户名）
        if (updateDTO.getUsername() != null && !updateDTO.getUsername().equals(existingUser.getUsername())) {
            // 检查用户名是否已被其他用户使用
            User existingUsername = userMapper.getUserByUsername(updateDTO.getUsername());
            if (existingUsername != null && !existingUsername.getUserId().equals(userId)) {
                return Result.error("用户名已被使用");
            }
        }
        
        // 3. 验证邮箱是否重复（如果修改了邮箱）
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(existingUser.getEmail())) {
            // 检查邮箱是否已被其他用户使用
            User existingEmail = userMapper.getUserByEmail(updateDTO.getEmail());
            if (existingEmail != null && !existingEmail.getUserId().equals(userId)) {
                return Result.error("邮箱已被其他账号使用");
            }
        }
        
        // 4. 验证手机号是否重复（如果修改了手机号）
        if (updateDTO.getPhone() != null && !updateDTO.getPhone().equals(existingUser.getPhone())) {
            // 检查手机号是否已被其他用户使用
            User existingPhone = userMapper.getUserByPhone(updateDTO.getPhone());
            if (existingPhone != null && !existingPhone.getUserId().equals(userId)) {
                return Result.error("手机号已被其他账号使用");
            }
        }
        
        // 5. 更新用户信息
        User userToUpdate = new User();
        userToUpdate.setUserId(userId);
        
        // 只更新不为null的字段
        if (updateDTO.getUsername() != null) {
            userToUpdate.setUsername(updateDTO.getUsername());
        }
        
        if (updateDTO.getEmail() != null) {
            userToUpdate.setEmail(updateDTO.getEmail());
        }
        
        if (updateDTO.getPhone() != null) {
            userToUpdate.setPhone(updateDTO.getPhone());
        }
        
        if (updateDTO.getAge() != null) {
            userToUpdate.setAge(updateDTO.getAge());
        }
        
        if (updateDTO.getGender() != null) {
            userToUpdate.setGender(updateDTO.getGender());
        }
        
        // 更新时间
        userToUpdate.setUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // 使用正确的方法执行更新
        int result = userMapper.updateUserInfo(userToUpdate);
        
        if (result > 0) {
            return Result.success("用户信息更新成功");
        } else {
            return Result.error("用户信息更新失败");
        }
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