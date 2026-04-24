package com.taskmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taskmanager.common.Result;
import com.taskmanager.common.annotation.Log;
import com.taskmanager.common.enums.BusinessTypeEnum;
import com.taskmanager.common.utils.TableDataInfo;
import com.taskmanager.domain.SysDictData;
import com.taskmanager.mapper.SysDictDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 字典数据管理控制器
 *
 * @author taskmanager
 */
@RestController
@RequestMapping("/api/system/dict/data")
public class SysDictDataController {

    @Autowired
    private SysDictDataMapper dictDataMapper;

    /** 分页查询字典数据列表 */
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public Result<TableDataInfo<SysDictData>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String dictType) {
        Page<SysDictData> page = new Page<>(pageNum, pageSize);
        var wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysDictData>()
            .eq(dictType != null && !dictType.isEmpty(), SysDictData::getDictType, dictType)
            .orderByAsc(SysDictData::getDictSort);
        return Result.success(TableDataInfo.build(dictDataMapper.selectPage(page, wrapper)));
    }

    /** 根据字典类型查询字典数据（公开接口，用于前端下拉框） */
    @GetMapping("/type/{dictType}")
    public Result<java.util.List<SysDictData>> getDictDataByType(@PathVariable String dictType) {
        var list = dictDataMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getStatus, "0")
                .orderByAsc(SysDictData::getDictSort)
        );
        return Result.success(list);
    }

    /** 获取字典数据详情 */
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping("/{dictCode}")
    public Result<SysDictData> getInfo(@PathVariable Long dictCode) {
        return Result.success(dictDataMapper.selectById(dictCode));
    }

    /** 新增字典数据 */
    @Log(title = "字典数据", businessType = BusinessTypeEnum.INSERT)
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @PostMapping
    public Result<Void> add(@RequestBody SysDictData dictData) {
        if (dictData.getStatus() == null) { dictData.setStatus("0"); }
        if (dictData.getIsDefault() == null) { dictData.setIsDefault("N"); }
        dictDataMapper.insert(dictData);
        return Result.success();
    }

    /** 修改字典数据 */
    @Log(title = "字典数据", businessType = BusinessTypeEnum.UPDATE)
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @PutMapping
    public Result<Void> edit(@RequestBody SysDictData dictData) {
        dictDataMapper.updateById(dictData);
        return Result.success();
    }

    /** 删除字典数据 */
    @Log(title = "字典数据", businessType = BusinessTypeEnum.DELETE)
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @DeleteMapping("/{dictCodes}")
    public Result<Void> remove(@PathVariable Long[] dictCodes) {
        for (Long id : dictCodes) { dictDataMapper.deleteById(id); }
        return Result.success();
    }
}
