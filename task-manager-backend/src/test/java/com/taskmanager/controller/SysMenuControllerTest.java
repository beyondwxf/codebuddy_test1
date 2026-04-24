package com.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.domain.SysMenu;
import com.taskmanager.domain.SysUser;
import com.taskmanager.domain.SysRole;
import com.taskmanager.mapper.SysMenuMapper;
import com.taskmanager.security.LoginUser;
import com.taskmanager.security.TokenService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
 * 菜单管理控制器单元测试
 * 测试菜单列表、菜单树、详情、新增、修改、删除等接口
 *
 * @author taskmanager
 */
@SpringBootTest(properties = {
    "spring.data.redis.repositories.enabled=false",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration"
})
@AutoConfigureMockMvc
class SysMenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SysMenuMapper menuMapper;

    @MockBean
    private TokenService tokenService;

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
    @DisplayName("获取菜单列表 - 成功")
    void list_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysMenu menu = new SysMenu();
        menu.setMenuId(1L);
        menu.setMenuName("系统管理");
        menu.setMenuType("M");
        when(menuMapper.selectList(any())).thenReturn(List.of(menu));

        mockMvc.perform(get("/api/system/menu/list")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data[0].menuName").value("系统管理"));
    }

    @Test
    @DisplayName("获取菜单树 - 成功")
    void treeSelect_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysMenu menu = new SysMenu();
        menu.setMenuId(1L);
        menu.setMenuName("系统管理");
        menu.setParentId(0L);
        when(menuMapper.selectMenuTreeAll()).thenReturn(List.of(menu));

        mockMvc.perform(get("/api/system/menu/treeSelect")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("获取菜单详情 - 成功")
    void getInfo_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysMenu menu = new SysMenu();
        menu.setMenuId(1L);
        menu.setMenuName("系统管理");
        menu.setMenuType("M");
        when(menuMapper.selectById(1L)).thenReturn(menu);

        mockMvc.perform(get("/api/system/menu/1")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.menuName").value("系统管理"));
    }

    @Test
    @DisplayName("新增菜单 - 目录类型")
    void add_directory_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysMenu newMenu = new SysMenu();
        newMenu.setMenuName("测试目录");
        newMenu.setMenuType("M");
        newMenu.setParentId(0L);
        newMenu.setPath("test");
        newMenu.setOrderNum(5);

        when(menuMapper.insert(any(SysMenu.class))).thenReturn(1);

        mockMvc.perform(post("/api/system/menu")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMenu)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("新增菜单 - 菜单类型")
    void add_menuItem_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysMenu newMenu = new SysMenu();
        newMenu.setMenuName("测试菜单");
        newMenu.setMenuType("C");
        newMenu.setParentId(1L);
        newMenu.setPath("testpage");
        newMenu.setComponent("system/test/index");
        newMenu.setOrderNum(1);

        when(menuMapper.insert(any(SysMenu.class))).thenReturn(1);

        mockMvc.perform(post("/api/system/menu")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMenu)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("新增菜单 - 按钮类型")
    void add_button_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysMenu newMenu = new SysMenu();
        newMenu.setMenuName("测试按钮");
        newMenu.setMenuType("F");
        newMenu.setParentId(2L);
        newMenu.setPerms("system:test:add");
        newMenu.setOrderNum(1);

        when(menuMapper.insert(any(SysMenu.class))).thenReturn(1);

        mockMvc.perform(post("/api/system/menu")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMenu)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("新增菜单 - 默认值填充")
    void add_defaultValues() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysMenu newMenu = new SysMenu();
        newMenu.setMenuName("默认值菜单");
        newMenu.setMenuType("C");
        newMenu.setPath("default");
        // 不设置 visible 和 status

        when(menuMapper.insert(any(SysMenu.class))).thenReturn(1);

        mockMvc.perform(post("/api/system/menu")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMenu)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("修改菜单 - 成功")
    void edit_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        SysMenu updateMenu = new SysMenu();
        updateMenu.setMenuId(1L);
        updateMenu.setMenuName("修改后菜单");

        when(menuMapper.updateById(any(SysMenu.class))).thenReturn(1);

        mockMvc.perform(put("/api/system/menu")
                .header("Authorization", "Bearer test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateMenu)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除菜单 - 成功（无子菜单）")
    void remove_success() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        when(menuMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(menuMapper.deleteById(100L)).thenReturn(1);

        mockMvc.perform(delete("/api/system/menu/100")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除菜单 - 存在子菜单不允许删除")
    void remove_hasChildren_failed() throws Exception {
        LoginUser loginUser = buildAdminLoginUser();
        mockAuthentication(loginUser);

        when(menuMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(2L);

        mockMvc.perform(delete("/api/system/menu/1")
                .header("Authorization", "Bearer test-admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(500))
            .andExpect(jsonPath("$.message").value("存在子菜单，不允许删除"));
    }
}
