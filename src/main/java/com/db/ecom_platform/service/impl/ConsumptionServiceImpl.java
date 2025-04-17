package com.db.ecom_platform.service.impl;

import com.db.ecom_platform.entity.ConsumptionStat;
import com.db.ecom_platform.entity.vo.ConsumptionCompareVO;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    public ConsumptionStatVO getConsumptionStats(Integer userId, String createTime, String endTime, String timeUnit) {
        ConsumptionStat stat = userMapper.getConsumptionStats(userId, createTime, endTime);
        if (stat == null) {
            return new ConsumptionStatVO();
        }
        
        ConsumptionStatVO statVO = new ConsumptionStatVO();
        statVO.setUserId(userId);
        statVO.setTimeRange(createTime + " 至 " + endTime);
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
        List<Map<String, Object>> categoryConsumptions = userMapper.getCategoryConsumption(userId, createTime, endTime);
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
    public ConsumptionStat getConsumptionStats(Integer userId, String timeRange) {
        // 根据时间范围计算开始和结束时间
        String[] dates = getDateRangeByTimeRange(timeRange);
        String createTime = dates[0];
        String endTime = dates[1];
        
        // 调用数据库查询
        return userMapper.getConsumptionStats(userId, createTime, endTime);
    }
    
    @Override
    public List<ConsumptionTrendVO> getConsumptionTrend(Integer userId, String createTime, String endTime, String timeUnit) {
        List<Map<String, Object>> trends = userMapper.getConsumptionTrend(userId, createTime, endTime, timeUnit);
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
    public Map<String, Object> getConsumptionTrend(Integer userId, String timeRange) {
        // 根据时间范围计算开始和结束时间
        String[] dates = getDateRangeByTimeRange(timeRange);
        String createTime = dates[0];
        String endTime = dates[1];
        String timeUnit = getTimeUnitByTimeRange(timeRange);
        
        // 获取趋势数据
        List<ConsumptionTrendVO> trends = getConsumptionTrend(userId, createTime, endTime, timeUnit);
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("timeRange", timeRange);
        result.put("createTime", createTime);
        result.put("endTime", endTime);
        result.put("data", trends);
        
        return result;
    }
    
    @Override
    public Map<String, Double> getCategoryConsumption(Integer userId, String createTime, String endTime) {
        List<Map<String, Object>> categoryConsumptions = userMapper.getCategoryConsumption(userId, createTime, endTime);
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
    public List<Map<String, Object>> getCategoryDistribution(Integer userId, String timeRange) {
        // 根据时间范围计算开始和结束时间
        String[] dates = getDateRangeByTimeRange(timeRange);
        String createTime = dates[0];
        String endTime = dates[1];
        
        // 获取分类消费数据
        Map<String, Double> categoryMap = getCategoryConsumption(userId, createTime, endTime);
        
        // 计算总消费金额
        double totalAmount = categoryMap.values().stream().mapToDouble(Double::doubleValue).sum();
        
        // 构建返回结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("category", entry.getKey());
            item.put("amount", entry.getValue());
            // 计算百分比，保留两位小数
            double percentage = totalAmount > 0 ? Math.round(entry.getValue() / totalAmount * 10000) / 100.0 : 0;
            item.put("percentage", percentage);
            result.add(item);
        }
        
        return result;
    }
    
    @Override
    public ConsumptionCompareVO getConsumptionCompare(Integer userId, String timeRange, String compareType) {
        // 计算当前时间范围
        String[] currentDates = getDateRangeByTimeRange(timeRange);
        String currentCreateTime = currentDates[0];
        String currentEndTime = currentDates[1];
        
        // 计算对比时间范围
        String[] previousDates;
        if ("mom".equals(compareType)) {
            // 环比（Month-on-Month）：与上个月相比
            previousDates = getPreviousMonthDateRange(currentCreateTime, currentEndTime);
        } else {
            // 同比（Year-on-Year）：与去年同期相比
            previousDates = getPreviousYearDateRange(currentCreateTime, currentEndTime);
        }
        
        String previousCreateTime = previousDates[0];
        String previousEndTime = previousDates[1];
        
        // 获取当前时间段的消费数据
        ConsumptionStat currentStat = userMapper.getConsumptionStats(userId, currentCreateTime, currentEndTime);
        
        // 获取对比时间段的消费数据
        ConsumptionStat previousStat = userMapper.getConsumptionStats(userId, previousCreateTime, previousEndTime);
        
        // 构建返回结果
        ConsumptionCompareVO compareVO = new ConsumptionCompareVO();
        compareVO.setUserId(userId);
        compareVO.setTimeRange(timeRange);
        compareVO.setCompareType(compareType);
        compareVO.setCurrentPeriod(currentCreateTime + " 至 " + currentEndTime);
        compareVO.setPreviousPeriod(previousCreateTime + " 至 " + previousEndTime);
        
        // 设置当前时间段数据
        double currentAmount = currentStat != null && currentStat.getTotalAmount() != null ? currentStat.getTotalAmount() : 0;
        int currentCount = currentStat != null && currentStat.getOrderCount() != null ? currentStat.getOrderCount() : 0;
        compareVO.setCurrentAmount(currentAmount);
        compareVO.setCurrentOrderCount(currentCount);
        
        // 设置对比时间段数据
        double previousAmount = previousStat != null && previousStat.getTotalAmount() != null ? previousStat.getTotalAmount() : 0;
        int previousCount = previousStat != null && previousStat.getOrderCount() != null ? previousStat.getOrderCount() : 0;
        compareVO.setPreviousAmount(previousAmount);
        compareVO.setPreviousOrderCount(previousCount);
        
        // 计算环比/同比增长率
        if (previousAmount > 0) {
            double amountChangeRate = (currentAmount - previousAmount) / previousAmount * 100;
            compareVO.setAmountChangeRate(Math.round(amountChangeRate * 100) / 100.0); // 保留两位小数
        } else {
            compareVO.setAmountChangeRate(currentAmount > 0 ? 100.0 : 0.0);
        }
        
        if (previousCount > 0) {
            double countChangeRate = (currentCount - previousCount) / (double) previousCount * 100;
            compareVO.setCountChangeRate(Math.round(countChangeRate * 100) / 100.0); // 保留两位小数
        } else {
            compareVO.setCountChangeRate(currentCount > 0 ? 100.0 : 0.0);
        }
        
        return compareVO;
    }
    
    @Override
    public Map<String, Object> getConsumptionRank(Integer userId, String timeRange) {
        // 根据时间范围计算开始和结束时间
        String[] dates = getDateRangeByTimeRange(timeRange);
        String createTime = dates[0];
        String endTime = dates[1];
        
        // 查询用户消费排名
        Integer rank = userMapper.getUserConsumptionRank(userId, createTime, endTime);
        Integer totalUsers = userMapper.getTotalConsumptionUsers(createTime, endTime);
        Double amount = userMapper.getUserTotalConsumption(userId, createTime);
        Double maxAmount = userMapper.getMaxConsumptionAmount(createTime, endTime);
        Double avgAmount = userMapper.getAvgConsumptionAmount(createTime, endTime);
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("timeRange", timeRange);
        result.put("rank", rank != null ? rank : 0);
        result.put("totalUsers", totalUsers != null ? totalUsers : 0);
        
        // 计算超过的用户百分比，保留两位小数
        if (totalUsers != null && totalUsers > 0 && rank != null) {
            double percentage = ((double) (totalUsers - rank) / totalUsers) * 100;
            result.put("beatPercentage", Math.round(percentage * 100) / 100.0);
        } else {
            result.put("beatPercentage", 0.0);
        }
        
        result.put("amount", amount != null ? amount : 0.0);
        result.put("maxAmount", maxAmount != null ? maxAmount : 0.0);
        result.put("avgAmount", avgAmount != null ? avgAmount : 0.0);
        
        return result;
    }
    
    @Override
    public Map<String, Object> getAverageConsumption(Integer userId, String timeRange) {
        // 根据时间范围计算开始和结束时间
        String[] dates = getDateRangeByTimeRange(timeRange);
        String createTime = dates[0];
        String endTime = dates[1];
        
        // 查询用户消费数据
        Double totalAmount = userMapper.getUserTotalConsumption(userId, createTime);
        Integer orderCount = userMapper.getUserOrderCount(userId, createTime);
        Double avgAmount = userMapper.getUserAvgConsumption(userId, createTime);
        Double platformAvgAmount = userMapper.getAvgConsumptionAmount(createTime, endTime);
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("timeRange", timeRange);
        result.put("totalAmount", totalAmount != null ? totalAmount : 0.0);
        result.put("orderCount", orderCount != null ? orderCount : 0);
        result.put("avgAmount", avgAmount != null ? avgAmount : 0.0);
        result.put("platformAvgAmount", platformAvgAmount != null ? platformAvgAmount : 0.0);
        
        // 计算与平台平均值的对比，保留两位小数
        if (platformAvgAmount != null && platformAvgAmount > 0 && avgAmount != null) {
            double compareRate = (avgAmount - platformAvgAmount) / platformAvgAmount * 100;
            result.put("compareRate", Math.round(compareRate * 100) / 100.0);
        } else {
            result.put("compareRate", 0.0);
        }
        
        return result;
    }
    
    @Override
    public byte[] exportConsumptionDetails(Integer userId, String createTime, String endTime, String format) {
        try {
            // 获取消费统计数据
            ConsumptionStat stat = userMapper.getConsumptionStats(userId, createTime, endTime);
            List<Map<String, Object>> trends = userMapper.getConsumptionTrend(userId, createTime, endTime, "day");
            List<Map<String, Object>> categories = userMapper.getCategoryConsumption(userId, createTime, endTime);
            
            if ("excel".equalsIgnoreCase(format)) {
                return exportToExcel(stat, trends, categories, createTime, endTime);
            } else if ("pdf".equalsIgnoreCase(format)) {
                return exportToPdf(stat, trends, categories, createTime, endTime);
            } else {
                // 默认导出Excel
                return exportToExcel(stat, trends, categories, createTime, endTime);
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
                                List<Map<String, Object>> categories, String createTime, String endTime) throws Exception {
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
        dataRow.createCell(0).setCellValue(createTime + " 至 " + endTime);
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
                              List<Map<String, Object>> categories, String createTime, String endTime) throws Exception {
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
        Paragraph timeRange = new Paragraph("统计时间: " + createTime + " 至 " + endTime, textFont);
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
    
    /**
     * 根据时间范围获取日期范围
     * @param timeRange 时间范围（week/month/year/all）
     * @return 开始和结束日期数组 [startTime, endTime]
     */
    private String[] getDateRangeByTimeRange(String timeRange) {
        LocalDate now = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate = now;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        switch (timeRange.toLowerCase()) {
            case "week":
                startDate = now.minusWeeks(1);
                break;
            case "month":
                startDate = now.minusMonths(1);
                break;
            case "year":
                startDate = now.minusYears(1);
                break;
            case "all":
            default:
                // 所有时间：默认从五年前开始
                startDate = now.minusYears(5);
                break;
        }
        
        return new String[]{startDate.format(formatter), endDate.format(formatter)};
    }
    
    /**
     * 根据时间范围获取时间单位
     * @param timeRange 时间范围（week/month/year/all）
     * @return 时间单位（day/week/month/year）
     */
    private String getTimeUnitByTimeRange(String timeRange) {
        switch (timeRange.toLowerCase()) {
            case "week":
                return "day";
            case "month":
                return "day";
            case "year":
                return "month";
            case "all":
            default:
                return "year";
        }
    }
    
    /**
     * 获取上个月的日期范围
     * @param createTime 当前创建时间
     * @param endTime 当前结束时间
     * @return 上个月的开始和结束日期数组 [createTime, endTime]
     */
    private String[] getPreviousMonthDateRange(String createTime, String endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(createTime, formatter);
        LocalDate end = LocalDate.parse(endTime, formatter);
        
        LocalDate previousStart = start.minusMonths(1);
        LocalDate previousEnd = end.minusMonths(1);
        
        return new String[]{previousStart.format(formatter), previousEnd.format(formatter)};
    }
    
    /**
     * 获取去年同期的日期范围
     * @param createTime 当前创建时间
     * @param endTime 当前结束时间
     * @return 去年同期的开始和结束日期数组 [createTime, endTime]
     */
    private String[] getPreviousYearDateRange(String createTime, String endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(createTime, formatter);
        LocalDate end = LocalDate.parse(endTime, formatter);
        
        LocalDate previousStart = start.minusYears(1);
        LocalDate previousEnd = end.minusYears(1);
        
        return new String[]{previousStart.format(formatter), previousEnd.format(formatter)};
    }
} 