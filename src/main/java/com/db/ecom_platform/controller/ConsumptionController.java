package com.db.ecom_platform.controller;

import com.db.ecom_platform.entity.ConsumptionStat;
import com.db.ecom_platform.entity.vo.ConsumptionCompareVO;
import com.db.ecom_platform.entity.vo.ConsumptionStatVO;
import com.db.ecom_platform.entity.vo.ConsumptionTrendVO;
import com.db.ecom_platform.service.ConsumptionService;
import com.db.ecom_platform.utils.Result;
import com.db.ecom_platform.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * 消费统计控制器
 */
@Api(tags = "消费统计")
@RestController
@Log4j2
@RequestMapping("/api/consumption")
public class ConsumptionController {
    
    @Autowired
    private ConsumptionService consumptionService;
    
    /**
     * 获取用户消费统计（简化版）
     */
    @ApiOperation(value = "获取消费统计（简化版）", notes = "获取当前用户的消费统计数据，支持不同时间范围")
    @ApiImplicitParam(name = "timeRange", value = "时间范围", required = false, dataTypeClass = String.class, 
        paramType = "query", allowableValues = "week,month,year,all", defaultValue = "month")
    @GetMapping("/stats")
    public Result<ConsumptionStat> getConsumptionStats(@RequestParam(defaultValue = "month") String timeRange) {
        Integer userId = UserUtils.getCurrentUserId();
        ConsumptionStat stats = consumptionService.getConsumptionStats(userId, timeRange);
        return Result.success(stats);
    }
    
