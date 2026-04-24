package com.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.common.utils.TableDataInfo;
import com.taskmanager.domain.SysRole;
import com.taskmanager.domain.SysUser;
import com.taskmanager.mapper.SysUserMapper;
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
import org.springframework.http.MediaType;
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
 * 用户管理控制器单元测试
 * 测试用户列表查询、新增、修改、删除、重置密码、修改状态等接口
 *
 * @author taskmanager
 */
@SpringBootTest(properties = {
    "spring.data.redis.repositories.enabled=false",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration"
})
@AutoConfigureMockMvc
class SysUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SysUserMapper userMapper;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** 构建管理员登录用户（拥有全部权限） */
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

    /** 模拟已认证请求 */
    private void mockAuthentication(LoginUser loginUser) {
        when(tokenService.getLoginUser(anyString())).thenReturn(loginUser);
        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));
        when(((ValueOperations) redisTemplate.opsForValue()).get(anyString())).thenReturn(loginUser);
    }

    @Test
    @DisplayName("获取用户列表 - 成功")
    void list_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        // 模拟分页查询结果
        Page<SysUser> page = new Page<>(1, 10);
        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setUserName("admin");
        user.setNickName("超级管理员");
        page.setRecords(List.of(user));
        page.setTotal(1);

        when(userMapper.selectUserList(any(Page.class), any(), any(), any(), any()))
            .thenReturn(page);

        mockMvc.perform(get("/api/system/user/list")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").value(1))
            .andExpect(jsonPath("$.data.rows[0].userName").value("admin"));
    }

    @Test
    @DisplayName("获取用户列表 - 带条件筛选")
    void list_withFilters() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        Page<SysUser> page = new Page<>(1, 10);
        page.setRecords(new ArrayList<>());
        page.setTotal(0);

        when(userMapper.selectUserList(any(Page.class), eq("test"), eq("13800138000"), eq("0"), any()))
            .thenReturn(page);

        mockMvc.perform(get("/api/system/user/list")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .param("userName", "test")
                .param("phonenumber", "13800138000")
                .param("status", "0")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    @DisplayName("获取用户详情 - 成功")
    void getInfo_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setUserName("admin");
        user.setNickName("超级管理员");
        when(userMapper.selectById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/system/user/1")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.userName").value("admin"));
    }

    @Test
    @DisplayName("新增用户 - 成功")
    void add_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysUser newUser = new SysUser();
        newUser.setUserName("testuser");
        newUser.setNickName("测试用户");
        newUser.setPassword("test123");
        newUser.setEmail("test@example.com");
        newUser.setPhonenumber("13800138000");

        when(userMapper.insert(any(SysUser.class))).thenReturn(1);

        mockMvc.perform(post("/api/system/user")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("修改用户 - 成功")
    void edit_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysUser existUser = new SysUser();
        existUser.setUserId(2L);
        existUser.setUserName("testuser");
        existUser.setPassword(passwordEncoder.encode("oldPassword"));

        SysUser updateUser = new SysUser();
        updateUser.setUserId(2L);
        updateUser.setNickName("修改后的昵称");

        when(userMapper.selectById(2L)).thenReturn(existUser);
        when(userMapper.updateById(any(SysUser.class))).thenReturn(1);

        mockMvc.perform(put("/api/system/user")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("修改用户 - 更新密码")
    void edit_withPassword() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysUser updateUser = new SysUser();
        updateUser.setUserId(2L);
        updateUser.setPassword("newPassword123");

        when(userMapper.updateById(any(SysUser.class))).thenReturn(1);

        mockMvc.perform(put("/api/system/user")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除用户 - 成功（逻辑删除）")
    void remove_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        when(userMapper.updateById(any(SysUser.class))).thenReturn(1);

        mockMvc.perform(delete("/api/system/user/2,3")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("重置密码 - 成功")
    void resetPwd_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysUser resetUser = new SysUser();
        resetUser.setUserId(2L);

        when(userMapper.updateById(any(SysUser.class))).thenReturn(1);

        mockMvc.perform(put("/api/system/user/resetPwd")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            // 修复：resetPwd 不再返回密码原文，而是返回提示消息
            .andExpect(jsonPath("$.data").value("密码重置成功，请通知用户及时修改默认密码"));
    }

    @Test
    @DisplayName("修改用户状态 - 成功")
    void changeStatus_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysUser statusUser = new SysUser();
        statusUser.setUserId(2L);
        statusUser.setStatus("1");

        when(userMapper.updateById(any(SysUser.class))).thenReturn(1);

        mockMvc.perform(put("/api/system/user/changeStatus")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("无权限访问用户列表 - 返回403")
    void list_noPermission_forbidden() throws Exception {
        // 构造无权限用户
        SysUser user = new SysUser();
        user.setUserId(3L);
        user.setUserName("noperm");
        user.setNickName("无权限用户");
        user.setPassword(passwordEncoder.encode("test123"));
        user.setStatus("0");

        Set<String> permissions = new HashSet<>();
        // 没有 system:user:list 权限
        LoginUser loginUser = new LoginUser(user, permissions, Collections.emptyList());
        loginUser.setToken("no-perm-token");
        mockAuthentication(loginUser);

        mockMvc.perform(get("/api/system/user/list")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .header("Authorization", "Bearer no-perm-token"))
            .andExpect(status().isForbidden());
    }
}
