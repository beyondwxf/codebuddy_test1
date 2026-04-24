package com.taskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taskmanager.domain.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 菜单权限 Mapper 接口
 *
 * @author taskmanager
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /** 查询所有菜单树（构建路由用） */
    List<SysMenu> selectMenuTreeAll();

    /** 根据用户ID查询菜单树 */
    List<SysMenu> selectMenuTreeByUserId(@Param("userId") Long userId);

    /** 根据用户ID查询权限标识集合 */
    Set<String> selectPermsByUserId(@Param("userId") Long userId);

    /** 根据角色ID查询菜单ID集合 */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);
}
