package com.db.ecom_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.db.ecom_platform.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    
    /**
     * 获取指定时间范围内的订单总金额
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单总金额
     */
    BigDecimal getSumAmountByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                     @Param("endTime") LocalDateTime endTime);
    
    /**
     * 获取指定时间范围内的订单数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单数量
     */
    Integer getCountByTimeRange(@Param("startTime") LocalDateTime startTime, 
                              @Param("endTime") LocalDateTime endTime);
    
    /**
     * 获取指定时间范围内的销售趋势数据
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param timeUnit 时间单位（HOUR/DAY/MONTH/YEAR）
     * @return 销售趋势数据
     */
    List<Map<String, Object>> getSalesTrendInTimeRange(@Param("startTime") LocalDateTime startTime, 
                                                    @Param("endTime") LocalDateTime endTime,
                                                    @Param("timeUnit") String timeUnit);
    
    /**
     * 获取指定时间范围内的订单趋势数据
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param timeUnit 时间单位（HOUR/DAY/MONTH/YEAR）
     * @return 订单趋势数据
     */
    List<Map<String, Object>> getOrderTrendInTimeRange(@Param("startTime") LocalDateTime startTime, 
                                                    @Param("endTime") LocalDateTime endTime,
                                                    @Param("timeUnit") String timeUnit);
    
    /**
     * 获取指定时间范围内的订单
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单列表
     */
    List<Order> getOrdersInTimeRange(@Param("startTime") LocalDateTime startTime, 
                                    @Param("endTime") LocalDateTime endTime);
    
    /**
     * 获取指定时间范围内的地区销售数据
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 地区销售数据
     */
    List<Map<String, Object>> getRegionSalesInTimeRange(@Param("startTime") LocalDateTime startTime, 
                                                     @Param("endTime") LocalDateTime endTime);
    
    /**
     * 获取指定时间范围内的地区订单数据
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 地区订单数据
     */
    List<Map<String, Object>> getRegionOrdersInTimeRange(@Param("startTime") LocalDateTime startTime, 
                                                      @Param("endTime") LocalDateTime endTime);
    
    /**
     * 获取指定时间范围内的订单状态分布
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单状态分布
     */
    List<Map<String, Object>> getOrderStatusDistribution(@Param("startTime") LocalDateTime startTime, 
                                                 @Param("endTime") LocalDateTime endTime);
    
    /**
     * 获取指定时间范围内的支付方式分布
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 支付方式分布
     */
    List<Map<String, Object>> getPaymentMethodDistribution(@Param("startTime") LocalDateTime startTime, 
                                                   @Param("endTime") LocalDateTime endTime);
    
    /**
     * 获取指定时间范围内的活跃用户数
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 活跃用户数
     */
    Integer getActiveUsersCountInTimeRange(@Param("startTime") LocalDateTime startTime, 
                                        @Param("endTime") LocalDateTime endTime);
} 