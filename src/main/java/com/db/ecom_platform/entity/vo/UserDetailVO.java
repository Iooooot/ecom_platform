package com.db.ecom_platform.entity.vo;

import lombok.Data;

/**
 * 用户详情视图对象
 */
@Data
public class UserDetailVO {
    
    private Integer userId;       // 用户ID
    private String username;      // 用户名
    private String email;         // 电子邮箱
    private String phone;         // 手机号码
    private Integer age;          // 年龄
    private String gender;        // 性别
    private Boolean isVip;        // 是否为VIP会员
    private String createTime;    // 创建时间
    private String updateTime;    // 更新时间
    private Integer role;         // 用户角色
    private Boolean isDisabled;   // 账号是否被禁用
    private Double totalConsumption; // 消费总额
    private Integer orderCount;      // 订单数量
} 