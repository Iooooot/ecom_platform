package com.db.ecom_platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.db.ecom_platform.entity.Product;
import com.db.ecom_platform.entity.dto.CouponDTO;
import com.db.ecom_platform.entity.dto.CouponProductDTO;
import com.db.ecom_platform.entity.dto.UserCouponDTO;
import com.db.ecom_platform.entity.vo.CouponVO;
import com.db.ecom_platform.service.CouponService;
import com.db.ecom_platform.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员优惠券控制器
 */
@Api(tags = "管理员-优惠券管理")
@RestController
@RequestMapping("/api/admin/coupons")
public class AdminCouponController {
    
    @Autowired
    private CouponService couponService;
    
    /**
     * 获取优惠券列表
     */
    @ApiOperation(value = "获取优惠券列表", notes = "分页获取优惠券列表，支持按类型、状态和关键词搜索")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "page", value = "页码", required = false, dataType = "int", paramType = "query", defaultValue = "1"),
        @ApiImplicitParam(name = "size", value = "每页大小", required = false, dataType = "int", paramType = "query", defaultValue = "10"),
        @ApiImplicitParam(name = "type", value = "优惠券类型", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "status", value = "优惠券状态", required = false, dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "keyword", value = "搜索关键词", required = false, dataType = "string", paramType = "query")
    })
    @GetMapping
    public Result<Page<CouponVO>> listCoupons(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        Page<CouponVO> coupons = couponService.listCoupons(page, size, type, status, keyword);
        return Result.success(coupons);
    }
    
    /**
     * 添加优惠券
     */
    @ApiOperation(value = "添加优惠券", notes = "添加新的优惠券")
    @PostMapping
    public Result<?> addCoupon(@RequestBody CouponDTO couponDTO) {
        return couponService.addCoupon(couponDTO);
    }
    
    /**
     * 修改优惠券
     */
    @ApiOperation(value = "修改优惠券", notes = "修改指定ID的优惠券信息")
    @PutMapping("/{couponId}")
    public Result<?> updateCoupon(@PathVariable String couponId, @RequestBody CouponDTO couponDTO) {
        couponDTO.setCouponId(couponId);
        return couponService.updateCoupon(couponDTO);
    }
    
    /**
     * 删除优惠券
     */
    @ApiOperation(value = "删除优惠券", notes = "删除指定ID的优惠券")
    @DeleteMapping("/{couponId}")
    public Result<?> deleteCoupon(@PathVariable String couponId) {
        return couponService.deleteCoupon(couponId);
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
     * 更新优惠券状态
     */
    @ApiOperation(value = "更新优惠券状态", notes = "更新指定ID的优惠券状态")
    @PutMapping("/{couponId}/status/{status}")
    public Result<?> updateCouponStatus(@PathVariable String couponId, @PathVariable Integer status) {
        return couponService.updateCouponStatus(couponId, status);
    }
    
    /**
     * 发放优惠券给用户
     */
    @ApiOperation(value = "发放优惠券给用户", notes = "将优惠券发放给指定用户")
    @PostMapping("/assign")
    public Result<?> assignCouponsToUsers(@RequestBody UserCouponDTO userCouponDTO) {
        return couponService.assignCouponsToUsers(userCouponDTO);
    }
    
    /**
     * 取消用户优惠券
     */
    @ApiOperation(value = "取消用户优惠券", notes = "取消指定用户的优惠券")
    @DeleteMapping("/user/{userId}/coupon/{couponId}")
    public Result<?> unassignCouponFromUser(@PathVariable Integer userId, @PathVariable String couponId) {
        return couponService.unassignCouponFromUser(userId, couponId);
    }
    
    /**
     * 绑定商品到优惠券
     */
    @ApiOperation(value = "绑定商品到优惠券", notes = "将指定商品绑定到优惠券，使优惠券只对这些商品有效")
    @PostMapping("/{couponId}/products")
    public Result<?> bindProductsToCoupon(@PathVariable String couponId, @RequestBody CouponProductDTO couponProductDTO) {
        couponProductDTO.setCouponId(couponId);
        return couponService.bindProductsToCoupon(couponProductDTO);
    }
    
    /**
     * 解绑优惠券与商品关系
     */
    @ApiOperation(value = "解绑优惠券与商品关系", notes = "解除指定优惠券与商品的绑定关系")
    @DeleteMapping("/{couponId}/products/{productId}")
    public Result<?> unbindProductFromCoupon(@PathVariable String couponId, @PathVariable String productId) {
        return couponService.unbindProductFromCoupon(couponId, productId);
    }
    
    /**
     * 获取优惠券关联的商品列表
     */
    @ApiOperation(value = "获取优惠券关联的商品列表", notes = "获取指定优惠券关联的所有商品列表")
    @GetMapping("/{couponId}/products")
    public Result<List<Product>> getCouponProducts(@PathVariable String couponId) {
        return couponService.getCouponProducts(couponId);
    }
    
    /**
     * 检查商品是否可以使用优惠券
     */
    @ApiOperation(value = "检查商品是否可以使用优惠券", notes = "检查指定商品是否可以使用指定优惠券")
    @GetMapping("/{couponId}/products/{productId}/check")
    public Result<Boolean> checkProductCoupon(@PathVariable String couponId, @PathVariable String productId) {
        return couponService.checkProductCoupon(couponId, productId);
    }
    
    /**
     * 获取优惠券已分配的用户列表
     */
    @ApiOperation(value = "获取优惠券已分配的用户列表", notes = "获取指定优惠券已分配的所有用户列表")
    @GetMapping("/{couponId}/users")
    public Result<?> getCouponUsers(@PathVariable String couponId) {
        return couponService.getCouponUsers(couponId);
    }
} 