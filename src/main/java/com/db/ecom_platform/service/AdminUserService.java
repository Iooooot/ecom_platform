package com.db.ecom_platform.service;

import com.db.ecom_platform.entity.dto.UserQueryDTO;
import com.db.ecom_platform.entity.vo.UserDetailVO;
import com.db.ecom_platform.entity.vo.UserOperationLogVO;
import com.db.ecom_platform.entity.vo.UserVO;
import com.db.ecom_platform.utils.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 管理员用户服务接口
 */
public interface AdminUserService {
    
    /**
     * 获取用户列表
     * @param queryDTO 查询条件
     * @param page 页码
     * @param size 每页大小
     * @return 用户列表分页结果
     */
    Page<UserVO> getUserList(UserQueryDTO queryDTO, Integer page, Integer size);
    
    /**
     * 获取用户详情
     * @param userId 用户ID
     * @return 用户详情
     */
    UserDetailVO getUserDetail(Integer userId);
    
    /**
     * 更新用户角色
     * @param userId 用户ID
     * @param roleType 角色类型（0-普通用户，1-VIP用户）
     * @return 更新结果
     */
    Result updateUserRole(Integer userId, Integer roleType);
    
    /**
     * 禁用/启用用户账号
     * @param userId 用户ID
     * @param disabled 是否禁用
     * @return 操作结果
     */
    Result toggleUserStatus(Integer userId, Boolean disabled);
    
    /**
     * 获取操作日志
     * @param userId 用户ID（可选）
     * @param operationType 操作类型（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 操作日志分页结果
     */
    Page<UserOperationLogVO> getOperationLogs(Integer userId, String operationType, 
                                             String startTime, String endTime, 
                                             Integer page, Integer size);
} 