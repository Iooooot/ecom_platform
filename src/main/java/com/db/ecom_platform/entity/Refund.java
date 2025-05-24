package com.db.ecom_platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 退款申请实体类
 */
@Data
@TableName("refunds")
public class Refund {
    
    @TableId(value = "refund_id", type = IdType.ASSIGN_UUID)
    private String refundId;          // 退款申请ID
    
    private String orderId;           // 订单ID
    private Integer userId;           // 用户ID
    private String reason;            // 退货原因
    private String description;       // 问题描述
    private String refundType;        // 退款类型：REFUND-仅退款，REFUND_WITH_RETURN-退货退款
    private BigDecimal refundAmount;  // 申请退款金额
    private BigDecimal actualAmount;  // 实际退款金额
    private String contactPhone;      // 联系电话
    private String images;            // 图片证明，JSON数组字符串
    private Integer status;           // 状态：0-待处理，1-同意退款，2-拒绝退款，3-已退款
    private String rejectReason;      // 拒绝原因
    private String adminRemarks;      // 管理员备注
    private Date createTime;          // 申请时间
    private Date updateTime;          // 更新时间
    private Date processTime;         // 处理时间
    
    @TableField(exist = false)
    private String statusDesc;        // 状态描述
    
    @TableField(exist = false)
    private Order order;              // 关联的订单
    
    /**
     * 获取状态描述
     */
    public String getStatusDesc() {
        if (status == null) return "未知状态";
        
        switch (status) {
            case 0: return "待处理";
            case 1: return "已同意";
            case 2: return "已拒绝";
            case 3: return "已退款";
            default: return "未知状态";
        }
    }
} 