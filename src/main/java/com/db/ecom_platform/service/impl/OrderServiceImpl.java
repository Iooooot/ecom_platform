package com.db.ecom_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.db.ecom_platform.entity.Address;
import com.db.ecom_platform.entity.Order;
import com.db.ecom_platform.entity.OrderItem;
import com.db.ecom_platform.entity.User;
import com.db.ecom_platform.entity.dto.AdminOrderQueryDTO;
import com.db.ecom_platform.entity.dto.OrderQueryDTO;
import com.db.ecom_platform.entity.dto.PaymentDTO;
import com.db.ecom_platform.entity.vo.AddressVO;
import com.db.ecom_platform.entity.vo.OrderVO;
import com.db.ecom_platform.mapper.AddressMapper;
import com.db.ecom_platform.mapper.OrderItemMapper;
import com.db.ecom_platform.mapper.OrderMapper;
import com.db.ecom_platform.mapper.UserMapper;
import com.db.ecom_platform.service.OrderService;
import com.db.ecom_platform.service.RefundService;
import com.db.ecom_platform.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private AddressMapper addressMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RefundService refundService;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public IPage<OrderVO> getUserOrders(Integer userId, OrderQueryDTO queryDTO) {
        // 参数校验
        if (userId == null) {
            return new Page<>();
        }
        
        // 查询条件
        Page<Order> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
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
        
        // 查询订单
        IPage<Order> orderPage = orderMapper.selectOrdersByUserId(
                page, userId, queryDTO.getStatus(), queryDTO.getKeyword(), startTime, endTime);
        
        // 转换为OrderVO
        IPage<OrderVO> resultPage = orderPage.convert(this::convertToOrderVO);
        
        // 查询订单项
        List<OrderVO> records = resultPage.getRecords();
        if (!records.isEmpty()) {
            for (OrderVO orderVO : records) {
                // 查询订单项
                List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderVO.getOrderId());
                orderVO.setOrderItems(orderItems);
            }
        }
        
        return resultPage;
    }
    
    @Override
    public Result<OrderVO> getOrderDetail(Integer userId, String orderId) {
        // 参数校验
        if (userId == null || orderId == null) {
            return Result.error("参数错误");
        }
        
        // 查询订单
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId).eq(Order::getOrderId, orderId);
        Order order = orderMapper.selectOne(queryWrapper);
        
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        // 转换为OrderVO
        OrderVO orderVO = convertToOrderVO(order);
        
        // 查询订单项
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        orderVO.setOrderItems(orderItems);
        
        // 查询收货地址
        if (order.getAddressId() != null) {
            Address address = addressMapper.selectById(order.getAddressId());
            if (address != null) {
                AddressVO addressVO = new AddressVO();
                BeanUtils.copyProperties(address, addressVO);
                orderVO.setAddress(addressVO);
            }
        }
        
        return Result.success(orderVO);
    }
    
    @Override
    @Transactional
    public Result<?> cancelOrder(Integer userId, String orderId) {
        // 参数校验
        if (userId == null || orderId == null) {
            return Result.error("参数错误");
        }
        
        // 查询订单
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId).eq(Order::getOrderId, orderId);
        Order order = orderMapper.selectOne(queryWrapper);
        
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        // 判断是否可以取消
        if (!order.canCancel()) {
            return Result.error("该订单状态不允许取消");
        }
        
        // 取消订单
        Date now = new Date();
        int rows = orderMapper.cancelOrder(orderId, userId, now, now);
        
        if (rows > 0) {
            return Result.success("订单取消成功");
        } else {
            return Result.error("订单取消失败");
        }
    }
    
    @Override
    @Transactional
    public int processExpiredOrders() {
        // 查询过期未支付订单
        Date now = new Date();
        List<Order> expiredOrders = orderMapper.selectExpiredUnpaidOrders(now);
        
        if (expiredOrders.isEmpty()) {
            return 0;
        }
        
        // 批量取消订单
        int count = 0;
        for (Order order : expiredOrders) {
            int rows = orderMapper.updateOrderStatus(order.getOrderId(), 0, 4, now);
            if (rows > 0) {
                count++;
                log.info("订单自动取消成功，订单号：{}", order.getOrderId());
            }
        }
        
        return count;
    }
    
    @Override
    public Result<List<Integer>> getOrderStatusCount(Integer userId) {
        if (userId == null) {
            return Result.error("参数错误");
        }
        
        List<Integer> counts = new ArrayList<>();
        
        // 依次查询各状态的订单数量
        for (int i = 0; i <= 5; i++) {
            int count = orderMapper.countOrdersByStatus(userId, i);
            counts.add(count);
        }
        
        return Result.success(counts);
    }
    
    @Override
    @Transactional
    public Result<?> payOrder(Integer userId, PaymentDTO paymentDTO) {
        // 参数校验
        if (userId == null || paymentDTO == null || 
            paymentDTO.getOrderId() == null || paymentDTO.getPassword() == null) {
            return Result.error("参数错误");
        }
        
        // 查询订单
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId).eq(Order::getOrderId, paymentDTO.getOrderId());
        Order order = orderMapper.selectOne(queryWrapper);
        
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        // 检查订单状态是否可支付
        if (!order.canPay()) {
            return Result.error("该订单不可支付，可能已过期或已支付");
        }
        
        // 验证用户密码
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        if (!passwordEncoder.matches(paymentDTO.getPassword(), user.getPassword())) {
            return Result.error("密码不正确");
        }
        
        // 支付成功，更新订单状态
        Date now = new Date();
        int rows = orderMapper.updateOrderStatus(paymentDTO.getOrderId(), 0, 1, now);
        
        if (rows > 0) {
            // 生成支付交易记录或执行其他需要的操作
            String transactionId = generateTransactionId();
            log.info("订单支付成功，订单号: {}, 交易号: {}", 
                    paymentDTO.getOrderId(), transactionId);
            
            return Result.success("支付成功");
        } else {
            log.error("订单状态更新失败，订单号: {}", paymentDTO.getOrderId());
            return Result.error("支付失败，请重试");
        }
    }
    
    /**
     * 管理员获取订单列表
     */
    @Override
    public IPage<Order> getOrdersForAdmin(AdminOrderQueryDTO queryDTO) {
        // 参数校验
        if (queryDTO == null) {
            queryDTO = new AdminOrderQueryDTO();
        }
        
        // 查询条件
        Page<Order> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
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
        
        // 查询订单
        IPage<Order> orderPage = orderMapper.selectOrdersForAdmin(
                page, queryDTO.getUserId(), queryDTO.getStatus(), 
                queryDTO.getPaymentMethod(), queryDTO.getKeyword(),
                startTime, endTime, queryDTO.getHasRefund());
        
        // 为每个订单查询是否有退款申请
        List<Order> records = orderPage.getRecords();
        if (!records.isEmpty()) {
            for (Order order : records) {
                // 设置订单状态描述
                order.setStatusDesc(order.getStatusDesc());
                
                // 查询是否有退款申请
                boolean hasRefund = refundService.hasRefundApplication(order.getOrderId());
                order.setHasRefund(hasRefund);
                
                // 查询订单项数量（可选）
                int itemCount = orderItemMapper.selectByOrderId(order.getOrderId()).size();
                // 可以设置到order的一个临时字段中
            }
        }
        
        return orderPage;
    }
    
    /**
     * 管理员获取订单详情
     */
    @Override
    public Result<Order> getOrderDetailForAdmin(String orderId) {
        // 参数校验
        if (orderId == null) {
            return Result.error("参数错误");
        }
        
        // 查询订单
        Order order = orderMapper.selectById(orderId);
        
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        // 查询订单项
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        order.setOrderItems(orderItems);
        
        // 查询收货地址
        if (order.getAddressId() != null) {
            Address address = addressMapper.selectById(order.getAddressId());
            order.setAddress(address);
        }
        
        // 查询是否有退款申请
        boolean hasRefund = refundService.hasRefundApplication(orderId);
        order.setHasRefund(hasRefund);
        
        return Result.success(order);
    }
    
    /**
     * 管理员标记订单为已发货
     */
    @Override
    @Transactional
    public Result<?> markOrderAsShipped(String orderId, String trackingNumber, String shippingCompany) {
        // 参数校验
        if (orderId == null) {
            return Result.error("参数错误");
        }
        
        // 查询订单
        Order order = orderMapper.selectById(orderId);
        
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        // 检查订单状态是否为已支付
        if (order.getStatus() != 1) {
            return Result.error("只有已支付的订单可以标记为已发货");
        }
        
        // 标记为已发货
        Date now = new Date();
        int rows = orderMapper.markOrderAsShipped(orderId, trackingNumber, shippingCompany, now, now);
        
        if (rows > 0) {
            return Result.success("订单已标记为已发货");
        } else {
            return Result.error("操作失败，请重试");
        }
    }
    
    /**
     * 管理员修改订单状态
     */
    @Override
    @Transactional
    public Result<?> updateOrderStatus(String orderId, Integer newStatus, String remarks) {
        // 参数校验
        if (orderId == null || newStatus == null) {
            return Result.error("参数错误");
        }
        
        // 检查状态值是否有效
        if (newStatus < 0 || newStatus > 5) {
            return Result.error("无效的订单状态");
        }
        
        // 查询订单
        Order order = orderMapper.selectById(orderId);
        
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        // 如果状态未变，直接返回成功
        if (order.getStatus().equals(newStatus)) {
            return Result.success("订单状态未变更");
        }
        
        // 更新状态
        Date now = new Date();
        int rows = orderMapper.updateOrderStatus(orderId, order.getStatus(), newStatus, now);
        
        if (rows > 0) {
            // 更新管理员备注
            if (remarks != null && !remarks.isEmpty()) {
                Order updateOrder = new Order();
                updateOrder.setOrderId(orderId);
                updateOrder.setAdminRemarks(remarks);
                updateOrder.setUpdateTime(now);
                orderMapper.updateById(updateOrder);
            }
            
            return Result.success("订单状态已更新");
        } else {
            return Result.error("订单状态更新失败");
        }
    }
    
    /**
     * 生成唯一交易ID
     */
    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);
    }
    
    /**
     * 将Order转换为OrderVO
     */
    private OrderVO convertToOrderVO(Order order) {
        if (order == null) {
            return null;
        }
        
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(order.getOrderId());
        orderVO.setTotalAmount(order.getTotalAmount());
        orderVO.setDiscountAmount(order.getDiscountAmount());
        orderVO.setShippingFee(order.getShippingFee());
        orderVO.setPaymentAmount(order.getPaymentAmount());
        orderVO.setStatus(order.getStatus());
        orderVO.setStatusDesc(order.getStatusDesc());
        orderVO.setPaymentMethod(order.getPaymentMethod());
        orderVO.setNotes(order.getNotes());
        orderVO.setTrackingNumber(order.getTrackingNumber());
        orderVO.setShippingCompany(order.getShippingCompany());
        
        // 日期格式化
        if (order.getCreateTime() != null) {
            orderVO.setCreateTime(dateFormat.format(order.getCreateTime()));
        }
        if (order.getPaymentTime() != null) {
            orderVO.setPaymentTime(dateFormat.format(order.getPaymentTime()));
        }
        if (order.getShippingTime() != null) {
            orderVO.setShippingTime(dateFormat.format(order.getShippingTime()));
        }
        if (order.getCompletionTime() != null) {
            orderVO.setCompletionTime(dateFormat.format(order.getCompletionTime()));
        }
        if (order.getCancellationTime() != null) {
            orderVO.setCancellationTime(dateFormat.format(order.getCancellationTime()));
        }
        
        // 订单状态判断
        orderVO.setCanCancel(order.canCancel());
        orderVO.setCanPay(order.canPay());
        orderVO.setIsExpired(order.isExpired());
        orderVO.setHasRefund(refundService.hasRefundApplication(order.getOrderId()));
        
        // 计算剩余支付时间
        if (order.getStatus() != null && order.getStatus() == 0 && order.getExpirationTime() != null) {
            long remainingTime = order.getExpirationTime().getTime() - System.currentTimeMillis();
            orderVO.setRemainingPaymentTime(remainingTime > 0 ? remainingTime : 0);
        }
        
        return orderVO;
    }

    public AddressVO getDefaultAddress(Integer userId) {
        Address address = addressMapper.getDefaultAddress(userId);
        if (address == null) {
            return null;
        }
        
        AddressVO addressVO = new AddressVO();
        BeanUtils.copyProperties(address, addressVO);
        // 使用时直接调用getFullAddress()方法
        return addressVO;
    }
} 