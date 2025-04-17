package com.db.ecom_platform.controller;

import com.db.ecom_platform.entity.dto.AddressDTO;
import com.db.ecom_platform.entity.vo.AddressVO;
import com.db.ecom_platform.service.AddressService;
import com.db.ecom_platform.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址控制器
 */
@RestController
@RequestMapping("/api/address")
public class AddressController {
    
    @Autowired
    private AddressService addressService;
    
    /**
     * 获取用户所有地址
     */
    @GetMapping("/list")
    public Result listAddresses() {
        Integer userId = getCurrentUserId();
        List<AddressVO> addresses = addressService.listAddresses(userId);
        return Result.success(addresses);
    }
    
    /**
     * 添加地址
     */
    @PostMapping("/add")
    public Result addAddress(@RequestBody AddressDTO addressDTO) {
        Integer userId = getCurrentUserId();
        return addressService.addAddress(userId, addressDTO);
    }
    
    /**
     * 更新地址
     */
    @PutMapping("/{addressId}")
    public Result updateAddress(@PathVariable String addressId, @RequestBody AddressDTO addressDTO) {
        Integer userId = getCurrentUserId();
        return addressService.updateAddress(userId, addressId, addressDTO);
    }
    
    /**
     * 删除地址
     */
    @DeleteMapping("/{addressId}")
    public Result deleteAddress(@PathVariable String addressId) {
        Integer userId = getCurrentUserId();
        return addressService.deleteAddress(userId, addressId);
    }
    
    /**
     * 设置默认地址
     */
    @PutMapping("/{addressId}/default")
    public Result setDefaultAddress(@PathVariable String addressId) {
        Integer userId = getCurrentUserId();
        return addressService.setDefaultAddress(userId, addressId);
    }
    
    /**
     * 获取默认地址
     */
    @GetMapping("/default")
    public Result getDefaultAddress() {
        Integer userId = getCurrentUserId();
        AddressVO address = addressService.getDefaultAddress(userId);
        return Result.success(address);
    }
    
    /**
     * 获取当前登录用户ID
     * 实际实现应该从安全上下文中获取
     */
    private Integer getCurrentUserId() {
        // 这里只是占位符，实际实现应该基于你的身份验证系统
        return 1; // 假设用户ID为1
    }
} 