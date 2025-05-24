package com.db.ecom_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.db.ecom_platform.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单项数据访问接口
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    
    /**
     * 根据订单ID查询订单项列表
     * @param orderId 订单ID
     * @return 订单项列表
     */
    List<OrderItem> selectByOrderId(@Param("orderId") String orderId);
    
    /**
     * 批量插入订单项
     * @param orderItems 订单项列表
     * @return 插入行数
     */
    int batchInsert(@Param("orderItems") List<OrderItem> orderItems);
    
    /**
     * 获取指定时间范围内的热销商品
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 数量限制
     * @return 热销商品列表
     */
    List<Map<String, Object>> getHotProductsInTimeRange(@Param("startTime") LocalDateTime startTime, 
                                                      @Param("endTime") LocalDateTime endTime,
                                                      @Param("limit") int limit);
    
    /**
     * 获取指定时间范围内按分类统计的销售数据
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分类销售数据
     */
    List<Map<String, Object>> getSalesByCategoryInTimeRange(@Param("startTime") LocalDateTime startTime, 
                                                          @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据订单ID列表获取订单项
     * @param orderIds 订单ID列表
     * @return 订单项列表
     */
    List<OrderItem> getOrderItemsByOrderIds(@Param("orderIds") List<String> orderIds);
} 