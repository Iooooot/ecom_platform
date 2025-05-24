package com.db.ecom_platform.service.impl;

import com.db.ecom_platform.entity.ConsumptionStat;
import com.db.ecom_platform.entity.vo.ConsumptionCompareVO;
import com.db.ecom_platform.entity.vo.ConsumptionStatVO;
import com.db.ecom_platform.entity.vo.ConsumptionTrendVO;
import com.db.ecom_platform.mapper.UserMapper;
import com.db.ecom_platform.service.ConsumptionService;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
@Slf4j
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
    public Map<String, Object> getConsumptionDetails(Integer userId, String startTime, String endTime, Integer page, Integer size) {
        // 计算偏移量
        int offset = (page - 1) * size;
        
        // 查询消费明细
        List<Map<String, Object>> details = userMapper.getConsumptionDetails(userId, startTime, endTime, offset, size);
        
        // 查询总记录数
        int total = userMapper.getConsumptionDetailsCount(userId, startTime, endTime);
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", details);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size); // 计算总页数
        
        return result;
    }
    
    @Override
    public byte[] exportConsumptionDetails(Integer userId, String startTime, String endTime, String format) {
        // 获取消费明细数据
        Map<String, Object> detailsResult = getConsumptionDetails(userId, startTime, endTime, 1, 1000);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> details = (List<Map<String, Object>>) detailsResult.get("list");

        try {
            if ("excel".equalsIgnoreCase(format)) {
                return generateExcelReport(details, startTime, endTime);
            } else {
                return generatePdfReport(details, startTime, endTime);
            }
        } catch (Exception e) {
            log.error("导出消费明细时出错", e);
            if ("excel".equalsIgnoreCase(format)) {
                try {
                    return "Excel生成错误。".getBytes("UTF-8");
                } catch (Exception ex) {
                    return new byte[]{69, 114, 114, 111, 114}; // "Error" in ASCII
                }
            } else {
                try {
                    return createMinimalErrorPdf(startTime, endTime);
                } catch (Exception ex) {
                    try {
                        return "PDF生成错误。".getBytes("UTF-8");
                    } catch (Exception innerEx) {
                        return new byte[]{69, 114, 114, 111, 114}; // "Error" in ASCII
                    }
                }
            }
        }
    }
    
    /**
     * 生成Excel报表
     */
    private byte[] generateExcelReport(List<Map<String, Object>> details, String startTime, String endTime) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // 创建工作簿和工作表
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("消费明细");
            
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("订单号");
            headerRow.createCell(1).setCellValue("下单时间");
            headerRow.createCell(2).setCellValue("商品品类");
            headerRow.createCell(3).setCellValue("商品名称");
            headerRow.createCell(4).setCellValue("消费金额");
            headerRow.createCell(5).setCellValue("商品数量");
            
            // 填充数据
            int rowNum = 1;
            for (Map<String, Object> detail : details) {
                // 格式化时间，去掉T字符
                String formattedTime = String.valueOf(detail.get("createTime")).replace('T', ' ');
                
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(String.valueOf(detail.get("orderId")));
                row.createCell(1).setCellValue(formattedTime);
                row.createCell(2).setCellValue(String.valueOf(detail.get("categoryName")));
                row.createCell(3).setCellValue(String.valueOf(detail.get("productNames")));
                row.createCell(4).setCellValue(((Number) detail.get("amount")).doubleValue());
                row.createCell(5).setCellValue(((Number) detail.get("itemCount")).intValue());
            }
            
            // 自动调整列宽
            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 写入输出流
            workbook.write(baos);
            return baos.toByteArray();
        }
    }
    
    /**
     * 生成PDF报表 (使用iText 7)
     */
    private byte[] generatePdfReport(List<Map<String, Object>> details, String startTime, String endTime) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // 创建PDF文档
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // 添加标题
            Paragraph title = new Paragraph("消费统计报表");
            title.setFontSize(18).setBold().setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            
            // 添加副标题
            Paragraph subtitle = new Paragraph("统计时间: " + startTime + " 至 " + endTime);
            subtitle.setFontSize(12).setTextAlignment(TextAlignment.CENTER);
            subtitle.setMarginBottom(15);
            document.add(subtitle);
            
            // 创建表格
            Table table = new Table(new float[]{3f, 2.5f, 2f, 4f, 2f, 1.5f});
            table.setWidth(UnitValue.createPercentValue(100));
            
            // 添加表头
            table.addHeaderCell(createHeaderCell("订单号"));
            table.addHeaderCell(createHeaderCell("下单时间"));
            table.addHeaderCell(createHeaderCell("商品品类"));
            table.addHeaderCell(createHeaderCell("商品名称"));
            table.addHeaderCell(createHeaderCell("消费金额"));
            table.addHeaderCell(createHeaderCell("商品数量"));
            
            // 计算总金额
            double totalAmount = 0.0;
            
            // 添加数据行
            for (Map<String, Object> detail : details) {
                try {
                    // 格式化时间，去掉T字符
                    String formattedTime = detail.get("createTime") != null ? 
                        String.valueOf(detail.get("createTime")).replace('T', ' ') : "";
                    
                    // 确保每个字段都有值，防止NPE
                    String orderId = detail.get("orderId") != null ? String.valueOf(detail.get("orderId")) : "";
                    String categoryName = detail.get("categoryName") != null ? String.valueOf(detail.get("categoryName")) : "";
                    String productNames = detail.get("productNames") != null ? String.valueOf(detail.get("productNames")) : "";
                    
                    double amount = 0.0;
                    int itemCount = 0;
                    
                    if (detail.get("amount") != null) {
                        try {
                            amount = ((Number) detail.get("amount")).doubleValue();
                            totalAmount += amount;
                        } catch (Exception e) {
                            log.warn("无法转换金额: " + detail.get("amount"), e);
                        }
                    }
                    
                    if (detail.get("itemCount") != null) {
                        try {
                            itemCount = ((Number) detail.get("itemCount")).intValue();
                        } catch (Exception e) {
                            log.warn("无法转换商品数量: " + detail.get("itemCount"), e);
                        }
                    }
                    
                    // 添加单元格
                    table.addCell(createCell(orderId));
                    table.addCell(createCell(formattedTime));
                    table.addCell(createCell(categoryName));
                    table.addCell(createCell(productNames));
                    
                    Cell amountCell = createCell("¥" + String.format("%.2f", amount));
                    amountCell.setTextAlignment(TextAlignment.RIGHT);
                    table.addCell(amountCell);
                    
                    Cell countCell = createCell(String.valueOf(itemCount));
                    countCell.setTextAlignment(TextAlignment.CENTER);
                    table.addCell(countCell);
                } catch (Exception e) {
                    log.error("处理PDF数据行时出错", e);
                    // 继续处理下一行，不中断整个导出过程
                }
            }
            
            document.add(table);
            
            // 添加总计
            Paragraph summary = new Paragraph("总消费金额: ¥" + String.format("%.2f", totalAmount));
            summary.setTextAlignment(TextAlignment.RIGHT);
            summary.setMarginTop(10);
            summary.setBold();
            document.add(summary);
            
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("生成PDF报表时出错", e);
            // 返回一个最基本的PDF
            return createMinimalErrorPdf(startTime, endTime);
        }
    }
    
    /**
     * 创建表头单元格
     */
    private Cell createHeaderCell(String text) {
        Cell cell = new Cell();
        cell.add(new Paragraph(text));
        cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        cell.setBold();
        cell.setTextAlignment(TextAlignment.CENTER);
        return cell;
    }
    
    /**
     * 创建普通单元格
     */
    private Cell createCell(String text) {
        Cell cell = new Cell();
        cell.add(new Paragraph(text));
        return cell;
    }
    
    /**
     * 创建最小化错误PDF（确保永远不会返回0字节）
     */
    private byte[] createMinimalErrorPdf(String startTime, String endTime) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // 创建PDF文档
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // 添加错误信息
            Paragraph title = new Paragraph("生成报表时出错");
            title.setFontSize(14).setBold().setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            
            Paragraph message = new Paragraph("无法生成详细报表。请尝试使用Excel格式导出。");
            message.setMarginTop(20);
            document.add(message);
            
            Paragraph timeRange = new Paragraph("请求的时间范围: " + startTime + " 至 " + endTime);
            timeRange.setMarginTop(10);
            document.add(timeRange);
            
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("创建最小化错误PDF时出错", e);
            try {
                return "PDF生成错误。请使用Excel格式导出。".getBytes("UTF-8");
            } catch (Exception ex) {
                return new byte[] {80, 68, 70, 32, 69, 114, 114, 111, 114}; // "PDF Error" in ASCII
            }
        }
    }
    
    /**
     * 根据时间范围计算日期范围
     */
    private String[] getDateRangeByTimeRange(String timeRange) {
        LocalDate now = LocalDate.now();
        String endTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String startTime;
        
        switch (timeRange) {
            case "week":
                startTime = now.minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                break;
            case "month":
                startTime = now.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                break;
            case "year":
                startTime = now.minusYears(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                break;
            case "all":
                startTime = "2000-01-01"; // 设置一个足够早的日期
                break;
            default:
                startTime = now.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        
        return new String[] { startTime, endTime };
    }
    
    /**
     * 根据时间范围获取时间单位
     */
    private String getTimeUnitByTimeRange(String timeRange) {
        switch (timeRange) {
            case "week":
                return "day";
            case "month":
                return "day";
            case "year":
                return "month";
            default:
                return "day";
        }
    }
    
    /**
     * 获取上个月的日期范围
     */
    private String[] getPreviousMonthDateRange(String startTime, String endTime) {
        LocalDate start = LocalDate.parse(startTime);
        LocalDate end = LocalDate.parse(endTime);
        
        LocalDate previousStart = start.minusMonths(1);
        LocalDate previousEnd = end.minusMonths(1);
        
        return new String[] { 
            previousStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            previousEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        };
    }
    
    /**
     * 获取去年同期的日期范围
     */
    private String[] getPreviousYearDateRange(String startTime, String endTime) {
        LocalDate start = LocalDate.parse(startTime);
        LocalDate end = LocalDate.parse(endTime);
        
        LocalDate previousStart = start.minusYears(1);
        LocalDate previousEnd = end.minusYears(1);
        
        return new String[] { 
            previousStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            previousEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        };
    }
} 