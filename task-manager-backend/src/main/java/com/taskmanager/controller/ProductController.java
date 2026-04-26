package com.taskmanager.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taskmanager.common.Result;
import com.taskmanager.common.annotation.Log;
import com.taskmanager.common.enums.BusinessTypeEnum;
import com.taskmanager.common.utils.TableDataInfo;
import com.taskmanager.domain.Product;
import com.taskmanager.domain.ProductInventory;
import com.taskmanager.domain.ProductSupplier;
import com.taskmanager.mapper.ProductInventoryMapper;
import com.taskmanager.mapper.ProductMapper;
import com.taskmanager.mapper.ProductSupplierMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 商品管理控制器
 *
 * @author taskmanager
 */
@RestController
@RequestMapping("/api/wms/product")
public class ProductController {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductSupplierMapper productSupplierMapper;

    @Autowired
    private ProductInventoryMapper productInventoryMapper;

    /**
     * 获取商品列表（分页 + 条件筛选）
     */
    @PreAuthorize("@ss.hasPermi('wms:product:list')")
    @GetMapping("/list")
    public Result<TableDataInfo<Product>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String skuCode,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        Page<Product> result = productMapper.selectProductList(page, productName, skuCode, status, minPrice, maxPrice);
        return Result.success(TableDataInfo.build(result));
    }

    /**
     * 根据商品ID获取详细信息（含供应商和库存信息）
     */
    @PreAuthorize("@ss.hasPermi('wms:product:query')")
    @GetMapping("/{productId}")
    public Result<Product> getInfo(@PathVariable Long productId) {
        Product product = productMapper.selectProductById(productId);
        if (product != null) {
            product.setSupplierList(productSupplierMapper.selectByProductId(productId));
            product.setInventoryList(productInventoryMapper.selectByProductId(productId));
        }
        return Result.success(product);
    }

    /**
     * 新增商品（含供应商和库存信息）
     */
    @Log(title = "商品管理", businessType = BusinessTypeEnum.INSERT)
    @PreAuthorize("@ss.hasPermi('wms:product:add')")
    @PostMapping
    @Transactional
    public Result<Void> add(@RequestBody Product product) {
        product.setDelFlag("0");
        productMapper.insert(product);
        // 保存供应商关联
        saveProductSuppliers(product);
        // 保存库存信息
        saveProductInventories(product);
        return Result.success();
    }

    /**
     * 修改商品（含供应商和库存信息）
     */
    @Log(title = "商品管理", businessType = BusinessTypeEnum.UPDATE)
    @PreAuthorize("@ss.hasPermi('wms:product:edit')")
    @PutMapping
    @Transactional
    public Result<Void> edit(@RequestBody Product product) {
        productMapper.updateById(product);
        // 先逻辑删除旧的关联数据，再保存新的
        productSupplierMapper.logicDeleteByProductId(product.getProductId());
        productInventoryMapper.logicDeleteByProductId(product.getProductId());
        saveProductSuppliers(product);
        saveProductInventories(product);
        return Result.success();
    }

    /**
     * 删除商品（逻辑删除，含供应商和库存关联）
     */
    @Log(title = "商品管理", businessType = BusinessTypeEnum.DELETE)
    @PreAuthorize("@ss.hasPermi('wms:product:remove')")
    @DeleteMapping("/{productIds}")
    @Transactional
    public Result<Void> remove(@PathVariable Long[] productIds) {
        for (Long id : productIds) {
            Product product = new Product();
            product.setProductId(id);
            product.setDelFlag("2");
            productMapper.updateById(product);
            productSupplierMapper.logicDeleteByProductId(id);
            productInventoryMapper.logicDeleteByProductId(id);
        }
        return Result.success();
    }

    /**
     * 导出商品数据为Excel
     */
    @Log(title = "商品管理", businessType = BusinessTypeEnum.EXPORT)
    @PreAuthorize("@ss.hasPermi('wms:product:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response,
                       @RequestParam(required = false) String productName,
                       @RequestParam(required = false) String skuCode,
                       @RequestParam(required = false) String status,
                       @RequestParam(required = false) BigDecimal minPrice,
                       @RequestParam(required = false) BigDecimal maxPrice) throws IOException {
        Page<Product> page = productMapper.selectProductList(
                new Page<>(1, 10000), productName, skuCode, status, minPrice, maxPrice);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("商品数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), Product.class)
                .sheet("商品数据")
                .doWrite(page.getRecords());
    }

    /**
     * 导入商品数据（Excel）
     */
    @Log(title = "商品管理", businessType = BusinessTypeEnum.IMPORT)
    @PreAuthorize("@ss.hasPermi('wms:product:import')")
    @PostMapping("/import")
    public Result<String> importData(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        int[] count = {0};
        int[] failCount = {0};
        StringBuilder failMsg = new StringBuilder();
        EasyExcel.read(file.getInputStream(), Product.class,
                new PageReadListener<Product>(dataList -> {
                    for (Product product : dataList) {
                        count[0]++;
                        try {
                            product.setProductId(null);
                            product.setDelFlag("0");
                            productMapper.insert(product);
                        } catch (Exception e) {
                            failCount[0]++;
                            failMsg.append("第").append(count[0])
                                    .append("行[").append(product.getProductName()).append("]导入失败：")
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
    @PreAuthorize("@ss.hasPermi('wms:product:import')")
    @PostMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("商品导入模板", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), Product.class)
                .sheet("商品数据")
                .doWrite(java.util.Collections.emptyList());
    }

    /**
     * 保存商品供应商关联
     */
    private void saveProductSuppliers(Product product) {
        List<ProductSupplier> supplierList = product.getSupplierList();
        if (supplierList != null && !supplierList.isEmpty()) {
            for (ProductSupplier ps : supplierList) {
                ps.setProductId(product.getProductId());
                ps.setDelFlag("0");
                ps.setId(null); // 确保自增ID
                productSupplierMapper.insert(ps);
            }
        }
    }

    /**
     * 保存商品库存信息
     */
    private void saveProductInventories(Product product) {
        List<ProductInventory> inventoryList = product.getInventoryList();
        if (inventoryList != null && !inventoryList.isEmpty()) {
            for (ProductInventory pi : inventoryList) {
                pi.setProductId(product.getProductId());
                pi.setDelFlag("0");
                pi.setId(null); // 确保自增ID
                productInventoryMapper.insert(pi);
            }
        }
    }
}
