package com.db.ecom_platform.controller;

import com.db.ecom_platform.entity.Address;
import com.db.ecom_platform.entity.dto.AddressDTO;
import com.db.ecom_platform.service.AddressService;
import com.db.ecom_platform.utils.Result;
import com.db.ecom_platform.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址控制器
 */
@Api(tags = "收货地址管理")
@RestController
@RequestMapping("/api/addresses")
public class AddressController {
    
    @Autowired
    private AddressService addressService;
    
    /**
     * 获取当前用户的所有地址
     */
    @ApiOperation(value = "获取地址列表", notes = "获取当前登录用户的所有收货地址")
    @GetMapping
    public Result<List<Address>> getUserAddresses() {
        Integer userId = UserUtils.getCurrentUserId();
        List<Address> addresses = addressService.getAddressesByUser(userId);
        return Result.success(addresses);
    }
    
    /**
     * 获取地址详情
     */
    @ApiOperation(value = "获取地址详情", notes = "获取指定ID的收货地址详情")
    @ApiImplicitParam(name = "id", value = "地址ID", required = true, dataTypeClass = String.class, paramType = "path")
    @GetMapping("/{id}")
    public Result<Address> getAddress(@PathVariable String id) {
        Integer userId = UserUtils.getCurrentUserId();
        Address address = addressService.getAddress(id, userId);
        if (address == null) {
            return Result.error("地址不存在或无权访问");
        }
        return Result.success(address);
    }
    
    /**
     * 添加新地址
     */
    @ApiOperation(value = "添加地址", notes = "为当前登录用户添加新的收货地址")
    @PostMapping
    public Result addAddress(@ApiParam(value = "地址信息", required = true) @RequestBody AddressDTO addressDTO) {
        Integer userId = UserUtils.getCurrentUserId();
        Address address = addressService.addAddress(addressDTO, userId);
        return Result.success(address);
    }
    
    /**
     * 更新地址
     */
    @ApiOperation(value = "更新地址", notes = "更新指定ID的收货地址信息")
    @ApiImplicitParam(name = "id", value = "地址ID", required = true, dataTypeClass = String.class, paramType = "path")
    @PutMapping("/{id}")
    public Result updateAddress(@PathVariable String id, @ApiParam(value = "地址信息", required = true) @RequestBody AddressDTO addressDTO) {
        Integer userId = UserUtils.getCurrentUserId();
        Address address = addressService.updateAddress(id, addressDTO, userId);
        if (address == null) {
            return Result.error("地址不存在或无权修改");
        }
        return Result.success(address);
    }
    
    /**
     * 删除地址
     */
    @ApiOperation(value = "删除地址", notes = "删除指定ID的收货地址")
    @ApiImplicitParam(name = "id", value = "地址ID", required = true, dataTypeClass = String.class, paramType = "path")
    @DeleteMapping("/{id}")
    public Result deleteAddress(@PathVariable String id) {
        Integer userId = UserUtils.getCurrentUserId();
        boolean success = addressService.deleteAddress(id, userId);
        if (!success) {
            return Result.error("地址不存在或无权删除");
        }
        return Result.success();
    }
    
    /**
     * 设置默认地址
     */
    @ApiOperation(value = "设置默认地址", notes = "将指定ID的地址设置为默认收货地址")
    @ApiImplicitParam(name = "id", value = "地址ID", required = true, dataTypeClass = String.class, paramType = "path")
    @PostMapping("/{id}/default")
    public Result setDefaultAddress(@PathVariable String id) {
        Integer userId = UserUtils.getCurrentUserId();
        boolean success = addressService.setDefaultAddress(id, userId);
        if (!success) {
            return Result.error("地址不存在或无权操作");
        }
        return Result.success();
    }
} 