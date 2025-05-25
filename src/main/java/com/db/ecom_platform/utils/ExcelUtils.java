package com.db.ecom_platform.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Excel报表生成工具类
 */
@Component
public class ExcelUtils {

    /**
     * 生成销售报表
     * @param reportData 报表数据
     * @param reportTitle 报表标题
     * @param outputStream 输出流
     * @throws IOException IO异常
     */
    public void generateSalesReport(Map<String, Object> reportData, String reportTitle, OutputStream outputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            // 创建销售概览工作表
            Sheet overviewSheet = workbook.createSheet("销售概览");
            createOverviewSheet(workbook, overviewSheet, reportData, reportTitle);
            
            // 创建热销商品工作表
            Sheet hotProductsSheet = workbook.createSheet("热销商品");
            createHotProductsSheet(workbook, hotProductsSheet, reportData);
            
            // 创建分类销售工作表
            Sheet categorySalesSheet = workbook.createSheet("分类销售");
            createCategorySalesSheet(workbook, categorySalesSheet, reportData);
            
            // 创建地区销售工作表
            Sheet regionSalesSheet = workbook.createSheet("地区销售");
            createRegionSalesSheet(workbook, regionSalesSheet, reportData);
            
            // 创建销售趋势工作表
            Sheet salesTrendSheet = workbook.createSheet("销售趋势");
            createSalesTrendSheet(workbook, salesTrendSheet, reportData);
            
            // 写入输出流
            workbook.write(outputStream);
        }
    }
    
    /**
     * 创建销售概览工作表
     */
    private void createOverviewSheet(Workbook workbook, Sheet sheet, Map<String, Object> reportData, String reportTitle) {
        // 设置列宽
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 5000);
        
        // 创建标题行样式
        CellStyle titleStyle = createTitleStyle(workbook);
        
        // 创建标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(reportTitle);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        
        // 创建数据行样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        // 创建销售总额行
        Row totalSalesRow = sheet.createRow(2);
        Cell totalSalesLabelCell = totalSalesRow.createCell(0);
        totalSalesLabelCell.setCellValue("销售总额");
        totalSalesLabelCell.setCellStyle(headerStyle);
        
        Cell totalSalesValueCell = totalSalesRow.createCell(1);
        Object totalSalesObj = reportData.get("totalSales");
        if (totalSalesObj instanceof BigDecimal) {
            totalSalesValueCell.setCellValue(((BigDecimal) totalSalesObj).doubleValue());
        } else if (totalSalesObj instanceof Double) {
            totalSalesValueCell.setCellValue((Double) totalSalesObj);
        } else {
            totalSalesValueCell.setCellValue(BigDecimal.ZERO.doubleValue());
        }
        totalSalesValueCell.setCellStyle(dataStyle);
        
        // 创建订单总数行
        Row totalOrdersRow = sheet.createRow(3);
        Cell totalOrdersLabelCell = totalOrdersRow.createCell(0);
        totalOrdersLabelCell.setCellValue("订单总数");
        totalOrdersLabelCell.setCellStyle(headerStyle);
        
        Cell totalOrdersValueCell = totalOrdersRow.createCell(1);
        Object totalOrdersObj = reportData.get("totalOrders");
        if (totalOrdersObj instanceof Integer) {
            totalOrdersValueCell.setCellValue((Integer) totalOrdersObj);
        } else if (totalOrdersObj instanceof Long) {
            totalOrdersValueCell.setCellValue(((Long) totalOrdersObj).intValue());
        } else if (totalOrdersObj instanceof String) {
            try {
                totalOrdersValueCell.setCellValue(Integer.parseInt((String) totalOrdersObj));
            } catch (NumberFormatException e) {
                totalOrdersValueCell.setCellValue(0);
            }
        } else {
            totalOrdersValueCell.setCellValue(0);
        }
        totalOrdersValueCell.setCellStyle(dataStyle);
        
        // 创建平均订单金额行
        Row avgOrderAmountRow = sheet.createRow(4);
        Cell avgOrderAmountLabelCell = avgOrderAmountRow.createCell(0);
        avgOrderAmountLabelCell.setCellValue("平均订单金额");
        avgOrderAmountLabelCell.setCellStyle(headerStyle);
        
        Cell avgOrderAmountValueCell = avgOrderAmountRow.createCell(1);
        Object avgOrderAmountObj = reportData.get("avgOrderAmount");
        if (avgOrderAmountObj instanceof BigDecimal) {
            avgOrderAmountValueCell.setCellValue(((BigDecimal) avgOrderAmountObj).doubleValue());
        } else if (avgOrderAmountObj instanceof Double) {
            avgOrderAmountValueCell.setCellValue((Double) avgOrderAmountObj);
        } else {
            avgOrderAmountValueCell.setCellValue(BigDecimal.ZERO.doubleValue());
        }
        avgOrderAmountValueCell.setCellStyle(dataStyle);
    }
    
    /**
     * 创建热销商品工作表
     */
    private void createHotProductsSheet(Workbook workbook, Sheet sheet, Map<String, Object> reportData) {
        // 设置列宽
        sheet.setColumnWidth(0, 3000); // 序号
        sheet.setColumnWidth(1, 5000); // 商品ID
        sheet.setColumnWidth(2, 8000); // 商品名称
        sheet.setColumnWidth(3, 4000); // 销售数量
        sheet.setColumnWidth(4, 4000); // 销售金额
        
        // 创建标题行样式
        CellStyle titleStyle = createTitleStyle(workbook);
        
        // 创建标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("热销商品Top10");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
        
        // 创建表头行样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        // 创建表头行
        Row headerRow = sheet.createRow(1);
        Cell[] headerCells = new Cell[5];
        for (int i = 0; i < headerCells.length; i++) {
            headerCells[i] = headerRow.createCell(i);
            headerCells[i].setCellStyle(headerStyle);
        }
        
        headerCells[0].setCellValue("序号");
        headerCells[1].setCellValue("商品ID");
        headerCells[2].setCellValue("商品名称");
        headerCells[3].setCellValue("销售数量");
        headerCells[4].setCellValue("销售金额");
        
        // 创建数据行样式
        CellStyle dataStyle = createDataStyle(workbook);
        
        // 填充数据行
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> hotProducts = (List<Map<String, Object>>) reportData.getOrDefault("hotProducts", Collections.emptyList());
        
        for (int i = 0; i < hotProducts.size(); i++) {
            Map<String, Object> product = hotProducts.get(i);
            
            Row dataRow = sheet.createRow(i + 2);
            Cell[] dataCells = new Cell[5];
            for (int j = 0; j < dataCells.length; j++) {
                dataCells[j] = dataRow.createCell(j);
                dataCells[j].setCellStyle(dataStyle);
            }
            
            dataCells[0].setCellValue(i + 1);
            dataCells[1].setCellValue(product.get("productId") != null ? product.get("productId").toString() : "");
            dataCells[2].setCellValue((String) product.getOrDefault("productName", ""));
            
            Object quantityObj = product.getOrDefault("salesCount", 0);
            if (quantityObj instanceof Integer) {
                dataCells[3].setCellValue((Integer) quantityObj);
            } else if (quantityObj instanceof Long) {
                dataCells[3].setCellValue(((Long) quantityObj).intValue());
            } else if (quantityObj instanceof String) {
                try {
                    dataCells[3].setCellValue(Integer.parseInt((String) quantityObj));
                } catch (NumberFormatException e) {
                    dataCells[3].setCellValue(0);
                }
            } else {
                dataCells[3].setCellValue(((BigDecimal) quantityObj).intValue());
            }
            
            Object amount = product.get("amount");
            if (amount == null) {
                amount = product.get("salesAmount");
            }
            if (amount instanceof BigDecimal) {
                dataCells[4].setCellValue(((BigDecimal) amount).doubleValue());
            } else if (amount instanceof Double) {
                dataCells[4].setCellValue((Double) amount);
            } else {
                dataCells[4].setCellValue(0.0);
            }
        }
    }
    
    /**
     * 创建分类销售工作表
     */
    private void createCategorySalesSheet(Workbook workbook, Sheet sheet, Map<String, Object> reportData) {
        // 设置列宽
        sheet.setColumnWidth(0, 3000); // 序号
        sheet.setColumnWidth(1, 5000); // 分类ID
        sheet.setColumnWidth(2, 8000); // 分类名称
        sheet.setColumnWidth(3, 4000); // 销售金额
        sheet.setColumnWidth(4, 4000); // 占比
        
        // 创建标题行样式
        CellStyle titleStyle = createTitleStyle(workbook);
        
        // 创建标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("分类销售统计");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
        
        // 创建表头行样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        // 创建表头行
        Row headerRow = sheet.createRow(1);
        Cell[] headerCells = new Cell[5];
        for (int i = 0; i < headerCells.length; i++) {
            headerCells[i] = headerRow.createCell(i);
            headerCells[i].setCellStyle(headerStyle);
        }
        
        headerCells[0].setCellValue("序号");
        headerCells[1].setCellValue("分类ID");
        headerCells[2].setCellValue("分类名称");
        headerCells[3].setCellValue("销售金额");
        headerCells[4].setCellValue("占比");
        
        // 创建数据行样式
        CellStyle dataStyle = createDataStyle(workbook);
        
        // 填充数据行
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> salesByCategory = (List<Map<String, Object>>) reportData.getOrDefault("salesByCategory", Collections.emptyList());
        
        // 计算总销售额
        BigDecimal totalSales = BigDecimal.ZERO;
        for (Map<String, Object> category : salesByCategory) {
            Object amount = category.get("salesAmount");
            if (amount instanceof BigDecimal) {
                totalSales = totalSales.add((BigDecimal) amount);
            } else if (amount instanceof Double) {
                totalSales = totalSales.add(BigDecimal.valueOf((Double) amount));
            }
        }
        
        for (int i = 0; i < salesByCategory.size(); i++) {
            Map<String, Object> category = salesByCategory.get(i);
            
            Row dataRow = sheet.createRow(i + 2);
            Cell[] dataCells = new Cell[5];
            for (int j = 0; j < dataCells.length; j++) {
                dataCells[j] = dataRow.createCell(j);
                dataCells[j].setCellStyle(dataStyle);
            }
            
            dataCells[0].setCellValue(i + 1);
            dataCells[1].setCellValue(category.get("categoryId") != null ? category.get("categoryId").toString() : "");
            dataCells[2].setCellValue((String) category.getOrDefault("categoryName", ""));
            
            BigDecimal amount;
            Object amountObj = category.get("amount");
            if (amountObj == null) {
                amountObj = category.get("salesAmount");
            }
            if (amountObj instanceof BigDecimal) {
                amount = (BigDecimal) amountObj;
            } else if (amountObj instanceof Double) {
                amount = BigDecimal.valueOf((Double) amountObj);
            } else {
                amount = BigDecimal.ZERO;
            }
            
            dataCells[3].setCellValue(amount.doubleValue());
            
            // 计算占比
            BigDecimal percentage = BigDecimal.ZERO;
            if (totalSales.compareTo(BigDecimal.ZERO) > 0) {
                percentage = amount.multiply(new BigDecimal(100)).divide(totalSales, 2, BigDecimal.ROUND_HALF_UP);
            }
            dataCells[4].setCellValue(percentage.doubleValue() + "%");
        }
    }
    
    /**
     * 创建地区销售工作表
     */
    private void createRegionSalesSheet(Workbook workbook, Sheet sheet, Map<String, Object> reportData) {
        // 设置列宽
        sheet.setColumnWidth(0, 3000); // 序号
        sheet.setColumnWidth(1, 8000); // 地区名称
        sheet.setColumnWidth(2, 4000); // 销售金额
        sheet.setColumnWidth(3, 4000); // 占比
        
        // 创建标题行样式
        CellStyle titleStyle = createTitleStyle(workbook);
        
        // 创建标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("地区销售统计");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        
        // 创建表头行样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        // 创建表头行
        Row headerRow = sheet.createRow(1);
        Cell[] headerCells = new Cell[4];
        for (int i = 0; i < headerCells.length; i++) {
            headerCells[i] = headerRow.createCell(i);
            headerCells[i].setCellStyle(headerStyle);
        }
        
        headerCells[0].setCellValue("序号");
        headerCells[1].setCellValue("地区名称");
        headerCells[2].setCellValue("销售金额");
        headerCells[3].setCellValue("占比");
        
        // 创建数据行样式
        CellStyle dataStyle = createDataStyle(workbook);
        
        // 填充数据行
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> regionSales = (List<Map<String, Object>>) reportData.getOrDefault("regionSales", Collections.emptyList());
        
        // 计算总销售额
        BigDecimal totalSales = BigDecimal.ZERO;
        for (Map<String, Object> region : regionSales) {
            Object amount = region.get("amount");
            if (amount instanceof BigDecimal) {
                totalSales = totalSales.add((BigDecimal) amount);
            } else if (amount instanceof Double) {
                totalSales = totalSales.add(BigDecimal.valueOf((Double) amount));
            }
        }
        
        for (int i = 0; i < regionSales.size(); i++) {
            Map<String, Object> region = regionSales.get(i);
            
            Row dataRow = sheet.createRow(i + 2);
            Cell[] dataCells = new Cell[4];
            for (int j = 0; j < dataCells.length; j++) {
                dataCells[j] = dataRow.createCell(j);
                dataCells[j].setCellStyle(dataStyle);
            }
            
            dataCells[0].setCellValue(i + 1);
            dataCells[1].setCellValue((String) region.getOrDefault("region", ""));
            
            BigDecimal amount;
            Object amountObj = region.get("amount");
            if (amountObj instanceof BigDecimal) {
                amount = (BigDecimal) amountObj;
            } else if (amountObj instanceof Double) {
                amount = BigDecimal.valueOf((Double) amountObj);
            } else {
                amount = BigDecimal.ZERO;
            }
            
            dataCells[2].setCellValue(amount.doubleValue());
            
            // 计算占比
            BigDecimal percentage = BigDecimal.ZERO;
            if (totalSales.compareTo(BigDecimal.ZERO) > 0) {
                percentage = amount.multiply(new BigDecimal(100)).divide(totalSales, 2, BigDecimal.ROUND_HALF_UP);
            }
            dataCells[3].setCellValue(percentage.doubleValue() + "%");
        }
    }
    
    /**
     * 创建销售趋势工作表
     */
    private void createSalesTrendSheet(Workbook workbook, Sheet sheet, Map<String, Object> reportData) {
        // 设置列宽
        sheet.setColumnWidth(0, 5000); // 时间点
        sheet.setColumnWidth(1, 5000); // 销售金额
        
        // 创建标题行样式
        CellStyle titleStyle = createTitleStyle(workbook);
        
        // 创建标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("销售趋势");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        
        // 创建表头行样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        // 创建表头行
        Row headerRow = sheet.createRow(1);
        Cell[] headerCells = new Cell[2];
        for (int i = 0; i < headerCells.length; i++) {
            headerCells[i] = headerRow.createCell(i);
            headerCells[i].setCellStyle(headerStyle);
        }
        
        headerCells[0].setCellValue("时间点");
        headerCells[1].setCellValue("销售金额");
        
        // 创建数据行样式
        CellStyle dataStyle = createDataStyle(workbook);
        
        // 填充数据行
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> salesTrend = (List<Map<String, Object>>) reportData.getOrDefault("salesTrend", Collections.emptyList());
        
        for (int i = 0; i < salesTrend.size(); i++) {
            Map<String, Object> point = salesTrend.get(i);
            
            Row dataRow = sheet.createRow(i + 2);
            Cell[] dataCells = new Cell[2];
            for (int j = 0; j < dataCells.length; j++) {
                dataCells[j] = dataRow.createCell(j);
                dataCells[j].setCellStyle(dataStyle);
            }
            
            dataCells[0].setCellValue((String) point.getOrDefault("timePoint", ""));
            
            BigDecimal amount;
            Object amountObj = point.get("amount");
            if (amountObj instanceof BigDecimal) {
                amount = (BigDecimal) amountObj;
            } else if (amountObj instanceof Double) {
                amount = BigDecimal.valueOf((Double) amountObj);
            } else {
                amount = BigDecimal.ZERO;
            }
            
            dataCells[1].setCellValue(amount.doubleValue());
        }
    }
    
    /**
     * 创建标题样式
     */
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    /**
     * 创建表头样式
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    /**
     * 创建数据样式
     */
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
} 