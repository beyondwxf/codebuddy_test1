package com.taskmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taskmanager.common.Result;
import com.taskmanager.common.annotation.Log;
import com.taskmanager.common.enums.BusinessTypeEnum;
import com.taskmanager.common.utils.TableDataInfo;
import com.taskmanager.domain.SysDictType;
import com.taskmanager.mapper.SysDictTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 字典类型管理控制器
 *
 * @author taskmanager
 */
@RestController
@RequestMapping("/api/system/dict/type")
public class SysDictTypeController {

    @Autowired
    private SysDictTypeMapper dictTypeMapper;

    /** 分页查询字典类型列表 */
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public Result<TableDataInfo<SysDictType>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String dictName,
            @RequestParam(required = false) String dictType,
            @RequestParam(required = false) String status) {
        Page<SysDictType> page = new Page<>(pageNum, pageSize);
        var wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysDictType>()
            .like(dictName != null && !dictName.isEmpty(), SysDictType::getDictName, dictName)
            .eq(dictType != null && !dictType.isEmpty(), SysDictType::getDictType, dictType)
            .eq(status != null && !status.isEmpty(), SysDictType::getStatus, status);
        return Result.success(TableDataInfo.build(dictTypeMapper.selectPage(page, wrapper)));
    }

    /** 获取字典类型详情 */
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping("/{dictId}")
    public Result<SysDictType> getInfo(@PathVariable Long dictId) {
        return Result.success(dictTypeMapper.selectById(dictId));
    }

    /** 新增字典类型 */
    @Log(title = "字典类型", businessType = BusinessTypeEnum.INSERT)
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @PostMapping
    public Result<Void> add(@RequestBody SysDictType dictType) {
        if (dictType.getStatus() == null) { dictType.setStatus("0"); }
        dictTypeMapper.insert(dictType);
        return Result.success();
    }

    /** 修改字典类型 */
    @Log(title = "字典类型", businessType = BusinessTypeEnum.UPDATE)
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @PutMapping
    public Result<Void> edit(@RequestBody SysDictType dictType) {
        dictTypeMapper.updateById(dictType);
        return Result.success();
    }

    /** 删除字典类型 */
    @Log(title = "字典类型", businessType = BusinessTypeEnum.DELETE)
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @DeleteMapping("/{dictIds}")
    public Result<Void> remove(@PathVariable Long[] dictIds) {
        for (Long id : dictIds) { dictTypeMapper.deleteById(id); }
        return Result.success();
    }
}
