package com.db.ecom_platform.utils;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.font.PdfEncodings;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * PDF报表生成工具类 (使用iText 7)
 */
@Component
public class PdfUtils {

    /**
     * 生成销售报表
     * @param reportData 报表数据
     * @param reportTitle 报表标题
     * @param outputStream 输出流
     * @throws IOException IO异常
     */
    public void generateSalesReport(Map<String, Object> reportData, String reportTitle, OutputStream outputStream) throws IOException {
        // 创建PDF文档
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        // 加载支持中文的字体
        try {
            // 尝试加载默认中文字体
            PdfFont font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            document.setFont(font);
        } catch (IOException e) {
            try {
                // 如果默认中文字体不可用，尝试使用系统中文字体
                PdfFont font = PdfFontFactory.createFont("C:/Windows/Fonts/simhei.ttf", PdfEncodings.IDENTITY_H);
                document.setFont(font);
            } catch (IOException ex) {
                // 如果仍然失败，记录错误但继续执行
                System.err.println("无法加载中文字体：" + ex.getMessage());
                // 继续使用默认字体
            }
        }
        
        // 添加标题
        Paragraph title = new Paragraph(reportTitle);
        title.setFontSize(18).setBold().setTextAlignment(TextAlignment.CENTER);
        title.setMarginBottom(20);  // 设置标题下边距
        document.add(title);
        document.add(new Paragraph(" "));
        
        // 添加销售概览
        addSalesOverview(document, reportData);
        document.add(new Paragraph(" "));
        
        // 添加热销商品
        addHotProducts(document, reportData);
        document.add(new Paragraph(" "));
        
        // 添加分类销售
        addCategorySales(document, reportData);
        document.add(new Paragraph(" "));
        
        // 添加地区销售
        addRegionSales(document, reportData);
        document.add(new Paragraph(" "));
        
        // 添加销售趋势
        addSalesTrend(document, reportData);
        
        // 关闭文档
        document.close();
    }
    
    /**
     * 添加销售概览
     */
    private void addSalesOverview(Document document, Map<String, Object> reportData) {
        // 添加小标题
        Paragraph subtitle = new Paragraph("销售概览");
        subtitle.setFontSize(14).setBold().setTextAlignment(TextAlignment.LEFT);
        subtitle.setMarginBottom(10);  // 设置下边距，使标题和表格有一定间隔
        document.add(subtitle);
        
        // 创建表格
        Table table = new Table(new float[]{1, 1});
        table.setWidth(UnitValue.createPercentValue(80));
        table.setMarginBottom(10);  // 设置表格下边距
        
        // 添加表头
        table.addHeaderCell(createHeaderCell("指标"));
        table.addHeaderCell(createHeaderCell("数值"));
        
        // 添加销售总额行
        table.addCell(createCell("销售总额"));
        Object totalSalesObj = reportData.get("totalSales");
        if (totalSalesObj instanceof BigDecimal) {
            table.addCell(createCell(((BigDecimal) totalSalesObj).toString()));
        } else {
            table.addCell(createCell(((BigDecimal) reportData.getOrDefault("totalSales", BigDecimal.ZERO)).toString()));
        }
        
        // 添加订单总数行
        table.addCell(createCell("订单总数"));
        table.addCell(createCell(reportData.getOrDefault("totalOrders", 0).toString()));
        
        // 添加平均订单金额行
        table.addCell(createCell("平均订单金额"));
        Object avgOrderAmountObj = reportData.get("avgOrderAmount");
        if (avgOrderAmountObj instanceof BigDecimal) {
            table.addCell(createCell(((BigDecimal) avgOrderAmountObj).toString()));
        } else {
            table.addCell(createCell(((BigDecimal) reportData.getOrDefault("avgOrderAmount", BigDecimal.ZERO)).toString()));
        }
        
        document.add(table);
    }
    
