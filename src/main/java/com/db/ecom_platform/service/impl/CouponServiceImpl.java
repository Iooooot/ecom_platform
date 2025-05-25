package com.db.ecom_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.db.ecom_platform.entity.Coupon;
import com.db.ecom_platform.entity.CouponProduct;
import com.db.ecom_platform.entity.Product;
import com.db.ecom_platform.entity.UserCoupon;
import com.db.ecom_platform.entity.dto.CouponDTO;
import com.db.ecom_platform.entity.dto.CouponProductDTO;
import com.db.ecom_platform.entity.dto.UserCouponDTO;
import com.db.ecom_platform.entity.vo.CouponVO;
import com.db.ecom_platform.mapper.CouponMapper;
import com.db.ecom_platform.mapper.CouponProductMapper;
import com.db.ecom_platform.mapper.ProductMapper;
import com.db.ecom_platform.mapper.UserCouponMapper;
import com.db.ecom_platform.mapper.UserMapper;
import com.db.ecom_platform.service.CouponService;
import com.db.ecom_platform.utils.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 优惠券服务实现类
 */
@Service
public class CouponServiceImpl implements CouponService {
    
    @Autowired
    private CouponMapper couponMapper;
    
    @Autowired
    private UserCouponMapper userCouponMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CouponProductMapper couponProductMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    public Page<CouponVO> listCoupons(Integer page, Integer size, String type, Integer status, String keyword) {
        Page<CouponVO> pageParam = new Page<>(page, size);
        return couponMapper.listCoupons(pageParam, type, status, keyword);
    }
    
    @Override
    @Transactional
    public Result<?> addCoupon(CouponDTO couponDTO) {
        // 创建优惠券对象
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(couponDTO, coupon);
        
        // 设置ID和时间
        coupon.setCouponId(UUID.randomUUID().toString().replace("-", ""));
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        coupon.setCreateTime(now);
        coupon.setUpdateTime(now);
        
        // 插入数据
        int result = couponMapper.insert(coupon);
        
        if (result > 0) {
            return Result.success("添加优惠券成功", coupon.getCouponId());
        } else {
            return Result.error("添加优惠券失败");
        }
    }
    
    @Override
    @Transactional
    public Result<?> updateCoupon(CouponDTO couponDTO) {
        if (couponDTO.getCouponId() == null || couponDTO.getCouponId().isEmpty()) {
            return Result.error("优惠券ID不能为空");
        }
        
        // 检查优惠券是否存在
        Integer count = couponMapper.checkCouponExists(couponDTO.getCouponId());
        if (count == null || count == 0) {
            return Result.error("优惠券不存在");
        }
        
        // 创建优惠券对象
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(couponDTO, coupon);
        
        // 设置更新时间
        coupon.setUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // 更新数据
        int result = couponMapper.updateById(coupon);
        
        if (result > 0) {
            return Result.success("更新优惠券成功");
        } else {
            return Result.error("更新优惠券失败");
        }
    }
    
    @Override
    @Transactional
    public Result<?> deleteCoupon(String couponId) {
        // 检查优惠券是否存在
        Integer count = couponMapper.checkCouponExists(couponId);
        if (count == null || count == 0) {
            return Result.error("优惠券不存在");
        }
        
        // 先删除优惠券的用户关系
        userCouponMapper.deleteByCouponId(couponId);
        
        // 删除优惠券的商品关系
        couponProductMapper.deleteByCouponId(couponId);
        
        // 再删除优惠券
        int result = couponMapper.deleteById(couponId);
        
        if (result > 0) {
            return Result.success("删除优惠券成功");
        } else {
            return Result.error("删除优惠券失败");
        }
    }
    
    @Override
    public Result<CouponVO> getCouponDetail(String couponId) {
        // 从数据库获取优惠券信息
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            return Result.error("优惠券不存在");
        }
        
        // 统计信息
        Map<String, Object> stats = couponMapper.getCouponStats(couponId);
        
        // 转换为VO
        CouponVO couponVO = new CouponVO();
        BeanUtils.copyProperties(coupon, couponVO);
        
        // 设置类型描述
        if ("AMOUNT".equals(coupon.getType())) {
            couponVO.setTypeDesc("固定金额");
        } else if ("PERCENT".equals(coupon.getType())) {
            couponVO.setTypeDesc("折扣百分比");
        } else if ("FULL_REDUCTION".equals(coupon.getType())) {
            couponVO.setTypeDesc("满减");
        }
        
        // 设置状态描述
        if (coupon.getStatus() == 0) {
            couponVO.setStatusDesc("未启用");
        } else if (coupon.getStatus() == 1) {
            couponVO.setStatusDesc("已启用");
        } else if (coupon.getStatus() == 2) {
            couponVO.setStatusDesc("已过期");
        }
        
        // 设置统计信息
        if (stats != null) {
            couponVO.setAvailableCount(stats.get("unusedCount") != null ? Integer.valueOf(stats.get("unusedCount").toString()) : 0);
            couponVO.setUsedCount(stats.get("usedCount") != null ? Integer.valueOf(stats.get("usedCount").toString()) : 0);
        }
        
