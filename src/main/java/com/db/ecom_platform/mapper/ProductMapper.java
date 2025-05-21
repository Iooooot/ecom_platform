package com.db.ecom_platform.mapper;

import com.db.ecom_platform.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ProductMapper {
    // 根据条件搜索商品
    List<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("categoryId") String categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minSales") Integer minSales,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit
    );
    
    // 获取搜索结果总数
    int countSearchProducts(
            @Param("keyword") String keyword,
            @Param("categoryId") String categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minSales") Integer minSales
    );
    
    // 根据ID获取商品
    Product getProductById(@Param("productId") String productId);
    
    // 根据分类ID获取商品列表
    // 注：此方法功能已被search接口替代，保留以备将来可能需要单独查询
    List<Product> getProductsByCategoryId(@Param("categoryId") String categoryId);
} 