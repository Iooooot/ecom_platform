package com.db.ecom_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.db.ecom_platform.entity.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 地址数据访问接口
 */
@Mapper
public interface AddressMapper extends BaseMapper<Address> {
    
    /**
     * 查询用户所有地址
     * @param userId 用户ID
     * @return 地址列表
     */
    List<Address> listByUserId(Integer userId);
    
    /**
     * 查询默认地址
     * @param userId 用户ID
     * @return 默认地址
     */
    Address getDefaultAddress(Integer userId);
    
    /**
     * 更新默认地址状态
     * @param userId 用户ID
     * @param addressId 地址ID
     * @param isDefault 是否为默认地址
     * @return 影响行数
     */
    int updateDefaultStatus(@Param("userId") Integer userId, 
                           @Param("addressId") String addressId, 
                           @Param("isDefault") Boolean isDefault);
} 