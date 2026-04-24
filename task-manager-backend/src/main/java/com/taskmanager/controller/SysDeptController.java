package com.taskmanager.controller;

import com.taskmanager.common.Result;
import com.taskmanager.common.annotation.Log;
import com.taskmanager.common.enums.BusinessTypeEnum;
import com.taskmanager.domain.SysDept;
import com.taskmanager.mapper.SysDeptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 *
 * @author taskmanager
 */
@RestController
@RequestMapping("/api/system/dept")
public class SysDeptController {

    @Autowired
    private SysDeptMapper deptMapper;

    /** 获取部门列表（树形结构） */
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list")
    public Result<List<SysDept>> list(SysDept dept) {
        List<SysDept> depts = deptMapper.selectDeptTreeAll();
        return Result.success(depts);
    }

    /** 获取部门详情 */
    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    @GetMapping("/{deptId}")
    public Result<SysDept> getInfo(@PathVariable Long deptId) {
        return Result.success(deptMapper.selectById(deptId));
    }

    /** 新增部门 */
    @Log(title = "部门管理", businessType = BusinessTypeEnum.INSERT)
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @PostMapping
    public Result<Void> add(@RequestBody SysDept dept) {
        if (dept.getParentId() == null || dept.getParentId() == 0L) {
            dept.setAncestors("0");
        } else {
            SysDept parent = deptMapper.selectById(dept.getParentId());
            if (parent != null) {
                dept.setAncestors(parent.getAncestors() + "," + dept.getParentId());
            }
        }
        if (dept.getDelFlag() == null) { dept.setDelFlag("0"); }
        if (dept.getStatus() == null) { dept.setStatus("0"); }
        deptMapper.insert(dept);
        return Result.success();
    }

    /** 修改部门 */
    @Log(title = "部门管理", businessType = BusinessTypeEnum.UPDATE)
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @PutMapping
    public Result<Void> edit(@RequestBody SysDept dept) {
        deptMapper.updateById(dept);
        return Result.success();
    }

    /** 删除部门（检查子部门和关联用户） */
    @Log(title = "部门管理", businessType = BusinessTypeEnum.DELETE)
    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    @DeleteMapping("/{deptId}")
    public Result<Void> remove(@PathVariable Long deptId) {
        int childCount = deptMapper.selectChildrenCountByDeptId(deptId);
        if (childCount > 0) {
            return Result.error(500, "存在下级部门，不允许删除");
        }
        SysDept dept = new SysDept();
        dept.setDeptId(deptId);
        dept.setDelFlag("2");
        deptMapper.updateById(dept);
        return Result.success();
    }
}
