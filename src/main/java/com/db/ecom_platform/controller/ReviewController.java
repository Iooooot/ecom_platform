package com.db.ecom_platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.db.ecom_platform.entity.Review;
import com.db.ecom_platform.entity.dto.AdditionalReviewDTO;
import com.db.ecom_platform.entity.dto.ReviewDTO;
import com.db.ecom_platform.entity.dto.ReviewQueryDTO;
import com.db.ecom_platform.service.ReviewService;
import com.db.ecom_platform.utils.Result;
import com.db.ecom_platform.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Api(tags = "商品评价")
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    
    @ApiOperation(value = "获取商品评价(联表查询)", notes = "使用SQL联表查询获取评价列表，包含用户名和商品名")
    @PostMapping("/list")
    public Result<Page<Map<String, Object>>> getProductReviewsWithJoin(@RequestBody ReviewQueryDTO queryDTO) {
        Page<Map<String, Object>> reviews = reviewService.getProductReviewsWithJoin(queryDTO);
        return Result.success(reviews);
    }

    @ApiOperation(value = "添加商品评价", notes = "为已购买的商品添加评价，支持文字评价和图片评价")
    @PostMapping("/add")
    public Result<?> addReview(@RequestBody ReviewDTO reviewDTO) {
        Integer userId = UserUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("请先登录");
        }
        return reviewService.addReview(userId.toString(), reviewDTO);
    }

    @ApiOperation(value = "添加追评", notes = "为已评价的商品添加追评")
    @PostMapping("/additional")
    public Result<?> addAdditionalReview(@RequestBody AdditionalReviewDTO additionalReviewDTO) {
        Integer userId = UserUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("请先登录");
        }
        return reviewService.addAdditionalReview(userId.toString(), additionalReviewDTO);
    }

    @ApiOperation(value = "获取我的评价", notes = "获取当前登录用户的所有评价")
    @GetMapping("/my")
    public Result<List<Review>> getMyReviews() {
        Integer userId = UserUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("请先登录");
        }
        List<Review> reviews = reviewService.getUserReviews(userId.toString());
        return Result.success(reviews);
    }

    @ApiOperation(value = "删除评价", notes = "删除当前用户的指定评价")
    @DeleteMapping("/{reviewId}")
    public Result<?> deleteReview(@PathVariable String reviewId, HttpServletRequest request) {
        Integer userId = UserUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("请先登录");
        }
        return reviewService.deleteReview(userId.toString(), reviewId, request);
    }
} 