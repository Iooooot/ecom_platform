package com.db.ecom_platform.controller;

import com.db.ecom_platform.entity.ConsumptionStat;
import com.db.ecom_platform.entity.vo.ConsumptionCompareVO;
import com.db.ecom_platform.service.ConsumptionService;
import com.db.ecom_platform.utils.Result;
import com.db.ecom_platform.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "消费统计")
@RestController
@RequestMapping("/api/consumption")
public class ConsumptionController {
    
    @Autowired
    private ConsumptionService consumptionService;
    
    /**
     * 获取用户消费统计
     */
    @ApiOperation(value = "获取消费统计", notes = "获取当前用户的消费统计数据，支持不同时间范围")
    @ApiImplicitParam(name = "timeRange", value = "时间范围", required = false, dataTypeClass = String.class, 
        paramType = "query", allowableValues = "week,month,year,all", defaultValue = "month")
    @GetMapping("/stats")
    public Result<ConsumptionStat> getConsumptionStats(@RequestParam(defaultValue = "month") String timeRange) {
        Integer userId = UserUtils.getCurrentUserId();
        ConsumptionStat stats = consumptionService.getConsumptionStats(userId, timeRange);
        return Result.success(stats);
    }
    
    /**
     * 获取消费趋势
     */
    @ApiOperation(value = "获取消费趋势", notes = "获取当前用户的消费趋势数据，支持不同时间范围")
    @ApiImplicitParam(name = "timeRange", value = "时间范围", required = false, dataTypeClass = String.class, 
        paramType = "query", allowableValues = "week,month,year", defaultValue = "month")
    @GetMapping("/trend")
    public Result<Map<String, Object>> getConsumptionTrend(@RequestParam(defaultValue = "month") String timeRange) {
        Integer userId = UserUtils.getCurrentUserId();
        Map<String, Object> trend = consumptionService.getConsumptionTrend(userId, timeRange);
        return Result.success(trend);
    }
    
    /**
     * 获取消费品类分布
     */
    @ApiOperation(value = "获取消费品类分布", notes = "获取当前用户的消费品类分布数据")
    @ApiImplicitParam(name = "timeRange", value = "时间范围", required = false, dataTypeClass = String.class, 
        paramType = "query", allowableValues = "week,month,year,all", defaultValue = "all")
    @GetMapping("/category-distribution")
    public Result<List<Map<String, Object>>> getCategoryDistribution(@RequestParam(defaultValue = "all") String timeRange) {
        Integer userId = UserUtils.getCurrentUserId();
        List<Map<String, Object>> distribution = consumptionService.getCategoryDistribution(userId, timeRange);
        return Result.success(distribution);
    }
    
    /**
     * 获取同比/环比消费对比
     */
    @ApiOperation(value = "获取消费同环比对比", notes = "获取当前用户的消费同比或环比数据")
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
     * 获取用户消费排名
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
     * 导出消费明细（Excel/PDF）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param format 格式（excel/pdf）
     */
    @ApiOperation(value = "导出消费明细", notes = "导出指定时间段内的消费明细，支持Excel和PDF格式")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "startTime", value = "开始时间(yyyy-MM-dd)", required = true, paramType = "query", dataTypeClass = String.class),
        @ApiImplicitParam(name = "endTime", value = "结束时间(yyyy-MM-dd)", required = true, paramType = "query", dataTypeClass = String.class),
        @ApiImplicitParam(name = "format", value = "导出格式", required = true, paramType = "query", dataTypeClass = String.class, 
            allowableValues = "excel,pdf", example = "excel")
    })
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportConsumptionDetails(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam String format) {
        Integer userId = UserUtils.getCurrentUserId();
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
} 