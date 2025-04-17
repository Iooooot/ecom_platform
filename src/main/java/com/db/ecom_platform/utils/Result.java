package com.db.ecom_platform.utils;

import lombok.Data;

/**
 * 通用响应结果包装类
 */
@Data
public class Result {
    
    private Boolean success; // 是否成功
    private Integer code;    // 状态码
    private String message;  // 提示信息
    private Object data;     // 数据
    
    /**
     * 构造函数私有化，使用静态方法创建实例
     */
    private Result() {}
    
    /**
     * 成功返回结果
     * @return Result
     */
    public static Result success() {
        Result result = new Result();
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
    public static Result success(Object data) {
        Result result = new Result();
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
    public static Result success(String message, Object data) {
        Result result = new Result();
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
    public static Result error() {
        Result result = new Result();
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
    public static Result error(String message) {
        Result result = new Result();
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
    public static Result error(Integer code, String message) {
        Result result = new Result();
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
    public static Result validateFailed(String message) {
        Result result = new Result();
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
    public static Result unauthorized(String message) {
        Result result = new Result();
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
    public static Result forbidden(String message) {
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(403);
        result.setMessage(message);
        return result;
    }
} 