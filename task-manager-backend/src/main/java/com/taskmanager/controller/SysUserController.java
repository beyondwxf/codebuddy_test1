package com.taskmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taskmanager.common.Result;
import com.taskmanager.common.annotation.Log;
import com.taskmanager.common.enums.BusinessTypeEnum;
import com.taskmanager.common.utils.TableDataInfo;
import com.taskmanager.domain.SysUser;
import com.taskmanager.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 *
 * @author taskmanager
 */
@RestController
@RequestMapping("/api/system/user")
public class SysUserController {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取用户列表（分页 + 条件筛选）
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public Result<TableDataInfo<SysUser>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String phonenumber,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long deptId) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        Page<SysUser> result = userMapper.selectUserList(page, userName, phonenumber, status, deptId);
        return Result.success(TableDataInfo.build(result));
    }

    /**
     * 根据用户ID获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/{userId}")
    public Result<SysUser> getInfo(@PathVariable Long userId) {
        return Result.success(userMapper.selectById(userId));
    }

    /**
     * 新增用户
     */
    @Log(title = "用户管理", businessType = BusinessTypeEnum.INSERT)
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @PostMapping
    public Result<Void> add(@RequestBody SysUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDelFlag("0");
        if (user.getStatus() == null) {
            user.setStatus("0");
        }
        userMapper.insert(user);
        return Result.success();
    }

    /**
     * 修改用户
     */
    @Log(title = "用户管理", businessType = BusinessTypeEnum.UPDATE)
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @PutMapping
    public Result<Void> edit(@RequestBody SysUser user) {
        // 如果密码不为空则重新加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // 查询原密码保持不变
            SysUser oldUser = userMapper.selectById(user.getUserId());
            user.setPassword(oldUser.getPassword());
        }
        userMapper.updateById(user);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @Log(title = "用户管理", businessType = BusinessTypeEnum.DELETE)
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @DeleteMapping("/{userIds}")
    public Result<Void> remove(@PathVariable Long[] userIds) {
        for (Long id : userIds) {
            // 逻辑删除
            SysUser user = new SysUser();
            user.setUserId(id);
            user.setDelFlag("2");
            userMapper.updateById(user);
        }
        return Result.success();
    }

    /**
     * 重置用户密码为默认密码
     */
    @Log(title = "用户管理", businessType = BusinessTypeEnum.UPDATE)
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @PutMapping("/resetPwd")
    public Result<String> resetPwd(@RequestBody SysUser user) {
        String newPassword = "admin123";
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
        return Result.success(newPassword);
    }

    /**
     * 修改用户状态
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(@RequestBody SysUser user) {
        userMapper.updateById(user);
        return Result.success();
    }
}
