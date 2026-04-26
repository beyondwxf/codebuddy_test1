package com.taskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taskmanager.domain.Product;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 商品 Mapper 接口
 *
 * @author taskmanager
 */
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 分页查询商品列表（支持多条件筛选）
     *
     * @param page        分页参数
     * @param productName 商品名称（模糊查询）
     * @param skuCode     SKU编码（模糊查询）
     * @param status      状态
     * @param minPrice    最低售价
     * @param maxPrice    最高售价
     * @return 分页结果
     */
    Page<Product> selectProductList(Page<Product> page,
                                     @Param("productName") String productName,
                                     @Param("skuCode") String skuCode,
                                     @Param("status") String status,
                                     @Param("minPrice") BigDecimal minPrice,
                                     @Param("maxPrice") BigDecimal maxPrice);

    /**
     * 根据商品ID查询商品详情（含供应商和库存信息）
     */
    Product selectProductById(@Param("productId") Long productId);
}
