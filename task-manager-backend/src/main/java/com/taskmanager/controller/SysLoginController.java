package com.taskmanager.controller;

import com.taskmanager.common.Result;
import com.taskmanager.domain.SysMenu;
import com.taskmanager.domain.SysUser;
import com.taskmanager.mapper.SysMenuMapper;
import com.taskmanager.mapper.SysRoleMapper;
import com.taskmanager.mapper.SysUserMapper;
import com.taskmanager.mapper.SysUserRoleMapper;
import com.taskmanager.domain.SysUserRole;
import com.taskmanager.security.CaptchaService;
import com.taskmanager.security.LoginUser;
import com.taskmanager.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 登录认证控制器
 * 处理登录、登出、验证码、获取当前用户信息、动态路由等接口
 *
 * @author taskmanager
 */
@RestController
@RequestMapping("/api/auth")
public class SysLoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private SysMenuMapper menuMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO dto) {
        if (dto.getUserName() == null || dto.getUserName().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        SysUser existing = userMapper.selectByUserName(dto.getUserName().trim());
        if (existing != null) {
            return Result.error("用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUserName(dto.getUserName().trim());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickName(dto.getNickName());
        user.setPhonenumber(dto.getPhonenumber());
        user.setStatus("0");
        user.setUserType("00");
        user.setDeptId(100L);
        user.setDelFlag("0");
        user.setCreateTime(new java.util.Date());
        userMapper.insert(user);
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getUserId());
        userRole.setRoleId(2L);
        userRoleMapper.insert(userRole);
        return Result.success();
    }

    /**
     * 获取图形验证码
     */
    @GetMapping("/captcha")
    public Result<CaptchaService.CaptchaVO> getCaptcha() {
        return Result.success(captchaService.createCaptcha());
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        // 生产环境必须启用验证码，防御暴力破解攻击
        if (loginDTO.getUuid() != null && !loginDTO.getUuid().isEmpty()
                && loginDTO.getCode() != null && !loginDTO.getCode().isEmpty()) {
            try {
                captchaService.validateCaptcha(loginDTO.getUuid(), loginDTO.getCode());
            } catch (Exception e) {
                return Result.error(500, e.getMessage());
            }
        }
        String userName = loginDTO.getUserName();
        String password = loginDTO.getPassword();
        
        // 调试日志：打印登录请求信息
        System.out.println("[DEBUG] 登录请求 - 用户名: " + userName);
        
        // 2. Spring Security 身份认证
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userName, password)
        );
        // 3. 获取认证后的 LoginUser
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        // 4. 创建 Token 并存入 Redis
        String token = tokenService.createToken(loginUser);
        // 5. 构建返回对象
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(loginUser.getUserId());
        loginVO.setUserName(loginUser.getUser().getUserName());
        loginVO.setNickName(loginUser.getUser().getNickName());
        return Result.success(loginVO);
    }

    /**
     * 用户登出（从 Redis 删除 Token）
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            tokenService.delLoginUser(loginUser.getToken());
            SecurityContextHolder.clearContext();
        }
        return Result.success();
    }

    /**
     * 获取当前登录用户信息（角色、权限、用户详情）
     */
    @GetMapping("/getInfo")
    public Result<UserInfoVO> getInfo() {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return Result.error(401, "未登录或登录已过期");
        }
        UserInfoVO vo = new UserInfoVO();
        vo.setUser(loginUser.getUser());
        // 角色标识列表（如 admin, common）
        vo.setRoles(loginUser.getRoles().stream()
            .map(com.taskmanager.domain.SysRole::getRoleKey)
            .collect(Collectors.toList()));
        // 权限标识列表
        vo.setPermissions(loginUser.getPermissions() != null 
            ? new java.util.ArrayList<>(loginUser.getPermissions()) 
            : new java.util.ArrayList<>());
        return Result.success(vo);
    }

    /**
     * 获取路由信息（动态菜单树，用于前端构建路由）
     */
    @GetMapping("/getRouters")
    public Result<List<RouterVO>> getRouters() {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return Result.error(401, "未登录或登录已过期");
        }
        List<SysMenu> menus;
        // 管理员获取所有菜单
        boolean isAdmin = loginUser.getRoles().stream()
            .anyMatch(r -> "admin".equals(r.getRoleKey()));
        if (isAdmin) {
            menus = menuMapper.selectMenuTreeAll();
        } else {
            menus = menuMapper.selectMenuTreeByUserId(loginUser.getUserId());
        }
        // 构建菜单树
        List<SysMenu> menuTree = buildMenuTree(menus, 0L);
        // 转换为前端路由格式
        List<RouterVO> routers = menuTree.stream()
            .map(this::buildRouter)
            .collect(Collectors.toList());
        return Result.success(routers);
    }

    /**
     * 从 SecurityContext 获取当前登录用户
     */
    private LoginUser getLoginUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() instanceof String) {
            return null;
        }
        return (LoginUser) auth.getPrincipal();
    }

    /**
     * 递归构建菜单树
     */
    private List<SysMenu> buildMenuTree(List<SysMenu> menus, Long parentId) {
        return menus.stream()
            .filter(m -> parentId.equals(m.getParentId()))
            .peek(m -> m.setChildren(buildMenuTree(menus, m.getMenuId())))
            .collect(Collectors.toList());
    }

    /**
     * 将 SysMenu 转换为前端路由 VO
     */
    private RouterVO buildRouter(SysMenu menu) {
        RouterVO router = new RouterVO();
        router.setName(menu.getMenuName());
        router.setPath(getRouterPath(menu));
        router.setComponent(getComponent(menu));
        router.setMeta(new RouterVO.MetaVO(
            menu.getMenuName(),
            "#".equals(menu.getIcon()) ? "" : menu.getIcon(),
            "0".equals(menu.getVisible()) ? false : true
        ));
        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
            // 只保留目录和菜单类型的子节点
            List<SysMenu> childMenus = menu.getChildren().stream()
                .filter(c -> !"F".equals(c.getMenuType()))
                .collect(Collectors.toList());
            router.setChildren(childMenus.stream()
                .map(this::buildRouter)
                .collect(Collectors.toList()));
        }
        return router;
    }

    /**
     * 获取路由地址
     */
    private String getRouterPath(SysMenu menu) {
        String path = menu.getPath();
        // 非外链且是一级目录，自动添加 /
        // isFrame 是 Integer 类型，需要用 Integer 比较或转字符串
        boolean isNotFrame = menu.getIsFrame() != null && menu.getIsFrame() == 0;
        if ("M".equals(menu.getMenuType()) && isNotFrame
                && menu.getParentId() != null && menu.getParentId() == 0
                && !"0".equals(path) && !path.startsWith("/")) {
            return "/" + path;
        }
        return path;
    }

    /**
     * 获取组件路径
     */
    private String getComponent(SysMenu menu) {
        String component = menu.getComponent();
        if ("M".equals(menu.getMenuType()) || (component == null || component.isEmpty())) {
            return "Layout";
        }
        return component;
    }

    /** 注册请求 DTO */
    @lombok.Data
    public static class RegisterDTO {
        private String userName;
        private String password;
        private String nickName;
        private String phonenumber;
    }

    /** 登录请求 DTO */
    @lombok.Data
    public static class LoginDTO {
        private String userName;
        private String password;
        private String uuid;
        private String code;
        private Boolean rememberMe;
    }

    /** 登录响应 VO */
    @lombok.Data
    public static class LoginVO {
        private String token;
        private Long userId;
        private String userName;
        private String nickName;
    }

    /** 用户信息 VO */
    @lombok.Data
    public static class UserInfoVO {
        private SysUser user;
        private List<String> roles;
        /** 权限标识列表 */
        private List<String> permissions;
    }

    /** 前端路由 VO */
    @lombok.Data
    public static class RouterVO {
        private String name;
        private String path;
        private String component;
        private MetaVO meta;
        private List<RouterVO> children;

        @lombok.Data
        @lombok.AllArgsConstructor
        public static class MetaVO {
            private String title;
            private String icon;
            private boolean hidden;
        }
    }
}
