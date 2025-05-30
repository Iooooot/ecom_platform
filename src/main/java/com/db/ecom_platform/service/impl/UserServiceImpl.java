package com.db.ecom_platform.service.impl;

import com.alibaba.fastjson.JSON;
import com.db.ecom_platform.entity.User;
import com.db.ecom_platform.entity.UserLoginLog;
import com.db.ecom_platform.entity.UserOperationLog;
import com.db.ecom_platform.entity.dto.*;
import com.db.ecom_platform.entity.vo.UserVO;
import com.db.ecom_platform.mapper.UserLoginLogMapper;
import com.db.ecom_platform.mapper.UserMapper;
import com.db.ecom_platform.mapper.UserOperationLogMapper;
import com.db.ecom_platform.service.UserService;
import com.db.ecom_platform.utils.JwtUtils;
import com.db.ecom_platform.utils.Result;
import com.db.ecom_platform.utils.UserUtils;
import com.db.ecom_platform.utils.VerificationCodeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserLoginLogMapper loginLogMapper;
    
    @Autowired
    private VerificationCodeUtils verificationCodeUtils;
    
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserOperationLogMapper userOperationLogMapper;
    
    /**
     * 用户注册
     */
    @Override
    @Transactional
    public Result<Object> register(UserRegisterDTO registerDTO) {
        // 1. 参数验证
        if (registerDTO.getUsername() == null || registerDTO.getPassword() == null) {
            return Result.error("用户名和密码不能为空");
        }

        // 确保至少有一种联系方式
        if (registerDTO.getEmail() == null && registerDTO.getPhone() == null) {
            return Result.error("邮箱和手机号不能同时为空");
        }

        // 2. 验证用户名是否已存在
        User existingUsername = userMapper.getUserByUsername(registerDTO.getUsername());
        if (existingUsername != null) {
            return Result.error("用户名已被使用");
        }

        // 3. 验证邮箱和手机号是否已被注册
        if (registerDTO.getEmail() != null) {
            User existingEmail = userMapper.getUserByEmail(registerDTO.getEmail());
            if (existingEmail != null) {
                return Result.error("邮箱已被注册");
            }
            
            // 邮箱验证码校验
            if (registerDTO.getVerificationCode() != null) {
                boolean verified = verificationCodeUtils.verifyCodeWithoutMarkUsed(
                        registerDTO.getEmail(), 
                        registerDTO.getVerificationCode()
                );
                
                if (!verified) {
                    return Result.error("邮箱验证码不正确或已过期");
                }
            }
        }

        if (registerDTO.getPhone() != null) {
            User existingPhone = userMapper.getUserByPhone(registerDTO.getPhone());
            if (existingPhone != null) {
                return Result.error("手机号已被注册");
            }
            
            // 手机验证码校验
            if (registerDTO.getVerificationCode() != null) {
                boolean verified = verificationCodeUtils.verifyCodeWithoutMarkUsed(
                        registerDTO.getPhone(), 
                        registerDTO.getVerificationCode()
                );
                
                if (!verified) {
                    return Result.error("手机验证码不正确或已过期");
                }
            }
        }

        // 4. 创建用户对象
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        // 实际生产中应该对密码进行加密处理
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setStatus(1); // 1: 正常状态
        user.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        user.setUpdateTime(user.getCreateTime());

        // 5. 保存用户
        int result = userMapper.insert(user);
        
        if (result > 0) {
            // 标记验证码为已使用
            if (registerDTO.getEmail() != null && registerDTO.getVerificationCode() != null) {
                verificationCodeUtils.markCodeAsUsed(registerDTO.getEmail());
            } else if (registerDTO.getPhone() != null && registerDTO.getVerificationCode() != null) {
                verificationCodeUtils.markCodeAsUsed(registerDTO.getPhone());
            }
            
            return Result.success("注册成功", user.getUserId());
        } else {
            return Result.error("注册失败");
        }
    }
    
    /**
     * 用户登录
     */
    @Override
    public Result<Object> login(UserLoginDTO loginDTO, HttpServletRequest request) {
        // 1. 参数验证
        if (loginDTO.getUsername() == null || loginDTO.getPassword() == null) {
            return Result.error("用户名和密码不能为空");
        }

        // 2. 尝试使用用户名、邮箱或手机号查找用户
        User user = userMapper.getUserByUsername(loginDTO.getUsername());
        
        if (user == null) {
            // 尝试通过邮箱查找
            user = userMapper.getUserByEmail(loginDTO.getUsername());
        }
        
        if (user == null) {
            // 尝试通过手机号查找
            user = userMapper.getUserByPhone(loginDTO.getUsername());
        }

        // 3. 验证用户是否存在
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 4. 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return Result.error("密码错误");
        }

        // 5. 验证用户状态
        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }

        // 6. 记录登录日志
        try {
            recordLoginLog(user.getUserId(), request);
        } catch (Exception e) {
            // 日志记录失败不影响登录流程
            System.err.println("记录登录日志失败: " + e.getMessage());
        }

        // 7. 更新用户上次登录时间
        User userToUpdate = new User();
        userToUpdate.setUserId(user.getUserId());
        userToUpdate.setLastLoginTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        userMapper.updateById(userToUpdate);

        // 8. 生成JWT令牌
        String token = jwtUtils.generateToken(user.getUserId());
        
        // 9. 构建返回数据
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        resultMap.put("userId", user.getUserId());
        resultMap.put("username", user.getUsername());
        
        // 10. 返回登录成功结果
        return Result.success("登录成功", resultMap);
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
    public Result<Object> forgotPassword(ResetPasswordDTO forgotPasswordDTO,HttpServletRequest request) {
        return resetPassword(forgotPasswordDTO,request);
    }
    
    /**
     * 重置密码（同时支持忘记密码和修改密码）
     */
    @Override
    @Transactional
    public Result<Object> resetPassword(ResetPasswordDTO resetPasswordDTO, HttpServletRequest request) {
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
        userToUpdate.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        userToUpdate.setUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        int result = userMapper.updateById(userToUpdate);
        
        if (result > 0) {
            // 记录敏感操作日志
            UserOperationLog operationLog = new UserOperationLog();
            operationLog.setUserId(user.getUserId());
            operationLog.setOperationType("password_change");
            operationLog.setOperationDesc("用户密码修改");
            operationLog.setOperationTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            operationLog.setOperationIp(UserUtils.getIpAddress(request));

            // 增加敏感信息记录
            Map<String, Object> params = new HashMap<>();
            params.put("type", resetPasswordDTO.getType());
            params.put("target", resetPasswordDTO.getTarget());
            operationLog.setOperationParams(JSON.toJSONString(params));

            userOperationLogMapper.insertOperationLog(operationLog);
            
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
        try {
            // 1. 验证用户是否存在
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 2. 根据类型更新用户信息
            User userToUpdate = new User();
            userToUpdate.setUserId(userId);
            
            if (bindAccountDTO.getType() == 0) {
                // 绑定手机号
                // 检查手机号是否已被其他用户使用
                User existingUser = userMapper.getUserByPhone(bindAccountDTO.getTarget());
                if (existingUser != null && !existingUser.getUserId().equals(userId)) {
                    return Result.error("该手机号已被其他账号使用");
                }
                userToUpdate.setPhone(bindAccountDTO.getTarget());
            } else if (bindAccountDTO.getType() == 1) {
                // 绑定邮箱
                // 检查邮箱是否已被其他用户使用
                User existingUser = userMapper.getUserByEmail(bindAccountDTO.getTarget());
                if (existingUser != null && !existingUser.getUserId().equals(userId)) {
                    return Result.error("该邮箱已被其他账号使用");
                }
                userToUpdate.setEmail(bindAccountDTO.getTarget());
            } else {
                return Result.error("无效的绑定类型");
            }
            
            // 3. 更新用户信息
            userToUpdate.setUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            int result = userMapper.updateById(userToUpdate);
            
            if (result > 0) {
                return Result.success("绑定成功");
            } else {
                return Result.error("绑定失败");
            }
        } catch (Exception e) {
            return Result.error("绑定过程中发生错误：" + e.getMessage());
        }
    }
    
    /**
     * 解绑手机/邮箱
     */
    @Override
    @Transactional
    public Result<Object> unbindAccount(Integer userId, UnbindAccountDTO unbindAccountDTO) {
        try {
            // 1. 验证用户是否存在
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            
            // 2. 根据类型更新用户信息
            User userToUpdate = new User();
            userToUpdate.setUserId(userId);
            
            if (unbindAccountDTO.getType() == 0) {
                // 解绑手机号
                if (!unbindAccountDTO.getTarget().equals(user.getPhone())) {
                    return Result.error("当前手机号与账号绑定的手机号不匹配");
                }
                userToUpdate.setPhone("");
            } else if (unbindAccountDTO.getType() == 1) {
                // 解绑邮箱
                if (!unbindAccountDTO.getTarget().equals(user.getEmail())) {
                    return Result.error("当前邮箱与账号绑定的邮箱不匹配");
                }
                userToUpdate.setEmail("");
            } else {
                return Result.error("无效的解绑类型");
            }
            
            // 3. 更新用户信息
            userToUpdate.setUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            int result = userMapper.updateById(userToUpdate);
            
            if (result > 0) {
                return Result.success("解绑成功");
            } else {
                return Result.error("解绑失败");
            }
        } catch (Exception e) {
            return Result.error("解绑过程中发生错误：" + e.getMessage());
        }
    }
    
    /**
     * 绑定第三方账号
     */
    @Override
    @Transactional
    public Result<Object> bindThirdParty(Integer userId, ThirdPartyBindDTO bindDTO) {
        try {
            // 1. 验证用户是否存在
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 2. 根据第三方类型更新用户表
            if ("alipay".equals(bindDTO.getType())) {
                // 检查该支付宝账号是否已被其他用户绑定
                User existingUser = userMapper.getUserByAlipayId(bindDTO.getOpenId());
                if (existingUser != null && !existingUser.getUserId().equals(userId)) {
                    return Result.error("该支付宝账号已被其他用户绑定");
                }

                // 创建更新对象
                User updateUser = new User();
                updateUser.setUserId(userId);
                updateUser.setAlipayId(bindDTO.getOpenId()); // 设置支付宝ID
                
                // 执行更新
                int result = userMapper.updateById(updateUser);
                
                if (result > 0) {
                    return Result.success("绑定支付宝账号成功");
                } else {
                    return Result.error("绑定支付宝账号失败");
                }
            } else {
                return Result.error("不支持的第三方账号类型");
            }
        } catch (Exception e) {
            return Result.error("绑定过程中发生错误：" + e.getMessage());
        }
    }
    
    /**
     * 解绑第三方账号
     */
    @Override
    @Transactional
    public Result<Object> unbindThirdParty(Integer userId, ThirdPartyUnbindDTO unbindDTO) {
        try {
            // 1. 验证用户是否存在
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 2. 根据第三方类型更新用户表
            if ("alipay".equals(unbindDTO.getType())) {
                // 创建更新对象
                User updateUser = new User();
                updateUser.setUserId(userId);
                updateUser.setAlipayId(""); // 清空支付宝ID
                
                // 执行更新
                int result = userMapper.updateById(updateUser);
                
                if (result > 0) {
                    return Result.success("解绑支付宝账号成功");
                } else {
                    return Result.error("解绑支付宝账号失败");
                }
            } else {
                return Result.error("不支持的第三方账号类型");
            }
        } catch (Exception e) {
            return Result.error("解绑过程中发生错误：" + e.getMessage());
        }
    }
    
    /**
     * 第三方登录
     */
    @Override
    @Transactional
    public Result<Object> thirdPartyLogin(ThirdPartyLoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // 1. 根据第三方类型和openId查询用户
            User user = null;
            if ("alipay".equals(loginDTO.getType())) {
                user = userMapper.getUserByAlipayId(loginDTO.getOpenId());
            } else {
                return Result.error("不支持的第三方账号类型");
            }

            // 2. 判断用户是否存在
            if (user == null) {
                return Result.error("未绑定账号，请先注册账号并绑定支付宝");
            }

            // 3. 验证用户状态
            if (user.getStatus() == 0) {
                return Result.error("账号已被禁用");
            }

            // 4. 记录登录日志
            try {
                recordLoginLog(user.getUserId(), request);
            } catch (Exception e) {
                // 日志记录失败不影响登录流程
                System.err.println("记录登录日志失败: " + e.getMessage());
            }

            // 5. 更新用户上次登录时间
            User userToUpdate = new User();
            userToUpdate.setUserId(user.getUserId());
            userToUpdate.setLastLoginTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            userMapper.updateById(userToUpdate);

            // 6. 生成JWT令牌
            String token = jwtUtils.generateToken(user.getUserId());

            // 构建重定向URL，将登录信息作为参数
            String redirectUrl = String.format(
                    "/static/index.html?token=%s&username=%s&userId=%d",
                    URLEncoder.encode(token, "UTF-8"),
                    URLEncoder.encode(user.getUsername(), "UTF-8"),
                    user.getUserId()
            );

            // 重定向到首页
            response.sendRedirect(redirectUrl);
            // 8. 返回登录成功结果
            return Result.success("登录成功", null);
        } catch (Exception e) {
            response.sendRedirect("/static/auth.html?error=" + URLEncoder.encode("第三方登录失败：" + e.getMessage(), "UTF-8"));
            return Result.error("第三方登录失败：" + e.getMessage());
        }
    }
    
    @Override
    public void recordLoginLog(Integer userId, HttpServletRequest request) {
        // 实现记录登录日志逻辑
        UserLoginLog loginLog = new UserLoginLog();
        loginLog.setLogId(UUID.randomUUID().toString());
        loginLog.setUserId(userId);
        loginLog.setLoginTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        loginLog.setLoginIp(UserUtils.getIpAddress(request));
        loginLog.setDeviceInfo(request.getHeader("User-Agent"));
        
        loginLogMapper.insert(loginLog);
    }
    

} 