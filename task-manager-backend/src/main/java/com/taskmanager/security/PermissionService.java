package com.taskmanager.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 自定义权限验证服务
 * 支持 *:*:* 通配符权限（超级管理员），在 @PreAuthorize 中通过 @ss.hasPermi() 调用
 *
 * @author taskmanager
 */
@Component("ss")
public class PermissionService {

    /** 超级管理员权限标识 */
    private static final String ALL_PERMISSION = "*:*:*";

    /**
     * 验证用户是否具备某权限
     *
     * @param permission 权限标识（如 system:user:list）
     * @return true=有权限，false=无权限
     */
    public boolean hasPermi(String permission) {
        if (permission == null || permission.isEmpty()) {
            return false;
        }
        LoginUser loginUser = getLoginUser();
        if (loginUser == null || loginUser.getPermissions() == null) {
            return false;
        }
        // 超级管理员拥有所有权限
        if (loginUser.getPermissions().contains(ALL_PERMISSION)) {
            return true;
        }
        return loginUser.getPermissions().contains(permission);
    }

    /**
     * 验证用户是否不具备某权限（与 hasPermi 相反，用于限制操作）
     *
     * @param permission 权限标识
     * @return true=无权限，false=有权限
     */
    public boolean lacksPermi(String permission) {
        return !hasPermi(permission);
    }

    /**
     * 从 Security 上下文中获取当前登录用户
     */
    private LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        if (authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        }
        return null;
    }
}
