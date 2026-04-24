package com.taskmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taskmanager.common.Result;
import com.taskmanager.common.utils.TableDataInfo;
import com.taskmanager.domain.SysLogininfor;
import com.taskmanager.mapper.SysLogininforMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 登录日志控制器
 *
 * @author taskmanager
 */
@RestController
@RequestMapping("/api/monitor/logininfor")
public class SysLogininforController {

    @Autowired
    private SysLogininforMapper logininforMapper;

    /**
     * 分页查询登录日志列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:list')")
    @GetMapping("/list")
    public Result<TableDataInfo<SysLogininfor>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String ipaddr,
            @RequestParam(required = false) String status) {
        Page<SysLogininfor> page = new Page<>(pageNum, pageSize);
        var wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysLogininfor>()
            .like(userName != null && !userName.isEmpty(), SysLogininfor::getUserName, userName)
            .like(ipaddr != null && !ipaddr.isEmpty(), SysLogininfor::getIpaddr, ipaddr)
            .eq(status != null && !status.isEmpty(), SysLogininfor::getStatus, status)
            .orderByDesc(SysLogininfor::getLoginTime);
        return Result.success(TableDataInfo.build(logininforMapper.selectPage(page, wrapper)));
    }

    /**
     * 获取登录日志详情
     */
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:query')")
    @GetMapping("/{infoId}")
    public Result<SysLogininfor> getInfo(@PathVariable Long infoId) {
        return Result.success(logininforMapper.selectById(infoId));
    }

    /**
     * 批量删除登录日志
     */
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @DeleteMapping("/{infoIds}")
    public Result<Void> remove(@PathVariable Long[] infoIds) {
        for (Long id : infoIds) {
            logininforMapper.deleteById(id);
        }
        return Result.success();
    }

    /**
     * 清空登录日志
     */
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        logininforMapper.delete(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysLogininfor>()
        );
        return Result.success();
    }

    /**
     * 账号解锁（预留接口）
     */
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:unlock')")
    @GetMapping("/unlock/{userName}")
    public Result<Void> unlock(@PathVariable String userName) {
        // TODO: 实现 Redis 登录失败次数重置
        return Result.success();
    }
}
