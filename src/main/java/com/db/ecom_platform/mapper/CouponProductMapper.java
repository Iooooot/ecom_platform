package com.db.ecom_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.db.ecom_platform.entity.CouponProduct;
import com.db.ecom_platform.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 优惠券-商品关系数据访问接口
 */
@Mapper
public interface CouponProductMapper extends BaseMapper<CouponProduct> {
    
    /**
     * 批量添加优惠券-商品关系
     * @param couponProducts 优惠券-商品关系列表
     * @return 影响行数
     */
    int batchInsertCouponProducts(@Param("list") List<CouponProduct> couponProducts);
    
    /**
     * 删除优惠券关联的所有商品
     * @param couponId 优惠券ID
     * @return 影响行数
     */
    int deleteByCouponId(@Param("couponId") String couponId);
    
    /**
     * 删除优惠券与指定商品的关联
     * @param couponId 优惠券ID
     * @param productId 商品ID
     * @return 影响行数
     */
    int deleteCouponProduct(
            @Param("couponId") String couponId,
            @Param("productId") String productId
    );
    
    /**
     * 获取优惠券关联的商品列表
     * @param couponId 优惠券ID
     * @return 商品列表
     */
    List<Product> getCouponProducts(@Param("couponId") String couponId);
    
    /**
     * 检查优惠券是否关联了指定商品
     * @param couponId 优惠券ID
     * @param productId 商品ID
     * @return 是否关联
     */
    Integer checkCouponProduct(
            @Param("couponId") String couponId,
            @Param("productId") String productId
    );
    
    /**
     * 获取商品可用的优惠券列表
     * @param productId 商品ID
     * @return 优惠券ID列表
     */
    List<String> getProductCouponIds(@Param("productId") String productId);
    
    /**
     * 获取优惠券关联的商品数量
     * @param couponId 优惠券ID
     * @return 商品数量
     */
    Integer countCouponProducts(@Param("couponId") String couponId);
} 