package com.db.ecom_platform.entity;

import lombok.Data;

@Data
public class Category {
    private String categoryId;
    private String name;
    private String parentId;
} 