package com.db.ecom_platform.entity.vo;

import lombok.Data;

@Data
public class ReviewStatVO {
    private String productId;       // 商品ID
    private Integer totalCount;     // 总评价数
    private Integer positiveCount;  // 好评数
    private Integer neutralCount;   // 中评数
    private Integer negativeCount;  // 差评数
    private Integer imageCount;     // 带图评价数
    private Integer additionalCount; // 追评数
    private Double averageRating;   // 平均评分
    private Double positiveRate;    // 好评率
} 