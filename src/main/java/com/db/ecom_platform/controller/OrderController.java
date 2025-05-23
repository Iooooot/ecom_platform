package com.db.ecom_platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.db.ecom_platform.entity.dto.OrderQueryDTO;
import com.db.ecom_platform.entity.dto.PaymentDTO;
import com.db.ecom_platform.entity.vo.OrderVO;
import com.db.ecom_platform.service.OrderService;
import com.db.ecom_platform.utils.Result;
import com.db.ecom_platform.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@Api(tags = "订单管理")
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 获取当前用户的订单列表
     * @param queryDTO 查询条件
     * @return 订单列表
     */
    @ApiOperation(value = "获取订单列表", notes = "获取当前登录用户的订单列表，支持按状态和时间筛选")
    @PostMapping("/list")
    public Result<IPage<OrderVO>> getUserOrders(@RequestBody OrderQueryDTO queryDTO) {
        Integer userId = UserUtils.getCurrentUserId();
        IPage<OrderVO> orderPage = orderService.getUserOrders(userId, queryDTO);
        return Result.success(orderPage);
    }
    
    /**
     * 获取订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    @ApiOperation(value = "获取订单详情", notes = "获取指定订单的详细信息")
    @ApiImplicitParam(name = "orderId", value = "订单ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/{orderId}")
    public Result<OrderVO> getOrderDetail(@PathVariable String orderId) {
        Integer userId = UserUtils.getCurrentUserId();
        return orderService.getOrderDetail(userId, orderId);
    }
    
    /**
     * 取消订单
     * @param orderId 订单ID
     * @return 操作结果
     */
    @ApiOperation(value = "取消订单", notes = "取消指定的未支付订单")
    @ApiImplicitParam(name = "orderId", value = "订单ID", required = true, dataType = "String", paramType = "path")
    @PostMapping("/{orderId}/cancel")
    public Result<?> cancelOrder(@PathVariable String orderId) {
        Integer userId = UserUtils.getCurrentUserId();
        return orderService.cancelOrder(userId, orderId);
    }
    
    /**
     * 获取各状态订单数量
     * @return 各状态订单数量
     */
    @ApiOperation(value = "获取各状态订单数量", notes = "获取当前登录用户各状态订单的数量")
    @GetMapping("/count")
    public Result<List<Integer>> getOrderStatusCount() {
        Integer userId = UserUtils.getCurrentUserId();
        return orderService.getOrderStatusCount(userId);
    }
    
    /**
     * 支付订单
     * @param paymentDTO 支付信息（订单ID和用户密码）
     * @return 支付结果
     */
    @ApiOperation(value = "支付订单", notes = "通过验证用户密码支付订单")
    @PostMapping("/pay")
    public Result<?> payOrder(@RequestBody PaymentDTO paymentDTO) {
        Integer userId = UserUtils.getCurrentUserId();
        return orderService.payOrder(userId, paymentDTO);
    }
} 