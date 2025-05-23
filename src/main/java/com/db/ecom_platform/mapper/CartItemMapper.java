package com.db.ecom_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.db.ecom_platform.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车项数据访问接口
 */
@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
    
    /**
     * 根据用户ID和商品ID查询购物车项
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 购物车项
     */
    CartItem getCartItemByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") String productId);
    
    /**
     * 批量删除购物车项
     * @param userId 用户ID
     * @param itemIds 购物车项ID列表
     * @return 影响行数
     */
    int batchDeleteCartItems(@Param("userId") Integer userId, @Param("itemIds") List<Long> itemIds);
    
    /**
     * 清空用户购物车
     * @param userId 用户ID
     * @return 影响行数
     */
    int clearUserCart(@Param("userId") Integer userId);
    
    /**
     * 获取用户购物车中的有效商品
     * @param userId 用户ID
     * @return 购物车项列表
     */
    List<CartItem> getValidCartItems(@Param("userId") Integer userId);
    
    /**
     * 获取用户购物车中的失效商品
     * @param userId 用户ID
     * @return 购物车项列表
     */
    List<CartItem> getInvalidCartItems(@Param("userId") Integer userId);
} 