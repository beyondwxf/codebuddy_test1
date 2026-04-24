package com.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.domain.SysDictType;
import com.taskmanager.domain.SysUser;
import com.taskmanager.domain.SysRole;
import com.taskmanager.mapper.SysDictTypeMapper;
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
 * 字典类型管理控制器单元测试
 * 测试字典类型列表、详情、新增、修改、删除等接口
 *
 * @author taskmanager
 */
@SpringBootTest(properties = {
    "spring.data.redis.repositories.enabled=false",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration"
})
@AutoConfigureMockMvc
class SysDictTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SysDictTypeMapper dictTypeMapper;

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
    @DisplayName("获取字典类型列表 - 成功")
    void list_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        Page<SysDictType> page = new Page<>(1, 10);
        SysDictType dictType = new SysDictType();
        dictType.setDictId(1L);
        dictType.setDictName("用户性别");
        dictType.setDictType("sys_user_sex");
        dictType.setStatus("0");
        page.setRecords(List.of(dictType));
        page.setTotal(1);

        when(dictTypeMapper.selectPage(any(Page.class), any())).thenReturn(page);

        mockMvc.perform(get("/api/system/dict/type/list")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").value(1))
            .andExpect(jsonPath("$.data.rows[0].dictName").value("用户性别"));
    }

    @Test
    @DisplayName("获取字典类型列表 - 带条件筛选")
    void list_withFilters() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        Page<SysDictType> page = new Page<>(1, 10);
        page.setRecords(new ArrayList<>());
        page.setTotal(0);

        when(dictTypeMapper.selectPage(any(Page.class), any())).thenReturn(page);

        mockMvc.perform(get("/api/system/dict/type/list")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .param("dictName", "性别")
                .param("dictType", "sys_user_sex")
                .param("status", "0")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    @DisplayName("获取字典类型详情 - 成功")
    void getInfo_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysDictType dictType = new SysDictType();
        dictType.setDictId(1L);
        dictType.setDictName("用户性别");
        dictType.setDictType("sys_user_sex");
        when(dictTypeMapper.selectById(1L)).thenReturn(dictType);

        mockMvc.perform(get("/api/system/dict/type/1")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.dictName").value("用户性别"));
    }

    @Test
    @DisplayName("新增字典类型 - 成功")
    void add_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysDictType newDictType = new SysDictType();
        newDictType.setDictName("测试字典");
        newDictType.setDictType("test_dict");

        when(dictTypeMapper.insert(any(SysDictType.class))).thenReturn(1);

        mockMvc.perform(post("/api/system/dict/type")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDictType)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("新增字典类型 - 默认值填充")
    void add_defaultValues() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysDictType newDictType = new SysDictType();
        newDictType.setDictName("默认值字典");
        newDictType.setDictType("default_dict");
        // 不设置 status

        when(dictTypeMapper.insert(any(SysDictType.class))).thenReturn(1);

        mockMvc.perform(post("/api/system/dict/type")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDictType)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("修改字典类型 - 成功")
    void edit_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysDictType updateDictType = new SysDictType();
        updateDictType.setDictId(1L);
        updateDictType.setDictName("修改后字典");

        when(dictTypeMapper.updateById(any(SysDictType.class))).thenReturn(1);

        mockMvc.perform(put("/api/system/dict/type")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDictType)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除字典类型 - 成功")
    void remove_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        when(dictTypeMapper.deleteById(anyLong())).thenReturn(1);

        mockMvc.perform(delete("/api/system/dict/type/1,2,3")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除字典类型 - 单个删除")
    void remove_single_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        when(dictTypeMapper.deleteById(1L)).thenReturn(1);

        mockMvc.perform(delete("/api/system/dict/type/1")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }
}
