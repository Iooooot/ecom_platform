package com.db.ecom_platform.entity.dto;

import lombok.Data;

/**
 * 用户信息更新数据传输对象
 */
@Data
public class UserUpdateDTO {
    
    private String username;  // 用户名
    private String email;     // 电子邮箱
    private String phone;     // 手机号码
    private Integer age;      // 年龄
    private String gender;    // 性别
} 