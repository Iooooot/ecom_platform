package com.db.ecom_platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 验证码实体类
 */
@Data
@TableName("verification_code")
public class VerificationCode {
    
    @TableId(type = IdType.AUTO)
    private Integer id;         // 主键ID
    
    private String target;      // 目标（手机号或邮箱）
    
    private String code;        // 验证码
    
    private Integer type;       // 类型（0:手机，1:邮箱）
    
    private LocalDateTime createTime;  // 创建时间
    
    private LocalDateTime expiryTime;  // 过期时间
    
    private boolean used;       // 是否已使用
} 