package com.db.ecom_platform.entity.vo;

import lombok.Data;

/**
 * 用户操作日志视图对象
 */
@Data
public class UserOperationLogVO {
    
    private String logId;         // 日志ID
    private Integer userId;       // 用户ID
    private String username;      // 用户名
    private String operationType; // 操作类型
    private String operationDesc; // 操作描述
    private String operationTime; // 操作时间
    private String operationIp;   // 操作IP
} 