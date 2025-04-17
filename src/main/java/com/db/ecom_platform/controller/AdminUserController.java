package com.db.ecom_platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.db.ecom_platform.entity.dto.UserQueryDTO;
import com.db.ecom_platform.entity.vo.UserDetailVO;
import com.db.ecom_platform.entity.vo.UserVO;
import com.db.ecom_platform.service.AdminUserService;
import com.db.ecom_platform.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员用户控制器
 */
@Api(tags = "管理员用户管理")
@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {
    
    @Autowired
    private AdminUserService adminUserService;
    
    /**
     * 获取用户列表
     * @param queryDTO 查询条件
     * @param page 页码
     * @param size 每页大小
     */
    @ApiOperation(value = "获取用户列表", notes = "根据查询条件分页获取用户列表，包含用户ID、注册时间、消费总额等信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "page", value = "页码", required = true, defaultValue = "1", paramType = "query", dataTypeClass = Integer.class),
        @ApiImplicitParam(name = "size", value = "每页大小", required = true, defaultValue = "10", paramType = "query", dataTypeClass = Integer.class)
    })
    @GetMapping("/list")
    public Result getUserList(
            UserQueryDTO queryDTO,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<UserVO> userList = adminUserService.getUserList(queryDTO, page, size);
        return Result.success(userList);
    }
    
    /**
     * 获取用户详情
     * @param userId 用户ID
     */
    @ApiOperation(value = "获取用户详情", notes = "根据用户ID获取用户详细信息")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "path", dataTypeClass = Integer.class)
    @GetMapping("/{userId}")
    public Result getUserDetail(@PathVariable Integer userId) {
        UserDetailVO userDetail = adminUserService.getUserDetail(userId);
        return Result.success(userDetail);
    }
    
    /**
     * 更新用户角色
     * @param userId 用户ID
     * @param roleType 角色类型（0-普通用户，1-VIP用户）
     */
    @ApiOperation(value = "更新用户角色", notes = "更新用户角色，0-普通用户，1-VIP用户")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "path", dataTypeClass = Integer.class),
        @ApiImplicitParam(name = "roleType", value = "角色类型", required = true, paramType = "query", dataTypeClass = Integer.class, 
            allowableValues = "0,1", example = "0")
    })
    @PutMapping("/{userId}/role")
    public Result updateUserRole(
            @PathVariable Integer userId,
            @RequestParam Integer roleType) {
        return adminUserService.updateUserRole(userId, roleType);
    }
    
    /**
     * 禁用/启用用户账号
     * @param userId 用户ID
     * @param disabled 是否禁用
     */
    @ApiOperation(value = "禁用/启用用户账号", notes = "禁用或启用用户账号")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "path", dataTypeClass = Integer.class),
        @ApiImplicitParam(name = "disabled", value = "是否禁用", required = true, paramType = "query", dataTypeClass = Boolean.class)
    })
    @PutMapping("/{userId}/status")
    public Result toggleUserStatus(
            @PathVariable Integer userId,
            @RequestParam Boolean disabled) {
        return adminUserService.toggleUserStatus(userId, disabled);
    }
} 