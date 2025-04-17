package com.db.ecom_platform.config;

import com.db.ecom_platform.utils.UserUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户ThreadLocal拦截器
 * 用于清理请求完成后的ThreadLocal，防止内存泄漏
 */
@Component
public class UserThreadLocalInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束后清理ThreadLocal
        UserUtils.clearCurrentUserId();
    }
} 