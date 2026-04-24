package com.taskmanager.controller;

import com.taskmanager.security.CaptchaService;
import com.taskmanager.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.domain.SysRole;
import com.taskmanager.domain.SysUser;
import com.taskmanager.security.LoginUser;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 控制器测试基类
 * 提供公共的 Mock 配置和工具方法
 *
 * @author taskmanager
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.data.redis.repositories.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration"
    })
@AutoConfigureMockMvc
public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected RedisConnectionFactory redisConnectionFactory;

    @MockBean
    protected RedisTemplate<String, Object> redisTemplate;

    @MockBean
    protected TokenService tokenService;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    /** 构建管理员登录用户（拥有全部权限） */
    protected LoginUser buildAdminLoginUser() {
        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setUserName("admin");
        user.setNickName("超级管理员");
        user.setPassword(passwordEncoder.encode("admin123"));
        user.setStatus("0");

        SysRole role = new SysRole();
        role.setRoleId(1L);
        role.setRoleKey("admin");
        role.setRoleName("超级管理员");

        Set<String> permissions = new HashSet<>();
        permissions.add("*:*:*");

        LoginUser loginUser = new LoginUser(user, permissions, Arrays.asList(role));
        loginUser.setToken("test-admin-token");
        return loginUser;
    }

    /** 模拟已认证请求 */
    protected void mockAuthentication(LoginUser loginUser) {
        when(tokenService.getLoginUser(anyString())).thenReturn(loginUser);
        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));
        when(((ValueOperations) redisTemplate.opsForValue()).get(anyString())).thenReturn(loginUser);
    }
}