        // 获取关联的商品
        List<Product> products = couponProductMapper.getCouponProducts(couponId);
        couponVO.setProducts(products);
        
        // 判断是否适用于所有商品
        Integer productCount = couponProductMapper.countCouponProducts(couponId);
        couponVO.setIsAllProducts(productCount == null || productCount == 0);
        
        return Result.success(couponVO);
    }
    
    @Override
    @Transactional
    public Result<?> assignCouponsToUsers(UserCouponDTO userCouponDTO) {
        if (userCouponDTO.getCouponId() == null || userCouponDTO.getCouponId().isEmpty()) {
            return Result.error("优惠券ID不能为空");
        }
        
        if (userCouponDTO.getUserIds() == null || userCouponDTO.getUserIds().isEmpty()) {
            return Result.error("用户ID列表不能为空");
        }
        
        // 检查优惠券是否存在
        Coupon coupon = couponMapper.selectById(userCouponDTO.getCouponId());
        if (coupon == null) {
            return Result.error("优惠券不存在");
        }
        
        // 检查优惠券状态
        if (coupon.getStatus() != 1) {
            return Result.error("只能分配已启用的优惠券");
        }
        
        // 当前时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        // 创建用户优惠券关系列表
        List<UserCoupon> userCoupons = new ArrayList<>();
        
        for (Integer userId : userCouponDTO.getUserIds()) {
            // 检查用户是否存在
            if (userMapper.selectById(userId) == null) {
                continue; // 用户不存在，跳过
            }
            
            // 检查是否已经分配过
            Integer exist = userCouponMapper.checkUserCouponExists(userId, userCouponDTO.getCouponId());
            if (exist != null && exist > 0) {
                continue; // 已分配过，跳过
            }
            
            // 创建用户优惠券关系
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUserId(userId);
            userCoupon.setCouponId(userCouponDTO.getCouponId());
            userCoupon.setStatus(0); // 未使用
            userCoupon.setCreateTime(now);
            
            userCoupons.add(userCoupon);
        }
        
        if (userCoupons.isEmpty()) {
            return Result.success("没有需要分配的用户");
        }
        
        // 批量插入
        int result = userCouponMapper.batchInsertUserCoupons(userCoupons);
        
        if (result > 0) {
            return Result.success("分配优惠券成功");
        } else {
            return Result.error("分配优惠券失败");
        }
    }
    
    @Override
    public List<CouponVO> getUserAvailableCoupons(Integer userId) {
        return couponMapper.getUserAvailableCoupons(userId);
    }
    
    @Override
    public List<CouponVO> getUserCoupons(Integer userId, Integer status) {
        return couponMapper.getUserCoupons(userId, status);
    }
    
    @Override
    @Transactional
    public Result<?> updateCouponStatus(String couponId, Integer status) {
        // 检查优惠券是否存在
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            return Result.error("优惠券不存在");
        }
        
        // 更新状态
        int result = couponMapper.updateCouponStatus(couponId, status);
        
        if (result > 0) {
            return Result.success("更新优惠券状态成功");
        } else {
            return Result.error("更新优惠券状态失败");
        }
    }
    
    @Override
    @Transactional
    public Result<?> unassignCouponFromUser(Integer userId, String couponId) {
        // 检查用户是否存在
        if (userMapper.selectById(userId) == null) {
            return Result.error("用户不存在");
        }
        
        // 检查优惠券是否存在
        if (couponMapper.selectById(couponId) == null) {
            return Result.error("优惠券不存在");
        }
        
        // 检查用户是否已拥有该优惠券
        Integer exist = userCouponMapper.checkUserCouponExists(userId, couponId);
        if (exist == null || exist == 0) {
            return Result.error("用户未拥有该优惠券");
        }
        
        // 删除用户优惠券关系
        int result = userCouponMapper.deleteUserCoupon(userId, couponId);
        
        if (result > 0) {
            return Result.success("取消优惠券分配成功");
        } else {
            return Result.error("取消优惠券分配失败");
        }
    }
    
    @Override
    @Transactional
    public Result<?> bindProductsToCoupon(CouponProductDTO couponProductDTO) {
        if (couponProductDTO.getCouponId() == null || couponProductDTO.getCouponId().isEmpty()) {
            return Result.error("优惠券ID不能为空");
        }
        
        if (couponProductDTO.getProductIds() == null || couponProductDTO.getProductIds().isEmpty()) {
            return Result.error("商品ID列表不能为空");
        }
        
        // 检查优惠券是否存在
        Coupon coupon = couponMapper.selectById(couponProductDTO.getCouponId());
        if (coupon == null) {
            return Result.error("优惠券不存在");
        }
        
        // 当前时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        // 创建优惠券商品关系列表
        List<CouponProduct> couponProducts = new ArrayList<>();
        
        for (String productId : couponProductDTO.getProductIds()) {
            // 检查商品是否存在
            Product product = productMapper.getProductById(productId);
            if (product == null) {
                continue; // 商品不存在，跳过
            }
            
            // 检查是否已经绑定过
            Integer exist = couponProductMapper.checkCouponProduct(couponProductDTO.getCouponId(), productId);
            if (exist != null && exist > 0) {
                continue; // 已绑定过，跳过
            }
            
            // 创建优惠券商品关系
            CouponProduct couponProduct = new CouponProduct();
            couponProduct.setCouponId(couponProductDTO.getCouponId());
            couponProduct.setProductId(productId);
            couponProduct.setCreateTime(now);
            
            couponProducts.add(couponProduct);
        }
        
        if (couponProducts.isEmpty()) {
            return Result.success("没有需要绑定的商品");
        }
        
        // 批量插入
        int result = couponProductMapper.batchInsertCouponProducts(couponProducts);
        
        if (result > 0) {
            return Result.success("绑定商品成功");
        } else {
            return Result.error("绑定商品失败");
        }
    }
    
    @Override
    @Transactional
    public Result<?> unbindProductFromCoupon(String couponId, String productId) {
        // 检查优惠券是否存在
        if (couponMapper.selectById(couponId) == null) {
            return Result.error("优惠券不存在");
        }
        
        // 检查商品是否存在
        if (productMapper.getProductById(productId) == null) {
            return Result.error("商品不存在");
        }
        
        // 检查优惠券是否已绑定该商品
        Integer exist = couponProductMapper.checkCouponProduct(couponId, productId);
        if (exist == null || exist == 0) {
            return Result.error("优惠券未绑定该商品");
        }
        
        // 解绑优惠券商品关系
        int result = couponProductMapper.deleteCouponProduct(couponId, productId);
        
        if (result > 0) {
            return Result.success("解绑商品成功");
        } else {
            return Result.error("解绑商品失败");
        }
    }
    
    @Override
    public Result<List<Product>> getCouponProducts(String couponId) {
        // 检查优惠券是否存在
        if (couponMapper.selectById(couponId) == null) {
            return Result.error("优惠券不存在");
        }
        
        // 获取绑定的商品列表
        List<Product> products = couponProductMapper.getCouponProducts(couponId);
        
        return Result.success(products);
    }
    
    @Override
    public Result<Boolean> checkProductCoupon(String couponId, String productId) {
        // 检查优惠券是否存在
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            return Result.error("优惠券不存在");
        }
        
        // 检查商品是否存在
        if (productMapper.getProductById(productId) == null) {
            return Result.error("商品不存在");
        }
        
        // 获取优惠券关联的商品数量
        Integer productCount = couponProductMapper.countCouponProducts(couponId);
        
        // 如果没有关联任何商品，则视为可用于所有商品
        if (productCount == null || productCount == 0) {
            return Result.success(true);
        }
        
        // 检查商品是否在优惠券关联列表中
        Integer exist = couponProductMapper.checkCouponProduct(couponId, productId);
        
        return Result.success(exist != null && exist > 0);
    }
    
    @Override
    public List<CouponVO> getProductCoupons(String productId) {
        // 检查商品是否存在
        if (productMapper.getProductById(productId) == null) {
            return new ArrayList<>();
        }
        
        // 获取商品可用的优惠券ID列表
        List<String> couponIds = couponProductMapper.getProductCouponIds(productId);
        
        if (couponIds.isEmpty()) {
            // 如果没有特定的优惠券，获取适用于所有商品的优惠券
            QueryWrapper<Coupon> wrapper = new QueryWrapper<>();
            wrapper.eq("status", 1)  // 已启用的优惠券
                   .ge("end_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));  // 未过期的优惠券
            
            List<Coupon> allCoupons = couponMapper.selectList(wrapper);
            
            // 过滤掉有商品绑定的优惠券
            List<Coupon> universalCoupons = allCoupons.stream()
                .filter(coupon -> {
                    Integer count = couponProductMapper.countCouponProducts(coupon.getCouponId());
                    return count == null || count == 0;
                })
                .collect(Collectors.toList());
            
            // 转换为VO
            List<CouponVO> result = new ArrayList<>();
            for (Coupon coupon : universalCoupons) {
                CouponVO vo = new CouponVO();
                BeanUtils.copyProperties(coupon, vo);
                result.add(vo);
            }
            
            return result;
        } else {
            // 获取绑定的优惠券详情
            List<CouponVO> result = new ArrayList<>();
            for (String couponId : couponIds) {
                Coupon coupon = couponMapper.selectById(couponId);
                if (coupon != null && coupon.getStatus() == 1) {
                    CouponVO vo = new CouponVO();
                    BeanUtils.copyProperties(coupon, vo);
                    result.add(vo);
                }
            }
            
            return result;
        }
    }
    
    @Override
    public Result<List<UserCoupon>> getCouponUsers(String couponId) {
        // 检查优惠券是否存在
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            return Result.error("优惠券不存在");
        }
        
        List<UserCoupon> userCoupons = userCouponMapper.getCouponUsers(couponId);
        return Result.success(userCoupons);
    }
} 