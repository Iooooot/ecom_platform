package com.db.ecom_platform.controller;

import com.db.ecom_platform.entity.Refund;
import com.db.ecom_platform.entity.dto.RefundApplyDTO;
import com.db.ecom_platform.service.RefundService;
import com.db.ecom_platform.utils.Result;
import com.db.ecom_platform.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户退款申请控制器
 */
@Api(tags = "退款申请")
@RestController
@RequestMapping("/api/refunds")
public class RefundController {
    
    @Autowired
    private RefundService refundService;
    
    /**
     * 申请退款
     * @param refundApplyDTO 退款申请信息
     * @return 操作结果
     */
    @ApiOperation(value = "申请退款", notes = "为已支付或已发货的订单申请退款")
    @PostMapping("/apply")
    public Result<?> applyRefund(@RequestBody RefundApplyDTO refundApplyDTO) {
        Integer userId = UserUtils.getCurrentUserId();
        return refundService.applyRefund(userId, refundApplyDTO);
    }
    
    /**
     * 获取用户的退款申请列表
     * @return 退款申请列表
     */
    @ApiOperation(value = "获取退款申请列表", notes = "获取当前用户的所有退款申请")
    @GetMapping("/list")
    public Result<List<Refund>> getUserRefunds() {
        Integer userId = UserUtils.getCurrentUserId();
        return refundService.getUserRefunds(userId);
    }
    
    /**
     * 获取退款申请详情
     * @param refundId 退款申请ID
     * @return 退款申请详情
     */
    @ApiOperation(value = "获取退款申请详情", notes = "获取指定退款申请的详细信息")
    @ApiImplicitParam(name = "refundId", value = "退款申请ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/{refundId}")
    public Result<Refund> getRefundDetail(@PathVariable String refundId) {
        Integer userId = UserUtils.getCurrentUserId();
        return refundService.getRefundDetail(userId, refundId);
    }
    
    /**
     * 取消退款申请
     * @param refundId 退款申请ID
     * @return 操作结果
     */
    @ApiOperation(value = "取消退款申请", notes = "取消指定的待处理退款申请")
    @ApiImplicitParam(name = "refundId", value = "退款申请ID", required = true, dataType = "String", paramType = "path")
    @PostMapping("/{refundId}/cancel")
    public Result<?> cancelRefund(@PathVariable String refundId) {
        Integer userId = UserUtils.getCurrentUserId();
        return refundService.cancelRefund(userId, refundId);
    }
} 