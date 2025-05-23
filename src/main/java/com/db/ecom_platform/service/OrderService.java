package com.db.ecom_platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.db.ecom_platform.entity.Order;
import com.db.ecom_platform.entity.dto.AdminOrderQueryDTO;
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
    
    /**
     * 管理员获取订单列表（分页）
     * @param queryDTO 查询条件
     * @return 分页订单列表
     */
    IPage<Order> getOrdersForAdmin(AdminOrderQueryDTO queryDTO);
    
    /**
     * 管理员获取订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    Result<Order> getOrderDetailForAdmin(String orderId);
    
    /**
     * 管理员标记订单为已发货
     * @param orderId 订单ID
     * @param trackingNumber 物流单号（可选）
     * @param shippingCompany 物流公司（可选）
     * @return 操作结果
     */
    Result<?> markOrderAsShipped(String orderId, String trackingNumber, String shippingCompany);
    
    /**
     * 管理员修改订单状态
     * @param orderId 订单ID
     * @param newStatus 新状态
     * @param remarks 备注
     * @return 操作结果
     */
    Result<?> updateOrderStatus(String orderId, Integer newStatus, String remarks);
} 