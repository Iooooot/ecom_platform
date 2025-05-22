package com.db.ecom_platform.controller;

import com.db.ecom_platform.entity.vo.CouponVO;
import com.db.ecom_platform.service.CouponService;
import com.db.ecom_platform.utils.Result;
import com.db.ecom_platform.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户优惠券控制器
 */
@Api(tags = "用户-优惠券管理")
@RestController
@RequestMapping("/api/coupons")
public class UserCouponController {
    
    @Autowired
    private CouponService couponService;
    
    /**
     * 获取用户可用的优惠券列表
     */
    @ApiOperation(value = "获取用户可用的优惠券列表", notes = "获取当前登录用户可以使用的优惠券列表")
    @GetMapping("/available")
    public Result<List<CouponVO>> getUserAvailableCoupons() {
        Integer userId = UserUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("请先登录");
        }
        List<CouponVO> coupons = couponService.getUserAvailableCoupons(userId);
        return Result.success(coupons);
    }
    
    /**
     * 获取用户所有优惠券
     */
    @ApiOperation(value = "获取用户所有优惠券", notes = "获取当前登录用户的所有优惠券，支持按状态筛选")
    @ApiImplicitParam(name = "status", value = "优惠券状态：0-未使用，1-已使用，2-已过期", required = false, dataType = "int", paramType = "query")
    @GetMapping("/my")
    public Result<List<CouponVO>> getUserCoupons(@RequestParam(required = false) Integer status) {
        Integer userId = UserUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("请先登录");
        }
        List<CouponVO> coupons = couponService.getUserCoupons(userId, status);
        return Result.success(coupons);
    }
    
    /**
     * 获取优惠券详情
     */
    @ApiOperation(value = "获取优惠券详情", notes = "获取指定ID的优惠券详细信息")
    @GetMapping("/{couponId}")
    public Result<CouponVO> getCouponDetail(@PathVariable String couponId) {
        return couponService.getCouponDetail(couponId);
    }
    
    /**
     * 获取商品可用的优惠券列表
     */
    @ApiOperation(value = "获取商品可用的优惠券", notes = "获取指定商品可以使用的优惠券列表")
    @ApiImplicitParam(name = "productId", value = "商品ID", required = true, dataType = "string", paramType = "path")
    @GetMapping("/products/{productId}")
    public Result<List<CouponVO>> getProductCoupons(@PathVariable String productId) {
        List<CouponVO> coupons = couponService.getProductCoupons(productId);
        return Result.success(coupons);
    }
} 