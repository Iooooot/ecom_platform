package com.db.ecom_platform.service;

import com.db.ecom_platform.entity.Product;
import com.db.ecom_platform.entity.dto.ProductDTO;
import com.db.ecom_platform.entity.dto.ProductSearchDTO;
import com.db.ecom_platform.entity.vo.PageResult;
import com.db.ecom_platform.utils.Result;

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
    
    /**
     * 添加商品
     * @param productDTO 商品信息
     * @return 添加结果
     */
    Result<?> addProduct(ProductDTO productDTO);
    
    /**
     * 更新商品信息
     * @param productDTO 商品信息
     * @return 更新结果
     */
    Result<?> updateProduct(ProductDTO productDTO);
    
    /**
     * 删除商品
     * @param productId 商品ID
     * @return 删除结果
     */
    Result<?> deleteProduct(String productId);
    
    /**
     * 更新商品状态（上架/下架）
     * @param productId 商品ID
     * @param status 商品状态：0-下架，1-上架
     * @return 更新结果
     */
    Result<?> updateProductStatus(String productId, Integer status);
} 