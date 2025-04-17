package com.db.ecom_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.db.ecom_platform.entity.User;
import com.db.ecom_platform.entity.UserOperationLog;
import com.db.ecom_platform.entity.dto.UserQueryDTO;
import com.db.ecom_platform.entity.vo.UserDetailVO;
import com.db.ecom_platform.entity.vo.UserOperationLogVO;
import com.db.ecom_platform.entity.vo.UserVO;
import com.db.ecom_platform.mapper.UserMapper;
import com.db.ecom_platform.mapper.UserOperationLogMapper;
import com.db.ecom_platform.service.AdminUserService;
import com.db.ecom_platform.utils.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员用户服务实现类
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserOperationLogMapper operationLogMapper;
    
    @Override
    public Page<UserVO> getUserList(UserQueryDTO queryDTO, Integer page, Integer size) {
        // 创建分页对象
        Page<User> userPage = new Page<>(page, size);
        
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new QueryWrapper<User>().lambda();
        
        if (queryDTO != null) {
            if (queryDTO.getUsername() != null && !queryDTO.getUsername().isEmpty()) {
                queryWrapper.like(User::getUsername, queryDTO.getUsername());
            }
            if (queryDTO.getEmail() != null && !queryDTO.getEmail().isEmpty()) {
                queryWrapper.like(User::getEmail, queryDTO.getEmail());
            }
            if (queryDTO.getPhone() != null && !queryDTO.getPhone().isEmpty()) {
                queryWrapper.like(User::getPhone, queryDTO.getPhone());
            }
            if (queryDTO.getIsVip() != null) {
                queryWrapper.eq(User::getIsVip, queryDTO.getIsVip());
            }
            if (queryDTO.getRole() != null) {
                queryWrapper.eq(User::getRole, queryDTO.getRole());
            }
            if (queryDTO.getIsDisabled() != null) {
                queryWrapper.eq(User::getIsDisabled, queryDTO.getIsDisabled());
            }
            if (queryDTO.getStartTime() != null && !queryDTO.getStartTime().isEmpty() &&
                queryDTO.getEndTime() != null && !queryDTO.getEndTime().isEmpty()) {
                queryWrapper.between(User::getCreateTime, queryDTO.getStartTime(), queryDTO.getEndTime());
            }
        }
        
        // 查询分页数据
        Page<User> result = userMapper.selectPage(userPage, queryWrapper);
        
        // 转换为VO
        Page<UserVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<UserVO> voList = result.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }
    
    @Override
    public UserDetailVO getUserDetail(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        
        UserDetailVO detailVO = new UserDetailVO();
        BeanUtils.copyProperties(user, detailVO);
        
        // 查询用户消费总额和订单数量
        try {
            // 这里假设有一个查询用户消费总额和订单数量的方法
            // 在实际实现中，可能需要额外的查询来获取这些信息
            detailVO.setTotalConsumption(0.0); // 占位，实际需要从订单表计算
            detailVO.setOrderCount(0);         // 占位，实际需要从订单表计算
        } catch (Exception e) {
            detailVO.setTotalConsumption(0.0);
            detailVO.setOrderCount(0);
        }
        
        return detailVO;
    }
    
    @Override
    @Transactional
    public Result updateUserRole(Integer userId, Integer roleType) {
        // 验证角色类型
        if (roleType != 0 && roleType != 1) {
            return Result.error("无效的角色类型");
        }
        
        // 查询用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 更新用户角色
        User updateUser = new User();
        updateUser.setUserId(userId);
        updateUser.setRole(roleType);
        
        // 如果角色是VIP用户，同时更新isVip字段
        if (roleType == 1) {
            updateUser.setIsVip(true);
        } else {
            updateUser.setIsVip(false);
        }
        
        int result = userMapper.updateById(updateUser);
        if (result > 0) {
            return Result.success("更新用户角色成功");
        } else {
            return Result.error("更新用户角色失败");
        }
    }
    
    @Override
    @Transactional
    public Result toggleUserStatus(Integer userId, Boolean disabled) {
        // 查询用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 更新用户状态
        User updateUser = new User();
        updateUser.setUserId(userId);
        updateUser.setIsDisabled(disabled);
        
        int result = userMapper.updateById(updateUser);
        if (result > 0) {
            return Result.success(disabled ? "禁用用户成功" : "启用用户成功");
        } else {
            return Result.error(disabled ? "禁用用户失败" : "启用用户失败");
        }
    }
    
    @Override
    public Page<UserOperationLogVO> getOperationLogs(Integer userId, String operationType, String startTime, String endTime, Integer page, Integer size) {
        // 查询操作日志
        List<UserOperationLog> logs = operationLogMapper.queryOperationLogs(userId, operationType, startTime, endTime);
        
        // 分页处理
        int total = logs.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        
        List<UserOperationLog> pageData = fromIndex < toIndex ? logs.subList(fromIndex, toIndex) : new ArrayList<>();
        
        // 转换为VO
        List<UserOperationLogVO> voList = new ArrayList<>();
        for (UserOperationLog log : pageData) {
            UserOperationLogVO vo = new UserOperationLogVO();
            BeanUtils.copyProperties(log, vo);
            
            // 获取用户名
            User user = userMapper.selectById(log.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
            }
            
            voList.add(vo);
        }
        
        // 构建分页结果
        Page<UserOperationLogVO> voPage = new Page<>(page, size, total);
        voPage.setRecords(voList);
        
        return voPage;
    }
} 