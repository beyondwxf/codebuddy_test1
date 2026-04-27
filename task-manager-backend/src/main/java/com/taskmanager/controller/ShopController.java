package com.taskmanager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taskmanager.common.Result;
import com.taskmanager.domain.Product;
import com.taskmanager.domain.ProductInventory;
import com.taskmanager.mapper.ProductInventoryMapper;
import com.taskmanager.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 公开商店控制器
 * 提供无需登录即可访问的商品查询接口
 *
 * @author taskmanager
 */
@RestController
@RequestMapping("/api/shop")
public class ShopController {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductInventoryMapper productInventoryMapper;

    /**
     * 公开商品列表（分页 + 条件筛选）
     * 只返回上架且未删除的商品
     */
    @GetMapping("/products")
    public Result<Page<Product>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "12") int pageSize,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, "0")
                .eq(Product::getDelFlag, "0")
                .select(Product::getProductId, Product::getProductName, Product::getSkuCode,
                        Product::getDescription, Product::getPreviewImage, Product::getSalePrice,
                        Product::getUnit, Product::getCreateTime);

        if (productName != null && !productName.trim().isEmpty()) {
            wrapper.like(Product::getProductName, productName.trim());
        }
        if (minPrice != null) {
            wrapper.ge(Product::getSalePrice, minPrice);
        }
        if (maxPrice != null) {
            wrapper.le(Product::getSalePrice, maxPrice);
        }

        wrapper.orderByDesc(Product::getCreateTime);

        Page<Product> result = productMapper.selectPage(page, wrapper);
        return Result.success(result);
    }

    /**
     * 公开商品详情
     * 返回完整商品信息及总库存数量
     */
    @GetMapping("/products/{productId}")
    public Result<Product> detail(@PathVariable Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null || !"0".equals(product.getStatus()) || !"0".equals(product.getDelFlag())) {
            return Result.error("商品不存在或已下架");
        }

        List<ProductInventory> inventories = productInventoryMapper.selectByProductId(productId);
        int totalStock = 0;
        if (inventories != null) {
            for (ProductInventory inv : inventories) {
                if (inv.getStockQuantity() != null) {
                    totalStock += inv.getStockQuantity();
                }
            }
        }

        product.setTotalStock(totalStock);
        return Result.success(product);
    }
}
