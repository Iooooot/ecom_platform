package com.db.ecom_platform.utils;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户工具类
 * 提供用户相关的工具方法
 */
@Component
public class UserUtils {

    // 使用ThreadLocal存储当前登录用户的ID
    private static final ThreadLocal<Integer> currentUserIdThreadLocal = new ThreadLocal<>();

    /**
     * 获取当前登录用户ID
     * 从ThreadLocal中获取用户ID
     * @return 当前用户ID
     */
    public static Integer getCurrentUserId() {
        return currentUserIdThreadLocal.get();
    }
    
    /**
     * 设置当前登录用户ID到ThreadLocal
     * @param userId 用户ID
     */
    public static void setCurrentUserId(Integer userId) {
        currentUserIdThreadLocal.set(userId);
    }
    
    /**
     * 清除ThreadLocal中的用户ID
     * 防止内存泄漏，在请求完成后调用
     */
    public static void clearCurrentUserId() {
        currentUserIdThreadLocal.remove();
    }

    /**
     * 获取客户端IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
} 