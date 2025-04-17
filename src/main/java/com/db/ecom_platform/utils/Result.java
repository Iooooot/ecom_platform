package com.db.ecom_platform.utils;

import lombok.Data;

/**
 * 通用响应结果包装类
 * @param <T> 数据类型
 */
@Data
public class Result<T> {
    
    private Boolean success; // 是否成功
    private Integer code;    // 状态码
    private String message;  // 提示信息
    private T data;          // 数据
    
    /**
     * 构造函数私有化，使用静态方法创建实例
     */
    private Result() {}
    
    /**
     * 成功返回结果
     * @return Result
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(200);
        result.setMessage("操作成功");
        return result;
    }
    
    /**
     * 成功返回结果
     * @param data 返回数据
     * @return Result
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }
    
    /**
     * 成功返回结果
     * @param message 提示信息
     * @param data 返回数据
     * @return Result
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
    
    /**
     * 失败返回结果
     * @return Result
     */
    public static <T> Result<T> error() {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(500);
        result.setMessage("操作失败");
        return result;
    }
    
    /**
     * 失败返回结果
     * @param message 提示信息
     * @return Result
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(500);
        result.setMessage(message);
        return result;
    }
    
    /**
     * 失败返回结果
     * @param code 状态码
     * @param message 提示信息
     * @return Result
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    
    /**
     * 参数验证失败返回结果
     * @param message 提示信息
     * @return Result
     */
    public static <T> Result<T> validateFailed(String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(400);
        result.setMessage(message);
        return result;
    }
    
    /**
     * 未登录返回结果
     * @param message 提示信息
     * @return Result
     */
    public static <T> Result<T> unauthorized(String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(401);
        result.setMessage(message);
        return result;
    }
    
    /**
     * 未授权返回结果
     * @param message 提示信息
     * @return Result
     */
    public static <T> Result<T> forbidden(String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(403);
        result.setMessage(message);
        return result;
    }
} 