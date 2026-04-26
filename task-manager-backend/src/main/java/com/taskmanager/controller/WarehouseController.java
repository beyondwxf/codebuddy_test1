package com.taskmanager.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taskmanager.common.Result;
import com.taskmanager.common.annotation.Log;
import com.taskmanager.common.enums.BusinessTypeEnum;
import com.taskmanager.common.utils.TableDataInfo;
import com.taskmanager.domain.Warehouse;
import com.taskmanager.mapper.WarehouseMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 仓库管理控制器
 *
 * @author taskmanager
 */
@RestController
@RequestMapping("/api/wms/warehouse")
public class WarehouseController {

    @Autowired
    private WarehouseMapper warehouseMapper;

    /**
     * 获取仓库列表（分页 + 条件筛选）
     */
    @PreAuthorize("@ss.hasPermi('wms:warehouse:list')")
    @GetMapping("/list")
    public Result<TableDataInfo<Warehouse>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String warehouseName,
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String warehouseType,
            @RequestParam(required = false) String status) {
        Page<Warehouse> page = new Page<>(pageNum, pageSize);
        List<String> provinceList = (province != null && !province.isEmpty())
                ? Arrays.asList(province.split(",")) : null;
        Page<Warehouse> result = warehouseMapper.selectWarehouseList(
                page, warehouseName, warehouseCode, provinceList, warehouseType, status);
        return Result.success(TableDataInfo.build(result));
    }

    /**
     * 获取所有正常状态的仓库（下拉选择用）
     */
    @GetMapping("/listAll")
    public Result<List<Warehouse>> listAll() {
        return Result.success(warehouseMapper.selectWarehouseAll());
    }

    /**
     * 根据仓库ID获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('wms:warehouse:query')")
    @GetMapping("/{warehouseId}")
    public Result<Warehouse> getInfo(@PathVariable Long warehouseId) {
        return Result.success(warehouseMapper.selectById(warehouseId));
    }

    /**
     * 新增仓库
     */
    @Log(title = "仓库管理", businessType = BusinessTypeEnum.INSERT)
    @PreAuthorize("@ss.hasPermi('wms:warehouse:add')")
    @PostMapping
    public Result<Void> add(@RequestBody Warehouse warehouse) {
        warehouse.setDelFlag("0");
        warehouseMapper.insert(warehouse);
        return Result.success();
    }

    /**
     * 修改仓库
     */
    @Log(title = "仓库管理", businessType = BusinessTypeEnum.UPDATE)
    @PreAuthorize("@ss.hasPermi('wms:warehouse:edit')")
    @PutMapping
    public Result<Void> edit(@RequestBody Warehouse warehouse) {
        warehouseMapper.updateById(warehouse);
        return Result.success();
    }

    /**
     * 删除仓库（逻辑删除）
     */
    @Log(title = "仓库管理", businessType = BusinessTypeEnum.DELETE)
    @PreAuthorize("@ss.hasPermi('wms:warehouse:remove')")
    @DeleteMapping("/{warehouseIds}")
    public Result<Void> remove(@PathVariable Long[] warehouseIds) {
        for (Long id : warehouseIds) {
            Warehouse warehouse = new Warehouse();
            warehouse.setWarehouseId(id);
            warehouse.setDelFlag("2");
            warehouseMapper.updateById(warehouse);
        }
        return Result.success();
    }

    /**
     * 导出仓库数据为Excel
     */
    @Log(title = "仓库管理", businessType = BusinessTypeEnum.EXPORT)
    @PreAuthorize("@ss.hasPermi('wms:warehouse:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response,
                       @RequestParam(required = false) String warehouseName,
                       @RequestParam(required = false) String warehouseCode,
                       @RequestParam(required = false) String province,
                       @RequestParam(required = false) String warehouseType,
                       @RequestParam(required = false) String status) throws IOException {
        List<String> provinceList = (province != null && !province.isEmpty())
                ? Arrays.asList(province.split(",")) : null;
        Page<Warehouse> page = warehouseMapper.selectWarehouseList(
                new Page<>(1, 10000), warehouseName, warehouseCode, provinceList, warehouseType, status);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("仓库数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), Warehouse.class)
                .sheet("仓库数据")
                .doWrite(page.getRecords());
    }

    /**
     * 导入仓库数据（Excel）
     */
    @Log(title = "仓库管理", businessType = BusinessTypeEnum.IMPORT)
    @PreAuthorize("@ss.hasPermi('wms:warehouse:import')")
    @PostMapping("/import")
    public Result<String> importData(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        int[] count = {0};
        int[] failCount = {0};
        StringBuilder failMsg = new StringBuilder();
        EasyExcel.read(file.getInputStream(), Warehouse.class,
                new PageReadListener<Warehouse>(dataList -> {
                    for (Warehouse warehouse : dataList) {
                        count[0]++;
                        try {
                            warehouse.setWarehouseId(null);
                            warehouse.setDelFlag("0");
                            warehouseMapper.insert(warehouse);
                        } catch (Exception e) {
                            failCount[0]++;
                            failMsg.append("第").append(count[0])
                                    .append("行[").append(warehouse.getWarehouseName()).append("]导入失败：")
                                    .append(e.getMessage()).append("; ");
                        }
                    }
                })).sheet().doRead();
        if (failCount[0] > 0) {
            return Result.success("成功导入" + count[0] + "条，失败" + failCount[0] + "条。" + failMsg);
        }
        return Result.success("成功导入" + count[0] + "条数据");
    }

    /**
     * 下载导入模板
     */
    @PreAuthorize("@ss.hasPermi('wms:warehouse:import')")
    @PostMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("仓库导入模板", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), Warehouse.class)
                .sheet("仓库数据")
                .doWrite(java.util.Collections.emptyList());
    }
}
