package com.db.ecom_platform.config;

import com.db.ecom_platform.utils.JwtUtils;
import com.db.ecom_platform.utils.Result;
import com.db.ecom_platform.utils.UserUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT认证拦截器
 * 用于拦截请求并验证JWT令牌
 */
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;
    
    // 不需要认证的路径
    private final List<String> excludePaths = Arrays.asList(
            "/api/user/login",
            "/api/user/register", 
            "/api/user/code/send",
            "/swagger-ui",
            "/swagger-resources",
            "/v3/api-docs",
            "/v2/api-docs",
            "/webjars",
            "/static/auth.html",
            "/static/test-login.html",
            "/static/css",
            "/static/js",
            "/static/images",
            "/favicon.ico"
    );
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        System.out.println("Intercepting request to: " + path + ", Method: " + request.getMethod());
        
        // 记录更详细的请求信息
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");
        System.out.println("User-Agent: " + userAgent);
        System.out.println("Referer: " + referer);
        
        // 检查请求的referer是否来自auth.html
        boolean fromAuthPage = referer != null && referer.contains("auth.html");
        if (fromAuthPage) {
            System.out.println("Request from auth page detected");
        }
        
        // 获取查询参数，检查是否由auth页面跳转而来或带有token存在标记
        String queryString = request.getQueryString();
        boolean fromAuthParam = queryString != null && queryString.contains("from=auth");
        boolean hasTokenExistsFlag = queryString != null && queryString.contains("token_exists=true");
        
        if (fromAuthParam) {
            System.out.println("Request with auth redirect parameter detected");
        }
        
        if (hasTokenExistsFlag) {
            System.out.println("Request with token_exists flag detected, client indicates it has a token");
        }
        
        // 检查请求路径是否在排除路径中
        if (isExcludedPath(path)) {
            System.out.println("Path is excluded from authentication: " + path);
            return true;
        }
        
        // 检查是否是HTML页面请求（非auth.html和test-login.html）
        boolean isHtmlRequest = path.endsWith(".html") || path.equals("/") || path.equals("/static/") || path.endsWith("/static");
        System.out.println("Is HTML request: " + isHtmlRequest);
        
        // 从请求头中获取令牌
        String token = request.getHeader("Authorization");
        System.out.println("Authorization header: " + (token != null ? token.substring(0, Math.min(15, token.length())) + "..." : "null"));
        
        // 如果请求头中没有token，尝试从cookie中获取
        if (token == null || token.isEmpty()) {
            javax.servlet.http.Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (javax.servlet.http.Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName())) {
                        token = "Bearer " + cookie.getValue();
                        System.out.println("Token found in cookie: " + token.substring(0, Math.min(15, token.length())) + "...");
                        break;
                    }
                }
            }
        }
        
        // 记录所有请求头，帮助调试
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            System.out.println("All request headers:");
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                System.out.println("  " + headerName + ": " + request.getHeader(headerName));
            }
        }
        
        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            System.out.println("Invalid or missing token for path: " + path);
            
            // 如果请求是从auth页面过来的，或者带有token_exists标记，那么可能客户端有token但请求头中没有
            // 为避免循环重定向，允许通过，让前端JS处理认证
            if ((fromAuthPage || fromAuthParam || hasTokenExistsFlag) && isHtmlRequest) {
                System.out.println("Request with token indication but missing in header, allowing to pass for client-side handling");
                return true;
            }
            
            if (isHtmlRequest) {
                // 如果是HTML页面请求，直接重定向到登录页面
                System.out.println("Redirecting HTML request to auth.html");
                response.sendRedirect("/static/auth.html");
                return false;
            } else {
                // API请求，返回401错误
                System.out.println("Returning 401 for API request");
                handleUnauthorized(response, "未提供有效的认证令牌");
                return false;
            }
        }
        
        // 去除Bearer前缀
        token = token.substring(7);
        
        // 验证令牌
        boolean isValid = jwtUtils.validateToken(token);
        System.out.println("Token validation result: " + isValid);
        
        if (!isValid) {
            System.out.println("Invalid token for path: " + path);
            if (isHtmlRequest) {
                // 如果是HTML页面请求，直接重定向到登录页面
                System.out.println("Redirecting HTML request to auth.html due to invalid token");
                response.sendRedirect("/static/auth.html");
                return false;
            } else {
                // API请求，返回401错误
                System.out.println("Returning 401 for API request due to invalid token");
                handleUnauthorized(response, "认证令牌已过期或无效");
                return false;
            }
        }
        
        // 从令牌中获取用户ID并存储到ThreadLocal
        try {
            Integer userId = jwtUtils.getUserIdFromToken(token);
            System.out.println("Extracted userId from token: " + userId);
            UserUtils.setCurrentUserId(userId);
            return true;
        } catch (Exception e) {
            System.out.println("Error extracting userId from token: " + e.getMessage());
            if (isHtmlRequest) {
                // 如果是HTML页面请求，直接重定向到登录页面
                System.out.println("Redirecting HTML request to auth.html due to token parsing error");
                response.sendRedirect("/static/auth.html");
                return false;
            } else {
                // API请求，返回401错误
                System.out.println("Returning 401 for API request due to token parsing error");
                handleUnauthorized(response, "无法解析用户ID");
                return false;
            }
        }
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理ThreadLocal
        UserUtils.clearCurrentUserId();
    }
    
    /**
     * 处理未授权的请求
     */
    private void handleUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        Result<Object> result = Result.error(message);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
    }
    
    /**
     * 检查路径是否在排除列表中
     */
    private boolean isExcludedPath(String path) {
        // 直接检查auth.html和test-login.html
        if (path.contains("auth.html") || path.contains("test-login.html")) {
            System.out.println("Auth or test login path detected and excluded: " + path);
            return true;
        }
        
        // 检查css, js, images等静态资源
        if (path.contains("/css/") || path.contains("/js/") || path.contains("/images/") || 
            path.contains("/fonts/") || path.contains("/icons/") || 
            path.endsWith(".css") || path.endsWith(".js") || 
            path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".jpeg") || 
            path.endsWith(".gif") || path.endsWith(".svg") || path.endsWith(".ico")) {
            System.out.println("Static resource detected and excluded: " + path);
            return true;
        }
        
        // 检查其他排除路径
        boolean isExcluded = excludePaths.stream().anyMatch(path::startsWith);
        if (isExcluded) {
            System.out.println("Path is in excluded list: " + path);
        }
        return isExcluded;
    }
} 