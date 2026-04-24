package com.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.common.constant.Constants;
import com.taskmanager.domain.SysMenu;
import com.taskmanager.domain.SysRole;
import com.taskmanager.domain.SysUser;
import com.taskmanager.mapper.SysMenuMapper;
import com.taskmanager.mapper.SysRoleMapper;
import com.taskmanager.security.CaptchaService;
import com.taskmanager.security.LoginUser;
import com.taskmanager.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 登录认证控制器单元测试
 * 测试验证码、登录、登出、获取用户信息、获取路由等接口
 *
 * @author taskmanager
 */
@SpringBootTest(properties = {
    "spring.data.redis.repositories.enabled=false",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration"
})
@AutoConfigureMockMvc
class SysLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private CaptchaService captchaService;

    @MockBean
    private SysMenuMapper menuMapper;

    @MockBean
    private SysRoleMapper roleMapper;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** 构建测试用登录用户 */
    private LoginUser buildTestLoginUser() {
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
        loginUser.setToken("test-token-123");
        return loginUser;
    }

    /** 构建认证对象 */
    private UsernamePasswordAuthenticationToken buildAuthentication(LoginUser loginUser) {
        return new UsernamePasswordAuthenticationToken(
            loginUser, null, loginUser.getAuthorities()
        );
    }

    @Test
    @DisplayName("获取验证码 - 成功")
    void getCaptcha_success() throws Exception {
        CaptchaService.CaptchaVO captchaVO = new CaptchaService.CaptchaVO("test-uuid", "base64ImageData");
        when(captchaService.createCaptcha()).thenReturn(captchaVO);

        mockMvc.perform(get("/api/auth/captcha"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.uuid").value("test-uuid"));
    }

    @Test
    @DisplayName("用户登录 - 成功")
    void login_success() throws Exception {
        LoginUser loginUser = buildTestLoginUser();
        Authentication auth = buildAuthentication(loginUser);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(auth);
        when(tokenService.createToken(any(LoginUser.class))).thenReturn("test-token-123");

        mockMvc.perform(post("/api/auth/login")
                .param("userName", "admin")
                .param("password", "admin123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.token").value("test-token-123"))
            .andExpect(jsonPath("$.data.userName").value("admin"));
    }

    @Test
    @DisplayName("用户登录 - 带验证码成功")
    void login_withCaptcha_success() throws Exception {
        LoginUser loginUser = buildTestLoginUser();
        Authentication auth = buildAuthentication(loginUser);

        doNothing().when(captchaService).validateCaptcha(anyString(), anyString());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(auth);
        when(tokenService.createToken(any(LoginUser.class))).thenReturn("test-token-123");

        mockMvc.perform(post("/api/auth/login")
                .param("userName", "admin")
                .param("password", "admin123")
                .param("uuid", "test-uuid")
                .param("code", "ABCD"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.token").value("test-token-123"));
    }

    @Test
    @DisplayName("用户登录 - 验证码错误")
    void login_captchaError() throws Exception {
        doThrow(new RuntimeException("验证码错误")).when(captchaService).validateCaptcha(anyString(), anyString());

        mockMvc.perform(post("/api/auth/login")
                .param("userName", "admin")
                .param("password", "admin123")
                .param("uuid", "test-uuid")
                .param("code", "WRONG"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(500))
            .andExpect(jsonPath("$.message").value("验证码错误"));
    }

    @Test
    @DisplayName("用户登出 - 成功")
    void logout_success() throws Exception {
        LoginUser loginUser = buildTestLoginUser();
        UsernamePasswordAuthenticationToken auth = buildAuthentication(loginUser);

        when(tokenService.getLoginUser(anyString())).thenReturn(loginUser);
        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));
        when(((ValueOperations) redisTemplate.opsForValue()).get(anyString())).thenReturn(loginUser);

        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer test-token-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取当前用户信息 - 成功")
    void getInfo_success() throws Exception {
        LoginUser loginUser = buildTestLoginUser();

        when(tokenService.getLoginUser(anyString())).thenReturn(loginUser);
        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));
        when(((ValueOperations) redisTemplate.opsForValue()).get(anyString())).thenReturn(loginUser);

        mockMvc.perform(get("/api/auth/getInfo")
                .header("Authorization", "Bearer test-token-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.user.userName").value("admin"))
            .andExpect(jsonPath("$.data.roles[0]").value("admin"))
            .andExpect(jsonPath("$.data.permissions").isArray());
    }

    @Test
    @DisplayName("获取路由信息 - 管理员获取全部菜单")
    void getRouters_admin_success() throws Exception {
        LoginUser loginUser = buildTestLoginUser();

        when(tokenService.getLoginUser(anyString())).thenReturn(loginUser);
        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));
        when(((ValueOperations) redisTemplate.opsForValue()).get(anyString())).thenReturn(loginUser);

        // 模拟菜单数据
        SysMenu menu = new SysMenu();
        menu.setMenuId(1L);
        menu.setMenuName("系统管理");
        menu.setParentId(0L);
        menu.setMenuType("M");
        menu.setPath("system");
        menu.setComponent(null);
        menu.setIcon("system");
        menu.setVisible("0");
        menu.setIsFrame(1);
        when(menuMapper.selectMenuTreeAll()).thenReturn(List.of(menu));

        mockMvc.perform(get("/api/auth/getRouters")
                .header("Authorization", "Bearer test-token-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].name").value("系统管理"))
            .andExpect(jsonPath("$.data[0].component").value("Layout"));
    }

    @Test
    @DisplayName("获取路由信息 - 普通用户获取分配菜单")
    void getRouters_normalUser_success() throws Exception {
        // 构造普通用户
        SysUser user = new SysUser();
        user.setUserId(2L);
        user.setUserName("testuser");
        user.setNickName("测试用户");
        user.setPassword(passwordEncoder.encode("test123"));
        user.setStatus("0");

        SysRole role = new SysRole();
        role.setRoleId(2L);
        role.setRoleKey("common");
        role.setRoleName("普通角色");

        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list");
        LoginUser loginUser = new LoginUser(user, permissions, Arrays.asList(role));
        loginUser.setToken("test-token-common");

        when(tokenService.getLoginUser(anyString())).thenReturn(loginUser);
        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));
        when(((ValueOperations) redisTemplate.opsForValue()).get(anyString())).thenReturn(loginUser);

        SysMenu menu = new SysMenu();
        menu.setMenuId(2L);
        menu.setMenuName("用户管理");
        menu.setParentId(1L);
        menu.setMenuType("C");
        menu.setPath("user");
        menu.setComponent("system/user/index");
        menu.setIcon("user");
        menu.setVisible("0");
        menu.setIsFrame(1);
        when(menuMapper.selectMenuTreeByUserId(2L)).thenReturn(List.of(menu));

        mockMvc.perform(get("/api/auth/getRouters")
                .header("Authorization", "Bearer test-token-common"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("未认证访问受保护接口 - 返回401")
    void unauthenticated_accessProtectedEndpoint() throws Exception {
        when(tokenService.getLoginUser(anyString())).thenReturn(null);

        mockMvc.perform(get("/api/auth/getInfo"))
            .andExpect(status().isUnauthorized());
    }
}
