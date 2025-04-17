package com.db.ecom_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.db.ecom_platform.entity.VerificationCode;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

/**
 * 验证码Mapper接口
 */
@Mapper
public interface VerificationCodeMapper extends BaseMapper<VerificationCode> {
    
    /**
     * 根据目标查询验证码
     * @param target 目标（手机号或邮箱）
     * @return 验证码对象
     */
    @Select("SELECT * FROM verification_code WHERE target = #{target} ORDER BY create_time DESC LIMIT 1")
    VerificationCode getByTarget(String target);
    
    /**
     * 删除指定目标的验证码
     * @param target 目标（手机号或邮箱）
     * @return 影响的行数
     */
    @Delete("DELETE FROM verification_code WHERE target = #{target}")
    int deleteByTarget(String target);
    
    /**
     * 更新验证码使用状态
     * @param id 验证码ID
     * @param used 是否已使用
     * @return 影响的行数
     */
    @Update("UPDATE verification_code SET used = #{used} WHERE id = #{id}")
    int updateUsedStatus(Integer id, boolean used);
    
    /**
     * 删除过期的验证码
     * @param now 当前时间
     * @return 影响的行数
     */
    @Delete("DELETE FROM verification_code WHERE expiry_time < #{now}")
    int deleteExpiredCodes(LocalDateTime now);
} 