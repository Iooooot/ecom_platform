package com.db.ecom_platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.db.ecom_platform.entity.Order;
import com.db.ecom_platform.entity.Refund;
import com.db.ecom_platform.entity.dto.AdminOrderQueryDTO;
import com.db.ecom_platform.entity.dto.RefundProcessDTO;
import com.db.ecom_platform.service.OrderService;
import com.db.ecom_platform.service.RefundService;
import com.db.ecom_platform.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员订单管理控制器
 */
@Api(tags = "管理员-订单管理")
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private RefundService refundService;
    
    /**
     * 获取订单列表
     * @param queryDTO 查询条件
     * @return 订单列表
     */
    @ApiOperation(value = "获取订单列表", notes = "管理员查询订单列表，支持多条件筛选")
    @PostMapping("/list")
    public Result<IPage<Order>> getOrderList(@RequestBody AdminOrderQueryDTO queryDTO) {
        IPage<Order> orderPage = orderService.getOrdersForAdmin(queryDTO);
        return Result.success(orderPage);
    }
    
    /**
     * 获取订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    @ApiOperation(value = "获取订单详情", notes = "管理员查询订单详情")
    @ApiImplicitParam(name = "orderId", value = "订单ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/{orderId}")
    public Result<Order> getOrderDetail(@PathVariable String orderId) {
        return orderService.getOrderDetailForAdmin(orderId);
    }
    
    /**
     * 标记订单为已发货
     * @param orderId 订单ID
     * @param trackingNumber 物流单号
     * @param shippingCompany 物流公司
     * @return 操作结果
     */
    @ApiOperation(value = "标记订单为已发货", notes = "管理员标记已支付订单为已发货状态")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "orderId", value = "订单ID", required = true, dataType = "String", paramType = "path"),
        @ApiImplicitParam(name = "trackingNumber", value = "物流单号", required = false, dataType = "String", paramType = "query"),
        @ApiImplicitParam(name = "shippingCompany", value = "物流公司", required = false, dataType = "String", paramType = "query")
    })
    @PostMapping("/{orderId}/ship")
    public Result<?> markOrderAsShipped(
            @PathVariable String orderId,
            @RequestParam(required = false) String trackingNumber,
            @RequestParam(required = false) String shippingCompany) {
        return orderService.markOrderAsShipped(orderId, trackingNumber, shippingCompany);
    }
    
    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param status 状态
     * @param remarks 备注
     * @return 操作结果
     */
    @ApiOperation(value = "更新订单状态", notes = "管理员修改订单状态")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "orderId", value = "订单ID", required = true, dataType = "String", paramType = "path"),
        @ApiImplicitParam(name = "status", value = "订单状态", required = true, dataType = "Integer", paramType = "query"),
        @ApiImplicitParam(name = "remarks", value = "备注", required = false, dataType = "String", paramType = "query")
    })
    @PostMapping("/{orderId}/status")
    public Result<?> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam Integer status,
            @RequestParam(required = false) String remarks) {
        return orderService.updateOrderStatus(orderId, status, remarks);
    }
    
    /**
     * 获取退款申请列表
     * @param queryDTO 查询条件
     * @return 退款申请列表
     */
    @ApiOperation(value = "获取退款申请列表", notes = "管理员查询退款申请列表")
    @PostMapping("/refunds/list")
    public Result<IPage<Refund>> getRefundList(@RequestBody AdminOrderQueryDTO queryDTO) {
        IPage<Refund> refundPage = refundService.getRefundsForAdmin(queryDTO);
        return Result.success(refundPage);
    }
    
    /**
     * 处理退款申请
     * @param refundProcessDTO 处理信息
     * @return 处理结果
     */
    @ApiOperation(value = "处理退款申请", notes = "管理员处理退款申请")
    @PostMapping("/refunds/process")
    public Result<?> processRefund(@RequestBody RefundProcessDTO refundProcessDTO) {
        return refundService.processRefund(refundProcessDTO);
    }
} 