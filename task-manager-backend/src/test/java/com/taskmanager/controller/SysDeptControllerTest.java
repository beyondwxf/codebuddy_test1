package com.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.domain.SysDept;
import com.taskmanager.domain.SysUser;
import com.taskmanager.domain.SysRole;
import com.taskmanager.mapper.SysDeptMapper;
import com.taskmanager.security.LoginUser;
import com.taskmanager.security.TokenService;
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
 * 部门管理控制器单元测试
 * 测试部门列表、详情、新增、修改、删除等接口
 *
 * @author taskmanager
 */
@SpringBootTest(properties = {
    "spring.data.redis.repositories.enabled=false",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration"
})
@AutoConfigureMockMvc
class SysDeptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SysDeptMapper deptMapper;

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
    @DisplayName("获取部门列表 - 成功")
    void list_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysDept dept = new SysDept();
        dept.setDeptId(100L);
        dept.setDeptName("总公司");
        dept.setParentId(0L);
        when(deptMapper.selectDeptTreeAll()).thenReturn(List.of(dept));

        mockMvc.perform(get("/api/system/dept/list")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data[0].deptName").value("总公司"));
    }

    @Test
    @DisplayName("获取部门详情 - 成功")
    void getInfo_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysDept dept = new SysDept();
        dept.setDeptId(100L);
        dept.setDeptName("总公司");
        when(deptMapper.selectById(100L)).thenReturn(dept);

        mockMvc.perform(get("/api/system/dept/100")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.deptName").value("总公司"));
    }

    @Test
    @DisplayName("新增部门 - 顶级部门")
    void add_topLevel_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysDept newDept = new SysDept();
        newDept.setDeptName("新总公司");
        newDept.setParentId(0L);
        newDept.setOrderNum(1);
        newDept.setLeader("张三");

        when(deptMapper.insert(any(SysDept.class))).thenReturn(1);

        mockMvc.perform(post("/api/system/dept")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDept)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("新增部门 - 子部门（自动拼接祖级列表）")
    void add_childDept_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        // 父部门
        SysDept parentDept = new SysDept();
        parentDept.setDeptId(100L);
        parentDept.setDeptName("总公司");
        parentDept.setAncestors("0");
        when(deptMapper.selectById(100L)).thenReturn(parentDept);

        SysDept newDept = new SysDept();
        newDept.setDeptName("研发部门");
        newDept.setParentId(100L);
        newDept.setOrderNum(1);

        when(deptMapper.insert(any(SysDept.class))).thenReturn(1);

        mockMvc.perform(post("/api/system/dept")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDept)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("新增部门 - 默认值填充")
    void add_defaultValues() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysDept newDept = new SysDept();
        newDept.setDeptName("默认值部门");
        newDept.setParentId(0L);
        newDept.setOrderNum(1);
        // 不设置 delFlag 和 status

        when(deptMapper.insert(any(SysDept.class))).thenReturn(1);

        mockMvc.perform(post("/api/system/dept")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDept)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("修改部门 - 成功")
    void edit_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysDept updateDept = new SysDept();
        updateDept.setDeptId(100L);
        updateDept.setDeptName("修改后公司");

        when(deptMapper.updateById(any(SysDept.class))).thenReturn(1);

        mockMvc.perform(put("/api/system/dept")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDept)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除部门 - 成功（无子部门）")
    void remove_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        when(deptMapper.selectChildrenCountByDeptId(200L)).thenReturn(0);
        when(deptMapper.updateById(any(SysDept.class))).thenReturn(1);

        mockMvc.perform(delete("/api/system/dept/200")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除部门 - 存在下级部门不允许删除")
    void remove_hasChildren_failed() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        when(deptMapper.selectChildrenCountByDeptId(100L)).thenReturn(3);

        mockMvc.perform(delete("/api/system/dept/100")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(500))
            .andExpect(jsonPath("$.message").value("存在下级部门，不允许删除"));
    }
}
