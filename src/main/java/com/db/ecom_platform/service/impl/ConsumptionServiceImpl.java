package com.db.ecom_platform.service.impl;

import com.db.ecom_platform.entity.ConsumptionStat;
import com.db.ecom_platform.entity.vo.ConsumptionStatVO;
import com.db.ecom_platform.entity.vo.ConsumptionTrendVO;
import com.db.ecom_platform.mapper.UserMapper;
import com.db.ecom_platform.service.ConsumptionService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消费统计服务实现类
 */
@Service
public class ConsumptionServiceImpl implements ConsumptionService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public ConsumptionStatVO getConsumptionStats(Integer userId, String startTime, String endTime, String timeUnit) {
        ConsumptionStat stat = userMapper.getConsumptionStats(userId, startTime, endTime);
        if (stat == null) {
            return new ConsumptionStatVO();
        }
        
        ConsumptionStatVO statVO = new ConsumptionStatVO();
        statVO.setUserId(userId);
        statVO.setTimeRange(startTime + " 至 " + endTime);
        statVO.setTimeUnit(timeUnit);
        statVO.setTotalAmount(stat.getTotalAmount() != null ? stat.getTotalAmount() : 0.0);
        statVO.setOrderCount(stat.getOrderCount() != null ? stat.getOrderCount() : 0);
        
        // 计算平均消费金额
        if (stat.getOrderCount() != null && stat.getOrderCount() > 0 && stat.getTotalAmount() != null) {
            statVO.setAvgAmount(stat.getTotalAmount() / stat.getOrderCount());
        } else {
            statVO.setAvgAmount(0.0);
        }
        
        // 获取分类消费
        List<Map<String, Object>> categoryConsumptions = userMapper.getCategoryConsumption(userId, startTime, endTime);
        Map<String, Double> categoryMap = new HashMap<>();
        
        if (categoryConsumptions != null && !categoryConsumptions.isEmpty()) {
            for (Map<String, Object> item : categoryConsumptions) {
                String category = (String) item.get("category");
                Double amount = ((Number) item.get("amount")).doubleValue();
                categoryMap.put(category, amount);
            }
        }
        
