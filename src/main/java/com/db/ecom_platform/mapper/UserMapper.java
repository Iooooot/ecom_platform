package com.db.ecom_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.db.ecom_platform.entity.ConsumptionStat;
import com.db.ecom_platform.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);
    
    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户信息
     */
    User getUserByEmail(String email);
    
    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户信息
     */
    User getUserByPhone(String phone);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 影响行数
     */
    int updateUserInfo(User user);
    
    /**
     * 更新密码
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 影响行数
     */
    int updatePassword(@Param("userId") Integer userId, @Param("newPassword") String newPassword);
    
    /**
     * 获取用户消费总额
     * @param userId 用户ID
     * @return 消费总额
     */
    Double getUserTotalConsumption(@Param("userId") Integer userId);
    
    /**
     * 获取用户消费总额（按时间范围）
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 消费总额
     */
    Double getUserTotalConsumption(@Param("userId") Integer userId, 
                                  @Param("startTime") String startTime, 
                                  @Param("endTime") String endTime);
    
    /**
     * 获取用户订单数量
     * @param userId 用户ID
     * @return 订单数量
     */
    Integer getUserOrderCount(@Param("userId") Integer userId);
    
    /**
     * 获取用户订单数量（按时间范围）
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单数量
     */
    Integer getUserOrderCount(@Param("userId") Integer userId, 
                            @Param("startTime") String startTime, 
                            @Param("endTime") String endTime);
    
    /**
     * 获取用户平均消费金额
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 平均消费金额
     */
    Double getUserAvgConsumption(@Param("userId") Integer userId, 
                               @Param("startTime") String startTime, 
                               @Param("endTime") String endTime);
    
    /**
     * 获取用户消费排名
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 排名（从1开始）
     */
    Integer getUserConsumptionRank(@Param("userId") Integer userId, 
                                 @Param("startTime") String startTime, 
                                 @Param("endTime") String endTime);
    
    /**
     * 获取有消费记录的总用户数
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 总用户数
     */
    Integer getTotalConsumptionUsers(@Param("startTime") String startTime, 
                                   @Param("endTime") String endTime);
    
    /**
     * 获取最高消费金额
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 最高消费金额
     */
    Double getMaxConsumptionAmount(@Param("startTime") String startTime, 
                                 @Param("endTime") String endTime);
    
    /**
     * 获取平均消费金额（所有用户）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 平均消费金额
     */
    Double getAvgConsumptionAmount(@Param("startTime") String startTime, 
                                 @Param("endTime") String endTime);
    
    /**
     * 查询消费统计
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 消费统计
     */
    ConsumptionStat getConsumptionStats(@Param("userId") Integer userId, 
                                       @Param("startTime") String startTime, 
                                       @Param("endTime") String endTime);
    
    /**
     * 查询消费趋势
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param timeUnit 时间单位（day/week/month/year）
     * @return 消费趋势
     */
    List<Map<String, Object>> getConsumptionTrend(@Param("userId") Integer userId, 
                                                 @Param("startTime") String startTime, 
                                                 @Param("endTime") String endTime, 
                                                 @Param("timeUnit") String timeUnit);
    
    /**
     * 查询分类消费
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分类消费
     */
    List<Map<String, Object>> getCategoryConsumption(@Param("userId") Integer userId, 
                                                    @Param("startTime") String startTime, 
                                                    @Param("endTime") String endTime);
} 