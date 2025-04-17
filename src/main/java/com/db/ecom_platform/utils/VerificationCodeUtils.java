package com.db.ecom_platform.utils;

import com.db.ecom_platform.entity.VerificationCode;
import com.db.ecom_platform.mapper.VerificationCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

/**
 * 验证码工具类
 * 提供验证码生成、验证和管理功能
 */
@Component
public class VerificationCodeUtils {

    @Autowired
    private VerificationCodeMapper verificationCodeMapper;

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    TemplateEngine templateEngine;
    
    // 验证码长度
    private static final int CODE_LENGTH = 6;
    
    // 验证码有效期（分钟）
    private static final int EXPIRATION_MINUTES = 2;
    
    /**
     * 生成随机验证码
     * @return 生成的验证码
     */
    public static String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
    
    /**
     * 生成并发送验证码
     * @param target 目标（手机号或邮箱）
     * @param type 类型（0:手机，1:邮箱）
     * @return 是否发送成功
     */
    public boolean generateAndSendCode(String target, Integer type) {
        // 生成随机验证码
        String code = generateCode();
        
        // 保存验证码
        boolean saved = saveCode(target, code, type);
        if (!saved) {
            return false;
        }
        
        // 发送验证码
        return sendCode(target, code, type);
    }
    
    /**
     * 发送验证码
     * @param target 目标（手机号或邮箱）
     * @param code 验证码
     * @param type 类型（0:手机，1:邮箱）
     * @return 是否发送成功
     */
    public boolean sendCode(String target, String code, Integer type) {
        try {
            // 根据类型发送短信或邮件
            if (type == 0) {
                // 发送短信
                // 实际实现中，这里应该调用短信服务API
                sendSmsCode(target, code);
            } else if (type == 1) {
                // 发送邮件
                // 实际实现中，这里应该调用邮件服务API
                sendEmailCode(target, code);
            } else {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 发送短信验证码
     * @param phone 手机号码
     * @param code 验证码
     */
    private void sendSmsCode(String phone, String code) {
        // TODO: 实现短信发送逻辑，调用第三方短信服务
        System.out.println("向手机号 " + phone + " 发送验证码: " + code);
    }
    
    /**
     * 发送邮件验证码
     * @param email 邮箱地址
     * @param code 验证码
     */
    private void sendEmailCode(String email, String code) {
        // TODO: 实现邮件发送逻辑，调用邮件服务
        System.out.println("向邮箱 " + email + " 发送验证码: " + code);
        // 创建邮件模板正文
        Context context = new Context();
        context.setVariable("emailCode", Arrays.asList(code.split("")));
        // 将模块引擎内容解析成html字符串
        String emailContent = templateEngine.process("emailCodeTemplates", context);
        MimeMessage message = mailSender.createMimeMessage();
        try {
            // true表示需要创建一个multipart message
            MimeMessageHelper helper=new MimeMessageHelper(message,true);
            // TODO: 需要改成自己邮箱
            helper.setFrom("1369281736@qq.com");
            helper.setTo(email);
            helper.setSubject("邮箱验证码");
            helper.setText(emailContent,true);
            mailSender.send(message);
            // 将code存mysql
            saveCode(email,code,1);
        }catch (MessagingException e) {
            throw new RuntimeException("邮箱验证码发送错误！");
        }

    }
    
    /**
     * 保存验证码到数据库
     * @param target 目标（手机号或邮箱）
     * @param code 验证码
     * @param type 类型（0:手机，1:邮箱）
     * @return 是否保存成功
     */
    public boolean saveCode(String target, String code, Integer type) {
        try {
            // 先删除该目标的旧验证码
            verificationCodeMapper.deleteByTarget(target);
            
            // 创建新的验证码记录
            VerificationCode verificationCode = new VerificationCode();
            verificationCode.setTarget(target);
            verificationCode.setCode(code);
            verificationCode.setType(type);
            verificationCode.setCreateTime(LocalDateTime.now());
            verificationCode.setExpiryTime(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES));
            verificationCode.setUsed(false);
            
            // 保存到数据库
            verificationCodeMapper.insert(verificationCode);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 验证验证码是否正确
     * @param target 目标（手机号或邮箱）
     * @param code 验证码
     * @return 是否验证成功
     */
    public boolean verifyCode(String target, String code) {
        try {
            // 从数据库获取验证码
            VerificationCode verificationCode = verificationCodeMapper.getByTarget(target);
            
            if (verificationCode == null) {
                return false; // 验证码不存在
            }
            
            // 检查验证码是否已过期
            if (LocalDateTime.now().isAfter(verificationCode.getExpiryTime())) {
                return false; // 验证码已过期
            }
            
            // 检查验证码是否已使用
            if (verificationCode.isUsed()) {
                return false; // 验证码已使用
            }
            
            // 检查验证码是否匹配
            if (!code.equals(verificationCode.getCode())) {
                return false; // 验证码不匹配
            }
            
            // 验证成功，标记验证码已使用
            verificationCode.setUsed(true);
            verificationCodeMapper.updateUsedStatus(verificationCode.getId(), true);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 验证验证码是否正确但不标记为已使用
     * @param target 目标（手机号或邮箱）
     * @param code 验证码
     * @return 是否验证成功
     */
    public boolean verifyCodeWithoutMarkUsed(String target, String code) {
        try {
            // 从数据库获取验证码
            VerificationCode verificationCode = verificationCodeMapper.getByTarget(target);
            
            if (verificationCode == null) {
                return false; // 验证码不存在
            }
            
            // 检查验证码是否已过期
            if (LocalDateTime.now().isAfter(verificationCode.getExpiryTime())) {
                return false; // 验证码已过期
            }
            
            // 检查验证码是否已使用
            if (verificationCode.isUsed()) {
                return false; // 验证码已使用
            }
            
            // 检查验证码是否匹配
            return code.equals(verificationCode.getCode());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 标记验证码为已使用
     * @param target 目标（手机号或邮箱）
     * @return 是否标记成功
     */
    public boolean markCodeAsUsed(String target) {
        try {
            VerificationCode verificationCode = verificationCodeMapper.getByTarget(target);
            if (verificationCode != null) {
                verificationCodeMapper.updateUsedStatus(verificationCode.getId(), true);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 清理过期的验证码
     * @return 清理的记录数
     */
    public int cleanExpiredCodes() {
        try {
            return verificationCodeMapper.deleteExpiredCodes(LocalDateTime.now());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
} 