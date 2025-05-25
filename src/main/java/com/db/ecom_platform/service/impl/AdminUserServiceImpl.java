package com.db.ecom_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.db.ecom_platform.entity.User;
import com.db.ecom_platform.entity.dto.UserQueryDTO;
import com.db.ecom_platform.entity.vo.UserDetailVO;
import com.db.ecom_platform.entity.vo.UserVO;
import com.db.ecom_platform.mapper.UserMapper;
import com.db.ecom_platform.service.AdminUserService;
import com.db.ecom_platform.utils.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员用户服务实现类
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Page<UserVO> getUserList(UserQueryDTO queryDTO, Integer page, Integer size) {
        // 创建分页对象
        Page<User> userPage = new Page<>(page, size);

        // 构建查询条件 - 使用普通 QueryWrapper 替代 LambdaQueryWrapper
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        if (queryDTO != null) {
            if (queryDTO.getUsername() != null && !queryDTO.getUsername().isEmpty()) {
                queryWrapper.like("username", queryDTO.getUsername());
            }
            if (queryDTO.getEmail() != null && !queryDTO.getEmail().isEmpty()) {
                queryWrapper.like("email", queryDTO.getEmail());
            }
            if (queryDTO.getPhone() != null && !queryDTO.getPhone().isEmpty()) {
                queryWrapper.like("phone", queryDTO.getPhone());
            }
            if (queryDTO.getIsVip() != null) {
                queryWrapper.eq("is_vip", queryDTO.getIsVip());
            }
            if (queryDTO.getRole() != null) {
                queryWrapper.eq("role", queryDTO.getRole());
            }
            if (queryDTO.getIsDisabled() != null) {
                queryWrapper.eq("is_disabled", queryDTO.getIsDisabled());
            }
            if (queryDTO.getCreateTime() != null && !queryDTO.getCreateTime().isEmpty()) {
                queryWrapper.ge("create_time", queryDTO.getCreateTime());
            }
        }

        // 查询分页数据
        Page<User> result = userMapper.selectPage(userPage, queryWrapper);

        // 转换为VO
        Page<UserVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<UserVO> voList = result.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);

            // 查询用户消费总额和订单数量
            try {
                // 这里获取用户消费总额和订单数量
                userVO.setTotalConsumption(getUserTotalConsumption(user.getUserId()));
                userVO.setOrderCount(getUserOrderCount(user.getUserId()));
            } catch (Exception e) {
                userVO.setTotalConsumption(0.0);
                userVO.setOrderCount(0);
            }

            return userVO;
        }).collect(Collectors.toList());

        // 根据消费总额过滤用户列表
        if (queryDTO != null) {
            if (queryDTO.getMinTotalConsumption() != null || queryDTO.getMaxTotalConsumption() != null) {
                voList = voList.stream().filter(userVO -> {
                    Double totalConsumption = userVO.getTotalConsumption();
                    if (totalConsumption == null) {
                        totalConsumption = 0.0;
                    }

                    boolean match = true;
                    if (queryDTO.getMinTotalConsumption() != null) {
                        match = match && totalConsumption >= queryDTO.getMinTotalConsumption();
                    }
                    if (queryDTO.getMaxTotalConsumption() != null) {
                        match = match && totalConsumption <= queryDTO.getMaxTotalConsumption();
                    }
                    return match;
                }).collect(Collectors.toList());

                // 重新计算总记录数
                voPage.setTotal(voList.size());
            }
        }

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
            detailVO.setTotalConsumption(getUserTotalConsumption(userId));
            detailVO.setOrderCount(getUserOrderCount(userId));
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

    /**
     * 获取用户消费总额
     * @param userId 用户ID
     * @return 消费总额
     */
    private Double getUserTotalConsumption(Integer userId) {
        try {
            // 调用不带createTime参数的重载方法
            Double amount = userMapper.getUserTotalConsumption(userId,null);
            return amount != null ? amount : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * 获取用户订单数量
     * @param userId 用户ID
     * @return 订单数量
     */
    private Integer getUserOrderCount(Integer userId) {
        try {
            // 调用不带createTime参数的重载方法
            Integer count = userMapper.getUserOrderCount(userId);
            return count != null ? count : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 备选的获取用户列表实现方法，使用自定义XML查询
     * 如果当前版本不工作，可以切换到这个方法
     */
    public Page<UserVO> getUserListPageCustom(UserQueryDTO queryDTO, Integer page, Integer size) {
        // 手动分页查询
        List<User> users = userMapper.getUserListPage(queryDTO, (page - 1) * size, size);

        // 手动转换为VO
        List<UserVO> voList = users.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);

            // 查询用户消费总额和订单数量
            try {
                // 这里获取用户消费总额和订单数量
                userVO.setTotalConsumption(getUserTotalConsumption(user.getUserId()));
                userVO.setOrderCount(getUserOrderCount(user.getUserId()));
            } catch (Exception e) {
                userVO.setTotalConsumption(0.0);
                userVO.setOrderCount(0);
            }

            return userVO;
        }).collect(Collectors.toList());

        // 创建分页对象
        Page<UserVO> voPage = new Page<>(page, size);
        voPage.setRecords(voList);
        voPage.setTotal(userMapper.selectCount(null)); // 获取总记录数

        return voPage;
    }
}