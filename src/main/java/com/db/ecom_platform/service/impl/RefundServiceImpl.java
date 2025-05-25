package com.db.ecom_platform.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.db.ecom_platform.entity.Order;
import com.db.ecom_platform.entity.Refund;
import com.db.ecom_platform.entity.dto.AdminOrderQueryDTO;
import com.db.ecom_platform.entity.dto.RefundApplyDTO;
import com.db.ecom_platform.entity.dto.RefundProcessDTO;
import com.db.ecom_platform.mapper.OrderMapper;
import com.db.ecom_platform.mapper.RefundMapper;
import com.db.ecom_platform.service.RefundService;
import com.db.ecom_platform.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 退款服务实现类
 */
@Service
public class RefundServiceImpl extends ServiceImpl<RefundMapper, Refund> implements RefundService {
    
    private static final Logger log = LoggerFactory.getLogger(RefundServiceImpl.class);
    
    @Autowired
    private RefundMapper refundMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Override
    @Transactional
    public Result<?> applyRefund(Integer userId, RefundApplyDTO refundApplyDTO) {
        // 参数校验
        if (userId == null || refundApplyDTO == null || refundApplyDTO.getOrderId() == null) {
            return Result.error("参数错误");
        }
        
        // 查询订单
        LambdaQueryWrapper<Order> orderQuery = new LambdaQueryWrapper<>();
        orderQuery.eq(Order::getUserId, userId).eq(Order::getOrderId, refundApplyDTO.getOrderId());
        Order order = orderMapper.selectOne(orderQuery);
        
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        // 检查订单状态（只有已支付、已发货、已完成的订单可以申请退款）
        if (order.getStatus() < 1 || order.getStatus() > 3) {
            return Result.error("订单状态不允许申请退款");
        }
        
        // 检查是否已有退款申请
        if (hasRefundApplication(refundApplyDTO.getOrderId())) {
            return Result.error("该订单已有退款申请，请勿重复提交");
        }
        
        // 检查退款金额
        if (refundApplyDTO.getRefundAmount() == null || 
            refundApplyDTO.getRefundAmount().compareTo(order.getPaymentAmount()) > 0) {
            return Result.error("退款金额不能大于订单支付金额");
        }
        
        // 创建退款申请
        Refund refund = new Refund();
        refund.setRefundId("REF-" + UUID.randomUUID().toString().substring(0, 8));
        refund.setOrderId(refundApplyDTO.getOrderId());
        refund.setUserId(userId);
        refund.setReason(refundApplyDTO.getReason());
        refund.setDescription(refundApplyDTO.getDescription());
        refund.setRefundType(refundApplyDTO.getRefundType());
        refund.setRefundAmount(refundApplyDTO.getRefundAmount());
        refund.setContactPhone(refundApplyDTO.getContactPhone());
        
        // 处理图片
        if (refundApplyDTO.getImageUrls() != null && refundApplyDTO.getImageUrls().length > 0) {
            refund.setImages(JSON.toJSONString(refundApplyDTO.getImageUrls()));
        }
        
        refund.setStatus(0); // 待处理
        
        Date now = new Date();
        refund.setCreateTime(now);
        refund.setUpdateTime(now);
        
        // 保存退款申请
        int rows = refundMapper.insert(refund);
        
        if (rows > 0) {
            return Result.success("退款申请提交成功");
        } else {
            return Result.error("退款申请提交失败");
        }
    }
    
    @Override
    public Result<List<Refund>> getUserRefunds(Integer userId) {
        if (userId == null) {
            return Result.error("参数错误");
        }
        
        List<Refund> refunds = refundMapper.selectByUserId(userId);
        return Result.success(refunds);
    }
    
    @Override
    public Result<Refund> getRefundDetail(Integer userId, String refundId) {
        if (userId == null || refundId == null) {
            return Result.error("参数错误");
        }
        
        LambdaQueryWrapper<Refund> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Refund::getUserId, userId).eq(Refund::getRefundId, refundId);
        
        Refund refund = refundMapper.selectOne(queryWrapper);
        
        if (refund == null) {
            return Result.error("退款申请不存在");
        }
        
