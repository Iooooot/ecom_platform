package com.db.ecom_platform.entity;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class Review {
    private String reviewId;          // 评价ID
    private String productId;         // 商品ID
    private String userId;            // 用户ID
    private Integer rating;           // 评分(1-5)
    private String content;           // 评价内容
    private List<String> images;      // 评价图片URL列表
    private String additionalReview;  // 追评内容
    private Date additionalReviewTime; // 追评时间
    private Date createTime;          // 创建时间
    private Date updateTime;          // 更新时间
    
    // 临时属性，不存储到数据库
    private transient String productName;  // 商品名称(来自products表)
    private transient String username;     // 用户名(来自users表)
    
    // 是否是好评（评分>=4为好评）
    public boolean isPositive() {
        return rating != null && rating >= 4;
    }
    
    // 是否是差评（评分<=2为差评）
    public boolean isNegative() {
        return rating != null && rating <= 2;
    }
} 