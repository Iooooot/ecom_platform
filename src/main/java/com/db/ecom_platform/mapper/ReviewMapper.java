package com.db.ecom_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.db.ecom_platform.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    // 获取商品评价(联表查询，包含用户名和商品名称)
    Page<Map<String, Object>> getProductReviewsWithJoin(
            Page<Map<String, Object>> page,
            @Param("productId") String productId,
            @Param("type") Integer type
    );
    
    // 获取商品评价统计
    Map<String, Object> getProductReviewStats(@Param("productId") String productId);
    
    // 检查用户是否已评价某商品
    Integer checkUserReview(@Param("userId") String userId, @Param("productId") String productId);
    
    // 获取用户的所有评价
    List<Review> getUserReviews(@Param("userId") String userId);
} 