    /**
     * 获取消费统计详情（详细版）
     */
    @ApiOperation(value = "获取消费统计详情", notes = "获取当前用户的详细消费统计数据，支持自定义时间范围")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "startTime", value = "开始时间(yyyy-MM-dd)", required = true, paramType = "query", dataTypeClass = String.class),
        @ApiImplicitParam(name = "endTime", value = "结束时间(yyyy-MM-dd)", required = true, paramType = "query", dataTypeClass = String.class),
        @ApiImplicitParam(name = "timeUnit", value = "时间单位", required = false, paramType = "query", dataTypeClass = String.class, 
            allowableValues = "day,week,month,year", defaultValue = "day")
    })
    @GetMapping("/stats/detail")
    public Result<ConsumptionStatVO> getConsumptionStatsDetail(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(defaultValue = "day") String timeUnit) {
        Integer userId = UserUtils.getCurrentUserId();
        ConsumptionStatVO stats = consumptionService.getConsumptionStats(userId, startTime, endTime, timeUnit);
        return Result.success(stats);
    }
    
    /**
     * 获取消费趋势（简化版）
     */
    @ApiOperation(value = "获取消费趋势（简化版）", notes = "获取当前用户的消费趋势数据，支持不同时间范围")
    @ApiImplicitParam(name = "timeRange", value = "时间范围", required = false, dataTypeClass = String.class, 
        paramType = "query", allowableValues = "week,month,year", defaultValue = "month")
    @GetMapping("/trend")
    public Result<Map<String, Object>> getConsumptionTrend(@RequestParam(defaultValue = "month") String timeRange) {
        Integer userId = UserUtils.getCurrentUserId();
        Map<String, Object> trend = consumptionService.getConsumptionTrend(userId, timeRange);
        return Result.success(trend);
    }
    
    /**
     * 获取消费趋势详情（详细版）
     */
    @ApiOperation(value = "获取消费趋势详情", notes = "获取当前用户的详细消费趋势数据，支持自定义时间范围")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "startTime", value = "开始时间(yyyy-MM-dd)", required = true, paramType = "query", dataTypeClass = String.class),
        @ApiImplicitParam(name = "endTime", value = "结束时间(yyyy-MM-dd)", required = true, paramType = "query", dataTypeClass = String.class),
        @ApiImplicitParam(name = "timeUnit", value = "时间单位", required = false, paramType = "query", dataTypeClass = String.class, 
            allowableValues = "day,week,month,year", defaultValue = "day")
    })
    @GetMapping("/trend/detail")
    public Result<List<ConsumptionTrendVO>> getConsumptionTrendDetail(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(defaultValue = "day") String timeUnit) {
        Integer userId = UserUtils.getCurrentUserId();
        List<ConsumptionTrendVO> trend = consumptionService.getConsumptionTrend(userId, startTime, endTime, timeUnit);
        return Result.success(trend);
    }
    
    /**
     * 获取消费品类分布
     */
    @ApiOperation(value = "获取消费品类分布", notes = "获取当前用户的消费品类分布数据")
    @ApiImplicitParam(name = "timeRange", value = "时间范围", required = false, dataTypeClass = String.class, 
        paramType = "query", allowableValues = "week,month,year,all", defaultValue = "month")
    @GetMapping("/category")
    public Result<List<Map<String, Object>>> getCategoryDistribution(@RequestParam(defaultValue = "month") String timeRange) {
        Integer userId = UserUtils.getCurrentUserId();
        List<Map<String, Object>> distribution = consumptionService.getCategoryDistribution(userId, timeRange);
        return Result.success(distribution);
    }
    
    /**
     * 获取消费品类分布详情（详细版）
     */
    @ApiOperation(value = "获取消费品类分布详情", notes = "获取当前用户的详细消费品类分布数据，支持自定义时间范围")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "startTime", value = "开始时间(yyyy-MM-dd)", required = true, paramType = "query", dataTypeClass = String.class),
        @ApiImplicitParam(name = "endTime", value = "结束时间(yyyy-MM-dd)", required = true, paramType = "query", dataTypeClass = String.class)
    })
    @GetMapping("/category/detail")
    public Result<Map<String, Double>> getCategoryDistributionDetail(
            @RequestParam String startTime,
            @RequestParam String endTime) {
        Integer userId = UserUtils.getCurrentUserId();
        Map<String, Double> distribution = consumptionService.getCategoryConsumption(userId, startTime, endTime);
        return Result.success(distribution);
    }
    
    /**
     * 获取消费同环比数据
     */
    @ApiOperation(value = "获取消费同环比数据", notes = "获取当前用户的消费同环比数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "timeRange", value = "时间范围", required = false, dataTypeClass = String.class, 
            paramType = "query", allowableValues = "week,month,year", defaultValue = "month"),
        @ApiImplicitParam(name = "compareType", value = "对比类型", required = false, dataTypeClass = String.class, 
            paramType = "query", allowableValues = "mom,yoy", defaultValue = "mom")
    })
    @GetMapping("/compare")
    public Result<ConsumptionCompareVO> getConsumptionCompare(
            @RequestParam(defaultValue = "month") String timeRange,
            @RequestParam(defaultValue = "mom") String compareType) {
        Integer userId = UserUtils.getCurrentUserId();
        ConsumptionCompareVO compare = consumptionService.getConsumptionCompare(userId, timeRange, compareType);
        return Result.success(compare);
    }
    
    /**
     * 获取消费排名
     */
    @ApiOperation(value = "获取消费排名", notes = "获取当前用户在平台用户中的消费排名")
    @ApiImplicitParam(name = "timeRange", value = "时间范围", required = false, dataTypeClass = String.class, 
        paramType = "query", allowableValues = "month,year,all", defaultValue = "all")
    @GetMapping("/rank")
    public Result<Map<String, Object>> getConsumptionRank(@RequestParam(defaultValue = "all") String timeRange) {
        Integer userId = UserUtils.getCurrentUserId();
        Map<String, Object> rank = consumptionService.getConsumptionRank(userId, timeRange);
        return Result.success(rank);
    }
    
    /**
     * 获取平均消费数据
     */
    @ApiOperation(value = "获取平均消费数据", notes = "获取当前用户的平均消费金额和次数")
    @ApiImplicitParam(name = "timeRange", value = "时间范围", required = false, dataTypeClass = String.class, 
        paramType = "query", allowableValues = "month,year,all", defaultValue = "all")
    @GetMapping("/average")
    public Result<Map<String, Object>> getAverageConsumption(@RequestParam(defaultValue = "all") String timeRange) {
        Integer userId = UserUtils.getCurrentUserId();
        Map<String, Object> average = consumptionService.getAverageConsumption(userId, timeRange);
        return Result.success(average);
    }
    
    /**
     * 获取消费明细记录（分页）
     */
    @ApiOperation(value = "获取消费明细记录", notes = "获取当前用户的消费明细记录，支持分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "startTime", value = "开始时间(yyyy-MM-dd)", required = true, paramType = "query", dataTypeClass = String.class),
        @ApiImplicitParam(name = "endTime", value = "结束时间(yyyy-MM-dd)", required = true, paramType = "query", dataTypeClass = String.class),
        @ApiImplicitParam(name = "page", value = "页码", required = false, paramType = "query", dataTypeClass = Integer.class, defaultValue = "1"),
        @ApiImplicitParam(name = "size", value = "每页大小", required = false, paramType = "query", dataTypeClass = Integer.class, defaultValue = "10")
    })
    @GetMapping("/details")
    public Result<Map<String, Object>> getConsumptionDetails(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Integer userId = UserUtils.getCurrentUserId();
        Map<String, Object> details = consumptionService.getConsumptionDetails(userId, startTime, endTime, page, size);
        return Result.success(details);
    }
    
    /**
     * 导出消费明细
     */
    @ApiOperation(value = "导出消费明细", notes = "导出指定时间段内的消费明细，支持Excel和PDF格式")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "startTime", value = "开始时间(yyyy-MM-dd)", required = true, paramType = "query", dataTypeClass = String.class),
        @ApiImplicitParam(name = "endTime", value = "结束时间(yyyy-MM-dd)", required = true, paramType = "query", dataTypeClass = String.class),
        @ApiImplicitParam(name = "format", value = "导出格式", required = true, paramType = "query", dataTypeClass = String.class, 
            allowableValues = "excel,pdf", defaultValue = "excel")
    })
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportConsumptionDetails(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam String format) {
        try {
            Integer userId = UserUtils.getCurrentUserId();
            byte[] data = consumptionService.exportConsumptionDetails(userId, startTime, endTime, format);
            
            if (data == null || data.length == 0) {
                log.error("导出数据为空，格式: {}", format);
                return ResponseEntity.noContent().build();
            }
            
            HttpHeaders headers = new HttpHeaders();
            String filename;
            
            if ("excel".equalsIgnoreCase(format)) {
                headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                filename = "consumption_details_" + startTime + "_" + endTime + ".xlsx";
            } else if ("pdf".equalsIgnoreCase(format)) {
                headers.setContentType(MediaType.APPLICATION_PDF);
                filename = "consumption_details_" + startTime + "_" + endTime + ".pdf";
            } else {
                return ResponseEntity.badRequest().body("不支持的导出格式".getBytes());
            }
            
            // 编码文件名，确保中文支持
            try {
                filename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
            } catch (UnsupportedEncodingException e) {
                // 如果编码失败，使用默认文件名
                filename = "consumption_details.pdf";
            }
            
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            headers.setContentLength(data.length);
            
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("导出消费明细失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_PLAIN)
                .body(("导出失败: " + e.getMessage()).getBytes());
        }
    }
} 