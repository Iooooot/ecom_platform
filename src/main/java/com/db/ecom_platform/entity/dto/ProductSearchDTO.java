package com.db.ecom_platform.entity.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductSearchDTO {
    private String keyword;            // 搜索关键词
    private String categoryId;         // 分类ID
    private BigDecimal minPrice;       // 最低价格
    private BigDecimal maxPrice;       // 最高价格
    private Integer minSales;          // 最低销量
    private String sortField;          // 排序字段：price-价格，sales-销量，time-上架时间
    private String sortOrder;          // 排序方式：asc-升序，desc-降序
    private Integer page = 1;          // 页码，默认第1页
    private Integer size = 10;         // 每页大小，默认10条
} 