    /**
     * 添加热销商品
     */
    private void addHotProducts(Document document, Map<String, Object> reportData) {
        // 添加小标题
        Paragraph subtitle = new Paragraph("热销商品Top10");
        subtitle.setFontSize(14).setBold().setTextAlignment(TextAlignment.LEFT);
        subtitle.setMarginBottom(10);  // 设置下边距
        document.add(subtitle);
        
        // 创建表格
        Table table = new Table(new float[]{0.5f, 1, 2, 1, 1});
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMarginBottom(10);  // 设置表格下边距
        
        // 添加表头
        table.addHeaderCell(createHeaderCell("序号"));
        table.addHeaderCell(createHeaderCell("商品ID"));
        table.addHeaderCell(createHeaderCell("商品名称"));
        table.addHeaderCell(createHeaderCell("销售数量"));
        table.addHeaderCell(createHeaderCell("销售金额"));
        
        // 添加数据行
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> hotProducts = (List<Map<String, Object>>) reportData.getOrDefault("hotProducts", Collections.emptyList());
        
        for (int i = 0; i < hotProducts.size(); i++) {
            Map<String, Object> product = hotProducts.get(i);
            
            table.addCell(createCell(String.valueOf(i + 1)));
            table.addCell(createCell(product.get("productId") != null ? product.get("productId").toString() : "未知"));
            table.addCell(createCell((String) product.getOrDefault("productName", "未知商品")));
            table.addCell(createCell(product.getOrDefault("quantity", 0).toString()));
            
            Object amount = product.get("amount");
            if (amount == null) {
                // 如果amount字段不存在，尝试使用salesAmount字段
                amount = product.get("salesAmount");
            }
            
            if (amount instanceof BigDecimal) {
                table.addCell(createCell(((BigDecimal) amount).toString()));
            } else if (amount instanceof Double) {
                table.addCell(createCell(BigDecimal.valueOf((Double) amount).toString()));
            } else {
                table.addCell(createCell("0"));
            }
        }
        
        document.add(table);
    }
    
    /**
     * 添加分类销售
     */
    private void addCategorySales(Document document, Map<String, Object> reportData) {
        // 添加小标题
        Paragraph subtitle = new Paragraph("分类销售统计");
        subtitle.setFontSize(14).setBold().setTextAlignment(TextAlignment.LEFT);
        subtitle.setMarginBottom(10);  // 设置下边距
        document.add(subtitle);
        
        // 创建表格
        Table table = new Table(new float[]{0.5f, 1, 2, 1, 1});
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMarginBottom(10);  // 设置表格下边距
        
        // 添加表头
        table.addHeaderCell(createHeaderCell("序号"));
        table.addHeaderCell(createHeaderCell("分类ID"));
        table.addHeaderCell(createHeaderCell("分类名称"));
        table.addHeaderCell(createHeaderCell("销售金额"));
        table.addHeaderCell(createHeaderCell("占比"));
        
        // 添加数据行
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> salesByCategory = (List<Map<String, Object>>) reportData.getOrDefault("salesByCategory", Collections.emptyList());
        
        // 计算总销售额
        BigDecimal totalSales = BigDecimal.ZERO;
        for (Map<String, Object> category : salesByCategory) {
            Object amountObj = category.get("amount");
            if (amountObj == null) {
                // 如果amount字段不存在，尝试使用salesAmount字段
                amountObj = category.get("salesAmount");
            }
            
            if (amountObj instanceof BigDecimal) {
                totalSales = totalSales.add((BigDecimal) amountObj);
            } else if (amountObj instanceof Double) {
                totalSales = totalSales.add(BigDecimal.valueOf((Double) amountObj));
            }
        }
        
        for (int i = 0; i < salesByCategory.size(); i++) {
            Map<String, Object> category = salesByCategory.get(i);
            
            table.addCell(createCell(String.valueOf(i + 1)));
            table.addCell(createCell(category.get("categoryId") != null ? category.get("categoryId").toString() : "未分类"));
            table.addCell(createCell((String) category.getOrDefault("categoryName", "未分类")));
            
            BigDecimal amount;
            Object amountObj = category.get("amount");
            if (amountObj == null) {
                // 如果amount字段不存在，尝试使用salesAmount字段
                amountObj = category.get("salesAmount");
            }
            
            if (amountObj instanceof BigDecimal) {
                amount = (BigDecimal) amountObj;
            } else if (amountObj instanceof Double) {
                amount = BigDecimal.valueOf((Double) amountObj);
            } else {
                amount = BigDecimal.ZERO;
            }
            
            table.addCell(createCell(amount.toString()));
            
            // 计算占比
            BigDecimal percentage = BigDecimal.ZERO;
            if (totalSales.compareTo(BigDecimal.ZERO) > 0) {
                percentage = amount.multiply(new BigDecimal(100)).divide(totalSales, 2, BigDecimal.ROUND_HALF_UP);
            }
            table.addCell(createCell(percentage.toString() + "%"));
        }
        
        document.add(table);
    }
    
