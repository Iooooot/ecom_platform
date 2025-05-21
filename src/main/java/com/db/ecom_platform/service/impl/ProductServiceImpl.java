package com.db.ecom_platform.service.impl;

import com.db.ecom_platform.entity.Product;
import com.db.ecom_platform.entity.dto.ProductSearchDTO;
import com.db.ecom_platform.entity.vo.PageResult;
import com.db.ecom_platform.mapper.ProductMapper;
import com.db.ecom_platform.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public PageResult<Product> searchProducts(ProductSearchDTO searchDTO) {
        // 计算分页偏移量
        int offset = (searchDTO.getPage() - 1) * searchDTO.getSize();
        
        // 查询数据列表
        List<Product> products = productMapper.searchProducts(
                searchDTO.getKeyword(),
                searchDTO.getCategoryId(),
                searchDTO.getMinPrice(),
                searchDTO.getMaxPrice(),
                searchDTO.getMinSales(),
                searchDTO.getSortField(),
                searchDTO.getSortOrder(),
                offset,
                searchDTO.getSize()
        );
        
        // 查询数据总数
        int total = productMapper.countSearchProducts(
                searchDTO.getKeyword(),
                searchDTO.getCategoryId(),
                searchDTO.getMinPrice(),
                searchDTO.getMaxPrice(),
                searchDTO.getMinSales()
        );
        
        // 构建分页结果
        return new PageResult<>(products, total, searchDTO.getPage(), searchDTO.getSize());
    }

    @Override
    public Product getProductById(String productId) {
        return productMapper.getProductById(productId);
    }
} 