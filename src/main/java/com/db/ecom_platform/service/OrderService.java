package com.db.ecom_platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.db.ecom_platform.entity.Order;
import com.db.ecom_platform.entity.dto.OrderQueryDTO;
import com.db.ecom_platform.entity.dto.PaymentDTO;
import com.db.ecom_platform.entity.vo.OrderVO;
import com.db.ecom_platform.utils.Result;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 获取用户订单列表（分页）
     * @param userId 用户ID
     * @param queryDTO 查询条件
     * @return 分页订单列表
     */
    IPage<OrderVO> getUserOrders(Integer userId, OrderQueryDTO queryDTO);
    
    /**
     * 获取订单详情
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 订单详情
     */
    Result<OrderVO> getOrderDetail(Integer userId, String orderId);
    
    /**
     * 取消订单
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 操作结果
     */
    Result<?> cancelOrder(Integer userId, String orderId);
    
    /**
     * 处理过期订单（定时任务）
     * 查找过期未支付的订单并自动取消
     * @return 处理的订单数量
     */
    int processExpiredOrders();
    
    /**
     * 获取各状态订单数量
     * @param userId 用户ID
     * @return 各状态订单数量
     */
    Result<List<Integer>> getOrderStatusCount(Integer userId);
    
    /**
     * 模拟支付订单
     * @param userId 用户ID
     * @param paymentDTO 支付信息
     * @return 支付结果
     */
    Result<?> payOrder(Integer userId, PaymentDTO paymentDTO);
} 