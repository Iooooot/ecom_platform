package com.db.ecom_platform.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.db.ecom_platform.entity.Product;
import com.db.ecom_platform.entity.Review;
import com.db.ecom_platform.entity.dto.AdditionalReviewDTO;
import com.db.ecom_platform.entity.dto.ReviewDTO;
import com.db.ecom_platform.entity.dto.ReviewQueryDTO;
import com.db.ecom_platform.mapper.ProductMapper;
import com.db.ecom_platform.mapper.ReviewMapper;
import com.db.ecom_platform.mapper.UserMapper;
import com.db.ecom_platform.service.ReviewService;
import com.db.ecom_platform.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Result<?> addReview(String userId, ReviewDTO reviewDTO) {
        // 检查是否已评价过该商品
        Integer count = reviewMapper.checkUserReview(userId, reviewDTO.getProductId());
        if (count != null && count > 0) {
            return Result.error("您已经评价过该商品");
        }
        
        // 获取商品信息，仅检查商品是否存在
        Product product = productMapper.getProductById(reviewDTO.getProductId());
        if (product == null) {
            return Result.error("商品不存在");
        }
        
        // 获取用户信息，仅检查用户是否存在
        com.db.ecom_platform.entity.User user = userMapper.selectById(Integer.valueOf(userId));
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 创建评价对象
        Review review = new Review();
        review.setReviewId(UUID.randomUUID().toString());
        review.setProductId(product.getProductId());
        review.setUserId(userId);
        review.setRating(reviewDTO.getRating());
        review.setContent(reviewDTO.getContent());
        review.setImages(reviewDTO.getImages());
        review.setCreateTime(new Date());
        review.setUpdateTime(new Date());
        
        // 保存评价
        reviewMapper.insert(review);
        
        return Result.success("评价成功", review.getReviewId());
    }

    @Override
    @Transactional
    public Result<?> deleteReview(String userId, String reviewId) {
        // 查找评价
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            return Result.error("评价不存在");
        }

        // 验证是否是本人的评价
        if (!review.getUserId().equals(userId)) {
            return Result.error("只能删除自己的评价");
        }

        // 删除评价
        int result = reviewMapper.deleteById(reviewId);

        if (result > 0) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    @Override
    @Transactional
    public Result<?> addAdditionalReview(String userId, AdditionalReviewDTO additionalReviewDTO) {
        // 查找原评价
        Review review = reviewMapper.selectById(additionalReviewDTO.getReviewId());
        if (review == null) {
            return Result.error("评价不存在");
        }
        
        // 验证是否是本人的评价
        if (!review.getUserId().equals(userId)) {
            return Result.error("只能追评自己的评价");
        }
        
        // 检查是否已有追评
        if (review.getAdditionalReview() != null && !review.getAdditionalReview().isEmpty()) {
            return Result.error("已经追评过，不能重复追评");
        }
        
        // 更新追评内容
        review.setAdditionalReview(additionalReviewDTO.getContent());
        review.setAdditionalReviewTime(new Date());
        review.setUpdateTime(new Date());
        
        reviewMapper.updateById(review);
        
        return Result.success("追评成功");
    }

    
    @Override
    public Page<Map<String, Object>> getProductReviewsWithJoin(ReviewQueryDTO queryDTO) {
        Page<Map<String, Object>> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        return reviewMapper.getProductReviewsWithJoin(page, queryDTO.getProductId(), queryDTO.getType());
    }

    @Override
    public List<Review> getUserReviews(String userId) {
        List<Review> reviews = reviewMapper.getUserReviews(userId);
        
        // 填充商品和用户信息
        if (reviews != null) {
            for (Review review : reviews) {
                fillProductAndUserInfo(review);
            }
        }
        
        return reviews;
    }
    
    /**
     * 填充商品和用户信息
     */
    private void fillProductAndUserInfo(Review review) {
        // 填充商品信息
        Product product = productMapper.getProductById(review.getProductId());
        if (product != null) {
            review.setProductName(product.getName());
        }
        
        // 填充用户信息
        com.db.ecom_platform.entity.User user = userMapper.selectById(Integer.valueOf(review.getUserId()));
        if (user != null) {
            review.setUsername(user.getUsername());
        }
    }
} 