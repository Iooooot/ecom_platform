package com.db.ecom_platform.controller;

import com.db.ecom_platform.entity.Product;
import com.db.ecom_platform.entity.dto.ProductSearchDTO;
import com.db.ecom_platform.entity.vo.PageResult;
import com.db.ecom_platform.service.ProductService;
import com.db.ecom_platform.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "商品查询")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation(value = "搜索商品", notes = "根据关键词、分类、价格区间、销量等条件搜索商品，支持多种排序方式")
    @PostMapping("/search")
    public Result<PageResult<Product>> search(@RequestBody ProductSearchDTO searchDTO) {
        PageResult<Product> pageResult = productService.searchProducts(searchDTO);
        return Result.success(pageResult);
    }

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
} 