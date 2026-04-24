package com.taskmanager.security;

import com.taskmanager.common.constant.Constants;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Token 服务类
 * 管理 Redis 中的用户会话（Token 创建/刷新/删除/验证）
 *
 * @author taskmanager
 */
@Component
public class TokenService {

    /** Token 有效期（毫秒），从配置文件读取 */
    @Value("${jwt.expiration}")
    private long expiration;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 创建 Token 并将用户信息存入 Redis
     *
     * @param loginUser 登录用户信息
     * @return 生成的 Token（UUID 格式）
     */
    public String createToken(LoginUser loginUser) {
        String token = UUID.randomUUID().toString().replace("-", "");
        loginUser.setToken(token);
        // 将 LoginUser 对象存入 Redis，设置过期时间
        String tokenKey = getTokenKey(token);
        redisTemplate.opsForValue().set(tokenKey, loginUser, expiration, TimeUnit.MILLISECONDS);
        return token;
    }

    /**
     * 从 Redis 获取登录用户信息
     *
     * @param token 用户 Token
     * @return 登录用户信息，不存在则返回 null
     */
    public LoginUser getLoginUser(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        try {
            Object obj = redisTemplate.opsForValue().get(getTokenKey(token));
            if (obj instanceof LoginUser) {
                return (LoginUser) obj;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 刷新 Token 过期时间（每次有效请求自动续期）
     */
    public void refreshToken(LoginUser loginUser) {
        if (loginUser != null && loginUser.getToken() != null) {
            redisTemplate.expire(getTokenKey(loginUser.getToken()), expiration, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 删除 Redis 中的用户身份信息（登出时调用）
     */
    public void delLoginUser(String token) {
        if (token != null && !token.isEmpty()) {
            redisTemplate.delete(getTokenKey(token));
        }
    }

    /**
     * 构建 Redis Key
     */
    private String getTokenKey(String token) {
        return Constants.LOGIN_TOKEN_KEY + token;
    }
}
