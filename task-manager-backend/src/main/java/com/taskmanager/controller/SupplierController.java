package com.taskmanager.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taskmanager.common.Result;
import com.taskmanager.common.annotation.Log;
import com.taskmanager.common.enums.BusinessTypeEnum;
import com.taskmanager.common.utils.TableDataInfo;
import com.taskmanager.domain.Supplier;
import com.taskmanager.mapper.SupplierMapper;
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
 * 供应商管理控制器
 *
 * @author taskmanager
 */
@RestController
@RequestMapping("/api/system/supplier")
public class SupplierController {

    @Autowired
    private SupplierMapper supplierMapper;

    /**
     * 获取供应商列表（分页 + 条件筛选）
     *
     * @param pageNum       页码
     * @param pageSize      每页大小
     * @param companyName   公司名称（模糊搜索）
     * @param province      省份（多选，逗号分隔）
     * @param contactPerson 联系人（模糊搜索）
     * @param category      品类（多选，逗号分隔）
     * @param contactStatus 联系状态
     * @return 分页结果
     */
    @PreAuthorize("@ss.hasPermi('system:supplier:list')")
    @GetMapping("/list")
    public Result<TableDataInfo<Supplier>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String contactPerson,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String contactStatus) {
        Page<Supplier> page = new Page<>(pageNum, pageSize);
        // 多选字段：前端传入逗号分隔字符串，转为List
        List<String> provinceList = (province != null && !province.isEmpty())
                ? Arrays.asList(province.split(",")) : null;
        List<String> categoryList = (category != null && !category.isEmpty())
                ? Arrays.asList(category.split(",")) : null;
        Page<Supplier> result = supplierMapper.selectSupplierList(
                page, companyName, provinceList, contactPerson, categoryList, contactStatus);
        return Result.success(TableDataInfo.build(result));
    }

    /**
     * 根据供应商ID获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:supplier:query')")
    @GetMapping("/{supplierId}")
    public Result<Supplier> getInfo(@PathVariable Long supplierId) {
        return Result.success(supplierMapper.selectById(supplierId));
    }

    /**
     * 新增供应商
     */
    @Log(title = "供应商管理", businessType = BusinessTypeEnum.INSERT)
    @PreAuthorize("@ss.hasPermi('system:supplier:add')")
    @PostMapping
    public Result<Void> add(@RequestBody Supplier supplier) {
        supplier.setDelFlag("0");
        supplierMapper.insert(supplier);
        return Result.success();
    }

    /**
     * 修改供应商
     */
    @Log(title = "供应商管理", businessType = BusinessTypeEnum.UPDATE)
    @PreAuthorize("@ss.hasPermi('system:supplier:edit')")
    @PutMapping
    public Result<Void> edit(@RequestBody Supplier supplier) {
        supplierMapper.updateById(supplier);
        return Result.success();
    }

    /**
     * 删除供应商（逻辑删除）
     */
    @Log(title = "供应商管理", businessType = BusinessTypeEnum.DELETE)
    @PreAuthorize("@ss.hasPermi('system:supplier:remove')")
    @DeleteMapping("/{supplierIds}")
    public Result<Void> remove(@PathVariable Long[] supplierIds) {
        for (Long id : supplierIds) {
            Supplier supplier = new Supplier();
            supplier.setSupplierId(id);
            supplier.setDelFlag("2");
            supplierMapper.updateById(supplier);
        }
        return Result.success();
    }

    /**
     * 导出供应商数据为Excel
     */
    @Log(title = "供应商管理", businessType = BusinessTypeEnum.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:supplier:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response,
                       @RequestParam(required = false) String companyName,
                       @RequestParam(required = false) String province,
                       @RequestParam(required = false) String contactPerson,
                       @RequestParam(required = false) String category,
                       @RequestParam(required = false) String contactStatus) throws IOException {
        // 查询全部符合条件的数据（不分页）
        List<String> provinceList = (province != null && !province.isEmpty())
                ? Arrays.asList(province.split(",")) : null;
        List<String> categoryList = (category != null && !category.isEmpty())
                ? Arrays.asList(category.split(",")) : null;
        Page<Supplier> page = supplierMapper.selectSupplierList(
                new Page<>(1, 10000), companyName, provinceList, contactPerson, categoryList, contactStatus);

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("供应商数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        // 写入Excel
        EasyExcel.write(response.getOutputStream(), Supplier.class)
                .sheet("供应商数据")
                .doWrite(page.getRecords());
    }

    /**
     * 导入供应商数据（Excel）
     */
    @Log(title = "供应商管理", businessType = BusinessTypeEnum.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:supplier:import')")
    @PostMapping("/import")
    public Result<String> importData(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        // 使用EasyExcel流式读取，逐条插入
        int[] count = {0};
        int[] failCount = {0};
        StringBuilder failMsg = new StringBuilder();
        EasyExcel.read(file.getInputStream(), Supplier.class,
                new PageReadListener<Supplier>(dataList -> {
                    for (Supplier supplier : dataList) {
                        count[0]++; // 先递增行号，确保报错信息行号准确
                        try {
                            // 清除主键，避免重复导入时 ID 冲突（IdType.AUTO 会自动生成新ID）
                            supplier.setSupplierId(null);
                            supplier.setDelFlag("0");
                            supplierMapper.insert(supplier);
                        } catch (Exception e) {
                            failCount[0]++;
                            failMsg.append("第").append(count[0])
                                    .append("行[").append(supplier.getCompanyName()).append("]导入失败：")
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
    @PreAuthorize("@ss.hasPermi('system:supplier:import')")
    @PostMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("供应商导入模板", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), Supplier.class)
                .sheet("供应商数据")
                .doWrite(java.util.Collections.emptyList());
    }
}
