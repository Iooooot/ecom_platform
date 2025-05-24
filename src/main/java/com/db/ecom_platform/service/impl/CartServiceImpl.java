package com.db.ecom_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.db.ecom_platform.entity.CartItem;
import com.db.ecom_platform.entity.Coupon;
import com.db.ecom_platform.entity.Product;
import com.db.ecom_platform.entity.UserCoupon;
import com.db.ecom_platform.entity.dto.CartItemDTO;
import com.db.ecom_platform.entity.dto.CartOperationDTO;
import com.db.ecom_platform.entity.vo.CartItemVO;
import com.db.ecom_platform.entity.vo.CartVO;
import com.db.ecom_platform.mapper.CartItemMapper;
import com.db.ecom_platform.mapper.CouponMapper;
import com.db.ecom_platform.mapper.ProductMapper;
import com.db.ecom_platform.mapper.UserCouponMapper;
import com.db.ecom_platform.service.CartService;
import com.db.ecom_platform.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车服务实现类
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements CartService {
    
    @Autowired
    private CartItemMapper cartItemMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private CouponMapper couponMapper;
    
    @Autowired
    private UserCouponMapper userCouponMapper;
    
    @Override
    public CartVO getUserCart(Integer userId) {
        CartVO cartVO = new CartVO();
        
        // 获取有效商品
        List<CartItem> validItems = cartItemMapper.getValidCartItems(userId);
        
        // 获取失效商品
        List<CartItem> invalidItems = cartItemMapper.getInvalidCartItems(userId);
        
        // 设置商品信息和小计金额
        if (!CollectionUtils.isEmpty(validItems)) {
            validItems.forEach(item -> {
                // 获取商品信息
                Product product = productMapper.selectById(item.getProductId());
                item.setProduct(product);
                
                // 计算小计金额
                if (product != null) {
                    item.setSubtotal(product.getPrice().multiply(new BigDecimal(item.getQuantity())));
                }
                
                // 默认选中
                item.setChecked(true);
                item.setIsInvalid(false);
            });
        }
        
        // 设置失效商品信息
        if (!CollectionUtils.isEmpty(invalidItems)) {
            invalidItems.forEach(item -> {
                Product product = productMapper.selectById(item.getProductId());
                item.setProduct(product);
                
                if (product != null) {
                    item.setSubtotal(product.getPrice().multiply(new BigDecimal(item.getQuantity())));
                }
                
                item.setChecked(false);
                item.setIsInvalid(true);
            });
        }
        
        // 计算总数量和总金额
        Integer totalQuantity = validItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        
        BigDecimal totalAmount = validItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 初始化优惠信息
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal payableAmount = totalAmount;
        
        // 设置购物车信息
        cartVO.setItems(validItems);
        cartVO.setTotalQuantity(totalQuantity);
        cartVO.setTotalAmount(totalAmount);
        cartVO.setDiscountAmount(discountAmount);
        cartVO.setPayableAmount(payableAmount);
        cartVO.setInvalidItemCount(invalidItems.size());
        cartVO.setInvalidItems(invalidItems);
        
        return cartVO;
    }
    
    @Override
    @Transactional
    public Result<?> addToCart(Integer userId, CartItemDTO cartItemDTO) {
        // 参数校验
        if (userId == null || cartItemDTO == null || cartItemDTO.getProductId() == null) {
            return Result.error("参数错误");
        }
        
        // 查询商品信息
        Product product = productMapper.selectById(cartItemDTO.getProductId());
        if (product == null) {
            return Result.error("商品不存在");
        }
        
        // 检查商品是否上架
        if (product.getStatus() == 0) {
            return Result.error("商品已下架");
        }
        
        // 检查库存
        int quantity = cartItemDTO.getQuantity() == null ? 1 : cartItemDTO.getQuantity();
        if (product.getStock() < quantity) {
            return Result.error("商品库存不足");
        }
        
        // 查询购物车中是否已存在该商品
        CartItem existItem = cartItemMapper.getCartItemByUserIdAndProductId(userId, cartItemDTO.getProductId());
        
        Date now = new Date();
        
        if (existItem != null) {
            // 更新数量
            int newQuantity = existItem.getQuantity() + quantity;
            
            // 再次检查库存
            if (product.getStock() < newQuantity) {
                return Result.error("商品库存不足");
            }
            
            existItem.setQuantity(newQuantity);
            existItem.setUpdateTime(now);
            cartItemMapper.updateById(existItem);
        } else {
            // 添加新商品到购物车
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(cartItemDTO.getProductId());
            cartItem.setQuantity(quantity);
            cartItem.setCreateTime(now);
            cartItem.setUpdateTime(now);
            cartItemMapper.insert(cartItem);
        }
        
        return Result.success("添加成功");
    }
    
    @Override
    @Transactional
    public Result<?> updateCartItemQuantity(Integer userId, Long itemId, Integer quantity) {
        // 参数校验
        if (userId == null || itemId == null || quantity == null || quantity <= 0) {
            return Result.error("参数错误");
        }
        
        // 查询购物车项
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getId, itemId).eq(CartItem::getUserId, userId);
        CartItem cartItem = cartItemMapper.selectOne(queryWrapper);
        
        if (cartItem == null) {
            return Result.error("购物车项不存在");
        }
        
        // 查询商品信息
        Product product = productMapper.selectById(cartItem.getProductId());
        if (product == null) {
            return Result.error("商品不存在");
        }
        
        // 检查库存
        if (product.getStock() < quantity) {
            return Result.error("商品库存不足");
        }
        
        // 更新数量
        cartItem.setQuantity(quantity);
        cartItem.setUpdateTime(new Date());
        cartItemMapper.updateById(cartItem);
        
        return Result.success("更新成功");
    }
    
    @Override
    @Transactional
    public Result<?> batchUpdateCartItems(Integer userId, List<CartItemDTO> cartItemDTOList) {
        // 参数校验
        if (userId == null || CollectionUtils.isEmpty(cartItemDTOList)) {
            return Result.error("参数错误");
        }
        
        for (CartItemDTO dto : cartItemDTOList) {
            // 查询购物车中是否已存在该商品
            CartItem existItem = cartItemMapper.getCartItemByUserIdAndProductId(userId, dto.getProductId());
            
            if (existItem != null) {
                // 查询商品信息
                Product product = productMapper.selectById(dto.getProductId());
                if (product == null || product.getStatus() == 0 || product.getStock() < dto.getQuantity()) {
                    continue; // 跳过无效商品
                }
                
                // 更新数量
                existItem.setQuantity(dto.getQuantity());
                existItem.setUpdateTime(new Date());
                cartItemMapper.updateById(existItem);
            } else {
                // 添加新商品到购物车
                Product product = productMapper.selectById(dto.getProductId());
                if (product == null || product.getStatus() == 0 || product.getStock() < dto.getQuantity()) {
                    continue; // 跳过无效商品
                }
                
                Date now = new Date();
                CartItem cartItem = new CartItem();
                cartItem.setUserId(userId);
                cartItem.setProductId(dto.getProductId());
                cartItem.setQuantity(dto.getQuantity());
                cartItem.setCreateTime(now);
                cartItem.setUpdateTime(now);
                cartItemMapper.insert(cartItem);
            }
        }
        
        return Result.success("批量更新成功");
    }
    
    @Override
    @Transactional
    public Result<?> removeCartItem(Integer userId, Long itemId) {
        // 参数校验
        if (userId == null || itemId == null) {
            return Result.error("参数错误");
        }
        
        // 删除购物车项
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getId, itemId).eq(CartItem::getUserId, userId);
        cartItemMapper.delete(queryWrapper);
        
        return Result.success("删除成功");
    }
    
    @Override
    @Transactional
    public Result<?> batchOperateCartItems(Integer userId, CartOperationDTO operationDTO) {
        // 参数校验
        if (userId == null || operationDTO == null || 
                CollectionUtils.isEmpty(operationDTO.getItemIds()) || 
                operationDTO.getOperation() == null) {
            return Result.error("参数错误");
        }
        
        String operation = operationDTO.getOperation().toLowerCase();
        List<Long> itemIds = operationDTO.getItemIds();
        
        // 批量操作
        switch (operation) {
            case "delete":
                // 批量删除
                cartItemMapper.batchDeleteCartItems(userId, itemIds);
                return Result.success("批量删除成功");
                
            case "check":
            case "uncheck":
                // 选中/取消选中的逻辑在前端处理
                return Result.success("操作成功");
                
            default:
                return Result.error("不支持的操作类型");
        }
    }
    
    @Override
    @Transactional
    public Result<?> clearCart(Integer userId) {
        // 参数校验
        if (userId == null) {
            return Result.error("参数错误");
        }
        
        // 清空购物车
        cartItemMapper.clearUserCart(userId);
        
        return Result.success("购物车已清空");
    }
    
    @Override
    @Transactional
    public Result<?> clearInvalidItems(Integer userId) {
        // 参数校验
        if (userId == null) {
            return Result.error("参数错误");
        }
        
        // 获取失效商品
        List<CartItem> invalidItems = cartItemMapper.getInvalidCartItems(userId);
        
        if (CollectionUtils.isEmpty(invalidItems)) {
            return Result.success("没有失效商品");
        }
        
        // 批量删除失效商品
        List<Long> invalidItemIds = invalidItems.stream()
                .map(CartItem::getId)
                .collect(Collectors.toList());
        
        cartItemMapper.batchDeleteCartItems(userId, invalidItemIds);
        
        return Result.success("失效商品已清除");
    }
    
    @Override
    public Result<?> calculateDiscount(Integer userId) {
        // 参数校验
        if (userId == null) {
            return Result.error("参数错误");
        }
        
        // 获取购物车信息
        CartVO cartVO = getUserCart(userId);
        
        // 获取选中的购物车项
        List<CartItem> checkedItems = cartVO.getItems().stream()
                .filter(CartItem::getChecked)
                .collect(Collectors.toList());
        
        if (CollectionUtils.isEmpty(checkedItems)) {
            return Result.success(cartVO);
        }
        
        // 计算总金额
        BigDecimal totalAmount = checkedItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 获取用户可用优惠券
        List<UserCoupon> userCoupons = userCouponMapper.getUserAvailableCoupons(userId);
        
        // 过滤出符合条件的优惠券
        List<Coupon> availableCoupons = new ArrayList<>();
        
        if (!CollectionUtils.isEmpty(userCoupons)) {
            for (UserCoupon userCoupon : userCoupons) {
                Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
                
                if (coupon != null && coupon.getStatus() == 1) {
                    // 检查优惠券是否过期
                    boolean isExpired = isExpired(coupon.getEndTime());
                    
                    if (!isExpired) {
                        // 检查订单金额是否满足优惠券使用条件
                        if (totalAmount.compareTo(coupon.getMinOrderAmount()) >= 0) {
                            availableCoupons.add(coupon);
                        }
                    }
                }
            }
        }
        
        // 找出最优惠的优惠券
        Coupon bestCoupon = findBestCoupon(availableCoupons, totalAmount);
        
        // 计算优惠金额
        BigDecimal discountAmount = BigDecimal.ZERO;
        
        if (bestCoupon != null) {
            discountAmount = calculateDiscountAmount(bestCoupon, totalAmount);
        }
        
        // 计算应付金额
        BigDecimal payableAmount = totalAmount.subtract(discountAmount);
        if (payableAmount.compareTo(BigDecimal.ZERO) < 0) {
            payableAmount = BigDecimal.ZERO;
        }
        
        // 更新购物车信息
        cartVO.setTotalAmount(totalAmount);
        cartVO.setDiscountAmount(discountAmount);
        cartVO.setPayableAmount(payableAmount);
        
        return Result.success(cartVO);
    }
    
    /**
     * 判断是否过期
     * @param endTime 结束时间
     * @return 是否过期
     */
    private boolean isExpired(String endTime) {
        try {
            // 这里假设endTime格式为"yyyy-MM-dd HH:mm:ss"
            // 实际中应根据真实的日期格式进行解析
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date end = sdf.parse(endTime);
            return new Date().after(end);
        } catch (Exception e) {
            return true; // 解析异常当作已过期处理
        }
    }
    
    /**
     * 找出最优惠的优惠券
     * @param coupons 可用优惠券列表
     * @param totalAmount 总金额
     * @return 最优惠的优惠券
     */
    private Coupon findBestCoupon(List<Coupon> coupons, BigDecimal totalAmount) {
        if (CollectionUtils.isEmpty(coupons)) {
            return null;
        }
        
        Coupon bestCoupon = null;
        BigDecimal maxDiscount = BigDecimal.ZERO;
        
        for (Coupon coupon : coupons) {
            BigDecimal discount = calculateDiscountAmount(coupon, totalAmount);
            
            if (discount.compareTo(maxDiscount) > 0) {
                maxDiscount = discount;
                bestCoupon = coupon;
            }
        }
        
        return bestCoupon;
    }
    
    /**
     * 计算优惠金额
     * @param coupon 优惠券
     * @param totalAmount 总金额
     * @return 优惠金额
     */
    private BigDecimal calculateDiscountAmount(Coupon coupon, BigDecimal totalAmount) {
        if (coupon == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discountAmount;
        
        switch (coupon.getType()) {
            case "AMOUNT":
                // 固定金额
                discountAmount = coupon.getDiscountValue();
                break;
                
            case "PERCENT":
                // 折扣百分比
                discountAmount = totalAmount.multiply(
                        BigDecimal.ONE.subtract(coupon.getDiscountValue().divide(BigDecimal.TEN, 2, RoundingMode.HALF_UP))
                );
                break;
                
            case "FULL_REDUCTION":
                // 满减
                if (totalAmount.compareTo(coupon.getMinOrderAmount()) >= 0) {
                    discountAmount = coupon.getDiscountValue();
                } else {
                    discountAmount = BigDecimal.ZERO;
                }
                break;
                
            default:
                discountAmount = BigDecimal.ZERO;
        }
        
        return discountAmount;
    }
    
    @Override
    public List<CartItemVO> getCartItemsByIds(Integer userId, List<Long> cartItemIds) {
        if (CollectionUtils.isEmpty(cartItemIds)) {
            return new ArrayList<>();
        }
        
        // 查询购物车项
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId)
                   .in(CartItem::getId, cartItemIds);
        List<CartItem> cartItems = cartItemMapper.selectList(queryWrapper);
        
        if (CollectionUtils.isEmpty(cartItems)) {
            return new ArrayList<>();
        }
        
        // 转换为VO对象
        List<CartItemVO> result = new ArrayList<>(cartItems.size());
        for (CartItem item : cartItems) {
            Product product = productMapper.selectById(item.getProductId());
            if (product == null) {
                continue;
            }
            
            CartItemVO vo = new CartItemVO();
            vo.setId(item.getId());
            vo.setProductId(item.getProductId());
            vo.setProductName(product.getName());
            vo.setPrice(product.getPrice());
            vo.setQuantity(item.getQuantity());
            vo.setImage(product.getImage());
            vo.setStock(product.getStock());
            vo.setIsAvailable(product.getStatus() == 1 && product.getStock() >= item.getQuantity());
            
            result.add(vo);
        }
        
        return result;
    }
} 