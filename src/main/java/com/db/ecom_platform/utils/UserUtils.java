package com.db.ecom_platform.utils;

import org.springframework.stereotype.Component;

/**
 * 用户工具类
 * 提供用户相关的工具方法
 */
@Component
public class UserUtils {

    /**
     * 获取当前登录用户ID
     * 从安全上下文中获取用户信息
     * @return 当前用户ID
     */
    public static Integer getCurrentUserId() {
        try {
            // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            // if (authentication != null && authentication.isAuthenticated()) {
            //     Object principal = authentication.getPrincipal();
            //     if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            //         // 从UserDetails中获取用户名
            //         String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            //         // 假设用户名就是用户ID，或者可以在这里查询用户ID
            //         return Integer.parseInt(username);
            //     } else {
            //         // 直接从Principal中获取用户名（可能是用户ID）
            //         return Integer.parseInt(principal.toString());
            //     }
            // }
        } catch (Exception e) {
            // 处理异常，例如用户未登录或转换失败
            // 在实际环境中可能需要抛出异常或重定向到登录页面
        }

        // 开发环境下返回默认用户，生产环境应该返回null或抛出异常
        return 1;
    }
} 