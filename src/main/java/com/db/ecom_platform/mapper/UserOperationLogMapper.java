package com.db.ecom_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.db.ecom_platform.entity.UserOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户操作日志数据访问接口
 */
@Mapper
public interface UserOperationLogMapper extends BaseMapper<UserOperationLog> {
    
    /**
     * 记录操作日志
     * @param operationLog 操作日志
     * @return 影响行数
     */
    int insertOperationLog(UserOperationLog operationLog);
    
    /**
     * 查询操作日志
     * @param userId 用户ID（可选）
     * @param operationType 操作类型（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 操作日志列表
     */
    List<UserOperationLog> queryOperationLogs(@Param("userId") Integer userId, 
                                             @Param("operationType") String operationType, 
                                             @Param("startTime") String startTime, 
                                             @Param("endTime") String endTime);
} 