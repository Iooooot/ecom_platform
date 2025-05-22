package com.db.ecom_platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.db.ecom_platform.entity.Product;
import com.db.ecom_platform.entity.dto.CouponDTO;
import com.db.ecom_platform.entity.dto.CouponProductDTO;
import com.db.ecom_platform.entity.dto.UserCouponDTO;
import com.db.ecom_platform.entity.vo.CouponVO;
import com.db.ecom_platform.utils.Result;

import java.util.List;

/**
 * 优惠券服务接口
 */
public interface CouponService {
    
    /**
     * 获取优惠券列表
     * @param page 页码
     * @param size 每页大小
     * @param type 优惠券类型
     * @param status 优惠券状态
     * @param keyword 搜索关键词
     * @return 优惠券列表
     */
    Page<CouponVO> listCoupons(Integer page, Integer size, String type, Integer status, String keyword);
    
    /**
     * 添加优惠券
     * @param couponDTO 优惠券信息
     * @return 添加结果
     */
    Result<?> addCoupon(CouponDTO couponDTO);
    
    /**
     * 修改优惠券
     * @param couponDTO 优惠券信息
     * @return 修改结果
     */
    Result<?> updateCoupon(CouponDTO couponDTO);
    
    /**
     * 删除优惠券
     * @param couponId 优惠券ID
     * @return 删除结果
     */
    Result<?> deleteCoupon(String couponId);
    
    /**
     * 获取优惠券详情
     * @param couponId 优惠券ID
     * @return 优惠券详情
     */
    Result<CouponVO> getCouponDetail(String couponId);
    
    /**
     * 发放优惠券给用户
     * @param userCouponDTO 用户优惠券信息
     * @return 发放结果
     */
    Result<?> assignCouponsToUsers(UserCouponDTO userCouponDTO);
    
    /**
     * 获取用户可用的优惠券列表
     * @param userId 用户ID
     * @return 优惠券列表
     */
    List<CouponVO> getUserAvailableCoupons(Integer userId);
    
    /**
     * 获取用户所有优惠券列表
     * @param userId 用户ID
     * @param status 优惠券状态
     * @return 优惠券列表
     */
    List<CouponVO> getUserCoupons(Integer userId, Integer status);
    
    /**
     * 更新优惠券状态
     * @param couponId 优惠券ID
     * @param status 状态
     * @return 更新结果
     */
    Result<?> updateCouponStatus(String couponId, Integer status);
    
    /**
     * 取消优惠券分配给用户
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 取消结果
     */
    Result<?> unassignCouponFromUser(Integer userId, String couponId);
    
    /**
     * 绑定优惠券与商品关系
     * @param couponProductDTO 优惠券-商品关系信息
     * @return 绑定结果
     */
    Result<?> bindProductsToCoupon(CouponProductDTO couponProductDTO);
    
    /**
     * 解绑优惠券与商品关系
     * @param couponId 优惠券ID
     * @param productId 商品ID
     * @return 解绑结果
     */
    Result<?> unbindProductFromCoupon(String couponId, String productId);
    
    /**
     * 获取优惠券关联的商品列表
     * @param couponId 优惠券ID
     * @return 商品列表
     */
    Result<List<Product>> getCouponProducts(String couponId);
    
    /**
     * 检查商品是否可以使用优惠券
     * @param couponId 优惠券ID
     * @param productId 商品ID
     * @return 检查结果
     */
    Result<Boolean> checkProductCoupon(String couponId, String productId);
    
    /**
     * 获取商品可用的优惠券列表
     * @param productId 商品ID
     * @return 优惠券列表
     */
    List<CouponVO> getProductCoupons(String productId);
} 