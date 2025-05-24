package com.db.ecom_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.db.ecom_platform.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 订单数据访问接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    /**
     * 根据用户ID查询订单列表（分页）
     * @param page 分页参数
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @param keyword 关键词搜索（订单号、商品名）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分页订单列表
     */
    IPage<Order> selectOrdersByUserId(Page<Order> page, 
                                     @Param("userId") Integer userId,
                                     @Param("status") Integer status,
                                     @Param("keyword") String keyword,
                                     @Param("startTime") Date startTime,
                                     @Param("endTime") Date endTime);
    
    /**
     * 查询指定状态的订单数量
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单数量
     */
    int countOrdersByStatus(@Param("userId") Integer userId, @Param("status") Integer status);
    
    /**
     * 查询过期未支付的订单
     * @param currentTime 当前时间
     * @return 过期未支付的订单列表
     */
    List<Order> selectExpiredUnpaidOrders(@Param("currentTime") Date currentTime);
    
    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param oldStatus 原状态
     * @param newStatus 新状态
     * @param updateTime 更新时间
     * @return 更新行数
     */
    int updateOrderStatus(@Param("orderId") String orderId, 
                        @Param("oldStatus") Integer oldStatus, 
                        @Param("newStatus") Integer newStatus,
                        @Param("updateTime") Date updateTime);
    
    /**
     * 取消订单
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param cancellationTime 取消时间
     * @param updateTime 更新时间
     * @return 更新行数
     */
    int cancelOrder(@Param("orderId") String orderId, 
                  @Param("userId") Integer userId,
                  @Param("cancellationTime") Date cancellationTime,
                  @Param("updateTime") Date updateTime);
    
    /**
     * 管理员查询订单列表（分页）
     * @param page 分页参数
     * @param userId 用户ID（可选）
     * @param status 状态（可选）
     * @param paymentMethod 支付方式（可选）
     * @param keyword 关键词搜索（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param hasRefund 是否有退款申请（可选）
     * @return 分页订单列表
     */
    IPage<Order> selectOrdersForAdmin(Page<Order> page, 
                                     @Param("userId") Integer userId,
                                     @Param("status") Integer status,
                                     @Param("paymentMethod") String paymentMethod,
                                     @Param("keyword") String keyword,
                                     @Param("startTime") Date startTime,
                                     @Param("endTime") Date endTime,
                                     @Param("hasRefund") Boolean hasRefund);
    
    /**
     * 标记订单为已发货
     * @param orderId 订单ID
     * @param trackingNumber 物流单号
     * @param shippingCompany 物流公司
     * @param shippingTime 发货时间
     * @param updateTime 更新时间
     * @return 更新行数
     */
    int markOrderAsShipped(@Param("orderId") String orderId, 
                          @Param("trackingNumber") String trackingNumber,
                          @Param("shippingCompany") String shippingCompany,
                          @Param("shippingTime") Date shippingTime,
                          @Param("updateTime") Date updateTime);
} 