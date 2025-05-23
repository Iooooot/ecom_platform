package com.db.ecom_platform.entity.vo;

import com.db.ecom_platform.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单视图对象
 */
@Data
public class OrderVO {
    
    private String orderId;          // 订单ID
    private BigDecimal totalAmount;  // 订单总金额
    private BigDecimal discountAmount; // 优惠金额
    private BigDecimal shippingFee;  // 运费
    private BigDecimal paymentAmount; // 实付金额
    private Integer status;          // 订单状态
    private String statusDesc;       // 状态描述
    private String paymentMethod;    // 支付方式
    private String paymentTime;      // 支付时间
    private String shippingTime;     // 发货时间
    private String completionTime;   // 完成时间
    private String cancellationTime; // 取消时间
    private String createTime;       // 创建时间
    private String notes;            // 订单备注
    private String trackingNumber;   // 物流单号
    private String shippingCompany;  // 物流公司
    
    private List<OrderItem> orderItems; // 订单项列表
    private AddressVO address;       // 收货地址
    
    private Boolean canCancel;       // 是否可取消
    private Boolean canPay;          // 是否可支付
    private Boolean isExpired;       // 是否已过期
    private Boolean hasRefund;       // 是否有退款申请
    
    // 如果有倒计时功能，可以添加剩余支付时间
    private Long remainingPaymentTime; // 剩余支付时间（毫秒）
} 