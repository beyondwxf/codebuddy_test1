package com.taskmanager.security;

import com.taskmanager.domain.SysRole;
import com.taskmanager.domain.SysUser;
import com.taskmanager.mapper.SysMenuMapper;
import com.taskmanager.mapper.SysRoleMapper;
import com.taskmanager.mapper.SysUserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 用户验证处理（实现 Spring Security UserDetailsService 接口）
 *
 * @author taskmanager
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysMenuMapper menuMapper;

    public UserDetailsServiceImpl(SysUserMapper userMapper,
                                   SysRoleMapper roleMapper,
                                   SysMenuMapper menuMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.menuMapper = menuMapper;
    }

    /**
     * 根据用户名加载用户详情（含角色和权限）
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 查询用户基本信息
        SysUser user = userMapper.selectByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户: " + username + " 不存在");
        }
        // 2. 查询用户角色列表
        List<SysRole> roles = roleMapper.selectRolesByUserId(user.getUserId());
        // 3. 查询用户权限标识集合（通过角色的菜单权限）
        Set<String> permissions = menuMapper.selectPermsByUserId(user.getUserId());
        return new LoginUser(user, permissions, roles);
    }
}
