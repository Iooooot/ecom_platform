package com.db.ecom_platform.service.impl;

import com.db.ecom_platform.entity.Order;
import com.db.ecom_platform.entity.OrderItem;
import com.db.ecom_platform.entity.Product;
import com.db.ecom_platform.entity.User;
import com.db.ecom_platform.mapper.OrderItemMapper;
import com.db.ecom_platform.mapper.OrderMapper;
import com.db.ecom_platform.mapper.ProductMapper;
import com.db.ecom_platform.mapper.UserMapper;
import com.db.ecom_platform.service.AdminStatisticsService;
import com.db.ecom_platform.utils.DateUtils;
import com.db.ecom_platform.utils.ExcelUtils;
import com.db.ecom_platform.utils.PdfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理员数据统计服务实现类
 */
@Service
public class AdminStatisticsServiceImpl implements AdminStatisticsService {

    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ExcelUtils excelUtils;
    
    @Autowired
    private PdfUtils pdfUtils;

    /**
     * 获取实时销售统计数据
     */
    @Override
    @Cacheable(value = "statistics", key = "'realtime'", condition = "#result != null", unless = "#result == null")
    public Map<String, Object> getRealTimeStatistics() {
        Map<String, Object> result = new HashMap<>();
        LocalDateTime today = LocalDate.now().atStartOfDay();
        
        // 今日销售额
        BigDecimal todaySales = orderMapper.getSumAmountByTimeRange(today, LocalDateTime.now());
        result.put("todaySales", todaySales != null ? todaySales : BigDecimal.ZERO);
        
        // 今日订单量
        Integer todayOrders = orderMapper.getCountByTimeRange(today, LocalDateTime.now());
        result.put("todayOrders", todayOrders != null ? todayOrders : 0);
        
        // 昨日销售额
        LocalDateTime yesterday = today.minusDays(1);
        BigDecimal yesterdaySales = orderMapper.getSumAmountByTimeRange(yesterday, today);
        result.put("yesterdaySales", yesterdaySales != null ? yesterdaySales : BigDecimal.ZERO);
        
        // 昨日订单量
        Integer yesterdayOrders = orderMapper.getCountByTimeRange(yesterday, today);
        result.put("yesterdayOrders", yesterdayOrders != null ? yesterdayOrders : 0);
        
        // 本周销售额
        LocalDateTime weekStart = DateUtils.getWeekStart(LocalDate.now()).atStartOfDay();
        BigDecimal weekSales = orderMapper.getSumAmountByTimeRange(weekStart, LocalDateTime.now());
        result.put("weekSales", weekSales != null ? weekSales : BigDecimal.ZERO);
        
        // 本月销售额
        LocalDateTime monthStart = DateUtils.getMonthStart(LocalDate.now()).atStartOfDay();
        BigDecimal monthSales = orderMapper.getSumAmountByTimeRange(monthStart, LocalDateTime.now());
        result.put("monthSales", monthSales != null ? monthSales : BigDecimal.ZERO);
        
        // 销售额同比增长率（与上月同期相比）
        LocalDateTime lastMonthSameDay = today.minusMonths(1);
        LocalDateTime lastMonthStart = DateUtils.getMonthStart(lastMonthSameDay.toLocalDate()).atStartOfDay();
        
        BigDecimal lastMonthSameTimeSales = orderMapper.getSumAmountByTimeRange(
                lastMonthStart, 
                lastMonthSameDay);
        
        BigDecimal currentMonthSameTimeSales = orderMapper.getSumAmountByTimeRange(
                monthStart, 
                today);
        
        if (lastMonthSameTimeSales != null && lastMonthSameTimeSales.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal growthRate = currentMonthSameTimeSales
                    .subtract(lastMonthSameTimeSales)
                    .divide(lastMonthSameTimeSales, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
            result.put("salesGrowthRate", growthRate);
        } else {
            result.put("salesGrowthRate", 0);
        }
        
        return result;
    }

    /**
     * 获取热销商品
     */
    @Override
    @Cacheable(value = "statistics", key = "'hotProducts_' + #limit", condition = "#result != null", unless = "#result == null")
    public List<Map<String, Object>> getHotProducts(int limit) {
        // 获取最近30天的热销商品
        LocalDateTime startTime = LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime endTime = LocalDateTime.now();
        
        List<Map<String, Object>> hotProducts = orderItemMapper.getHotProductsInTimeRange(startTime, endTime, limit);
        
        // 补充商品详细信息
        for (Map<String, Object> product : hotProducts) {
            String productId = (String) product.get("productId");
            Product productInfo = productMapper.selectById(productId);
            if (productInfo != null) {
                product.put("productName", productInfo.getName());
                product.put("productImage", productInfo.getImage());
                product.put("productPrice", productInfo.getPrice());
                product.put("productCategory", productInfo.getCategoryId());
            }
        }
        
        return hotProducts;
    }

    /**
     * 按地区分析消费数据
     */
    @Override
    @Cacheable(value = "statistics", key = "'regionAnalysis_' + #startDate + '_' + #endDate", condition = "#result != null", unless = "#result == null")
    public Map<String, Object> getRegionAnalysis(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取地区销售数据
        List<Map<String, Object>> regionSales = orderMapper.getRegionSalesInTimeRange(
                startDate.atStartOfDay(), 
                endDate.plusDays(1).atStartOfDay());
        
        // 确保regionSales不为null
        if (regionSales == null) {
            regionSales = new ArrayList<>();
        }
        result.put("regionSales", regionSales);
        
        // 获取地区订单数据
        List<Map<String, Object>> regionOrders = orderMapper.getRegionOrdersInTimeRange(
                startDate.atStartOfDay(), 
                endDate.plusDays(1).atStartOfDay());
        
        // 确保regionOrders不为null
        if (regionOrders == null) {
            regionOrders = new ArrayList<>();
        }
        result.put("regionOrders", regionOrders);
        
        // 获取地区客单价数据
        Map<String, BigDecimal> regionAvgOrderAmount = new HashMap<>();
        
        for (Map<String, Object> region : regionSales) {
            String regionName = (String) region.get("region");
            if (regionName == null) continue; // 跳过region为null的情况
            
            BigDecimal sales = (BigDecimal) region.get("amount");
            if (sales == null) sales = BigDecimal.ZERO;
            
            // 找到对应地区的订单数
            Integer orders = 0;
            if (regionOrders != null && !regionOrders.isEmpty()) {
                Optional<Map<String, Object>> matchingRegion = regionOrders.stream()
                        .filter(r -> regionName.equals(r.get("region")))
                        .findFirst();
                
                if (matchingRegion.isPresent()) {
                    Object countObj = matchingRegion.get().get("count");
                    if (countObj instanceof Integer) {
                        orders = (Integer) countObj;
                    } else if (countObj instanceof Long) {
                        orders = ((Long) countObj).intValue();
                    } else if (countObj != null) {
                        orders = Integer.parseInt(countObj.toString());
                    }
                }
            }
            
            if (orders > 0) {
                regionAvgOrderAmount.put(regionName, sales.divide(new BigDecimal(orders), 2, BigDecimal.ROUND_HALF_UP));
            } else {
                regionAvgOrderAmount.put(regionName, BigDecimal.ZERO);
            }
        }
        
        result.put("regionAvgOrderAmount", regionAvgOrderAmount);
        
        return result;
    }

    /**
     * 按用户年龄段分析消费偏好
     */
    @Override
    @Cacheable(value = "statistics", key = "'ageGroupAnalysis_' + #startDate + '_' + #endDate", condition = "#result != null", unless = "#result == null")
    public Map<String, Object> getAgeGroupAnalysis(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        
        // 定义年龄段
        Map<String, int[]> ageGroups = new LinkedHashMap<>();
        ageGroups.put("18岁以下", new int[]{0, 17});
        ageGroups.put("18-24岁", new int[]{18, 24});
        ageGroups.put("25-34岁", new int[]{25, 34});
        ageGroups.put("35-44岁", new int[]{35, 44});
        ageGroups.put("45-54岁", new int[]{45, 54});
        ageGroups.put("55岁以上", new int[]{55, 200});
        
        // 获取时间范围内的所有订单
        List<Order> orders = orderMapper.getOrdersInTimeRange(
                startDate.atStartOfDay(), 
                endDate.plusDays(1).atStartOfDay());
        
        // 按年龄段分组统计
        Map<String, List<Order>> ordersByAgeGroup = new HashMap<>();
        for (Order order : orders) {
            User user = userMapper.selectById(order.getUserId());
            if (user != null && user.getAge() != null) {
                int age = user.getAge();

                // 确定年龄段
                String ageGroup = ageGroups.entrySet().stream()
                        .filter(entry -> age >= entry.getValue()[0] && age <= entry.getValue()[1])
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse("未知");

                ordersByAgeGroup.computeIfAbsent(ageGroup, k -> new ArrayList<>()).add(order);
            }
        }
        
        // 计算各年龄段的消费总额
        Map<String, BigDecimal> salesByAgeGroup = new LinkedHashMap<>();
        for (Map.Entry<String, List<Order>> entry : ordersByAgeGroup.entrySet()) {
            BigDecimal totalAmount = entry.getValue().stream()
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            salesByAgeGroup.put(entry.getKey(), totalAmount);
        }
        result.put("salesByAgeGroup", salesByAgeGroup);
        
        // 计算各年龄段的订单数量
        Map<String, Integer> orderCountByAgeGroup = new LinkedHashMap<>();
        for (Map.Entry<String, List<Order>> entry : ordersByAgeGroup.entrySet()) {
            orderCountByAgeGroup.put(entry.getKey(), entry.getValue().size());
        }
        result.put("orderCountByAgeGroup", orderCountByAgeGroup);
        
        // 计算各年龄段的品类偏好
        Map<String, List<Map<String, Object>>> categoryPreferenceByAgeGroup = new LinkedHashMap<>();
        
        for (Map.Entry<String, List<Order>> entry : ordersByAgeGroup.entrySet()) {
            String ageGroup = entry.getKey();
            List<Order> ageGroupOrders = entry.getValue();
            
            // 获取该年龄段的所有订单项
            List<String> orderIds = ageGroupOrders.stream()
                    .map(Order::getOrderId)
                    .collect(Collectors.toList());
            
            if (!orderIds.isEmpty()) {
                List<OrderItem> orderItems = orderItemMapper.getOrderItemsByOrderIds(orderIds);
                
                // 按商品分类统计销量
                Map<String, Integer> categorySales = new HashMap<>();
                Map<String, BigDecimal> categoryAmount = new HashMap<>();
                
                for (OrderItem item : orderItems) {
                    Product product = productMapper.selectById(item.getProductId());
                    if (product != null) {
                        String categoryId = product.getCategoryId();
                        categorySales.put(categoryId, categorySales.getOrDefault(categoryId, 0) + item.getQuantity());
                        
                        BigDecimal itemAmount = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
                        categoryAmount.put(categoryId, categoryAmount.getOrDefault(categoryId, BigDecimal.ZERO).add(itemAmount));
                    }
                }
                
                // 转换为列表并排序
                List<Map<String, Object>> categoryPreference = new ArrayList<>();
                for (Map.Entry<String, Integer> catEntry : categorySales.entrySet()) {
                    Map<String, Object> preference = new HashMap<>();
                    preference.put("categoryId", catEntry.getKey());
                    preference.put("categoryName", productMapper.getCategoryName(catEntry.getKey()));
                    preference.put("salesCount", catEntry.getValue());
                    preference.put("salesAmount", categoryAmount.get(catEntry.getKey()));
                    categoryPreference.add(preference);
                }
                
                // 按销量排序
                categoryPreference.sort((a, b) -> ((Integer) b.get("salesCount")).compareTo((Integer) a.get("salesCount")));
                
                // 取前5个
                categoryPreferenceByAgeGroup.put(ageGroup, categoryPreference.stream().limit(5).collect(Collectors.toList()));
            } else {
                categoryPreferenceByAgeGroup.put(ageGroup, new ArrayList<>());
            }
        }
        
        result.put("categoryPreferenceByAgeGroup", categoryPreferenceByAgeGroup);
        
        return result;
    }

    /**
     * 获取销售数据看板
     */
    @Override
    @Cacheable(value = "statistics", key = "'dashboard_' + #period", condition = "#result != null", unless = "#result == null")
    public Map<String, Object> getDashboardData(String period) {
        Map<String, Object> result = new HashMap<>();
        
        LocalDateTime startTime;
        LocalDateTime endTime = LocalDateTime.now();
        String timeUnit;
        
        // 根据周期确定起始时间和时间单位
        switch (period.toLowerCase()) {
            case "day":
                startTime = LocalDate.now().minusDays(1).atStartOfDay();
                timeUnit = "HOUR";
                break;
            case "week":
                startTime = LocalDate.now().minusWeeks(1).atStartOfDay();
                timeUnit = "DAY";
                break;
            case "month":
                startTime = LocalDate.now().minusMonths(1).atStartOfDay();
                timeUnit = "DAY";
                break;
            case "year":
                startTime = LocalDate.now().minusYears(1).atStartOfDay();
                timeUnit = "MONTH";
                break;
            default:
                startTime = LocalDate.now().minusDays(7).atStartOfDay();
                timeUnit = "DAY";
        }
        
        // 获取今日销售额 (首页需要)
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        BigDecimal todaySales = orderMapper.getSumAmountByTimeRange(todayStart, endTime);
        result.put("todaySales", todaySales != null ? todaySales : BigDecimal.ZERO);
        
        // 获取今日订单数 (首页需要)
        Integer todayOrders = orderMapper.getCountByTimeRange(todayStart, endTime);
        result.put("todayOrders", todayOrders != null ? todayOrders : 0);

        // 总销售额
        BigDecimal totalSales = orderMapper.getSumAmountByTimeRange(startTime, endTime);
        result.put("totalSales", totalSales != null ? totalSales : BigDecimal.ZERO);

        // 总订单数
        Integer totalOrders = orderMapper.getCountByTimeRange(startTime, endTime);
        result.put("totalOrders", totalOrders != null ? totalOrders : 0);
        
        // 获取用户总数 (首页需要)
        Long userCount = userMapper.selectCount(null);
        result.put("totalUsers", userCount != null ? userCount.intValue() : 0);
        
        // 获取商品总数 (首页需要)
        Long productCount = productMapper.selectCount(null);
        result.put("totalProducts", productCount != null ? productCount.intValue() : 0);
        
        // 获取销售趋势数据
        List<Map<String, Object>> salesTrend = orderMapper.getSalesTrendInTimeRange(startTime, endTime, timeUnit);
        result.put("salesTrend", salesTrend);
        
        // 获取订单趋势数据
        List<Map<String, Object>> orderTrend = orderMapper.getOrderTrendInTimeRange(startTime, endTime, timeUnit);
        result.put("orderTrend", orderTrend);
        
        // 获取热销商品
        List<Map<String, Object>> hotProducts = getHotProducts(10);
        result.put("hotProducts", hotProducts);
        
        // 获取销售额分布（按商品分类）
        List<Map<String, Object>> salesByCategory = orderItemMapper.getSalesByCategoryInTimeRange(startTime, endTime);
        result.put("salesByCategory", salesByCategory);
        
        // 获取订单状态分布
        List<Map<String, Object>> orderStatusList = orderMapper.getOrderStatusDistribution(startTime, endTime);
        Map<String, Integer> orderStatusDistribution = new HashMap<>();
        for (Map<String, Object> status : orderStatusList) {
            orderStatusDistribution.put(status.get("status").toString(), ((Number) status.get("count")).intValue());
        }
        result.put("orderStatusDistribution", orderStatusDistribution);
        
        // 获取支付方式分布
        List<Map<String, Object>> paymentMethodList = orderMapper.getPaymentMethodDistribution(startTime, endTime);
        Map<String, Integer> paymentMethodDistribution = new HashMap<>();
        for (Map<String, Object> method : paymentMethodList) {
            String methodKey = method.get("paymentMethod") != null ? method.get("paymentMethod").toString() : "unknown";
            paymentMethodDistribution.put(methodKey, ((Number) method.get("count")).intValue());
        }
        result.put("paymentMethodDistribution", paymentMethodDistribution);
        
        // 获取新用户数据
        Integer newUsers = userMapper.getNewUsersCountInTimeRange(startTime, endTime);
        result.put("newUsers", newUsers);
        
        // 获取活跃用户数据
        Integer activeUsers = orderMapper.getActiveUsersCountInTimeRange(startTime, endTime);
        result.put("activeUsers", activeUsers);
        
        return result;
    }

    /**
     * 生成销售报表
     */
    @Override
    public byte[] generateReport(String reportType, String format, LocalDate date) {
        // 根据报表类型确定时间范围
        LocalDateTime startTime;
        LocalDateTime endTime;
        String reportTitle;
        
        switch (reportType.toLowerCase()) {
            case "daily":
                startTime = date.atStartOfDay();
                endTime = date.plusDays(1).atStartOfDay();
                reportTitle = "每日销售报表 - " + date;
                break;
            case "weekly":
                startTime = DateUtils.getWeekStart(date).atStartOfDay();
                endTime = DateUtils.getWeekEnd(date).plusDays(1).atStartOfDay();
                reportTitle = "每周销售报表 - " + startTime.toLocalDate() + " 至 " + endTime.toLocalDate().minusDays(1);
                break;
            case "monthly":
                startTime = DateUtils.getMonthStart(date).atStartOfDay();
                endTime = DateUtils.getMonthEnd(date).plusDays(1).atStartOfDay();
                reportTitle = "每月销售报表 - " + date.getYear() + "年" + date.getMonthValue() + "月";
                break;
            default:
                throw new IllegalArgumentException("不支持的报表类型: " + reportType);
        }
        
        // 获取报表数据
        Map<String, Object> reportData = new HashMap<>();
        
        // 销售总额
        BigDecimal totalSales = orderMapper.getSumAmountByTimeRange(startTime, endTime);
        reportData.put("totalSales", totalSales != null ? totalSales : BigDecimal.ZERO);
        
        // 订单总数
        Integer totalOrders = orderMapper.getCountByTimeRange(startTime, endTime);
        reportData.put("totalOrders", totalOrders != null ? totalOrders : 0);
        
        // 平均订单金额
        BigDecimal avgOrderAmount = BigDecimal.ZERO;
        if (totalOrders != null && totalOrders > 0 && totalSales != null) {
            avgOrderAmount = totalSales.divide(new BigDecimal(totalOrders), 2, BigDecimal.ROUND_HALF_UP);
        }
        reportData.put("avgOrderAmount", avgOrderAmount);
        
        // 热销商品
        List<Map<String, Object>> hotProducts = orderItemMapper.getHotProductsInTimeRange(startTime, endTime, 10);
        reportData.put("hotProducts", hotProducts);
        
        // 销售额分布（按商品分类）
        List<Map<String, Object>> salesByCategory = orderItemMapper.getSalesByCategoryInTimeRange(startTime, endTime);
        reportData.put("salesByCategory", salesByCategory);
        
        // 销售趋势数据
        String timeUnit = reportType.equalsIgnoreCase("daily") ? "HOUR" : "DAY";
        List<Map<String, Object>> salesTrend = orderMapper.getSalesTrendInTimeRange(startTime, endTime, timeUnit);
        reportData.put("salesTrend", salesTrend);
        
        // 地区销售数据
        List<Map<String, Object>> regionSales = orderMapper.getRegionSalesInTimeRange(startTime, endTime);
        reportData.put("regionSales", regionSales);
        
        // 生成报表
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try {
            if ("excel".equalsIgnoreCase(format)) {
                excelUtils.generateSalesReport(reportData, reportTitle, outputStream);
            } else {
                pdfUtils.generateSalesReport(reportData, reportTitle, outputStream);
            }
            return outputStream.toByteArray();
        } catch (Exception e) {
            // 记录具体错误信息
            String errorMessage = "生成报表失败: " + e.getMessage();
            System.err.println(errorMessage);
            e.printStackTrace();
            throw new RuntimeException(errorMessage, e);
        }
    }
} 