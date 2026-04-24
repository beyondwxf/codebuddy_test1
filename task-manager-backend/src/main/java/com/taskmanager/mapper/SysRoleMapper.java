package com.taskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taskmanager.domain.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色 Mapper 接口
 *
 * @author taskmanager
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据用户ID查询角色列表
     */
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 分页查询角色列表（支持多条件筛选）
     */
    Page<SysRole> selectRoleList(Page<SysRole> page,
                                 @Param("roleName") String roleName,
                                 @Param("roleKey") String roleKey,
                                 @Param("status") String status);
}
