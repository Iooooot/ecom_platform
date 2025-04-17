package com.db.ecom_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.db.ecom_platform.entity.UserLoginLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户登录日志数据访问接口
 */
@Mapper
public interface UserLoginLogMapper extends BaseMapper<UserLoginLog> {
    
    /**
     * 记录登录日志
     * @param loginLog 登录日志
     * @return 影响行数
     */
    int insertLoginLog(UserLoginLog loginLog);
    
    /**
     * 查询用户登录记录
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 登录记录列表
     */
    List<UserLoginLog> queryUserLoginLogs(@Param("userId") Integer userId, 
                                         @Param("startTime") String startTime, 
                                         @Param("endTime") String endTime);
} 