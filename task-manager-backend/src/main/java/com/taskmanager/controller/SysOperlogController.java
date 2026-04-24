package com.taskmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taskmanager.common.Result;
import com.taskmanager.common.enums.BusinessTypeEnum;
import com.taskmanager.common.utils.TableDataInfo;
import com.taskmanager.domain.SysOperLog;
import com.taskmanager.mapper.SysOperLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志控制器
 *
 * @author taskmanager
 */
@RestController
@RequestMapping("/api/monitor/operlog")
public class SysOperlogController {

    @Autowired
    private SysOperLogMapper operLogMapper;

    /**
     * 分页查询操作日志列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:operlog:list')")
    @GetMapping("/list")
    public Result<TableDataInfo<SysOperLog>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) Integer businessType,
            @RequestParam(required = false) String operName,
            @RequestParam(required = false) Integer status) {
        Page<SysOperLog> page = new Page<>(pageNum, pageSize);
        var wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysOperLog>()
            .like(module != null && !module.isEmpty(), SysOperLog::getModule, module)
            .eq(businessType != null, SysOperLog::getBusinessType, businessType)
            .like(operName != null && !operName.isEmpty(), SysOperLog::getOperName, operName)
            .eq(status != null, SysOperLog::getStatus, status)
            .orderByDesc(SysOperLog::getOperTime);
        return Result.success(TableDataInfo.build(operLogMapper.selectPage(page, wrapper)));
    }

    /**
     * 获取操作日志详情
     */
    @PreAuthorize("@ss.hasPermi('monitor:operlog:query')")
    @GetMapping("/{operId}")
    public Result<SysOperLog> getInfo(@PathVariable Long operId) {
        return Result.success(operLogMapper.selectById(operId));
    }

    /**
     * 批量删除操作日志
     */
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/{operIds}")
    public Result<Void> remove(@PathVariable Long[] operIds) {
        for (Long id : operIds) {
            operLogMapper.deleteById(id);
        }
        return Result.success();
    }

    /**
     * 清空操作日志
     */
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        operLogMapper.delete(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysOperLog>()
        );
        return Result.success();
    }
}
