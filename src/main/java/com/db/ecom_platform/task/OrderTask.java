package com.db.ecom_platform.task;

import com.db.ecom_platform.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 订单相关定时任务
 */
@Component
public class OrderTask {
    
    private static final Logger log = LoggerFactory.getLogger(OrderTask.class);
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 处理过期未支付订单
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000)
    public void processExpiredOrders() {
        log.info("开始处理过期未支付订单...");
        int count = orderService.processExpiredOrders();
        log.info("处理过期未支付订单完成，共处理{}个订单", count);
    }
} 