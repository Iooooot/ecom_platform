package com.db.ecom_platform.controller;

import com.db.ecom_platform.entity.dto.CartItemDTO;
import com.db.ecom_platform.entity.dto.CartOperationDTO;
import com.db.ecom_platform.entity.vo.CartVO;
import com.db.ecom_platform.service.CartService;
import com.db.ecom_platform.utils.Result;
import com.db.ecom_platform.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 */
@Api(tags = "购物车管理")
@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    /**
     * 获取用户购物车
     * @return 购物车信息
     */
    @ApiOperation(value = "获取用户购物车", notes = "获取当前登录用户的购物车信息，包含有效商品和失效商品")
    @GetMapping("/list")
    public Result<CartVO> getUserCart() {
        Integer userId = UserUtils.getCurrentUserId();
        CartVO cartVO = cartService.getUserCart(userId);
        return Result.success(cartVO);
    }
    
    /**
     * 添加商品到购物车
     * @param cartItemDTO 购物车项信息
     * @return 添加结果
     */
    @ApiOperation(value = "添加商品到购物车", notes = "将商品添加到当前登录用户的购物车，如果购物车中已存在该商品则更新数量")
    @ApiImplicitParam(name = "cartItemDTO", value = "购物车项信息", required = true, dataType = "CartItemDTO", paramType = "body")
    @PostMapping("/add")
    public Result<?> addToCart(@RequestBody CartItemDTO cartItemDTO) {
        Integer userId = UserUtils.getCurrentUserId();
        return cartService.addToCart(userId, cartItemDTO);
    }
    
    /**
     * 更新购物车项数量
     * @param itemId 购物车项ID
     * @param quantity 数量
     * @return 更新结果
     */
    @ApiOperation(value = "更新购物车项数量", notes = "更新当前登录用户的购物车中某个商品的数量")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "itemId", value = "购物车项ID", required = true, dataType = "Long", paramType = "path"),
        @ApiImplicitParam(name = "quantity", value = "商品数量", required = true, dataType = "Integer", paramType = "query")
    })
    @PutMapping("/item/{itemId}")
    public Result<?> updateCartItemQuantity(
            @PathVariable Long itemId, 
            @RequestParam Integer quantity) {
        Integer userId = UserUtils.getCurrentUserId();
        return cartService.updateCartItemQuantity(userId, itemId, quantity);
    }
    
    /**
     * 批量更新购物车项
     * @param cartItemDTOList 购物车项列表
     * @return 更新结果
     */
    @ApiOperation(value = "批量更新购物车项", notes = "批量更新当前登录用户的购物车中多个商品的信息")
    @ApiImplicitParam(name = "cartItemDTOList", value = "购物车项列表", required = true, dataType = "List<CartItemDTO>", paramType = "body")
    @PutMapping("/batch-update")
    public Result<?> batchUpdateCartItems(@RequestBody List<CartItemDTO> cartItemDTOList) {
        Integer userId = UserUtils.getCurrentUserId();
        return cartService.batchUpdateCartItems(userId, cartItemDTOList);
    }
    
    /**
     * 删除购物车项
     * @param itemId 购物车项ID
     * @return 删除结果
     */
    @ApiOperation(value = "删除购物车项", notes = "删除当前登录用户购物车中的某个商品")
    @ApiImplicitParam(name = "itemId", value = "购物车项ID", required = true, dataType = "Long", paramType = "path")
    @DeleteMapping("/item/{itemId}")
    public Result<?> removeCartItem(@PathVariable Long itemId) {
        Integer userId = UserUtils.getCurrentUserId();
        return cartService.removeCartItem(userId, itemId);
    }
    
    /**
     * 批量操作购物车项（删除、选中、取消选中）
     * @param operationDTO 操作信息
     * @return 操作结果
     */
    @ApiOperation(value = "批量操作购物车项", notes = "批量操作当前登录用户购物车中的商品，支持删除、选中、取消选中")
    @ApiImplicitParam(name = "operationDTO", value = "操作信息", required = true, dataType = "CartOperationDTO", paramType = "body")
    @PostMapping("/batch-operation")
    public Result<?> batchOperateCartItems(@RequestBody CartOperationDTO operationDTO) {
        Integer userId = UserUtils.getCurrentUserId();
        return cartService.batchOperateCartItems(userId, operationDTO);
    }
    
    /**
     * 清空购物车
     * @return 清空结果
     */
    @ApiOperation(value = "清空购物车", notes = "清空当前登录用户的整个购物车")
    @DeleteMapping("/clear")
    public Result<?> clearCart() {
        Integer userId = UserUtils.getCurrentUserId();
        return cartService.clearCart(userId);
    }
    
    /**
     * 清除失效商品
     * @return 清除结果
     */
    @ApiOperation(value = "清除失效商品", notes = "清除当前登录用户购物车中的所有失效商品（库存不足或已下架）")
    @DeleteMapping("/clear-invalid")
    public Result<?> clearInvalidItems() {
        Integer userId = UserUtils.getCurrentUserId();
        return cartService.clearInvalidItems(userId);
    }
    
    /**
     * 计算购物车中选中商品的优惠信息
     * @return 计算结果
     */
    @ApiOperation(value = "计算优惠信息", notes = "计算当前登录用户购物车中选中商品的优惠信息，包括适用的优惠券、折扣金额等")
    @GetMapping("/calculate-discount")
    public Result<?> calculateDiscount() {
        Integer userId = UserUtils.getCurrentUserId();
        return cartService.calculateDiscount(userId);
    }
} 