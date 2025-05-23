package com.db.ecom_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.db.ecom_platform.entity.Refund;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 退款申请数据访问接口
 */
@Mapper
public interface RefundMapper extends BaseMapper<Refund> {
    
    /**
     * 根据用户ID查询退款申请列表
     * @param userId 用户ID
     * @return 退款申请列表
     */
    List<Refund> selectByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据订单ID查询退款申请
     * @param orderId 订单ID
     * @return 退款申请
     */
    Refund selectByOrderId(@Param("orderId") String orderId);
    
    /**
     * 管理员查询退款申请列表（分页）
     * @param page 分页参数
     * @param userId 用户ID（可选）
     * @param status 状态（可选）
     * @param keyword 关键词搜索（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分页退款申请列表
     */
    IPage<Refund> selectRefundsForAdmin(Page<Refund> page, 
                                      @Param("userId") Integer userId,
                                      @Param("status") Integer status,
                                      @Param("keyword") String keyword,
                                      @Param("startTime") Date startTime,
                                      @Param("endTime") Date endTime);
    
    /**
     * 更新退款申请状态
     * @param refundId 退款申请ID
     * @param oldStatus 原状态
     * @param newStatus 新状态
     * @param updateTime 更新时间
     * @return 更新行数
     */
    int updateRefundStatus(@Param("refundId") String refundId, 
                         @Param("oldStatus") Integer oldStatus, 
                         @Param("newStatus") Integer newStatus,
                         @Param("updateTime") Date updateTime);
} 