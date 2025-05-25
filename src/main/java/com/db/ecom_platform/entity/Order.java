package com.db.ecom_platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单实体类
 */
@Data
@TableName("orders")
public class Order {
    
    @TableId(value = "order_id")
    private String orderId;          // 订单ID
    
    private Integer userId;          // 用户ID
    private BigDecimal totalAmount;  // 订单总金额
    private BigDecimal discountAmount; // 优惠金额
    private BigDecimal shippingFee;  // 运费
    private BigDecimal paymentAmount; // 实付金额
    private String couponId;         // 使用的优惠券ID
    private String addressId;        // 收货地址ID
    private Integer status;          // 订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消，5-已退款
    private String paymentMethod;    // 支付方式
    private Date paymentTime;        // 支付时间
    private Date shippingTime;       // 发货时间
    private Date completionTime;     // 完成时间
    private Date cancellationTime;   // 取消时间
    private Date expirationTime;     // 订单过期时间（未支付订单的有效期）
    private Date createTime;         // 创建时间
    private Date updateTime;         // 更新时间
    private String notes;            // 订单备注
    private String trackingNumber;   // 物流单号
    private String shippingCompany;  // 物流公司
    private String adminRemarks;     // 管理员备注
    
    // 非数据库字段
    @TableField(exist = false)
    private List<OrderItem> orderItems; // 订单项列表
    
    @TableField(exist = false)
    private Address address;         // 收货地址
    
    @TableField(exist = false)
    private Coupon coupon;           // 优惠券信息
    
    @TableField(exist = false)
    private String statusDesc;       // 状态描述
    
    @TableField(exist = false)
    private String userName;         // 用户名称（仅管理员查询使用）
    
    @TableField(exist = false)
    private Boolean hasRefund;       // 是否有退款申请（仅管理员查询使用）
    
    @TableField(exist = false)
    private Boolean isReviewed;      // 是否已评价
    
    /**
     * 获取订单状态描述
     */
    public String getStatusDesc() {
        if (status == null) {
            return "未知状态";
        }
        
        switch (status) {
            case 0:
                return "待支付";
            case 1:
                return "已支付";
            case 2:
                return "已发货";
            case 3:
                return "已完成";
            case 4:
                return "已取消";
            case 5:
                return "已退款";
            default:
                return "未知状态";
        }
    }
    
    /**
     * 判断订单是否可取消
     * 只有待支付状态的订单可以取消
     */
    public boolean canCancel() {
        return status != null && status == 0;
    }
    
    /**
     * 判断订单是否可支付
     * 只有待支付状态且未过期的订单可以支付
     */
    public boolean canPay() {
        if (status == null || status != 0) {
            return false;
        }
        // 检查是否已过期
        return expirationTime != null && expirationTime.after(new Date());
    }
    
    /**
     * 判断订单是否已过期
     */
    public boolean isExpired() {
        return status != null && status == 0 && expirationTime != null && expirationTime.before(new Date());
    }
} 