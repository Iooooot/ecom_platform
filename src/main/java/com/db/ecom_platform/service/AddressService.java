package com.db.ecom_platform.service;

import com.db.ecom_platform.entity.dto.AddressDTO;
import com.db.ecom_platform.entity.vo.AddressVO;
import com.db.ecom_platform.utils.Result;

import java.util.List;

/**
 * 地址服务接口
 */
public interface AddressService {
    
    /**
     * 获取用户所有地址
     * @param userId 用户ID
     * @return 地址列表
     */
    List<AddressVO> listAddresses(Integer userId);
    
    /**
     * 添加地址
     * @param userId 用户ID
     * @param addressDTO 地址信息
     * @return 添加结果
     */
    Result addAddress(Integer userId, AddressDTO addressDTO);
    
    /**
     * 更新地址
     * @param userId 用户ID
     * @param addressId 地址ID
     * @param addressDTO 地址信息
     * @return 更新结果
     */
    Result updateAddress(Integer userId, String addressId, AddressDTO addressDTO);
    
    /**
     * 删除地址
     * @param userId 用户ID
     * @param addressId 地址ID
     * @return 删除结果
     */
    Result deleteAddress(Integer userId, String addressId);
    
    /**
     * 设置默认地址
     * @param userId 用户ID
     * @param addressId 地址ID
     * @return 设置结果
     */
    Result setDefaultAddress(Integer userId, String addressId);
    
    /**
     * 获取默认地址
     * @param userId 用户ID
     * @return 默认地址
     */
    AddressVO getDefaultAddress(Integer userId);
} 