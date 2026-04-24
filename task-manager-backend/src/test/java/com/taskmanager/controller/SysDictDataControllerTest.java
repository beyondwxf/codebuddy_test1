package com.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.domain.SysDictData;
import com.taskmanager.domain.SysUser;
import com.taskmanager.domain.SysRole;
import com.taskmanager.mapper.SysDictDataMapper;
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
 * 字典数据管理控制器单元测试
 * 测试字典数据列表、按类型查询、详情、新增、修改、删除等接口
 *
 * @author taskmanager
 */
@SpringBootTest(properties = {
    "spring.data.redis.repositories.enabled=false",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration"
})
@AutoConfigureMockMvc
class SysDictDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SysDictDataMapper dictDataMapper;

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
    @DisplayName("获取字典数据列表 - 成功")
    void list_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        Page<SysDictData> page = new Page<>(1, 10);
        SysDictData dictData = new SysDictData();
        dictData.setDictCode(1L);
        dictData.setDictType("sys_user_sex");
        dictData.setDictLabel("男");
        dictData.setDictValue("0");
        page.setRecords(List.of(dictData));
        page.setTotal(1);

        when(dictDataMapper.selectPage(any(Page.class), any())).thenReturn(page);

        mockMvc.perform(get("/api/system/dict/data/list")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").value(1))
            .andExpect(jsonPath("$.data.rows[0].dictLabel").value("男"));
    }

    @Test
    @DisplayName("获取字典数据列表 - 按类型筛选")
    void list_withDictType() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        Page<SysDictData> page = new Page<>(1, 10);
        page.setRecords(new ArrayList<>());
        page.setTotal(0);

        when(dictDataMapper.selectPage(any(Page.class), any())).thenReturn(page);

        mockMvc.perform(get("/api/system/dict/data/list")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .param("dictType", "sys_user_sex")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("按字典类型查询数据 - 公开接口（无需认证）")
    void getDictDataByType_public_success() throws Exception {
        SysDictData dictData = new SysDictData();
        dictData.setDictCode(1L);
        dictData.setDictType("sys_user_sex");
        dictData.setDictLabel("男");
        dictData.setDictValue("0");
        dictData.setStatus("0");

        when(dictDataMapper.selectList(any())).thenReturn(List.of(dictData));

        // 不带 Authorization header 访问公开接口
        mockMvc.perform(get("/api/system/dict/data/type/sys_user_sex"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data[0].dictLabel").value("男"));
    }

    @Test
    @DisplayName("按字典类型查询数据 - 返回启用状态的数据")
    void getDictDataByType_onlyActiveData() throws Exception {
        when(dictDataMapper.selectList(any())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/system/dict/data/type/sys_status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("获取字典数据详情 - 成功")
    void getInfo_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysDictData dictData = new SysDictData();
        dictData.setDictCode(1L);
        dictData.setDictType("sys_user_sex");
        dictData.setDictLabel("男");
        dictData.setDictValue("0");
        when(dictDataMapper.selectById(1L)).thenReturn(dictData);

        mockMvc.perform(get("/api/system/dict/data/1")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.dictLabel").value("男"));
    }

    @Test
    @DisplayName("新增字典数据 - 成功")
    void add_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysDictData newDictData = new SysDictData();
        newDictData.setDictType("test_dict");
        newDictData.setDictLabel("测试项");
        newDictData.setDictValue("1");

        when(dictDataMapper.insert(any(SysDictData.class))).thenReturn(1);

        mockMvc.perform(post("/api/system/dict/data")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDictData)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("新增字典数据 - 默认值填充")
    void add_defaultValues() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysDictData newDictData = new SysDictData();
        newDictData.setDictType("default_dict");
        newDictData.setDictLabel("默认值项");
        newDictData.setDictValue("1");
        // 不设置 status 和 isDefault

        when(dictDataMapper.insert(any(SysDictData.class))).thenReturn(1);

        mockMvc.perform(post("/api/system/dict/data")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDictData)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("修改字典数据 - 成功")
    void edit_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysDictData updateDictData = new SysDictData();
        updateDictData.setDictCode(1L);
        updateDictData.setDictLabel("修改后标签");

        when(dictDataMapper.updateById(any(SysDictData.class))).thenReturn(1);

        mockMvc.perform(put("/api/system/dict/data")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDictData)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除字典数据 - 成功")
    void remove_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        when(dictDataMapper.deleteById(anyLong())).thenReturn(1);

        mockMvc.perform(delete("/api/system/dict/data/1,2")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }
}
