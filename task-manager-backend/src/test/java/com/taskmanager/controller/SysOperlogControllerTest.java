package com.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.domain.SysOperLog;
import com.taskmanager.domain.SysUser;
import com.taskmanager.domain.SysRole;
import com.taskmanager.mapper.SysOperLogMapper;
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
 * 操作日志控制器单元测试
 * 测试操作日志列表、详情、批量删除、清空等接口
 *
 * @author taskmanager
 */
@SpringBootTest(properties = {
    "spring.data.redis.repositories.enabled=false",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration"
})
@AutoConfigureMockMvc
class SysOperlogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SysOperLogMapper operLogMapper;

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
    @DisplayName("获取操作日志列表 - 成功")
    void list_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        Page<SysOperLog> page = new Page<>(1, 10);
        SysOperLog operLog = new SysOperLog();
        operLog.setOperId(1L);
        operLog.setModule("用户管理");
        operLog.setBusinessType(1);
        operLog.setOperName("admin");
        operLog.setStatus(0);
        page.setRecords(List.of(operLog));
        page.setTotal(1);

        when(operLogMapper.selectPage(any(Page.class), any())).thenReturn(page);

        mockMvc.perform(get("/api/monitor/operlog/list")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").value(1))
            .andExpect(jsonPath("$.data.rows[0].module").value("用户管理"));
    }

    @Test
    @DisplayName("获取操作日志列表 - 带条件筛选")
    void list_withFilters() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        Page<SysOperLog> page = new Page<>(1, 10);
        page.setRecords(new ArrayList<>());
        page.setTotal(0);

        when(operLogMapper.selectPage(any(Page.class), any())).thenReturn(page);

        mockMvc.perform(get("/api/monitor/operlog/list")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .param("module", "用户")
                .param("businessType", "1")
                .param("operName", "admin")
                .param("status", "0")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    @DisplayName("获取操作日志详情 - 成功")
    void getInfo_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysOperLog operLog = new SysOperLog();
        operLog.setOperId(1L);
        operLog.setModule("用户管理");
        operLog.setBusinessType(1);
        operLog.setOperName("admin");
        operLog.setMethod("com.taskmanager.controller.SysUserController.add()");
        operLog.setRequestMethod("POST");
        operLog.setStatus(0);
        when(operLogMapper.selectById(1L)).thenReturn(operLog);

        mockMvc.perform(get("/api/monitor/operlog/1")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.module").value("用户管理"))
            .andExpect(jsonPath("$.data.requestMethod").value("POST"));
    }

    @Test
    @DisplayName("批量删除操作日志 - 成功")
    void remove_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        when(operLogMapper.deleteById(anyLong())).thenReturn(1);

        mockMvc.perform(delete("/api/monitor/operlog/1,2,3")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("清空操作日志 - 成功")
    void clean_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        when(operLogMapper.delete(any())).thenReturn(100);

        mockMvc.perform(delete("/api/monitor/operlog/clean")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("无权限访问操作日志 - 返回403")
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

        mockMvc.perform(get("/api/monitor/operlog/list")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .header("Authorization", "Bearer no-perm-token"))
            .andExpect(status().isForbidden());
    }
}
