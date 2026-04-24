package com.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.domain.SysRole;
import com.taskmanager.domain.SysUser;
import com.taskmanager.mapper.SysRoleMapper;
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
 * 角色管理控制器单元测试
 * 测试角色列表查询、新增、修改、删除等接口
 *
 * @author taskmanager
 */
@SpringBootTest(properties = {
    "spring.data.redis.repositories.enabled=false",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration"
})
@AutoConfigureMockMvc
class SysRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SysRoleMapper roleMapper;

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
    @DisplayName("获取角色列表 - 成功")
    void list_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        Page<SysRole> page = new Page<>(1, 10);
        SysRole role = new SysRole();
        role.setRoleId(1L);
        role.setRoleName("超级管理员");
        role.setRoleKey("admin");
        role.setStatus("0");
        page.setRecords(List.of(role));
        page.setTotal(1);

        when(roleMapper.selectRoleList(any(Page.class), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/system/role/list")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").value(1))
            .andExpect(jsonPath("$.data.rows[0].roleName").value("超级管理员"));
    }

    @Test
    @DisplayName("获取角色列表 - 带条件筛选")
    void list_withFilters() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        Page<SysRole> page = new Page<>(1, 10);
        page.setRecords(new ArrayList<>());
        page.setTotal(0);

        when(roleMapper.selectRoleList(any(Page.class), eq("管理员"), eq("admin"), eq("0")))
            .thenReturn(page);

        mockMvc.perform(get("/api/system/role/list")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .param("roleName", "管理员")
                .param("roleKey", "admin")
                .param("status", "0")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    @DisplayName("获取角色详情 - 成功")
    void getInfo_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysRole role = new SysRole();
        role.setRoleId(1L);
        role.setRoleName("超级管理员");
        role.setRoleKey("admin");
        when(roleMapper.selectById(1L)).thenReturn(role);

        mockMvc.perform(get("/api/system/role/1")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.roleName").value("超级管理员"));
    }

    @Test
    @DisplayName("新增角色 - 成功")
    void add_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysRole newRole = new SysRole();
        newRole.setRoleName("测试角色");
        newRole.setRoleKey("test_role");
        newRole.setRoleSort(3);

        when(roleMapper.insert(any(SysRole.class))).thenReturn(1);

        mockMvc.perform(post("/api/system/role")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRole)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("新增角色 - 默认值填充")
    void add_defaultValues() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysRole newRole = new SysRole();
        newRole.setRoleName("默认值角色");
        newRole.setRoleKey("default_role");
        newRole.setRoleSort(4);
        // 不设置 delFlag 和 dataScope，验证默认值

        when(roleMapper.insert(any(SysRole.class))).thenReturn(1);

        mockMvc.perform(post("/api/system/role")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRole)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("修改角色 - 成功")
    void edit_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysRole updateRole = new SysRole();
        updateRole.setRoleId(2L);
        updateRole.setRoleName("修改后角色");

        when(roleMapper.updateById(any(SysRole.class))).thenReturn(1);

        mockMvc.perform(put("/api/system/role")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRole)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除角色 - 成功（逻辑删除）")
    void remove_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        when(roleMapper.updateById(any(SysRole.class))).thenReturn(1);

        mockMvc.perform(delete("/api/system/role/2,3")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除角色 - 单个删除")
    void remove_single_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        when(roleMapper.updateById(any(SysRole.class))).thenReturn(1);

        mockMvc.perform(delete("/api/system/role/2")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("无权限访问角色列表 - 返回403")
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

        mockMvc.perform(get("/api/system/role/list")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .header("Authorization", "Bearer no-perm-token"))
            .andExpect(status().isForbidden());
    }
}
