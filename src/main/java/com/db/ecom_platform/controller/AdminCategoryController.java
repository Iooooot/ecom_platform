package com.db.ecom_platform.controller;

import com.db.ecom_platform.entity.Category;
import com.db.ecom_platform.service.CategoryService;
import com.db.ecom_platform.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "管理员商品分类管理")
@RestController
@RequestMapping("/api/admin/category")
public class AdminCategoryController {
    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "添加分类", notes = "添加新的商品分类，支持多级分类")
    @PostMapping
    public Result<?> add(@RequestBody Category category) {
        categoryService.addCategory(category);
        return Result.success();
    }

    @ApiOperation(value = "更新分类", notes = "更新商品分类信息，支持修改分类名称和父级分类")
    @PutMapping
    public Result<?> update(@RequestBody Category category) {
        categoryService.updateCategory(category);
        return Result.success();
    }

    @ApiOperation(value = "删除分类", notes = "根据分类ID删除商品分类")
    @ApiImplicitParam(name = "categoryId", value = "分类ID", required = true, paramType = "path", dataTypeClass = String.class)
    @DeleteMapping("/{categoryId}")
    public Result<?> delete(@PathVariable String categoryId) {
        categoryService.deleteCategory(categoryId);
        return Result.success();
    }

    @ApiOperation(value = "获取分类详情", notes = "根据分类ID获取商品分类详情")
    @ApiImplicitParam(name = "categoryId", value = "分类ID", required = true, paramType = "path", dataTypeClass = String.class)
    @GetMapping("/{categoryId}")
    public Result<Category> get(@PathVariable String categoryId) {
        return Result.success(categoryService.getCategory(categoryId));
    }

    @ApiOperation(value = "获取所有分类", notes = "获取所有商品分类列表")
    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryService.getAllCategories());
    }

    @ApiOperation(value = "获取子分类", notes = "根据父分类ID获取其下的所有直接子分类")
    @ApiImplicitParam(name = "parentId", value = "父分类ID", required = true, paramType = "path", dataTypeClass = String.class)
    @GetMapping("/children/{parentId}")
    public Result<List<Category>> children(@PathVariable String parentId) {
        return Result.success(categoryService.getChildren(parentId));
    }
} 