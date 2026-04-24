package com.taskmanager.controller;

import com.taskmanager.common.Result;
import com.taskmanager.common.annotation.Log;
import com.taskmanager.common.enums.BusinessTypeEnum;
import com.taskmanager.domain.SysMenu;
import com.taskmanager.mapper.SysMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 *
 * @author taskmanager
 */
@RestController
@RequestMapping("/api/system/menu")
public class SysMenuController {

    @Autowired
    private SysMenuMapper menuMapper;

    /** 获取菜单列表（平铺，前端构建树） */
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/list")
    public Result<List<SysMenu>> list(SysMenu menu) {
        return Result.success(menuMapper.selectList(null));
    }

    /** 获取菜单树（用于角色分配权限下拉选择） */
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/treeSelect")
    public Result<List<SysMenu>> treeSelect() {
        List<SysMenu> menus = menuMapper.selectMenuTreeAll();
        return Result.success(menus);
    }

    /** 获取菜单详情 */
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping("/{menuId}")
    public Result<SysMenu> getInfo(@PathVariable Long menuId) {
        return Result.success(menuMapper.selectById(menuId));
    }

    /** 新增菜单 */
    @Log(title = "菜单管理", businessType = BusinessTypeEnum.INSERT)
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @PostMapping
    public Result<Void> add(@RequestBody SysMenu menu) {
        if (menu.getVisible() == null) { menu.setVisible("0"); }
        if (menu.getStatus() == null) { menu.setStatus("0"); }
        menuMapper.insert(menu);
        return Result.success();
    }

    /** 修改菜单 */
    @Log(title = "菜单管理", businessType = BusinessTypeEnum.UPDATE)
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @PutMapping
    public Result<Void> edit(@RequestBody SysMenu menu) {
        menuMapper.updateById(menu);
        return Result.success();
    }

    /** 删除菜单（需检查子菜单） */
    @Log(title = "菜单管理", businessType = BusinessTypeEnum.DELETE)
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @DeleteMapping("/{menuId}")
    public Result<Void> remove(@PathVariable Long menuId) {
        long childCount = menuMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getParentId, menuId)
        );
        if (childCount > 0) {
            return Result.error(500, "存在子菜单，不允许删除");
        }
        // 注意：sys_menu 表无 del_flag 字段，使用物理删除
        // 如需改为逻辑删除，需先给 sys_menu 表和 SysMenu 实体添加 del_flag 字段
        menuMapper.deleteById(menuId);
        return Result.success();
    }
}