        return Result.success(refund);
    }
    
    @Override
    @Transactional
    public Result<?> cancelRefund(Integer userId, String refundId) {
        if (userId == null || refundId == null) {
            return Result.error("参数错误");
        }
        
        LambdaQueryWrapper<Refund> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Refund::getUserId, userId).eq(Refund::getRefundId, refundId);
        
        Refund refund = refundMapper.selectOne(queryWrapper);
        
        if (refund == null) {
            return Result.error("退款申请不存在");
        }
        
        // 只有待处理状态可以取消
        if (refund.getStatus() != 0) {
            return Result.error("该申请已处理，无法取消");
        }
        
        // 删除退款申请
        int rows = refundMapper.deleteById(refundId);
        
        if (rows > 0) {
            return Result.success("退款申请已取消");
        } else {
            return Result.error("取消退款申请失败");
        }
    }
    
    @Override
    public IPage<Refund> getRefundsForAdmin(AdminOrderQueryDTO queryDTO) {
        // 参数校验
        if (queryDTO == null) {
            queryDTO = new AdminOrderQueryDTO();
        }
        
        // 查询条件
        Page<Refund> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        Date startTime = null;
        Date endTime = null;
        
        try {
            if (queryDTO.getStartTime() != null && !queryDTO.getStartTime().isEmpty()) {
                startTime = dateFormat.parse(queryDTO.getStartTime() + " 00:00:00");
            }
            if (queryDTO.getEndTime() != null && !queryDTO.getEndTime().isEmpty()) {
                endTime = dateFormat.parse(queryDTO.getEndTime() + " 23:59:59");
            }
        } catch (Exception e) {
            log.error("日期格式转换异常", e);
        }
        
        // 查询退款申请
        return refundMapper.selectRefundsForAdmin(
                page, queryDTO.getUserId(), queryDTO.getStatus(), 
                queryDTO.getKeyword(), startTime, endTime);
    }
    
    @Override
    @Transactional
    public Result<?> processRefund(RefundProcessDTO refundProcessDTO) {
        // 参数校验
        if (refundProcessDTO == null || refundProcessDTO.getRefundId() == null || 
            refundProcessDTO.getDecision() == null) {
            return Result.error("参数错误");
        }
        
        // 查询退款申请
        Refund refund = refundMapper.selectById(refundProcessDTO.getRefundId());
        
        if (refund == null) {
            return Result.error("退款申请不存在");
        }
        
        // 检查是否已处理
        if (refund.getStatus() != 0) {
            return Result.error("该申请已处理");
        }
        
        Date now = new Date();
        
        if ("APPROVED".equals(refundProcessDTO.getDecision())) {
            // 同意退款
            refund.setStatus(1); // 同意退款
            refund.setActualAmount(refundProcessDTO.getRefundAmount());
            refund.setAdminRemarks(refundProcessDTO.getRemarks());
            refund.setUpdateTime(now);
            refund.setProcessTime(now);
            
            // 更新订单状态为退款中（如果需要）
            orderMapper.updateOrderStatus(refund.getOrderId(), null, 5, now);
        } else if ("REJECTED".equals(refundProcessDTO.getDecision())) {
            // 拒绝退款
            if (refundProcessDTO.getRejectReason() == null || refundProcessDTO.getRejectReason().isEmpty()) {
                return Result.error("拒绝原因不能为空");
            }
            
            refund.setStatus(2); // 拒绝退款
            refund.setRejectReason(refundProcessDTO.getRejectReason());
            refund.setAdminRemarks(refundProcessDTO.getRemarks());
            refund.setUpdateTime(now);
            refund.setProcessTime(now);
        } else {
            return Result.error("无效的处理决定");
        }
        
        // 更新退款申请
        int rows = refundMapper.updateById(refund);
        
        if (rows > 0) {
            return Result.success("处理成功");
        } else {
            return Result.error("处理失败");
        }
    }
    
    @Override
    public boolean hasRefundApplication(String orderId) {
        if (orderId == null) {
            return false;
        }
        
        Refund refund = refundMapper.selectByOrderId(orderId);
        return refund != null;
    }
} 