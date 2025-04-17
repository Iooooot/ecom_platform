package com.db.ecom_platform.service;

import com.db.ecom_platform.entity.Address;
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
     * 获取用户所有地址（与控制器调用一致的方法名）
     * @param userId 用户ID
     * @return 地址列表
     */
    List<Address> getAddressesByUser(Integer userId);
    
    /**
     * 获取地址详情
     * @param id 地址ID
     * @param userId 用户ID
     * @return 地址信息
     */
    Address getAddress(Integer id, Integer userId);
    
    /**
     * 添加地址
     * @param userId 用户ID
     * @param addressDTO 地址信息
     * @return 添加结果
     */
    Result<Object> addAddress(Integer userId, AddressDTO addressDTO);
    
    /**
     * 添加地址（与控制器调用一致的参数顺序）
     * @param addressDTO 地址信息
     * @param userId 用户ID
     * @return 添加的地址
     */
    Address addAddress(AddressDTO addressDTO, Integer userId);
    
    /**
     * 更新地址
     * @param userId 用户ID
     * @param addressId 地址ID
     * @param addressDTO 地址信息
     * @return 更新结果
     */
    Result<Object> updateAddress(Integer userId, String addressId, AddressDTO addressDTO);
    
    /**
     * 更新地址（与控制器调用一致的参数顺序）
     * @param id 地址ID
     * @param addressDTO 地址信息
     * @param userId 用户ID
     * @return 更新后的地址
     */
    Address updateAddress(Integer id, AddressDTO addressDTO, Integer userId);
    
    /**
     * 删除地址
     * @param userId 用户ID
     * @param addressId 地址ID
     * @return 删除结果
     */
    Result<Object> deleteAddress(Integer userId, String addressId);
    
    /**
     * 删除地址（与控制器调用一致的参数顺序）
     * @param id 地址ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteAddress(Integer id, Integer userId);
    
    /**
     * 设置默认地址
     * @param userId 用户ID
     * @param addressId 地址ID
     * @return 设置结果
     */
    Result<Object> setDefaultAddress(Integer userId, String addressId);
    
    /**
     * 设置默认地址（与控制器调用一致的参数顺序）
     * @param id 地址ID
     * @param userId 用户ID
     * @return 是否设置成功
     */
    boolean setDefaultAddress(Integer id, Integer userId);
    
    /**
     * 获取默认地址
     * @param userId 用户ID
     * @return 默认地址
     */
    AddressVO getDefaultAddress(Integer userId);
} 