    /**
     * 添加地区销售
     */
    private void addRegionSales(Document document, Map<String, Object> reportData) {
        // 添加小标题
        Paragraph subtitle = new Paragraph("地区销售统计");
        subtitle.setFontSize(14).setBold().setTextAlignment(TextAlignment.LEFT);
        subtitle.setMarginBottom(10);  // 设置下边距
        document.add(subtitle);
        
        // 创建表格
        Table table = new Table(new float[]{0.5f, 2, 1, 1});
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMarginBottom(10);  // 设置表格下边距
        
        // 添加表头
        table.addHeaderCell(createHeaderCell("序号"));
        table.addHeaderCell(createHeaderCell("地区名称"));
        table.addHeaderCell(createHeaderCell("销售金额"));
        table.addHeaderCell(createHeaderCell("占比"));
        
        // 添加数据行
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> regionSales = (List<Map<String, Object>>) reportData.getOrDefault("regionSales", Collections.emptyList());
        
        // 计算总销售额
        BigDecimal totalSales = BigDecimal.ZERO;
        for (Map<String, Object> region : regionSales) {
            Object amountObj = region.get("amount");
            if (amountObj == null) {
                // 如果amount字段不存在，尝试使用salesAmount字段
                amountObj = region.get("salesAmount");
            }
            
            if (amountObj instanceof BigDecimal) {
                totalSales = totalSales.add((BigDecimal) amountObj);
            } else if (amountObj instanceof Double) {
                totalSales = totalSales.add(BigDecimal.valueOf((Double) amountObj));
            }
        }
        
        for (int i = 0; i < regionSales.size(); i++) {
            Map<String, Object> region = regionSales.get(i);
            
            table.addCell(createCell(String.valueOf(i + 1)));
            table.addCell(createCell((String) region.getOrDefault("region", "未知地区")));
            
            BigDecimal amount;
            Object amountObj = region.get("amount");
            if (amountObj == null) {
                // 如果amount字段不存在，尝试使用salesAmount字段
                amountObj = region.get("salesAmount");
            }
            
            if (amountObj instanceof BigDecimal) {
                amount = (BigDecimal) amountObj;
            } else if (amountObj instanceof Double) {
                amount = BigDecimal.valueOf((Double) amountObj);
            } else {
                amount = BigDecimal.ZERO;
            }
            
            table.addCell(createCell(amount.toString()));
            
            // 计算占比
            BigDecimal percentage = BigDecimal.ZERO;
            if (totalSales.compareTo(BigDecimal.ZERO) > 0) {
                percentage = amount.multiply(new BigDecimal(100)).divide(totalSales, 2, BigDecimal.ROUND_HALF_UP);
            }
            table.addCell(createCell(percentage.toString() + "%"));
        }
        
        document.add(table);
    }
    
    /**
     * 添加销售趋势
     */
    private void addSalesTrend(Document document, Map<String, Object> reportData) {
        // 添加小标题
        Paragraph subtitle = new Paragraph("销售趋势");
        subtitle.setFontSize(14).setBold().setTextAlignment(TextAlignment.LEFT);
        subtitle.setMarginBottom(10);  // 设置下边距
        document.add(subtitle);
        
        // 创建表格
        Table table = new Table(new float[]{2, 1, 1});
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMarginBottom(10);  // 设置表格下边距
        
        // 添加表头
        table.addHeaderCell(createHeaderCell("时间"));
        table.addHeaderCell(createHeaderCell("销售额"));
        table.addHeaderCell(createHeaderCell("订单数"));
        
        // 添加数据行
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> salesTrend = (List<Map<String, Object>>) reportData.getOrDefault("salesTrend", Collections.emptyList());
        
        for (Map<String, Object> point : salesTrend) {
            table.addCell(createCell((String) point.getOrDefault("timePoint", "未知时间")));
            
            Object amount = point.get("amount");
            if (amount == null) {
                // 如果amount字段不存在，尝试使用salesAmount字段
                amount = point.get("salesAmount");
            }
            
            if (amount instanceof BigDecimal) {
                table.addCell(createCell(((BigDecimal) amount).toString()));
            } else if (amount instanceof Double) {
                table.addCell(createCell(BigDecimal.valueOf((Double) amount).toString()));
            } else {
                table.addCell(createCell("0"));
            }
            
            table.addCell(createCell(point.getOrDefault("count", 0).toString()));
        }
        
        document.add(table);
    }
    
    /**
     * 创建表头单元格
     */
    private Cell createHeaderCell(String text) {
        Cell cell = new Cell();
        Paragraph p = new Paragraph(text);
        p.setBold();  // 在段落上设置粗体
        p.setFontSize(11);  // 设置字体大小
        cell.add(p);
        cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        cell.setTextAlignment(TextAlignment.CENTER);
        cell.setPadding(5);  // 设置单元格内边距
        return cell;
    }
    
    /**
     * 创建普通单元格
     */
    private Cell createCell(String text) {
        Cell cell = new Cell();
        Paragraph p = new Paragraph(text);
        p.setFontSize(10);  // 设置字体大小
        cell.add(p);
        cell.setTextAlignment(TextAlignment.CENTER);  // 居中显示
        cell.setPadding(5);  // 设置单元格内边距
        return cell;
    }
} 