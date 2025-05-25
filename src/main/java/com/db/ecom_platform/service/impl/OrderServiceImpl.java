package com.db.ecom_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.db.ecom_platform.entity.*;
import com.db.ecom_platform.entity.dto.AdminOrderQueryDTO;
import com.db.ecom_platform.entity.dto.OrderCreateDTO;
import com.db.ecom_platform.entity.dto.OrderQueryDTO;
import com.db.ecom_platform.entity.dto.PaymentDTO;
import com.db.ecom_platform.entity.vo.AddressVO;
import com.db.ecom_platform.entity.vo.CartItemVO;
import com.db.ecom_platform.entity.vo.OrderVO;
import com.db.ecom_platform.mapper.*;
import com.db.ecom_platform.service.CartService;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private CartItemMapper cartItemMapper;
    
    @Autowired
    private CouponMapper couponMapper;
    
    @Autowired
    private UserCouponMapper userCouponMapper;
    
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
                
                // 查询收货地址信息
                Order originalOrder = orderMapper.selectById(orderVO.getOrderId());
                if (originalOrder != null && originalOrder.getAddressId() != null) {
                    Address address = addressMapper.selectById(originalOrder.getAddressId());
                    if (address != null) {
                        AddressVO addressVO = new AddressVO();
                        BeanUtils.copyProperties(address, addressVO);
                        orderVO.setAddress(addressVO);
                    }
                }
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

    @Override
    @Transactional
    public Result<String> createOrder(Integer userId, OrderCreateDTO orderCreateDTO) {
        // 参数校验
        if (userId == null || orderCreateDTO == null || CollectionUtils.isEmpty(orderCreateDTO.getCartItemIds())) {
            return Result.error("参数错误");
        }
        
        if (orderCreateDTO.getAddressId() == null) {
            return Result.error("收货地址不能为空");
        }
        
        // 获取购物车项
        List<CartItemVO> cartItems = cartService.getCartItemsByIds(userId, orderCreateDTO.getCartItemIds());
        if (CollectionUtils.isEmpty(cartItems)) {
            return Result.error("购物车商品不存在");
        }
        
        // 检查商品是否有效
        List<CartItemVO> invalidItems = cartItems.stream()
                .filter(item -> !item.getIsAvailable())
                .collect(Collectors.toList());
        
        if (!invalidItems.isEmpty()) {
            String invalidNames = invalidItems.stream()
                    .map(CartItemVO::getProductName)
                    .collect(Collectors.joining(", "));
            return Result.error("以下商品已失效或库存不足: " + invalidNames);
        }
        
        // 获取收货地址
        Address address = addressMapper.selectById(orderCreateDTO.getAddressId());
        if (address == null || !userId.toString().equals(address.getUserId())) {
            return Result.error("收货地址不存在或不属于当前用户");
        }
        
        // 计算订单金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItemVO item : cartItems) {
            totalAmount = totalAmount.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        
        // 处理优惠券
        BigDecimal discountAmount = BigDecimal.ZERO;
        String usedCouponId = null;
        UserCoupon userCoupon = null;
        
        if (StringUtils.hasText(orderCreateDTO.getCouponId())) {
            try {
                // 获取优惠券信息
                Coupon coupon = couponMapper.selectById(orderCreateDTO.getCouponId());
                if (coupon != null) {
                    // 检查优惠券是否属于当前用户且可用
                    LambdaQueryWrapper<UserCoupon> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(UserCoupon::getUserId, userId)
                               .eq(UserCoupon::getCouponId, orderCreateDTO.getCouponId())
                               .eq(UserCoupon::getStatus, 0); // 未使用状态
                    userCoupon = userCouponMapper.selectOne(queryWrapper);
                    
                    if (userCoupon == null) {
                        return Result.error("优惠券不存在或已使用");
                    }
                    
                    // 检查优惠券金额限制
                    if (totalAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
                        return Result.error("订单金额不满足优惠券使用条件，最低消费：¥" + coupon.getMinOrderAmount());
                    }
                    
                    // 检查有效期
                    try {
                        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        if (now.compareTo(coupon.getStartTime()) < 0 || now.compareTo(coupon.getEndTime()) > 0) {
                            return Result.error("优惠券不在有效期内");
                        }
                    } catch (Exception e) {
                        log.error("优惠券时间格式错误", e);
                        return Result.error("优惠券信息有误，请联系客服");
                    }
                    
                    // 计算优惠金额
                    if ("fixed".equals(coupon.getType())) {
                        // 固定金额
                        discountAmount = coupon.getDiscountValue();
                    } else if ("percentage".equals(coupon.getType())) {
                        // 百分比折扣，正确计算方式
                        BigDecimal discountRate = coupon.getDiscountValue().divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
                        discountAmount = totalAmount.multiply(BigDecimal.ONE.subtract(discountRate)).setScale(2, BigDecimal.ROUND_HALF_UP);
                    }
                    
                    // 记住使用的优惠券ID
                    usedCouponId = orderCreateDTO.getCouponId();
                }
            } catch (Exception e) {
                log.error("处理优惠券异常", e);
                return Result.error("处理优惠券异常，请重试");
            }
        }
        
        // 计算运费（简单计算）
        BigDecimal shippingFee = totalAmount.compareTo(new BigDecimal("99")) > 0 ? 
                BigDecimal.ZERO : new BigDecimal("10");
        
        // 计算实付金额
        BigDecimal paymentAmount = totalAmount.subtract(discountAmount).add(shippingFee);
        if (paymentAmount.compareTo(BigDecimal.ZERO) < 0) {
            paymentAmount = BigDecimal.ZERO;
        }
        
        // 创建订单
        Date now = new Date();
        String orderId = generateOrderId();
        
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setShippingFee(shippingFee);
        order.setPaymentAmount(paymentAmount);
        order.setCouponId(usedCouponId);
        order.setAddressId(orderCreateDTO.getAddressId()); // 直接使用String类型的addressId
        order.setStatus(0); // 待支付
        order.setCreateTime(now);
        order.setUpdateTime(now);
        order.setNotes(orderCreateDTO.getRemark());
        
        // 设置过期时间（30分钟后）
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MINUTE, 30);
        order.setExpirationTime(calendar.getTime());
        
        // 保存订单
        orderMapper.insert(order);
        
        // 如果使用了优惠券，更新优惠券状态
        if (userCoupon != null) {
            UserCoupon updatedCoupon = new UserCoupon();
            updatedCoupon.setId(userCoupon.getId());
            updatedCoupon.setStatus(1); // 已使用状态
            updatedCoupon.setUseTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            updatedCoupon.setOrderId(orderId); // 设置使用该优惠券的订单ID
            userCouponMapper.updateById(updatedCoupon);
        }
        
        // 创建订单项
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItemVO item : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setProductId(item.getProductId());
            orderItem.setProductName(item.getProductName());
            orderItem.setProductImage(item.getImage());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getPrice());
            orderItem.setSubtotal(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            orderItem.setCreateTime(now);
            
            orderItems.add(orderItem);
            
            // 减少商品库存 - 使用正确的方法
            Product product = productMapper.selectById(item.getProductId());
            if (product != null && product.getStock() >= item.getQuantity()) {
                product.setStock(product.getStock() - item.getQuantity());
                product.setSalesVolume(product.getSalesVolume() + item.getQuantity()); // 增加销量
                product.setUpdateTime(now);
                productMapper.updateById(product);
            } else {
                log.error("商品库存不足，商品ID: {}, 当前库存: {}, 需要数量: {}", 
                          item.getProductId(), 
                          product != null ? product.getStock() : 0, 
                          item.getQuantity());
            }
        }
        
        // 批量保存订单项
        for (OrderItem item : orderItems) {
            orderItemMapper.insert(item);
        }
        
        // 从购物车中移除已购买的商品
        cartItemMapper.batchDeleteCartItems(userId, orderCreateDTO.getCartItemIds());
        
        return Result.success(orderId);
    }

    /**
     * 生成唯一订单ID
     */
    private String generateOrderId() {
        return "OD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6);
    }
} 