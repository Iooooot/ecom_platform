package com.db.ecom_platform.service.impl;

import com.db.ecom_platform.entity.Address;
import com.db.ecom_platform.entity.dto.AddressDTO;
import com.db.ecom_platform.entity.vo.AddressVO;
import com.db.ecom_platform.mapper.AddressMapper;
import com.db.ecom_platform.service.AddressService;
import com.db.ecom_platform.utils.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 地址服务实现类
 */
@Service
public class AddressServiceImpl implements AddressService {
    
    @Autowired
    private AddressMapper addressMapper;
    
    @Override
    public List<AddressVO> listAddresses(Integer userId) {
        List<Address> addresses = addressMapper.listByUserId(userId);
        List<AddressVO> addressVOList = new ArrayList<>();
        
        for (Address address : addresses) {
            AddressVO addressVO = new AddressVO();
            BeanUtils.copyProperties(address, addressVO);
            
            // 构建完整地址字符串
            StringBuilder fullAddress = new StringBuilder();
            fullAddress.append(address.getState()).append(" ")
                      .append(address.getCity()).append(" ");
            
            if (address.getAddressLine1() != null && !address.getAddressLine1().isEmpty()) {
                fullAddress.append(address.getAddressLine1()).append(" ");
            }
            
            if (address.getAddressLine2() != null && !address.getAddressLine2().isEmpty()) {
                fullAddress.append(address.getAddressLine2());
            }
            
            addressVO.setFullAddress(fullAddress.toString().trim());
            addressVOList.add(addressVO);
        }
        
        return addressVOList;
    }
    
    @Override
    public List<Address> getAddressesByUser(Integer userId) {
        // 调用Mapper获取用户地址列表
        return addressMapper.listByUserId(userId);
    }
    
    @Override
    public Address getAddress(String id, Integer userId) {
        // 根据ID查询地址
        Address address = addressMapper.selectById(id);
        // 验证地址是否存在及属于当前用户
        if (address != null && address.getUserId().equals(userId.toString())) {
            return address;
        }
        return null;
    }
    
    @Override
    @Transactional
    public Result<Object> addAddress(Integer userId, AddressDTO addressDTO) {
        Address address = new Address();
        BeanUtils.copyProperties(addressDTO, address);
        
        address.setAddressId(UUID.randomUUID().toString().replace("-", ""));
        address.setUserId(userId.toString());
        address.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // 如果是默认地址，则将其他地址设置为非默认
        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault()) {
            addressMapper.updateDefaultStatus(userId, null, false);
        }
        
