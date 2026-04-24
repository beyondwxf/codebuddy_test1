package com.taskmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taskmanager.common.Result;
import com.taskmanager.common.annotation.Log;
import com.taskmanager.common.enums.BusinessTypeEnum;
import com.taskmanager.common.utils.TableDataInfo;
import com.taskmanager.domain.SysRole;
import com.taskmanager.mapper.SysRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 角色管理控制器
 *
 * @author taskmanager
 */
@RestController
@RequestMapping("/api/system/role")
public class SysRoleController {

    @Autowired
    private SysRoleMapper roleMapper;

    /**
     * 获取角色列表（分页 + 条件筛选）
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public Result<TableDataInfo<SysRole>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String roleKey,
            @RequestParam(required = false) String status) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        Page<SysRole> result = roleMapper.selectRoleList(page, roleName, roleKey, status);
        return Result.success(TableDataInfo.build(result));
    }

    /** 获取角色详细信息 */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/{roleId}")
    public Result<SysRole> getInfo(@PathVariable Long roleId) {
        return Result.success(roleMapper.selectById(roleId));
    }

    /** 新增角色 */
    @Log(title = "角色管理", businessType = BusinessTypeEnum.INSERT)
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @PostMapping
    public Result<Void> add(@RequestBody SysRole role) {
        if (role.getDelFlag() == null) { role.setDelFlag("0"); }
        if (role.getDataScope() == null) { role.setDataScope("1"); }
        roleMapper.insert(role);
        return Result.success();
    }

    /** 修改角色 */
    @Log(title = "角色管理", businessType = BusinessTypeEnum.UPDATE)
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @PutMapping
    public Result<Void> edit(@RequestBody SysRole role) {
        roleMapper.updateById(role);
        return Result.success();
    }

    /** 删除角色（逻辑删除） */
    @Log(title = "角色管理", businessType = BusinessTypeEnum.DELETE)
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @DeleteMapping("/{roleIds}")
    public Result<Void> remove(@PathVariable Long[] roleIds) {
        for (Long id : roleIds) {
            SysRole role = new SysRole();
            role.setRoleId(id);
            role.setDelFlag("2");
            roleMapper.updateById(role);
        }
        return Result.success();
    }
}
