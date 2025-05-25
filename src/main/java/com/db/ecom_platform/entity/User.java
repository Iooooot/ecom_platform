package com.db.ecom_platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户实体类
 */
@Data
@TableName("users")
public class User {
    
    /**
     * 用户ID（主键）
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;
    
    /**
     * 兼容前端的id字段
     */
    @TableField(exist = false)
    private Integer id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码（加密存储）
     */
    private String password;
    
    /**
     * 电子邮箱
     */
    private String email;
    
    /**
     * 手机号码
     */
    private String phone;
    
    /**
     * 年龄
     */
    private Integer age;
    
    /**
     * 性别
     */
    private String gender;
    
    /**
     * 是否为VIP会员
     */
    @TableField("is_vip")
    private Boolean isVip;
    
    /**
     * 创建时间
     */
    private String createTime;
    
    /**
     * 更新时间
     */
    private String updateTime;
    
    /**
     * 用户角色（0-普通用户，1-VIP用户，2-管理员）
     */
    private Integer role;
    
    /**
     * 账号是否被禁用
     */
    @TableField("is_disabled")
    private Boolean isDisabled;
    
    /**
     * 微信OpenID
     */
    private String wxOpenId;
    
    /**
     * 支付宝ID
     */
    private String alipayId;
    
    /**
     * 最后登录时间
     */
    private String lastLoginTime;
    
    /**
     * 最后登录IP
     */
    private String lastLoginIp;
    
    /**
     * 登录次数
     */
    private Integer loginCount;
    
    /**
     * 头像URL
     */
    private String avatarUrl;
    
    /**
     * 个人简介
     */
    private String bio;
    
    /**
     * 账号状态（0-正常，1-锁定，2-已注销）
     */
    private Integer status;
    
    // 自定义getter方法，确保返回id时与userId同步
    public Integer getId() {
        return this.userId;
    }
    
    // 自定义setter方法，确保设置id时也同步更新userId
    public void setId(Integer id) {
        this.id = id;
        this.userId = id;
    }
} 