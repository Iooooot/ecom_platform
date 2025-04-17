package com.db.ecom_platform.controller;

import com.db.ecom_platform.entity.dto.UserQueryDTO;
import com.db.ecom_platform.entity.vo.UserDetailVO;
import com.db.ecom_platform.entity.vo.UserOperationLogVO;
import com.db.ecom_platform.entity.vo.UserVO;
import com.db.ecom_platform.service.AdminUserService;
import com.db.ecom_platform.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 管理员用户控制器
 */
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
    @PutMapping("/{userId}/status")
    public Result toggleUserStatus(
            @PathVariable Integer userId,
            @RequestParam Boolean disabled) {
        return adminUserService.toggleUserStatus(userId, disabled);
    }
    
    /**
     * 获取操作日志
     * @param userId 用户ID（可选）
     * @param operationType 操作类型（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param page 页码
     * @param size 每页大小
     */
    @GetMapping("/logs")
    public Result getOperationLogs(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<UserOperationLogVO> logs = adminUserService.getOperationLogs(
                userId, operationType, startTime, endTime, page, size);
        return Result.success(logs);
    }
} 