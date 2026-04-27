package com.taskmanager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.common.Result;
import com.taskmanager.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Spring Security 安全配置类
 * 配置请求授权规则、JWT 过滤器链、密码编码器、认证入口点等
 *
 * @author taskmanager
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 启用 @PreAuthorize 注解权限控制
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, ObjectMapper objectMapper) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.objectMapper = objectMapper;
    }

    /**
     * 配置安全过滤链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 启用 CORS 支持（使用 CorsConfig 中定义的配置）
            .cors(cors -> {})
            // 禁用 CSRF（前后端分离，Token 认证无需 CSRF）
            .csrf(csrf -> csrf.disable())
            // 基于 Token 的无状态会话（不使用 Session）
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // 未认证请求处理：返回 401 而非默认的 403/重定向
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    Result<?> result = Result.error(401, "认证失败，请重新登录");
                    response.getWriter().write(objectMapper.writeValueAsString(result));
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    Result<?> result = Result.error(403, "没有权限，请联系管理员授权");
                    response.getWriter().write(objectMapper.writeValueAsString(result));
                })
            )
            // 请求授权配置 - 使用 AntPathRequestMatcher 避免路径解析问题
            .authorizeHttpRequests(auth -> auth
                // 放行认证相关接口（无需登录即可访问）
                .requestMatchers(new AntPathRequestMatcher("/api/auth/login")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/auth/register")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/auth/captcha")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/shop/products/**")).permitAll()
                // 放行 Knife4j API 文档
                .requestMatchers(new AntPathRequestMatcher("/doc.html")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/webjars/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/swagger-resources/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/favicon.ico")).permitAll()
                // 字典数据公开接口
                .requestMatchers(new AntPathRequestMatcher("/api/system/dict/data/type/*")).permitAll()
                // 其他所有接口需要认证
                .anyRequest().authenticated()
            )
            // 在 UsernamePasswordAuthenticationFilter 前添加 JWT 过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 密码编码器（BCrypt 加密算法，strength=10）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器（用于手动触发认证流程）
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
