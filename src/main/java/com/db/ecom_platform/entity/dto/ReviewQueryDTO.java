package com.db.ecom_platform.entity.dto;

import lombok.Data;

@Data
public class ReviewQueryDTO {
    private String productId;     // 商品ID
    private Integer type;         // 评价类型：0-全部，1-好评，2-中评，3-差评，4-有图，5-追评
    private Integer page = 1;     // 页码，默认第1页
    private Integer size = 10;    // 每页记录数，默认10条
} 