package com.db.ecom_platform.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT工具类
 * 用于生成和验证JWT令牌
 */
@Component
public class JwtUtils {

    // 默认密钥，可在配置中覆盖
    private String secret = "ecomPlatformSecretKey";
    
    // 令牌过期时间（毫秒），默认24小时，可在配置中覆盖
    private long expiration = 86400000;
    
    @Value("${jwt.secret:ecomPlatformSecretKey}")
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    @Value("${jwt.expiration:86400000}")
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
    
    /**
     * 从令牌中获取用户ID
     */
    public Integer getUserIdFromToken(String token) {
        String userId = getClaimFromToken(token, Claims::getSubject);
        return Integer.parseInt(userId);
    }
    
    /**
     * 获取令牌过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    /**
     * 从令牌中获取指定声明
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * 解析令牌获取所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
    
    /**
     * 检查令牌是否过期
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    /**
     * 为指定用户ID生成令牌
     */
    public String generateToken(Integer userId) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userId.toString());
    }
    
    /**
     * 生成令牌的核心方法
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    
    /**
     * 验证令牌
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
} 