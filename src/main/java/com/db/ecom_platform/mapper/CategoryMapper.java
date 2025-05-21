package com.db.ecom_platform.mapper;

import com.db.ecom_platform.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CategoryMapper {
    int insertCategory(Category category);
    int updateCategory(Category category);
    int deleteCategory(@Param("categoryId") String categoryId);
    Category selectCategoryById(@Param("categoryId") String categoryId);
    List<Category> selectAllCategories();
    List<Category> selectByParentId(@Param("parentId") String parentId);
} 