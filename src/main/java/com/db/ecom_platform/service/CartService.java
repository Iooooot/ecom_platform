package com.db.ecom_platform.service;

import com.db.ecom_platform.entity.dto.CartItemDTO;
import com.db.ecom_platform.entity.dto.CartOperationDTO;
import com.db.ecom_platform.entity.vo.CartItemVO;
import com.db.ecom_platform.entity.vo.CartVO;
import com.db.ecom_platform.utils.Result;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {
    
    /**
     * 获取用户购物车
     * @param userId 用户ID
     * @return 购物车信息
     */
    CartVO getUserCart(Integer userId);
    
    /**
     * 根据ID列表获取购物车项
     * @param userId 用户ID
     * @param cartItemIds 购物车项ID列表
     * @return 购物车项信息列表
     */
    List<CartItemVO> getCartItemsByIds(Integer userId, List<Long> cartItemIds);
    
    /**
     * 添加商品到购物车
     * @param userId 用户ID
     * @param cartItemDTO 购物车项信息
     * @return 添加结果
     */
    Result<?> addToCart(Integer userId, CartItemDTO cartItemDTO);
    
    /**
     * 更新购物车项数量
     * @param userId 用户ID
     * @param itemId 购物车项ID
     * @param quantity 数量
     * @return 更新结果
     */
    Result<?> updateCartItemQuantity(Integer userId, Long itemId, Integer quantity);
    
    /**
     * 批量更新购物车项数量
     * @param userId 用户ID
     * @param cartItemDTOList 购物车项列表
     * @return 更新结果
     */
    Result<?> batchUpdateCartItems(Integer userId, List<CartItemDTO> cartItemDTOList);
    
    /**
     * 删除购物车项
     * @param userId 用户ID
     * @param itemId 购物车项ID
     * @return 删除结果
     */
    Result<?> removeCartItem(Integer userId, Long itemId);
    
    /**
     * 批量操作购物车项（删除、选中、取消选中）
     * @param userId 用户ID
     * @param operationDTO 操作信息
     * @return 操作结果
     */
    Result<?> batchOperateCartItems(Integer userId, CartOperationDTO operationDTO);
    
    /**
     * 清空购物车
     * @param userId 用户ID
     * @return 清空结果
     */
    Result<?> clearCart(Integer userId);
    
    /**
     * 清除失效商品
     * @param userId 用户ID
     * @return 清除结果
     */
    Result<?> clearInvalidItems(Integer userId);
    
    /**
     * 计算购物车中选中商品的优惠信息
     * @param userId 用户ID
     * @return 计算结果
     */
    Result<?> calculateDiscount(Integer userId);
} 