        statVO.setCategoryConsumption(categoryMap);
        return statVO;
    }
    
    @Override
    public List<ConsumptionTrendVO> getConsumptionTrend(Integer userId, String startTime, String endTime, String timeUnit) {
        List<Map<String, Object>> trends = userMapper.getConsumptionTrend(userId, startTime, endTime, timeUnit);
        List<ConsumptionTrendVO> result = new ArrayList<>();
        
        if (trends != null && !trends.isEmpty()) {
            for (Map<String, Object> item : trends) {
                ConsumptionTrendVO trendVO = new ConsumptionTrendVO();
                trendVO.setTimePoint((String) item.get("timePoint"));
                trendVO.setAmount(((Number) item.get("amount")).doubleValue());
                trendVO.setCount(((Number) item.get("count")).intValue());
                result.add(trendVO);
            }
        }
        
        return result;
    }
    
    @Override
    public Map<String, Double> getCategoryConsumption(Integer userId, String startTime, String endTime) {
        List<Map<String, Object>> categoryConsumptions = userMapper.getCategoryConsumption(userId, startTime, endTime);
        Map<String, Double> result = new HashMap<>();
        
        if (categoryConsumptions != null && !categoryConsumptions.isEmpty()) {
            for (Map<String, Object> item : categoryConsumptions) {
                String category = (String) item.get("category");
                Double amount = ((Number) item.get("amount")).doubleValue();
                result.put(category, amount);
            }
        }
        
        return result;
    }
    
    @Override
    public byte[] exportConsumptionDetails(Integer userId, String startTime, String endTime, String format) {
        try {
            // 获取消费统计数据
            ConsumptionStat stat = userMapper.getConsumptionStats(userId, startTime, endTime);
            List<Map<String, Object>> trends = userMapper.getConsumptionTrend(userId, startTime, endTime, "day");
            List<Map<String, Object>> categories = userMapper.getCategoryConsumption(userId, startTime, endTime);
            
            if ("excel".equalsIgnoreCase(format)) {
                return exportToExcel(stat, trends, categories, startTime, endTime);
            } else if ("pdf".equalsIgnoreCase(format)) {
                return exportToPdf(stat, trends, categories, startTime, endTime);
            } else {
                // 默认导出Excel
                return exportToExcel(stat, trends, categories, startTime, endTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
    
    /**
     * 导出为Excel文件
     */
    private byte[] exportToExcel(ConsumptionStat stat, List<Map<String, Object>> trends, 
                                List<Map<String, Object>> categories, String startTime, String endTime) throws Exception {
        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        
        // 创建消费总览工作表
        Sheet overviewSheet = workbook.createSheet("消费总览");
        Row headerRow = overviewSheet.createRow(0);
        headerRow.createCell(0).setCellValue("统计时间");
        headerRow.createCell(1).setCellValue("订单数量");
        headerRow.createCell(2).setCellValue("总消费金额");
        headerRow.createCell(3).setCellValue("平均消费金额");
        
        Row dataRow = overviewSheet.createRow(1);
        dataRow.createCell(0).setCellValue(startTime + " 至 " + endTime);
        dataRow.createCell(1).setCellValue(stat != null && stat.getOrderCount() != null ? stat.getOrderCount() : 0);
        dataRow.createCell(2).setCellValue(stat != null && stat.getTotalAmount() != null ? stat.getTotalAmount() : 0.0);
        
        double avgAmount = 0.0;
        if (stat != null && stat.getOrderCount() != null && stat.getOrderCount() > 0 && stat.getTotalAmount() != null) {
            avgAmount = stat.getTotalAmount() / stat.getOrderCount();
        }
        dataRow.createCell(3).setCellValue(avgAmount);
        
        // 创建消费趋势工作表
        Sheet trendSheet = workbook.createSheet("消费趋势");
        Row trendHeaderRow = trendSheet.createRow(0);
        trendHeaderRow.createCell(0).setCellValue("日期");
        trendHeaderRow.createCell(1).setCellValue("消费金额");
        trendHeaderRow.createCell(2).setCellValue("订单数量");
        
        if (trends != null && !trends.isEmpty()) {
            for (int i = 0; i < trends.size(); i++) {
                Map<String, Object> trend = trends.get(i);
                Row row = trendSheet.createRow(i + 1);
                row.createCell(0).setCellValue((String) trend.get("timePoint"));
                row.createCell(1).setCellValue(((Number) trend.get("amount")).doubleValue());
                row.createCell(2).setCellValue(((Number) trend.get("count")).intValue());
            }
        }
        
        // 创建分类消费工作表
        Sheet categorySheet = workbook.createSheet("分类消费");
        Row categoryHeaderRow = categorySheet.createRow(0);
        categoryHeaderRow.createCell(0).setCellValue("分类");
        categoryHeaderRow.createCell(1).setCellValue("消费金额");
        
        if (categories != null && !categories.isEmpty()) {
            for (int i = 0; i < categories.size(); i++) {
                Map<String, Object> category = categories.get(i);
                Row row = categorySheet.createRow(i + 1);
                row.createCell(0).setCellValue((String) category.get("category"));
                row.createCell(1).setCellValue(((Number) category.get("amount")).doubleValue());
            }
        }
        
        // 自动调整列宽
        for (int i = 0; i < 4; i++) {
            overviewSheet.autoSizeColumn(i);
        }
        for (int i = 0; i < 3; i++) {
            trendSheet.autoSizeColumn(i);
        }
        for (int i = 0; i < 2; i++) {
            categorySheet.autoSizeColumn(i);
        }
        
        // 将工作簿写入字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return outputStream.toByteArray();
    }
    
    /**
     * 导出为PDF文件
     */
    private byte[] exportToPdf(ConsumptionStat stat, List<Map<String, Object>> trends, 
                              List<Map<String, Object>> categories, String startTime, String endTime) throws Exception {
        // 创建Document
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
        
        // 打开文档
        document.open();
        document.addTitle("消费统计报告");
        
        // 使用中文字体
        Font titleFont = new Font(BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), 16, Font.BOLD);
        Font headFont = new Font(BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), 12, Font.BOLD);
        Font textFont = new Font(BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), 10, Font.NORMAL);
        
        // 添加标题
        Paragraph title = new Paragraph("消费统计报告", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);
        
        // 添加时间范围
        Paragraph timeRange = new Paragraph("统计时间: " + startTime + " 至 " + endTime, textFont);
        document.add(timeRange);
        document.add(Chunk.NEWLINE);
        
        // 添加消费总览表格
        Paragraph overviewTitle = new Paragraph("消费总览", headFont);
        document.add(overviewTitle);
        document.add(Chunk.NEWLINE);
        
        PdfPTable overviewTable = new PdfPTable(4);
        overviewTable.setWidthPercentage(100);
        
        // 添加表头
        PdfPCell orderCountHeader = new PdfPCell(new Phrase("订单数量", headFont));
        PdfPCell totalAmountHeader = new PdfPCell(new Phrase("总消费金额", headFont));
        PdfPCell avgAmountHeader = new PdfPCell(new Phrase("平均消费金额", headFont));
        
        overviewTable.addCell(orderCountHeader);
        overviewTable.addCell(totalAmountHeader);
        overviewTable.addCell(avgAmountHeader);
        
        // 添加数据
        int orderCount = stat != null && stat.getOrderCount() != null ? stat.getOrderCount() : 0;
        double totalAmount = stat != null && stat.getTotalAmount() != null ? stat.getTotalAmount() : 0.0;
        double avgAmount = 0.0;
        if (stat != null && stat.getOrderCount() != null && stat.getOrderCount() > 0 && stat.getTotalAmount() != null) {
            avgAmount = stat.getTotalAmount() / stat.getOrderCount();
        }
        
        PdfPCell orderCountCell = new PdfPCell(new Phrase(String.valueOf(orderCount), textFont));
        PdfPCell totalAmountCell = new PdfPCell(new Phrase(String.valueOf(totalAmount), textFont));
        PdfPCell avgAmountCell = new PdfPCell(new Phrase(String.valueOf(avgAmount), textFont));
        
        overviewTable.addCell(orderCountCell);
        overviewTable.addCell(totalAmountCell);
        overviewTable.addCell(avgAmountCell);
        
        document.add(overviewTable);
        document.add(Chunk.NEWLINE);
        
        // 添加消费趋势表格
        if (trends != null && !trends.isEmpty()) {
            Paragraph trendTitle = new Paragraph("消费趋势", headFont);
            document.add(trendTitle);
            document.add(Chunk.NEWLINE);
            
            PdfPTable trendTable = new PdfPTable(3);
            trendTable.setWidthPercentage(100);
            
            // 添加表头
            PdfPCell dateHeader = new PdfPCell(new Phrase("日期", headFont));
            PdfPCell amountHeader = new PdfPCell(new Phrase("消费金额", headFont));
            PdfPCell countHeader = new PdfPCell(new Phrase("订单数量", headFont));
            
            trendTable.addCell(dateHeader);
            trendTable.addCell(amountHeader);
            trendTable.addCell(countHeader);
            
            // 添加数据
            for (Map<String, Object> trend : trends) {
                PdfPCell dateCell = new PdfPCell(new Phrase((String) trend.get("timePoint"), textFont));
                PdfPCell amountCell = new PdfPCell(new Phrase(String.valueOf(((Number) trend.get("amount")).doubleValue()), textFont));
                PdfPCell countCell = new PdfPCell(new Phrase(String.valueOf(((Number) trend.get("count")).intValue()), textFont));
                
                trendTable.addCell(dateCell);
                trendTable.addCell(amountCell);
                trendTable.addCell(countCell);
            }
            
            document.add(trendTable);
            document.add(Chunk.NEWLINE);
        }
        
        // 添加分类消费表格
        if (categories != null && !categories.isEmpty()) {
            Paragraph categoryTitle = new Paragraph("分类消费", headFont);
            document.add(categoryTitle);
            document.add(Chunk.NEWLINE);
            
            PdfPTable categoryTable = new PdfPTable(2);
            categoryTable.setWidthPercentage(100);
            
            // 添加表头
            PdfPCell categoryHeader = new PdfPCell(new Phrase("分类", headFont));
            PdfPCell categoryAmountHeader = new PdfPCell(new Phrase("消费金额", headFont));
            
            categoryTable.addCell(categoryHeader);
            categoryTable.addCell(categoryAmountHeader);
            
            // 添加数据
            for (Map<String, Object> category : categories) {
                PdfPCell categoryCell = new PdfPCell(new Phrase((String) category.get("category"), textFont));
                PdfPCell categoryAmountCell = new PdfPCell(new Phrase(String.valueOf(((Number) category.get("amount")).doubleValue()), textFont));
                
                categoryTable.addCell(categoryCell);
                categoryTable.addCell(categoryAmountCell);
            }
            
            document.add(categoryTable);
        }
        
        // 关闭文档
        document.close();
        
        return outputStream.toByteArray();
    }
} 