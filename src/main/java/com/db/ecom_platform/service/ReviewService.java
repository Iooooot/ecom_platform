package com.db.ecom_platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.db.ecom_platform.entity.Review;
import com.db.ecom_platform.entity.dto.AdditionalReviewDTO;
import com.db.ecom_platform.entity.dto.ReviewDTO;
import com.db.ecom_platform.entity.dto.ReviewQueryDTO;
import com.db.ecom_platform.utils.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface ReviewService {
    
    /**
     * 添加商品评价
     * @param userId 用户ID
     * @param reviewDTO 评价信息
     * @return 结果
     */
    Result<?> addReview(String userId, ReviewDTO reviewDTO);
    
    /**
     * 添加追评
     * @param userId 用户ID
     * @param additionalReviewDTO 追评信息
     * @return 结果
     */
    Result<?> addAdditionalReview(String userId, AdditionalReviewDTO additionalReviewDTO);
    
    /**
     * 按条件查询商品评价（使用SQL联表直接获取用户名和商品名）
     * @param queryDTO 查询条件
     * @return 评价分页列表
     */
    Page<Map<String, Object>> getProductReviewsWithJoin(ReviewQueryDTO queryDTO);
    
    /**
     * 获取用户的所有评价
     * @param userId 用户ID
     * @return 评价列表
     */
    List<Review> getUserReviews(String userId);
    
    /**
     * 删除评价
     * @param userId 用户ID
     * @param reviewId 评价ID
     * @param request HTTP请求
     * @return 删除结果
     */
    Result<?> deleteReview(String userId, String reviewId, HttpServletRequest request);
} 