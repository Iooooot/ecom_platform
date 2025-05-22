package com.db.ecom_platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户操作日志实体类
 */
@Data
@TableName("user_operation_logs")
public class UserOperationLog {
    
    @TableId(value = "log_id", type = IdType.ASSIGN_UUID)
    private String logId;         // 日志ID
    
    private Integer userId;       // 用户ID
    private String operationType; // 操作类型（如login, register, order, payment等）
    private String operationDesc; // 操作描述
    private String operationTime; // 操作时间
    private String operationIp;   // 操作IP地址
    private String operationParams; // 操作相关参数（如原值、新值等）
} 