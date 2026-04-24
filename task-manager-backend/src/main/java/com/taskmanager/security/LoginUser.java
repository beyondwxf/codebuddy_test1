package com.taskmanager.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taskmanager.domain.SysRole;
import com.taskmanager.domain.SysUser;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 登录用户身份信息
 * 实现 Spring Security UserDetails 接口，包含用户实体、权限集合和角色列表
 *
 * @author taskmanager
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 用户Token（Redis Key 标识） */
    private String token;

    /** 用户信息 */
    private SysUser user;

    /** 权限标识集合（如 system:user:list） */
    private Set<String> permissions;

    /** 角色列表 */
    private List<SysRole> roles;

    public LoginUser() {
    }

    public LoginUser(SysUser user, Set<String> permissions, List<SysRole> roles) {
        this.user = user;
        this.userId = user.getUserId();
        this.permissions = permissions;
        this.roles = roles;
    }

    /**
     * 将权限字符串集合转换为 GrantedAuthority 集合
     * Spring Security 框架需要此格式进行权限校验
     */
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (permissions == null) {
            return List.of();
        }
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        // 根据 status 字段判断：0=正常启用，1=停用
        return "0".equals(user.getStatus());
    }
}
