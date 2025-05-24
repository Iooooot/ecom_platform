package com.db.ecom_platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.db.ecom_platform.entity.Refund;
import com.db.ecom_platform.entity.dto.AdminOrderQueryDTO;
import com.db.ecom_platform.entity.dto.RefundApplyDTO;
import com.db.ecom_platform.entity.dto.RefundProcessDTO;
import com.db.ecom_platform.utils.Result;

import java.util.List;

/**
 * 退款服务接口
 */
public interface RefundService {
    
    /**
     * 用户申请退款
     * @param userId 用户ID
     * @param refundApplyDTO 退款申请信息
     * @return 操作结果
     */
    Result<?> applyRefund(Integer userId, RefundApplyDTO refundApplyDTO);
    
    /**
     * 获取用户的退款申请列表
     * @param userId 用户ID
     * @return 退款申请列表
     */
    Result<List<Refund>> getUserRefunds(Integer userId);
    
    /**
     * 获取退款申请详情
     * @param userId 用户ID
     * @param refundId 退款申请ID
     * @return 退款申请详情
     */
    Result<Refund> getRefundDetail(Integer userId, String refundId);
    
    /**
     * 取消退款申请
     * @param userId 用户ID
     * @param refundId 退款申请ID
     * @return 操作结果
     */
    Result<?> cancelRefund(Integer userId, String refundId);
    
    /**
     * 管理员查询退款申请列表
     * @param queryDTO 查询条件
     * @return 分页退款申请列表
     */
    IPage<Refund> getRefundsForAdmin(AdminOrderQueryDTO queryDTO);
    
    /**
     * 管理员处理退款申请
     * @param refundProcessDTO 处理信息
     * @return 操作结果
     */
    Result<?> processRefund(RefundProcessDTO refundProcessDTO);
    
    /**
     * 检查订单是否有退款申请
     * @param orderId 订单ID
     * @return 是否有退款申请
     */
    boolean hasRefundApplication(String orderId);
} 