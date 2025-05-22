package com.db.ecom_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.db.ecom_platform.entity.Coupon;
import com.db.ecom_platform.entity.vo.CouponVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 优惠券数据访问接口
 */
@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {
    
    /**
     * 获取优惠券统计信息
     * @param couponId 优惠券ID
     * @return 统计信息
     */
    Map<String, Object> getCouponStats(@Param("couponId") String couponId);
    
    /**
     * 查询优惠券列表
     * @param page 分页对象
     * @param type 优惠券类型
     * @param status 优惠券状态
     * @param keyword 搜索关键词
     * @return 优惠券列表
     */
    Page<CouponVO> listCoupons(
            Page<CouponVO> page,
            @Param("type") String type,
            @Param("status") Integer status,
            @Param("keyword") String keyword
    );
    
    /**
     * 检查优惠券是否存在
     * @param couponId 优惠券ID
     * @return 存在数量
     */
    Integer checkCouponExists(@Param("couponId") String couponId);
    
    /**
     * 获取用户可用的优惠券列表
     * @param userId 用户ID
     * @return 优惠券列表
     */
    List<CouponVO> getUserAvailableCoupons(@Param("userId") Integer userId);
    
    /**
     * 获取用户所有优惠券列表
     * @param userId 用户ID
     * @param status 优惠券状态
     * @return 优惠券列表
     */
    List<CouponVO> getUserCoupons(
            @Param("userId") Integer userId,
            @Param("status") Integer status
    );
    
    /**
     * 更新优惠券状态
     * @param couponId 优惠券ID
     * @param status 状态
     * @return 更新结果
     */
    int updateCouponStatus(
            @Param("couponId") String couponId,
            @Param("status") Integer status
    );
} 