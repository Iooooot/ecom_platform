package com.db.ecom_platform.controller;

import com.db.ecom_platform.entity.vo.ConsumptionStatVO;
import com.db.ecom_platform.entity.vo.ConsumptionTrendVO;
import com.db.ecom_platform.service.ConsumptionService;
import com.db.ecom_platform.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消费统计控制器
 */
@RestController
@RequestMapping("/api/consumption")
public class ConsumptionController {
    
    @Autowired
    private ConsumptionService consumptionService;
    
    /**
     * 获取消费统计（按时间段）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param timeUnit 时间单位（day/week/month/year）
     */
    @GetMapping("/stats")
    public Result getConsumptionStats(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam String timeUnit) {
        Integer userId = getCurrentUserId();
        ConsumptionStatVO stats = consumptionService.getConsumptionStats(userId, startTime, endTime, timeUnit);
        return Result.success(stats);
    }
    
    /**
     * 获取消费趋势
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param timeUnit 时间单位（day/week/month/year）
     */
    @GetMapping("/trend")
    public Result getConsumptionTrend(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam String timeUnit) {
        Integer userId = getCurrentUserId();
        List<ConsumptionTrendVO> trend = consumptionService.getConsumptionTrend(userId, startTime, endTime, timeUnit);
        return Result.success(trend);
    }
    
    /**
     * 获取分类消费占比
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    @GetMapping("/category")
    public Result getCategoryConsumption(
            @RequestParam String startTime,
            @RequestParam String endTime) {
        Integer userId = getCurrentUserId();
        Map<String, Double> categoryConsumption = consumptionService.getCategoryConsumption(userId, startTime, endTime);
        return Result.success(categoryConsumption);
    }
    
    /**
     * 导出消费明细（Excel/PDF）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param format 格式（excel/pdf）
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportConsumptionDetails(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam String format) {
        Integer userId = getCurrentUserId();
        byte[] data = consumptionService.exportConsumptionDetails(userId, startTime, endTime, format);
        
        HttpHeaders headers = new HttpHeaders();
        if ("excel".equalsIgnoreCase(format)) {
            headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
            headers.setContentDispositionFormData("attachment", "consumption_details.xlsx");
        } else if ("pdf".equalsIgnoreCase(format)) {
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "consumption_details.pdf");
        }
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
    
    /**
     * 获取当前登录用户ID
     * 实际实现应该从安全上下文中获取
     */
    private Integer getCurrentUserId() {
        // 这里只是占位符，实际实现应该基于你的身份验证系统
        return 1; // 假设用户ID为1
    }
} 