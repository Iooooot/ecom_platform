package com.db.ecom_platform.service.impl;

import com.db.ecom_platform.entity.Category;
import com.db.ecom_platform.mapper.CategoryMapper;
import com.db.ecom_platform.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @Transactional
    public void addCategory(Category category) {
        if (category.getCategoryId() == null || category.getCategoryId().isEmpty()) {
            category.setCategoryId(UUID.randomUUID().toString());
        }
        categoryMapper.insertCategory(category);
    }

    @Override
    @Transactional
    public void updateCategory(Category category) {
        categoryMapper.updateCategory(category);
    }

    @Override
    @Transactional
    public void deleteCategory(String categoryId) {
        categoryMapper.deleteCategory(categoryId);
    }

    @Override
    public Category getCategory(String categoryId) {
        return categoryMapper.selectCategoryById(categoryId);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.selectAllCategories();
    }

    @Override
    public List<Category> getChildren(String parentId) {
        return categoryMapper.selectByParentId(parentId);
    }
} 