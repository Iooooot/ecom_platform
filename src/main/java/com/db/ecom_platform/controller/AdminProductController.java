package com.db.ecom_platform.controller;

import com.db.ecom_platform.entity.Product;
import com.db.ecom_platform.entity.dto.ProductDTO;
import com.db.ecom_platform.entity.dto.ProductSearchDTO;
import com.db.ecom_platform.entity.vo.PageResult;
import com.db.ecom_platform.service.ProductService;
import com.db.ecom_platform.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员商品管理控制器
 */
@Api(tags = "管理员-商品管理")
@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    /**
     * 商品列表查询（复用前台搜索接口）
     */
    @ApiOperation(value = "商品列表查询", notes = "管理员查询商品列表，支持多种筛选条件")
    @PostMapping("/list")
    public Result<PageResult<Product>> listProducts(@RequestBody ProductSearchDTO searchDTO) {
        PageResult<Product> pageResult = productService.searchProducts(searchDTO);
        return Result.success(pageResult);
    }

    /**
     * 获取商品详情
     */
    @ApiOperation(value = "获取商品详情", notes = "根据商品ID获取商品详细信息")
    @ApiImplicitParam(name = "productId", value = "商品ID", required = true, paramType = "path", dataTypeClass = String.class)
    @GetMapping("/{productId}")
    public Result<Product> getProduct(@PathVariable String productId) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }

    /**
     * 添加商品
     */
    @ApiOperation(value = "添加商品", notes = "添加新商品")
    @PostMapping
    public Result<?> addProduct(@RequestBody ProductDTO productDTO) {
        return productService.addProduct(productDTO);
    }

    /**
     * 更新商品信息
     */
    @ApiOperation(value = "更新商品", notes = "更新商品信息")
    @PutMapping("/{productId}")
    public Result<?> updateProduct(@PathVariable String productId, @RequestBody ProductDTO productDTO) {
        productDTO.setProductId(productId);
        return productService.updateProduct(productDTO);
    }

    /**
     * 删除商品
     */
    @ApiOperation(value = "删除商品", notes = "根据商品ID删除商品")
    @ApiImplicitParam(name = "productId", value = "商品ID", required = true, paramType = "path", dataTypeClass = String.class)
    @DeleteMapping("/{productId}")
    public Result<?> deleteProduct(@PathVariable String productId) {
        return productService.deleteProduct(productId);
    }

    /**
     * 更新商品状态（上架/下架）
     */
    @ApiOperation(value = "更新商品状态", notes = "上架或下架商品")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "productId", value = "商品ID", required = true, paramType = "path", dataTypeClass = String.class),
        @ApiImplicitParam(name = "status", value = "商品状态：0-下架，1-上架", required = true, paramType = "path", dataTypeClass = Integer.class)
    })
    @PutMapping("/{productId}/status/{status}")
    public Result<?> updateProductStatus(@PathVariable String productId, @PathVariable Integer status) {
        return productService.updateProductStatus(productId, status);
    }
} 