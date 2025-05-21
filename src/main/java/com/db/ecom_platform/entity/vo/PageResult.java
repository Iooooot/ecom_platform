package com.db.ecom_platform.entity.vo;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> list;       // 数据列表
    private Integer total;      // 总记录数
    private Integer pages;      // 总页数
    private Integer pageNum;    // 当前页码
    private Integer pageSize;   // 每页记录数
    
    public PageResult(List<T> list, Integer total, Integer pageNum, Integer pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = (total + pageSize - 1) / pageSize; // 计算总页数
    }
} 