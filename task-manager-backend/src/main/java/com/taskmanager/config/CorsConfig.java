package com.taskmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS 跨域配置
 * 允许前端开发服务器跨域访问后端 API
 *
 * @author taskmanager
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许携带 Cookie 等凭证信息
        config.setAllowCredentials(true);
        // 生产环境应限制为前端部署域名列表，禁止通配符
        String[] allowedOrigins = {
            "http://localhost:3000",
            "http://localhost:5173"
        };
        for (String origin : allowedOrigins) {
            config.addAllowedOrigin(origin);
        }
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许所有请求方法
        config.addAllowedMethod("*");
        // 预检请求缓存时间（秒）
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
