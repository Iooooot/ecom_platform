package com.db.ecom_platform.service;

import com.db.ecom_platform.entity.Product;
import com.db.ecom_platform.entity.dto.ProductSearchDTO;
import com.db.ecom_platform.entity.vo.PageResult;

public interface ProductService {
    /**
     * 根据条件搜索和筛选商品
     * @param searchDTO 搜索条件
     * @return 分页结果
     */
    PageResult<Product> searchProducts(ProductSearchDTO searchDTO);
    
    /**
     * 根据ID获取商品详情
     * @param productId 商品ID
     * @return 商品详情
     */
    Product getProductById(String productId);
} 