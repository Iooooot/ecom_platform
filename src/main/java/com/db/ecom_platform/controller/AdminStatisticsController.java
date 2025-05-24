package com.db.ecom_platform.controller;

import com.db.ecom_platform.utils.Result;
import com.db.ecom_platform.service.AdminStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

/**
 * 管理员数据统计控制器
 */
@Api(tags = "管理员数据统计接口")
@RestController
@RequestMapping("/api/admin/statistics")
public class AdminStatisticsController {

    @Autowired
    private AdminStatisticsService adminStatisticsService;

    /**
     * 获取实时销售数据
     * @return 实时销售数据
     */
    @ApiOperation(value = "获取实时销售数据", notes = "返回今日销售额、订单量、同比增长等实时统计数据")
    @GetMapping("/realtime")
    public Result<Map<String, Object>> getRealTimeStatistics() {
        Map<String, Object> data = adminStatisticsService.getRealTimeStatistics();
        return Result.success(data);
    }

    /**
     * 获取热销商品Top10
     * @return 热销商品列表
     */
    @ApiOperation(value = "获取热销商品Top10", notes = "返回最近30天内销量最高的10个商品")
    @GetMapping("/hot-products")
    public Result<?> getHotProducts() {
        return Result.success(adminStatisticsService.getHotProducts(10));
    }

    /**
     * 按地区分析消费数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 地区消费数据
     */
    @ApiOperation(value = "按地区分析消费数据", notes = "根据时间范围统计各地区的销售额、订单量和客单价")
    @GetMapping("/region-analysis")
    public Result<?> getRegionAnalysis(
            @ApiParam(value = "开始日期", example = "2023-01-01") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @ApiParam(value = "结束日期", example = "2023-12-31") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        return Result.success(adminStatisticsService.getRegionAnalysis(startDate, endDate));
    }

    /**
     * 按用户年龄段分析消费偏好
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 年龄段消费偏好数据
     */
    @ApiOperation(value = "按用户年龄段分析消费偏好", notes = "根据时间范围统计各年龄段的消费总额、订单量和品类偏好")
    @GetMapping("/age-group-analysis")
    public Result<?> getAgeGroupAnalysis(
            @ApiParam(value = "开始日期", example = "2023-01-01") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @ApiParam(value = "结束日期", example = "2023-12-31") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        return Result.success(adminStatisticsService.getAgeGroupAnalysis(startDate, endDate));
    }

    /**
     * 获取销售数据看板
     * @param period 时间周期（day/week/month/year）
     * @return 销售数据看板
     */
    @ApiOperation(value = "获取销售数据看板", notes = "根据指定时间周期获取销售趋势、订单趋势、热销商品等数据")
    @GetMapping("/dashboard")
    public Result<?> getDashboardData(
            @ApiParam(value = "时间周期", example = "day", allowableValues = "day,week,month,year") 
            @RequestParam(defaultValue = "day") String period) {
        return Result.success(adminStatisticsService.getDashboardData(period));
    }

    /**
     * 生成销售报表
     * @param reportType 报表类型（daily/weekly/monthly）
     * @param format 格式（pdf/excel）
     * @param date 指定日期
     * @param response HTTP响应对象
     */
    @ApiOperation(value = "生成销售报表", notes = "根据报表类型和日期生成销售报表，支持PDF和Excel格式")
    @GetMapping("/report/{reportType}")
    public void generateReport(
            @ApiParam(value = "报表类型", example = "daily", allowableValues = "daily,weekly,monthly") 
            @PathVariable String reportType,
            @ApiParam(value = "报表格式", example = "pdf", allowableValues = "pdf,excel") 
            @RequestParam(defaultValue = "pdf") String format,
            @ApiParam(value = "报表日期", example = "2023-12-31") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpServletResponse response) throws IOException {
        
        if (date == null) {
            date = LocalDate.now().minusDays(1); // 默认生成昨天的报表
        }
        
        byte[] reportData = adminStatisticsService.generateReport(reportType, format, date);
        
        // 修复文件扩展名问题，Excel格式使用xlsx后缀
        String fileExtension = format.equalsIgnoreCase("excel") ? "xlsx" : format.toLowerCase();
        String fileName = String.format("%s_report_%s.%s", 
                reportType, date.toString(), fileExtension);
        
        response.setContentType(format.equalsIgnoreCase("pdf") ? 
                "application/pdf" : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.getOutputStream().write(reportData);
        response.getOutputStream().flush();
    }
} 