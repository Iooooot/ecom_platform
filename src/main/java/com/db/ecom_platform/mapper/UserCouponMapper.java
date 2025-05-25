package com.db.ecom_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.db.ecom_platform.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户-优惠券关系数据访问接口
 */
@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {
    
    /**
     * 批量发放优惠券给用户
     * @param userCoupons 用户优惠券列表
     * @return 影响行数
     */
    int batchInsertUserCoupons(@Param("list") List<UserCoupon> userCoupons);
    
    /**
     * 检查用户是否已拥有指定优惠券
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 存在数量
     */
    Integer checkUserCouponExists(
            @Param("userId") Integer userId,
            @Param("couponId") String couponId
    );
    
    /**
     * 更新用户优惠券状态
     * @param id 用户优惠券ID
     * @param status 状态
     * @param useTime 使用时间
     * @param orderId 订单ID
     * @return 影响行数
     */
    int updateUserCouponStatus(
            @Param("id") Integer id,
            @Param("status") Integer status, 
            @Param("useTime") String useTime,
            @Param("orderId") String orderId
    );
    
    /**
     * 获取优惠券的分配用户数量
     * @param couponId 优惠券ID
     * @return 用户数量
     */
    Integer countCouponUsers(@Param("couponId") String couponId);
    
    /**
     * 获取优惠券的已使用数量
     * @param couponId 优惠券ID
     * @return 已使用数量
     */
    Integer countCouponUsed(@Param("couponId") String couponId);
    
    /**
     * 删除用户优惠券关系
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 影响行数
     */
    int deleteUserCoupon(
            @Param("userId") Integer userId,
            @Param("couponId") String couponId
    );
    
    /**
     * 删除优惠券的所有用户关系
     * @param couponId 优惠券ID
     * @return 影响行数
     */
    int deleteByCouponId(@Param("couponId") String couponId);

    /**
     * 获取用户可用优惠券列表
     * @param userId 用户ID
     * @return 可用优惠券列表
     */
    List<UserCoupon> getUserAvailableCoupons(@Param("userId") Integer userId);
    
    /**
     * 获取已分配指定优惠券的用户列表
     * @param couponId 优惠券ID
     * @return 用户优惠券关系列表
     */
    List<UserCoupon> getCouponUsers(@Param("couponId") String couponId);
} 