package com.taskmanager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 * 从请求头提取 Token，从 Redis 获取用户信息，构建 Security 认证对象
 *
 * @author taskmanager
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.prefix}")
    private String prefix;

    private final TokenService tokenService;

    public JwtAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. 从请求头获取 Token
        String token = getToken(request);
        if (StringUtils.hasText(token)) {
            // 2. 从 Redis 解析用户信息
            LoginUser loginUser = tokenService.getLoginUser(token);
            if (loginUser != null && loginUser.getUser() != null) {
                // 3. 构建认证令牌（已通过 Token 验证身份）
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                // 4. 设置到 Security 上下文
                SecurityContextHolder.getContext().setAuthentication(authToken);
                // 5. 自动续期
                tokenService.refreshToken(loginUser);
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头提取纯 Token 字符串
     */
    private String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(header);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(prefix + " ")) {
            return bearerToken.substring(prefix.length() + 1);
        }
        return null;
    }
}
