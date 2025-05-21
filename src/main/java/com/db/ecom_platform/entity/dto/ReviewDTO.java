package com.db.ecom_platform.entity.dto;

import lombok.Data;
import java.util.List;

@Data
public class ReviewDTO {
    private String productId;      // 商品ID
    private String orderId;        // 订单ID
    private Integer rating;        // 评分(1-5)
    private String content;        // 评价内容
    private List<String> images;   // 评价图片Base64编码或URL列表
} 