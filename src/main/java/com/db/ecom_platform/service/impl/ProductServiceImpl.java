package com.db.ecom_platform.service.impl;

import com.db.ecom_platform.entity.Product;
import com.db.ecom_platform.entity.dto.ProductDTO;
import com.db.ecom_platform.entity.dto.ProductSearchDTO;
import com.db.ecom_platform.entity.vo.PageResult;
import com.db.ecom_platform.mapper.ProductMapper;
import com.db.ecom_platform.service.ProductService;
import com.db.ecom_platform.utils.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    
    @Override
    @Transactional
    public Result<?> addProduct(ProductDTO productDTO) {
        // 参数校验
        if (productDTO.getName() == null || productDTO.getName().trim().isEmpty()) {
            return Result.error("商品名称不能为空");
        }
        if (productDTO.getPrice() == null || productDTO.getPrice().compareTo(new java.math.BigDecimal("0")) <= 0) {
            return Result.error("商品价格必须大于0");
        }
        if (productDTO.getStock() == null || productDTO.getStock() < 0) {
            return Result.error("商品库存不能为负数");
        }
        if (productDTO.getCategoryId() == null || productDTO.getCategoryId().trim().isEmpty()) {
            return Result.error("商品分类不能为空");
        }
        
        // 获取分类名称
        String categoryName = productMapper.getCategoryName(productDTO.getCategoryId());
        if (categoryName == null) {
            return Result.error("商品分类不存在");
        }
        
        // 创建商品对象
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        
        // 设置商品ID
        product.setProductId("P" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        product.setCategoryName(categoryName);
        product.setSalesVolume(0); // 新商品销量为0
        
        // 设置创建时间和更新时间
        Date now = new Date();
        product.setCreateTime(now);
        product.setUpdateTime(now);
        
        // 插入数据库
        int result = productMapper.insertProduct(product);
        
        if (result > 0) {
            return Result.success("添加商品成功", product.getProductId());
        } else {
            return Result.error("添加商品失败");
        }
    }
    
    @Override
    @Transactional
    public Result<?> updateProduct(ProductDTO productDTO) {
        // 检查商品ID
        if (productDTO.getProductId() == null || productDTO.getProductId().trim().isEmpty()) {
            return Result.error("商品ID不能为空");
        }
        
        // 检查商品是否存在
        int exists = productMapper.checkProductExists(productDTO.getProductId());
        if (exists <= 0) {
            return Result.error("商品不存在");
        }
        
        // 参数校验
        if (productDTO.getName() != null && productDTO.getName().trim().isEmpty()) {
            return Result.error("商品名称不能为空");
        }
        if (productDTO.getPrice() != null && productDTO.getPrice().compareTo(new java.math.BigDecimal("0")) <= 0) {
            return Result.error("商品价格必须大于0");
        }
        if (productDTO.getStock() != null && productDTO.getStock() < 0) {
            return Result.error("商品库存不能为负数");
        }
        
        // 如果更新了分类ID，则获取新的分类名称
        String categoryName = null;
        if (productDTO.getCategoryId() != null && !productDTO.getCategoryId().trim().isEmpty()) {
            categoryName = productMapper.getCategoryName(productDTO.getCategoryId());
            if (categoryName == null) {
                return Result.error("商品分类不存在");
            }
        }
        
        // 创建商品对象
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        
        // 设置分类名称和更新时间
        if (categoryName != null) {
            product.setCategoryName(categoryName);
        }
        product.setUpdateTime(new Date());
        
        // 更新数据库
        int result = productMapper.updateProduct(product);
        
        if (result > 0) {
            return Result.success("更新商品成功");
        } else {
            return Result.error("更新商品失败");
        }
    }
    
    @Override
    @Transactional
    public Result<?> deleteProduct(String productId) {
        // 检查商品是否存在
        int exists = productMapper.checkProductExists(productId);
        if (exists <= 0) {
            return Result.error("商品不存在");
        }
        
        // 删除商品
        int result = productMapper.deleteProduct(productId);
        
        if (result > 0) {
            return Result.success("删除商品成功");
        } else {
            return Result.error("删除商品失败");
        }
    }
    
    @Override
    @Transactional
    public Result<?> updateProductStatus(String productId, Integer status) {
        // 检查商品是否存在
        int exists = productMapper.checkProductExists(productId);
        if (exists <= 0) {
            return Result.error("商品不存在");
        }
        
        // 检查状态值
        if (status != 0 && status != 1) {
            return Result.error("商品状态值无效");
        }
        
        // 更新商品状态
        int result = productMapper.updateProductStatus(productId, status);
        
        if (result > 0) {
            return Result.success("更新商品状态成功");
        } else {
            return Result.error("更新商品状态失败");
        }
    }
} 