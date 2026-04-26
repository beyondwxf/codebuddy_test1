package com.taskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taskmanager.domain.ProductSupplier;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品供应商关联 Mapper 接口
 *
 * @author taskmanager
 */
public interface ProductSupplierMapper extends BaseMapper<ProductSupplier> {

    /**
     * 根据商品ID查询供应商列表（含供应商名称）
     */
    List<ProductSupplier> selectByProductId(@Param("productId") Long productId);

    /**
     * 逻辑删除商品的所有供应商关联
     */
    int logicDeleteByProductId(@Param("productId") Long productId);
}
