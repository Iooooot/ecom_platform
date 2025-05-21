package com.db.ecom_platform.service;

import com.db.ecom_platform.entity.Category;
import java.util.List;

public interface CategoryService {
    void addCategory(Category category);
    void updateCategory(Category category);
    void deleteCategory(String categoryId);
    Category getCategory(String categoryId);
    List<Category> getAllCategories();
    List<Category> getChildren(String parentId);
} 