        int result = addressMapper.insert(address);
        if (result > 0) {
            return Result.success("添加地址成功");
        } else {
            return Result.error("添加地址失败");
        }
    }
    
    @Override
    @Transactional
    public Address addAddress(AddressDTO addressDTO, Integer userId) {
        Address address = new Address();
        BeanUtils.copyProperties(addressDTO, address);
        
        address.setAddressId(UUID.randomUUID().toString().replace("-", ""));
        address.setUserId(userId.toString());
        address.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // 如果是默认地址，则将其他地址设置为非默认
        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault()) {
            addressMapper.updateDefaultStatus(userId, null, false);
        }
        
        // 插入数据库
        addressMapper.insert(address);
        return address;
    }
    
    @Override
    @Transactional
    public Result<Object> updateAddress(Integer userId, String addressId, AddressDTO addressDTO) {
        // 检查地址是否存在且属于当前用户
        Address existingAddress = addressMapper.selectById(addressId);
        if (existingAddress == null || !existingAddress.getUserId().equals(userId.toString())) {
            return Result.error("地址不存在或无权限修改");
        }
        
        Address address = new Address();
        BeanUtils.copyProperties(addressDTO, address);
        address.setAddressId(addressId);
        address.setUserId(userId.toString());
        
        // 如果设置为默认地址，则将其他地址设置为非默认
        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault() && 
            (existingAddress.getIsDefault() == null || !existingAddress.getIsDefault())) {
            addressMapper.updateDefaultStatus(userId, addressId, true);
        }
        
        int result = addressMapper.updateById(address);
        if (result > 0) {
            return Result.success("更新地址成功");
        } else {
            return Result.error("更新地址失败");
        }
    }
    
    @Override
    @Transactional
    public Address updateAddress(String id, AddressDTO addressDTO, Integer userId) {
        // 检查地址是否存在且属于当前用户
        Address existingAddress = addressMapper.selectById(id);
        if (existingAddress == null || !existingAddress.getUserId().equals(userId.toString())) {
            return null;
        }
        
        // 更新地址信息
        Address address = new Address();
        BeanUtils.copyProperties(addressDTO, address);
        address.setAddressId(id);
        address.setUserId(userId.toString());
        
        // 如果设置为默认地址，则将其他地址设置为非默认
        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault() && 
            (existingAddress.getIsDefault() == null || !existingAddress.getIsDefault())) {
            addressMapper.updateDefaultStatus(userId, id, false);
        }
        
        // 更新数据库
        addressMapper.updateById(address);
        return address;
    }
    
    @Override
    public Result<Object> deleteAddress(Integer userId, String addressId) {
        // 检查地址是否存在且属于当前用户
        Address existingAddress = addressMapper.selectById(addressId);
        if (existingAddress == null || !existingAddress.getUserId().equals(userId.toString())) {
            return Result.error("地址不存在或无权限删除");
        }
        
        int result = addressMapper.deleteById(addressId);
        if (result > 0) {
            return Result.success("删除地址成功");
        } else {
            return Result.error("删除地址失败");
        }
    }
    
    @Override
    public boolean deleteAddress(String id, Integer userId) {
        // 检查地址是否存在且属于当前用户
        Address existingAddress = addressMapper.selectById(id);
        if (existingAddress == null || !existingAddress.getUserId().equals(userId.toString())) {
            return false;
        }
        
        // 删除地址
        int result = addressMapper.deleteById(id);
        return result > 0;
    }
    
    @Override
    @Transactional
    public Result<Object> setDefaultAddress(Integer userId, String addressId) {
        // 检查地址是否存在且属于当前用户
        Address existingAddress = addressMapper.selectById(addressId);
        if (existingAddress == null || !existingAddress.getUserId().equals(userId.toString())) {
            return Result.error("地址不存在或无权限设置");
        }
        
        // 先将所有地址设置为非默认
        addressMapper.updateDefaultStatus(userId, null, false);
        
        // 将指定地址设置为默认
        int result = addressMapper.updateDefaultStatus(userId, addressId, true);
        if (result > 0) {
            return Result.success("设置默认地址成功");
        } else {
            return Result.error("设置默认地址失败");
        }
    }
    
    @Override
    @Transactional
    public boolean setDefaultAddress(String id, Integer userId) {
        // 检查地址是否存在且属于当前用户
        Address existingAddress = addressMapper.selectById(id);
        if (existingAddress == null || !existingAddress.getUserId().equals(userId.toString())) {
            return false;
        }
        
        // 先将所有地址设置为非默认
        addressMapper.updateDefaultStatus(userId, null, false);
        
        // 将指定地址设置为默认
        int result = addressMapper.updateDefaultStatus(userId, id, true);
        return result > 0;
    }
    
    @Override
    public AddressVO getDefaultAddress(Integer userId) {
        Address address = addressMapper.getDefaultAddress(userId);
        if (address == null) {
            return null;
        }
        
        AddressVO addressVO = new AddressVO();
        BeanUtils.copyProperties(address, addressVO);
        
        // 构建完整地址字符串
        StringBuilder fullAddress = new StringBuilder();
        fullAddress.append(address.getState()).append(" ")
                  .append(address.getCity()).append(" ");
        
        if (address.getAddressLine1() != null && !address.getAddressLine1().isEmpty()) {
            fullAddress.append(address.getAddressLine1()).append(" ");
        }
        
        if (address.getAddressLine2() != null && !address.getAddressLine2().isEmpty()) {
            fullAddress.append(address.getAddressLine2());
        }
        
        addressVO.setFullAddress(fullAddress.toString().trim());
        return addressVO;
    }
} 