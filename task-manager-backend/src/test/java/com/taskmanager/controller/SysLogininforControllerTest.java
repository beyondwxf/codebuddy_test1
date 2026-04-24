package com.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.domain.SysLogininfor;
import com.taskmanager.domain.SysUser;
import com.taskmanager.domain.SysRole;
import com.taskmanager.mapper.SysLogininforMapper;
import com.taskmanager.security.LoginUser;
import com.taskmanager.security.TokenService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 登录日志控制器单元测试
 * 测试登录日志列表、详情、批量删除、清空、账号解锁等接口
 *
 * @author taskmanager
 */
@SpringBootTest(properties = {
    "spring.data.redis.repositories.enabled=false",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration"
})
@AutoConfigureMockMvc
class SysLogininforControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SysLogininforMapper logininforMapper;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private LoginUser buildAdminLoginUser() {
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

    private void mockAuthentication(LoginUser loginUser) {
        when(tokenService.getLoginUser(anyString())).thenReturn(loginUser);
        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));
        when(((ValueOperations) redisTemplate.opsForValue()).get(anyString())).thenReturn(loginUser);
    }

    @Test
    @DisplayName("获取登录日志列表 - 成功")
    void list_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        Page<SysLogininfor> page = new Page<>(1, 10);
        SysLogininfor logininfor = new SysLogininfor();
        logininfor.setInfoId(1L);
        logininfor.setUserName("admin");
        logininfor.setIpaddr("127.0.0.1");
        logininfor.setStatus("0");
        logininfor.setMsg("登录成功");
        page.setRecords(List.of(logininfor));
        page.setTotal(1);

        when(logininforMapper.selectPage(any(Page.class), any())).thenReturn(page);

        mockMvc.perform(get("/api/monitor/logininfor/list")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").value(1))
            .andExpect(jsonPath("$.data.rows[0].userName").value("admin"));
    }

    @Test
    @DisplayName("获取登录日志列表 - 带条件筛选")
    void list_withFilters() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        Page<SysLogininfor> page = new Page<>(1, 10);
        page.setRecords(new ArrayList<>());
        page.setTotal(0);

        when(logininforMapper.selectPage(any(Page.class), any())).thenReturn(page);

        mockMvc.perform(get("/api/monitor/logininfor/list")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .param("userName", "admin")
                .param("ipaddr", "127.0.0")
                .param("status", "0")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    @DisplayName("获取登录日志详情 - 成功")
    void getInfo_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysLogininfor logininfor = new SysLogininfor();
        logininfor.setInfoId(1L);
        logininfor.setUserName("admin");
        logininfor.setIpaddr("127.0.0.1");
        logininfor.setBrowser("Chrome");
        logininfor.setOs("Windows 10");
        logininfor.setStatus("0");
        logininfor.setMsg("登录成功");
        when(logininforMapper.selectById(1L)).thenReturn(logininfor);

        mockMvc.perform(get("/api/monitor/logininfor/1")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.userName").value("admin"))
            .andExpect(jsonPath("$.data.browser").value("Chrome"));
    }

    @Test
    @DisplayName("批量删除登录日志 - 成功")
    void remove_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        when(logininforMapper.deleteById(anyLong())).thenReturn(1);

        mockMvc.perform(delete("/api/monitor/logininfor/1,2,3")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("清空登录日志 - 成功")
    void clean_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        when(logininforMapper.delete(any())).thenReturn(50);

        mockMvc.perform(delete("/api/monitor/logininfor/clean")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("账号解锁 - 成功（预留接口）")
    void unlock_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        mockMvc.perform(get("/api/monitor/logininfor/unlock/admin")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("无权限访问登录日志 - 返回403")
    void list_noPermission_forbidden() throws Exception {
        SysUser user = new SysUser();
        user.setUserId(3L);
        user.setUserName("noperm");
        user.setNickName("无权限用户");
        user.setPassword(passwordEncoder.encode("test123"));
        user.setStatus("0");

        Set<String> permissions = new HashSet<>();
        LoginUser loginUser = new LoginUser(user, permissions, Collections.emptyList());
        loginUser.setToken("no-perm-token");
        mockAuthentication(loginUser);

        mockMvc.perform(get("/api/monitor/logininfor/list")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .header("Authorization", "Bearer no-perm-token"))
            .andExpect(status().isForbidden());
    }
}
