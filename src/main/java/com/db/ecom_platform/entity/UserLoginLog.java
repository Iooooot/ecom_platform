package com.db.ecom_platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户登录日志实体类
 */
@Data
@TableName("user_login_logs")
public class UserLoginLog {
    
    @TableId(value = "log_id", type = IdType.ASSIGN_UUID)
    private String logId;         // 日志ID
    
    private Integer userId;       // 用户ID
    private String loginTime;     // 登录时间
    private String loginIp;       // 登录IP
    private String deviceInfo;    // 设备信息
} 