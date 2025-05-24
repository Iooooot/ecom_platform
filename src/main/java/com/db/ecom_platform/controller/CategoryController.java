package com.db.ecom_platform.controller;

import com.db.ecom_platform.entity.Category;
import com.db.ecom_platform.service.CategoryService;
import com.db.ecom_platform.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品分类")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "获取所有分类", notes = "获取所有商品分类列表")
    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryService.getAllCategories());
    }
} 